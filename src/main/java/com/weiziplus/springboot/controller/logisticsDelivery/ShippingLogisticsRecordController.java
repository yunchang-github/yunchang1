package com.weiziplus.springboot.controller.logisticsDelivery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.service.logistics.LogisticsDeliveryService;
import com.weiziplus.springboot.utils.ResultUtil;

import springfox.documentation.annotations.ApiIgnore;

@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/logisticsDelivery/shippingLogisticsRecord")
public class ShippingLogisticsRecordController {

	@Autowired
	LogisticsDeliveryService service;
	@GetMapping("/getPageList")
	public ResultUtil getPageList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "shop", required = false) String shop,
            @RequestParam(value = "area", required = false) String area) {
        return service.getShippingLogisticsRecordPageList(pageNum, pageSize,shop,area);
    }
}
