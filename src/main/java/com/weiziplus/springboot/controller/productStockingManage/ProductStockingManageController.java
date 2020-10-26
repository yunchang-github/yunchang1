package com.weiziplus.springboot.controller.productStockingManage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.service.logistics.LogisticsDeliveryService;
import com.weiziplus.springboot.service.productStockingManage.ProductStockingManageService;
import com.weiziplus.springboot.utils.ResultUtil;

import springfox.documentation.annotations.ApiIgnore;

@Api(value="ProductStockingManageController",tags={"产品备货"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/productStockingManage/productStockingList")
public class ProductStockingManageController {

	@Autowired
	ProductStockingManageService service;

	@ApiOperation(value = "分页列表", notes = "分页列表")
	@GetMapping("/getPageList")
	public ResultUtil getPageList(
			@ApiParam(value = "当前页", required = true)@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
			@ApiParam(value = "每页数", required = true)@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@ApiParam(value = "店铺", required = false)@RequestParam(value = "shop", required = false) String shop,
			@ApiParam(value = "区域", required = false) @RequestParam(value = "area", required = false) String area) {
        return service.getProductStockingPageList(pageNum, pageSize,shop,area);
    }
}
