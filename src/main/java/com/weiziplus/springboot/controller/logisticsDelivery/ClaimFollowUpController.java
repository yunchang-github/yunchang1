package com.weiziplus.springboot.controller.logisticsDelivery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("/pc/logisticsDelivery/claimFollowUp")
public class ClaimFollowUpController {
	@Autowired
	LogisticsDeliveryService service;
	@GetMapping("/getPageList")
	public ResultUtil getPageList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "shop", required = false) String shop,
            @RequestParam(value = "area", required = false) String area) {
        return service.getClaimFollowUpPageList(pageNum, pageSize,shop,area);
    }


	/**
	 * 索赔跟进添加，修改
	 */
	@PostMapping(value="/saveClaimFollowUp")
	public ResultUtil saveClaimFollowUp(
			@RequestParam(value = "shipmentId", required = false) String shipmentId,
            @RequestParam(value = "shop", required = false) String shop,
            @RequestParam(value = "area", required = false) String area,
            @RequestParam(value = "msku", required = false) String msku,
            @RequestParam(value = "claimProgressRecord", required = false) String claimProgressRecord,
            @RequestParam(value = "extendedFutures", required = false) String extendedFutures,
            @RequestParam(value = "reasonDelay", required = false) String reasonDelay,
            @RequestParam(value = "estimatedDelayTime", required = false) String estimatedDelayTime,
            @RequestParam(value = "claimResult", required = false) String claimResult
			) {

		 return service.saveClaimFollowUp(shop,area,shipmentId,msku,claimProgressRecord,extendedFutures,reasonDelay,estimatedDelayTime,claimResult);
    }



}
