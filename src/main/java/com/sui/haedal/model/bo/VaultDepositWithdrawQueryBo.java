package com.sui.haedal.model.bo;

import com.sui.haedal.common.page.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "VaultDepositWithdrawQueryBo结构", description = "vault存取查询条件")
public class VaultDepositWithdrawQueryBo extends BasePageQuery {

    @Schema(description = "earn vaultId")
    private String vaultId;

    @Schema(description = "用户地址")
    private String userAddress;
}
