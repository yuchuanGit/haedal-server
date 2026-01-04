package com.sui.haedal.service;

import com.sui.haedal.model.vo.FarmingPoolCreateVo;

import java.util.Map;
import java.util.Set;

public interface FarmingPoolCreateService {

    /**
     * vault/borrow绑定激励池奖励
     * @param condition 查询条件(vault(htokenType)/borrow(marketId))
     * @param conditionType 查询条件类型 false:vault(htokenType) true:borrow(marketId)
     * @param rewardFeedIds
     * @return
     */
    Map<String, FarmingPoolCreateVo>  farmingPoolCreateReward(Set<String> condition,Boolean conditionType,Map<String,String> rewardFeedIds);
}
