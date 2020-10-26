package com.weiziplus.springboot.service.system;

import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.config.GlobalConfig;
import com.weiziplus.springboot.mapper.system.SysFunctionMapper;
import com.weiziplus.springboot.mapper.system.SysRoleFunctionMapper;
import com.weiziplus.springboot.mapper.system.SysRoleMapper;
import com.weiziplus.springboot.mapper.system.SysUserMapper;
import com.weiziplus.springboot.models.SysRole;
import com.weiziplus.springboot.utils.DateUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.utils.ToolUtil;
import com.weiziplus.springboot.utils.ValidateUtil;
import com.weiziplus.springboot.utils.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wanglongwei
 * @data 2019/5/10 8:39
 */
@Slf4j
@Service
public class SysRoleService extends BaseService {

    @Autowired
    SysRoleMapper mapper;

    @Autowired
    SysFunctionMapper sysFunctionMapper;

    @Autowired
    SysRoleFunctionMapper sysRoleFunctionMapper;

    @Autowired
    SysUserMapper sysUserMapper;

    /**
     * 角色允许使用为0，禁止为1
     */
    public static final Integer ADMIN_ROLE_IS_STOP = 0;

    /**
     * 系统功能表中角色管理id为4
     */
    private static final Long SYS_FUNCTION_ROLE_ID = 4L;

    /**
     * SysRole基础redis的key
     */
    private static final String BASE_REDIS_KEY = "service:system:SysRoleService:";

    /**
     * 获取角色树形结构
     *
     * @return
     */
    public List<SysRole> getRoleTree() {
        String key = createRedisKey(BASE_REDIS_KEY + "getRoleTree");
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ToolUtil.objectOfList(object, SysRole.class);
        }
        List<SysRole> resultList = new ArrayList<>();
        //默认根节点为0
        List<SysRole> menuList = mapper.getRoleListByParentId(0L);
        for (SysRole sysRole : menuList) {
            sysRole.setChildren(findChildren(sysRole));
            resultList.add(sysRole);
        }
        RedisUtil.set(key, resultList);
        return resultList;
    }

    /**
     * 获取子级角色树形结构
     *
     * @param sysRole
     * @return
     */
    private List<SysRole> findChildren(SysRole sysRole) {
        List<SysRole> childrenList = new ArrayList<>();
        List<SysRole> menuList = mapper.getRoleListByParentId(sysRole.getId());
        for (SysRole sys : menuList) {
            sys.setChildren(findChildren(sys));
            childrenList.add(sys);
        }
        return childrenList;
    }

    /**
     * 获取角色列表
     *
     * @return
     */
    public List<SysRole> getRoleList() {
        String key = createRedisKey(BASE_REDIS_KEY + "getRoleList");
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ToolUtil.objectOfList(object, SysRole.class);
        }
        List<SysRole> roleList = mapper.getRoleList();
        RedisUtil.set(key, roleList);
        return roleList;
    }

    /**
     * 新增角色功能
     *
     * @param roleId
     * @param funIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil addRoleFun(Long roleId, Long[] funIds) {
        if (null == roleId || 0 >= roleId) {
            return ResultUtil.error("roleId不能为空");
        }
        if (GlobalConfig.SUPER_ADMIN_ROLE_ID.equals(roleId)) {
            boolean haveRoleId = false;
            for (Long id : funIds) {
                if (null == id || 0 > id) {
                    return ResultUtil.error("ids错误");
                }
                if (SYS_FUNCTION_ROLE_ID.equals(id)) {
                    haveRoleId = true;
                    break;
                }
            }
            if (!haveRoleId) {
                return ResultUtil.error("超级管理员:角色管理权限一定要添加啊(*/ω＼*)");
            }
        }
        RedisUtil.setExpireDeleteLikeKey(SysFunctionService.ROLE_FUNCTION_LIST_REDIS_KEY);
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            sysRoleFunctionMapper.deleteByRoleId(roleId);
            if (null == funIds || 0 >= funIds.length) {
                return ResultUtil.success();
            }
            sysRoleFunctionMapper.addRoleFunction(roleId, funIds);
        } catch (Exception e) {
            log.warn("系统用户角色更新失败---" + e);
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ResultUtil.error("系统错误，请重试");
        }
        RedisUtil.deleteLikeKey(SysFunctionService.ROLE_FUNCTION_LIST_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 新增角色
     *
     * @param sysRole
     * @return
     */
    public ResultUtil addRole(SysRole sysRole) {
        //中英文开头、数字、下划线
        if (ValidateUtil.notUsername(sysRole.getName())) {
            return ResultUtil.error("角色名不能包含特殊字符");
        }
        //因为超级管理员只有一个所以角色父级id从1开始
        if (null == sysRole.getParentId() || GlobalConfig.SUPER_ADMIN_ID > sysRole.getParentId()) {
            return ResultUtil.error("parentId不能为空");
        }
        SysRole role = mapper.getRoleInfoByName(sysRole.getName());
        if (null != role && null != role.getId()) {
            return ResultUtil.error("角色名已存在");
        }
        SysRole superRole = mapper.getInfoByRoleId(sysRole.getParentId());
        if (!ADMIN_ROLE_IS_STOP.equals(superRole.getIsStop())) {
            return ResultUtil.error("操作失败，父级处于禁用状态");
        }
        sysRole.setCreateTime(DateUtil.getFutureDateTime(0));
        RedisUtil.setExpireDeleteLikeKey(BASE_REDIS_KEY);
        baseInsert(sysRole);
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改角色
     *
     * @param sysRole
     * @return
     */
    public ResultUtil updateRole( SysRole sysRole) {
        //中英文开头、数字、下划线
        if (ValidateUtil.notUsername(sysRole.getName())) {
            return ResultUtil.error("角色名不能包含特殊字符");
        }
        if (GlobalConfig.SUPER_ADMIN_ROLE_ID.equals(sysRole.getId())) {
            return ResultUtil.error("不能修改超级管理员角色");
        }
        SysRole role = mapper.getRoleInfoByName(sysRole.getName());
        if (null != role && null != role.getId() && !role.getId().equals(sysRole.getId())) {
            return ResultUtil.error("角色名已存在");
        }
        if (!ADMIN_ROLE_IS_STOP.equals(sysRole.getIsStop())) {
            return ResultUtil.error("操作失败，角色处于禁用状态");
        }
        SysRole superRole = mapper.getInfoByRoleId(sysRole.getParentId());
        if (!ADMIN_ROLE_IS_STOP.equals(superRole.getIsStop())) {
            return ResultUtil.error("操作失败，父级处于禁用状态");
        }
        RedisUtil.setExpireDeleteLikeKey(BASE_REDIS_KEY);
        baseUpdate(sysRole);
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 删除角色
     *
     * @param roleId
     * @return
     */
    public ResultUtil deleteRole(Long roleId) {
        if (null == roleId || 0 >= roleId) {
            return ResultUtil.error("roleId错误");
        }
        if (GlobalConfig.SUPER_ADMIN_ROLE_ID.equals(roleId)) {
            return ResultUtil.error("不能删除超级管理员角色");
        }
        List<SysRole> list = mapper.getRoleListByParentId(roleId);
        if (null != list && 0 < list.size()) {
            return ResultUtil.error("当前角色存在下级");
        }
        RedisUtil.setExpireDeleteLikeKey(BASE_REDIS_KEY);
        RedisUtil.setExpireDeleteLikeKey(SysFunctionService.ROLE_FUNCTION_LIST_REDIS_KEY);
        baseDeleteByClassAndId(SysRole.class, roleId);
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        RedisUtil.deleteLikeKey(SysFunctionService.ROLE_FUNCTION_LIST_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 改变角色状态
     *
     * @param roleId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil changeRoleIsStop(Long roleId, Integer isStop) {
        if (null == roleId || 0 >= roleId) {
            return ResultUtil.error("id不能为空");
        }
        if (GlobalConfig.SUPER_ADMIN_ROLE_ID.equals(roleId)) {
            return ResultUtil.error("不能操作超级管理员");
        }
        if (null == isStop) {
            return ResultUtil.error("状态不能为空");
        }
        //判断是否启用
        if (ADMIN_ROLE_IS_STOP.equals(isStop)) {
            SysRole role = mapper.getInfoByRoleId(roleId);
            SysRole superRole = mapper.getInfoByRoleId(role.getParentId());
            if (!ADMIN_ROLE_IS_STOP.equals(superRole.getIsStop())) {
                return ResultUtil.error("父级当前处于禁用状态");
            }
        }
        RedisUtil.setExpireDeleteLikeKey(BASE_REDIS_KEY);
        RedisUtil.setExpireDeleteLikeKey(SysFunctionService.ROLE_FUNCTION_LIST_REDIS_KEY);
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            mapper.changeRoleIsStopByIdAndIsStop(roleId, isStop);
            for (SysRole sysRole : mapper.getRoleListByParentId(roleId)) {
                findChildrenChangeIsStop(sysRole.getId(), isStop);
            }
        } catch (Exception e) {
            log.warn("改变角色状态失败，详情:" + e);
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ResultUtil.error("系统错误，请重试");
        }
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        RedisUtil.deleteLikeKey(SysFunctionService.ROLE_FUNCTION_LIST_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改子级角色状态
     *
     * @param roleId
     * @param isStop
     */
    private void findChildrenChangeIsStop(Long roleId, Integer isStop) throws Exception {
        mapper.changeRoleIsStopByIdAndIsStop(roleId, isStop);
        for (SysRole sysRole : mapper.getRoleListByParentId(roleId)) {
            findChildrenChangeIsStop(sysRole.getId(), isStop);
        }
    }
}
