package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wanglongwei
 * @data 2019/7/17 17:07
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("logistics_bill")
public class LogisticsBill implements Serializable {
    @Id("id")
    private Long id;

    @Column("billing_logistics_number")
    private String billingLogisticsNumber;

    @Column("freight")
    private String freight;

    @Column("carrier")
    private String carrier;
}
