package com.weiziplus.springboot.interceptor;

import com.alibaba.fastjson.JSON;
import com.weiziplus.springboot.config.GlobalConfig;
import com.weiziplus.springboot.mapper.system.SysLogMapper;
import com.weiziplus.springboot.mapper.system.SysUserMapper;
import com.weiziplus.springboot.service.system.SysFunctionService;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.utils.StringUtil;
import com.weiziplus.springboot.utils.redis.StringRedisUtil;
import com.weiziplus.springboot.utils.token.AdminTokenUtil;
import com.weiziplus.springboot.utils.token.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * 自定义的拦截器interceptor
 *
 * @author wanglongwei
 * @data 2019/4/22 16:08
 */
@Slf4j
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {
    @Autowired
    SysUserMapper sysUserMapper;

    @Autowired
    SysLogMapper sysLogMapper;

    @Autowired
    SysFunctionService sysFunctionService;

    /**
     * 请求之前拦截
     *
     * @param request
     * @param response
     * @param object
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        //如果有忽略token注解
        if (null != handlerMethod.getBeanType().getAnnotation(AuthTokenIgnore.class) || null != method.getAnnotation(AuthTokenIgnore.class)) {
            return true;
        }
        //检查class或者方法是否有@AdminAuthToken，没有的话跳过拦截
        AdminAuthToken adminAuthTokenClass = handlerMethod.getBeanType().getAnnotation(AdminAuthToken.class);
        AdminAuthToken adminAuthTokenMethod = method.getAnnotation(AdminAuthToken.class);
        if (null == adminAuthTokenClass && null == adminAuthTokenMethod) {
            return true;
        }
        //获取头部的token
        String token = request.getHeader(GlobalConfig.TOKEN);
        if (StringUtil.isBlank(token)) {
            handleResponse(response, ResultUtil.errorToken("token不存在"));
            return false;
        }
        try {
            //判断jwtToken是否过期
            if (JwtTokenUtil.isExpiration(token)) {
                handleResponse(response, ResultUtil.errorToken("token失效"));
                return false;
            }
        } catch (Exception e) {
            handleResponse(response, ResultUtil.errorToken("token失效"));
            return false;
        }
        //查看是否有日志注解，有的话将日志信息放入数据库
        SystemLog systemLog = method.getAnnotation(SystemLog.class);
        if (null != systemLog) {
            sysLogMapper.addSysLog(JwtTokenUtil.getUserIdByToken(token), systemLog.description());
        }
        return handleAdminToken(request, response, token, adminAuthTokenClass, adminAuthTokenMethod);
    }

    /**
     * 处理admin的token
     *
     * @param response
     * @param token
     * @param authTokenClass
     * @param authTokenMethod
     * @return
     */
    private boolean handleAdminToken(HttpServletRequest request, HttpServletResponse response, String token, AdminAuthToken authTokenClass, AdminAuthToken authTokenMethod) {
        //获取用户id
        Long userId = JwtTokenUtil.getUserIdByToken(token);
        //判断当前注解是否和当前角色匹配
        if (null == authTokenClass && null == authTokenMethod) {
            handleResponse(response, ResultUtil.errorToken("token失效"));
            return false;
        }
        //查看redis是否过期
        if (!StringRedisUtil.hasKye(AdminTokenUtil.getAudienceRedisKey(userId))) {
            handleResponse(response, ResultUtil.errorToken("token失效"));
            return false;
        }
        //查看redis中token是否是当前token
//        if (!StringRedisUtil.get(AdminTokenUtil.getAudienceRedisKey(userId)).equals(token)) {
//            handleResponse(response, ResultUtil.errorToken("token失效"));
//            return false;
//        }
        //更新用户最后活跃时间
        int i = sysUserMapper.updateLastActiveTimeById(userId);
        //如果更新成功，证明有该用户，反之没有该用户
        if (0 >= i) {
            handleResponse(response, ResultUtil.errorToken("token失效"));
            return false;
        }
        //如果当前是超级管理员---直接放过
        if (GlobalConfig.SUPER_ADMIN_ID.equals(userId)) {
            //更新token过期时间
            AdminTokenUtil.updateExpireTime(userId);
            return true;
        }
        //获取当前访问的url
        String requestURI = request.getRequestURI();
        Set<String> allFunContainApi = sysFunctionService.getAllFunContainApi();
        //如果限制的功能api不包含当前url---直接放过
        if (null == allFunContainApi || !allFunContainApi.contains(requestURI)) {
            //更新token过期时间
            AdminTokenUtil.updateExpireTime(userId);
            return true;
        }
        //获取roleId
        Long roleId = JwtTokenUtil.getRoleIdByToken(token);
        if (null == roleId) {
            handleResponse(response, ResultUtil.errorRole("您没有权限"));
            return false;
        }
        //获取当前角色拥有的方法url
        Set<String> funContainApiByRoleId = sysFunctionService.getFunContainApiByRoleId(roleId);
        if (null != funContainApiByRoleId && funContainApiByRoleId.contains(requestURI)) {
            //更新token过期时间
            AdminTokenUtil.updateExpireTime(userId);
            return true;
        }
        handleResponse(response, ResultUtil.errorRole("您没有权限"));
        return false;
    }

    /**
     * 将token出错信息输入到前端页面
     *
     * @param response
     * @param errResult
     */
    private void handleResponse(HttpServletResponse response, ResultUtil errResult) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=utf-8");
            out = response.getWriter();
            out.print(JSON.toJSONString(errResult));
        } catch (Exception e) {
            log.warn("token失效输入到前端页面出错，catch" + e);
        } finally {
            if (null != out) {
                out.flush();
                out.close();
            }
        }
    }
}
