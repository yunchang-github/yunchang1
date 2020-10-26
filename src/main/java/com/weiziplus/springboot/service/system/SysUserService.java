package com.weiziplus.springboot.service.system;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.config.GlobalConfig;
import com.weiziplus.springboot.mapper.shop.ShopAreaMapper;
import com.weiziplus.springboot.mapper.shop.UserShopMapper;
import com.weiziplus.springboot.mapper.system.SysUserMapper;
import com.weiziplus.springboot.models.SysUser;
import com.weiziplus.springboot.utils.*;
import com.weiziplus.springboot.utils.token.AdminTokenUtil;
import com.weiziplus.springboot.utils.token.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/5/10 9:00
 */
@Slf4j
@Service
public class SysUserService extends BaseService {
    @Autowired
    SysUserMapper mapper;

    @Autowired
    UserShopMapper userShopMapper;

    @Autowired
    ShopAreaMapper shopAreaMapper;

    /**
     * 获取用户列表
     *
     * @param pageNum
     * @param pageSize
     * @param userName
     * @param allowLogin
     * @return
     */
    public ResultUtil getUserList(Integer pageNum, Integer pageSize, String userName, Integer allowLogin, String createTime) {
        if (0 >= pageNum || 0 >= pageSize) {
            return ResultUtil.error("pageNum,pageSize错误");
        }
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(mapper.getUserList(userName, allowLogin, createTime));
        return ResultUtil.success(pageUtil);
    }

    /**
     * 添加用户
     *
     * @param sysUser
     * @return
     */
    public ResultUtil addUser(SysUser sysUser) {
        if (ValidateUtil.notUsername(sysUser.getUsername())) {
            return ResultUtil.error("用户名不能包含特殊字符");
        }
        if (null != sysUser.getRealName() && ValidateUtil.notRealName(sysUser.getRealName())) {
            return ResultUtil.error("真实姓名格式错误");
        }
        SysUser user = mapper.getUserInfoByName(sysUser.getUsername());
        if (null != user) {
            return ResultUtil.error("用户名已存在");
        }
        sysUser.setPassword(Md5Util.encode(GlobalConfig.DETAIL_INIT_PASSWORD));
        sysUser.setCreateTime(DateUtil.getDate());
        return ResultUtil.success(baseInsert(sysUser));
    }

    /**
     * 更新用户
     *
     * @param sysUser
     * @return
     */
    public ResultUtil updateUser(SysUser sysUser) {
        if (ValidateUtil.notUsername(sysUser.getUsername())) {
            return ResultUtil.error("用户名不能包含特殊字符");
        }
        if (null != sysUser.getRealName() && ValidateUtil.notRealName(sysUser.getRealName())) {
            return ResultUtil.error("真实姓名格式错误");
        }
        SysUser user = mapper.getUserInfoByName(sysUser.getUsername());
        if (null != user && !sysUser.getId().equals(user.getId())) {
            return ResultUtil.error("用户名已存在");
        }
        sysUser.setPassword(null);
        return ResultUtil.success(baseUpdate(sysUser));
    }

    /**
     * 删除用户
     *
     * @param ids
     * @return
     */
    public ResultUtil deleteUser(Long[] ids) {
        if (null == ids || 0 >= ids.length) {
            return ResultUtil.error("ids为空");
        }
        for (Long id : ids) {
            if (GlobalConfig.SUPER_ADMIN_ID.equals(id)) {
                return ResultUtil.error("不能删除超级管理员");
            }
        }
        return ResultUtil.success(baseDeleteByClassAndIds(SysUser.class, ids));
    }

    /**
     * 更新用户角色
     *
     * @param userId
     * @param roleId
     * @return
     */
    public ResultUtil updateUserRole(Long userId, Long roleId) {
        if (null == userId || 0 >= userId) {
            return ResultUtil.error("userId不能为空");
        }
        if (GlobalConfig.SUPER_ADMIN_ID.equals(userId)) {
            return ResultUtil.error("不能修改超级管理员角色");
        }
        if (null == roleId || 0 > roleId) {
            return ResultUtil.error("roleId不能为空");
        }
        return ResultUtil.success(mapper.updateRoleIdByUserIdAndRoleId(userId, roleId));
    }

    /**
     * 修改用户密码
     *
     * @param request
     * @param oldPwd
     * @param newPwd
     * @return
     */
    public ResultUtil updatePassword(HttpServletRequest request, String oldPwd, String newPwd) {
        if (ValidateUtil.notPassword(oldPwd) || ValidateUtil.notPassword(newPwd)) {
            return ResultUtil.error("密码为6-20位大小写和数字");
        }
        Long userId = JwtTokenUtil.getUserIdByHttpServletRequest(request);
        Map<String, Object> map = baseFindByClassAndId(SysUser.class, userId);
        String passwordFiled = "password";
        if (null == map || null == map.get(passwordFiled) || !Md5Util.encode(oldPwd).equals(map.get(passwordFiled).toString())) {
            return ResultUtil.error("原密码错误");
        }
        if (GlobalConfig.DETAIL_INIT_PASSWORD.equals(newPwd)) {
            return ResultUtil.error("不能重新设置为初始密码，请重新设置密码");
        }
        mapper.resetUserPassword(userId, Md5Util.encode(newPwd));
        AdminTokenUtil.deleteToken(userId);
        return ResultUtil.success();
    }

    /**
     * 重置用户密码
     *
     * @param request
     * @param userId
     * @param password
     * @return
     */
    public ResultUtil resetUserPassword(HttpServletRequest request, Long userId, String password) {
        if (null == userId || 0 > userId) {
            return ResultUtil.error("id不能为空");
        }
        if (ValidateUtil.notPassword(password)) {
            return ResultUtil.error("密码为6-20位大小写和数字");
        }
        if (GlobalConfig.SUPER_ADMIN_ID.equals(userId) && !GlobalConfig.SUPER_ADMIN_ID.equals(JwtTokenUtil.getUserIdByHttpServletRequest(request))) {
            return ResultUtil.error("您没有权限");
        }
        String newPwd = Md5Util.encode(password);
        return ResultUtil.success(mapper.resetUserPassword(userId, newPwd));
    }

    /**
     * 根据用户id获取所有网店列表
     *
     * @param userId
     * @return
     */
    public ResultUtil getAllShopList(Long userId) {
        if (null == userId || 0 > userId) {
            return ResultUtil.error("userId错误");
        }
        return ResultUtil.success(userShopMapper.getAllShopListByUserId(userId));
    }

    /**
     * 根据用户id和网店id添加网店
     *
     * @param userId
     * @param shopIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil addShop(Long userId, Long[] shopIds) {
        if (null == userId || 0 > userId) {
            return ResultUtil.error("userId错误");
        }
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            userShopMapper.deleteShopListByUserId(userId);
            if (null == shopIds || 0 >= shopIds.length) {
                return ResultUtil.success();
            }
            userShopMapper.addShopListByUserIdAndShopIds(userId, shopIds);
        } catch (Exception e) {
            log.warn("用户添加网店---" + e);
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ResultUtil.error("系统错误，请重试");
        }
        return ResultUtil.success();
    }

    /**
     * 根据用户id获取网店列表
     *
     * @param userId
     * @return
     */
    public ResultUtil getShopList(Long userId) {
        if (null == userId || 0 > userId) {
            return ResultUtil.error("userId错误");
        }
        return ResultUtil.success(userShopMapper.getShopListByUserId(userId));
    }

    /**
     * 根据用户id获取网店和区域列表
     *
     * @param request
     * @return
     */
    public ResultUtil getShopAreaList(HttpServletRequest request) {
        Long userId = JwtTokenUtil.getUserIdByHttpServletRequest(request);
        List<Map<String, Object>> shopListByUserId = userShopMapper.getShopListByUserId(userId);
        if (null == shopListByUserId || 0 >= shopListByUserId.size()) {
            return ResultUtil.error("您还没有网店，请联系管理员添加");
        }
        for (Map<String, Object> map : shopListByUserId) {
            map.put("area", shopAreaMapper.getAreaListByShopId(Long.valueOf(map.get("id").toString())));
        }
        return ResultUtil.success(shopListByUserId);
    }
}
