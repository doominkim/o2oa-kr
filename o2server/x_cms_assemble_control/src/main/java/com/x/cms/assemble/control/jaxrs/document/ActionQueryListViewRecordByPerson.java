package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.DocumentViewRecord;

public class ActionQueryListViewRecordByPerson extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionQueryListViewRecordByPerson.class );

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String name ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<String> ids = null;
		List<DocumentViewRecord> documentViewRecordList = null;
		Boolean check = true;

		if( check ){
			if( StringUtils.isEmpty(name) ){
				check = false;
				Exception exception = new ExceptionPersonNameEmpty();
				result.error( exception );
			}
		}

		if( check ){
			try {
				ids = documentViewRecordServiceAdv.listByPerson( name, 100 );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionServiceLogic( e,"사용자 이름으로 게시글 접근 기록 목록을 조회하는 중 오류가 발생했습니다. Name: " + name );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					documentViewRecordList = documentViewRecordServiceAdv.list(ids);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionServiceLogic( e,"접근 기록 ID 목록으로 접근 기록 목록을 조회하는 중 오류가 발생했습니다." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}

		if( check ){
			if( documentViewRecordList != null && !documentViewRecordList.isEmpty() ){
				try {
					wraps = Wo.copier.copy( documentViewRecordList );
					result.setCount( Long.parseLong( wraps.size() + "" ) );
					result.setData( wraps );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionServiceLogic( e,"조회 결과를 출력용 데이터로 변환하는 중 오류가 발생했습니다." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo extends DocumentViewRecord{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<String>();

		public static final WrapCopier<DocumentViewRecord, Wo> copier = WrapCopierFactory.wo( DocumentViewRecord.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}
