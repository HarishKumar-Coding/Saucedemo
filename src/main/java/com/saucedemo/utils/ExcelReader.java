package com.saucedemo.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads test data from Excel (.xls / .xlsx) into Object[][] for TestNG DataProviders.
 * Header keys are normalized to lowercase (e.g. "Username" → "username").
 */
public final class ExcelReader {

    private ExcelReader() {
    }

    public static Object[][] getTestData(String filePath, String sheetName) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet not found: '" + sheetName + "' in " + filePath
                        + ". Available sheets: " + getSheetNames(workbook));
            }

            return readSheet(sheet);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read Excel file: " + filePath, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> getFirstRowByIndex(String filePath, int sheetIndex) {
        Object[][] data = getTestDataByIndex(filePath, sheetIndex);
        if (data.length == 0 || data[0].length == 0 || data[0][0] == null) {
            throw new RuntimeException("No data rows in sheet index " + sheetIndex + " of " + filePath);
        }
        return (Map<String, String>) data[0][0];
    }

    public static Object[][] getTestDataByIndex(String filePath, int sheetIndex) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            if (sheetIndex < 0 || sheetIndex >= workbook.getNumberOfSheets()) {
                throw new RuntimeException("Sheet index out of range: " + sheetIndex);
            }
            return readSheet(workbook.getSheetAt(sheetIndex));
        } catch (IOException e) {
            throw new RuntimeException("Unable to read Excel file: " + filePath, e);
        }
    }

    private static Object[][] readSheet(Sheet sheet) {
        int rowCount = sheet.getPhysicalNumberOfRows();
        if (rowCount < 2) {
            return new Object[0][0];
        }

        Row headerRow = sheet.getRow(0);
        int colCount = headerRow.getLastCellNum();
        DataFormatter formatter = new DataFormatter();

        List<Object[]> rows = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            Map<String, String> rowData = new HashMap<>();
            boolean hasValue = false;
            for (int j = 0; j < colCount; j++) {
                String header = formatter.formatCellValue(headerRow.getCell(j)).trim().toLowerCase();
                if (header.isEmpty()) {
                    continue;
                }
                Cell cell = row.getCell(j);
                String value = formatter.formatCellValue(cell).trim();
                if (!value.isEmpty()) {
                    hasValue = true;
                }
                rowData.put(header, value);
                rowData.put(header.replaceAll("[^a-z0-9]", ""), value);
            }
            if (hasValue) {
                rows.add(new Object[]{rowData});
            }
        }
        return rows.toArray(new Object[0][0]);
    }

    private static String getSheetNames(Workbook workbook) {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            names.add(workbook.getSheetName(i));
        }
        return names.toString();
    }
}
