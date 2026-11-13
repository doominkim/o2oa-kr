package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceSetting;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionGetWithCode extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionGetWithCode.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String code ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		AttendanceSetting attendanceSetting = null;
		Boolean check = true;
		
		if( check ){
			if( code == null || code.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSettingCodeEmpty();
				result.error( exception );
			}
		}
		if( check ){
			try {
				attendanceSetting = attendanceSettingServiceAdv.getByCode( code );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceSettingProcess( e, "지정한 코드 'CODE'로 근태 시스템 설정 목록을 조회하는 중 오류가 발생했습니다. Code:" + code );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				wrap = Wo.copier.copy( attendanceSetting );
				result.setData( wrap );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceSettingProcess( e, "조회된 근태 설정 객체를 출력용 정보로 변환하는 중 오류가 발생했습니다." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends AttendanceSetting  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		
		public static WrapCopier<AttendanceSetting, Wo> copier = WrapCopierFactory.wo( AttendanceSetting.class, Wo.class, null,JpaObject.FieldsInvisible);
	}

}