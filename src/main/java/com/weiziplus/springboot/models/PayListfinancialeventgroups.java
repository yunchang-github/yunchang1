package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 亚马逊api获取all_order表
 * all_order
 * @author WeiziPlus
 * @date 2019-07-30 11:25:31
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("pay_listfinancialeventgroups")
public class PayListfinancialeventgroups implements Serializable {
    /**
     */
    @Id("id")
    private String id;

    /**
     * groupId
     */
    @Column("financialEventGroupId")
    private String FinancialEventGroupId;

    @Column("originalTotal_CurrencyAmount")
    private String OriginalTotalCurrencyAmount;

    @Column("originalTotal_CurrencyCode")
    private String OriginalTotalCurrencyCode;

    @Column("processingStatus")
    private String ProcessingStatus;

    @Column("beginningBalance_CurrencyAmount")
    private String BeginningBalanceCurrencyAmount;

    @Column("beginningBalance_CurrencyCode")
    private String BeginningBalanceCurrencyCode;

    @Column("financialEventGroupStart")
    private String FinancialEventGroupStart;

    @Column("requestId")
    private String RequestId;





    private static final long serialVersionUID = 1L;
}