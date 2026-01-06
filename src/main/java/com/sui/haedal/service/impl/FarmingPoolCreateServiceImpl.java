package com.sui.haedal.service.impl;

import com.sui.haedal.common.BigDecimalUtil;
import com.sui.haedal.common.PythOracleUtil;
import com.sui.haedal.common.TimePeriodUtil;
import com.sui.haedal.mapper.FarmingPoolCreateMapper;
import com.sui.haedal.model.bo.FarmingPoolCreateBo;
import com.sui.haedal.model.enums.DecimalType;
import com.sui.haedal.model.vo.FarmingDayRewardVo;
import com.sui.haedal.model.vo.FarmingPoolCreateVo;
import com.sui.haedal.model.vo.PythCoinFeedPriceVo;
import com.sui.haedal.service.FarmingPoolCreateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


@Slf4j
@Service("farmingPoolCreateService")
public class FarmingPoolCreateServiceImpl implements FarmingPoolCreateService {

    @Resource
    private FarmingPoolCreateMapper farmingPoolCreateMapper;

    /**
     * vault/borrow绑定激励池奖励
     * @param condition 查询条件(vault(htokenType)/borrow(marketId))
     * @param conditionType 查询条件类型 false:vault(htokenType) true:borrow(marketId)
     * @param rewardFeedIds
     * @return
     */
    @Override
    public Map<String, List<FarmingPoolCreateVo>> farmingPoolCreateReward(Set<String> condition, Boolean conditionType, Map<String,String> rewardFeedIds){
        FarmingPoolCreateBo bo = new FarmingPoolCreateBo();
        bo.setHtokenTypes(condition);
        if(conditionType){
            bo.setMarketIds(condition);
        }
        List<FarmingPoolCreateVo> farmingPoolRewards = farmingPoolCreateMapper.farmingPoolRewardPerSecond(bo);
        Map<String,List<FarmingPoolCreateVo>> htokenRewardMap = new HashMap<>();
        for (FarmingPoolCreateVo farmingPoolReward : farmingPoolRewards) {
            rewardFeedIds.put(farmingPoolReward.getRewardFeedId(),farmingPoolReward.getRewardFeedId());
            List<FarmingPoolCreateVo> rewardList = htokenRewardMap.get(farmingPoolReward.getStakeTokenType());
            if(null==rewardList){
                rewardList = new ArrayList<>();
            }
            rewardList.add(farmingPoolReward);
            htokenRewardMap.put(farmingPoolReward.getStakeTokenType(),rewardList);
        }
        return htokenRewardMap;
    }

    /**
     * 激励奖励计算
     * @param coinPrice
     * @param rewardList
     * @param dayRewards
     * @return
     */
    @Override
    public BigDecimal farmingRewardCalculate(Map<String, PythCoinFeedPriceVo> coinPrice, List<FarmingPoolCreateVo> rewardList, List<FarmingDayRewardVo> dayRewards){
        Integer daySeconds = 60*60*24;
        Integer annualSeconds = daySeconds*365;
        BigDecimal rewardUsdAmount = new BigDecimal(0);
        for (FarmingPoolCreateVo htokenReward : rewardList) {
            BigDecimal coinRewardUsdAmount = PythOracleUtil.coinUsd(coinPrice,htokenReward.getRewardFeedId(),htokenReward.getRewardPerSecond(),
                    htokenReward.getRewardCoinDecimals());
            rewardUsdAmount = rewardUsdAmount.add(coinRewardUsdAmount);
            BigDecimal dayReward = coinRewardUsdAmount.multiply(new BigDecimal(daySeconds));
            FarmingDayRewardVo farmingDayRewardVo = new FarmingDayRewardVo();
            farmingDayRewardVo.setCoinType(TimePeriodUtil.coinTokenTypeVal(htokenReward.getRewardTokenType()));
            farmingDayRewardVo.setRewardAmount(dayReward.toString());
            dayRewards.add(farmingDayRewardVo);
        }
        BigDecimal annualReward = rewardUsdAmount.multiply(new BigDecimal(annualSeconds));
        return annualReward;
    }
}
