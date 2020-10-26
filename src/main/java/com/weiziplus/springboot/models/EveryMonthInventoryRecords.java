package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * every_month_inventory_records
 * @author WeiziPlus
 * @date 2019-08-01 09:01:35
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("every_month_inventory_records")
public class EveryMonthInventoryRecords implements Serializable {
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
    @Column("month")
    private String month;

    /**
     */
    @Column("fnsku")
    private String fnsku;

    /**
     */
    @Column("sku")
    private String sku;

    /**
     */
    @Column("product_name")
    private String productName;

    /**
     */
    @Column("average_quantity")
    private Double averageQuantity;

    /**
     */
    @Column("end_quantity")
    private Integer endQuantity;

    /**
     */
    @Column("fulfillment_center_id")
    private String fulfillmentCenterId;

    /**
     */
    @Column("detailed_disposition")
    private String detailedDisposition;

    /**
     */
    @Column("country")
    private String country;

    private static final long serialVersionUID = 1L;
}