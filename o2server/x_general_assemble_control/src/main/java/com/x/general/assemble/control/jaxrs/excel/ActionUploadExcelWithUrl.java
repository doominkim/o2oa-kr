package com.x.general.assemble.control.jaxrs.excel;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ActionUploadExcelWithUrl extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionUploadExcelWithUrl.class);


    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

        LOGGER.info("ActionUploadExcelWithUrl receive:{}.", jsonElement.toString());

        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

        int sheetIndexValue = wi.getSheetIndex();
        int rowIndexValue = wi.getRowIndex();

        if (StringUtils.isEmpty(wi.getFileUrl())) {
            throw new Exception("fileUrl은 비워 둘 수 없습니다!");
        }


        byte[] bytes = CipherConnectionAction.getBinary(false, wi.getFileUrl());
        if (bytes == null || bytes.length == 0) {
            throw new IllegalStateException("can not down file from url.");
        }
        LOGGER.info("bytes-len===:{}.", bytes.length);

        try (InputStream is = new ByteArrayInputStream(bytes);
             XSSFWorkbook workbook = new XSSFWorkbook(is);) {

                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

                if (sheetIndexValue < 0 || sheetIndexValue >= workbook.getNumberOfSheets()) {
                    throw new Exception("잘못된 sheet 인덱스: " + sheetIndexValue +
                            ", 전체 " + workbook.getNumberOfSheets() + " 개 sheet가 있습니다.");
                }
                Sheet sheet = workbook.getSheetAt(sheetIndexValue); // sheet

                int firstRow = sheet.getFirstRowNum(); // 첫 번째 행
                int lastRow = sheet.getLastRowNum();

                //  rowIndex가 유효 범위 내인지 검증
                if (rowIndexValue < firstRow) {
                    rowIndexValue = firstRow; // 첫 번째 행으로 자동 보정
                }
                if (rowIndexValue > lastRow) {
                    // rowIndex부터 데이터 없음
                    LOGGER.warn("지정한 시작 행 번호 {}이(가) 데이터 범위(최대 행 번호 {})를 벗어나 빈 데이터를 반환합니다.", rowIndexValue, lastRow);
                    ActionResult<Wo> result = new ActionResult<>();
                    Wo wo = new Wo();
                    wo.setDataList(new ArrayList<>()); // 빈 목록
                    result.setData(wo);
                    return result;
                }

                //모든 행 데이터 저장용
                List<List<Object>> rowList = new ArrayList<>();

                // 모든 행을 먼저 스캔하여 최대 열 수 확인
                int maxColumnCount = 0;
                for (int i = rowIndexValue; i <= lastRow; i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        int lastCellNum = row.getLastCellNum(); // 주의: 열 개수임(0부터 시작하는 인덱스+1)
                        if (lastCellNum > maxColumnCount) {
                            maxColumnCount = lastCellNum;
                        }
                    }
                }
//                LOGGER.info("excel 내용 최대 열 수: {}", maxColumnCount);
                for (int i = rowIndexValue; i <= lastRow; i++) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("처리 행 수: " + i + " ========================================");
                    }
                    Row row = sheet.getRow(i);
                    List<Object> colList = new ArrayList<>();

                    try {
                        if (row == null) {
//                            LOGGER.info("빈 행일 때 최대 열 수: {}", maxColumnCount);
                            // 빈 행: maxColumnCount개의 빈 문자열 추가
                            for (int j = 0; j < maxColumnCount; j++) {
                                colList.add("");
                            }
                        }else{
                            for (int j = 0; j < maxColumnCount; j++) {
//                                Cell cell = row != null ? row.getCell(j) : null;
                                Cell cell = row.getCell(j);
                                if (cell == null) {
                                    colList.add(""); // 존재하지 않는 행 또는 셀
                                } else {
                                    colList.add(getCellValueAsString(cell,evaluator));
                                }
                            }
                        }

                    } catch (Exception e) {

                        LOGGER.warn("{}번째 행 처리 중 오류 발생: {}", i, e.getMessage());
                        colList.clear();
                        for (int j = 0; j < maxColumnCount; j++) {
                            colList.add("");
                        }
                    }

                    rowList.add(colList);

                }
//                LOGGER.debug("파싱한 excel 전체 데이터: {} ", rowList);
                ActionResult<Wo> result = new ActionResult<>();

                Wo wo = new Wo();
                wo.setDataList(rowList);
                result.setData(wo);
                return result;
        }
    }
    /**
     * Excel 셀 값을 안전하게 문자열로 변환
     * 날짜 형식 자동 판단: 시간이 있으면 시분초까지, 없으면 날짜만 출력
     */
    private Object getCellValueAsString(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) {
            return "";
        }

        // 수식 처리
        if (cell.getCellType() == CellType.FORMULA) {
            try {
                CellValue evaluated = evaluator.evaluate(cell);
                if (evaluated == null) return "";

                switch (evaluated.getCellType()) {
                    case STRING:
                        return evaluated.getStringValue();
                    case NUMERIC:
                        // 주의: 수식 결과도 날짜일 수 있음
                        return formatNumericValue(evaluated.getNumberValue(), cell);
                    case BOOLEAN:
                        return String.valueOf(evaluated.getBooleanValue());
                    case BLANK:
                        return "";
                    default:
                        return "";
                }
            } catch (Exception e) {
                LOGGER.warn("수식 계산 실패, 오류: {}", e.getMessage());
                return ""; // 수식 오류 시 빈 값 반환
            }
        }

        // 수식이 아니면 유형별 처리
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return formatDateValue(cell.getDateCellValue(), cell);
                } else {
//                    LOGGER.info("처리 값: {}", cell.getNumericCellValue());
                    return formatNumericValue(cell.getNumericCellValue(), null);
                }


            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case BLANK:
            case ERROR:
            default:
                return "";
        }
    }

    /**
     * 수치(정수, 소수) 서식화, 과학적 표기법 방지
     */
    private Object formatNumericValue(double value, Cell cell) {
        // 특수 값 처리
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return String.valueOf(value); // NaN 또는 Infinity는 BigDecimal로 표현 불가
        }

        // 날짜 형식 여부 우선 판단
        if (cell != null && DateUtil.isCellDateFormatted(cell)) {
            return formatDateValue(DateUtil.getJavaDate(value), cell);
        }

        // String.valueOf로 double 정밀도 문제 방지
        try {
            BigDecimal bd = new BigDecimal(String.valueOf(value));
//            LOGGER.info("처리 후 값: {}", bd.stripTrailingZeros().toPlainString());
            return Double.valueOf(bd.stripTrailingZeros().toPlainString()); // 끝의 불필요한 0 제거
        } catch (NumberFormatException e) {
            // fallback
            return value; // 원래 double 반환
        }
    }


    /**
     * 셀 서식 문자열에 따라 날짜 자동 서식화
     */
    private String formatDateValue(Date date, Cell cell) {
        if (date == null) return "";

        // 셀 서식 문자열 조회, 예: "m/d/yy" 또는 "yyyy-mm-dd hh:mm"
        short formatIndex = cell.getCellStyle().getDataFormat();
        String formatString = cell.getCellStyle().getDataFormatString();

        // 시간을 포함하는 일반적인 키워드
        boolean hasTime = formatString != null &&
                (formatString.contains("H") || formatString.contains("h") ||
                        formatString.contains("M") && formatString.contains(":") ||
                        formatString.contains("m") && formatString.contains(":") ||
                        formatString.toLowerCase().contains("am") ||
                        formatString.toLowerCase().contains("pm"));

        SimpleDateFormat sdf;
        if (hasTime) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }

        return sdf.format(date);
    }


    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 6022979308455360364L;

        @FieldDescribe("*excel 파일 출처 url 주소.")
        @Schema(description = "*excel 파일 출처 url 주소.")
        private String fileUrl;

        @FieldDescribe("*읽을 내용의 시작 행 인덱스, 기본값 0.")
        @Schema(description = "*읽을 내용의 시작 행 인덱스, 기본값 0.")
        private Integer rowIndex = 0;

        @FieldDescribe("*읽을 내용의 Sheet 인덱스, 기본값 0.")
        @Schema(description = "*읽을 내용의 Sheet 인덱스, 기본값 0.")
        private Integer sheetIndex = 0;


        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public Integer getRowIndex() {
            return rowIndex;
        }

        public void setRowIndex(Integer rowIndex) {
            this.rowIndex = rowIndex;
        }

        public Integer getSheetIndex() {
            return sheetIndex;
        }

        public void setSheetIndex(Integer sheetIndex) {
            this.sheetIndex = sheetIndex;
        }
    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = 6022979308455360363L;
        @FieldDescribe("excel에서 파싱한 내용, 예: ['이름','성별'],['홍길동','남']")
        public List<List<Object>> dataList;


        public List<List<Object>> getDataList() {
            return dataList;
        }

        public void setDataList(List<List<Object>> dataList) {
            this.dataList = dataList;
        }

    }
}
