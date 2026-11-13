package com.x.program.center.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.ThisApplication;

public class QiyeweixinSyncOrganizationTrigger extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(QiyeweixinSyncOrganizationTrigger.class);

	/* 목록에 동기화 신호 전송 */
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			if (pirmaryCenter()) {
				ThisApplication.qiyeweixinSyncOrganizationCallbackRequest.add(new Object());
			}
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

}
