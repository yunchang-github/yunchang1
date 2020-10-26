package com.weiziplus.springboot.service.shop;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.shop.*;
import com.weiziplus.springboot.models.*;
import com.weiziplus.springboot.models.DO.AuthorizationDO;
import com.weiziplus.springboot.scheduled.amazonAdvert.AmazonAdvertOriginalDataSchedule;
import com.weiziplus.springboot.utils.*;
import com.weiziplus.springboot.utils.amazon.AdvertMonthOriginalDataUtil;
import com.weiziplus.springboot.mapper.shop.ShopAreaMapper;
import com.weiziplus.springboot.mapper.shop.ShopMapper;
import com.weiziplus.springboot.mapper.shop.UserShopMapper;
import com.weiziplus.springboot.utils.amazon.AmazonAdvertApiUtil;
import com.weiziplus.springboot.utils.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/11 9:45
 */
@Slf4j
@Service
public class ShopService extends BaseService {

    @Autowired
    ShopMapper shopMapper;

    @Autowired
    ShopAreaMapper shopAreaMapper;

    @Autowired
    UserShopMapper userShopMapper;

    @Autowired
    AreaMapper areaMapper;

    @Autowired
    ProfileMapper profileMapper;

    @Autowired
    AmazonAdvertOriginalDataSchedule amazonAdvertOriginalDataSchedule;

    @Autowired
    AdvertMonthOriginalDataUtil advertMonthOriginalDataUtil;

    @Autowired
    RefreshTokenMapper refreshTokenMapper;
    /**
     * 基准redis的key
     */
    private final String BASE_REDIS_KEY = "pc:service:ShopService:";

    /**
     * 获取分页列表数据
     *
     * @return
     */
    public ResultUtil getPageList(Integer pageNum, Integer pageSize, String name, String createTime) {
        if (0 >= pageNum || 0 >= pageSize) {
            return ResultUtil.error("pageNum,pageSize错误");
        }
        String key = BASE_REDIS_KEY + "getPageList:pageNum_" + pageNum + "pageSize_" + pageSize + "name_" + name + "createTime_" + createTime;
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ResultUtil.success(object);
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Shop> list = shopMapper.getList(name, createTime);
        for (Shop shop : list) {
            shop.setShopAccount(CryptoUtil.decode(shop.getShopAccount()));
            shop.setSellerId(CryptoUtil.decode(shop.getSellerId()));
            shop.setMwsAuthToken(CryptoUtil.decode(shop.getMwsAuthToken()));
            shop.setAwsAccessKeyId(CryptoUtil.decode(shop.getAwsAccessKeyId()));
            shop.setSecretKey(CryptoUtil.decode(shop.getSecretKey()));
        }
        PageUtil pageUtil = PageUtil.pageInfo(list);
        RedisUtil.set(key, pageUtil);
        return ResultUtil.success(pageUtil);
    }

    /**
     * 获取网点列表
     *
     * @return
     */
    public ResultUtil getNameAndIdList() {
        String key = BASE_REDIS_KEY + "getNameAndIdList";
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ResultUtil.success(object);
        }
        List<Shop> allList = shopMapper.getAllList();
        for (Shop shop : allList) {
            shop.setAreas(shopAreaMapper.getAreaListByShopId(shop.getId()));
        }
        RedisUtil.set(key, allList);
        return ResultUtil.success(allList);
    }

    /**
     * 新增
     *
     * @param shop
     * @return
     */
    public ResultUtil add(Shop shop) {
        if (StringUtil.isBlank(shop.getShopName())) {
            return ResultUtil.error("名称为空");
        }
       /* if (StringUtil.isBlank(shop.getShopAccount())) {
            return ResultUtil.error("账号为空");
        }
        if (StringUtil.isBlank(shop.getShopPassword())) {
            return ResultUtil.error("密码为空");
        }*/
        Shop oneInfoByName = shopMapper.getOneInfoByName(shop.getShopName());
        if (null != oneInfoByName && null != oneInfoByName.getId()) {
            return ResultUtil.error("名称已存在");
        }
        Shop oneInfoByName1 = shopMapper.getOneInfoBySellerId(CryptoUtil.decode(shop.getSellerId()));
        if (null != oneInfoByName1 && null != oneInfoByName1.getId()) {
            return ResultUtil.error("卖家ID已存在");
        }
        //对账号等敏感信息加密
        shop.setShopAccount(CryptoUtil.encode(shop.getShopAccount()));
        shop.setShopPassword(CryptoUtil.encode(shop.getShopPassword()));
        shop.setSellerId(CryptoUtil.encode(shop.getSellerId()));
        shop.setMwsAuthToken(CryptoUtil.encode(shop.getMwsAuthToken()));
        shop.setAwsAccessKeyId(CryptoUtil.encode(shop.getAwsAccessKeyId()));
        shop.setSecretKey(CryptoUtil.encode(shop.getSecretKey()));
        shop.setCreateTime(DateUtil.getDate());
        baseInsert(shop);
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param shop
     * @return
     */
    public ResultUtil update(Shop shop) {
        if (StringUtil.isBlank(shop.getShopName())) {
            return ResultUtil.error("名称为空");
        }
      /*  if (StringUtil.isBlank(shop.getShopAccount())) {
            return ResultUtil.error("账号为空");
        }*/
        Shop oneInfoByName = shopMapper.getOneInfoByName(shop.getShopName());
        if (null != oneInfoByName && null != oneInfoByName.getId() && !shop.getId().equals(oneInfoByName.getId())) {
            return ResultUtil.error("名称已存在");
        }
        Shop oneInfoByName1 = shopMapper.getOneInfoBySellerId(CryptoUtil.decode(shop.getSellerId()));
        if (null != oneInfoByName1 && null != oneInfoByName1.getId() && !shop.getId().equals(oneInfoByName1.getId())) {
            return ResultUtil.error("卖家ID已存在");
        }
        //对账号等敏感信息加密
        shop.setShopAccount(CryptoUtil.encode(shop.getShopAccount()));
        shop.setSellerId(CryptoUtil.encode(shop.getSellerId()));
        shop.setMwsAuthToken(CryptoUtil.encode(shop.getMwsAuthToken()));
        shop.setAwsAccessKeyId(CryptoUtil.encode(shop.getAwsAccessKeyId()));
        shop.setSecretKey(CryptoUtil.encode(shop.getSecretKey()));
        shop.setShopPassword(null);
        shop.setCreateTime(null);
        baseUpdate(shop);
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
            UserShop oneInfoByShopId = userShopMapper.getOneInfoByShopId(id);
            if (null != oneInfoByShopId && null != oneInfoByShopId.getId()) {
                return ResultUtil.error("网店名下有用户信息");
            }
        }
        baseDeleteByClassAndIds(Shop.class, ids);
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 根据网店id获取区域信息
     *
     * @param shopId
     * @return
     */
    public ResultUtil getAreaList(Long shopId) {
        if (null == shopId || 0 > shopId) {
            return ResultUtil.error("shopId错误");
        }
        return ResultUtil.success(shopAreaMapper.getAreaListByShopId(shopId));
    }

    /**
     * 根据网店id获取所有区域信息
     *
     * @param shopId
     * @return
     */
    public ResultUtil getAllAreaList(Long shopId) {
        if (null == shopId || 0 > shopId) {
            return ResultUtil.error("shopId错误");
        }
        return ResultUtil.success(shopAreaMapper.getAllAreaListByShopId(shopId));
    }

    /**
     * 更新网店区域列表
     *
     * @param shopId
     * @param areaIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil updateAreaList(Long shopId, Long[] areaIds) {
        if (null == shopId || 0 > shopId) {
            return ResultUtil.error("shopId为空");
        }
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            shopAreaMapper.deleteAreaListByShopId(shopId);
            if (null == areaIds || 0 >= areaIds.length) {
                return ResultUtil.success();
            }
            shopAreaMapper.addAreaListByShopIdAndAreaIds(shopId, areaIds);
            Shop shop = shopMapper.getOneInfoByShopId(shopId);
            String sellerId = shop.getSellerId();
            List<RefreshTokenDO> refreshTokenDOList = refreshTokenMapper.selectRefreshTokensBySellerId(sellerId);
            //先将该店铺原来有的ProfileID数据删除，之后根据选择的区域来添加对应地区的ProfileID
            profileMapper.delShopAreaProfileId(shopId);
            for (RefreshTokenDO refreshTokenDO : refreshTokenDOList) {
                AmazonAdvertApiUtil.setAccessTokenToRedis(refreshTokenDO);
                //去掉其中type不是seller的profiles
                List<Map<String, Object>> originalProfiles = AmazonAdvertApiUtil.getProfiles(refreshTokenDO);
                List<Map<String, Object>> processedProfiles = originalProfiles;
                for (int index = 0; index < originalProfiles.size(); index++) {
                    Map<String, Object> profile = originalProfiles.get(index);
                    Map<String, Object> accountInfo = (Map<String, Object>) profile.get("accountInfo");
                    String type = String.valueOf(accountInfo.get("type"));
                    if (!"seller".equals(type)) {
                        processedProfiles.remove(index);
                    }
                }
                //更新profileid
                for (int j = 0; j < processedProfiles.size(); j++) {
                    Map<String, Object> profilesMap = processedProfiles.get(j);
                    String profileId = String.valueOf(profilesMap.get("profileId"));
                    String countryCode = String.valueOf(profilesMap.get("countryCode"));
                    Area area = areaMapper.getAreaByCountryCode(countryCode);
                    //ShopAreaProfile shopAreaProfile = profileMapper.getDatasByShopIdAreaId(shopId, area.getId());
                    profileMapper.addShopAreaProfileId(shopMapper.getOneInfoByShopId(shopId).getId(), area.getId(), profileId, area.getRegionCode(), 0);
                }
            }
        } catch (Exception e) {
            log.warn("更新网店区域列表---" + e);
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ResultUtil.error("系统错误，请重试");
        }
        return ResultUtil.success();
    }

    /**
     * 根据网店id获取用户列表
     *
     * @param shopId
     * @return
     */
    public ResultUtil getUserList(Long shopId) {
        if (null == shopId || 0 > shopId) {
            return ResultUtil.error("shopId错误");
        }
        return ResultUtil.success(userShopMapper.getUserListByShopId(shopId));
    }

    /**
     * 授权
     *
     * @return
     */
    public ResultUtil saveAuthorization(AuthorizationDO authorizationDO) {
        String regionCode = authorizationDO.getRegionCode();
        String code = authorizationDO.getCode();
        if (StringUtil.isBlank(code)) {
            return ResultUtil.error("code不能为空");
        }
        if (regionCode == null || "".equals(regionCode)) {
            return ResultUtil.error("大区域不能为空");
        }
        //根据sellerID和regioncode去查找是否有对应的refreshtoken
        RefreshTokenDO refreshToken = refreshTokenMapper.selectRefreshTokenBySellerIdAndRegionCode(CryptoUtil.encode(authorizationDO.getSellerId()), regionCode);
        if (refreshToken != null) {
            return ResultUtil.error("该店已授过权");
        }
        Map map = AmazonAdvertApiUtil.setTokenByCode(regionCode, code);
        boolean flag = MapUtils.getBoolean(map, "flag", false);
        if (flag) {
//            //对账号等敏感信息加密
//            authorizationDO.setShopAccount(CryptoUtil.encode(authorizationDO.getShopAccount()));
//            authorizationDO.setSellerId(CryptoUtil.encode(authorizationDO.getSellerId()));
//            authorizationDO.setMwsAuthToken(CryptoUtil.encode(authorizationDO.getMwsAuthToken()));
//            authorizationDO.setAwsAccessKeyId(CryptoUtil.encode(authorizationDO.getAwsAccessKeyId()));
//            authorizationDO.setSecretKey(CryptoUtil.encode(authorizationDO.getSecretKey()));
//            authorizationDO.setShopPassword(null);
//            authorizationDO.setCreateTime(null);
//            shopMapper.insertShopInfo(authorizationDO);
            RefreshTokenDO refreshTokenDO = new RefreshTokenDO();
            refreshTokenDO.setSellerId(CryptoUtil.encode(authorizationDO.getSellerId()));
            refreshTokenDO.setRegionCode(regionCode);
            refreshTokenDO.setRefreshToken(MapUtils.getString(map, "refresh_token"));
            refreshTokenMapper.addRefreshToken(refreshTokenDO);
            RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
            return ResultUtil.success();
        } else {
            return ResultUtil.error("授权失败");
        }
    }

    public ResultUtil ttt(Long id) {
        System.out.println("测试方法启动");
        Shop shop = shopMapper.getOneInfoByShopId(id);
        List<ShopAreaProfile> list = profileMapper.getDatasByShopId(id);
        if (list == null || list.size() == 0) {
            addProfileId(shop);
        }
        advertMonthOriginalDataUtil.addAdvertMonthOriginalData(shop);
        return ResultUtil.success();
    }

    /*
     * 获取指定区域的店铺的profileId并存入数据库中
     * --苏建东
     * */
    public void addProfileId(Shop shop) {
        String sellerId = shop.getSellerId();
        List<RefreshTokenDO> refreshTokenDOList = refreshTokenMapper.selectRefreshTokensBySellerId(sellerId);
        for (RefreshTokenDO refreshTokenDO : refreshTokenDOList) {
            AmazonAdvertApiUtil.setAccessTokenToRedis(refreshTokenDO);
            List<Map<String, Object>> profiles = AmazonAdvertApiUtil.getProfiles(refreshTokenDO);
            for (int i = 0; i < profiles.size(); i++) {
                Map<String, Object> profilesMap = profiles.get(i);
                String profileId = String.valueOf(profilesMap.get("profileId"));
                String countryCode = String.valueOf(profilesMap.get("countryCode"));
                Area area = areaMapper.getAreaByCountryCode(countryCode);
                profileMapper.addShopAreaProfileId(shop.getId(), area.getId(), profileId, area.getRegionCode(), 0);
            }
        }
    }

    /**
     * 获取所有的店铺列表
     * --苏建东
     */
    public List<Shop> getAllShops() {
        return shopMapper.getAllList();
    }
}
