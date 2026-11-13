package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;


public class AttendanceDetailService {
	
	private static  Logger logger = LoggerFactory.getLogger( AttendanceDetailService.class );
	private AttendanceDetailAnalyseService attendanceDetailAnalyseService = new AttendanceDetailAnalyseService();

	public AttendanceDetail get( EntityManagerContainer emc, String id ) throws Exception {
		return emc.find(id, AttendanceDetail.class);
	}

	public List<AttendanceDetail> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().list( ids );
	}	
	
	public List<String> listByBatchName( EntityManagerContainer emc, String file_id ) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listByBatchName( file_id );
	}

	/**
	 * 직원과 날짜 식별자로 존재하지 않는 체크 데이터 정보 보충
	 * @param emc
	 * @param dayList
	 * @param attendanceEmployeeConfig
	 */
	public synchronized void checkAndReplenish( EntityManagerContainer emc, List<String> dayList, AttendanceEmployeeConfig attendanceEmployeeConfig ) {
		if( dayList == null || dayList.isEmpty() ){
			logger.warn( "근태 통계 주기 안의 날짜 목록이 비어 있어 데이터를 보충할 수 없습니다." );
			return;
		}
		if( attendanceEmployeeConfig == null){//입사 이전 데이터는 보충하지 않음
			logger.warn( "직원 정보 설정이 비어 있어 데이터를 보충할 수 없습니다." );
			return;
		}
		logger.info( "직원["+attendanceEmployeeConfig.getEmployeeName()+"]의 체크 데이터 확인 및 보충 시작......" );
		DateOperation dateOperation = new DateOperation();
		Business business = null;
		List<String> ids = null;
		AttendanceDetail attendanceDetail = null;
		for( String day : dayList ){
			try {
				/*if( dateOperation.getDateFromString(day).before( dateOperation.getDateFromString( attendanceEmployeeConfig.getEmpInTopUnitTime() )) ){
					logger.warn( "직원["+attendanceEmployeeConfig.getEmployeeName()+"]의 입사 전 체크 데이터는 보충할 필요가 없습니다." );
					continue;
				}*/
				if( dateOperation.getDateFromString( day ).before(dateOperation.getDateFromString(dateOperation.getNowDate()))){
					business = new Business(emc);
					ids = business.getAttendanceDetailFactory().listByEmployeeNameAndDate( attendanceEmployeeConfig.getEmployeeName(), day );
					if( ids == null || ids.isEmpty() ){
						emc.beginTransaction( AttendanceDetail.class );
						//체크 데이터 한 건 보충 필요
						attendanceDetail = new AttendanceDetail();
						attendanceDetail.setTopUnitName(attendanceEmployeeConfig.getTopUnitName());
						attendanceDetail.setUnitName(attendanceEmployeeConfig.getUnitName());
						attendanceDetail.setEmpNo( attendanceEmployeeConfig.getEmployeeNumber() );
						attendanceDetail.setEmpName( attendanceEmployeeConfig.getEmployeeName() );
						attendanceDetail.setYearString( dateOperation.getYear( dateOperation.getDateFromString(day)) );
						attendanceDetail.setMonthString( dateOperation.getMonth( dateOperation.getDateFromString(day)));
						attendanceDetail.setRecordDate( dateOperation.getDateFromString( day ) );
						attendanceDetail.setRecordDateString( day );
						attendanceDetail.setRecordStatus( 0 );
						attendanceDetail.setBatchName( "시스템 보충" );
						emc.beginTransaction( AttendanceDetail.class );
						emc.persist( attendanceDetail );
						emc.commit();
						logger.info("직원["+attendanceEmployeeConfig.getEmployeeName()+"]의 ["+day+"]에 체크 데이터가 없어 한 건을 보충했습니다.");
					}else{
						for(String id :ids){
							AttendanceDetail detail = this.get(emc, id );
							if(detail !=null){
								detail.setTopUnitName(attendanceEmployeeConfig.getTopUnitName());
								detail.setUnitName(attendanceEmployeeConfig.getUnitName());
								emc.beginTransaction( AttendanceDetail.class );
								emc.persist( detail , CheckPersistType.all );
								emc.commit();
							}
						}
					}
				}else {
					logger.warn( "직원["+attendanceEmployeeConfig.getEmployeeName()+"]의 체크 데이터["+day+"]는 아직 보충할 수 없습니다." );
				}
			} catch (Exception e) {
				logger.warn("시스템이 직원["+attendanceEmployeeConfig.getEmployeeName()+"]의 ["+day+"]의 존재하지 않는 출퇴근 데이터를 확인 및 보충하는 중 예외가 발생했습니다!" );
				logger.error(e);
			}
		}
	}

	public String getMaxRecordDate(EntityManagerContainer emc) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().getMaxRecordDate();
	}

	public List<String> listUserAttendanceDetailByYearAndMonth(EntityManagerContainer emc, String q_empName,
			String q_year, String q_month) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listUserAttendanceDetailByYearAndMonth( q_empName, q_year, q_month );	
	}

	public List<String> listDetailByCycleYearAndMonthWithOutStatus( EntityManagerContainer emc, String user, String year, String month )  throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listDetailByCycleYearAndMonthWithOutStatus( user, year, month );
	}
	
	//사용자와 출퇴근 날짜로 출퇴근 기록 조회  
	public List<String> listDetailByNameAndDate( EntityManagerContainer emc, String user, String datestr )  throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listDetailByNameAndDate( user, datestr );
	}

	public List<String> listUserAttendanceDetailByCycleYearAndMonth(EntityManagerContainer emc, String q_empName, String cycleYear,
			String cycleMonth) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listUserAttendanceDetailByCycleYearAndMonth( q_empName, cycleYear, cycleMonth );
	}

	public List<String> listTopUnitAttendanceDetailByYearAndMonth(EntityManagerContainer emc, List<String> topUnitNames,
			String q_year, String q_month) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listTopUnitAttendanceDetailByYearAndMonth( topUnitNames, q_year, q_month );	
	}

	public List<String> listUnitAttendanceDetailByYearAndMonth(EntityManagerContainer emc,
			List<String> unitNames, String q_year, String q_month) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listUnitAttendanceDetailByYearAndMonth( unitNames, q_year, q_month );	
	}

	public List<String> getAllAnalysenessDetails( EntityManagerContainer emc, String startDate, String endDate, String personName ) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().getAllAnalysenessDetails( startDate, endDate, personName );	
	}
	
	public List<String> getAllAnalysenessDetailsForce( EntityManagerContainer emc, String startDate, String endDate, String personName ,Boolean forceFlag) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().getAllAnalysenessDetailsForce( startDate, endDate, personName ,forceFlag);
	}

	/**
	 * 
	 * @param emc
	 * @param detailId
	 * @param status 소명 상태: 0-미소명, 1-소명 중, -1-소명 반려, 9-소명 승인
	 * @param autoCommit
	 * @throws Exception
	 */
	public AttendanceDetail updateAppealProcessStatus( EntityManagerContainer emc, String detailId, Integer status, Boolean autoCommit ) throws Exception {
		if( detailId == null ){
			return null;
		}
		AttendanceDetail attendanceDetail = emc.find( detailId, AttendanceDetail.class);
		if( autoCommit ){
			emc.beginTransaction(AttendanceDetail.class);
		}
		if( attendanceDetail != null ){
			if( status != 1 ) {
				attendanceDetail.setAppealProcessor( null );
			}
			attendanceDetail.setAppealStatus( status );
			if(status == 9){
				//소명이 승인되면 Detail 상태를 갱신하여 Detail을 정상 체크로 만듦
				attendanceDetail.setIsGetSelfHolidays(false);
				attendanceDetail.setIsLate(false);
				attendanceDetail.setIsAbsent(false);
				attendanceDetail.setIsAbnormalDuty(false);
				attendanceDetail.setIsLackOfTime(false);
				attendanceDetail.setIsLeaveEarlier(false);
				//그리고 해당 근태 데이터에 대해 통계 요청을 발생
				attendanceDetailAnalyseService.recordStatisticRequireLog(attendanceDetail,true);
			}
			emc.check( attendanceDetail, CheckPersistType.all );

		}
		if( autoCommit ){
			emc.commit();
		}
		
		return attendanceDetail;
	}

	public void archive( EntityManagerContainer emc, String id, String datetime ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id can not be null!");
		}
		AttendanceDetail attendanceDetail = emc.find(id, AttendanceDetail.class);
		emc.beginTransaction( AttendanceDetail.class );
		if( attendanceDetail != null ){
			
		}else{
			throw new Exception("attendance detail info is not exists");
		}
		attendanceDetail.setArchiveTime( datetime );
		emc.check( attendanceDetail, CheckPersistType.all);
		emc.commit();
	}

	public AttendanceDetail save( EntityManagerContainer emc, AttendanceDetail attendanceDetail ) throws Exception {
		if( attendanceDetail == null ){
			throw new Exception("attendanceDetail can not be null!");
		}
		List<String> ids = null;
		List<AttendanceDetail> attendanceDetailList = null;
		AttendanceDetail attendanceDetail_old = null;
		Business business = new Business( emc );
		
		//없으면 해당 직원의 당일 체크 정보가 이미 있는지 확인
		ids = business.getAttendanceDetailFactory().listByEmployeeNameAndDate( attendanceDetail.getEmpName(), attendanceDetail.getRecordDateString() );
		if( ids != null && !ids.isEmpty() ){
			attendanceDetailList = business.getAttendanceDetailFactory().list(ids);
		}
		if( attendanceDetailList != null && !attendanceDetailList.isEmpty() ){
			attendanceDetail_old = attendanceDetailList.get(0);
		}
		
		if( attendanceDetail_old != null ){
			//데이터 갱신 필요			
			attendanceDetail_old.refresh();
			attendanceDetail_old.setEmpName( attendanceDetail.getEmpName() );
			attendanceDetail_old.setEmpNo( attendanceDetail.getEmpNo() );
			attendanceDetail_old.setRecordDate( attendanceDetail.getRecordDate() );
			attendanceDetail_old.setRecordDateString( attendanceDetail.getRecordDateString() );
			attendanceDetail_old.setRecordStatus( 0 );
			if(attendanceDetail.getOnDutyTime()!= null && attendanceDetail.getOffDutyTime().trim().length() > 0){
				attendanceDetail_old.setOnDutyTime( attendanceDetail.getOnDutyTime() );
			}
			if(attendanceDetail.getMorningOffDutyTime()!= null && attendanceDetail.getMorningOffDutyTime().trim().length() > 0){
				attendanceDetail_old.setMorningOffDutyTime( attendanceDetail.getMorningOffDutyTime() );
			}
			if(attendanceDetail.getAfternoonOnDutyTime()!= null && attendanceDetail.getAfternoonOnDutyTime().trim().length() > 0){
				attendanceDetail_old.setAfternoonOnDutyTime( attendanceDetail.getAfternoonOnDutyTime() );
			}
			if(attendanceDetail.getOffDutyTime()!= null && attendanceDetail.getOffDutyTime().trim().length() > 0){
				attendanceDetail_old.setOffDutyTime( attendanceDetail.getOffDutyTime() );
			}
			
			//emc.beginTransaction( AttendanceSetting.class );
			emc.beginTransaction( AttendanceDetail.class );
			emc.check( attendanceDetail_old, CheckPersistType.all);	
			emc.commit();
			attendanceDetail = attendanceDetail_old;
		}else{
			//체크 정보 데이터 추가 필요
			if( attendanceDetail.getId() == null ) {
				attendanceDetail.setId( AttendanceDetail.createId() );
			}
			emc.beginTransaction( AttendanceDetail.class );
			emc.persist( attendanceDetail, CheckPersistType.all);	
			emc.commit();
		}
		return attendanceDetail;
	}
	
	public AttendanceDetail saveSingle( EntityManagerContainer emc, AttendanceDetail attendanceDetail ) throws Exception {
		if( attendanceDetail == null ){
			throw new Exception("attendanceDetail can not be null!");
		}
		List<String> ids = null;
		List<AttendanceDetail> attendanceDetailList = null;
		AttendanceDetail attendanceDetail_old = null;
		Business business = new Business( emc );
		
		//없으면 해당 직원의 당일 체크 정보가 이미 있는지 확인
		ids = business.getAttendanceDetailFactory().listByEmployeeNameAndDate( attendanceDetail.getEmpName(), attendanceDetail.getRecordDateString() );
		if( ids != null && !ids.isEmpty() ){
			attendanceDetailList = business.getAttendanceDetailFactory().list(ids);
		}
		if( attendanceDetailList != null && !attendanceDetailList.isEmpty() ){
			attendanceDetail_old = attendanceDetailList.get(0);
		}
		
		if( attendanceDetail_old != null ){
			//데이터 갱신 필요			
			attendanceDetail_old.refresh();
			attendanceDetail_old.setEmpName( attendanceDetail.getEmpName() );
			attendanceDetail_old.setEmpNo( attendanceDetail.getEmpNo() );
			attendanceDetail_old.setRecordDate( attendanceDetail.getRecordDate() );
			attendanceDetail_old.setRecordDateString( attendanceDetail.getRecordDateString() );
			attendanceDetail_old.setRecordStatus( 0 );
			if(attendanceDetail.getOnDutyTime()!= null && attendanceDetail.getOffDutyTime().trim().length() > 0){
				attendanceDetail_old.setOnDutyTime( attendanceDetail.getOnDutyTime() );
			}
			if(attendanceDetail.getMorningOffDutyTime()!= null && attendanceDetail.getMorningOffDutyTime().trim().length() > 0){
				attendanceDetail_old.setMorningOffDutyTime( attendanceDetail.getMorningOffDutyTime() );
			}
			if(attendanceDetail.getAfternoonOnDutyTime()!= null && attendanceDetail.getAfternoonOnDutyTime().trim().length() > 0){
				attendanceDetail_old.setAfternoonOnDutyTime( attendanceDetail.getAfternoonOnDutyTime() );
			}
			if(attendanceDetail.getOffDutyTime()!= null && attendanceDetail.getOffDutyTime().trim().length() > 0){
				attendanceDetail_old.setOffDutyTime( attendanceDetail.getOffDutyTime() );
			}
			 
			 
			//emc.beginTransaction( AttendanceSetting.class );
			emc.beginTransaction( AttendanceDetail.class );
			emc.check( attendanceDetail_old, CheckPersistType.all);	
			emc.commit();
			attendanceDetail = attendanceDetail_old;
		}else{
			//체크 정보 데이터 추가 필요
			if( attendanceDetail.getId() == null ) {
				attendanceDetail.setId( AttendanceDetail.createId() );
			}
			emc.beginTransaction( AttendanceDetail.class );
			emc.persist( attendanceDetail, CheckPersistType.all);	
			emc.commit();
		}
		return attendanceDetail;
	}
	


	public AttendanceDetailMobile save( EntityManagerContainer emc, AttendanceDetailMobile attendanceDetailMobile ) throws Exception {
		if( attendanceDetailMobile == null ){
			throw new Exception("attendanceDetailMobile can not be null!");
		}
		List<String> ids = null;
		List<AttendanceDetailMobile> attendanceDetailMobileList = null;
		AttendanceDetailMobile attendanceDetailMobile_old = null;
		Business business = new Business( emc );
		//먼저 같은 ID가 있는지 확인
		attendanceDetailMobile_old = emc.find( attendanceDetailMobile.getId(), AttendanceDetailMobile.class );
		//없으면 해당 직원의 당일 체크 정보가 이미 있는지 확인
		if( attendanceDetailMobile_old == null ){
			ids = business.getAttendanceDetailMobileFactory().listByEmployeeNameDateAndTime( attendanceDetailMobile.getEmpName(), attendanceDetailMobile.getRecordDateString(), attendanceDetailMobile.getSignTime() );
			if( ids != null && !ids.isEmpty() ){
				attendanceDetailMobileList = business.getAttendanceDetailMobileFactory().list(ids);
			}
			if( attendanceDetailMobileList != null && !attendanceDetailMobileList.isEmpty() ){
				attendanceDetailMobile_old = attendanceDetailMobileList.get(0);
			}
		}
		if( attendanceDetailMobile_old != null ){
			//데이터 갱신 필요
			emc.beginTransaction( AttendanceDetailMobile.class );
			attendanceDetailMobile.copyTo( attendanceDetailMobile_old, JpaObject.FieldsUnmodify );
			attendanceDetailMobile_old.setRecordStatus( 0 );
			emc.check( attendanceDetailMobile_old, CheckPersistType.all);	
			emc.commit();
		}else{
			//체크 정보 데이터 추가 필요
			emc.beginTransaction( AttendanceDetailMobile.class );
			emc.persist( attendanceDetailMobile, CheckPersistType.all);	
			emc.commit();
		}
		return attendanceDetailMobile;
	}

	public List<String> getAllAnalysenessPersonNames(EntityManagerContainer emc, String startDate, String endDate) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().getAllAnalysenessPersonNames( startDate, endDate );
	}
	
	public List<String> getAllAnalysenessPersonNamesForce(EntityManagerContainer emc, String startDate, String endDate,Boolean forceFlag) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().getAllAnalysenessPersonNamesForce( startDate, endDate , forceFlag);
	}

	public AttendanceDetail listDetailWithEmployee(EntityManagerContainer emc, String employeeName, String recordDateString) throws Exception {
		Business business =  new Business( emc );
		List<AttendanceDetail> details =  business.getAttendanceDetailFactory().listDetailByEmployeeNameAndDate(employeeName, recordDateString);
		if( ListTools.isNotEmpty( details )) {
			return  details.get(0);
		}else {
			return null;
		}
	}

    public List<String> listRecordWithDateAndNoOffDuty(EntityManagerContainer emc, String dateString) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listRecordWithDateAndNoOffDuty( dateString );
    }

	/**
	 * 지정 마감일 이전에 이미 출퇴근 체크한 사용자 조회
	 * @param deadline
	 * @param type:all#onDuty#offDuty#morningOffDuty#afternoonOnDuty
	 * @return
	 * @throws Exception
	 */
    public List<String> listSignedPersonsWithDeadLine(EntityManagerContainer emc, String deadline, String type) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listSignedPersonsWithDeadLine( deadline, type );
    }
}
