package com.x.calendar.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.Business;
import com.x.calendar.core.entity.Calendar;


/**
 * 일정 캘린더 정보 서비스 클래스
 * @author O2LEE
 *
 */
public class CalendarServiceAdv{

	private CalendarService calendarInfoService = new CalendarService();
	
	/**
	 * 사용자가 접근 가능한 모든 캘린더 정보 조회
	 * @return
	 * @throws Exception
	 */
	public List<Calendar> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendarInfoService.listAll( emc );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<Calendar> list(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendarInfoService.list(emc, ids);
		} catch ( Exception e ) {
			throw e;
		}
	}	
	
	/**
	 * ID로 지정 캘린더 정보 조회
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Calendar get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(id, Calendar.class);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 캘린더 정보 저장
	 * @param calendar
	 * @param effectivePerson 
	 * @return
	 * @throws Exception
	 */
	public Calendar save( Calendar calendar, EffectivePerson effectivePerson ) throws Exception {
		Calendar calendar_old = null;
		Business business = null;
		
		if( StringUtils.isEmpty( calendar.getCreateor() )) {
			calendar.setCreateor( effectivePerson.getDistinguishedName() );
		}
		
		//관리자는 모두 열람 권한이 있어야 함
		if( ListTools.isNotEmpty( calendar.getManageablePersonList() )) {
			for( String managerName : calendar.getManageablePersonList() ) {
				addStringToList( managerName, effectivePerson, calendar.getViewablePersonList() );
			}
		}
		
		//캘린더 정보 권한 설정, 최소한 본인이 생성한 캘린더는 본인이 관리·게시·열람 가능
		addStringToList( calendar.getCreateor(), effectivePerson, calendar.getManageablePersonList() );
		addStringToList( calendar.getCreateor(), effectivePerson, calendar.getPublishablePersonList() );
		addStringToList( calendar.getCreateor(), effectivePerson, calendar.getViewablePersonList() );
		
		if( "UNIT".equals( calendar.getType() )) {
			if( ListTools.isEmpty(calendar.getViewableUnitList() )
				&& ListTools.isEmpty(calendar.getViewableGroupList() )
				&& ListTools.isEmpty(calendar.getViewablePersonList() )){
				addStringToList( calendar.getTarget(), effectivePerson, calendar.getViewableUnitList() );
			}
		}else {
			addStringToList( calendar.getTarget(), effectivePerson, calendar.getViewablePersonList() );
		}		
		
		if( StringUtils.isEmpty( calendar.getColor() )) {
			calendar.setColor( "#1462be" );
		}
		if( StringUtils.isEmpty( calendar.getType() )) {
			calendar.setType("PERSON");
		}else {
			calendar.setType( calendar.getType().toUpperCase() );
		}
		if( StringUtils.isEmpty( calendar.getSource() )) {
			calendar.setSource("PERSON");
		}else {
			calendar.setSource( calendar.getSource().toUpperCase() );
		}
		if( "OPEN".equalsIgnoreCase( calendar.getStatus() ) || StringUtils.isEmpty(  calendar.getStatus() )) {
			calendar.setStatus( "OPEN" );
		}else {
			calendar.setStatus( "CLOSE" );
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			calendar_old = business.calendarFactory().get( calendar.getId() );
			if( calendar_old != null ){
				calendar.setId( calendar_old.getId() );
				return calendarInfoService.update( emc, calendar );	
			}else{
				return calendarInfoService.create( emc, calendar );	
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 문자열을 List에 추가
	 * @param distinguishedName
	 * @param manageablePersonList
	 */
	private void addStringToList(String distinguishedName, EffectivePerson effectivePerson, List<String> manageablePersonList) {
		if( manageablePersonList == null ) {
			manageablePersonList = new ArrayList<>();
		}
		if( StringUtils.equalsAnyIgnoreCase( distinguishedName, "SYSTEM")){
			distinguishedName = effectivePerson.getDistinguishedName();
		}
		if( StringUtils.isNotEmpty( distinguishedName ) ) {
			if( !manageablePersonList.contains( distinguishedName )) {
				manageablePersonList.add( distinguishedName );
			}
		}
	}

	/**
	 * 조건으로 지정한 캘린더 정보 ID 목록 조회
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
	public List<String> listWithCondition( String name, String type, String source, String createor,
			 List<String> inFilterCalendarIds, String personName, List<String> unitNames, List<String> groupNames ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			 return calendarInfoService.listWithCondition(emc, name, type, source, createor, inFilterCalendarIds, 
					 personName, unitNames, groupNames);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 조건에 따라 지정 캘린더 정보 ID 목록 조회(권한 무시)
	 * @param name
	 * @param type
	 * @param source
	 * @param createor
	 * @param inFilterCalendarIds
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithCondition( String name, String type, String source, String createor,
			 List<String> inFilterCalendarIds ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			 return calendarInfoService.listWithCondition(emc, name, type, source, createor, inFilterCalendarIds, 
					 null, null, null );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 권한에 따라 캘린더 정보 ID 목록 조회
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listMyCalendarIds( String personName ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			 return calendarInfoService.listMyCalenders(emc, personName);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 권한에 따라 캘린더 정보 ID 목록 조회
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithCondition( String personName, List<String> unitNames, List<String> groupNames ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			 return calendarInfoService.listWithCondition(emc, null, null, null, null, null, personName, unitNames, groupNames);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * Id로 캘린더 정보 삭제, 캘린더의 모든 레코드 정보 및 반복 주체 정보도 삭제
	 * @param id
	 * @throws Exception 
	 */
	public void destory(String id) throws Exception {
		if(StringUtils.isEmpty( id )) {
			throw new Exception("id is empty!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			calendarInfoService.destory(emc, id);
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listPublicCalendar() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendarInfoService.listPublicCalendar(emc);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 캘린더 구독
	 * @param effectivePerson 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public Boolean follow( EffectivePerson effectivePerson, String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendarInfoService.follow(emc, effectivePerson.getDistinguishedName(), id );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 캘린더 구독 취소
	 * @param effectivePerson 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public Boolean followCancel( EffectivePerson effectivePerson, String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendarInfoService.followCancel(emc, effectivePerson.getDistinguishedName(), id );
		} catch ( Exception e ) {
			throw e;
		}
	}
}
