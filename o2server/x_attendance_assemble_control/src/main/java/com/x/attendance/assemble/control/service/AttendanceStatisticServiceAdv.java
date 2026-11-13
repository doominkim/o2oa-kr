package com.x.attendance.assemble.control.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.attendance.entity.StatisticPersonForMonth;
import com.x.attendance.entity.StatisticTopUnitForDay;
import com.x.attendance.entity.StatisticTopUnitForMonth;
import com.x.attendance.entity.StatisticUnitForDay;
import com.x.attendance.entity.StatisticUnitForMonth;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class AttendanceStatisticServiceAdv {
	
	private static  Logger logger = LoggerFactory.getLogger( AttendanceStatisticServiceAdv.class );
//	private AttendanceWorkDayConfigService attendanceWorkDayConfigService = new AttendanceWorkDayConfigService();
	private AttendanceStatisticRequireLogService attendanceStatisticRequireLogService = new AttendanceStatisticRequireLogService();
	private AttendanceStatisticService attendanceStatisticService = new AttendanceStatisticService();
//	private AttendanceStatisticalCycleService attendanceStatisticalCycleService = new AttendanceStatisticalCycleService();

	/**
	 * 통계 연월 목록에 따라 월별 데이터 통계
	 */
	public void doStatistic( Boolean debugger ) {
		logger.debug( debugger, ">>>>>>>>>>>시스템 데이터 통계 시작 준비 중......" );
//		AttendanceStatisticalCycle attendanceStatisticalCycle  = null;
//		List<AttendanceWorkDayConfig> workDayConfigList = null;
		List<AttendanceStatisticRequireLog> attendanceStatisticRequireLogList = null;
//		Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap = null;
		
//		try {//먼저 모든 법정 공휴일 및 근무일 설정 목록 조회
//			workDayConfigList = attendanceWorkDayConfigService.getAllWorkDayConfigWithCache(debugger);
//		} catch ( Exception e ) {
//			logger.warn("[통계] 시스템이 당월 출퇴근 기록이 있는 직원 이름 목록을 조회하는 중 예외가 발생했습니다!" );
//			logger.error(e);
//		}
//
//		try{//모든 근태 통계 주기 정보를 조회하여 MAP으로 구성
//			statisticalCycleMap = attendanceStatisticalCycleService.getAllStatisticalCycleMapWithCache(debugger);
//		}catch(Exception e){
//			logger.warn( "[통계] 시스템이 모든 근태 통계 주기 정보를 조회 및 구성하는 중 예외가 발생했습니다." );
//			logger.error(e);
//		}
		
		//먼저 모든 통계 오류 처리
		try {
			logger.debug( debugger, ">>>>>>>>>>>복구된 통계 오류 정보 처리 준비, 모든 오류 통계를 다시 계산합니다......" );
			attendanceStatisticRequireLogService.resetStatisticError();
		} catch (Exception e) {
			logger.warn("[통계] 통계 오류 정보 재설정 중 예외가 발생했습니다!" );
			logger.error(e);
		}
		
		
		//통계 유형:PERSON_PER_MONTH|UNIT_PER_MONTH|TOPUNIT_PER_MONTH|UNIT_PER_DAY|TOPUNIT_PER_DAY
		//통계 처리 상태: WAITING|PROCESSING|COMPLETE|ERROR
		//1  직원 월별 통계
		//  1.1  직원 월별 통계가 필요한 통계 요청 수를 먼저 조회  AttendanceStatisticRequireLog
		logger.debug( debugger, ">>>>>>>>>>>시스템 통계 준비 [직원 월별 통계]......" );
		try {
			attendanceStatisticRequireLogList = attendanceStatisticRequireLogService.listByStatisticTypeAndStatus( "PERSON_PER_MONTH", "WAITING" );
		} catch (Exception e) {
			logger.warn("[통계] 시스템이 직원 월별 통계 요청 목록을 조회하는 중 예외가 발생했습니다!" );
			logger.error(e);
		}
		
		if( attendanceStatisticRequireLogList == null ){
			attendanceStatisticRequireLogList = new ArrayList<AttendanceStatisticRequireLog>();
		}		
		
		if( attendanceStatisticRequireLogList != null && !attendanceStatisticRequireLogList.isEmpty() ){
			for( AttendanceStatisticRequireLog log : attendanceStatisticRequireLogList ){
				//근태 데이터 통계 후 실행 큐로 전송
				try {
//					ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
//				logger.debug( debugger, ">>>>>>>>>>>시스템 통계 준비 [직원 월별 통계], 직원: " + log.getStatisticKey() + ", 통계 월:" + log.getStatisticYear() + "-" +log.getStatisticMonth() );
//				try {
//					attendanceStatisticalCycle = attendanceStatisticalCycleService.getStatisticCycleByEmployee( log, statisticalCycleMap, debugger );
//				} catch (Exception e) {
//					logger.warn("[통계] 시스템이 통계 요청 기록 정보로 통계 주기 정보를 조회하는 중 예외가 발생했습니다!" );
//					logger.error(e);
//				}
//				if( attendanceStatisticalCycle != null ){
//					try{
//						attendanceStatisticService.statisticEmployeeAttendanceForMonth( log, attendanceStatisticalCycle, workDayConfigList, statisticalCycleMap);
//					}catch(Exception e){
//						logger.warn( "[통계] 시스템이 요청에 따라 직원 월별 출퇴근 기록 분석 결과를 통계하는 중 예외가 발생했습니다." );
//						logger.error(e);
//					}
//				}
			}
		}
		
		
		//2  조직 월별 통계
		//  2.1 조직 월별 통계가 필요한 통계 요청 수를 먼저 조회  AttendanceStatisticRequireLog
		logger.debug( debugger, ">>>>>>>>>>>시스템 통계 준비 [조직 월별 통계]......" );
		try {
			attendanceStatisticRequireLogList = attendanceStatisticRequireLogService.listByStatisticTypeAndStatus( "UNIT_PER_MONTH", "WAITING" );
		} catch (Exception e) {
			logger.warn("[통계] 시스템이 조직 월별 통계 요청 목록을 조회하는 중 예외가 발생했습니다!" );
			logger.error(e);
		}
		if( attendanceStatisticRequireLogList != null && !attendanceStatisticRequireLogList.isEmpty() ){
			for( AttendanceStatisticRequireLog log : attendanceStatisticRequireLogList ){
				//근태 데이터 통계 후 실행 큐로 전송
				try {
//					ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
//				logger.debug( debugger, ">>>>>>>>>>>시스템 통계 준비 [조직 월별 통계], 조직: " + log.getStatisticKey() + ", 통계 월:" + log.getStatisticYear() + "-" +log.getStatisticMonth() );
//				try{
//					attendanceStatisticService.statisticUnitAttendanceForMonth( log, workDayConfigList, statisticalCycleMap );
//				}catch(Exception e){
//					logger.warn( "[통계] 시스템이 요청에 따라 직원 월별 출퇴근 기록 분석 결과를 통계하는 중 예외가 발생했습니다." );
//					logger.error(e);
//				}
			}
		}		
		
		//3  최상위 조직 월별 통계
		//  3.1 최상위 조직 월별 통계가 필요한 통계 요청 수를 먼저 조회  AttendanceStatisticRequireLog
		logger.debug( debugger, ">>>>>>>>>>>시스템 통계 준비 [최상위 조직 월별 통계]......" );
		try {
			attendanceStatisticRequireLogList = attendanceStatisticRequireLogService.listByStatisticTypeAndStatus( "TOPUNIT_PER_MONTH", "WAITING" );
		} catch (Exception e) {
			logger.warn("[통계] 시스템이 최상위 조직 월별 통계 요청 목록을 조회하는 중 예외가 발생했습니다!" );
			logger.error(e);
		}
		if( attendanceStatisticRequireLogList != null && !attendanceStatisticRequireLogList.isEmpty() ){
			for( AttendanceStatisticRequireLog log : attendanceStatisticRequireLogList ){
				//근태 데이터 통계 후 실행 큐로 전송
				try {
//					ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
//				logger.debug( debugger, ">>>>>>>>>>>시스템 통계 준비 [최상위 조직 월별 통계], 최상위 조직: " + log.getStatisticKey() + ", 통계 월:" + log.getStatisticYear() + "-" +log.getStatisticMonth() );
//				try{
//					attendanceStatisticService.statisticTopUnitAttendanceForMonth( log, workDayConfigList, statisticalCycleMap);
//				}catch(Exception e){
//					logger.warn( "[통계] 시스템이 요청에 따라 직원 월별 출퇴근 기록 분석 결과를 통계하는 중 예외가 발생했습니다." );
//					logger.error(e);
//				}
			}
		}		
		
		//4  조직 일별 통계
		//  4.1 조직 일별 통계가 필요한 통계 요청 수를 먼저 조회  AttendanceStatisticRequireLog
		logger.debug( debugger, ">>>>>>>>>>>시스템 통계 준비 [조직 일별 통계]......" );
		try {
			attendanceStatisticRequireLogList = attendanceStatisticRequireLogService.listByStatisticTypeAndStatus( "UNIT_PER_DAY", "WAITING" );
		} catch (Exception e) {
			logger.warn("[통계] 시스템이 조직 일별 통계 요청 목록을 조회하는 중 예외가 발생했습니다!" );
			logger.error(e);
		}
		if( attendanceStatisticRequireLogList != null && !attendanceStatisticRequireLogList.isEmpty() ){
			for( AttendanceStatisticRequireLog log : attendanceStatisticRequireLogList ){
				//근태 데이터 통계 후 실행 큐로 전송
				try {
//					ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
//				logger.debug( debugger, ">>>>>>>>>>>시스템 통계 준비 [조직 월별 통계], 조직: " + log.getStatisticKey() + ", 통계 일자:" + log.getStatisticDay() );
//				try{
//					attendanceStatisticService.statisticUnitAttendanceForDay( log, workDayConfigList, statisticalCycleMap, debugger );
//				}catch(Exception e){
//					logger.warn( "[통계] 시스템이 요청에 따라 조직 일별 출퇴근 기록 분석 결과를 통계하는 중 예외가 발생했습니다." );
//					logger.error(e);
//				}
			}
		}
		
		//5  최상위 조직 일별 통계
		//  5.1 최상위 조직 일별 통계가 필요한 통계 요청 수를 먼저 조회  AttendanceStatisticRequireLog
		logger.debug( debugger, ">>>>>>>>>>>시스템 통계 준비 [최상위 조직 일별 통계]......" );
		try {
			attendanceStatisticRequireLogList = attendanceStatisticRequireLogService.listByStatisticTypeAndStatus( "TOPUNIT_PER_DAY", "WAITING" );
		} catch (Exception e) {
			logger.warn("[통계] 시스템이 최상위 조직 일별 통계 요청 목록을 조회하는 중 예외가 발생했습니다!" );
			logger.error(e);
		}
		if( attendanceStatisticRequireLogList != null && !attendanceStatisticRequireLogList.isEmpty() ){
			for( AttendanceStatisticRequireLog log : attendanceStatisticRequireLogList ){
				//근태 데이터 통계 후 실행 큐로 전송
				try {
//					ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
//				logger.debug( debugger, ">>>>>>>>>>>시스템 통계 준비 [최상위 조직 월별 통계], 최상위 조직: " + log.getStatisticKey() + ", 통계 일자:" + log.getStatisticDay() );
//				try{
//					attendanceStatisticService.statisticTopUnitAttendanceForDay( log, workDayConfigList, statisticalCycleMap );
//				}catch(Exception e){
//					logger.warn( "[통계] 시스템이 요청에 따라 최상위 조직 일별 출퇴근 기록 분석 결과를 통계하는 중 예외가 발생했습니다." );
//					logger.error(e);
//				}
			}
		}
		logger.debug( debugger, ">>>>>>>>>>>시스템 데이터 통계 실행 완료." );
	}

	public List<String> listPersonForMonthByUserYearAndMonth(String name, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listPersonForMonthByUserYearAndMonth( emc, name, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<StatisticPersonForMonth> listPersonForMonth(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listPersonForMonth( emc, ids );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listPersonForMonthByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listPersonForMonthByUnitYearAndMonth( emc, unitNameList, year, month);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	//불필요한 조직과 사용자 제외
	public List<String> listPersonForMonthByUnitYearMonthAndUn(List<String> unitNameList,List<String> unUnitNameList,List<String> personNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listPersonForMonthByUnitYearMonthAndUn( emc, unitNameList,unUnitNameList,personNameList, year, month);
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listUnitForMonthByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listUnitForMonthByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listUnitForMonthByUnitYearAndMonth( String name, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listUnitForMonthByUnitYearAndMonth( emc, name, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<StatisticUnitForMonth> listUnitForMonth( List<String> ids ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listUnitForMonth( emc, ids );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 조직 이름, 통계 연월로 최상위 조직 모든 사용자 결근 건수 합계 통계
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Double sumUnitForMonth_AbsenceDayCount_ByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumUnitForMonth_AbsenceDayCount_ByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 조직 이름, 통계 연월로 최상위 조직 모든 사용자 휴가 건수 합계 통계
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Double sumUnitForMonth_OnSelfHolidayCount_ByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumUnitForMonth_OnSelfHolidayCount_ByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 조직 이름, 통계 연월로 최상위 조직 모든 사용자 지각 횟수 합계 통계
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumUnitForMonth_LateCount_ByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumUnitForMonth_LateCount_ByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 조직 이름, 통계 연월로 최상위 조직 모든 사용자 조퇴 건수 합계 통계
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumUnitForMonth_LeaveEarlyCount_ByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumUnitForMonth_LeaveEarlyCount_ByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 조직 이름, 통계 연월로 최상위 조직 모든 사용자 출근 체크 건수 합계 통계
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumUnitForMonth_OnDutyCount_ByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumUnitForMonth_OnDutyCount_ByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 조직 이름, 통계 연월로 최상위 조직 모든 사용자 퇴근 체크 건수 합계 통계
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumUnitForMonth_OffDutyCount_ByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumUnitForMonth_OffDutyCount_ByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 조직 이름, 통계 연월로 최상위 조직 모든 사용자 비정상 체크 횟수 합계 통계
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumUnitForMonth_AbNormalDutyCount_ByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumUnitForMonth_AbNormalDutyCount_ByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 조직 이름, 통계 연월로 최상위 조직 모든 사용자 근무시간 부족 건수 합계 통계
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumUnitForMonth_LackOfTimeCount_ByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumUnitForMonth_LackOfTimeCount_ByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 조직 이름, 통계 연월로 최상위 조직 모든 사용자 출근 인일 합계 통계
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Object sumUnitForMonth_AttendanceDayCount_ByUnitYearAndMonth(List<String> unitNameList,
			String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumUnitForMonth_AttendanceDayCount_ByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listStatisticTopUnitForMonth_ByTopUnitYearAndMonth(String name, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listStatisticTopUnitForMonth_ByTopUnitYearAndMonth( emc, name, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<StatisticTopUnitForMonth> listTopUnitForMonth( List<String> ids ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listTopUnitForMonth( emc, ids );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listStatisticUnitForDay_ByUnitDayYearAndMonth(List<String> unitNames, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listStatisticUnitForDay_ByUnitDayYearAndMonth( emc, unitNames, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<StatisticUnitForDay> listUnitForDay(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listUnitForDay( emc, ids );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listStatisticTopUnitForDay_ByNameYearAndMonth(String name, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listStatisticTopUnitForDay_ByNameYearAndMonth( emc, name, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<StatisticTopUnitForDay> listTopUnitForDay(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listTopUnitForDay( emc, ids );	
		} catch ( Exception e ) {
			throw e;
		}
	}

    public void statisticEmployeeAttendanceForMonth(AttendanceStatisticRequireLog log, AttendanceStatisticalCycle attendanceStatisticalCycle, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap) throws Exception {
		attendanceStatisticService.statisticEmployeeAttendanceForMonth( log, attendanceStatisticalCycle, workDayConfigList, statisticalCycleMap);
	}

	public void statisticUnitAttendanceForMonth(AttendanceStatisticRequireLog log, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap) throws Exception {
		attendanceStatisticService.statisticUnitAttendanceForMonth( log, workDayConfigList, statisticalCycleMap );
	}

	public void statisticTopUnitAttendanceForMonth(AttendanceStatisticRequireLog log, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap) throws Exception {
		attendanceStatisticService.statisticTopUnitAttendanceForMonth( log, workDayConfigList, statisticalCycleMap);
	}

	public void statisticUnitAttendanceForDay(AttendanceStatisticRequireLog log, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap, boolean b) throws Exception {
		attendanceStatisticService.statisticUnitAttendanceForDay( log, workDayConfigList, statisticalCycleMap, false );
	}

	public void statisticTopUnitAttendanceForDay(AttendanceStatisticRequireLog log, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap) throws Exception {
		attendanceStatisticService.statisticTopUnitAttendanceForDay( log, workDayConfigList, statisticalCycleMap );
	}
}
