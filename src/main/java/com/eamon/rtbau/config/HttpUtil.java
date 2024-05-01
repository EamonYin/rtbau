package com.eamon.rtbau.config;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class HttpUtil {

    public String get(String url,String ck,String ContentType, Map<String, Object> params) throws URISyntaxException {
        String resultContent = null;

        URIBuilder uriBuilder = new URIBuilder(url);
//        if (!params.isEmpty()) {
//            params.forEach((k, v) -> {
//                uriBuilder.addParameter(k, v.toString());
//            });
//        }
        HttpGet httpGet = new HttpGet(uriBuilder.build());

//        BasicHeader cookie = new BasicHeader("Cookie", ck);
//        if (StringUtils.isEmpty(ContentType)) {
//            ContentType = "application/json";
//        }
//        BasicHeader contentType = new BasicHeader("Content-Type", ContentType);
//        BasicHeader referer = new BasicHeader("Referer", "https://blog.csdn.net/Tianc666");
//        Header[] headers = new Header[]{cookie,contentType,referer};
//        httpGet.setHeaders(headers);
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                // 获取状态码
                System.out.println(response.getVersion()); // HTTP/1.1
                System.out.println(response.getCode()); // 200
                System.out.println(response.getReasonPhrase()); // OK
                HttpEntity entity = response.getEntity();
                // 获取响应信息
                resultContent = EntityUtils.toString(entity);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return resultContent;
    }

    public String post(String url, String ck, String ContentType, Map<String, Object> params) {
        String result = null;
        HttpPost httpPost = new HttpPost(url);
        // 表单参数
        List<NameValuePair> nvps = new ArrayList<>();
        // POST 请求参数
        if (!params.isEmpty()) {
            params.forEach((k, v) -> {
                nvps.add(new BasicNameValuePair(k, v.toString()));
            });
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
//        // POST Header
        BasicHeader cookie = new BasicHeader("Cookie", ck);
        if (StringUtils.isEmpty(ContentType)) {
            ContentType = "application/json";
        }
        BasicHeader contentType = new BasicHeader("Content-Type", ContentType);
        Header[] headers = new Header[]{contentType};
        httpPost.setHeaders(headers);

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
                System.out.println(response.getVersion()); // HTTP/1.1
                System.out.println(response.getCode()); // 200
                System.out.println(response.getReasonPhrase()); // OK

                HttpEntity entity = response.getEntity();
                // 获取响应信息
                result = EntityUtils.toString(entity);
                // 确保流被完全消费
                EntityUtils.consume(entity);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String jsonPostV2(String url,String ck, String cType, String jsonBody) {
        String result = null;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        // POST Header
        BasicHeader cookie = new BasicHeader("Cookie", ck);
        if (StringUtils.isEmpty(cType)) {
            cType = "application/json";
        }
        BasicHeader contentType = new BasicHeader("Content-Type", cType);
//        BasicHeader secFetchMode = new BasicHeader("Sec-Fetch-Mode", "cors");
//        BasicHeader secFetchSite = new BasicHeader("Sec-Fetch-Site", "same-origin");
        Header[] headers = new Header[]{cookie,contentType};
        httpPost.setHeaders(headers);

        // 获取cookies信息
        BasicCookieStore store = new BasicCookieStore();

        try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(store).build()) {
            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
                // 获取响应信息
                result = EntityUtils.toString(response.getEntity());
            }

            //获得Cookies
            List<Cookie> cookielist = store.getCookies();
            for(Cookie cookie1: cookielist){
                String name=cookie1.getName();
                String value=cookie1.getValue();
                System.out.println("cookie name =" + name);
                System.out.println("Cookie name=" + value);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

}
