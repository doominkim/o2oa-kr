package com.x.mind.assemble.control.jaxrs.mind;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindDelete;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindQuery;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindShareRecordNotExists;
import com.x.mind.entity.MindShareRecord;

/**
 * 지정 공유 기록으로 마인드맵 공유 취소
 * @author O2LEE
 *
 */
public class ActionMindShareCancel extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionMindShareCancel.class );

	@AuditLog(operation = "마인드맵 파일 공유 취소")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String shareRecordId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		MindShareRecord mindShareRecord = null;
		Boolean check = true;

		if( check ){
			try {
				mindShareRecord = mindInfoService.getMindShareRecord( shareRecordId );
				if( mindShareRecord == null ) {
					check = false;
					Exception exception = new ExceptionMindShareRecordNotExists( shareRecordId );
					result.error(exception);
				}
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindQuery( e,  "ID로 마인드맵 공유 기록을 조회하는 중 오류가 발생했습니다.", shareRecordId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				mindInfoService.shareCancel( shareRecordId );
				wo.setId( shareRecordId );
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindDelete( e, "{‘id’:'"+shareRecordId+"'}" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends WoId {

	}
}