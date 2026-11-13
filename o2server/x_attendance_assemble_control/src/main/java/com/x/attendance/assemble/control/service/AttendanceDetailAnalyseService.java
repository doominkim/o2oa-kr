package com.x.attendance.assemble.control.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.factory.AttendanceStatisticRequireLogFactory;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

/**
 * 근태 출퇴근 기록 분석 서비스 클래스
 * @author LIYI
 *
 */
public class AttendanceDetailAnalyseService {

	private static  Logger logger = LoggerFactory.getLogger( AttendanceDetailAnalyseService.class );
	private AttendanceSelfHolidayService attendanceSelfHolidayService = new AttendanceSelfHolidayService();
	private AttendanceScheduleSettingService attendanceScheduleSettingService = new AttendanceScheduleSettingService();
	private AttendanceStatisticalCycleService statisticalCycleService = new AttendanceStatisticalCycleService();
	private AttendanceWorkDayConfigService workDayConfigService = new AttendanceWorkDayConfigService();
	private AttendanceDetailAnalyseCoreService attendanceDetailAnalyseCoreService = new AttendanceDetailAnalyseCoreService();
	private DateOperation dateOperation = new DateOperation();
	private UserManagerService userManagerService = new UserManagerService();
	private  AttendanceSettingServiceAdv attendanceSettingServiceAdv = new AttendanceSettingServiceAdv();

	/**
	 * 직원 이름\시작 날짜\종료 날짜로 날짜 범위 안의 모든 출퇴근 기록 정보 ID 목록 조회<br/>
	 * 시간은 시작 날짜 0시부터 종료 날짜 23시 59분까지
	 * @param empName
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public List<String> listAnalyseAttendanceDetailIds( EntityManagerContainer emc, String empName, Date startDate, Date endDate )throws Exception{
		Business business = null;
		List<String> ids = null;
		String startDateString = null, endDateString = null;
		business = new Business(emc);
		startDateString = dateOperation.getDateStringFromDate( startDate, "yyyy-MM-dd" ) + " 00:00:00";
		endDateString = dateOperation.getDateStringFromDate( endDate, "yyyy-MM-dd" ) + "23:59:59";
		ids = business.getAttendanceDetailFactory().getUserAnalysenessDetails( empName, startDateString, endDateString);
		return ids;
	}

	/**
	 * 체크 정보 데이터 묶음 분석
	 * @param emc
	 * @param detailIds
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetailWithIds(EntityManagerContainer emc, List<String> detailIds, Boolean debugger ) throws Exception{
		if( ListTools.isEmpty( detailIds ) ){
			return true;
		}
		analyseAttendanceDetails( emc, emc.list( AttendanceDetail.class, detailIds ), debugger );
		logger.debug( "employee attendance detail analyse over！" );
		return true;
	}

	/**
	 * 체크 정보 데이터 묶음 분석
	 * @param emc
	 * @param detailList
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetails( EntityManagerContainer emc, List<AttendanceDetail> detailList, Boolean debugger ) throws Exception{
		if( ListTools.isEmpty( detailList ) ){
			return true;
		}
		List<AttendanceWorkDayConfig> workDayConfigList = workDayConfigService.getAllWorkDayConfigWithCache(debugger);
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap = statisticalCycleService.getAllStatisticalCycleMapWithCache(debugger);

		for( AttendanceDetail detail : detailList ){
			try{
				analyseAttendanceDetail( emc, detail, workDayConfigList, statisticalCycleMap, debugger );
			}catch(Exception e){
				logger.debug( "employee attendance detail analyse got an exception:["+detail.getEmpName()+"]["+detail.getRecordDateString()+"]" );
				logger.error(e);
			}
		}

		logger.debug( "employee attendance detail analyse over！" );
		return true;
	}

	/**
	 * 체크 정보 데이터 묶음 분석
	 * @param emc
	 * @param detailList
	 * @param statisticalCycleMap
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetails( EntityManagerContainer emc, List<AttendanceDetail> detailList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap, Boolean debugger ) throws Exception{
		if( detailList == null || detailList.isEmpty() ){
			return true;
		}
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = workDayConfigService.getAllWorkDayConfigWithCache(debugger);

		for( AttendanceDetail detail : detailList ){
			try{
				analyseAttendanceDetail( emc, detail, attendanceWorkDayConfigList, statisticalCycleMap, debugger );
			}catch(Exception e){
				logger.debug( "employee attendance detail analyse got an exception:["+detail.getEmpName()+"]["+detail.getRecordDateString()+"]" );
				logger.error(e);
			}
		}

		logger.debug( "employee attendance detail analyse over！" );
		return true;
	}

	/**
	 * 직원 이름, 시작 날짜, 종료 날짜로 모든 출퇴근 기록을 재분석. 보통 사용자가 휴가 기록을 보충했을 때 사용하며, 휴가 날짜 범위 안의 모든 출퇴근 기록을 재분석하고 해당 월 통계를 실행해야 함
	 * @param emc
	 * @param empName
	 * @param startDate
	 * @param endDate
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public Boolean analyseAttendanceDetails( EntityManagerContainer emc, String empName, Date startDate, Date endDate, Boolean debugger ) throws Exception{
		Business business = null;
		List<String> ids = null;
		List<AttendanceWorkDayConfig> workDayConfigList = workDayConfigService.getAllWorkDayConfigWithCache(debugger);
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap = statisticalCycleService.getAllStatisticalCycleMapWithCache(debugger);
		List<AttendanceDetail> attendanceDetailList = null;

		ids = listAnalyseAttendanceDetailIds( emc, empName, startDate, endDate );
		if(ListTools.isEmpty( ids )){
			logger.debug( debugger, "attendance detail info ids not exists for emp{'empName':'"+empName+"','startDate':'"+startDate+"','endDate':'"+endDate+"'}");
			return true;
		}
		attendanceDetailList = business.getAttendanceDetailFactory().list( ids );
		if( ListTools.isEmpty( attendanceDetailList ) ){
			logger.debug( debugger, "attendance detail info not exists for emp{'empName':'"+empName+"','startDate':'"+startDate+"','endDate':'"+endDate+"'}");
			return true;
		}

		for( AttendanceDetail detail : attendanceDetailList ){
			try{
				analyseAttendanceDetail( emc, detail, workDayConfigList, statisticalCycleMap, debugger );
			}catch(Exception e){
				logger.info( "employee attenance detail analyse got an exception:["+detail.getEmpName()+"]["+detail.getRecordDateString()+"]");
				logger.error(e);
			}
		}
		logger.info( "employee attendance details by empname, startdate and end date analyse over！" );;
		return true;
	}

	/**
	 * 체크 데이터 한 건 분석
	 * @param emc
	 * @param detailId
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetail( EntityManagerContainer emc, String detailId, Boolean debugger ) throws Exception{
		if( StringUtils.isNotEmpty( detailId )){
			return analyseAttendanceDetail( emc, emc.find( detailId, AttendanceDetail.class ),
					workDayConfigService.getAllWorkDayConfigWithCache(debugger),
					statisticalCycleService.getAllStatisticalCycleMapWithCache(debugger),
					debugger );
		}else{
			logger.info( "detailId가 비어 있어 분석을 계속할 수 없습니다." );
		}
		return false;
	}

	/**
	 * 체크 데이터 한 건 분석
	 * @param emc
	 * @param detail
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetail( EntityManagerContainer emc, AttendanceDetail detail, Boolean debugger ) throws Exception{
		if( detail != null ){
			return analyseAttendanceDetail( emc, detail,
					workDayConfigService.getAllWorkDayConfigWithCache(debugger),
					statisticalCycleService.getAllStatisticalCycleMapWithCache(debugger),
					debugger );
		}else{
			logger.info( "detail이 비어 있어 분석을 계속할 수 없습니다." );
		}
		return false;
	}

	/**
	 * 체크 데이터 한 건 상세 분석
	 * @param emc
	 * @param detail
	 * @param workDayConfigList
	 * @param statisticalCycleMap
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public Boolean analyseAttendanceDetail( EntityManagerContainer emc, AttendanceDetail detail, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap, Boolean debugger ) throws Exception{
		List<String> ids_temp = null;
		AttendanceScheduleSetting scheduleSetting = null;
		List<AttendanceSelfHoliday> selfHolidayList = null;
		if( detail != null ){
			selfHolidayList = attendanceSelfHolidayService.listWithPersonFromCache(emc, detail.getEmpName(), false );
			//사용자에 맞는 근무 일정 설정 조회
			if(StringUtils.isNotEmpty(detail.getUnitName())){
				scheduleSetting = attendanceScheduleSettingService.getAttendanceScheduleSettingWithDetail( detail, debugger );
			}else{
				scheduleSetting = attendanceScheduleSettingService.getAttendanceScheduleSettingWithPerson( detail.getEmpName(), debugger );
			}
			return analyseAttendanceDetail( emc, detail, scheduleSetting, selfHolidayList, workDayConfigList, statisticalCycleMap, debugger );

		}else{
			logger.warn( "attendance detail is null, system can not analyse." );
		}
		return false;
	}

	/**
	 * 체크 데이터 한 건 상세 분석
	 * @param emc
	 * @param detail
	 * @param scheduleSetting
	 * @param selfHolidayList
	 * @param workDayConfigList
	 * @param statisticalCycleMap
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	private Boolean analyseAttendanceDetail( EntityManagerContainer emc, AttendanceDetail detail,
											AttendanceScheduleSetting scheduleSetting,
											List<AttendanceSelfHoliday> selfHolidayList,
											List<AttendanceWorkDayConfig> workDayConfigList,
											Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap,
			Boolean debugger ) throws Exception{

		if( detail != null ){
			logger.debug( debugger, "attendance detail analysing, employee name:["+detail.getEmpName()+"]-["+detail.getRecordDateString()+"]......" );
			Boolean check = true;


			if( scheduleSetting == null ){
				check = false;
				saveAnalyseResultAndStatus( emc, detail.getId(), -1, "직원[" + detail.getEmpName() + "]의 소속 조직 근무 일정을 찾을 수 없습니다" );
			}

			//근태 출퇴근 기록 초기화
			if( check ){
				//다시 조회해야 이 emc 안에서 트랜잭션 관리가 가능함
				detail = emc.find( detail.getId(), AttendanceDetail.class );
				if( detail != null ){
					//체크 데이터의 분석된 상태를 모두 비워 미분석 데이터로 되돌림
					detail.refresh();
				}else{
					check = false;
					logger.warn( "system can not find detail, record may be deleted." );
				}
			}

			Date recordDate = null;
			if( check ){
				if( StringUtils.isNotEmpty(detail.getRecordDateString()) ){
					try{
						//날짜 문자열을 날짜 객체로 변환하여 비교·계산하기 쉽게 함
						recordDate = dateOperation.getDateFromString( detail.getRecordDateString());
					}catch(Exception e){
						saveAnalyseResultAndStatus( emc, detail.getId(), -1, "출퇴근 날짜 변환 중 오류가 발생해 recordDate를 설정하지 못했습니다. recordDateString: " + detail.getRecordDateString() );
						check = false;
					}
					if( recordDate != null ){
						detail.setRecordDate( recordDate );
						detail.setYearString( dateOperation.getYear( detail.getRecordDate() ) );
						detail.setMonthString( dateOperation.getMonth( detail.getRecordDate() ) );
					}
				}else{
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "출퇴근 날짜 변환 중 오류가 발생해 날짜 속성이 비어 있습니다" );
					check = false;
				}
			}

			if( check ){
				if( scheduleSetting != null ){
					if(StringUtils.isEmpty(detail.getTopUnitName())){
						detail.setTopUnitName(scheduleSetting.getTopUnitName());
					}
					//if(StringUtils.isEmpty(detail.getTopUnitName())){
					if(StringUtils.isEmpty(detail.getUnitName())){
						detail.setUnitName( userManagerService.getUnitNameWithPersonName( detail.getEmpName() ) );
					}
					detail.setOnWorkTime( scheduleSetting.getOnDutyTime() );
					detail.setMiddayRestStartTime( scheduleSetting.getMiddayRestStartTime() );
					detail.setMiddayRestEndTime( scheduleSetting.getMiddayRestEndTime() );
					detail.setOffWorkTime( scheduleSetting.getOffDutyTime() );
				}else{
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "근무 일정 정보가 없어 근태 기록을 분석할 수 없습니다." );
					check = false;
					logger.warn( "system can not find scheduleSetting for detail. UNIT:" + detail.getUnitName() );
				}
			}

			if( check ){
				try{
					composeSelfHolidaysForAttendanceDetail( detail, selfHolidayList, dateOperation, debugger );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse employee self holiday for detail got an exception." + detail.getEmpName() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "출퇴근·휴가 정보로 직원 휴가를 분석하는 중 오류가 발생했습니다" );
				}
			}

			if( check ){
				try{
					//System.out.println("isWeekend="+attendanceSettingServiceAdv.isWeekend( detail.getRecordDate()));
					detail.setIsWeekend( attendanceSettingServiceAdv.isWeekend( detail.getRecordDate() ));
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse record date may be weekend got an exception." + detail.getRecordDateString() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "출퇴근 날짜의 주말 여부 분석 중 오류 발생, recordDate:" + detail.getRecordDateString() );
				}
			}

			if( check ){
				try{
					detail.setIsHoliday( isHoliday( detail, workDayConfigList, dateOperation ) );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse record date may be holiday got an exception." + detail.getRecordDateString() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "출퇴근 날짜의 공휴일 여부 분석 중 오류 발생, recordDate:"+ detail.getRecordDateString() );
				}
			}

			if( check ){
				try{
					detail.setIsWorkday( isWorkday( detail, workDayConfigList, dateOperation ) );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse record date may be workday got an exception." + detail.getRecordDateString() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "출퇴근 날짜의 근무일 여부 분석 중 오류가 발생했습니다" );
				}
			}

			if( check ){
				try{
					composeStatisticCycleForAttendanceDetail( detail, statisticalCycleMap, debugger );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse detail statistic cycle got an exception." + detail.getEmpName() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "출퇴근·근무 일정 정보로 통계 주기를 분석하는 중 오류가 발생했습니다" );
				}
			}
			if( check ){
				try{
					//분석 핵심 알고리즘으로 근태 상태 분석
					detail = attendanceDetailAnalyseCoreService.analyseCore( detail, scheduleSetting, debugger );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse detail by on and off work time for advance analyse got an exception." + detail.getEmpName() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "출퇴근·근무 일정 정보로 직원 출근 현황을 분석하는 중 오류가 발생했습니다" );
				}
			}
			if( check ){
				//체크 데이터 분석 결과 저장
				emc.beginTransaction( AttendanceDetail.class );
				detail.setRecordStatus( 1 );
				detail.setDescription("직원 출퇴근 기록 분석 완료!" );
				emc.check( detail, CheckPersistType.all );
				emc.commit();
			}

			if( check ){
				//분석 성공 시 체크 데이터로 이 데이터와 관련된 통계 요청 기록
				recordStatisticRequireLog( detail, debugger );
			}
			return true;
		}else{
			logger.warn( "attendance detail is null, system can not analyse." );
		}
		return false;
	}

	/**
	 * 체크 정보를 분석하여 체크 데이터의 근태 주기 정보 계산
	 *
	 * @param detail
	 * @return
	 * @throws Exception
	 */
	private AttendanceDetail composeStatisticCycleForAttendanceDetail( AttendanceDetail detail, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger ) throws Exception{
		AttendanceStatisticalCycle attendanceStatisticalCycle = null;
		//체크 정보 소유자의 조직으로 적합한 통계 주기 객체 조회
		attendanceStatisticalCycle = statisticalCycleService.getStatisticCycleWithStartAndEnd(
				detail.getTopUnitName(),
				detail.getUnitName(),
				detail.getRecordDate(),
				topUnitAttendanceStatisticalCycleMap,
				debugger
		);
		if( attendanceStatisticalCycle != null ) {
			logger.debug( debugger, "날짜" + detail.getRecordDateString() + ", 주기" + attendanceStatisticalCycle.getCycleStartDateString() + "-" + attendanceStatisticalCycle.getCycleEndDateString() );
			detail.setCycleYear( attendanceStatisticalCycle.getCycleYear() );
			detail.setCycleMonth( attendanceStatisticalCycle.getCycleMonth() );
		}
		return detail;
	}

	/**
	 * 체크 정보를 분석하여 직원이 휴가 중인지와 휴가 시간대를 판단
	 * @param attendanceSelfHolidayList
	 * @return
	 * @throws Exception
	 */
	private AttendanceDetail composeSelfHolidaysForAttendanceDetail( AttendanceDetail detail, List<AttendanceSelfHoliday> attendanceSelfHolidayList, DateOperation dateOperation, Boolean debugger ) throws Exception {
		//현재 체크 사용자의 모든 휴가 신청 중 체크 시간이 휴가 기간 안에 있는지 조회
		Date dayWorkStart = null, dayWorkEnd = null, dayMiddle = null ;
		if( attendanceSelfHolidayList != null ){
			//체크 날짜 하루 전체가 휴가 기간 안에 있으면 종일 휴가
			try {
				dayWorkStart = dateOperation.getDateFromString( dateOperation.getDateStringFromDate(detail.getRecordDate(), "yyyy-MM-dd") + " " + detail.getOnWorkTime());
			} catch (Exception e) {
				logger.warn( "체크 날짜의 근무 시작 시간을 조회하는 중 예외가 발생했습니다." );
				logger.error(e);
				return detail;
			}
			try {
				dayWorkEnd = dateOperation.getDateFromString( dateOperation.getDateStringFromDate(detail.getRecordDate(), "yyyy-MM-dd") + " " + detail.getOffWorkTime() );
			} catch (Exception e) {
				logger.warn( "체크 날짜의 근무 종료 시간을 조회하는 중 예외가 발생했습니다." );
				logger.error(e);
				return detail;
			}
			try {
				dayMiddle = dateOperation.getDateFromString( dateOperation.getDateStringFromDate(detail.getRecordDate(), "yyyy-MM-dd") + " 12:00:00" );
			} catch (Exception e) {
				logger.warn( "체크 날짜의 점심 휴식 시간을 조회하는 중 예외가 발생했습니다." );
				logger.error(e);
				return detail;
			}
			//반복 비교하여 종일 휴가인지 확인
			for( AttendanceSelfHoliday selfHoliday : attendanceSelfHolidayList ){
				if( selfHoliday.getEndTime().getTime() > dayWorkStart.getTime() && selfHoliday.getStartTime().getTime() < dayWorkEnd.getTime() ){
					//직원 이름 비교, 직원 이름은 고유 키
					if( detail.getEmpName().equals( selfHoliday.getEmployeeName() ) ){
						if( selfHoliday.getStartTime().getTime() <= dayWorkStart.getTime() && selfHoliday.getEndTime().getTime() >= ( dayMiddle.getTime() + 2*60*60*1000 )){
							logger.debug( debugger, detail.getEmpName()+"全天请假了");
							//종일 휴가
							detail.setIsGetSelfHolidays(true);
							detail.setLeaveType(selfHoliday.getLeaveType());
							detail.setSelfHolidayDayTime("全天");
							detail.setGetSelfHolidayDays(1.0);
						}else if( selfHoliday.getEndTime().getTime() <= dayMiddle.getTime() && selfHoliday.getEndTime().getTime() > dayWorkStart.getTime()
						){
							//오전 휴가
							logger.debug( debugger, detail.getEmpName()+"上午休假了");
							detail.setIsGetSelfHolidays(true);
							detail.setLeaveType(selfHoliday.getLeaveType());
							detail.setSelfHolidayDayTime("上午");
							detail.setGetSelfHolidayDays(0.5);
						}else if( selfHoliday.getStartTime().getTime() >= dayMiddle.getTime() && selfHoliday.getStartTime().getTime() <= dayWorkEnd.getTime()
						){
							//오전 휴가
							logger.debug( debugger, detail.getEmpName()+"下午休假了");
							detail.setIsGetSelfHolidays( true );
							detail.setLeaveType(selfHoliday.getLeaveType());
							detail.setSelfHolidayDayTime("下午");
							detail.setGetSelfHolidayDays(0.5);
						}
					}
				}
			}
		}
		return detail;
	}
	
	/**
	 * 현재 날짜가 대체 근무일인지 판단
	 * @param detail
	 * @param attendanceWorkDayConfigList
	 * @return
	 */
	private boolean isWorkday(AttendanceDetail detail, List<AttendanceWorkDayConfig> attendanceWorkDayConfigList, DateOperation dateOperation) {
		if( attendanceWorkDayConfigList != null ){
			Date configDate = null;
			for( AttendanceWorkDayConfig workDayConfig : attendanceWorkDayConfigList ){
				try {
					configDate = dateOperation.getDateFromString( workDayConfig.getConfigDate() );
				} catch (Exception e) {
					logger.warn( "시스템 변환"+workDayConfig.getConfigDate()+"날짜 형식 변환 중 예외가 발생했습니다!" );
					logger.error(e);
				}
				//날짜 비교
				Calendar calendar_record = Calendar.getInstance();
				Calendar calendar_config = Calendar.getInstance();
				calendar_record.setTime( detail.getRecordDate() );
				calendar_config.setTime( configDate );				
				if( calendar_record.get( Calendar.YEAR ) == calendar_config.get( Calendar.YEAR )
					&& calendar_record.get( Calendar.MONTH ) == calendar_config.get( Calendar.MONTH )
					&& calendar_record.get( Calendar.DATE ) == calendar_config.get( Calendar.DATE )
					&& "Workday".equalsIgnoreCase( workDayConfig.getConfigType() )
				){
					return true;
				}
			}
		}	
		return false;
	}

	/**
	 * 현재 날짜가 법정 공휴일인지 판단
	 * @param detail
	 * @param attendanceWorkDayConfigList
	 * @return
	 */
	private boolean isHoliday( AttendanceDetail detail, List<AttendanceWorkDayConfig> attendanceWorkDayConfigList, DateOperation dateOperation) {
		if( attendanceWorkDayConfigList != null ){
			Date configDate = null;
			for( AttendanceWorkDayConfig workDayConfig : attendanceWorkDayConfigList ){
				try {
					configDate = dateOperation.getDateFromString( workDayConfig.getConfigDate() );
				} catch (Exception e) {
					logger.warn( "시스템 변환"+workDayConfig.getConfigDate()+"날짜 형식 변환 중 예외가 발생했습니다!" );
					logger.error(e);
				}
				//날짜 비교
				Calendar calendar_record = Calendar.getInstance();
				Calendar calendar_config = Calendar.getInstance();
				calendar_record.setTime( detail.getRecordDate() );
				calendar_config.setTime( configDate );				
				if( calendar_record.get( Calendar.YEAR ) == calendar_config.get( Calendar.YEAR )
					&& calendar_record.get( Calendar.MONTH ) == calendar_config.get( Calendar.MONTH )
					&& calendar_record.get( Calendar.DATE ) == calendar_config.get( Calendar.DATE )
					&& "Holiday".equalsIgnoreCase( workDayConfig.getConfigType() )
				){
					return true;
				}
			}
		}	
		return false;
	}

	/**
	 * 출퇴근 기록의 분석 상태 수정, 0-미분석, 1-분석 완료
	 * @param emc
	 * @param id
	 * @param status
	 * @param description
	 * @throws Exception
	 */
	private void saveAnalyseResultAndStatus( EntityManagerContainer emc, String id, int status, String description) throws Exception {
		AttendanceDetail detail = emc.find( id, AttendanceDetail.class );
		emc.beginTransaction( AttendanceDetail.class );
		detail.setRecordStatus( status );
		detail.setDescription( description );
		if( detail != null && detail.getEmpName() != null ){
			detail.setEmpName( detail.getEmpName().trim() );
		}
		emc.check( detail, CheckPersistType.all );
		emc.commit();
	}

	/**
	 * 체크 데이터로 이 데이터와 관련된 통계 요청 기록
	 * @param detail
	 * @param debugger
	 * @throws Exception
	 */
	public void recordStatisticRequireLog( AttendanceDetail detail, Boolean debugger ) throws Exception{
		//데이터 분석이 완료되면 통계가 필요한 정보 데이터를 기록해야 함
		AttendanceStatisticRequireLog log = null;
		AttendanceStatisticRequireLogFactory attendanceStatisticRequireLogFactory = null;
		Business business = null;
		List<AttendanceStatisticRequireLog> logList = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			attendanceStatisticRequireLogFactory = business.getAttendanceStatisticRequireLogFactory();
			//개인 월별 통계
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("PERSON_PER_MONTH", detail.getEmpName(), detail.getCycleYear(), detail.getCycleMonth(), null, "WAITING");
			if( ListTools.isEmpty(logList) ){
				logger.debug( debugger, "통계 데이터 없음: PERSON_PER_MONTH,"+ detail.getEmpName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
				log = new AttendanceStatisticRequireLog();
				log.setStatisticName("직원 월별 통계");
				log.setStatisticType("PERSON_PER_MONTH");
				log.setStatisticKey( detail.getEmpName() );
				log.setStatisticYear( detail.getCycleYear() );
				log.setStatisticMonth( detail.getCycleMonth() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( log, CheckPersistType.all);
				emc.commit();

				//근태 데이터 통계
				try {
					// ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
			}else{
				logger.debug( debugger, "통계 데이터 이미 있음: PERSON_PER_MONTH,"+ detail.getEmpName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
			}
			//조직 월별 통계
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("UNIT_PER_MONTH", detail.getUnitName(), detail.getCycleYear(), detail.getCycleMonth(), null, "WAITING");
			if( ListTools.isEmpty(logList) ){
				logger.debug( debugger, "통계 데이터 없음: UNIT_PER_MONTH,"+ detail.getUnitName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
				log = new AttendanceStatisticRequireLog();
				log.setStatisticName("조직 월별 통계");
				log.setStatisticType("UNIT_PER_MONTH");
				log.setStatisticKey( detail.getUnitName() );
				log.setStatisticYear( detail.getCycleYear() );
				log.setStatisticMonth( detail.getCycleMonth() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( log, CheckPersistType.all);
				emc.commit();
				//근태 데이터 통계
				try {
					// ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
			}else{
				logger.debug( debugger, "통계 데이터 이미 있음: UNIT_PER_MONTH,"+ detail.getUnitName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
			}
			//최상위 조직 월별 통계
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("TOPUNIT_PER_MONTH", detail.getTopUnitName(), detail.getCycleYear(), detail.getCycleMonth(), null, "WAITING");
			if( ListTools.isEmpty(logList) ){
				logger.debug( debugger, "통계 데이터 없음: TOPUNIT_PER_MONTH,"+ detail.getTopUnitName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
				log = new AttendanceStatisticRequireLog();
				log.setStatisticName("최상위 조직 월별 통계");
				log.setStatisticType("TOPUNIT_PER_MONTH");
				log.setStatisticKey( detail.getTopUnitName() );
				log.setStatisticYear( detail.getCycleYear() );
				log.setStatisticMonth( detail.getCycleMonth() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( log, CheckPersistType.all);
				emc.commit();
				//근태 데이터 통계
				try {
					// ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
			}else{
				logger.debug( debugger, "통계 데이터 이미 있음: TOPUNIT_PER_MONTH,"+ detail.getTopUnitName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
			}
			//조직 일별 통계
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("UNIT_PER_DAY", detail.getUnitName(), null, null, detail.getRecordDateString(), "WAITING");
			if( ListTools.isEmpty(logList) ){
				logger.debug( debugger, "통계 데이터 없음: UNIT_PER_DAY,"+ detail.getUnitName() + ", null, null , "+ detail.getRecordDateString() +", WAITING" );
				log = new AttendanceStatisticRequireLog();
				log.setStatisticName("조직 일별 통계");
				log.setStatisticType("UNIT_PER_DAY");
				log.setStatisticKey( detail.getUnitName() );
				log.setStatisticYear( detail.getCycleYear() );
				log.setStatisticMonth( detail.getCycleMonth() );
				log.setStatisticDay( detail.getRecordDateString() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( log, CheckPersistType.all);
				emc.commit();
				//근태 데이터 통계
				try {
					// ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
			}else{
				logger.debug( debugger, "통계 데이터 이미 있음: UNIT_PER_DAY,"+ detail.getUnitName() + ", null, null , "+ detail.getRecordDateString() +", WAITING" );
			}
			//최상위 조직 일별 통계
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("TOPUNIT_PER_DAY", detail.getTopUnitName(), null, null, detail.getRecordDateString(), "WAITING");
			if( ListTools.isEmpty(logList) ){
				logger.debug( debugger, "통계 데이터 없음: TOPUNIT_PER_DAY,"+ detail.getTopUnitName() + ", null, null , "+ detail.getRecordDateString() +", WAITING" );
				log = new AttendanceStatisticRequireLog();
				log.setStatisticName("최상위 조직 일별 통계");
				log.setStatisticType("TOPUNIT_PER_DAY");
				log.setStatisticKey( detail.getTopUnitName() );
				log.setStatisticYear( detail.getCycleYear() );
				log.setStatisticMonth( detail.getCycleMonth() );
				log.setStatisticDay( detail.getRecordDateString() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( log, CheckPersistType.all);
				emc.commit();
				//근태 데이터 통계
				try {
					// ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
			}else{
				logger.debug( debugger, "통계 데이터 이미 있음: TOPUNIT_PER_DAY,"+ detail.getTopUnitName() + ", null, null , "+ detail.getRecordDateString() +", WAITING" );
			}
		}catch(Exception e){
			logger.warn("시스템이 데이터베이스에 통계 요청을 추가하는 중 예외가 발생했습니다" );
			logger.error(e);
		}
	}
}
