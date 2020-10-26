package com.weiziplus.springboot.models;

import lombok.Data;
/*
* 该实体类只是用来对应销售量与访问量报表中的表头数据
* */
@Data
public class SalesAndTrafficColumn {
    private String ValueFormat;
    private String default_selected;
    private String def;
    private String name;
    private String defaultTitle;
    private String decimal;
    private String total;
    private Integer graphable;
    private String default_graphed;
    private String display;
}
