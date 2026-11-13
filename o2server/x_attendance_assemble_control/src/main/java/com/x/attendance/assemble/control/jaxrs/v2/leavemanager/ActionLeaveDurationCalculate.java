package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2RestDayHelper;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2ShiftWorkTimeHelper;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.WoGroupShift;
import com.x.attendance.assemble.control.jaxrs.v2.detail.ExceptionDateEndBeforeStartError;
import com.x.attendance.entity.v2.AttendanceV2Holiday;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.attendance.entity.v2.AttendanceV2ShiftCheckTime;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.DateTools;

public class ActionLeaveDurationCalculate extends BaseAction {

    ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getPerson())) {
                throw new ExceptionEmptyParameter("인원 식별자");
            }
            Date startTime = wi.getStartTime();
            Date endTime = wi.getEndTime();
            if (startTime == null) {
                throw new ExceptionEmptyParameter("시작 시간");
            }
            if (endTime == null) {
                throw new ExceptionEmptyParameter("종료 시간");
            }
            if (startTime.after(endTime)) {
                throw new ExceptionDateEndBeforeStartError();
            }

            Business business = new Business(emc);
            Person person = business.organization().person().getObject(wi.getPerson(), true);
            if (person == null) {
                throw new ExceptionNotExistObject("인원 " + wi.getPerson());
            }

            Date startDate = DateTools.floorDate(startTime, 0);
            Date endDate = DateTools.floorDate(startTime.before(endTime) ? DateTools.addMinutes(endTime, -1) : endTime,
                    0);
            List<String> dateList = AttendanceV2RestDayHelper.listDateRange(startDate, endDate);
            String personDn = person.getDistinguishedName();
            List<String> restDateList = AttendanceV2RestDayHelper.listRestDate(business, personDn, dateList);
            List<String> weekendDateList = AttendanceV2RestDayHelper.listWeekend(dateList);
            List<String> legalHolidayDateList = listLegalHolidayDate(emc, dateList);
            List<String> calculateDateList = listCalculateDate(dateList, restDateList, weekendDateList,
                    legalHolidayDateList, wi.getExcludeRestDay(), wi.getIncludeWeekend());
            List<String> leaveDateList = new ArrayList<>();
            long durationMinutes = 0;
            double duration = 0.0;
            for (String date : calculateDateList) {
                boolean calculateWithWeekendShift = isWeekendLeaveDate(date, restDateList, weekendDateList,
                        legalHolidayDateList, wi.getIncludeWeekend());
                DayDuration dayDuration = calculateDayDuration(business, personDn, date, startTime, endTime,
                        calculateWithWeekendShift);
                if (dayDuration.getDurationMinutes() > 0) {
                    leaveDateList.add(date);
                }
                durationMinutes += dayDuration.getDurationMinutes();
                duration += dayDuration.getDuration();
            }

            Wo wo = new Wo();
            wo.setTotalDays(dateList.size());
            wo.setDuration(new BigDecimal(duration).setScale(1, RoundingMode.HALF_UP).doubleValue());
            wo.setDurationMinutes(durationMinutes);
            wo.setLeaveDateList(leaveDateList);
            wo.setRestDateList(restDateList);
            result.setData(wo);
            return result;
        }
    }

    static List<String> listCalculateDate(List<String> dateList, List<String> restDateList, Boolean excludeRestDay) {
        return listCalculateDate(dateList, restDateList, new ArrayList<>(), new ArrayList<>(), excludeRestDay, false);
    }

    static List<String> listCalculateDate(List<String> dateList, List<String> restDateList,
            List<String> weekendDateList, List<String> legalHolidayDateList, Boolean excludeRestDay,
            Boolean includeWeekend) {
        if (BooleanUtils.isFalse(excludeRestDay)) {
            return dateList;
        }
        if (BooleanUtils.isTrue(includeWeekend)) {
            return dateList.stream()
                    .filter(date -> !legalHolidayDateList.contains(date)
                            && (!restDateList.contains(date) || weekendDateList.contains(date)))
                    .collect(Collectors.toList());
        }
        return dateList.stream().filter(date -> !restDateList.contains(date)).collect(Collectors.toList());
    }

    private DayDuration calculateDayDuration(Business business, String personDn, String date, Date startTime,
            Date endTime, boolean calculateWithWeekendShift) throws Exception {
        WoGroupShift woGroupShift = business.getAttendanceV2ManagerFactory().getGroupShiftByPersonDate(personDn, date);
        AttendanceV2Shift shift = woGroupShift.getShift();
        if (shift == null || shift.getProperties() == null || shift.getProperties().getTimeList().isEmpty()) {
            if (calculateWithWeekendShift) {
                AttendanceV2Shift weekendShift = nearestPastWorkdayShift(business, personDn, date);
                if (weekendShift != null) {
                    return calculateDayDurationWithShift(weekendShift, date, startTime, endTime);
                }
            }
            return calculateDayDurationWithoutShift(date, startTime, endTime);
        }
        return calculateDayDurationWithShift(shift, date, startTime, endTime);
    }

    private DayDuration calculateDayDurationWithShift(AttendanceV2Shift shift, String date, Date startTime,
            Date endTime) throws Exception {
        long shiftWorkMinutes = shift.getWorkTime();
        long calculatedWorkMinutes = 0;
        long durationMinutes = 0;
        for (AttendanceV2ShiftCheckTime checkTime : shift.getProperties().getTimeList()) {
            calculatedWorkMinutes += AttendanceV2ShiftWorkTimeHelper.standardWorkMinutes(date, checkTime);
            durationMinutes += AttendanceV2ShiftWorkTimeHelper.overlapWorkMinutes(startTime, endTime, date, checkTime);
        }
        long standardMinutes = calculatedWorkMinutes > 0 ? calculatedWorkMinutes : shiftWorkMinutes;
        double duration = standardMinutes > 0 ? durationMinutes * 1.0 / standardMinutes : 0.0;
        return new DayDuration(durationMinutes, duration);
    }

    private AttendanceV2Shift nearestPastWorkdayShift(Business business, String personDn, String date) throws Exception {
        Date cursor = DateTools.addDay(DateTools.parse(date, DateTools.format_yyyyMMdd), -1);
        for (int i = 0; i < 366; i++) {
            String cursorDate = DateTools.format(cursor, DateTools.format_yyyyMMdd);
            if (!AttendanceV2RestDayHelper.isRestDay(business, personDn, cursorDate)) {
                WoGroupShift woGroupShift = business.getAttendanceV2ManagerFactory().getGroupShiftByPersonDate(personDn,
                        cursorDate);
                if (woGroupShift != null) {
                    AttendanceV2Shift shift = woGroupShift.getShift();
                    if (shift != null && shift.getProperties() != null && !shift.getProperties().getTimeList()
                            .isEmpty()) {
                        return shift;
                    }
                }
            }
            cursor = DateTools.addDay(cursor, -1);
        }
        return null;
    }

    private DayDuration calculateDayDurationWithoutShift(String date, Date startTime, Date endTime) throws Exception {
        Date dayStart = DateTools.parse(date + " 00:00:00", DateTools.format_yyyyMMddHHmmss);
        Date dayEnd = DateTools.addDay(dayStart, 1);
        long durationMinutes = overlapMinutes(startTime, endTime, dayStart, dayEnd);
        double duration = durationMinutes > 0 ? durationMinutes * 1.0 / (24 * 60) : 0.0;
        return new DayDuration(durationMinutes, duration);
    }

    private long overlapMinutes(Date startTime, Date endTime, Date rangeStart, Date rangeEnd) {
        long start = Math.max(startTime.getTime(), rangeStart.getTime());
        long end = Math.min(endTime.getTime(), rangeEnd.getTime());
        if (end <= start) {
            return 0;
        }
        return (end - start) / (60 * 1000);
    }

    private static boolean isWeekendLeaveDate(String date, List<String> restDateList, List<String> weekendDateList,
            List<String> legalHolidayDateList, Boolean includeWeekend) {
        return BooleanUtils.isTrue(includeWeekend) && restDateList.contains(date) && weekendDateList.contains(date)
                && !legalHolidayDateList.contains(date);
    }

    private static List<String> listLegalHolidayDate(EntityManagerContainer emc, List<String> dateList)
            throws Exception {
        List<String> legalHolidayDateList = new ArrayList<>();
        for (String date : dateList) {
            if (isLegalHoliday(emc, date)) {
                legalHolidayDateList.add(date);
            }
        }
        return legalHolidayDateList;
    }

    private static boolean isLegalHoliday(EntityManagerContainer emc, String date) throws Exception {
        Date parsedDate = DateTools.parse(date, DateTools.format_yyyyMMdd);
        if (Config.workTime() != null && Config.workTime().inDefinedHoliday(parsedDate)) {
            return true;
        }
        List<AttendanceV2Holiday> holidays = emc.listEqual(AttendanceV2Holiday.class,
                AttendanceV2Holiday.dateString_FIELDNAME, date);
        if (holidays == null || holidays.isEmpty()) {
            return false;
        }
        return holidays.stream().anyMatch(holiday -> BooleanUtils.isTrue(holiday.getOffDay()));
    }

    private static class DayDuration {

        private long durationMinutes;

        private double duration;

        private DayDuration(long durationMinutes, double duration) {
            this.durationMinutes = durationMinutes;
            this.duration = duration;
        }

        private long getDurationMinutes() {
            return durationMinutes;
        }

        private double getDuration() {
            return duration;
        }
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 3972136115624287303L;

        @FieldDescribe("인원 식별자")
        private String person;

        @FieldDescribe("시작 시간, yyyy-MM-dd HH:mm:ss")
        private Date startTime;

        @FieldDescribe("종료 시간, yyyy-MM-dd HH:mm:ss")
        private Date endTime;


        @FieldDescribe("공휴일·휴일 제외 여부, 기본값 true. false이면 공휴일·휴일을 제외하지 않음")
        private Boolean excludeRestDay;

        @FieldDescribe("일반 주말을 휴가 시간에 포함할지 여부, 기본값 false. true이면 일반 주말은 가장 최근 지난 근무일 교대로 계산하고 법정 공휴일은 계속 제외")
        private Boolean includeWeekend;

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public Date getEndTime() {
            return endTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }

        public Boolean getExcludeRestDay() {
            return excludeRestDay;
        }

        public void setExcludeRestDay(Boolean excludeRestDay) {
            this.excludeRestDay = excludeRestDay;
        }

        public Boolean getIncludeWeekend() {
            return includeWeekend;
        }

        public void setIncludeWeekend(Boolean includeWeekend) {
            this.includeWeekend = includeWeekend;
        }
    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = 2373093028785508444L;

        @FieldDescribe("달력일 일수")
        private Integer totalDays = 0;

        @FieldDescribe("실제 휴가 일수")
        private Double duration = 0.0;

        @FieldDescribe("실제 휴가 분 수")
        private Long durationMinutes = 0L;

        @FieldDescribe("실제 휴가 날짜 목록, yyyy-MM-dd")
        private List<String> leaveDateList = new ArrayList<>();

        @FieldDescribe("휴일 날짜 목록, yyyy-MM-dd")
        private List<String> restDateList = new ArrayList<>();

        public Integer getTotalDays() {
            return totalDays;
        }

        public void setTotalDays(Integer totalDays) {
            this.totalDays = totalDays;
        }

        public Double getDuration() {
            return duration;
        }

        public void setDuration(Double duration) {
            this.duration = duration;
        }

        public Long getDurationMinutes() {
            return durationMinutes;
        }

        public void setDurationMinutes(Long durationMinutes) {
            this.durationMinutes = durationMinutes;
        }

        public List<String> getLeaveDateList() {
            return leaveDateList;
        }

        public void setLeaveDateList(List<String> leaveDateList) {
            this.leaveDateList = leaveDateList;
        }

        public List<String> getRestDateList() {
            return restDateList;
        }

        public void setRestDateList(List<String> restDateList) {
            this.restDateList = restDateList;
        }
    }
}
