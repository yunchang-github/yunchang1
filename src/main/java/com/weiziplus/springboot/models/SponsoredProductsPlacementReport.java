package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品推广广告展示位置报告（后台抓取）
 * sponsored_products_placement_report
 * @author WeiziPlus
 * @date 2019-08-20 10:14:21
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("sponsored_products_placement_report")
public class SponsoredProductsPlacementReport implements Serializable {
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
     */
    @Column("campaign_id")
    private Long campaignId;

    /**
     * 竞价策略
     */
    @Column("bidding_strategy")
    private String biddingStrategy;

    /**
     * 放置
     */
    @Column("placement")
    private String placement;

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
     * 每次点击成本
     */
    @Column("cpc")
    private String cpc;

    /**
     * 花费(原单位美元$)
     */
    @Column("spend")
    private Double spend;

    /**
     * 7天总销售额
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
     * 7天总销售量
     */
    @Column("seven_day_total_units")
    private Integer sevenDayTotalUnits;

    private static final long serialVersionUID = 1L;
}