package com.x.calendar.assemble.control.jaxrs.event;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapInteger;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.calendar.assemble.control.ThisApplication;
import com.x.calendar.core.entity.Calendar;
import com.x.calendar.core.entity.Calendar_Event;

/**
 * 반복 일정 이벤트 중 특정 이벤트부터 이후의 모든 일정 이벤트 정보 삭제
 * 
 * 1. 현재 이벤트 및 해당 반복 이벤트 주체 정보로 이미 생성된, 해당 ID 이벤트 이후 시간의 모든 생성된 일정 이벤트 정보 삭제
 * 
 * @author O2LEE
 *
 */
public class ActionDestroyAfterEventId extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger( ActionUpdateAfterEventId.class );

	@AuditLog(operation = "일정 삭제")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		
		//LogUtil.INFO( ">>>>>>ActionDestroyAfterEventId.execute......" );
		ActionResult<Wo> result = new ActionResult<>();
		Integer count = 0;
		Wo wo = new Wo();
		Calendar calendar = null;
		Calendar_Event calendar_Event = null;
		List<String> unitNames = null;
		List<String> groupNames = null;
		String personName = effectivePerson.getDistinguishedName();
		Boolean check = true;
		
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
						Exception exception = new ExceptionInsufficientPermissions( "캘린더에서 일정을 삭제할 권한이 없습니다. 캘린더:" + calendar.getName() );
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
			if( StringUtils.isEmpty( calendar_Event.getRepeatMasterId() ) ) {
				check = false;
				Exception exception = new ExceptionEventIsNotRecurrenceEvent( id );
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				count = calendar_EventServiceAdv.destoryAfterEventId( calendar_Event.getRepeatMasterId(), calendar_Event );
				wo.setValue( count );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "일정 정보를 삭제하는 중 오류가 발생했습니다." );
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
	
	public static class Wo extends WrapInteger {
	}
}