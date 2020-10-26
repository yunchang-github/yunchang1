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
@Table("pay_rentaltransactionevent")
public class PayRentaltransactionevent implements Serializable {
    @Id("id")
    private String id;
    @Column("ObjectId")
    private String objectid;
    @Column("DataType")
    private String datatype;
    @Column("AmazonOrderId")
    private String amazonorderid;
    @Column("RentalEventType")
    private String rentaleventtype;
    @Column("ExtensionLength")
    private String extensionlength;
    @Column("PostedDate")
    private Date posteddate;
    @Column("MarketplaceName")
    private String marketplacename;
    @Column("RentalInitialValue_CurrencyCode")
    private String rentalinitialvalueCurrencycode;
    @Column("RentalInitialValue_CurrencyAmount")
    private String rentalinitialvalueCurrencyamount;
    @Column("RentalReimbursement_CurrencyCode")
    private String rentalreimbursementCurrencycode;
    @Column("RentalReimbursement_CurrencyAmount")
    private String rentalreimbursementCurrencyamount;
    @Column("RentalTaxWithheldList_TaxCollectionModel")
    private String rentaltaxwithheldlistTaxcollectionmodel;
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

    public String getAmazonorderid() {
        return amazonorderid;
    }

    public void setAmazonorderid(String amazonorderid) {
        this.amazonorderid = amazonorderid == null ? null : amazonorderid.trim();
    }

    public String getRentaleventtype() {
        return rentaleventtype;
    }

    public void setRentaleventtype(String rentaleventtype) {
        this.rentaleventtype = rentaleventtype == null ? null : rentaleventtype.trim();
    }

    public String getExtensionlength() {
        return extensionlength;
    }

    public void setExtensionlength(String extensionlength) {
        this.extensionlength = extensionlength == null ? null : extensionlength.trim();
    }

    public Date getPosteddate() {
        return posteddate;
    }

    public void setPosteddate(Date posteddate) {
        this.posteddate = posteddate;
    }

    public String getMarketplacename() {
        return marketplacename;
    }

    public void setMarketplacename(String marketplacename) {
        this.marketplacename = marketplacename == null ? null : marketplacename.trim();
    }

    public String getRentalinitialvalueCurrencycode() {
        return rentalinitialvalueCurrencycode;
    }

    public void setRentalinitialvalueCurrencycode(String rentalinitialvalueCurrencycode) {
        this.rentalinitialvalueCurrencycode = rentalinitialvalueCurrencycode == null ? null : rentalinitialvalueCurrencycode.trim();
    }

    public String getRentalinitialvalueCurrencyamount() {
        return rentalinitialvalueCurrencyamount;
    }

    public void setRentalinitialvalueCurrencyamount(String rentalinitialvalueCurrencyamount) {
        this.rentalinitialvalueCurrencyamount = rentalinitialvalueCurrencyamount == null ? null : rentalinitialvalueCurrencyamount.trim();
    }

    public String getRentalreimbursementCurrencycode() {
        return rentalreimbursementCurrencycode;
    }

    public void setRentalreimbursementCurrencycode(String rentalreimbursementCurrencycode) {
        this.rentalreimbursementCurrencycode = rentalreimbursementCurrencycode == null ? null : rentalreimbursementCurrencycode.trim();
    }

    public String getRentalreimbursementCurrencyamount() {
        return rentalreimbursementCurrencyamount;
    }

    public void setRentalreimbursementCurrencyamount(String rentalreimbursementCurrencyamount) {
        this.rentalreimbursementCurrencyamount = rentalreimbursementCurrencyamount == null ? null : rentalreimbursementCurrencyamount.trim();
    }

    public String getRentaltaxwithheldlistTaxcollectionmodel() {
        return rentaltaxwithheldlistTaxcollectionmodel;
    }

    public void setRentaltaxwithheldlistTaxcollectionmodel(String rentaltaxwithheldlistTaxcollectionmodel) {
        this.rentaltaxwithheldlistTaxcollectionmodel = rentaltaxwithheldlistTaxcollectionmodel == null ? null : rentaltaxwithheldlistTaxcollectionmodel.trim();
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