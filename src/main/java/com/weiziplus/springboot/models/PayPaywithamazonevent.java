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
@Table("pay_paywithamazonevent")
public class PayPaywithamazonevent implements Serializable {
    @Id("id")
    private String id;
    @Column("DataType")
    private String datatype;
    @Column("TransactionPostedDate")
    private String transactionposteddate;
    @Column("SellerOrderId")
    private String sellerorderid;
    @Column("BusinessObjectType")
    private String businessobjecttype;
    @Column("SalesChannel")
    private String saleschannel;
    @Column("PaymentAmountType")
    private String paymentamounttype;
    @Column("AmountDescription")
    private String amountdescription;
    @Column("FulfillmentChannel")
    private String fulfillmentchannel;
    @Column("StoreName")
    private String storename;
    @Column("Charge_ChargeType")
    private String chargeChargetype;
    @Column("Charge_CurrencyCode")
    private String chargeCurrencycode;
    @Column("Charge_CurrencyAmount")
    private String chargeCurrencyamount;
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

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype == null ? null : datatype.trim();
    }

    public String getTransactionposteddate() {
        return transactionposteddate;
    }



    public String getBusinessobjecttype() {
        return businessobjecttype;
    }

    public void setBusinessobjecttype(String businessobjecttype) {
        this.businessobjecttype = businessobjecttype == null ? null : businessobjecttype.trim();
    }

    public String getSaleschannel() {
        return saleschannel;
    }

    public void setSaleschannel(String saleschannel) {
        this.saleschannel = saleschannel == null ? null : saleschannel.trim();
    }

    public String getPaymentamounttype() {
        return paymentamounttype;
    }

    public void setPaymentamounttype(String paymentamounttype) {
        this.paymentamounttype = paymentamounttype == null ? null : paymentamounttype.trim();
    }

    public String getAmountdescription() {
        return amountdescription;
    }

    public void setAmountdescription(String amountdescription) {
        this.amountdescription = amountdescription == null ? null : amountdescription.trim();
    }

    public String getFulfillmentchannel() {
        return fulfillmentchannel;
    }

    public void setFulfillmentchannel(String fulfillmentchannel) {
        this.fulfillmentchannel = fulfillmentchannel == null ? null : fulfillmentchannel.trim();
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename == null ? null : storename.trim();
    }

    public String getChargeChargetype() {
        return chargeChargetype;
    }

    public void setChargeChargetype(String chargeChargetype) {
        this.chargeChargetype = chargeChargetype == null ? null : chargeChargetype.trim();
    }

    public String getChargeCurrencycode() {
        return chargeCurrencycode;
    }

    public void setChargeCurrencycode(String chargeCurrencycode) {
        this.chargeCurrencycode = chargeCurrencycode == null ? null : chargeCurrencycode.trim();
    }

    public String getChargeCurrencyamount() {
        return chargeCurrencyamount;
    }

    public void setChargeCurrencyamount(String chargeCurrencyamount) {
        this.chargeCurrencyamount = chargeCurrencyamount == null ? null : chargeCurrencyamount.trim();
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