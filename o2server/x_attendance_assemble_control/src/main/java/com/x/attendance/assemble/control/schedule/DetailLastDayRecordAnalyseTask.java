package com.x.attendance.assemble.control.schedule;

import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.service.AttendanceDetailServiceAdv;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;

/**
 * 매일 새벽 전날 퇴근 체크하지 않은 출퇴근 데이터 분석
 * 일부 사용자는 퇴근 체크를 자주 잊어 최종 퇴근 기록 데이터가 없음
 */
public class DetailLastDayRecordAnalyseTask extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(DetailLastDayRecordAnalyseTask.class);

	private AttendanceDetailServiceAdv attendanceDetailServiceAdv = new AttendanceDetailServiceAdv();

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		List<String> ids = null;
		DateOperation dateOperation = new DateOperation();

		//전날 날짜를 구함, 이후 전날 퇴근 체크하지 않은 모든 출퇴근 데이터를 조회해야 함
		String date = dateOperation.getDayAdd( new Date(), -1 );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ids = attendanceDetailServiceAdv.listRecordWithDateAndNoOffDuty( date  );
		} catch (Exception e) {
			logger.error(new QueryMobileDetailWithStatusException(0));
		}
		if(ListTools.isNotEmpty( ids )){
			logger.debug( date + "총 " + ids.size() + "건의 근태 출퇴근 데이터를 다시 분석해야 합니다.");
			for( String id : ids ){
				try {
//					ThisApplication.detailAnalyseQueue.send( id );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
			}
		}

		logger.info("Timertask DetailLastDayRecordAnalyseTask completed and excute success.");
	}

}