package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * original_data_financial_fee_component
 * @author Administrator
 * @date 2019-11-26 17:09:15
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("original_data_financial_fee_component")
public class OriginalDataFinancialFeeComponent implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     */
    @Column("pid")
    private Long pid;

    /**
     * 获取数据来源类型  1.CouponPaymentEvent 2.ShipmentItemList 下的ShipmentItem 3.RefundEventList 下 ShipmentItem
     */
    @Column("type")
    private String type;

    /**
     */
    @Column("fee_type")
    private String feeType;

    /**
     */
    @Column("fee_amount_currency_code")
    private String feeAmountCurrencyCode;

    /**
     */
    @Column("fee_amount_currency_amount")
    private Double feeAmountCurrencyAmount;

    private static final long serialVersionUID = 1L;
}