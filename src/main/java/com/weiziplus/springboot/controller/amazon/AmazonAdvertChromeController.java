package com.weiziplus.springboot.controller.amazon;

import com.weiziplus.springboot.models.DO.DetailPagesDO;
import com.weiziplus.springboot.service.amazon.AmazonAdvertChromeService;
import com.weiziplus.springboot.utils.ResultUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 浏览器插件数据
 *
 * @author wanglongwei
 * @data 2019/8/15 14:41
 */

/*
* 稍作修改，入参增加date
* --苏建东
* */
@RestController
//@ApiIgnore
@RequestMapping("/pc/amazon/advertChromeExAid")
public class AmazonAdvertChromeController {

    @Autowired
    AmazonAdvertChromeService service;

    /**
     * 销售与访问量（Sales and Traffic）
     *
     *
     * @return
     */
    @PostMapping("/detailSalesTraffic")
    public ResultUtil detailPageSalesAndTraffic(@RequestBody DetailPagesDO detailPagesDO) {
        return service.detailPageSalesAndTraffic(detailPagesDO);
    }

    /**
     * 父商品详情页面上的销售量与访问量
     *
     * @return
     */
    @PostMapping("/detailSalesTrafficByParentItems")
    public ResultUtil detailSalesTrafficByParentItems(@RequestBody DetailPagesDO detailPagesDO) {
        return service.detailSalesTrafficByParentItems(detailPagesDO);
    }

    /**
     * 子商品详情页面上的销售量与访问量
     *
     * @return
     */
    @PostMapping("/detailSalesTrafficByChildItems")
    public ResultUtil detailSalesTrafficByChildItems(@RequestBody DetailPagesDO detailPagesDO) {
        return service.detailSalesTrafficByChildItems(detailPagesDO);
    }

    /**
     * 获取秒杀的总数量
     *
     * @param shop
     * @param area
     * @return
     */

//    @GetMapping("/getSeckillNum")
//   @ApiOperation(value = "获取秒杀的总数量", notes = "获取秒杀的总数量")
//    public ResultUtil getSeckillNum(@ApiParam(value = "店铺", required = true)@RequestParam("shop") String shop,@ApiParam(value = "区域", required = true)@RequestParam("area") String area) {
//        return service.getSeckillNum(shop, area);
//    }

    /**
     * 秒杀活动情况
     *
     * @param shop
     * @param area
     * @param data
     * @return
     */
    @PostMapping("/seckill")
    @ApiOperation(value = "秒杀活动情况", notes = "秒杀活动情况")
    public ResultUtil seckill(@ApiParam(value = "店铺", required = true)@RequestParam("shop") String shop,@ApiParam(value = "区域", required = true)@RequestParam("area") String area,@ApiParam(value = "后台扒取过来的数据", required = false)@RequestParam("data") String data) {
        return service.seckill(shop, area, data);
    }
}
