package com.weiziplus.springboot.controller.sspa;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.service.sspa.AsinDiagnosisService;
import com.weiziplus.springboot.utils.ResultUtil;

import java.util.HashMap;
import java.util.Map;

import com.weiziplus.springboot.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author wanglongwei
 * @data 2019/7/3 11:55
 */

@Api(value="AsinDiagnosisController",tags={"SSPA ASIN诊断"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/sspa/asinDiagnosis")
public class AsinDiagnosisController {

    @Autowired
    AsinDiagnosisService service;

    /**
     * 获取分页数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
	@ApiOperation(value = "获取分页数据", notes = "获取分页数据")
    @GetMapping("/getPageList")
    public ResultUtil getPageList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "asin",required = false) String asin,
            @RequestParam(value = "shop",required = false) String shop,
            @RequestParam(value = "area",required = false) String area
    		) {
    	Map<String,Object> map =  new HashMap<String,Object>();

    	map.put("pageNum", pageNum);
    	map.put("pageSize", pageSize);
    	map.put("asin", asin);
    	map.put("shop", shop);
    	map.put("shopArray", StringUtil.parseReqParamToArrayUseTrim(shop));
    	map.put("area", area);
    	map.put("areaArray",StringUtil.parseReqParamToArrayUseTrim(area));
        return service.getPageList(map);
    }
}
