package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionDelete extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionDelete.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AttendanceScheduleSetting attendanceScheduleSetting = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionScheduleIdEmpty();
				result.error( exception );
			}
		}
		if( check ){
			try {
				attendanceScheduleSetting = attendanceScheduleSettingServiceAdv.get( id );
				if( attendanceScheduleSetting == null ){
					check = false;
					Exception exception = new ExceptionScheduleNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceScheduleProcess( e, "ID로 조직 근무 편성 정보를 조회하는 중 오류가 발생했습니다. ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceScheduleSettingServiceAdv.delete(id);
				result.setData( new Wo( id ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceScheduleProcess( e, "ID로 조직 근무 편성 정보를 삭제하는 중 오류가 발생했습니다. ID:" + id );
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