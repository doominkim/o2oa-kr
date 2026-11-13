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

/**
 * 이벤트 ID로 단일 일정 이벤트 정보 수정
 * 
 * @author O2LEE
 *
 */
public class ActionUpdateSingleEventWithId extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger( ActionUpdateSingleEventWithId.class );

	@AuditLog(operation = "일정 수정")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		Calendar calendar = null;
		Calendar_Event calendar_Event = null;
		Calendar_Event new_Event = null;
		List<String> unitNames = null;
		List<String> groupNames = null;
		String personName = effectivePerson.getDistinguishedName();
		Boolean check = true;
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			if( StringUtils.isEmpty( id ) ){
				check = false;
				Exception exception = new ExceptionEventPropertyEmpty("ID");
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				calendar_Event = calendar_EventServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "ID로 캘린더 기록 정보를 조회하는 중 오류가 발생했습니다. ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( calendar_Event == null ) {
				check = false;
				Exception exception = new ExceptionEventNotExists( id );
				result.error( exception );
			}
		}
		
		if( check ){
			if( StringUtils.isNotEmpty( calendar_Event.getCalendarId() )) {
				try {
					calendar = calendarServiceAdv.get( calendar_Event.getCalendarId() );
					if( calendar == null ) {
						check = false;
						Exception exception = new ExceptionCalendarNotExists( calendar_Event.getCalendarId() );
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionEventProcess( e, "ID로 캘린더 정보를 조회하는 중 오류가 발생했습니다. ID:" + calendar_Event.getCalendarId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		if( check && !ThisApplication.isCalendarSystemManager(effectivePerson) ) {
			//사용자 권한 판단
			try {
				unitNames = userManagerService.listUnitNamesWithPerson(personName);
				groupNames = userManagerService.listGroupNamesByPerson(personName);
				if( calendar != null ) {
					if( !(ThisApplication.isCalendarManager(effectivePerson, calendar) 
							|| ThisApplication.isCalendarPublisher(effectivePerson, unitNames, groupNames, calendar )) ) {//권한 없음
						check = false;
						Exception exception = new ExceptionInsufficientPermissions( "이 캘린더에서 일정을 삭제할 권한이 없습니다. 캘린더:" + calendar.getName() );
						result.error( exception );
					}
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
				new_Event = Wi.copier.copy( wi );
				new_Event.setId( calendar_Event.getId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "사용자가 입력한 데이터를 일정 정보 객체로 변환하는 중 오류가 발생했습니다." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ) {
			PromptException exception = this.eventValidate( new_Event, calendar );
			if( exception != null ) {
				check = false;
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				calendar_Event = calendar_EventServiceAdv.updateSingleEvent( new_Event, effectivePerson );
				result.setData( new Wo( calendar_Event.getId() ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "단일 일정을 수정하는 중 오류가 발생했습니다. ID:" + id );
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