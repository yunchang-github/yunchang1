package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;
@JsonInclude(JsonInclude.Include.NON_NULL)

@Table("pay_adjustmentevent")

public class PayAdjustmentevent implements Serializable {
    @Id("id")
    private String id;
    @Column("ObjectId")
    private String objectid;
    @Column("DataType")
    private String datatype;
    @Column("AdjustmentType")
    private String adjustmenttype;
    @Column("AdjustmentAmount_CurrencyCode")
    private String adjustmentamountCurrencycode;
    @Column("AdjustmentAmount_CurrencyAmount")
    private String adjustmentamountCurrencyamount;
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

    public String getAdjustmenttype() {
        return adjustmenttype;
    }

    public void setAdjustmenttype(String adjustmenttype) {
        this.adjustmenttype = adjustmenttype == null ? null : adjustmenttype.trim();
    }

    public String getAdjustmentamountCurrencycode() {
        return adjustmentamountCurrencycode;
    }

    public void setAdjustmentamountCurrencycode(String adjustmentamountCurrencycode) {
        this.adjustmentamountCurrencycode = adjustmentamountCurrencycode == null ? null : adjustmentamountCurrencycode.trim();
    }

    public String getAdjustmentamountCurrencyamount() {
        return adjustmentamountCurrencyamount;
    }

    public void setAdjustmentamountCurrencyamount(String adjustmentamountCurrencyamount) {
        this.adjustmentamountCurrencyamount = adjustmentamountCurrencyamount == null ? null : adjustmentamountCurrencyamount.trim();
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