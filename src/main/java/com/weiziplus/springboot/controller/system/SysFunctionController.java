package com.weiziplus.springboot.controller.system;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.interceptor.SystemLog;
import com.weiziplus.springboot.models.SysFunction;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.service.system.SysFunctionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author wanglongwei
 * @data 2019/5/10 8:23
 */
@Api(value="SysFunctionController",tags={"菜单管理"})
@RestController
@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/sysFunction")
public class SysFunctionController {
    @Autowired
    SysFunctionService service;

    /**
     * 获取所有功能树形列表
     *
     * @return
     */
    @ApiOperation(value = "查看功能列表包括按钮", notes = "查看功能列表包括按钮")
    @GetMapping("/getAllFunctionTree")
    @SystemLog(description = "")
    public ResultUtil getAllFunctionTree() {
        return ResultUtil.success(service.getFunTree());
    }

    /**
     * 获取所有功能树形列表
     *
     * @return
     */
    @ApiOperation(value = "查看功能列表不包括按钮", notes = "查看功能列表不包括按钮")
    @GetMapping("/getAllFunctionTreeNotButton")
    @SystemLog(description = "查看功能列表")
    public ResultUtil getAllFunctionTreeNotButton() {
        return ResultUtil.success(service.getAllFunctionTreeNotButton());
    }

    /**
     * 根据parentId获取功能列表
     *
     * @param parentId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "根据parentId获取功能列表", notes = "根据parentId获取功能列表")
    @GetMapping("/getFunctionList")
    public ResultUtil getFunctionList(
            @RequestParam(value = "parentId", defaultValue = "0") Long parentId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return service.getFunctionListByParentId(parentId, pageNum, pageSize);
    }

    /**
     * 根据角色id获取功能列表
     *
     * @param roleId
     * @return
     */
    @ApiOperation(value = "根据角色id获取功能列表", notes = "根据角色id获取功能列表")
    @GetMapping("/getRoleFunList")
    public ResultUtil getRoleFunList(Long roleId) {
        return ResultUtil.success(service.getRoleFunList(roleId));
    }

    /**
     * 新增功能
     *
     * @param sysFunction
     * @return
     */
    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("/addFunction")
    @SystemLog(description = "新增功能")
    public ResultUtil addFunction(SysFunction sysFunction) {
        return service.addFunction(sysFunction);
    }

    /**
     * 修改功能
     *
     * @param sysFunction
     * @return
     */
    @ApiOperation(value = "修改", notes = "修改")
    @PostMapping("/updateFunction")
    @SystemLog(description = "修改功能")
    public ResultUtil updateFunction(SysFunction sysFunction) {
        return service.updateFunction(sysFunction);
    }

    /**
     * 删除功能
     *
     * @param ids
     * @return
     */
    @ApiOperation(value = "删除", notes = "删除")
    @PostMapping("/deleteFunction")
    @SystemLog(description = "删除功能")
    public ResultUtil deleteFunction(Long[] ids) {
        return service.deleteFunction(ids);
    }
}
