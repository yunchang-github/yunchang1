package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 已完成订单(亚马逊api)
 * complete_order
 * @author WeiziPlus
 * @date 2019-07-29 17:25:36
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("complete_order")
public class CompleteOrder implements Serializable {
    /**
     * 自增
     */
    @Id("id")
    private Long id;

    /**
     * 店铺(名称)
     */
    @Column("shop")
    private String shop;

    /**
     * 区域(国家代码)
     */
    @Column("area")
    private String area;

    /**
     * shipment-date
     */
    @Column("shipment_date")
    private String shipmentDate;

    /**
     * sku
     */
    @Column("sku")
    private String sku;

    /**
     * fnsku
     */
    @Column("fnsku")
    private String fnsku;

    /**
     * asin
     */
    @Column("asin")
    private String asin;

    /**
     * fulfillment-center-id
     */
    @Column("fulfillment_center_id")
    private String fulfillmentCenterId;

    /**
     * quantity
     */
    @Column("quantity")
    private String quantity;

    /**
     * amazon-order-id
     */
    @Column("amazon_order_id")
    private String amazonOrderId;

    /**
     * currency
     */
    @Column("currency")
    private String currency;

    /**
     * item-price-per-unit
     */
    @Column("item_price_per_unit")
    private Double itemPricePerUnit;

    /**
     * shipping-price
     */
    @Column("shipping_price")
    private Double shippingPrice;

    /**
     * gift-wrap-price
     */
    @Column("gift_wrap_price")
    private Double giftWrapPrice;

    /**
     * ship-city
     */
    @Column("ship_city")
    private String shipCity;

    /**
     * ship-state
     */
    @Column("ship_state")
    private String shipState;

    /**
     * ship-postal-code
     */
    @Column("ship_postal_code")
    private String shipPostalCode;

    private static final long serialVersionUID = 1L;
}