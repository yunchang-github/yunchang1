package com.weiziplus.springboot.controller.system;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.interceptor.SystemLog;
import com.weiziplus.springboot.models.SysRole;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.service.system.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author wanglongwei
 * @data 2019/5/10 8:40
 */
@Api(value="SysRoleController",tags={"角色管理"})
@RestController
@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/sysRole")
public class SysRoleController {

    @Autowired
    SysRoleService service;

    /**
     * 获取权限树形列表
     *
     * @return
     */
    @ApiOperation(value = "查看角色树", notes = "查看角色树")
    @GetMapping("/getRoleTree")
    @SystemLog(description = "查看角色树")
    public ResultUtil getRoleTree() {
        return ResultUtil.success(service.getRoleTree());
    }

    /**
     * 获取权限列表
     *
     * @return
     */
    @ApiOperation(value = "获取权限列表", notes = "获取权限列表")
    @GetMapping("/getRoleList")
    @SystemLog(description = "查看角色列表")
    public ResultUtil getRoleList() {
        return ResultUtil.success(service.getRoleList());
    }

    /**
     * 新增角色功能
     *
     * @param roleId
     * @param funIds
     * @return
     */
    @ApiOperation(value = "分配给角色菜单", notes = "分配给角色菜单")
    @PostMapping("/addRoleFun")
    @SystemLog(description = "新增角色功能")
    public ResultUtil addRoleFun(
            @RequestParam(value = "roleId") Long roleId,
            @RequestParam(value = "funIds", defaultValue = "") Long[] funIds) {
        return service.addRoleFun(roleId, funIds);
    }

    /**
     * 新增角色
     *
     * @param sysRole
     * @return
     */
    @ApiOperation(value = " 新增角色", notes = " 新增角色")
    @PostMapping("/addRole")
    @SystemLog(description = "新增角色")
    public ResultUtil addRole(SysRole sysRole) {
        return service.addRole(sysRole);
    }

    /**
     * 修改角色
     *
     * @param sysRole
     * @return
     */
    @ApiOperation(value = "修改角色", notes = "修改角色")
    @PostMapping("/updateRole")
    @SystemLog(description = "修改角色")
    public ResultUtil updateRole(SysRole sysRole) {
        return service.updateRole(sysRole);
    }

    /**
     * 删除角色
     *
     * @param roleId
     * @return
     */
    @ApiOperation(value = "删除角色", notes = "删除角色")
    @PostMapping("/deleteRole")
    @SystemLog(description = "删除角色")
    public ResultUtil deleteRole(Long roleId) {
        return service.deleteRole(roleId);
    }

    /**
     * 改变角色状态
     *
     * @param roleId
     * @return
     */
    @ApiOperation(value = "改变角色状态", notes = "改变角色状态")
    @PostMapping("/changeRoleIsStop")
    @SystemLog(description = "改变角色状态")
    public ResultUtil changeRoleIsStop(Long roleId, Integer isStop) {
        return service.changeRoleIsStop(roleId, isStop);
    }
}
