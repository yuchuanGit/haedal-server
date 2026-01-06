package com.sui.haedal.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "VaultVo结构", description = "VaultVo结构")
public class VaultTotalVo {

    @Schema(description = "所有Vault收益")
    private String allVaultYieldUsdAmount;
}
