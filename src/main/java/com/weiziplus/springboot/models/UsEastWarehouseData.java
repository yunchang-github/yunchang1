package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * us_east_warehouse_data
 * @author Administrator
 * @date 2019-08-16 17:35:58
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("us_east_warehouse_data")
public class UsEastWarehouseData implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     */
    @Column("msku")
    private String msku;

    /**
     * 库存sku
     */
    @Column("inventory_sku")
    private String inventorySku;

    /**
     * 实际签收数量
     */
    @Column("actual_number_of_receipts")
    private Integer actualNumberOfReceipts;

    /**
     * 放置箱号
     */
    @Column("place_box_number")
    private String placeBoxNumber;

    /**
     * 0,是  1 否
     */
    @Column("is_shipped_separately")
    private String isShippedSeparately;

    /**
     * 签收时间
     */
    @Column("receive_time")
    private String receiveTime;

    private static final long serialVersionUID = 1L;
}