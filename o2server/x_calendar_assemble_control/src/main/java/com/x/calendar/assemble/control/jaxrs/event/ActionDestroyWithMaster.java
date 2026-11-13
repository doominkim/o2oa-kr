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

public class ActionDestroyWithMaster extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionDestroyWithMaster.class );

	@AuditLog(operation = "删除日程事件")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String eventId ) throws Exception {
		//LogUtil.INFO( ">>>>>>ActionDestroyWithMaster.execute......" );
		ActionResult<Wo> result = new ActionResult<>();
		Calendar calendar = null;
		Calendar_Event calendar_event = null;
		List<String> unitNames = null;
		List<String> groupNames = null;
		String personName = effectivePerson.getDistinguishedName();
		Boolean check = true;
		
		if( check ){
			if( StringUtils.isEmpty( eventId ) ){
				check = false;
				Exception exception = new ExceptionEventPropertyEmpty("ID");
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				calendar_event = calendar_EventServiceAdv.get( eventId );
				if( calendar_event == null ) {
					check = false;
					Exception exception = new ExceptionEventNotExists( eventId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "ID로 일정 정보를 조회하는 중 오류가 발생했습니다. ID:" + eventId );
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
						Exception exception = new ExceptionInsufficientPermissions( "您没有权限从日历中删除日程事件.日历:" + calendar.getName() );
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
			//그 다음 repeatMaster 삭제, repeatMaster 삭제 시 해당 Master가 생성한 모든 이벤트가 함께 삭제됨
			try {
				calendar_RepeatedMasterServiceAdv.destoryWithMasterId( calendar_event.getRepeatMasterId() );
				Wo wo = new Wo();
				wo.setId( eventId );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "일정 ID로 모든 반복 일정을 삭제하는 중 오류가 발생했습니다." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoId  {
	
	}

}