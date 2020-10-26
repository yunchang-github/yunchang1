package com.weiziplus.springboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 设置全局静态常量
 *
 * @author wanglongwei
 * @data 2019/5/6 15:50
 */
@Component
public class GlobalConfig {
    /**
     * 身份验证，请求头，token
     */
    public final static String TOKEN = "token";

    /**
     * 空字符串
     */
    public static final String UNDEFINED = "undefined";

    /**
     * 空字符串
     */
    public static final String NULL = "null";

    /**
     * 用户允许登录为0，禁止为1
     */
    public static final Integer ALLOW_LOGIN = 0;

    /**
     * 允许使用为0，禁止为1
     */
    public static final Integer IS_STOP = 0;

    /**
     * 超级管理员id为1
     */
    public static final Long SUPER_ADMIN_ID = 1L;

    /**
     * 超级管理员角色id为1
     */
    public static final Long SUPER_ADMIN_ROLE_ID = 1L;

    /**
     * 系统功能表中角色管理id为3
     */
    public static final Long SYS_FUNCTION_ROLE_ID = 3L;

    /**
     * 系统默认初始密码
     */
    public static final String DETAIL_INIT_PASSWORD = "000000";


    /**
     * 设置可以跨域访问的地址
     */
    public static String CORS_FILTER_ORIGINS;

    @Value("${global.cors-filter-origins}")
    public void setCorsFilterOrigins(String corsFilterOrigins) {
        GlobalConfig.CORS_FILTER_ORIGINS = corsFilterOrigins;
    }

    /**
     * 设置图片上传基础路径
     */
    public static String BASE_FILE_PATH;

    @Value("${global.base-file-path}")
    public void setBaseFilePath(String baseFilePath) {
        GlobalConfig.BASE_FILE_PATH = baseFilePath;
    }

    /**
     * 设置十二月份英文简写
     */
    public static String[] TWELVE_MONTH_ENGLISH = new String[]{
            "Jan", "Feb", "March", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    /**
     * 运营角色id为
     */
    public static final Long OPERATE_ROLE_ID = 6666666L;

    /**
     * 当前运行的店铺id
     */
    public static Long SHOP_ID;

    @Value("${global.shop-id}")
    public void setShopId(String shopId) {
        GlobalConfig.SHOP_ID = Long.valueOf(shopId);
    }

    /**
     * 当前运行的店铺名称
     */
    public static String SHOP_NAME;

    @Value("${global.shop-name}")
    public void setShopName(String shopName) {
        GlobalConfig.SHOP_NAME = shopName;
    }

    ///////////////以下为亚马逊广告api
    /**
     * refresh-token
     */
   /* public static String REFRESH_TOKEN;

    @Value("${global.refresh-token}")
    public void setRefreshToken(String refreshToken) {
        GlobalConfig.REFRESH_TOKEN = refreshToken;
    }*/


    /**
     * 返回地址链接
     */
    public static  String REDIRECT_URI;

    @Value("${global.redirect_uri}")
    public void setRedirectUri(String redirectUri) {
        GlobalConfig.REDIRECT_URI = redirectUri;
    }



    /**
     * client_id
     */
    public static String CLIENT_ID;

    @Value("${global.client-id}")
    public void setClientId(String clientId) {
        GlobalConfig.CLIENT_ID = clientId;
    }

    /**
     * client_secret
     */
    public static String CLIENT_SECRET;

    @Value("${global.client-secret}")
    public void setClientSecret(String clientSecret) {
        GlobalConfig.CLIENT_SECRET = clientSecret;
    }

    /**
     * scope-profile-id-------Amazon-Advertising-API-Scope
     */
   /*  public static String SCOPE_PROFILE_ID;

   @Value("${global.scope-profile-id}")
    public void setScopeProfileId(String scopeProfileId) {
        GlobalConfig.SCOPE_PROFILE_ID = scopeProfileId;
    }*/

    ////////////////////////以下为马帮api
    /**
     * 开发者账号
     */
    public static String DEVELOPER_ID;

    @Value("${global.developer-id}")
    public void setDeveloperId(String developerId) {
        GlobalConfig.DEVELOPER_ID = developerId;
    }

    /**
     * 开发者秘钥
     */
    public static String AUTH_TOKEN;

    @Value("${global.auth-token}")
    public void setAuthToken(String authToken) {
        GlobalConfig.AUTH_TOKEN = authToken;
    }

    /**
     * 企业编号
     */
    public static String COMPANY_ID;

    @Value("${global.company-id}")
    public void setCompanyId(String companyId) {
        GlobalConfig.COMPANY_ID = companyId;
    }

}
