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
@Table("pay_safetreimbursementevent")
public class PaySafetreimbursementevent implements Serializable {
    @Id("id")
    private String id;
    @Column("ObjectId")
    private String objectid;
    @Column("DataType")
    private String datatype;
    @Column("SAFETClaimId")
    private String safetclaimid;
    @Column("ReimbursedAmount_CurrencyCode")
    private String reimbursedamountCurrencycode;
    @Column("ReimbursedAmount_CurrencyAmount")
    private String reimbursedamountCurrencyamount;
    @Column("PostedDate")
    private Date posteddate;
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

    public String getSafetclaimid() {
        return safetclaimid;
    }

    public void setSafetclaimid(String safetclaimid) {
        this.safetclaimid = safetclaimid == null ? null : safetclaimid.trim();
    }

    public String getReimbursedamountCurrencycode() {
        return reimbursedamountCurrencycode;
    }

    public void setReimbursedamountCurrencycode(String reimbursedamountCurrencycode) {
        this.reimbursedamountCurrencycode = reimbursedamountCurrencycode == null ? null : reimbursedamountCurrencycode.trim();
    }

    public String getReimbursedamountCurrencyamount() {
        return reimbursedamountCurrencyamount;
    }

    public void setReimbursedamountCurrencyamount(String reimbursedamountCurrencyamount) {
        this.reimbursedamountCurrencyamount = reimbursedamountCurrencyamount == null ? null : reimbursedamountCurrencyamount.trim();
    }

    public Date getPosteddate() {
        return posteddate;
    }

    public void setPosteddate(Date posteddate) {
        this.posteddate = posteddate;
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