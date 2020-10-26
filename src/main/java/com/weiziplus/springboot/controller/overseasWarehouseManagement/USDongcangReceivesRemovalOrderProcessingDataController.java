package com.weiziplus.springboot.controller.overseasWarehouseManagement;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.models.UsDongcangReceivesRemovalOrderProcessingData;
import com.weiziplus.springboot.service.overseasWarehouseManagement.USDongcangReceivesRemovalOrderProcessingDataService;
import com.weiziplus.springboot.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


@Api(value="USDongcangReceivesRemovalOrderProcessingDataController",tags={"海外仓管理美东仓接收移除订单处理数据"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/overseasWarehouseManagement/USDongcangReceivesRemovalOrderProcessingData")
public class USDongcangReceivesRemovalOrderProcessingDataController {
	@Autowired
	USDongcangReceivesRemovalOrderProcessingDataService service;

	@ApiOperation(value = "分页列表", notes = "分页列表")
	@GetMapping(value="/getPageList")
	public ResultUtil getPageChildList(
			@ApiParam(value = "当前页", required = true)@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
			@ApiParam(value = "每页数", required = true)@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@ApiParam(value = "日期", required = false)@RequestParam(value = "date", required = false) String date,
			@ApiParam(value = "店铺", required = false)@RequestParam(value = "shop", required = false) String shop,
			@ApiParam(value = "区域", required = false)@RequestParam(value = "area", required = false) String area) {
        return service.getPageList(pageNum, pageSize,shop,area,date);
    }

	//============有运单号================

	//缺少运单号导入把查询的内容保存到数据库

	//扫描运单号把查询的内容保存到数据库
	@ApiOperation(value = "扫描运单号把查询的内容保存到数据库", notes = "扫描运单号把查询的内容保存到数据库")
	@PostMapping(value="/saveSingleTrackingNumberData")
	public ResultUtil saveSingleTrackingNumberData(
			@ApiParam(value = "运单号", required = true)@RequestParam(value = "trackingNumber", required = true) String trackingNumber,
			@ApiParam(value = "店铺", required = false) @RequestParam(value = "shop", required = false) String shop,
			@ApiParam(value = "区域", required = false)@RequestParam(value = "area", required = false) String area) {

		 return service.addByTrackingNumber(shop,area,trackingNumber);
    }

	//============无运单号================

	//缺少导入多个fnsku号


	//输入单个FNsku
	@ApiOperation(value = "无运单号根据FNsku添加", notes = "无运单号根据FNsku添加")
	@PostMapping(value="/saveNoTrackingNumberDataByScan")
	public ResultUtil saveNoTrackingNumberDataByScan(
			@ApiParam(value = "fnsku", required = true)@RequestParam(value = "fnsku", required = true) String fnsku,
			@ApiParam(value = "店铺", required = false)@RequestParam(value = "shop", required = false) String shop,
			@ApiParam(value = "区域", required = false)@RequestParam(value = "area", required = false) String area) {

		 return service.addByFnsku(shop,area,fnsku);
    }


	//表格中输入修改内容
	@ApiOperation(value = "表格中输入修改内容", notes = "表格中输入修改内容")
	@PostMapping(value="/updateTableContent")
	public ResultUtil saveTableContent(
			@RequestParam(value = "usDongcangReceivesRemovalOrderProcessingData",required = true) UsDongcangReceivesRemovalOrderProcessingData usDongcangReceivesRemovalOrderProcessingData
           ) {

		 return service.updateByTableContent(usDongcangReceivesRemovalOrderProcessingData);
    }

	//缺少标签打印功能


	/**
	 * 更新实际签收数量
	 *
	 * @param id
	 * @param value
	 * @return
	 */
	@ApiOperation(value = "更新实际签收数量", notes = "更新实际签收数量")
	@PostMapping("/updateActualNumberReceipts")
	public ResultUtil updateActualNumberReceipts(Long id, Integer value) {
		return service.updateActualNumberReceipts(id, value);
	}

	/**
	 * 更新放置箱号
	 *
	 * @param id
	 * @param value
	 * @return
	 */
	@ApiOperation(value = "更新放置箱号", notes = "更新放置箱号")
	@PostMapping("/updatePlaceBoxNumber")
	public ResultUtil updatePlaceBoxNumber(Long id, String value) {
		return service.updatePlaceBoxNumber(id, value);
	}

}
