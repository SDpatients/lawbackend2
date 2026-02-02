package com.lawbackend2.lawbackend2.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EasyExcelUtil {

    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz) throws IOException {
        List<T> dataList = new ArrayList<>();
        
        EasyExcel.read(file.getInputStream(), clazz, new AnalysisEventListener<T>() {
            @Override
            public void invoke(T data, AnalysisContext context) {
                dataList.add(data);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                log.info("Excel导入完成，共{}条数据", dataList.size());
            }
        }).sheet().doRead();
        
        return dataList;
    }

    public static void exportExcel(HttpServletResponse response, List<?> dataList, Class<?> clazz, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), clazz)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("Sheet1")
                .doWrite(dataList);
    }

    public static <T> void exportExcelWithListener(HttpServletResponse response, List<?> dataList, Class<?> clazz, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), clazz)
                .sheet("Sheet1")
                .doWrite(dataList);
    }
}
