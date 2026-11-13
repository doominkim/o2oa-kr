package com.x.attendance.assemble.control.schedule;

import org.quartz.JobExecutionContext;

import com.x.attendance.assemble.control.service.AttendanceStatisticServiceAdv;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;

/**
 * 정기 통계 에이전트, 모든 통계 요청을 정기적으로 통계 처리
 * 
 * @author LIYI
 *
 */
public class AttendanceStatisticTask extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(AttendanceStatisticTask.class);
 
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		AttendanceStatisticServiceAdv attendanceStatisticServiceAdv = new AttendanceStatisticServiceAdv();
		attendanceStatisticServiceAdv.doStatistic(false);
		logger.info("Timertask completed and excute success.");
	}

}