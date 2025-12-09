package com.sui.haedal.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:  计算类型枚举
 * @Author wangyuchuan
 * @Date 2025/12/08
 * @Version 1.0
 **/
@Getter
@AllArgsConstructor
public enum DecimalType {

    ADD(1,"加"),
    SUBTRACT(2,"减"),
    MULTIPLY(3,"乘"),
    DIVIDE(4,"除");

    /**
     * 编码
     */
    private final Integer value;

    /**
     * 说明
     */
    private final String label;
}
