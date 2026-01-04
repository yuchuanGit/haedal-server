package com.sui.haedal.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 激励池创建vo结构
 */
@Data
@Schema(title = "FarmingPoolCreateVo结构", description = "激励池创建vo结构")
public class FarmingPoolCreateVo implements Serializable {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "激励池id")
    private String poolId;

    @Schema(description = "调用者地址")
    private String caller;

    @Schema(description = "earn vault htoken币种")
    private String stakeTokenType;

    @Schema(description = "计息模型类型")
    private String model;

    @Schema(description = "市场ID")
    private String marketId;

    @Schema(description = "hearn地址")
    private String hearnAddr;

    @Schema(description = "earn vault金库地址")
    private String vaultAddr;

    @Schema(description = "digest")
    private String digest;

    @Schema(description = "时间戳（毫秒级Unix时间）")
    private Long timestampMsUnix;

    @Schema(description = "时间戳（毫秒级）")
    private Date timestampMs;

    @Schema(description = "数据链上交易unix时间")
    private Long transactionTimeUnix;

    @Schema(description = "数据链上交易时间")
    private Date transactionTime;

    @Schema(description = "秒级激励数量")
    private String rewardPerSecond;

    @Schema(description = "奖励代币类型")
    private String rewardTokenType;

    @Schema(description = "奖励代币feedId")
    private String rewardFeedId;

    @Schema(description = "奖励代币精度")
    private Integer rewardCoinDecimals;
}
