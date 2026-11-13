package com.x.attendance.assemble.control.jaxrs.selfholiday;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionDeleteByWfDocId extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionDeleteByWfDocId.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String wfDocId ) throws Exception {
		logger.debug( effectivePerson, ">>>>>>>>>>method delete has been called, try to delete attendanceSelfHoliday{'wfDocId':'" + wfDocId + "'}......");
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		
		logger.debug( effectivePerson, ">>>>>>>>>>user " + currentPerson.getDistinguishedName() + "try to delete AttendanceSelfHoliday......");
		List<String> ids = null;
		List<AttendanceSelfHoliday> attendanceSelfHolidays = null;
		Business business = null;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			// 먼저 작업할 앱 정보 존재 여부를 ID로 조회하고, 없으면 작업을 계속할 수 없음
			ids = business.getAttendanceSelfHolidayFactory().listIdsWithBatchFlag(wfDocId);
			if ( ListTools.isNotEmpty( ids ) ) {
				attendanceSelfHolidays = business.getAttendanceSelfHolidayFactory().list(ids);
				if (attendanceSelfHolidays != null && attendanceSelfHolidays.size() > 0) {
					for (AttendanceSelfHoliday attendanceSelfHoliday : attendanceSelfHolidays) {
						logger.debug( effectivePerson, ">>>>>>>>>>System trying to beginTransaction to delete attendanceSelfHoliday......");
						emc.beginTransaction(AttendanceSelfHoliday.class);
						emc.remove(attendanceSelfHoliday, CheckRemoveType.all);
						emc.commit();

						//캐시 삭제
						CacheManager.notify( AttendanceSelfHoliday.class );

						logger.debug( effectivePerson, ">>>>>>>>>>System delete attendanceSelfHoliday success......");

						// 해당 사용자의 휴가 기간 내 기존 출퇴근 데이터만 다시 분석하면 됨
						ids = attendanceDetailAnalyseServiceAdv.listAnalyseAttendanceDetailIds(
								attendanceSelfHoliday.getEmployeeName(), attendanceSelfHoliday.getStartTime(),
								attendanceSelfHoliday.getEndTime(), effectivePerson.getDebugger() );
//						if ( ListTools.isNotEmpty( ids ) ) {
//							attendanceDetailAnalyseServiceAdv.analyseAttendanceDetails( attendanceSelfHoliday.getEmployeeName(), attendanceSelfHoliday.getStartTime(), attendanceSelfHoliday.getEndTime(), effectivePerson.getDebugger() );
//						}
						if( ListTools.isNotEmpty( ids ) ){
							for( String id : ids ){
								try { //저장된 근태 데이터 분석
//									ThisApplication.detailAnalyseQueue.send( id );
								} catch ( Exception e1 ) {
									e1.printStackTrace();
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Exception exception = new ExceptionSelfHolidayProcess( e, "결재 ID로 동기화된 직원 휴가 기록을 삭제하는 중 오류가 발생했습니다. DocId:" + wfDocId );
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}
		result.setData( new Wo( wfDocId ) );
		return result;
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}