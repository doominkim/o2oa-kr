package com.x.calendar.assemble.control.jaxrs.event;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.service.CalendarServiceAdv;
import com.x.calendar.assemble.control.service.Calendar_EventRepeatMasterServiceAdv;
import com.x.calendar.assemble.control.service.Calendar_EventServiceAdv;
import com.x.calendar.assemble.control.service.UserManagerService;
import com.x.calendar.common.date.DateOperation;
import com.x.calendar.core.entity.Calendar;
import com.x.calendar.core.entity.Calendar_Event;

import net.fortuna.ical4j.model.property.RRule;

public class BaseAction extends StandardJaxrsAction{
	
	protected UserManagerService userManagerService = new UserManagerService();
	protected CalendarServiceAdv calendarServiceAdv = new CalendarServiceAdv();
	protected Calendar_EventServiceAdv calendar_EventServiceAdv = new Calendar_EventServiceAdv();
	protected Calendar_EventRepeatMasterServiceAdv calendar_RepeatedMasterServiceAdv = new Calendar_EventRepeatMasterServiceAdv();
	protected static DateOperation dateOperation = new DateOperation();

	/**
	 * 캘린더 이벤트 정보 검증 후 올바른 안내 메시지 제공
	 * @param event
	 * @param calendar
	 * @return
	 * @throws Exception
	 */
	protected PromptException eventValidate( Calendar_Event event, Calendar calendar ) throws Exception {
		
		//캘린더 ID는 비어 있을 수 없습니다
		if( StringUtils.isEmpty( event.getCalendarId() ) ){
			return new ExceptionEventPropertyEmpty("캘린더 ID(calendarId)");
		}
		
		if( calendar == null ) {
			try {
				calendar = calendarServiceAdv.get( event.getCalendarId() );
				if( calendar == null ) {
					return new ExceptionCalendarNotExists( event.getCalendarId() );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return new ExceptionEventProcess( e, "ID로 캘린더 정보를 조회하는 중 오류가 발생했습니다. ID:" + event.getCalendarId() );
			}
		}
		
		//이벤트가 캘린더의 공개 범위 상속
		if( ListTools.isNotEmpty( calendar.getViewablePersonList() )) {
			for( String value : calendar.getViewablePersonList() ) {
				event.addViewablePerson( value );
			}
		}
		if( ListTools.isNotEmpty( calendar.getViewableUnitList() )) {
			for( String value : calendar.getViewableUnitList() ) {
				event.addViewableUnit( value );
			}
		}
		if( ListTools.isNotEmpty( calendar.getViewableGroupList() )) {
			for( String value : calendar.getViewableGroupList() ) {
				event.addViewableGroup( value );
			}
		}
		
		//이벤트가 캘린더의 관리 범위 상속
		if( ListTools.isNotEmpty( calendar.getManageablePersonList() )) {
			for( String value : calendar.getManageablePersonList() ) {
				event.addManageablePerson( value );
			}
		}
		
		//이벤트가 캘린더의 공개 형식 상속
		event.setIsPublic( calendar.getIsPublic() );
		
		//기본으로 캘린더 색상 사용
		if( StringUtils.isEmpty( event.getColor() ) ){
			event.setColor( calendar.getColor() );
		}
				
		//캘린더 이벤트 공개 여부 설정, 캘린더 설정 상속
		event.setIsPublic( calendar.getIsPublic() );
		
		//제목은 비어 있을 수 없습니다
		if( StringUtils.isEmpty( event.getTitle() ) ){
			return new ExceptionEventPropertyEmpty("일정 제목(title)");
		}
		
		if( event.getStartTime() == null ) {
			event.setStartTime( new Date() );
		}
		
		if( StringUtils.isEmpty(event.getEventType())) {
			event.setEventType ("CAL_EVENT");
		}
		
		if( StringUtils.isEmpty(event.getTargetType())) {
			event.setTargetType ("PERSON");
		}
		
		if( StringUtils.isEmpty( event.getSource()) ){
			event.setSource( "PERSONAL" );
		}
		
		//시작·종료 시간 필수 여부 및 종일 여부 검증
		if( event.getIsAllDayEvent() ) {
			//시작·종료 시간 자동 입력, 전달된 시작 시간 기준
			event.setStartTime( dateOperation.getBeginTimeInDay( event.getStartTime() ) );
			event.setEndTime( dateOperation.getEndTimeInDay( event.getEndTime() ) );
		}else {
			if( event.getEndTime() == null ) {
				event.setEndTime( new Date() );
			}
			if( event.getStartTime().after(event.getEndTime() )) {
				//위치 교환
				Date temp = event.getStartTime();
				event.setStartTime( event.getEndTime() );
				event.setEndTime( temp );
			}
		}
		
		event.setStartTimeStr(dateOperation.getDateFromDate(event.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
		event.setEndTimeStr(dateOperation.getDateFromDate(event.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
		
		List<String> days = dateOperation.listDaysBetweenDate( event.getStartTime(), event.getEndTime() );
		if( ListTools.isNotEmpty( days )) {
			event.setDaysOfDuration( days.size() );
		}
		if( event.getDaysOfDuration() == 1 ) {
			//종일인지 확인, 종일이 아니면 0으로 설정
			if( !event.getIsAllDayEvent() ) {
				event.setDaysOfDuration( 0 );
			}
		}
		
		//반복 규칙 검증
		if( "TASK_EVENT".equalsIgnoreCase( event.getEventType() )) {
			//작업 이벤트는 반복 불가, 일정 이벤트 CAL_EVENT만 반복 규칙 가능
			if( StringUtils.isNotEmpty( event.getRecurrenceRule() )) {
				return new ExceptionTaskEventCanNotRecurrence();
			}
			if( StringUtils.isNotEmpty( event.getRepeatMasterId() )) {
				return new ExceptionTaskEventCanNotRepeatMaster( event.getRepeatMasterId() ) ;
			}
		}

		//반복 규칙이 있으면 규칙이 올바른지 검증 필요
		if( StringUtils.isNotEmpty( event.getRecurrenceRule() )) {
			//RRule로 변환
			try{
				new RRule(event.getRecurrenceRule());
			}catch( Exception e) {
				return new ExceptionEventRRuleInvalid(event.getRecurrenceRule());
			}
		}
		
		//알림 설정 검증
		if( StringUtils.isEmpty( event.getValarmTime_config() ) || "0,0,0,0".equalsIgnoreCase( event.getValarmTime_config().trim() )) {
			event.setValarmTime_config("0,0,0,0");
			event.setAlarm( false );
			event.setAlarmTime( null );
			event.setValarm_description( null );
			event.setValarm_mailto( null );
			event.setValarm_Summary( null );
		}else {			
			event.setAlarm( true );
			if( StringUtils.isEmpty( event.getValarm_Summary() )) {
				event.setValarm_Summary( "일정 알림" );
			}
			//시작 시간 기준으로 알림 시간 계산
			String[] alarm_config = event.getValarmTime_config().trim().split(","); 
			if( alarm_config.length == 4 ) {
				int day, hour, min, sec = 0;
				Date alarmTime = null;
				try {
					day = Integer.parseInt( alarm_config[0].trim() );
					hour = Integer.parseInt( alarm_config[01].trim() );
					min = Integer.parseInt( alarm_config[2].trim() );
					sec = Integer.parseInt( alarm_config[3].trim() );
					alarmTime = dateOperation.caculateNewDate( event.getStartTime(), day, hour, min, sec );
					event.setAlarmTime(alarmTime);
				}catch( Exception e ) {
					return new ExceptionEventAlarmConfigError("알림 설정은 ','로 구분한 숫자여야 합니다.");
				}				 
			}else {
				return new ExceptionEventAlarmConfigError("형식이 잘못되었습니다. 올바른 형식: 일,시,분,초.");
			}
		}
		return null;
	}
}
