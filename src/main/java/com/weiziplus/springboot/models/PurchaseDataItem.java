package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 采购item（马帮api）
 * purchase_data_item
 * @author WeiziPlus
 * @date 2019-09-03 09:03:12
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("purchase_data_item")
public class PurchaseDataItem implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     */
    @Column("purchase_data_id")
    private Long purchaseDataId;

    /**
     * 商品id
     */
    @Column("stockId")
    private String stockid;

    /**
     * sku
     */
    @Column("stockSku")
    private String stocksku;

    /**
     * sku中文名
     */
    @Column("stockSkuNameCn")
    private String stockskunamecn;

    /**
     * 采购数量
     */
    @Column("purchaseNum")
    private Integer purchasenum;

    /**
     * 已入库量
     */
    @Column("enterWarehouseNum")
    private Integer enterwarehousenum;

    /**
     * 损耗量
     */
    @Column("wastageNum")
    private String wastagenum;

    /**
     * 采购单价
     */
    @Column("sellPrice")
    private String sellprice;

    /**
     * 商品备注
     */
    @Column("remark")
    private String remark;

    /**
     * 运费单价
     */
    @Column("expressMoney")
    private String expressmoney;

    /**
     */
    @Column("buyerId")
    private String buyerid;

    /**
     * 采购员
     */
    @Column("buyerName")
    private String buyername;

    /**
     * 税前单价(税前总金额/采购数量)
     */
    @Column("pretaxUnitPrice")
    private String pretaxunitprice;

    /**
     * 税前入库金额(税前单价*入库数量)
     */
    @Column("pretaxEnterWarehousValue")
    private String pretaxenterwarehousvalue;

    /**
     * 商品是否被删除 1、没有删除 2、已删除
     */
    @Column("isDelete")
    private String isdelete;

    private static final long serialVersionUID = 1L;
}