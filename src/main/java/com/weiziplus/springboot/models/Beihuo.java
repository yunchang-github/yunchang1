package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * beihuo
 * @author Administrator
 * @date 2019-09-12 09:29:53
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("beihuo")
public class Beihuo implements Serializable {
    /**
     */
    @Id("id")
    private Integer id;

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
    @Column("stockSku")
    private String stocksku;

    /**
     */
    @Column("asin")
    private String asin;

    /**
     * 备货天数
     */
    @Column("stocking_days")
    private Byte stockingDays;

    /**
     */
    @Column("percentage_past3_days")
    private Double percentagePast3Days;

    /**
     */
    @Column("percentage_recent_week")
    private Double percentageRecentWeek;

    /**
     */
    @Column("percentage_past_two_weeks")
    private Double percentagePastTwoWeeks;

    /**
     */
    @Column("percentage_past_three_weeks")
    private Double percentagePastThreeWeeks;

    /**
     */
    @Column("percentage_past_four_weeks")
    private Double percentagePastFourWeeks;

    /**
     * 特殊情况
     */
    @Column("special_case")
    private String specialCase;

    /**
     * 需求数量
     */
    @Column("required_quantity")
    private Integer requiredQuantity;

    /**
     * 重点标记：0不重点；1重点
     */
    @Column("is_special_msku")
    private Byte isSpecialMsku;

    /**
     * 销售波动大： 0 波动不大；1波动大
     */
    @Column("high_sales_fluctuations")
    private Byte highSalesFluctuations;

    /**
     * 品类
     */
    @Column("category")
    private String category;

    private static final long serialVersionUID = 1L;
}