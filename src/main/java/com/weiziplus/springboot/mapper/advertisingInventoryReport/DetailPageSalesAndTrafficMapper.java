package com.weiziplus.springboot.mapper.advertisingInventoryReport;

import com.weiziplus.springboot.models.DO.DatailPageSalesAndTrafficNullValueDateDO;
import com.weiziplus.springboot.models.DetailPageSalesAndTraffic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/*
 * 新增的 详情页面上的销售量和访问量 的mapper接口
 * --苏建东
 * */
@Mapper
public interface DetailPageSalesAndTrafficMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DetailPageSalesAndTraffic record);

    int insertSelective(DetailPageSalesAndTraffic record);

    /**
     * 通过list新增多条数据
     * */
    int insertListSelective(@Param("detailPageSalesAndTrafficList") List<DetailPageSalesAndTraffic> detailPageSalesAndTrafficList);

    DetailPageSalesAndTraffic selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DetailPageSalesAndTraffic record);

    int updateByPrimaryKey(DetailPageSalesAndTraffic record);

    /**
     * 获得数据库中的最近日期
     * */
    Integer getDateSum(@Param("date") String date,@Param("sellerId") String sellerId,@Param("area") String area);

    /**
     * 删除数据库中的该日期的数据
     */
    Integer deleteDateSum(@Param("sellerId")String sellerId, @Param("area")String area, @Param("date") String date);

    /**
     * 将空值日期新增
     * */
    int insertNullValueDate(@Param("dateDO") DatailPageSalesAndTrafficNullValueDateDO dateDO);

    /**
     * 查找该日期是否已经存在于空值表中
     * */
    DatailPageSalesAndTrafficNullValueDateDO selectDateFromNullValueDate(@Param("dateDO")DatailPageSalesAndTrafficNullValueDateDO dateDO);
}