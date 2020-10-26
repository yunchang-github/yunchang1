package com.weiziplus.springboot.mapper.advertisingInventoryReport;

import com.weiziplus.springboot.models.DO.ChildBodyInventoryReportDO;
import com.weiziplus.springboot.models.DO.ParentBodyInventoryReportDO;
import com.weiziplus.springboot.models.VO.ChildItemAdInventoryReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface InventoryReportMapper {
    /**
     * 新增子体库存报告数据
     * */
    int addChildBodyInventoryReport(@Param("childBodyInventoryReportDOList") List<ChildBodyInventoryReportDO> childBodyInventoryReportDOList);

    /**
     * 新增父体库存报告数据
     * */
    int addParentBodyInventoryReport(@Param("parentBodyInventoryReportDOList") List<ParentBodyInventoryReportDO> parentBodyInventoryReportDOList);

    /**
     * 查询指定日期的子体报告数据
     * */
    List<ChildBodyInventoryReportDO> selectChildInventoryReportByDate(Map maps);

    /**
     * 查询指定日期的父体报告数据
     * */
    List<ParentBodyInventoryReportDO> selectParentInventoryReportByDate(Map maps);
}
