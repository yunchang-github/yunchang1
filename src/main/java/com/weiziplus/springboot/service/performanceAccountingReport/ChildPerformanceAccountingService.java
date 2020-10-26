package com.weiziplus.springboot.service.performanceAccountingReport;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.advertisingInventoryReport.InventoryAgeMapper;
import com.weiziplus.springboot.mapper.advertisingInventoryReport.ManageFbaInventoryMapper;
import com.weiziplus.springboot.mapper.advertisingInventoryReport.ReservedInventoryMapper;
import com.weiziplus.springboot.mapper.performanceAccountingReport.ChildPerformanceAccountingMapper;
import com.weiziplus.springboot.mapper.sspa.SponsoredProductsAdvertisedProductReportMapper;
import com.weiziplus.springboot.utils.DateUtil;
import com.weiziplus.springboot.utils.PageUtil;
import com.weiziplus.springboot.utils.ResultUtil;

@Service
public class ChildPerformanceAccountingService extends BaseService {
	@Autowired
	ManageFbaInventoryMapper manageFbaInventoryMapper;
	@Autowired
	ReservedInventoryMapper reservedInventoryMapper;
	@Autowired
	InventoryAgeMapper inventoryAgeMapper;
	@Autowired
	SponsoredProductsAdvertisedProductReportMapper sponsoredProductsAdvertisedProductReportMapper;
	@Autowired
	ChildPerformanceAccountingMapper childPerformanceAccountingMapper;

	/**
	 * 获取子分页数据
	 * @param maps
	 * @return
	 */
	public ResultUtil getPageChildList(Map maps) {
		Integer pageNum = 	MapUtils.getInteger(maps,"pageNum");
		Integer pageSize = MapUtils.getInteger(maps,"pageSize");
		String date = MapUtils.getString(maps,"date");
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
			maps.put("firstDate",firstDate);
			maps.put("lastDate",lastDate);


		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PageHelper.startPage(pageNum, pageSize);
		PageUtil pageUtil = PageUtil
				.pageInfo(childPerformanceAccountingMapper.getChildPageList(maps));
		Map map = getChildPerformanceAccountingList(pageUtil.getList(), pageUtil.getTotal(), firstDate,
				lastDate);

		return ResultUtil.success(map);
	}

	private Map getChildPerformanceAccountingList(List<HashMap<String, Object>> list,
			Long total, String firstDate, String lastDate) {
		// List<String> noSkuList = new ArrayList<String>();//需要导入
		List<HashMap<String, Object>> returnList = new ArrayList<HashMap<String, Object>>();
		Map<String,Object> shopAreaCommissionRateMap = new HashMap<String,Object>();
		Map<String,Object> shopAreaexchangeRateMap = new HashMap<String,Object>();
		for (int i = 0, size = list.size(); i < size; i++) {
			HashMap<String, Object> map = list.get(i);
			Double commissionRate = 1d;
			Double exchangeRate = 1d;
			String shop = MapUtils.getString(map,"shop");
			String area = MapUtils.getString(map,"area");
			String sku = MapUtils.getString(map,"sku");
			String asin = MapUtils.getString(map,"asin");
			String parentSku = MapUtils.getString(map,"parentSku");
			if(shopAreaCommissionRateMap.containsKey(shop+"-"+area)){
				commissionRate = MapUtils.getDouble(shopAreaCommissionRateMap,"commissionRate",1d);
			}else{
				// 店铺佣金费率
				commissionRate = childPerformanceAccountingMapper.findShopCommissionRate(shop, area, firstDate,
						lastDate);
				MapUtils.safeAddToMap(shopAreaCommissionRateMap, "commissionRate", commissionRate);
			}

			if(shopAreaexchangeRateMap.containsKey(shop+"-"+area)){
				exchangeRate = MapUtils.getDouble(shopAreaexchangeRateMap,"exchangeRate",1d);
			}else{
				 exchangeRate = childPerformanceAccountingMapper.findShopExchangeRate(shop, area, firstDate, lastDate);
				MapUtils.safeAddToMap(shopAreaexchangeRateMap, "exchangeRate", exchangeRate);

			}
			if (StringUtils.isEmpty(parentSku)) {
				map.put("notFindParentSku", "1");// 这一行未找到父sku
			}
			Object netSalesStr = map.get("netSales");
			Double netSales = Double.valueOf((String.valueOf(netSalesStr)));
			// 净销售额
			Object netSalesNumStr = map.get("netSalesNum");
			Integer netSalesNum = Double.valueOf(String.valueOf(netSalesNumStr)).intValue();// 净销量
			Object totalRefundAmountStr = map.get("totalRefundAmount");
			Double totalRefundAmount = Double.valueOf(String.valueOf(totalRefundAmountStr));// 退款总金额
			Object totalRefundNumStr = map.get("totalRefundNum");
			Integer totalRefundNum = Double.valueOf(String.valueOf(totalRefundNumStr)).intValue();// 退款量
			Object sellableStr = map.get("sellable");
			Integer sellable = Double.valueOf(String.valueOf(sellableStr)).intValue();// SELLABLE

			/* 单位采购成本start */
			HashMap<String, Object> purchasePriceMap = childPerformanceAccountingMapper.findPurchasePrice(shop, area,
					sku);// 查询采购价格
			Double purchasePrice = null;
			String isEstimatePurchasePrice = null;
			if (purchasePriceMap == null || purchasePriceMap.size() == 0) {// 未查找到
				// noSkuList.add("需要导入的采购价(店铺>>区域>>msku):"+shop+">>"+area+">>"+sku);
				map.put("notFindPurchasePrice", "1");// 1为找不到标识
				purchasePrice = 0.00;
			} else {
				String purchasePriceStr = (String) purchasePriceMap.get("purchase_price");
				isEstimatePurchasePrice = (String) purchasePriceMap.get("is_estimate");// 采购价是否预估: 0 不是 1 是
				if (!StringUtils.isEmpty(purchasePriceStr)) { // 查询到了
					purchasePrice = Double.valueOf(purchasePriceStr);
					if ("是".equals(isEstimatePurchasePrice)) {// 并且查询到的是预估的
						map.put("isEstimatePurchase", "1");// 1为预估的标识
					}

				} else {
					map.put("notFindPurchasePrice", "1");// 找不到
					purchasePrice = 0.00;
				}

			}

			map.put("purchasePrice", purchasePrice);

			/* 单位采购成本end */

			/* 头程运费start */
			HashMap<String, Object> headFreightMap = childPerformanceAccountingMapper.findHeadFreight(shop, area, sku);// 查询头程运费价格
			Double headFreight = null;
			String isEstimateHeadFreight = null;
			if (headFreightMap == null || headFreightMap.size() == 0) {// 未查找到
				// noSkuList.add("需要导入的头程运费(店铺>>区域>>msku):"+shop+">>"+area+">>"+sku);
				map.put("notFindHeadFreight", "1");// 1为找不到标识
				headFreight = 0.00;
			} else {
				String headFreightStr = (String) headFreightMap.get("head_freight");
				isEstimateHeadFreight = (String) headFreightMap.get("is_estimate");// 采购价是否预估: 0 不是 1 是
				if (!StringUtils.isEmpty(headFreightStr)) { // 查询到了
					headFreight = Double.valueOf(headFreightStr);
					if ("是".equals(isEstimateHeadFreight)) {// 并且查询到的是预估的
						map.put("isEstimateHeadFreight", "1");// 1为预估的标识
					}

				} else {
					map.put("notFindHeadFreight", "1");// 找不到
					headFreight = 0.00;
				}

			}

			map.put("headFreight", headFreight);

			/* 头程运费end */

			/* 亚马逊配送费用start */

			String shippingFeesStr = childPerformanceAccountingMapper.findShippingFees(shop, area, asin);// 查询亚马逊配送费用

			if (StringUtils.isEmpty(shippingFeesStr) || !shippingFeesStr.matches("\\d+(.\\d+)?[fF]?[dD]?")) {// 不存在或者非数值类型
				// noSkuList.add("feePreview中无此msku信息(店铺>>区域>>msku):"+shop+">>"+area+">>"+sku);

				map.put("notFindShippingFees", "1");// 未找到此信息

				// 查找预估亚马逊配送费表格中查找同一店铺同一区域MSKU对应的亚马逊配送费
				shippingFeesStr = childPerformanceAccountingMapper.findExpectedAmazonDistributionCost(shop, area, sku);// 查询预估亚马逊配送费用

				if (StringUtils.isEmpty(shippingFeesStr)) {
					shippingFeesStr = "0.00";
				} else {
					map.put("isEstimateShippingFees", "1");// 预估
				}
			}

			Double shippingFees = Double.valueOf(shippingFeesStr);

			/* 亚马逊配送费用end */

			// 广告费
			Double adFee = childPerformanceAccountingMapper.findSponsoredProductsAdvertisedProductReport(shop, area,
					sku, firstDate, lastDate);

			// 仓储费
			Double monthlyStorageFees = childPerformanceAccountingMapper.findMonthlyStorageFees(shop, area, asin,
					firstDate, lastDate);

			// 长期仓储费
			Double longTermStorageFees = childPerformanceAccountingMapper.findLongTermStorageFees(shop, area, asin,
					firstDate, lastDate);
			Double grossProfit = 0d;
			if(commissionRate!=null && exchangeRate != null){



				// 毛利
				// 净销售额*（1-店铺佣金费率）*店铺汇率-((采购成本+单件FBA运费成本)*净销量+亚马逊配送费用*(净销量+退货量)*店铺汇率+(采购成本+单件FBA运费成本)*(退货量-SELLABLE)+亚马逊配送费用*退货量*店铺汇率)-退货总金额*店铺佣金费率*0.2*店铺汇率-广告费*店铺汇率-仓储费*店铺汇率-长期仓储费*店铺汇率
				grossProfit = netSales * (1 - commissionRate) * exchangeRate
						- ((purchasePrice + headFreight) * netSalesNum
						+ shippingFees * (netSalesNum + totalRefundNum) * exchangeRate
						+ (purchasePrice + headFreight) * (totalRefundNum - sellable)
						+ shippingFees * totalRefundNum * exchangeRate)
						- totalRefundAmount * commissionRate * 0.2 * exchangeRate - adFee * exchangeRate
						- monthlyStorageFees * exchangeRate - longTermStorageFees * exchangeRate;
			}
			// 退货率
			// 如果净销售额与退货总金额之和为0，则退货率为空值，反之为退货量/（退货量+净销量），备注（用百分比的格式展示）
			Double returnRate = null;
			Double singleGrossprofit = null;// 单件毛利
			Double profitMargin = null;// 利润率
			Double temp = netSales + totalRefundAmount;

			BigDecimal temp1 = new BigDecimal(temp);
			BigDecimal temp2 = new BigDecimal(0.00);

			if (temp1.compareTo(temp2) == 0) {
				returnRate = 0.0000;// 不存在退货率
				singleGrossprofit = 0.00;// 不存在单件毛利
				profitMargin = 0.0000;
			} else {
				returnRate = totalRefundNum * 1.0000 / (netSalesNum + totalRefundNum);
				singleGrossprofit = grossProfit * 1.00 / (netSalesNum + totalRefundNum);
				if (netSales / netSalesNum == 0) {
					profitMargin = 0.0000;
				} else {
					profitMargin = singleGrossprofit / exchangeRate / (netSales / netSalesNum);// TODO
				}

			}
			map.put("returnRate", returnRate);
			map.put("singleGrossprofit", singleGrossprofit);
			map.put("profitMargin", profitMargin);
			map.put("grossProfit", grossProfit);
			map.put("longTermStorageFees", longTermStorageFees);
			map.put("monthlyStorageFees", monthlyStorageFees);
			map.put("adFee", adFee);
			map.put("shippingFees", shippingFees);

			Double scrappedRate = null;// 报废率
			if (totalRefundNum == 0) {
				scrappedRate = 0.0000;
			} else {
				scrappedRate = (totalRefundNum - sellable) * 1.0000 / totalRefundNum;
			}

			map.put("scrappedRate", scrappedRate);

			// 报废衣服成本 如果退货量+SELLABLE为0，则报废衣服成本为0，反之则为（退货量-SELLABLE）*（单位采购成本+单件FBA运费成本）
			int temp3 = totalRefundNum + sellable;
			Double bfyfPrice = null;
			if (temp3 == 0) {
				bfyfPrice = 0.00;
			} else {
				bfyfPrice = (totalRefundNum - sellable) * (purchasePrice + shippingFees);
			}
			map.put("bfyfPrice", bfyfPrice);

			// 平均单价 如果净销售额为0，则平均单价为0，反之为净销售额/净销量
			Double averagePrice = null;// 平均单价
			if (netSalesNum == 0) {
				averagePrice = 0.00;
			} else {
				averagePrice = netSales / netSalesNum;
			}

			map.put("averagePrice", averagePrice);
			returnList.add(map);
		}



		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("list", returnList);
		returnMap.put("total", total);
		return returnMap;
	}

	/**
	 * 获取父分页数据
	 * @param maps
	 * @return
	 */
	public ResultUtil getPageParentList(Map maps) {
			Integer pageNum = 	MapUtils.getInteger(maps,"pageNum");
			Integer pageSize = MapUtils.getInteger(maps,"pageSize");
			String date = MapUtils.getString(maps,"date");


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
			MapUtils.safeAddToMap(maps,"firstDate",firstDate);
			MapUtils.safeAddToMap(maps,"lastDate",lastDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PageHelper.startPage(pageNum, pageSize);
		PageUtil pageUtil = PageUtil
				.pageInfo(childPerformanceAccountingMapper.getParentPageList(maps));
		Map map = getParentPerformanceAccountingList(pageUtil.getList(), pageUtil.getTotal(), firstDate,
				lastDate);

		return ResultUtil.success(map);
	}

	/**
	 * 父体
	 *
	 * @param list
	 * @param shop
	 * @param area
	 * @param total
	 * @param firstDate
	 * @param lastDate
	 * @return
	 */
	private Map getParentPerformanceAccountingList(List<HashMap<String, Object>> list,
			Long total, String firstDate, String lastDate) {
		List<HashMap<String, Object>> returnList = new ArrayList<HashMap<String, Object>>();


		Map<String,Object> shopAreaCommissionRateMap = new HashMap<String,Object>();
		Map<String,Object> shopAreaexchangeRateMap = new HashMap<String,Object>();
		for (HashMap<String, Object> map : list) {
			String parentSku = MapUtils.getString(map,"parentSku");
			String skuStr = MapUtils.getString(map,"sku");
			String shop = MapUtils.getString(map,"shop");
			String area = MapUtils.getString(map,"area");
			Double commissionRate = 1d;
			Double exchangeRate = 1d;
			if (StringUtils.isEmpty(skuStr)) {
				continue;
			}
			String[] skuAttr = skuStr.split(",");

			// 父体净销售额
			Double netSalesParent = 0.00;
			// 父体净销量
			Integer netSalesNumParent = 0;
			// 父体退款总金额
			Double totalRefundAmountParent = 0.00;
			// 父体退款量
			Integer totalRefundNumParent = 0;
			Integer sellableParent = 0;
			Double purchasePriceParent = 0.00;// 父体采购成本
			Double headFreightParent = 0.00;// 父体头程运费
			Double shippingFeesParent = 0.00;// 父体亚马逊配送费
			Double grossProfitParent = 0.00;// 父体毛利
			Double adParent = 0.00;// 父体广告费
			Double monthlyStorageFeesParent = 0.00;// 父体仓储费
			Double longTermStorageFeesParent = 0.00;// 父体长期仓储费


			if(shopAreaCommissionRateMap.containsKey(shop+"-"+area)){
				commissionRate = MapUtils.getDouble(shopAreaCommissionRateMap,"commissionRate",1d);
			}else{
				// 店铺佣金费率
				commissionRate = childPerformanceAccountingMapper.findShopCommissionRate(shop, area, firstDate,
						lastDate);
				MapUtils.safeAddToMap(shopAreaCommissionRateMap, "commissionRate", commissionRate);
			}

			if(shopAreaexchangeRateMap.containsKey(shop+"-"+area)){
				exchangeRate = MapUtils.getDouble(shopAreaexchangeRateMap,"exchangeRate",1d);
			}else{
				exchangeRate = childPerformanceAccountingMapper.findShopExchangeRate(shop, area, firstDate, lastDate);
				MapUtils.safeAddToMap(shopAreaexchangeRateMap, "exchangeRate", exchangeRate);

			}


			for (String sku : skuAttr) {
				// 同一sku下
				// 净销售额 和净销量
				Map<String, Object> netSalesMap = childPerformanceAccountingMapper.findNetSalesFromOneSku(sku, shop,
						area, firstDate, lastDate);
				Object netSalesStr = netSalesMap.get("netSales");
				Double netSales = Double.valueOf((String.valueOf(netSalesStr)));// 净销售额

				Object netSalesNumStr = netSalesMap.get("netSalesNum");
				Integer netSalesNum = Integer.valueOf(String.valueOf(netSalesNumStr));// 净销量

				String asin = (String) netSalesMap.get("asin");

				// 退款总金额、退款量
				Map<String, Object> totalRefundMap = childPerformanceAccountingMapper.findTotalRefundFromOneSku(sku,
						shop, area, firstDate, lastDate);
				Object totalRefundAmountStr = totalRefundMap.get("totalRefundAmount");
				Double totalRefundAmount = Double.valueOf(String.valueOf(totalRefundAmountStr));// 退款总金额
				Object totalRefundNumStr = totalRefundMap.get("totalRefundNum");
				Integer totalRefundNum = Integer.valueOf(String.valueOf(totalRefundNumStr));// 退款量

				// 退货可售数量
				String sellableStr = childPerformanceAccountingMapper.findSellableFromOneSku(sku, shop, area, firstDate,
						lastDate);
				Integer sellable = Integer.valueOf(sellableStr);// SELLABLE

				netSalesParent += netSales; // 父体净销售额
				netSalesNumParent += +netSalesNum;// 父体净销量
				totalRefundAmountParent += totalRefundAmount;// 父体退款总金额
				totalRefundNumParent += totalRefundNum;// 父体退款量
				sellableParent += sellable;
				/* 单位采购成本start */
				HashMap<String, Object> purchasePriceMap = childPerformanceAccountingMapper.findPurchasePrice(shop,
						area, sku);// 查询采购价格
				Double purchasePrice = null;
				String isEstimatePurchasePrice = null;
				if (purchasePriceMap == null || purchasePriceMap.size() == 0) {// 未查找到
					// noSkuList.add("需要导入的采购价(店铺>>区域>>msku):"+shop+">>"+area+">>"+sku);
					// map.put("notFindPurchasePrice", "1");//1为找不到标识
					purchasePrice = 0.00;
				} else {
					String purchasePriceStr = (String) purchasePriceMap.get("purchase_price");
					isEstimatePurchasePrice = (String) purchasePriceMap.get("is_estimate");// 采购价是否预估: 0 不是 1 是
					if (!StringUtils.isEmpty(purchasePriceStr)) { // 查询到了
						purchasePrice = Double.valueOf(purchasePriceStr);
//						if("是".equals(isEstimatePurchasePrice)) {//并且查询到的是预估的
//							map.put("isEstimatePurchase", "1");//1为预估的标识
//						}

					} else {
						// map.put("notFindPurchasePrice", "1");//找不到
						purchasePrice = 0.00;
					}

				}


				// 单位采购成本*（净销量+退款量-退货可售数量）
				Integer tempOperation = netSalesNum + totalRefundNum - sellable;
				Double purchasePriceTemp = purchasePrice * tempOperation;

				// 父体sku 采购成本
				purchasePriceParent += purchasePriceTemp;

				/* 单位采购成本end */

				/* 头程运费start */
				HashMap<String, Object> headFreightMap = childPerformanceAccountingMapper.findHeadFreight(shop, area,
						sku);// 查询头程运费价格
				Double headFreight = null;
				String isEstimateHeadFreight = null;
				if (headFreightMap == null || headFreightMap.size() == 0) {// 未查找到
					// noSkuList.add("需要导入的头程运费(店铺>>区域>>msku):"+shop+">>"+area+">>"+sku);
					// map.put("notFindHeadFreight", "1");//1为找不到标识
					headFreight = 0.00;
				} else {
					String headFreightStr = (String) headFreightMap.get("head_freight");
					isEstimateHeadFreight = (String) headFreightMap.get("is_estimate");// 采购价是否预估: 0 不是 1 是
					if (!StringUtils.isEmpty(headFreightStr)) { // 查询到了
						headFreight = Double.valueOf(headFreightStr);
//						if("是".equals(isEstimateHeadFreight)) {//并且查询到的是预估的
//							map.put("isEstimateHeadFreight", "1");//1为预估的标识
//						}

					} else {
						// map.put("notFindHeadFreight", "1");//找不到
						headFreight = 0.00;
					}

				}

				// map.put("headFreight", headFreight);

				Double headFreightTemp = headFreight * tempOperation;
				headFreightParent += headFreightTemp;// 父体头程运费
				/* 头程运费end */

				/* 亚马逊配送费用start */

				String shippingFeesStr = childPerformanceAccountingMapper.findShippingFees(shop, area, asin);// 查询亚马逊配送费用

				if (StringUtils.isEmpty(shippingFeesStr) || !shippingFeesStr.matches("\\d+(.\\d+)?[fF]?[dD]?")) {// 不存在或者非数值类型
					// noSkuList.add("feePreview中无此msku信息(店铺>>区域>>msku):"+shop+">>"+area+">>"+sku);

					// map.put("notFindShippingFees", "1");//未找到此信息

					// 查找预估亚马逊配送费表格中查找同一店铺同一区域MSKU对应的亚马逊配送费
					shippingFeesStr = childPerformanceAccountingMapper.findExpectedAmazonDistributionCost(shop, area,
							sku);// 查询预估亚马逊配送费用

					if (StringUtils.isEmpty(shippingFeesStr)) {
						shippingFeesStr = "0.00";
					} else {
						// map.put("isEstimateShippingFees", "1");//预估
					}
				}

				Double shippingFees = Double.valueOf(shippingFeesStr);

				// 父体 亚马逊配送费用 亚马逊配送费*（净销量+2*退款量）的和
				shippingFeesParent += shippingFees * (netSalesNum + 2 * totalRefundNum);

				/* 亚马逊配送费用end */

				// 广告费
				Double adFee = childPerformanceAccountingMapper.findSponsoredProductsAdvertisedProductReport(shop, area,
						sku, firstDate, lastDate);
				adParent += adFee;// 父体广告费

				// 仓储费
				Double monthlyStorageFees = childPerformanceAccountingMapper.findMonthlyStorageFees(shop, area, asin,
						firstDate, lastDate);
				monthlyStorageFeesParent += monthlyStorageFees;// 父体仓储费

				// 长期仓储费
				Double longTermStorageFees = childPerformanceAccountingMapper.findLongTermStorageFees(shop, area, asin,
						firstDate, lastDate);
				longTermStorageFeesParent += longTermStorageFees;// 父体长期仓储费
				// 毛利
				// 净销售额*（1-店铺佣金费率）*店铺汇率-((采购成本+单件FBA运费成本)*净销量+亚马逊配送费用*(净销量+退货量)*店铺汇率+(采购成本+单件FBA运费成本)*(退货量-SELLABLE)+亚马逊配送费用*退货量*店铺汇率)-退货总金额*店铺佣金费率*0.2*店铺汇率-广告费*店铺汇率-仓储费*店铺汇率-长期仓储费*店铺汇率
				Double grossProfit = netSales * (1 - commissionRate) * exchangeRate
						- ((purchasePrice + shippingFees) * netSalesNum
								+ shippingFees * (netSalesNum + totalRefundNum) * exchangeRate
								+ (purchasePrice + shippingFees) * (totalRefundNum - sellable)
								+ shippingFees * totalRefundNum * exchangeRate)
						- totalRefundAmount * commissionRate * 0.2 * exchangeRate - adFee * exchangeRate
						- monthlyStorageFees * exchangeRate - longTermStorageFees * exchangeRate;

				grossProfitParent += grossProfit;// 父体毛利

			}

			// 退货率
			// 如果净销售额与退货总金额之和为0，则退货率为空值，反之为退货量/（退货量+净销量），备注（用百分比的格式展示）
			Double returnRate = null;
			Double singleGrossprofit = null;// 单件毛利
			Double profitMargin = null;// 利润率
			Double temp = netSalesParent + totalRefundAmountParent;

			BigDecimal temp1 = new BigDecimal(temp);
			BigDecimal temp2 = new BigDecimal(0.00);

			if (temp1.compareTo(temp2) == 0) {
				returnRate = 0.0000;// 不存在退货率
				singleGrossprofit = 0.00;// 不存在单件毛利
				profitMargin = 0.0000;
			} else {
				returnRate = totalRefundNumParent * 1.0000 / (netSalesNumParent + totalRefundNumParent);
				singleGrossprofit = grossProfitParent * 1.00 / (netSalesNumParent + totalRefundNumParent);
				if (netSalesParent / netSalesNumParent == 0) {
					profitMargin = 0.0000;
				} else {
					profitMargin = singleGrossprofit / exchangeRate / (netSalesParent / netSalesNumParent);// TODO
				}

			}
			Double scrappedRate = null;// 报废率
			if (totalRefundNumParent == 0) {
				scrappedRate = 0.0000;
			} else {
				scrappedRate = (totalRefundNumParent - sellableParent) * 1.0000 / totalRefundNumParent;
			}

			// 报废衣服成本 如果退货量+SELLABLE为0，则报废衣服成本为0，反之则为（退货量-SELLABLE）*（单位采购成本+单件FBA运费成本）
			int temp3 = totalRefundNumParent + sellableParent;
			Double bfyfPrice = null;
			if (temp3 == 0) {
				bfyfPrice = 0.00;
			} else {
				bfyfPrice = (totalRefundNumParent - sellableParent) * (purchasePriceParent + headFreightParent);
			}
			map.put("bfyfPrice", bfyfPrice);

			// 平均单价
			Double averagePrice = null;// 平均单价
			if (netSalesNumParent + totalRefundNumParent == 0) {
				averagePrice = 0.00;
			} else {
				averagePrice = (netSalesParent + totalRefundAmountParent) / (netSalesNumParent + totalRefundNumParent);
			}

			HashMap<String,Object> itemMaps = new HashMap<String,Object>();
			itemMaps.put("returnRate", returnRate);
			itemMaps.put("singleGrossprofit", singleGrossprofit);
			itemMaps.put("profitMargin", profitMargin);
			itemMaps.put("grossProfit", grossProfitParent);
			itemMaps.put("longTermStorageFees", longTermStorageFeesParent);
			itemMaps.put("monthlyStorageFees", monthlyStorageFeesParent);
			itemMaps.put("adFee", adParent);
			itemMaps.put("shippingFees", shippingFeesParent);
			itemMaps.put("averagePrice", averagePrice);
			itemMaps.put("bfyfPrice", bfyfPrice);
			itemMaps.put("scrappedRate", scrappedRate);

			// 父体净销售额
			itemMaps.put("netSales",netSalesParent);
			// 父体净销量
			itemMaps.put("netSalesNum",netSalesNumParent);
			// 父体退款总金额
			itemMaps.put("totalRefundAmount",totalRefundAmountParent);
			// 父体退款量
			itemMaps.put("totalRefundNum",totalRefundNumParent);
			itemMaps.put("sellable",sellableParent);
			// 父体采购成本
			itemMaps.put("purchasePrice",purchasePriceParent);
			// 父体头程运费
			itemMaps.put("headFreight",headFreightParent);
			itemMaps.put("parentSku",parentSku);
			returnList.add(itemMaps);
		}

		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("list", returnList);
		returnMap.put("total", total);
		return returnMap;

	}

}
