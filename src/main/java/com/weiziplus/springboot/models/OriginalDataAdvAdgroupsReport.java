package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * original_data_adv_adgroups_report
 * @author Administrator
 * @date 2019-11-13 15:22:03
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("original_data_adv_adgroups_report")
public class OriginalDataAdvAdgroupsReport implements Serializable {
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
     * 总广告展示次数
     */
    @Column("impressions")
    private Integer impressions;

    /**
     * 广告点击总数
     */
    @Column("clicks")
    private Integer clicks;

    /**
     * 所有点击的总费用
     */
    @Column("cost")
    private Double cost;

    /**
     * 点击广告后1天内发生的转化数
     */
    @Column("attributed_conversions1d")
    private Integer attributedConversions1d;

    /**
     * 点击广告后7天内发生的转化数
     */
    @Column("attributed_conversions7d")
    private Integer attributedConversions7d;

    /**
     * 点击广告后14天内发生的转化数
     */
    @Column("attributed_conversions14d")
    private Integer attributedConversions14d;

    /**
     * 点击广告后30天内发生的转化数
     */
    @Column("attributed_conversions30d")
    private Integer attributedConversions30d;

    /**
     * 点击的广告在1天内（购买的SKU与广告的SKU相同）发生的转化数
     */
    @Column("attributed_conversions1d_same_SKU")
    private Integer attributedConversions1dSameSku;

    /**
     * 点击的广告在7天内（购买的SKU与广告的SKU相同）发生的转化数
     */
    @Column("attributed_conversions7d_same_SKU")
    private Integer attributedConversions7dSameSku;

    /**
     * 点击的广告在14天内（购买的SKU与广告的SKU相同）发生的转化数
     */
    @Column("attributed_conversions14d_same_SKU")
    private Integer attributedConversions14dSameSku;

    /**
     * 点击的广告在30天内（购买的SKU与广告的SKU相同）发生的转化数
     */
    @Column("attributed_conversions30d_same_SKU")
    private Integer attributedConversions30dSameSku;

    /**
     * 点击广告后1天内订购的数量
     */
    @Column("attributed_units_ordered1d")
    private Integer attributedUnitsOrdered1d;

    /**
     * 点击广告后7天内订购的数量
     */
    @Column("attributed_units_ordered7d")
    private Integer attributedUnitsOrdered7d;

    /**
     * 点击广告后14天内订购的数量
     */
    @Column("attributed_units_ordered14d")
    private Integer attributedUnitsOrdered14d;

    /**
     * 点击广告后30天内订购的数量
     */
    @Column("attributed_units_ordered30d")
    private Integer attributedUnitsOrdered30d;

    /**
     * 点击广告后1天内发生的销售次数
     */
    @Column("attributed_sales1d")
    private Double attributedSales1d;

    /**
     * 点击广告后7天内发生的销售次数
     */
    @Column("attributed_sales7d")
    private Double attributedSales7d;

    /**
     * 点击广告后14天内发生的销售次数
     */
    @Column("attributed_sales14d")
    private Double attributedSales14d;

    /**
     * 点击广告后30天内发生的销售次数
     */
    @Column("attributed_sales30d")
    private Double attributedSales30d;

    /**
     * 点击广告后1天内发生的销售次数（其中购买的SKU与广告的SKU相同）
     */
    @Column("attributed_sales1d_same_SKU")
    private Double attributedSales1dSameSku;

    /**
     * 点击广告后7天内发生的销售次数（其中购买的SKU与广告的SKU相同）
     */
    @Column("attributed_sales7d_same_SKU")
    private Double attributedSales7dSameSku;

    /**
     * 点击广告后14天内发生的销售次数（其中购买的SKU与广告的SKU相同）
     */
    @Column("attributed_sales14d_same_SKU")
    private Double attributedSales14dSameSku;

    /**
     * 点击广告后30天内发生的销售次数（其中购买的SKU与广告的SKU相同）
     */
    @Column("attributed_sales30d_same_SKU")
    private Double attributedSales30dSameSku;

    /**
     * 点击的广告在1天内订购的数量（购买的SKU与广告的SKU相同）
     */
    @Column("attributed_units_ordered1d_same_SKU")
    private Integer attributedUnitsOrdered1dSameSku;

    /**
     * 点击的广告在7天内订购的数量（购买的SKU与广告的SKU相同）
     */
    @Column("attributed_units_ordered7d_same_SKU")
    private Integer attributedUnitsOrdered7dSameSku;

    /**
     * 点击的广告在14天内订购的数量（购买的SKU与广告的SKU相同）
     */
    @Column("attributed_units_ordered14d_same_SKU")
    private Integer attributedUnitsOrdered14dSameSku;

    /**
     * 点击的广告在30天内订购的数量（购买的SKU与广告的SKU相同）
     */
    @Column("attributed_units_ordered30d_same_SKU")
    private Integer attributedUnitsOrdered30dSameSku;

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