package com.sui.haedal.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@Schema(title = "BorrowLiquidateVo结构", description = "清算事件表结构")
public class BorrowLiquidateVo {

    @Schema(description = "主键ID")
    private Integer id;

    @Schema(description = "市场ID")
    private String marketId;

    @Schema(description = "调用者地址")
    private String caller;

    @Schema(description = "借款方地址")
    private String borrower;

    @Schema(description = "偿还的资产数量")
    private String repaidAssets;

    @Schema(description = "偿还的份额数量")
    private String repaidShares;

    @Schema(description = "查封的资产数量")
    private String seizedAssets;

    @Schema(description = "坏账资产数量")
    private String badDebtAssets;

    @Schema(description = "坏账份额数量")
    private String badDebtShares;

    @Schema(description = "贷款代币类型")
    private String loanTokenType;

    @Schema(description = "抵押代币类型")
    private String collateralTokenType;

    @Schema(description = "摘要")
    private String digest;

    @Schema(description = "数据链上交易unix时间")
    private Long transactionTimeUnix;

    @Schema(description = "数据链上交易时间")
    private Date transactionTime;

}