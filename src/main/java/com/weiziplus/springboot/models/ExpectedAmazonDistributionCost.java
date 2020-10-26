package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 预估亚马逊配送费(手动导入)
 * expected_amazon_distribution_cost
 * @author WeiziPlus
 * @date 2019-07-26 14:31:18
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("expected_amazon_distribution_cost")
public class ExpectedAmazonDistributionCost implements Serializable {
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
     * 预计配送费
     */
    @Column("expected_distribution_cost")
    private String expectedDistributionCost;

    private static final long serialVersionUID = 1L;
}