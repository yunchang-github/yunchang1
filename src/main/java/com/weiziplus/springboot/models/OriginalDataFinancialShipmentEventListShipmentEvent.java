package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * original_data_financial_shipment_event_list_shipment_event
 * @author Administrator
 * @date 2019-11-26 17:00:51
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("original_data_financial_shipment_event_list_shipment_event")
public class OriginalDataFinancialShipmentEventListShipmentEvent implements Serializable {
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
    @Column("marketplace_name")
    private String marketplaceName;

    /**
     */
    @Column("amazon_order_id")
    private String amazonOrderId;

    /**
     */
    @Column("seller_order_id")
    private String sellerOrderId;

    /**
     */
    @Column("posted_date")
    private String postedDate;

    /**
     * 请求api时所选时间参数 请求时必填
     */
    @Column("posted_after")
    private String postedAfter;

    /**
     * 请求api时所选时间参数  请求时非必填 默认大于after2分钟
     */
    @Column("posted_before")
    private String postedBefore;

    /**
     * 数据入库时间
     */
    @Column("create_time")
    private String createTime;

    private static final long serialVersionUID = 1L;
}