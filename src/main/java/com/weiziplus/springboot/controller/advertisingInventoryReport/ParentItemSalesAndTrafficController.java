package com.weiziplus.springboot.controller.advertisingInventoryReport;

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
import com.weiziplus.springboot.service.advertisingInventoryReport.ChildItemSalesAndTrafficService;
import com.weiziplus.springboot.utils.ResultUtil;

import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.Map;

@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/advertisingInventoryReport/parentItemSalesAndTraffic")
@Api(value="ParentItemSalesAndTrafficController",tags={"广告库存父体接口"})
public class ParentItemSalesAndTrafficController {
	@Autowired
	ChildItemSalesAndTrafficService service;

	@ApiOperation(value = "广告库存父体", notes = "广告库存父体")
	@GetMapping(value="/getPageParentList")
	public ResultUtil getPageParentList(
			@ApiParam(value = "当前页", required = true) @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
			@ApiParam(value = "每页数", required = true)@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "type", required = false) String type,
			@ApiParam(value = "日期", required = false)@RequestParam(value = "date", required = false) String date,
			@ApiParam(value = "店铺", required = false)@RequestParam(value = "shop", required = false) String shop,
			@ApiParam(value = "区域", required = false)@RequestParam(value = "area", required = false) String area,
            //@RequestParam(value = "searchType", required = false) String searchType,
			@ApiParam(value = "父sku", required = false)@RequestParam(value = "parentSku",defaultValue = "") String parentSku,
			@RequestParam(value = "field", defaultValue = "firstSumQuantity") String field,
			@RequestParam(value = "sort", defaultValue = "desc") String sort)
	{
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("pageNum",pageNum);
		map.put("pageSize",pageSize);
		map.put("type",type);
		map.put("date",date);
		map.put("shop",shop);
		map.put("area",area);
        //map.put("searchType",searchType);
		map.put("parentSku",parentSku);
		map.put("field",field);
		map.put("sort",sort);
        return service.getPageParentList(map);
    }



}







