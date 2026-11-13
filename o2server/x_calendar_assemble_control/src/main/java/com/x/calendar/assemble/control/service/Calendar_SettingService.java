package com.x.calendar.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.calendar.assemble.control.Business;
import com.x.calendar.core.entity.Calendar_Setting;
import com.x.calendar.core.entity.Calendar_SettingLobValue;


/**
 * 일정 관리 시스템 설정 서비스 클래스
 * @author O2LEE
 *
 */
public class Calendar_SettingService {
	
	//private Logger logger = LoggerFactory.getLogger( Calendar_SettingService.class );

	/**
	 * 모든 시스템 설정 정보 조회
	 * @return
	 * @throws Exception
	 */
	public List<Calendar_Setting> listAll( EntityManagerContainer emc ) throws Exception {
		Business business =  new Business( emc );
		return business.calendar_SettingFactory().listAll();
	}
	
	/**
	 * ID로 지정 시스템 설정 정보 조회
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Calendar_Setting get( EntityManagerContainer emc, String id ) throws Exception {
		return emc.find(id, Calendar_Setting.class);
	}

	/**
	 * ID로 지정 시스템 설정 정보 삭제
	 * @param id
	 * @throws Exception
	 */
	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		Calendar_Setting report_S_Setting = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		report_S_Setting = emc.find( id, Calendar_Setting.class );
		if ( null == report_S_Setting ) {
			throw new Exception( "object is not exist {'id':'"+ id +"'}" );
		}else{
			emc.beginTransaction( Calendar_Setting.class );
			emc.remove( report_S_Setting, CheckRemoveType.all );
			emc.commit();
		}
	}
	/**
	 * 설정 코드로 지정 설정 정보 조회
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public Calendar_Setting getByCode( EntityManagerContainer emc, String code ) throws Exception {
		List<Calendar_Setting> report_S_SettingList = null;
		Calendar_Setting report_S_Setting = null;
		List<String> ids = null;
		Business business = new Business( emc );
		if( code == null || code.isEmpty() ){
			throw new Exception( "code is null, system can not query any object." );
		}
		
		ids = business.calendar_SettingFactory().listIdsByCode(code);
		if( ids != null && !ids.isEmpty() ){
			report_S_SettingList =  business.calendar_SettingFactory().list( ids );
		}
		
		if( report_S_SettingList != null && !report_S_SettingList.isEmpty() ){
			for( int i=0; i< report_S_SettingList.size(); i++  ){
				if( i == 0 ){
					report_S_Setting = report_S_SettingList.get( i );
				}else{
					emc.beginTransaction( Calendar_Setting.class );
					emc.remove( report_S_SettingList.get( i ), CheckRemoveType.all );
					emc.commit();
				}
			}
		}
		return report_S_Setting;
	}

	/**
	 * 시스템 설정 정보 생성
	 * @param report_S_Setting
	 * @return
	 * @throws Exception
	 */
	public Calendar_Setting create( EntityManagerContainer emc, Calendar_Setting report_S_Setting ) throws Exception {
		Calendar_Setting report_S_Setting_old = null;
		Calendar_SettingLobValue reportSettingLobValue = null;
		
		report_S_Setting_old = emc.find( report_S_Setting.getId(), Calendar_Setting.class );
		if( report_S_Setting_old != null ){
			throw new Exception("report_S_Setting{'id':' "+ report_S_Setting.getId() +" '} exists, can not create new object");
		}else{
			emc.beginTransaction( Calendar_Setting.class );

			//긴 텍스트 레코드 처리
			if( report_S_Setting.getIsLob() ) {
				emc.beginTransaction( Calendar_SettingLobValue.class );
				reportSettingLobValue = emc.find( report_S_Setting.getId(), Calendar_SettingLobValue.class );
				if( reportSettingLobValue != null ) {
					reportSettingLobValue.setLobValue( report_S_Setting.getConfigValue() );//값 수정
					emc.check( reportSettingLobValue, CheckPersistType.all );
				}else {
					//없으면 LOB 값 레코드 생성
					reportSettingLobValue = new Calendar_SettingLobValue();
					reportSettingLobValue.setId( report_S_Setting.getId() );
					reportSettingLobValue.setLobValue( report_S_Setting.getConfigValue() );
					emc.persist( reportSettingLobValue, CheckPersistType.all );
				}
				report_S_Setting.setConfigValue( "LobValue" );
			}
			
			emc.persist( report_S_Setting, CheckPersistType.all);
			emc.commit();
		}
		return report_S_Setting;
	}
	
	/**
	 * 시스템 설정 정보 수정
	 * @param report_S_Setting
	 * @return
	 * @throws Exception
	 */
	public Calendar_Setting update( EntityManagerContainer emc, Calendar_Setting report_S_Setting ) throws Exception {
		if( report_S_Setting == null ){
			throw new Exception("report_S_Setting is null, can not update object!");
		}
		Calendar_Setting report_S_Setting_old = null;
		Calendar_SettingLobValue reportSettingLobValue = null;
		Business business = new Business(emc);
		report_S_Setting_old = business.calendar_SettingFactory().getWithConfigCode(report_S_Setting.getConfigCode());
		if( report_S_Setting_old != null ){
			emc.beginTransaction( Calendar_Setting.class );
			report_S_Setting_old.setConfigCode( report_S_Setting.getConfigCode() );
			report_S_Setting_old.setConfigName( report_S_Setting.getConfigName() );
			report_S_Setting_old.setConfigValue( report_S_Setting.getConfigValue() );
			report_S_Setting_old.setOrderNumber( report_S_Setting.getOrderNumber() );
			report_S_Setting_old.setIsLob( report_S_Setting.getIsLob() );
			
			//긴 텍스트 레코드 처리
			if( report_S_Setting_old.getIsLob() ) {
				emc.beginTransaction( Calendar_SettingLobValue.class );
				reportSettingLobValue = emc.find( report_S_Setting.getId(), Calendar_SettingLobValue.class );
				if( reportSettingLobValue != null ) {
					reportSettingLobValue.setLobValue( report_S_Setting.getConfigValue() );//값 수정
					emc.check( reportSettingLobValue, CheckPersistType.all );
				}else {
					//없으면 LOB 값 레코드 생성
					reportSettingLobValue = new Calendar_SettingLobValue();
					reportSettingLobValue.setId( report_S_Setting.getId() );
					reportSettingLobValue.setLobValue( report_S_Setting.getConfigValue() );
					emc.persist( reportSettingLobValue, CheckPersistType.all );
				}
				report_S_Setting_old.setConfigValue( "LobValue" );
			}
			emc.check( report_S_Setting_old, CheckPersistType.all);
			emc.commit();
		}else{
			throw new Exception("old object report_S_Setting{'id':' "+ report_S_Setting.getId() +" '} is not exists. ");
		}
		
		return report_S_Setting_old;
	}

}
