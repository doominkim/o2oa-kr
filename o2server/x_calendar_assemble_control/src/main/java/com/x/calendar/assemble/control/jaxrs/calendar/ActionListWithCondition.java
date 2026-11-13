package com.x.calendar.assemble.control.jaxrs.calendar;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.ExceptionWrapInConvert;
import com.x.calendar.assemble.control.ThisApplication;
import com.x.calendar.core.entity.Calendar;

/**
 * 조건과 권한에 따라 접근 가능한 조건에 맞는 캘린더 정보 조회
 * @author O2LEE
 *
 */
public class ActionListWithCondition extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionListWithCondition.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson,  JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wi = null;
		List<Wo> wraps = null;
		List<Calendar> calendarList = null;
		Boolean check = true;
		//Boolean manager = false;
		List<String> unitNames = null;
		List<String> groupNames = null;
		String personName = effectivePerson.getDistinguishedName();
		List<String> ids = null;
		
		if( check ){
			try {
				unitNames = userManagerService.listUnitNamesWithPerson(personName);
				groupNames = userManagerService.listGroupNamesByPerson(personName);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCalendarInfoProcess( e, "사용자의 조직 및 그룹 목록을 조회하는 중 오류가 발생했습니다." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}			
		}
		

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
//		try {
//			manager = ThisApplication.isCalendarSystemManager(effectivePerson);
//		} catch (Exception e) {
//			check = false;
//			Exception exception = new ExceptionCalendarInfoProcess(e, "시스템이 사용자의 플랫폼 관리자 여부를 확인하는 중 예외가 발생했습니다. Name:" + personName);
//			result.error(exception);
//			logger.error(e, effectivePerson, request, null);
//		}
		
		if( check ){
			try {
				ids = calendarServiceAdv.listWithCondition( wi.getName(), wi.getType(), wi.getSource(), wi.getCreateor(), null, personName, 
						unitNames, groupNames );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCalendarInfoProcess( e, "조건에 맞는 캘린더 ID 목록을 조회하는 중 오류가 발생했습니다." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
//			if( manager) { //권한 미적용
//				try {
//					ids = calendarServiceAdv.listWithCondition( wi.getName(), wi.getType(), wi.getSource(), wi.getCreateor(), null );
//				} catch (Exception e) {
//					check = false;
//					Exception exception = new ExceptionCalendarInfoProcess( e, "시스템이 조건으로 지정한 캘린더 정보 ID 목록(권한 무시)을 조회하는 중 예외가 발생했습니다." );
//					result.error( exception );
//					logger.error( e, effectivePerson, request, null);
//				}
//			}else {
//				
//			}
		}
		
		if( check ){
			if( ListTools.isNotEmpty( ids )) {
				calendarList = calendarServiceAdv.list(ids);
				try {
					wraps = Wo.copier.copy( calendarList );
					if( ListTools.isNotEmpty( wraps )) {
						for( Wo wo : wraps ) {
							wo.setManageable( ThisApplication.isCalendarManager( effectivePerson, wo ) );
							wo.setPublishable( ThisApplication.isCalendarPublisher( effectivePerson, unitNames, groupNames, wo ) );	
						}
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionCalendarInfoProcess( e, "조회한 캘린더 정보를 출력 정보로 변환하는 중 오류가 발생했습니다." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		result.setData(wraps);
		return result;
	}
	
	public static class Wi{
		
		@FieldDescribe( "캘린더 이름" )
		private String name = null;
		
		@FieldDescribe( "캘린더 유형" )
		private String type = null; 
		
		@FieldDescribe( "캘린더 출처" )
		private String source = null; 
		
		@FieldDescribe( "작성자" )
		private String createor = null;

		public String getName() {
			return name;
		}
		public String getType() {
			return type;
		}
		public String getSource() {
			return source;
		}
		public String getCreateor() {
			return createor;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setType(String type) {
			this.type = type;
		}
		public void setSource(String source) {
			this.source = source;
		}
		public void setCreateor(String createor) {
			this.createor = createor;
		}
	}

	public static class Wo extends Calendar  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		@FieldDescribe("사용자의 해당 캘린더 관리 가능 여부.")
		private Boolean manageable = false;
		
		@FieldDescribe("사용자의 해당 캘린더 일정 이벤트 게시 가능 여부.")
		private Boolean publishable = false;
		
		public static WrapCopier<Calendar, Wo> copier = WrapCopierFactory.wo( Calendar.class, Wo.class, null,Wo.Excludes);
		
		public Boolean getManageable() {
			return manageable;
		}

		public Boolean getPublishable() {
			return publishable;
		}

		public void setManageable(Boolean manageable) {
			this.manageable = manageable;
		}

		public void setPublishable(Boolean publishable) {
			this.publishable = publishable;
		}
	}
}