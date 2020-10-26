package com.weiziplus.springboot.controller.system;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.interceptor.SystemLog;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.service.system.SysLogService;
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
 * @data 2019/5/13 15:33
 */
@Api(value="SysLogController",tags={"系统日志"})
@RestController
@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/sysLog")
public class SysLogController {
    @Autowired
    SysLogService service;

    @ApiOperation(value = "查看系统日志", notes = "查看系统日志")
    @GetMapping("/getLogList")
    @SystemLog(description = "查看系统日志")
    public ResultUtil getLogList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "roleId", required = false) Long roleId,
            @RequestParam(value = "createTime", required = false) String createTime,
            @RequestParam(value = "description", required = false) String description) {
        return ResultUtil.success(service.getLogList(pageNum, pageSize, username, roleId, createTime,description));
    }
}
