package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * 商品评论（爬取）
 * crawl_goods_review
 * @author WeiziPlus
 * @date 2019-08-28 11:45:58
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("crawl_goods_review")
public class CrawlGoodsReview implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     */
    @Column("shop")
    private String shop;

    /**
     */
    @Column("area")
    private String area;

    /**
     */
    @Column("asin")
    private String asin;

    /**
     * 评论人
     */
    @Column("author")
    private String author;

    /**
     * 星级
     */
    @Column("star")
    private Integer star;

    /**
     * 评论日期
     */
    @Column("review_date")
    private String reviewDate;

    /**
     * 是否购买，0:未购买,1:已购买
     */
    @Column("is_buy")
    private Integer isBuy;

    /**
     * 评论内容
     */
    @Column("content")
    private String content;

    /**
     * 评论数
     */
    @Column("num")
    private Integer num;

    /**
     * 星级详情
     */
    @Column("star_detail")
    private String starDetail;

    private static final long serialVersionUID = 1L;
}