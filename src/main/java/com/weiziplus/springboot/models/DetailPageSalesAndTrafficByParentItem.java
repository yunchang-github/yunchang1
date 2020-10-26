package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * 父商品详情页面上的销量与访问量(后台抓取)
 * detail_page_sales_and_traffic_by_parent_item
 * @author WeiziPlus
 * @date 2019-08-16 11:03:36
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("detail_page_sales_and_traffic_by_parent_item")
public class DetailPageSalesAndTrafficByParentItem implements Serializable {
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
     * 下载时间
     */
    @Column("date")
    private String date;

    /**
     * (父） ASIN
     */
    @Column("parent_asin")
    private String parentAsin;

    /**
     * 商品名称
     */
    @Column("title")
    private String title;

    /**
     * 买家访问次数
     */
    @Column("sessions")
    private Integer sessions;

    /**
     * 买家访问次数百分比(单位%)
     */
    @Column("session_percentage")
    private Double sessionPercentage;

    /**
     * 页面浏览次数
     */
    @Column("page_views")
    private Integer pageViews;

    /**
     * 页面浏览次数百分比(单位%)
     */
    @Column("page_views_percentage")
    private Double pageViewsPercentage;

    /**
     * 购买按钮赢得率(单位%)
     */
    @Column("buy_box_percentage")
    private Double buyBoxPercentage;

    /**
     * 已订购商品数量
     */
    @Column("units_ordered")
    private Integer unitsOrdered;

    /**
     * 订单商品数量转化率(单位%)
     */
    @Column("unit_session_percentage")
    private Double unitSessionPercentage;

    /**
     * 已订购商品销售额(原单位$)
     */
    @Column("ordered_product_sales")
    private Double orderedProductSales;

    /**
     * 订单商品种类数
     */
    @Column("total_order_items")
    private Integer totalOrderItems;

    private static final long serialVersionUID = 1L;
}