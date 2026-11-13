package com.x.cms.assemble.control.timertask;

import org.quartz.JobExecutionContext;

import com.x.base.core.project.schedule.AbstractJob;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;
import com.x.cms.assemble.control.service.DocumentPersistService;

/**
 * 모든 문서의 권한 정보 재계산
 *
 */
public class Timertask_RefreshAllDocumentReviews extends AbstractJob {
	
	private CmsBatchOperationPersistService cmsBatchOperationPersistService;

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		DocumentPersistService documentPersistService = new DocumentPersistService();
		documentPersistService.refreshAllDocumentPermission(false);
	}

}