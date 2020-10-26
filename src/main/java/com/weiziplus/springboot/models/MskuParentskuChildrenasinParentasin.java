package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * MSKU子父体对应关系
 * msku_parentsku_childrenasin_parentasin
 * @author WeiziPlus
 * @date 2019-07-26 09:16:02
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("msku_parentsku_childrenasin_parentasin")
public class MskuParentskuChildrenasinParentasin implements Serializable {
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
    @Column("msku")
    private String msku;

    /**
     */
    @Column("parent_sku")
    private String parentSku;

    private static final long serialVersionUID = 1L;
}