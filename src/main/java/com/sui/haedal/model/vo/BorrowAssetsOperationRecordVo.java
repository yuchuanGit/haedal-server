package com.sui.haedal.model.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(title = "BorrowAssetsSupplyWithdrawVo结构", description = "borrow资产存取结构")
public class BorrowAssetsOperationRecordVo {
    private Integer id;

    @Schema(description = "操作类型：存资产:Deposit 存抵押:Collateral 取资产:Withdraw 取抵押:CollateralWithdraw 借:Borrow 还：Repay 清算:Liquidation")
    private String operationType;

    @Schema(description = "市场id(borrow池)")
    private String marketId;

    @Schema(description = "市场名称")
    private String marketTitle;

    @Schema(description = "用户操作地址")
    private String caller;

    @Schema(description = "代操作的地址(VaultId)")
    private String onBehalf;

    @Schema(description = "资产数量(存金额)")
    private String assets;

    @Schema(description = "份额数量(存份额)")
    private String shares;

    @Schema(description = "接收者地址（address类型）")
    private String receiver;

    @Schema(description = "抵押币种")
    private String collateralTokenType;

    @Schema(description = "贷款币种")
    private String loanTokenType;

    @Schema(description = "digest")
    private String digest;

    @Schema(description = "数据链上交易unix时间")
    private Long transactionTimeUnix;

    @Schema(description = "数据链上交易时间")
    @JsonFormat(
            pattern = "MMM dd,yyyy HH:mm",  // 核心格式串，匹配 Nov 21,2025 01:23
            timezone = "UTC",              // 时区指定为UTC
//            timezone = "GMT+8",              //  改为东八区（也可以写 "Asia/Shanghai"）
            locale = "en"                 // 强制使用英文环境，避免月份显示为中文
    )
    private Date transactionTime;
}
