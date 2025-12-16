package com.sui.haedal.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(title = "StrategyVo结构", description = "vault资金去向借贷池")
public class StrategyVo {

    @Schema(description = "vault address")
    private String vaultId;

    @Schema(description = "borrow抵押币种")
    private String collateralTokenType;

    @Schema(description = "borrow贷款币种")
    private String loanTokenType;

    @Schema(description = "分配borrow数量")
    private String cap;

    @Schema(description = "分配borrow权重(比例)")
    private String weightBps;

    @Schema(description = "分配的流动性金额")
    private String liquidity;

    @Schema(description = "borrow存款利率")
    private String apy;

    @Schema(description = "borrow lltv")
    private String lltv;

    @Schema(description = "borrow marketId")
    private String marketId;
}
