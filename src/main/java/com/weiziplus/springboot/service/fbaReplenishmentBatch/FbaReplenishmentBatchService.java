package com.weiziplus.springboot.service.fbaReplenishmentBatch;

import com.weiziplus.springboot.mapper.fbaReplenishmentBatch.FbaReplenishmentBatchMapper;
import com.weiziplus.springboot.models.DO.FbaReplenishmentBatch;
import com.weiziplus.springboot.utils.BarcodeUtil;
import com.weiziplus.springboot.utils.ImageHandleHelper;
import com.weiziplus.springboot.utils.PageUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class FbaReplenishmentBatchService {
    @Autowired
    private FbaReplenishmentBatchMapper fbaReplenishmentBatchMapper;

    public ResultUtil getData(HashMap map){
        Integer pageNum = MapUtils.getInteger(map,"pageNum");
        Integer pageSize = MapUtils.getInteger(map,"pageSize");
        String replenishmentBatchNo = MapUtils.getString(map,"replenishmentBatchNo");
        String localSku = MapUtils.getString(map,"localSku");
        String localDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<FbaReplenishmentBatch> fbaReplenishmentBatchList = fbaReplenishmentBatchMapper.selectDataByReplenishmentBatchNoAndLocalSku(replenishmentBatchNo,localSku);
        PageUtil pageList = PageUtil.Pagination(pageNum, pageSize, fbaReplenishmentBatchList);
        pageList.setTotal((long) fbaReplenishmentBatchList.size());
        List<FbaReplenishmentBatch> fbaReplenishmentBatchPageList = pageList.getList();
        for (FbaReplenishmentBatch fbaReplenishmentBatch:fbaReplenishmentBatchPageList) {
            String fnsku = fbaReplenishmentBatch.getFnsku();
            String msku = fbaReplenishmentBatch.getMsku();
            String title = fbaReplenishmentBatch.getTitle();
            ///usr/local/server/sspa/      src/main/resources/
            String barcodePath = "/usr/local/server/sspa/image/barcode.png";
            BarcodeUtil.generateFile(fnsku, barcodePath);
            Color contentColor = new Color(0, 0, 0);
            float qualNum = 1.0f;
            String backgroundPath = "/usr/local/server/sspa/image/background.png";
            String content1 = localDate + "                                NewItem";
            String contentFile1 = "/usr/local/server/sspa/image/content1.png";
            ImageHandleHelper.drawStringForImage(backgroundPath,content1,contentColor,3,96,qualNum,contentFile1);
            String content2 = msku + "    made in China";
            String contentFile2 = "/usr/local/server/sspa/image/content2.png";
            ImageHandleHelper.drawStringForImage(contentFile1,content2,contentColor,3,107,qualNum,contentFile2);
            String content3 = "";
            if (title.length()<=40){
                content3 = title;
            }else {
                title.substring(0,40);
            }
            String contentFile3 = "/usr/local/server/sspa/image/content3.png";
            ImageHandleHelper.drawStringForImage(contentFile2,content3,contentColor,3,103,qualNum,contentFile3);
//            String content4 =
//            String contentFile4 = "src/main/resources/image/content4.png";
//            ImageHandleHelper.drawStringForImage(contentFile3,content4,contentColor,3,103,qualNum,contentFile4);
            String resultPath = "/usr/local/server/sspa/image/result.png";
            byte[] bytes = ImageHandleHelper.overlapImageToByte(contentFile2,barcodePath,resultPath);
            BASE64Encoder encoder = new BASE64Encoder();
            String base64Str = encoder.encode(bytes);
            fbaReplenishmentBatch.setBarcodeBase64Str(base64Str);
        }
        return ResultUtil.success(pageList);
    }
}
