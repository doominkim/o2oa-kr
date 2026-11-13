package com.x.processplatform.assemble.bam.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerCompletedTaskApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerCompletedTaskUnitStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerCompletedWorkApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerCompletedWorkUnitStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerExpiredTaskApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerExpiredTaskUnitStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerExpiredWorkApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerExpiredWorkUnitStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerStartTaskApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerStartTaskUnitStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerStartWorkApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerStartWorkUnitStubs;

public class PeriodTimer extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(PeriodTimer.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			/* 월별 발생 업무 문서에 필요한 ApplicationStub과 CompanyStub */
			ThisApplication.period.setStartTaskApplicationStubs(new TimerStartTaskApplicationStubs().execute(emc));
			ThisApplication.period.setStartTaskUnitStubs(new TimerStartTaskUnitStubs().execute(emc));
			/* 월별 완료 업무 문서에 필요한 ApplicationStub과 CompanyStub */
			ThisApplication.period
					.setCompletedTaskApplicationStubs(new TimerCompletedTaskApplicationStubs().execute(emc));
			ThisApplication.period.setCompletedTaskUnitStubs(new TimerCompletedTaskUnitStubs().execute(emc));
			/* 월별 기한 초과 업무 문서에 필요한 ApplicationStub과 CompanyStub */
			ThisApplication.period.setExpiredTaskApplicationStubs(new TimerExpiredTaskApplicationStubs().execute(emc));
			ThisApplication.period.setExpiredTaskUnitStubs(new TimerExpiredTaskUnitStubs().execute(emc));
			/* 월별 발생 업무 문서에 필요한 ApplicationStub과 CompanyStub */
			ThisApplication.period.setStartWorkApplicationStubs(new TimerStartWorkApplicationStubs().execute(emc));
			ThisApplication.period.setStartWorkUnitStubs(new TimerStartWorkUnitStubs().execute(emc));
			/* 월별 완료 업무 문서에 필요한 ApplicationStub과 CompanyStub */
			ThisApplication.period
					.setCompletedWorkApplicationStubs(new TimerCompletedWorkApplicationStubs().execute(emc));
			ThisApplication.period.setCompletedWorkUnitStubs(new TimerCompletedWorkUnitStubs().execute(emc));
			/* 월별 기한 초과 업무 문서에 필요한 ApplicationStub과 CompanyStub */
			ThisApplication.period.setExpiredWorkApplicationStubs(new TimerExpiredWorkApplicationStubs().execute(emc));
			ThisApplication.period.setExpiredWorkUnitStubs(new TimerExpiredWorkUnitStubs().execute(emc));
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

}