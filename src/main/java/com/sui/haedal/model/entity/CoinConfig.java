package com.sui.haedal.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 币种配置表实体类
 */

@Data
@TableName("coin_config")
public class CoinConfig {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 币种类型
     */
    private String coinType;

    /**
     * 币种feedId
     */
    private String feedId;

    /**
     * 币种objectId
     */
    private String feedObjectId;
}
