package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("pay_promotion")
public class PayPromotion implements Serializable {

    @Id("id")
    private String id;
    @Column("ObjectId")
    private String objectid;
    @Column("DataType")
    private String datatype;
    @Column("Promotion_PromotionType")
    private String promotionPromotiontype;
    @Column("Promotion_PromotionId")
    private String promotionPromotionid;
    @Column("Promotion_CurrencyCode")
    private String promotionCurrencycode;
    @Column("Promotion_CurrencyAmount")
    private String promotionCurrencyamount;
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

    public String getPromotionPromotiontype() {
        return promotionPromotiontype;
    }

    public void setPromotionPromotiontype(String promotionPromotiontype) {
        this.promotionPromotiontype = promotionPromotiontype == null ? null : promotionPromotiontype.trim();
    }

    public String getPromotionPromotionid() {
        return promotionPromotionid;
    }

    public void setPromotionPromotionid(String promotionPromotionid) {
        this.promotionPromotionid = promotionPromotionid == null ? null : promotionPromotionid.trim();
    }

    public String getPromotionCurrencycode() {
        return promotionCurrencycode;
    }

    public void setPromotionCurrencycode(String promotionCurrencycode) {
        this.promotionCurrencycode = promotionCurrencycode == null ? null : promotionCurrencycode.trim();
    }

    public String getPromotionCurrencyamount() {
        return promotionCurrencyamount;
    }

    public void setPromotionCurrencyamount(String promotionCurrencyamount) {
        this.promotionCurrencyamount = promotionCurrencyamount == null ? null : promotionCurrencyamount.trim();
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