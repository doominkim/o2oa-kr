package com.x.attendance.assemble.control.jaxrs.v2.appeal;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.detail.ExceptionDateEndBeforeStartError;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapLong;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.DateTools;

/**
 * 근태 정보 확인, 지정 날짜 범위 내 대기 중·결재 중인 이의 신청 데이터 잠금.
 */
public class ActionConfirm extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getStartDate())) {
                throw new ExceptionEmptyParameter("startDate");
            }
            if (StringUtils.isEmpty(wi.getEndDate())) {
                throw new ExceptionEmptyParameter("endDate");
            }
            Date startDate = DateTools.parse(wi.getStartDate(), DateTools.format_yyyyMMdd);
            Date endDate = DateTools.parse(wi.getEndDate(), DateTools.format_yyyyMMdd);
            if (startDate.after(endDate)) {
                throw new ExceptionDateEndBeforeStartError();
            }

            String person = effectivePerson.getDistinguishedName();
            if (business.isManager(effectivePerson) && StringUtils.isNotEmpty(wi.getPerson())) {
                Person target = business.organization().person().getObject(wi.getPerson(), true);
                if (target == null) {
                    throw new ExceptionNotExistObject("인원 " + wi.getPerson());
                }
                person = target.getDistinguishedName();
            }

            List<AttendanceV2AppealInfo> list = business.getAttendanceV2ManagerFactory()
                    .listAppealInfoByPersonDateAndStatus(person, wi.getStartDate(), wi.getEndDate(),
                            AttendanceV2AppealInfo.status_TYPE_INIT, AttendanceV2AppealInfo.status_TYPE_PROCESSING);
            if (list != null && !list.isEmpty()) {
                emc.beginTransaction(AttendanceV2AppealInfo.class);
                for (AttendanceV2AppealInfo info : list) {
                    info.setStatus(AttendanceV2AppealInfo.status_TYPE_LOCK);
                    emc.check(info, CheckPersistType.all);
                }
                emc.commit();
            }

            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo((long) (list == null ? 0 : list.size()));
            result.setData(wo);
            result.setCount(wo.getValue());
            return result;
        }
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 1327123598236996393L;

        @FieldDescribe("시작일, 형식: yyyy-MM-dd")
        private String startDate;

        @FieldDescribe("종료일, 형식: yyyy-MM-dd")
        private String endDate;

        @FieldDescribe("사용자 식별자, 관리자가 전달 가능")
        private String person;

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }
    }

    public static class Wo extends WrapLong {

        private static final long serialVersionUID = 2025532921636978511L;

        public Wo(Long count) {
            super(count);
        }
    }
}
