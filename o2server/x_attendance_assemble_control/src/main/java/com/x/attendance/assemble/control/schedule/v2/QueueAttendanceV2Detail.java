package com.x.attendance.assemble.control.schedule.v2;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.assemble.control.jaxrs.v2.WoGroupShift;
import com.x.attendance.entity.v2.*;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 대기열에서 사용자의 특정 날짜 출퇴근 데이터를 처리하여 근태 상세 정보 생성
 * Created by fancyLou on 2023/2/24.
 * Copyright © 2023 O2. All rights reserved.
 */
public class QueueAttendanceV2Detail extends AbstractQueue<QueueAttendanceV2DetailModel> {

    private static final Logger logger = LoggerFactory.getLogger(QueueAttendanceV2Detail.class);

    @Override
    protected void execute(QueueAttendanceV2DetailModel model) throws Exception {
        if (model == null || StringUtils.isEmpty(model.getPerson()) || StringUtils.isEmpty(model.getDate())) {
            logger.info("전달된 매개변수가 잘못되어 데이터를 처리할 수 없습니다");
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("근태 데이터 처리 ==== 처리 사용자 {} 처리 날짜 {} ===================", model.getPerson(), model.getDate());
        }
        if (!allowProcessDate(model)) {
            return;
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            // 사용자 소속 근태 그룹 조회
            List<AttendanceV2Group> groups = business.getAttendanceV2ManagerFactory()
                    .listGroupWithPerson(model.getPerson());
            if (groups == null || groups.isEmpty()) {
                throw new ExceptionQueueAttendanceV2Detail(model.getPerson() + "해당하는 근태 그룹이 없습니다!");
            }
            // 날짜
            Date date = DateTools.parse(model.getDate(), DateTools.format_yyyyMMdd);
            // 요일 0-6은 일요일 - 토요일을 의미
            int day = DateTools.dayForWeekAttendanceV2(date);
            // 근무일 여부
            boolean isWorkDay = false;
            // 체크 데이터 조회
            List<AttendanceV2CheckInRecord> recordList = business.getAttendanceV2ManagerFactory()
                    .listRecordWithPersonAndDate(model.getPerson(), model.getDate());
            AttendanceV2Group group = groups.get(0);
            AttendanceV2Shift shift = null; // 근무조 객체
            AttendanceV2Config config = null; // 설정 객체
            List<AttendanceV2Config> configs = emc.listAll(AttendanceV2Config.class);
            if (configs != null && !configs.isEmpty()) {
                config = configs.get(0);
            }
            // 근무조 객체
            WoGroupShift woGroupShift = business.getAttendanceV2ManagerFactory()
                    .getGroupShiftByPersonDate(model.getPerson(), model.getDate());
            if (woGroupShift != null) {
                shift = woGroupShift.getShift();
            }
            if (group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Free)) { // 자율 근무
                // 휴일인지 근무일인지 판단
                if (StringUtils.isEmpty(group.getWorkDateList())) {
                    throw new ExceptionQueueAttendanceV2Detail("근태 근무일 설정이 비어 있습니다!");
                }
                String[] workDayList = group.getWorkDateList().split(",");
                List<Integer> dayList = Arrays.stream(workDayList).map(Integer::parseInt).collect(Collectors.toList());
                // 근무일 여부
                isWorkDay = dayList.contains(day);
                // 특별 공휴일
                if (isWorkDay && AttendanceV2Helper.isSpecialRestDay(model.getDate(), group)) {
                    isWorkDay = false;
                }
            } else {
                if (shift != null) {
                    isWorkDay = true;
                    List<AttendanceV2ShiftCheckTime> timeList = shift.getProperties().getTimeList();
                    if (timeList == null || timeList.isEmpty()) {
                        throw new ExceptionQueueAttendanceV2Detail(shift.getShiftName() + "해당하는 출퇴근 체크 시간이 없습니다!");
                    }
                    // 근무일에 데이터가 없으면 미체크 데이터 생성
                    if (recordList == null || recordList.isEmpty()) {
                        recordList = new ArrayList<>();
                        for (AttendanceV2ShiftCheckTime shiftCheckTime : timeList) {
                            // 출근 체크
                            AttendanceV2CheckInRecord onDutyRecord = saveNoCheckInRecord(emc, model.getPerson(),
                                    AttendanceV2CheckInRecord.OnDuty, group, shift, model.getDate(),
                                    shiftCheckTime.getOnDutyTime(), shiftCheckTime.getOnDutyTimeBeforeLimit(),
                                    shiftCheckTime.getOnDutyTimeAfterLimit(), false);
                            recordList.add(onDutyRecord);
                            // 퇴근 체크
                            AttendanceV2CheckInRecord offDutyRecord = saveNoCheckInRecord(emc, model.getPerson(),
                                    AttendanceV2CheckInRecord.OffDuty, group, shift, model.getDate(),
                                    shiftCheckTime.getOffDutyTime(), shiftCheckTime.getOffDutyTimeBeforeLimit(),
                                    shiftCheckTime.getOffDutyTimeAfterLimit(),
                                    BooleanUtils.isTrue(shiftCheckTime.getOffDutyNextDay()));
                            recordList.add(offDutyRecord);
                        }
                    }
                }
            }
            // 데이터가 없으면 자유 근무이거나 휴일에 교대 정보가 없는 경우이므로 출근 1건, 퇴근 1건의 체크 기록만 생성
            if (recordList == null || recordList.isEmpty()) {
                recordList = new ArrayList<>();
                // 출근 체크
                AttendanceV2CheckInRecord onDutyRecord = saveNoCheckInRecord(emc, model.getPerson(),
                        AttendanceV2CheckInRecord.OnDuty, group, null, model.getDate(),
                        null, null, null, false);
                recordList.add(onDutyRecord);
                // 퇴근 체크
                AttendanceV2CheckInRecord offDutyRecord = saveNoCheckInRecord(emc, model.getPerson(),
                        AttendanceV2CheckInRecord.OffDuty, group, null, model.getDate(),
                        null, null, null, false);
                recordList.add(offDutyRecord);
            }

            long lateMinute = 0;
            long earlyMinute = 0;
            if (isWorkDay) {
                // 미체크 사전 저장 데이터 처리
                Date now = new Date();
                for (AttendanceV2CheckInRecord record : recordList) {
                    // 미체크 데이터이면서 체크 시간이 이미 지남
                    if (record.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn)
                        && now.after(record.getRecordDate())) {
                        updateRecord2NoCheckIn(emc, record);
                    }
                }
                // 휴가 데이터 처리: 이상 체크가 휴가 데이터에 속하는지
                for (AttendanceV2CheckInRecord record : recordList) {
                    if (!record.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL)
                            && !record.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn)) {
                        checkUpdateRecordIsLeave(emc, business, record);
                    }
                }
                // 지각 데이터
                List<AttendanceV2CheckInRecord> late = recordList.stream()
                        .filter((r) -> (r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_Late) || r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_SeriousLate)) && !r.hasLeaveOrRequest())
                        .collect(Collectors.toList());
                if (!late.isEmpty()) {
                    for (AttendanceV2CheckInRecord record : late) {
                        Date dutyTime = parseRecordDutyTime(record);
                        long time = record.getRecordDate().getTime() - dutyTime.getTime();
                        lateMinute += Math.max(0, time) / 1000 / 60;
                    }
                }
                // 조퇴 데이터
                List<AttendanceV2CheckInRecord> early = recordList.stream()
                        .filter((r) -> r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_Early) && !r.hasLeaveOrRequest())
                        .collect(Collectors.toList());
                if (!early.isEmpty()) {
                    for (AttendanceV2CheckInRecord record : early) {
                        Date dutyTime = parseRecordDutyTime(record);
                        long time = dutyTime.getTime() - record.getRecordDate().getTime();
                        earlyMinute += Math.max(0, time) / 1000 / 60;
                    }
                }
            }
            // 출근 체크
            List<AttendanceV2CheckInRecord> onDutyList = recordList.stream().filter(
                            (r) -> r.getCheckInType().equals(AttendanceV2CheckInRecord.OnDuty)
                                    && AttendanceV2Helper.isRecordAttendance(r))
                    .sorted(Comparator.comparing(AttendanceV2CheckInRecord::getRecordDate))
                    .collect(Collectors.toList());
            // 퇴근 체크
            List<AttendanceV2CheckInRecord> offDutyList = recordList.stream().filter(
                            (r) -> r.getCheckInType().equals(AttendanceV2CheckInRecord.OffDuty)
                                    && AttendanceV2Helper.isRecordAttendance(r))
                    .sorted(Comparator.comparing(AttendanceV2CheckInRecord::getRecordDate))
                    .collect(Collectors.toList());
            // 근무 시간
            long workTimeDuration = 0;
            for (int i = 0; i < onDutyList.size(); i++) {
                AttendanceV2CheckInRecord onDuty = onDutyList.get(i);
                if (offDutyList.size() - 1 >= i) {
                    AttendanceV2CheckInRecord offDuty = offDutyList.get(i);
                    workTimeDuration += (offDuty.getRecordDate().getTime() - onDuty.getRecordDate().getTime()) / 1000
                            / 60;
                }
            }
            // 외근 체크
            List<AttendanceV2CheckInRecord> fieldWorkList = recordList.stream()
                    .filter((r) -> r.getFieldWork() != null && BooleanUtils.isTrue(r.getFieldWork()))
                    .collect(Collectors.toList());
            // 휴가가 있는 목록
            List<AttendanceV2CheckInRecord> leaveList = recordList.stream()
                    .filter(AttendanceV2CheckInRecord::hasLeaveOrRequest).collect(Collectors.toList());

            // 근태 대상
            List<AttendanceV2Detail> details = business.getAttendanceV2ManagerFactory()
                    .listDetailWithPersonAndDate(model.getPerson(), model.getDate());
            AttendanceV2Detail v2Detail;
            if (details != null && !details.isEmpty()) {
                v2Detail = details.get(0);
                deleteDuplicateDetails(emc, details);
            } else {
                v2Detail = new AttendanceV2Detail();
            }
            v2Detail.setUserId(model.getPerson());
            v2Detail.setYearString(model.getDate().substring(0, 4));
            v2Detail.setMonthString(model.getDate().substring(5, 7));
            v2Detail.setRecordDateString(model.getDate()); // 날짜
            v2Detail.setWorkDay(isWorkDay); // 근무일 여부
            v2Detail.setRecordDay(day + ""); // 요일
            v2Detail.setLateTimeDuration(lateMinute);
            v2Detail.setLateTimes(lateMinute > 0 ? 1 : 0);
            v2Detail.setLeaveEarlierTimeDuration(earlyMinute);
            v2Detail.setLeaveEarlierTimes(earlyMinute > 0 ? 1 : 0);
            // 근무 시간
            v2Detail.setWorkTimeDuration(workTimeDuration);
            // 출근 일수
            List<AttendanceV2CheckInRecord> attendanceList = recordList.stream().filter(
                            AttendanceV2Helper::isRecordAttendance)
                    .collect(Collectors.toList());
            v2Detail.setAttendance(!attendanceList.isEmpty() ? 1 : 0);
            if (!isWorkDay) {
                v2Detail.setRest(1);
                // 재계산 시 데이터 오류가 발생하므로 0을 넣어야 함
                v2Detail.setLeaveDays(0);
                v2Detail.setAbsenteeismDays(0);
                v2Detail.setOnDutyAbsenceTimes(0);
                v2Detail.setOffDutyAbsenceTimes(0);
            } else {
                v2Detail.setRest(0);
                if (!leaveList.isEmpty()) { // 휴가
                    v2Detail.setLeaveDays(1);
                    v2Detail.setAbsenteeismDays(0); // 재계산 시 데이터 오류가 발생하므로 0을 넣어야 함
                } else {
                    v2Detail.setLeaveDays(0); // 재계산 시 데이터 오류가 발생하므로 0을 넣어야 함
                    // 무단결근: 정상 체크 기록이 있으면 무단결근이 아닌가?
                    v2Detail.setAbsenteeismDays(!attendanceList.isEmpty() ? 0 : 1);
                }
                // 출근 체크 누락
                List<AttendanceV2CheckInRecord> noCheckInOnDutyList = recordList.stream().filter(
                                (r) -> r.getCheckInType().equals(AttendanceV2CheckInRecord.OnDuty)
                                        && AttendanceV2Helper.isRecordNotSign(r))
                        .collect(Collectors.toList());
                v2Detail.setOnDutyAbsenceTimes(noCheckInOnDutyList.size());
                // 퇴근 체크 누락
                List<AttendanceV2CheckInRecord> noCheckInOffDutyList = recordList.stream().filter(
                                (r) -> r.getCheckInType().equals(AttendanceV2CheckInRecord.OffDuty)
                                        && AttendanceV2Helper.isRecordNotSign(r))
                        .collect(Collectors.toList());
                v2Detail.setOffDutyAbsenceTimes(noCheckInOffDutyList.size());
            }
            // 외근 횟수
            v2Detail.setFieldWorkTimes(fieldWorkList.size());
            v2Detail.setRecordIdList(
                    recordList.stream().map(AttendanceV2CheckInRecord::getId).collect(Collectors.toList()));
            v2Detail.setGroupId(group.getId());
            v2Detail.setGroupName(group.getGroupName());
            if (shift != null) {
                v2Detail.setShiftId(shift.getId());
                String name = formatShiftName(shift);
                v2Detail.setShiftName(name);
            }
            emc.beginTransaction(AttendanceV2Detail.class);
            emc.persist(v2Detail, CheckPersistType.all);
            emc.commit();
            if (logger.isDebugEnabled()) {
                logger.debug("근태 데이터 처리 완료, {}", v2Detail.toString());
            }
            // 이상 체크 데이터가 있으면 해당 데이터 생성
            generateAppealInfo(emc, business, config, recordList, BooleanUtils.isTrue(group.getFieldWorkMarkError()),
                    isWorkDay);
        }

    }

    private boolean allowProcessDate(QueueAttendanceV2DetailModel model) throws Exception {
        if (BooleanUtils.isTrue(model.getAllowToday())) {
            Date date = DateTools.parse(model.getDate(), DateTools.format_yyyyMMdd);
            Date today = DateTools.parse(DateTools.format(new Date(), DateTools.format_yyyyMMdd),
                    DateTools.format_yyyyMMdd);
            if (date.after(today)) {
                logger.info("날짜가 올바르지 않습니다!");
                return false;
            }
            return true;
        }
        if (!AttendanceV2Helper.beforeToday(model.getDate())) {
            logger.info("날짜가 올바르지 않습니다!");
            return false;
        }
        return true;
    }

    private Date parseRecordDutyTime(AttendanceV2CheckInRecord record) throws Exception {
        Date dutyTime = DateTools.parse(record.getRecordDateString() + " " + record.getPreDutyTime(),
                DateTools.format_yyyyMMddHHmm);
        if (AttendanceV2CheckInRecord.OffDuty.equals(record.getCheckInType())
                && BooleanUtils.isTrue(record.getOffDutyNextDay())) {
            dutyTime = DateTools.addDay(dutyTime, 1);
        }
        return dutyTime;
    }

    private void deleteDuplicateDetails(EntityManagerContainer emc, List<AttendanceV2Detail> details) throws Exception {
        if (details == null || details.size() < 2) {
            return;
        }
        List<String> deleteIds = new ArrayList<>();
        for (int i = 1; i < details.size(); i++) {
            deleteIds.add(details.get(i).getId());
        }
        emc.beginTransaction(AttendanceV2Detail.class);
        emc.delete(AttendanceV2Detail.class, deleteIds);
        emc.commit();
        logger.warn("중복 근태 상세 데이터 삭제: {}", StringUtils.join(deleteIds, ","));
    }

    // 근무 시간대 이름 포맷
    private String formatShiftName(AttendanceV2Shift shift) {
        String name = shift.getShiftName();
        if (shift.getProperties() != null && shift.getProperties().getTimeList() != null
                && !shift.getProperties().getTimeList().isEmpty()) {
            name += " " + shift.getProperties().getTimeList().get(0).getOnDutyTime() + " - "
                    + shift.getProperties().getTimeList().get(shift.getProperties().getTimeList().size() - 1)
                    .getOffDutyTime();
        }
        return name;
    }

    /**
     * 누락 체크 보충 신청 기능이 켜져 있으면 해당 이상 체크 신청 데이터 생성
     *
     * @param config             설정
     * @param recordList
     * @param fieldWorkMarkError 외근을 이상 데이터로 표시할지 여부
     * @param isWorkDay          근무일 여부
     * @throws Exception
     */
    private void generateAppealInfo(EntityManagerContainer emc, Business business, AttendanceV2Config config,
                                    List<AttendanceV2CheckInRecord> recordList, boolean fieldWorkMarkError, boolean isWorkDay)
            throws Exception {
        if (emc != null && business != null && recordList != null && !recordList.isEmpty()) {
            for (AttendanceV2CheckInRecord record : recordList) {
                List<AttendanceV2AppealInfo> appealList = business.getAttendanceV2ManagerFactory()
                        .listAppealInfoWithRecordId(record.getId());
                // 이상 체크 시 AttendanceV2AppealInfo 기록 추가
                if (isWorkDay && record.checkResultException(fieldWorkMarkError)) {
                    if (appealList != null && !appealList.isEmpty()) {
                        logger.info("현재 체크 기록에 소명 데이터가 이미 있어 중복 생성할 필요가 없습니다! {}", record.getId());
                        continue;
                    }
                    AttendanceV2AppealInfo appealInfo = new AttendanceV2AppealInfo();
                    appealInfo.setRecordId(record.getId());
                    appealInfo.setRecordDateString(record.getRecordDateString());
                    appealInfo.setRecordDate(record.getRecordDate());
                    appealInfo.setUserId(record.getUserId());
                    emc.beginTransaction(AttendanceV2AppealInfo.class);
                    emc.persist(appealInfo, CheckPersistType.all);
                    emc.commit();
                    if (logger.isDebugEnabled()) {
                        logger.debug("해당 이상 체크 신청 데이터 생성, {}", appealInfo.toString());
                    }
                } else {
                    // 소명한 데이터는 삭제하지 않음
                    if (StringUtils.isNotEmpty(record.getAppealId())) {
                        continue;
                    }
                    // 정상 체크 기록은 기존 이상 AttendanceV2AppealInfo 데이터를 삭제해야 함. 체크 기록이 수정되어 지금은 정상일 수 있음
                    if (appealList != null && !appealList.isEmpty()) {
                        List<String> deleteIds = new ArrayList<>();
                        for (AttendanceV2AppealInfo appealInfo : appealList) {
                            deleteIds.add(appealInfo.getId());
                        }
                        emc.beginTransaction(AttendanceV2AppealInfo.class);
                        emc.delete(AttendanceV2AppealInfo.class, deleteIds);
                        emc.commit();
                    }
                }
            }
        }
    }

    // 현재 체크 기록이 휴가 기간 안에 있는지 확인
    private void checkUpdateRecordIsLeave(EntityManagerContainer emc, Business business,
                                          AttendanceV2CheckInRecord record) throws Exception {
        List<AttendanceV2LeaveData> list = business.getAttendanceV2ManagerFactory()
                .listLeaveDataWithRecordTime(record.getUserId(), record.getRecordDate());
        List<AttendanceV2LeaveRequest> requestList = business.getAttendanceV2ManagerFactory()
                .listLeaveRequestWithRecordTime(record.getUserId(), record.getRecordDate());
        boolean hasLeave = (list != null && !list.isEmpty()) || (requestList != null && !requestList.isEmpty());
        if (list != null && !list.isEmpty()) {
            record.setLeaveDataId(list.get(0).getId());
        }
        if (requestList != null && !requestList.isEmpty()) {
            record.setRequestDataId(requestList.get(0).getId());
        }
        if (hasLeave) {
            emc.beginTransaction(AttendanceV2CheckInRecord.class);
            emc.persist(record, CheckPersistType.all);
            emc.commit();
        }
    }

    // 근무일 사전 저장 데이터를 미체크로 갱신
    private void updateRecord2NoCheckIn(EntityManagerContainer emc, AttendanceV2CheckInRecord record) throws Exception {
        record.setCheckInResult(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned);
        record.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_AUTO_CHECK);
        record.setSourceDevice("其他");
        record.setDescription("시스템 생성, 미체크 기록");
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        emc.persist(record, CheckPersistType.all);
        emc.commit();
    }

    // 미체크 저장 todo offDutyNext
    private AttendanceV2CheckInRecord saveNoCheckInRecord(EntityManagerContainer emc, String person, String dutyType,
                                                          AttendanceV2Group group, AttendanceV2Shift shift, String cDate,
                                                          String dutyTime, String dutyTimeBeforeLimit, String dutyTimeAfterLimit, boolean offDutyNextDay)
            throws Exception {
        String result = AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned;
        // 체크 시간
        if (StringUtils.isEmpty(dutyTime)) {
            if (AttendanceV2CheckInRecord.OnDuty.equals(dutyType)) {
                dutyTime = "09:00";
            } else {
                dutyTime = "18:00";
            }
        }
        Business business = new Business(emc);
        AttendanceV2CheckInRecord existing = findRecordByExist(
                business.getAttendanceV2ManagerFactory().listRecordWithPersonAndDate(person, cDate), dutyType, cDate,
                dutyTime);
        if (existing != null) {
            return existing;
        }

        AttendanceV2CheckInRecord noCheckRecord = new AttendanceV2CheckInRecord();
        noCheckRecord.setCheckInType(dutyType);
        noCheckRecord.setCheckInResult(result);
        noCheckRecord.setUserId(person);
        Date onDutyTime = DateTools.parse(cDate + " " + dutyTime, DateTools.format_yyyyMMddHHmm);
        if (AttendanceV2CheckInRecord.OffDuty.equals(dutyType) && offDutyNextDay) {
            Date nextDate = DateTools.addDay(onDutyTime, 1);
            Date now = new Date();
            if (now.before(nextDate)) { // 날짜를 넘기는 데이터는 아직 체크 시간이 되지 않았을 수 있음
                result = AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn;
                noCheckRecord.setCheckInResult(result);
            }
            noCheckRecord.setRecordDate(nextDate);
        } else {
            noCheckRecord.setRecordDate(onDutyTime);
        }
        noCheckRecord.setRecordDateString(cDate);
        noCheckRecord.setPreDutyTime(dutyTime);
        noCheckRecord.setPreDutyTimeBeforeLimit(dutyTimeBeforeLimit);
        noCheckRecord.setPreDutyTimeAfterLimit(dutyTimeAfterLimit);
        noCheckRecord.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_AUTO_CHECK);
        noCheckRecord.setSourceDevice("其他");
        noCheckRecord.setDescription("시스템 생성, 미체크 기록");
        noCheckRecord.setGroupId(group.getId());
        noCheckRecord.setGroupName(group.getGroupName());
        noCheckRecord.setGroupCheckType(group.getCheckType());
        if (shift != null) {
            noCheckRecord.setShiftId(shift.getId());
            noCheckRecord.setShiftName(shift.getShiftName());
        }
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        emc.persist(noCheckRecord, CheckPersistType.all);
        emc.commit();
        logger.info("체크 기록 저장: {}, {}, {} ", person, cDate, result);
        return noCheckRecord;
    }

    private AttendanceV2CheckInRecord findRecordByExist(List<AttendanceV2CheckInRecord> recordList, String dutyType,
                                                        String date, String dutyTime) {
        if (recordList == null || recordList.isEmpty()) {
            return null;
        }
        return recordList.stream()
                .filter((r) -> StringUtils.equals(r.getCheckInType(), dutyType)
                        && StringUtils.equals(r.getPreDutyTime(), dutyTime)
                        && StringUtils.equals(r.getRecordDateString(), date))
                .findFirst().orElse(null);
    }
}
