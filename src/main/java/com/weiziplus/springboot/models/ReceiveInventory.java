package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * receive_inventory
 * @author WeiziPlus
 * @date 2019-08-01 08:41:39
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("receive_inventory")
public class ReceiveInventory implements Serializable {
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
    @Column("received_date")
    private String receivedDate;

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
    @Column("quantity")
    private Integer quantity;

    /**
     */
    @Column("fba_shipment_id")
    private String fbaShipmentId;

    /**
     */
    @Column("fulfillment_center_id")
    private String fulfillmentCenterId;

    private static final long serialVersionUID = 1L;
}