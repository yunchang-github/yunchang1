package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 货币汇率(手动导入)
 * currency_exchange_rate
 * @author WeiziPlus
 * @date 2019-07-26 16:06:32
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("currency_exchange_rate")
public class CurrencyExchangeRate implements Serializable {
    /**
     * 自增
     */
    @Id("id")
    private Long id;

    /**
     * 币种
     */
    @Column("currency")
    private String currency;

    /**
     * 汇率
     */
    @Column("exchange_rate")
    private String exchangeRate;

    /**
     * 汇率启用时间
     */
    @Column("exchange_rate_start_time")
    private String exchangeRateStartTime;

    private static final long serialVersionUID = 1L;
}