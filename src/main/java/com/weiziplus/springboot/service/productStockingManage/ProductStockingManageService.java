package com.weiziplus.springboot.service.productStockingManage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.productStockingManage.ProductStockingManageMapper;
import com.weiziplus.springboot.models.Beihuo;
import com.weiziplus.springboot.utils.PageUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.utils.ToolUtil;

@Service
public class ProductStockingManageService extends BaseService {

	@Autowired
	ProductStockingManageMapper productStockingManageMapper;


	/**
	 * 产品备货列表
	 * @param pageNum
	 * @param pageSize
	 * @param shop
	 * @param area
	 * @return
	 */
	public ResultUtil getProductStockingPageList(Integer pageNum, Integer pageSize, String shop, String area) {
		PageHelper.startPage(pageNum, pageSize);
	    PageUtil pageUtil = PageUtil.pageInfo(productStockingManageMapper.getProductStockingPageList(shop,area));

        Map map = getPageList(pageUtil.getList(),shop,area,pageUtil.getTotal());


	    return ResultUtil.success(map);
	}


	private Map getPageList(List list, String shop, String area, Long total) {

		//暂时
		 shop = "迈阳测试账号";
		 area = "US";
		//List<HashMap<String,Object>> returnList = new ArrayList<HashMap<String,Object>>();
		for (Object object : list) {
			Map<String, Object> map = ToolUtil.objectToMap(object);

			String msku = MapUtils.getString(map, "msku");
			String stocksku = MapUtils.getString(map, "stockSku");
			String asin = MapUtils.getString(map, "asin");
			MapUtils.safeAddToMap(map, "shop", shop);
			MapUtils.safeAddToMap(map, "area", area);
			Beihuo beihuo  = productStockingManageMapper.findRecordByCondition(map);//查找是否在备货表中存在

			if(ObjectUtils.isEmpty(beihuo)) {
				beihuo = new Beihuo();
				beihuo.setShop(shop);
				beihuo.setArea(area);
				beihuo.setAsin(asin);
				beihuo.setStocksku(stocksku);
				beihuo.setMsku(msku);
				beihuo.setHighSalesFluctuations((byte)0);
				beihuo.setIsSpecialMsku((byte)0);
				beihuo.setPercentagePast3Days(0.2);
				beihuo.setPercentagePastFourWeeks(0.2);
				beihuo.setPercentagePastThreeWeeks(0.2);
				beihuo.setPercentagePastTwoWeeks(0.2);
				beihuo.setPercentageRecentWeek(0.2);
				beihuo.setStockingDays((byte)60);
				baseInsert(beihuo);
			}
			MapUtils.safeAddToMap(map, "stockingDays", beihuo.getStockingDays());//备货天数

			//日均销量
			Double threeSalesAverage = MapUtils.getDouble(map, "threeSalesAverage",0.0);
			Double sevenOneSalesAverage = MapUtils.getDouble(map, "sevenOneSalesAverage",0.0);
			Double sevenTwoSalesAverage = MapUtils.getDouble(map, "sevenTwoSalesAverage",0.0);
			Double sevenThreeSalesAverage = MapUtils.getDouble(map, "sevenThreeSalesAverage",0.0);
			Double sevenFourSalesAverage = MapUtils.getDouble(map, "sevenFourSalesAverage",0.0);
			Double sale = threeSalesAverage*beihuo.getPercentagePast3Days()+sevenOneSalesAverage*beihuo.getPercentageRecentWeek()+sevenTwoSalesAverage*beihuo.getPercentagePastTwoWeeks()+sevenThreeSalesAverage*beihuo.getPercentagePastThreeWeeks()+sevenFourSalesAverage*beihuo.getPercentagePastFourWeeks();
			MapUtils.safeAddToMap(map, "saleAverage", sale);//日均销量



			Double keshoukucun = MapUtils.getDouble(map, "keshoukucun",0.0);//可售库存
			Double yuliukucun = MapUtils.getDouble(map, "yuliukucun",0.0);//预留库存
			Double rukutuzhong = MapUtils.getDouble(map, "rukutuzhong",0.0);//入库途中
			Double haiyunNum = MapUtils.getDouble(map, "haiyunNum",0.0);//海运
			Double bfnum  =MapUtils.getDouble(map, "bfnum",0.0);//报废数量
			Double meixihuanwaicangnum  =MapUtils.getDouble(map, "yuliukucun",0.0);//美西海外仓库存
			Double yichumeidonghaiwainum  =MapUtils.getDouble(map, "yuliukucun",0.0);//美东海外仓库存（移除）
			Double noremovemeidongnum  =MapUtils.getDouble(map, "yuliukucun",0.0);//美东仓库存（非移除）

			//现货可售天数
			double spotDaysAvailable = Math.ceil((keshoukucun+yuliukucun)/sale);
			MapUtils.safeAddToMap(map, "spotDaysAvailable", spotDaysAvailable);//现货可售天数
			//需求数量
			Integer requiredQuantity = null;
			if(beihuo.getRequiredQuantity()==null) { //如果表中需求数量为null,则查询

				//公式：需求数量=备货天数*日均销量-可售库存-预留库存-入库途中-海运+报废数量 -  美西海外仓库存-美东海外仓库存（移除）-美东仓库存（非移除）
				//		(若计算结果小于0，则值为0，计算结果四舍五入取整)

				Double temp = beihuo.getStockingDays()*sale-keshoukucun-yuliukucun-rukutuzhong-haiyunNum+bfnum-meixihuanwaicangnum-yichumeidonghaiwainum-noremovemeidongnum;
				if(temp<0) {
					requiredQuantity = 0;
				}else {
					requiredQuantity = Integer.parseInt(Math.round(temp)+"");
				}

			}else {
				requiredQuantity = beihuo.getRequiredQuantity();
			}
			MapUtils.safeAddToMap(map, "requiredQuantity", requiredQuantity);//需求数量
			//平均可售天数
			// （可售库存+预留库存+入库途中+海运+需求数量-报废数量）/日均销量
			double circumstanceNum = Math.ceil((keshoukucun+yuliukucun+rukutuzhong+haiyunNum+requiredQuantity-bfnum)/sale);
			MapUtils.safeAddToMap(map, "circumstanceNum", circumstanceNum);

			//特殊情况
			MapUtils.safeAddToMap(map, "specialCase", beihuo.getSpecialCase());
			//销量波动大标记
			MapUtils.safeAddToMap(map, "highSalesFluctuations", beihuo.getHighSalesFluctuations());
			//重点msku
			MapUtils.safeAddToMap(map, "isSpecialMsku", beihuo.getIsSpecialMsku());
			//品类
			String category = null;
			if(beihuo.getCategory()!=null) {
				category = beihuo.getCategory();
				MapUtils.safeAddToMap(map, "category", category);
			}

			//实际需求转化的采购量
			//=需求数量-本地库存-采购途中库存-原料SKU库存-原料SKU采购途中库存  （若计算结果小于0，则值为0）
			double purchasingQuantityActualDemandConversion = requiredQuantity - MapUtils.getDouble(map, "localStockQuantity",0.0) - MapUtils.getDouble(map, "caigoutuzhongkc",0.0) - MapUtils.getDouble(map, "rmsStockQuantity",0.0) - MapUtils.getDouble(map, "ylcaigoutuzhongkc",0.0);

			if(purchasingQuantityActualDemandConversion<0) {
				purchasingQuantityActualDemandConversion = 0;
			}else {
				purchasingQuantityActualDemandConversion = Math.ceil(purchasingQuantityActualDemandConversion);
			}

			MapUtils.safeAddToMap(map, "purchasingQuantityActualDemandConversion", purchasingQuantityActualDemandConversion);



		}
		 Map<String,Object> returnMap = new HashMap<String, Object>();

	        returnMap.put("list",list);
			returnMap.put("total",total);
			return returnMap;
	}
}
