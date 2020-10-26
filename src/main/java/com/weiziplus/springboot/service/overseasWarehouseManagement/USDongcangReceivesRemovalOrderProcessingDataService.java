package com.weiziplus.springboot.service.overseasWarehouseManagement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.catalina.manager.util.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.advertisingInventoryReport.ChildItemSalesAndTrafficMapper;
import com.weiziplus.springboot.mapper.advertisingInventoryReport.InventoryAgeMapper;
import com.weiziplus.springboot.mapper.advertisingInventoryReport.ManageFbaInventoryMapper;
import com.weiziplus.springboot.mapper.advertisingInventoryReport.ReservedInventoryMapper;
import com.weiziplus.springboot.mapper.overseasWarehouseManagement.USDongcangReceivesRemovalOrderProcessingDataMapper;
import com.weiziplus.springboot.mapper.sspa.SponsoredProductsAdvertisedProductReportMapper;
import com.weiziplus.springboot.models.AllOrder;
import com.weiziplus.springboot.models.InventoryAge;
import com.weiziplus.springboot.models.ManageFbaInventory;
import com.weiziplus.springboot.models.Msku;
import com.weiziplus.springboot.models.OverseasWarehousePlanInformationRegistration;
import com.weiziplus.springboot.models.RemoveOrderActionRecord;
import com.weiziplus.springboot.models.ReservedInventory;
import com.weiziplus.springboot.models.Shop;
import com.weiziplus.springboot.models.UsDongcangReceivesRemovalOrderProcessingData;
import com.weiziplus.springboot.models.UsEastWarehouseData;
import com.weiziplus.springboot.utils.CryptoUtil;
import com.weiziplus.springboot.utils.DateUtil;
import com.weiziplus.springboot.utils.PageUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.utils.StringUtil;
import com.weiziplus.springboot.utils.redis.RedisUtil;

@Service
public class USDongcangReceivesRemovalOrderProcessingDataService extends BaseService {

    @Autowired
    USDongcangReceivesRemovalOrderProcessingDataMapper uSDongcangReceivesRemovalOrderProcessingDataMapper;


    /**
     * 获取分页数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ResultUtil getPageList(Integer pageNum, Integer pageSize, String shop, String area, String date) {
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(uSDongcangReceivesRemovalOrderProcessingDataMapper.getList(shop, area, date));
        return ResultUtil.success(pageUtil);
    }


    /**
     * 新增 根据trackingNumber
     *
     * @param shop
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil addByTrackingNumber(String shop, String area, String trackingNumbers) {
        //1  单个运单号  去登记表中查找  一般会有多个fnsku,根据运单号和fnsku 查出msku，处理方式，是否换标，新fnsku，分仓代号

        //2.多个运单号   去登记表中查找多个  同1
        if (StringUtil.isBlank(trackingNumbers)) {
            return ResultUtil.error("请输入运单号");
        }

        String[] trackingArr = trackingNumbers.trim().split(",");
        for (String trackingNumber : trackingArr) {
            //移除登记中查找
            List<RemoveOrderActionRecord> removeOrderActionRecordList = uSDongcangReceivesRemovalOrderProcessingDataMapper.findDataByTrackingNumber(shop, area, trackingNumber);
            if (removeOrderActionRecordList.size() == 0) {
                return ResultUtil.error("运单号：" + trackingNumber + "在移除登记中不存在");
            } else {

                for (RemoveOrderActionRecord rd : removeOrderActionRecordList) {
                    //移除登记中存在此运单号
                    UsDongcangReceivesRemovalOrderProcessingData usDongcangReceivesRemovalOrderProcessingData = new UsDongcangReceivesRemovalOrderProcessingData();
                    usDongcangReceivesRemovalOrderProcessingData.setShop(shop);
                    usDongcangReceivesRemovalOrderProcessingData.setArea(area);
                    usDongcangReceivesRemovalOrderProcessingData.setTrackingNumber(trackingNumbers);
                    usDongcangReceivesRemovalOrderProcessingData.setFnsku(rd.getFnsku());
                    usDongcangReceivesRemovalOrderProcessingData.setMsku(rd.getMsku());
                    usDongcangReceivesRemovalOrderProcessingData.setProcessingMethod(rd.getRefundType() == 0 ? "发走" : "存放");
                    usDongcangReceivesRemovalOrderProcessingData.setIsChangeStandard(rd.getIsChange() == 0 ? "换标" : "不换标");
                    usDongcangReceivesRemovalOrderProcessingData.setNewFnsku(rd.getNewFnsku());
                    usDongcangReceivesRemovalOrderProcessingData.setWarehouseCode(rd.getWarehouseCode());
                    baseInsert(usDongcangReceivesRemovalOrderProcessingData);
                }
            }

        }

        return ResultUtil.success();
    }

    /**
     * 新增 根据FNSKU
     *
     * @param shop
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil addByFnsku(String shop, String area, String fnskus) {

        if (StringUtil.isBlank(fnskus)) {
            return ResultUtil.error("请输入fnsku");
        }

        String[] fnskuArr = fnskus.trim().split(",");

        for (String fnsku : fnskuArr) {
            RemoveOrderActionRecord removeOrderActionRecord = uSDongcangReceivesRemovalOrderProcessingDataMapper.findDataByFnsku(shop, area, fnsku);
            UsDongcangReceivesRemovalOrderProcessingData usDongcangReceivesRemovalOrderProcessingData = new UsDongcangReceivesRemovalOrderProcessingData();
            usDongcangReceivesRemovalOrderProcessingData.setShop(shop);
            usDongcangReceivesRemovalOrderProcessingData.setArea(area);
            usDongcangReceivesRemovalOrderProcessingData.setFnsku(fnsku);
            usDongcangReceivesRemovalOrderProcessingData.setMsku(removeOrderActionRecord.getNewMsku());
            usDongcangReceivesRemovalOrderProcessingData.setProcessingMethod(removeOrderActionRecord.getRefundType() == 0 ? "发走" : "存放");
            usDongcangReceivesRemovalOrderProcessingData.setIsChangeStandard(removeOrderActionRecord.getIsChange() == 0 ? "换标" : "不换标");
            usDongcangReceivesRemovalOrderProcessingData.setNewFnsku(removeOrderActionRecord.getNewFnsku());
            usDongcangReceivesRemovalOrderProcessingData.setWarehouseCode(removeOrderActionRecord.getWarehouseCode());

            baseInsert(usDongcangReceivesRemovalOrderProcessingData);
        }

        return ResultUtil.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultUtil updateByTableContent(
            UsDongcangReceivesRemovalOrderProcessingData usDongcangReceivesRemovalOrderProcessingData) {
        String date = DateUtil.getDate();
        int flag = 0;
        String placeBoxNumber = usDongcangReceivesRemovalOrderProcessingData.getPlaceBoxNumber();
        if (StringUtil.notBlank(placeBoxNumber)) {
            //根据放置箱号匹配海外仓计划信息中的箱子状态
            //根据放置箱号匹配海外仓计划信息中的ShipmentID
            OverseasWarehousePlanInformationRegistration overseasWarehousePlanInformationRegistration = uSDongcangReceivesRemovalOrderProcessingDataMapper.findDataByPlaceBoxNumber(placeBoxNumber);

            if (StringUtils.isEmpty(overseasWarehousePlanInformationRegistration)) {
                return ResultUtil.error("海外仓计划信息中箱号:" + placeBoxNumber + "不存在");
            } else {
                overseasWarehousePlanInformationRegistration.setBoxStatus(overseasWarehousePlanInformationRegistration.getBoxStatus());
                overseasWarehousePlanInformationRegistration.setShipmentId(overseasWarehousePlanInformationRegistration.getShipmentId());
            }

        } else {
            usDongcangReceivesRemovalOrderProcessingData.setShipmentid(null);
            usDongcangReceivesRemovalOrderProcessingData.setBoxStatus(null);
        }
        String rcvTime = "";
        Boolean temp = false;
        if (StringUtil.notBlank(usDongcangReceivesRemovalOrderProcessingData.getReceiveTime())) {//如果签收时间不为空,则不改变
            rcvTime = usDongcangReceivesRemovalOrderProcessingData.getReceiveTime();
            usDongcangReceivesRemovalOrderProcessingData.setReceiveTime(null);//设置为空 则 后台不更新
        } else {
            rcvTime = date;
            usDongcangReceivesRemovalOrderProcessingData.setReceiveTime(date);
            temp = true;
        }


        if ("放置".equals(usDongcangReceivesRemovalOrderProcessingData.getProcessingMethod())) {
            UsEastWarehouseData usEastWarehouseData = new UsEastWarehouseData();
            String msku = usDongcangReceivesRemovalOrderProcessingData.getMsku();
            usEastWarehouseData.setMsku(msku);
            //fnsku
            //获取店信息
            String shop = "";
            String area = "";
            Msku mskuInfo = uSDongcangReceivesRemovalOrderProcessingDataMapper.findFnsku(shop, area, msku);
            usEastWarehouseData.setInventorySku(mskuInfo.getStocksku());

            usEastWarehouseData.setActualNumberOfReceipts(usDongcangReceivesRemovalOrderProcessingData.getActualNumberReceipts());
            usEastWarehouseData.setPlaceBoxNumber(placeBoxNumber);
            usEastWarehouseData.setReceiveTime(rcvTime);

            if (temp) {//插入
                flag += baseInsert(usEastWarehouseData);
            } else {  //修改
                flag += baseUpdate(usEastWarehouseData);
            }
        }

        flag += uSDongcangReceivesRemovalOrderProcessingDataMapper.updateByTableContent(usDongcangReceivesRemovalOrderProcessingData);

        if (flag > 0) {
            return ResultUtil.success();
        } else {
            return ResultUtil.error();
        }
    }

    /**
     * 更新实际签收数量
     *
     * @param id
     * @param value
     * @return
     */
    public ResultUtil updateActualNumberReceipts(Long id, Integer value) {
        if (null == id || 0 >= id) {
            return ResultUtil.error("id错误");
        }
        if (0 > value) {
            return ResultUtil.error("数量错误");
        }
        UsDongcangReceivesRemovalOrderProcessingData data = new UsDongcangReceivesRemovalOrderProcessingData();
        data.setId(id);
        data.setActualNumberReceipts(value);
        return ResultUtil.success(baseUpdate(data));
    }

    /**
     * 更新放置箱号
     *
     * @param id
     * @param value
     * @return
     */
    public ResultUtil updatePlaceBoxNumber(Long id, String value) {
        if (null == id || 0 >= id) {
            return ResultUtil.error("id错误");
        }
        if (StringUtil.isBlank(value)) {
            return ResultUtil.error("放置箱号错误");
        }
        UsDongcangReceivesRemovalOrderProcessingData data = new UsDongcangReceivesRemovalOrderProcessingData();
        data.setId(id);
        data.setPlaceBoxNumber(value);
        return ResultUtil.success(baseUpdate(data));
    }


}
