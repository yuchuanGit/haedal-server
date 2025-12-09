package com.sui.haedal.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "HTokenBo传输结构", description = "HTokenBo传输结构")
public class HTokenBo {

    @Schema(description = "模块名称")
    private String moduleName;

    @Schema(description = "coin类型")
    private String coinType;

    @Schema(description = "币种")
    private String coinSymbol;

    @Schema(description = "coin精度")
    private String coinDecimals;
}
