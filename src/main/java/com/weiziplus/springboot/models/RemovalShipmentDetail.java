package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * 移除货件详情(亚马逊api)
 * removal_shipment_detail
 * @author WeiziPlus
 * @date 2019-08-01 11:47:51
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("removal_shipment_detail")
public class RemovalShipmentDetail implements Serializable {
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
    @Column("request_date")
    private String requestDate;

    /**
     */
    @Column("order_id")
    private String orderId;

    /**
     */
    @Column("shipment_date")
    private String shipmentDate;

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
    @Column("disposition")
    private String disposition;

    /**
     */
    @Column("shipped_quantity")
    private Integer shippedQuantity;

    /**
     */
    @Column("carrier")
    private String carrier;

    /**
     */
    @Column("tracking_number")
    private String trackingNumber;

    private static final long serialVersionUID = 1L;
}