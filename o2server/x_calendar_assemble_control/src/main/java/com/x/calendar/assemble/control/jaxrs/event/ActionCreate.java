package com.x.calendar.assemble.control.jaxrs.event;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.calendar.assemble.control.ExceptionWrapInConvert;
import com.x.calendar.assemble.control.ThisApplication;
import com.x.calendar.core.entity.Calendar;
import com.x.calendar.core.entity.Calendar_Event;

public class ActionCreate extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger( ActionCreate.class );

	@AuditLog(operation = "保存日程事件")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		Calendar calendar = null;
		Calendar_Event calendar_Event = null;
		List<String> unitNames = null;
		List<String> groupNames = null;
		String personName = effectivePerson.getDistinguishedName();
		Boolean check = true;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
			if( wi == null ) {
				check = false;
				Exception exception = new ExceptionEventProcess( "저장할 일정 데이터가 비어 있습니다!" );
				result.error( exception );
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( StringUtils.isEmpty( wi.getCalendarId() )) {
			check = false;
			Exception exception = new ExceptionCalendarPropertyEmpty("calendarId");
			result.error( exception );
		}
		
		if( check ){
			try {
				calendar = calendarServiceAdv.get( wi.getCalendarId() );
				if( calendar == null ) {
					check = false;
					Exception exception = new ExceptionCalendarNotExists( wi.getCalendarId() );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "ID로 캘린더 정보를 조회하는 중 오류가 발생했습니다. ID:" + wi.getCalendarId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check && !ThisApplication.isCalendarSystemManager(effectivePerson) ) {
			//사용자 권한 판단
			try {
				unitNames = userManagerService.listUnitNamesWithPerson(personName);
				groupNames = userManagerService.listGroupNamesByPerson(personName);
				if( !(ThisApplication.isCalendarManager(effectivePerson, calendar) 
						|| ThisApplication.isCalendarPublisher(effectivePerson, unitNames, groupNames, calendar )) ) {//권한 없음
					check = false;
					Exception exception = new ExceptionInsufficientPermissions( "이 캘린더에 일정을 만들 권한이 없습니다. 캘린더:" + calendar.getName() );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "사용자의 조직 및 그룹 목록을 조회하는 중 오류가 발생했습니다." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				calendar_Event = Wi.copier.copy( wi );
				if( StringUtils.isNotEmpty( wi.getId())){
					calendar_Event.setId( wi.getId() );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "사용자가 입력한 데이터를 일정 정보 객체로 변환하는 중 오류가 발생했습니다." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ) {
			PromptException exception = this.eventValidate( calendar_Event, calendar );
			if( exception != null ) {
				check = false;
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				calendar_Event = calendar_EventServiceAdv.create( calendar_Event, effectivePerson );
				result.setData( new Wo( calendar_Event.getId() ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "일정 정보를 저장하는 중 오류가 발생했습니다." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends Calendar_Event{
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);
		
		public static WrapCopier< Wi, Calendar_Event > copier =  WrapCopierFactory.wi( Wi.class, Calendar_Event.class, null, null );	
		
		private String identity = null;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}