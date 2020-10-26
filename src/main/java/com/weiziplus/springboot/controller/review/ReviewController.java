package com.weiziplus.springboot.controller.review;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.service.review.ReviewService;
import com.weiziplus.springboot.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author wanglongwei
 * @data 2019/8/28 9:44
 */

@Api(value="ReviewController",tags={"商品评价"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/review")
public class ReviewController {

    @Autowired
    ReviewService service;

    /**
     * 获取分页列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "获取分页列表", notes = "获取分页列表")
    @GetMapping("/getPageList")
    public ResultUtil getPageList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return service.getPageList(pageNum, pageSize);
    }
}
