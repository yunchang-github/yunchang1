package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 调拨pack(箱子马帮api)
 * msku_shippbatch_pack
 * @author Administrator
 * @date 2019-08-26 14:59:45
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("msku_shippbatch_pack")
public class MskuShippbatchPack implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * 箱子编号
     */
    @Column("packNo")
    private String packno;

    /**
     * 箱子长
     */
    @Column("length")
    private String length;

    /**
     * 箱子宽
     */
    @Column("width")
    private String width;

    /**
     * 箱子高
     */
    @Column("height")
    private String height;

    /**
     * 箱子毛重
     */
    @Column("weight")
    private String weight;

    private static final long serialVersionUID = 1L;
}