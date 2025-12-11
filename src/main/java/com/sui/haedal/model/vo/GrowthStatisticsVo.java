package com.sui.haedal.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "GrowthStatisticsVo", description = "统计增长数据")
public class GrowthStatisticsVo {
    @Schema(description = "增长比例")
    private String growthRate;

    @Schema(description = "增长天")
    private Integer growthDay;
}
