package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 库龄（亚马逊api）
 * inventory_age
 *
 * @author WeiziPlus
 * @date 2019-07-30 15:44:17
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("inventory_age")
public class InventoryAge implements Serializable {
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
    @Column("snapshot_date")
    private String snapshotDate;

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
    @Column("avaliable_quantity_sellable")
    private Integer avaliableQuantitySellable;

    /**
     */
    @Column("qty_with_removals_in_progress")
    private Integer qtyWithRemovalsInProgress;

    /**
     */
    @Column("inv_age_0_to_90_days")
    private Integer invAge0To90Days;

    /**
     */
    @Column("inv_age_91_to_180_days")
    private Integer invAge91To180Days;

    /**
     */
    @Column("inv_age_181_to_270_days")
    private Integer invAge181To270Days;

    /**
     */
    @Column("inv_age_271_to_365_days")
    private Integer invAge271To365Days;

    /**
     */
    @Column("inv_age_365_plus_days")
    private Integer invAge365PlusDays;

    /**
     */
    @Column("currency")
    private String currency;

    /**
     */
    @Column("qty_to_be_charged_ltsf_6_mo")
    private Integer qtyToBeChargedLtsf6Mo;

    /**
     */
    @Column("projected_ltsf_6_mo")
    private Double projectedLtsf6Mo;

    /**
     */
    @Column("qty_to_be_charged_ltsf_12_mo")
    private Integer qtyToBeChargedLtsf12Mo;

    /**
     */
    @Column("projected_ltsf_12_mo")
    private Double projectedLtsf12Mo;

    /**
     */
    @Column("units_shipped_last_7_days")
    private Integer unitsShippedLast7Days;

    /**
     */
    @Column("units_shipped_last_30_days")
    private Integer unitsShippedLast30Days;

    /**
     */
    @Column("units_shipped_last_60_days")
    private Integer unitsShippedLast60Days;

    /**
     */
    @Column("units_shipped_last_90_days")
    private Integer unitsShippedLast90Days;

    /**
     */
    @Column("alert")
    private String alert;

    /**
     */
    @Column("your_price")
    private String yourPrice;

    /**
     */
    @Column("sales_price")
    private String salesPrice;

    /**
     */
    @Column("lowest_price_new")
    private Double lowestPriceNew;

    /**
     */
    @Column("lowest_price_used")
    private Double lowestPriceUsed;

    /**
     */
    @Column("recommended_action")
    private String recommendedAction;

    /**
     */
    @Column("healthy_inventory_level")
    private Integer healthyInventoryLevel;

    /**
     */
    @Column("recommended_sales_price")
    private Double recommendedSalesPrice;

    /**
     */
    @Column("recommended_sale_duration_days")
    private Integer recommendedSaleDurationDays;

    /**
     */
    @Column("recommended_removal_quantity")
    private Integer recommendedRemovalQuantity;

    /**
     */
    @Column("estimated_cost_savings_of_removal")
    private String estimatedCostSavingsOfRemoval;

    /**
     */
    @Column("sell_through")
    private String sellThrough;

    /**
     */
    @Column("cubic_feet")
    private String cubicFeet;

    /**
     */
    @Column("storage_type")
    private String storageType;


    private int moreNinety;//库龄大于90天库存

    private static final long serialVersionUID = 1L;
}