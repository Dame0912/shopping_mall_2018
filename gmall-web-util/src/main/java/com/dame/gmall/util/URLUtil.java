package com.dame.gmall.util;

import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class URLUtil {

    public static class UrlEntity {
        /**
         * 基础url
         */
        public String baseUrl;
        /**
         * url参数
         */
        public Map<String, String> params;
    }

    /**
     * 解析url
     *
     * @param url
     * @return
     */
    public static UrlEntity parse(String url) {
        UrlEntity entity = new UrlEntity();
        if (url == null) {
            return entity;
        }
        url = url.trim();
        if (url.equals("")) {
            return entity;
        }
        String[] urlParts = url.split("\\?");
        entity.baseUrl = urlParts[0];
        //没有参数
        if (urlParts.length == 1) {
            return entity;
        }
        //有参数
        String[] params = urlParts[1].split("&");
        entity.params = new HashMap<>();
        for (String param : params) {
            String[] keyValue = param.split("=");
            entity.params.put(keyValue[0], keyValue[1]);
        }
        return entity;
    }

    /**
     * 获取URL中指定的值
     * @param url
     * @param name
     * @return
     */
    public static String getParam(String url, String name){
        Map<String, String> params = parse(url).params;
        if(MapUtils.isNotEmpty(params)){
            return params.get(name);
        }
        return null;
    }

    /**
     * 剔除URL中的某个参数
     * @param url
     * @param names
     * @return
     */
    public static String moveParam(String url, String... names){
        UrlEntity urlEntity = parse(url);
        StringBuilder newURL = new StringBuilder(urlEntity.baseUrl);
        Map<String, String> params = urlEntity.params;
        if(MapUtils.isNotEmpty(params)) {
            for (String name : names) {
                params.remove(name);
            }
            if(MapUtils.isNotEmpty(params)){
                newURL.append("?");
                Set<Map.Entry<String, String>> entries = params.entrySet();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    newURL.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                }
                String urlParamsStr = newURL.toString();
                return urlParamsStr.substring(0, urlParamsStr.length()-1);
            }
        }
        return newURL.toString();
    }

}
