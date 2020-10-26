package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * sales_and_traffic
 * @author Administrator
 * @date 2019-11-15 15:05:36
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("sales_and_traffic")
public class SalesAndTraffic implements Serializable {
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
     * 已订购商品销售额(原单位美元$)
     */
    @Column("ordered_product_sales")
    private Double orderedProductSales;

    /**
     * 已订购商品的销售额B2B(原单位美元$)(原单位美元$)
     */
    @Column("ordered_product_sales_b2b")
    private Double orderedProductSalesB2b;

    /**
     * 已订购商品数量
     */
    @Column("units_ordered")
    private Integer unitsOrdered;

    /**
     * 已订购商品数量B2B
     */
    @Column("units_ordered_b2b")
    private Integer unitsOrderedB2b;

    /**
     * 订单商品种类数
     */
    @Column("total_order_items")
    private Integer totalOrderItems;

    /**
     * 订单商品种类数B2B
     */
    @Column("total_order_items_b2b")
    private Integer totalOrderItemsB2b;

    /**
     * 每种订单商品的平均销售额(原单位美元$)
     */
    @Column("average_sales_per_order_item")
    private Double averageSalesPerOrderItem;

    /**
     * 每种订单商品的平均销售额b2b(原单位美元$)
     */
    @Column("average_sales_per_order_item_b2b")
    private Double averageSalesPerOrderItemB2b;

    /**
     * 每种订单商品的平均数量
     */
    @Column("average_units_per_order_item")
    private Double averageUnitsPerOrderItem;

    /**
     * 每种订单商品的平均数量B2B
     */
    @Column("average_units_per_order_item_b2b")
    private Double averageUnitsPerOrderItemB2b;

    /**
     * 平均销售价格(原单位美元$)
     */
    @Column("average_selling_price")
    private Double averageSellingPrice;

    /**
     * 平均销售价格B2B(原单位美元$)
     */
    @Column("average_selling_price_b2b")
    private Double averageSellingPriceB2b;

    /**
     * 买家访问次数
     */
    @Column("sessions")
    private Integer sessions;

    /**
     * 订单商品种类数转化率(单位%)
     */
    @Column("order_item_session_percentage")
    private Double orderItemSessionPercentage;

    /**
     * 订单商品种类数转化率B2B(单位%)
     */
    @Column("order_item_session_percentage_b2b")
    private Double orderItemSessionPercentageB2b;

    /**
     * 平均在售商品数量
     */
    @Column("average_offer_count")
    private Integer averageOfferCount;

    /**
     * 数据入库的创建时间
     */
    @Column("create_time")
    private String createTime;

    /**
     * 货币
     */
    @Column("currency")
    private String currency;

    private static final long serialVersionUID = 1L;
}