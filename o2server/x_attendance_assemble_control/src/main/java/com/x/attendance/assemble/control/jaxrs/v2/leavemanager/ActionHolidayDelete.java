package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.entity.v2.AttendanceV2Holiday;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

public class ActionHolidayDelete extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(effectivePerson)) {
                throw new ExceptionAccessDenied(effectivePerson);
            }
            ActionResult<Wo> result = new ActionResult<>();
            AttendanceV2Holiday holiday = emc.find(id, AttendanceV2Holiday.class);
            if (holiday == null) {
                throw new ExceptionNotExistObject("지정한 ID의 휴일 데이터를 찾을 수 없습니다. ID:" + id);
            }
            validateCanDelete(holiday);
            emc.beginTransaction(AttendanceV2Holiday.class);
            emc.delete(AttendanceV2Holiday.class, holiday.getId());
            emc.commit();
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
            return result;
        }
    }

    static void validateCanDelete(AttendanceV2Holiday holiday) throws Exception {
        if (!AttendanceV2Holiday.SOURCE_API.equals(holiday.getSource())) {
            throw new ExceptionWithMessage("프런트엔드 인터페이스로 추가한 휴일 데이터만 삭제할 수 있습니다");
        }
    }

    public static class Wo extends WrapBoolean {

        private static final long serialVersionUID = -6837787480413223246L;
    }
}
