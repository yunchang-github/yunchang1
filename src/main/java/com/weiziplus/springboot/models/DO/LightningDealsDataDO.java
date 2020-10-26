package com.weiziplus.springboot.models.DO;

import lombok.Data;

@Data
public class LightningDealsDataDO {
    private String cSku;//子sku
    private String cAsin;//子asin
    private String pSku;//父sku
    private String shop;
    private String area;
    private Integer weekOfYear;//该asin的秒杀数据对应的周
}
