package com.x.calendar.assemble.control.jaxrs.event;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.calendar.assemble.control.ExceptionWrapInConvert;
import com.x.calendar.core.entity.Calendar_Event;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * 조건과 권한에 따라 접근 가능한 조건에 맞는 캘린더 정보 나열(단순 목록 출력, 일별 구성 안 함)
 * 1. 조회 기간 내 이벤트 생성이 필요한 반복 이벤트 주체 조회
 * 2. 이벤트 생성
 * 3. 조건에 따라 일정 이벤트 정보 목록 조회
 * @author O2LEE
 *
 */
public class ActionSimpleListWithConditionManager extends BaseAction {

private static final Logger logger = LoggerFactory.getLogger( ActionSimpleListWithConditionManager.class );

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		if(effectivePerson.isNotManager()){
			throw new ExceptionAccessDenied(effectivePerson);
		}
		List<Wo> wos = new ArrayList<>();
		List<Calendar_Event> calendar_EventList = null;
		boolean check = true;
		Wi wi = this.convertToWrapIn( jsonElement, Wi.class );
		List<String> ids = null;
		List<String> unitNames = null;
		List<String> groupNames = null;
		String personName = StringUtils.isBlank(wi.getPerson()) ? effectivePerson.getDistinguishedName() : wi.getPerson();

		if( wi.getStartTime() == null && wi.getEndTime() == null ) {
			wi.setStartTime( dateOperation.getFirstDayInMonth( new Date() ));
			wi.setEndTime( dateOperation.getEndDayInMonth( new Date() ));
		}
		if( StringUtils.isEmpty( wi.getEventType() )) {
			wi.setEventType( "CAL_EVENT" );
		}

		try {
			personName = userManagerService.getPersonNameWithFlag( personName );
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

		if( check ){
			if( ListTools.isNotEmpty( ids )) {
				calendar_EventList = calendar_EventServiceAdv.list(ids);
				try {
					wos = Wo.copier.copy( calendar_EventList );
					if( ListTools.isNotEmpty( wos )) {
						SortTools.asc( wos, "startTime" );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionEventProcess( e, "조회한 캘린더 정보를 출력 정보로 변환하는 중 오류가 발생했습니다." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		result.setData( wos );
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

		@FieldDescribe("조회 사용자")
		private String person;

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

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}
	}

	public static class Wo extends Calendar_Event  {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<Calendar_Event, Wo> copier = WrapCopierFactory.wo( Calendar_Event.class, Wo.class, null,
                Wo.Excludes);
	}
}
