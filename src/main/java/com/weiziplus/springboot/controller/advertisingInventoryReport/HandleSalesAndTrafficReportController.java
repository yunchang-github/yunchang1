package com.weiziplus.springboot.controller.advertisingInventoryReport;

import com.weiziplus.springboot.service.advertisingInventoryReport.HandleSalesAndTrafficReportService;
import com.weiziplus.springboot.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
//@AdminAuthToken
@RequestMapping("/pc/advertisingInventoryReport")
public class HandleSalesAndTrafficReportController {
    @Autowired
    HandleSalesAndTrafficReportService handleSalesAndTrafficReportService;

    @GetMapping("/childItemSalesAndTraffic/exportExcel")
    public Object exportChildItemSalesAndTrafficReport(HttpServletResponse response,
                                                       @RequestParam(value = "type", required = false) String type,
                                                       @RequestParam(value = "date", required = false) String date,
                                                       @RequestParam(value = "shop", required = false) String shop,
                                                       @RequestParam(value = "area", required = false) String area,
                                                       //@RequestParam(value = "searchType", required = false) String searchType,
                                                       @RequestParam(value = "asin", defaultValue = "") String asin,
                                                       @RequestParam(value = "parentSku", defaultValue = "") String parentSku,
                                                       @RequestParam(value = "sku", defaultValue = "") String sku) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", type);
        map.put("date", date);
        map.put("shop", shop);
        map.put("area", area);
        //map.put("searchType",searchType);
        map.put("asin", asin);
        map.put("sku", sku);
        map.put("parentSku", parentSku);
        try {
            return handleSalesAndTrafficReportService.exportChildSalesAndTrafficReport(response, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultUtil.error("导出Excel表失败！");
    }

    @GetMapping("/parentItemSalesAndTraffic/exportExcel")
    public Object exportParentItemSalesAndTrafficReport(HttpServletResponse response,
                                                            @RequestParam(value = "type", required = false) String type,
                                                            @RequestParam(value = "date", required = false) String date,
                                                            @RequestParam(value = "shop", required = false) String shop,
                                                            @RequestParam(value = "area", required = false) String area,
                                                            //@RequestParam(value = "searchType", required = false) String searchType,
                                                            @RequestParam(value = "parentSku", defaultValue = "") String parentSku
                                                            ) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", type);
        map.put("date", date);
        map.put("shop", shop);
        map.put("area", area);
        //map.put("searchType",searchType);
        map.put("parentSku", parentSku);
        try {
            return handleSalesAndTrafficReportService.exportParentSalesAndTrafficReport(response, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultUtil.error("导出Excel表失败！");
    }
}
