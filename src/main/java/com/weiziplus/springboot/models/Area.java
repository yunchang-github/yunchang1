package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 区域表
 * area
 *
 * @author WeiziPlus
 * @date 2019-07-25 10:32:10
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("area")
public class Area implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * 区域名称(亚马逊商城)
     */
    @Column("area_name")
    private String areaName;

    /**
     * 广告接口的国家代码
     */
    @Column("advert_country_code")
    private String advertCountryCode;

    /**
     * mws的国家代码
     */
    @Column("mws_country_code")
    private String mwsCountryCode;

    /**
     * 亚马逊MWS 端点
     */
    @Column("mws_end_point")
    private String mwsEndPoint;

    /**
     * MarketplaceId
     */
    @Column("marketplace_id")
    private String marketplaceId;

    /**
     * areaUrl
     */
    @Column("area_url")
    private String areaUrl;

    /**
     * regionCode
     */
    @Column("region_code")
    private String regionCode;

    private static final long serialVersionUID = 1L;
}