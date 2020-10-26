package com.weiziplus.springboot.models.DO;

import com.weiziplus.springboot.base.Table;
import com.weiziplus.springboot.models.Shop;
import lombok.Data;

import java.io.Serializable;

/**
 * 店铺授权实体类---sjd
 * */
@Data
public class AuthorizationDO implements Serializable {
    private Long id;

    /**
     * 网店名称
     */
    private String shopName;

    /**
     * 网店账号
     */
    private String shopAccount;

    /**
     * 网店密码
     */
    private String shopPassword;

    /**
     * 卖家ID(SellerId)
     */
    private String sellerId;

    /**
     * MWSAuthToken
     */
    private String mwsAuthToken;

    /**
     * AWS 访问密钥 ID(AWSAccessKeyId)
     */
    private String awsAccessKeyId;

    /**
     * 密钥(Secret Key)
     */
    private String secretKey;

    /**
     * 创建时间
     */
    private String createTime;

    private String code;

    private String regionCode;
}
