package com.x.attendance.assemble.control.jaxrs.v2.mobile;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.jaxrs.v2.WoGroupShift;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.DateTools;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 외부 출처 체크 기록, 예: 출입 통제 시스템은 한 사람이 하루에 여러 건 들어올 수 있어 어느 근태 기록을 업데이트할지 판단 필요
 */
public class ActionCheckInRecordFromOut extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionCheckInRecordFromOut.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
            throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(effectivePerson)) {
                throw new ExceptionAccessDenied(effectivePerson);
            }
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getPerson())) {
                throw new ExceptionEmptyParameter("사용자 식별자");
            }
            if (wi.getCheckInTime() == null) {
                throw new ExceptionEmptyParameter("체크 시간");
            }
            Person p = business.organization().person().getObject(wi.getPerson());
            if (p == null || StringUtils.isEmpty(p.getDistinguishedName())) {
                throw new ExceptionNotExistObject("인원: " + wi.getPerson());
            }
            Date checkInDate = null;
            try {
                checkInDate = new Date(wi.getCheckInTime()*1000);
            } catch (Exception ignore) {
            }
            if (checkInDate == null) {
                throw new ExceptionWithMessage("날짜 형식이 올바르지 않습니다!");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("체크 날짜: " + checkInDate);
            }
            if (!isToday(checkInDate)) {
                throw new ExceptionWithMessage("체크 날짜"+DateTools.format(checkInDate)+"오늘이 아닙니다!");
            }
            WoGroupShift woGroupShift = business.getAttendanceV2ManagerFactory()
                    .getGroupShiftByPersonDate(p.getDistinguishedName(),
                            DateTools.format(checkInDate, DateTools.format_yyyyMMdd));
            if (woGroupShift == null || woGroupShift.getGroup() == null) {
                throw new ExceptionNotExistObject("근태 그룹 정보");
            }

            // 같은 사용자·같은 날의 사전 체크 생성은 직렬화하고 다른 사용자는 병렬 처리 가능.
            String checkInDateString = DateTools.format(checkInDate, DateTools.format_yyyyMMdd);
            List<AttendanceV2CheckInRecord> recordList = executeWithCheckLock(
                    "pre:" + p.getDistinguishedName() + ":" + checkInDateString,
                    new CallableImpl(p.getDistinguishedName(), woGroupShift.getGroup(),
                            woGroupShift.getShift(), checkInDate));
            if (recordList == null || recordList.isEmpty()) {
                throw new ExceptionNotExistObject("체크 기록");
            }
            AttendanceV2CheckInRecord record = recordList.stream()
                    .filter(r -> AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn.equals(
                            r.getCheckInResult())).findFirst().orElse(null);
            if (record == null) {
                record = recordList.get(recordList.size() - 1);
            }

            Wo wo = new Wo();
            ActionResult<Wo> result = new ActionResult<>();
            String detailPerson = null;
            String detailDate = null;
            if (record != null) {
//                checkIn(emc, business, checkInDate, rInstance, null, null, wi);
                AttendanceV2CheckInRecord back = executeWithCheckLock("check:" + record.getId(),
                        new CheckInCallableImpl(checkInDate, record.getId(), CheckInWi.fromOutside(wi)));
                if (BooleanUtils.isTrue(wi.getGenerateErrorInfo())) {
                    // 이상 데이터
                    generateAppealInfo(back, woGroupShift.getGroup().getFieldWorkMarkError(), emc,
                            business);
                }
                if (back != null) {
                    detailPerson = back.getUserId();
                    detailDate = back.getRecordDateString();
                }
                wo.setValue(true);
            } else {
                LOGGER.warn("체크 기록을 찾을 수 없습니다!");
                wo.setValue(false);
            }
            result.setData(wo);
            checkAndSendV2DetailIfAllCheckInCompletedAsync(detailPerson, detailDate);
            return result;
        }
    }

    private boolean isToday(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        return localDate.equals(today);
    }

    public static class Wo extends WrapBoolean {

        private static final long serialVersionUID = -9108259407820975624L;
    }

    public static class Wi extends GsonPropertyObject {


        private static final long serialVersionUID = 3470655254449767419L;
        @FieldDescribe("사용자 고유 식별자")
        private String person;
        @FieldDescribe("체크 시간(Unix 타임스탬프)")
        private Long checkInTime;
        @FieldDescribe("출처, 예: 출입 통제 시스템")
        private String source;
        @FieldDescribe("이상 데이터 생성 여부")
        private Boolean generateErrorInfo;


        public Boolean getGenerateErrorInfo() {
            return generateErrorInfo;
        }

        public void setGenerateErrorInfo(Boolean generateErrorInfo) {
            this.generateErrorInfo = generateErrorInfo;
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
    }
}
