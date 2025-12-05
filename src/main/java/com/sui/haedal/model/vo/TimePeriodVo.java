package com.sui.haedal.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class TimePeriodVo {

    private LocalDateTime start;

    private LocalDateTime end;

    /**
     * 是否按周
     */
    private Boolean isWeek;

    private String mysqlDateFormat;
}
