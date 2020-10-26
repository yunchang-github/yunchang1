package com.weiziplus.springboot.controller.data.manualimport;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.models.LogisticsTimeLimitation;
import com.weiziplus.springboot.service.data.manualimport.LogisticsTimeLimitationService;
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
 * @data 2019/7/17 10:37
 */


@Api(value="LogisticsTimeLimitationController",tags={"物流时效"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/data/manualImport/logisticsTimeLimitation")
public class LogisticsTimeLimitationController {

    @Autowired
    LogisticsTimeLimitationService service;

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
            @ApiParam(value = "每页数", required = true) @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @ApiParam(value = "物流方式代码", required = false) @RequestParam(value = "code", required = false) String code,
            @ApiParam(value = "物流方式", required = false)@RequestParam(value = "mode", required = false) String mode,
            @ApiParam(value = "物流类型", required = false)@RequestParam(value = "type", required = false) String type) {
        return service.getPageList(pageNum, pageSize, code, mode, type);
    }

    /**
     * 新增
     *
     * @param logisticsTimeLimitation
     * @return
     */
    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("/add")
    public ResultUtil add(@ApiParam(value = "物流时效", required = true)LogisticsTimeLimitation logisticsTimeLimitation) {
        return service.add(logisticsTimeLimitation);
    }

    /**
     * 修改
     *
     * @param logisticsTimeLimitation
     * @return
     */
    @ApiOperation(value = "修改", notes = "修改")
    @PostMapping("/update")
    public ResultUtil update(@ApiParam(value = "物流时效", required = true)LogisticsTimeLimitation logisticsTimeLimitation) {
        return service.update(logisticsTimeLimitation);
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
