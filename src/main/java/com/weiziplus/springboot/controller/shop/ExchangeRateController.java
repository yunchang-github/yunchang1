package com.weiziplus.springboot.controller.shop;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.models.ExchangeRate;
import com.weiziplus.springboot.service.shop.ExchangeRateService;
import com.weiziplus.springboot.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author wanglongwei
 * @data 2019/7/11 15:20
 */
@Api(value="ExchangeRateController",tags={"汇率管理"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/shop/exchangeRate")
public class ExchangeRateController {

    @Autowired
    ExchangeRateService service;

    /**
     * 获取分页列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "获取分页列表", notes = "获取分页列表")
    @GetMapping("/getPageList")
    public ResultUtil getPageList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "areaId", required = false) Long areaId,
            @RequestParam(value = "currency", required = false) String currency,
            @RequestParam(value = "createTime", required = false) String createTime) {
        return service.getPageList(pageNum, pageSize, areaId, currency, createTime);
    }

    /**
     * 新增
     *
     * @param exchangeRate
     * @return
     */
    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("/add")
    public ResultUtil add(ExchangeRate exchangeRate) {
        return service.add(exchangeRate);
    }

    /**
     * 修改
     *
     * @param exchangeRate
     * @return
     */
    @ApiOperation(value = "修改", notes = "修改")
    @PostMapping("/update")
    public ResultUtil update(ExchangeRate exchangeRate) {
        return service.update(exchangeRate);
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @ApiOperation(value = "删除", notes = "删除")
    @PostMapping("/delete")
    public ResultUtil delete(Long[] ids) {
        return service.delete(ids);
    }
}
