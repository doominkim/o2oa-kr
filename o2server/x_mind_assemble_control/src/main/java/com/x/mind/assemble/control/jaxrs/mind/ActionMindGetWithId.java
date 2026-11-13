package com.x.mind.assemble.control.jaxrs.mind;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindNotExists;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindQuery;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindWrapOutConvert;
import com.x.mind.entity.MindBaseInfo;

/**
 * 마인드맵 정보 조회(다음 페이지)
 * @author O2LEE
 *
 */
public class ActionMindGetWithId extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionMindGetWithId.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson ) {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		MindBaseInfo mindBaseInfo = null;
		Boolean check = true;

		if( check ){
			try {
				mindBaseInfo = mindInfoService.getMindBaseInfo(id);
				if( mindBaseInfo == null ) {
					check = false;
					Exception exception = new ExceptionMindNotExists( id );
					result.error(exception);
				}
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindQuery( e, "ID로 지정한 마인드맵 정보를 조회하는 중 오류가 발생했습니다." );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				wo = Wo.copier.copy(mindBaseInfo);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionMindWrapOutConvert( e, "데이터베이스 엔터티를 출력 객체로 변환하는 중 오류가 발생했습니다!" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData(wo);
		return result;
	}
	
	public static class Wo extends MindBaseInfo  {		
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<String>();
		public static WrapCopier<MindBaseInfo, Wo> copier = WrapCopierFactory.wo( MindBaseInfo.class, Wo.class, null,Wo.Excludes);
	}
}