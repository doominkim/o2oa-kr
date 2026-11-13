package com.x.attendance.assemble.control.jaxrs.attendanceemployeeconfig;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionDelete extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionDelete.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AttendanceEmployeeConfig attendanceEmployeeConfig = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionConfigIdEmpty();
				result.error( exception );
			}
		}		
		if( check ){
			try {
				attendanceEmployeeConfig = attendanceEmployeeConfigServiceAdv.get( id );
				if( attendanceEmployeeConfig == null ){
					check = false;
					Exception exception = new ExceptionConfigNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceEmployeeProcess( e, "ID로 인원 근태 설정을 조회하는 중 오류가 발생했습니다. ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceEmployeeConfigServiceAdv.delete( id );
				result.setData( new Wo( id ) );
				logger.info( "사용자 근태 설정 데이터를 저장했습니다!" );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceEmployeeProcess( e, "ID로 인원 근태 설정을 삭제하는 중 오류가 발생했습니다. ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
			
		}
		return result;
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}