package com.sui.haedal.mapper;

import com.sui.haedal.model.bo.FarmingPoolCreateBo;
import com.sui.haedal.model.vo.FarmingPoolCreateVo;

import java.util.List;

public interface FarmingPoolCreateMapper {

    /**
     * 激励池每秒奖励
     * @param bo
     * @return
     */
    List<FarmingPoolCreateVo> farmingPoolRewardPerSecond(FarmingPoolCreateBo bo);
}
