package com.sui.haedal.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class RateModelVo {

    /**
     * 模板最佳利用率
     */
    private String targetU;

    /**
     * 当前利用率
     */
    private String currentU;

    /**
     * 利用率详情
     */
    private List<RateModelDetailVo> rates;
}
