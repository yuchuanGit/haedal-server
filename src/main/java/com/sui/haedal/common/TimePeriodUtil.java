package com.sui.haedal.common;

import com.sui.haedal.model.bo.TimePeriodStatisticsBo;
import com.sui.haedal.model.enums.DecimalType;
import com.sui.haedal.model.vo.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
public class TimePeriodUtil {

    public static TimePeriodVo TimePeriodTypeStartAndEndTime(Integer timePeriodType){
        TimePeriodVo vo = new TimePeriodVo();
        LocalDateTime now = LocalDateTime.now();
        String mysqlDateFormat = "%m/%d %H"; // 默认格式
        boolean isWeek = true;
        LocalDateTime end = now.with(LocalTime.of(23, 59, 59, 999_000_000));
        // 默认取7天前的开始时间
        LocalDateTime start = end.minusDays(7);

        // 根据时间类型调整
        if (timePeriodType== 2) {
            mysqlDateFormat = "%m/%d";
            start = end.minusDays(30);
            isWeek = false;
        }

        if (timePeriodType == 3) {
            mysqlDateFormat = "%m/%d";
            start = end.minusDays(90);
            isWeek = false;
        }
        start = start.plusNanos(1000 * 1000000);
        vo.setStart(start);
        vo.setEnd(end);
        vo.setIsWeek(isWeek);
        vo.setMysqlDateFormat(mysqlDateFormat);
        return vo;
    }

    public static TimePeriodStatisticsBo getTimePeriodParameter(Integer timePeriodType){
        TimePeriodVo vo = TimePeriodTypeStartAndEndTime(timePeriodType);
        TimePeriodStatisticsBo statisticsBo = new TimePeriodStatisticsBo();
        statisticsBo.setStart(Date.from(vo.getStart().atZone(ZoneId.systemDefault()).toInstant()));
        statisticsBo.setEnd(Date.from(vo.getEnd().atZone(ZoneId.systemDefault()).toInstant()));
        statisticsBo.setStartLD(vo.getStart());
        statisticsBo.setEndLD(vo.getEnd());
        statisticsBo.setMysqlDateFormat(vo.getMysqlDateFormat());
        statisticsBo.setIsWeek(vo.getIsWeek());
        return statisticsBo;
    }

    public static void virtualTimePeriodMatchValue(List<TimePeriodStatisticsVo> virtualTimePeriodData,Map<String,TimePeriodStatisticsVo> dateKeys,
                                             Boolean isWeek,List<TimePeriodStatisticsVo> resultData){
        List<String> supplyWithdrawKeysDesc = timeStrSortDescLambda(dateKeys); //存/取所有TransactionTime日期倒序
        List<String> supplyWithdrawKeys = supplyWithdrawKeysDesc;
        supplyWithdrawKeys.sort((s1,s2)-> s1.compareTo(s2)); //存/取所有TransactionTime日期从小到大排序
        for (TimePeriodStatisticsVo rsVal : virtualTimePeriodData) {
            TimePeriodStatisticsVo obj = new TimePeriodStatisticsVo();
            TimePeriodStatisticsVo val = dateKeys.get(rsVal.getDateUnit());

            if (val == null) {
                // 获取小于目标时间的key列表并降序排序
                List<String> lessThanKeys = tagerLessThanKeys(rsVal.getTransactionTime(), supplyWithdrawKeysDesc);
                lessThanKeys.sort((k1, k2) -> {
                    Date t1 = parseTimeKey(k1);
                    Date t2 = parseTimeKey(k2);
                    return t2.compareTo(t1); // 降序排序
                });

                // 获取最新的小于目标时间的key
                String newLessThanTimeStr = tagerNewLessThanKey(rsVal.getTransactionTime(), lessThanKeys);
                Date timeL = parseTimeKey(newLessThanTimeStr);

                if (timeL != null) {
                    String targetStr = DateUtil.dateGroupFormat(isWeek,timeL);
                    TimePeriodStatisticsVo supplyWithdrawL = dateKeys.get(targetStr);

                    if (supplyWithdrawL == null) {
                        // 获取大于目标时间的key
                        String newGreaterThanTimeStr = tagerNewGreaterThanKey(rsVal.getTransactionTime(), supplyWithdrawKeys);
                        Date timeG = parseTimeKey(newGreaterThanTimeStr);
                        if (timeG != null) {
                            String targetStrG = DateUtil.dateGroupFormat(isWeek,timeG);
                            TimePeriodStatisticsVo supplyWithdrawG = dateKeys.get(targetStrG);
                            if (supplyWithdrawG == null) {
                                log.info("没有大于TransactionTime={}", rsVal.getTransactionTime());
                            } else {
                                obj.setTransactionTime(rsVal.getTransactionTime());
                                obj.setDateUnit(rsVal.getDateUnit());
                                obj.setVal(supplyWithdrawG.getVal());
                                obj.setTotalVal(supplyWithdrawG.getTotalVal());
                            }
                        }
                    } else {
                        obj.setTransactionTime(rsVal.getTransactionTime());
                        obj.setDateUnit(rsVal.getDateUnit());
                        obj.setVal(supplyWithdrawL.getVal());
                        obj.setTotalVal(supplyWithdrawL.getTotalVal());
                    }
                }
            } else {
                obj = val;
                obj.setTransactionTime(rsVal.getTransactionTime());
            }
            resultData.add(obj);
        }
    }
    /**
     *  map key时间倒序
     * @param dateMaps 存入map数据
     * @return
     */
    private static List<String> timeStrSortDescLambda(Map<String, TimePeriodStatisticsVo> dateMaps) {
        List<String> supplyKeys = new ArrayList<>();
        for (TimePeriodStatisticsVo deposit : dateMaps.values()) {
            supplyKeys.add(deposit.getTransactionTime());
        }
        supplyKeys.sort((s1, s2) -> s2.compareTo(s1));
        return supplyKeys;
    }

    public static void matchWithdrawTimeCalculate(Map<String,TimePeriodStatisticsVo> dateUnitRemoveWithdrawMaps,Map<String,TimePeriodStatisticsVo> dateUnitDepositMaps,
                                            Map<String,TimePeriodStatisticsVo> dateKeys){
        List<String> supplyKeys = timeStrSortDescLambda(dateUnitDepositMaps);

        SimpleDateFormat targetSdf = new SimpleDateFormat("MM/dd HH");

        for (TimePeriodStatisticsVo withdraw : dateUnitRemoveWithdrawMaps.values()) {
            // 获取新的小于当前时间的key
            String newLessThanTimeStr = tagerNewLessThanKey(withdraw.getTransactionTime(), supplyKeys);

            try {
                // 解析时间并格式化
                Date time = parseTimeKey(newLessThanTimeStr);
                String targetStr = targetSdf.format(time);

                // 查找对应的supply对象
                TimePeriodStatisticsVo deposit = dateUnitDepositMaps.get(targetStr);
                if (deposit == null) {
                    log.info("没有小于" + withdraw.getTransactionTime()+"存入记录");
                } else {
                    //对应时间点累计统计值-取出值
                    BigDecimal val = getStrBigDecimal(deposit.getTotalVal()).subtract(getStrBigDecimal(withdraw.getVal()));
                    withdraw.setVal(val.toPlainString());
                    dateKeys.put(withdraw.getDateUnit(),withdraw);
                }
            } catch (NumberFormatException e) {
                log.error("金额转换失败: " + e.getMessage());
            }
        }
    }


    //循环存时间点匹配对应取时间点 计算当前剩余抵押
    public static void matchDepositTimeCalculate(Map<String, TimePeriodStatisticsVo> dateUnitDepositMap,List<String> withdrawTransactionTimes,
                                           Map<String, TimePeriodStatisticsVo> dateUnitWithdrawMap,Map<String, TimePeriodStatisticsVo> dateUnitRemoveWithdrawMaps,
                                          Boolean isWeek,Map<String,TimePeriodStatisticsVo> dateKeys){
        withdrawTransactionTimes.sort((s1,s2)-> s2.compareTo(s1));//倒序
        for (TimePeriodStatisticsVo deposit : dateUnitDepositMap.values()) {
            TimePeriodStatisticsVo withdraw = dateUnitWithdrawMap.get(deposit.getDateUnit());
            if (withdraw == null) {
                log.info(deposit.getDateUnit() + "时间没有取抵押");
                // 获取新的小于当前时间的key
                String newLessThanTimeStr = tagerNewLessThanKey(deposit.getTransactionTime(), withdrawTransactionTimes);
                Date dateL = parseTimeKey(newLessThanTimeStr);
                String targetStr = DateUtil.dateGroupFormat(isWeek,dateL);
                TimePeriodStatisticsVo withdrawLessThanSupply = dateUnitWithdrawMap.get(targetStr);
                if(null==withdrawLessThanSupply){
                    log.info("取抵押没有小于存TransactionTime%="+deposit.getTransactionTime());
                }else{
                    BigDecimal val = getStrBigDecimal(deposit.getVal()).subtract(getStrBigDecimal(withdrawLessThanSupply.getVal()));
                    deposit.setVal(val.toPlainString());
                }
            } else {
                try {
                    BigDecimal val = getStrBigDecimal(deposit.getVal()).subtract(getStrBigDecimal(withdraw.getVal()));
                    deposit.setVal(val.toPlainString());
                } catch (NumberFormatException e) {
                    // 处理数字转换异常
                    log.info("金额转换失败: " + e.getMessage());
                }
            }
            dateKeys.put(deposit.getDateUnit(),deposit);
            dateUnitRemoveWithdrawMaps.remove(deposit.getDateUnit());
        }
    }

    public static Map<String, TimePeriodStatisticsVo> timePeriodDataConvertDateUnitMaps(
            List<TimePeriodStatisticsVo> timePeriodData ,  List<TimePeriodStatisticsVo> ltStartTimeVos,
            Map<String, PythCoinFeedPriceVo> coinPrice,List<String> transactionTimes,Map<String, TimePeriodStatisticsVo> dateUnitRemoveMaps){
        Map<String, TimePeriodStatisticsVo> dateUnitMaps = new HashMap<>();
//        BigDecimal cumulativeSum = new BigDecimal(0.00);//累计数量
        BigDecimal cumulativeSum = lessThanStartTimeTotalSumUsd(ltStartTimeVos,coinPrice);//小于开始统计时间usd价格
        for (TimePeriodStatisticsVo statisticsVo : timePeriodData) {
            PythCoinFeedPriceVo pythCoinFeedPrice = coinPrice.get(statisticsVo.getFeedId());
            BigDecimal usdUnitPrice = feedIdUsdUnitPrice(pythCoinFeedPrice);// 计算FeedId对应USD单价
            // 计算币种金额的浮点值（假设CalculateCoinDecimalFloat方法已实现）
            BigDecimal floatAmountVal = calculateCoinDecimalFloat(statisticsVo.getVal(), statisticsVo.getCoinType());
            BigDecimal coinUsdAmount = floatAmountVal.multiply(usdUnitPrice);// 计算USD金额并累加
            cumulativeSum = cumulativeSum.add(coinUsdAmount);
            statisticsVo.setVal(roundingModeStr(cumulativeSum,2,RoundingMode.DOWN));
            statisticsVo.setTotalVal(roundingModeStr(cumulativeSum,2,RoundingMode.DOWN));
            // 更新dateMaps
            if (!dateUnitMaps.containsKey(statisticsVo.getDateUnit())) {
                dateUnitMaps.put(statisticsVo.getDateUnit(), statisticsVo);
                if(dateUnitRemoveMaps!=null){
                    dateUnitRemoveMaps.put(statisticsVo.getDateUnit(), statisticsVo);
                }
            } else {
                TimePeriodStatisticsVo dateData = dateUnitMaps.get(statisticsVo.getDateUnit());
                dateData.setVal(roundingModeStr(cumulativeSum,2,RoundingMode.DOWN));
                dateUnitMaps.put(statisticsVo.getDateUnit(), dateData); // 或直接修改对象（因对象引用传递）
                if(dateUnitRemoveMaps!=null){
                    dateUnitRemoveMaps.put(statisticsVo.getDateUnit(), dateData);
                }
            }
            if(transactionTimes!=null){
                transactionTimes.add(statisticsVo.getTransactionTime());
            }
        }
        return dateUnitMaps;
    }


    /**
     * 四舍五入模式
     * @param val
     * @param decimals 保留小数位
     * @param roundingMode 小数四舍五入模式 RoundingMode.DOWN 丢弃多余小数
     * @return
     */
    public static String roundingModeStr(BigDecimal val,int decimals,RoundingMode roundingMode){
        return val.setScale(decimals,roundingMode).toString();
    }


    /**
     * 计算FeedId对应的USD单价 add  subtract multiply divide
     * @param pythCoinFeedPrice PythCoinFeedPrice对象
     * @return USD单价（double类型）
     */
    public static BigDecimal feedIdUsdUnitPrice(PythCoinFeedPriceVo pythCoinFeedPrice) {
        BigDecimal price = new BigDecimal(pythCoinFeedPrice.getPrice());
        // 计算10的Expo绝对值次方（math.Pow(10, |Expo|)）
        double expoAbs = Math.abs(pythCoinFeedPrice.getExpo());
        double denominator = Math.pow(10, expoAbs);
        return price.divide(new BigDecimal(denominator));
    }

    /**
     * 计算币种金额的浮点值（转换为基础单位）
     * @param val 金额字符串（科学计数法或普通格式）
     * @param coinType 币种类型（如xxx::xxx::SUI）
     * @return 转换后的浮点值
     */
    public static BigDecimal calculateCoinDecimalFloat(String val, String coinType) {
        // 获取最后一个::后的币种类型
        String loanCoinType = coinTokenTypeVal(coinType);
        // 获取币种的小数位数
        int coinDecimal = getCoinDecimal(loanCoinType);

        // 处理科学计数法并转换为浮点值
        String convVal = scientificComputingConvertToDecimal(val);
        BigDecimal floatVal = new BigDecimal(convVal);
        return  floatVal.divide(new BigDecimal(Math.pow(10, coinDecimal)));
    }

    public static String coinTokenTypeVal(String coinType){
        String[] collateralTokens = coinType.split("::");
        return collateralTokens[collateralTokens.length - 1]; // 取最后一个元素
    }

    public static int getCoinDecimal(String typeVal) {
        if("SUI".equals(typeVal)){
            return 9;
        }else if("USDC".equals(typeVal)){
            return 6;
        }
        return 0;
    }


    /**
     * 科学计算转十进制
     * @param val
     * @return
     */
    public static String scientificComputingConvertToDecimal(String val) {
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
     * 获取第一个小于目标key的key
     * @param transactionTime 目标key
     * @param supplyKeys
     * @return
     */
    public static String tagerNewLessThanKey(String transactionTime, List<String> supplyKeys) {
        for (String key : supplyKeys) {
            Date keyTime = parseTimeKey(key);
            Date transTime = parseTimeKey(transactionTime);
            if (keyTime.before(transTime)) {
                return key;
            }
        }
        return transactionTime; // 默认返回原时间
    }

    /**
     * 获取小于目标key的所有key
     * @param targetKey
     * @param supplyKeys
     * @return
     */
    public static List<String> tagerLessThanKeys(String targetKey, List<String> supplyKeys) {
        List<String> resultKeys = new ArrayList<>();
        Date targetTime = parseTimeKey(targetKey);
        if (targetTime == null) {
            return resultKeys;
        }
        for (String key : supplyKeys) {
            Date keyTime = parseTimeKey(key);
            if (keyTime != null && keyTime.before(targetTime)) {
                resultKeys.add(key);
            }
        }
        return resultKeys;
    }


    /**
     * 获取第一个大于目标key的key
     * @param targetKey
     * @param supplyKeys
     * @return
     */
    public static String tagerNewGreaterThanKey(String targetKey, List<String> supplyKeys) {
        Date targetTime = parseTimeKey(targetKey);
        if (targetTime == null) {
            return "";
        }

        for (String key : supplyKeys) {
            Date keyTime = parseTimeKey(key);
            if (keyTime != null && targetTime.before(keyTime)) {
                return key;
            }
        }
        return "";
    }

    /**
     * 字符串转换date
     * @param timeStr
     * @return
     */
    public static Date parseTimeKey(String timeStr) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat( DateUtil.DATE_FORMAT_YMD_HM);
        try {
            date = sdf.parse(timeStr);
        }catch (ParseException p){
        }
        return date;
    }

    private static BigDecimal getStrBigDecimal(String val){
        return new BigDecimal(val);
    }


    /**
     * 获取小于开始统计时间usd价格
     * @param ltVos
     * @param coinPrice
     * @return
     */
    public static BigDecimal lessThanStartTimeTotalSumUsd(List<TimePeriodStatisticsVo>  ltVos, Map<String, PythCoinFeedPriceVo> coinPrice){
        BigDecimal lessThanStartBaseSum = new BigDecimal(0.00);//小于统计开始时间基础总计
        for (TimePeriodStatisticsVo ltVo : ltVos) {
            PythCoinFeedPriceVo pythCoinFeedPrice = coinPrice.get(ltVo.getFeedId());
            BigDecimal usdUnitPrice = TimePeriodUtil.feedIdUsdUnitPrice(pythCoinFeedPrice);// 计算FeedId对应USD单价
            BigDecimal floatAmountVal = TimePeriodUtil.calculateCoinDecimalFloat(ltVo.getVal(), ltVo.getCoinType());
            BigDecimal coinUsdAmount = floatAmountVal.multiply(usdUnitPrice);// 计算USD金额并累加
            lessThanStartBaseSum = lessThanStartBaseSum.add(coinUsdAmount);
        }
        return lessThanStartBaseSum;
    }
}
