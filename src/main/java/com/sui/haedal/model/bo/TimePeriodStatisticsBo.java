package com.sui.haedal.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class TimePeriodStatisticsBo {

    @Schema(description = "mysql日期格式")
    private String mysqlDateFormat;

    @Schema(description = "统计开始时间")
    private Date start;

    @Schema(description = "统计结束时间")
    private Date end;

    @Schema(description = "按用户统计")
    private String userAddress;

    @Schema(description = "按业务id统计(earn vaultId/borrow marketId)")
    private String businessPoolId;

    /**
     * 是否按周
     */
    private Boolean isWeek;

    private LocalDateTime startLD;

    private LocalDateTime endLD;

}
