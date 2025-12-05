package com.sui.haedal.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sui.haedal.model.vo.PythCoinFeedPriceVo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PythOracleUtil {

    public static Map<String, PythCoinFeedPriceVo> getPythPrice(Map<String, String> feedIds) {
        String BASE_URL = "https://hermes-beta.pyth.network/v2/updates/price/latest";
        Map<String, PythCoinFeedPriceVo> feedPrices = new HashMap<>();
        OkHttpClient client = new OkHttpClient();

        try {
            // 构建请求参数
            HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL).newBuilder();
            for (String feedId : feedIds.values()) {
//                String cleanId = feedId.startsWith("0x") ? feedId.substring(2) : feedId;
                urlBuilder.addQueryParameter("ids[]", feedId); // 直接添加ids[]参数
            }
            String fullURL = urlBuilder.build().toString();

            // 发送请求
            Request request = new Request.Builder().url(fullURL).build();
            Response response = client.newCall(request).execute();

            // 处理响应
            if (!response.isSuccessful()) {
                log.error("请求失败，状态码：{}", response.code());
                return feedPrices;
            }

            // 解析响应体
            String responseBody = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseBody);
            JSONArray parsedArray = jsonObject.getJSONArray("parsed");
            if (parsedArray == null || parsedArray.isEmpty()) {
                log.error("PythPrice-parsed数组为空或不存在");
                return feedPrices;
            }

            // 遍历解析每个元素
            for (int i = 0; i < parsedArray.size(); i++) {
                JSONObject parsedObj = parsedArray.getJSONObject(i);
                if (parsedObj == null) continue;

                PythCoinFeedPriceVo feedPrice = new PythCoinFeedPriceVo();
                // 设置FeedId（拼接0x前缀）
                String id = parsedObj.getString("id");
                feedPrice.setFeedId("0x" + id);

                // 解析price对象
                JSONObject priceObj = parsedObj.getJSONObject("price");
                if (priceObj != null) {
                    feedPrice.setPrice(priceObj.getString("price"));
                    feedPrice.setExpo(priceObj.getDoubleValue("expo"));
                }

                feedPrices.put(feedPrice.getFeedId(), feedPrice);
            }

        } catch (Exception e) {
            log.error("请求异常：", e);
        }

        return feedPrices;
    }
}
