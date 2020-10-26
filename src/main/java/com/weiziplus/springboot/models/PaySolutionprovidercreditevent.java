package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("pay_solutionprovidercreditevent")
public class PaySolutionprovidercreditevent implements Serializable {
    @Id("id")
    private String id;
    @Column("ObjectId")
    private String objectid;
    @Column("DataType")
    private String datatype;
    @Column("ProviderTransactionType")
    private String providertransactiontype;
    @Column("SellerOrderId")
    private String sellerorderid;
    @Column("MarketplaceId")
    private String marketplaceid;
    @Column("MarketplaceCountryCode")
    private String marketplacecountrycode;
    @Column("SellerId")
    private String sellerid;
    @Column("SellerStoreName")
    private String sellerstorename;
    @Column("ProviderId")
    private String providerid;
    @Column("ProviderStoreName")
    private String providerstorename;
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

    public String getProvidertransactiontype() {
        return providertransactiontype;
    }

    public void setProvidertransactiontype(String providertransactiontype) {
        this.providertransactiontype = providertransactiontype == null ? null : providertransactiontype.trim();
    }

    public String getSellerorderid() {
        return sellerorderid;
    }

    public void setSellerorderid(String sellerorderid) {
        this.sellerorderid = sellerorderid == null ? null : sellerorderid.trim();
    }

    public String getMarketplaceid() {
        return marketplaceid;
    }

    public void setMarketplaceid(String marketplaceid) {
        this.marketplaceid = marketplaceid == null ? null : marketplaceid.trim();
    }

    public String getMarketplacecountrycode() {
        return marketplacecountrycode;
    }

    public void setMarketplacecountrycode(String marketplacecountrycode) {
        this.marketplacecountrycode = marketplacecountrycode == null ? null : marketplacecountrycode.trim();
    }

    public String getSellerid() {
        return sellerid;
    }

    public void setSellerid(String sellerid) {
        this.sellerid = sellerid == null ? null : sellerid.trim();
    }

    public String getSellerstorename() {
        return sellerstorename;
    }

    public void setSellerstorename(String sellerstorename) {
        this.sellerstorename = sellerstorename == null ? null : sellerstorename.trim();
    }

    public String getProviderid() {
        return providerid;
    }

    public void setProviderid(String providerid) {
        this.providerid = providerid == null ? null : providerid.trim();
    }

    public String getProviderstorename() {
        return providerstorename;
    }

    public void setProviderstorename(String providerstorename) {
        this.providerstorename = providerstorename == null ? null : providerstorename.trim();
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