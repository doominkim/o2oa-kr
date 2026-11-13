package com.x.attendance.assemble.control.jaxrs.selfholiday;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

public class ActionDelete extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionDelete.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			//먼저 작업할 앱 정보 존재 여부를 ID로 조회하고, 없으면 작업을 계속할 수 없음
			AttendanceSelfHoliday attendanceSelfHoliday = emc.find(id, AttendanceSelfHoliday.class);
			if (null == attendanceSelfHoliday) {
				Exception exception = new ExceptionSelfHolidayNotExists( id );
				result.error( exception );
			}else{
				emc.beginTransaction( AttendanceSelfHoliday.class );
				emc.remove( attendanceSelfHoliday, CheckRemoveType.all );
				emc.commit();

				//캐시 삭제
				CacheManager.notify( AttendanceSelfHoliday.class );

				result.setData( new Wo(id) );

				//직원 휴가 데이터로 관련 통계 요청 기록 기록
				List<String> ids = attendanceDetailAnalyseServiceAdv.listAnalyseAttendanceDetailIds( attendanceSelfHoliday.getEmployeeName(), attendanceSelfHoliday.getStartTime(), attendanceSelfHoliday.getEndTime(), effectivePerson.getDebugger() );
//				if( ListTools.isNotEmpty( ids ) ){
//					attendanceDetailAnalyseServiceAdv.analyseAttendanceDetails( attendanceSelfHoliday.getEmployeeName(), attendanceSelfHoliday.getStartTime(), attendanceSelfHoliday.getEndTime(), effectivePerson.getDebugger()  );
//				}
				if( ListTools.isNotEmpty( ids ) ){
					for( String _id : ids ){
						try { //저장된 근태 데이터 분석
//							ThisApplication.detailAnalyseQueue.send( _id );
						} catch ( Exception e1 ) {
							e1.printStackTrace();
						}
					}
				}
			}			
		} catch ( Exception e ) {
			Exception exception = new ExceptionSelfHolidayProcess( e, "직원 휴가 기록 삭제 중 오류가 발생했습니다. ID:" + id );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		return result;
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}