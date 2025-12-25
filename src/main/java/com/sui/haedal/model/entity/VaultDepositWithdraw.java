package com.sui.haedal.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * vault用户存/取记录表实体类
 */
@Data
@TableName("vault_deposit_withdraw")
public class VaultDepositWithdraw implements Serializable {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 金库ID
     */
    private String vaultId;

    /**
     * 存取类型 存:deposit 取:withdraw
     */
    private String operationType;

    /**
     * 用户地址
     */
    private String user;

    /**
     * 存入/取数量
     */
    private String assetAmount;

    /**
     * 存/取份额
     */
    private String shares;

    /**
     * 资产类型
     */
    private String assetType;

    /**
     * 质押代币类型
     */
    private String htokenType;

    /**
     * 摘要
     */
    private String digest;

    /**
     * 时间戳（unix毫秒）
     */
    private Long timestampMsUnix;

    /**
     * 时间
     */
    private Date timestampMs;

    /**
     * 数据链上交易unix时间
     */
    private Long transactionTimeUnix;

    /**
     * 数据链上交易时间
     */
    private Date transactionTime;
}