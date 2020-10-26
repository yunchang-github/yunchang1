package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * 月度仓储费
 * monthly_storage_fees
 * @author WeiziPlus
 * @date 2019-07-31 11:49:43
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("monthly_storage_fees")
public class MonthlyStorageFees implements Serializable {
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
    @Column("asin")
    private String asin;

    /**
     */
    @Column("fnsku")
    private String fnsku;

    /**
     */
    @Column("product_name")
    private String productName;

    /**
     */
    @Column("fulfillment_center")
    private String fulfillmentCenter;

    /**
     */
    @Column("country_code")
    private String countryCode;

    /**
     */
    @Column("longest_side")
    private Double longestSide;

    /**
     */
    @Column("median_side")
    private Double medianSide;

    /**
     */
    @Column("shortest_side")
    private Double shortestSide;

    /**
     */
    @Column("measurement_units")
    private String measurementUnits;

    /**
     */
    @Column("weight")
    private Double weight;

    /**
     */
    @Column("weight_units")
    private String weightUnits;

    /**
     */
    @Column("item_volume")
    private Double itemVolume;

    /**
     */
    @Column("volume_units")
    private String volumeUnits;

    /**
     */
    @Column("average_quantity_on_hand")
    private Double averageQuantityOnHand;

    /**
     */
    @Column("average_quantity_pending_removal")
    private Double averageQuantityPendingRemoval;

    /**
     */
    @Column("estimated_total_item_volume")
    private Double estimatedTotalItemVolume;

    /**
     */
    @Column("month_of_charge")
    private String monthOfCharge;

    /**
     */
    @Column("storage_rate")
    private Double storageRate;

    /**
     */
    @Column("currency")
    private String currency;

    /**
     */
    @Column("estimated_monthly_storage_fee")
    private Double estimatedMonthlyStorageFee;

    private static final long serialVersionUID = 1L;
}