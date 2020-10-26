package com.weiziplus.springboot.service.system;

import com.weiziplus.springboot.config.GlobalConfig;
import com.weiziplus.springboot.mapper.shop.ShopAreaMapper;
import com.weiziplus.springboot.mapper.shop.UserShopMapper;
import com.weiziplus.springboot.models.SysRole;
import com.weiziplus.springboot.models.SysUser;
import com.weiziplus.springboot.utils.*;
import com.weiziplus.springboot.utils.token.AdminTokenUtil;
import com.weiziplus.springboot.utils.token.JwtTokenUtil;
import com.weiziplus.springboot.mapper.system.SysFunctionMapper;
import com.weiziplus.springboot.mapper.system.SysRoleMapper;
import com.weiziplus.springboot.mapper.system.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/5/9 11:08
 */
@Slf4j
@Service
public class AdminLoginService {

    @Autowired
    SysUserMapper sysUserMapper;

    @Autowired
    SysRoleMapper sysRoleMapper;

    @Autowired
    SysFunctionMapper sysFunctionMapper;

    @Autowired
    SysFunctionService sysFunctionService;
    @Autowired
    SysUserService sysUserService;

    @Autowired
    UserShopMapper userShopMapper;

    @Autowired
    ShopAreaMapper shopAreaMapper;

    /**
     * 图片验证码放入session中的key
     */
    private final String LOGIN_RANDOM_CODE = "loginRandomCoe";

    /**
     * 生成图片验证码
     *
     * @param request
     * @param response
     * @return
     */
    public void getValidateCode(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> validateCode = ImageValidateCodeUtil.getValidateCode();
        HttpSession session = request.getSession();
        session.setAttribute(LOGIN_RANDOM_CODE, validateCode.get("random"));
        //设置相应类型,告诉浏览器输出的内容为图片
        response.setContentType("image/jpeg");
        //设置响应头信息，告诉浏览器不要缓存此内容
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);
        try {
            ImageIO.write((BufferedImage) validateCode.get("image"), "JPEG", response.getOutputStream());
        } catch (IOException e) {
            log.warn("登录时生成图片验证码失败" + e);
        }
    }

    /**
     * 系统用户登录
     *
     * @param username
     * @param password
     * @return
     */
    public ResultUtil login(HttpServletRequest request, HttpSession session, String username, String password, String code) {
        if (StringUtil.isBlank(username) || StringUtil.isBlank(password)) {
            return ResultUtil.error("用户名或密码为空");
        }
//        if (StringUtil.isBlank(code) || ImageValidateCodeUtil.RANDOM_NUM != code.length()) {
//            session.removeAttribute(LOGIN_RANDOM_CODE);
//            return ResultUtil.error("验证码错误");
//        }
//        Object randomCode = session.getAttribute(LOGIN_RANDOM_CODE);
//        session.removeAttribute(LOGIN_RANDOM_CODE);
//        if (null == randomCode || !code.equalsIgnoreCase(randomCode.toString())) {
//            return ResultUtil.error("验证码错误");
//        }
        SysUser sysUser = sysUserMapper.getInfoByUsername(username);
        if (null == sysUser || !sysUser.getPassword().equals(Md5Util.encode(password))) {
            return ResultUtil.error("用户名或密码错误");
        }
        if (!GlobalConfig.ALLOW_LOGIN.equals(sysUser.getAllowLogin())) {
            return ResultUtil.error("账号被禁用，请联系管理员");
        }
        SysRole sysRole = sysRoleMapper.getInfoByUserId(sysUser.getId());
        if (null == sysRole) {
            return ResultUtil.error("您还没有角色，请联系管理员添加");
        }
        if (!GlobalConfig.IS_STOP.equals(sysRole.getIsStop())) {
            return ResultUtil.error("角色被禁用，请联系管理员");
        }
        Map<String, Object> map = new HashMap<>(5);
        //如果用户角色为运营
        if (GlobalConfig.OPERATE_ROLE_ID.equals(sysRole.getId())) {
            List<Map<String, Object>> shopListByUserId = userShopMapper.getShopListByUserId(sysUser.getId());
            if (null == shopListByUserId || 0 >= shopListByUserId.size()) {
                return ResultUtil.error("您还没有网店，请联系管理员添加");
            }
            for (Map<String, Object> m : shopListByUserId) {
                m.put("area", shopAreaMapper.getAreaListByShopId(Long.valueOf(m.get("id").toString())));
            }
            map.put("shopList", shopListByUserId);
        }
        String token = AdminTokenUtil.createToken(sysUser.getId(), HttpRequestUtil.getIpAddress(request), sysRole.getId());
        if (null == token) {
            return ResultUtil.error("登录失败，请重试");
        }
        //判断是否是默认密码
        if (sysUser.getPassword().equals(Md5Util.encode(GlobalConfig.DETAIL_INIT_PASSWORD))) {
            return ResultUtil.errorDetailPassword("初次登录，请重置密码", token);
        }
        map.put("token", token);
        map.put("userInfo", sysUser);
        map.put("role", sysRole.getName());
        map.put("routerTree", sysFunctionService.getMenuTreeByRoleId(sysRole.getId()));
        map.put("roleButtons", sysFunctionMapper.getButtonListByRoleId(sysRole.getId()));
        map.put("shopAreaList", getShopAreaList(sysUser.getId()));
        return ResultUtil.success(map);
    }
   private List<Map<String, Object>> getShopAreaList(long userId) {
        List<Map<String, Object>> shopListByUserId = userShopMapper.getShopListByUserId(userId);
        if (shopListByUserId==null||shopListByUserId.size()<=0){
            return new ArrayList<>();
        }
        for (Map<String, Object> map : shopListByUserId) {
            map.put("area", shopAreaMapper.getAreaListByShopId(Long.valueOf(map.get("id").toString())));
        }
        return shopListByUserId;
    }
    /**
     * 退出登录
     *
     * @param request
     * @return
     */
    public ResultUtil logout(HttpServletRequest request) {
        Long userId = JwtTokenUtil.getUserIdByHttpServletRequest(request);
        AdminTokenUtil.deleteToken(userId);
        return ResultUtil.success();
    }
}
