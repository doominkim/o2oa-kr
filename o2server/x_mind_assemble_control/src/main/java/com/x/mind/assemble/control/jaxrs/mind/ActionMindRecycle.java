package com.x.mind.assemble.control.jaxrs.mind;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindDelete;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindNotExists;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindQuery;
import com.x.mind.entity.MindBaseInfo;

/**
 * 마인드맵 정보를 휴지통으로 이동
 * @author O2LEE
 *
 */
public class ActionMindRecycle extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionMindRecycle.class );

	@AuditLog(operation = "将脑图文件放入回收站")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String mindId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		MindBaseInfo mindBaseInfo = null;
		Boolean check = true;
		
		if( check ){
			try {
				mindBaseInfo = mindInfoService.getMindBaseInfo( mindId );
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindQuery( e,  "ID로 마인드맵 정보를 조회하는 중 오류가 발생했습니다.", mindId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( mindBaseInfo == null ) {
				check = false;
				Exception exception = new ExceptionMindNotExists( mindId );
				result.error(exception);
			}
		}
		if( check ){
			try {
				mindInfoService.recycle(mindId);
				wo.setId( mindId );
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindDelete( e, "{‘id’:'"+mindId+"'}" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoId {

	}
}