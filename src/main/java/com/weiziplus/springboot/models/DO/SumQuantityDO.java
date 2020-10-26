package com.weiziplus.springboot.models.DO;

import lombok.Data;

import javax.print.DocFlavor;

@Data
public class SumQuantityDO {
    private String sku;
    private String asin;
    private String shopName;
    private String areaCode;
    private Integer sumQuantity;
    private Integer latestWeek;
    private String createTime;

    public SumQuantityDO(String shopName, String areaCode,Integer latestWeek){
        this.shopName = shopName;
        this.areaCode = areaCode;
        this.sumQuantity = 0;
        this.latestWeek = latestWeek;
    }
}
