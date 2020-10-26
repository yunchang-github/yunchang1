package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wanglongwei
 * @data 2019/7/11 15:19
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("exchange_rate")
public class ExchangeRate implements Serializable {
    @Id("id")
    private Long id;

    @Column("area_id")
    private Long areaId;

    @Column("currency")
    private String currency;

    @Column("exchange_rate")
    private Double exchangeRate;

    @Column("create_time")
    private String createTime;
}
