package com.sui.haedal.model.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "TimePeriodStatisticsVo结构", description = "时间段统计通用结构")
public class TimePeriodStatisticsVo {

    @Schema(description = "交易时间")
    private String transactionTime;

    @Schema(description = "统计时间格式")
    private String dateUnit;

    @Schema(description = "统计值")
    private String val;

    @Schema(description = "累计统计值")
    private String totalVal;

    @Schema(description = "vaultId")
    private String vaultId;

    @Schema(description = "币种")
    private String coinType;


    @Schema(description = "查询币种id")
    private String feedId;
}
