package com.weiziplus.springboot.mwshandle;

import com.alibaba.fastjson.JSONObject;
import com.weiziplus.springboot.mwsclient.MwsClientAction;
import com.weiziplus.springboot.mwsclient.MwsClientConfig;

import java.util.HashMap;

public interface MwsOrdersApi {
    JSONObject listOrders(HashMap<String, String> params, MwsClientConfig clientConfig, MwsClientAction clientAction)throws Exception;
}
