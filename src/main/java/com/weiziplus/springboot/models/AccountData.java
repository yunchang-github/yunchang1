package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 银行账户（马帮api）
 * account_data
 * @author WeiziPlus
 * @date 2019-09-03 11:31:55
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("account_data")
public class AccountData implements Serializable {
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
     */
    @Column("accountId")
    private String accountid;

    /**
     * 账户
     */
    @Column("accountName")
    private String accountname;

    /**
     * 收入或者支出 1收入 2支出
     */
    @Column("revenueExpenditure")
    private String revenueexpenditure;

    /**
     * 类型：1收款 3付款 5账户变动 7费用
     */
    @Column("type")
    private String type;

    /**
     * 单据编号
     */
    @Column("orderId")
    private String orderid;

    /**
     * 金额
     */
    @Column("variable")
    private String variable;

    /**
     * 余额
     */
    @Column("balance")
    private String balance;

    /**
     * 币种
     */
    @Column("currency")
    private String currency;

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
     */
    @Column("confirmorId")
    private String confirmorid;

    /**
     * 确认人
     */
    @Column("confirmorName")
    private String confirmorname;

    /**
     * 操作时间
     */
    @Column("modifyTime")
    private String modifytime;

    /**
     * 备注
     */
    @Column("comment")
    private String comment;

    /**
     */
    @Column("createDataTime")
    private String createdatatime;

    private static final long serialVersionUID = 1L;
}