package com.x.attendance.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AppealConfig;
import com.x.attendance.entity.AttendanceAppealAuditInfo;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceSetting;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class AttendanceAppealInfoServiceAdv {
	
	private static  Logger logger = LoggerFactory.getLogger( AttendanceAppealInfoServiceAdv.class );
	private AttendanceAppealInfoService attendanceAppealInfoService = new AttendanceAppealInfoService();
	private AttendanceDetailService attendanceDetailService = new AttendanceDetailService();
	private AttendanceNoticeService attendanceNoticeService = new AttendanceNoticeService();
	private AttendanceSettingService attendanceSettingService = new AttendanceSettingService();
	private UserManagerService userManagerService = new UserManagerService();
	
	public AttendanceAppealInfo get( String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceAppealInfoService.get( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<AttendanceAppealInfo> listWithDetailId(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceAppealInfoService.listWithDetailId( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<AttendanceAppealInfo> list(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceAppealInfoService.list( emc, ids );	
		} catch ( Exception e ) {
			throw e;
		}
	}
	public void delete( String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceAppealInfoService.delete( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceAppealInfo saveNewAppeal( AttendanceAppealInfo attendanceAppealInfo, AttendanceAppealAuditInfo attendanceAppealAuditInfo ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceAppealInfoService.save( emc, attendanceAppealInfo, attendanceAppealAuditInfo );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 체크 정보와 시스템 관련 설정으로 소명 정보 처리자 조회
	 * @param personName
	 * @param personUnitName
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public String getAppealAuditPerson( String personName, String personUnitName, String identity ) throws Exception {
		/**
		 * 시스템 설정 APPEAL_AUDITOR_TYPE은 세 가지 값을 가짐:
		 * 1. 지정 사용자 
		 * 2. 사용자 속성
		 * 3. 소속 부서 직위
		 * 관련 설정으로 소명 처리자 조회
		 */
		AttendanceSetting attendanceSetting  = null;
		String appeal_auditor_type = null;
		String appeal_auditor_value = null;
		//1. 먼저 시스템 설정 APPEAL_AUDITOR_TYPE 조회
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceSetting = attendanceSettingService.getByCode( emc, "APPEAL_AUDITOR_TYPE" );
			if( attendanceSetting != null ) {
				appeal_auditor_type = attendanceSetting.getConfigValue();
			}
			attendanceSetting = attendanceSettingService.getByCode( emc, "APPEAL_AUDITOR_VALUE" );
			if( attendanceSetting != null ) {
				appeal_auditor_value = attendanceSetting.getConfigValue();
			}
		} catch ( Exception e ) {
			throw e;
		}
		return getAppealProcessPerson( personName, appeal_auditor_type, appeal_auditor_value, personUnitName, identity );
	}

	/**
	 * 체크 정보와 시스템 관련 설정으로 소명 정보 재검토자 조회
	 * @param personName
	 * @param personUnitName
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public String getAppealCheckPerson( String personName, String personUnitName, String identity ) throws Exception {
		/**
		 * 시스템 설정 APPEAL_AUDITOR_TYPE은 세 가지 값을 가짐:
		 * 1. 지정 사용자 
		 * 2. 사용자 속성
		 * 3. 소속 부서 직위
		 * 관련 설정으로 소명 처리자 조회
		 */
		AttendanceSetting attendanceSetting  = null;
		String appeal_checker_type = null;
		String appeal_checker_value = null;
		//1. 먼저 시스템 설정 APPEAL_AUDITOR_TYPE 조회
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceSetting = attendanceSettingService.getByCode( emc, "APPEAL_CHECKER_TYPE" );
			if( attendanceSetting != null ) {
				appeal_checker_type = attendanceSetting.getConfigValue();
			}
			attendanceSetting = attendanceSettingService.getByCode( emc, "APPEAL_CHECKER_VALUE" );
			if( attendanceSetting != null ) {
				appeal_checker_value = attendanceSetting.getConfigValue();
			}
		} catch ( Exception e ) {
			throw e;
		}
		return getAppealProcessPerson( personName, appeal_checker_type, appeal_checker_value, personUnitName, identity );
	}

	/**
	 * 체크 정보와 시스템 관련 설정으로 소명 정보 처리자 조회
	 * @param personName
	 * @param auditorType
	 * @param auditorTypeValue
	 * @param personUnitName
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	private String getAppealProcessPerson( String personName, String auditorType, String auditorTypeValue, String personUnitName, String identity ) throws Exception {
		if( StringUtils.isEmpty(personName) ) {
			logger.info( "personName is null!" );
			return null;
		}
		if( StringUtils.isEmpty(auditorType)) {
			logger.info( "auditorType is null!" );
			return null;
		}
		if( StringUtils.isEmpty(auditorTypeValue)  ) {
			logger.info( "auditorTypeValue is null!" );
			return null;
		}
		String auditorPersonName = null;
		if( AppealConfig.APPEAL_AUDITTYPE_PERSON.equals( auditorType ) ) {
			return auditorTypeValue;
		}else if( AppealConfig.APPEAL_AUDITTYPE_PERSONATTRIBUTE.equals( auditorType ) ) {
			auditorPersonName = getPersonWithAtrribute( personName, auditorTypeValue );
		}else if( AppealConfig.APPEAL_AUDITTYPE_REPORTLEADER.equals( auditorType ) ) {
			auditorPersonName = getPersonWithReporter( personName );
		}else if( AppealConfig.APPEAL_AUDITTYPE_UNITDUTY.equals( auditorType ) ) {
			auditorPersonName = getPersonWithUnitDuty( personName, auditorTypeValue, personUnitName, identity );
		}
		return auditorPersonName;
	}
	
	/**
	 * 지정한 사용자 소속 조직의 직위 조회
	 * @param personName  근태 대상 사용자
	 * @param dutyName 직위 이름
	 * @param personUnitName 근태 대상 사용자 소속 조직
	 * @param identity  근태 대상 사용자 선택 가능 신분
	 * @return
	 * @throws Exception
	 */
	private String getPersonWithUnitDuty( String personName, String dutyName, String personUnitName, String identity ) throws Exception {
		if( StringUtils.isEmpty( personName ) ) {
			logger.info( "personName is null!" );
			return null;
		}
		if( StringUtils.isEmpty( dutyName ) ) {
			logger.info( "dutyName is null!" );
			return null;
		}
		List<String> duties = null;
		if( StringUtils.isNotEmpty( identity ) ) {
			duties = userManagerService.getUnitDutyWithIdentityWithDuty( identity, dutyName );
		}else {
			if( StringUtils.isNotEmpty( personUnitName ) ) {
				duties = userManagerService.getUnitDutyWithUnitWithDuty( personUnitName, dutyName );
			}else {
				duties = userManagerService.getUnitDutyWithPersonWithDuty( personName, dutyName );
			}			
		}
		if( duties != null && !duties.isEmpty() ) {
			return duties.get( 0 );
		}
		return null;
	}

	/**
	 * 지정한 사용자의 보고 대상 조회
	 * @param personName
	 * @return
	 * @throws Exception 
	 */
	private String getPersonWithReporter( String personName ) throws Exception {
		return userManagerService.getReporterWithPerson( personName );
	}

	/**
	 * 지정한 사용자의 개인 속성 조회
	 * @param personName
	 * @param attributeName
	 * @return
	 * @throws Exception 
	 */
	private String getPersonWithAtrribute(String personName, String attributeName ) throws Exception {
		List<String> attributes = userManagerService.listAttributeWithPersonWithName( personName, attributeName );
		if( attributes != null && !attributes.isEmpty() ) {
			return attributes.get( 0 );
		}
		return null;
	}

	/**
	 * 
	 * @param id
	 * @param unitName
	 * @param topUnitName
	 * @param processor
	 * @param processTime
	 * @param opinion
	 * @param status // 소명 상태: 0-미소명, 1-소명 중, -1-소명 반려, 9-소명 승인
	 * @return
	 * @throws Exception
	 */
	public AttendanceAppealInfo firstProcessAttendanceAppeal( String id, String unitName, String topUnitName, String processor, Date processTime, String opinion, Integer status ) throws Exception {
		AttendanceAppealAuditInfo attendanceAppealAuditInfo = null;
		AttendanceAppealInfo attendanceAppealInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//소명 정보 상태와 처리 과정 정보 수정
			emc.beginTransaction( AttendanceAppealAuditInfo.class );
			emc.beginTransaction( AttendanceAppealInfo.class );
			emc.beginTransaction( AttendanceDetail.class );
			attendanceAppealInfo = attendanceAppealInfoService.updateAppealProcessInfoForFirstProcess( emc, id, unitName, topUnitName, processor, processTime, opinion, status, false );
			if( attendanceAppealInfo != null ){
				attendanceAppealAuditInfo = emc.find( id, AttendanceAppealAuditInfo.class );
				if ( status == 1 ) {
					attendanceAppealInfo.setCurrentProcessor( null );
					attendanceDetailService.updateAppealProcessStatus( emc, id, 9, false );
					attendanceNoticeService.notifyAttendanceAppealAcceptMessage( attendanceAppealInfo, attendanceAppealInfo.getEmpName() );
				} else if ( status == 2 ) {// 2차 승인을 계속해야 하는 경우
					//사용자의 재검토자 정보가 있는지 판단
					if( attendanceAppealAuditInfo.getProcessPerson2() == null || attendanceAppealAuditInfo.getProcessPerson2().isEmpty() ) {
						attendanceDetailService.updateAppealProcessStatus( emc, id, 1, false );
						attendanceAppealAuditInfo.setCurrentProcessor( attendanceAppealAuditInfo.getProcessPerson2() );
						attendanceAppealInfo.setCurrentProcessor( attendanceAppealAuditInfo.getProcessPerson2() );
						emc.check( attendanceAppealInfo, CheckPersistType.all );
						emc.check( attendanceAppealAuditInfo, CheckPersistType.all );
						attendanceNoticeService.notifyAttendanceAppealProcessness2Message( attendanceAppealInfo );
					}
				} else {// 승인되지 않은 경우
					attendanceAppealInfo.setCurrentProcessor( null );
					attendanceDetailService.updateAppealProcessStatus( emc, id, -1, false );
					attendanceNoticeService.notifyAttendanceAppealRejectMessage( attendanceAppealInfo, attendanceAppealInfo.getEmpName());
				}
			}
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
		return attendanceAppealInfo;
	}

	/**
	 *
	 * @param id
	 * @param unitName
	 * @param topUnitName
	 * @param processor
	 * @param processTime
	 * @param opinion
	 * @param status //소명 상태: 0-미소명, 1-소명 중, -1-소명 반려, 9-소명 승인
	 * @return
	 * @throws Exception
	 */
	public AttendanceAppealInfo secondProcessAttendanceAppeal( String id, String unitName, String topUnitName,
			String processor, Date processTime, String opinion, Integer status ) throws Exception {
		AttendanceAppealAuditInfo attendanceAppealAuditInfo = null;
		AttendanceAppealInfo attendanceAppealInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//소명 정보 상태와 처리 과정 정보 수정
			emc.beginTransaction( AttendanceAppealAuditInfo.class );
			emc.beginTransaction( AttendanceAppealInfo.class );
			emc.beginTransaction( AttendanceDetail.class );
			attendanceAppealInfo = attendanceAppealInfoService.updateAppealProcessInfoForSecondProcess( emc, id, unitName, topUnitName, processor, processTime, opinion, status, false );
			if( attendanceAppealInfo != null ){
				attendanceAppealAuditInfo = emc.find( id, AttendanceAppealAuditInfo.class );
				if ( status == 1 ) {
					attendanceDetailService.updateAppealProcessStatus( emc, id, 9, false );
					attendanceNoticeService.notifyAttendanceAppealAcceptMessage( attendanceAppealInfo, attendanceAppealInfo.getEmpName() );
				}else {// 승인되지 않은 경우
					attendanceDetailService.updateAppealProcessStatus( emc, id, -1, false );
					attendanceNoticeService.notifyAttendanceAppealRejectMessage( attendanceAppealInfo, attendanceAppealInfo.getEmpName());
				}
				attendanceAppealInfo.setCurrentProcessor( null );
				attendanceAppealAuditInfo.setCurrentProcessor( null );
			}
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
		return attendanceAppealInfo;
	}

	public void archive( String id ) throws Exception {
		DateOperation dateOperation = new DateOperation();
		String datetime = dateOperation.getNowDateTime();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceAppealInfoService.archive( emc, id, datetime );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void archiveAll() throws Exception {
		DateOperation dateOperation = new DateOperation();
		String datetime = dateOperation.getNowDateTime();
		List<String> ids = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			ids = business.getAttendanceAppealInfoFactory().listNonArchiveAppealInfoIds();
			if( ids != null && !ids.isEmpty() ){
				for( String id : ids ){
					try{
						attendanceAppealInfoService.archive( emc, id, datetime );
					}catch( Exception e ){
						logger.info( "system archive attendance appeal info got an exception.");
						logger.error( e );
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 사용자 이름, 기본 조직 이름, 사용자 신분으로 조직 이름 하나를 결정
	 * 우선, 전달된 신분 이름
	 * 둘째, 전달된 조직 이름
	 * 마지막으로, 사용자 이름으로 조직 이름 하나를 선택
	 * 
	 * @param personName
	 * @param unitName
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public String getPersonUnitName( String personName, String unitName, String identity ) throws Exception {
		if( identity != null && !identity.isEmpty() ) {
			//신분으로 지정 조직 조회
			return userManagerService.getUnitNameWithIdentity(identity);
		}else if( unitName != null && !unitName.isEmpty() ) {
			return unitName;
		}else {
			//사용자로 조회
			return userManagerService.getTopUnitNameWithPersonName( personName );
		}
	}


	public AttendanceAppealAuditInfo getAppealAuditInfo(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find( id, AttendanceAppealAuditInfo.class );
		} catch ( Exception e ) {
			throw e;
		}
	}


	/**
	 * 소명 상태: 0-미소명, 1-소명 중, -1-소명 반려, 9-소명 승인
	 * @param attendanceAppealInfo
	 * @param activityType
	 * @param currentProcessor
	 * @param status
	 * @throws Exception
	 */
	public void syncAppealStatus( AttendanceAppealInfo attendanceAppealInfo, String activityType, String currentProcessor, Integer status ) throws Exception {

		AttendanceAppealAuditInfo attendanceAppealAuditInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceAppealInfo = emc.find( attendanceAppealInfo.getId(), AttendanceAppealInfo.class );
			attendanceAppealAuditInfo = emc.find( attendanceAppealInfo.getId(), AttendanceAppealAuditInfo.class );

			emc.beginTransaction( AttendanceAppealInfo.class );
			emc.beginTransaction( AttendanceAppealAuditInfo.class );
			emc.beginTransaction( AttendanceDetail.class );

			attendanceAppealInfo.setStatus(status);
			if( StringUtils.equalsAnyIgnoreCase( "end", activityType )){//심사 종료
				attendanceAppealInfo.setCurrentProcessor( null );
				attendanceAppealAuditInfo.setCurrentProcessor( null );
			}else{
				attendanceAppealInfo.setCurrentProcessor( currentProcessor );
				attendanceAppealAuditInfo.setCurrentProcessor( currentProcessor );
			}
			if ( status == 1 ) {
				attendanceDetailService.updateAppealProcessStatus( emc, attendanceAppealInfo.getId(), 9, false );
			}else{
				attendanceDetailService.updateAppealProcessStatus( emc, attendanceAppealInfo.getId(), -1, false );
			}
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}



	}


}
