package com.weiziplus.springboot.service.sspa;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.sspa.SponsoredProductsAdvertisedProductReportMapper;
import com.weiziplus.springboot.utils.PageUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/1 16:09
 */
@Service
public class AsinPerspectiveService extends BaseService {

    @Autowired
    SponsoredProductsAdvertisedProductReportMapper sponsoredProductsAdvertisedProductReportMapper;

    /**
     * 获取分页数据
     *
     * @param pageNum
     * @param pageSize
     * @param date
     * @param campaignName
     * @param adGroupName
     * @param asin
     * @return
     */
    public ResultUtil getPageList(Integer pageNum, Integer pageSize, String month, String date, String campaignName, String adGroupName, String asin) {
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(sponsoredProductsAdvertisedProductReportMapper.getAsinPerspectivePageList(month, date, campaignName, adGroupName, asin));
        return ResultUtil.success(pageUtil);
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    public ResultUtil getList(Map map) {
        return ResultUtil.success(sponsoredProductsAdvertisedProductReportMapper.getAsinPerspectiveList(map));
    }

    /**
     * 获取年月列表
     *
     * @return
     */
    public ResultUtil getYearMonthList() {
        return ResultUtil.success(sponsoredProductsAdvertisedProductReportMapper.getYearMonthList());
    }

    /**
     * 获取Advertised ASIN
     *
     * @return
     */
//    public ResultUtil getAdvertisedAsin(Map map) {
//        return ResultUtil.success(sponsoredProductsAdvertisedProductReportMapper.getAdvertisedAsin(map));
//    }

    /**
     * 获取campaign_name
     *
     * @return
     * @param map
     */
    public ResultUtil getCampaignName(Map map) {
        return ResultUtil.success(sponsoredProductsAdvertisedProductReportMapper.getCampaignName(map));
    }

    /**
     * 获取ad_group_name
     *
     * @return
     */
    public ResultUtil getAdGroupName() {
        return ResultUtil.success(sponsoredProductsAdvertisedProductReportMapper.getAdGroupName());
    }

}
