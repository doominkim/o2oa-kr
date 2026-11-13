package com.x.attendance.assemble.control.schedule.v2;

import java.util.List;

import org.quartz.JobExecutionContext;

import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.schedule.v2.model.QueueAttendanceV2LeavePolicyGrantModel;
import com.x.attendance.entity.v2.AttendanceV2LeavePolicy;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;

public class AttendanceV2LeavePolicyGrantTask extends AbstractJob {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceV2LeavePolicyGrantTask.class);

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
        long start = System.currentTimeMillis();
        int policyCount = 0;
        int enqueueCount = 0;
        logger.info("======================신규 근태 휴가 관리 정책 지급 타이머 실행 시작==============================");
        try {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                logger.info("사용 중인 휴가 지급 정책 조회 시작.");
                List<AttendanceV2LeavePolicy> policyList = emc.listEqual(AttendanceV2LeavePolicy.class, AttendanceV2LeavePolicy.active_FIELDNAME, true);
                policyCount = policyList == null ? 0 : policyList.size();
                logger.info("사용 중인 휴가 지급 정책 조회 완료, 수: {}.", policyCount);
                if (policyList == null || policyList.isEmpty()) {
                    return;
                }
                for (AttendanceV2LeavePolicy policy : policyList) {
                    try {
                        logger.info("휴가 지급 정책 큐 등록 준비, policyId: {}, policyName: {}.", policy.getId(), policy.getPolicyName());
                        QueueAttendanceV2LeavePolicyGrantModel model = new QueueAttendanceV2LeavePolicyGrantModel();
                        model.setPolicy(policy);
                        model.setIsImmediately(false);
                        ThisApplication.queueV2LeavePolicyGrant.send(model);
                        enqueueCount++;
                        logger.info("휴가 지급 정책 큐 등록 완료, policyId: {}, policyName: {}.", policy.getId(), policy.getPolicyName());
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            logger.info("======================신규 근태 휴가 관리 정책 지급 타이머 실행 완료, 정책 수: {}, 큐 등록 수: {}, 소요 시간: {}ms==============================",
                    policyCount, enqueueCount, System.currentTimeMillis() - start);
        }
    }
    
}
