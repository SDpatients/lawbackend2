import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;

import java.util.List;

public class ExcelWriter {

    public static <T> void writeToLocalFile(String filePath, List<T> dataList, Class<T> clazz) {
        EasyExcel.write(filePath, clazz)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("已申报债权登记簿")
                .doWrite(dataList);
    }
}
