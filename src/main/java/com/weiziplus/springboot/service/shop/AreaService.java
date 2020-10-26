package com.weiziplus.springboot.service.shop;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.shop.AreaMapper;
import com.weiziplus.springboot.mapper.shop.ExchangeRateMapper;
import com.weiziplus.springboot.models.Area;
import com.weiziplus.springboot.models.ExchangeRate;
import com.weiziplus.springboot.models.Shop;
import com.weiziplus.springboot.utils.DateUtil;
import com.weiziplus.springboot.utils.PageUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.utils.StringUtil;
import com.weiziplus.springboot.utils.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/11 11:18
 */
@Service
public class AreaService extends BaseService {

    @Autowired
    AreaMapper mapper;

    @Autowired
    ExchangeRateMapper exchangeRateMapper;

    /**
     * 基准redis的key
     */
    private final String BASE_REDIS_KEY = "pc:service:AreaService:";

    /**
     * 获取分页列表数据
     *
     * @return
     */
    public ResultUtil getPageList(Integer pageNum, Integer pageSize, String name) {
        if (0 >= pageNum || 0 >= pageSize) {
            return ResultUtil.error("pageNum,pageSize错误");
        }
        String key = BASE_REDIS_KEY + "getPageList:pageNum_" + pageNum + "pageSize_" + pageSize + "name_" + name;
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ResultUtil.success(object);
        }
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(mapper.getList(name));
        RedisUtil.set(key, pageUtil);
        return ResultUtil.success(pageUtil);
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    public ResultUtil getList() {
        String key = BASE_REDIS_KEY + "getList";
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ResultUtil.success(object);
        }
        List<Map<String, Object>> list = baseFindAllByClass(Area.class);
        RedisUtil.set(key, list);
        return ResultUtil.success(list);
    }

    /**
     * 获取国家代码列表
     *
     * @return
     */
    public List<String> getMwsCountryCodeList() {
        String key = BASE_REDIS_KEY + "getMwsCountryCodeList";
        Object object = RedisUtil.get(key);
        if (null != object) {
            return (List<String>) object;
        }
        List<Map<String, Object>> list = baseFindAllByClass(Area.class);
        List<String> result = new ArrayList<>(list.size());
        for (Map<String, Object> map : list) {
            Object countryCode = map.get("countryCode");
            if (null == countryCode) {
                continue;
            }
            result.add(StringUtil.valueOf(countryCode));
        }
        RedisUtil.set(key, result);
        return result;
    }


    /**
     * 新增
     *
     * @param area
     * @return
     */
    public ResultUtil add(Area area) {
        if (StringUtil.isBlank(area.getAreaName())) {
            return ResultUtil.error("名称为空");
        }
        if (StringUtil.isBlank(area.getAdvertCountryCode())) {
            return ResultUtil.error("国家代码为空");
        }
        if (StringUtil.isBlank(area.getMwsEndPoint())) {
            return ResultUtil.error("mws端点为空");
        }
        if (StringUtil.isBlank(area.getMarketplaceId())) {
            return ResultUtil.error("MarketplaceId为空");
        }
        if (!getMwsCountryCodeList().contains(area.getAdvertCountryCode())) {
            return ResultUtil.error("国家代码错误");
        }
        Area oneInfoByName = mapper.getOneInfoByName(area.getAreaName());
        if (null != oneInfoByName && null != oneInfoByName.getId()) {
            return ResultUtil.error("名称已存在");
        }
        Area oneInfoByName1 = mapper.getOneInfoByMarketplaceId(area.getMarketplaceId());
        if (null != oneInfoByName1 && null != oneInfoByName1.getMarketplaceId()) {
            return ResultUtil.error("MarketplaceId已存在");
        }
        baseInsert(area);
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param area
     * @return
     */
    public ResultUtil update(Area area) {
        if (StringUtil.isBlank(area.getAreaName())) {
            return ResultUtil.error("名称为空");
        }
        if (StringUtil.isBlank(area.getAdvertCountryCode())) {
            return ResultUtil.error("国家代码为空");
        }
        if (StringUtil.isBlank(area.getMwsEndPoint())) {
            return ResultUtil.error("mws端点为空");
        }
        if (StringUtil.isBlank(area.getMarketplaceId())) {
            return ResultUtil.error("MarketplaceId为空");
        }
        if (!getMwsCountryCodeList().contains(area.getAdvertCountryCode())) {
            return ResultUtil.error("国家代码错误");
        }
        Area oneInfoByName = mapper.getOneInfoByName(area.getAreaName());
        if (null != oneInfoByName && null != oneInfoByName.getId() && !area.getId().equals(oneInfoByName.getId())) {
            return ResultUtil.error("名称已存在");
        }
        Area oneInfoByName1 = mapper.getOneInfoByMarketplaceId(area.getMarketplaceId());
        if (null != oneInfoByName1 && null != oneInfoByName1.getId() && !area.getId().equals(oneInfoByName1.getId())) {
            return ResultUtil.error("名称已存在");
        }
        baseUpdate(area);
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    public ResultUtil delete(Long[] ids) {
        if (null == ids || 0 >= ids.length) {
            return ResultUtil.error("ids为空");
        }
        for (Long id : ids) {
            ExchangeRate oneInfoByAreaId = exchangeRateMapper.getOneInfoByAreaId(id);
            if (null != oneInfoByAreaId && null != oneInfoByAreaId.getId()) {
                return ResultUtil.error("有区域存在汇率绑定，请先解除");
            }
        }
        baseDeleteByClassAndIds(Area.class, ids);
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }
}
