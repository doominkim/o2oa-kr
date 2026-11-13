package com.x.attendance.assemble.control.schedule.v2;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.*;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 근태 관련 알림 메시지 발송 작업
 * <p>
 * Created by fancyLou on 2023/5/8.
 * Copyright © 2023 O2. All rights reserved.
 */
public class AttendanceV2MessageSendTask extends AbstractJob {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceV2MessageSendTask.class);

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("======================신규 근태 메시지 발송 타이머 실행 시작==============================");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<AttendanceV2Config> list = emc.listAll(AttendanceV2Config.class);
            AttendanceV2Config config;
            if (list != null && !list.isEmpty()) {
                config = list.get(0);
            } else {
                config = new AttendanceV2Config();
            }
            // 이상 데이터 매일 알림
            exceptionMsgAlert(emc, config);
            // 출퇴근 체크 메시지 알림
            alertCheckinMessage(emc, config);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("======================신규 근태 메시지 발송 타이머 실행 완료==============================");
        }
    }

    /**
     * 출퇴근 체크 알림 메시지 발송
     *
     * @param emc
     */
    private void alertCheckinMessage(EntityManagerContainer emc, AttendanceV2Config config) {
        // 메시지 사용
        if (!BooleanUtils.isTrue(config.getCheckInAlertEnable())) {
            return;
        }
        try {
            Business business = new Business(emc);
            Date now = new Date();
            List<AttendanceV2AlertMessage> messageList = business.getAttendanceV2ManagerFactory().listAlertMessageBeforeDate(now);
            if (messageList == null || messageList.isEmpty()) {
                return;
            }
            for (AttendanceV2AlertMessage message : messageList) {
                // 중복 발송을 피하기 위해 메시지 상태를 먼저 갱신
                AttendanceV2AlertMessage old = emc.find(message.getId(), AttendanceV2AlertMessage.class);
                old.setSendStatus(true); // 이미 발송됨
                emc.beginTransaction(AttendanceV2AlertMessage.class);
                emc.persist(old, CheckPersistType.all);
                emc.commit();
                // 오늘 메시지가 아니므로 발송하지 않음
                String today = DateTools.format(now, DateTools.format_yyyyMMdd);
                String sendDate = DateTools.format(message.getSendDateTime(), DateTools.format_yyyyMMdd);
                if (!today.equals(sendDate)) {
                    continue;
                }
                // 개인 설정에서 실제 메시지 발송이 필요한지 판단
                List<AttendanceV2PersonConfig> list = business.getAttendanceV2ManagerFactory()
                        .personConfigWithPerson(message.getUserId());
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
                if (message.getCheckInType().equals(AttendanceV2CheckInRecord.OnDuty) && !isOnDutySendPerson) {
                    continue;
                }
                if (message.getCheckInType().equals(AttendanceV2CheckInRecord.OffDuty) && !isOffDutySendPerson) {
                    continue;
                }
                // 이미 출퇴근 체크했는지 판단
                List<AttendanceV2CheckInRecord> records = business.getAttendanceV2ManagerFactory().listRecordWithPersonAndDate(message.getUserId(), today);
                if (records != null && !records.isEmpty()) {
                    boolean isOnDutyCheckIn = records.stream().anyMatch(r -> AttendanceV2CheckInRecord.OnDuty.equals(r.getCheckInType()) && !AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn.equals(r.getCheckInResult()));
                    boolean isOffDutyCheckIn = records.stream().anyMatch(r -> AttendanceV2CheckInRecord.OffDuty.equals(r.getCheckInType()) && !AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn.equals(r.getCheckInResult()));
                    if (AttendanceV2CheckInRecord.OnDuty.equals(message.getCheckInType()) && isOnDutyCheckIn) {
                        continue;
                    }
                    if (AttendanceV2CheckInRecord.OffDuty.equals(message.getCheckInType()) && isOffDutyCheckIn) {
                        continue;
                    }
                }

                String title;
                if (AttendanceV2CheckInRecord.OnDuty.equals(message.getCheckInType())) {
                    title = "곧 출근 시간입니다. 출근 체크를 잊지 마세요!";
                } else {
                    title = "퇴근 시간입니다. 퇴근 체크를 잊지 마세요!";
                }
                MessageConnector.send(MessageConnector.TYPE_ATTENDANCE_CHECK_IN_ALERT, title, message.getUserId(), message);
            }
        }catch (Exception e) {
            logger.error(e);
        }

    }

    /**
     * 이상 데이터 알림
     *
     * @param emc
     */
    private void exceptionMsgAlert(EntityManagerContainer emc, AttendanceV2Config config) {
        try {
            // 이상 메시지 알림
            if (!BooleanUtils.isTrue(config.getAppealEnable()) || !BooleanUtils.isTrue(config.getExceptionAlertEnable())) {
                return;
            }
            String date = config.getExceptionAlertDate();
            String time = config.getExceptionAlertTime();
            if (StringUtils.isEmpty(time)) {
                time = "09:30"; // 기본 9시 30분 실행
            }
            Date now = new Date();
            String today = DateTools.format(now, DateTools.format_yyyyMMdd);
            Date alertTime = DateTools.parse(today + " " + time, DateTools.format_yyyyMMddHHmm);
            if (today.equals(date) || alertTime.after(now)) { // 오늘 이미 알림을 보냈거나 시간이 되지 않음
                return;
            }
            logger.info("이상 데이터 알림 발송 시작, 현재 시간: {} , 이상 데이터 알림 시간: {}", now.toString(), alertTime.toString());
            sendExceptionMsgAlert(emc, config);
            // 설정 갱신, today는 이미 알림을 보냈음을 의미
            config.setExceptionAlertDate(today);
            emc.beginTransaction(AttendanceV2Config.class);
            emc.persist(config, CheckPersistType.all);
            emc.commit();
        } catch (Exception e) {
            logger.error(e);
        }
    }


    /**
     * 이상 데이터 알림 발송
     */
    private void sendExceptionMsgAlert(EntityManagerContainer emc, AttendanceV2Config config) {
        try {
            Business business = new Business(emc);
            //
            Integer dateNumber = config.getExceptionAlertDateNumber();
            if (dateNumber == null || dateNumber < 0 ) {
                dateNumber = 1; // 기본으로 전날 이상 데이터를 알림
            }
            Date yesterday;
            if (dateNumber == 0) {
                yesterday = new Date();
            } else {
                yesterday = DateTools.addDay(new Date(), -(dateNumber));
            }
            String yesterdayString = DateTools.format(yesterday, DateTools.format_yyyyMMdd);
            List<AttendanceV2AppealInfo> list = business.getAttendanceV2ManagerFactory().listAppealInfoWithRecordDateString(yesterdayString);
            if (list == null || list.isEmpty()) {
                return;
            }
            Map<String, List<AttendanceV2AppealInfo>> map = list.stream().collect(Collectors.groupingBy(AttendanceV2AppealInfo::getUserId));
            for (Map.Entry<String, List<AttendanceV2AppealInfo>> entry : map.entrySet()) {
                List<AttendanceV2AppealInfo> appealInfoList = entry.getValue();
                if (appealInfoList != null && !appealInfoList.isEmpty()) {
                    String title = "처리할 이상 출퇴근 데이터가 " + appealInfoList.size() + "건 있습니다. 신속히 처리해 주세요!";
                    MessageConnector.send(MessageConnector.TYPE_ATTENDANCE_CHECK_IN_EXCEPTION, title, entry.getKey(), appealInfoList.get(0));
                    if (logger.isDebugEnabled()) {
                        logger.debug("이상 데이터 알림 메시지 발송, person: {}", entry.getKey());
                    }
                }
            }

        } catch (Exception e) {
            logger.error(e);
        }
    }
}
