package com.sui.haedal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sui.haedal.common.BigDecimalUtil;
import com.sui.haedal.common.DateUtil;
import com.sui.haedal.common.PythOracleUtil;
import com.sui.haedal.common.TimePeriodUtil;
import com.sui.haedal.mapper.BorrowMapper;
import com.sui.haedal.mapper.CoinConfigMapper;
import com.sui.haedal.mapper.EarnMapper;
import com.sui.haedal.model.bo.EarnTotalBo;
import com.sui.haedal.model.bo.TimePeriodStatisticsBo;
import com.sui.haedal.model.entity.CoinConfig;
import com.sui.haedal.model.entity.Vault;
import com.sui.haedal.model.enums.DecimalType;
import com.sui.haedal.model.vo.*;
import com.sui.haedal.service.EarnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service("earnService")
public class EarnServiceImpl implements EarnService {

    @Resource
    private EarnMapper earnMapper;

    @Resource
    private CoinConfigMapper coinConfigMapper;
    /**
     * earn vault列表
     * @return
     */
    @Override
    public List<VaultVo> list(){
        List<VaultVo> vos = new ArrayList<>();
        List<Vault> list = earnMapper.selectList(Wrappers.<Vault>query().lambda());
        Map<String,CoinConfig> coinConfigMap = getCoinConfigMap();
        for (Vault vault : list) {
            VaultVo vo = new VaultVo();
            BeanUtils.copyProperties(vault, vo);
            vo.setApy("12.24%");
            vo.setTvl(vault.getTotalAsset());
            BigDecimal tvlCapacity = BigDecimalUtil.calculate(DecimalType.SUBTRACT.getValue(),new BigDecimal(vault.getSupplyCap()),
                    new BigDecimal(vault.getTotalAsset()),0,RoundingMode.DOWN);
            vo.setTvlCapacity(tvlCapacity.toString());//该池子总存款-剩余容量
            CoinConfig coinConfig = coinConfigMap.get(vault.getAssetType());
            if(coinConfig!=null){
                vo.setAssetTypeFeedId(coinConfig.getFeedId());
                vo.setAssetTypeFeedObjectId(coinConfig.getFeedObjectId());
            }
            vos.add(vo);
        }
        return vos;
    }

    /**
     * Vault详情
     * @param vaultId
     * @return
     */
    @Override
    public VaultVo vaultDetail(String vaultId){
        VaultVo vaultVo = new VaultVo();
        List<VaultVo> list = earnMapper.vaultDetail(vaultId);
        if(list.size()>0){
            vaultVo = list.get(0);
            Map<String,CoinConfig> coinConfigMap = getCoinConfigMap();
            CoinConfig coinConfig = coinConfigMap.get(vaultVo.getAssetType());
            if(coinConfig!=null){
                vaultVo.setAssetTypeFeedId(coinConfig.getFeedId());
                vaultVo.setAssetTypeFeedObjectId(coinConfig.getFeedObjectId());
            }
            vaultVo.setApy("12.24%");
            vaultVo.setTvl(vaultVo.getTotalAsset());
            BigDecimal tvlCapacity = BigDecimalUtil.calculate(DecimalType.SUBTRACT.getValue(),new BigDecimal(vaultVo.getSupplyCap()),
                    new BigDecimal(vaultVo.getTotalAsset()),0,RoundingMode.DOWN);
            vaultVo.setTvlCapacity(tvlCapacity.toString());//该池子总存款-剩余容量
        }
        return vaultVo;
    }

    /**
     * vault池分配借款池比例资金信息
     * @param vaultId
     * @return
     */
    @Override
    public List<StrategyVo> vaultStrategy(String vaultId){
        List<StrategyVo> data = earnMapper.vaultStrategy(vaultId);
        BigDecimal precision = decimalPower(16);
        BigDecimal baseVal = new BigDecimal(0.01);
        for (StrategyVo vo : data) {
            BigDecimal supplyRate = new BigDecimal(vo.getApy()).divide(precision);
            String apy = TimePeriodUtil.roundingModeStr(supplyRate, 2,RoundingMode.UP);
            if(supplyRate.compareTo(baseVal)<0){
                apy = "<0.01";
            }
            vo.setApy(apy+"%");
        }
        return data;
    }

    private BigDecimal decimalPower(int power){
        return new BigDecimal(Math.pow(10,power));
    }
    /**
     * 用户时间段最新存入/去除数量
     * 1.
     * @return
     */
    @Override
    public List<TimePeriodStatisticsVo> yourDepositsWithdraw(EarnTotalBo bo){
        List<TimePeriodStatisticsVo> resultData = new ArrayList<>();
        Map<String,TimePeriodStatisticsVo> dateUnitKeys = new HashMap<>();//存/取所有数据
        List<String> depositTransactionTimes = new ArrayList<>();//存交易时间
        List<String> withdrawTransactionTimes = new ArrayList<>();//取交易时间
        /**获取时间段类型 时间段数据**/
        TimePeriodStatisticsBo statisticsBo = TimePeriodUtil.getTimePeriodParameter(bo.getTimePeriodType());
        statisticsBo.setBusinessPoolId(bo.getVaultId());
        statisticsBo.setUserAddress(bo.getUserAddress());
        /**根据时间段查询存/取数据**/
        List<TimePeriodStatisticsVo> userDepositVos = earnMapper.userDeposit(statisticsBo);
        List<TimePeriodStatisticsVo> userWithdrawVos = earnMapper.userWithdraw(statisticsBo);
        Map<String,String> feedIds = userDepositVos.stream().collect(Collectors.toMap(TimePeriodStatisticsVo::getFeedId, TimePeriodStatisticsVo::getFeedId,(v1, v2)->  v1));
        feedIds.putAll(userWithdrawVos.stream().collect(Collectors.toMap(TimePeriodStatisticsVo::getFeedId, TimePeriodStatisticsVo::getFeedId,(v1, v2)->  v1)));
        /**存/取币种所有feedId查询Pyth价格**/
        Map<String, PythCoinFeedPriceVo> coinPrice = PythOracleUtil.getPythPrice(feedIds);
        Map<String, TimePeriodStatisticsVo> dateUnitRemoveWithdrawMaps = new HashMap<>();// 取map数据,用于dateUnit删除
        /**存/取list转map计算usd**/
        Map<String, TimePeriodStatisticsVo> dateUnitDepositMap = TimePeriodUtil.timePeriodDataConvertDateUnitMaps(userDepositVos,new ArrayList<>(),coinPrice,depositTransactionTimes,null);
        Map<String, TimePeriodStatisticsVo> dateUnitWithdrawMap = TimePeriodUtil.timePeriodDataConvertDateUnitMaps(userWithdrawVos,new ArrayList<>(),coinPrice,withdrawTransactionTimes,dateUnitRemoveWithdrawMaps);
        /**循环存时间点匹配对应取时间点 计算当前剩余数量**/
        TimePeriodUtil.matchDepositTimeCalculate(dateUnitDepositMap,withdrawTransactionTimes,dateUnitWithdrawMap,dateUnitRemoveWithdrawMaps,statisticsBo.getIsWeek(),dateUnitKeys);
        /**循环取时间点匹配对应取时间点 计算当前剩余数量**/
        TimePeriodUtil.matchWithdrawTimeCalculate(dateUnitRemoveWithdrawMaps,dateUnitDepositMap,dateUnitKeys);
        /**虚拟时间段生成**/
        List<TimePeriodStatisticsVo> virtualTimePeriodData = DateUtil.timePeriodDayGenerateNew(statisticsBo.getStartLD(),statisticsBo.getEndLD(),statisticsBo.getIsWeek());
        /**虚拟时间数据匹配虚拟时间最近点dateUnitKeys(所有存/取数据)**/
        TimePeriodUtil.virtualTimePeriodMatchValue(virtualTimePeriodData,dateUnitKeys,statisticsBo.getIsWeek(),resultData);
        return resultData;
    }


    /**
     * 统计单个Vault池存入数据
     * @param bo
     * @return
     */
    @Override
    public List<TimePeriodStatisticsVo> totalDeposits(EarnTotalBo bo){
        List<TimePeriodStatisticsVo> data = new ArrayList<>();

        return data;
    }


    /**
     * 统计单个Vault池APY
     * @param bo
     * @return
     */
    @Override
    public List<TimePeriodStatisticsVo> totalAPY(EarnTotalBo bo){
        List<TimePeriodStatisticsVo> data = new ArrayList<>();

        return data;
    }

    private Map<String,CoinConfig> getCoinConfigMap(){
        List<CoinConfig> coinList = coinConfigMapper.selectList(Wrappers.<CoinConfig>query().lambda());
        return coinList.stream().collect(Collectors.toMap(CoinConfig::getCoinType, Function.identity(),(v1, v2)->v1));
    }

}
