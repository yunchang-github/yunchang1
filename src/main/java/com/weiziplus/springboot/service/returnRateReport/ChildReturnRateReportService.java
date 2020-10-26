package com.weiziplus.springboot.service.returnRateReport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.returnRateReport.ChildReturnRateReportMapper;
import com.weiziplus.springboot.utils.DateUtil;
import com.weiziplus.springboot.utils.PageUtil;
import com.weiziplus.springboot.utils.ResultUtil;

@Service
public class ChildReturnRateReportService extends BaseService {
    @Autowired
    ChildReturnRateReportMapper childReturnRateReportMapper;


    /**
     * 获取子分页数据
     *
     * @param pageNum
     * @param pageSize
     * @param date
     * @param date
     * @param asin
     * @return
     */

    public ResultUtil getPageChildList(Map maps) {
        Integer pageNum = MapUtils.getInteger(maps, "pageNum");
        Integer pageSize = MapUtils.getInteger(maps, "pageSize");
        String date = MapUtils.getString(maps, "date");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String firstDate = "";
        String lastDate = "";

        // String shop = "";//店铺+区域唯一标识后期需要加

        if (StringUtils.isEmpty(date)) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -1); // 默认当前天的前一天
            date = format.format(calendar.getTime());
        }
        try {
            Date dd = format.parse(date); // 选中的时间前一天或者默认时间前一天
            Calendar calendar = Calendar.getInstance();
            int nowMonth = calendar.get(Calendar.MONTH) + 1;
            calendar.setTime(dd);
            int paraMonth = calendar.get(Calendar.MONTH) + 1;
            firstDate = DateUtil.getFirstTimeMonth(dd);// 给定月份的第一天

            if (nowMonth == paraMonth) {// 属于和当前时间一个月份
                lastDate = date;
            } else {
                lastDate = DateUtil.getLastTimeMonth(dd);
            }
            maps.put("firstDate", firstDate);
            maps.put("lastDate", lastDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil
                .pageInfo(childReturnRateReportMapper.getChildPageList(maps));
        return ResultUtil.success(pageUtil);
    }


    /**
     * 获取父分页数据
     *
     * @param pageNum
     * @param pageSize
     * @param date
     * @param date
     * @param asin
     * @return
     */
    public ResultUtil getPageParentList(Map maps) {
        Integer pageNum = MapUtils.getInteger(maps, "pageNum");
        Integer pageSize = MapUtils.getInteger(maps, "pageSize");
        String date = MapUtils.getString(maps, "date");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String firstDate = "";
        String lastDate = "";

        // String shop = "";//店铺+区域唯一标识后期需要加

        if (StringUtils.isEmpty(date)) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -1); // 默认当前天的前一天
            date = format.format(calendar.getTime());
        }
        try {
            Date dd = format.parse(date); // 选中的时间前一天或者默认时间前一天
            Calendar calendar = Calendar.getInstance();
            int nowMonth = calendar.get(Calendar.MONTH) + 1;
            calendar.setTime(dd);
            int paraMonth = calendar.get(Calendar.MONTH) + 1;
            firstDate = DateUtil.getFirstTimeMonth(dd);// 给定月份的第一天

            if (nowMonth == paraMonth) {// 属于和当前时间一个月份
                lastDate = date;
            } else {
                lastDate = DateUtil.getLastTimeMonth(dd);
            }
            maps.put("firstDate", firstDate);
            maps.put("lastDate", lastDate);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil
                .pageInfo(childReturnRateReportMapper.getParentPageList(maps));

        return ResultUtil.success(pageUtil);
    }
}
