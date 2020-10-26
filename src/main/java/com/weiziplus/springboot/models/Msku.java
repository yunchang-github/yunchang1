package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * msku列表（马帮api）
 * msku
 * @author WeiziPlus
 * @date 2019-09-03 11:19:30
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("msku")
public class Msku implements Serializable {
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
    @Column("shops")
    private String shops;

    /**
     * 店铺
     */
    @Column("shopsName")
    private String shopsname;

    /**
     */
    @Column("msku")
    private String msku;

    /**
     * 商品链接
     */
    @Column("commodityLinks")
    private String commoditylinks;

    /**
     */
    @Column("stockId")
    private String stockid;

    /**
     * 本地sku（商品sku）
     */
    @Column("stockSku")
    private String stocksku;

    /**
     * 站点
     */
    @Column("amazonsite")
    private String amazonsite;

    /**
     * 上架时间
     */
    @Column("groundingTime")
    private String groundingtime;

    /**
     */
    @Column("developerId")
    private String developerid;

    /**
     * 开发员名称
     */
    @Column("developerName")
    private String developername;

    /**
     * 创建时间
     */
    @Column("createTime")
    private String createtime;

    /**
     * 更新时间
     */
    @Column("updateTime")
    private String updatetime;

    /**
     * 状态:1:正常;2:删除
     */
    @Column("status")
    private String status;

    /**
     */
    @Column("statusdesc")
    private String statusdesc;

    private static final long serialVersionUID = 1L;
}