package com.sui.haedal.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:  Haedal操作类型
 * @Author wangyuchuan
 * @Date 2025/12/10
 * @Version 1.0
 **/
@Getter
@AllArgsConstructor
public enum HaedalOperationType {

    SUPPLY(1,"存资产"),
    Collateral(2,"存抵押"),
    BORROW(3,"借");

    /**
     * 编码
     */
    private final Integer value;

    /**
     * 说明
     */
    private final String label;
}
