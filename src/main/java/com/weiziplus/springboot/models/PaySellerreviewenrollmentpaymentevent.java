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
@Table("pay_sellerreviewenrollmentpaymentevent")
public class PaySellerreviewenrollmentpaymentevent implements Serializable {
    @Id("id")
    private String id;
    @Column("ObjectId")
    private String objectid;
    @Column("DataType")
    private String datatype;
    @Column("EnrollmentId")
    private String enrollmentid;
    @Column("TotalAmount_CurrencyCode")
    private String totalamountCurrencycode;
    @Column("TotalAmount_CurrencyAmount")
    private String totalamountCurrencyamount;
    @Column("PostedDate")
    private Date posteddate;
    @Column("ParentASIN")
    private String parentasin;
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

    public String getEnrollmentid() {
        return enrollmentid;
    }

    public void setEnrollmentid(String enrollmentid) {
        this.enrollmentid = enrollmentid == null ? null : enrollmentid.trim();
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

    public String getParentasin() {
        return parentasin;
    }

    public void setParentasin(String parentasin) {
        this.parentasin = parentasin == null ? null : parentasin.trim();
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