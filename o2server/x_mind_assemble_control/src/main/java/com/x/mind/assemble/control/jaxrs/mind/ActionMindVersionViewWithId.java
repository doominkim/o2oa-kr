package com.x.mind.assemble.control.jaxrs.mind;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindQuery;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindVersionNotExists;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindWrapOutConvert;
import com.x.mind.entity.MindVersionInfo;

/**
 * 이전 버전 마인드맵 정보 조회
 * @author O2LEE
 *
 */
public class ActionMindVersionViewWithId extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionMindVersionViewWithId.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson ) {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		MindVersionInfo mindVersionInfo = null;
		Boolean check = true;

		if( check ){
			try {
				mindVersionInfo = mindInfoService.getMindVersionInfo(id);
				if( mindVersionInfo == null ) {
					check = false;
					Exception exception = new ExceptionMindVersionNotExists( id );
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
				wo = Wo.copier.copy(mindVersionInfo);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionMindWrapOutConvert( e, "데이터베이스 엔터티를 출력 객체로 변환하는 중 오류가 발생했습니다!" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if( check ){
			String content;
			try {
				content = mindInfoService.getMindVersionContent(id);
				if( StringUtils.isNotEmpty(content)) {
					wo.setContent(content);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionMindQuery( e, "ID로 지정한 마인드맵 이전 버전 내용을 조회하는 중 오류가 발생했습니다." );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData(wo);
		return result;
	}
	
	public static class Wo extends MindVersionInfo  {		
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<String>();
		public static WrapCopier<MindVersionInfo, Wo> copier = WrapCopierFactory.wo( MindVersionInfo.class, Wo.class, null,Wo.Excludes);
		private String content = null;
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
	}
}