package com.eamon.rtbau.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eamon.rtbau.config.HttpUtil;
import com.eamon.rtbau.rtbauUser.mapper.RtbauUserMapper;
import com.eamon.rtbau.weather.service.GetBadWeatherService;
import com.eamon.rtbau.zara.entity.BrandSale;
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
import java.util.stream.Collectors;

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
//    @Scheduled(cron = "0 0/10 * * * ? ") //测试每隔两分钟运行一次
    @Scheduled(cron = "0 0/1 * * * ? ") //测试每隔两分钟运行一次
    public void getZaraTask() throws Exception {
        List<BrandSale> brandSalesLst = getZaraSaleService.getZaraSalesLst();
        log.info("目标Zara和优衣库商品列表:", JSONObject.toJSONString(brandSalesLst));
        // 按照品牌分组
        Map<String, List<BrandSale>> zaraSaleMap = brandSalesLst.stream()
                .collect(Collectors.groupingBy(BrandSale::getBrandIdentity));
        // zara逻辑
        boolean hasZara = zaraSaleMap.containsKey("zara");
        if (hasZara) {
            log.info("目标Zara商品列表:", JSONObject.toJSONString(zaraSaleMap.get("zara")));
            zaraDiscountCalc(zaraSaleMap);
        }
        // yyk优衣库逻辑
        boolean yyk = zaraSaleMap.containsKey("yyk");
        if (yyk) {
            log.info("目标优衣库商品列表:", JSONObject.toJSONString(zaraSaleMap.get("yyk")));
            yykDiscountCalc(zaraSaleMap);
        }
    }

    // zara发送优惠计算
    private void zaraDiscountCalc(Map<String, List<BrandSale>> zaraSaleMap) throws IOException {
        try {
            for (BrandSale zaraSale : zaraSaleMap.get("zara")) {
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
        } catch (Exception e) {
            log.error("zaraDiscountCalc 计算zara优惠异常:{},异常:{}", e, e.getStackTrace());
        }
    }

    // 优衣库发送优惠计算
    private void yykDiscountCalc(Map<String, List<BrandSale>> zaraSaleMap) {
        try {
            for (BrandSale yykSale : zaraSaleMap.get("yyk")) {
                HttpUtil httpUtil = new HttpUtil();
                // 1.商品号转化
                Map<String, Object> yykParams = new HashMap<>();
                yykParams.put("description", yykSale.getCode());
                yykParams.put("rank", "overall");
                Map<String, Object> pageInfoParams = new HashMap<String, Object>();
                pageInfoParams.put("page", 1);
                pageInfoParams.put("pageSize", 20);
                pageInfoParams.put("withSideBar", "Y");
                yykParams.put("pageInfo", pageInfoParams);
                Map<String, Object> priceRangeParams = new HashMap<String, Object>();
                priceRangeParams.put("low", 0);
                priceRangeParams.put("high", 0);
                yykParams.put("priceRange", priceRangeParams);
                String yykUrl = "https://d.uniqlo.cn/p/hmall-sc-service/search/searchWithDescriptionAndConditions/zh_CN";
//            String yykPostRes = httpUtil.doPostWithParams(yykUrl, yykParams);
                String params = JSONObject.toJSONString(yykParams);
                String yykPostRes = httpUtil.jsonPostV2(yykUrl, null, null, params);
                JSONObject yykObject = JSON.parseObject(yykPostRes);
                JSONArray resp = yykObject.getJSONArray("resp");
                String productCode = resp.getJSONArray(1).getJSONObject(0).getString("productCode");

                // 2.获取对应颜色和尺码是否有货
                String yykGetUrl = "https://www.uniqlo.cn/data/products/spu/zh_CN/" + productCode + ".json";
                String yykGetRes = httpUtil.doGetWithParams(yykGetUrl);
                JSONObject yykProductsInfo = JSON.parseObject(yykGetRes);
                JSONArray yykResults = yykProductsInfo.getJSONArray("rows");
                for (int i = 0; i < yykResults.size(); i++) {
                    JSONObject result = yykResults.getJSONObject(i);
                    // 尺码
                    String sizeText = result.getString("sizeText");
                    // 颜色
                    String styleText = result.getString("styleText");
                    // 是否有货
                    String enabledFlag = result.getString("enabledFlag");
                    // 现价
                    Integer varyPrice = result.getInteger("varyPrice");
                    // 商品名称
                    String name = result.getString("name");

                    if (Objects.equals(sizeText, yykSale.getSizeText())
                            && Objects.equals(styleText, yykSale.getColorName())
                            && Objects.equals(enabledFlag, "Y") && varyPrice < yykSale.getOldPrice()) {
                        String uid = yykSale.getUid();
                        Boolean isSendZara = getZaraSaleService.isSendZara(yykSale.getCode(), varyPrice);
                        System.out.println("isSendZara=" + isSendZara);
                        if (isSendZara) {
                            sendYYKMsg(uid, sizeText, styleText, varyPrice, name);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("yykDiscountCalc 计算优衣库优惠异常:{},异常:{}", e, e.getStackTrace());
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
        message.setContent("折扣：" + (100 - displayDiscountPercentage) + " ｜现价：" + price);
        message.setUrl("http://hello.xiaoming100.club/#/guide?uid=" + uid);
        return WxPusher.send(message);
    }

    public Result sendYYKMsg(String uid, String sizeText, String styleText, int price, String name) {
        Message message = new Message();
        message.setContentType(Message.CONTENT_TYPE_TEXT);
        message.setUid(uid);
        message.setAppToken(appToken);
        message.setSummary("Zara降价！" + price + name);
        message.setContent("名称" + name + " ｜尺码：" + sizeText + " ｜颜色：" + styleText + " ｜现价：" + price);
        message.setUrl("http://hello.xiaoming100.club/#/guide?uid=" + uid);
        return WxPusher.send(message);
    }
}
