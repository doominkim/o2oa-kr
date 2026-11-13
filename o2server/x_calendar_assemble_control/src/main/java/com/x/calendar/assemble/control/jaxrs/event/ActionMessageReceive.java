package com.x.calendar.assemble.control.jaxrs.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.calendar.assemble.control.EnumCalendarSource;
import com.x.calendar.assemble.control.ExceptionWrapInConvert;
import com.x.calendar.core.entity.Calendar_Event;

public class ActionMessageReceive extends BaseAction {

	private Logger logger = LoggerFactory.getLogger(ActionMessageReceive.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.debug("receive:{}.", jsonElement);
		ActionResult<Wo> result = new ActionResult<>();
		Wi message = new Wi();
		Wo wo = new Wo();
		wo.setValue( false );
		Calendar_Event calendar_Event = null;
		Boolean check = true;
		
		try {
			message = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ) {
			//MEETING: meeting_invite | meeting_delete | meeting_accept | meeting_reject"
			if( message != null ) {
				if( "meeting_accept".equalsIgnoreCase( message.getType() ) ) {
					calendar_Event = composeCalendarEvent( message );
					PromptException exception = this.eventValidate( calendar_Event, null );
					if( exception != null ) {
						check = false;
						result.error( exception );
					}else {
						try {
							calendar_Event = calendar_EventServiceAdv.createByCipher( calendar_Event );
							List<String> ids = new ArrayList<>();
							ids.add( calendar_Event.getId() );
							wo.setValue( true );
						} catch (Exception e) {
							check = false;
							exception = new ExceptionMessageProcess( e, "메시지를 처리하는 중 오류가 발생했습니다." );
							result.error( exception );
							logger.error( e, effectivePerson, request, null);
						}
					}
				}else if( "calendar_event_delete".equalsIgnoreCase( message.getType() ) ) {
					deleteCalendarEvent( message );
					wo.setValue( true );
				}
			}
		}
		result.setData(wo);
		return result;
	}

	/**
	 * 캘린더 이벤트 삭제
	 * @param message
	 * @return
	 * @throws Exception
	 */
	private List<String> deleteCalendarEvent( Wi message ) throws Exception {
		WiMeeting wiMeeting = gson.fromJson( message.getBody(), WiMeeting.class );
		if( StringUtils.isNotEmpty( wiMeeting.getId() ) ) {
			//bundle 기준으로 삭제
			return calendar_EventServiceAdv.destoryWithBundle( wiMeeting.getId() );
		}
		return null;
	}
	
	/**
	 * 메시지 객체를 일정 이벤트 객체로 변환
	 * @param message
	 * @return
	 * @throws Exception 
	 */
	private Calendar_Event composeCalendarEvent( Wi message ) throws Exception {
		WiMeeting wiMeeting = null;
		Calendar_Event calendar_Event = null;
		String person = message.getPerson();
		String title = message.getTitle();
		if( StringUtils.isEmpty( person )) {
			throw new Exception("person is empy!");
		}
		wiMeeting = gson.fromJson( message.getBody(), WiMeeting.class );
		if( wiMeeting != null ) {
			calendar_Event = new Calendar_Event();
			calendar_Event.setBundle( wiMeeting.getId() );
			calendar_Event.setColor( "#1462BE" );
			calendar_Event.setComment( title );
			calendar_Event.setValarm_description( wiMeeting.getDescription() );
			calendar_Event.setValarm_Summary( title );			
			calendar_Event.setEventType( "CAL_EVENT" );
			calendar_Event.setStartTime( wiMeeting.getStartTime() );
			calendar_Event.setEndTime( wiMeeting.getCompletedTime() );	
			calendar_Event.setSource( EnumCalendarSource.MEETING.name() );
			calendar_Event.setValarmTime_config( "0,0,-30,0" );
			return calendar_Event;
		}
		return null;
	}
	
	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("메시지 유형: meeting_invite | meeting_delete | meeting_accept | meeting_reject")
		private String type;

		@FieldDescribe("수신자")
		private String person;

		@FieldDescribe("메시지 알림 제목")
		private String title;

		@FieldDescribe("메시지 내용")
		private JsonElement body;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public JsonElement getBody() {
			return body;
		}

		public void setBody(JsonElement body) {
			this.body = body;
		}
	}

	public static class WiMeeting{
		
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);
		
		public static WrapCopier< Wi, WiMeeting > copier =  WrapCopierFactory.wi( Wi.class, WiMeeting.class, null, null );	
		
		@FieldDescribe("데이터베이스 기본 키, 자동 생성.")
		private String id = null;
		
		@FieldDescribe("이름")
		private String subject;

		@FieldDescribe("설명")
		private String description;

		@FieldDescribe("소속 층.")
		private String room;
		
		@FieldDescribe("시작 시간.")
		private Date startTime;

		@FieldDescribe("종료 시간.")
		private Date completedTime;

		@FieldDescribe("초대 사용자.")
		private List<String> invitePersonList;

		@FieldDescribe("수락 사용자.")
		private List<String> acceptPersonList;

		@FieldDescribe("거절 사용자.")
		private List<String> rejectPersonList;

		@FieldDescribe("회의 신청자")
		private String applicant;

		@FieldDescribe("비고.")
		private String memo;

		public String getId() {
			return id;
		}

		public String getSubject() {
			return subject;
		}

		public String getDescription() {
			return description;
		}

		public String getRoom() {
			return room;
		}

		public Date getStartTime() {
			return startTime;
		}

		public Date getCompletedTime() {
			return completedTime;
		}

		public List<String> getInvitePersonList() {
			return invitePersonList;
		}

		public List<String> getAcceptPersonList() {
			return acceptPersonList;
		}

		public List<String> getRejectPersonList() {
			return rejectPersonList;
		}

		public String getApplicant() {
			return applicant;
		}

		public String getMemo() {
			return memo;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setRoom(String room) {
			this.room = room;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public void setCompletedTime(Date completedTime) {
			this.completedTime = completedTime;
		}

		public void setInvitePersonList(List<String> invitePersonList) {
			this.invitePersonList = invitePersonList;
		}

		public void setAcceptPersonList(List<String> acceptPersonList) {
			this.acceptPersonList = acceptPersonList;
		}

		public void setRejectPersonList(List<String> rejectPersonList) {
			this.rejectPersonList = rejectPersonList;
		}

		public void setApplicant(String applicant) {
			this.applicant = applicant;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}		
	}

	public static class Wo extends WrapBoolean {

	}

}