import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ExcelHeaderReader {
    public static void main(String[] args) {
        String filePath = "d:\\Ai\\lawbackend2\\已申报债权登记簿(1).xls";
        
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = WorkbookFactory.create(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("Sheet name: " + sheet.getSheetName());
            System.out.println("Total rows: " + (sheet.getLastRowNum() + 1));
            
            // Find header row
            int headerRowIndex = findHeaderRowIndex(sheet);
            Row headerRow = sheet.getRow(headerRowIndex);
            
            if (headerRow != null) {
                System.out.println("\nHeader row index: " + headerRowIndex);
                System.out.println("Header cells:");
                
                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                    Cell cell = headerRow.getCell(j);
                    String headerValue = getCellValueAsString(cell);
                    System.out.println("Column " + j + ": " + headerValue);
                }
                
                // Print first 5 data rows as example
                System.out.println("\nFirst 5 data rows:");
                for (int i = headerRowIndex + 1; i <= Math.min(headerRowIndex + 5, sheet.getLastRowNum()); i++) {
                    Row dataRow = sheet.getRow(i);
                    if (dataRow != null) {
                        System.out.print("Row " + i + ": ");
                        for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                            Cell cell = dataRow.getCell(j);
                            String cellValue = getCellValueAsString(cell);
                            System.out.print(cellValue + " | ");
                        }
                        System.out.println();
                    }
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static int findHeaderRowIndex(Sheet sheet) {
        int maxNonEmptyCells = 0;
        int headerRowIndex = 0;
        
        // Check first 10 rows, find the row with most non-empty cells as header row
        for (int i = 0; i <= Math.min(10, sheet.getLastRowNum()); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                int nonEmptyCells = 0;
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null && !getCellValueAsString(cell).trim().isEmpty()) {
                        nonEmptyCells++;
                    }
                }
                if (nonEmptyCells > maxNonEmptyCells) {
                    maxNonEmptyCells = nonEmptyCells;
                    headerRowIndex = i;
                }
            }
        }
        return headerRowIndex;
    }
    
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numValue = cell.getNumericCellValue();
                    if (numValue == (long) numValue) {
                        return String.valueOf((long) numValue);
                    }
                    return String.valueOf(numValue);
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
