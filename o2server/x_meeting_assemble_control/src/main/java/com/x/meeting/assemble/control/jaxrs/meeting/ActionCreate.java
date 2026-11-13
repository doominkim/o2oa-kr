package com.x.meeting.assemble.control.jaxrs.meeting;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.MessageFactory;
import com.x.meeting.assemble.control.service.HstService;
import com.x.meeting.core.entity.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

class ActionCreate extends BaseAction {

	private final static Logger logger = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Meeting meeting = Wi.copier.copy(wi);
			Room room = null;
			if(StringUtils.isBlank(wi.getSubject())){
				throw new ExceptionCustomError("회의 제목은 비워 둘 수 없습니다!");
			}

			String applicant = effectivePerson.getDistinguishedName();
			/** 백엔드 호출이면 프로세스로 회의를 트리거 */
			if (effectivePerson.isCipher() && StringUtils.isNotEmpty(wi.getApplicant())) {
				applicant = wi.getApplicant();
			}
			if (!Config.token().isInitialManager(applicant)) {
				applicant = business.organization().person().get(applicant);
			}
			if (StringUtils.isEmpty(applicant)) {
				throw new ExceptionPersonNotExist(applicant);
			}
			meeting.setApplicant(applicant);
			if (meeting.getInviteMemberList() == null) {
				meeting.setInviteMemberList(meeting.getInvitePersonList());
			}
			meeting.setInvitePersonList(this.convertToPerson(business, meeting.getInviteMemberList()));

			if(MeetingModeEnum.ONLINE.getValue().equals(meeting.getMode())){
				if(StringUtils.isBlank(meeting.getRoomId()) || StringUtils.isBlank(meeting.getRoomLink())){
					MeetingConfigProperties config = business.getConfig();
					if(config.onLineEnabled()){
						boolean flag = HstService.createMeeting(meeting, config);
						if(!flag){
							throw new ExceptionCustomError("온라인 회의를 만들지 못했습니다. 관리자에게 문의하세요!");
						}
					}else{
						throw new ExceptionCustomError("회의 번호와 회의 링크는 비워 둘 수 없습니다!");
					}
				}
			}else if(StringUtils.isBlank(wi.getRoom())){
				throw new ExceptionCustomError("회의실은 비워 둘 수 없습니다!");
			}
			if(StringUtils.isNotBlank(wi.getRoom())){
				room = emc.find(wi.getRoom(), Room.class);
				if (null == room) {
					throw new ExceptionRoomNotExist(wi.getRoom());
				}
				if (room.getAvailable() == false) {
					throw new ExceptionRoomNotAvailable(room.getName());
				}
				if(StringUtils.isNotBlank(room.getAuditor())) {
					meeting.setAuditor(room.getAuditor());
				}
				meeting.setRoom(room.getId());
				Date startTime = DateTools.addSeconds(meeting.getStartTime(),1);
				Date completedTime = DateTools.addSeconds(meeting.getCompletedTime(),-1);
				if (!business.room().checkIdle(meeting.getRoom(), startTime, completedTime, "")) {
					throw new ExceptionRoomNotAvailable(room.getName());
				}
			}

			meeting.setManualCompleted(false);

			meeting.setAcceptPersonList(this.convertToPerson(business, meeting.getAcceptPersonList()));
			meeting.setRejectPersonList(this.convertToPerson(business, meeting.getRejectPersonList()));
//			meeting.getInvitePersonList().remove(meeting.getApplicant());

			business.estimateConfirmStatus(meeting);
			emc.beginTransaction(Meeting.class);
			emc.persist(meeting, CheckPersistType.all);
			emc.commit();
			if (ConfirmStatus.allow.equals(meeting.getConfirmStatus())) {
				for (String _s : meeting.getInvitePersonList()) {
					MessageFactory.meeting_invite(_s, meeting, room);
				}
				// this.notifyMeetingInviteMessage(business, meeting);
			}
			Wo wo = new Wo();
			wo.setId(meeting.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Meeting {

		private static final long serialVersionUID = -4637797853096659198L;
		static WrapCopier<Wi, Meeting> copier = WrapCopierFactory.wi(Wi.class, Meeting.class, null,
				JpaObject.FieldsUnmodify);
	}

	public static class Wo extends WoId {

	}

}
