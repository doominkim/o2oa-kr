package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveManager;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTransactionEnums.BizTypeEnum;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTypeEnums.QuotaTypeEnum;
import com.x.attendance.entity.v2.AttendanceV2LeaveAccount;
import com.x.attendance.entity.v2.AttendanceV2LeaveLedger;
import com.x.attendance.entity.v2.AttendanceV2LeaveTransaction;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

abstract class BaseAction extends StandardJaxrsAction {
    private static Logger logger = LoggerFactory.getLogger(BaseAction.class);



    /**
     * 휴가 잔여 차감
     * 
     * @param leaveType
     * @param personDN
     * @param useAmount
     * @throws Exception
     */
    protected void deductingLeaveBalance(AttendanceV2LeaveType leaveType, String personDN, Double useAmount,
            String leaveRequestId) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            emc.beginTransaction(AttendanceV2LeaveLedger.class);
            emc.beginTransaction(AttendanceV2LeaveTransaction.class);
            deductingLeaveBalance(emc, leaveType, personDN, useAmount, leaveRequestId);
            emc.commit();
            AttendanceV2LeaveManager.asyncUpdateLeaveAccount(personDN, leaveType.getId());
        }
    }

    protected void deductingLeaveBalance(EntityManagerContainer emc, AttendanceV2LeaveType leaveType, String personDN,
            Double useAmount, String leaveRequestId) throws Exception {
        if (QuotaTypeEnum.UNLIMITED.getValue().equals(leaveType.getQuotaType())) {
            logger.warn("휴가 유형 {}의 한도 유형은 {}이므로 잔여를 차감하지 않습니다.", leaveType.getName(), leaveType.getQuotaType());
            return; // 잔여 차감 안 함
        }
        List<AttendanceV2LeaveLedger> ledgers = emc.listEqualAndEqualAndEqual(AttendanceV2LeaveLedger.class,
                AttendanceV2LeaveLedger.person_FIELDNAME, personDN, AttendanceV2LeaveLedger.leaveTypeId_FIELDNAME,
                leaveType.getId(), AttendanceV2LeaveLedger.active_FIELDNAME, true);
        if (ledgers == null || ledgers.isEmpty()) {
            throw new ExceptionWithMessage("다음 사용자를 찾을 수 없습니다: " + personDN + " 의 휴가 유형 " + leaveType.getName() + " 의 휴가 잔여 기록");
        }
        ledgers.sort(new Comparator<AttendanceV2LeaveLedger>() {
            @Override
            public int compare(AttendanceV2LeaveLedger a, AttendanceV2LeaveLedger b) {
                boolean aOne = AttendanceV2LeaveManager.ONE_TIME.equals(a.getGrantPeriod());
                boolean bOne = AttendanceV2LeaveManager.ONE_TIME.equals(b.getGrantPeriod());
                // ONE_TIME은 맨 뒤
                if (aOne != bOne) {
                    return aOne ? 1 : -1;
                }
                // 일반 시간 정렬
                return grantPeriodParse(a.getGrantPeriod()).compareTo(grantPeriodParse(b.getGrantPeriod()));
            }
        });
        // AttendanceV2LeaveLedger의 remainingAmount는 잔여 한도,
        // 전달된 useAmount로 잔여를 차감하며, 지급 시간 순서대로 가장 먼저 지급된 한도부터 차감.
        // 첫 건으로 다 차감하지 못하면 두 번째 건을 계속 차감하여 다 차감하거나 잔여가 없을 때까지 진행. 차감 중 각 레코드의 remainingAmount 필드를 업데이트.
        double totalRemainingAmount = 0.0;
        for (AttendanceV2LeaveLedger ledger : ledgers) {
            double remaining = ledger.getRemainingAmount() != null ? ledger.getRemainingAmount() : 0.0;
            totalRemainingAmount += remaining;
        }
        if (totalRemainingAmount < useAmount) {
            throw new ExceptionWithMessage(
                    "사용자 " + personDN + " 의 휴가 유형 " + leaveType.getName() + " 의 휴가 잔여가 부족하여 차감할 수 없습니다: " + useAmount + " 일");
        }
        for (AttendanceV2LeaveLedger ledger : ledgers) {
            if (useAmount <= 0) {
                break; // 차감 완료
            }
            double remaining = ledger.getRemainingAmount() != null ? ledger.getRemainingAmount() : 0.0;
            if (remaining <= 0) {
                continue; // 잔여 없음, 다음 건 계속 차감
            }
            double indexUseAmount = 0.0;
            if (remaining >= useAmount) {
                indexUseAmount = useAmount;
                // 잔여가 충분해 모두 차감
                ledger.setRemainingAmount(remaining - useAmount);
                useAmount = 0.0;
            } else {
                indexUseAmount = remaining;
                // 잔여가 부족해 다 차감하지 못하면 남은 잔여를 차감하고 다음 건을 계속 차감
                ledger.setRemainingAmount(0.0);
                useAmount -= remaining;
            }
            ledger.setUsedAmount(indexUseAmount + (ledger.getUsedAmount() != null ? ledger.getUsedAmount() : 0.0));
            AttendanceV2LeaveLedger old = emc.find(ledger.getId(), AttendanceV2LeaveLedger.class);
            ledger.copyTo(old, JpaObject.FieldsUnmodify);
            emc.check(old, CheckPersistType.all);
            AttendanceV2LeaveTransaction transaction = new AttendanceV2LeaveTransaction();
            transaction.setPerson(personDN);
            transaction.setLeaveTypeId(leaveType.getId());
            transaction.setLedgerId(ledger.getId());
            if (StringUtils.isNotBlank(leaveRequestId)) {
                transaction.setLeaveRequestId(leaveRequestId);
            }
            transaction.setBizType(BizTypeEnum.USE.getValue());
            transaction.setAmount(indexUseAmount); // 차감 수량
            emc.persist(transaction, CheckPersistType.all);
        }
    }

    // 지급 주기 파싱, ONE_TIME은 특수 케이스로 한 번만 지급함을 뜻하며 맨 뒤에 오도록 매우 큰 값 부여
    private YearMonth grantPeriodParse(String str) {
        if (AttendanceV2LeaveManager.ONE_TIME.equals(str)) {
            // 매우 큰 값을 주어 맨 뒤에 오게 함(이론상 여기까지 오지 않지만 더 안전함)
            return YearMonth.of(9999, 12);
        }
        if (str.length() == 4) {
            return YearMonth.of(Integer.parseInt(str), 1);
        } else {
            return YearMonth.parse(str);
        }
    }

    // 사용자 목록 파싱, 조직 아래 사용자를 모두 조회해 userList에 넣음(재귀하지 않음)
    protected void analysisPerson(List<String> userList, String filter, Business business) throws Exception {
        if (filter.endsWith("@U")) {
            List<String> users = business.organization().person().listWithUnitSubDirect(filter);
            if (users != null && !users.isEmpty()) {
                userList.addAll(users);
            }
        } else if (filter.endsWith("@P")) {
            userList.add(filter);
        }
    }

    // 휴가 유형의 한도 유형으로 휴가 유형 목록 조회
    protected List<AttendanceV2LeaveType> getLeaveTypeList(String quotaType) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<AttendanceV2LeaveType> types = emc.listEqual(AttendanceV2LeaveType.class,
                    AttendanceV2LeaveType.active_FIELDNAME, true);
            if (StringUtils.isNotBlank(quotaType) && (QuotaTypeEnum.QUOTA.getValue().equals(quotaType)
                    || QuotaTypeEnum.UNLIMITED.getValue().equals(quotaType))) {
                types = types.stream().filter(t -> quotaType.equals(t.getQuotaType())).collect(Collectors.toList());
            }
           //AttendanceV2LeaveType의 orderNumber 필드 기준 내림차순 정렬
            types.sort(Comparator.comparing(AttendanceV2LeaveType::getOrderNumber, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
            return types;
        }
    }


   
}
