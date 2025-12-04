package com.sui.haedal.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RateModelDetailVo {

    /**
     * 模板最佳利用率
     */
    @JsonProperty("DateUnit")
    private String dateUnit;
    /**
     * 存利率
     */
    @JsonProperty("SupplyRate")
    private String supplyRate;
    /**
     * 借利率
     */
    @JsonProperty("BorrowRate")
    private String borrowRate;
    /**
     * 当前利用率
     */
    @JsonProperty("CurrentU")
    private String currentU;
}
