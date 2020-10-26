package com.weiziplus.springboot.models.DO;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 汇率实体类
 */
@Data
public class ExchangeRateDO {
    private String code;            /*货币代码*/
    private String currency;        /*货币名称*/
    private BigDecimal closePri;        /*最新价*/
    private String diffPer;        /*涨跌%*/
    private BigDecimal diffAmo;    /*涨跌金额*/
    private BigDecimal openPri;        /*开盘价*/
    private BigDecimal highPic;    /*最高价*/
    private BigDecimal lowPic;    /*最低价*/
    private String range;        /*震幅%*/
    private BigDecimal buyPic;        /*买入价*/
    private BigDecimal sellPic;        /*卖出价*/
    private String color;        /**/
    private BigDecimal yesPic;            /*昨收价*/
    private String date;        /*日期*/
    private String datatime;    /*数据时间*/
}
