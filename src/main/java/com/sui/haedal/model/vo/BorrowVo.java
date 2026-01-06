package com.sui.haedal.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@TableName("borrow")
public class BorrowVo implements Serializable {

    /**
     * 主键id
     */

    @JsonProperty("Id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 摘要
     */
    @JsonProperty("Digest")
    private String digest;

    /**
     * 抵押代币类型
     */
    @JsonProperty("CollateralTokenType")
    private String collateralTokenType;

    /**
     * 费用
     */
    @JsonProperty("Fee")
    private String fee;

    /**
     * 清算借贷价值比
     */
    @JsonProperty("Lltv")
    private String lltv;

    /**
     * 借贷价值比
     */
    @JsonProperty("Ltv")
    private String ltv;

    /**
     * 币种
     */
    @JsonProperty("Pair")
    private String pair;

    /**
     * 贷款代币类型
     */
    @JsonProperty("LoanTokenType")
    private String loanTokenType;

    /**
     * 市场ID
     */
    @JsonProperty("MarketId")
    private String marketId;

    /**
     * borrow贷款池标题
     */
    @JsonProperty("MarketTitle")
    private String marketTitle;

    /**
     * 市场日志
     */
    @JsonProperty("MarketLog")
    private String marketLog;

    /**
     * 存利率
     */
    @JsonProperty("SupplyRate")
    private String supplyRate;

    /**
     * 借利率
     */
    @JsonProperty("BorrowRate")
    private String borrowRate;

    /**
     * borrow池存放总额
     */
    @JsonProperty("TotalSupplyAmount")
    private String totalSupplyAmount;

    /**
     * borrow池存放抵押总额
     */
    @JsonProperty("TotalSupplyCollateralAmount")
    private String totalSupplyCollateralAmount;

    /**
     * borrow贷款总额
     */
    @JsonProperty("TotalLoanAmount")
    private String totalLoanAmount;

    /**
     * 预言机ID
     */
    @JsonProperty("OracleId")
    private String oracleId;

    /**
     * 流动性
     */
    @JsonProperty("Liquidity")
    private String liquidity;

    /**
     * 流动性占比
     */
    @JsonProperty("LiquidityProportion")
    private String liquidityProportion;

    /**
     * 基础代币小数位数
     */
    @JsonProperty("BaseTokenDecimals")
    private Integer baseTokenDecimals;

    /**
     * 报价代币小数位数
     */
    @JsonProperty("QuoteTokenDecimals")
    private Integer quoteTokenDecimals;

    /**
     * 交易时间
     */
    @JsonProperty("TransactionTime")
    private Long transactionTime;

    /**
     * 是否定时执行（0-否，1-是）
     */
    @JsonProperty("ScheduledExecution")
    private Integer scheduledExecution;

    @JsonProperty("CollateralFeedId")
    private String collateralFeedId;

    @JsonProperty("CollateralFeedObjectId")
    private String collateralFeedObjectId;

    @JsonProperty("LoanFeedId")
    private String  loanFeedId;

    @JsonProperty("LoanFeedObjectId")
    private String loanFeedObjectId;

    @JsonProperty("CollateralCoinDecimals")
    private Integer collateralCoinDecimals;//抵押币种精度

    @JsonProperty("LoanCoinDecimals")
    private Integer loanCoinDecimals;//贷款币种精度

    @JsonProperty("LiqPenalty")
    private String liqPenalty;//清算者优惠比例

    @Schema(description = "激励奖励池id")
    private String farmingPoolId;

    @Schema(description = "激励奖励apr")
    private BigDecimal farmingRewardApr;

    @Schema(description = "激励不同币种天奖励列表")
    private List<FarmingDayRewardVo> dayRewards;

    @Schema(description = "borrow对应vaultAddress")
    private List<String> vaultAddress;

}
