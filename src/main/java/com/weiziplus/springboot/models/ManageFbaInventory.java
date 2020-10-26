package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 管理亚马逊库存
 * manage_fba_inventory
 * @author WeiziPlus
 * @date 2019-07-30 10:59:55
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("manage_fba_inventory")
public class ManageFbaInventory implements Serializable {
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
    @Column("sku")
    private String sku;

    /**
     */
    @Column("fnsku")
    private String fnsku;

    /**
     */
    @Column("asin")
    private String asin;

    /**
     */
    @Column("product_name")
    private String productName;

    /**
     */
    @Column("condition")
    private String condition;

    /**
     */
    @Column("your_price")
    private String yourPrice;

    /**
     */
    @Column("mfn_listing_exists")
    private String mfnListingExists;

    /**
     */
    @Column("mfn_fulfillable_quantity")
    private String mfnFulfillableQuantity;

    /**
     */
    @Column("afn_listing_exists")
    private String afnListingExists;

    /**
     */
    @Column("afn_warehouse_quantity")
    private String afnWarehouseQuantity;

    /**
     * 可售库存
     */
    @Column("afn_fulfillable_quantity")
    private String afnFulfillableQuantity;

    /**
     * 不可售库存
     */
    @Column("afn_unsellable_quantity")
    private String afnUnsellableQuantity;

    /**
     */
    @Column("afn_reserved_quantity")
    private String afnReservedQuantity;

    /**
     */
    @Column("afn_total_quantity")
    private String afnTotalQuantity;

    /**
     */
    @Column("per_unit_volume")
    private String perUnitVolume;

    /**
     */
    @Column("afn_inbound_working_quantity")
    private String afnInboundWorkingQuantity;

    /**
     * 待签收库存
     */
    @Column("afn_inbound_shipped_quantity")
    private String afnInboundShippedQuantity;

    /**
     * 已签收待处理库存
     */
    @Column("afn_inbound_receiving_quantity")
    private String afnInboundReceivingQuantity;

    /**
     * 定时任务获取时间
     */
    @Column("create_time")
    private String createTime;

    /**
     * 卖家ID
     */
    @Column("seller_id")
    private String sellerId;



    /**
     * 页面展示字段
     * 可售库存
     */
    private int sellableStock;
    /**
     * 页面展示字段
     * 不可售库存
     */
    private int notSellableStock;
    /**
     * 页面展示字段
     * 待签收库存
     */
    private int unsignedInventory;
    /**
     * 页面展示字段
     * 已签收待处理库存
     */
    private int signedInventory;

    private static final long serialVersionUID = 1L;
}