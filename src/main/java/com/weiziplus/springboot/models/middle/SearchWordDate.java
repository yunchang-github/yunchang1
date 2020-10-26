package com.weiziplus.springboot.models.middle;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;


/**
 * 搜索词业务表
 * @author 1
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("search_word_date")
public class SearchWordDate implements Serializable {

    @Id("id")
    private Long id;

    @Column("ctr")
    private double ctr;

    @Column("week")
    private int week;

    @Column("year")
    private int year;

    /**
     * 总销售额
     */
    @Column("seven_day_total_sales")
    private double sevenDayTotalSales;

    /**
     * 搜索词显示名称
     */
    @Column("customer_search_term")
    private String customerSearchTerm;

    /**
     * 未知字段
     */
    @Column("seven_day_total_units")
    private int sevenDayTotalUnits;

    @Column("acos")
    private int acos;

    /**
     * Sales%显示字段
     */
    @Column("sum_of_sales")
    private double sumofSales;

    /**
     * 展现量
     */
    @Column("sum_impressions")
    private int sumImpressions;

    @Column("cr")
    private double cr;

    /**
     * Click% 显示字段
     */
    @Column("sum_of_click")
    private double sumofClick;

    /**
     * 点击量
     */
    @Column("sum_clicks")
    private int sumClicks;

    @Column("month")
    private int month;

    /**
     * 广告sku销售额
     */
    @Column("seven_day_advertised_sku_sales")
    private double sevenDayAdvertisedSkuSales;

    /**
     * 花费
     */
    @Column("spend")
    private double spend;

    @Column("cpc")
    private double cpc;

    /**
     * Impression% 显示字段
     */
    @Column("sum_impression")
    private double sumImpression;
}
