package com.telephonemanager.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class ExcelExportUtil {
    public static ByteArrayInputStream writeToExcel(List<String[]> rows) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Export");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Row style (alternating)
            CellStyle evenRowStyle = workbook.createCellStyle();
            evenRowStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            evenRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < rows.size(); i++) {
                Row row = sheet.createRow(i);
                String[] data = rows.get(i);
                for (int j = 0; j < data.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(data[j]);
                    if (i == 0) {
                        cell.setCellStyle(headerStyle);
                    } else if (i % 2 == 0) {
                        cell.setCellStyle(evenRowStyle);
                    }
                }
            }
            // Auto-size columns
            if (!rows.isEmpty()) {
                for (int i = 0; i < rows.get(0).length; i++) {
                    sheet.autoSizeColumn(i);
                }
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel file", e);
        }
    }
} 