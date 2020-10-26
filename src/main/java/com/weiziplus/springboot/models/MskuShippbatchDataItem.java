package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 调拨item(马帮api)
 * msku_shippbatch_data_item
 * @author Administrator
 * @date 2019-08-26 11:53:24
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("msku_shippbatch_data_item")
public class MskuShippbatchDataItem implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * 调拨id
     */
    @Column("msku_shippbatch_data_id")
    private Long mskuShippbatchDataId;

    /**
     * 货件SKU
     */
    @Column("platformSku")
    private String platformsku;

    /**
     * 商品编号
     */
    @Column("fbaStockId")
    private String fbastockid;

    /**
     * 运输中数量
     */
    @Column("shippedQuantity")
    private String shippedquantity;

    /**
     * 签收数量
     */
    @Column("receivedQuantity")
    private String receivedquantity;

    /**
     * 发货数量
     */
    @Column("delieverQuantity")
    private String delieverquantity;

    /**
     * 申报数量
     */
    @Column("applyQuantity")
    private String applyquantity;

    private static final long serialVersionUID = 1L;
}