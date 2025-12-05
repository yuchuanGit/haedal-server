package com.sui.haedal.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PythCoinFeedPriceVo {
    @Schema(description = "feedId")
    private String feedId;

    @Schema(description = "feedId对应单价")
    private String price;

    @Schema(description = "eedId对应单价精度")
    private double expo;
}
