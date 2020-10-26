package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 调拨发货(马帮api)
 * msku_shippbatch_data
 * @author Administrator
 * @date 2019-08-26 11:35:16
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("msku_shippbatch_data")
public class MskuShippbatchData implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * 收货仓库
     */
    @Column("shop")
    private String shop;

    /**
     */
    @Column("companyId")
    private String companyid;

    /**
     * 发货批次-------调拨批次
     */
    @Column("shippNo")
    private String shippno;

    /**
     * 批次名称-------申报名称
     */
    @Column("shippName")
    private String shippname;

    /**
     * 批次状态1:等待发货2:已发货3:已签收4:伪删
     */
    @Column("status")
    private String status;

    /**
     * 发货仓库编号-----发货仓库编号
     */
    @Column("warehouseId")
    private String warehouseid;

    /**
     * 发货仓库名称-----发货仓库名称
     */
    @Column("warehouse")
    private String warehouse;

    /**
     * 目的国家
     */
    @Column("countryCode")
    private String countrycode;

    /**
     * 发货时间
     */
    @Column("expressTime")
    private String expresstime;

    /**
     */
    @Column("timeCreate")
    private String timecreate;

    /**
     */
    @Column("timeUpdate")
    private String timeupdate;

    private static final long serialVersionUID = 1L;
}