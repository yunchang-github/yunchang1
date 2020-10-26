package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 预留库存（亚马逊api）
 * reserved_inventory
 *
 * @author WeiziPlus
 * @date 2019-07-30 10:39:27
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("reserved_inventory")
public class ReservedInventory implements Serializable {
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
    @Column("product_name")
    private String productName;

    /**
     */
    @Column("reserved_qty")
    private Integer reservedQty;

    /**
     */
    @Column("reserved_customerorders")
    private Integer reservedCustomerorders;

    /**
     * 转库中库存
     */
    @Column("reserved_fc_transfers")
    private Integer reservedFcTransfers;

    /**
     */
    @Column("reserved_fc_processing")
    private Integer reservedFcProcessing;

    /**
     * 定时任务抓取时间
     */
    @Column("create_time")
    private String createTime;

    /**
     * 卖家ID
     */
    @Column("seller_id")
    private String sellerId;


    /**
     * //转库中库存
     */
    private int inventoryInTransfer;

    private static final long serialVersionUID = 1L;
}