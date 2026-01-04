package com.sui.haedal.service.impl;

import com.sui.haedal.mapper.FarmingPoolCreateMapper;
import com.sui.haedal.model.bo.FarmingPoolCreateBo;
import com.sui.haedal.model.vo.FarmingPoolCreateVo;
import com.sui.haedal.service.FarmingPoolCreateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
    public Map<String, FarmingPoolCreateVo> farmingPoolCreateReward(Set<String> condition, Boolean conditionType, Map<String,String> rewardFeedIds){
        FarmingPoolCreateBo bo = new FarmingPoolCreateBo();
        bo.setHtokenTypes(condition);
        if(conditionType){
            bo.setMarketIds(condition);
        }
        List<FarmingPoolCreateVo> farmingPoolRewards = farmingPoolCreateMapper.farmingPoolRewardPerSecond(bo);
        Map<String,FarmingPoolCreateVo> htokenRewardMap = new HashMap<>();
        for (FarmingPoolCreateVo farmingPoolReward : farmingPoolRewards) {
            rewardFeedIds.put(farmingPoolReward.getRewardFeedId(),farmingPoolReward.getRewardFeedId());
            htokenRewardMap.put(farmingPoolReward.getStakeTokenType(),farmingPoolReward);
        }
        return htokenRewardMap;
    }

}
