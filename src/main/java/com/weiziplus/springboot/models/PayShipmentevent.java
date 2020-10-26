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
@Table("pay_shipmentevent")
public class PayShipmentevent implements Serializable {
    @Id("id")
    private String id;
    @Column("AmazonOrderId")
    private String amazonorderid;
    @Column("SellerOrderId")
    private String sellerorderid;
    @Column("MarketplaceName")
    private String marketplacename;
    @Column("PostedDate")
    private String posteddate;
    @Column("DataType")
    private String datatype;
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

    public String getAmazonorderid() {
        return amazonorderid;
    }

    public void setAmazonorderid(String amazonorderid) {
        this.amazonorderid = amazonorderid == null ? null : amazonorderid.trim();
    }

    public String getSellerorderid() {
        return sellerorderid;
    }

    public void setSellerorderid(String sellerorderid) {
        this.sellerorderid = sellerorderid == null ? null : sellerorderid.trim();
    }

    public String getMarketplacename() {
        return marketplacename;
    }

    public void setMarketplacename(String marketplacename) {
        this.marketplacename = marketplacename == null ? null : marketplacename.trim();
    }

    public String getPosteddate() {
        return posteddate;
    }

    public void setPosteddate(String posteddate) {
        this.posteddate = posteddate;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype == null ? null : datatype.trim();
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