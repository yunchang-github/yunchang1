package com.weiziplus.springboot.models.VO;

import lombok.Data;
import net.bytebuddy.asm.Advice;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 子体广告库存报表展示类---sjd
 *
 * */
@Data
public class ChildItemAdInventoryReportVO implements Serializable{
    private String childSKU;//子sku
    private String childASIN;//子asin
    private String parentSKU;//父sku
    private Integer firstSumQuantity;//第一周销量总和
    private Integer secondSumQuantity;//第二周销量总和
    private Integer thirdSumQuantity;//第三周销量总和
    private Integer fourthSumQuantity;//第四周销量总和
    private BigDecimal firstUnitPrice;//第一周销售单价
    private BigDecimal secondUnitPrice;//第二周销售单价
    private BigDecimal thirdUnitPrice;//第三周销售单价
    private BigDecimal fourthUnitPrice;//第四周销售单价
    private Integer afnFulfillableQuantity;//可售库存
    private Integer afnInboundShippedQuantity;//待签收库存
    private Integer afnInboundReceivingQuantity;//已签收库存
    private Integer reservedFcTransfers;//转库中库存
    private Integer totalInventory;//总库存
    private Integer afnUnsellableQuantity;//不可售库存
    private Integer sumInvAge90PlusDays;//库存大于90天库存
    private BigDecimal firstSumImpressions;//广告第一周展现量
    private BigDecimal secondSumImpressions;//广告第二周展现量
    private BigDecimal thirdSumImpressions;//广告第三周展现量
    private BigDecimal fourthSumImpressions;//广告第四周展现量
    private BigDecimal firstSumClicks;//广告第一周点击量
    private BigDecimal secondSumClicks;//广告第二周点击量
    private BigDecimal thirdSumClicks;//广告第三周点击量
    private BigDecimal fourthSumClicks;//广告第四周点击量
    private BigDecimal firstSumCost;//广告第一周广告费
    private BigDecimal secondSumCost;//广告第二周广告费
    private BigDecimal thirdSumCost;//广告第三周广告费
    private BigDecimal fourthSumCost;//广告第四周广告费
    private BigDecimal firstSumAttributedUnitsOrdered;//广告第一周广告销售量
    private BigDecimal secondSumAttributedUnitsOrdered;//广告第二周广告销售量
    private BigDecimal thirdSumAttributedUnitsOrdered;//广告第三周广告销售量
    private BigDecimal fourthSumAttributedUnitsOrdered;//广告第四周广告销售量
    private BigDecimal firstSumAttributedSales;//广告第一周广告销售额
    private BigDecimal secondSumAttributedSales;//广告第二周广告销售额
    private BigDecimal thirdSumAttributedSales;//广告第三周广告销售额
    private BigDecimal fourthSumAttributedSales;//广告第四周广告销售额
    private BigDecimal firstSumAttributedSalesSameSKU;//广告第一周广告SKU销售额
    private BigDecimal secondSumAttributedSalesSameSKU;//广告第二周广告SKU销售额
    private BigDecimal thirdSumAttributedSalesSameSKU;//广告第三周广告SKU销售额
    private BigDecimal fourthSumAttributedSalesSameSKU;//广告第四周广告SKU销售额
    private String firstEffectiveConversionRate;//第一周有效转化率
    private String secondEffectiveConversionRate;//第二周有效转化率
    private String thirdEffectiveConversionRate;//第三周有效转化率
    private String fourthEffectiveConversionRate;//第四周有效转化率
    private String firstCTR;//第一周CTR
    private String secondCTR;//第二周CTR
    private String thirdCTR;//第三周CTR
    private String fourthCTR;//第四周CTR
    private String firstCR;//第一周CR
    private String secondCR;//第二周CR
    private String thirdCR;//第三周CR
    private String fourthCR;//第四周CR
    private String firstACoS;//第一周ACoS
    private String secondACoS;//第二周ACoS
    private String thirdACoS;//第三周ACoS
    private String fourthACoS;//第四周ACoS
    private String firstCPC;//第一周CPC
    private String secondCPC;//第二周CPC
    private String thirdCPC;//第三周CPC
    private String fourthCPC;//第四周CPC
    private BigDecimal firstSumBuyerVisits;//第一周访客人数
    private BigDecimal secondSumBuyerVisits;//第二周访客人数
    private BigDecimal thirdSumBuyerVisits;//第三周访客人数
    private BigDecimal fourthSumBuyerVisits;//第四周访客人数
    private String firstSalesViewsConversionRate;//第一周（销量/浏览次数）转化率
    private String secondSalesViewsConversionRate;//第二周（销量/浏览次数）转化率
    private String thirdSalesViewsConversionRate;//第三周（销量/浏览次数）转化率
    private String fourthSalesViewsConversionRate;//第四周（销量/浏览次数）转化率
    private Integer expectedDailySales;//预估每日销量
    private Integer expected60DaysSales;//预计60天销量
    private Integer expectedTotalInventoryAfter60Days;//预计60日后总库存
    private Integer expectedAvailability;//预计可售天数
    private String expectedSaleDate;//预计可售日期
    private Integer shortageInventoryEstimates;//紧缺库存预估量
    private String firstClicksVisitsRate;//第一周Click/访客人数%
    private String secondClicksVisitsRate;//第二周Click/访客人数%
    private String thirdClicksVisitsRate;//第三周Click/访客人数%
    private String fourthClicksVisitsRate;//第四周Click/访客人数%
    private String firstAdsRevRate;//第一周Ads Rev/Rev%
    private String secondAdsRevRate;//第二周Ads Rev/Rev%
    private String thirdAdsRevRate;//第三周Ads Rev/Rev%
    private String fourthAdsRevRate;//第四周Ads Rev/Rev%
    private String firstAdsCostRate;//第一周Ads Cost/Rev%
    private String secondAdsCostRate;//第二周Ads Cost/Rev%
    private String thirdAdsCostRate;//第三周Ads Cost/Rev%
    private String fourthAdsCostRate;//第四周Ads Cost/Rev%
    private Integer firstLightningDealsWeek;//第一周该asin秒杀的信息
    private Integer secondLightningDealsWeek;//第二周该asin秒杀的信息
    private Integer thirdLightningDealsWeek;//第三周该asin秒杀的信息
    private Integer fourthLightningDealsWeek;//第四周该asin秒杀的信息
}
