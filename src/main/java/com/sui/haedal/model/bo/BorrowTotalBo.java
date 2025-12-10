package com.sui.haedal.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class BorrowTotalBo {

    @Schema(description = "marketId")
    private String marketId;

    @Schema(description = "用户地址")
    private String userAddress;

    @Schema(description = "统计时间段类型 默认周： 1周 2月 3年")
    private Integer timePeriodType = 1;

    @Schema(description = "统计类型 1 Supply(存资产) 2 Collateral(存抵押品) 3 Borrow(借) ")
    private Integer statisticsType;

    private Integer lineType;       // 折线图类型 1 Supply 2 Borrow

    @Schema(description = "利率类型",hidden = true)
    private List<String> rateTypes;
}
