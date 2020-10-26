package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * 移除（亚马逊api）
 * removal_order_detail
 * @author WeiziPlus
 * @date 2019-08-01 11:00:00
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("removal_order_detail")
public class RemovalOrderDetail implements Serializable {
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
    @Column("order_type")
    private String orderType;

    /**
     */
    @Column("order_status")
    private String orderStatus;

    /**
     */
    @Column("last_updated_date")
    private String lastUpdatedDate;

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
    @Column("requested_quantity")
    private Integer requestedQuantity;

    /**
     */
    @Column("cancelled_quantity")
    private Integer cancelledQuantity;

    /**
     */
    @Column("disposed_quantity")
    private String disposedQuantity;

    /**
     */
    @Column("shipped_quantity")
    private Integer shippedQuantity;

    /**
     */
    @Column("in-process_quantity")
    private Integer inProcessQuantity;

    /**
     */
    @Column("removal_fee")
    private String removalFee;

    /**
     */
    @Column("currency")
    private String currency;

    private static final long serialVersionUID = 1L;
}