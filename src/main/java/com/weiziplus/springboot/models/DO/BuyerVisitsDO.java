package com.weiziplus.springboot.models.DO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BuyerVisitsDO {
    private String cSku;//子sku
    private String cAsin;//子asin
    private String pSku;//父sku
    private String shop;
    private String area;
    private BigDecimal sumBuyerVisits;//访客人数

}
