package com.sui.haedal.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("multiply_update_market")
@Schema(description = "borrow循环贷")
public class MultiplyUpdateMarket implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键id")
    private Integer id;

    /**
     * market_id
     */
    @Schema(description = "market_id")
    private String marketId;

    /**
     * 是否循环贷
     */
    @Schema(description = "是否循环贷")
    private Boolean valid;

    /**
     * 操作用户地址
     */
    @Schema(description = "操作用户地址")
    private String caller;

    /**
     * hearn地址
     */
    @Schema(description = "hearn地址")
    private String hearnAddr;

    /**
     * digest
     */
    @Schema(description = "digest")
    private String digest;

    /**
     * timestamp_ms_unix
     */
    @Schema(description = "timestamp_ms_unix")
    private Long timestampMsUnix;

    /**
     * timestamp_ms
     */
    @Schema(description = "timestamp_ms")
    private Date timestampMs;

    /**
     * 数据链上交易unix时间
     */
    @Schema(description = "数据链上交易unix时间")
    private Long transactionTimeUnix;

    /**
     * 数据链上交易时间
     */
    @Schema(description = "数据链上交易时间")
    private Date transactionTime;

}