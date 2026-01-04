package com.sui.haedal.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

@Data
@Schema(title = "FarmingPoolCreateBo结构", description = "激励池条件bo")
public class FarmingPoolCreateBo implements Serializable {

    @Schema(description = "earn htokenType")
    private Set<String> htokenTypes;

    @Schema(description = "borrow marketId")
    private Set<String> marketIds;
}
