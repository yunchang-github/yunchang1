package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * us_xicang_goods_issued_records
 * @author Administrator
 * @date 2019-10-31 10:48:34
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("us_xicang_goods_issued_records")
public class UsXicangGoodsIssuedRecords implements Serializable {
    /**
     */
    @Id("id")
    private Integer id;

    /**
     */
    @Column("msku")
    private String msku;

    /**
     */
    @Column("fnsku")
    private String fnsku;

    /**
     * 库存sku
     */
    @Column("inventory_sku")
    private String inventorySku;

    /**
     */
    @Column("shipment_id")
    private String shipmentId;

    /**
     * 箱号
     */
    @Column("case_number")
    private String caseNumber;

    /**
     * 数量
     */
    @Column("number")
    private String number;

    /**
     * 店铺
     */
    @Column("shop")
    private String shop;

    /**
     * 区域
     */
    @Column("area")
    private String area;

    /**
     * 发货时间
     */
    @Column("delivery_time")
    private String deliveryTime;

    private static final long serialVersionUID = 1L;
}