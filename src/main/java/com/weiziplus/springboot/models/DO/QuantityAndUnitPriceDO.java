package com.weiziplus.springboot.models.DO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class QuantityAndUnitPriceDO {
    private String cSku;
    private String cAsin;
    private String pSku;
    private String shop;
    private String area;
    private Integer sumQuantity;
    private BigDecimal sumSalesAmount;
    private BigDecimal unitPrice;
    private Integer expectedDailySales;//预估每日销量
    private Integer expected60DaysSales;//预计60天销量

}
