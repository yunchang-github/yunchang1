package com.weiziplus.springboot.controller.data.manualimport;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.models.MskuPurchaseNote;
import com.weiziplus.springboot.service.data.manualimport.MskuPurchaseNoteService;
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
 * @data 2019/7/29 8:34
 */

@Api(value="MskuPurchaseNoteController",tags={"msku采购注意事项"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/data/manualImport/mskuPurchaseNote")
public class MskuPurchaseNoteController {

    @Autowired
    MskuPurchaseNoteService service;

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
            @ApiParam(value = "当前页", required = true)@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数", required = true)@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @ApiParam(value = "店铺", required = false) @RequestParam(value = "shop", required = false) String shop,
            @ApiParam(value = "区域", required = false)@RequestParam(value = "area", required = false) String area,
            @ApiParam(value = "msku", required = false)@RequestParam(value = "msku", required = false) String msku) {
        return service.getPageList(pageNum, pageSize, shop, area, msku);
    }

    /**
     * 新增
     *
     * @param mskuPurchaseNote
     * @return
     */
    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("/add")
    public ResultUtil add(@ApiParam(value = "msku采购注意事项", required = true)MskuPurchaseNote mskuPurchaseNote) {
        return service.add(mskuPurchaseNote);
    }

    /**
     * 修改
     *
     * @param mskuPurchaseNote
     * @return
     */
    @ApiOperation(value = "修改", notes = "修改")
    @PostMapping("/update")
    public ResultUtil update(@ApiParam(value = "msku采购注意事项", required = true)MskuPurchaseNote mskuPurchaseNote) {
        return service.update(mskuPurchaseNote);
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
