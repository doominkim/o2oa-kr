package com.x.attendance.assemble.control.jaxrs.v2;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.detail.ExceptionDateError;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2Holiday;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.x_attendance_assemble_control;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 신규 근태 일부 공용 비즈니스 로직 유틸 클래스 Created by fancyLou on 2023/4/24. Copyright © 2023 O2. All rights reserved.
 */
public class AttendanceV2Helper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceV2Helper.class);


    /**
     * 오늘 이전 날짜인지 여부
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static boolean beforeToday(String date) throws Exception {
        Date dateD = DateTools.parse(date, DateTools.format_yyyyMMdd); // 형식 검사
        Date today = new Date();
        today = DateUtils.truncate(today, Calendar.DATE); // 오늘 0시 0분
        if (dateD.after(today) || dateD.equals(today)) {
            throw new ExceptionDateError();
        }
        return true;
    }

    /**
     * 문자열이 월 형식 yyyy-MM인지 여부
     *
     * @param dateString
     * @return
     */
    public static boolean isValidMonthString(String dateString) {
        // 형식 일치 여부를 검사하는 정규식
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}");
        Matcher matcher = pattern.matcher(dateString);
        if (!matcher.matches()) {
            return false; // 형식 불일치
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DateTools.format_yyyyMM);
        sdf.setLenient(false);
        try {
            // SimpleDateFormat으로 날짜 파싱 시도
            sdf.parse(dateString);
            return true; // 파싱 성공, 날짜 형식 올바름
        } catch (ParseException e) {
            return false; // 파싱 실패, 날짜 형식 올바르지 않음
        }
    }

    /**
     * 문자열이 날짜 형식 yyyy-MM-dd인지 여부
     *
     * @param dateString
     * @return
     */
    public static boolean isValidDateString(String dateString) {
        // 형식 일치 여부를 검사하는 정규식
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        Matcher matcher = pattern.matcher(dateString);
        if (!matcher.matches()) {
            return false; // 형식 불일치
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DateTools.format_yyyyMMdd);
        sdf.setLenient(false);
        try {
            // SimpleDateFormat으로 날짜 파싱 시도
            sdf.parse(dateString);
            return true; // 파싱 성공, 날짜 형식 올바름
        } catch (ParseException e) {
            return false; // 파싱 실패, 날짜 형식 올바르지 않음
        }
    }

    /**
     * 현재 출퇴근 객체가 출근에 해당하는지 여부
     * <p>
     * != CHECKIN_RESULT_NotSigned !=CHECKIN_RESULT_PreCheckIn 휴가 데이터 없음
     *
     * @param r 출퇴근 객체
     * @return
     */
    public static boolean isRecordAttendance(AttendanceV2CheckInRecord r) {
        return (!r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned)
                && !r.getCheckInResult()
                .equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn))
               || r.hasLeaveOrRequest();
    }

    /**
     * 현재 출퇴근 객체가 미체크 데이터에 해당하는지 여부
     * <p>
     * result = CHECKIN_RESULT_NotSigned 이고 휴가 데이터 없음
     *
     * @param r
     * @return
     */
    public static boolean isRecordNotSign(AttendanceV2CheckInRecord r) {
        return r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned)
               && !r.hasLeaveOrRequest();
    }

    /**
     * 근태 그룹·근태 사용자 처리, 사용자·조직을 모두 사용자 DN으로 변환
     *
     * @param emc
     * @param business
     * @param groupId
     * @param participateList
     * @param unParticipateList
     * @return
     * @throws Exception
     */
    public static List<String> calTruePersonFromMixList(EntityManagerContainer emc,
            Business business, String groupId,
            List<String> participateList, List<String> unParticipateList) throws Exception {
        // 근태 그룹 처리
        List<String> peopleList = new ArrayList<>();
        for (String p : participateList) {
            if (p.endsWith("@P")) {
                peopleList.add(p);
            } else if (p.endsWith("@I")) {
                String person = business.organization().person().getWithIdentity(p);
                peopleList.add(person);
            } else if (p.endsWith("@U")) { // 사용자 재귀 조회
                List<String> pList = business.organization().person().listWithUnitSubNested(p);
                peopleList.addAll(pList);
            } else {
                // LOGGER.info("잘못된 식별자? " + p);
            }
        }
        // 제외 사용자 삭제
        if (unParticipateList != null && !unParticipateList.isEmpty()) {
            for (String p : unParticipateList) {
                peopleList.remove(p);
            }
        }
        // 중복 제거
        HashSet<String> peopleSet = new HashSet<>(peopleList);
        // 다른 근태 그룹 구성원과 충돌하는지 확인
        List<String> conflictPersonInOtherGroup = new ArrayList<>();
        List<AttendanceV2Group> groups = emc.listAll(AttendanceV2Group.class);
        if (groups != null && !groups.isEmpty()) {
            for (String person : peopleSet) {
                for (AttendanceV2Group oldG : groups) {
                    // 자기 자신은 처리하지 않음
                    if (oldG.getId().equals(groupId)) {
                        continue;
                    }
                    // 자동 저장 데이터 건너뜀
                    if (oldG.getStatus() != null
                        && oldG.getStatus() == AttendanceV2Group.status_auto) {
                        continue;
                    }
                    if (oldG.getTrueParticipantList().contains(person)) {
                        conflictPersonInOtherGroup.add(person);
                        break;
                    }
                }
            }
        }
        if (!conflictPersonInOtherGroup.isEmpty()) {
            throw new ExceptionParticipateConflict(conflictPersonInOtherGroup);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("근태 그룹 {} 최종 근태 그룹 사용자 수: {}", groupId, peopleSet.size());
        }

        return new ArrayList<>(peopleSet);
    }

    /**
     * 사용자/조직 필터 조건을 분석해 조직을 사용자 DN으로 변환.
     */
    public static void analysisFilterToPersonList(List<String> userList, String filter, Business business,
            Boolean recursive) throws Exception {
        if (StringUtils.isEmpty(filter)) {
            return;
        }
        if (filter.endsWith("@U")) {
            List<String> users = BooleanUtils.isNotFalse(recursive)
                    ? business.organization().person().listWithUnitSubNested(filter)
                    : business.organization().person().listWithUnitSubDirect(filter);
            if (users != null && !users.isEmpty()) {
                userList.addAll(users);
            }
        } else if (filter.endsWith("@P")) {
            userList.add(filter);
        }
    }

    /**
     * 중국 공휴일 여부
     *
     * @param date yyyy-MM-dd
     */
    public static boolean checkIsChineseHoliday(String date) throws Exception {
        boolean isHoliday = false;
        HolidayGetWithDateWo wo = ThisApplication.context().applications()
                .getQuery(x_attendance_assemble_control.class,
                        "v2/leavemanager/holiday/date/" + date).getData(HolidayGetWithDateWo.class);
        if (wo != null && BooleanUtils.isTrue(wo.getOffDay())) {
            isHoliday = true;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("날짜 {} 는 공휴일, 공휴일 이름: {}", date, wo.getName());
            }
        }
        return isHoliday;
    }

    /**
     * 중국 공휴일 대체 근무일 여부
     *
     * @param date yyyy-MM-dd
     */
    public static boolean checkIsChineseWorkday(String date) throws Exception {
        boolean isWorkday = false;
        HolidayGetWithDateWo wo = ThisApplication.context().applications()
                .getQuery(x_attendance_assemble_control.class,
                        "v2/leavemanager/holiday/date/" + date).getData(HolidayGetWithDateWo.class);
        if (wo != null && BooleanUtils.isTrue(wo.getWorkDay())) {
            isWorkday = true;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("날짜 {} 는 공휴일 대체 근무일, 공휴일 이름: {}", date, wo.getName());
            }
        }
        return isWorkday;
    }

    /**
     * 특별 공휴일 여부
     *
     * @param date  yyyy-MM-dd
     * @param group
     * @return
     * @throws Exception
     */
    public static boolean isSpecialRestDay(String date, AttendanceV2Group group) throws Exception {
        boolean isRestDay = false;
        // 근태 설정 공휴일·근무일
        Date myDate = DateTools.parse(date, DateTools.format_yyyyMMdd);
        if (Config.workTime() != null && Config.workTime().inDefinedHoliday(myDate)) {
            isRestDay = true;
        }
        if (checkIsChineseHoliday(date)) {
            isRestDay = true;
        }
        // 근태 그룹의 체크 불필요일
        if (group.getNoNeedCheckInDateList() != null && !group.getNoNeedCheckInDateList()
                .isEmpty()) {
            for (String d : group.getNoNeedCheckInDateList()) { // 포함 날짜 | 반복 여부
                String[] dArray = d.split("\\|");
                if (dArray.length < 2) {
                    // 형식이 올바르지 않음
                    continue;
                }
                if (dArray[0].equals(date)) {
                    isRestDay = true;// 체크 불필요하면 휴일
                    break;
                }
                if (dArray[1].equals("week")
                    && DateTools.dateIsInWeekCycle(
                        DateTools.parse(dArray[0], DateTools.format_yyyyMMdd),
                        DateTools.parse(date, DateTools.format_yyyyMMdd))) { // 매주
                    isRestDay = true;// 체크 불필요하면 휴일
                    break;
                }
                if (dArray[1].equals("twoWeek")
                    && DateTools.dateIsInTwoWeekCycle(
                        DateTools.parse(dArray[0], DateTools.format_yyyyMMdd),
                        DateTools.parse(date, DateTools.format_yyyyMMdd))) { // 매주
                    isRestDay = true;// 체크 불필요하면 휴일
                    break;
                }
                if (dArray[1].equals("month")
                    && DateTools.dateIsInMonthCycle(
                        DateTools.parse(dArray[0], DateTools.format_yyyyMMdd),
                        DateTools.parse(date, DateTools.format_yyyyMMdd))) { // 매주
                    isRestDay = true;// 체크 불필요하면 휴일
                    break;
                }
            }

        }
        return isRestDay;
    }

    /**
     * 특별 근무일 여부 및 근무일 교대 id 반환
     *
     * @param date  yyyy-MM-dd
     * @param group
     * @return
     * @throws Exception
     */
    public static String specialWorkDayShift(String date, AttendanceV2Group group)
            throws Exception {
        String shiftId = null;
        // 근무일
        Date myDate = DateTools.parse(date, DateTools.format_yyyyMMdd);
        if (Config.workTime() != null && Config.workTime().inDefinedWorkday(myDate)) {
            shiftId = group.getShiftId();
        }
        if (checkIsChineseWorkday(date)) {
            shiftId = group.getShiftId();
        }
        // 근태 그룹의 필수 체크일
        if (group.getRequiredCheckInDateList() != null && !group.getRequiredCheckInDateList()
                .isEmpty()) {
            for (String d : group.getRequiredCheckInDateList()) { // 포함 날짜 | 교대 id | 반복 여부
                String[] dArray = d.split("\\|");
                if (dArray.length < 3) {
                    // 형식이 올바르지 않음
                    continue;
                }
                if (dArray[0].equals(date)) {
                    shiftId = dArray[1];
                    break;
                }
                if (dArray[2].equals("week")
                    && DateTools.dateIsInWeekCycle(
                        DateTools.parse(dArray[0], DateTools.format_yyyyMMdd),
                        DateTools.parse(date, DateTools.format_yyyyMMdd))) { // 매주
                    shiftId = dArray[1];
                    break;
                }
                if (dArray[2].equals("twoWeek")
                    && DateTools.dateIsInTwoWeekCycle(
                        DateTools.parse(dArray[0], DateTools.format_yyyyMMdd),
                        DateTools.parse(date, DateTools.format_yyyyMMdd))) { // 매주
                    shiftId = dArray[1];
                    break;
                }
                if (dArray[2].equals("month")
                    && DateTools.dateIsInMonthCycle(
                        DateTools.parse(dArray[0], DateTools.format_yyyyMMdd),
                        DateTools.parse(date, DateTools.format_yyyyMMdd))) { // 매주
                    shiftId = dArray[1];
                    break;
                }
            }
        }
        return shiftId;
    }

    public static void setExcelCellError(Row row, String error, int columnIndex) {
        Cell cell = CellUtil.getCell(row, columnIndex);
        cell.setCellValue(error);
    }

    public static String getExcelCellStringValue(Cell cell) {
        if (null != cell) {
            switch (cell.getCellType()) {
                case BLANK:
                    return "";
                case BOOLEAN:
                    return BooleanUtils.toString(cell.getBooleanCellValue(), "true", "false",
                            "false");
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

    public static String getExcelCellDateFormat(Cell cell, String format) throws Exception {
        if (null == cell) {
            return "";
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            Date date = cell.getDateCellValue();
            if (date != null) {
                return DateTools.format(date, format);
            }
        }
        String dateString = cell.getStringCellValue().trim();
        if (StringUtils.isEmpty(dateString)) {
            return "";
        }
        Date date = parseDate(dateString);
        if (date != null) {
            return DateTools.format(date, format);
        }
        return dateString;
    }

    private static Date parseDate(String dateString) throws ParseException {
        return DateUtils.parseDate(dateString,
                new String[]{DateTools.format_yyyyMMdd, DateTools.format_yyyyMMddHHmmss,
                        DateTools.format_HHmmss, DateTools.format_HHmm,
                        DateTools.format_yyyyMMddHHmm});
    }


    private static class HolidayGetWithDateWo extends GsonPropertyObject {


        private static final long serialVersionUID = -2684730459120409608L;
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
