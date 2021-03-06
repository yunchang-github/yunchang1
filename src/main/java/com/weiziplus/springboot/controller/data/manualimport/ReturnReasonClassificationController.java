package com.weiziplus.springboot.controller.data.manualimport;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.models.ReturnReasonClassification;
import com.weiziplus.springboot.service.data.manualimport.ReturnReasonClassificationService;
import com.weiziplus.springboot.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

/**
 * @author wanglongwei
 * @data 2019/7/17 10:34
 */

@Api(value="ReturnReasonClassificationController",tags={"退货原因分类（手动导入）"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/data/manualImport/returnReasonClassification")
public class ReturnReasonClassificationController {

    @Autowired
    ReturnReasonClassificationService service;

    /**
     * 获取分页列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "查询列表", notes = "查询列表")
    @GetMapping("/getPageList")
    public ResultUtil getPageList(
            @ApiParam(value = "当前页", required = true) @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数", required = true) @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return service.getPageList(pageNum, pageSize);
    }

    /**
     * 新增
     *
     * @param returnReasonClassification
     * @return
     */
    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("/add")
    public ResultUtil add(@ApiParam(value = "退货原因分类对象", required = true)ReturnReasonClassification returnReasonClassification) {
        return service.add(returnReasonClassification);
    }

    /**
     * 修改
     *
     * @param returnReasonClassification
     * @return
     */
    @ApiOperation(value = "修改", notes = "修改")
    @PostMapping("/update")
    public ResultUtil update(@ApiParam(value = "退货原因分类对象", required = true)ReturnReasonClassification returnReasonClassification) {
        return service.update(returnReasonClassification);
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @ApiOperation(value = "删除", notes = "删除")
    @PostMapping("/delete")
    public ResultUtil delete(@ApiParam(value = "所选id,数组", required = true)Long[] ids) {
        return service.delete(ids);
    }

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "上传", notes = "上传")
    @PostMapping("/upload")
    public ResultUtil upload(MultipartFile file) {
        return service.upload(file);
    }

    /**
     * 模板下载
     *
     * @param response
     * @return
     */
    @ApiOperation(value = "下载模板", notes = "下载模板")
    @PostMapping("/downTemplate")
    public void downTemplate(HttpServletResponse response) {
        service.downTemplate(response);
    }
}
