package com.weiziplus.springboot.controller.system;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.interceptor.SystemLog;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.service.system.AdminLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author wanglongwei
 * @data 2019/5/9 11:07
 */
@Api(value="AdminLoginController",tags={"登录相关接口"})
@RestController
//@ApiIgnore
@RequestMapping("/pc")
public class AdminLoginController {
    @Autowired
    AdminLoginService service;

    /**
     * 生成验证码
     *
     * @param request
     * @param response
     * @return
     */
    @ApiOperation(value = "生成验证码", notes = "生成验证码")
    @GetMapping("/getValidateCode")
    public void getValidateCode(HttpServletRequest request, HttpServletResponse response) {
        service.getValidateCode(request, response);
    }

    /**
     * 系统用户登录
     *
     * @param username
     * @param password
     * @return
     */
    @ApiOperation(value = "系统用户登录", notes = "系统用户登录")
    @PostMapping("/login")
    @SystemLog(description = "系统用户登录")
    public ResultUtil login(HttpServletRequest request, HttpSession session, String username, String password, String code) {
        return service.login(request, session, username, password, code);
    }

    /**
     * 系统用户退出登录
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "系统用户退出登录", notes = "系统用户退出登录")
    @AdminAuthToken
    @GetMapping("/logout")
    @SystemLog(description = "系统用户退出登录")
    public ResultUtil logout(HttpServletRequest request) {
        return service.logout(request);
    }
}
