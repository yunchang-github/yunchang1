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
import com.weiziplus.springboot.mapper.overseasWarehouseManagement.RemoveOrderRootDataMapper;
import com.weiziplus.springboot.mapper.overseasWarehouseManagement.USDongcangReceivesRemovalOrderProcessingDataMapper;
import com.weiziplus.springboot.mapper.sspa.SponsoredProductsAdvertisedProductReportMapper;
import com.weiziplus.springboot.models.AllOrder;
import com.weiziplus.springboot.models.InventoryAge;
import com.weiziplus.springboot.models.ManageFbaInventory;
import com.weiziplus.springboot.models.OverseasWarehousePlanInformationRegistration;
import com.weiziplus.springboot.models.RemoveOrderActionRecord;
import com.weiziplus.springboot.models.ReservedInventory;
import com.weiziplus.springboot.models.Shop;
import com.weiziplus.springboot.models.UsDongcangReceivesRemovalOrderProcessingData;
import com.weiziplus.springboot.utils.CryptoUtil;
import com.weiziplus.springboot.utils.DateUtil;
import com.weiziplus.springboot.utils.PageUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.utils.StringUtil;
import com.weiziplus.springboot.utils.redis.RedisUtil;

@Service
public class RemoveOrderRootDataService extends BaseService {

	@Autowired
	RemoveOrderRootDataMapper removeOrderRootDataMapper;
	
	
    /**
     * 获取分页数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ResultUtil getPageList(Integer pageNum, Integer pageSize,String shop,String area) {
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(removeOrderRootDataMapper.getList(shop,area));
        return ResultUtil.success(pageUtil);
    }

}
