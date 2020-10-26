package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * 出入库流水关联数据（马帮api）
 * storage_log_data
 * @author WeiziPlus
 * @date 2019-09-06 10:52:21
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("storage_log_data")
public class StorageLogData implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * 出入库流水id ，获取的数据中的id
     */
    @Column("log_id")
    private Long logId;

    /**
     * 数据获取时间
     */
    @Column("date")
    private String date;

    /**
     */
    @Column("companyId")
    private String companyid;

    /**
     * 店铺
     */
    @Column("shopName")
    private String shopname;

    /**
     * 商品编号（商品ID）
     */
    @Column("stockId")
    private String stockid;

    /**
     */
    @Column("stockSKu")
    private String stocksku;

    /**
     * 商品名称
     */
    @Column("stockSkuName")
    private String stockskuname;

    /**
     */
    @Column("wearhouseId")
    private String wearhouseid;

    /**
     * 仓库
     */
    @Column("wearhouseName")
    private String wearhousename;

    /**
     * 类型 1:入库 2：出库
     */
    @Column("ctype")
    private String ctype;

    /**
     * 库存（操作后库存）
     */
    @Column("quantityAfter")
    private String quantityafter;

    /**
     * 数量
     */
    @Column("quantity")
    private String quantity;

    /**
     * 单价
     */
    @Column("price")
    private String price;

    /**
     * 总价
     */
    @Column("pricesum")
    private String pricesum;

    /**
     */
    @Column("operId")
    private String operid;

    /**
     * 操作人
     */
    @Column("operName")
    private String opername;

    /**
     * 单据类型
     */
    @Column("documentNum")
    private String documentnum;

    /**
     * 单据号
     */
    @Column("documentType")
    private String documenttype;

    /**
     * 备注
     */
    @Column("remark")
    private String remark;

    /**
     * 日期
     */
    @Column("timecreate")
    private String timecreate;

    /**
     */
    @Column("createDateTime")
    private String createdatetime;

    private static final long serialVersionUID = 1L;
}