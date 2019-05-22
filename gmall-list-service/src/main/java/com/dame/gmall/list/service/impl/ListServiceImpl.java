package com.dame.gmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dame.gmall.bean.SkuLsInfo;
import com.dame.gmall.bean.SkuLsParams;
import com.dame.gmall.bean.SkuLsResult;
import com.dame.gmall.config.RedisUtil;
import com.dame.gmall.service.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ListServiceImpl implements ListService {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 操作ES的client
     */
    @Autowired
    private JestClient jestClient;
    /**
     * ES中的index 和 type
     */
    private static final String ES_INDEX = "gmall";
    private static final String ES_TYPE = "SkuInfo";

    /**
     * 热度排名，redis中zset的key
     */
    private static final String HOT_SCORE = "hotScore";

    /**
     * 热度排名，redis中zset的memeber的前缀
     */
    private static final String HOT_MEMBER_PREFIX = "skuId:";

    /**
     * ES中保存热度值基数
     */
    private static final Integer ES_HOT_SAVE_BASE_VALUE = 5;


    /**
     * 保存sku数据到es中
     *
     * @param skuLsInfo
     */
    @Override
    public void saveSkuInfo(SkuLsInfo skuLsInfo) {
        try {
            Index index = new Index.Builder(skuLsInfo).index(ES_INDEX).type(ES_TYPE).id(skuLsInfo.getId()).build();
            DocumentResult result = jestClient.execute(index);
            int code = result.getResponseCode();
            System.out.println("code:" + code);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("数据插入ES失败");
        }
    }

    // 从es中查询数据
    @Override
    public SkuLsResult search(SkuLsParams skuLsParams) {
        // 构建ES查询语句
        String query = makeQueryStringForSearch(skuLsParams);
        // 查询ES
        Search search = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
        SearchResult searchResult = null;
        try {
            searchResult = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 拼装ES返回参数
        SkuLsResult skuLsResult = makeResultForSearch(skuLsParams, searchResult);
        return skuLsResult;
    }

    /**
     * 拼装ES的返回参数
     *
     * @param skuLsParams
     * @param searchResult
     * @return
     */
    private SkuLsResult makeResultForSearch(SkuLsParams skuLsParams, SearchResult searchResult) {
        SkuLsResult skuLsResult = new SkuLsResult();
        // 拼装skuLsInfoList
        ArrayList<SkuLsInfo> skuLsInfoList = new ArrayList<>(skuLsParams.getPageSize());
        List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);
        for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
            SkuLsInfo skuLsInfo = hit.source;
            // 将skuName给替换掉高亮显示的skuName
            Map<String, List<String>> highlight = hit.highlight;
            if (!CollectionUtils.isEmpty(highlight)) {
                String skuNameHI = highlight.get("skuName").get(0);
                skuLsInfo.setSkuName(skuNameHI);
            }

            skuLsInfoList.add(skuLsInfo);
        }
        // skuLsInfo集合
        skuLsResult.setSkuLsInfoList(skuLsInfoList);
        // 总条数
        skuLsResult.setTotal(searchResult.getTotal());
        // 总页数
        // long totalPage = searchResult.getTotal()%skuLsParams.getPageSize() == 0 ? searchResult.getTotal()/skuLsParams.getPageSize() : searchResult.getTotal()/skuLsParams.getPageSize() + 1;
        // 这种写法更精确
        long totalPage = (searchResult.getTotal() + skuLsParams.getPageSize() - 1) / skuLsParams.getPageSize();
        skuLsResult.setTotalPages(totalPage);

        // 拼装属性值id
        ArrayList<String> attrValueIdList = new ArrayList<>();
        MetricAggregation aggregations = searchResult.getAggregations();
        TermsAggregation termsAggregation = aggregations.getTermsAggregation("groupby_attr");
        if (null != termsAggregation) {
            List<TermsAggregation.Entry> buckets = termsAggregation.getBuckets();
            for (TermsAggregation.Entry bucket : buckets) {
                attrValueIdList.add(bucket.getKey());
            }
            skuLsResult.setAttrValueIdList(attrValueIdList);
        }
        return skuLsResult;
    }

    /**
     * 构建查询ES的查询语句
     *
     * @param skuLsParams
     * @return
     */
    private String makeQueryStringForSearch(SkuLsParams skuLsParams) {
        /*
            GET /gmall/SkuInfo/_search
            {
              "query": {
                "bool": {
                  "filter": [
                    {"term": {"skuAttrValueList.valueId": "2"}},
                    {"term": {"skuAttrValueList.valueId": "7"}},
                    {"term": {"catalog3Id": "61"}}
                  ],
                  "must": {"match": {"skuName": "手机"}}
                }
              },
              "highlight": {
                "fields": {"skuName": {}}
              },
              "from": 0,
              "size": 5,
              "sort": {
                  "hotScore": {
                    "order": "desc"
                  }
              },
              "aggs": {
                "groupby_attr": {
                  "terms": {
                    "field": "skuAttrValueList.valueId"
                  }
                }
              }
            }
         */
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        // term": {"skuAttrValueList.valueId": "2"}
        if (null != skuLsParams.getValueId() && skuLsParams.getValueId().length > 0) {
            for (String valId : skuLsParams.getValueId()) {
                TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("skuAttrValueList.valueId", valId);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        // {"term": {"catalog3Id": "61"}}
        if (StringUtils.isNotBlank(skuLsParams.getCatalog3Id())) {
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("catalog3Id", skuLsParams.getCatalog3Id());
            boolQueryBuilder.filter(termQueryBuilder);
        }
        // "must": {"match": {"skuName": "手机"}}
        if (StringUtils.isNotBlank(skuLsParams.getKeyword())) {
            MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("skuName", skuLsParams.getKeyword());
            boolQueryBuilder.must(matchQueryBuilder);
        }
        searchSourceBuilder.query(boolQueryBuilder);

        // highlight
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("skuName").preTags("<span style='color:red'>").postTags("</span>");
        searchSourceBuilder.highlight(highlightBuilder);

        // 分页
        int from = (skuLsParams.getPageNo() - 1) * skuLsParams.getPageSize();
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(skuLsParams.getPageSize());

        // sort
        searchSourceBuilder.sort("hotScore", SortOrder.DESC);

        // 获取平台属性值，"aggs": {"groupby_attr": {"terms": { "field": "skuAttrValueList.valueId" }}}
        TermsBuilder termsBuilder = AggregationBuilders.terms("groupby_attr").field("skuAttrValueList.valueId");
        searchSourceBuilder.aggregation(termsBuilder);

        // 获取查询语句
        String query = searchSourceBuilder.toString();
        return query;
    }

    /**
     * 保存商品的热度分值
     *
     * @param skuId
     */
    @Override
    public void incrHotScore(String skuId) {
        Jedis jedis = redisUtil.getJedis();
        // 保存ES，利用原子性，提高并发能力
        Double hotScore = jedis.zincrby(HOT_SCORE, 1, HOT_MEMBER_PREFIX + skuId);
        // 点击每5次保存下ES中
        if (hotScore % ES_HOT_SAVE_BASE_VALUE == 0) {
            updateHotScore(skuId, Math.round(hotScore));
        }
        jedis.close();
    }

    /**
     * 更新ES中sku的热度分值
     *
     * @param skuId
     * @param hotScore
     */
    private void updateHotScore(String skuId, long hotScore) {
        String updateStr = "{\n" +
                "  \"doc\": {\n" +
                "    \"hotScore\":" + hotScore + "\n" +
                "  }\n" +
                "}";
        try {
            Update build = new Update.Builder(updateStr).index(ES_INDEX).type(ES_TYPE).id(skuId).build();
            jestClient.execute(build);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
