package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.entity.v2.AttendanceV2Holiday;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.tools.DateTools;

public class ActionHolidayGetWithDate extends BaseAction {

    private static final String RESULT_WORKDAY = "WORKDAY";
    private static final String RESULT_OFFDAY = "OFFDAY";
    private static final String RESULT_NOT_FOUND = "NOT_FOUND";

    ActionResult<Wo> execute(String dateString) throws Exception {
        if (StringUtils.isBlank(dateString) || DateTools.parseDate(dateString) == null) {
            throw new ExceptionWithMessage("날짜는 비워 둘 수 없습니다. 형식: yyyy-MM-dd");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            wo.setDateString(dateString);
            List<AttendanceV2Holiday> list = emc.listEqual(AttendanceV2Holiday.class,
                    AttendanceV2Holiday.dateString_FIELDNAME, dateString);
            if (list == null || list.isEmpty()) {
                wo.setResult(RESULT_NOT_FOUND);
                // 공휴일 설정 데이터가 조회되지 않아 주말 여부 판단
                try {
                    Date d = DateTools.parse(dateString, DateTools.format_yyyyMMdd);
                    int day = DateTools.dayForWeekAttendanceV2(d);
                    if (day == 0 || day == 6) {
                        wo.setWorkDay(false);
                        wo.setOffDay(true);
                    } else {
                        wo.setWorkDay(true);
                        wo.setOffDay(false);
                    }
                } catch (Exception e) {
                    // 날짜 형식이 올바르지 않아 주말 여부를 판단할 수 없어 기본값 근무일로 설정
                    wo.setWorkDay(true);
                    wo.setOffDay(false);
                }
                result.setData(wo);
                result.setCount(0L);
                return result;
            }
            AttendanceV2Holiday holiday = list.get(0);
            wo.setHoliday(holiday);
            wo.setName(holiday.getName());
            wo.setOffDay(holiday.getOffDay());
            wo.setWorkDay(!Boolean.TRUE.equals(holiday.getOffDay()));
            wo.setResult(Boolean.TRUE.equals(holiday.getOffDay()) ? RESULT_OFFDAY : RESULT_WORKDAY);
            result.setCount(1L);
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = -6467264123471364646L;

        @FieldDescribe("조회 날짜, 형식 yyyy-MM-dd")
        private String dateString;

        @FieldDescribe("판정 결과: WORKDAY 근무일, OFFDAY 휴일, NOT_FOUND 찾지 못함")
        private String result;

        @FieldDescribe("공휴일 이름")
        private String name;

        @FieldDescribe("근무일 여부")
        private Boolean workDay;

        @FieldDescribe("휴무일 여부")
        private Boolean offDay;

        @FieldDescribe("휴일 데이터")
        private AttendanceV2Holiday holiday;

        public String getDateString() {
            return dateString;
        }

        public void setDateString(String dateString) {
            this.dateString = dateString;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getWorkDay() {
            return workDay;
        }

        public void setWorkDay(Boolean workDay) {
            this.workDay = workDay;
        }

        public Boolean getOffDay() {
            return offDay;
        }

        public void setOffDay(Boolean offDay) {
            this.offDay = offDay;
        }

        public AttendanceV2Holiday getHoliday() {
            return holiday;
        }

        public void setHoliday(AttendanceV2Holiday holiday) {
            this.holiday = holiday;
        }
    }
}
