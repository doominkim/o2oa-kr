package com.x.calendar.assemble.control;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.schedule.AlarmTrigger;
import com.x.calendar.assemble.control.schedule.CheckEventComment;
import com.x.calendar.assemble.control.service.UserManagerService;
import com.x.calendar.core.entity.Calendar;
import com.x.calendar.core.entity.Calendar_Event;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	protected static Context context;
	public static final String CalendarMANAGER = "CalendarManager";

	public static Context context() {
		return context;
	}

	public static void init() throws Exception {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			MessageConnector.start(context());
			// 30초마다 푸시할 메시지 확인
			context.schedule(AlarmTrigger.class, "0/30 * * * * ?");
			// 2시간마다 comment 정보의 참조 상태를 확인하고 불필요한 데이터 삭제
			context.schedule(CheckEventComment.class, "4 4 */2 * * ?");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			CacheManager.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 사용자의 Calendar 시스템 관리 권한 여부 판단 1. 시스템 관리자 Manager 또는 xadmin 2. CalendarManager 역할을 가진 사용자
	 * 
	 * @param effectivePerson
	 * @return
	 */
	public static Boolean isCalendarSystemManager(EffectivePerson effectivePerson) {
		UserManagerService userManagerService = new UserManagerService();
		try {
			if (userManagerService.isHasPlatformRole(effectivePerson.getDistinguishedName(),
					ThisApplication.CalendarMANAGER) || effectivePerson.isManager()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 사용자가 지정한 캘린더의 관리 권한을 가졌는지 판단
	 * 
	 * @param effectivePerson
	 * @param calendar
	 * @return
	 */
	public static Boolean isCalendarManager(EffectivePerson effectivePerson, Calendar calendar) {
		if (BooleanUtils.isTrue(isCalendarSystemManager(effectivePerson))) {
			return true;
		}
		if (calendar != null) {
			// 관리 권한 판단
			if (calendar.getCreateor().equalsIgnoreCase(effectivePerson.getDistinguishedName())) {
				return true;
			}
			if (ListTools.isNotEmpty(calendar.getManageablePersonList())
					&& calendar.getManageablePersonList().contains(effectivePerson.getDistinguishedName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 사용자가 지정한 캘린더의 일정 이벤트 게시 권한을 가졌는지 판단
	 * 
	 * @param effectivePerson
	 * @param calendar
	 * @return
	 */
	public static Boolean isCalendarPublisher(EffectivePerson effectivePerson, List<String> unitNames,
			List<String> groupNames, Calendar calendar) {
		if (BooleanUtils.isTrue(isCalendarSystemManager(effectivePerson))) {
			return true;
		}
		if (calendar != null) {
			if (BooleanUtils.isTrue(isCalendarManager(effectivePerson, calendar))) {
				return true;
			}
			// 게시 권한 판단
			String personName = effectivePerson.getDistinguishedName();
			if (ListTools.isNotEmpty(calendar.getPublishablePersonList())
					&& calendar.getPublishablePersonList().contains(personName)) {
				return true;
			}
			if (ListTools.isNotEmpty(calendar.getPublishableUnitList()) && ListTools.isNotEmpty(unitNames)) {
				for (String publishUnitName : calendar.getPublishableUnitList()) {
					for (String unitName : unitNames) {
						if (unitName.equalsIgnoreCase(publishUnitName)) {
							return true;
						}
					}
				}
			}
			if (ListTools.isNotEmpty(calendar.getPublishableGroupList()) && ListTools.isNotEmpty(groupNames)) {
				for (String publishGroupName : calendar.getPublishableGroupList()) {
					for (String groupName : groupNames) {
						if (groupName.equalsIgnoreCase(publishGroupName)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * 사용자가 지정한 캘린더의 일정 이벤트 게시 권한을 가졌는지 판단
	 * 
	 * @param effectivePerson
	 * @param calendar
	 * @return
	 * @throws Exception
	 */
	public static Boolean isCalendarPublisher(EffectivePerson effectivePerson, Calendar calendar) throws Exception {
		if (BooleanUtils.isTrue(isCalendarSystemManager(effectivePerson))) {
			return true;
		}
		List<String> unitNames = null;
		List<String> groupNames = null;
		if (calendar != null) {
			if (BooleanUtils.isTrue(isCalendarManager(effectivePerson, calendar))) {
				return true;
			}
			UserManagerService userManagerService = new UserManagerService();
			// 게시 권한 판단
			String personName = effectivePerson.getDistinguishedName();
			unitNames = userManagerService.listUnitNamesWithPerson(personName);
			groupNames = userManagerService.listGroupNamesByPerson(personName);
			if (ListTools.isNotEmpty(calendar.getPublishablePersonList())
					&& calendar.getPublishablePersonList().contains(personName)) {
				return true;
			}
			if (ListTools.isNotEmpty(calendar.getPublishableUnitList()) && ListTools.isNotEmpty(unitNames)) {
				for (String publishUnitName : calendar.getPublishableUnitList()) {
					for (String unitName : unitNames) {
						if (unitName.equalsIgnoreCase(publishUnitName)) {
							return true;
						}
					}
				}
			}
			if (ListTools.isNotEmpty(calendar.getPublishableGroupList()) && ListTools.isNotEmpty(groupNames)) {
				for (String publishGroupName : calendar.getPublishableGroupList()) {
					for (String groupName : groupNames) {
						if (groupName.equalsIgnoreCase(publishGroupName)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * 사용자가 지정한 일정 이벤트의 관리 권한을 가졌는지 판단
	 * 
	 * @param effectivePerson
	 * @param event
	 * @return
	 * @throws Exception
	 */
	public static Boolean isEventManager(EffectivePerson effectivePerson, Calendar calendar, Calendar_Event event)
			throws Exception {
		if (BooleanUtils.isTrue(isCalendarSystemManager(effectivePerson))) {
			return true;
		}
		List<String> unitNames = null;
		List<String> groupNames = null;
		if (event != null) {
			UserManagerService userManagerService = new UserManagerService();
			String personName = effectivePerson.getDistinguishedName();
			unitNames = userManagerService.listUnitNamesWithPerson(personName);
			groupNames = userManagerService.listGroupNamesByPerson(personName);
			// 캘린더 게시 권한 판단
			if (BooleanUtils.isTrue(isCalendarPublisher(effectivePerson, unitNames, groupNames, calendar))) {
				return true;
			}
			// 이벤트 관리 권한 판단
			if (ListTools.isNotEmpty(event.getManageablePersonList())
					&& calendar.getManageablePersonList().contains(personName)) {
				return true;
			}
		}
		return false;
	}
}
