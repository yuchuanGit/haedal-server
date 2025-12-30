package com.sui.haedal.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * borrow资产操作记录实体类
 */
@Data
@TableName("borrow_assets_operation_record")
public class BorrowAssetsOperationRecord implements Serializable {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 存取类型 存:Deposit 取:Withdraw
     */
    private String operationType;

    /**
     * 市场id(borrow池)
     */
    private String marketId;

    /**
     * 用户操作地址
     */
    private String caller;

    /**
     * 代操作的地址(VaultId)
     */
    private String onBehalf;

    /**
     * 资产数量(存金额)
     */
    private String assets;

    /**
     * 份额数量(存份额)
     */
    private String shares;

    /**
     * 接收者地址（address类型）
     */
    private String receiver;

    /**
     * 抵押币种
     */
    private String collateralTokenType;

    /**
     * 贷款币种
     */
    private String loanTokenType;

    /**
     * 交易事务唯一值
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