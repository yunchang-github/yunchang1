package com.weiziplus.springboot.mwsclient;

public class MwsClientAction {
    // POST请求地址
    private String postUrI;
    // POST请求模块
    private String module;
    // 请求API版本
    private String version;
    // 请求方法
    private String method;
    // 亚马逊节点ID
    private String marketplaceId;
    public MwsClientAction(){}
    public MwsClientAction( String marketplaceId,String postUrI, String module, String version, String method) {
        this.postUrI = postUrI;
        this.module = module;
        this.version = version;
        this.method = method;
        this.marketplaceId = marketplaceId;
    }

    public String getPostUrI() {
        return postUrI;
    }

    public void setPostUrI(String postUrI) {
        this.postUrI = postUrI;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMarketplaceId() {
        return marketplaceId;
    }

    public void setMarketplaceId(String marketplaceId) {
        this.marketplaceId = marketplaceId;
    }
}
