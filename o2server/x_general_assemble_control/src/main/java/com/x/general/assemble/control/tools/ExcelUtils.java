package com.x.general.assemble.control.tools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excel 유틸리티 클래스(xls, xlsx 호환)
 *
 * @author qijian
 * @version 1.0.0
 *
 */
public class ExcelUtils {

    private static final String XLS = "xls";
    private static final String XLSX = "xlsx";
    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /**
     * 사용자 정의 템플릿 Excel 출력 스트림으로 데이터 출력
     *
     * @param excelTemplate 사용자 정의 템플릿 파일
     * @param data 데이터
     * @param outputStream Excel 출력 스트림
     * @throws IOException 오류 시 예외 발생, 호출자가 처리
     */
    public static void writeDataToTemplateOutputStream(File excelTemplate, List<List<Object>> data, OutputStream outputStream) throws IOException {
        Workbook book = ExcelUtils.getWorkbookFromExcel(excelTemplate);
        ExcelUtils.writeDataToWorkbook(null, data, book, 0);
        ExcelUtils.writeWorkbookToOutputStream(book, outputStream);
    }

    public static void writeDataToTemplateFile(byte[] excelTemplateBytes, List<SheetData> sheetDataList, OutputStream outputStream) throws IOException {
        Workbook book = ExcelUtils.getWorkbookFromExcel(excelTemplateBytes);
        ExcelUtils.writeDataToWorkbook(sheetDataList, book);
        ExcelUtils.writeWorkbookToOutputStream(book, outputStream);
    }

    /**
     * Excel 파일에서 Workbook 객체 조회
     *
     * @param excelTemplateBytes Excel 파일
     * @return Workbook 객체
     * @throws IOException 오류 시 예외 발생, 호출자가 처리
     */
    public static Workbook getWorkbookFromExcel(byte[] excelTemplateBytes) throws IOException {
        try (
                InputStream inputStream = new ByteArrayInputStream(excelTemplateBytes);
        ) {
            return new XSSFWorkbook(inputStream);
        }
    }

    /**
     * Excel 파일에서 Workbook 객체 조회
     *
     * @param excelFile Excel 파일
     * @return Workbook 객체
     * @throws IOException 오류 시 예외 발생, 호출자가 처리
     */
    public static Workbook getWorkbookFromExcel(File excelFile) throws IOException {
        try (
                InputStream inputStream = new FileInputStream(excelFile);
        ) {

            if (excelFile.getName().endsWith(XLS)) {
                return new HSSFWorkbook(inputStream);
            }

            else if (excelFile.getName().endsWith(XLSX)) {
                return new XSSFWorkbook(inputStream);
            }

            else {
                throw new IOException("파일 형식 오류");
            }
        }
    }

    /**
     * Workbook 객체 내용을 Excel 파일로 출력
     *
     * @param book Workbook 객체
     * @param file Excel 파일
     * @throws FileNotFoundException 파일을 찾을 수 없음 예외(파일이 이미 생성되므로 실제로는 발생하지 않음)
     * @throws IOException 입출력 예외
     */
    public static void writeWorkbookToFile(Workbook book, File file) throws FileNotFoundException, IOException {
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        try (
                OutputStream outputStream = new FileOutputStream(file);
        ) {
            writeWorkbookToOutputStream(book, outputStream);
        }
    }

    /**
     * Workbook 객체를 Excel 출력 스트림으로 출력
     *
     * @param book Workbook 객체
     * @param outputStream Excel 출력 스트림
     * @throws IOException 오류 시 예외 발생, 호출자가 처리
     */
    public static void writeWorkbookToOutputStream(Workbook book, OutputStream outputStream) throws IOException {
        book.write(outputStream);
    }

    /**
     * Workbook 객체에 데이터 출력
     * @param sheetDataList List<SheetData>
     * @param book Workbook
     */
    public static void writeDataToWorkbook(List<SheetData> sheetDataList, Workbook book) {
        int numberOfSheets = book.getNumberOfSheets();
        for (int page=0; page<sheetDataList.size(); page++) {
            SheetData sheetData = sheetDataList.get(page);
            Sheet sheet;
            if(page < numberOfSheets) {
                sheet = book.getSheetAt(page);
                if(StringUtils.isNotBlank(sheetData.getSheetName())){
                    book.setSheetName(page, sheetData.getSheetName());
                }
            }else{
                if(StringUtils.isNotBlank(sheetData.getSheetName())){
                    sheet = book.createSheet(sheetData.getSheetName());
                }else{
                    sheet = book.createSheet();
                }
            }
            Row row = null;
            Cell cell = null;
            List<String> rowData = null;
            int startRow = sheetData.getStartRow() == null || sheetData.getStartRow() < 0 ? 0 : sheetData.getStartRow();
            for (int i = 0; i < sheetData.getDataList().size(); i++) {
                row = sheet.getRow(i + startRow);
                if (null == row) {
                    row = sheet.createRow(i + startRow);
                }
                rowData = sheetData.getDataList().get(i);
                if (null == rowData) {
                    continue;
                }
                for (int j = 0; j < rowData.size(); j++) {
                    cell = row.getCell(j);
                    if (null == cell) {
                        cell = row.createCell(j);
                    }
                    setValue(cell, rowData.get(j));
                }
            }
        }
    }

    /**
     * Workbook 객체의 지정 페이지에 데이터 출력
     *
     * @param title 제목(첫 행에 기록, null 가능)
     * @param data 데이터
     * @param book Workbook 객체
     * @param page 데이터를 출력할 Workbook 페이지 번호
     */
    public static void writeDataToWorkbook(List<String> title, List<List<Object>> data, Workbook book, int page) {
        Sheet sheet = book.getSheetAt(page);

        Row row = null;
        Cell cell = null;

        // 헤더 설정
        if (null != title && !title.isEmpty()) {
            row = sheet.getRow(0);
            if (null == row) {
                row = sheet.createRow(0);
            }

            for (int i = 0; i < title.size(); i++) {
                cell = row.getCell(i);
                if (null == cell) {
                    cell = row.createCell(i);
                }
                cell.setCellValue(title.get(i));
            }
        }

        List<Object> rowData = null;
        for (int i = 0; i < data.size(); i++) {

            row = sheet.getRow(i + 1);
            if (null == row) {
                row = sheet.createRow(i + 1);
            }

            rowData = data.get(i);
            if (null == rowData) {
                continue;
            }
            for (int j = 0; j < rowData.size(); j++) {
                cell = row.getCell(j);
                if (null == cell) {
                    cell = row.createCell(j);
                }
                setValue(cell, rowData.get(j));
            }
        }
    }

    /**
     * Excel 파일 첫 페이지 읽기
     *
     * @param pathname 파일 경로명
     * @return 첫 페이지 데이터 목록
     * @throws IOException 오류 시 예외 발생, 호출자가 처리
     */
    public static List<List<Object>> readExcelFirstSheet(String pathname) throws IOException {
        File file = new File(pathname);
        return readExcelFirstSheet(file);

    }

    /**
     * Excel 파일 첫 페이지 읽기
     *
     * @param file Excel 파일
     * @return 첫 페이지 데이터 목록
     * @throws IOException 오류 시 예외 발생, 호출자가 처리
     */
    public static List<List<Object>> readExcelFirstSheet(File file) throws IOException {
        try (
                InputStream inputStream = new FileInputStream(file);
        ) {

            if (file.getName().endsWith(XLS)) {
                return readXlsFirstSheet(inputStream);
            }

            else if (file.getName().endsWith(XLSX)) {
                return readXlsxFirstSheet(inputStream);
            }

            else {
                throw new IOException("파일 형식 오류");
            }
        }

    }

    /**
     * xls 형식 Excel 파일 첫 페이지 읽기
     *
     * @param inputStream Excel 파일 입력 스트림
     * @return 첫 페이지 데이터 목록
     * @throws IOException 오류 시 예외 발생, 호출자가 처리
     */
    public static List<List<Object>> readXlsFirstSheet(InputStream inputStream) throws IOException {
        Workbook workbook = new HSSFWorkbook(inputStream);
        return readExcelFirstSheet(workbook);
    }

    /**
     * xlsx 형식 Excel 파일 첫 페이지 읽기
     *
     * @param inputStream Excel 파일 입력 스트림
     * @return 첫 페이지 데이터 목록
     * @throws IOException 오류 시 예외 발생, 호출자가 처리
     */
    public static List<List<Object>> readXlsxFirstSheet(InputStream inputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        return readExcelFirstSheet(workbook);
    }

    /**
     * Workbook 첫 페이지 읽기
     *
     * @param book Workbook 객체
     * @return 첫 페이지 데이터 목록
     */
    public static List<List<Object>> readExcelFirstSheet(Workbook book) {
        return readExcel(book, 0, 0);
    }

    /**
     * 지정 페이지의 Excel 읽기
     *
     * @param book Workbook 객체
     * @param page 페이지 번호
     * @param startRow 시작 행
     * @return 지정 페이지 데이터 목록
     */
    public static List<List<Object>> readExcel(Workbook book, int page, int startRow) {
        List<List<Object>> list = new ArrayList<>();
        Sheet sheet = book.getSheetAt(page);
        for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (null == row) {
                continue;
            }

            List<Object> columns = new ArrayList<>();
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                columns.add(getValue(cell));
            }

            list.add(columns);
        }

        return list;
    }

    /**
     * 셀 값 파싱
     *
     * @param cell 셀
     * @return 셀 값
     */
    private static Object getValue(Cell cell) {
        if (null != cell) {
            switch (cell.getCellType()) {
                case BLANK:
                    return "";
                case BOOLEAN:
                    return BooleanUtils.toString(cell.getBooleanCellValue(), "true", "false", "false");
                case ERROR:
                    return "";
                case FORMULA:
                    return "";
                case NUMERIC:
                    Double d = cell.getNumericCellValue();
                    Long l = d.longValue();
                    if (l.doubleValue() == d) {
                        return l.toString();
                    } else {
                        return d.toString();
                    }
                default:
                    return cell.getStringCellValue().trim();
            }
        }
        return "";
    }

    /**
     * 셀 값 설정
     *
     * @param cell 셀
     * @param value 값
     */
    private static void setValue(Cell cell, Object value) {
        if (null == cell) {
            return;
        }

        if (null == value) {
            cell.setCellValue((String) null);
        }

        else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }

        else if (value instanceof Date) {
            cell.setCellValue(FORMAT.format((Date) value));
        }

        else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        }

        else {
            cell.setCellValue(value.toString());
        }

    }

}
