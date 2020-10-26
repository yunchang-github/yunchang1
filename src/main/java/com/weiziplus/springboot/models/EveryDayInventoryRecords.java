package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 每日库存记录(亚马逊api)
 * every_day_inventory_records
 * @author WeiziPlus
 * @date 2019-07-31 10:53:01
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("every_day_inventory_records")
public class EveryDayInventoryRecords implements Serializable {
    /**
     * 自增
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
     * snapshot-date
     */
    @Column("snapshot_date")
    private String snapshotDate;

    /**
     * fnsku
     */
    @Column("fnsku")
    private String fnsku;

    /**
     * sku
     */
    @Column("sku")
    private String sku;

    /**
     * product-name
     */
    @Column("product_name")
    private String productName;

    /**
     * quantity
     */
    @Column("quantity")
    private Integer quantity;

    /**
     * fulfillment-center-id
     */
    @Column("fulfillment_center_id")
    private String fulfillmentCenterId;

    /**
     * detailed-disposition
     */
    @Column("detailed_disposition")
    private String detailedDisposition;

    /**
     * country
     */
    @Column("country")
    private String country;

    private static final long serialVersionUID = 1L;
}