package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 头程运费（手动导入）
 * head_freight
 * @author WeiziPlus
 * @date 2019-07-26 11:35:22
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("head_freight")
public class HeadFreight implements Serializable {
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
     * 区域
     */
    @Column("area")
    private String area;

    /**
     * 店铺
     */
    @Column("shop")
    private String shop;

    /**
     * 头程运费
     */
    @Column("head_freight")
    private String headFreight;

    /**
     * 是否为预估(是:0,否:1)
     */
    @Column("is_estimate")
    private Integer isEstimate;

    private static final long serialVersionUID = 1L;
}