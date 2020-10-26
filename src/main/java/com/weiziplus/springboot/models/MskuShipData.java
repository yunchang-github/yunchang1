package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 补货计划（马帮api）
 * msku_ship_data
 * @author WeiziPlus
 * @date 2019-09-03 11:40:52
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("msku_ship_data")
public class MskuShipData implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * 数据获取日期
     */
    @Column("date")
    private String date;

    /**
     */
    @Column("companyId")
    private String companyid;

    /**
     */
    @Column("fbaStockId")
    private String fbastockid;

    /**
     * 商品sku
     */
    @Column("fbaStockSku")
    private String fbastocksku;

    /**
     * 本地库存
     */
    @Column("applyQuantity")
    private String applyquantity;

    /**
     */
    @Column("msku")
    private String msku;

    /**
     */
    @Column("shopid")
    private String shopid;

    /**
     * 店铺
     */
    @Column("shopName")
    private String shopname;

    /**
     * 站点
     */
    @Column("amazonsite")
    private String amazonsite;

    /**
     * 申请补货量
     */
    @Column("replenishNum")
    private String replenishnum;

    /**
     */
    @Column("timeCreate")
    private String timecreate;

    /**
     */
    @Column("timeUpdate")
    private String timeupdate;

    /**
     * 状态：1:待审核;2:已审核;3:已作废
     */
    @Column("status")
    private String status;

    private static final long serialVersionUID = 1L;
}