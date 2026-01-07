package com.sui.haedal.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * vaultVo
 */
@Data
@Schema(title = "VaultVo结构", description = "VaultVo结构")
public class VaultVo implements Serializable {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 金库ID
     */
    @Schema(description = "金库ID")
    private String vaultId;

    /**
     * 所有者
     */
    @Schema(description = "所有者")
    private String owner;

    /**
     * 管理者
     */
    @Schema(description = "管理者")
    private String curator;

    /**
     * 分配者
     */
    @Schema(description = "分配者")
    private String allocator;

    /**
     * 守护者
     */
    @Schema(description = "守护者")
    private String guardian;

    /**
     * 资产小数位数
     */
    @Schema(description = "资产小数位数")
    private Integer assetDecimals;

    /**
     * 资产类型
     */
    @Schema(description = "资产类型")
    private String assetType;

    /**
     * 质押代币类型
     */
    @Schema(description = "质押代币类型")
    private String htokenType;

    @Schema(description = "htoken精度")
    private Integer htokenDecimals;

    /**
     * 摘要
     */
    @Schema(description = "digest")
    private String digest;

    /**
     * 交易时间戳（unix毫秒/秒）
     */
    @Schema(description = "交易时间戳")
    private Long transactionTimeUnix;

    /**
     * 交易时间
     */
    @Schema(description = "交易时间")
    private Date transactionTime;

    /**
     * vault名称
     */
    @Schema(description = "vault名称")
    private String vaultName;

    /**
     * 年化收益率
     * APY= [（单日收益 ÷ 存款本金）× 365] × 100%
     */
    @Schema(description = "apy年化收益率")
    private String apy;

    /**
     * 该池子总存款
     */
    @Schema(description = "该池子总存款")
    private String tvl;

    /**
     * 该池子总存款-剩余容量
     */
    @Schema(description = "该池子总存款-剩余容量")
    private String tvlCapacity;

    /**
     * 用户存款规模(前端合约)
     */
    @Schema(description = "用户存款规模(前端合约)")
    private String yourDeposit;

    /**
     * 用户存款收益
     */
    @Schema(description = "用户存款收益")
    private String earnings;

    @Schema(description = "assetTypeFeedId")
    private String  assetTypeFeedId;

    @Schema(description = "assetTypeFeedObjectId")
    private String assetTypeFeedObjectId;

    @Schema(description = "存入币种精度")
    private Integer assetTypeDecimals;


    /**
     * 抵押代币类型
     */
    @Schema(description = "borrow抵押代币类型")
    private String collateralTokenType;

    /**
     * 清算借贷价值比
     */
    @Schema(description = "borrow清算借贷价值比")
    private String lltv;

    @Schema(description = "最大存入数量")
    private String supplyCap;


    @Schema(description = "存入总的数量")
    private String totalAsset;


    @Schema(description = "存入总的份额")
    private String totalShares;


    @Schema(description = "总的闲置数量")
    private String assetReserve;


    @Schema(description = "单次最大存款量")
    private String maxDeposit;


    @Schema(description = "单次最小存款量")
    private String minDeposit;

    @Schema(description = "vault最新价格")
    private String vaultSharePrice;

    @Schema(description = "vault最新价格增长")
    private String vaultSharePriceGrowth;

    @Schema(description = "vault最新收益")
    private String yieldEarned;


    @Schema(description = "Vault 管理费用")
    private String managementFeeBps;

    @Schema(description = "Vault 绩效费用")
    private String performanceFeeBps;

    @Schema(description = "激励奖励池id")
    private String farmingPoolId;

    @Schema(description = "激励奖励apr")
    private BigDecimal farmingRewardApr;

    @Schema(description = "分配不同borrow策略")
    private List<StrategyVo> strategyVos;

    @Schema(description = "激励不同币种天奖励列表")
    private List<FarmingDayRewardVo> dayRewards;

    @Schema(description = "用户激励标识")
    private Boolean userFarmingFlag;

    @Schema(description = "用户存放激励poolId")
    private String userFarmingPoolId;

    @Schema(description = "池子每份额收益")
    private BigDecimal  vaultEachSharesYield;


}