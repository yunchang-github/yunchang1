package com.weiziplus.springboot.models;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/*
 * 新增的 子商品详情页面上的销售量和访问量 的实体类
 * --苏建东
 * */
@Data
public class DetailPageSalesAndTrafficByChildItems implements Serializable {
    private Long id;

    private String shopName;

    private String countryCode;

    private String sellerId;

    private String date;

    private String createTime;

    private String parentAsin;

    private String childAsin;

    private String productName;

    private Integer buyerVisits;

    private BigDecimal buyerVisitsPercentage;

    private Integer pageViews;

    private BigDecimal pageViewsPercentage;

    private BigDecimal purchaseButtonWinRate;

    private Integer orderedItemsNumber;

    private Integer orderQuantityB2b;

    private BigDecimal orderItemsConversionRate;

    private BigDecimal itemsConversionRateB2b;

    private BigDecimal orderedItemSales;

    private BigDecimal orderedItemSalesB2b;

    private Integer orderProductCategorys;

    private Integer totalOrderItemsB2b;

    private static final long serialVersionUID = 1L;

}