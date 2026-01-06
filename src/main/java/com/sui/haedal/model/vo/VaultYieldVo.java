package com.sui.haedal.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(title = "VaultYieldVo结构", description = "vault获取收益结构")
public class VaultYieldVo {

    @Schema(description = "金库ID")
    private String vaultId;

    @Schema(description = "存入币种类型")
    private String assetType;

    @Schema(description = "存入币种FeedId")
    private String  assetTypeFeedId;

    @Schema(description = "存入币种精度")
    private Integer assetTypeDecimals;

    @Schema(description = "Vault池收益")
    private String  earnedAsset;

    @Schema(description = "交易时间")
    private Date transactionTime;
}
