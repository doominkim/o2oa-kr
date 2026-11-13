package com.x.attendance.assemble.control.jaxrs.v2.appeal;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.attendance.entity.v2.AttendanceV2Config;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.DateTools;
import java.util.Objects;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by fancyLou on 2023/3/3.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionUpdateForStart extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson person, String id, JsonElement jsonElement) throws Exception {

        if (StringUtils.isEmpty(id)) {
            throw new ExceptionEmptyParameter("id");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            AttendanceV2AppealInfo info = emc.find(id, AttendanceV2AppealInfo.class);
            if (info == null) {
                throw new ExceptionNotExistObject("데이터가 없습니다, " + id);
            }
            if (!person.getDistinguishedName().equals(info.getUserId())) {
                throw new ExceptionPersonNotEqual();
            }
            if (Objects.equals(info.getStatus(), AttendanceV2AppealInfo.status_TYPE_LOCK)) {
                throw new ExceptionAppealLocked();
            }
            // 이의 신청 횟수 제한 조회
            List<AttendanceV2Config> list = emc.listAll(AttendanceV2Config.class);
            AttendanceV2Config config;
            if (list != null && !list.isEmpty()) {
                config = list.get(0);
            } else {
                config = new AttendanceV2Config();
            }
            if (BooleanUtils.isNotTrue(config.getAppealEnable())) {
                throw new ExceptionAppealNotEnable();
            }
            if (config.getAppealMaxTimes() > 0) { // 0보다 커야 제한 있음
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(info.getRecordDate()); // 현재 이의 신청 데이터의 날짜
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                Date beginDate = calendar.getTime(); // 해당 월의 시작일 계산
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                Date endDate = calendar.getTime(); // 해당 월의 종료일 계산
                Business business = new Business(emc);
                List<AttendanceV2AppealInfo> appealInfoList = business.getAttendanceV2ManagerFactory().listAppealInfoByDateNotInit(beginDate, endDate, person.getDistinguishedName());
                if (appealInfoList != null && appealInfoList.size() >= config.getAppealMaxTimes()) {
                    throw new ExceptionOverAppealMaxTimes();
                }
            }
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            emc.beginTransaction(AttendanceV2AppealInfo.class);
            info.setJobId(wi.getJob()); // job 설정, 프론트에서 job 기준으로 프로세스 열기 버튼 표시
            info.setStatus(AttendanceV2AppealInfo.status_TYPE_PROCESSING); // 결재 중
            info.setStartTime(DateTools.now());
            emc.check(info, CheckPersistType.all);
            emc.commit();
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
            return result;
        }
    }

    public static class Wi extends GsonPropertyObject {
        @FieldDescribe("프로세스가 반환한 job")
        private String job;

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }
    }

    public static class Wo extends WrapBoolean {

        private static final long serialVersionUID = -4351149479944550193L;
    }
}
