package com.x.meeting.assemble.control.jaxrs.meeting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.WrapTools;
import com.x.meeting.assemble.control.wrapout.WrapOutMeeting;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.Meeting_;

class ActionPaging extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

			Business business = new Business(emc);
			List<String> ids = new ArrayList<>();

			EntityManager em = emc.get(Meeting.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();

			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Meeting> root = cq.from(Meeting.class);

			Predicate p = cb.equal(root.get(Meeting_.applicant), effectivePerson.getDistinguishedName());
			p = cb.or(p, cb.equal(root.get(Meeting_.auditor), effectivePerson.getDistinguishedName()));

			Expression<List<String>> expression = root.get(Meeting_.invitePersonList);
			p = cb.or(p, expression.in(effectivePerson.getDistinguishedName()));

			p = filterSubject(cb, root, p, wi.getSubject());
			p = filterRoom(cb, root, p, wi.getRoom());

			if (StringUtils.isNotBlank(wi.getMeetingStatus())) {
				p = filterMeetingStatus(cb, root, p, wi.getMeetingStatus());
			} else {
				p = filterStartTime(cb, root, p, wi.getStartTime());

				p = filterCompletedTime(cb, root, p, wi.getCompletedTime());
			}

			p = filterConfirmStatus(cb, root, p, wi.getConfirmStatus());

			p = filterApplicant(cb, root, p, wi.getApplicant());

			p = filterInvitePerson(cb, root, p, wi.getInvitePersonList());

			p = filterAcceptPerson(cb, root, p, wi.getAcceptPersonList());

			p = filterCheckinPerson(cb, root, p, wi.getCheckinPersonList());

			p = filterManualCompleted(cb, root, p, wi.getManualCompleted());

			p = filterHostUnit(cb, root, p, wi.getHostUnit());

			p = filterHostPerson(cb, root, p, wi.getHostPerson());

			p = filterType(cb, root, p, wi.getType());

			Order order;
			String sortField = wi.getSortField();
			String sortType = wi.getSortType();

			if (!StringUtils.isBlank(sortField)) {
				if (sortType.equalsIgnoreCase("asc")) {
					order = cb.asc(root.get(sortField));
				} else {
					order = cb.desc(root.get(sortField));
				}
			} else {
				order = cb.desc(root.get("startTime"));
			}

			cq.select(root.get(Meeting_.id)).where(p).orderBy(order);
			cq.distinct(true);

			TypedQuery<String> typedQuery = em.createQuery(cq);
			int pageIndex = (page - 1) * size;
			int pageSize = size;
			typedQuery.setFirstResult(pageIndex);
			typedQuery.setMaxResults(pageSize);
			ids = typedQuery.getResultList();
			LOGGER.info("pagingtypedQuery:{}", typedQuery::toString);

			TypedQuery<String> tqCount = em.createQuery(cq.select(root.get(Meeting_.id)).where(p).distinct(true));
			List<String> allid = tqCount.getResultList();
			Long tpsize = (long) allid.size();

			CriteriaQuery<Meeting> cqMeeting = cb.createQuery(Meeting.class);
			Predicate pMeeting = cb.isMember(root.get(Meeting_.id), cb.literal(ids));
			Root<Meeting> rootMeeting = cqMeeting.from(Meeting.class);
			cqMeeting.select(rootMeeting).where(pMeeting).orderBy(order);
			List<Meeting> os = em.createQuery(cqMeeting).getResultList();

			List<Wo> wos = Wo.copier.copy(os);
			this.setOnlineLink(business, effectivePerson, wos);
			WrapTools.decorate(business, wos, effectivePerson);
			WrapTools.setAttachment(business, wos);
			result.setData(wos);
			result.setCount(tpsize);
			return result;
		}
	}

	public static class Wo extends WrapOutMeeting {
		private static final long serialVersionUID = 4609263020989488356L;
		static final WrapCopier<Meeting, Wo> copier = WrapCopierFactory.wo(Meeting.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

	public static class Wi {

		@FieldDescribe("제목.")
		private String subject;

		@FieldDescribe("소속 층.")
		private String room;

		@FieldDescribe("시작 시간.")
		private Date startTime;

		@FieldDescribe("종료 시간.")
		private Date completedTime;

		@FieldDescribe("회의 예약 상태.(allow|deny|wait)")
		private String confirmStatus;

		@FieldDescribe("생성 사용자.")
		private String applicant;

		@FieldDescribe("초대 사용자, 신분, 조직.")
		private String invitePersonList;

		@FieldDescribe("수락 사용자.")
		private String acceptPersonList;

		@FieldDescribe("출석 체크 사용자.")
		private String checkinPersonList;

		@FieldDescribe("회의 수동 종료 여부.(true|false)")
		private Boolean manualCompleted;

		@FieldDescribe("회의 현재 상태.(applying|wait|processing|completed)")
		private String meetingStatus;

		@FieldDescribe("정렬 필드.(startTime|completedTime|room)")
		private String sortField;

		@FieldDescribe("정렬.(desc|asc)")
		private String sortType;

		@FieldDescribe("주관 부서")
		private String hostUnit;

		@FieldDescribe("사회자")
		private String hostPerson;

		@FieldDescribe("유형")
		private String type;

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getConfirmStatus() {
			return confirmStatus;
		}

		public void setConfirmStatus(String confirmStatus) {
			this.confirmStatus = confirmStatus;
		}

		public String getRoom() {
			return room;
		}

		public void setRoom(String room) {
			this.room = room;
		}

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public Date getCompletedTime() {
			return completedTime;
		}

		public void setCompletedTime(Date completedTime) {
			this.completedTime = completedTime;
		}

		public String getApplicant() {
			return applicant;
		}

		public void setApplicant(String applicant) {
			this.applicant = applicant;
		}

		public String getHostUnit() {
			return hostUnit;
		}

		public void setHostUnit(String hostUnit) {
			this.hostUnit = hostUnit;
		}

		public String getHostPerson() {
			return hostPerson;
		}

		public void setHostPerson(String hostPerson) {
			this.hostPerson = hostPerson;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getInvitePersonList() {
			return invitePersonList;
		}

		public void setInvitePersonList(String invitePersonList) {
			this.invitePersonList = invitePersonList;
		}

		public String getAcceptPersonList() {
			return acceptPersonList;
		}

		public void setAcceptPersonList(String acceptPersonList) {
			this.acceptPersonList = acceptPersonList;
		}

		public String getCheckinPersonList() {
			return checkinPersonList;
		}

		public void setCheckinPersonList(String checkinPersonList) {
			this.checkinPersonList = checkinPersonList;
		}

		public Boolean getManualCompleted() {
			return manualCompleted;
		}

		public void setManualCompleted(Boolean manualCompleted) {
			this.manualCompleted = manualCompleted;
		}

		public String getMeetingStatus() {
			return meetingStatus;
		}

		public void setMeetingStatus(String meetingStatus) {
			this.meetingStatus = meetingStatus;
		}

		public String getSortField() {
			return sortField;
		}

		public void setSortField(String sortField) {
			this.sortField = sortField;
		}

		public String getSortType() {
			return sortType;
		}

		public void setSortType(String sortType) {
			this.sortType = sortType;
		}

	}
}
