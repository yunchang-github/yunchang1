package com.weiziplus.springboot.service.shop;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.shop.ExchangeRateMapper;
import com.weiziplus.springboot.models.ExchangeRate;
import com.weiziplus.springboot.utils.DateUtil;
import com.weiziplus.springboot.utils.PageUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wanglongwei
 * @data 2019/7/11 15:21
 */
@Service
public class ExchangeRateService extends BaseService {

    @Autowired
    ExchangeRateMapper mapper;

    /**
     * 获取分页列表数据
     *
     * @return
     */
    public ResultUtil getPageList(Integer pageNum, Integer pageSize, Long areaId, String currency, String createTime) {
        if (0 >= pageNum || 0 >= pageSize) {
            return ResultUtil.error("pageNum,pageSize错误");
        }
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(mapper.getList(areaId, currency, createTime));
        return ResultUtil.success(pageUtil);
    }

    /**
     * 新增
     *
     * @param exchangeRate
     * @return
     */
    public ResultUtil add(ExchangeRate exchangeRate) {
        if (null == exchangeRate.getAreaId() || 0 > exchangeRate.getAreaId()) {
            return ResultUtil.error("区域为空");
        }
        if (StringUtil.isBlank(exchangeRate.getCurrency())) {
            return ResultUtil.error("币种为空");
        }
        if (null == exchangeRate.getExchangeRate() || 0 > exchangeRate.getExchangeRate()) {
            return ResultUtil.error("汇率为空");
        }
        exchangeRate.setCreateTime(DateUtil.getDate());
        return ResultUtil.success(baseInsert(exchangeRate));
    }

    /**
     * 修改
     *
     * @param exchangeRate
     * @return
     */
    public ResultUtil update(ExchangeRate exchangeRate) {
        if (null == exchangeRate.getAreaId() || 0 > exchangeRate.getAreaId()) {
            return ResultUtil.error("区域为空");
        }
        if (StringUtil.isBlank(exchangeRate.getCurrency())) {
            return ResultUtil.error("币种为空");
        }
        if (null == exchangeRate.getExchangeRate() || 0 > exchangeRate.getExchangeRate()) {
            return ResultUtil.error("汇率为空");
        }
        exchangeRate.setCreateTime(null);
        return ResultUtil.success(baseUpdate(exchangeRate));
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
        return ResultUtil.success(baseDeleteByClassAndIds(ExchangeRate.class, ids));
    }

}
