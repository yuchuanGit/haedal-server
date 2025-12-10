package com.sui.haedal.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(title = "HTokenBo传输结构", description = "HTokenBo传输结构")
public class HTokenBo {

    @NotBlank(message = "模块名称不能为空,请确认")
    @Schema(description = "模块名称")
    private String moduleName;

    @NotBlank(message = "coinUrl不能为空,请确认")
    @Schema(description = "coinUrl")
    private String coinUrl;

    @NotBlank(message = "币种不能为空,请确认")
    @Schema(description = "币种")
    private String coinSymbol;

    @NotBlank(message = "coin精度不能为空,请确认")
    @Schema(description = "coin精度")
    private String coinDecimals;
}
