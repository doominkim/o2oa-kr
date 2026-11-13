package com.x.attendance.assemble.control.jaxrs.v2.detail;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.detail.model.StatisticWi;
import com.x.attendance.assemble.control.jaxrs.v2.detail.model.StatisticWo;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.general.core.entity.GeneralFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by fancyLou on 2023/3/15.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionStatisticExportExcel extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionStatisticExportExcel.class);
    private static final ReentrantLock lock = new ReentrantLock();
    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        lock.lock();
        
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
                EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            StatisticWi wi = this.convertToWrapIn(jsonElement, StatisticWi.class);
            if (StringUtils.isEmpty(wi.getFilter()) && (wi.getFilterList() == null || wi.getFilterList().isEmpty())) {
                throw new ExceptionEmptyParameter("필터 인원 또는 조직");
            }
            if (StringUtils.isEmpty(wi.getStartDate())) {
                throw new ExceptionEmptyParameter("시작 날짜");
            }
            if (StringUtils.isEmpty(wi.getEndDate())) {
                throw new ExceptionEmptyParameter("종료 날짜");
            }
            LOGGER.info("근태 데이터 통계 시작, filter: {}, start:{}, end:{} ", ListTools.toStringJoin(wi.getFilterList()), wi.getStartDate(), wi.getEndDate());
            // 날짜 검사
            Date startDate = DateTools.parse(wi.getStartDate(), DateTools.format_yyyyMMdd); // 형식 검사
            Date endDate = DateTools.parse(wi.getEndDate(), DateTools.format_yyyyMMdd); // 형식 검사
            if (startDate.after(endDate)) {
                throw new ExceptionDateEndBeforeStartError();
            }
            List<String> userList = new ArrayList<>();
            Business business = new Business(emc);
            if (wi.getFilterList() != null && !wi.getFilterList().isEmpty()) {
                for (String f : wi.getFilterList()) {
                    analysisPerson(userList, f, business);
                }
            } else if (StringUtils.isNotEmpty(wi.getFilter())) {
                analysisPerson(userList, wi.getFilter(), business);
            }
            if (userList.isEmpty()) {
                throw new ExceptionEmptyParameter("필터 인원");
            }
            ActionResult<Wo> result = new ActionResult<>();
            // 사용자별로 반복 조회해 데이터 통계
            List<StatisticWo> wos = new ArrayList<>();
            // 통계 데이터 계산
            statisticDetail(wi, userList, business, wos);
            // 데이터 excel 파일 생성
            Workbook wb = new XSSFWorkbook();
            createExcelFile(wb, wos);
            // excel 생성
            String fileName = "근태통계_"+wi.getStartDate() + "-" + wi.getEndDate() + "_" +DateTools.format(new Date(), DateTools.formatCompact_yyyyMMddHHmmss)+".xlsx";
            wb.write(os);
            StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
            GeneralFile generalFile = new GeneralFile(gfMapping.getName(), fileName,
                    effectivePerson.getDistinguishedName());
            generalFile.saveContent(gfMapping, os.toByteArray(), fileName);
            emc.beginTransaction(GeneralFile.class);
            emc.persist(generalFile, CheckPersistType.all);
            emc.commit();
            Wo wo = new Wo();
            wo.setFlag(generalFile.getId());
            result.setData(wo);
            return result;
        } finally {
            lock.unlock();
            LOGGER.info("통계 종료!");
        }
    }

    private void analysisPerson(List<String> userList, String filter, Business business) throws Exception {
        if (filter.endsWith("@U")) { // 조직을 사용자 목록으로 변환(재귀)
            List<String> users = business.organization().person().listWithUnitSubNested(filter);
            if (users != null && !users.isEmpty()) {
                userList.addAll(users);
            }
        } else if (filter.endsWith("@P")) {
            userList.add(filter);
        }
    }

    private void createExcelFile(Workbook wb, List<StatisticWo> wos) throws Exception {
        // 새 표 생성
        Sheet sheet = wb.createSheet("근태 통계");
        sheet.setDefaultColumnWidth(25);
        // 먼저 헤더 생성
        Row row  = sheet.createRow(0);
        row.createCell(0).setCellValue("이름");
        row.createCell(1).setCellValue("평균 근무시간(시간)");
        row.createCell(2).setCellValue("근무 시간");
        row.createCell(3).setCellValue("출근 일수");
        row.createCell(4).setCellValue("휴무 일수");
        row.createCell(5).setCellValue("결근 일수");
        row.createCell(6).setCellValue("지각 횟수");
        row.createCell(7).setCellValue("조퇴 횟수");
        row.createCell(8).setCellValue("누락 횟수");
        row.createCell(9).setCellValue("외근 횟수");
        if (wos != null && !wos.isEmpty()) {
            for (int i = 0; i < wos.size(); i++) {
                Row _row = sheet.createRow( i + 1 );
                StatisticWo wo = wos.get(i);
                String person = wo.getUserId();
                if (person.contains("@")) {
                    person = person.split("@")[0];
                }
                _row.createCell(0).setCellValue(person);
                _row.createCell(1).setCellValue(wo.getAverageWorkTimeDuration());
                _row.createCell(2).setCellValue(convertMinutesToHoursAndMinutes(wo.getWorkTimeDuration().intValue()));
                _row.createCell(3).setCellValue(wo.getAttendance());
                _row.createCell(4).setCellValue(wo.getRest());
                _row.createCell(5).setCellValue(wo.getAbsenteeismDays());
                _row.createCell(6).setCellValue(wo.getLateTimes());
                _row.createCell(7).setCellValue(wo.getLeaveEarlierTimes());
                _row.createCell(8).setCellValue(wo.getAbsenceTimes());
                _row.createCell(9).setCellValue(wo.getFieldWorkTimes());
            }
        }

    }

    // 분 수를 xx시간xx분 형식으로 변환
    private String convertMinutesToHoursAndMinutes(int workTimeDuration) {
        int hours = workTimeDuration / 60; // 시간 수 계산
        int remainingMinutes = workTimeDuration % 60; // 남은 분 수 계산
        String result = "";

        if (hours > 0) {
            result += hours + " 시간";
        }

        if (remainingMinutes > 0 || StringUtils.isEmpty(result)) {
            result += remainingMinutes + " 분" ;
        }

        return result;
    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = 1380487824637447358L;
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
