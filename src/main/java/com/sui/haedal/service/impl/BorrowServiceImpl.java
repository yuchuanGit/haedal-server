package com.sui.haedal.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sui.haedal.mapper.BorrowMapper;
import com.sui.haedal.mapper.CoinConfigMapper;
import com.sui.haedal.model.bo.BorrowTotalBo;
import com.sui.haedal.model.bo.YourTotalSupplyLineBo;
import com.sui.haedal.model.entity.Borrow;
import com.sui.haedal.model.entity.CoinConfig;
import com.sui.haedal.model.vo.*;
import com.sui.haedal.service.BorrowService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service("borrowService")
public class BorrowServiceImpl implements BorrowService {

    @Autowired
    private BorrowMapper borrowMapper;

    @Autowired
    private CoinConfigMapper coinConfigMapper;


    @Override
    public RateModelVo queryBorrowDetailRateModel(BorrowTotalBo conditionBo){
        RateModelVo vo = new RateModelVo();
        List<RateModelDetailVo> ratesVo = new ArrayList<>();
        YourTotalSupplyLineBo mysqlConditionBo = borrowTotalConditionBo(conditionBo);
        List<JSONObject> rates = borrowMapper.queryRateDateGroup(mysqlConditionBo);
        List<JSONObject> dateSupplyAssets = borrowMapper.dateGroupSupplyAssets(mysqlConditionBo);
        List<JSONObject> dateBorrowAssets = borrowMapper.dateGroupBorrowAssets(mysqlConditionBo);
        Map<String,String> supplyTimeMap = dateSupplyAssets.stream().collect(Collectors.toMap(key-> key.getString("timeGroup"),val -> val.getString("supplyAssets"), (v1, v2)-> v1));
        Map<String,String>  borrowTimeMap = dateBorrowAssets.stream().collect(Collectors.toMap(key-> key.getString("timeGroup"),val -> val.getString("borrowAssets"), (v1, v2)-> v1));
        String currentU = borrowMapper.marketCurrentU(mysqlConditionBo);
        vo.setCurrentU(currentU);
        for (int i = 0; i < rates.size(); i++) {
            RateModelDetailVo rmd = new RateModelDetailVo();
            JSONObject rate = rates.get(i);

            String[] rateTypes = rate.getString("rateType").split(",");
            String[] interestRates = rate.getString("interestRate").split(",");

            rmd.setDateUnit(rate.getString("dateUnit"));
            rmd.setCurrentU("0");

            if (rateTypes.length > 1) {
                rmd.setSupplyRate(interestRates[0]);
                rmd.setBorrowRate(interestRates[1]);

                try {
                    double s = Double.parseDouble(rmd.getSupplyRate());
                    double b = Double.parseDouble(rmd.getBorrowRate());
                    double u = b / s;
                    rmd.setCurrentU(String.format("%.2f", u));
                } catch (NumberFormatException e) {
                    // 处理数值转换异常
                    rmd.setCurrentU("0.00");
                }
            } else if (rateTypes.length > 0) {
                if ("supply".equals(rateTypes[0])) {
                    rmd.setSupplyRate(interestRates[0]);
                    rmd.setBorrowRate("0");
                } else if ("borrow".equals(rateTypes[0])) {
                    rmd.setSupplyRate("0");
                    rmd.setBorrowRate(interestRates[0]);
                }

                String daySupply = supplyTimeMap.get(rmd.getDateUnit());
                String dayBorrow = borrowTimeMap.get(rmd.getDateUnit());

                if (daySupply != null && !daySupply.isEmpty()) {
                    try {
                        double s = Double.parseDouble(daySupply);
                        double u = 50000000.0 / s;
                        rmd.setCurrentU(String.format("%.2f", u));
                    } catch (NumberFormatException e) {
                        rmd.setCurrentU("0.00");

                    }
                }

                if (dayBorrow != null && !dayBorrow.isEmpty()) {
                    try {
                        double s = Double.parseDouble(dayBorrow);
                        double u = s / 26000000.0;
                        rmd.setCurrentU(String.format("%.2f", u));
                    } catch (NumberFormatException e) {
                        rmd.setCurrentU("0.00");
                    }
                }
            }
            ratesVo.add(rmd);
        }
        vo.setRates(ratesVo);
        vo.setTargetU("0.9");
        return vo;
    }

    @Override
    public List<BorrowRateLineVo> queryBorrowDetailRateLine(BorrowTotalBo conditionBo){
        YourTotalSupplyLineBo mysqlConditionBo = borrowTotalConditionBo(conditionBo);
        return borrowMapper.queryBorrowDetailRateLine(mysqlConditionBo);
    }
    @Override
    public List<BorrowRateLineVo> queryBorrowDetailLine(BorrowTotalBo conditionBo){
        YourTotalSupplyLineBo mysqlConditionBo = borrowTotalConditionBo(conditionBo);
        List<BorrowRateLineVo> data = borrowMapper.queryBorrowDetailLine(mysqlConditionBo);
        return data;
    }


    private YourTotalSupplyLineBo borrowTotalConditionBo(BorrowTotalBo conditionBo){
        YourTotalSupplyLineBo bo = new YourTotalSupplyLineBo();
        LocalDateTime now = LocalDateTime.now();
        String dateFormat = "%m/%d %H"; // 默认格式
        // 构造当天23:59:59.999的结束时间
        LocalDateTime end = now.with(LocalTime.of(23, 59, 59, 999_000_000));
        // 默认取7天前的开始时间
        LocalDateTime start = end.minusDays(7);

        // 根据时间类型调整
        if (conditionBo.getTimePeriodType() == 2) {
            dateFormat = "%m/%d";
            start = end.minusDays(30);
        }

        if (conditionBo.getTimePeriodType() == 3) {
            dateFormat = "%m/%d";
            start = end.minusDays(90);
        }
        bo.setMysqlDateFormat(dateFormat);
        bo.setStart(Date.from(start.atZone(ZoneId.systemDefault()).toInstant()));
        bo.setEnd(Date.from(end.atZone(ZoneId.systemDefault()).toInstant()));
        bo.setMarketId(conditionBo.getMarketId());
        bo.setLineType(conditionBo.getLineType());
        return bo;
    }

    @Override
    public BorrowVo queryBorrowDetail(BorrowTotalBo conditionBo){
        BorrowVo vo = new BorrowVo();
        LambdaQueryWrapper<Borrow>  queryWrapper = Wrappers.<Borrow>query().lambda();
        queryWrapper.eq(Borrow::getMarketId,conditionBo.getMarketId());
        List<Borrow> list = borrowMapper.selectList(Wrappers.<Borrow>query().lambda());
        if(list.size()>0){
            BeanUtils.copyProperties(list.get(0),vo);
            vo.setLltv(ltvConvPercentage(vo.getLltv()));
            vo.setLtv(ltvConvPercentage(vo.getLtv()));
            String collateralCoinType = coinTokenTypeVal(vo.getCollateralTokenType());
            String loanCoinType = coinTokenTypeVal(vo.getLoanTokenType());
            vo.setPair(collateralCoinType + "/" + loanCoinType);
            vo.setCollateralCoinDecimals(GetCoinDecimal(collateralCoinType));
            vo.setLoanCoinDecimals(GetCoinDecimal(loanCoinType));
            List<CoinConfig> coinList = coinConfigMapper.selectList(Wrappers.<CoinConfig>query().lambda());
            Map<String,CoinConfig> coinConfigMap = coinList.stream().collect(Collectors.toMap(CoinConfig::getCoinType,Function.identity(),(v1,v2)->v1));


            CoinConfig collaCoin = coinConfigMap.get(vo.getCollateralTokenType());
            CoinConfig loanCoin = coinConfigMap.get(vo.getLoanTokenType());
            vo.setCollateralFeedId(collaCoin.getFeedId());
            vo.setCollateralFeedObjectId(collaCoin.getFeedObjectId());
            vo.setLoanFeedId(loanCoin.getFeedId());
            vo.setLoanFeedObjectId(loanCoin.getFeedObjectId());
            //todo
            vo.setLiqPenalty("3%");

        }
        return vo;
    }

    public String ltvConvPercentage(String val) {
        Double num = 0.00;
        try {
            // 解析字符串为long类型（对应Go的strconv.ParseInt）
            num = Double.parseDouble(val);
        } catch (NumberFormatException e) {
            // 处理转换失败的情况（对应Go的err处理）
            System.err.println("string转Double失败：" + e.getMessage());
            return val; // 转换失败时返回原字符串，可根据需求调整
        }
        double result = num / 10000000000000000L;

        // 格式化为无小数位的字符串（对应Go的strconv.FormatFloat(result, 'f', 0, 64)）
        return String.format("%.0f", result);
    }

    @Override
    public List<BorrowVo> queryList() {
        List<Borrow> list = borrowMapper.selectList(Wrappers.<Borrow>query().lambda());
        List<CoinConfig> coinConfigList = coinConfigMapper.selectList(Wrappers.<CoinConfig>query().lambda());
        Map<String,CoinConfig> coinConfigMap = coinConfigList.stream().collect(Collectors.toMap(CoinConfig::getCoinType, Function.identity(),(v1, v2)->  v1));
        List<BorrowVo> results = new ArrayList<>();
        for (Borrow borrow : list) {
            BorrowVo vo = new BorrowVo();
            BeanUtils.copyProperties(borrow, vo);
            CoinConfig collateralCoin = coinConfigMap.get(vo.getCollateralTokenType());
            CoinConfig loanCoin = coinConfigMap.get(vo.getLoanTokenType());
            String collateralType = coinTokenTypeVal(vo.getCollateralTokenType());
            String loanType = coinTokenTypeVal(vo.getLoanTokenType());
            vo.setPair(collateralType + "/" + loanType);
            vo.setCollateralCoinDecimals(GetCoinDecimal(collateralType));
            vo.setLoanCoinDecimals(GetCoinDecimal(loanType));
            if(collateralCoin!=null){
                vo.setCollateralFeedId(collateralCoin.getFeedId());
                vo.setCollateralFeedObjectId(collateralCoin.getFeedObjectId());
            }
            if(loanCoin!=null){
                vo.setLoanFeedId(loanCoin.getFeedId());
                vo.setLoanFeedObjectId(loanCoin.getFeedObjectId());
            }
            results.add(vo);
        }
        return results;
    }

    @Override
    public List<BorrowRateLineVo> yourTotalSupplyLine(BorrowTotalBo conditionBo){
        List<BorrowRateLineVo> data = new ArrayList<>();
        Map<String,BorrowRateLineVo> dateMaps = new HashMap<>();
        Map<String,BorrowRateLineVo> dateWithdrawMaps = new HashMap<>();
        YourTotalSupplyLineBo bo = new YourTotalSupplyLineBo();
        LocalDateTime now = LocalDateTime.now();
        String dateFormat = "%m/%d %H"; // 默认格式
        // 构造当天23:59:59.999的结束时间
        LocalDateTime end = now.with(LocalTime.of(23, 59, 59, 999_000_000));
        // 默认取7天前的开始时间
        LocalDateTime start = end.minusDays(7);

        // 根据时间类型调整
        if (conditionBo.getTimePeriodType() == 2) {
            dateFormat = "%m/%d";
            start = end.minusDays(30);
        }

        if (conditionBo.getTimePeriodType() == 3) {
            dateFormat = "%m/%d";
            start = end.minusDays(90);
        }
        bo.setMysqlDateFormat(dateFormat);
        bo.setStart(Date.from(start.atZone(ZoneId.systemDefault()).toInstant()));
        bo.setEnd(Date.from(end.atZone(ZoneId.systemDefault()).toInstant()));
        bo.setUserAddress(conditionBo.getUserAddress());
        List<UserTotalCollateralVo> collateralSupplyVos = borrowMapper.userCollateralSupply(bo);
        List<UserTotalCollateralVo> collateralWithdrawVos =borrowMapper.userCollateralWithdraw(bo);
        Map<String,String> feedIds = collateralSupplyVos.stream().collect(Collectors.toMap(UserTotalCollateralVo::getFeedId, UserTotalCollateralVo::getFeedId,(v1, v2)->  v1));
        feedIds.putAll(collateralWithdrawVos.stream().collect(Collectors.toMap(UserTotalCollateralVo::getFeedId, UserTotalCollateralVo::getFeedId,(v1, v2)->  v1)));
        Map<String, PythCoinFeedPriceVo> coinPrice = getPythPrice(feedIds);


        double supplyCollateralSum = 0.00;
        double withdrawCollateralSum = 0.00; // 原逻辑中未使用，保留声明

        for (UserTotalCollateralVo um : collateralSupplyVos) {
            BorrowRateLineVo b = new BorrowRateLineVo();
            PythCoinFeedPriceVo pythCoinFeedPrice = coinPrice.get(um.getFeedId());

            // 调用工具方法计算USD单价
            double usdUnitPrice = feedIdUsdUnitPrice(pythCoinFeedPrice);

            // 计算币种金额的浮点值（假设CalculateCoinDecimalFloat方法已实现）
            double floatAmountVal = calculateCoinDecimalFloat(um.getAmount(), um.getCoinType());

            // 计算USD金额并累加
            double coinUsdAmount = floatAmountVal * usdUnitPrice;
            supplyCollateralSum += coinUsdAmount;
//
//            // 设置BorrowLine字段（保留两位小数）
            b.setAmount(String.format("%.2f", supplyCollateralSum));
            b.setTotalAmount(String.format("%.2f", supplyCollateralSum));
            b.setDateUnit(um.getDateUnit());
            b.setTransactionTime(um.getTransactionTime());

            // 更新dateMaps
            if (!dateMaps.containsKey(um.getDateUnit())) {
                dateMaps.put(um.getDateUnit(), b);
            } else {
                BorrowRateLineVo dateData = dateMaps.get(um.getDateUnit());
                dateData.setAmount(String.format("%.2f", supplyCollateralSum));
                dateMaps.put(um.getDateUnit(), dateData); // 或直接修改对象（因对象引用传递）
            }
        }

        for (UserTotalCollateralVo um : collateralWithdrawVos) {
            BorrowRateLineVo b = new BorrowRateLineVo();
            PythCoinFeedPriceVo pythCoinFeedPrice = coinPrice.get(um.getFeedId());

            // 调用工具方法计算USD单价
            double usdUnitPrice = feedIdUsdUnitPrice(pythCoinFeedPrice);

            // 计算币种金额的浮点值（假设CalculateCoinDecimalFloat方法已实现）
            double floatAmountVal = calculateCoinDecimalFloat(um.getAmount(), um.getCoinType());

            // 计算USD金额并累加
            double coinUsdAmount = floatAmountVal * usdUnitPrice;
            withdrawCollateralSum += coinUsdAmount;
//
//            // 设置BorrowLine字段（保留两位小数）
            b.setAmount(String.format("%.2f", withdrawCollateralSum));
            b.setTotalAmount(String.format("%.2f", withdrawCollateralSum));
            b.setDateUnit(um.getDateUnit());
            b.setTransactionTime(um.getTransactionTime());

            // 更新dateMaps
            if (!dateWithdrawMaps.containsKey(um.getDateUnit())) {
                dateWithdrawMaps.put(um.getDateUnit(), b);
            } else {
                BorrowRateLineVo dateData = dateWithdrawMaps.get(um.getDateUnit());
                dateData.setAmount(String.format("%.2f", withdrawCollateralSum));
                dateWithdrawMaps.put(um.getDateUnit(), dateData); // 或直接修改对象（因对象引用传递）
            }
        }

        for (BorrowRateLineVo bl : dateMaps.values()) {
            BorrowRateLineVo withdraw = dateWithdrawMaps.get(bl.getDateUnit());
            if (withdraw == null) {
                log.info(bl.getDateUnit() + "时间没有取抵押");
            } else {
                try {
//                    double supplyCollateralVal = Double.parseDouble(bl.getAmount());
//                    double withdrawCollateralVal = Double.parseDouble(withdraw.getAmount());
//                    double val = supplyCollateralVal - withdrawCollateralVal;
//                    bl.setAmount(new BigDecimal(String.format("%.2f", val)));

                    BigDecimal val = strConversionDecimal(bl.getAmount()).subtract(strConversionDecimal(withdraw.getAmount()));
                    bl.setAmount(val.toPlainString());
                } catch (NumberFormatException e) {
                    // 处理数字转换异常
                    log.info("金额转换失败: " + e.getMessage());
                }
            }
            data.add(bl);
            dateWithdrawMaps.remove(bl.getDateUnit());
        }

        List<String> supplyKeys = supplyCollateralTimeStrSortDescLambda(dateMaps);

        SimpleDateFormat targetSdf = new SimpleDateFormat("MM/dd HH");

        for (BorrowRateLineVo wl : dateWithdrawMaps.values()) {
            // 获取新的小于当前时间的key
            String newLessThanTimeStr = tagerNewLessThanKey(wl.getTransactionTime(), supplyKeys);

            try {
                // 解析时间并格式化
                Date time = parseTimeKey(newLessThanTimeStr);
                String targetStr = targetSdf.format(time);

                // 查找对应的supply对象
                BorrowRateLineVo supplyO = dateMaps.get(targetStr);
                if (supplyO == null) {
                    log.info("YourTotalSupplyLine supplyDate=" + targetStr);
                } else {
                    // 解析金额并计算
//                    double supplyCollateralVal = Double.parseDouble(supplyO.getTotalAmount());
//                    double withdrawCollateralVal = Double.parseDouble(wl.getAmount());
//
//                    log.info(String.format("supplyCollateralVal=%.2f", supplyCollateralVal));
//                    log.info(String.format("withdrawCollateralVal=%.2f", withdrawCollateralVal));
//
//                    double val = supplyCollateralVal - withdrawCollateralVal;
//                    wl.setAmount(String.format("%.2f", val));

                   BigDecimal val = strConversionDecimal(supplyO.getTotalAmount()).subtract(strConversionDecimal(wl.getAmount()));
                    wl.setAmount(val.toPlainString());
                    data.add(wl);
                }
            } catch (ParseException e) {
                log.error("解析时间失败: " + e.getMessage());
            } catch (NumberFormatException e) {
                log.error("金额转换失败: " + e.getMessage());
            }
        }

        sortLinesByTransactionTimeAscLambda(data);

        return data;
    }

    private BigDecimal strConversionDecimal(String val){
        return new BigDecimal(val);
    }

    public void sortLinesByTransactionTimeAscLambda(List<BorrowRateLineVo> lines) {
        lines.sort((line1, line2) -> {
            Date ti = parseTransactionTime(line1.getTransactionTime());
            Date tj = parseTransactionTime(line2.getTransactionTime());

            if (ti == null && tj == null) return 0;
            if (ti == null) return 1;
            if (tj == null) return -1;

            return ti.before(tj) ? -1 : (ti.after(tj) ? 1 : 0);
        });
    }

    private Date parseTransactionTime(String timeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf.parse(timeStr);
        } catch (ParseException e) {
            log.error("解析时间失败: " + timeStr + ", 错误: " + e.getMessage());
            return null; // 解析失败时返回null，可根据业务调整
        }
    }

    private String tagerNewLessThanKey(String transactionTime, List<String> supplyKeys) {
        // 这里需要实现原Go函数TagerNewLessThanKey的逻辑
        // 示例：假设返回第一个小于transactionTime的supplyKey
        for (String key : supplyKeys) {
            try {
                Date keyTime = parseTimeKey(key);
                Date transTime = parseTimeKey(transactionTime);
                if (keyTime.before(transTime)) {
                    return key;
                }
            } catch (ParseException e) {
                log.error("解析时间失败: " + e.getMessage());
            }
        }
        return transactionTime; // 默认返回原时间
    }

    
    public List<String> supplyCollateralTimeStrSortDescLambda(Map<String, BorrowRateLineVo> dateMaps) {
        List<String> supplyKeys = new ArrayList<>();
        for (BorrowRateLineVo supplyObj : dateMaps.values()) {
            supplyKeys.add(supplyObj.getTransactionTime());
        }
        supplyKeys.sort((s1, s2) -> s2.compareTo(s1));
        return supplyKeys;
    }


    private Date parseTimeKey(String timeStr) throws ParseException {
        // 请根据实际的时间字符串格式修改SimpleDateFormat的模式
        // 例如："yyyy-MM-dd HH:mm:ss"、"yyyyMMddHHmmss"等
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.parse(timeStr);
    }

    /**
     * 计算币种金额的浮点值（转换为基础单位）
     * @param val 金额字符串（科学计数法或普通格式）
     * @param coinType 币种类型（如xxx::xxx::SUI）
     * @return 转换后的浮点值
     */
    public double calculateCoinDecimalFloat(String val, String coinType) {
        // 获取最后一个::后的币种类型
        String loanCoinType = coinTokenTypeVal(coinType);
        // 获取币种的小数位数
        int coinDecimal = GetCoinDecimal(loanCoinType);

        // 处理科学计数法并转换为浮点值
        double floatVal = 0.0;
        try {
            String convVal = scientificComputingConvStr(val);
            floatVal = Double.parseDouble(convVal);
        } catch (NumberFormatException e) {
            // 转换失败返回0.0
            return 0.0;
        }

        // 除以10的coinDecimal次方
        return floatVal / Math.pow(10, coinDecimal);
    }


    public Map<String, PythCoinFeedPriceVo> getPythPrice(Map<String, String> feedIds) {
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


    public  String scientificComputingConvStr(String val) {
        if (val == null || val.isEmpty()) {
            return "0";
        }

        try {
            // 解析字符串为double（支持科学计数法）
            double num = Double.parseDouble(val);
            // 格式化为无小数位的字符串（四舍五入）
            return String.format("%.0f", num);
        } catch (NumberFormatException e) {
            // 解析失败返回默认值"0"
            return "0";
        }
    }

    /**
     * 计算FeedId对应的USD单价
     * @param pythCoinFeedPrice PythCoinFeedPrice对象
     * @return USD单价（double类型）
     */
    public double feedIdUsdUnitPrice(PythCoinFeedPriceVo pythCoinFeedPrice) {
        // 将Price字符串转换为double（忽略转换异常，默认返回0.0）
        double floatVal = 0.0;
        try {
            floatVal = Double.parseDouble(pythCoinFeedPrice.getPrice());
        } catch (NumberFormatException e) {
            // 转换失败时返回0.0（或根据业务需求处理异常）
            return 0.0;
        }

        // 计算10的Expo绝对值次方（math.Pow(10, |Expo|)）
        double expoAbs = Math.abs(pythCoinFeedPrice.getExpo());
        double denominator = Math.pow(10, expoAbs);

        return floatVal / denominator;
    }

    private String coinTokenTypeVal(String coinType){
        String[] collateralTokens = coinType.split("::");
        return collateralTokens[collateralTokens.length - 1]; // 取最后一个元素
    }

    private int GetCoinDecimal(String typeVal) {
        if("SUI".equals(typeVal)){
            return 9;
        }else if("USDC".equals(typeVal)){
            return 6;
        }
        return 0;
    }
}

