package com.x.attendance.assemble.control.jaxrs.attendancestatisticrequirelog;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionDelete extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionDelete.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//먼저 작업할 앱 정보 존재 여부를 ID로 조회하고, 없으면 작업을 계속할 수 없음
			AttendanceStatisticRequireLog attendanceStatisticRequireLog = emc.find(id, AttendanceStatisticRequireLog.class);
			if ( null == attendanceStatisticRequireLog ) {
				Exception exception = new ExceptionStatisticRequireNotExists( id );
				result.error( exception );
				//logger.error( e, currentPerson, request, null);
			}else{
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.remove( attendanceStatisticRequireLog, CheckRemoveType.all );
				emc.commit();
				result.setData( new Wo( id ) );
			}			
		} catch ( Exception e ) {
			Exception exception = new ExceptionStatisticRequireProcess( e, "통계 요청 삭제 중 오류가 발생했습니다. ID:" + id );
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