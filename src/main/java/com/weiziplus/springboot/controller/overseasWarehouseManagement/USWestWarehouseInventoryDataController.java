package com.weiziplus.springboot.controller.overseasWarehouseManagement;

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
import com.weiziplus.springboot.service.overseasWarehouseManagement.USWestWarehouseInventoryDataService;
import com.weiziplus.springboot.utils.ResultUtil;

import springfox.documentation.annotations.ApiIgnore;

@Api(value="USWestWarehouseInventoryDataController",tags={"海外仓管理美西仓库存数据"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/overseasWarehouseManagement/USWestWarehouseInventoryData")
public class USWestWarehouseInventoryDataController {
	@Autowired
	USWestWarehouseInventoryDataService service;


	@ApiOperation(value = "分页列表", notes = "分页列表")
	@GetMapping(value="/getPageList")
	public ResultUtil getPageList(
			@ApiParam(value = "当前页", required = true) @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
			@ApiParam(value = "每页数", required = true)@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@ApiParam(value = "日期", required = false)@RequestParam(value = "date", required = false) String date,
			@ApiParam(value = "店铺", required = false)@RequestParam(value = "shop", required = false) String shop,
			@ApiParam(value = "区域", required = false)@RequestParam(value = "area", required = false) String area) {
        return service.getPageList(pageNum, pageSize,shop,area);
    }

}
