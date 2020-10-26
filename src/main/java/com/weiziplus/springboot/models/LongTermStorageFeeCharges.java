package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 长期仓储费
 * long_term_storage_fee_charges
 * @author WeiziPlus
 * @date 2019-07-31 14:27:10
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("long_term_storage_fee_charges")
public class LongTermStorageFeeCharges implements Serializable {
    /**
     */
    @Id("id")
    private Integer id;

    /**
     */
    @Column("shop")
    private String shop;

    /**
     */
    @Column("area")
    private String area;

    /**
     */
    @Column("snapshot_date")
    private String snapshotDate;

    /**
     */
    @Column("sku")
    private String sku;

    /**
     */
    @Column("fnsku")
    private String fnsku;

    /**
     */
    @Column("asin")
    private String asin;

    /**
     */
    @Column("product_name")
    private String productName;

    /**
     */
    @Column("condition")
    private String condition;

    /**
     */
    @Column("qty_charged_12_mo_long_term_storage_fee")
    private Integer qtyCharged12MoLongTermStorageFee;

    /**
     */
    @Column("per_unit_volume")
    private Double perUnitVolume;

    /**
     */
    @Column("currency")
    private String currency;

    /**
     */
    @Column("12_mo_long_terms_storage_fee")
    private Double column12MoLongTermsStorageFee;

    /**
     */
    @Column("qty_charged_6_mo_long_term_storage_fee")
    private Integer qtyCharged6MoLongTermStorageFee;

    /**
     */
    @Column("6_mo_long_terms_storage_fee")
    private Double column6MoLongTermsStorageFee;

    /**
     */
    @Column("volume_unit")
    private String volumeUnit;

    /**
     */
    @Column("country")
    private String country;

    /**
     */
    @Column("enrolled_in_small_and_light")
    private String enrolledInSmallAndLight;

    private static final long serialVersionUID = 1L;
}