package com.sui.haedal.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sui.haedal.common.DateUtil;
import com.sui.haedal.common.PythOracleUtil;
import com.sui.haedal.common.TimePeriodUtil;
import com.sui.haedal.mapper.BorrowMapper;
import com.sui.haedal.mapper.CoinConfigMapper;
import com.sui.haedal.model.bo.BorrowTotalBo;
import com.sui.haedal.model.bo.TimePeriodStatisticsBo;
import com.sui.haedal.model.bo.YourTotalSupplyLineBo;
import com.sui.haedal.model.entity.Borrow;
import com.sui.haedal.model.entity.CoinConfig;
import com.sui.haedal.model.enums.HaedalOperationType;
import com.sui.haedal.model.vo.*;
import com.sui.haedal.service.BorrowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Resource
    private BorrowMapper borrowMapper;

    @Resource
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

    /**
     * borrow 详情借利率统计
     * @param conditionBo
     * @return
     */
    @Override
    public List<TimePeriodStatisticsVo> borrowDetailRateStatistics(BorrowTotalBo conditionBo){
        List<TimePeriodStatisticsVo> resultData = new ArrayList<>();
        /**获取时间段类型 时间段数据**/
        TimePeriodStatisticsBo statisticsBo = TimePeriodUtil.getTimePeriodParameter(conditionBo.getTimePeriodType());
        statisticsBo.setBusinessPoolId(conditionBo.getMarketId());
        List<TimePeriodStatisticsVo> virtualTimePeriodData = DateUtil.timePeriodDayGenerateNew(statisticsBo.getStartLD(),statisticsBo.getEndLD(),statisticsBo.getIsWeek());
        List<TimePeriodStatisticsVo> list = borrowMapper.queryBorrowDetailRateLine(statisticsBo);
        Map<String,TimePeriodStatisticsVo> dateUnitKeys = list.stream().collect(Collectors.toMap(TimePeriodStatisticsVo::getDateUnit,Function.identity(),(v1,v2)-> v1));
        /**虚拟时间数据匹配虚拟时间最近点dateUnitKeys(所有存/取数据)**/
        if(dateUnitKeys.size()>0){
            TimePeriodUtil.virtualTimePeriodMatchValue(virtualTimePeriodData,dateUnitKeys,statisticsBo.getIsWeek(),resultData);
        }
        return resultData;

    }

    /**
     * borrow 详情抵押品利率
     * @param conditionBo
     * @return
     */
    @Override
    public  List<TimePeriodStatisticsVo> borrowDetailCollateralAPRStatistics(BorrowTotalBo conditionBo){
        List<TimePeriodStatisticsVo> resultData = new ArrayList<>();
        //todo
        return resultData;
    }
    @Override
    public List<BorrowRateLineVo> queryBorrowDetailLine(BorrowTotalBo conditionBo){
        YourTotalSupplyLineBo mysqlConditionBo = borrowTotalConditionBo(conditionBo);
        List<String> rateTypes = new ArrayList<>();
        if(HaedalOperationType.Collateral.getValue().equals(conditionBo.getStatisticsType())){

        }else if(HaedalOperationType.BORROW.getValue().equals(conditionBo.getStatisticsType())){

        }
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
        List<Borrow> list = borrowMapper.selectList(queryWrapper);
        if(list.size()>0){
            BeanUtils.copyProperties(list.get(0),vo);
            vo.setLltv(ltvConvPercentage(vo.getLltv()));
            vo.setLtv(ltvConvPercentage(vo.getLtv()));
            String collateralCoinType = TimePeriodUtil.coinTokenTypeVal(vo.getCollateralTokenType());
            String loanCoinType = TimePeriodUtil.coinTokenTypeVal(vo.getLoanTokenType());
            vo.setPair(collateralCoinType + "/" + loanCoinType);
            vo.setCollateralCoinDecimals(TimePeriodUtil.getCoinDecimal(collateralCoinType));
            vo.setLoanCoinDecimals(TimePeriodUtil.getCoinDecimal(loanCoinType));
            List<CoinConfig> coinList = coinConfigMapper.selectList(Wrappers.<CoinConfig>query().lambda());
            Map<String,CoinConfig> coinConfigMap = coinList.stream().collect(Collectors.toMap(CoinConfig::getCoinType,Function.identity(),(v1,v2)->v1));
            setBorrowFeed(vo,coinConfigMap);
            //todo
            vo.setLiqPenalty("3%");
            vo.setVaultAddress(borrowMapper.queryVaultAddress(vo.getMarketId()));

        }
        return vo;
    }


    /**
     * borrow 资产存入统计
     * @param bo
     * @return
     */
    @Override
    public List<TimePeriodStatisticsVo> borrowDetailSupplyStatistics(BorrowTotalBo bo){
        /**获取时间段类型 时间段数据**/
        TimePeriodStatisticsBo statisticsBo = TimePeriodUtil.getTimePeriodParameter(bo.getTimePeriodType());
        statisticsBo.setBusinessPoolId(bo.getMarketId());
        statisticsBo.setSupplyType(HaedalOperationType.SUPPLY.getValue());
        statisticsBo.setStatisticalRole(false);//borrow池 统计
        List<TimePeriodStatisticsVo> supplyVos = borrowMapper.borrowTimePeriodStatisticsSupplyOrCollateral(statisticsBo);
        List<TimePeriodStatisticsVo> supplyLtVos =  borrowMapper.borrowTimePeriodStatisticsSupplyOrCollateralLTTransactionTime(statisticsBo);
        List<TimePeriodStatisticsVo> withdrawVos = borrowMapper.borrowWithdraw(statisticsBo);
        List<TimePeriodStatisticsVo> withdrawLtVos =  borrowMapper.borrowWithdrawLTTransactionTime(statisticsBo);
        List<TimePeriodStatisticsVo> resultData = TimePeriodUtil.getTimePeriodData(statisticsBo,supplyVos,supplyLtVos,withdrawVos,withdrawLtVos,false);
        return resultData;
    }

    private Borrow getMarketId(String marketId){
        Borrow borrow = null;
        LambdaQueryWrapper<Borrow>  queryWrapper = Wrappers.<Borrow>query().lambda();
        queryWrapper.eq(Borrow::getMarketId,marketId);
        List<Borrow> list = borrowMapper.selectList(queryWrapper);
        if(list.size()>0){
            borrow = list.get(0);
        }
        return borrow;
    }

    /**
     * borrow 资产存入抵押品统计
     * @param bo
     * @return
     */
    @Override
    public List<TimePeriodStatisticsVo> borrowDetailCollateralStatistics(BorrowTotalBo bo){
        TimePeriodStatisticsBo statisticsBo = TimePeriodUtil.getTimePeriodParameter(bo.getTimePeriodType());
        statisticsBo.setBusinessPoolId(bo.getMarketId());
        statisticsBo.setSupplyType(HaedalOperationType.Collateral.getValue());
        statisticsBo.setStatisticalRole(false);//borrow池 统计
        List<TimePeriodStatisticsVo> collateralSupplyVos = borrowMapper.borrowTimePeriodStatisticsSupplyOrCollateral(statisticsBo);
        List<TimePeriodStatisticsVo> collateralSupplyLtVos =  borrowMapper.borrowTimePeriodStatisticsSupplyOrCollateralLTTransactionTime(statisticsBo);
        List<TimePeriodStatisticsVo> collateralWithdrawVos =borrowMapper.borrowCollateralWithdraw(statisticsBo);
        List<TimePeriodStatisticsVo> collateralWithdrawLtVos =borrowMapper.borrowCollateralWithdrawLTTransactionTime(statisticsBo);
        List<TimePeriodStatisticsVo> resultData = TimePeriodUtil.getTimePeriodData(statisticsBo,collateralSupplyVos,collateralSupplyLtVos,collateralWithdrawVos,collateralWithdrawLtVos,false);
        return resultData;
    }

    /**
     * borrow 借明细统计
     * @param bo
     * @return
     */
    @Override
    public List<TimePeriodStatisticsVo> borrowDetailStatistics(BorrowTotalBo bo){
        TimePeriodStatisticsBo statisticsBo = TimePeriodUtil.getTimePeriodParameter(bo.getTimePeriodType());
        statisticsBo.setBusinessPoolId(bo.getMarketId());
        statisticsBo.setStatisticalRole(false);//borrow池 统计
        List<TimePeriodStatisticsVo> supplyVos = borrowMapper.borrowTimePeriodStatistics(statisticsBo);
        List<TimePeriodStatisticsVo> supplyLtVos =  borrowMapper.borrowTimePeriodStatisticsLTTransactionTime(statisticsBo);
        List<TimePeriodStatisticsVo> withdrawVos = borrowMapper.borrowRepayTimePeriodStatistics(statisticsBo);
        List<TimePeriodStatisticsVo> withdrawLtVos =  borrowMapper.borrowRepayTimePeriodStatisticsLTTransactionTime(statisticsBo);
        List<TimePeriodStatisticsVo> resultData = TimePeriodUtil.getTimePeriodData(statisticsBo,supplyVos,supplyLtVos,withdrawVos,withdrawLtVos,false);
        return resultData;
    }


    private void setBorrowFeed(BorrowVo vo, Map<String,CoinConfig> coinConfigMap ){
        CoinConfig collaCoin = coinConfigMap.get(vo.getCollateralTokenType());
        CoinConfig loanCoin = coinConfigMap.get(vo.getLoanTokenType());
        if (collaCoin != null) {
            vo.setCollateralFeedId(collaCoin.getFeedId());
            vo.setCollateralFeedObjectId(collaCoin.getFeedObjectId());
        }
        if (loanCoin != null) {
            vo.setLoanFeedId(loanCoin.getFeedId());
            vo.setLoanFeedObjectId(loanCoin.getFeedObjectId());
        }

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
            String collateralType = TimePeriodUtil.coinTokenTypeVal(vo.getCollateralTokenType());
            String loanType = TimePeriodUtil.coinTokenTypeVal(vo.getLoanTokenType());
            vo.setPair(collateralType + "/" + loanType);
            vo.setCollateralCoinDecimals(TimePeriodUtil.getCoinDecimal(collateralType));
            vo.setLoanCoinDecimals(TimePeriodUtil.getCoinDecimal(loanType));
            setBorrowFeed(vo,coinConfigMap);
            vo.setLltv(ltvConvPercentage(vo.getLltv()));
            vo.setLtv(ltvConvPercentage(vo.getLtv()));
            results.add(vo);
        }
        return results;
    }

    private void dateSupplyMaps(List<UserTotalCollateralVo> collateralSupplyVos,Map<String, PythCoinFeedPriceVo> coinPrice,Map<String,BorrowRateLineVo> dateSupplyMaps){
        double supplyCollateralSum = 0.00;
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
            if (!dateSupplyMaps.containsKey(um.getDateUnit())) {
                dateSupplyMaps.put(um.getDateUnit(), b);
            } else {
                BorrowRateLineVo dateData = dateSupplyMaps.get(um.getDateUnit());
                dateData.setAmount(String.format("%.2f", supplyCollateralSum));
                dateSupplyMaps.put(um.getDateUnit(), dateData); // 或直接修改对象（因对象引用传递）
            }
        }
    }

    private List<String> dateWithdrawMaps(List<UserTotalCollateralVo> collateralWithdrawVos,Map<String, PythCoinFeedPriceVo> coinPrice,
                                  Map<String,BorrowRateLineVo> dateWithdrawMaps,Map<String,BorrowRateLineVo> dateWithdrawRemoveMaps){

        double withdrawCollateralSum = 0.00;
        List<String> withdrawTransactionTimes = new ArrayList<>();
        for (UserTotalCollateralVo um : collateralWithdrawVos) {
            withdrawTransactionTimes.add(um.getTransactionTime());
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
                dateWithdrawRemoveMaps.put(um.getDateUnit(), b);
            } else {
                BorrowRateLineVo dateData = dateWithdrawMaps.get(um.getDateUnit());
                dateData.setAmount(String.format("%.2f", withdrawCollateralSum));
                dateWithdrawMaps.put(um.getDateUnit(), dateData);
                dateWithdrawRemoveMaps.put(um.getDateUnit(), dateData);
            }
        }
        return withdrawTransactionTimes;
    }

    //循环存时间点匹配对应取时间点 计算当前剩余抵押
    private void matchSupplyTimeCalculate(Map<String,BorrowRateLineVo> dateSupplyMaps,List<String> withdrawTransactionTimes,
                                          Map<String,BorrowRateLineVo> dateWithdrawMaps,Map<String,BorrowRateLineVo> dateWithdrawRemoveMaps,
                                          Boolean isWeek,Map<String,BorrowRateLineVo> dateKeys){
        withdrawTransactionTimes.sort((s1,s2)-> s2.compareTo(s1));//倒序
        for (BorrowRateLineVo bl : dateSupplyMaps.values()) {
            BorrowRateLineVo withdraw = dateWithdrawMaps.get(bl.getDateUnit());
            if (withdraw == null) {
                log.info(bl.getDateUnit() + "时间没有取抵押");
                // 获取新的小于当前时间的key
                String newLessThanTimeStr = tagerNewLessThanKey(bl.getTransactionTime(), withdrawTransactionTimes);
                Date dateL = parseTimeKey(newLessThanTimeStr);
                String targetStr = DateUtil.dateGroupFormat(isWeek,dateL);
                BorrowRateLineVo withdrawLessThanSupply = dateWithdrawMaps.get(targetStr);
                if(null==withdrawLessThanSupply){
                    log.info("取抵押没有小于存TransactionTime%="+bl.getTransactionTime());
                }else{
                    BigDecimal val = strConversionDecimal(bl.getAmount()).subtract(strConversionDecimal(withdrawLessThanSupply.getAmount()));
                    bl.setAmount(val.toPlainString());
                }
            } else {
                try {
                    BigDecimal val = strConversionDecimal(bl.getAmount()).subtract(strConversionDecimal(withdraw.getAmount()));
                    bl.setAmount(val.toPlainString());
                } catch (NumberFormatException e) {
                    // 处理数字转换异常
                    log.info("金额转换失败: " + e.getMessage());
                }
            }
//            data.add(bl);
            dateKeys.put(bl.getDateUnit(),bl);
            dateWithdrawRemoveMaps.remove(bl.getDateUnit());
        }
    }

    private void matchWithdrawTimeCalculate(Map<String,BorrowRateLineVo> dateSupplyMaps,Map<String,BorrowRateLineVo> dateWithdrawRemoveMaps,
                                            Map<String,BorrowRateLineVo> dateKeys){
        List<String> supplyKeys = supplyCollateralTimeStrSortDescLambda(dateSupplyMaps);

        SimpleDateFormat targetSdf = new SimpleDateFormat("MM/dd HH");

        for (BorrowRateLineVo wl : dateWithdrawRemoveMaps.values()) {
            // 获取新的小于当前时间的key
            String newLessThanTimeStr = tagerNewLessThanKey(wl.getTransactionTime(), supplyKeys);

            try {
                // 解析时间并格式化
                Date time = parseTimeKey(newLessThanTimeStr);
                String targetStr = targetSdf.format(time);

                // 查找对应的supply对象
                BorrowRateLineVo supplyO = dateSupplyMaps.get(targetStr);
                if (supplyO == null) {
                    log.info("YourTotalSupplyLine supplyDate=" + targetStr);
                } else {
                    BigDecimal val = strConversionDecimal(supplyO.getTotalAmount()).subtract(strConversionDecimal(wl.getAmount()));
                    wl.setAmount(val.toPlainString());
//                    data.add(wl);
                    dateKeys.put(wl.getDateUnit(),wl);
                }
            } catch (NumberFormatException e) {
                log.error("金额转换失败: " + e.getMessage());
            }
        }
    }

    @Override
    public List<BorrowRateLineVo> yourTotalSupplyLine(BorrowTotalBo conditionBo){
        List<BorrowRateLineVo> data = new ArrayList<>();
        List<TimePeriodStatisticsVo> resultData = new ArrayList<>();
        Map<String,TimePeriodStatisticsVo> dateUnitKeys = new HashMap<>();//存/取所有数据
        List<String> supplyTransactionTimes = new ArrayList<>();//存交易时间
        List<String> withdrawTransactionTimes = new ArrayList<>();//取交易时间
        Map<String, TimePeriodStatisticsVo> dateUnitRemoveWithdrawMaps = new HashMap<>();// 取map数据,用于dateUnit删除

//        Map<String,TimePeriodStatisticsVo> dateSupplyMaps = new HashMap<>();
//        Map<String,BorrowRateLineVo> dateWithdrawMaps = new HashMap<>();
//        Map<String,BorrowRateLineVo> dateWithdrawRemoveMaps = new HashMap<>();
//        Map<String,BorrowRateLineVo>  dateKeys = new HashMap<>();
        TimePeriodStatisticsBo statisticsBo = TimePeriodUtil.getTimePeriodParameter(conditionBo.getTimePeriodType());
        statisticsBo.setUserAddress(conditionBo.getUserAddress());
        statisticsBo.setSupplyType(HaedalOperationType.Collateral.getValue());
        statisticsBo.setStatisticalRole(true);//用户统计
        List<TimePeriodStatisticsVo> collateralSupplyVos = borrowMapper.borrowTimePeriodStatisticsSupplyOrCollateral(statisticsBo);
        List<TimePeriodStatisticsVo> collateralSupplyLtVos =  borrowMapper.borrowTimePeriodStatisticsSupplyOrCollateralLTTransactionTime(statisticsBo);
        List<TimePeriodStatisticsVo> collateralWithdrawVos =borrowMapper.borrowCollateralWithdraw(statisticsBo);
        List<TimePeriodStatisticsVo> collateralWithdrawLtVos =borrowMapper.borrowCollateralWithdrawLTTransactionTime(statisticsBo);
        Map<String,String> feedIds = collateralSupplyVos.stream().collect(Collectors.toMap(TimePeriodStatisticsVo::getFeedId, TimePeriodStatisticsVo::getFeedId,(v1, v2)->  v1));
        feedIds.putAll(collateralWithdrawVos.stream().collect(Collectors.toMap(TimePeriodStatisticsVo::getFeedId, TimePeriodStatisticsVo::getFeedId,(v1, v2)->  v1)));
        Map<String, PythCoinFeedPriceVo> coinPrice = PythOracleUtil.getPythPrice(feedIds);
//        dateSupplyMaps(collateralSupplyVos,coinPrice,dateSupplyMaps);
//        List<String> withdrawTransactionTimes = dateWithdrawMaps(collateralWithdrawVos,coinPrice,dateWithdrawMaps,dateWithdrawRemoveMaps);
//        matchSupplyTimeCalculate(dateSupplyMaps,withdrawTransactionTimes,dateWithdrawMaps,dateWithdrawRemoveMaps,statisticsBo.getIsWeek(),dateKeys);
//        matchWithdrawTimeCalculate(dateSupplyMaps,dateWithdrawRemoveMaps,dateKeys);

//        List<BorrowRateLineVo> virtualTimePeriodData = DateUtil.timePeriodDayGenerate(statisticsBo.getStartLD(),statisticsBo.getEndLD(),statisticsBo.getIsWeek());
        /**存/取list转map计算usd**/
        Map<String, TimePeriodStatisticsVo> dateUnitSupplyMap = TimePeriodUtil.timePeriodDataConvertDateUnitMaps(collateralSupplyVos,collateralSupplyLtVos,coinPrice,supplyTransactionTimes,null,true);
        Map<String, TimePeriodStatisticsVo> dateUnitWithdrawMap = TimePeriodUtil.timePeriodDataConvertDateUnitMaps(collateralWithdrawVos,collateralWithdrawLtVos,coinPrice,withdrawTransactionTimes,dateUnitRemoveWithdrawMaps,true);
        /**循环存时间点匹配对应取时间点 计算当前剩余数量**/
        TimePeriodUtil.matchDepositTimeCalculate(dateUnitSupplyMap,withdrawTransactionTimes,dateUnitWithdrawMap,dateUnitRemoveWithdrawMaps,statisticsBo.getIsWeek(),dateUnitKeys);
        /**循环取时间点匹配对应取时间点 计算当前剩余数量**/
        TimePeriodUtil.matchWithdrawTimeCalculate(dateUnitRemoveWithdrawMaps,dateUnitSupplyMap,dateUnitKeys);
        /**虚拟时间段生成**/
        List<TimePeriodStatisticsVo> virtualTimePeriodData = DateUtil.timePeriodDayGenerateNew(statisticsBo.getStartLD(),statisticsBo.getEndLD(),statisticsBo.getIsWeek());

        /**虚拟时间数据匹配虚拟时间最近点dateUnitKeys(所有存/取数据)**/
        TimePeriodUtil.virtualTimePeriodMatchValue(virtualTimePeriodData,dateUnitKeys,statisticsBo.getIsWeek(),resultData);

        for (TimePeriodStatisticsVo resultDatum : resultData) {
            BorrowRateLineVo vo = new BorrowRateLineVo();
            vo.setDateUnit(resultDatum.getDateUnit());
            vo.setTransactionTime(resultDatum.getTransactionTime());
            vo.setAmount(resultDatum.getVal());
            vo.setTotalAmount(resultDatum.getTotalVal());
            data.add(vo);
        }
//        List<String> supplyWithdrawKeysDesc = supplyCollateralTimeStrSortDescLambda(dateKeys); //TransactionTime日期从大到小排序
//        List<String> supplyWithdrawKeys = supplyWithdrawKeysDesc;
//        supplyWithdrawKeys.sort((s1,s2)-> s1.compareTo(s2)); //TransactionTime日期从小到大排序


//        for (BorrowRateLineVo rsVal : virtualTimePeriodData) {
//            BorrowRateLineVo obj = new BorrowRateLineVo();
//            BorrowRateLineVo val = dateKeys.get(rsVal.getDateUnit());
//
//            if (val == null) {
//                // 获取小于目标时间的key列表并降序排序
//                List<String> lessThanKeys = tagerLessThanKeys(rsVal.getTransactionTime(), supplyWithdrawKeysDesc);
//                lessThanKeys.sort((k1, k2) -> {
//                    Date t1 = parseTimeKey(k1);
//                    Date t2 = parseTimeKey(k2);
//                    return t2.compareTo(t1); // 降序排序
//                });
//
//                // 获取最新的小于目标时间的key
//                String newLessThanTimeStr = tagerNewLessThanKey(rsVal.getTransactionTime(), lessThanKeys);
//                Date timeL = parseTimeKey(newLessThanTimeStr);
//
//                if (timeL != null) {
////                    String targetStr = new SimpleDateFormat(targetLayout).format(timeL);
//                    String targetStr = DateUtil.dateGroupFormat(statisticsBo.getIsWeek(),timeL);
//                    BorrowRateLineVo supplyWithdrawL = dateKeys.get(targetStr);
//
//                    if (supplyWithdrawL == null) {
//                        // 获取大于目标时间的key
//                        String newGreaterThanTimeStr = tagerNewGreaterThanKey(rsVal.getTransactionTime(), supplyWithdrawKeys);
//                        Date timeG = parseTimeKey(newGreaterThanTimeStr);
//                        if (timeG != null) {
////                            String targetStrG = new SimpleDateFormat(targetLayout).format(timeG);
//                            String targetStrG = DateUtil.dateGroupFormat(statisticsBo.getIsWeek(),timeG);
//                            BorrowRateLineVo supplyWithdrawG = dateKeys.get(targetStrG);
//
//                            if (supplyWithdrawG == null) {
//                                log.info("没有大于TransactionTime={}", rsVal.getTransactionTime());
//                            } else {
//                                obj.setTransactionTime(rsVal.getTransactionTime());
//                                obj.setDateUnit(rsVal.getDateUnit());
//                                obj.setAmount(supplyWithdrawG.getAmount());
//                                obj.setTotalAmount(supplyWithdrawG.getTotalAmount());
//                            }
//                        }
//                    } else {
//                        obj.setTransactionTime(rsVal.getTransactionTime());
//                        obj.setDateUnit(rsVal.getDateUnit());
//                        obj.setAmount(supplyWithdrawL.getAmount());
//                        obj.setTotalAmount(supplyWithdrawL.getTotalAmount());
//                    }
//                }
//            } else {
//                obj = val;
//                obj.setTransactionTime(rsVal.getTransactionTime());
//            }
//            resultData.add(obj);
//        }
        return data;
    }

    // 获取小于目标key的所有key
    public List<String> tagerLessThanKeys(String targetKey, List<String> supplyKeys) {
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

    // 获取第一个大于目标key的key
    public String tagerNewGreaterThanKey(String targetKey, List<String> supplyKeys) {
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
    // 获取第一个小于目标key的key
    private String tagerNewLessThanKey(String transactionTime, List<String> supplyKeys) {
        // 这里需要实现原Go函数TagerNewLessThanKey的逻辑
        // 示例：假设返回第一个小于transactionTime的supplyKey
        for (String key : supplyKeys) {
            Date keyTime = parseTimeKey(key);
            Date transTime = parseTimeKey(transactionTime);
            if (keyTime.before(transTime)) {
                return key;
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


    private Date parseTimeKey(String timeStr) {
        // 请根据实际的时间字符串格式修改SimpleDateFormat的模式
        // 例如："yyyy-MM-dd HH:mm:ss"、"yyyyMMddHHmmss"等
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            date = sdf.parse(timeStr);
        }catch (ParseException p){
        }
        return date;
    }

    /**
     * 计算币种金额的浮点值（转换为基础单位）
     * @param val 金额字符串（科学计数法或普通格式）
     * @param coinType 币种类型（如xxx::xxx::SUI）
     * @return 转换后的浮点值
     */
    public double calculateCoinDecimalFloat(String val, String coinType) {
        // 获取最后一个::后的币种类型
        String loanCoinType = TimePeriodUtil.coinTokenTypeVal(coinType);
        // 获取币种的小数位数
        int coinDecimal = TimePeriodUtil.getCoinDecimal(loanCoinType);

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

}

