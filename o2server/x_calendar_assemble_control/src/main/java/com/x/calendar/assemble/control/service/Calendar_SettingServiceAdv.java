package com.x.calendar.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.calendar.assemble.control.Business;
import com.x.calendar.core.entity.Calendar_Setting;
import com.x.calendar.core.entity.Calendar_SettingLobValue;


/**
 * 일정 관리 시스템 설정 서비스 클래스
 * @author O2LEE
 *
 */
public class Calendar_SettingServiceAdv{

	private Calendar_SettingService calendar_SettingService = new Calendar_SettingService();
	
	/**
	 * 모든 시스템 설정 정보 조회
	 * @return
	 * @throws Exception
	 */
	public List<Calendar_Setting> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_SettingService.listAll( emc );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * ID로 지정 시스템 설정 정보 조회
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Calendar_Setting get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_SettingService.get( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 시스템 설정 정보 저장
	 * @param calendar_Setting
	 * @return
	 * @throws Exception
	 */
	public Calendar_Setting save( Calendar_Setting calendar_Setting ) throws Exception {
		Calendar_Setting calendar_Setting_old = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			calendar_Setting_old = business.calendar_SettingFactory().getWithConfigCode(calendar_Setting.getConfigCode());
			if( calendar_Setting_old != null ){
				calendar_Setting.setId( calendar_Setting_old.getId() );
				return calendar_SettingService.update( emc, calendar_Setting );	
			}else{
				return calendar_SettingService.create( emc, calendar_Setting );	
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 설정 코드로 지정 설정 정보 조회
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public Calendar_Setting getByCode( String code ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			 return calendar_SettingService.getByCode( emc, code );	
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 코드로 지정 설정 정보의 구체적 설정 값 조회
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public String getValueByCode( String code ) throws Exception {
		Calendar_Setting calendar_Setting = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			calendar_Setting = calendar_SettingService.getByCode( emc, code );
			if( calendar_Setting != null ) {
				if( !calendar_Setting.getIsLob() ) {
					return calendar_Setting.getConfigValue();
				}else {
					return getLobValueWithId( calendar_Setting.getId() );
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return null;
	}

	/**
	 * ID로 설정 값 정보 조회
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String getLobValueWithId( String id ) throws Exception {
		Calendar_SettingLobValue lobValue = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			lobValue = emc.find( id, Calendar_SettingLobValue.class );
			if( lobValue != null ) {
				return lobValue.getLobValue();
			}
		} catch ( Exception e ) {
			throw e;
		}
		return null;
	}
	
}
