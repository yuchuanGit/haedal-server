package com.sui.haedal.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "FarmingDayRewardVo结构", description = "激励每天奖励vo")
public class FarmingDayRewardVo {

    @Schema(description = "奖励币种")
    private String coinType;

    @Schema(description = "日奖励金额")
    private String rewardAmount;
}
