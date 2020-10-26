package com.weiziplus.springboot.mapper.middle;

import com.weiziplus.springboot.models.middle.SearchWordDate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author czw
 */
@Mapper
public interface SearchWordDateMapper {

    /**
     * 将SearchWordDate对象存入表中
     * @param searchWordDate
     * @return
     */
    Integer insertSearchWordDate (SearchWordDate searchWordDate);

    /**
     * 批量插入SearchWordDate
     * @param SearchWordDateList
     * @return
     */
    Integer insertSearchWordDateList (List<SearchWordDate> SearchWordDateList);
}
