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
@Table("pay_fbaliquidationevent")
public class PayFbaliquidationevent implements Serializable {
    @Id("id")
    private String id;
    @Column("ObjectId")
    private String objectid;
    @Column("DataType")
    private String datatype;
    @Column("LiquidationProceedsAmount_CurrencyCode")
    private String liquidationproceedsamountCurrencycode;
    @Column("LiquidationProceedsAmount_CurrencyAmount")
    private String liquidationproceedsamountCurrencyamount;
    @Column("PostedDate")
    private Date posteddate;
    @Column("OriginalRemovalOrderId")
    private String originalremovalorderid;
    @Column("LiquidationFeeAmount_CurrencyCode")
    private String liquidationfeeamountCurrencycode;
    @Column("LiquidationFeeAmount_CurrencyAmount")
    private String liquidationfeeamountCurrencyamount;
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

    public String getLiquidationproceedsamountCurrencycode() {
        return liquidationproceedsamountCurrencycode;
    }

    public void setLiquidationproceedsamountCurrencycode(String liquidationproceedsamountCurrencycode) {
        this.liquidationproceedsamountCurrencycode = liquidationproceedsamountCurrencycode == null ? null : liquidationproceedsamountCurrencycode.trim();
    }

    public String getLiquidationproceedsamountCurrencyamount() {
        return liquidationproceedsamountCurrencyamount;
    }

    public void setLiquidationproceedsamountCurrencyamount(String liquidationproceedsamountCurrencyamount) {
        this.liquidationproceedsamountCurrencyamount = liquidationproceedsamountCurrencyamount == null ? null : liquidationproceedsamountCurrencyamount.trim();
    }

    public Date getPosteddate() {
        return posteddate;
    }

    public void setPosteddate(Date posteddate) {
        this.posteddate = posteddate;
    }

    public String getOriginalremovalorderid() {
        return originalremovalorderid;
    }

    public void setOriginalremovalorderid(String originalremovalorderid) {
        this.originalremovalorderid = originalremovalorderid == null ? null : originalremovalorderid.trim();
    }

    public String getLiquidationfeeamountCurrencycode() {
        return liquidationfeeamountCurrencycode;
    }

    public void setLiquidationfeeamountCurrencycode(String liquidationfeeamountCurrencycode) {
        this.liquidationfeeamountCurrencycode = liquidationfeeamountCurrencycode == null ? null : liquidationfeeamountCurrencycode.trim();
    }

    public String getLiquidationfeeamountCurrencyamount() {
        return liquidationfeeamountCurrencyamount;
    }

    public void setLiquidationfeeamountCurrencyamount(String liquidationfeeamountCurrencyamount) {
        this.liquidationfeeamountCurrencyamount = liquidationfeeamountCurrencyamount == null ? null : liquidationfeeamountCurrencyamount.trim();
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