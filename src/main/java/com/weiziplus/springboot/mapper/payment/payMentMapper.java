package com.weiziplus.springboot.mapper.payment;

import com.weiziplus.springboot.models.InventoryAge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;


@Mapper
public interface payMentMapper {
	InventoryAge getStockFromInventoryAge(@Param("param") Map<String, Object> paraMap);

	/**
	 * 获取最新的时间
	 *
	 * @return
	 */


	String getLatestDay();
}
