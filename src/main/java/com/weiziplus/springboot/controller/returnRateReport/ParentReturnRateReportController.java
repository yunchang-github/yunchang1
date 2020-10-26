package com.weiziplus.springboot.controller.returnRateReport;

import com.weiziplus.springboot.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.service.returnRateReport.ChildReturnRateReportService;
import com.weiziplus.springboot.utils.ResultUtil;

import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.Map;

@Api(value="ChildReturnRateReportController",tags={"退货率报告父体数据"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/returnRateReport/parentReturnRateReport")
public class ParentReturnRateReportController {
	@Autowired
	ChildReturnRateReportService service;

	@ApiOperation(value = "分页列表", notes = "分页列表")
	@GetMapping(value="/getPageParentList")
	public ResultUtil getPageParentList(
	@ApiParam(value = "当前页", required = true)@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
			@ApiParam(value = "每页数", required = true) @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@ApiParam(value = "日期", required = false)@RequestParam(value = "date", required = false) String date,
			@ApiParam(value = "店铺", required = false)@RequestParam(value = "shop", required = false) String shop,
			@ApiParam(value = "区域", required = false)@RequestParam(value = "area", required = false) String area,
			@ApiParam(value = "sku", required = false)@RequestParam(value = "sku",required = false) String sku,
			@ApiParam(value = "asin", required = false)@RequestParam(value = "asin",required = false) String asin,
			@ApiParam(value = "父sku", required = false)@RequestParam(value = "parentSku",required = false) String parentSku
			)
	{
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("pageNum",pageNum);
		map.put("pageSize",pageSize);
		map.put("date",date);
		map.put("shop",shop);
		map.put("area",area);
		map.put("parentSku",parentSku);
		map.put("shopArray", StringUtil.parseReqParamToArrayUseTrim(shop));
		map.put("areaArray",StringUtil.parseReqParamToArrayUseTrim(area));
		return service.getPageParentList(map);
    }

}







