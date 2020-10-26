package com.weiziplus.springboot.models.DO;

import lombok.Data;

@Data
public class SalesIndicatorDO {
    private int id;
    private int year;
    private String groupName;//大组名称
    private String boundShop;//绑定的店铺
    private String sellerId;//绑定的店铺卖家id
    private String area;//绑定的店铺区域
    private Long januaryIndicator;
    private Long februaryIndicator;
    private Long MarchIndicator;
    private Long AprilIndicator;
    private Long MayIndicator;
    private Long JuneIndicator;
    private Long JulyIndicator;
    private Long AugustIndicator;
    private Long SeptemberIndicator;
    private Long OctoberIndicator;
    private Long NovemberIndicator;
    private Long DecemberIndicator;
    private Long totalIndicator;
    private String createTime;


    public SalesIndicatorDO() {
        this.januaryIndicator = 0L;
        this.februaryIndicator = 0L;
        this.MarchIndicator = 0L;
        this.AprilIndicator = 0L;
        this.MayIndicator = 0L;
        this.JuneIndicator = 0L;
        this.JulyIndicator = 0L;
        this.AugustIndicator = 0L;
        this.SeptemberIndicator = 0L;
        this.OctoberIndicator = 0L;
        this.NovemberIndicator = 0L;
        this.DecemberIndicator = 0L;
        this.totalIndicator = 0L;
    }
}
