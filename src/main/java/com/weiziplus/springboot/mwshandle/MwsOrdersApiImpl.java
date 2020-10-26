package com.weiziplus.springboot.mwshandle;

import com.alibaba.fastjson.JSONObject;
import com.weiziplus.springboot.mwsclient.MwsClient;
import com.weiziplus.springboot.mwsclient.MwsClientAction;
import com.weiziplus.springboot.mwsclient.MwsClientConfig;

import java.util.HashMap;

public class MwsOrdersApiImpl extends MwsClient implements MwsOrdersApi {


    @Override
    public JSONObject listOrders(HashMap<String, String> params, MwsClientConfig clientConfig, MwsClientAction clientAction) throws Exception {
        this.builderConfig(clientConfig);
        this.builderAction(clientAction);
        return JSONObject.parseObject(this.doPost(params).toString());
    }
}
