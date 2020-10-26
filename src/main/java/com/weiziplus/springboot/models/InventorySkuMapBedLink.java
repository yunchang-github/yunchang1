package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * 库存SKU图床链接
 * inventory_sku_map_bed_link
 * @author Administrator
 * @date 2019-09-23 11:20:07
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("inventory_sku_map_bed_link")
public class InventorySkuMapBedLink implements Serializable {
    /**
     */
    @Id("id")
    private Integer id;

    /**
     * 库存sku
     */
    @Column("inventory_sku")
    private String inventorySku;

    /**
     * 链接
     */
    @Column("link")
    private String link;

    private static final long serialVersionUID = 1L;
}