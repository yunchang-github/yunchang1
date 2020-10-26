package com.weiziplus.springboot.controller.system;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.interceptor.SystemLog;
import com.weiziplus.springboot.models.SysUser;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.service.system.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wanglongwei
 * @data 2019/5/10 9:01
 */
@Api(value="SysUserController",tags={"用户管理"})
@RestController
@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/sysUser")
public class SysUserController {
    @Autowired
    SysUserService service;

    /**
     * 获取用户列表
     *
     * @param pageNum
     * @param pageSize
     * @param userName
     * @param allowLogin
     * @return
     */
    @ApiOperation(value = "查看用户列表", notes = "查看用户列表")
    @GetMapping("/getUserList")
    @SystemLog(description = "查看用户列表")
    public ResultUtil getUserList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "allowLogin", required = false) Integer allowLogin,
            @RequestParam(value = "createTime", required = false) String createTime) {
        return service.getUserList(pageNum, pageSize, userName, allowLogin, createTime);
    }

    /**
     * 新增用户
     *
     * @param sysUser
     * @return
     */
    @ApiOperation(value = "新增用户", notes = "新增用户")
    @PostMapping("/addUser")
    @SystemLog(description = "新增用户")
    public ResultUtil addUser(SysUser sysUser) {
        return service.addUser(sysUser);
    }

    /**
     * 更新用户
     *
     * @param sysUser
     * @return
     */
    @ApiOperation(value = "更新用户", notes = "更新用户")
    @PostMapping("/updateUser")
    @SystemLog(description = "更新用户")
    public ResultUtil updateUser(SysUser sysUser) {
        return service.updateUser(sysUser);
    }

    /**
     * 删除用户
     *
     * @param ids
     * @return
     */
    @ApiOperation(value = "删除用户", notes = "删除用户")
    @PostMapping("/deleteUser")
    @SystemLog(description = "删除用户")
    public ResultUtil deleteUser(
            @RequestParam(value = "ids", defaultValue = "") Long[] ids) {
        return service.deleteUser(ids);
    }

    /**
     * 更新用户角色
     *
     * @param userId
     * @param roleId
     * @return
     */
    @ApiOperation(value = "更新用户角色", notes = "更新用户角色")
    @PostMapping("/updateUserRole")
    @SystemLog(description = "更新用户角色")
    public ResultUtil updateUserRole(Long userId, Long roleId) {
        return service.updateUserRole(userId, roleId);
    }

    /**
     * 修改密码
     *
     * @param request
     * @param oldPwd
     * @param newPwd
     * @return
     */
    @ApiOperation(value = "修改密码", notes = "修改密码")
    @PostMapping("/updatePassword")
    @SystemLog(description = "修改密码")
    public ResultUtil updatePassword(HttpServletRequest request, String oldPwd, String newPwd) {
        return service.updatePassword(request, oldPwd, newPwd);
    }

    /**
     * 重置密码
     */
    @ApiOperation(value = "重置用户密码", notes = "重置用户密码")
    @PostMapping("/resetUserPassword")
    @SystemLog(description = "重置用户密码")
    public ResultUtil resetUserPassword(HttpServletRequest request, Long userId, String password) {
        return service.resetUserPassword(request, userId, password);
    }

    /**
     * 根据用户id获取所有网店列表
     *
     * @param userId
     * @return
     */
    @ApiOperation(value = "查看角色树", notes = "查看角色树")
    @GetMapping("/getAllShopList")
    public ResultUtil getAllShopList(Long userId) {
        return service.getAllShopList(userId);
    }

    /**
     * 添加网店信息
     *
     * @param userId
     * @param shopIds
     * @return
     */
    @ApiOperation(value = "添加网店信息", notes = "添加网店信息")
    @PostMapping("/addShop")
    public ResultUtil addShop(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "shopIds", defaultValue = "") Long[] shopIds) {
        return service.addShop(userId, shopIds);
    }

    /**
     * 根据用户id获取网店列表
     *
     * @param userId
     * @return
     */
    @ApiOperation(value = "根据用户id获取网店列表", notes = "根据用户id获取网店列表")
    @GetMapping("/getShopList")
    public ResultUtil getShopList(Long userId) {
        return service.getShopList(userId);
    }

    /**
     * 根据用户id获取网店和地区列表
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "根据用户id获取网店和地区列表", notes = "根据用户id获取网店和地区列表")
    @GetMapping("/getShopAreaList")
    public ResultUtil getShopAreaList(HttpServletRequest request) {
        return service.getShopAreaList(request);
    }
}
