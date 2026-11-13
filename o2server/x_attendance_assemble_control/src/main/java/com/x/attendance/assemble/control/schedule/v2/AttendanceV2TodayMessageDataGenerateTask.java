package com.x.attendance.assemble.control.schedule.v2;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.WoGroupShift;
import com.x.attendance.entity.v2.*;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.BooleanUtils;
import org.quartz.JobExecutionContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 매일 새벽 3시 30분에 당일 발송할 메시지 데이터 생성 Created by fancyLou on 2023/5/8. Copyright © 2023 O2. All rights
 * reserved.
 */
public class AttendanceV2TodayMessageDataGenerateTask extends AbstractJob {

    private static final Logger logger = LoggerFactory.getLogger(
            AttendanceV2TodayMessageDataGenerateTask.class);

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Date nowDate = new Date();
            logger.info(
                    "======================신규 근태 메시지 생성 타이머 실행 시작, 날짜: {}=============================",
                    nowDate.toString());
            if (logger.isDebugEnabled()) {
                logger.debug("먼저 기존 데이터 삭제=====================7일 이전 데이터");
            }
            Business business = new Business(emc);
            Date now = new Date();
            now = DateTools.addDay(now, -7);
            List<AttendanceV2AlertMessage> messageList = business.getAttendanceV2ManagerFactory()
                    .listAlertMessageBeforeDate(now);
            if (messageList != null && !messageList.isEmpty()) {
                List<String> ids = new ArrayList<>();
                for (int i = 0; i < messageList.size(); i++) {
                    ids.add(messageList.get(i).getId());
                }
                emc.beginTransaction(AttendanceV2AlertMessage.class);
                emc.delete(AttendanceV2AlertMessage.class, ids);
                emc.commit();
            }
            if (logger.isDebugEnabled()) {
                logger.debug(" 새 데이터 생성 시작=====================");
            }
            List<AttendanceV2Config> configs = emc.listAll(AttendanceV2Config.class);
            AttendanceV2Config config;
            if (configs != null && !configs.isEmpty()) {
                config = configs.get(0);
            } else {
                config = new AttendanceV2Config();
            }
            // 메시지 사용
            if (!BooleanUtils.isTrue(config.getCheckInAlertEnable())) {
                return;
            }
            String today = DateTools.format(nowDate, DateTools.format_yyyyMMdd);
            if (logger.isDebugEnabled()) {
                logger.debug("날짜: {}", today);
            }
            List<AttendanceV2Group> groups = emc.listAll(AttendanceV2Group.class);
            if (groups == null || groups.isEmpty()) {
                return;
            }
            for (AttendanceV2Group group : groups) {
                // 유효하지 않은 근태 그룹
                if (group.getStatus() == AttendanceV2Group.status_auto) {
                    continue;
                }
                if (!group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Fixed)
                    && !group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Arrangement)) {
                    continue;
                }
                for (String person : group.getTrueParticipantList()) {
                    WoGroupShift woGroupShift = business.getAttendanceV2ManagerFactory()
                            .getGroupShiftByPersonDate(person, today);
                    AttendanceV2Shift shift = woGroupShift.getShift();
                    if (shift == null) { // 근무 시간대 객체가 없어 메시지를 보낼 필요 없음
                        continue;
                    }
                    List<AttendanceV2ShiftCheckTime> timeList = Optional.ofNullable(
                                    shift.getProperties().getTimeList())
                            .orElse(Collections.emptyList());
                    if (timeList.isEmpty()) {
                        continue;
                    }
                    List<AttendanceV2PersonConfig> list = business.getAttendanceV2ManagerFactory()
                            .personConfigWithPerson(person);
                    boolean isOnDutySendPerson = true;
                    boolean isOffDutySendPerson = true;
                    if (list != null && !list.isEmpty()) {
                        AttendanceV2PersonConfig personConfig = list.get(0);
                        if (personConfig.getProperties() != null && BooleanUtils.isFalse(
                                personConfig.getProperties().getCheckInAlertOnDutyEnable())) {
                            isOnDutySendPerson = false;
                        }
                        if (personConfig.getProperties() != null && BooleanUtils.isFalse(
                                personConfig.getProperties().getCheckInAlertOffDutyEnable())) {
                            isOffDutySendPerson = false;
                        }
                    }
                    for (AttendanceV2ShiftCheckTime shiftCheckTime : timeList) {
                        // 출근 알림 메시지
                        if (isOnDutySendPerson) {
                            saveMessage(emc, today, person, shiftCheckTime.getOnDutyTime(),
                                    AttendanceV2CheckInRecord.OnDuty, false, config);
                        }
                        // 퇴근 알림 메시지
                        if (isOffDutySendPerson) {
                            saveMessage(emc, today, person, shiftCheckTime.getOffDutyTime(),
                                    AttendanceV2CheckInRecord.OffDuty,
                                    BooleanUtils.isTrue(shiftCheckTime.getOffDutyNextDay()),
                                    config);
                        }
                    }
                }
            }
        }
    }

    /**
     * 데이터베이스에 데이터 저장
     *
     * @param emc
     * @param today
     * @param person
     * @param dutyTimeString
     * @param dutyType
     * @throws Exception
     */
    private void saveMessage(EntityManagerContainer emc, String today, String person,
            String dutyTimeString, String dutyType, boolean offDutyNext, AttendanceV2Config config)
            throws Exception {
        AttendanceV2AlertMessage messageOnDuty = new AttendanceV2AlertMessage();
        messageOnDuty.setUserId(person);
        String dutyTimeStringFull = today + " " + dutyTimeString;
        Date dutyTime = DateTools.parse(dutyTimeStringFull, DateTools.format_yyyyMMddHHmm);
        Integer checkInAlertOffDutyAfterMinutes = config.getProperties()
                .getCheckInAlertOffDutyAfterMinutes();
        Integer checkInAlertOnDutyBeforeMinutes = config.getProperties()
                .getCheckInAlertOnDutyBeforeMinutes();
        if (offDutyNext) { // 날짜 넘김
            dutyTime = DateTools.addDay(dutyTime, 1);
        }
        // 출근 체크는 checkInAlertOnDutyBeforeMinutes 분 전, 퇴근 체크는 퇴근 후 checkInAlertOffDutyAfterMinutes 분.
        if (AttendanceV2CheckInRecord.OnDuty.equals(dutyType)) {
            dutyTime = DateTools.addMinutes(dutyTime, -checkInAlertOnDutyBeforeMinutes);
        } else {
            dutyTime = DateTools.addMinutes(dutyTime, checkInAlertOffDutyAfterMinutes);
        }
        messageOnDuty.setCheckInType(dutyType);
        messageOnDuty.setSendDateTime(dutyTime);
        messageOnDuty.setSendStatus(false);
        emc.beginTransaction(AttendanceV2AlertMessage.class);
        emc.persist(messageOnDuty, CheckPersistType.all);
        emc.commit();
    }
}
