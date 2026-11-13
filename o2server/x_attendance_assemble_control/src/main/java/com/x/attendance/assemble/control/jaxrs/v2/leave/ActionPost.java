package com.x.attendance.assemble.control.jaxrs.v2.leave;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2LeaveData;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.organization.Person;

/**
 * Created by fancyLou on 2023/3/29.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionPost extends BaseAction {

    ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getPerson())) {
                throw new ExceptionEmptyParameter("인원 식별자");
            }
            if (StringUtils.isEmpty(wi.getLeaveType())) {
                throw new ExceptionEmptyParameter("외근 유형");
            }
            if (null == wi.getStartTime()) {
                throw new ExceptionEmptyParameter("시작 시간");
            }
            if (null == wi.getEndTime()) {
                throw new ExceptionEmptyParameter("종료 시간");
            }
            // 사용자 조회 후 DN으로 변환
            Business business = new Business(emc);
            Person person = business.organization().person().getObject(wi.getPerson(), true);
            if (person == null) {
                throw new ExceptionNotExistObject("인원 " + wi.getPerson());
            }
            AttendanceV2LeaveData leaveData = Wi.copier.copy(wi);
            if (leaveData.getLeaveDayNumber() <= 0.0) { // 전달된 데이터가 있으면 계산하지 않고 전달된 값 사용.
                // 날짜 간격 계산
                long interval = wi.getEndTime().getTime() - wi.getStartTime().getTime();
                if (interval < 0) {
                    interval = -interval;
                }
                double days = interval / (1000.0 * 3600 * 24);
                // 소수점 1자리 유지
                BigDecimal b = new BigDecimal(days);
                days = b.setScale(1, RoundingMode.HALF_UP).doubleValue();
                leaveData.setLeaveDayNumber(days);
            }
            leaveData.setPerson(person.getDistinguishedName());
            emc.beginTransaction(AttendanceV2LeaveData.class);
            emc.persist(leaveData, CheckPersistType.all);
            emc.commit();
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
            return result;
        }
    }

    public static class Wi extends AttendanceV2LeaveData {

        static WrapCopier<Wi, AttendanceV2LeaveData> copier = WrapCopierFactory.wi(Wi.class, AttendanceV2LeaveData.class, null,
                JpaObject.FieldsUnmodify);
    }

    public static class Wo extends WrapBoolean {

    }
}
