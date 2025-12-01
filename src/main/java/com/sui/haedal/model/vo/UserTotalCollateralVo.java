package com.sui.haedal.model.vo;


import lombok.Data;

/**
 * 用户抵押总额
 *
 */
@Data
public class UserTotalCollateralVo {
    /**
     * 交易时间
     */
    private String transactionTime;

    /**
     * 日期单位（如%m/%d格式的日期标识）
     */
    private String dateUnit;

    /**
     * 金额
     */
    private String amount;

    /**
     * 市场ID
     */
    private String marketId;

    /**
     * 币种类型
     */
    private String coinType;

    /**
     * 币种FeedId
     */
    private String feedId;
}
