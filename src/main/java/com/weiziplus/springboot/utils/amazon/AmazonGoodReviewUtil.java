package com.weiziplus.springboot.utils.amazon;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/8/27 11:04
 */
@Slf4j
public class AmazonGoodReviewUtil {

    public static void main(String[] args) throws Exception {
        Map<String, Object> b00J7YAEJM = getNumAndStar("https://www.amazon.com", "B07H56HYM4");
        System.out.println(JSON.toJSONString(b00J7YAEJM));
    }

    public static Map<String, Object> getNumAndStar(String url, String asin) throws Exception {
        //商品的评论页面
        String reviewsLink = url + "/product-reviews/" + asin;
        Connection connect = Jsoup.connect(reviewsLink);
        connect.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        connect.header("Accept", "  text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connect.header("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8,en-US;q=0.7,en;q=0.6");
        connect.header("Accept-Charset", "  GB2312,utf-8;q=0.7,*;q=0.7");
        connect.header("Connection", "keep-alive");
        connect.header("Accept-Encoding", "gzip, deflate");
        Document indexDocument = null;
        try {
            indexDocument = connect.get();
        } catch (Exception e) {
            log.warn("爬取页面出错，可能是asin不正确，详情:" + e);
            //返回map，该次爬取有效
            return null;
        }
        Map<String, Object> result = new HashMap<>(2);
        //数量
        Elements numElements = indexDocument.select("#cm_cr-product_info div.averageStarRatingIconAndCount div.a-col-right span.totalReviewCount");
        int num = 0;
        if (null != numElements) {
            String replace = numElements.get(0).text().replace(",", "");
            num = Integer.parseInt(replace);
        }
        result.put("num", num);
        //星级
        Elements starElements = indexDocument.select("#cm_cr-product_info div.averageStarRatingNumerical span.arp-rating-out-of-text");
        String star = "";
        if (null != starElements) {
            star = starElements.get(0).text();
        }
        result.put("star", star);
        return result;
    }

    /**
     * 获取商品评论信息
     *
     * @param url
     * @param asin
     * @throws Exception
     */
    public static Map<String, Object> getReview(String url, String asin) throws Exception {
        Map<String, Object> result = new HashMap<>(5);
        //商品的评论页面
        String reviewsLink = url + "/product-reviews/" + asin;
        Connection connect = Jsoup.connect(reviewsLink);
        connect.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        connect.header("Accept", "  text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connect.header("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8,en-US;q=0.7,en;q=0.6");
        connect.header("Accept-Charset", "  GB2312,utf-8;q=0.7,*;q=0.7");
        connect.header("Connection", "keep-alive");
        connect.header("Accept-Encoding", "gzip, deflate");
        Document indexDocument = null;
        try {
            indexDocument = connect.get();
        } catch (Exception e) {
            log.warn("本次爬取有效---爬取页面出错，可能是asin不正确，详情:" + e);
            //返回map，该次爬取有效
            return result;
        }
        //五个评论等级
        Elements trElements = indexDocument.select("#histogramTable > tbody tr");
        for (Element trElement : trElements) {
            //获取等级跳转a标签
            Elements aElements = trElement.children().select("td a");
            if (null == aElements || 0 >= aElements.size()) {
                log.warn("该等级没有评价");
                continue;
            }
            List<Map<String, Object>> reviewList = new ArrayList<>();
            //获取每个评价等级url
            String href = aElements.get(0).attr("abs:href");
            List<Map<String, Object>> list = getReviewList(href, reviewList);
            //获取当前的等级，示例：1,2,3,4,5
            String substring = aElements.text().substring(0, 1);
            result.put(substring, list);
        }
        return result;
    }

    /**
     * 获取评论的数据
     *
     * @param href
     * @param reviewList
     * @return
     * @throws Exception
     */
    private static List<Map<String, Object>> getReviewList(String href, List<Map<String, Object>> reviewList) throws Exception {
        Thread.sleep(1 + Math.round(Math.random() * 7));
        int starIndex = href.indexOf("_star");
        StringBuffer stringBuffer = new StringBuffer(href);
        //设置排序为Most recent
        stringBuffer.insert(starIndex + 5, "&sortBy=recent");
        //进入评论详情页面
        Connection connect = Jsoup.connect(String.valueOf(stringBuffer));
        connect.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        connect.header("Accept", "  text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connect.header("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8,en-US;q=0.7,en;q=0.6");
        connect.header("Accept-Charset", "  GB2312,utf-8;q=0.7,*;q=0.7");
        connect.header("Connection", "keep-alive");
        connect.header("Accept-Encoding", "gzip, deflate");
        Document document = connect.get();
        Elements select = document.select("#cm_cr-review_list > div");
        for (Element s : select) {
            String author = s.children().select("div.a-profile-content > span.a-profile-name").text();
            String date = s.children().select("span.review-date").text();
            String content = s.children().select("div.review-data > span.review-text-content > span").text();
            Elements verifiedPurchaseElement = s.children()
                    .select("div.review-format-strip > span.a-declarative > a.a-link-normal > span.a-size-mini.a-color-state.a-text-bold");
            boolean isBuyFlag = false;
            if (null != verifiedPurchaseElement && 0 < verifiedPurchaseElement.size()) {
                isBuyFlag = true;
            }
            //是否购买,0:未购买,1:已购买
            int isBuy = isBuyFlag ? 1 : 0;
            log.warn("评论人：" + author + "日期:" + date + "内容:" + content);
            reviewList.add(new HashMap<String, Object>(4) {{
                put("author", author);
                put("date", date);
                put("content", content);
                put("isBuy", isBuy);
            }});
        }
        //#cm_cr-pagination_bar > ul > li.a-last
        Elements nextPage = document.select("#cm_cr-pagination_bar > ul > li.a-last");
        //如果没有下一页按钮，跳过
        if (null == nextPage || 0 >= nextPage.size()) {
            return reviewList;
        }
        boolean nextPageDisabled = nextPage.hasClass("a-disabled");
        //如果下一页按钮禁用，跳过
        if (nextPageDisabled) {
            return reviewList;
        }
        //下一页按钮a标签
        Elements a = nextPage.get(0).children().select("a");
        if (null == a || 0 >= a.size()) {
            return reviewList;
        }
        String nextUrl = a.get(0).attr("abs:href");
        return getReviewList(nextUrl, reviewList);
    }

}
