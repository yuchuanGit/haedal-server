package com.sui.haedal.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 激励资金池创建记录实体类
 */
@Data
@TableName("farming_pool_create")
public class FarmingPoolCreate implements Serializable {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 资金池地址
     */
    private String poolId;

    /**
     * 调用者地址
     */
    private String caller;

    /**
     * 质押代币类型
     */
    private String stakeTokenType;

    /**
     * 计息模型类型
     */
    private String model;

    /**
     * 市场ID
     */
    private String marketId;

    /**
     * 收益接收地址
     */
    private String hearnAddr;

    /**
     * 金库合约地址
     */
    private String vaultAddr;

    /**
     * 交易事务唯一值
     */
    private String digest;

    /**
     * 时间戳（毫秒级Unix时间）
     */
    private Long timestampMsUnix;

    /**
     * 时间戳（毫秒级）
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
