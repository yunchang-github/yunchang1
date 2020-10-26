package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("pay_shipmentitem")
public class PayShipmentitem implements Serializable {
    @Id("id")
    private String id;
    @Column("ObjectId")
    private String objectid;
    @Column("DataType")
    private String datatype;
    @Column("SellerSKU")
    private String sellersku;
    @Column("OrderItemId")
    private String orderitemid;
    @Column("OrderAdjustmentItemId")
    private String orderadjustmentitemid;
    @Column("QuantityShipped")
    private String quantityshipped;
    @Column("ItemChargeList")
    private String itemchargelist;
    @Column("CostOfPointsGranted_CurrencyCode")
    private String costofpointsgrantedCurrencycode;
    @Column("CostOfPointsGranted_CurrencyAmount")
    private String costofpointsgrantedCurrencyamount;
    @Column("CostOfPointsReturned_CurrencyCode")
    private String costofpointsreturnedCurrencycode;
    @Column("CostOfPointsReturned_CurrencyAmount")
    private String costofpointsreturnedCurrencyamount;
    @Column("TaxWithheldComponent_TaxCollectionModel")
    private String taxwithheldcomponentTaxcollectionmodel;
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

    public String getSellersku() {
        return sellersku;
    }

    public void setSellersku(String sellersku) {
        this.sellersku = sellersku == null ? null : sellersku.trim();
    }

    public String getOrderitemid() {
        return orderitemid;
    }

    public void setOrderitemid(String orderitemid) {
        this.orderitemid = orderitemid == null ? null : orderitemid.trim();
    }

    public String getOrderadjustmentitemid() {
        return orderadjustmentitemid;
    }

    public void setOrderadjustmentitemid(String orderadjustmentitemid) {
        this.orderadjustmentitemid = orderadjustmentitemid == null ? null : orderadjustmentitemid.trim();
    }

    public String getQuantityshipped() {
        return quantityshipped;
    }

    public void setQuantityshipped(String quantityshipped) {
        this.quantityshipped = quantityshipped == null ? null : quantityshipped.trim();
    }

    public String getItemchargelist() {
        return itemchargelist;
    }

    public void setItemchargelist(String itemchargelist) {
        this.itemchargelist = itemchargelist == null ? null : itemchargelist.trim();
    }

    public String getCostofpointsgrantedCurrencycode() {
        return costofpointsgrantedCurrencycode;
    }

    public void setCostofpointsgrantedCurrencycode(String costofpointsgrantedCurrencycode) {
        this.costofpointsgrantedCurrencycode = costofpointsgrantedCurrencycode == null ? null : costofpointsgrantedCurrencycode.trim();
    }

    public String getCostofpointsgrantedCurrencyamount() {
        return costofpointsgrantedCurrencyamount;
    }

    public void setCostofpointsgrantedCurrencyamount(String costofpointsgrantedCurrencyamount) {
        this.costofpointsgrantedCurrencyamount = costofpointsgrantedCurrencyamount == null ? null : costofpointsgrantedCurrencyamount.trim();
    }

    public String getCostofpointsreturnedCurrencycode() {
        return costofpointsreturnedCurrencycode;
    }

    public void setCostofpointsreturnedCurrencycode(String costofpointsreturnedCurrencycode) {
        this.costofpointsreturnedCurrencycode = costofpointsreturnedCurrencycode == null ? null : costofpointsreturnedCurrencycode.trim();
    }

    public String getCostofpointsreturnedCurrencyamount() {
        return costofpointsreturnedCurrencyamount;
    }

    public void setCostofpointsreturnedCurrencyamount(String costofpointsreturnedCurrencyamount) {
        this.costofpointsreturnedCurrencyamount = costofpointsreturnedCurrencyamount == null ? null : costofpointsreturnedCurrencyamount.trim();
    }

    public String getTaxwithheldcomponentTaxcollectionmodel() {
        return taxwithheldcomponentTaxcollectionmodel;
    }

    public void setTaxwithheldcomponentTaxcollectionmodel(String taxwithheldcomponentTaxcollectionmodel) {
        this.taxwithheldcomponentTaxcollectionmodel = taxwithheldcomponentTaxcollectionmodel == null ? null : taxwithheldcomponentTaxcollectionmodel.trim();
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