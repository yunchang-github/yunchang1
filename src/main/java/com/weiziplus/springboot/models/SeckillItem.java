package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
<<<<<<< HEAD
 * 秒杀列表商品数据(后台爬取)
 * seckill_item
 * @author WeiziPlus
 * @date 2019-08-30 09:23:15
=======
 * seckill_item
 * @author Administrator
 * @date 2019-11-18 16:46:25
>>>>>>> 33270b9bf283b1d27e28731af3ec74afa9daf8d9
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("seckill_item")
public class SeckillItem implements Serializable {
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
     * 秒杀列表主键，id
     */
    @Column("seckill_id")
    private String seckillId;

    /**
     */
    @Column("sku")
    private String sku;

    /**
     */
    @Column("asin")
    private String asin;

    /**
     * 促销价格
     */
    @Column("promotion_price")
    private Double promotionPrice;

    /**
<<<<<<< HEAD
     * 已售出数量
=======
     * 商品已售出数量
>>>>>>> 33270b9bf283b1d27e28731af3ec74afa9daf8d9
     */
    @Column("quantity_num")
    private Integer quantityNum;

    /**
     * 已确定参与数量
     */
    @Column("partake_num")
    private Integer partakeNum;

    /**
     * 秒杀备注
     */
    @Column("spike_note")
    private String spikeNote;

    /**
     * 促销数量
     */
    @Column("promotion_quantity")
    private Integer promotionQuantity;

    /**
     * 商品价格
     */
    @Column("goods_price")
    private Double goodsPrice;

    /**
     * 商品被加入等待列表数量
     */
    @Column("number_added_waiting_list")
    private Integer numberAddedWaitingList;

    /**
     * 最高秒杀价格
     */
    @Column("highest_spike_price")
    private Double highestSpikePrice;

    /**
     * 最低促销数量
     */
    @Column("minimum_promotion_quantity")
    private Double minimumPromotionQuantity;

    /**
     * 费用
     */
    @Column("fee")
    private Double fee;

    /**
     * 状态
     */
    @Column("status")
    private String status;

    /**
     * 爬取类型  0.已结束  1.即将推出
     */
    @Column("type")
    private String type;

    /**
     * 开始时间
     */
    @Column("start_time")
    private String startTime;

    /**
     * 结束时间
     */
    @Column("end_time")
    private String endTime;

    /**
     * 入库时间
     */
    @Column("create_time")
    private String createTime;

    private static final long serialVersionUID = 1L;
}