package com.x.attendance.assemble.control.jaxrs.v2.leave;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.stream.IntStream;

/**
 * Created by fancyLou on 2023/4/6.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionExcelTemplate extends BaseAction {

    final String tempName = "attendance_leave_data_input_template.xlsx";

    ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ActionResult<Wo> result = new ActionResult<>();

            XSSFSheet sheet = workbook.createSheet("외출 데이터 가져오기");
            sheet.setDefaultColumnWidth(25);
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("사용자 식별자");
            cell = row.createCell(1);
            cell.setCellValue("외출 유형: 출장|교육|기타");
            cell = row.createCell(2);
            cell.setCellValue("시작 시간: yyyy-MM-dd HH:mm:ss");
            cell = row.createCell(3);
            cell.setCellValue("종료 시간: yyyy-MM-dd HH:mm:ss");
            cell = row.createCell(4);
            cell.setCellValue("외출 설명");
            cell = row.createCell(5);
            cell.setCellValue("결재 jobId, 비워 둘 수 있음");
            cell = row.createCell(7);
            cell.setCellValue("가져오기 오류 정보");
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setWrapText(true);
            IntStream.rangeClosed(0, 6).forEach(i -> {
                sheet.setDefaultColumnStyle(i, cellStyle);
            });

            workbook.write(os);
            Wo wo = new Wo(os.toByteArray(), this.contentType(true, tempName), this.contentDisposition(true, tempName));
            result.setData(wo);
            return result;
        }
    }
    public static class Wo extends WoFile {

        public Wo(byte[] bytes, String contentType, String contentDisposition) {
            super(bytes, contentType, contentDisposition);
        }

    }
}
