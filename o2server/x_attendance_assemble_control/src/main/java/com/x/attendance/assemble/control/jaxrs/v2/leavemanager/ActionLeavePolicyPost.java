package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeavePolicyEnums.ExpireTypeEnum;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeavePolicyEnums.GrantScopeTypeEnum;
import com.x.attendance.assemble.control.schedule.v2.model.QueueAttendanceV2LeavePolicyGrantModel;
import com.x.attendance.entity.v2.AttendanceV2LeavePolicy;
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
import com.x.base.core.project.tools.CronTools;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class ActionLeavePolicyPost extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionLeavePolicyPost.class);

    ActionResult<Wo> execute(EffectivePerson person, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(person)) {
                throw new ExceptionAccessDenied(person);
            }

            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

            if (StringUtils.isBlank(wi.getPolicyName())) {
                throw new ExceptionEmptyParameter("규칙 이름");
            }
            // 이름은 중복될 수 없습니다
            List<AttendanceV2LeavePolicy> checkRepetitive = emc.listEqualAndEqual(
                    AttendanceV2LeavePolicy.class,
                    AttendanceV2LeavePolicy.policyName_FIELDNAME, wi.getPolicyName(),
                    AttendanceV2LeavePolicy.active_FIELDNAME, true);
            if (checkRepetitive != null && !checkRepetitive.isEmpty()) {
                for (AttendanceV2LeavePolicy check : checkRepetitive) {
                    if (check.getPolicyName().equals(wi.getPolicyName()) && !check.getId()
                            .equals(wi.getId())) {
                        throw new ExceptionWithMessage("규칙 이름이 이미 있습니다");
                    }
                }
            }
            if (StringUtils.isBlank(wi.getLeaveTypeId())) {
                throw new ExceptionEmptyParameter("휴가 유형 ID");
            }
            if (StringUtils.isEmpty(wi.getGrantCron()) || !CronTools.available(wi.getGrantCron())) {
                throw new ExceptionWithMessage("타이머 표현식이 잘못되었습니다!");
            }
            if (StringUtils.isBlank(wi.getGrantScopeType())
                || (!GrantScopeTypeEnum.ALL.getValue().equals(wi.getGrantScopeType())
                    && !GrantScopeTypeEnum.DEPARTMENT.getValue().equals(wi.getGrantScopeType()))) {
                throw new ExceptionEmptyParameter("지급 범위");
            }
            if (GrantScopeTypeEnum.DEPARTMENT.getValue().equals(wi.getGrantScopeType())
                && (wi.getGrantScopeList() == null || wi.getGrantScopeList().isEmpty())) {
                throw new ExceptionEmptyParameter("지급 범위 목록");
            }
            // 전체 사용자: 같은 유형이 이미 있는지 검사,
            if (GrantScopeTypeEnum.ALL.getValue().equals(wi.getGrantScopeType())) {
                List<AttendanceV2LeavePolicy> checkTypeAndScopeAll = emc.listEqualAndEqualAndEqual(
                        AttendanceV2LeavePolicy.class,
                        AttendanceV2LeavePolicy.leaveTypeId_FIELDNAME,
                        wi.getLeaveTypeId(), AttendanceV2LeavePolicy.grantScopeType_FIELDNAME,
                        GrantScopeTypeEnum.ALL.getValue(), AttendanceV2LeavePolicy.active_FIELDNAME,
                        true);
                if (logger.isDebugEnabled()) {
                    logger.debug(
                            "checkTypeAndScopeAll size: {}, leaveTypeId: {}, grantScopeType: {} , id: {}",
                            checkTypeAndScopeAll == null ? 0 : checkTypeAndScopeAll.size(),
                            wi.getLeaveTypeId(),
                            GrantScopeTypeEnum.ALL.getValue(), wi.getId());
                }
                if (checkTypeAndScopeAll != null && !checkTypeAndScopeAll.isEmpty()) {
                    for (AttendanceV2LeavePolicy check : checkTypeAndScopeAll) {
                        if (!check.getId().equals(wi.getId())) {
                            throw new ExceptionWithMessage("현재 휴가 유형에 전체 대상 규칙이 이미 설정되어 있습니다");
                        }
                    }
                }
            } else if (GrantScopeTypeEnum.DEPARTMENT.getValue().equals(wi.getGrantScopeType())) {
                // 부서: 부서 목록에 같은 유형이 이미 있는지 검사
                List<AttendanceV2LeavePolicy> checkTypeAndScopeDepartment = emc.listEqualAndEqualAndEqual(
                        AttendanceV2LeavePolicy.class,
                        AttendanceV2LeavePolicy.leaveTypeId_FIELDNAME,
                        wi.getLeaveTypeId(), AttendanceV2LeavePolicy.grantScopeType_FIELDNAME,
                        GrantScopeTypeEnum.DEPARTMENT.getValue(),
                        AttendanceV2LeavePolicy.active_FIELDNAME, true);
                if (logger.isDebugEnabled()) {
                    logger.debug(
                            "checkTypeAndScopeDepartment size: {}, leaveTypeId: {}, grantScopeType: {} , id: {}",
                            checkTypeAndScopeDepartment == null ? 0
                                    : checkTypeAndScopeDepartment.size(),
                            wi.getLeaveTypeId(), GrantScopeTypeEnum.DEPARTMENT.getValue(),
                            wi.getId());
                }
                if (checkTypeAndScopeDepartment != null && !checkTypeAndScopeDepartment.isEmpty()) {
                    for (AttendanceV2LeavePolicy check : checkTypeAndScopeDepartment) {
                        List<String> checkGrantScopeList = check.getGrantScopeList();
                        if (!check.getId().equals(wi.getId()) && checkGrantScopeListHasSamePerson(
                                checkGrantScopeList, check.getGrantExcludeList(),
                                wi.getGrantScopeList(), wi.getGrantExcludeList(), business)) {
                            throw new ExceptionWithMessage("현재 휴가 유형의 부서 목록에 중복된 인원이 있습니다");
                        }
                    }
                }
            }
            // 스크립트 사용
            if (BooleanUtils.isTrue(wi.getGrantAmountTypeUseScript()) && StringUtils.isEmpty(wi.getGrantScript())) {
                throw new ExceptionWithMessage("한도 지급 실행 스크립트는 비워 둘 수 없습니다");
            }
            // 스크립트를 사용하지 않으면 지급 한도는 비어 있거나 0보다 작을 수 없습니다
            if (BooleanUtils.isFalse(wi.getGrantAmountTypeUseScript()) && (wi.getGrantAmount() == null || wi.getGrantAmount() < 0)) {
                throw new ExceptionEmptyParameter("지급 한도");
            }
            if (StringUtils.isBlank(wi.getExpireType()) ||  !ExpireTypeEnum.isValidateKey(wi.getExpireType()) ) {
                throw new ExceptionEmptyParameter("만료 유형");
            }
            if (wi.getExpireType().equals(ExpireTypeEnum.AFTER_GRANT.getValue())) {
                if (wi.getExpireValue() == null || wi.getExpireValue() <= 0) {
                    throw new ExceptionEmptyParameter("만료 날짜 설정");
                }
            } else {
                if (StringUtils.isBlank(wi.getExpireMonthDay())) {
                    throw new ExceptionEmptyParameter("만료 날짜 설정");
                }
                if (!isValidExpireMonthDay(wi.getExpireMonthDay())) {
                    throw new ExceptionWithMessage("만료 날짜 설정 형식이 잘못되었습니다. MM-dd 형식을 사용하십시오");
                }
            }

            if (BooleanUtils.isTrue(wi.getCarryForward())
                && (wi.getMaxCarryForward() == null || wi.getMaxCarryForward() < 0)) {
                throw new ExceptionEmptyParameter("최대 이월 한도");
            }

            AttendanceV2LeavePolicy leavePolicy = Wi.copier.copy(wi);
            if (ExpireTypeEnum.AFTER_GRANT.getValue().equals(leavePolicy.getExpireType())) {
                leavePolicy.setExpireMonthDay(null);
            } else {
                leavePolicy.setExpireValue(null);
            }
            emc.beginTransaction(AttendanceV2LeavePolicy.class);
            if (StringUtils.isBlank(wi.getId())) {
                emc.persist(leavePolicy, CheckPersistType.all);
                Wo wo = new Wo();
                wo.setId(leavePolicy.getId());
                result.setData(wo);
            } else {
                AttendanceV2LeavePolicy old = emc.find(wi.getId(), AttendanceV2LeavePolicy.class);
                if (old != null) {
                    leavePolicy.copyTo(old, JpaObject.FieldsUnmodify);
                    emc.check(old, CheckPersistType.all);
                    Wo wo = new Wo();
                    wo.setId(old.getId());
                    result.setData(wo);
                } else {
                    leavePolicy.setGrantLastExecuteTime(new Date()); // 신규 추가 시 실행 시간을 추가하여 타이머에서 실행
                    emc.persist(leavePolicy, CheckPersistType.all);
                    Wo wo = new Wo();
                    wo.setId(leavePolicy.getId());
                    result.setData(wo);
                }
            }
            emc.commit();

            if (BooleanUtils.isTrue(wi.getIsGrantImmediately())) {
                AttendanceV2LeavePolicy p = emc.find(result.getData().getId(),
                        AttendanceV2LeavePolicy.class);
                QueueAttendanceV2LeavePolicyGrantModel model = new QueueAttendanceV2LeavePolicyGrantModel();
                model.setPolicy(p);
                model.setIsImmediately(true);
                ThisApplication.queueV2LeavePolicyGrant.send(model);
            }

            return result;
        }

    }

    private static final Pattern EXPIRE_MONTH_DAY_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])-([0-2][0-9]|3[0-1])$");

    private boolean isValidExpireMonthDay(String expireMonthDay) {
        if (!EXPIRE_MONTH_DAY_PATTERN.matcher(expireMonthDay).matches()) {
            return false;
        }
        String[] monthDay = expireMonthDay.split("-");
        int month = Integer.parseInt(monthDay[0]);
        int day = Integer.parseInt(monthDay[1]);
        return YearMonth.of(2001, month).isValidDay(day);
    }

    // 사용자 목록 파싱, 조직 아래 사용자를 모두 조회한 뒤 비교
    private boolean checkGrantScopeListHasSamePerson(List<String> scopeList1,
            List<String> excludeList1, List<String> scopeList2, List<String> excludeList2,
            Business business)
            throws Exception {
        List<String> userList1 = new ArrayList<>();
        for (String s : scopeList1) {
            drillDownPerson(userList1, s, business);
        }
        List<String> userList2 = new ArrayList<>();
        for (String s2 : scopeList2) {
            drillDownPerson(userList2, s2, business);
        }
        List<String> eList1 = Optional.ofNullable(excludeList1).orElse(new ArrayList<>());
        List<String> eList2 = Optional.ofNullable(excludeList2).orElse(new ArrayList<>());
        userList1.removeAll(eList1);
        userList2.removeAll(eList2);
        // 두 사용자 목록에 중복 데이터가 있는지 비교
        if (userList1.isEmpty() || userList2.isEmpty()) {
            return false;
        }
        java.util.Set<String> set = new java.util.HashSet<>(userList1);
        java.util.Set<String> set2 = new java.util.HashSet<>(userList2);
        for (String u : set2) {
            if (set.contains(u)) {
                return true;
            }
        }
        return false;
    }

    // 사용자 목록 파싱, 조직 아래 사용자를 모두 조회해 userList에 넣음
    private void drillDownPerson(List<String> userList, String filter, Business business)
            throws Exception {
        if (filter.endsWith("@U")) { // 조직을 사용자 목록으로 변환(재귀)
            List<String> users = business.organization().person().listWithUnitSubNested(filter);
            if (users != null && !users.isEmpty()) {
                userList.addAll(users);
            }
        } else if (filter.endsWith("@P")) {
            userList.add(filter);
        }
    }

    public static class Wi extends AttendanceV2LeavePolicy {

        private static final long serialVersionUID = -2609308371335390023L;
        static WrapCopier<Wi, AttendanceV2LeavePolicy> copier = WrapCopierFactory.wi(Wi.class,
                AttendanceV2LeavePolicy.class, null,
                JpaObject.FieldsUnmodify);

        @FieldDescribe("즉시 지급 여부")
        private Boolean isGrantImmediately;

        public Boolean getIsGrantImmediately() {
            return isGrantImmediately;
        }

        public void setIsGrantImmediately(Boolean isGrantImmediately) {
            this.isGrantImmediately = isGrantImmediately;
        }


    }

    public static class Wo extends WoId {

        private static final long serialVersionUID = -6628188676802865564L;
    }
}
