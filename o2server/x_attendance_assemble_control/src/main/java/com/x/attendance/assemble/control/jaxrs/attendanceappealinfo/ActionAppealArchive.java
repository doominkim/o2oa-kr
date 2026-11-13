package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionAppealArchive extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionAppealArchive.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();

		if ( StringUtils.isNotEmpty( id )) { // 지정 근태 이의 신청 기록 보관
			try {
				attendanceAppealInfoServiceAdv.archive(id);
				result.setData(new Wo(id));
			} catch (Exception e) {
				result.error(e);
				Exception exception = new ExceptionAttendanceAppealProcess(e, "근태 체크 이의 신청 정보 보관 중 오류가 발생했습니다. ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		} else { // 모든 근태 이의 신청 기록 보관
			try {
				attendanceAppealInfoServiceAdv.archiveAll();
			} catch (Exception e) {
				Exception exception = new ExceptionAttendanceAppealProcess(e, "전체 근태 이의 신청 데이터 보관 중 오류가 발생했습니다");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
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