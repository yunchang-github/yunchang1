package com.weiziplus.springboot.mapper.advertisingInventoryReport;

import com.weiziplus.springboot.models.DetailPageSalesAndTraffic;
import com.weiziplus.springboot.models.DetailPageSalesAndTrafficByParentItems;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/*
 * 新增的 父商品详情页面上的销售量和访问量 的mapper接口
 * --苏建东
 * */
@Mapper
public interface DetailPageSalesAndTrafficByParentItemsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DetailPageSalesAndTrafficByParentItems record);

    int insertSelective(DetailPageSalesAndTrafficByParentItems record);

    /**
     * 通过list新增多条数据
     * */
    int insertListSelective(@Param("detailPageSalesAndTrafficByParentItemsList") List<DetailPageSalesAndTrafficByParentItems> detailPageSalesAndTrafficByParentItemsList);

    DetailPageSalesAndTrafficByParentItems selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DetailPageSalesAndTrafficByParentItems record);

    int updateByPrimaryKey(DetailPageSalesAndTrafficByParentItems record);

    /**
     * 根据该日期获取表中该日期的数据条数
     * */
    Integer getDateSum(@Param("date") String date,@Param("sellerId") String sellerId,@Param("area") String area);

    /**
     * 删除数据库中的该日期的数据
     */
    Integer deleteDateSum(@Param("sellerId")String sellerId, @Param("area")String area, @Param("date") String date);
}