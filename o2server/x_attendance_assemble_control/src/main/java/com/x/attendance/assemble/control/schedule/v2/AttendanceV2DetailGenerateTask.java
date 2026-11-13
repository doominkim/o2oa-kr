package com.x.attendance.assemble.control.schedule.v2;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.DateTools;
import org.quartz.JobExecutionContext;

import java.util.Date;
import java.util.List;

/**
 * 신규 근태 정기 작업
 * 출퇴근 기록 AttendanceV2CheckInRecord로 해당 근태 상세 데이터 AttendanceV2Detail 생성
 * Created by fancyLou on 2023/2/24.
 * Copyright © 2023 O2. All rights reserved.
 */
public class AttendanceV2DetailGenerateTask  extends AbstractJob {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceV2DetailGenerateTask.class);


    // 정기 작업 1단계: 모든 근태 그룹을 처리하여 사용자·조직을 모두 사용자 DN으로 변환. 새로 가입한 구성원이 근태 그룹 범위에 자동 추가되도록 하기 위한 단계
    // 2단계: 근태 그룹 안의 근태 대상 사용자를 하나씩 처리하여, 출퇴근 기록 AttendanceV2CheckInRecord로 해당 근태 상세 데이터 AttendanceV2Detail 생성
    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
         logger.info("======================신규 근태 정기 작업 시작==============================");
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            List<AttendanceV2Group> list = emc.listAll(AttendanceV2Group.class);
            if (list != null && !list.isEmpty()) {
                for (AttendanceV2Group group : list) {
                    if (group.getStatus() != null && group.getStatus() == AttendanceV2Group.status_auto) {
                        continue;  // 자동 저장 데이터 건너뜀
                    }
                    try {
                        // 부서 사용자 재검색 계산 처리
                        List<String> trueList = AttendanceV2Helper.calTruePersonFromMixList(emc, business, group.getId(), group.getParticipateList(), group.getUnParticipateList());
                        if (trueList == null || trueList.isEmpty()) {
                            throw new ExceptionEmptyParameter("근태 체크 인원·조직");
                        }
                        group.setTrueParticipantList(trueList);
                        emc.beginTransaction(AttendanceV2Group.class);
                        emc.persist(group, CheckPersistType.all);
                        emc.commit();

                        // 2단계 처리 시작. 이 정기 작업은 새벽 3시에 실행되므로 여기서는 전날 데이터를 처리
                        Date yesterday = DateTools.addDay(new Date(), -1);
                        String yesterdayString = DateTools.format(yesterday, DateTools.format_yyyyMMdd);
                        logger.info("근태 그룹【{}】 처리 시작, 근태 대상 사용자 수: {}", group.getGroupName(), ""+trueList.size());
                        for (String person : trueList) {
                            ThisApplication.queueV2Detail.send(new QueueAttendanceV2DetailModel(person, yesterdayString));
                        }
                        // 교대 근무제이면 날짜를 넘길 수 있으므로 하루치 데이터를 더 처리
                        if (group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Arrangement)) {
                            Date beforeYesterday = DateTools.addDay(yesterday, -1);
                            String beforeYesterdayString = DateTools.format(beforeYesterday, DateTools.format_yyyyMMdd);
                            logger.info("그저께 날짜: {} ", beforeYesterdayString);
                            for (String person : trueList) {
                                ThisApplication.queueV2Detail.send(new QueueAttendanceV2DetailModel(person, beforeYesterdayString));
                            }
                        }

                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
            } else {
                logger.info("근태 그룹 목록이 비어 있어 처리할 필요가 없습니다!!!!!!!");
            }
        }
    }

}
