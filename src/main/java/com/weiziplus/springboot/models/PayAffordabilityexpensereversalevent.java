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
@Table("pay_affordabilityexpensereversalevent")
public class PayAffordabilityexpensereversalevent implements Serializable {
    @Id("id")
    private String id;
    @Column("ObjectId")
    private String objectid;
    @Column("DataType")
    private String datatype;
    @Column("TransactionType")
    private String transactiontype;
    @Column("BaseExpense_CurrencyCode")
    private String baseexpenseCurrencycode;
    @Column("BaseExpense_CurrencyAmount")
    private String baseexpenseCurrencyamount;
    @Column("PostedDate")
    private Date posteddate;
    @Column("AmazonOrderId")
    private String amazonorderid;
    @Column("TotalExpense_CurrencyCode")
    private String totalexpenseCurrencycode;
    @Column("TotalExpense_CurrencyAmount")
    private String totalexpenseCurrencyamount;
    @Column("TaxTypeIGST_CurrencyCode")
    private String taxtypeigstCurrencycode;
    @Column("TaxTypeIGST_CurrencyAmount")
    private String taxtypeigstCurrencyamount;
    @Column("TaxTypeCGST_CurrencyCode")
    private String taxtypecgstCurrencycode;
    @Column("TaxTypeCGST_CurrencyAmount")
    private String taxtypecgstCurrencyamount;
    @Column("TaxTypeSGST_CurrencyCode")
    private String taxtypesgstCurrencycode;
    @Column("TaxTypeSGST_CurrencyAmount")
    private String taxtypesgstCurrencyamount;
    @Column("MarketplaceId")
    private String marketplaceid;
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

    public String getTransactiontype() {
        return transactiontype;
    }

    public void setTransactiontype(String transactiontype) {
        this.transactiontype = transactiontype == null ? null : transactiontype.trim();
    }

    public String getBaseexpenseCurrencycode() {
        return baseexpenseCurrencycode;
    }

    public void setBaseexpenseCurrencycode(String baseexpenseCurrencycode) {
        this.baseexpenseCurrencycode = baseexpenseCurrencycode == null ? null : baseexpenseCurrencycode.trim();
    }

    public String getBaseexpenseCurrencyamount() {
        return baseexpenseCurrencyamount;
    }

    public void setBaseexpenseCurrencyamount(String baseexpenseCurrencyamount) {
        this.baseexpenseCurrencyamount = baseexpenseCurrencyamount == null ? null : baseexpenseCurrencyamount.trim();
    }

    public Date getPosteddate() {
        return posteddate;
    }

    public void setPosteddate(Date posteddate) {
        this.posteddate = posteddate;
    }

    public String getAmazonorderid() {
        return amazonorderid;
    }

    public void setAmazonorderid(String amazonorderid) {
        this.amazonorderid = amazonorderid == null ? null : amazonorderid.trim();
    }

    public String getTotalexpenseCurrencycode() {
        return totalexpenseCurrencycode;
    }

    public void setTotalexpenseCurrencycode(String totalexpenseCurrencycode) {
        this.totalexpenseCurrencycode = totalexpenseCurrencycode == null ? null : totalexpenseCurrencycode.trim();
    }

    public String getTotalexpenseCurrencyamount() {
        return totalexpenseCurrencyamount;
    }

    public void setTotalexpenseCurrencyamount(String totalexpenseCurrencyamount) {
        this.totalexpenseCurrencyamount = totalexpenseCurrencyamount == null ? null : totalexpenseCurrencyamount.trim();
    }

    public String getTaxtypeigstCurrencycode() {
        return taxtypeigstCurrencycode;
    }

    public void setTaxtypeigstCurrencycode(String taxtypeigstCurrencycode) {
        this.taxtypeigstCurrencycode = taxtypeigstCurrencycode == null ? null : taxtypeigstCurrencycode.trim();
    }

    public String getTaxtypeigstCurrencyamount() {
        return taxtypeigstCurrencyamount;
    }

    public void setTaxtypeigstCurrencyamount(String taxtypeigstCurrencyamount) {
        this.taxtypeigstCurrencyamount = taxtypeigstCurrencyamount == null ? null : taxtypeigstCurrencyamount.trim();
    }

    public String getTaxtypecgstCurrencycode() {
        return taxtypecgstCurrencycode;
    }

    public void setTaxtypecgstCurrencycode(String taxtypecgstCurrencycode) {
        this.taxtypecgstCurrencycode = taxtypecgstCurrencycode == null ? null : taxtypecgstCurrencycode.trim();
    }

    public String getTaxtypecgstCurrencyamount() {
        return taxtypecgstCurrencyamount;
    }

    public void setTaxtypecgstCurrencyamount(String taxtypecgstCurrencyamount) {
        this.taxtypecgstCurrencyamount = taxtypecgstCurrencyamount == null ? null : taxtypecgstCurrencyamount.trim();
    }

    public String getTaxtypesgstCurrencycode() {
        return taxtypesgstCurrencycode;
    }

    public void setTaxtypesgstCurrencycode(String taxtypesgstCurrencycode) {
        this.taxtypesgstCurrencycode = taxtypesgstCurrencycode == null ? null : taxtypesgstCurrencycode.trim();
    }

    public String getTaxtypesgstCurrencyamount() {
        return taxtypesgstCurrencyamount;
    }

    public void setTaxtypesgstCurrencyamount(String taxtypesgstCurrencyamount) {
        this.taxtypesgstCurrencyamount = taxtypesgstCurrencyamount == null ? null : taxtypesgstCurrencyamount.trim();
    }

    public String getMarketplaceid() {
        return marketplaceid;
    }

    public void setMarketplaceid(String marketplaceid) {
        this.marketplaceid = marketplaceid == null ? null : marketplaceid.trim();
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