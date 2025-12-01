package com.sui.haedal.model.vo;


import lombok.Data;

/**
 * 抵押借贷总额视图对象
 *
 */
@Data
public class TotalCollateralBorrowVo {
    /**
     * 所有market池子抵押总额
     */
    private String totalCollatera;

    /**
     * 所有market池子存入总额
     */
    private String totalSupply;

    /**
     * 所有market池子借总额
     */
    private String totalBorrow;

    /**
     * 用户market池子存入总额
     */
    private String userTotalSupply;

    /**
     * 用户market池子抵押总额
     */
    private String userTotalCollatera;

    /**
     * 用户所有market池子抵押总额（注：原Go结构体注释可能存在笔误，建议确认实际含义）
     */
    private String userTotalBorrow;

    /**
     * 抵押币种类型
     */
    private String collateraCoinType;

    /**
     * 抵押币种FeedId
     */
    private String collateraFeedId;

    /**
     * 贷款币种类型
     */
    private String loanCoinType;

    /**
     * 贷款币种FeedId
     */
    private String loanFeedId;

    /**
     * 币种类型
     */
    private String coinType;

    /**
     * 币种FeedId
     */
    private String feedId;
}
