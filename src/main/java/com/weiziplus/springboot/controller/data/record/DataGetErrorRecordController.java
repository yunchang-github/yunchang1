package com.weiziplus.springboot.controller.data.record;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.interceptor.AuthTokenIgnore;
import com.weiziplus.springboot.models.DataGetErrorRecord;
import com.weiziplus.springboot.models.SalesAndTraffic;
import com.weiziplus.springboot.service.data.record.DataGetErrorRecordMwsApiScheduleService;
import com.weiziplus.springboot.service.data.record.DataGetErrorRecordService;
import com.weiziplus.springboot.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 数据获取失败错误记录
 *
 * @author wanglongwei
 * @data 2019/8/21 15:44
 */
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/data/record/dataGetErrorRecord")
public class DataGetErrorRecordController {

    @Autowired
    DataGetErrorRecordService service;

    @Autowired
    DataGetErrorRecordMwsApiScheduleService mwsApiScheduleService;

    /**
     * 获取分页列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/getPageList")
    public ResultUtil getPageList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "shop", required = false) String shop,
            @RequestParam(value = "area", required = false) String area,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "isHandle", required = false) Integer isHandle,
            @RequestParam(value = "createTime", required = false) String createTime) {
        return service.getPageList(pageNum, pageSize, shop, area, date, type, isHandle, createTime);
    }

    /**
     * 新增广告错误记录
     *
     * @param date
     * @param shop
     * @param area
     * @param remark
     * @param type
     * @return
     */
    @AuthTokenIgnore
    @DeleteMapping("/chromeAddRecord")
    public ResultUtil chromeAddRecord(String date, String shop, String area, String name, String remark, Integer type) {
        return service.chromeAddRecord(date, shop, area, name, remark, type);
    }

    /**
     * 修复销售与访问量（Sales and Traffic）
     *
     * @param salesAndTraffic
     * @param dataGetErrorRecord
     * @return
     */
    @PostMapping("/repairErrorDataSalesAndTraffic")
    public ResultUtil repairErrorDataSalesAndTraffic(SalesAndTraffic salesAndTraffic, DataGetErrorRecord dataGetErrorRecord) {
        return service.repairErrorDataSalesAndTraffic(salesAndTraffic, dataGetErrorRecord);
    }

    /**
     * 修复父商品详情页面上的销售量与访问量
     *
     * @param file
     * @return
     */
    @PostMapping("/repairErrorDataDetailSalesTrafficByParentItem")
    public ResultUtil repairErrorDataDetailSalesTrafficByParentItem(MultipartFile file, DataGetErrorRecord dataGetErrorRecord) {
        return service.repairErrorDataDetailSalesTrafficByParentItem(file, dataGetErrorRecord);
    }

    /**
     * 修复子商品详情页面上的销售量与访问量
     *
     * @param file
     * @return
     */
    @PostMapping("/repairErrorDataDetailSalesTrafficByChildItem")
    public ResultUtil repairErrorDataDetailSalesTrafficByChildItem(MultipartFile file, DataGetErrorRecord dataGetErrorRecord) {
        return service.repairErrorDataDetailSalesTrafficByChildItem(file, dataGetErrorRecord);
    }

    /**
     * 修复亚马逊MwsApi定时任务
     *
     * @param dataGetErrorRecord
     * @return
     */
    @PostMapping("/repairErrorDataMwsApiSchedule")
    public ResultUtil repairErrorDataMwsApi(DataGetErrorRecord dataGetErrorRecord) {
        return mwsApiScheduleService.repairErrorDataMwsApi(dataGetErrorRecord);
    }

    /**
     * 修复亚马逊广告api定时任务
     *
     * @param dataGetErrorRecord
     * @return
     */
    @PostMapping("/repairErrorDataAdvertApiSchedule")
    public ResultUtil repairErrorDataAdvertApiSchedule(DataGetErrorRecord dataGetErrorRecord) {
        return service.repairErrorDataAdvertApiSchedule(dataGetErrorRecord);
    }

    /**
     * 修复获取商品评价定时任务结束
     *
     * @param dataGetErrorRecord
     * @return
     */
    @PostMapping("/repairErrorDataReviewSchedule")
    public ResultUtil repairErrorDataReviewSchedule(DataGetErrorRecord dataGetErrorRecord) {
        return service.repairErrorDataReviewSchedule(dataGetErrorRecord);
    }

    /**
     * 修复马帮api定时任务
     *
     * @param dataGetErrorRecord
     * @return
     */
    @PostMapping("/repairErrorDataCaravanApiSchedule")
    public ResultUtil repairErrorDataCaravanApiSchedule(DataGetErrorRecord dataGetErrorRecord) {
        return service.repairErrorDataCaravanApiSchedule(dataGetErrorRecord);
    }
}
