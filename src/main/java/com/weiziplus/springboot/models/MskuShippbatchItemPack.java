package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 调拨货物安排关系表
 * msku_shippbatch_item_pack
 * @author Administrator
 * @date 2019-08-26 16:39:29
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("msku_shippbatch_item_pack")
public class MskuShippbatchItemPack implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * 箱子id
     */
    @Column("packId")
    private Long packid;

    /**
     * itemid
     */
    @Column("itemId")
    private Long itemid;

    /**
     * 装箱数量
     */
    @Column("delieverQuantity")
    private String delieverquantity;
    
    /**
     * 箱号
     */
    @Column("packNo")
    private String packNo;

    private static final long serialVersionUID = 1L;
}