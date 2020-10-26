package com.weiziplus.springboot.service.replenishmentFlowArrangement;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.utils.PageUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class ReplenishmentFlowArrangementService {



    public ResultUtil getPageList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        //PageUtil pageUtil = PageUtil.pageInfo(productStockingManageMapper.getProductStockingPageList(shop,area));
        PageUtil pageUtil = PageUtil.pageInfo(new ArrayList());

       // Map map = getPageList(pageUtil.getList(),shop,area,pageUtil.getTotal());


        return ResultUtil.success(pageUtil);
    }

}
