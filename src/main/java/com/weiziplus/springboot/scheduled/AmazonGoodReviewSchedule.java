package com.weiziplus.springboot.scheduled;

import com.alibaba.fastjson.JSON;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.review.CrawlGoodsReviewMapper;
import com.weiziplus.springboot.models.CrawlGoodsReview;
import com.weiziplus.springboot.models.CrawlShop;
import com.weiziplus.springboot.models.DataGetErrorRecord;
import com.weiziplus.springboot.utils.DateUtil;
import com.weiziplus.springboot.utils.StringUtil;
import com.weiziplus.springboot.utils.ToolUtil;
import com.weiziplus.springboot.utils.amazon.AmazonGoodReviewUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wanglongwei
 * @data 2019/8/27 17:25
 */
@Component
@Configuration
@Service
@Slf4j
//@EnableScheduling  //有接口先不使用
public class AmazonGoodReviewSchedule extends BaseService {

    /**
     * 国家代码对应的url前缀
     */
    public final Map<String, String> BASE_URL_PREFIX = new HashMap<String, String>(7) {{
        put("US", "https://www.amazon.com");
        put("CA", "https://www.amazon.ca");
        put("DE", "https://www.amazon.de");
        put("UK", "https://www.amazon.co.uk");
        put("FR", "https://www.amazon.fr");
        put("ES", "https://www.amazon.es");
        put("IT", "https://www.amazon.it");
    }};

    @Autowired
    CrawlGoodsReviewMapper mapper;

    /**
     * 开始获取数据
     * 每周二上午8:01
     */
   // @Scheduled(cron = "0 1 8 ? * 2")
    @Transactional(rollbackFor = Exception.class)
    public void start() {
        log.info("********************获取商品评价定时任务开始******************");
        //出错重试次数
        int maxErrorNum = 5;
        StringBuffer errorMsg = new StringBuffer();
        //创建事务还原点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        for (int errorNum = 0; errorNum < maxErrorNum; errorNum++) {
            try {
                List<Object> insertList = new ArrayList<>();
                //获取所有的店铺、国家代码、asin信息
                List<Map<String, Object>> list = baseFindAllByClass(CrawlShop.class);
                for (Map<String, Object> map : list) {
                    String area = StringUtil.valueOf(map.get("area"));
                    String baseUrl = BASE_URL_PREFIX.get(area);
                    //国家代码异常
                    if (StringUtil.isBlank(baseUrl)) {
                        errorMsg.append("第").append(errorNum + 1).append("次出错，错误详情:")
                                .append("获取商品评价定时任务,没有找到国家代码为:").append(area)
                                .append("对应的商品详情路径前缀,详细信息:").append(JSON.toJSONString(map));
                        handleError(errorMsg);
                        return;
                    }
                    String shop = StringUtil.valueOf(map.get("shop"));
                    //网点异常
                    if (StringUtil.isBlank(shop)) {
                        errorMsg.append("第").append(errorNum + 1).append("次出错，错误详情:")
                                .append("获取商品评价定时任务,网店为空,详细信息:").append(JSON.toJSONString(map));
                        handleError(errorMsg);
                        return;
                    }
                    String asin = StringUtil.valueOf(map.get("asin"));
                    if (StringUtil.isBlank(asin)) {
                        String errMsg = "获取商品评价定时任务,asin为空,详细信息:" + JSON.toJSONString(map);
                        errorMsg.append("第").append(errorNum + 1).append("次出错，错误详情:").append(errMsg);
                        handleError(errorMsg);
                        return;
                    }
                    Map<String, Object> review = AmazonGoodReviewUtil.getNumAndStar(baseUrl, asin);
                    if (null == review) {
                        errorMsg.append("第").append(errorNum + 1).append("次出错，错误详情:")
                                .append("获取商品评价定时任务,没有获取到评论信息,详细信息:").append(JSON.toJSONString(map));
                        insertList = new ArrayList<>();
                        break;
                    }
                    CrawlGoodsReview goodsReview = new CrawlGoodsReview();
                    goodsReview.setShop(shop);
                    goodsReview.setArea(area);
                    goodsReview.setAsin(asin);
                    goodsReview.setStarDetail(StringUtil.valueOf(review.get("star")));
                    goodsReview.setNum(ToolUtil.valueOfInteger(StringUtil.valueOf(review.get("num"))));
                    CrawlGoodsReview oneInfoByShopAndAreaAndAsin = mapper.getOneInfoByShopAndAreaAndAsin(shop, area, asin);
                    if (null != oneInfoByShopAndAreaAndAsin && null != oneInfoByShopAndAreaAndAsin.getId()) {
                        goodsReview.setId(oneInfoByShopAndAreaAndAsin.getId());
                        baseUpdate(goodsReview);
                    } else {
                        goodsReview.setId(null);
                        insertList.add(goodsReview);
                    }
                    //随机休眠几秒
                    Thread.sleep(1 + Math.round(Math.random() * 7));
                }
                baseInsertList(insertList);
                //获取成功
                log.info("********************获取商品评价定时任务结束******************");
                return;
            } catch (Exception e) {
                //回滚事务
                TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                errorMsg.append("第").append(errorNum + 1).append("次出错，错误详情:").append(e);
            }
        }
        handleError(errorMsg);
        //获取失败
        log.info("********************获取商品评价定时任务结束******************");
    }

    /**
     * 处理错误
     *
     * @param errorMsg
     */
    private void handleError(StringBuffer errorMsg) {
        log.warn("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓获取商品评价定时任务出错↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        log.warn("××××××获取商品评价定时任务出错×××错误详情:" + errorMsg);
        log.warn("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑定时任务出错↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setShop("所有店铺");
        record.setArea("所有区域");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format1 = format.format(date);
        record.setDate(format1);
        //是否处理，0:未处理,1:已处理',
        record.setIsHandle(0);
        //类型:商品评论定时任务
        record.setType(3);
        record.setName("商品评论定时任务");
        record.setRemark("商品评论定时任务出错，出错时间：" + format1 + ",出错详情:" + errorMsg);
        record.setCreateTime(DateUtil.getFutureDateTime(0));
        baseInsert(record);
    }

    /**
     * 处理获取到的评论信息---需求改变，备份
     *
     * @param review
     */
    private void handleReview(Map<String, Object> review, String shop, String area, String asin) throws Exception {
        List<Object> insertList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : review.entrySet()) {
            String key = entry.getKey();
            if (null == entry.getValue()) {
                continue;
            }
            List<Map<String, Object>> valueList = (List<Map<String, Object>>) entry.getValue();
            for (Map<String, Object> map : valueList) {
                CrawlGoodsReview goodsReview = new CrawlGoodsReview();
                goodsReview.setShop(shop);
                goodsReview.setArea(area);
                goodsReview.setAsin(asin);
                goodsReview.setStar(ToolUtil.valueOfInteger(key));
                goodsReview.setAuthor(StringUtil.valueOf(map.get("author")));
                goodsReview.setReviewDate(DateUtil.usDateToDate(StringUtil.valueOf(map.get("date"))));
                goodsReview.setContent(StringUtil.valueOf(map.get("content")));
                goodsReview.setIsBuy(ToolUtil.valueOfInteger(StringUtil.valueOf(map.get("isBuy"))));
                insertList.add(goodsReview);
            }
        }
        baseInsertList(insertList);
    }
}
