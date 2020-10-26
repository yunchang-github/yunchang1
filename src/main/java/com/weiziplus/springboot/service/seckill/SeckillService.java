package com.weiziplus.springboot.service.seckill;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.seckill.SeckillItemMapper;
import com.weiziplus.springboot.models.Seckill;
import com.weiziplus.springboot.models.SeckillItem;
import com.weiziplus.springboot.utils.PageUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wanglongwei
 * @data 2019/8/29 16:00
 */
@Service
public class SeckillService extends BaseService {

    @Autowired
    SeckillItemMapper seckillItemMapper;

    /**
     * 获取分页数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ResultUtil getPageList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(baseFindAllByClass(Seckill.class));
        return ResultUtil.success(pageUtil);
    }

    /**
     * 根据id获取详情
     *
     * @param id
     * @return
     */
    public ResultUtil getItemList(String id) {
        if (StringUtil.isBlank(id)) {
            return ResultUtil.error("id错误");
        }
        List<SeckillItem> itemListBySeckillId = seckillItemMapper.getItemListBySeckillId(id);
        return ResultUtil.success(itemListBySeckillId);
    }
}
