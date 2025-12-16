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

    @Schema(description = "存入类型 1 supply存入 2 collateral存入",hidden = true)
    private Integer supplyType;

    @Schema(description = "统计 角色 true 用户 false 池子",hidden = true)
    private Boolean statisticalRole;

    /**
     * 是否按周
     */
    private Boolean isWeek;

    @Schema(description = "统计开始时间 LocalDateTime",hidden = true)
    private LocalDateTime startLD;

    @Schema(description = "统计结束时间 LocalDateTime",hidden = true)
    private LocalDateTime endLD;

    @Schema(description = "统计时间段数据最小时间(第一次发送)",hidden = true)
    private Date timePeriodMinTime;

    @Schema(description = "是否使用创建池子时间小时",hidden = true)
    private Boolean isCreatePoolTimeHours = false;

}
