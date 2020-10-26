package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * 订单item表（马帮api）
 * order_data_item
 * @author WeiziPlus
 * @date 2019-09-03 11:57:47
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("order_data_item")
public class OrderDataItem implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * 主表id
     */
    @Column("order_id")
    private Long orderId;

    /**
     * 仓库编号
     */
    @Column("stockWarehouseId")
    private String stockwarehouseid;

    /**
     * 仓库名称
     */
    @Column("stockWarehouseName")
    private String stockwarehousename;

    /**
     * 退款原始金额
     */
    @Column("refuntMoney")
    private String refuntmoney;

    /**
     * 商品单品重量
     */
    @Column("itemWeight")
    private String itemweight;

    /**
     * sku
     */
    @Column("stockSku")
    private String stocksku;

    /**
     * 商品数量
     */
    @Column("itemQuantity")
    private String itemquantity;

    /**
     * 平台sku
     */
    @Column("platformSku")
    private String platformsku;

    /**
     * 平台数量
     */
    @Column("platformQuantity")
    private String platformquantity;

    /**
     */
    @Column("asin")
    private String asin;

    /**
     * 状态 1未付款订单 2已付款订单 3已发货 4作废
     */
    @Column("status")
    private String status;

    private static final long serialVersionUID = 1L;
}