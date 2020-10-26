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
@Table("pay_retrochargeevent")
public class PayRetrochargeevent implements Serializable {
    @Id("id")
    private String id;
    @Column("ObjectId")
    private String objectid;
    @Column("DataType")
    private String datatype;
    @Column("RetrochargeEventType")
    private String retrochargeeventtype;
    @Column("AmazonOrderId")
    private String amazonorderid;
    @Column("PostedDate")
    private Date posteddate;
    @Column("BaseTax_CurrencyCode")
    private String basetaxCurrencycode;
    @Column("BaseTax_CurrencyAmount")
    private String basetaxCurrencyamount;
    @Column("ShippingTax_CurrencyCode")
    private String shippingtaxCurrencycode;
    @Column("ShippingTax_CurrencyAmount")
    private String shippingtaxCurrencyamount;
    @Column("MarketplaceName")
    private String marketplacename;
    @Column("RetrochargeTaxWithheldComponentList_TaxCollectionModel")
    private String retrochargetaxwithheldcomponentlistTaxcollectionmodel;
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

    public String getRetrochargeeventtype() {
        return retrochargeeventtype;
    }

    public void setRetrochargeeventtype(String retrochargeeventtype) {
        this.retrochargeeventtype = retrochargeeventtype == null ? null : retrochargeeventtype.trim();
    }

    public String getAmazonorderid() {
        return amazonorderid;
    }

    public void setAmazonorderid(String amazonorderid) {
        this.amazonorderid = amazonorderid == null ? null : amazonorderid.trim();
    }

    public Date getPosteddate() {
        return posteddate;
    }

    public void setPosteddate(Date posteddate) {
        this.posteddate = posteddate;
    }

    public String getBasetaxCurrencycode() {
        return basetaxCurrencycode;
    }

    public void setBasetaxCurrencycode(String basetaxCurrencycode) {
        this.basetaxCurrencycode = basetaxCurrencycode == null ? null : basetaxCurrencycode.trim();
    }

    public String getBasetaxCurrencyamount() {
        return basetaxCurrencyamount;
    }

    public void setBasetaxCurrencyamount(String basetaxCurrencyamount) {
        this.basetaxCurrencyamount = basetaxCurrencyamount == null ? null : basetaxCurrencyamount.trim();
    }

    public String getShippingtaxCurrencycode() {
        return shippingtaxCurrencycode;
    }

    public void setShippingtaxCurrencycode(String shippingtaxCurrencycode) {
        this.shippingtaxCurrencycode = shippingtaxCurrencycode == null ? null : shippingtaxCurrencycode.trim();
    }

    public String getShippingtaxCurrencyamount() {
        return shippingtaxCurrencyamount;
    }

    public void setShippingtaxCurrencyamount(String shippingtaxCurrencyamount) {
        this.shippingtaxCurrencyamount = shippingtaxCurrencyamount == null ? null : shippingtaxCurrencyamount.trim();
    }

    public String getMarketplacename() {
        return marketplacename;
    }

    public void setMarketplacename(String marketplacename) {
        this.marketplacename = marketplacename == null ? null : marketplacename.trim();
    }

    public String getRetrochargetaxwithheldcomponentlistTaxcollectionmodel() {
        return retrochargetaxwithheldcomponentlistTaxcollectionmodel;
    }

    public void setRetrochargetaxwithheldcomponentlistTaxcollectionmodel(String retrochargetaxwithheldcomponentlistTaxcollectionmodel) {
        this.retrochargetaxwithheldcomponentlistTaxcollectionmodel = retrochargetaxwithheldcomponentlistTaxcollectionmodel == null ? null : retrochargetaxwithheldcomponentlistTaxcollectionmodel.trim();
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