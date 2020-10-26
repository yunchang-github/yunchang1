package com.weiziplus.springboot.controller.seckill;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.service.seckill.SeckillService;
import com.weiziplus.springboot.utils.ResultUtil;
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
 * @data 2019/8/29 15:59
 */

@Api(value="SeckillController",tags={"秒杀管理"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/seckill")
public class SeckillController {

    @Autowired
    SeckillService service;

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
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return service.getPageList(pageNum, pageSize);
    }

    /**
     * 根据id获取item列表
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id获取item列表", notes = "根据id获取item列表")
    @GetMapping("/getItemList")
    public ResultUtil getItemList(String id) {
        return service.getItemList(id);
    }
}
