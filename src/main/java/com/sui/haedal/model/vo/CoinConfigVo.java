package com.sui.haedal.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 币种vo结构
 */

@Data
@Schema(title = "CoinConfigVo结构", description = "币种vo结构")
public class CoinConfigVo {
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
     * 币种精度
     */
    private Integer coinDecimals;

    /**
     * 币种feedId
     */
    private String feedId;

    /**
     * 币种objectId
     */
    private String feedObjectId;
}
