package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("pay_adjustmentitem")

public class PayAdjustmentitem implements Serializable {
    @Id("id")
    private String id;
    @Column("ObjectId")
    private String objectid;
    @Column("DataType")
    private String datatype;
    @Column("Quantity")
    private String quantity;
    @Column("PerUnitAmount_CurrencyCode")
    private String perunitamountCurrencycode;
    @Column("PerUnitAmount_CurrencyAmount")
    private String perunitamountCurrencyamount;
    @Column("TotalAmount_CurrencyCode")
    private String totalamountCurrencycode;
    @Column("TotalAmount_CurrencyAmount")
    private String totalamountCurrencyamount;
    @Column("SellerSKU")
    private String sellersku;
    @Column("FnSKU")
    private String fnsku;
    @Column("ProductDescription")
    private String productdescription;
    @Column("ASIN")
    private String asin;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity == null ? null : quantity.trim();
    }

    public String getPerunitamountCurrencycode() {
        return perunitamountCurrencycode;
    }

    public void setPerunitamountCurrencycode(String perunitamountCurrencycode) {
        this.perunitamountCurrencycode = perunitamountCurrencycode == null ? null : perunitamountCurrencycode.trim();
    }

    public String getPerunitamountCurrencyamount() {
        return perunitamountCurrencyamount;
    }

    public void setPerunitamountCurrencyamount(String perunitamountCurrencyamount) {
        this.perunitamountCurrencyamount = perunitamountCurrencyamount == null ? null : perunitamountCurrencyamount.trim();
    }

    public String getTotalamountCurrencycode() {
        return totalamountCurrencycode;
    }

    public void setTotalamountCurrencycode(String totalamountCurrencycode) {
        this.totalamountCurrencycode = totalamountCurrencycode == null ? null : totalamountCurrencycode.trim();
    }

    public String getTotalamountCurrencyamount() {
        return totalamountCurrencyamount;
    }

    public void setTotalamountCurrencyamount(String totalamountCurrencyamount) {
        this.totalamountCurrencyamount = totalamountCurrencyamount == null ? null : totalamountCurrencyamount.trim();
    }

    public String getSellersku() {
        return sellersku;
    }

    public void setSellersku(String sellersku) {
        this.sellersku = sellersku == null ? null : sellersku.trim();
    }

    public String getFnsku() {
        return fnsku;
    }

    public void setFnsku(String fnsku) {
        this.fnsku = fnsku == null ? null : fnsku.trim();
    }

    public String getProductdescription() {
        return productdescription;
    }

    public void setProductdescription(String productdescription) {
        this.productdescription = productdescription == null ? null : productdescription.trim();
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin == null ? null : asin.trim();
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