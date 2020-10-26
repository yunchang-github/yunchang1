package com.weiziplus.springboot.models;

import java.io.Serializable;

public class Payment implements Serializable {
    private Integer id;

    private String dateTime;

    private String settlementId;

    private String type;

    private String orderId;

    private String sku;

    private String description;

    private String quantity;

    private String marketplace;

    private String fulfillment;

    private String orderCity;

    private String orderState;

    private String orderPostal;

    private String productSales;

    private String shippingCredits;

    private String giftWrapCredits;

    private String promotionalRebates;

    private String salesTaxCollected;

    private String marketplaceFacilitatorTax;

    private String sellingFees;

    private String fbaFees;

    private String otherTransactionFees;

    private String other;

    private String total;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime == null ? null : dateTime.trim();
    }

    public String getSettlementId() {
        return settlementId;
    }

    public void setSettlementId(String settlementId) {
        this.settlementId = settlementId == null ? null : settlementId.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku == null ? null : sku.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity == null ? null : quantity.trim();
    }

    public String getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(String marketplace) {
        this.marketplace = marketplace == null ? null : marketplace.trim();
    }

    public String getFulfillment() {
        return fulfillment;
    }

    public void setFulfillment(String fulfillment) {
        this.fulfillment = fulfillment == null ? null : fulfillment.trim();
    }

    public String getOrderCity() {
        return orderCity;
    }

    public void setOrderCity(String orderCity) {
        this.orderCity = orderCity == null ? null : orderCity.trim();
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState == null ? null : orderState.trim();
    }

    public String getOrderPostal() {
        return orderPostal;
    }

    public void setOrderPostal(String orderPostal) {
        this.orderPostal = orderPostal == null ? null : orderPostal.trim();
    }

    public String getProductSales() {
        return productSales;
    }

    public void setProductSales(String productSales) {
        this.productSales = productSales == null ? null : productSales.trim();
    }

    public String getShippingCredits() {
        return shippingCredits;
    }

    public void setShippingCredits(String shippingCredits) {
        this.shippingCredits = shippingCredits == null ? null : shippingCredits.trim();
    }

    public String getGiftWrapCredits() {
        return giftWrapCredits;
    }

    public void setGiftWrapCredits(String giftWrapCredits) {
        this.giftWrapCredits = giftWrapCredits == null ? null : giftWrapCredits.trim();
    }

    public String getPromotionalRebates() {
        return promotionalRebates;
    }

    public void setPromotionalRebates(String promotionalRebates) {
        this.promotionalRebates = promotionalRebates == null ? null : promotionalRebates.trim();
    }

    public String getSalesTaxCollected() {
        return salesTaxCollected;
    }

    public void setSalesTaxCollected(String salesTaxCollected) {
        this.salesTaxCollected = salesTaxCollected == null ? null : salesTaxCollected.trim();
    }

    public String getMarketplaceFacilitatorTax() {
        return marketplaceFacilitatorTax;
    }

    public void setMarketplaceFacilitatorTax(String marketplaceFacilitatorTax) {
        this.marketplaceFacilitatorTax = marketplaceFacilitatorTax == null ? null : marketplaceFacilitatorTax.trim();
    }

    public String getSellingFees() {
        return sellingFees;
    }

    public void setSellingFees(String sellingFees) {
        this.sellingFees = sellingFees == null ? null : sellingFees.trim();
    }

    public String getFbaFees() {
        return fbaFees;
    }

    public void setFbaFees(String fbaFees) {
        this.fbaFees = fbaFees == null ? null : fbaFees.trim();
    }

    public String getOtherTransactionFees() {
        return otherTransactionFees;
    }

    public void setOtherTransactionFees(String otherTransactionFees) {
        this.otherTransactionFees = otherTransactionFees == null ? null : otherTransactionFees.trim();
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other == null ? null : other.trim();
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total == null ? null : total.trim();
    }
}