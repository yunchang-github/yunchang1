package com.weiziplus.springboot.controller.replenishmentFlowArrangement;

import com.weiziplus.springboot.service.replenishmentFlowArrangement.ReplenishmentFlowArrangementService;
import com.weiziplus.springboot.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Api(value="ReplenishmentFlowArrangementController",tags={"补货物流安排"})
@RestController
@RequestMapping("/pc/replenishmentFlowArrangement/replenishmentList")
public class ReplenishmentFlowArrangementController {
    @Autowired
    private ReplenishmentFlowArrangementService service;

    @ApiOperation(value = "分页列表", notes = "分页列表")
    @GetMapping("/getPageList")
    public ResultUtil getPageList(
            @ApiParam(value = "当前页", required = true)@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数", required = true)@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @ApiParam(value = "店铺", required = false)@RequestParam(value = "shop", required = false) String shop,
            @ApiParam(value = "区域", required = false)@RequestParam(value = "area", required = false) String area) {
        return service.getPageList(pageNum, pageSize);


    }
}
