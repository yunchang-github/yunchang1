package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品推广广告商品报告（后台抓取）
 * sponsored_products_advertised_product_report
 * @author WeiziPlus
 * @date 2019-08-19 17:22:03
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("sponsored_products_advertised_product_report")
public class SponsoredProductsAdvertisedProductReport implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     */
    @Column("shop")
    private String shop;

    /**
     */
    @Column("area")
    private String area;

    /**
     */
    @Column("date")
    private String date;

    /**
     * 广告组合名称
     */
    @Column("portfolio_name")
    private String portfolioName;

    /**
     */
    @Column("currency")
    private String currency;

    /**
     */
    @Column("campaign_name")
    private String campaignName;

    /**
     */
    @Column("ad_group_name")
    private String adGroupName;

    /**
     */
    @Column("advertised_sku")
    private String advertisedSku;

    /**
     */
    @Column("advertised_asin")
    private String advertisedAsin;

    /**
     */
    @Column("impressions")
    private Integer impressions;

    /**
     */
    @Column("clicks")
    private Integer clicks;

    /**
     * 点击率
     */
    @Column("ctr")
    private String ctr;

    /**
     * 每次点击成本
     */
    @Column("cpc")
    private String cpc;

    /**
     */
    @Column("spend")
    private Double spend;

    /**
     */
    @Column("seven_day_total_sales")
    private Double sevenDayTotalSales;

    /**
     * 广告成本销售比
     */
    @Column("acos")
    private String acos;

    /**
     * 投入产出比
     */
    @Column("roas")
    private String roas;

    /**
     * 7天总订单数
     */
    @Column("seven_day_total_orders")
    private Integer sevenDayTotalOrders;

    /**
     */
    @Column("seven_day_total_units")
    private Integer sevenDayTotalUnits;

    /**
     * 7天的转化率
     */
    @Column("seven_day_conversion_rate")
    private String sevenDayConversionRate;

    /**
     */
    @Column("seven_day_advertised_sku_units")
    private Integer sevenDayAdvertisedSkuUnits;

    /**
     * 7天内其他SKU销售量
     */
    @Column("seven_day_other_sku_units")
    private Integer sevenDayOtherSkuUnits;

    /**
     */
    @Column("seven_day_advertised_sku_sales")
    private Double sevenDayAdvertisedSkuSales;

    /**
     * 7天内其他SKU销售额(原单位美元$)
     */
    @Column("seven_day_other_sku_sales")
    private Double sevenDayOtherSkuSales;

    private static final long serialVersionUID = 1L;
}