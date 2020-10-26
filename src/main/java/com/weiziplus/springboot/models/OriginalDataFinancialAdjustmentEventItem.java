package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * original_data_financial_adjustment_event_item
 * @author Administrator
 * @date 2019-11-26 16:55:34
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("original_data_financial_adjustment_event_item")
public class OriginalDataFinancialAdjustmentEventItem implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * original_data_financial_adjustment_event的id
     */
    @Column("p_id")
    private Long pId;

    /**
     */
    @Column("adjustment_item_per_unit_amount_currency_code")
    private String adjustmentItemPerUnitAmountCurrencyCode;

    /**
     */
    @Column("adjustment_item_per_unit_amount_currency_amount")
    private Double adjustmentItemPerUnitAmountCurrencyAmount;

    /**
     */
    @Column("adjustment_item_total_amount_currency_code")
    private String adjustmentItemTotalAmountCurrencyCode;

    /**
     */
    @Column("adjustment_item_total_amount_currency_amount")
    private Double adjustmentItemTotalAmountCurrencyAmount;

    /**
     */
    @Column("adjustment_item_seller_SKU")
    private String adjustmentItemSellerSku;

    /**
     */
    @Column("adjustment_item_quantity")
    private String adjustmentItemQuantity;

    /**
     */
    @Column("adjustment_item_product_description")
    private String adjustmentItemProductDescription;

    private static final long serialVersionUID = 1L;
}