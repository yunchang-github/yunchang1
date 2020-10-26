package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * original_data_adv_asins_report
 * @author Administrator
 * @date 2019-11-13 18:39:38
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("original_data_adv_asins_report")
public class OriginalDataAdvAsinsReport implements Serializable {
    /**
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
     * 买家ID
     */
    @Column("seller_id")
    private String sellerId;

    /**
     * 广告活动的唯一名称
     */
    @Column("campaign_name")
    private String campaignName;

    /**
     * 广告活动的唯一数字ID
     */
    @Column("campaign_id")
    private Long campaignId;

    /**
     */
    @Column("ad_group_id")
    private Long adGroupId;

    /**
     */
    @Column("ad_group_name")
    private String adGroupName;

    /**
     */
    @Column("keyword_id")
    private Long keywordId;

    /**
     */
    @Column("keyword_text")
    private String keywordText;

    /**
     */
    @Column("match_type")
    private String matchType;

    /**
     */
    @Column("asin")
    private String asin;

    /**
     */
    @Column("other_asin")
    private String otherAsin;

    /**
     */
    @Column("sku")
    private String sku;

    /**
     */
    @Column("currency")
    private String currency;

    /**
     */
    @Column("attributed_units_ordered1d")
    private Integer attributedUnitsOrdered1d;

    /**
     */
    @Column("attributed_units_ordered7d")
    private Integer attributedUnitsOrdered7d;

    /**
     */
    @Column("attributed_units_ordered14d")
    private Integer attributedUnitsOrdered14d;

    /**
     */
    @Column("attributed_units_ordered30d")
    private Integer attributedUnitsOrdered30d;

    /**
     * 另一个ASIN（SKU）的销售 1天
     */
    @Column("attributed_sales1d_other_SKU")
    private Double attributedSales1dOtherSku;

    /**
     * 另一个ASIN（SKU）的销售 7天
     */
    @Column("attributed_sales7d_other_SKU")
    private Double attributedSales7dOtherSku;

    /**
     * 另一个ASIN（SKU）的销售 14天
     */
    @Column("attributed_sales14d_other_SKU")
    private Double attributedSales14dOtherSku;

    /**
     * 另一个ASIN（SKU）的销售 30天
     */
    @Column("attributed_sales30d_other_SKU")
    private Double attributedSales30dOtherSku;

    /**
     */
    @Column("attributed_units_ordered1d_other_SKU")
    private Integer attributedUnitsOrdered1dOtherSku;

    /**
     */
    @Column("attributed_units_ordered7d_other_SKU")
    private Integer attributedUnitsOrdered7dOtherSku;

    /**
     */
    @Column("attributed_units_ordered14d_other_SKU")
    private Integer attributedUnitsOrdered14dOtherSku;

    /**
     */
    @Column("attributed_units_ordered30d_other_SKU")
    private Integer attributedUnitsOrdered30dOtherSku;

    /**
     * api中的reportDate
     */
    @Column("date")
    private String date;

    /**
     * 数据保存到数据库时间
     */
    @Column("create_time")
    private String createTime;

    private static final long serialVersionUID = 1L;
}