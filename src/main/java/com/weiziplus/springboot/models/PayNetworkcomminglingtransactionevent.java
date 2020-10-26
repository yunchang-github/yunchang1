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
@Table("pay_networkcomminglingtransactionevent")
public class PayNetworkcomminglingtransactionevent implements Serializable {
    @Id("id")
    private String id;
    @Column("ObjectId")
    private String objectid;
    @Column("DataType")
    private String datatype;
    @Column("NetCoTransactionID")
    private String netcotransactionid;
    @Column("SwapReason")
    private String swapreason;
    @Column("PostedDate")
    private Date posteddate;
    @Column("TransactionType")
    private String transactiontype;
    @Column("ASIN")
    private String asin;
    @Column("MarketplaceId")
    private String marketplaceid;
    @Column("TaxExclusiveAmount_CurrencyCode")
    private String taxexclusiveamountCurrencycode;
    @Column("TaxExclusiveAmount_CurrencyAmount")
    private String taxexclusiveamountCurrencyamount;
    @Column("TaxAmount_CurrencyCode")
    private String taxamountCurrencycode;
    @Column("TaxAmount_CurrencyAmount")
    private String taxamountCurrencyamount;
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

    public String getNetcotransactionid() {
        return netcotransactionid;
    }

    public void setNetcotransactionid(String netcotransactionid) {
        this.netcotransactionid = netcotransactionid == null ? null : netcotransactionid.trim();
    }

    public String getSwapreason() {
        return swapreason;
    }

    public void setSwapreason(String swapreason) {
        this.swapreason = swapreason == null ? null : swapreason.trim();
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

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin == null ? null : asin.trim();
    }

    public String getMarketplaceid() {
        return marketplaceid;
    }

    public void setMarketplaceid(String marketplaceid) {
        this.marketplaceid = marketplaceid == null ? null : marketplaceid.trim();
    }

    public String getTaxexclusiveamountCurrencycode() {
        return taxexclusiveamountCurrencycode;
    }

    public void setTaxexclusiveamountCurrencycode(String taxexclusiveamountCurrencycode) {
        this.taxexclusiveamountCurrencycode = taxexclusiveamountCurrencycode == null ? null : taxexclusiveamountCurrencycode.trim();
    }

    public String getTaxexclusiveamountCurrencyamount() {
        return taxexclusiveamountCurrencyamount;
    }

    public void setTaxexclusiveamountCurrencyamount(String taxexclusiveamountCurrencyamount) {
        this.taxexclusiveamountCurrencyamount = taxexclusiveamountCurrencyamount == null ? null : taxexclusiveamountCurrencyamount.trim();
    }

    public String getTaxamountCurrencycode() {
        return taxamountCurrencycode;
    }

    public void setTaxamountCurrencycode(String taxamountCurrencycode) {
        this.taxamountCurrencycode = taxamountCurrencycode == null ? null : taxamountCurrencycode.trim();
    }

    public String getTaxamountCurrencyamount() {
        return taxamountCurrencyamount;
    }

    public void setTaxamountCurrencyamount(String taxamountCurrencyamount) {
        this.taxamountCurrencyamount = taxamountCurrencyamount == null ? null : taxamountCurrencyamount.trim();
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