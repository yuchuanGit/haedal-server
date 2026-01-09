package com.sui.haedal.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 清算事件表实体类
 */
@Data
@TableName("borrow_liquidate")
public class BorrowLiquidate implements Serializable {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 市场ID
     */
    private String marketId;

    /**
     * 调用者地址
     */
    private String caller;

    /**
     * 借款方地址
     */
    private String borrower;

    /**
     * 偿还的资产数量
     */
    private String repaidAssets;

    /**
     * 偿还的份额数量
     */
    private String repaidShares;

    /**
     * 查封的资产数量
     */
    private String seizedAssets;

    /**
     * 坏账资产数量
     */
    private String badDebtAssets;

    /**
     * 坏账份额数量
     */
    private String badDebtShares;

    /**
     * 贷款代币类型
     */
    private String loanTokenType;

    /**
     * 抵押代币类型
     */
    private String collateralTokenType;

    /**
     * 摘要
     */
    private String digest;

    /**
     * 数据链上交易unix时间
     */
    private Long transactionTimeUnix;

    /**
     * 数据链上交易时间
     */
    private Date transactionTime;

}