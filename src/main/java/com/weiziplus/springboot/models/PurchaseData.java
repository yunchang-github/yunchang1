package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 采购（马帮api）
 * purchase_data
 * @author WeiziPlus
 * @date 2019-09-03 09:03:02
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("purchase_data")
public class PurchaseData implements Serializable {
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
     * 采购订单号
     */
    @Column("purchaseId")
    private String purchaseid;

    /**
     */
    @Column("companyId")
    private String companyid;

    /**
     */
    @Column("groupId")
    private String groupid;

    /**
     * 总运费
     */
    @Column("freightSum")
    private String freightsum;

    /**
     * 折扣
     */
    @Column("discountAmount")
    private String discountamount;

    /**
     * 税金
     */
    @Column("taxAmount")
    private String taxamount;

    /**
     * 总金额
     */
    @Column("totalAmount")
    private String totalamount;

    /**
     * 总金额(税前)
     */
    @Column("pretaxTotalAmount")
    private String pretaxtotalamount;

    /**
     * 商品总数量
     */
    @Column("quantitySum")
    private String quantitysum;

    /**
     * 1、新采购 2、等待一审 3、等待二审 4、采购中 5、签收 6、验货 7、上架 8、部分到货 9、全部到货 10、已完成 11、异常 12、删除
     */
    @Column("flag")
    private String flag;

    /**
     */
    @Column("flagDesc")
    private String flagdesc;

    /**
     * 未入库数量
     */
    @Column("noWarehousing")
    private String nowarehousing;

    /**
     */
    @Column("providerId")
    private String providerid;

    /**
     * 供应商
     */
    @Column("providerName")
    private String providername;

    /**
     */
    @Column("targetWarehouseId")
    private String targetwarehouseid;

    /**
     * 采购仓库
     */
    @Column("targetWarehouseName")
    private String targetwarehousename;

    /**
     * 备注
     */
    @Column("content")
    private String content;

    /**
     */
    @Column("createOperId")
    private String createoperid;

    /**
     * 下单员
     */
    @Column("createOperName")
    private String createopername;

    /**
     * 下单时间
     */
    @Column("createTime")
    private String createtime;

    /**
     */
    @Column("updateTime")
    private String updatetime;

    /**
     * 1688实付金额
     */
    @Column("ali1688SumPayment")
    private String ali1688sumpayment;

    /**
     * 最后入库时间
     */
    @Column("lastStorageTime")
    private String laststoragetime;

    /**
     */
    @Column("buyerId")
    private String buyerid;

    /**
     * 采购员
     */
    @Column("buyerName")
    private String buyername;

    private static final long serialVersionUID = 1L;
}