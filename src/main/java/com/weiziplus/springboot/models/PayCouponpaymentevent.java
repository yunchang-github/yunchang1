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
@Table("pay_couponpaymentevent")
public class PayCouponpaymentevent implements Serializable {
    @Id("id")
    private String id;
    @Column("ObjectId")
    private String objectid;
    @Column("DataType")
    private String datatype;
    @Column("TotalAmount_CurrencyCode")
    private String totalamountCurrencycode;
    @Column("TotalAmount_CurrencyAmount")
    private String totalamountCurrencyamount;
    @Column("PostedDate")
    private Date posteddate;
    @Column("CouponId")
    private String couponid;
    @Column("SellerCouponDescription")
    private String sellercoupondescription;
    @Column("ClipOrRedemptionCount")
    private String cliporredemptioncount;
    @Column("PaymentEventId")
    private String paymenteventid;
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

    public Date getPosteddate() {
        return posteddate;
    }

    public void setPosteddate(Date posteddate) {
        this.posteddate = posteddate;
    }

    public String getCouponid() {
        return couponid;
    }

    public void setCouponid(String couponid) {
        this.couponid = couponid == null ? null : couponid.trim();
    }

    public String getSellercoupondescription() {
        return sellercoupondescription;
    }

    public void setSellercoupondescription(String sellercoupondescription) {
        this.sellercoupondescription = sellercoupondescription == null ? null : sellercoupondescription.trim();
    }

    public String getCliporredemptioncount() {
        return cliporredemptioncount;
    }

    public void setCliporredemptioncount(String cliporredemptioncount) {
        this.cliporredemptioncount = cliporredemptioncount == null ? null : cliporredemptioncount.trim();
    }

    public String getPaymenteventid() {
        return paymenteventid;
    }

    public void setPaymenteventid(String paymenteventid) {
        this.paymenteventid = paymenteventid == null ? null : paymenteventid.trim();
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