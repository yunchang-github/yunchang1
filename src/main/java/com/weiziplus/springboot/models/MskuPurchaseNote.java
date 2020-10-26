package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * msku采购注意事项(手动导入)
 * msku_purchase_note
 *
 * @author WeiziPlus
 * @date 2019-07-29 08:34:15
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("msku_purchase_note")
public class MskuPurchaseNote implements Serializable {
    /**
     * 自增
     */
    @Id("id")
    private Long id;

    /**
     * msku
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
     * 注意事项
     */
    @Column("note")
    private String note;

    private static final long serialVersionUID = 1L;
}