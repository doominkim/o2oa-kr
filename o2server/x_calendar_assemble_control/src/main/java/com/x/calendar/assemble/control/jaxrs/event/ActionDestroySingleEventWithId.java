package com.x.calendar.assemble.control.jaxrs.event;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.calendar.assemble.control.ThisApplication;
import com.x.calendar.core.entity.Calendar;
import com.x.calendar.core.entity.Calendar_Event;

public class ActionDestroySingleEventWithId extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionDestroySingleEventWithId.class );

	@AuditLog(operation = "일정 삭제")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		//LogUtil.INFO( ">>>>>>ActionDestroySingleEventWithId.execute......" );
		ActionResult<Wo> result = new ActionResult<>();
		Calendar calendar = null;
		Calendar_Event calendar_event = null;
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
				calendar_event = calendar_EventServiceAdv.get( id );
				if( calendar_event == null ) {
					check = false;
					Exception exception = new ExceptionEventNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "ID로 일정 정보를 조회하는 중 오류가 발생했습니다. ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( StringUtils.isNotEmpty( calendar_event.getCalendarId() )) {
				try {
					calendar = calendarServiceAdv.get( calendar_event.getCalendarId() );
					if( calendar == null ) {
						check = false;
						Exception exception = new ExceptionCalendarNotExists( calendar_event.getCalendarId() );
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionEventProcess( e, "ID로 캘린더 정보를 조회하는 중 오류가 발생했습니다. ID:" + calendar_event.getCalendarId() );
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
			try {
				calendar_EventServiceAdv.destory( id );
				Wo wo = new Wo();
				wo.setId( id );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "ID로 일정 정보를 삭제하는 중 오류가 발생했습니다." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoId  {
	
	}

}