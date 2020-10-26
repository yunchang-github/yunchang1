package com.weiziplus.springboot.scheduled.mwsapi;

import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.config.GlobalConfig;
import com.weiziplus.springboot.mapper.shop.ShopAreaMapper;
import com.weiziplus.springboot.models.Area;
import com.weiziplus.springboot.models.DataGetErrorRecord;
import com.weiziplus.springboot.models.Shop;
import com.weiziplus.springboot.utils.CryptoUtil;
import com.weiziplus.springboot.utils.DateUtil;
import com.weiziplus.springboot.utils.StringUtil;
import com.weiziplus.springboot.utils.amazon.AmazonMwsUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 亚马逊api定时任务----------定时任务必须try-catch
 * <p>
 * 秒 ：范围:0－59
 * 分 ：范围:0－59
 * 时 ：范围:0-23
 * 天（月） ：范围:1-31,但要注意一些特别的月份2月份没有只能1-28，有些月份没有31
 * 月 ：用0-11 或用字符串 “JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV and DEC” 表示
 * 天（周）：用1-7表示（1 ＝ 星期日）或用字符口串“SUN, MON, TUE, WED, THU, FRI and SAT”表示
 * 年：范围:1970－2099
 * <p>
 * “/”：表示为“每”，如“0/10”表示每隔10分钟执行一次,“0”表示为从“0”分开始, “3/20”表示表示每隔20分钟执行一次
 * “?”：只用于月与周，表示不指定值
 * “L”：只用于月与周，5L用在月表示为每月的最后第五天天；1L用在周表示每周的最后一天；
 * “W”：:表示有效工作日(周一到周五),只能出现在day-of-month，系统将在离指定日期的最近的有效工作日触发事件。例如：在 DayofMonth使用5W，如果5日是星期六，则将在最近的工作日：星期五，即4日触发。如果5日是星期天，则在6日(周一)触发；如果5日在星期一到星期五中的一天，则就在5日触发。另外一点，W的最近寻找不会跨过月份
 * “#”：用于确定每个月第几个星期几，只能出现在DayofMonth域。例如在4#2，表示某月的第二个星期三。
 * “*” 代表整个时间段。
 * <p>
 * 注意：每个元素可以是一个值(如6),一个连续区间(9-12),一个间隔时间(8-18/4)(/表示每隔4小时),一个列表(1,3,5),通配符。由于"月份中的日期"和"星期中的日期"这两个元素互斥的,必须要对其中一个设置‘?’
 *
 * <p>
 * 官方解释：
 * 0 0 3 * * ?         每天 3 点执行
 * 0 5 3 * * ?         每天 3 点 5 分执行
 * 0 5 3 ? * *         每天 3 点 5 分执行
 * 0 5/10 3 * * ?      每天 3 点 5 分，15 分，25 分，35 分，45 分，55 分这几个点执行
 * 0 10 3 ? * 1        每周星期天的 3 点10 分执行，注：1 表示星期天
 * 0 10 3 ? * 1#3      每个月的第三个星期的星期天 执行，#号只能出现在星期的位置
 *
 * @author wanglongwei
 * @data 2019/7/15 16:27
 */
@Component
@Configuration
@Service
@Slf4j
@EnableScheduling
public class AmazonMwsApiBaseSchedule extends BaseService {

    @Autowired
    ShopAreaMapper shopAreaMapper;

    /**
     * 店铺id
     */
    /*protected final Long SHOP_ID = GlobalConfig.SHOP_ID;*/

    /**
     * 数据库取出
     */
    protected final String COLUMN_SELLER_ID = "sellerId";
    protected final String COLUMN_MWS_AUTH_TOKEN = "mwsAuthToken";
    protected final String COLUMN_AWS_ACCESS_KEY_ID = "awsAccessKeyId";
    protected final String COLUMN_SECRET_KEY = "secretKey";
    protected final String COLUMN_SHOP_NAME = "shopName";

    /**
     * 亚马逊api请求参数字段
     */
    protected final String PARAM_AWS_ACCESS_KEY_ID = "AWSAccessKeyId";
    protected final String PARAM_MWS_AUTH_TOKEN = "MWSAuthToken";
    protected final String PARAM_SELLER_ID = "SellerId";
    protected final String PARAM_MARKETPLACE_ID_LIST_ID = "MarketplaceIdList.Id.1";
    public final static String PUBLIC_PARAM_MARKETPLACE_ID_LIST_ID = "MarketplaceIdList.Id.1";

    /**
     * MarketplaceIdList 请求参数不在日本和中国使用。
     */
    protected final List<String> NOT_MARKETPLACE_ID_LIST_COUNTRY_CODE = Arrays.asList("JP", "CN");

    /**
     * 欧洲五国国家代码
     */
    protected final List<String> FIVE_EUROPEAN_COUNTRIES_COUNTRY_CODE = Arrays.asList("DE", "ES", "FR", "GB", "IT");

    /**
     * 错误重试次数
     */
    protected final Integer MAX_ERROR_NUM = 3;

    /**
     * MwsApi定时任务任务名称---详情参考更新频率思维导图
     */
    protected final String[] TASK_NAME = {"预留库存", "管理亚马逊库存", "all order", "库龄", "每日库存记录"
            , "FBA customer returns", "月度仓储费", "长期仓储费", "费用预览", "已接收库存", "每月库存记录", "盘库", "移除"
            , "移除货件详情", "已完成订单", "payment"};

    /**
     * 定时任务开始之前校验检查shop和area信息
     *
     * @param taskName---如果传null---不是定时任务，不打印定时任务信息
     * @return 若返回null证明出错，正常返回shop和area的map
     */
    public Map<String, Object> beginScheduleTaskValidate(String taskName,Shop shop) {
        String warnStr = "";
//        if (!StringUtil.isBlank(taskName)) {
//            warnStr = "定时任务---获取---" + taskName + "失败---";
//        }
        Long SHOP_ID = shop.getId();
        Map<String, Object> shopMap = baseFindByClassAndId(Shop.class, SHOP_ID);
        if (null == shopMap) {
            log.warn(warnStr + "没有查询到shop信息，请检查shopId是否正确");
            return null;
        }
        String sellerId = String.valueOf(shopMap.get(COLUMN_SELLER_ID));
        if (StringUtil.isBlank(sellerId)) {
            log.warn(warnStr + "没有查询到sellerId信息，请检查shopId为" + SHOP_ID + "的sellerId信息是否正确");
            return null;
        }
        shopMap.put(COLUMN_SELLER_ID, CryptoUtil.decode(sellerId));
        String mwsAuthToken = String.valueOf(shopMap.get(COLUMN_MWS_AUTH_TOKEN));
        if (StringUtil.isBlank(mwsAuthToken)) {
            log.warn(warnStr + "没有查询到mwsAuthToken信息，请检查shopId为" + SHOP_ID + "的mwsAuthToken信息是否正确");
            return null;
        }
        shopMap.put(COLUMN_MWS_AUTH_TOKEN, CryptoUtil.decode(mwsAuthToken));
        String awsAccessKeyId = String.valueOf(shopMap.get(COLUMN_AWS_ACCESS_KEY_ID));
        if (StringUtil.isBlank(awsAccessKeyId)) {
            log.warn(warnStr + "没有查询到awsAccessKeyId信息，请检查shopId为" + SHOP_ID + "的awsAccessKeyId信息是否正确");
            return null;
        }
        shopMap.put(COLUMN_AWS_ACCESS_KEY_ID, CryptoUtil.decode(awsAccessKeyId));
        String secretKey = String.valueOf(shopMap.get(COLUMN_SECRET_KEY));
        if (StringUtil.isBlank(secretKey)) {
            log.warn(warnStr + "没有查询到secretKey信息，请检查shopId为" + SHOP_ID + "的secretKey信息是否正确");
            return null;
        }
        shopMap.put(COLUMN_SECRET_KEY, CryptoUtil.decode(secretKey));
        List<Area> areaListByShopId = shopAreaMapper.getAreaListByShopId(SHOP_ID);
        for (Area area : areaListByShopId) {
            //尝试去除关于EU欧洲五国的area对象---苏建东
            if ("EU".equals(area.getMwsCountryCode())){
                log.warn("该店铺:" + shop.getShopName() + "有欧洲五国的地区信息，略过"+ "任务名称为" + taskName );
                continue;
            }
            if (StringUtil.isBlank(area.getMwsEndPoint())) {
                log.warn(warnStr + "没有查询到区域名为" + area.getAreaName() + "区域亚马逊MWS 端点，请检查shopId为" + SHOP_ID + "的区域信息以及区域详情是否正确");
                return null;
            }
            if (StringUtil.isBlank(area.getMwsCountryCode())) {
                log.warn(warnStr + "没有查询到区域名为" + area.getAreaName() + "区域国家代码，请检查shopId为" + SHOP_ID + "的区域信息以及区域详情是否正确");
                return null;
            }
            if (StringUtil.isBlank(area.getMarketplaceId())) {
                log.warn(warnStr + "没有查询到区域名为" + area.getAreaName() + "区域MarketplaceId，请检查shopId为" + SHOP_ID + "的区域信息以及区域详情是否正确");
                return null;
            }
        }
        return new HashMap<String, Object>(2) {{
            put("shop", shopMap);
            put("area", areaListByShopId);
        }};
    }

    /**
     * 打印出错信息
     *
     * @param taskName
     * @param shopName
     * @param areaCountryCode
     * @param errorMsg
     */
    protected void logWarnErrorMsg(String taskName, String shopName, String areaCountryCode, StringBuffer errorMsg) {
        log.warn("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓定时任务出错↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        log.warn("×××××" + taskName + "×××定时任务出错×××" +
                "---店铺名称:" + shopName +
                "---区域国家代码:" + areaCountryCode +
                "×××××错误详情:" + errorMsg);
        log.warn("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑定时任务出错↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setShop(shopName);
        record.setArea(areaCountryCode);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format1 = format.format(date);
        record.setDate(format1);
        //是否处理，0:未处理,1:已处理',
        record.setIsHandle(0);
        //类型:亚马逊MwsApi定时任务
        record.setType(0);
        record.setName(taskName);
        record.setRemark("亚马逊MwsApi定时任务出错,任务名称:" + taskName
                + "出错的时间:" + format1
                + "错误详情:" + errorMsg);
        record.setCreateTime(DateUtil.getFutureDateTime(0));
        baseInsert(record);
    }
}
