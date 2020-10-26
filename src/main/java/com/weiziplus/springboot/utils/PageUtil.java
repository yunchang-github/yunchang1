package com.weiziplus.springboot.utils;

import com.github.pagehelper.PageInfo;
import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页返回结果处理
 *
 * @author wanglongwei
 * @data 2019/5/7 17:06
 */
@Getter
@Setter
public class PageUtil implements Serializable {

    private Integer pageNum;

    private Integer pageSize;

    private Long total;

    private List list;

    private int flag;

    public static PageUtil pageInfo(List list) {
        PageInfo pageInfo = new PageInfo(list);
        PageUtil pageUtil = new PageUtil();
        pageUtil.setPageNum(pageInfo.getPageNum());
        pageUtil.setPageSize(pageInfo.getSize());
        pageUtil.setTotal(pageInfo.getTotal());
        pageUtil.setList(pageInfo.getList());
        return pageUtil;
    }

    /**
     * 自己写的分页方法---sjd
     * */
    public static PageUtil Pagination(int pageNum,int pageSize,List list){
        PageUtil pageUtil = new PageUtil();
        pageUtil.setPageNum(pageNum);
        pageUtil.setPageSize(pageSize);
        pageUtil.setTotal((long) list.size());
        List newList = new ArrayList();
        if ((pageNum) * pageSize <= list.size()){
            newList = list.subList((pageNum - 1) * pageSize,pageNum * pageSize);
        }else {
            newList = list.subList((pageNum - 1) * pageSize,list.size());
        }
        pageUtil.setList(newList);
        return pageUtil;
    }

    /**
     * 自己写的分页方法---sjd
     * */
    public static PageUtil Pagination2(int pageNum,int pageSize,List list,int flag){
        PageUtil pageUtil = new PageUtil();
        pageUtil.setPageNum(pageNum);
        pageUtil.setPageSize(pageSize);
        pageUtil.setTotal((long) list.size());
        pageUtil.setFlag(flag);
        List newList = new ArrayList();
        if ((pageNum) * pageSize <= list.size()){
            newList = list.subList((pageNum - 1) * pageSize,pageNum * pageSize);
        }else {
            newList = list.subList((pageNum - 1) * pageSize,list.size());
        }
        pageUtil.setList(newList);
        return pageUtil;
    }
}
