package com.weiziplus.springboot.controller.shop;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.models.Area;
import com.weiziplus.springboot.service.shop.AreaService;
import com.weiziplus.springboot.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author wanglongwei
 * @data 2019/7/11 11:17
 */
@Api(value="AreaController",tags={"区域管理"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/shop/area")
public class AreaController {

    @Autowired
    AreaService service;

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
            @RequestParam(value = "name", required = false) String name) {
        return service.getPageList(pageNum, pageSize, name);
    }

    /**
     * 获取列表
     *
     * @return
     */
    @ApiOperation(value = "获取列表", notes = "获取列表")
    @GetMapping("/getList")
    public ResultUtil getList() {
        return service.getList();
    }

    /**
     * 新增
     *
     * @param area
     * @return
     */
    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("/add")
    public ResultUtil add(Area area) {
        return service.add(area);
    }

    /**
     * 修改
     *
     * @param area
     * @return
     */
    @ApiOperation(value = "修改", notes = "修改")
    @PostMapping("/update")
    public ResultUtil update(Area area) {
        return service.update(area);
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
