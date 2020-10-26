package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("pay_servicefeeevent")
public class PayServicefeeevent implements Serializable {

    @Id("id")
    private String id;
    @Column("ObjectId")
    private String objectid;
    @Column("DataType")
    private String datatype;
    @Column("AmazonOrderId")
    private String amazonorderid;
    @Column("FeeReason")
    private String feereason;
    @Column("SellerSKU")
    private String sellersku;
    @Column("FnSKU")
    private String fnsku;
    @Column("FeeDescription")
    private String feedescription;
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

    public String getAmazonorderid() {
        return amazonorderid;
    }

    public void setAmazonorderid(String amazonorderid) {
        this.amazonorderid = amazonorderid == null ? null : amazonorderid.trim();
    }

    public String getFeereason() {
        return feereason;
    }

    public void setFeereason(String feereason) {
        this.feereason = feereason == null ? null : feereason.trim();
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

    public String getFeedescription() {
        return feedescription;
    }

    public void setFeedescription(String feedescription) {
        this.feedescription = feedescription == null ? null : feedescription.trim();
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