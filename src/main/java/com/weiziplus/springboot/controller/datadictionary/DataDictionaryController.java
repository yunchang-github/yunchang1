package com.weiziplus.springboot.controller.datadictionary;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.service.datadictionary.DataDictionaryService;
import com.weiziplus.springboot.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author wanglongwei
 * @data 2019/7/2 15:12
 */
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/dataDictionary")
public class DataDictionaryController {

    @Autowired
    DataDictionaryService service;

    /**
     * 获取指标参数列表
     *
     * @return
     */
    @GetMapping("/getIndexParameterList")
    public ResultUtil getIndexParameterList() {
        return ResultUtil.success(service.getIndexParameterList());
    }
}
