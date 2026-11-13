package com.x.mind.assemble.control.jaxrs.mind;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindRecycleInfoNotExists;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindRecycleQuery;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindRestore;
import com.x.mind.entity.MindRecycleInfo;

/**
 * 휴지통에서 마인드맵 정보 복원
 * @author O2LEE
 *
 */
public class ActionMindRestore extends BaseAction {

	private Logger logger = LoggerFactory.getLogger( ActionMindRestore.class );

	@AuditLog(operation = "마인드맵 파일 복원")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String recycleId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		MindRecycleInfo mindRecycleInfo = null;
		Boolean check = true;
		
		if( check ){
			try {
				mindRecycleInfo = mindInfoService.getMindRecycleInfo( recycleId );
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindRecycleQuery( e,  "ID로 휴지통 마인드맵 정보를 조회하는 중 오류가 발생했습니다.",  recycleId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( mindRecycleInfo == null ) {
				check = false;
				Exception exception = new ExceptionMindRecycleInfoNotExists( recycleId );
				result.error(exception);
			}
		}
		if( check ){
			try {
				mindInfoService.restore(recycleId);
				wo.setId( recycleId );
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindRestore( e, recycleId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoId {
	}
}