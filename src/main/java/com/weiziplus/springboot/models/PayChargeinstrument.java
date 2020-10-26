package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("pay_chargeinstrument")
public class PayChargeinstrument implements Serializable {
    @Id("id")
    private String id;
    @Column("ObjectId")
    private String objectid;
    @Column("DataType")
    private String datatype;
    @Column("Description")

    private String description;
    @Column("Tail")

    private String tail;
    @Column("Amount_CurrencyCode")
    private String amountCurrencycode;
    @Column("Amount_CurrencyAmount")
    private String amountCurrencyamount;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getTail() {
        return tail;
    }

    public void setTail(String tail) {
        this.tail = tail == null ? null : tail.trim();
    }

    public String getAmountCurrencycode() {
        return amountCurrencycode;
    }

    public void setAmountCurrencycode(String amountCurrencycode) {
        this.amountCurrencycode = amountCurrencycode == null ? null : amountCurrencycode.trim();
    }

    public String getAmountCurrencyamount() {
        return amountCurrencyamount;
    }

    public void setAmountCurrencyamount(String amountCurrencyamount) {
        this.amountCurrencyamount = amountCurrencyamount == null ? null : amountCurrencyamount.trim();
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