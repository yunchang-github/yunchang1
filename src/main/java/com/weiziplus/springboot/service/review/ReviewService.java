package com.weiziplus.springboot.service.review;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.models.CrawlGoodsReview;
import com.weiziplus.springboot.utils.PageUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import org.springframework.stereotype.Service;

/**
 * @author wanglongwei
 * @data 2019/8/28 9:44
 */
@Service
public class ReviewService extends BaseService {

    /**
     * 获取分页数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ResultUtil getPageList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(baseFindAllByClass(CrawlGoodsReview.class));
        return ResultUtil.success(pageUtil);
    }
}
