package com.x.calendar.assemble.control.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.Business;
import com.x.calendar.common.date.DateOperation;
import com.x.calendar.core.entity.Calendar_Event;
import com.x.calendar.core.entity.Calendar_EventComment;
import com.x.calendar.core.entity.Calendar_EventRepeatMaster;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Comment;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.validate.ValidationException;

/**
 * 일정 캘린더 정보 서비스 클래스
 * 
 * @author O2LEE
 *
 */
public class Calendar_EventServiceAdv {

	private DateOperation dateOperation = new DateOperation();
	private Calendar_EventService calendar_EventService = new Calendar_EventService();
	private Calendar_EventRepeatMasterService calendar_EventRepeatMasterService = new Calendar_EventRepeatMasterService();

	public List<Calendar_Event> list(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_EventService.list(emc, ids);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * ID로 지정 캘린더 레코드 정보 조회
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Calendar_Event get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(id, Calendar_Event.class);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 캘린더 레코드 정보 저장 2019-11-11
	 * 긴 비고 정보 대응 로직 추가, 비고 정보가 너무 길면 Comment 테이블에 저장하고 event와 eventmaster에 참조 ID 저장
	 * 
	 * @param calendar_event
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Calendar_Event create(Calendar_Event calendar_event, EffectivePerson effectivePerson) throws Exception {

		if (StringUtils.isEmpty(calendar_event.getId())) {
			calendar_event.setId(Calendar_Event.createId());
		}

		// 캘린더 정보 권한 설정, 최소한 본인이 생성한 캘린더는 본인이 관리·게시·열람 가능
		addStringToList(effectivePerson.getDistinguishedName(), calendar_event.getParticipants());
		addStringToList(effectivePerson.getDistinguishedName(), calendar_event.getManageablePersonList());
		addStringToList(effectivePerson.getDistinguishedName(), calendar_event.getViewablePersonList());

		if (StringUtils.isEmpty(calendar_event.getCreatePerson())) {
			calendar_event.setCreatePerson(effectivePerson.getDistinguishedName());
		}
		if (StringUtils.isEmpty(calendar_event.getUpdatePerson())) {
			calendar_event.setUpdatePerson(effectivePerson.getDistinguishedName());
		}

		Calendar_EventComment calendar_EventComment = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			calendar_EventComment = calendar_EventService.createNewEventComment(emc, calendar_event);

			// Comment 정보 수정
			if (calendar_EventComment != null) {
				calendar_event.setComment("{#CLOB#}");
				calendar_event.setCommentId(calendar_EventComment.getId());
			} else {
				calendar_event.setCommentId(null);
			}

			// 반복 일정이면 반복 정보 주체로 저장, 아니면 일반 일정 정보로 저장
			if (StringUtils.isNotEmpty(calendar_event.getRecurrenceRule())) {
				Calendar_EventRepeatMaster calendar_EventRepeatMaster = composeEventRepeatMasterWithEvent(
						calendar_event);
				calendar_event.setRepeatMasterId(calendar_EventRepeatMaster.getId());
				emc.beginTransaction(Calendar_EventRepeatMaster.class);
				calendar_EventRepeatMasterService.create(emc, calendar_EventRepeatMaster, false);
			}

			if (StringUtils.isEmpty(calendar_event.getId())) {
				calendar_event.setId(Calendar_Event.createId());
			}
			emc.beginTransaction(Calendar_Event.class);
			// 일정 정보 저장
			calendar_event = calendar_EventService.create(emc, calendar_event, false);
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
		return calendar_event;
	}

	/**
	 * 캘린더 레코드 정보 저장
	 * 
	 * @param calendar_event
	 * @return
	 * @throws Exception
	 */
	public Calendar_Event createByCipher(Calendar_Event calendar_event) throws Exception {

		if (StringUtils.isEmpty(calendar_event.getId())) {
			calendar_event.setId(Calendar_Event.createId());
		}
		String personName = calendar_event.getCreatePerson();
		// 캘린더 정보 권한 설정, 최소한 본인이 생성한 캘린더는 본인이 관리·게시·열람 가능
		addStringToList(personName, calendar_event.getParticipants());
		addStringToList(personName, calendar_event.getManageablePersonList());
		addStringToList(personName, calendar_event.getViewablePersonList());

		if (StringUtils.isEmpty(calendar_event.getCreatePerson())) {
			calendar_event.setCreatePerson(personName);
		}
		if (StringUtils.isEmpty(calendar_event.getUpdatePerson())) {
			calendar_event.setUpdatePerson(personName);
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (StringUtils.isEmpty(calendar_event.getId())) {
				calendar_event.setId(Calendar_Event.createId());
			}
			Calendar_EventComment calendar_EventComment = calendar_EventService.createNewEventComment(emc,
					calendar_event);
			// Comment 정보 수정
			if (calendar_EventComment != null) {
				calendar_event.setComment("{#CLOB#}");
				calendar_event.setCommentId(calendar_EventComment.getId());
			} else {
				calendar_event.setCommentId(null);
			}
			emc.beginTransaction(Calendar_Event.class);
			// 일정 정보 저장
			calendar_event = calendar_EventService.create(emc, calendar_event, false);
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
		return calendar_event;
	}

	/**
	 * 모든 반복 일정 이벤트 수정(반드시 반복 일정)
	 * 
	 * 1. 현재 일정 이벤트에 대응하는 반복 주체 정보 RepeatMaster 정보 수정 후 해당 이벤트 반복 주체 정보에서 이미 생성된 모든 월 정보 삭제
	 * 2. 해당 RepeatMaster로 이미 생성된 모든 일정 이벤트 삭제
	 * 
	 * @param repeatMasterId
	 * @param calendar_event
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Integer updateWithMaster(String repeatMasterId, Calendar_Event calendar_event,
			EffectivePerson effectivePerson) throws Exception {

		if (StringUtils.isEmpty(repeatMasterId)) {
			throw new Exception("calendar repeat master id is empty, calendar can not save!");
		}

		if (calendar_event == null) {
			throw new Exception("calendar event is null, calendar can not save!");
		}

		Integer count = 0;
		Business business = null;
		Calendar_EventRepeatMaster calendar_EventRepeatMaster = null;
		List<String> eventIds = null;
		List<Calendar_Event> calendarEvents = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			business = new Business(emc);
			// 1. 현재 일정 이벤트에 대응하는 반복 주체 정보 RepeatMaster 정보 수정
			calendar_EventRepeatMaster = emc.find(repeatMasterId, Calendar_EventRepeatMaster.class);
			if (calendar_EventRepeatMaster == null) {
				throw new Exception(
						"calendar repeat master not exists, calendar can not save! MasterID:" + repeatMasterId);
			}

			calendar_EventRepeatMaster = copyEventPropertyToMaster(calendar_event, calendar_EventRepeatMaster);

			// 캘린더 이벤트 반복 정보 주체 권한 설정, 최소한 본인이 생성한 캘린더는 본인이 관리·게시·열람 가능
			addStringToList(effectivePerson.getDistinguishedName(), calendar_EventRepeatMaster.getParticipants());
			addStringToList(effectivePerson.getDistinguishedName(),
					calendar_EventRepeatMaster.getManageablePersonList());
			addStringToList(effectivePerson.getDistinguishedName(), calendar_EventRepeatMaster.getViewablePersonList());

			if (StringUtils.isEmpty(calendar_EventRepeatMaster.getCreatePerson())) {
				calendar_EventRepeatMaster.setCreatePerson(effectivePerson.getDistinguishedName());
			}
			if (StringUtils.isEmpty(calendar_EventRepeatMaster.getUpdatePerson())) {
				calendar_EventRepeatMaster.setUpdatePerson(effectivePerson.getDistinguishedName());
			}
			calendar_EventRepeatMaster.setCreatedMonthList(new ArrayList<>());

			// 이벤트 비고 정보가 너무 긴지 확인, 이미 있으면 수정 필요
			Calendar_EventComment calendar_EventComment = calendar_EventService.createOrUpdateEventComment(emc,
					calendar_event);
			if (calendar_EventComment != null) {
				calendar_EventRepeatMaster.setComment("{#CLOB#}");
				calendar_EventRepeatMaster.setCommentId(calendar_EventComment.getId());
			} else {
				calendar_EventRepeatMaster.setCommentId(null);
			}

			emc.beginTransaction(Calendar_EventRepeatMaster.class);
			emc.check(calendar_EventRepeatMaster, CheckPersistType.all);

			// 2. 해당 RepeatMaster로 이미 생성된 모든 일정 이벤트 삭제, 재생성 대기
			eventIds = business.calendar_EventFactory().listWithRepeatMaster(repeatMasterId, null, null);
			if (ListTools.isNotEmpty(eventIds)) {
				count = eventIds.size();
				calendarEvents = business.calendar_EventFactory().list(eventIds);
			}
			if (ListTools.isNotEmpty(calendarEvents)) {
				emc.beginTransaction(Calendar_Event.class);
				for (Calendar_Event calendar_Event : calendarEvents) {
					emc.remove(calendar_Event, CheckRemoveType.all);
				}
			}
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
		return count;
	}

	/**
	 * 반복 일정 이벤트 중 특정 이벤트부터 이후의 모든 일정 이벤트 정보 수정(반드시 반복 일정)
	 * 
	 * 1. 현재 이벤트 및 해당 반복 이벤트 주체 정보로 이미 생성된, 해당 ID 이벤트 이후의 모든 생성된 일정 이벤트 정보 삭제
	 * 2. 해당 이벤트 반복 주체 정보에서 이미 생성된 해당 월 정보 삭제(지정된 월 및 이후 모든 월 삭제)
	 * 3. 일정 이벤트 반복 정보 주체 수정, 종료일을 지정 ID 날짜의 전날 23:59:59로 변경
	 * 4. 새 일정 반복 주체 추가, 지정 ID 일정 날짜부터 시작하며 정보와 반복 규칙을 최신 정보로 갱신
	 * 
	 * @param repeatMasterId
	 * @param calendar_event
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Integer updateAfterEventId(String repeatMasterId, Calendar_Event calendar_event,
			EffectivePerson effectivePerson) throws Exception {

		if (StringUtils.isEmpty(repeatMasterId)) {
			throw new Exception("calendar repeat master id is empty, calendar can not save!");
		}
		if (calendar_event == null) {
			throw new Exception("calendar event is null, calendar can not save!");
		}

		Integer count = 0;
		Business business = null;
		String eventYearAndMonth = null;
		Date eventMonth = null;
		Date createdMonth = null;
		Calendar_EventRepeatMaster calendar_EventRepeatMaster = null;
		Calendar_EventRepeatMaster calendar_EventRepeatMaster_new = null;
		List<String> eventIds = null;
		List<String> createdMonthList = new ArrayList<>();
		List<Calendar_Event> calendarEvents = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			business = new Business(emc);

			// -----------------------------------------------------------------------------------------------------------------//
			// 1. 현재 이벤트 및 해당 반복 이벤트 주체 정보로 이미 생성된, 해당 ID 이벤트 이후 시간의 모든 생성된 일정 이벤트 정보 삭제
			eventIds = business.calendar_EventFactory().listWithRepeatMaster(repeatMasterId,
					calendar_event.getStartTime(), null);
			if (ListTools.isNotEmpty(eventIds)) {
				count = eventIds.size();
				calendarEvents = business.calendar_EventFactory().list(eventIds);
				if (ListTools.isNotEmpty(calendarEvents)) {
					emc.beginTransaction(Calendar_Event.class);
					for (Calendar_Event calendar_Event : calendarEvents) {
						emc.remove(calendar_Event, CheckRemoveType.all);
					}
				}
			}

			// -----------------------------------------------------------------------------------------------------------------//
			// 2. 해당 이벤트 반복 주체 정보에서 이미 생성된 해당 월 정보 삭제(지정된 월 및 이후 모든 월 삭제)
			// 3. 일정 이벤트 반복 정보 주체 수정, 종료일을 지정 ID 날짜의 전날 23:59:59로 변경
			calendar_EventRepeatMaster = emc.find(repeatMasterId, Calendar_EventRepeatMaster.class);
			if (calendar_EventRepeatMaster == null) {
				throw new Exception(
						"calendar repeat master not exists, calendar can not save! MasterID:" + repeatMasterId);
			}
			// 이벤트 권한 보장: 캘린더 이벤트 반복 정보 주체 권한 설정, 최소한 본인이 생성한 캘린더는 본인이 관리·게시·열람 가능하도록 보장
			addStringToList(effectivePerson.getDistinguishedName(), calendar_EventRepeatMaster.getParticipants());
			addStringToList(effectivePerson.getDistinguishedName(),
					calendar_EventRepeatMaster.getManageablePersonList());
			addStringToList(effectivePerson.getDistinguishedName(), calendar_EventRepeatMaster.getViewablePersonList());
			if (StringUtils.isEmpty(calendar_EventRepeatMaster.getCreatePerson())) {
				calendar_EventRepeatMaster.setCreatePerson(effectivePerson.getDistinguishedName());
			}
			if (StringUtils.isEmpty(calendar_EventRepeatMaster.getUpdatePerson())) {
				calendar_EventRepeatMaster.setUpdatePerson(effectivePerson.getDistinguishedName());
			}
			// 이벤트 당월 및 이후 이미 생성된 모든 월 정보 레코드 삭제, 이후 재생성 용이
			if (ListTools.isNotEmpty(calendar_EventRepeatMaster.getCreatedMonthList())) {
				eventYearAndMonth = dateOperation.getYearAndMonth(calendar_event.getStartTime());
				if (StringUtils.isNotEmpty(eventYearAndMonth)) {
					eventMonth = dateOperation.format_year_month.parse(eventYearAndMonth);
					for (String yearAndMonth : calendar_EventRepeatMaster.getCreatedMonthList()) {
						// eventYearAndMonth 이전인지 판단, 이전이면 createdMonthList에 추가, 아니면 삭제하고 추가 안 함
						createdMonth = dateOperation.format_year_month.parse(yearAndMonth);
						if (eventMonth.getTime() >= createdMonth.getTime()) {
							createdMonthList.add(yearAndMonth);
						}
					}
				}
			}
			calendar_EventRepeatMaster.setCreatedMonthList(createdMonthList);
			// 종료일을 지정 ID 날짜의 전날 23:59:59로 변경
			Date repeatEndTime = dateOperation
					.getEndTimeInDay(dateOperation.getDayAddDate(calendar_event.getStartTime(), -1));
			RRule rule = new RRule(calendar_EventRepeatMaster.getRecurrenceRule());
			Recur recur = rule.getRecur();
			recur.setUntil(new net.fortuna.ical4j.model.Date(repeatEndTime));
			calendar_EventRepeatMaster.setRecurrenceRule(new RRule(recur).getValue());

			calendar_EventRepeatMaster_new = new Calendar_EventRepeatMaster();
			calendar_EventRepeatMaster_new.setRecurrenceStartTime(calendar_EventRepeatMaster.getRecurrenceStartTime());
			calendar_EventRepeatMaster_new.setStartTime(calendar_EventRepeatMaster.getStartTime());
			calendar_EventRepeatMaster_new = copyEventPropertyToMaster(calendar_event, calendar_EventRepeatMaster_new);
			calendar_EventRepeatMaster_new.setRecurrenceStartTime(calendar_event.getStartTime());

			// 새 calendar_EventRepeatMaster_new의 비고 정보 처리, 기존 calendar_EventRepeatMaster는 유지
			Calendar_EventComment calendar_EventComment = calendar_EventService.createNewEventComment(emc,
					calendar_event);
			if (calendar_EventComment != null) {
				calendar_EventRepeatMaster_new.setComment("{#CLOB#}");
				calendar_EventRepeatMaster_new.setCommentId(calendar_EventComment.getId());
			} else {
				calendar_EventRepeatMaster_new.setCommentId(null);
			}

			emc.beginTransaction(Calendar_EventRepeatMaster.class);
			emc.check(calendar_EventRepeatMaster, CheckPersistType.all);
			emc.persist(calendar_EventRepeatMaster_new, CheckPersistType.all);
			emc.commit();

		} catch (Exception e) {
			throw e;
		}
		return count;
	}

	/**
	 * 단일 캘린더 이벤트 레코드 정보 수정
	 * 
	 * @param calendar_event
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Calendar_Event updateSingleEvent(Calendar_Event calendar_event, EffectivePerson effectivePerson)
			throws Exception {

		// 캘린더 정보 권한 설정, 최소한 본인이 생성한 캘린더는 본인이 관리·게시·열람 가능
		addStringToList(effectivePerson.getDistinguishedName(), calendar_event.getParticipants());
		addStringToList(effectivePerson.getDistinguishedName(), calendar_event.getManageablePersonList());
		addStringToList(effectivePerson.getDistinguishedName(), calendar_event.getViewablePersonList());

		if (StringUtils.isEmpty(calendar_event.getCreatePerson())) {
			calendar_event.setCreatePerson(effectivePerson.getDistinguishedName());
		}
		if (StringUtils.isEmpty(calendar_event.getUpdatePerson())) {
			calendar_event.setUpdatePerson(effectivePerson.getDistinguishedName());
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Calendar_Event old_event = emc.find(calendar_event.getId(), Calendar_Event.class);
			if (old_event == null) {
				throw new Exception("calendar event is not exsits. ID:" + calendar_event.getId());
			}
			calendar_event.copyTo(old_event, JpaObject.FieldsUnmodify);
			if (StringUtils.isEmpty(calendar_event.getId())) {
				old_event.setId(Calendar_Event.createId());
			}
			// 일정 비고 정보 길이 확인, 너무 길면 새 Calendar_EventComment 레코드 생성 필요
			// 너무 길면 새 Calendar_EventComment 레코드 생성 필요
			Calendar_EventComment calendar_EventComment = calendar_EventService.createNewEventComment(emc,
					calendar_event);
			if (calendar_EventComment != null) {
				old_event.setComment("{#CLOB#}");
				old_event.setCommentId(calendar_EventComment.getId());
			} else {
				old_event.setCommentId(null);
			}

			emc.beginTransaction(Calendar_Event.class);
			if (StringUtils.isEmpty(old_event.getRepeatMasterId())) {
				// 기존에 반복 일정이 아니었으나 수정 후 반복 일정이면 반복 정보 주체로 저장
				if (StringUtils.isNotEmpty(calendar_event.getRecurrenceRule())) {
					Calendar_EventRepeatMaster calendar_EventRepeatMaster = composeEventRepeatMasterWithEvent(
							calendar_event);
					old_event.setRepeatMasterId(calendar_EventRepeatMaster.getId());
					emc.beginTransaction(Calendar_EventRepeatMaster.class);
					calendar_EventRepeatMasterService.create(emc, calendar_EventRepeatMaster, false);
				}
			} else {
				if (StringUtils.isEmpty(calendar_event.getRecurrenceRule())) {
					// 기존에 반복이었으나 이제 반복이 아니면 repeatMaster와 이미 생성된 모든 event 삭제 필요
					if (StringUtils.isNotEmpty(old_event.getRepeatMasterId())) {
						ArrayList arrayList = new ArrayList<>();
						arrayList.add(old_event.getId());
						calendar_EventRepeatMasterService.destoryWithMasterId(emc, old_event.getRepeatMasterId(),
								arrayList);
					}
				}
			}

			// 일정 정보 저장
			calendar_event = calendar_EventService.update(emc, old_event, false);
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
		return calendar_event;
	}

	/**
	 * 문자열을 List에 추가
	 * 
	 * @param distinguishedName
	 * @param manageablePersonList
	 */
	private void addStringToList(String distinguishedName, List<String> manageablePersonList) {
		if (manageablePersonList == null) {
			manageablePersonList = new ArrayList<>();
		}
		if (StringUtils.isNotEmpty(distinguishedName)) {
			if (!manageablePersonList.contains(distinguishedName)) {
				manageablePersonList.add(distinguishedName);
			}
		}
	}

	private Calendar_EventRepeatMaster composeEventRepeatMasterWithEvent(Calendar_Event calendar_event)
			throws Exception {
		Calendar_EventRepeatMaster calendar_EventRepeatMaster = new Calendar_EventRepeatMaster(
				Calendar_EventRepeatMaster.createId(), calendar_event.getTitle(), calendar_event.getStartTime(),
				calendar_event.getEndTime(), calendar_event.getRecurrenceRule());
		calendar_event.copyTo(calendar_EventRepeatMaster, JpaObject.FieldsUnmodify);
		calendar_EventRepeatMaster.setCalendarId(calendar_event.getCalendarId());
		return calendar_EventRepeatMaster;
	}

	/**
	 * 조건에 따라 지정 캘린더 레코드 정보 ID 목록 조회
	 * 
	 * @param key
	 * @param eventType
	 * @param source
	 * @param createPerson
	 * @param calendarIds
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithCondition(String key, String eventType, String source, String createPerson,
			List<String> calendarIds, String personName, List<String> unitNames, List<String> groupNames,
			Date startTime, Date endTime) throws Exception {
		if (startTime == null) {
			startTime = new Date();
		}
		if (endTime == null) {
			endTime = new Date();
		}
		if (startTime.after(endTime)) {
			// 위치 변경
			Date tmp = startTime;
			startTime = endTime;
			endTime = tmp;
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 두 시간 사이의 월을 구해, 해당 월에 캘린더 이벤트 생성이 필요한지 확인
			List<String> needCreateMonths = new DateOperation().listMonthsBetweenDate(startTime, endTime);

			if (ListTools.isNotEmpty(needCreateMonths)) {
				List<String> repeatMasterIds = new ArrayList<>();
				List<String> repeatMasterIds_month = null;

				for (String createMonth : needCreateMonths) {
					// 먼저 생성이 필요한 캘린더 반복 주체가 있는지 확인
					repeatMasterIds_month = calendar_EventRepeatMasterService.listNeedRepeatMaster(emc, calendarIds,
							eventType, createMonth, personName, unitNames, groupNames);
					if (ListTools.isNotEmpty(repeatMasterIds_month)) {
						repeatMasterIds.addAll(repeatMasterIds_month);
					}
				}
				if (ListTools.isNotEmpty(repeatMasterIds)) {
					// 날짜 범위에 따라 지정 반복 주체의 캘린더 이벤트 정보 생성
					calendar_EventService.createCalendarWithMaster(emc, repeatMasterIds, startTime, endTime,
							needCreateMonths);
				}
			}

			// 마지막으로 캘린더 이벤트 정보 테이블에서 조건에 따라 데이터 조회 후 반환
			return calendar_EventService.listWithCondition(emc, key, eventType, source, createPerson, calendarIds,
					personName, unitNames, groupNames, startTime, endTime);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 조건에 따라 지정 캘린더 레코드 정보 ID 목록 조회
	 * 
	 * @param inFilterCalendarIds
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithCondition(List<String> inFilterCalendarIds, String personName, List<String> unitNames,
			List<String> groupNames, Date startTime, Date endTime) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_EventService.listWithCondition(emc, null, null, null, null, inFilterCalendarIds, personName,
					unitNames, groupNames, startTime, endTime);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 반복 주체 및 날짜 범위에 따라 지정 캘린더 레코드 정보 ID 목록 조회
	 * 
	 * @param repeatMasterId
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithRepeatMaster(String repeatMasterId, Date startTime, Date endTime) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_EventService.listWithRepeatMaster(emc, repeatMasterId, startTime, endTime);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * ID로 지정 캘린더 레코드 정보 영구 삭제
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void destory(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			calendar_EventService.destory(emc, id);
		} catch (Exception e) {
			throw e;
		}
	}

	public void destory(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			calendar_EventService.destory(emc, ids);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 반복 일정 이벤트 중 특정 이벤트부터 이후의 모든 일정 이벤트 정보 삭제(반드시 반복 일정)
	 * 
	 * 1. 현재 이벤트 및 해당 반복 이벤트 주체 정보로 이미 생성된, 해당 ID 이벤트 이후 시간의 모든 생성된 일정 이벤트 정보 삭제
	 * 2. 일정 이벤트 반복 정보 주체 수정, 종료일을 지정 ID 날짜의 전날 23:59:59로 변경
	 * 
	 * @param repeatMasterId
	 * @param calendar_event
	 * @return
	 * @throws Exception
	 */
	public Integer destoryAfterEventId(String repeatMasterId, Calendar_Event calendar_event) throws Exception {

		if (StringUtils.isEmpty(repeatMasterId)) {
			throw new Exception("calendar repeat master id is empty, calendar can not delete!");
		}

		if (calendar_event == null) {
			throw new Exception("calendar event is null, calendar can not delete!");
		}

		Integer count = 0;
		Business business = null;
		List<String> eventIds = null;
		List<Calendar_Event> calendarEvents = null;
		Calendar_EventRepeatMaster calendar_EventRepeatMaster = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			calendar_EventRepeatMaster = emc.find(calendar_event.getRepeatMasterId(), Calendar_EventRepeatMaster.class);

			// -----------------------------------------------------------------------------------------------------------------
			// 1. 현재 이벤트 및 해당 반복 이벤트 주체 정보로 이미 생성된, 해당 ID 이벤트 이후 시간의 모든 생성된 일정 이벤트 정보 삭제
			eventIds = business.calendar_EventFactory().listWithRepeatMaster(repeatMasterId,
					calendar_event.getStartTime(), null);
			if (ListTools.isNotEmpty(eventIds)) {
				count = eventIds.size();
				calendarEvents = business.calendar_EventFactory().list(eventIds);
			}
			if (ListTools.isNotEmpty(calendarEvents)) {
				emc.beginTransaction(Calendar_Event.class);
				for (Calendar_Event calendar_Event : calendarEvents) {
					emc.remove(calendar_Event, CheckRemoveType.all);
				}
			}

			// 종료일을 지정 ID 날짜의 전날 23:59:59로 변경
			Date repeatEndTime = dateOperation
					.getEndTimeInDay(dateOperation.getDayAddDate(calendar_event.getStartTime(), -1));
			RRule rule = new RRule(calendar_EventRepeatMaster.getRecurrenceRule());
			Recur recur = rule.getRecur();
			recur.setUntil(new net.fortuna.ical4j.model.Date(repeatEndTime));
			calendar_EventRepeatMaster.setRecurrenceRule(new RRule(recur).getValue());

			emc.beginTransaction(Calendar_EventRepeatMaster.class);
			emc.check(calendar_EventRepeatMaster, CheckPersistType.all);
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
		return count;
	}

	/**
	 * 일정 이벤트 객체를 iCal4j의 Calendar 객체로 변환
	 * 
	 * @param o2_calendar_event
	 * @return
	 */
	public Calendar parseToiCal(Calendar_Event o2_calendar_event) {
		// 캘린더 생성
		Calendar calendar = null;
		try {
			calendar = new Calendar();
			calendar.getProperties().add(new ProdId("-//O2OA//iCal4j 1.0//EN"));
			calendar.getProperties().add(Version.VERSION_2_0);
			calendar.getProperties().add(CalScale.GREGORIAN);

			// 이벤트 제목
			String summary = null;
			if (StringUtils.isNotEmpty(o2_calendar_event.getTitle())) {
				summary = o2_calendar_event.getTitle();
			} else {
				summary = "제목 없는 일정";
			}

			if (o2_calendar_event.getStartTime() == null) {
				throw new Exception("event start time is null!");
			}

			if (o2_calendar_event.getEndTime() == null) {
				throw new Exception("event end time is null!");
			}

			// 시작 시간
			DateTime start = new DateTime(o2_calendar_event.getStartTime().getTime());
			// 시작 시간을 UTC 시간으로 변환(UTC + 시차 = 현지 시간)
			start.setUtc(true);
			// 종료 시간
			DateTime end = new DateTime(o2_calendar_event.getEndTime().getTime());
			// 종료 시간을 UTC 시간으로 설정(UTC + 시차 = 현지 시간)
			end.setUtc(true);

			VEvent event = new VEvent(start, end, summary);
			if (StringUtils.isNotEmpty(o2_calendar_event.getLocationName())) {
				event.getProperties().add(new Location(o2_calendar_event.getLocationName()));
			}

			// 고유 식별자 생성
			event.getProperties().add(new Uid(o2_calendar_event.getId()));

			// 시간대(TimeZone) 생성
			TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
			TimeZone timezone = registry.getTimeZone("Asia/Shanghai");
			VTimeZone tz = timezone.getVTimeZone();
			// 시간대 정보 추가
			event.getProperties().add(tz.getTimeZoneId());

			// 비고 정보 추가
			if (StringUtils.isNotEmpty(o2_calendar_event.getComment())) {
				Comment comment = new Comment(o2_calendar_event.getComment());
				event.getProperties().add(comment);
			}

			// 참여자 추가
			if (ListTools.isNotEmpty(o2_calendar_event.getParticipants())) {
				Attendee attendee = null;
				for (String participant : o2_calendar_event.getParticipants()) {
					attendee = new Attendee();
					attendee.getParameters().add(Role.OPT_PARTICIPANT);
					attendee.getParameters().add(new Cn(participant));
					event.getProperties().add(attendee);
				}
			}

			// 반복 이벤트 START------------------------------------------------------------
			if (StringUtils.isNotEmpty(o2_calendar_event.getRecurrenceRule())) {
				RRule rule = new RRule(o2_calendar_event.getRecurrenceRule());
				event.getProperties().add(rule);
			}
			// 반복 이벤트 END------------------------------------------------------------

			// 알림, 10분 전
			if (StringUtils.isNotEmpty(o2_calendar_event.getValarmTime_config())) {
				String[] time_config = o2_calendar_event.getValarmTime_config().split(",");
				VAlarm valarm = null;
				if (time_config != null && time_config.length == 4) {
					int day = Integer.parseInt(time_config[0]);
					int hour = Integer.parseInt(time_config[1]);
					int min = Integer.parseInt(time_config[2]);
					int sec = Integer.parseInt(time_config[3]);

					// P2DT3H4M5S"
					valarm = new VAlarm(Duration.parse("P" + day + "DT" + hour + "H" + min + "M" + sec + "S"));
					// valarm = new VAlarm(new Dur(day, hour, min, sec));
					if (StringUtils.isNotEmpty(o2_calendar_event.getValarm_Summary())) {
						valarm.getProperties().add(new Summary(o2_calendar_event.getValarm_Summary()));
					} else {
						valarm.getProperties().add(new Summary("일정 알림"));
					}
					valarm.getProperties().add(Action.AUDIO);
					valarm.getProperties().add(Action.DISPLAY);
					// valarm.getProperties().add(Action.EMAIL);
					if (StringUtils.isNotEmpty(o2_calendar_event.getValarm_description())) {
						valarm.getProperties().add(new Description(o2_calendar_event.getValarm_description()));
					}
					// VAlarm을 VEvent에 추가
					event.getAlarms().add(valarm);
				}
			}
			// 이벤트 추가
			calendar.getComponents().add(event);
			// 검증
			calendar.validate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return calendar;
	}

	/**
	 * 캘린더가 ICAL 요구 사항에 맞는지 검증
	 * 
	 * @param o2_calendar_event
	 * @throws ValidationException
	 * @throws IOException
	 */
	public Boolean validateCalendar(Calendar_Event o2_calendar_event) throws ValidationException, IOException {
		Calendar calendar = parseToiCal(o2_calendar_event);
		calendar.validate();
		return true;
	}

	/**
	 * 일정 이벤트를 ical 파일로 작성
	 * 
	 * @param o2_calendar_event
	 * @param path
	 * @throws ValidationException
	 * @throws IOException
	 */
//	public void writeiCal(Calendar_Event o2_calendar_event, String path) throws ValidationException, IOException {
//		Calendar calendar = parseToiCal(o2_calendar_event);
//		FileOutputStream fout = new FileOutputStream("D://2.ics");
//		CalendarOutputter outputter = new CalendarOutputter();
//		outputter.output(calendar, fout);
//	}

	/**
	 * 일정 이벤트를 ical 파일로 작성
	 * 
	 * @param o2_calendar_event
	 * @param out
	 * @throws ValidationException
	 * @throws IOException
	 */
	public void wirteiCal(Calendar_Event o2_calendar_event, OutputStream out) throws ValidationException, IOException {
		Calendar calendar = parseToiCal(o2_calendar_event);
		CalendarOutputter outputter = new CalendarOutputter();
		outputter.output(calendar, out);
	}

	/**
	 * 일정 이벤트를 ICAL로 변환한 내용 정보 조회
	 * 
	 * @param o2_calendar_event
	 * @throws ValidationException
	 * @throws IOException
	 */
	public String getiCalContent(Calendar_Event o2_calendar_event) throws ValidationException, IOException {
		Calendar calendar = parseToiCal(o2_calendar_event);
		CalendarOutputter calendarOutputter = new CalendarOutputter(false);
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			calendarOutputter.output(calendar, baos);
			return new String(baos.toByteArray(), "utf-8");
		}
	}

	/**
	 * 알림이 필요한 일정 이벤트 ID 목록 조회
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public List<String> listNeedAlarmEventIds(Date date) throws Exception {
		if (date == null) {
			date = new Date();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_EventService.listNeedAlarmEventIds(emc, date);
		}
	}

	/**
	 * calendar_event 정보로 새 반복 주체 갱신
	 * 
	 * @param calendar_event
	 * @return
	 */
	private Calendar_EventRepeatMaster copyEventPropertyToMaster(Calendar_Event calendar_event,
			Calendar_EventRepeatMaster calendar_EventRepeatMaster) {
		if (calendar_event != null && calendar_EventRepeatMaster != null) {
			Date recurrenceStartTime = calendar_EventRepeatMaster.getRecurrenceStartTime();
			Date startTime = calendar_EventRepeatMaster.getStartTime();

			Date startTime_event = calendar_event.getStartTime();
			Date recurrenceStartTime_new = null;
			Date startTime_new = null;

			// 시작 시간의 시분초 수정
			String newRecurrenceStartTime_str = dateOperation.getDate(recurrenceStartTime, "yyyy-MM-dd") + " "
					+ dateOperation.getDate(startTime_event, "HH:mm:ss");
			String newStartTime_str = dateOperation.getDate(startTime_event, "yyyy-MM-dd") + " "
					+ dateOperation.getDate(startTime_event, "HH:mm:ss");
			try {
				recurrenceStartTime_new = dateOperation.getDateFromString(newRecurrenceStartTime_str,
						"yyyy-MM-dd HH:mm:ss");
			} catch (Exception e) {
				recurrenceStartTime_new = recurrenceStartTime;
				e.printStackTrace();
			}
			try {
				startTime_new = dateOperation.getDateFromString(newStartTime_str, "yyyy-MM-dd HH:mm:ss");
			} catch (Exception e) {
				startTime_new = startTime;
				e.printStackTrace();
			}
			calendar_EventRepeatMaster.setAlarm(calendar_event.getAlarm());
			calendar_EventRepeatMaster.setAlarmTime(calendar_event.getAlarmTime());
			calendar_EventRepeatMaster.setCalendarId(calendar_event.getCalendarId());
			calendar_EventRepeatMaster.setComment(calendar_event.getComment());
			calendar_EventRepeatMaster.setCreatePerson(calendar_event.getCreatePerson());
			calendar_EventRepeatMaster.setDaysOfDuration(calendar_event.getDaysOfDuration());
			calendar_EventRepeatMaster.setEventType(calendar_event.getEventType());
			calendar_EventRepeatMaster.setIsAllDayEvent(calendar_event.getIsAllDayEvent());
			calendar_EventRepeatMaster.setIsPublic(calendar_event.getIsPublic());
			calendar_EventRepeatMaster.setLatitude(calendar_event.getLatitude());
			calendar_EventRepeatMaster.setLocationName(calendar_event.getLocationName());
			calendar_EventRepeatMaster.setLongitude(calendar_event.getLongitude());
			calendar_EventRepeatMaster.setManageablePersonList(calendar_event.getManageablePersonList());
			calendar_EventRepeatMaster.setParticipants(calendar_event.getParticipants());
			calendar_EventRepeatMaster.setRecurrenceExc(calendar_event.getRecurrenceExc());
			calendar_EventRepeatMaster.setRecurrenceRule(calendar_event.getRecurrenceRule());
			calendar_EventRepeatMaster.setRepeatStatus("생성 대기");
			calendar_EventRepeatMaster.setSource(calendar_event.getSource());
			calendar_EventRepeatMaster.setTargetType(calendar_event.getTargetType());
			calendar_EventRepeatMaster.setTitle(calendar_event.getTitle());
			calendar_EventRepeatMaster.setUpdatePerson(calendar_event.getUpdatePerson());
			calendar_EventRepeatMaster.setValarm_description(calendar_event.getValarm_description());
			calendar_EventRepeatMaster.setValarm_mailto(calendar_event.getValarm_mailto());
			calendar_EventRepeatMaster.setValarm_Summary(calendar_event.getValarm_Summary());
			calendar_EventRepeatMaster.setValarmTime_config(calendar_event.getValarmTime_config());
			calendar_EventRepeatMaster.setViewableGroupList(calendar_event.getViewableGroupList());
			calendar_EventRepeatMaster.setViewableUnitList(calendar_event.getViewableUnitList());
			calendar_EventRepeatMaster.setColor(calendar_event.getColor());
			calendar_EventRepeatMaster.setRecurrenceStartTime(recurrenceStartTime_new);
			calendar_EventRepeatMaster.setStartTime(startTime_new);
			calendar_EventRepeatMaster.setEndTime(calendar_event.getEndTime());
		}
		return calendar_EventRepeatMaster;
	}

	public List<String> destoryWithBundle(String bundle) throws Exception {
		if (StringUtils.isEmpty(bundle)) {
			throw new Exception("bundle is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_EventService.destoryWithBundle(emc, bundle);
		}  
	}

	/**
	 * commentId로 Calendar_EventComment 정보 조회
	 * 
	 * @param commentId
	 * @return
	 * @throws Exception
	 */
	public Calendar_EventComment getCommentWithCommentId(String commentId) throws Exception {
		if (StringUtils.isEmpty(commentId)) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(commentId, Calendar_EventComment.class);
		}
	}
}
