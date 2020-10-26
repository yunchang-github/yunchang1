package com.weiziplus.springboot.service.logistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.mapper.logistics.LogisticsDeliveryMapper;
import com.weiziplus.springboot.models.ClaimFollowUp;
import com.weiziplus.springboot.utils.PageUtil;
import com.weiziplus.springboot.utils.ResultUtil;

@Service
public class LogisticsDeliveryService extends BaseService {

	@Autowired
	LogisticsDeliveryMapper logisticsDeliveryMapper;
	
	
	/**
	 * 店铺发货明细
	 * @param pageNum
	 * @param pageSize
	 * @param shop
	 * @param area
	 * @return
	 */
	public ResultUtil getShopDeliveryDetailsPageList(Integer pageNum, Integer pageSize, String shop, String area) {
		PageHelper.startPage(pageNum, pageSize);
	    PageUtil pageUtil = PageUtil.pageInfo(logisticsDeliveryMapper.getShopDeliveryDetailsPageList(shop,area));
	    return ResultUtil.success(pageUtil);
	}
	
	
	/**
	 * 时间轴
	 * @param pageNum
	 * @param pageSize
	 * @param shop
	 * @param area
	 * @return
	 */
	public ResultUtil getTimeLinePageList(Integer pageNum, Integer pageSize, String shop, String area) {
		PageHelper.startPage(pageNum, pageSize);
	    PageUtil pageUtil = PageUtil.pageInfo(logisticsDeliveryMapper.getTimeLinePageList(shop,area));
	    return ResultUtil.success(pageUtil);
	}

	/**
	 * 发货物流记录
	 * @param pageNum
	 * @param pageSize
	 * @param shop
	 * @param area
	 * @return
	 */
	public ResultUtil getShippingLogisticsRecordPageList(Integer pageNum, Integer pageSize, String shop, String area) {
		PageHelper.startPage(pageNum, pageSize);
	    PageUtil pageUtil = PageUtil.pageInfo(logisticsDeliveryMapper.getShippingLogisticsRecordPageList(shop,area));
	    return ResultUtil.success(pageUtil);
	}

	
	
	/**
	 * 索赔跟进
	 * @param pageNum
	 * @param pageSize
	 * @param shop
	 * @param area
	 * @return
	 */
	public ResultUtil getClaimFollowUpPageList(Integer pageNum, Integer pageSize, String shop, String area) {
		PageHelper.startPage(pageNum, pageSize);
	    PageUtil pageUtil = PageUtil.pageInfo(logisticsDeliveryMapper.getClaimFollowUpPageList(shop,area));
	    return ResultUtil.success(pageUtil);
	}


	/**
	 * 添加索赔跟进
	 * @param shop
	 * @param area
	 * @param shipmentId
	 * @param msku
	 * @param claimProgressRecord
	 * @param extendedFutures
	 * @param reasonDelay
	 * @param estimatedDelayTime
	 * @param claimResult
	 * @return
	 */
	public ResultUtil saveClaimFollowUp(String shop, String area, String shipmentId, String msku,
			String claimProgressRecord, String extendedFutures, String reasonDelay, String estimatedDelayTime,
			String claimResult) {
		
		ClaimFollowUp  claimFollowUp= logisticsDeliveryMapper.getClaimFollowUpByCondition(shop,area,shipmentId,msku);
		
		if(ObjectUtils.isEmpty(claimFollowUp)) {
			claimFollowUp = new ClaimFollowUp();
			claimFollowUp.setShop(shop);
			claimFollowUp.setArea(area);
			claimFollowUp.setShipmentId(shipmentId);
			claimFollowUp.setMsku(msku);
			claimFollowUp.setClaimProgressRecord(claimProgressRecord);
			claimFollowUp.setExtendedFutures(extendedFutures);
			claimFollowUp.setReasonDelay(reasonDelay);
			claimFollowUp.setEstimatedDelayTime(estimatedDelayTime);
			claimFollowUp.setClaimResult(claimResult);
	        return ResultUtil.success(baseInsert(claimFollowUp));
		}else {
			claimFollowUp.setClaimProgressRecord(claimProgressRecord);
			claimFollowUp.setExtendedFutures(extendedFutures);
			claimFollowUp.setReasonDelay(reasonDelay);
			claimFollowUp.setEstimatedDelayTime(estimatedDelayTime);
			claimFollowUp.setClaimResult(claimResult);
			return ResultUtil.success(baseUpdate(claimFollowUp));
		}
	}
}
