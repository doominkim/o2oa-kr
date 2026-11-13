package com.x.cms.assemble.control.timertask;

import org.quartz.JobExecutionContext;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.cms.assemble.control.service.DocumentViewRecordServiceAdv;
import com.x.cms.assemble.control.service.LogService;

/**
 * 예약 에이전트: 작업 로그와 문서 접근 로그를 주기적으로 점검 사용자 작업 로그는 3년 또는 최대 100만 건 보존 문서 접근 로그는 3년 또는 최대 100만 건 보존
 * 
 * @author O2LEE
 *
 */
public class Timertask_LogRecordCheckTask extends AbstractJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(Timertask_LogRecordCheckTask.class);
	private LogService logService = new LogService();
	private DocumentViewRecordServiceAdv documentViewRecordServiceAdv = new DocumentViewRecordServiceAdv();
	private Integer stay_yearnum_operationLog = 1; // 1년 보존
	private Integer stay_yeanumr_viewRecord = 1; // 1년 보존
	private Integer stay_count_operationLog = 100000; // 100,000건 보존
	private Integer stay_count_viewRecord = 100000; // 100,000건 보존

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			logService.clean(stay_yearnum_operationLog, stay_count_operationLog);
			LOGGER.info("Timertask_LogRecordCheckTask -> clean operation logs excute success. stay_yearnum:"
					+ stay_yearnum_operationLog + ",stay_count:" + stay_count_operationLog + ".");
		} catch (Exception e) {
			LOGGER.error(e);
		}
		try {
			documentViewRecordServiceAdv.clean(stay_yeanumr_viewRecord, stay_count_viewRecord);
			LOGGER.info("Timertask_LogRecordCheckTask -> clean view records excute success. stay_yeanumr:"
					+ stay_yeanumr_viewRecord + ",stay_count:" + stay_count_viewRecord + ".");
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

}