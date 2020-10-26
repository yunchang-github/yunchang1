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
@Table("pay_productadspaymentevent")
public class PayProductadspaymentevent implements Serializable {
    @Id("id")
    private String id;
    @Column("ObjectId")
    private String objectid;
    @Column("DataType")
    private String datatype;
    @Column("postedDate")
    private Date posteddate;
    @Column("transactionType")
    private String transactiontype;
    @Column("invoiceId")
    private String invoiceid;
    @Column("baseValue_CurrencyCode")
    private String basevalueCurrencycode;
    @Column("baseValue_CurrencyAmount")
    private String basevalueCurrencyamount;
    @Column("taxValue_CurrencyCode")
    private String taxvalueCurrencycode;
    @Column("taxValue_CurrencyAmount")
    private String taxvalueCurrencyamount;
    @Column("transactionValue_CurrencyCode")
    private String transactionvalueCurrencycode;
    @Column("transactionValue_CurrencyAmount")
    private String transactionvalueCurrencyamount;
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

    public Date getPosteddate() {
        return posteddate;
    }

    public void setPosteddate(Date posteddate) {
        this.posteddate = posteddate;
    }

    public String getTransactiontype() {
        return transactiontype;
    }

    public void setTransactiontype(String transactiontype) {
        this.transactiontype = transactiontype == null ? null : transactiontype.trim();
    }

    public String getInvoiceid() {
        return invoiceid;
    }

    public void setInvoiceid(String invoiceid) {
        this.invoiceid = invoiceid == null ? null : invoiceid.trim();
    }

    public String getBasevalueCurrencycode() {
        return basevalueCurrencycode;
    }

    public void setBasevalueCurrencycode(String basevalueCurrencycode) {
        this.basevalueCurrencycode = basevalueCurrencycode == null ? null : basevalueCurrencycode.trim();
    }

    public String getBasevalueCurrencyamount() {
        return basevalueCurrencyamount;
    }

    public void setBasevalueCurrencyamount(String basevalueCurrencyamount) {
        this.basevalueCurrencyamount = basevalueCurrencyamount == null ? null : basevalueCurrencyamount.trim();
    }

    public String getTaxvalueCurrencycode() {
        return taxvalueCurrencycode;
    }

    public void setTaxvalueCurrencycode(String taxvalueCurrencycode) {
        this.taxvalueCurrencycode = taxvalueCurrencycode == null ? null : taxvalueCurrencycode.trim();
    }

    public String getTaxvalueCurrencyamount() {
        return taxvalueCurrencyamount;
    }

    public void setTaxvalueCurrencyamount(String taxvalueCurrencyamount) {
        this.taxvalueCurrencyamount = taxvalueCurrencyamount == null ? null : taxvalueCurrencyamount.trim();
    }

    public String getTransactionvalueCurrencycode() {
        return transactionvalueCurrencycode;
    }

    public void setTransactionvalueCurrencycode(String transactionvalueCurrencycode) {
        this.transactionvalueCurrencycode = transactionvalueCurrencycode == null ? null : transactionvalueCurrencycode.trim();
    }

    public String getTransactionvalueCurrencyamount() {
        return transactionvalueCurrencyamount;
    }

    public void setTransactionvalueCurrencyamount(String transactionvalueCurrencyamount) {
        this.transactionvalueCurrencyamount = transactionvalueCurrencyamount == null ? null : transactionvalueCurrencyamount.trim();
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