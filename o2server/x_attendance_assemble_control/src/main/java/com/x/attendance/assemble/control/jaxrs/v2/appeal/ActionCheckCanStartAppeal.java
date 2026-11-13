package com.x.attendance.assemble.control.jaxrs.v2.appeal;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.attendance.entity.v2.AttendanceV2Config;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import java.util.Objects;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by fancyLou on 2023/4/6. Copyright © 2023 O2. All rights reserved.
 */
public class ActionCheckCanStartAppeal extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson person, String id) throws Exception {
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

            List<AttendanceV2Config> list = emc.listAll(AttendanceV2Config.class);
            AttendanceV2Config config;
            if (list != null && !list.isEmpty()) {
                config = list.get(0);
            } else {
                config = new AttendanceV2Config();
            }
            // 이의 신청 기능 스위치 조회
            if (BooleanUtils.isNotTrue(config.getAppealEnable())) {
                throw new ExceptionAppealNotEnable();
            }
            // 이의 신청 횟수 제한 조회
            if (config.getAppealMaxTimes() > 0) { // 0보다 커야 제한 있음
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(info.getRecordDate()); // 현재 이의 신청 데이터의 날짜
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                Date beginDate = calendar.getTime(); // 해당 월의 시작일 계산
                calendar.set(Calendar.DAY_OF_MONTH,
                        calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                Date endDate = calendar.getTime(); // 해당 월의 종료일 계산
                Business business = new Business(emc);
                List<AttendanceV2AppealInfo> appealInfoList = business.getAttendanceV2ManagerFactory()
                        .listAppealInfoByDateNotInit(beginDate, endDate,
                                person.getDistinguishedName());
                if (appealInfoList != null && appealInfoList.size() >= config.getAppealMaxTimes()) {
                    throw new ExceptionOverAppealMaxTimes();
                }
            }
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WrapBoolean {


        private static final long serialVersionUID = 2924560227032403309L;
    }
}
