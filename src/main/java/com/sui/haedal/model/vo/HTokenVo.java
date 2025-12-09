package com.sui.haedal.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Data
@Schema(title = "HTokenVo结构", description = "HTokenVo结构")
public class HTokenVo {

    @Schema(description = "modules")
    private List<String> modules;

    @Schema(description = "dependencies")
    private List<String> dependencies;

    @Schema(description = "digest")
    private List<Integer> digest;


}
