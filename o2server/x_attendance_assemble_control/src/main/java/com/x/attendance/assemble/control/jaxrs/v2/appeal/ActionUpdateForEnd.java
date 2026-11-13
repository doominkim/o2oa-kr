package com.x.attendance.assemble.control.jaxrs.v2.appeal;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.schedule.v2.QueueAttendanceV2DetailModel;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 이의 신청 프로세스 종료 후 데이터 반영
 * Created by fancyLou on 2023/3/3.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionUpdateForEnd extends BaseAction {


    private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateForEnd.class);


    ActionResult<Wo> execute(String id, JsonElement jsonElement) throws Exception {
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if (StringUtils.isEmpty(id)) {
            throw new ExceptionEmptyParameter("id");
        }
        if (wi.getResult() == null) {
            throw new ExceptionEmptyParameter("result");
        }
        if (StringUtils.isEmpty(wi.getReason())) {
            throw new ExceptionEmptyParameter("reason");
        }
        if (StringUtils.isEmpty(wi.getJobId())) {
            throw new ExceptionEmptyParameter("jobId");
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("결재 종료 데이터 반영: {}", wi.toString());
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            AttendanceV2AppealInfo info = emc.find(id, AttendanceV2AppealInfo.class);
            if (info == null) {
                throw new ExceptionNotExistObject("데이터가 없습니다, " + id);
            }
            emc.beginTransaction(AttendanceV2AppealInfo.class);
            info.setStatus(wi.getResult() == 1 ? AttendanceV2AppealInfo.status_TYPE_PROCESS_AGREE : AttendanceV2AppealInfo.status_TYPE_PROCESS_DISAGREE); // 결재 결과
            info.setEndTime(DateTools.now());
            info.setReason(wi.getReason());
            info.setJobId(wi.getJobId());
            emc.check(info, CheckPersistType.all);
            emc.commit();
            if (wi.getResult() == 1) { // 결재 승인 시 출퇴근 기록 갱신, 아니면 변경 없음
                AttendanceV2CheckInRecord record = emc.find(info.getRecordId(), AttendanceV2CheckInRecord.class);
                if (record != null) {
                    emc.beginTransaction(AttendanceV2CheckInRecord.class);
                    // 이의 신청 성공 후 출퇴근 상태 갱신
                    record.setCheckInResult(AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL);
                    record.setAppealId(info.getId()); // 이의 신청 완료 후 심사 데이터 id 반영
                    record.setDescription("소명 완료!");
                    try {
                        if (BooleanUtils.isTrue(wi.getNeedResetTime())) {
                            Date onDutyTime = DateTools.parse(record.getRecordDateString() + " " + record.getPreDutyTime(), DateTools.format_yyyyMMddHHmm);
                            record.setRecordDate(onDutyTime);
                        }
                    } catch (Exception e) {
                        LOGGER.error(e);
                    }
                    emc.check(record, CheckPersistType.all);
                    emc.commit();
                    // 이의 신청 성공 후 해당 데이터 재생성
                    if (wi.getResult() == 1) {
                        LOGGER.info("이의 신청 성공, 근태 데이터 재생성 요청, Date: {} person: {}", record.getRecordDateString(), record.getUserId());
                        ThisApplication.queueV2Detail.send(new QueueAttendanceV2DetailModel(record.getUserId(), record.getRecordDateString()));
                    }
                } else {
                    LOGGER.info("해당 출퇴근 기록 데이터를 찾지 못했습니다,"+info.getRecordId());
                }
            }
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
            return result;
        }
    }

    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("결재 결과: 1-승인, -1-반려")
        private Integer result;
        @FieldDescribe("이의 신청 상세, 이의 신청 프로세스 종료 후 기록.")
        private String reason;
        @FieldDescribe("프로세스 jobId, 이의 신청 프로세스 종료 후 기록")
        private String jobId;
        @FieldDescribe("출퇴근 시간 초기화 필요 여부")
        private Boolean needResetTime;

        public Boolean getNeedResetTime() {
            return needResetTime;
        }

        public void setNeedResetTime(Boolean needResetTime) {
            this.needResetTime = needResetTime;
        }

        public Integer getResult() {
            return result;
        }

        public void setResult(Integer result) {
            this.result = result;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }
    }

    public static class Wo extends WrapBoolean {

        private static final long serialVersionUID = -4351149479944550193L;
    }
}
