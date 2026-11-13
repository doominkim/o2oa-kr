package com.x.meeting.assemble.control;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.DateTools;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.Room;

public class MessageFactory {

	public static void meeting_invite(String person, Meeting meeting, Room room) {
		String title = "회의 초대: " + meeting.getSubject() + ", 시간: "
				+ DateTools.format(meeting.getStartTime(), DateTools.format_yyyyMMddHHmm);
		if(room != null) {
			title = title + ", 장소: " + room.getName();
		}else{
			title = title + ", 회의 번호: " + meeting.getRoomId();
		}
		MessageConnector.send(MessageConnector.TYPE_MEETING_INVITE, title, person, meeting);
	}

	public static void meeting_accept(String person, Meeting meeting) throws Exception {
		String title = "(" + OrganizationDefinition.name(person) + ")님이 회의 " + meeting.getSubject() + " 초대를 수락했습니다.";
		Wo wo = Wo.copier.copy(meeting);
		wo.setFromPerson(person);
		MessageConnector.send(MessageConnector.TYPE_MEETING_ACCEPT, title, meeting.getApplicant(), wo);
	}

	public static void meeting_reject(String person, Meeting meeting) throws Exception {
		String title = "(" + OrganizationDefinition.name(person) + ")님이 회의 " + meeting.getSubject() + " 초대를 거절했습니다.";
		Wo wo = Wo.copier.copy(meeting);
		wo.setFromPerson(person);
		MessageConnector.send(MessageConnector.TYPE_MEETING_REJECT, title, meeting.getApplicant(), wo);
	}

	public static void meeting_delete(String person, Meeting meeting) throws Exception {
		String title = "회의 " + meeting.getSubject() + "이(가) 취소되었습니다.";
		MessageConnector.send(MessageConnector.TYPE_MEETING_DELETE, title, person, meeting);
	}

	public static void meeting_deleteInvitePerson(String person, Meeting meeting) throws Exception {
		String title = "회의 " + meeting.getSubject() + "이(가) 취소되었습니다.";
		MessageConnector.send(MessageConnector.TYPE_MEETING_DELETE, title, person, meeting);
	}

	public static class Wo extends Meeting {

		private static final long serialVersionUID = 6833382885219886394L;
		public static WrapCopier<Meeting, Wo> copier = WrapCopierFactory.wo(Meeting.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("사용자 수락 또는 거절")
		private String fromPerson;

		public String getFromPerson() {
			return fromPerson;
		}

		public void setFromPerson(String fromPerson) {
			this.fromPerson = fromPerson;
		}
	}

}
