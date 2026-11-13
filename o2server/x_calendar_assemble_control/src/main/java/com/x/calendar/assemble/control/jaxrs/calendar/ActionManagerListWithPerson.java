package com.x.calendar.assemble.control.jaxrs.calendar;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.ThisApplication;
import com.x.calendar.core.entity.Calendar;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 지정한 사용자가 접근할 수 있는 캘린더 정보 조회
 *
 * @author O2LEE
 *
 */
public class ActionManagerListWithPerson extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManagerListWithPerson.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String person) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		if(BooleanUtils.isNotTrue(userManagerService.isManager(request, effectivePerson))){
			throw new ExceptionAccessDenied(effectivePerson);
		}
		Wo wo = new Wo();
		List<Calendar> calendarList = null;
		List<String> ids = null;

		List<String> unitNames = null;
		List<String> groupNames = null;
		String personName = userManagerService.getPersonNameWithFlag(person);
		if(StringUtils.isBlank(personName)){
			throw new ExceptionPersonNotExist(person);
		}

		unitNames = userManagerService.listUnitNamesWithPerson(personName);
		groupNames = userManagerService.listGroupNamesByPerson(personName);

		ids = calendarServiceAdv.listWithCondition(personName, unitNames, groupNames);

		if (ListTools.isNotEmpty(ids)) {
			calendarList = calendarServiceAdv.list(ids);
		}

		if (ListTools.isNotEmpty(calendarList)) {
			result.setCount(Long.parseLong(calendarList.size() + ""));
			WoCalendar woCalendar = null;
			Boolean existsSystemDefaultCalendar = false;
			for (Calendar calendar : calendarList) {
				woCalendar = WoCalendar.copier.copy(calendar);
				woCalendar.setManageable(ThisApplication.isCalendarManager(effectivePerson, calendar));
				woCalendar.setPublishable(
						ThisApplication.isCalendarPublisher(effectivePerson, unitNames, groupNames, calendar));
				// 모든 캘린더를 세 종류로 나눠 서로 다른 세 LIST에 저장
				// 조직 캘린더
				if (ListTools.isNotEmpty(calendar.getFollowers())
						&& calendar.getFollowers().contains(effectivePerson.getDistinguishedName())) {
					// 내가 구독한 캘린더
					wo.addFollowCalendar(woCalendar);
				} else {
					// 개인 또는 조직 캘린더
					if ("UNIT".equalsIgnoreCase(calendar.getType())) {
						// 조직 캘린더
						wo.addUnitCalendar(woCalendar);
					} else {
						// 개인 캘린더
//							Boolean isMineCalendar = calendar.getCreateor().equalsIgnoreCase( effectivePerson.getDistinguishedName() );
						Boolean isSystemCreate = StringUtils.equalsAnyIgnoreCase("SYSTEM", calendar.getCreateor());
						if (isSystemCreate) {
							if (!existsSystemDefaultCalendar) {
								woCalendar.setCreateor(effectivePerson.getDistinguishedName());
								wo.addMyCalendar(woCalendar);
								existsSystemDefaultCalendar = true;
							} else {
								// 시스템 캘린더가 하나 더 있으므로 현재 캘린더 삭제
								calendarServiceAdv.destory(calendar.getId());
							}
						} else {
							wo.addMyCalendar(woCalendar);
						}
					}
				}
			}
		}
		result.setData(wo);
		return result;
	}

	public static class Wo {

		@FieldDescribe("내가 만든 개인 캘린더 목록.")
		private List<WoCalendar> myCalendars = new ArrayList<>();

		@FieldDescribe("볼 수 있는 조직 캘린더 목록.")
		private List<WoCalendar> unitCalendars = new ArrayList<>();

		@FieldDescribe("구독한 캘린더 목록.")
		private List<WoCalendar> followCalendars = new ArrayList<>();

		public List<WoCalendar> getMyCalendars() {
			return myCalendars;
		}

		public List<WoCalendar> getUnitCalendars() {
			return unitCalendars;
		}

		public List<WoCalendar> getFollowCalendars() {
			return followCalendars;
		}

		public void setMyCalendars(List<WoCalendar> myCalendars) {
			this.myCalendars = myCalendars;
		}

		public void setUnitCalendars(List<WoCalendar> unitCalendars) {
			this.unitCalendars = unitCalendars;
		}

		public void setFollowCalendars(List<WoCalendar> followCalendars) {
			this.followCalendars = followCalendars;
		}

		public void addMyCalendar(WoCalendar calendar) {
			if (this.myCalendars == null) {
				this.myCalendars = new ArrayList<>();
			}
			this.myCalendars.add(calendar);
		}

		public void addUnitCalendar(WoCalendar calendar) {
			if (this.unitCalendars == null) {
				this.unitCalendars = new ArrayList<>();
			}
			this.unitCalendars.add(calendar);
		}

		public void addFollowCalendar(WoCalendar calendar) {
			if (this.followCalendars == null) {
				this.followCalendars = new ArrayList<>();
			}
			this.followCalendars.add(calendar);
		}

	}

	public static class WoCalendar extends Calendar {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static {
			Excludes.add(JpaObject.sequence_FIELDNAME);
			Excludes.add("manageablePersonList");
			Excludes.add("viewablePersonList");
			Excludes.add("viewableUnitList");
			Excludes.add("viewableGroupList");
			Excludes.add("publishablePersonList");
			Excludes.add("publishableUnitList");
			Excludes.add("publishableGroupList");
			// Excludes.add( "status" );
			Excludes.add("distributeFactor");
			Excludes.add("followers");

		}

		@FieldDescribe("사용자의 해당 캘린더 관리 가능 여부.")
		private Boolean manageable = false;

		@FieldDescribe("사용자의 해당 캘린더 일정 이벤트 게시 가능 여부.")
		private Boolean publishable = false;

		public static WrapCopier<Calendar, WoCalendar> copier = WrapCopierFactory.wo(Calendar.class, WoCalendar.class,
				null, WoCalendar.Excludes);

		public Boolean getManageable() {
			return manageable;
		}

		public void setManageable(Boolean manageable) {
			this.manageable = manageable;
		}

		public Boolean getPublishable() {
			return publishable;
		}

		public void setPublishable(Boolean publishable) {
			this.publishable = publishable;
		}
	}
}
