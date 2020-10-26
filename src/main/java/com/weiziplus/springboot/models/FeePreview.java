package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * 费用预览
 * fee_preview
 * @author WeiziPlus
 * @date 2019-07-31 17:06:54
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("fee_preview")
public class FeePreview implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

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
    @Column("sku")
    private String sku;

    /**
     * 在亚马逊仓库中起作用----亚马逊库存的产品编码
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
    @Column("product_group")
    private String productGroup;

    /**
     */
    @Column("brand")
    private String brand;

    /**
     */
    @Column("fulfilled_by")
    private String fulfilledBy;

    /**
     */
    @Column("your_price")
    private Double yourPrice;

    /**
     */
    @Column("sales_price")
    private Double salesPrice;

    /**
     */
    @Column("longest_side")
    private Double longestSide;

    /**
     */
    @Column("median_side")
    private Double medianSide;

    /**
     */
    @Column("shortest_side")
    private Double shortestSide;

    /**
     */
    @Column("length_and_girth")
    private Double lengthAndGirth;

    /**
     */
    @Column("unit_of_dimension")
    private String unitOfDimension;

    /**
     */
    @Column("item_package_weight")
    private Double itemPackageWeight;

    /**
     */
    @Column("unit_of_weight")
    private String unitOfWeight;

    /**
     */
    @Column("currency")
    private String currency;

    /**
     */
    @Column("estimated_fee_total")
    private String estimatedFeeTotal;

    /**
     */
    @Column("estimated_referral_fee_per_unit")
    private String estimatedReferralFeePerUnit;

    /**
     */
    @Column("estimated_variable_closing_fee")
    private String estimatedVariableClosingFee;

    /**
     */
    @Column("estimated_order_handling_fee_per_order")
    private String estimatedOrderHandlingFeePerOrder;

    /**
     */
    @Column("estimated_pick_pack_fee_per_unit")
    private String estimatedPickPackFeePerUnit;

    /**
     */
    @Column("estimated_weight_handling_fee_per_unit")
    private String estimatedWeightHandlingFeePerUnit;

    /**
     */
    @Column("expected_fulfillment_fee_per_unit")
    private String expectedFulfillmentFeePerUnit;

    /**
     * 英国
     */
    @Column("expected-efn-fulfilment-fee-per-unit-uk")
    private String expectedEfnFulfilmentFeePerUnitUk;

    /**
     * 德国
     */
    @Column("expected-efn-fulfilment-fee-per-unit-de")
    private String expectedEfnFulfilmentFeePerUnitDe;

    /**
     * 法国
     */
    @Column("expected-efn-fulfilment-fee-per-unit-fr")
    private String expectedEfnFulfilmentFeePerUnitFr;

    /**
     * 意大利
     */
    @Column("expected-efn-fulfilment-fee-per-unit-it")
    private String expectedEfnFulfilmentFeePerUnitIt;

    /**
     * 西班牙
     */
    @Column("expected-efn-fulfilment-fee-per-unit-es")
    private String expectedEfnFulfilmentFeePerUnitEs;

    /**
     * 数据获取时间
     */
    @Column("create_time")
    private String createTime;

    private static final long serialVersionUID = 1L;
}