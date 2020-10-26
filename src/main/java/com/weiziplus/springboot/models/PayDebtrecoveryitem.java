package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("pay_debtrecoveryitem")

public class PayDebtrecoveryitem implements Serializable {
    @Id("id")
    private String id;
    @Column("ObjectId")
    private String objectid;
    @Column("DataType")
    private String datatype;
    @Column("RecoveryAmount_CurrencyCode")
    private String recoveryamountCurrencycode;
    @Column("RecoveryAmount_CurrencyAmount")
    private String recoveryamountCurrencyamount;
    @Column("OriginalAmount_CurrencyCode")
    private String originalamountCurrencycode;
    @Column("OriginalAmount_CurrencyAmount")
    private String originalamountCurrencyamount;
    @Column("GroupBeginDate")
    private Date groupbegindate;
    @Column("GroupEndDate")
    private Date groupenddate;
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

    public String getOriginalamountCurrencycode() {
        return originalamountCurrencycode;
    }

    public void setOriginalamountCurrencycode(String originalamountCurrencycode) {
        this.originalamountCurrencycode = originalamountCurrencycode == null ? null : originalamountCurrencycode.trim();
    }

    public String getOriginalamountCurrencyamount() {
        return originalamountCurrencyamount;
    }

    public void setOriginalamountCurrencyamount(String originalamountCurrencyamount) {
        this.originalamountCurrencyamount = originalamountCurrencyamount == null ? null : originalamountCurrencyamount.trim();
    }

    public Date getGroupbegindate() {
        return groupbegindate;
    }

    public void setGroupbegindate(Date groupbegindate) {
        this.groupbegindate = groupbegindate;
    }

    public Date getGroupenddate() {
        return groupenddate;
    }

    public void setGroupenddate(Date groupenddate) {
        this.groupenddate = groupenddate;
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