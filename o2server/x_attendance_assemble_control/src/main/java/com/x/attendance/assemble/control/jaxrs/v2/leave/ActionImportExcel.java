package com.x.attendance.assemble.control.jaxrs.v2.leave;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
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
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.DateTools;
import com.x.general.core.entity.GeneralFile;

/**
 * excel 휴가 데이터 가져오기
 * Created by fancyLou on 2023/4/6.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionImportExcel extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionImportExcel.class);

    private static ReentrantLock lock = new ReentrantLock();

    ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition)
            throws Exception {
        lock.lock();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("휴가 데이터 가져오기 시작!!!!!!");
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
            for (int i = firstRow; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                String person = AttendanceV2Helper.getExcelCellStringValue(row.getCell(0)); // 첫 번째는 person
                if (StringUtils.isEmpty(person)) {
                    setExcelCellError(row, "사용자 식별자는 비워 둘 수 없습니다");
                    continue;
                }
                Person mPerson = business.organization().person().getObject(person, true);
                if (mPerson == null) {
                    setExcelCellError(row, "사용자 식별자에 해당하는 사람을 찾을 수 없습니다");
                    continue;
                }
                String type = AttendanceV2Helper.getExcelCellStringValue(row.getCell(1)); // 두 번째는 외출 유형:출장|교육|기타
                if (StringUtils.isEmpty(type)) {
                    setExcelCellError(row, "외출 유형은 비워 둘 수 없습니다");
                    continue;
                }
                String start = AttendanceV2Helper.getExcelCellStringValue(row.getCell(2)); // 시작 시간: yyyy-MM-dd HH:mm:ss
                if (StringUtils.isEmpty(start)) {
                    setExcelCellError(row, "시작 시간은 비워 둘 수 없습니다");
                    continue;
                }
                Date startDate;
                try {
                    startDate = DateTools.parse(start, DateTools.format_yyyyMMddHHmmss);
                } catch (Exception e) {
                    setExcelCellError(row, "시작 시간 형식이 올바르지 않습니다");
                    continue;
                }
                String end = AttendanceV2Helper.getExcelCellStringValue(row.getCell(3)); // 종료 시간: yyyy-MM-dd HH:mm:ss
                if (StringUtils.isEmpty(end)) {
                    setExcelCellError(row, "종료 시간은 비워 둘 수 없습니다");
                    continue;
                }
                Date endDate;
                try {
                    endDate = DateTools.parse(end, DateTools.format_yyyyMMddHHmmss);
                } catch (Exception e) {
                    setExcelCellError(row, "종료 시간 형식이 올바르지 않습니다");
                    continue;
                }
                String desc = AttendanceV2Helper.getExcelCellStringValue(row.getCell(4)); // 외출 설명
                if (StringUtils.isEmpty(desc)) {
                    desc = "";
                }
                String job = AttendanceV2Helper.getExcelCellStringValue(row.getCell(5)); // 프로세스 jobId, 비워 둘 수 있음
                if (StringUtils.isEmpty(job)) {
                    job = "";
                }
                ActionPost.Wi wi = new ActionPost.Wi();
                wi.setPerson(mPerson.getDistinguishedName());
                wi.setLeaveType(type);
                wi.setStartTime(startDate);
                wi.setEndTime(endDate);
                wi.setDescription(desc);
                wi.setJobId(job);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("데이터 가져오기, row : " + i + " ， " + wi.toString());
                }
                try {
                    ActionPost.Wo postResult = ThisApplication.context().applications().postQuery(x_attendance_assemble_control.class, "v2/leave", wi).getData(ActionPost.Wo.class);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("처리 결과, {}", postResult.toString());
                    }
                } catch (Exception e) {
                    setExcelCellError(row, e.getLocalizedMessage());
                }
            }
            ActionResult<Wo> result = new ActionResult<>();
            String name = "attendance_leave_data_input_" + DateTools.format(new Date(), DateTools.formatCompact_yyyyMMddHHmmss) + ".xlsx";
            workbook.write(os);
            String flag = saveAttachment(os.toByteArray(), name, effectivePerson);
            Wo wo = new Wo();
            wo.setFlag(flag);
            result.setData(wo);
            return result;
        } finally {
            lock.unlock();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("가져오기 종료!!!!!!");
            }
        }
    }

    private void setExcelCellError(Row row, String error) {
        AttendanceV2Helper.setExcelCellError(row, error, 7);
    }

    private String saveAttachment(byte[] bytes, String attachmentName, EffectivePerson effectivePerson)
            throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
            GeneralFile generalFile = new GeneralFile(gfMapping.getName(), attachmentName,
                    effectivePerson.getDistinguishedName());
            generalFile.saveContent(gfMapping, bytes, attachmentName);
            emc.beginTransaction(GeneralFile.class);
            emc.persist(generalFile, CheckPersistType.all);
            emc.commit();
            return generalFile.getId();
        }
    }

    public static class Wo extends GsonPropertyObject {

        @FieldDescribe("반환 결과 식별자, 결과 파일 다운로드에 사용")
        private String flag;

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

    }
}
