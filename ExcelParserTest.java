import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ExcelParserTest {
    public static void main(String[] args) {
        String filePath = "d:\\Ai\\lawbackend2\\已申报债权登记簿(1).xls";
        try {
            FileInputStream fis = new FileInputStream(new File(filePath));
            Workbook workbook;
            
            // check file type
            if (filePath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else {
                workbook = new HSSFWorkbook(fis);
            }
            
            // get first sheet
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("Sheet name: " + sheet.getSheetName());
            System.out.println("Total rows: " + sheet.getLastRowNum() + 1);
            
            // read header
            Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                System.out.println("Header row cells count: " + headerRow.getLastCellNum());
                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                    Cell cell = headerRow.getCell(j);
                    String headerValue = getCellValueAsString(cell);
                    System.out.println("Header cell " + j + ": " + headerValue);
                }
            }
            
            // read first 5 rows data
            for (int i = 1; i <= Math.min(5, sheet.getLastRowNum()); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    System.out.println("\nRow " + i + " cells count: " + row.getLastCellNum());
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        Cell cell = row.getCell(j);
                        String cellValue = getCellValueAsString(cell);
                        System.out.println("Cell " + j + ": " + cellValue);
                    }
                }
            }
            
            workbook.close();
            fis.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}