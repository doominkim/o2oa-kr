package com.x.calendar.assemble.control.jaxrs.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

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
import com.x.calendar.core.entity.Calendar_Event;

/**
 * 조건과 권한에 따라 접근 가능한 조건에 맞는 캘린더 정보 조회
 * 1. 조회 기간 내 이벤트 생성이 필요한 반복 이벤트 주체 조회
 * 2. 이벤트 생성
 * 3. 조건에 따라 일정 이벤트 정보 목록 조회
 * @author O2LEE
 *
 */
public class ActionListWithCondition extends BaseAction {
	
private Logger logger = LoggerFactory.getLogger( ActionListWithCondition.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {		
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		List<WoCalendar_Event> wrapEvents = null;
		List<Calendar_Event> calendar_EventList = null;
		Boolean check = true;
		Wi wi = null;
		List<String> ids = null;
		List<String> unitNames = null;
		List<String> groupNames = null;
		String personName = effectivePerson.getDistinguishedName();
				
//		if( check ) {
//			try {
//				manager = ThisApplication.isCalendarSystemManager( effectivePerson );
//			}catch( Exception e) {
//				check = false;
//				Exception exception = new ExceptionEventProcess( e, "사용자가 시스템 관리자인지 조회하는 중 예외가 발생했습니다." );
//				result.error( exception );
//				logger.error( e, effectivePerson, request, null);
//			}
//		}		

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			//조회 날짜 범위가 설정되지 않으면 이번 달 조회
			if( wi.getStartTime() == null && wi.getEndTime() == null ) {
				wi.setStartTime( dateOperation.getFirstDayInMonth( new Date() ));
				wi.setEndTime( dateOperation.getEndDayInMonth( new Date() ));
			}
			if( StringUtils.isEmpty( wi.getEventType() )) {
				wi.setEventType( "CAL_EVENT" );
			}
		}
		
		if( check ){
			try {
				unitNames = userManagerService.listUnitNamesWithPerson( personName );
				groupNames = userManagerService.listGroupNamesByPerson( personName );
				if( ListTools.isEmpty( wi.getCalendarIds()  )) {
					//사용자가 볼 수 있는 모든 CalendarIds 조회
					wi.setCalendarIds( calendarServiceAdv.listWithCondition(personName, unitNames, groupNames) );
				}
				if( ListTools.isNotEmpty( wi.getCalendarIds()  ) ) {
					ids = calendar_EventServiceAdv.listWithCondition( wi.getKey(), wi.getEventType(), wi.getSource(), wi.getCreatePerson(), wi.getCalendarIds(),
							personName, unitNames, groupNames, wi.getStartTime(), wi.getEndTime() );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionEventProcess( e, "사용자 권한으로 캘린더 ID 목록을 조회하는 중 오류가 발생했습니다." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
//			if( manager ) {
//				try {
//					ids = calendar_EventServiceAdv.listWithCondition( wi.getKey(), wi.getEventType(), wi.getSource(), wi.getCreatePerson(), wi.getCalendarIds(), 
//							null, null, null, wi.getStartTime(), wi.getEndTime() );
//				} catch (Exception e) {
//					check = false;
//					Exception exception = new ExceptionEventProcess( e, "사용자 권한에 따라 캘린더 정보 ID 목록을 조회하는 중 예외가 발생했습니다." );
//					result.error( exception );
//					logger.error( e, effectivePerson, request, null);
//				}
//			}else {
//				
//			}
		}
		
		if( check ){
			if( ListTools.isNotEmpty( ids )) {
				calendar_EventList = calendar_EventServiceAdv.list(ids);
				try {
					wrapEvents = WoCalendar_Event.copier.copy( calendar_EventList );
					if( ListTools.isNotEmpty( wrapEvents )) {
						//입력에 필요한 형식으로 구성
						wo.initInOneDayEventsList( wi.getStartTime(), wi.getEndTime() );
						for( WoCalendar_Event calendar_Event : wrapEvents ) {
							//출력 형식 구성
							if( calendar_Event.getDaysOfDuration() >= 1 ) {
								wo.addWholeDayEvent(calendar_Event);
							}else {
								wo.addInOneDayEvents( dateOperation.getDate( calendar_Event.getStartTime(), "yyyy-MM-dd"), calendar_Event);
							}
						}
					}
					if( wo.getInOneDayEvents() == null ) {
						wo.setInOneDayEvents( new ArrayList<>());
					}
					if( wo.getWholeDayEvents() == null ) {
						wo.setWholeDayEvents( new ArrayList<>());
					}					
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionEventProcess( e, "조회한 캘린더 정보를 출력 정보로 변환하는 중 오류가 발생했습니다." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		result.setData(wo);
		return result;
	}
	
	public static class Wi{

		@FieldDescribe("캘린더 계정 ID")
		private List<String> calendarIds = null;
		
		@FieldDescribe("정보 유형: CAL_EVENT | TASK_EVENT")
		private String eventType = "CAL_EVENT";
		
		@FieldDescribe("정보 출처: PERSONAL| LEADER | UNIT | MEETING | BUSINESS_TRIP | HOLIDAY")
		private String source;
		
		@FieldDescribe("이벤트 제목 또는 비고 정보 유사 검색")
		private String key = null;
		
		@FieldDescribe("조회 시작 시간")
		private Date startTime = null;

		@FieldDescribe("조회 종료 시간")
		private Date endTime = null;

		@FieldDescribe("작성자")
	    private String createPerson = null;

		public String getEventType() {
			return eventType;
		}

		public String getKey() {
			return key;
		}

		public Date getStartTime() {
			return startTime;
		}

		public Date getEndTime() {
			return endTime;
		}

		public void setEventType(String eventType) {
			this.eventType = eventType;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public void setEndTime(Date endTime) {
			this.endTime = endTime;
		}

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public List<String> getCalendarIds() {
			return calendarIds;
		}

		public void setCalendarIds(List<String> calendarIds) {
			this.calendarIds = calendarIds;
		}

		public String getCreatePerson() {
			return createPerson;
		}

		public void setCreatePerson(String createPerson) {
			this.createPerson = createPerson;
		}
		
	}
	
	public static class Wo {
		
		@FieldDescribe("종일 또는 1일 초과(1일 걸침) 이벤트 목록.")
		private List<WoCalendar_Event> wholeDayEvents = null;
		
		@FieldDescribe("지속 시간 1일 이내 이벤트 목록. 날짜별로 구분")
		private List<WoCalendar_Event_ForDay> inOneDayEvents = null;

		public List<WoCalendar_Event> getWholeDayEvents() {
			if( wholeDayEvents == null ) {
				wholeDayEvents = new ArrayList<>();
			}
			return wholeDayEvents;
		}

		public List<WoCalendar_Event_ForDay> getInOneDayEvents() {
			if( inOneDayEvents == null ) {
				inOneDayEvents = new ArrayList<>();
			}
			return inOneDayEvents;
		}

		public void setWholeDayEvents(List<WoCalendar_Event> wholeDayEvents) {
			this.wholeDayEvents = wholeDayEvents;
		}

		public void setInOneDayEvents(List<WoCalendar_Event_ForDay> inOneDayEvents) {
			this.inOneDayEvents = inOneDayEvents;
		}
		
		public List<WoCalendar_Event_ForDay> initInOneDayEventsList( Date startDate, Date endDate ){
			List<WoCalendar_Event_ForDay> eventsForDayList = new ArrayList<>();
			List<String> dateStrings = dateOperation.listDaysBetweenDate(startDate, endDate);
			if( ListTools.isNotEmpty( dateStrings )) {
				for( String dateString : dateStrings ) {
					WoCalendar_Event_ForDay woCalendar_Event_ForDay = new WoCalendar_Event_ForDay();
					woCalendar_Event_ForDay.setEventDate( dateString );
					woCalendar_Event_ForDay.setInOneDayEvents( new ArrayList<>());
					eventsForDayList.add( woCalendar_Event_ForDay );
				}
			}
			this.inOneDayEvents = eventsForDayList;
			return this.inOneDayEvents;
		}
		
		public List<WoCalendar_Event_ForDay> addInOneDayEvents( String eventDate, WoCalendar_Event woEvent ){
			if( this.inOneDayEvents == null ) {
				this.inOneDayEvents = new ArrayList<>();
			}
			for( WoCalendar_Event_ForDay calendar_Events_ForDay : this.inOneDayEvents ) {
				if( eventDate.equalsIgnoreCase(calendar_Events_ForDay.getEventDate()  )) {
					calendar_Events_ForDay.addEventInDay( woEvent );
				}
			}
			return this.inOneDayEvents;
		}
		
		public List<WoCalendar_Event> addWholeDayEvent( WoCalendar_Event woEvent ){
			if( this.wholeDayEvents == null ) {
				this.wholeDayEvents = new ArrayList<>();
			}
			if( !this.wholeDayEvents.contains( woEvent )) {
				this.wholeDayEvents.add( woEvent );
			}
			return this.wholeDayEvents;
		}
	}
	
	public static class WoCalendar_Event_ForDay  {
		
		@FieldDescribe("날짜 문자열.")
		private String eventDate = null;
		
		@FieldDescribe("지속 시간 1일 이내 이벤트 목록.")
		private List<WoCalendar_Event> inOneDayEvents = null;

		public String getEventDate() {
			return eventDate;
		}

		public List<WoCalendar_Event> getInOneDayEvents() {
			if( inOneDayEvents == null ) {
				inOneDayEvents = new ArrayList<>();
			}
			return inOneDayEvents;
		}

		public void setEventDate(String eventDate) {
			this.eventDate = eventDate;
		}

		public void setInOneDayEvents(List<WoCalendar_Event> inOneDayEvents) {
			this.inOneDayEvents = inOneDayEvents;
		}		
		
		public List<WoCalendar_Event> addEventInDay( WoCalendar_Event woEvent ){
			if( this.inOneDayEvents == null ) {
				this.inOneDayEvents = new ArrayList<>();
			}
			if( !this.inOneDayEvents.contains( woEvent )) {
				this.inOneDayEvents.add( woEvent );
			}
			return this.inOneDayEvents;
		}
	}

	public static class WoCalendar_Event extends Calendar_Event  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<Calendar_Event, WoCalendar_Event> copier = 
				WrapCopierFactory.wo( Calendar_Event.class, WoCalendar_Event.class, null,WoCalendar_Event.Excludes);
	}
}