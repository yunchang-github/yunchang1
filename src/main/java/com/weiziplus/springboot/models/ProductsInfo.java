package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品信息表(手动导入)
 * products_info
 * @author WeiziPlus
 * @date 2019-07-26 16:16:30
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("products_info")
public class ProductsInfo implements Serializable {
    /**
     * 自增
     */
    @Id("id")
    private Long id;

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
     * msku
     */
    @Column("msku")
    private String msku;

    /**
     * 库存SKU
     */
    @Column("inventory_sku")
    private String inventorySku;

    /**
     * 品类
     */
    @Column("category")
    private String category;

    /**
     * 尺寸
     */
    @Column("size")
    private String size;

    /**
     * 款式
     */
    @Column("style")
    private String style;

    private static final long serialVersionUID = 1L;
}