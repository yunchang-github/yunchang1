package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * 要爬取商品评价的店铺、区域、asin信息(手动导入)
 * crawl_shop
 * @author WeiziPlus
 * @date 2019-08-27 16:52:03
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("crawl_shop")
public class CrawlShop implements Serializable {
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
     * asin
     */
    @Column("asin")
    private String asin;

    private static final long serialVersionUID = 1L;
}