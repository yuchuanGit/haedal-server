package com.sui.haedal.model.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "TimePeriodStatisticsGrowthVo", description = "时间段统计/增长数据")
public class TimePeriodStatisticsGrowthVo {

    @Schema(description = "统计数据")
    private List<TimePeriodStatisticsVo> statisticsData;

    @Schema(description = "天数增长")
    private List<GrowthStatisticsVo> growthData;
}
