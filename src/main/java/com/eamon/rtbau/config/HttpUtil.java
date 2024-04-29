package com.eamon.rtbau.config;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicHeader;
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

}
