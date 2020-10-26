package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wanglongwei
 * @data 2019/7/5 9:38
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("sponsored_products_targeting_report")
public class SponsoredProductsTargetingReport implements Serializable {
    @Id("id")
    private Long id;

    @Column("date")
    private String date;

    @Column("portfolio_name")
    private String portfolioName;

    @Column("currency")
    private String currency;

    @Column("campaign_name")
    private String campaignName;

    @Column("ad_group_name")
    private String adGroupName;

    @Column("targeting")
    private String targeting;

    @Column("match_type")
    private String matchType;

    @Column("impressions")
    private Long impressions;

    @Column("clicks")
    private Long clicks;

    @Column("click_thru_rate")
    private String clickThruRate;

    @Column("cost_per_click")
    private String costPerClick;

    @Column("spend")
    private Double spend;

    @Column("total_advertising_cost_of_sales")
    private String totalAdvertisingCostOfSales;

    @Column("total_return_on_advertising_spend")
    private String totalReturnOnAdvertisingSpend;

    @Column("seven_day_total_sales")
    private Double sevenDayTotalSales;

    @Column("seven_day_total_orders")
    private Long sevenDayTotalOrders;

    @Column("seven_day_total_units")
    private Long sevenDayTotalUnits;

    @Column("seven_day_conversion_rate")
    private String sevenDayConversionRate;

    @Column("seven_day_advertised_sku_units")
    private String sevenDayAdvertisedSkuUnits;

    @Column("seven_day_other_sku_units")
    private String sevenDayOtherSkuUnits;

    @Column("seven_day_advertised_sku_sales")
    private Double sevenDayAdvertisedSkuSales;

    @Column("seven_day_other_sku_sales")
    private Double sevenDayOtherSkuSales;
}
