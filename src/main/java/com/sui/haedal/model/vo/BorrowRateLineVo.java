package com.sui.haedal.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BorrowRateLineVo {

    private String transactionTime;


    private String dateUnit;


    private String amount;


    private String totalAmount;

    // 利率
    private String interestRate;
}
