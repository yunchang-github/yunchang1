package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * original_data_financial_event_group
 * @author Administrator
 * @date 2019-11-26 16:59:27
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("original_data_financial_event_group")
public class OriginalDataFinancialEventGroup implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * 店铺
     */
    @Column("shop")
    private String shop;

    /**
     * 区域
     */
    @Column("area")
    private String area;

    /**
     */
    @Column("financial_event_group_id")
    private String financialEventGroupId;

    /**
     */
    @Column("processing_status")
    private String processingStatus;

    /**
     */
    @Column("original_total_currency_code")
    private String originalTotalCurrencyCode;

    /**
     */
    @Column("original_total_currency_amount")
    private Double originalTotalCurrencyAmount;

    /**
     */
    @Column("financial_event_group_start")
    private String financialEventGroupStart;

    /**
     */
    @Column("financial_event_group_end")
    private String financialEventGroupEnd;

    /**
     */
    @Column("beginning_balance_currency_code")
    private String beginningBalanceCurrencyCode;

    /**
     */
    @Column("beginning_balance_currency_amount")
    private Double beginningBalanceCurrencyAmount;

    /**
     */
    @Column("trace_id")
    private String traceId;

    /**
     */
    @Column("fund_transfer_status")
    private String fundTransferStatus;

    /**
     */
    @Column("account_tail")
    private String accountTail;

    /**
     */
    @Column("fund_transfer_date")
    private String fundTransferDate;

    /**
     * 请求api时所选时间参数 请求时必填
     */
    @Column("financial_event_group_started_after")
    private String financialEventGroupStartedAfter;

    /**
     * 请求api时所选时间参数  请求时非必填 默认大于after2分钟
     */
    @Column("financial_event_group_started_before")
    private String financialEventGroupStartedBefore;

    /**
     * 数据入库时间
     */
    @Column("create_time")
    private String createTime;

    private static final long serialVersionUID = 1L;
}