package com.sui.haedal.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(title = "VaultYieldVo结构", description = "vault获取收益结构")
public class VaultYieldVo {

    @Schema(description = "金库ID")
    private String vaultId;

    @Schema(description = "金库总资产+利息")
    private String tvl;

    @Schema(description = "金库存取净总资产")
    private String depositWithdrawAsset;

    @Schema(description = "管理费(管理费+绩效费)")
    private String handlingFee;

    @Schema(description = "Vault池收益")
    private String  earnedAsset;

    @Schema(description = "存入币种类型")
    private String assetType;

    @Schema(description = "存入币种精度")
    private Integer assetDecimals;

    @Schema(description = "交易Unix时间")
    private String transactionTimeUnix;

    @Schema(description = "交易时间")
    private Date transactionTime;

    @Schema(description = "存入币种FeedId")
    private String  assetTypeFeedId;

    @Schema(description = "最新时间")
    private String minTime;

    @Schema(description = "最大时间")
    private String maxTime;


}
