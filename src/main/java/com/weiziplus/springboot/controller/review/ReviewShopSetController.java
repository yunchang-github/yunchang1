package com.weiziplus.springboot.controller.review;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.models.CrawlShop;
import com.weiziplus.springboot.service.review.ReviewShopSetService;
import com.weiziplus.springboot.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

/**
 * 商品评价，店铺设置
 *
 * @author wanglongwei
 * @data 2019/8/27 16:35
 */
@Api(value="ReviewShopSetController",tags={"商品评价的店铺设置"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/review/shopSet")
public class ReviewShopSetController {

    @Autowired
    ReviewShopSetService service;

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
     * 模板下载
     *
     * @param response
     * @return
     */
    @ApiOperation(value = "模板下载", notes = "模板下载")
    @PostMapping("/downTemplate")
    public void downTemplate(HttpServletResponse response) {
        service.downTemplate(response);
    }

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "文件上传", notes = "文件上传")
    @PostMapping("/upload")
    public ResultUtil upload(MultipartFile file) {
        return service.upload(file);
    }

    /**
     * 新增
     *
     * @param shop
     * @return
     */
    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("/add")
    public ResultUtil add(CrawlShop shop) {
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
    public ResultUtil update(CrawlShop shop) {
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
}
