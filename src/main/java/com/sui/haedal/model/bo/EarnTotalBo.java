package com.sui.haedal.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "EarnTotalBo结构", description = "earn统计公用参数结构")
public class EarnTotalBo {

    private String vaultId;

    @Schema(description = "用户地址")
    private String userAddress;

    @Schema(description = "统计时间段类型 默认周： 1周 2月 3年")
    private Integer timePeriodType = 1;

    @Schema(description = "统计图类型 1 Deposits(存入) 2 withdraw(取出)")
    private Integer lineType;
}
