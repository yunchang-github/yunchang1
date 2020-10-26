package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * fba_customer_returns
 * @author WeiziPlus
 * @date 2019-07-31 11:07:48
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("fba_customer_returns")
public class FbaCustomerReturns implements Serializable {
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
    @Column("return_date")
    private String returnDate;

    /**
     */
    @Column("order_id")
    private String orderId;

    /**
     */
    @Column("sku")
    private String sku;

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
    @Column("quantity")
    private Integer quantity;

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
    @Column("reason")
    private String reason;

    /**
     */
    @Column("status")
    private String status;

    /**
     */
    @Column("license_plate_number")
    private String licensePlateNumber;

    /**
     */
    @Column("customer_comments")
    private String customerComments;

    private static final long serialVersionUID = 1L;
}