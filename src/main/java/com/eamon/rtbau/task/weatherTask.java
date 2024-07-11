package com.eamon.rtbau.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eamon.rtbau.rtbauUser.mapper.RtbauUserMapper;
import com.eamon.rtbau.weather.service.GetBadWeatherService;
import com.eamon.rtbau.zara.entity.ZaraSale;
import com.eamon.rtbau.zara.service.IZaraSaleService;
import com.zjiecode.wxpusher.client.WxPusher;
import com.zjiecode.wxpusher.client.bean.Message;
import com.zjiecode.wxpusher.client.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * @Author: Eamon
 * @Date: 2022/4/3 22:30
 */
@EnableAsync
@Component
@Slf4j
public class weatherTask {

    @Autowired
    RtbauUserMapper rtbauUserMapper;

    @Autowired
    GetBadWeatherService getBadWeatherService;

    @Value("${appToken}")
    private String appToken;

    @Autowired
    IZaraSaleService getZaraSaleService;

    private String sign = "";

    @Async
    @Scheduled(cron = "0 0 21 * * ? ")
//    @Scheduled(cron = "0 0/2 * * * ? ") //测试每隔两分钟运行一次
    public void reminderTask() throws Exception {

        //获取数据库中所有用户涉及的地域编list1
        List<String> allRegionCode = rtbauUserMapper.getAllRegionCode();

        //初始化明天恶劣天气的地域编号list2
        List<String> badWeatherRegionCode = new ArrayList<>();

        //遍历list1获取明天恶劣天气的地域编号放到list2中
        for (String badCode : allRegionCode) {
            Boolean tomorrowIsBadWeather = getBadWeatherService.getTomorrowIsBadWeather(badCode);
            if (tomorrowIsBadWeather) {
                badWeatherRegionCode.add(badCode);
            }
        }

        //如果存在恶劣天气城市，执行下面的逻辑
        if (badWeatherRegionCode.size() != 0) {
            log.info("【*】运行时间：[" + new Date() + "] 存在恶劣天气城市，执行发送消息逻辑");
            //获取数据库中恶劣天气地域的所有用户uid
            List<String> sendUids = rtbauUserMapper.getSendUids(badWeatherRegionCode);
            //给list2中的用户发信息
            for (String uid : sendUids) {
                Result result = sendText(uid);
                System.out.println("消息:" + result);
            }
        } else {
            log.info("运行时间：[" + new Date() + "] 不存在恶劣天气城市");
        }
    }

    public Result sendText(String uid) {
        Message message = new Message();
        message.setContentType(Message.CONTENT_TYPE_TEXT);
        message.setUid(uid);
        message.setAppToken(appToken);
        message.setSummary("Eamon提醒明天记得带伞！");
        message.setContent("【记得带伞！！！】\n\n\n" + sign);
        message.setUrl("http://hello.xiaoming100.club/#/guide?uid="+uid);
        return WxPusher.send(message);
    }

    /**
     * 获取zara包折扣
     */
    @Scheduled(cron = "0 0/10 * * * ? ") //测试每隔两分钟运行一次
    public void getZaraTask() throws URISyntaxException, IOException {
        List<ZaraSale> zaraSalesLst = getZaraSaleService.getZaraSalesLst();
        log.info("目标Zara商品列表:", JSONObject.toJSONString(zaraSalesLst));
        for (ZaraSale zaraSale : zaraSalesLst) {
            String code = zaraSale.getCode();
            //外接口路径
            String gdApi = "https://www.zara.cn/itxrest/1/search/store/11716/reference?reference=" + code + "&locale=zh_CN&origin=default&deviceType=mobile&deviceOS=Android&deviceOSVersion=6.0&scope=mobileweb";
            //链接URL
            URL url = new URL(gdApi);
            //返回结果集
            StringBuffer document = new StringBuffer();
            //创建链接
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //读取返回结果集
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                document.append(line);
            }
            reader.close();
            JSONObject json = JSON.parseObject(document.toString());
            //查看json格式
            System.out.println("json" + json.toString());
//        JSONArray facets = json.getJSONArray("facets");
//        System.out.println("facets"+facets.toString());
//        // 函数用于获取JSONArray中所有对象的特定key对应的value列表
//        String keyToFind = "price_range_facet";
//        Integer newPrice = getZaraPrice(facets, keyToFind);

            int totalResults = json.getIntValue("totalResults");
            // 有货
            if (totalResults > 0) {
                JSONArray results = json.getJSONArray("results");
                JSONObject jsonObject = results.getJSONObject(0);
                for (int i = 0; i < results.size(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    JSONObject content = result.getJSONObject("content");
                    JSONObject detail = content.getJSONObject("detail");
                    JSONObject color = detail.getJSONArray("colors").getJSONObject(0);
                    String colorName = color.getString("name");
                    if (Objects.equals(colorName, zaraSale.getColorName())) {
                        jsonObject = results.getJSONObject(i);
                        break;
                    }
                }
                System.out.println("===选颜色后的对象===：：：" + JSONObject.toJSONString(jsonObject));
//                JSONObject jsonObject = results.getJSONObject(0);
                JSONObject content = jsonObject.getJSONObject("content");
                int displayDiscountPercentage = content.getIntValue("displayDiscountPercentage");
                // 现在折扣价
                int price = content.getIntValue("price") / 100;
                // 入库时的折扣价
                int oldPrice = zaraSale.getOldPrice();
                // 商品名称
                String name = content.getString("name");
//            int oldPrice = content.getIntValue("oldPrice");
//            // 低于现在（0.52）折扣
//            int nowPrice = (oldPrice * (100 - 48)) / 10000;
                System.out.println("price = " + price + " ;oldPrice = " + oldPrice);
                // TODO：取库中老价格比较
                Boolean isSendZara = getZaraSaleService.isSendZara(code, price);
                System.out.println("isSendZara=" + isSendZara);
                if (isSendZara) {
//                    String uid = "UID_lGO6GjIYDsrd9rkHVSPSpWldt1uL";
                    String uid = zaraSale.getUid();
                    sendZaraMsg(uid, displayDiscountPercentage, price, name);
                }
            }
        }
    }

    public Integer getZaraPrice(JSONArray jsonArray, String key) {
        System.out.println("jsonArray:" + jsonArray.toString());
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (Objects.equals(jsonObject.get("key"), key)) {
                JSONArray values = jsonObject.getJSONArray("values");
                for (int j = 0; j < values.size(); j++) {
                    JSONObject valueObject = values.getJSONObject(j);
                    System.out.println("jsonArray内部：" + valueObject.toString());
                    return valueObject.getIntValue("value");
                }
            }
        }
        return 0;
    }

    public Result sendZaraMsg(String uid, Integer displayDiscountPercentage, int price, String name) {
        Message message = new Message();
        message.setContentType(Message.CONTENT_TYPE_TEXT);
        message.setUid(uid);
        message.setAppToken(appToken);
        message.setSummary("Zara降价！" + price + name);
        message.setContent("折扣：" + (100 - displayDiscountPercentage) + "现价：" + price);
        message.setUrl("http://hello.xiaoming100.club/#/guide?uid=" + uid);
        return WxPusher.send(message);
    }
}
