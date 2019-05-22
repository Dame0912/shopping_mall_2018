package com.dame.gmall.list;

import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListServiceApplicationTests {

    @Autowired
    private JestClient jestClient;

    @Test
    public void testQuery(){

    }


    @Test
    public void testES() {
        try {
            String query = "{\n" +
                    "  \"query\": {\n" +
                    "    \"match\": {\n" +
                    "      \"name\": \"sea\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            Search search = new Search.Builder(query).addIndex("movie_index").addType("movie").build();
            SearchResult searchResult = jestClient.execute(search);
            List<SearchResult.Hit<Map, Void>> hits = searchResult.getHits(Map.class);
            for (SearchResult.Hit<Map, Void> hit : hits) {
                System.out.println(hit.source);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
