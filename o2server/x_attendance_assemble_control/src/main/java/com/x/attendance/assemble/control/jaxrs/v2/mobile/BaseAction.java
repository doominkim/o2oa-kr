package com.x.attendance.assemble.control.jaxrs.v2.mobile;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.schedule.v2.QueueAttendanceV2DetailModel;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2Config;
import com.x.attendance.entity.v2.AttendanceV2ConfigProperties;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.attendance.entity.v2.AttendanceV2ShiftCheckTime;
import com.x.attendance.entity.v2.AttendanceV2WorkPlace;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

abstract class BaseAction extends StandardJaxrsAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);
    private static final ConcurrentMap<String, ReentrantLock> CHECK_LOCKS = new ConcurrentHashMap<>();

    protected <T> T executeWithCheckLock(String lockKey, Callable<T> callable) throws Exception {
        ReentrantLock lock = CHECK_LOCKS.computeIfAbsent(lockKey, key -> new ReentrantLock());
        lock.lock();
        try {
            return callable.call();
        } finally {
            lock.unlock();
            if (!lock.hasQueuedThreads()) {
                CHECK_LOCKS.remove(lockKey, lock);
            }
        }
    }

    protected void checkAndSendV2DetailIfAllCheckInCompletedAsync(String peopleDn, String date) {
        if (StringUtils.isEmpty(peopleDn) || StringUtils.isEmpty(date)) {
            return;
        }
        CompletableFuture.runAsync(() -> {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                List<AttendanceV2CheckInRecord> recordList = business.getAttendanceV2ManagerFactory()
                        .listRecordWithPersonAndDate(peopleDn, date);
                boolean allCompleted = recordList != null && !recordList.isEmpty()
                                       && recordList.stream().noneMatch(
                        record -> AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn
                                .equals(record.getCheckInResult()));
                if (allCompleted) {
                    LOGGER.info("당일 체크가 모두 완료되어 근태 데이터 생성 시작, Date: {} person: {}", date,
                            peopleDn);
                    ThisApplication.queueV2Detail
                            .send(new QueueAttendanceV2DetailModel(peopleDn, date, true));
                } else if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("당일 체크가 모두 완료되지 않아 근태 데이터를 생성하지 않음, Date: {} person: {}", date,
                            peopleDn);
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        });
    }


    protected class CallableImpl implements Callable<List<AttendanceV2CheckInRecord>> {

        private String person;
        private AttendanceV2Group group;
        private AttendanceV2Shift shift;
        private Date checkInDate;

        protected CallableImpl(String person, AttendanceV2Group group, AttendanceV2Shift shift,
                Date checkInDate) {
            this.person = person;
            this.group = group;
            this.shift = shift;
            this.checkInDate = checkInDate;
        }

        @Override
        public List<AttendanceV2CheckInRecord> call() throws Exception {
            // 출퇴근 기록 조회
            String today = DateTools.format(checkInDate, DateTools.format_yyyyMMdd);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("날짜: {}", today);
            }
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                // 현재 사용자의 근태 그룹 조회
                Business business = new Business(emc);
                // 시간순으로 체크 목록 조회
                List<AttendanceV2CheckInRecord> recordList = business.getAttendanceV2ManagerFactory()
                        .listRecordWithPersonAndDate(person, today);
                if (group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Arrangement)) {
                    // 교대제 데이터 처리: 교대제는 날짜를 넘기는 체크가 있을 수 있어 어제 체크 기록을 먼저 조회
                    Date yesterday = DateTools.addDay(checkInDate, -1);
                    String yesterdayString = DateTools.format(yesterday, DateTools.format_yyyyMMdd);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("어제 날짜: {}", yesterdayString);
                    }
                    List<AttendanceV2CheckInRecord> yesterdayRecordList = business.getAttendanceV2ManagerFactory()
                            .listRecordWithPersonAndDate(person, yesterdayString);
                    if (yesterdayRecordList == null || yesterdayRecordList.isEmpty()) {
                        if (shift != null) {
                            yesterdayRecordList = dealShiftForRecord(shift, emc, business, person,
                                    group, yesterday, yesterdayString, null);
                        }
                    }
                    if (yesterdayRecordList != null && !yesterdayRecordList.isEmpty()) {
                        AttendanceV2CheckInRecord last = yesterdayRecordList.get(
                                yesterdayRecordList.size() - 1);
                        // 어제 데이터이며 날짜를 넘김
                        if (last.getOffDutyNextDay()) {
                            Date onDutyAfterTime;
                            if (StringUtils.isEmpty(last.getPreDutyTimeAfterLimit())) {
                                onDutyAfterTime = DateTools.parse(
                                        today + " " + last.getPreDutyTime(),
                                        DateTools.format_yyyyMMddHHmm);
                                onDutyAfterTime = DateTools.addMinutes(onDutyAfterTime,
                                        60);// 날짜를 넘긴 마지막 데이터가 60분 초과
                            } else {
                                onDutyAfterTime = DateTools.parse(
                                        today + " " + last.getPreDutyTimeAfterLimit(),
                                        DateTools.format_yyyyMMddHHmm);
                            }
                            // 제한 시간 종료 전이 아니면 어제 체크 기록 반환
                            if (!checkInDate.after(onDutyAfterTime)) {
                                LOGGER.info("어제 데이터 반환, 날짜를 넘겨 아직 완료되지 않은 체크 있음");
                                return yesterdayRecordList;
                            }
                        }
                    }
                }
                if (shift != null) {
                    recordList = dealShiftForRecord(shift, emc, business, person, group,
                            checkInDate, today, recordList);
                }
                // 데이터가 없으면 자유 근무이거나 휴일에 교대 정보가 없는 경우이므로 출근 1건, 퇴근 1건의 체크 기록만 생성
                if (recordList == null || recordList.isEmpty()) {
                    recordList = new ArrayList<>();
                    // 출근 체크
                    AttendanceV2CheckInRecord onDutyRecord = savePreCheckInRecord(emc, person,
                            AttendanceV2CheckInRecord.OnDuty, group, null, today,
                            null, null, null, false);
                    recordList.add(onDutyRecord);
                    // 퇴근 체크
                    AttendanceV2CheckInRecord offDutyRecord = savePreCheckInRecord(emc, person,
                            AttendanceV2CheckInRecord.OffDuty, group, null, today,
                            null, null, null, false);
                    recordList.add(offDutyRecord);
                }

                return recordList;
            }
        }

    }

    protected class CheckInCallableImpl implements Callable<AttendanceV2CheckInRecord> {

        private Date checkTime;
        private String recordId;
        private CheckInWi wi;

        protected CheckInCallableImpl(Date checkTime, String recordId, CheckInWi wi) {
            this.checkTime = checkTime;
            this.recordId = recordId;
            this.wi = wi;
        }

        @Override
        public AttendanceV2CheckInRecord call() throws Exception {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                AttendanceV2WorkPlace workPlace = null;
                if (StringUtils.isNotEmpty(wi.getWorkPlaceId())) {
                    workPlace = emc.find(wi.getWorkPlaceId(), AttendanceV2WorkPlace.class);
                }
                AttendanceV2CheckInRecord record = emc.find(recordId,
                        AttendanceV2CheckInRecord.class);
                if (record == null) {
                    throw new ExceptionNotExistObject("체크 기록");
                }
                String today = DateTools.format(checkTime, DateTools.format_yyyyMMdd);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("체크 날짜: {}", today);
                }
                String checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                // 교대 정보 유무
                if (StringUtils.isNotEmpty(record.getShiftId())) {
                    AttendanceV2Shift shift = business.getAttendanceV2ManagerFactory()
                            .pick(record.getShiftId(), AttendanceV2Shift.class);
                    if (shift == null) {
                        throw new ExceptionNotExistObject("근무조 객체");
                    }
                    if (StringUtils.isNotEmpty(record.getPreDutyTimeBeforeLimit())) {
                        Date beforeOnDuty = parseRecordDutyTime(record,
                                record.getPreDutyTimeBeforeLimit());
                        if (checkTime.before(beforeOnDuty)) { // 시작 시간 전에는 체크할 수 없습니다
                            throw new ExceptionTimeError("시작 시간 전에는 체크할 수 없습니다");
                        }
                    }
                    if (StringUtils.isNotEmpty(record.getPreDutyTimeAfterLimit())) {
                        Date afterDuty = parseRecordDutyTime(record,
                                record.getPreDutyTimeAfterLimit());
                        if (checkTime.after(afterDuty)) { // 종료 시간이 지나 체크할 수 없습니다
                            throw new ExceptionTimeError("종료 시간이 지나 체크할 수 없습니다");
                        }
                    }
                    String dutyTimeString = record.getRecordDateString() + " " + record.getPreDutyTime();
                    String checkTimeString = DateTools.format(checkTime, DateTools.format_yyyyMMddHHmm);
                    // 출근 체크
                    if (record.getCheckInType().equals(AttendanceV2CheckInRecord.OnDuty)) {
                        Date dutyTime = parseRecordDutyTime(record, record.getPreDutyTime());
                        checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                        // 지각
                        // 현재 체크 시간이 체크해야 할 시간보다 크고 같은 시간이 아님. 예: 9:00 체크가 지각으로 판단되는 것 방지
                        if (checkTime.after(dutyTime) && !dutyTimeString.equals(checkTimeString)) {
                            checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_Late;
                        }
                        // 심각한 지각
                        if (shift.getSeriousTardinessLateMinutes() > 0) {
                            Date seriousLateTime = DateTools.addMinutes(dutyTime,
                                    shift.getSeriousTardinessLateMinutes());
                            if (checkTime.after(seriousLateTime)) {
                                checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_SeriousLate;
                            }
                        }
                        // 늦게 와도 됨
                        if (StringUtils.isNotEmpty(shift.getLateAndEarlyOnTime())) {
                            int minute = -1;
                            try {
                                minute = Integer.parseInt(shift.getLateAndEarlyOnTime());
                            } catch (Exception ignored) {
                            }
                            if (minute > 0) {
                                Date lateAndEarlyOnTime = DateTools.addMinutes(dutyTime, minute);
                                if (checkTime.before(lateAndEarlyOnTime)) {
                                    checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                                }
                            }
                        }

                    } else if (record.getCheckInType().equals(AttendanceV2CheckInRecord.OffDuty)) {
                        Date offDutyTime = parseRecordDutyTime(record, record.getPreDutyTime());
                        checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                        // 조퇴
                        // 현재 체크 시간이 체크해야 할 시간보다 작고 같은 시간이 아님. 예: 18:00 체크가 조퇴로 판단되는 것 방지
                        if (checkTime.before(offDutyTime) && !dutyTimeString.equals(checkTimeString)) {
                            checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_Early;
                        }
                        // 일찍 가도 됨
                        if (StringUtils.isNotEmpty(shift.getLateAndEarlyOffTime())) {
                            int minute = -1;
                            try {
                                minute = Integer.parseInt(shift.getLateAndEarlyOffTime());
                            } catch (Exception e) {
                            }
                            if (minute > 0) {
                                Date lateAndEarlyOffTime = DateTools.addMinutes(offDutyTime,
                                        -minute);
                                if (checkTime.after(lateAndEarlyOffTime)) {
                                    checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                                }
                            }
                        }
                        // 근무 시간 검사
                        if (checkInResult.equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL)
                            && BooleanUtils.isTrue(shift.getNeedLimitWorkTime())
                            && shift.getWorkTime() > 0) {
                            // 현재 체크의 recordString으로 해당 체크 기록 조회, 날짜를 넘길 수 있으므로 같은 그룹의 체크 기록 조회 필요
                            List<AttendanceV2CheckInRecord> recordList = business.getAttendanceV2ManagerFactory()
                                    .listRecordWithPersonAndDate(record.getUserId(),
                                            record.getRecordDateString());
                            if (recordList == null || recordList.isEmpty()) {
                                throw new ExceptionNoTodayRecordList();
                            }
                            // 마지막 체크로 확정
                            if (record.getId()
                                    .equals(recordList.get(recordList.size() - 1).getId())) {
                                long realWorkTime = 0;
                                // 출근 체크
                                List<AttendanceV2CheckInRecord> onDutyList = recordList.stream()
                                        .filter(
                                                (r) -> r.getCheckInType()
                                                        .equals(AttendanceV2CheckInRecord.OnDuty))
                                        .sorted(Comparator.comparing(
                                                AttendanceV2CheckInRecord::getRecordDate))
                                        .collect(Collectors.toList());
                                // 퇴근 체크
                                List<AttendanceV2CheckInRecord> offDutyList = recordList.stream()
                                        .filter(
                                                (r) -> r.getCheckInType()
                                                        .equals(AttendanceV2CheckInRecord.OffDuty))
                                        .sorted(Comparator.comparing(
                                                AttendanceV2CheckInRecord::getRecordDate))
                                        .collect(Collectors.toList());
                                for (int i = 0; i < onDutyList.size(); i++) {
                                    AttendanceV2CheckInRecord onDuty = onDutyList.get(i);
                                    AttendanceV2CheckInRecord offDuty = offDutyList.get(i);
                                    if (offDuty.getId().equals(record.getId())) {
                                        realWorkTime += (checkTime.getTime()
                                                         - onDuty.getRecordDate().getTime());
                                    } else {
                                        realWorkTime += (offDuty.getRecordDate().getTime()
                                                         - onDuty.getRecordDate().getTime());
                                    }
                                }
                                if (realWorkTime < shift.getWorkTime()) { // 근무 시간 부족, 조퇴로 표시
                                    checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_Early;
                                    LOGGER.info(
                                            "시간 부족, 조퇴로 표시, person {} , realWorkTime {} , needWorkTime {}",
                                            record.getUserId(), "" + realWorkTime,
                                            "" + shift.getWorkTime());
                                }
                            }
                        }

                    }
                }

                // 체크 업데이트
                emc.beginTransaction(AttendanceV2CheckInRecord.class);
                record.setRecordDate(checkTime);
                record.setCheckInResult(checkInResult);
                if (workPlace != null) {
                    record.setWorkPlaceId(workPlace.getId());
                    record.setPlaceName(workPlace.getPlaceName());
                }

                if ("outside".equals(wi.getFrom())) {
                    record.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_SYSTEM_IMPORT);
                    record.setSourceDevice(wi.getSource());
                    record.setDescription("");
                    record.setFieldWork(false);
                    record.setSignDescription("");
                    record.setLatitude("");
                    record.setLongitude("");
                    record.setRecordAddress("");
                } else if ("app".equals(wi.getFrom())) {
                    if (StringUtils.isNotEmpty(wi.getSourceType())) {
                        record.setSourceType(wi.getSourceType());
                    } else {
                        record.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_USER_CHECK);
                    }
                    record.setSourceDevice(wi.getSourceDevice());
                    if (StringUtils.isNotEmpty(wi.getDescription())) {
                        record.setDescription(wi.getDescription());
                    } else {
                        record.setDescription("");
                    }
                    record.setFieldWork(wi.getFieldWork());
                    record.setSignDescription(wi.getSignDescription());
                    record.setFieldWorkPhotoFileIdList(wi.getFieldWorkPhotoFileIdList());
                    record.setLatitude(wi.getLatitude());
                    record.setLongitude(wi.getLongitude());
                    record.setRecordAddress(wi.getRecordAddress());
                }
                emc.check(record, CheckPersistType.all);
                emc.commit();
                LOGGER.info("checkIn 체크 데이터 기록, 체크 사용자: {}, 체크 날짜: {}, 체크 결과: {} ",
                        record.getUserId(), today, checkInResult);
                return record;
            }
        }

    }


    /**
     * 체크 결과 이상 여부를 검사해 해당 소명 데이터 생성. 이 메서드는 체크 시 사용하므로 원래 이상이던 체크가 지금은 정상일 수도 있음. 예: 일찍 체크해 조퇴였으나 퇴근 후 체크를 업데이트한 경우 소명 데이터를 삭제해야 함
     *
     * @param record   체크 기록
     * @param emc      데이터베이스 조작 객체
     * @param business 업무 객체
     */
    protected void generateAppealInfo(AttendanceV2CheckInRecord record, boolean fieldWorkMarkError,
            EntityManagerContainer emc, Business business) {
        try {
            if (record != null) {
                if (BooleanUtils.isTrue(record.getFieldWork())) {
                    fieldWorkScriptInvokeAsync(record.getId());
                }
                List<AttendanceV2AppealInfo> appealList = business.getAttendanceV2ManagerFactory()
                        .listAppealInfoWithRecordId(record.getId());
                if (record.checkResultException(fieldWorkMarkError)) { // 이상 데이터
                    if (appealList != null && !appealList.isEmpty()) {
                        LOGGER.info("현재 체크 기록에 소명 데이터가 이미 있어 중복 생성할 필요가 없습니다! {}",
                                record.getId());
                        return;
                    }
                    AttendanceV2AppealInfo appealInfo = new AttendanceV2AppealInfo();
                    appealInfo.setRecordId(record.getId());
                    appealInfo.setRecordDateString(record.getRecordDateString());
                    appealInfo.setRecordDate(record.getRecordDate());
                    appealInfo.setUserId(record.getUserId());
                    emc.beginTransaction(AttendanceV2AppealInfo.class);
                    emc.persist(appealInfo, CheckPersistType.all);
                    emc.commit();
                    LOGGER.info("해당 이상 체크 신청 데이터 생성, {}", appealInfo.toString());
                } else if (StringUtils.isEmpty(record.getAppealId())) {  // 정상 데이터 // 소명된 데이터는 삭제하지 않음
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
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }


    /**
     * 해당 체크 기록 찾기
     *
     * @param recordList   기록 목록
     * @param currentIndex 몇 번째
     * @param checkType    OnDuty OffDuty
     */
    protected AttendanceV2CheckInRecord hasCheckedRecord(List<AttendanceV2CheckInRecord> recordList,
            int currentIndex, String checkType) {
        if (recordList == null || recordList.isEmpty()) {
            return null;
        }
        List<AttendanceV2CheckInRecord> list = recordList.stream()
                .filter(r -> r.getCheckInType().equals(checkType)).collect(Collectors.toList());
        if (!list.isEmpty() && currentIndex <= list.size() - 1) {
            return list.get(currentIndex);
        }
        return null;
    }


    /**
     * 교대에 따라 사전 저장 체크 기록 처리
     *
     * @param shift
     * @param emc
     * @param person
     * @param group
     * @param nowDate
     * @param today
     * @param recordList
     * @throws Exception
     */
    private List<AttendanceV2CheckInRecord> dealShiftForRecord(AttendanceV2Shift shift,
            EntityManagerContainer emc,
            Business business,
            String person, AttendanceV2Group group, Date nowDate, String today,
            List<AttendanceV2CheckInRecord> recordList) throws Exception {
        List<AttendanceV2ShiftCheckTime> timeList = shift.getProperties().getTimeList();
        if (timeList == null || timeList.isEmpty()) {
            LOGGER.info("해당하는 출퇴근 체크 시간이 없습니다");
            // return null;
            throw new ExceptionEmptyParameter("해당하는 출퇴근 체크 시간이 없습니다");
        }
        // 데이터가 없으면 먼저 교대 체크 정보에 따라 체크 데이터 사전 저장
        if (recordList == null) {
            recordList = createNewRecordList(shift, emc, person, group, today, timeList);
        } else if (recordList.size() < (timeList.size() * 2)) {
            // 이상 데이터 처리를 위한 부분. recordList 생성 로직은 위에 있지만 데이터베이스 오류 등으로 recordList 데이터가 완전히 저장되지 않을 수 있음
            List<AttendanceV2CheckInRecord> recordListNew = createNewRecordList(shift, emc, person,
                    group, today, timeList, false);
            for (AttendanceV2CheckInRecord newRecord : recordListNew) {
                AttendanceV2CheckInRecord oldOnDutyRecord = findRecordByExist(recordList,
                        newRecord.getCheckInType(), today,
                        newRecord.getPreDutyTime());
                if (oldOnDutyRecord != null) {
                    emc.beginTransaction(AttendanceV2CheckInRecord.class);
                    newRecord.setCheckInResult(oldOnDutyRecord.getCheckInResult());
                    newRecord.setRecordDate(oldOnDutyRecord.getRecordDate());
                    newRecord.setLatitude(oldOnDutyRecord.getLatitude());
                    newRecord.setLongitude(oldOnDutyRecord.getLongitude());
                    newRecord.setSourceDevice(oldOnDutyRecord.getSourceDevice());
                    newRecord.setSourceType(oldOnDutyRecord.getSourceType());
                    newRecord.setAppealId(oldOnDutyRecord.getAppealId());
                    newRecord.setRecordAddress(oldOnDutyRecord.getRecordAddress());
                    newRecord.setWorkPlaceId(oldOnDutyRecord.getWorkPlaceId());
                    newRecord.setPlaceName(oldOnDutyRecord.getPlaceName());
                    emc.persist(newRecord, CheckPersistType.all);
                    emc.commit();
                }
            }
            // 기존 데이터 삭제
            deleteOldRecordList(recordList, emc, business);
            // 이미 지난 체크 기록을 미체크로 자동 처리
            dealWithOvertimeRecord(emc, business,
                    BooleanUtils.isTrue(group.getFieldWorkMarkError()),
                    nowDate, recordListNew);
            return recordListNew;
        }
        // 이미 지난 체크 기록을 미체크로 자동 처리
        dealWithOvertimeRecord(emc, business, BooleanUtils.isTrue(group.getFieldWorkMarkError()),
                nowDate, recordList);
        return recordList;
    }

    private void deleteOldRecordList(List<AttendanceV2CheckInRecord> oldRecordList,
            EntityManagerContainer emc, Business business) throws Exception {
        List<String> deleteIds = new ArrayList<>();
        String personDn = "";
        String date = "";
        for (AttendanceV2CheckInRecord record : oldRecordList) {
            if (StringUtils.isEmpty(personDn)) {
                personDn = record.getUserId();
            }
            if (StringUtils.isEmpty(date)) {
                date = record.getRecordDateString();
            }
            deleteIds.add(record.getId());
            // 이상 데이터가 있으면 함께 삭제해야 함
            List<AttendanceV2AppealInfo> appealList = business.getAttendanceV2ManagerFactory()
                    .listAppealInfoWithRecordId(record.getId());
            if (appealList != null && !appealList.isEmpty()) {
                List<String> appealListDeleteIds = new ArrayList<>();
                for (AttendanceV2AppealInfo appealInfo : appealList) {
                    appealListDeleteIds.add(appealInfo.getId());
                }
                emc.beginTransaction(AttendanceV2AppealInfo.class);
                emc.delete(AttendanceV2AppealInfo.class, appealListDeleteIds);
                emc.commit();
            }
        }
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        emc.delete(AttendanceV2CheckInRecord.class, deleteIds);
        emc.commit();
        LOGGER.info("체크 기록 삭제: {}, {}, {} ", personDn, date, ListTools.toStringJoin(deleteIds));
    }

    private List<AttendanceV2CheckInRecord> createNewRecordList(AttendanceV2Shift shift,
            EntityManagerContainer emc, String person, AttendanceV2Group group, String today,
            List<AttendanceV2ShiftCheckTime> timeList) throws Exception {
        return createNewRecordList(shift, emc, person, group, today, timeList, true);
    }

    private List<AttendanceV2CheckInRecord> createNewRecordList(AttendanceV2Shift shift,
            EntityManagerContainer emc, String person, AttendanceV2Group group, String today,
            List<AttendanceV2ShiftCheckTime> timeList, boolean reuseExisting) throws Exception {
        List<AttendanceV2CheckInRecord> recordList = new ArrayList<>();
        for (AttendanceV2ShiftCheckTime shiftCheckTime : timeList) {
            // 출근 체크
            AttendanceV2CheckInRecord onDutyRecord = savePreCheckInRecord(emc, person,
                    AttendanceV2CheckInRecord.OnDuty, group, shift, today,
                    shiftCheckTime.getOnDutyTime(), shiftCheckTime.getOnDutyTimeBeforeLimit(),
                    shiftCheckTime.getOnDutyTimeAfterLimit(), false, reuseExisting);
            recordList.add(onDutyRecord);
            // 퇴근 체크

            AttendanceV2CheckInRecord offDutyRecord = savePreCheckInRecord(emc, person,
                    AttendanceV2CheckInRecord.OffDuty, group, shift, today,
                    shiftCheckTime.getOffDutyTime(), shiftCheckTime.getOffDutyTimeBeforeLimit(),
                    shiftCheckTime.getOffDutyTimeAfterLimit(),
                    BooleanUtils.isTrue(shiftCheckTime.getOffDutyNextDay()), reuseExisting);
            recordList.add(offDutyRecord);
        }
        return recordList;
    }

    /**
     * 기존 체크 기록 조회
     */
    private AttendanceV2CheckInRecord findRecordByExist(List<AttendanceV2CheckInRecord> recordList,
            String dutyType, String today, String dutyTime) {
        if (recordList == null || recordList.isEmpty()) {
            return null;
        }
        // 체크 시간
        if (StringUtils.isEmpty(dutyTime)) {
            if (AttendanceV2CheckInRecord.OnDuty.equals(dutyType)) {
                dutyTime = "09:00";
            } else {
                dutyTime = "18:00";
            }
        }
        String finalDutyTime = dutyTime;
        return recordList.stream()
                .filter((r) -> StringUtils.equals(r.getCheckInType(), dutyType)
                               && StringUtils.equals(r.getPreDutyTime(), finalDutyTime)
                               && StringUtils.equals(r.getRecordDateString(), today))
                .findFirst().orElse(null);
    }

    private Date parseRecordDutyTime(AttendanceV2CheckInRecord record, String dutyTime)
            throws Exception {
        Date date = DateTools.parse(record.getRecordDateString() + " " + dutyTime,
                DateTools.format_yyyyMMddHHmm);
        if (AttendanceV2CheckInRecord.OffDuty.equals(record.getCheckInType())
            && BooleanUtils.isTrue(record.getOffDutyNextDay())) {
            date = DateTools.addDay(date, 1);
        }
        return date;
    }

    /**
     * 시간이 지난 체크 기록을 미체크로 표시
     *
     * @param emc
     * @param nowDate
     * @param recordList
     * @throws Exception
     */
    private void dealWithOvertimeRecord(EntityManagerContainer emc, Business business,
            boolean fieldWorkMarkError, Date nowDate, List<AttendanceV2CheckInRecord> recordList)
            throws Exception {
        for (int i = 0; i < recordList.size(); i++) {
            AttendanceV2CheckInRecord record = recordList.get(i);
            // 사전 체크 데이터가 아니면 건너뜀
            if (record.getCheckInResult()
                    .equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn)) {
                if (StringUtils.isNotEmpty(record.getPreDutyTimeAfterLimit())) { // 체크 종료 제한 있음
                    Date onDutyAfterTime = parseRecordDutyTime(record,
                            record.getPreDutyTimeAfterLimit());
                    if (nowDate.after(onDutyAfterTime)) { // 체크 종료 제한 시간을 넘겨 바로 미체크 데이터 생성
                        update2NoCheckInRecord(emc, record);
                        generateAppealInfo(record, fieldWorkMarkError, emc, business);
                    }
                } else {
                    Date onDutyTime = parseRecordDutyTime(record, record.getPreDutyTime());
                    if (nowDate.after(onDutyTime)) {
                        // 다음 데이터 조회: 퇴근 체크
                        if (i < recordList.size() - 1) {
                            AttendanceV2CheckInRecord nextRecord = recordList.get(i + 1);
                            Date offDutyTime = parseRecordDutyTime(nextRecord,
                                    nextRecord.getPreDutyTime());
                            long minutes = (offDutyTime.getTime() - onDutyTime.getTime()) / 60000
                                           / 2; // 간격 시간의 절반
                            Date middleTime = DateTools.addMinutes(onDutyTime, (int) minutes);
                            if (nowDate.after(middleTime)) { // 미체크 데이터 생성
                                update2NoCheckInRecord(emc, record);
                                generateAppealInfo(record, fieldWorkMarkError, emc, business);
                            }
                        }

                    }
                }
            }
        }
    }

    /**
     * 사전 저장 체크 데이터 저장
     *
     * @offDutyNextDay 익일 여부, 마지막 퇴근 체크에만 있음
     */
    private AttendanceV2CheckInRecord savePreCheckInRecord(EntityManagerContainer emc,
            String person, String dutyType,
            AttendanceV2Group group, AttendanceV2Shift shift, String today,
            String dutyTime, String dutyTimeBeforeLimit, String dutyTimeAfterLimit,
            boolean offDutyNextDay) throws Exception {
        return savePreCheckInRecord(emc, person, dutyType, group, shift, today, dutyTime,
                dutyTimeBeforeLimit, dutyTimeAfterLimit, offDutyNextDay, true);
    }

    private AttendanceV2CheckInRecord savePreCheckInRecord(EntityManagerContainer emc,
            String person, String dutyType,
            AttendanceV2Group group, AttendanceV2Shift shift, String today,
            String dutyTime, String dutyTimeBeforeLimit, String dutyTimeAfterLimit,
            boolean offDutyNextDay, boolean reuseExisting) throws Exception {
        // 체크 시간
        if (StringUtils.isEmpty(dutyTime)) {
            if (AttendanceV2CheckInRecord.OnDuty.equals(dutyType)) {
                dutyTime = "09:00";
            } else {
                dutyTime = "18:00";
            }
        }
        if (reuseExisting) {
            Business business = new Business(emc);
            AttendanceV2CheckInRecord existing = findRecordByExist(
                    business.getAttendanceV2ManagerFactory()
                            .listRecordWithPersonAndDate(person, today),
                    dutyType, today, dutyTime);
            if (existing != null) {
                return existing;
            }
        }
        AttendanceV2CheckInRecord noCheckRecord = new AttendanceV2CheckInRecord();
        noCheckRecord.setCheckInType(dutyType);
        noCheckRecord.setCheckInResult(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn);
        noCheckRecord.setUserId(person);
        Date onDutyTime = DateTools.parse(today + " " + dutyTime, DateTools.format_yyyyMMddHHmm);
        if (AttendanceV2CheckInRecord.OffDuty.equals(dutyType) && offDutyNextDay) {
            Date nextDate = DateTools.addDay(onDutyTime, 1);
            noCheckRecord.setRecordDate(nextDate);
        } else {
            noCheckRecord.setRecordDate(onDutyTime);
        }
        noCheckRecord.setRecordDateString(today);
        noCheckRecord.setPreDutyTime(dutyTime);
        noCheckRecord.setPreDutyTimeBeforeLimit(dutyTimeBeforeLimit);
        noCheckRecord.setPreDutyTimeAfterLimit(dutyTimeAfterLimit);
        noCheckRecord.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_AUTO_CHECK);
        noCheckRecord.setSourceDevice("기타");
        noCheckRecord.setDescription("시스템 생성, 사전 출퇴근 기록");
        noCheckRecord.setGroupId(group.getId());
        noCheckRecord.setGroupName(group.getGroupName());
        noCheckRecord.setGroupCheckType(group.getCheckType());
        noCheckRecord.setOffDutyNextDay(offDutyNextDay);
        if (shift != null) {
            noCheckRecord.setShiftId(shift.getId());
            noCheckRecord.setShiftName(shift.getShiftName());
        }
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        emc.persist(noCheckRecord, CheckPersistType.all);
        emc.commit();
        LOGGER.info("체크 기록 저장: {}, {}, {} ", person, today,
                AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn);
        return noCheckRecord;
    }

    /**
     * 미체크로 업데이트
     *
     * @param emc
     * @param record
     * @throws Exception
     */
    private void update2NoCheckInRecord(EntityManagerContainer emc,
            AttendanceV2CheckInRecord record) throws Exception {
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        record.setCheckInResult(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned);
        record.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_AUTO_CHECK);
        record.setSourceDevice("기타");
        record.setDescription("시스템 생성, 누락 기록");
        emc.persist(record, CheckPersistType.all);
        emc.commit();
    }


    private void fieldWorkScriptInvokeAsync(String recordId) {
        if (StringUtils.isEmpty(recordId)) {
            return;
        }
        ClassLoader classLoader = AttendanceV2ConfigProperties.class.getClassLoader();

        CompletableFuture.runAsync(() -> {
            Thread thread = Thread.currentThread();
            ClassLoader oldClassLoader = thread.getContextClassLoader();
            try {
                thread.setContextClassLoader(classLoader);
                executeWithCheckLock("fieldWorkScriptInvoke:" + recordId, () -> {
                    fieldWorkScriptInvoke(recordId);
                    return null;
                });
            } catch (Exception e) {
                LOGGER.error(e);
            } finally {
                thread.setContextClassLoader(oldClassLoader);
            }
        }, ThisApplication.forkJoinPool());
    }
    // 외근 체크에 스크립트가 설정되어 있으면 스크립트 실행
    private void fieldWorkScriptInvoke(String recordId) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            AttendanceV2CheckInRecord record = emc.find(recordId, AttendanceV2CheckInRecord.class);
            if (record == null) {
                throw new ExceptionNotExistObject("체크 기록");
            }
            if (BooleanUtils.isNotTrue(record.getFieldWork())) {
                LOGGER.debug("외근 체크 기록이 아니어서 외근 체크 스크립트를 실행하지 않음, recordId: {}", recordId);
                return;
            }
            String invokeScriptName = null;
            List<AttendanceV2Config> list = emc.listAll(AttendanceV2Config.class);
            if (list != null && !list.isEmpty()) {
                AttendanceV2Config config = list.get(0);
                if (StringUtils.isNotEmpty(config.getFieldWorkExecuteScript())) {
                    invokeScriptName = config.getFieldWorkExecuteScript();
                }
            }
            if (StringUtils.isEmpty(invokeScriptName)) {
                LOGGER.debug("외근 체크 실행 스크립트가 설정되지 않아 외근 체크 스크립트를 실행하지 않음");
                return;
            }
            if (  StringUtils.isNotEmpty(record.getFieldWorkJobId())) {
                LOGGER.debug("외근 체크 스크립트가 이미 실행되어 중복 실행하지 않음, recordId: {}", recordId);
                return;
            }
            FieldWorkScriptInvokeBody body = new FieldWorkScriptInvokeBody();
            body.setRecord(record);
            ActionResponse response = CipherConnectionAction.post(false, 4000, 8000,
                    Config.url_x_program_center_jaxrs("invoke", invokeScriptName, "execute"), body);
            FieldWorkScriptInvokeResponse wo = response.getData(
                    FieldWorkScriptInvokeResponse.class);
            if (wo != null && StringUtils.isNotEmpty(wo.getJobid())) {
                LOGGER.debug("외근 체크 스크립트 실행 완료, jobid: {}", wo.getJobid());
                emc.beginTransaction(AttendanceV2CheckInRecord.class);
                record.startFieldWorkJob(wo.getJobid());
                emc.persist(record, CheckPersistType.all);
                emc.commit();
            } else {
                LOGGER.debug("외근 체크 스크립트 실행 완료, response: {}", response);
            }
            LOGGER.info("외근 체크 스크립트 실행 완료, recordId: {}, invokeScriptName: {}", recordId,
                    invokeScriptName);
        }
    }


    public static class FieldWorkScriptInvokeBody extends GsonPropertyObject {

        @FieldDescribe("외근 invoke 스크립트 실행용 post 객체")
        private AttendanceV2CheckInRecord record;

        public AttendanceV2CheckInRecord getRecord() {
            return record;
        }

        public void setRecord(AttendanceV2CheckInRecord record) {
            this.record = record;
        }
    }

    public static class FieldWorkScriptInvokeResponse extends GsonPropertyObject {

        @FieldDescribe("외근 invoke 스크립트 실행 후 반환 결과")
        private String jobid;

        public String getJobid() {
            return jobid;
        }

        public void setJobid(String jobid) {
            this.jobid = jobid;
        }
    }

    public static class CheckInWi extends GsonPropertyObject {


        public static CheckInWi fromOutside(ActionCheckInRecordFromOut.Wi wi) {
            CheckInWi checkInWi = new CheckInWi();
            checkInWi.setFrom("outside");
            checkInWi.setPerson(wi.getPerson());
            checkInWi.setCheckInTime(wi.getCheckInTime());
            checkInWi.setSource(wi.getSource());
            checkInWi.setGenerateErrorInfo(wi.getGenerateErrorInfo());
            return checkInWi;
        }

        public static CheckInWi fromApp(ActionCheckIn.Wi wi) {
            CheckInWi checkInWi = new CheckInWi();
            checkInWi.setFrom("app");
            checkInWi.setRecordId(wi.getRecordId());
            checkInWi.setCheckInType(wi.getCheckInType());
            checkInWi.setWorkPlaceId(wi.getWorkPlaceId());
            checkInWi.setFieldWork(wi.getFieldWork());
            checkInWi.setSignDescription(wi.getSignDescription());
            checkInWi.setFieldWorkPhotoFileIdList(wi.getFieldWorkPhotoFileIdList());
            checkInWi.setSourceDevice(wi.getSourceDevice());
            checkInWi.setDescription(wi.getDescription());
            checkInWi.setLongitude(wi.getLongitude());
            checkInWi.setLatitude(wi.getLatitude());
            checkInWi.setRecordAddress(wi.getRecordAddress());
            checkInWi.setSourceType(wi.getSourceType());
            return checkInWi;
        }

        @FieldDescribe("app|outside")
        private String from;


        // 외부 출처 필드
        @FieldDescribe("사용자 고유 식별자")
        private String person;

        @FieldDescribe("체크 시간(Unix 타임스탬프)")
        private Long checkInTime;

        @FieldDescribe("출처, 예: 출입 통제 시스템")
        private String source;

        @FieldDescribe("이상 데이터 생성 여부")
        private Boolean generateErrorInfo;


        // 모바일 체크 필드
        @FieldDescribe("체크 대상 id")
        private String recordId;

        @FieldDescribe("출퇴근 체크 유형, OnDuty OffDuty")
        private String checkInType;

        @FieldDescribe("체크 근무지 id, 범위 내 체크 시 전달 필요")
        private String workPlaceId;

        @FieldDescribe("외근 체크 여부.")
        private Boolean fieldWork;

        @FieldDescribe("외근 체크 설명")
        private String signDescription;

        @FieldDescribe("외근 체크 사진 첨부파일 ID 목록")
        private List<String> fieldWorkPhotoFileIdList;

        @FieldDescribe("출처 기기: Mac|Windows|IOS|Android|기타")
        private String sourceDevice;

        @FieldDescribe("기타 설명 메모")
        private String description;

        @FieldDescribe("현재 위치 경도")
        private String longitude;

        @FieldDescribe("현재 위치 위도")
        private String latitude;

        @FieldDescribe("현재 위치 장소 설명")
        private String recordAddress;

        @FieldDescribe("체크 데이터 출처: USER_CHECK(사용자 체크) FAST_CHECK(빠른 체크) ")
        private String sourceType;


        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public String getRecordId() {
            return recordId;
        }

        public void setRecordId(String recordId) {
            this.recordId = recordId;
        }

        public String getCheckInType() {
            return checkInType;
        }

        public void setCheckInType(String checkInType) {
            this.checkInType = checkInType;
        }

        public String getWorkPlaceId() {
            return workPlaceId;
        }

        public void setWorkPlaceId(String workPlaceId) {
            this.workPlaceId = workPlaceId;
        }

        public Boolean getFieldWork() {
            return fieldWork;
        }

        public void setFieldWork(Boolean fieldWork) {
            this.fieldWork = fieldWork;
        }

        public String getSignDescription() {
            return signDescription;
        }

        public void setSignDescription(String signDescription) {
            this.signDescription = signDescription;
        }

        public List<String> getFieldWorkPhotoFileIdList() {
            return fieldWorkPhotoFileIdList;
        }

        public void setFieldWorkPhotoFileIdList(List<String> fieldWorkPhotoFileIdList) {
            this.fieldWorkPhotoFileIdList = fieldWorkPhotoFileIdList;
        }

        public String getSourceDevice() {
            return sourceDevice;
        }

        public void setSourceDevice(String sourceDevice) {
            this.sourceDevice = sourceDevice;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getRecordAddress() {
            return recordAddress;
        }

        public void setRecordAddress(String recordAddress) {
            this.recordAddress = recordAddress;
        }

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public Long getCheckInTime() {
            return checkInTime;
        }

        public void setCheckInTime(Long checkInTime) {
            this.checkInTime = checkInTime;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public Boolean getGenerateErrorInfo() {
            return generateErrorInfo;
        }

        public void setGenerateErrorInfo(Boolean generateErrorInfo) {
            this.generateErrorInfo = generateErrorInfo;
        }
    }
}
