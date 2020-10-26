package com.weiziplus.springboot.models.DO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalesAmountDO {
    private String shopName;
    private String sellerId;
    private String areaCode;
    private String currency;
    private String year;
    private String month;
    private BigDecimal sumSalesAmount;

}
