package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 采购价(手动导入)
 * purchase_price
 * @author WeiziPlus
 * @date 2019-07-26 14:11:41
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("purchase_price")
public class PurchasePrice implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * MSKU
     */
    @Column("msku")
    private String msku;

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
     * 库存sku
     */
    @Column("kcsku")
    private String kcsku;

    /**
     * 采购价
     */
    @Column("purchase_price")
    private String purchasePrice;

    /**
     * 是否为预估(0:是，1:否)
     */
    @Column("is_estimate")
    private Integer isEstimate;

    private static final long serialVersionUID = 1L;
}