package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 网店
 * shop
 *
 * @author WeiziPlus
 * @date 2019-07-25 09:38:18
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("shop")
public class Shop implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * 网店名称
     */
    @Column("shop_name")
    private String shopName;

    /**
     * 网店账号
     */
    @Column("shop_account")
    private String shopAccount;

    /**
     * 网店密码
     */
    @Column("shop_password")
    private String shopPassword;

    /**
     * 卖家ID(SellerId)
     */
    @Column("seller_id")
    private String sellerId;

    /**
     * MWSAuthToken
     */
    @Column("mws_auth_token")
    private String mwsAuthToken;

    /**
     * AWS 访问密钥 ID(AWSAccessKeyId)
     */
    @Column("aws_access_key_id")
    private String awsAccessKeyId;

    /**
     * 密钥(Secret Key)
     */
    @Column("secret_key")
    private String secretKey;

    /**
     * 创建时间
     */
    @Column("create_time")
    private String createTime;

    /**
     * 广告是否授权
     */
    @Column("is_adv_authorization")
    private String isAdvAuthorization;

    /**
     * 刷新令牌
     */
    @Column("refresh_token")
    private String refreshToken;

    private List<Area> areas;

    private String code;

    private static final long serialVersionUID = 1L;
}
