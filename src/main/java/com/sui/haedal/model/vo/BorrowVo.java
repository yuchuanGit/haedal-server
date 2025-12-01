package com.sui.haedal.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("borrow")
public class BorrowVo implements Serializable {

    /**
     * 主键id
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 摘要
     */
    private String digest;

    /**
     * 抵押代币类型
     */
    private String collateralTokenType;

    /**
     * 费用
     */
    private String fee;

    /**
     * 清算借贷价值比
     */
    private String lltv;

    /**
     * 借贷价值比
     */
    private String ltv;

    /**
     * 币种
     */
    private String pair;

    /**
     * 贷款代币类型
     */
    private String loanTokenType;

    /**
     * 市场ID
     */
    private String marketId;

    /**
     * borrow贷款池标题
     */
    private String marketTitle;

    /**
     * 市场日志
     */
    private String marketLog;

    /**
     * 存利率
     */
    private String supplyRate;

    /**
     * 借利率
     */
    private String borrowRate;

    /**
     * borrow池存放总额
     */
    private String totalSupplyAmount;

    /**
     * borrow池存放抵押总额
     */
    private String totalSupplyCollateralAmount;

    /**
     * borrow贷款总额
     */
    private String totalLoanAmount;

    /**
     * 预言机ID
     */
    private String oracleId;

    /**
     * 流动性
     */
    private String liquidity;

    /**
     * 流动性占比
     */
    private String liquidityProportion;

    /**
     * 基础代币小数位数
     */
    private Integer baseTokenDecimals;

    /**
     * 报价代币小数位数
     */
    private Integer quoteTokenDecimals;

    /**
     * 交易时间
     */
    private Long transactionTime;

    /**
     * 是否定时执行（0-否，1-是）
     */
    private Integer scheduledExecution;

    private String collateralFeedId;
    private String collateralFeedObjectId;
    private String  loanFeedId;
    private String loanFeedObjectId;
    private Integer collateralCoinDecimals;//抵押币种精度
    private Integer loanCoinDecimals;//贷款币种精度
    private String liqPenalty;//清算者优惠比例


}
