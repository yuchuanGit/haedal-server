package com.sui.haedal.model.bo;

import com.sui.haedal.common.page.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Schema(title = "VaultDepositWithdrawQueryBo结构", description = "vault存取查询条件")
public class VaultDepositWithdrawQueryBo extends BasePageQuery {

    @Schema(description = "earn vaultId")
    private String vaultId;

    @Schema(description = "用户地址")
    private String userAddress;

    @Schema(description = "操作类型：存资产:Deposit 取资产:Withdraw")
    @NotEmpty(message = "操作类型集合至少一条")
    private List<String> operationTypes;
}
