package com.weiziplus.springboot.service.sspa;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.mapper.sspa.SponsoredProductsAdvertisedProductReportMapper;
import com.weiziplus.springboot.utils.PageUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wanglongwei
 * @data 2019/7/8 9:28
 */
@Service
public class CampaignPerspectiveContrastService {

    @Autowired
    SponsoredProductsAdvertisedProductReportMapper sponsoredProductsAdvertisedProductReportMapper;

    /**
     * 获取分页数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ResultUtil getPageList(Integer pageNum, Integer pageSize, String monday) {
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(sponsoredProductsAdvertisedProductReportMapper.getCampaignPerspectiveContrastList(monday));
        return ResultUtil.success(pageUtil);
    }


    /**
     * 获取周一列表
     *
     * @return
     */
    public ResultUtil getMondayList() {
        return ResultUtil.success(sponsoredProductsAdvertisedProductReportMapper.getMondayList());
    }
}
