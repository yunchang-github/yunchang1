package com.weiziplus.springboot.mwsclient;

public class MwsClientConfig {

    // 商家授权ID
    private String sellerId;
    // 商家授权Token
    private String mWSAuthToken;
    // 亚马逊秘钥ID
    private String aWSAccessKeyId;
    // 亚马逊秘钥
    private String secretKey;

    public MwsClientConfig(){}
    public MwsClientConfig(String sellerId, String mWSAuthToken, String aWSAccessKeyId, String secretKey) {
        this.sellerId = sellerId;
        this.mWSAuthToken = mWSAuthToken;
        this.aWSAccessKeyId = aWSAccessKeyId;
        this.secretKey = secretKey;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getmWSAuthToken() {
        return mWSAuthToken;
    }

    public void setmWSAuthToken(String mWSAuthToken) {
        this.mWSAuthToken = mWSAuthToken;
    }

    public String getaWSAccessKeyId() {
        return aWSAccessKeyId;
    }

    public void setaWSAccessKeyId(String aWSAccessKeyId) {
        this.aWSAccessKeyId = aWSAccessKeyId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

}
