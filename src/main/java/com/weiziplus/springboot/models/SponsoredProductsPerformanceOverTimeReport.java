package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 按时间查看业绩（后台抓取）
 * sponsored_products_performance_over_time_report
 * @author WeiziPlus
 * @date 2019-08-20 09:03:34
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("sponsored_products_performance_over_time_report")
public class SponsoredProductsPerformanceOverTimeReport implements Serializable {
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
     * 日期
     */
    @Column("date")
    private String date;

    /**
     * 点击量
     */
    @Column("clicks")
    private Integer clicks;

    /**
     * 每次点击成本
     */
    @Column("cpc")
    private Double cpc;

    /**
     * 花费
     */
    @Column("spend")
    private Double spend;

    private static final long serialVersionUID = 1L;
}