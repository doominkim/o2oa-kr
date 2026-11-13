package com.x.calendar.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.Business;
import com.x.calendar.core.entity.Calendar;
import com.x.calendar.core.entity.Calendar_Event;
import com.x.calendar.core.entity.Calendar_EventRepeatMaster;


/**
 * 캘린더 계정 정보 서비스 클래스
 * @author O2LEE
 *
 */
public class CalendarService {

	/**
	 * 모든 캘린더 계정 정보 조회
	 * @return
	 * @throws Exception
	 */
	public List<Calendar> listAll( EntityManagerContainer emc ) throws Exception {
		Business business =  new Business( emc );
		return business.calendarFactory().listAll();
	}

	/**
	 * ID로 지정 캘린더 계정 정보 삭제
	 * @param id
	 * @throws Exception
	 */
	public void destory( EntityManagerContainer emc, String id ) throws Exception {
		Calendar calendar = null;
		if( StringUtils.isEmpty( id ) ){
			throw new Exception( "id is empty, system can not delete any object." );
		}
		calendar = emc.find( id, Calendar.class );
		if ( null == calendar ) {
			throw new Exception( "object is not exist {'id':'"+ id +"'}" );
		}else{
			List<String> ids = null;
			Business business = new Business(emc);
			emc.beginTransaction( Calendar.class );
			emc.beginTransaction( Calendar_Event.class );
			emc.beginTransaction( Calendar_EventRepeatMaster.class );
			
			//모든 캘린더 레코드 정보 Calendar_Event 삭제
			List<Calendar_Event> eventList = null;
			ids = business.calendar_EventFactory().listWithCalendarId(id);
			if( ListTools.isNotEmpty( ids )) {
				eventList = business.calendar_EventFactory().list(ids);
				if( ListTools.isNotEmpty( eventList )) {
					for( Calendar_Event event : eventList ) {
						emc.remove( event, CheckRemoveType.all );
					}
				}
			}
			
			//모든 반복 주체 정보 Calendar_EventRepeatMaster 삭제
			List<Calendar_EventRepeatMaster> repeatedMasterList = null;
			ids = business.calendar_EventRepeatMasterFactory().listWithCalendarId(id);
			if( ListTools.isNotEmpty( ids )) {
				repeatedMasterList = business.calendar_EventRepeatMasterFactory().list(ids);
				if( ListTools.isNotEmpty( repeatedMasterList )) {
					for( Calendar_EventRepeatMaster repeatedMaster : repeatedMasterList ) {
						emc.remove( repeatedMaster, CheckRemoveType.all );
					}
				}
			}
			
			//캘린더 정보 삭제
			emc.remove( calendar, CheckRemoveType.all );
			emc.commit();
		}
	}

	/**
	 * 캘린더 계정 정보 생성
	 * @param calendar
	 * @return
	 * @throws Exception
	 */
	public Calendar create( EntityManagerContainer emc, Calendar calendar ) throws Exception {
		Calendar calendar_old = null;
		calendar_old = emc.find( calendar.getId(), Calendar.class );
		if( calendar_old != null ){
			throw new Exception("calendar{'id':' "+ calendar.getId() +" '} exists, can not create new object");
		}else{
			emc.beginTransaction( Calendar.class );
			emc.persist( calendar, CheckPersistType.all);
			emc.commit();
		}
		return calendar;
	}
	
	/**
	 * 캘린더 계정 정보 수정
	 * @param calendar
	 * @return
	 * @throws Exception
	 */
	public Calendar update( EntityManagerContainer emc, Calendar calendar ) throws Exception {
		if( calendar == null ){
			throw new Exception("calendar is null, can not update object!");
		}
		Calendar calendar_old = null;
		Business business = new Business(emc);
		calendar_old = business.calendarFactory().get( calendar.getId() );
		if( calendar_old != null ){
			emc.beginTransaction( Calendar.class );
			calendar_old.setName( calendar.getName() );
			calendar_old.setColor( calendar.getColor() );
			calendar_old.setCreateor( calendar.getCreateor() );
			calendar_old.setManageablePersonList( calendar.getManageablePersonList() );
			calendar_old.setPublishableGroupList( calendar.getPublishableGroupList() );
			calendar_old.setPublishablePersonList( calendar.getPublishablePersonList() );
			calendar_old.setPublishableUnitList( calendar.getPublishableUnitList() );
			calendar_old.setViewableGroupList( calendar.getViewableGroupList() );
			calendar_old.setViewablePersonList( calendar.getViewablePersonList() );
			calendar_old.setViewableUnitList( calendar.getViewableUnitList() );
			calendar_old.setTarget( calendar.getTarget() );
			calendar_old.setDescription( calendar.getDescription() );
			calendar_old.setIsPublic( calendar.getIsPublic() );
			calendar_old.setStatus( calendar.getStatus() );
			emc.check( calendar_old, CheckPersistType.all);
			emc.commit();
		}else{
			throw new Exception("old object calendar{'id':' "+ calendar.getId() +" '} is not exists. ");
		}
		return calendar_old;
	}

	/**
	 * 사용자가 생성한 캘린더 조회
	 * @param emc
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listMyCalenders( EntityManagerContainer emc, String personName ) throws Exception {
		Business business =  new Business( emc );
		return business.calendarFactory().listMyCalender( personName );
	}
	
	/**
	 * 조건으로 지정한 캘린더 정보 ID 목록 조회
	 * @param emc
	 * @param name
	 * @param type
	 * @param source
	 * @param createor
	 * @param inFilterCalendarIds
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithCondition( EntityManagerContainer emc,  String name, String type, String source, String createor,
			 List<String> inFilterCalendarIds, String personName, List<String> unitNames, List<String> groupNames ) throws Exception {
		Business business =  new Business( emc );

		//동기화, 프론트엔드 연속 호출로 개인 캘린더가 여러 개 생기는 것 방지
		List<String> ids = business.calendarFactory().listMyCalender( personName );
		if( ListTools.isEmpty( ids ) ) {
			//본인 기본 캘린더 생성
			Calendar calendar = new Calendar();
			calendar.setSource( "PERSON" );
			calendar.setCreateor( "SYSTEM" );
			calendar.setColor( "#1462be" );
			calendar.setDescription( "내 기본 캘린더" );
			calendar.setId( Calendar.createId() );
			calendar.setIsPublic( false );
			calendar.setName( "내 캘린더" );
			calendar.setSource("PERSON");
			calendar.setType("PERSON");
			calendar.setTarget( personName );
			calendar.setManageablePersonList( new ArrayList<>() );
			calendar.setPublishablePersonList( new ArrayList<>() );
			calendar.setViewablePersonList( new ArrayList<>() );

			addStringToList( personName, calendar.getManageablePersonList() );
			addStringToList( personName, calendar.getPublishablePersonList() );
			addStringToList( personName, calendar.getViewablePersonList() );

			queryForAddNewPersonalDefaultCalendar(emc, personName,calendar );

		}
		return business.calendarFactory().listWithCondition( name, type, source, createor, inFilterCalendarIds, personName, unitNames, groupNames );
	}

	/**
	 * 동기화 메서드, 프론트엔드 연속 호출로 개인 캘린더 정보가 여러 개 생기는 것 방지
	 * @param emc
	 * @param personName
	 * @param calendar
	 * @throws Exception
	 */
	private synchronized void queryForAddNewPersonalDefaultCalendar(EntityManagerContainer emc, String personName, Calendar calendar) throws Exception {
		Business business =  new Business( emc );
		List<String> ids = business.calendarFactory().listMyCalender( personName );
		if( ListTools.isEmpty( ids )){
			emc.beginTransaction( Calendar.class );
			emc.persist( calendar, CheckPersistType.all );
			emc.commit();
		}
	}

	/**
	 * 문자열을 List에 추가
	 * @param distinguishedName
	 * @param manageablePersonList
	 */
	private void addStringToList(String distinguishedName, List<String> manageablePersonList) {
		if( manageablePersonList == null ) {
			manageablePersonList = new ArrayList<>();
		}
		if( StringUtils.isNotEmpty( distinguishedName ) ) {
			if( !manageablePersonList.contains( distinguishedName )) {
				manageablePersonList.add( distinguishedName );
			}
		}
	}
	
	public List<Calendar> list(EntityManagerContainer emc,  List<String> ids) throws Exception {
		Business business =  new Business( emc );
		return business.calendarFactory().list(ids);
	}

	public List<String> listPublicCalendar(EntityManagerContainer emc) throws Exception {
		Business business =  new Business( emc );
		return business.calendarFactory().listPublicCalendar();
	}

	/**
	 * 캘린더 구독
	 * @param emc
	 * @param distinguishedName
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public Boolean follow(EntityManagerContainer emc, String followerName, String id) throws Exception {
		Calendar calendar = emc.find( id, Calendar.class );
		if( calendar != null ) {
			emc.beginTransaction( Calendar.class );
			calendar.addFollower(followerName);
			emc.check( calendar, CheckPersistType.all );
			emc.commit();
			return true;
		}
		return false;
	}
	
	/**
	 * 캘린더 구독 취소
	 * @param emc
	 * @param distinguishedName
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public Boolean followCancel(EntityManagerContainer emc, String followerName, String id) throws Exception {
		List<String> followerNames = new ArrayList<>();
		Calendar calendar = emc.find( id, Calendar.class );
		if( calendar != null ) {
			emc.beginTransaction( Calendar.class );
			if( ListTools.isNotEmpty( calendar.getFollowers() )) {
				for( String _followerName : calendar.getFollowers() ) {
					if( !followerName.equals( _followerName )) {
						followerNames.add( _followerName );
					}
				}
			}
			calendar.setFollowers( followerNames );
			emc.check( calendar, CheckPersistType.all );
			emc.commit();
			return true;
		}
		return false;
	}
}
