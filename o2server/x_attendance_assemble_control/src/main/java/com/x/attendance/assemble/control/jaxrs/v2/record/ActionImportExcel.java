package com.x.attendance.assemble.control.jaxrs.v2.record;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.x_attendance_assemble_control;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.general.core.entity.GeneralFile;

/**
 * Created by fancyLou on 2023/4/24.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionImportExcel extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionImportExcel.class);

    private static ReentrantLock lock = new ReentrantLock();

    ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition)
            throws Exception {
        lock.lock();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("체크 기록 데이터 가져오기 시작!!!!!!");
        }
        try (InputStream is = new ByteArrayInputStream(bytes);
             XSSFWorkbook workbook = new XSSFWorkbook(is);
             ByteArrayOutputStream os = new ByteArrayOutputStream();
             EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(effectivePerson)) {
                throw new ExceptionAccessDenied(effectivePerson);
            }
            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 sheet
            // 고정 템플릿
            int firstRow = sheet.getFirstRowNum() + 1; // 첫 행은 제목이므로 건너뜀
            int lastRow = sheet.getLastRowNum();
            // 오류 건수 count
            int errorRowNumber = 0;
            for (int i = firstRow; i <= lastRow; i++) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("처리 행 수: " + i + " ========================================");
                }
                Row row = sheet.getRow(i);
                try {
                    ActionPostDailyRecord.Wi thisWi = new ActionPostDailyRecord.Wi();
                    thisWi.setPerson(AttendanceV2Helper.getExcelCellStringValue(row.getCell(0)));
                    thisWi.setDate(AttendanceV2Helper.getExcelCellDateFormat(row.getCell(1), DateTools.format_yyyyMMdd));
                    thisWi.setOnDutyTime1(AttendanceV2Helper.getExcelCellDateFormat(row.getCell(2), DateTools.format_HHmm));
                    thisWi.setOffDutyTime1(AttendanceV2Helper.getExcelCellDateFormat(row.getCell(3), DateTools.format_HHmm));
                    thisWi.setOnDutyTime2(AttendanceV2Helper.getExcelCellDateFormat(row.getCell(4), DateTools.format_HHmm));
                    thisWi.setOffDutyTime2(AttendanceV2Helper.getExcelCellDateFormat(row.getCell(5), DateTools.format_HHmm));
                    thisWi.setOnDutyTime3(AttendanceV2Helper.getExcelCellDateFormat(row.getCell(6), DateTools.format_HHmm));
                    thisWi.setOffDutyTime3(AttendanceV2Helper.getExcelCellDateFormat(row.getCell(7), DateTools.format_HHmm));
                    ActionPostDailyRecord.Wo result = ThisApplication.context().applications().postQuery(x_attendance_assemble_control.class, "v2/record/import/daily", thisWi).getData(ActionPostDailyRecord.Wo.class);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("처리 결과, {}", result.toString());
                    }
                } catch (Exception e) {
                    AttendanceV2Helper.setExcelCellError(row, e.getLocalizedMessage(), 8);
                    errorRowNumber++;
                }
            }
            ActionResult<Wo> result = new ActionResult<>();
            // excel 파일 저장
            String name = "attendance_record_data_input_" + DateTools.format(new Date(), DateTools.formatCompact_yyyyMMddHHmmss) + ".xlsx";
            workbook.write(os);
            StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
            GeneralFile generalFile = new GeneralFile(gfMapping.getName(), name,
                    effectivePerson.getDistinguishedName());
            generalFile.saveContent(gfMapping, os.toByteArray(), name);
            emc.beginTransaction(GeneralFile.class);
            emc.persist(generalFile, CheckPersistType.all);
            emc.commit();
            Wo wo = new Wo();
            wo.setFlag(generalFile.getId());
            wo.setErrorRows(errorRowNumber);
            result.setData(wo);
            return result;
        } finally {
            lock.unlock();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("가져오기 종료!!!!!!");
            }
        }
    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = 6022979308455360363L;
        @FieldDescribe("반환 결과 식별자, 결과 파일 다운로드에 사용")
        private String flag;
        @FieldDescribe("오류 데이터 건수")
        private int errorRows;

        public int getErrorRows() {
            return errorRows;
        }

        public void setErrorRows(int errorRows) {
            this.errorRows = errorRows;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

    }
}
