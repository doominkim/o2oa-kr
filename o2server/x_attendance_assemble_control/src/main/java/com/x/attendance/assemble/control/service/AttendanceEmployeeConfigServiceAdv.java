package com.x.attendance.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;

public class AttendanceEmployeeConfigServiceAdv {
	
	private UserManagerService userManagerService = new UserManagerService();
	private AttendanceEmployeeConfigService attendanceEmployeeConfigService = new AttendanceEmployeeConfigService();

	public AttendanceEmployeeConfig get( String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceEmployeeConfigService.get( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<AttendanceEmployeeConfig> list( List<String> ids ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceEmployeeConfigService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<AttendanceEmployeeConfig> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceEmployeeConfigService.listAll( emc );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listIdsByConfigType( String requireType ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceEmployeeConfigService.listByConfigType( emc, requireType );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<AttendanceEmployeeConfig> listByConfigType( String requireType ) throws Exception {
		List<String> ids = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ids = attendanceEmployeeConfigService.listByConfigType( emc, requireType );
			return attendanceEmployeeConfigService.list(emc, ids);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 설정 파일에 있는 사용자의 소속 조직과 최상위 조직 정보가 정상인지 보충·검사
	 * @param attendanceEmployeeConfig
	 * @return
	 * @throws Exception
	 */
	public AttendanceEmployeeConfig checkAttendanceEmployeeConfig( AttendanceEmployeeConfig attendanceEmployeeConfig ) throws Exception {
		String unitName = null;
		String topUnitName = null;
		if ( StringUtils.isNotEmpty( attendanceEmployeeConfig.getUnitName() )){
			//조직 존재 여부를 확인하고, 없으면 다시 조회
			unitName = userManagerService.checkUnitNameExists( attendanceEmployeeConfig.getUnitName() );
		}
		if( StringUtils.isEmpty( unitName ) ){
			unitName = userManagerService.getUnitNameWithPersonName( attendanceEmployeeConfig.getEmployeeName() );
		}
		if( StringUtils.isNotEmpty( unitName ) ){
			topUnitName = userManagerService.getTopUnitNameWithUnitName( unitName );
			attendanceEmployeeConfig.setUnitName( unitName );
			attendanceEmployeeConfig.setTopUnitName( topUnitName );
		}else{
			throw new Exception( "system can not find user unit with username:"+ attendanceEmployeeConfig.getEmployeeName() +"." );
		}
		return attendanceEmployeeConfig;
	}

	public AttendanceEmployeeConfig save(AttendanceEmployeeConfig attendanceEmployeeConfig) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceEmployeeConfigService.save( emc, attendanceEmployeeConfig );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceEmployeeConfigService.delete( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}
}
