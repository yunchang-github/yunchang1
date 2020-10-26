package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("pay_loanservicingevent")
public class PayLoanservicingevent implements Serializable {
    @Id("id")
    private String id;
    @Column("ObjectId")
    private String objectid;
    @Column("DataType")
    private String datatype;
    @Column("LoanAmount_CurrencyCode")
    private String loanamountCurrencycode;
    @Column("LoanAmount_CurrencyAmount")
    private String loanamountCurrencyamount;
    @Column("SourceBusinessEventType")
    private String sourcebusinesseventtype;
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

    public String getLoanamountCurrencycode() {
        return loanamountCurrencycode;
    }

    public void setLoanamountCurrencycode(String loanamountCurrencycode) {
        this.loanamountCurrencycode = loanamountCurrencycode == null ? null : loanamountCurrencycode.trim();
    }

    public String getLoanamountCurrencyamount() {
        return loanamountCurrencyamount;
    }

    public void setLoanamountCurrencyamount(String loanamountCurrencyamount) {
        this.loanamountCurrencyamount = loanamountCurrencyamount == null ? null : loanamountCurrencyamount.trim();
    }

    public String getSourcebusinesseventtype() {
        return sourcebusinesseventtype;
    }

    public void setSourcebusinesseventtype(String sourcebusinesseventtype) {
        this.sourcebusinesseventtype = sourcebusinesseventtype == null ? null : sourcebusinesseventtype.trim();
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