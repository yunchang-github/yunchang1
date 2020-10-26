package com.weiziplus.springboot.controller.shop;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.models.DO.AuthorizationDO;
import com.weiziplus.springboot.models.Shop;
import com.weiziplus.springboot.service.shop.ShopService;
import com.weiziplus.springboot.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author wanglongwei
 * @data 2019/7/11 9:44
 */
@Api(value="ShopController",tags={"店铺管理"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/shop/shop")
public class ShopController {

    @Autowired
    ShopService service;

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
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "createTime", required = false) String createTime) {
        return service.getPageList(pageNum, pageSize, name, createTime);
    }

    /**
     * 获取网店列表
     *
     * @return
     */
    @ApiOperation(value = "获取网店列表", notes = "获取网店列表")
    @GetMapping("/getNameAndIdList")
    public ResultUtil getNameAndIdList() {
        return service.getNameAndIdList();
    }

    /**
     * 新增
     *
     * @param shop
     * @return
     */
    @ApiOperation(value = "新增表", notes = "新增")
    @PostMapping("/add")
    public ResultUtil add(Shop shop) {
        return service.add(shop);
    }

    /**
     * 修改
     *
     * @param shop
     * @return
     */
    @ApiOperation(value = "修改", notes = "修改")
    @PostMapping("/update")
    public ResultUtil update(Shop shop) {
        return service.update(shop);
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

    /**
     * 根据网店id获取区域列表
     *
     * @param shopId
     * @return
     */
    @ApiOperation(value = "根据网店id获取区域列表", notes = "根据网店id获取区域列表")
    @GetMapping("/getAreaList")
    public ResultUtil getAreaList(Long shopId) {
        return service.getAreaList(shopId);
    }

    /**
     * 根据网店id获取所有区域列表
     *
     * @param shopId
     * @return
     */
    @ApiOperation(value = "根据网店id获取所有区域列表", notes = "根据网店id获取所有区域列表")
    @GetMapping("/getAllAreaList")
    public ResultUtil getAllAreaList(Long shopId) {
        return service.getAllAreaList(shopId);
    }

    /**
     * 更新网店区域列表
     *
     * @param shopId
     * @param areaIds
     * @return
     */
    @ApiOperation(value = "更新网店区域列表", notes = "更新网店区域列表")
    @PostMapping("/updateAreaList")
    public ResultUtil updateAreaList(
            @RequestParam(value = "shopId") Long shopId,
            @RequestParam(value = "areaIds", defaultValue = "") Long[] areaIds) {
        return service.updateAreaList(shopId, areaIds);
    }

    /**
     * 根据网店id获取用户列表信息
     *
     * @param shopId
     * @return
     */
    @ApiOperation(value = "根据网店id获取用户列表信息", notes = "根据网店id获取用户列表信息")
    @GetMapping("/getUserList")
    public ResultUtil getUserList(Long shopId) {
        return service.getUserList(shopId);
    }

    /**
     * 广告授权
     * @return
     */
    @ApiOperation(value = "广告授权", notes = "广告授权")
    @PostMapping("/advertisingAuthorization")
    public ResultUtil advertisingAuthorization(AuthorizationDO authorizationDO){
        return service.saveAuthorization(authorizationDO);
    }


}
