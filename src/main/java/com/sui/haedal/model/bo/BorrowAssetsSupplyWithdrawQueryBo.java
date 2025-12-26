package com.sui.haedal.model.bo;

import com.sui.haedal.common.page.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "BorrowAssetsSupplyWithdrawQueryBo结构", description = "borrow资产存取查询条件")
public class BorrowAssetsSupplyWithdrawQueryBo extends BasePageQuery {

    @Schema(description = "earn vaultId")
    private String vaultId;

    @Schema(description = "marketId")
    private String marketId;

    @Schema(description = "用户地址")
    private String userAddress;
}
