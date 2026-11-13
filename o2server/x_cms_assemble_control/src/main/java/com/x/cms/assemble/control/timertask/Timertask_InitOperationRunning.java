package com.x.cms.assemble.control.timertask;

import org.quartz.JobExecutionContext;

import com.x.base.core.project.schedule.AbstractJob;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;

/**
 * 실행되지 않은 배치 처리 작업이 있는지 확인
 *
 */
public class Timertask_InitOperationRunning extends AbstractJob {
	
	private CmsBatchOperationPersistService cmsBatchOperationPersistService;

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		cmsBatchOperationPersistService = new CmsBatchOperationPersistService();
		cmsBatchOperationPersistService.initOperationRunning();
	}

}