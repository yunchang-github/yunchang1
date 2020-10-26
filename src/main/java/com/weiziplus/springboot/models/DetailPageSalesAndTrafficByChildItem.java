package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 子商品详情页面上的销售量与访问量（后台抓取并加上下载时间）
 * detail_page_sales_and_traffic_by_child_item
 * @author WeiziPlus
 * @date 2019-08-16 11:33:40
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("detail_page_sales_and_traffic_by_child_item")
public class DetailPageSalesAndTrafficByChildItem implements Serializable {
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
    @Column("pasin")
    private String pasin;

    /**
     * （子）ASIN
     */
    @Column("casin")
    private String casin;

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
     * 买家访问次数百分比
     */
    @Column("session_percentage")
    private Double sessionPercentage;

    /**
     * 页面浏览次数
     */
    @Column("page_views")
    private Integer pageViews;

    /**
     * 页面浏览次数百分比
     */
    @Column("page_views_percentage")
    private Double pageViewsPercentage;

    /**
     * 购买按钮赢得率
     */
    @Column("buy_box_percentage")
    private Double buyBoxPercentage;

    /**
     * 已订购商品数量
     */
    @Column("units_ordered")
    private Integer unitsOrdered;

    /**
     */
    @Column("units_ordered_b2b")
    private String unitsOrderedB2b;

    /**
     * 订单商品数量转化率
     */
    @Column("unit_session_percentage")
    private Double unitSessionPercentage;

    /**
     */
    @Column("unit_session_percentage_b2b")
    private Double unitSessionPercentageB2b;

    /**
     * 已订购商品销售额(原单位为$)
     */
    @Column("ordered_product_sales")
    private Double orderedProductSales;

    /**
     * (原单位为$)
     */
    @Column("ordered_product_sales_b2b")
    private Double orderedProductSalesB2b;

    /**
     * 订单商品种类数
     */
    @Column("total_order_items")
    private Integer totalOrderItems;

    /**
     */
    @Column("total_order_items_b2b")
    private String totalOrderItemsB2b;

    private static final long serialVersionUID = 1L;
}