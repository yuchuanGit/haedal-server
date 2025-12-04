package com.sui.haedal.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RateModelVo {

    /**
     * 模板最佳利用率
     */
    @JsonProperty("TargetU")
    private String targetU;

    /**
     * 当前利用率
     */
    @JsonProperty("CurrentU")
    private String currentU;

    /**
     * 利用率详情
     */
    private List<RateModelDetailVo> rates;
}
