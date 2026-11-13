package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AppealConfig;
import com.x.attendance.entity.AttendanceSetting;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class AttendanceSettingService {
	
	private static  Logger logger = LoggerFactory.getLogger( AttendanceSettingService.class );

	public List<AttendanceSetting> listAll( EntityManagerContainer emc ) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceSettingFactory().listAll();
	}

	public AttendanceSetting get( EntityManagerContainer emc, String id ) throws Exception {
		return emc.find(id, AttendanceSetting.class);
	}

	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		AttendanceSetting attendanceSetting = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		attendanceSetting = emc.find( id, AttendanceSetting.class );
		if ( null == attendanceSetting ) {
			throw new Exception( "object is not exist {'id':'"+ id +"'}" );
		}else{
			emc.beginTransaction( AttendanceSetting.class );
			emc.remove( attendanceSetting, CheckRemoveType.all );
			emc.commit();
		}
	}

	public AttendanceSetting getByCode( EntityManagerContainer emc, String code ) throws Exception {
		List<AttendanceSetting> attendanceSettingList = null;
		AttendanceSetting attendanceSetting = null;
		List<String> ids = null;
		Business business = new Business( emc );
		if( code == null || code.isEmpty() ){
			throw new Exception( "code is null, system can not query any object." );
		}
		
		ids = business.getAttendanceSettingFactory().listIdsByCode(code);
		if( ids != null && !ids.isEmpty() ){
			attendanceSettingList =  business.getAttendanceSettingFactory().list( ids );
		}
		
		if( attendanceSettingList != null && !attendanceSettingList.isEmpty() ){
			for( int i=0; i< attendanceSettingList.size(); i++  ){
				if( i == 0 ){
					attendanceSetting = attendanceSettingList.get( i );
				}else{
					emc.beginTransaction( AttendanceSetting.class );
					emc.remove( attendanceSettingList.get( i ), CheckRemoveType.all );
					emc.commit();
				}
			}
		}
		return attendanceSetting;
	}

	public AttendanceSetting create( EntityManagerContainer emc, AttendanceSetting attendanceSetting ) throws Exception {
		AttendanceSetting attendanceSetting_old = null;
		attendanceSetting_old = emc.find( attendanceSetting.getId(), AttendanceSetting.class );
		if( attendanceSetting_old != null ){
			throw new Exception("attendanceSetting{'id':' "+ attendanceSetting.getId() +" '} exists, can not create new object");
		}else{
			emc.beginTransaction( AttendanceSetting.class );
			emc.persist( attendanceSetting, CheckPersistType.all);
			emc.commit();
		}
		return attendanceSetting;
	}
	
	public AttendanceSetting update( EntityManagerContainer emc, AttendanceSetting attendanceSetting ) throws Exception {
		if( attendanceSetting == null ){
			throw new Exception("attendanceSetting is null, can not update object!");
		}
		AttendanceSetting attendanceSetting_old = null;
		attendanceSetting_old = emc.find( attendanceSetting.getId(), AttendanceSetting.class );
		if( attendanceSetting_old != null ){
			emc.beginTransaction( AttendanceSetting.class );
			attendanceSetting_old.setConfigCode( attendanceSetting.getConfigCode() );
			attendanceSetting_old.setConfigName( attendanceSetting.getConfigName() );
			attendanceSetting_old.setConfigValue( attendanceSetting.getConfigValue() );
			attendanceSetting_old.setOrderNumber( attendanceSetting.getOrderNumber() );
			emc.check( attendanceSetting_old, CheckPersistType.all);
			emc.commit();
		}else{
			throw new Exception("old object attendanceSetting{'id':' "+ attendanceSetting.getId() +" '} is not exists. ");
		}
		
		return attendanceSetting;
	}

	/**
	 *  APPEALABLE	직원 이의 신청 및 신청 승인	true|false
		APPEAL_AUDITOR_TYPE	        근태 결과 이의 신청 심사자 결정 방식	       소속 조직 직위|소속 조직 속성|사용자 속성(기본)|지정 사용자|지정 역할
		APPEAL_AUDITOR_VALUE	근태 결과 이의 신청 심사자 결정 내용	    직속 상사(기본)    
		APPEAL_CHECKER_TYPE	        근태 결과 이의 신청 재검토자 결정 방식	       없음(기본)|소속 조직 직위|소속 조직 속성|사용자 속성|지정 사용자|지정 역할
		APPEAL_CHECKER_VALUE	근태 결과 이의 신청 재검토자 결정 내용
	 */
	public void initAllSystemConfig() {
		String value = null, description = null, type = null, selectContent = null;
		Boolean isMultiple = false;
		Integer ordernumber = 0;
		
		value = "true";
		type = "select";
		selectContent = null;
		isMultiple = false;
		description = "직원 소명 및 소명 승인: 선택값 true|false, 단일값. 직원이 근태 결과에 소명할 수 있는지 설정합니다.";
		try {
			checkAndInitSystemConfig("APPEALABLE", "직원 소명 및 소명 승인", value, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "attendance system init system config 'APPEALABLE' got an exception." );
			logger.error(e);
		}

		value = AppealConfig.APPEAL_AUDIFLOWTYPE_BUILTIN;
		type = "select";
		selectContent = AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW + "|" + AppealConfig.APPEAL_AUDIFLOWTYPE_BUILTIN;
		isMultiple = false;
		description = "근태 결과 소명 결재 방식: 선택값: "+AppealConfig.APPEAL_AUDIFLOWTYPE_BUILTIN+"|"+ AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW +". 근태 결과 소명을 사용자 정의 결재로 할지 내장 승인 단계(심사-재심사)로 할지 설정합니다.";
		try {
			checkAndInitSystemConfig("APPEAL_AUDIFLOWTYPE", "근태 결과 소명 결재 유형", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "attendance system init system config 'APPEAL_AUDIFLOWTYPE' got an exception." );
			logger.error(e);
		}

		value = "无";
		type = "text";
		selectContent = null;
		isMultiple = false;
		description = "근태 결과 소명 결재, 단일값. '사용자 정의 소명 결재("+AppealConfig.APPEAL_AUDIFLOWTYPE_BUILTIN+")'일 때 시작할 결재 ID입니다.";
		try {
			checkAndInitSystemConfig("APPEAL_AUDIFLOW_ID", "사용자 정의 소명 결재", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'APPEAL_AUDIFLOW_ID' got an exception." );
			logger.error(e);
		}

		value = AppealConfig.APPEAL_AUDITTYPE_UNITDUTY;
		type = "select";
		selectContent = AppealConfig.APPEAL_CHOOSEVALUE_AUDITTYPE;
		isMultiple = false;
		description = "근태 결과 소명 심사자 결정 방식: 선택값: 所属组织职务(소속 조직 직무)|汇报对象(보고 대상)|个人属性(개인 속성)|指定人(지정인). '내장 소명 결재("+AppealConfig.APPEAL_AUDIFLOWTYPE_BUILTIN+")'에서 근태 결과 소명 심사자를 정하는 방식입니다.";
		try {
			checkAndInitSystemConfig("APPEAL_AUDITOR_TYPE", "내장 결재의 근태 결과 소명 심사자 결정 방식", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "attendance system init system config 'APPEAL_AUDITOR_TYPE' got an exception." );
			logger.error(e);
		}
		
		value = "部门经理";
		type = "text";
		selectContent = null;
		isMultiple = false;
		description = "근태 결과 소명 심사자 결정 내용: 지정한 인원 신분, 단일값. '내장 소명 결재("+AppealConfig.APPEAL_AUDIFLOWTYPE_BUILTIN+")'에서 보고 결재 방식의 ADMIN_AND_ALLLEADER와 함께 사용합니다";
		try {
			checkAndInitSystemConfig("APPEAL_AUDITOR_VALUE", "내장 결재의 근태 결과 소명 심사자 결정 내용", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'APPEAL_AUDITOR_VALUE' got an exception." );
			logger.error(e);
		}
		
		value = "无";
		type = "select";
		selectContent = AppealConfig.APPEAL_CHOOSEVALUE_CHECKTYPE;
		isMultiple = false;
		description = "근태 결과 소명 재심사자 결정 방식: 선택값: 无(없음)|所属组织职务(소속 조직 직무)|汇报对象(보고 대상)|个人属性(개인 속성)|指定人(지정인). '내장 소명 결재("+AppealConfig.APPEAL_AUDIFLOWTYPE_BUILTIN+")'에서 근태 결과 소명 재심사자를 정하는 방식이며, 无는 재심사가 필요 없음을 뜻합니다.";
		try {
			checkAndInitSystemConfig("APPEAL_CHECKER_TYPE", "내장 결재의 근태 결과 소명 재심사자 결정 방식", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "attendance system init system config 'APPEAL_CHECKER_TYPE' got an exception." );
			logger.error(e);
		}
		
		value = "无";
		type = "text";
		selectContent = null;
		isMultiple = false;
		description = "근태 결과 소명 재심사자 결정 내용: 지정한 인원 신분, 단일값. '내장 소명 결재("+AppealConfig.APPEAL_AUDIFLOWTYPE_BUILTIN+")'에서 보고 결재 방식의 ADMIN_AND_ALLLEADER와 함께 사용합니다";
		try {
			checkAndInitSystemConfig("APPEAL_CHECKER_VALUE", "내장 결재의 근태 결과 소명 재심사자 결정 내용", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'APPEAL_CHECKER_VALUE' got an exception." );
			logger.error(e);
		}

		value = "无";
		type = "select";
		selectContent = "无|周六|周日";
		isMultiple = false;
		description = "주말 설정: 周六(토요일) 또는 周日(일요일)을 선택합니다. 선택한 요일은 주말, 선택하지 않은 요일은 근무일로 계산합니다";
		try {
			checkAndInitSystemConfig("ATTENDANCE_WEEKEND", "주말 계산 요일 설정", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'APPEAL_CHECKER_VALUE' got an exception." );
			logger.error(e);
		}
	}
	
	/**
	 * 설정 항목 존재 여부를 확인하고, 없으면 정보로 새 설정 항목 생성
	 * @param configCode
	 * @param configName
	 * @param configValue
	 * @param description
	 * @throws Exception
	 */
	public void checkAndInitSystemConfig( String configCode, String configName, String configValue, String description, String type, String selectContent, Boolean isMultiple, Integer orderNumber ) throws Exception {
		if( configCode  == null || configCode.isEmpty() ){
			throw new Exception( "configCode is null!" );
		}
		if( configName  == null || configName.isEmpty() ){
			throw new Exception( "configName is null!" );
		}
		Business business = null;
		AttendanceSetting attendanceSetting = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			attendanceSetting = business.getAttendanceSettingFactory().getWithConfigCode( configCode );
		}catch( Exception e ){
			logger.warn( "system find system config{'configCode':'"+configCode+"'} got an exception. " );
			logger.error(e);
		}
		//설정이 없으면 새 설정 레코드 생성
		if( attendanceSetting == null ){
			attendanceSetting = new AttendanceSetting();
			attendanceSetting.setConfigCode( configCode );
			attendanceSetting.setConfigName( configName );
			attendanceSetting.setConfigValue( configValue );
			attendanceSetting.setDescription( description );
			attendanceSetting.setOrderNumber( orderNumber );
			attendanceSetting.setValueType( type );
			attendanceSetting.setSelectContent( selectContent );
			attendanceSetting.setIsMultiple( isMultiple );
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				emc.beginTransaction( AttendanceSetting.class );
				emc.persist( attendanceSetting, CheckPersistType.all );
				//logger.info("attendance system config has been add：" + attendanceSetting.getConfigCode() + "[" + attendanceSetting.getConfigName()+ "].");
				emc.commit();
			}catch( Exception e ){
				logger.warn("attendance system persist new system config{'configCode':'"+configCode+"'} got an exception. " );
				logger.error(e);
			}
		}else{
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				attendanceSetting = emc.find( attendanceSetting.getId(), AttendanceSetting.class );
				emc.beginTransaction( AttendanceSetting.class );
				if( !configName.equals( attendanceSetting.getConfigName() ) ){
					attendanceSetting.setConfigName( configName );
				}
				if( orderNumber != attendanceSetting.getOrderNumber() ){
					attendanceSetting.setOrderNumber(orderNumber);
				}
				if( description != null ){
					attendanceSetting.setDescription( description );
				}
				if( type != null ){
					attendanceSetting.setValueType( type );
				}
				if( selectContent != null ){
					attendanceSetting.setSelectContent( selectContent );
				}
				if( isMultiple != null ){
					attendanceSetting.setIsMultiple( isMultiple );
				}
				emc.check( attendanceSetting, CheckPersistType.all );
				emc.commit();
			}catch( Exception e ){
				logger.warn("attendance system update system config{'configCode':'"+configCode+"'} got an exception. " );
				logger.error(e);
			}
		}
	}
}
