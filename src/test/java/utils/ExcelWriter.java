package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;

public class ExcelWriter {

    private static final String FILE_PATH = "ForecastComparison.xlsx";
    private static Workbook workbook;
    private static Sheet sheet;
    private static int rowCount = 0;

    private static void initializeExcel() {
        if (workbook == null) {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Forecast Results");
            
            // Updated header row with day-specific column
            Row header = sheet.createRow(rowCount++);
            header.createCell(0).setCellValue("Store Name");
            header.createCell(1).setCellValue("Store ID");
            header.createCell(2).setCellValue("Expected (Same Day Avg)");
            header.createCell(3).setCellValue("Actual (Yesterday)");
            header.createCell(4).setCellValue("Day Filter");
            header.createCell(5).setCellValue("Deviation");
            header.createCell(6).setCellValue("Percentage Deviation");
            header.createCell(7).setCellValue("Status");
        }
    }

    // Updated method with day name parameter
    public static void writeComparisonResult(String storeName, int storeId, int sameDayAvg, 
                                           int yesterdayActual, int deviation, 
                                           double deviationPercentage, String status, String dayName) {
        initializeExcel();
        
        Row row = sheet.createRow(rowCount++);
        row.createCell(0).setCellValue(storeName);
        row.createCell(1).setCellValue(storeId);
        row.createCell(2).setCellValue(sameDayAvg);
        row.createCell(3).setCellValue(yesterdayActual);
        row.createCell(4).setCellValue(dayName);
        row.createCell(5).setCellValue(deviation);
        row.createCell(6).setCellValue(String.format("%+.2f%%", deviationPercentage));
        row.createCell(7).setCellValue(status);
    }
    
    // Updated method for no data cases
    public static void writeNoDataResult(String storeName, int storeId, String reason) {
        initializeExcel();
        
        Row row = sheet.createRow(rowCount++);
        row.createCell(0).setCellValue(storeName);
        row.createCell(1).setCellValue(storeId);
        row.createCell(2).setCellValue("No Data");
        row.createCell(3).setCellValue("No Data");
        row.createCell(4).setCellValue("N/A");
        row.createCell(5).setCellValue("N/A");
        row.createCell(6).setCellValue("N/A");
        row.createCell(7).setCellValue(reason);
    }

    public static void saveExcel() {
        if (workbook == null) {
            System.out.println("No data to save - workbook is empty");
            return;
        }
        
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
            workbook.write(fos);
            System.out.println("Excel file saved to: " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("Error saving Excel file: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                System.err.println("Error closing workbook: " + e.getMessage());
            }
        }
    }
}
