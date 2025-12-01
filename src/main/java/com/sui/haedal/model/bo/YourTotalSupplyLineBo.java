package com.sui.haedal.model.bo;

import lombok.Data;

import java.util.Date;

@Data
public class YourTotalSupplyLineBo {
    private String mysqlDateFormat;
    private String userAddress;
    private String marketId;
    private Date start;
    private Date end;
    private Integer lineType;       // 折线图类型 1 Supply 2 Borrow
}
