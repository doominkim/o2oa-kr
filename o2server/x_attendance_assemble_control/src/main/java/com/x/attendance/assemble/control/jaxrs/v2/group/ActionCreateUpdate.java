package com.x.attendance.assemble.control.jaxrs.v2.group;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionCannotRepetitive;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by fancyLou on 2023/2/15.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionCreateUpdate extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateUpdate.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson,
                             JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getGroupName())) {
                throw new ExceptionEmptyParameter("근태 그룹 이름");
            }
            if(!business.isManager(effectivePerson)){
                 // 보조 관리자는 수정만 가능 
                 if(StringUtils.isEmpty(wi.getId())) {
                    throw new ExceptionAccessDenied(effectivePerson);
                } else {
                    AttendanceV2Group group = emc.find(wi.getId(), AttendanceV2Group.class);
                    if (group.getAssistAdminList() == null || !group.getAssistAdminList().contains(effectivePerson.getDistinguishedName())) {
                        throw new ExceptionAccessDenied(effectivePerson);
                    }
                }
            }
            // 검증
            // 이름은 중복될 수 없습니다
            List<AttendanceV2Group> checkNameGroup = emc.listEqual(AttendanceV2Group.class, AttendanceV2Group.groupName_FIELDNAME, wi.getGroupName());
            if (checkNameGroup != null && !checkNameGroup.isEmpty()) {
                for (AttendanceV2Group attendanceV2Group : checkNameGroup) {
                    if (attendanceV2Group.getGroupName().equals(wi.getGroupName()) && !attendanceV2Group.getId().equals(wi.getId())) {
                        throw new ExceptionCannotRepetitive("근태 그룹 이름");
                    }
                }
            }
            if (wi.getParticipateList() == null || wi.getParticipateList().isEmpty()) {
                throw new ExceptionEmptyParameter("근태 체크 인원·조직");
            }
            List<String> trueList = AttendanceV2Helper.calTruePersonFromMixList(emc, business, wi.getId(), wi.getParticipateList(), wi.getUnParticipateList());
            if (trueList == null || trueList.isEmpty()) {
                throw new ExceptionEmptyParameter("근태 체크 인원·조직");
            }
            wi.setTrueParticipantList(trueList);

            // 정상 저장, 추가 검증 필요
            if (!(wi.getStatus() != null && wi.getStatus() == AttendanceV2Group.status_auto)) {
                // 고정 근무제
                if (AttendanceV2Group.CHECKTYPE_Fixed.equals(wi.getCheckType())) {
                    if (wi.getWorkDateProperties() == null || !wi.getWorkDateProperties().validateNotEmpty()) {
                        throw new ExceptionEmptyParameter("근태 근무일 설정");
                    }
                } else if (AttendanceV2Group.CHECKTYPE_Arrangement.equals(wi.getCheckType())) { 
                    // 교대 편성제, 현재 추가 검증 없음
                } else if (AttendanceV2Group.CHECKTYPE_Free.equals(wi.getCheckType())) { // 자율 근무제
                    if (StringUtils.isEmpty(wi.getWorkDateList())) {
                        throw new ExceptionEmptyParameter("근태 근무일 설정");
                    }
                } else {
                    throw new ExceptionEmptyParameter("체크 유형");
                }
//                if (wi.getWorkPlaceIdList() == null || wi.getWorkPlaceIdList().isEmpty()) {
//                    throw new ExceptionEmptyParameter("근무지 목록");
//                }
            }
           
            // 신규 또는 수정
            AttendanceV2Group group = emc.find(wi.getId(), AttendanceV2Group.class);
            if (group == null) { // 신규
                group = Wi.copier.copy(wi);
            } else { // 수정
                Wi.copier.copy(wi, group);
            }
            group.setOperator(effectivePerson.getDistinguishedName());
            emc.beginTransaction(AttendanceV2Group.class);
            emc.persist(group, CheckPersistType.all);
            emc.commit();
            Wo wo = new Wo();
            wo.setId(group.getId());
            wo.setTrueParticipantList(trueList);
            result.setData(wo);
        }
        return result;
    }


    public static class Wi extends AttendanceV2Group {

        private static final long serialVersionUID = -3776223218556349868L;
        static WrapCopier<Wi, AttendanceV2Group> copier = WrapCopierFactory.wi(Wi.class, AttendanceV2Group.class, null,
                JpaObject.FieldsUnmodify);
    }

    public static class Wo extends WoId {

        private static final long serialVersionUID = 2307399114926768280L;

        @FieldDescribe("실제 근태 체크 사용자 목록.")
        private List<String> trueParticipantList; // 프론트 교대 편성용

        public List<String> getTrueParticipantList() {
            return trueParticipantList;
        }

        public void setTrueParticipantList(List<String> trueParticipantList) {
            this.trueParticipantList = trueParticipantList;
        }

        
    }
}
