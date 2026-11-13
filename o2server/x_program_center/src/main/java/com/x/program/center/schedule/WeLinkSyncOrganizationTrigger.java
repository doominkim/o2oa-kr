package com.x.program.center.schedule;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.ThisApplication;

public class WeLinkSyncOrganizationTrigger implements Job {

	private static Logger logger = LoggerFactory.getLogger(WeLinkSyncOrganizationTrigger.class);

	/* 목록에 동기화 신호 전송 */
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		ThisApplication.weLinkSyncOrganizationCallbackRequest.add(new Object());
	}

}
