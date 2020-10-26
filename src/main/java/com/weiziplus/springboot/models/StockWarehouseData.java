package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 库存查询（商品库存关联数据   马帮api）
 * stock_warehouse_data
 *
 * @author WeiziPlus
 * @date 2019-09-03 08:51:51
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("stock_warehouse_data")
public class StockWarehouseData implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * 数据获取日期
     */
    @Column("date")
    private String date;

    /**
     */
    @Column("companyId")
    private String companyid;

    /**
     */
    @Column("stockId")
    private String stockid;

    /**
     * 库存SKU编号（商品sku）
     */
    @Column("stockSku")
    private String stocksku;

    /**
     * 商品名称
     */
    @Column("stockName")
    private String stockname;

    /**
     */
    @Column("wearhouseId")
    private String wearhouseid;

    /**
     * 仓库名称
     */
    @Column("warhouseName")
    private String warhousename;

    /**
     * 库存
     */
    @Column("stockQuantity")
    private String stockquantity;

    /**
     * 在途量
     */
    @Column("shippingQuantity")
    private String shippingquantity;

    /**
     * 成本价
     */
    @Column("defaultCost")
    private String defaultcost;

    /**
     * 总价值
     */
    @Column("totalValue")
    private String totalvalue;

    /**
     * 最后出库时间
     */
    @Column("lastDepotTime")
    private String lastdepottime;

    /**
     * 最后入库时间
     */
    @Column("lastStorageTime")
    private String laststoragetime;

    /**
     */
    @Column("createTime")
    private String createtime;

    private static final long serialVersionUID = 1L;
}