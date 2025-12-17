package com.sui.haedal.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户对应vault权限
 */
@Data
@Schema(title = "UserVaultPermissionVo结构", description = "用户vault权限")
public class UserVaultPermissionVo {

    private List<String> curatorVaultAddress;
    private List<String> guardianVaultAddress;
}
