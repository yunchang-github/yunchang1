package com.weiziplus.springboot.service.sspa;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.sspa.DetailPageSalesAndTrafficByChildItemMapper;
import com.weiziplus.springboot.utils.PageUtil;
import com.weiziplus.springboot.utils.ResultUtil;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author wanglongwei
 * @data 2019/7/3 11:56
 */
@Service
public class AsinDiagnosisService extends BaseService {

    @Autowired
    DetailPageSalesAndTrafficByChildItemMapper detailPageSalesAndTrafficByChildItemMapper;

    /**
     * 获取分页数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
//    public ResultUtil getPageList(Integer pageNum, Integer pageSize) {
//        PageHelper.startPage(pageNum, pageSize);
//        PageUtil pageUtil = PageUtil.pageInfo(detailPageSalesAndTrafficByChildItemMapper.getList());
//        return ResultUtil.success(pageUtil);
//    }

    public ResultUtil getPageList(Map map) {
    	int pageNum = MapUtils.getInteger(map, "pageNum");
    	int pageSize = MapUtils.getInteger(map, "pageSize");
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(detailPageSalesAndTrafficByChildItemMapper.getList(map));
        return ResultUtil.success(pageUtil);
    }
    
}
