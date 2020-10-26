package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 收付款单(马帮api)
 * bills_data
 * @author WeiziPlus
 * @date 2019-09-03 11:03:45
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("bills_data")
public class BillsData implements Serializable {
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
     * 费用单号
     */
    @Column("payMentOrderId")
    private String paymentorderid;

    /**
     * 关联单号
     */
    @Column("orderNum")
    private String ordernum;

    /**
     * 1688单号
     */
    @Column("ali1688OrderId")
    private String ali1688orderid;

    /**
     * 账号
     */
    @Column("accountId")
    private String accountid;

    /**
     * 费用类型1收款单  2 付款单  3费用单
     */
    @Column("orderType")
    private String ordertype;

    /**
     * 币种
     */
    @Column("currency")
    private String currency;

    /**
     * 收付款类型
     */
    @Column("paymentTerm")
    private String paymentterm;

    /**
     */
    @Column("paymentTermId")
    private String paymenttermid;

    /**
     * 付款方式
     */
    @Column("paymentTermDesc")
    private String paymenttermdesc;

    /**
     */
    @Column("paymentSupplierId")
    private String paymentsupplierid;

    /**
     * 供应商
     */
    @Column("paymentSupplierName")
    private String paymentsuppliername;

    /**
     * 应收应付金额
     */
    @Column("totalAmount")
    private String totalamount;

    /**
     * 实收实付金额
     */
    @Column("amount")
    private String amount;

    /**
     * 状态1未确认 2已确认 3已完成 9删除
     */
    @Column("status")
    private String status;

    /**
     * 预计付款收款时间
     */
    @Column("prepayTime")
    private String prepaytime;

    /**
     */
    @Column("createrId")
    private String createrid;

    /**
     * 创建人
     */
    @Column("createrName")
    private String creatername;

    /**
     * 创建时间
     */
    @Column("createTime")
    private String createtime;

    /**
     */
    @Column("updateTime")
    private String updatetime;

    /**
     */
    @Column("auditorId")
    private String auditorid;

    /**
     * 审核人
     */
    @Column("auditor")
    private String auditor;

    /**
     */
    @Column("payerId")
    private String payerid;

    /**
     * 收款付款人名称
     */
    @Column("payerName")
    private String payername;

    /**
     * 收款付款人完成时间
     */
    @Column("payTime")
    private String paytime;

    /**
     */
    @Column("comment")
    private String comment;

    private static final long serialVersionUID = 1L;
}