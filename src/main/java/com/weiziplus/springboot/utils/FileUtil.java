package com.weiziplus.springboot.utils;

import com.alibaba.fastjson.JSON;
import com.weiziplus.springboot.config.GlobalConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * @author wanglongwei
 * @data 2019/6/20 15:45
 */
@Slf4j
public class FileUtil {

    /**
     * 文件上传
     *
     * @param file
     * @param mkdir 如果分文件夹存放，传入文件夹
     * @return 成功返回路径，失败返回null
     */
    public static String upFile(MultipartFile file, String mkdir) {
        if (file.isEmpty()) {
            return null;
        }
        String resultPath = "";
        if (StringUtil.notBlank(mkdir)) {
            resultPath = mkdir + File.separatorChar;
        }
        // 获取原始名字
        String fileName = file.getOriginalFilename();
        // 如果原始名字为null获取当前名字
        if (StringUtil.isBlank(fileName)) {
            fileName = file.getName();
        }
        //  获取文件后缀类型
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        // 生成新文件名
        fileName = StringUtil.createUUID() + suffixName;
        resultPath = resultPath + fileName;
        File dest = new File(GlobalConfig.BASE_FILE_PATH + resultPath);
        if (!dest.getParentFile().exists()) {
            boolean flag = dest.getParentFile().mkdirs();
            if (!flag) {
                return null;
            }
        }
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.warn("文件上传失败".concat(e.getMessage()));
            return null;
        }
        return resultPath;
    }

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    public static String upFile(MultipartFile file) {
        return upFile(file, null);
    }

    public static void downResourceFile(HttpServletResponse response, String path) {
        InputStream inputStream = null;
        ServletOutputStream servletOutputStream = null;
        try {
            response.setContentType("application/vnd.ms-excel");
            response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.addHeader("charset", "utf-8");
            response.addHeader("Pragma", "no-cache");
            ClassPathResource classPathResource = new ClassPathResource(path);
            inputStream = classPathResource.getInputStream();
            servletOutputStream = response.getOutputStream();
            IOUtils.copy(inputStream, servletOutputStream);
            response.flushBuffer();
        } catch (Exception e) {
            log.warn("文件下载失败---路径:" + path + "---详情:" + e.getMessage());
            handleResponse(response, ResultUtil.error("文件下载失败---详情:" + e.getMessage()));
        } finally {
            try {
                if (null != servletOutputStream) {
                    servletOutputStream.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (Exception e) {
                log.warn("文件下载流关闭失败");
            }
        }
    }

    /**
     * 将出错信息输入到前端页面
     *
     * @param response
     * @param errResult
     */
    private static void handleResponse(HttpServletResponse response, ResultUtil errResult) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=utf-8");
            out = response.getWriter();
            out.print(JSON.toJSONString(errResult));
        } catch (Exception e) {
            log.warn("token失效输入到前端页面出错，catch" + e);
        } finally {
            if (null != out) {
                out.flush();
                out.close();
            }
        }
    }
}
