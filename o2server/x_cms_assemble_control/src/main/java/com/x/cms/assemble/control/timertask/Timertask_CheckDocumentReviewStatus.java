package com.x.cms.assemble.control.timertask;

import org.quartz.JobExecutionContext;

import com.x.base.core.project.schedule.AbstractJob;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;

/**
 * 문서가 이미 review되었는지 확인
 *
 */
public class Timertask_CheckDocumentReviewStatus extends AbstractJob {
	
	private CmsBatchOperationPersistService cmsBatchOperationPersistService;

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		cmsBatchOperationPersistService = new CmsBatchOperationPersistService();
		cmsBatchOperationPersistService.checkDocumentReviewStatus();
		cmsBatchOperationPersistService.checkDocumentIsTop(false);
	}

}