package com.x.calendar.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.Business;
import com.x.calendar.common.date.DateOperation;
import com.x.calendar.core.entity.Calendar_Event;
import com.x.calendar.core.entity.Calendar_EventRepeatMaster;


/**
 * 캘린더 반복 정보 주체 레코드 정보 서비스 클래스
 *
 */
public class Calendar_EventRepeatMasterService {

	/**
	 * ID로 지정 캘린더 반복 정보 주체 레코드 정보 영구 삭제
	 * @param emc
	 * @param repeatMasterId
	 * @param excludeIds 제외할 EventID 목록
	 * @throws Exception
	 */
	public void destoryWithMasterId( EntityManagerContainer emc, String repeatMasterId, List<String> excludeIds ) throws Exception {
		Calendar_EventRepeatMaster calendar_EventRepeatMaster = null;
		if( StringUtils.isEmpty( repeatMasterId ) ){
			throw new Exception( "repeatMasterId is empty, system can not delete any object." );
		}
		List<String> eventIds = null;
		Business business =  new Business( emc );
		Calendar_Event calendar_Event = null;
		calendar_EventRepeatMaster = emc.find( repeatMasterId, Calendar_EventRepeatMaster.class );
		eventIds = business.calendar_EventFactory().listWithRepeatMaster(repeatMasterId, null, null );
		emc.beginTransaction( Calendar_Event.class );
		emc.beginTransaction( Calendar_EventRepeatMaster.class );
		if( ListTools.isNotEmpty( eventIds )) {
			for( String id : eventIds ) {
				if( !ListTools.contains( excludeIds, id ) ) {
					calendar_Event = emc.find( id, Calendar_Event.class );
					if( calendar_Event != null) {
						emc.remove( calendar_Event, CheckRemoveType.all );
					}
				}
			}
		}
		if( calendar_EventRepeatMaster != null  ) {			
			emc.remove( calendar_EventRepeatMaster, CheckRemoveType.all );
		}
		emc.commit();
	}

	/**
	 * 캘린더 반복 정보 주체 레코드 정보 생성
	 * @param emc
	 * @param calendar_EventRepeatMaster
	 * @param autoTransaction
	 * @return
	 * @throws Exception
	 */
	public Calendar_EventRepeatMaster create( EntityManagerContainer emc, 
			Calendar_EventRepeatMaster calendar_EventRepeatMaster,
			Boolean autoTransaction ) throws Exception {
		if( autoTransaction == null ) {
			autoTransaction = true;
		}
		Calendar_EventRepeatMaster calendar_EventRepeatMaster_old = null;
		calendar_EventRepeatMaster_old = emc.find( calendar_EventRepeatMaster.getId(), Calendar_EventRepeatMaster.class );
		if( calendar_EventRepeatMaster_old != null ){
			throw new Exception("calendar_EventRepeatMaster{'id':' "+ calendar_EventRepeatMaster.getId() +" '} exists, can not create new object");
		}else{
			if(autoTransaction) {
				emc.beginTransaction( Calendar_EventRepeatMaster.class );
			}
			if( StringUtils.isEmpty( calendar_EventRepeatMaster.getId() )) {
				calendar_EventRepeatMaster.setId( Calendar_Event.createId() );
			}
			emc.persist( calendar_EventRepeatMaster, CheckPersistType.all);
			if(autoTransaction) {
				emc.commit();
			}
		}
		return calendar_EventRepeatMaster;
	}

	/**
	 * 캘린더 반복 정보 주체 레코드 정보 수정
	 * @param emc
	 * @param calendar_EventRepeatMaster
	 * @param autoTransaction
	 * @return
	 * @throws Exception
	 */
	public Calendar_EventRepeatMaster update( EntityManagerContainer emc, 
			Calendar_EventRepeatMaster calendar_EventRepeatMaster, Boolean autoTransaction ) throws Exception {
		if( autoTransaction == null ) {
			autoTransaction = true;
		}
		if( calendar_EventRepeatMaster == null ){
			throw new Exception("calendar_EventRepeatMaster is null, can not update object!");
		}
		Calendar_EventRepeatMaster calendar_EventRepeatMaster_old = null;
		Business business = new Business(emc);
		calendar_EventRepeatMaster_old = business.calendar_EventRepeatMasterFactory().get( calendar_EventRepeatMaster.getId() );
		if( calendar_EventRepeatMaster_old != null ){
			if(autoTransaction) {
				emc.beginTransaction( Calendar_EventRepeatMaster.class );
			}
			calendar_EventRepeatMaster.copyTo(calendar_EventRepeatMaster_old, JpaObject.FieldsUnmodify);
			emc.check( calendar_EventRepeatMaster_old, CheckPersistType.all);
			if(autoTransaction) {
				emc.commit();
			}
		}else{
			throw new Exception("old object calendar_EventRepeatMaster{'id':' "+ calendar_EventRepeatMaster.getId() +" '} is not exists. ");
		}
		return calendar_EventRepeatMaster_old;
	}

	/**
	 * 조건으로 지정한 캘린더 정보 ID 목록 조회
	 * @param emc
	 * @param title
	 * @param type
	 * @param source
	 * @param createor
	 * @param inFilterCalendarIds
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithCondition( EntityManagerContainer emc,  String title, String type, String source, String createor,
			 List<String> inFilterCalendarIds, String personName, List<String> unitNames, List<String> groupNames, Date startTime, Date endTime
			 ) throws Exception {
		Business business =  new Business( emc );
		return business.calendar_EventRepeatMasterFactory().listWithCondition(title, type, source, createor, inFilterCalendarIds, personName, 
				unitNames, groupNames, startTime, endTime);
	}
	

	public List<Calendar_EventRepeatMaster> list(EntityManagerContainer emc,  List<String> ids) throws Exception {
		Business business =  new Business( emc );
		return business.calendar_EventRepeatMasterFactory().list(ids);
	}

	/**
	 * 해당 repeatMaster 아래 모든 레코드가 삭제되었는지 확인, 유효한 레코드가 없으면 모두 삭제
	 * @param emc
	 * @param repeatMasterId
	 * @throws Exception 
	 */
	public void checkRepeatMaster( EntityManagerContainer emc, String repeatMasterId ) throws Exception {
		Business business =  new Business( emc );
		long count = business.calendar_EventFactory().countWithRepeatMaster( repeatMasterId );
		if( count == 0 ) {
			//repeatMaster 삭제
			Calendar_EventRepeatMaster calendar_EventRepeatMaster = emc.find( repeatMasterId, Calendar_EventRepeatMaster.class );
			emc.beginTransaction( Calendar_EventRepeatMaster.class );
			emc.remove( calendar_EventRepeatMaster, CheckRemoveType.all );
			emc.commit(); 
		}
	}

	/**
	 * 조건과 시간 범위에 따라 이벤트 생성이 필요한 반복 주체 ID 목록 조회,  시간은 이미 생성된 날짜로 판단
	 * @param emc
	 * @param calendarIds
	 * @param eventType
	 * @param startTime
	 * @param endTime
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception 
	 */
	public List<String> listNeedRepeatMaster( EntityManagerContainer emc, List<String> calendarIds, String eventType,
			Date startTime, Date endTime, String personName, List<String> unitNames, List<String> groupNames) throws Exception {
		Business business =  new Business( emc );
		List<String> result = new ArrayList<>();
		List<String> ids = null;
		//두 시간 사이의 월을 구해, 해당 월에 캘린더 이벤트 생성이 필요한지 확인
		List<String> needCreateMonths = new DateOperation().listMonthsBetweenDate(startTime, endTime);
		if( ListTools.isNotEmpty( needCreateMonths ) ) {
			for(String createMonth : needCreateMonths) {
				ids = business.calendar_EventRepeatMasterFactory().listNeedRepeatMaster( calendarIds, eventType,
						createMonth, personName, unitNames,  groupNames);
				if( ListTools.isNotEmpty( ids )) {
					for( String id : ids ) {
						if( !result.contains( id )) {
							result.add( id );
						}
					}
				}
			}
		}		
		return result;
	}

	/**
	 * 조건과 시간 범위에 따라 이벤트 생성이 필요한 반복 주체 ID 목록 조회,  시간은 이미 생성된 날짜로 판단
	 * @param emc
	 * @param calendarIds
	 * @param eventType
	 * @param createMonth
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	public List<String> listNeedRepeatMaster( EntityManagerContainer emc, List<String> calendarIds, String eventType,
			String createMonth, String personName, List<String> unitNames, List<String> groupNames) throws Exception {
		Business business =  new Business( emc );		
		return business.calendar_EventRepeatMasterFactory().listNeedRepeatMaster( calendarIds, eventType,
				createMonth, personName, unitNames,  groupNames);
	}
}
