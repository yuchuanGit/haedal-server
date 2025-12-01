package com.sui.haedal.model.vo;

import lombok.Data;

@Data
public class RateModelDetailVo {

    /**
     * 模板最佳利用率
     */
    private String dateUnit;
    /**
     * 存利率
     */
    private String supplyRate;
    /**
     * 借利率
     */
    private String borrowRate;
    /**
     * 当前利用率
     */
    private String currentU;
}
