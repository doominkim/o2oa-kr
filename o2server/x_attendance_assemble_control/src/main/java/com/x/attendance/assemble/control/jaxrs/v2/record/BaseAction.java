package com.x.attendance.assemble.control.jaxrs.v2.record;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

abstract class BaseAction extends StandardJaxrsAction {


  private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);

    /**
     * 기존 기록 먼저 삭제
     * @param personDn
     * @param date yyyy-MM-dd
     * @param emc
     * @param business
     * @throws Exception
     */
    protected void deleteOldRecords(String personDn, String date, EntityManagerContainer emc, Business business) throws Exception {
        // 기존 데이터 존재 여부 조회
        List<AttendanceV2CheckInRecord> oldRecordList = business.getAttendanceV2ManagerFactory().listRecordWithPersonAndDate(personDn, date);
        List<String> deleteIds = new ArrayList<>();
        for (AttendanceV2CheckInRecord record : oldRecordList) {
            deleteIds.add(record.getId());
            // 이상 데이터가 있으면 함께 삭제해야 함
            List<AttendanceV2AppealInfo> appealList = business.getAttendanceV2ManagerFactory().listAppealInfoWithRecordId(record.getId());
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


    /**
     * 체크 데이터 저장
     * @param dutyType
     * @param person
     * @param date
     * @param recordDate
     * @param preDutyTime
     * @param dutyTimeBeforeLimit
     * @param dutyTimeAfterLimit
     * @param group
     * @param shift
     * @param emc
     * @throws Exception
     */
    protected void saveRecord(String dutyType, String person, String date, Date recordDate, String preDutyTime, String dutyTimeBeforeLimit, String dutyTimeAfterLimit,
                            AttendanceV2Group group, AttendanceV2Shift shift, EntityManagerContainer emc) throws Exception {
        AttendanceV2CheckInRecord noCheckRecord = new AttendanceV2CheckInRecord();
        noCheckRecord.setCheckInType(dutyType);
        noCheckRecord.setUserId(person);
        // 체크 시간
        if (StringUtils.isEmpty(preDutyTime)) {
            if (AttendanceV2CheckInRecord.OnDuty.equals(dutyType)) {
                preDutyTime = "09:00";
            } else {
                preDutyTime = "18:00";
            }
        }
        String checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
        // 교대에 따라 체크 결과 판단
        if (shift != null) {
            if (recordDate != null) {
                String dutyTimeString = date + " " + preDutyTime;
                String checkTimeString = DateTools.format(recordDate, DateTools.format_yyyyMMddHHmm);
                // 출근 체크
                if (dutyType.equals(AttendanceV2CheckInRecord.OnDuty)) {
                    Date dutyTime = DateTools.parse(date + " " + preDutyTime, DateTools.format_yyyyMMddHHmm);
                    checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                    // 지각
                    if (recordDate.after(dutyTime) && !dutyTimeString.equals(checkTimeString)) {
                        checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_Late;
                    }
                    // 심각한 지각
                    if (shift.getSeriousTardinessLateMinutes() > 0) {
                        Date seriousLateTime = DateTools.addMinutes(dutyTime, shift.getSeriousTardinessLateMinutes());
                        if (recordDate.after(seriousLateTime)) {
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
                            if (recordDate.before(lateAndEarlyOnTime)) {
                                checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                            }
                        }
                    }
                } else if (dutyType.equals(AttendanceV2CheckInRecord.OffDuty)) {
                    Date offDutyTime = DateTools.parse(date + " " + preDutyTime, DateTools.format_yyyyMMddHHmm);
                    checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                    // 조퇴
                    if (recordDate.before(offDutyTime) && !dutyTimeString.equals(checkTimeString)) {
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
                            Date lateAndEarlyOffTime = DateTools.addMinutes(offDutyTime, -minute);
                            if (recordDate.after(lateAndEarlyOffTime)) {
                                checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                            }
                        }
                    }

                }
            } else { // 체크 없음
                recordDate = DateTools.parse(date+" "+preDutyTime, DateTools.format_yyyyMMddHHmm);
                checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned;
            }
        } else  if (recordDate == null) {
            recordDate = DateTools.parse(date+" "+preDutyTime, DateTools.format_yyyyMMddHHmm);
        }

        noCheckRecord.setCheckInResult(checkInResult);
        noCheckRecord.setRecordDate(recordDate);
        noCheckRecord.setRecordDateString(date);
        noCheckRecord.setPreDutyTime(preDutyTime);
        noCheckRecord.setPreDutyTimeBeforeLimit(dutyTimeBeforeLimit);
        noCheckRecord.setPreDutyTimeAfterLimit(dutyTimeAfterLimit);
        noCheckRecord.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_SYSTEM_IMPORT);
        noCheckRecord.setSourceDevice("其他");
        noCheckRecord.setDescription("");
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
        LOGGER.info("체크 기록 저장: {}, {}, {} ", person, date, checkInResult);
    }

}
