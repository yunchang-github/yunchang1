package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单关联数据主表（马帮api）
 * order_data
 * @author WeiziPlus
 * @date 2019-09-03 11:57:26
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("order_data")
public class OrderData implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * 数据获取日期
     */
    @Column("date")
    private String date;

    /**
     */
    @Column("companyId")
    private String companyid;

    /**
     * 订单编号
     */
    @Column("platformOrderId")
    private String platformorderid;

    /**
     * 交易编号(平台交易号)
     */
    @Column("salesRecordNumber")
    private String salesrecordnumber;

    /**
     * 付款时间
     */
    @Column("paidTime")
    private String paidtime;

    /**
     */
    @Column("updateTime")
    private String updatetime;

    /**
     */
    @Column("shopId")
    private String shopid;

    /**
     * 店铺名
     */
    @Column("shopName")
    private String shopname;

    /**
     */
    @Column("platformId")
    private String platformid;

    /**
     * 平台名
     */
    @Column("platformName")
    private String platformname;

    /**
     */
    @Column("orderStatus")
    private String orderstatus;

    /**
     * 状态
     */
    @Column("orderStatusCn")
    private String orderstatuscn;

    /**
     * 原始商品总金额
     */
    @Column("itemTotalOrigin")
    private String itemtotalorigin;

    /**
     * 订单原始总金额
     */
    @Column("orderTotalOrigin")
    private String ordertotalorigin;

    /**
     */
    @Column("currencyId")
    private String currencyid;

    /**
     * 订单总重量
     */
    @Column("orderWeight")
    private String orderweight;

    /**
     * 是否退货，1 是 2 否
     */
    @Column("isReturned")
    private String isreturned;

    /**
     * 发货时间
     */
    @Column("expressTime")
    private String expresstime;

    /**
     * 商品总数量
     */
    @Column("skuNum")
    private String skunum;

    private static final long serialVersionUID = 1L;
}