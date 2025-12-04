package com.sui.haedal.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BorrowRateLineVo {

    @JsonProperty("TransactionTime")
    private String transactionTime;


    @JsonProperty("DateUnit")
    private String dateUnit;

    @JsonProperty("Amount")
    private String amount;

    @JsonProperty("TotalAmount")
    private String totalAmount;

    // 利率
    @JsonProperty("InterestRate")
    private String interestRate;
}
