package com.sui.haedal.model.bo;

import com.sui.haedal.common.page.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "BorrowAssetsSupplyWithdrawQueryBo结构", description = "borrow资产存取查询条件")
public class BorrowAssetsSupplyWithdrawQueryBo extends BasePageQuery {

    @Schema(description = "earn vaultId")
    private String vaultId;

    @Schema(description = "marketId")
    private String marketId;

    @Schema(description = "用户地址")
    private String userAddress;

    @Schema(description = "是否borrow维度查询 false:earn vault true:borrow")
    private Boolean isBorrow = false;

    @Schema(description = "操作类型：存资产:Deposit 存抵押:Collateral 取资产:Withdraw 取抵押:CollateralWithdraw 借:Borrow 还：Repay 清算:Liquidation",hidden = true)
    private List<String> operationTypes;
}
