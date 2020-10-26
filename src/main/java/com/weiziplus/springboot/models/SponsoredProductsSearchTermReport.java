package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品推广自动投放报告(抓取)
 * sponsored_products_search_term_report
 * @author WeiziPlus
 * @date 2019-08-09 10:58:31
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("sponsored_products_search_term_report")
public class SponsoredProductsSearchTermReport implements Serializable {
    /**
     * 主键,自增
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
     * 日期
     */
    @Column("date")
    private String date;

    /**
     * 广告组合名称
     */
    @Column("portfolio_name")
    private String portfolioName;

    /**
     * 货币
     */
    @Column("currency")
    private String currency;

    /**
     * 广告活动名称
     */
    @Column("campaign_name")
    private String campaignName;

    /**
     * 组名称
     */
    @Column("ad_group_name")
    private String adGroupName;

    /**
     * 投放
     */
    @Column("targeting")
    private String targeting;

    /**
     * 匹配类型
     */
    @Column("match_type")
    private String matchType;

    /**
     * 客户搜索词
     */
    @Column("customer_search_term")
    private String customerSearchTerm;

    /**
     * 展现量
     */
    @Column("impressions")
    private Integer impressions;

    /**
     * 点击量
     */
    @Column("clicks")
    private Integer clicks;

    /**
     * 点击率
     */
    @Column("click_thru_rate")
    private String clickThruRate;

    /**
     * 每次点击成本(CPC)
     */
    @Column("cost_per_click")
    private Double costPerClick;

    /**
     * 花费
     */
    @Column("spend")
    private Double spend;

    /**
     * 7天总销售额
     */
    @Column("seven_day_total_sales")
    private Double sevenDayTotalSales;

    /**
     * 广告成本销售比(ACoS)
     */
    @Column("total_advertising_cost_of_sales")
    private String totalAdvertisingCostOfSales;

    /**
     * 投入产出比(RoAS)
     */
    @Column("total_return_on_advertising_spend")
    private Double totalReturnOnAdvertisingSpend;

    /**
     * 7天总订单数
     */
    @Column("seven_day_total_orders")
    private Integer sevenDayTotalOrders;

    /**
     * 7天总销售量
     */
    @Column("seven_day_total_units")
    private Integer sevenDayTotalUnits;

    /**
     * 7天的转化率
     */
    @Column("seven_day_conversion_rate")
    private String sevenDayConversionRate;

    /**
     * 7天内广告SKU销售量
     */
    @Column("seven_day_advertised_sku_units")
    private Integer sevenDayAdvertisedSkuUnits;

    /**
     * 7天内其他SKU销售量
     */
    @Column("seven_day_other_sku_units")
    private Integer sevenDayOtherSkuUnits;

    /**
     * 7天内广告SKU销售额
     */
    @Column("seven_day_advertised_sku_sales")
    private Double sevenDayAdvertisedSkuSales;

    /**
     * 7天内其他SKU销售额
     */
    @Column("seven_day_other_sku_sales")
    private Double sevenDayOtherSkuSales;

    private static final long serialVersionUID = 1L;
}