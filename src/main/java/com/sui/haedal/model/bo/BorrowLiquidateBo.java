package com.sui.haedal.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "BorrowLiquidateBo结构", description = "borrow清算bo")
public class BorrowLiquidateBo {

    private String userAddress;
}
