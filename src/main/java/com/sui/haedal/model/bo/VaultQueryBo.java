package com.sui.haedal.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "VaultQueryBo结构", description = "earn vault条件结构")
public class VaultQueryBo {

    @Schema(description = "curator权限用户地址")
    private String userAddress;

    @Schema(description = "用户端登录用户地址")
    private String clientSideUserAddress;

    @Schema(description = "curator权限角色类型 1 owner curator guardian 2 owner curator 3 owner guardian 4 curator guardian 5 owner 6 curator 7 guardian")
    private Integer roleType;
}
