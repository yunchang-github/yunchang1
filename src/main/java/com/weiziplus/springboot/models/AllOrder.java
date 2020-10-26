package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 亚马逊api获取all_order表
 * all_order
 * @author WeiziPlus
 * @date 2019-07-30 11:25:31
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("all_order")
public class AllOrder implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * 店铺标识
     */
    @Column("shop")
    private String shop;

    /**
     * 卖家ID
     */
    @Column("seller_id")
    private String sellerId;

    /**
     * 区域
     */
    @Column("area")
    private String area;

    /**
     */
    @Column("amazon_order_id")
    private String amazonOrderId;

    /**
     */
    @Column("merchant_order_id")
    private String merchantOrderId;

    /**
     */
    @Column("purchase_date")
    private String purchaseDate;

    /**
     */
    @Column("last_updated_date")
    private String lastUpdatedDate;

    /**
     */
    @Column("order_status")
    private String orderStatus;

    /**
     */
    @Column("fulfillment_channel")
    private String fulfillmentChannel;

    /**
     */
    @Column("sales_channel")
    private String salesChannel;

    /**
     */
    @Column("order_channel")
    private String orderChannel;

    /**
     */
    @Column("url")
    private String url;

    /**
     */
    @Column("ship_service_level")
    private String shipServiceLevel;

    /**
     */
    @Column("product_name")
    private String productName;

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
    @Column("item_status")
    private String itemStatus;

    /**
     */
    @Column("quantity")
    private String quantity;

    /**
     */
    @Column("currency")
    private String currency;

    /**
     */
    @Column("item_price")
    private String itemPrice;

    /**
     */
    @Column("item_tax")
    private String itemTax;

    /**
     */
    @Column("shipping_price")
    private String shippingPrice;

    /**
     */
    @Column("shipping_tax")
    private String shippingTax;

    /**
     */
    @Column("gift_wrap_price")
    private String giftWrapPrice;

    /**
     */
    @Column("gift_wrap_tax")
    private String giftWrapTax;

    /**
     */
    @Column("item_promotion_discount")
    private String itemPromotionDiscount;

    /**
     */
    @Column("ship_promotion_discount")
    private String shipPromotionDiscount;

    /**
     */
    @Column("ship_city")
    private String shipCity;

    /**
     */
    @Column("ship_state")
    private String shipState;

    /**
     */
    @Column("ship_postal_code")
    private String shipPostalCode;

    /**
     */
    @Column("ship_country")
    private String shipCountry;

    /**
     */
    @Column("promotion_ids")
    private String promotionIds;

    /**
     */
    @Column("is_business_order")
    private String isBusinessOrder;

    /**
     */
    @Column("purchase_order_number")
    private String purchaseOrderNumber;

    /**
     */
    @Column("price_designation")
    private String priceDesignation;

    @Column("is_sold_by_ab")
    private String isSoldByAd;

    @Column("customized_url")
    private String customizedUrl;

    @Column("customized_page")
    private String customizedPage;

    /**
     */
    @Column("create_time")
    private  String createTime;

    private static final long serialVersionUID = 1L;
}