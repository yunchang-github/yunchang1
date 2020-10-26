package com.weiziplus.springboot.models.DO;

import lombok.Data;

import java.math.BigDecimal;

@Data
/**
 * 现弃用--sjd
 * */
public class ParentBodyInventoryReportDO {
    private String cSku;//子sku
    private String cAsin;//子asin
    private String pSku;//父sku
    private String sellerId;//卖家id
    private String area;//区域
    private String dateType;//周或月
    private String date;//哪一年的第几个月或第几周
    private String createTime;//创建时间
    private Integer sumQuantity;//销量总和
    private BigDecimal unitPrice;//销售单价
    private Integer afnFulfillableQuantity;//可售库存
    private Integer afnInboundShippedQuantity;//待签收库存
    private Integer afnInboundReceivingQuantity;//已签收库存
    private Integer reservedFcTransfers;//转库中库存
    private Integer totalInventory;//总库存
    private Integer afnUnsellableQuantity;//不可售库存
    private Integer sumInvAge90PlusDays;//库存大于90天库存
    private BigDecimal sumImpressions;//展现量
    private BigDecimal sumClicks;//点击量
    private BigDecimal sumCost;//广告费
    private BigDecimal sumAttributedUnitsOrdered;//广告销售量
    private BigDecimal sumAttributedSales;//广告销售额
    private BigDecimal sumAttributedSalesSameSKU;//广告SKU销售额
    private String effectiveConversionRate;//有效转化率
    private String CTR;//CTR
    private String CR;//CR
    private String ACoS;//ACoS
    private String CPC;//CPC
    private BigDecimal sumBuyerVisits;//访客人数
    private String salesViewsConversionRate;//第一周（销量/浏览次数）转化率
    private Integer expectedDailySales;//预估每日销量
    private Integer expected60DaysSales;//预计60天销量
    private Integer expectedTotalInventoryAfter60Days;//预计60日后总库存
    private Integer expectedAvailability;//预计可售天数
    private String expectedSaleDate;//预计可售日期
    private Integer shortageInventoryEstimates;//紧缺库存预估量
    private String clicksVisitsRate;//Click/访客人数%
    private String adsRevRate;//Ads Rev/Rev%
    private String adsCostRate;//Ads Cost/Rev%
}
