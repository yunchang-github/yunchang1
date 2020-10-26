package com.weiziplus.springboot.models.VO;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 一个具体店铺的销售数据
 * */
@Data
public class ShopSalesStatisticsDataVO {
    private String shopName;
    private String sellerId;
    private String areaCode;
    private int year;
    private BigDecimal januarySales;
    private BigDecimal februarySales;
    private BigDecimal marchSales;
    private BigDecimal aprilSales;
    private BigDecimal maySales;
    private BigDecimal juneSales;
    private BigDecimal julySales;
    private BigDecimal augustSales;
    private BigDecimal septemberSales;
    private BigDecimal octoberSales;
    private BigDecimal novemberSales;
    private BigDecimal decemberSales;
    private BigDecimal totalAnnualSales;//单店的所有月销售额合计
    private BigDecimal availableStocksSoldOutTime;//（可售+预留）/销量
    private BigDecimal allStocksSoldOutTime;//（可售+预留+在途）/销量
    private Integer availableStocks;//可售+预留
    private Integer allStocks;//可售+预留+在途
    private double dailyAverageQuantity;//平均每日销量

    public ShopSalesStatisticsDataVO() {
        this.januarySales = BigDecimal.valueOf(0);
        this.februarySales = BigDecimal.valueOf(0);
        this.marchSales = BigDecimal.valueOf(0);
        this.aprilSales = BigDecimal.valueOf(0);
        this.maySales = BigDecimal.valueOf(0);
        this.juneSales = BigDecimal.valueOf(0);
        this.julySales = BigDecimal.valueOf(0);
        this.augustSales = BigDecimal.valueOf(0);
        this.septemberSales = BigDecimal.valueOf(0);
        this.octoberSales = BigDecimal.valueOf(0);
        this.novemberSales = BigDecimal.valueOf(0);
        this.decemberSales = BigDecimal.valueOf(0);
        this.totalAnnualSales = BigDecimal.valueOf(0);
        this.availableStocksSoldOutTime = BigDecimal.valueOf(0);
        this.allStocksSoldOutTime = BigDecimal.valueOf(0);
        this.availableStocks = 0;
        this.allStocks = 0;
        this.dailyAverageQuantity = 0.0;
    }
}
