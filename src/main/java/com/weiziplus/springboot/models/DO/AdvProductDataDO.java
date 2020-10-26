package com.weiziplus.springboot.models.DO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdvProductDataDO {
    private String cSku;//子sku
    private String cAsin;//子asin
    private String pSku;//父sku
    private String shop;
    private String area;
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

}
