package com.sui.haedal.model.bo;

import lombok.Data;

@Data
public class BorrowTotalBo {

    private String marketId;
    private String userAddress;
    private Integer timePeriodType = 1; //统计时间段类型 1周 2月 3年
    private Integer lineType;       // 折线图类型 1 Supply 2 Borrow
}
