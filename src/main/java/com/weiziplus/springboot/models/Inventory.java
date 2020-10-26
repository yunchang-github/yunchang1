package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * 盘库(亚马逊api)
 * inventory
 * @author WeiziPlus
 * @date 2019-08-01 10:26:02
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("inventory")
public class Inventory implements Serializable {
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
    @Column("adjusted_date")
    private String adjustedDate;

    /**
     */
    @Column("transaction_item_id")
    private String transactionItemId;

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
    @Column("fulfillment_center_id")
    private String fulfillmentCenterId;

    /**
     */
    @Column("quantity")
    private Integer quantity;

    /**
     */
    @Column("reason")
    private String reason;

    /**
     */
    @Column("disposition")
    private String disposition;

    private static final long serialVersionUID = 1L;
}