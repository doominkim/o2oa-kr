package com.x.attendance.assemble.control.schedule;

import java.util.Date;

import org.quartz.JobExecutionContext;

import com.x.attendance.assemble.control.ThisApplication;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.DateTools;

/**
 * DingTalk 근태 결과 통계 타이머
 * 매일 밤 3시에 DingTalk 근태 통계 데이터 정기 처리
 * Created by fancyLou on 2020-04-03.
 * Copyright © 2020 O2. All rights reserved.
 */
public class DingdingAttendanceStatisticScheduleTask extends AbstractJob {

    private static final Logger logger = LoggerFactory.getLogger(DingdingAttendanceStatisticScheduleTask.class);

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
        try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            //전날 0시부터 23시까지
            Date from = new Date();
            from = DateTools.addDay(from, -1);//전날
//            ThisApplication.unitStatisticQueue.send(from);
        }
    }



}
