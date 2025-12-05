package com.sui.haedal.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * vault表实体类
 */
@Data
@TableName("vault")
public class Vault implements Serializable {

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
     * vault名称
     */
    private String vaultName;

    /**
     * 所有者
     */
    private String owner;

    /**
     * 管理者
     */
    private String curator;

    /**
     * 分配者
     */
    private String allocator;

    /**
     * 守护者
     */
    private String guardian;

    /**
     * 资产小数位数
     */
    private Integer assetDecimals;

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
     * 交易时间戳（unix毫秒/秒）
     */
    private Long transactionTimeUnix;

    /**
     * 交易时间
     */
    private Date transactionTime;

}