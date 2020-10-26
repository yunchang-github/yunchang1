package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("pay_debtrecoveryevent")
public class PayDebtrecoveryevent implements Serializable {
    @Id("id")
    private String id;
    @Column("ObjectId")
    private String objectid;
    @Column("DataType")
    private String datatype;
    @Column("DebtRecoveryType")
    private String debtrecoverytype;
    @Column("RecoveryAmount_CurrencyCode")
    private String recoveryamountCurrencycode;
    @Column("RecoveryAmount_CurrencyAmount")
    private String recoveryamountCurrencyamount;
    @Column("OverPaymentCredit_CurrencyCode")
    private String overpaymentcreditCurrencycode;
    @Column("OverPaymentCredit_CurrencyAmount")
    private String overpaymentcreditCurrencyamount;
    @Column("shop")
    private String shop;
    @Column("area")
    private String area;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObjectid() {
        return objectid;
    }

    public void setObjectid(String objectid) {
        this.objectid = objectid == null ? null : objectid.trim();
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype == null ? null : datatype.trim();
    }

    public String getDebtrecoverytype() {
        return debtrecoverytype;
    }

    public void setDebtrecoverytype(String debtrecoverytype) {
        this.debtrecoverytype = debtrecoverytype == null ? null : debtrecoverytype.trim();
    }

    public String getRecoveryamountCurrencycode() {
        return recoveryamountCurrencycode;
    }

    public void setRecoveryamountCurrencycode(String recoveryamountCurrencycode) {
        this.recoveryamountCurrencycode = recoveryamountCurrencycode == null ? null : recoveryamountCurrencycode.trim();
    }

    public String getRecoveryamountCurrencyamount() {
        return recoveryamountCurrencyamount;
    }

    public void setRecoveryamountCurrencyamount(String recoveryamountCurrencyamount) {
        this.recoveryamountCurrencyamount = recoveryamountCurrencyamount == null ? null : recoveryamountCurrencyamount.trim();
    }

    public String getOverpaymentcreditCurrencycode() {
        return overpaymentcreditCurrencycode;
    }

    public void setOverpaymentcreditCurrencycode(String overpaymentcreditCurrencycode) {
        this.overpaymentcreditCurrencycode = overpaymentcreditCurrencycode == null ? null : overpaymentcreditCurrencycode.trim();
    }

    public String getOverpaymentcreditCurrencyamount() {
        return overpaymentcreditCurrencyamount;
    }

    public void setOverpaymentcreditCurrencyamount(String overpaymentcreditCurrencyamount) {
        this.overpaymentcreditCurrencyamount = overpaymentcreditCurrencyamount == null ? null : overpaymentcreditCurrencyamount.trim();
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop == null ? null : shop.trim();
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area == null ? null : area.trim();
    }
}