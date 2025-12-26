package com.sui.haedal.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Schema(title = "VaultDepositWithdrawVo结构", description = "Vault存取结构")
public class VaultDepositWithdrawVo implements Serializable {

    private Integer id;

    /**
     * 金库ID
     */
    @Schema(description = "金库ID")
    private String vaultId;

    @Schema(description = "用户地址")
    private String user;

    @Schema(description = "存取类型 存:deposit 取:withdraw")
    private String operationType;

    @Schema(description = "存入/取数量")
    private String assetAmount;

    @Schema(description = "存/取份额")
    private String shares;

    @Schema(description = "资产类型")
    private String assetType;

    @Schema(description = "质押代币类型")
    private String htokenType;

    @Schema(description = "digest")
    private String digest;

    @Schema(description = "时间戳（unix毫秒）")
    private Long timestampMsUnix;

    @Schema(description = "时间")
    private Date timestampMs;

    @Schema(description = "数据链上交易unix时间")
    private Long transactionTimeUnix;

    @Schema(description = "数据链上交易时间")
    @JsonFormat(pattern = "MMM dd,yyyy HH:mm",timezone = "UTC",locale = "en")
    private Date transactionTime;
}
