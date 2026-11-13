package com.x.attendance.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class AttendanceStatisticalCycleServiceAdv {

	private AttendanceStatisticalCycleService attendanceStatisticCycleService = new AttendanceStatisticalCycleService();
	private static  Logger logger = LoggerFactory.getLogger( AttendanceStatisticalCycleServiceAdv.class );
	private UserManagerService userManagerService = new UserManagerService();
	
	public List<AttendanceStatisticalCycle> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticCycleService.listAll( emc );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 조회 편의를 위해 모든 주기 설정을 Map으로 구성
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public Map<String, Map<String, List<AttendanceStatisticalCycle>>> getCycleMapFormAllCycles( Boolean debugger ) throws Exception{
		List<AttendanceStatisticalCycle> cycles = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			cycles = attendanceStatisticCycleService.listAll( emc );
			return attendanceStatisticCycleService.getCycleMapFormAllCycles( emc, cycles, debugger );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	
	/**
	 * 출퇴근 기록 상세와 주기 설정 Map으로 출퇴근 기록에 맞는 통계 주기 설정 조회, 없으면 새로 생성
	 * @param detail
	 * @param topUnitAttendanceStatisticalCycleMap
	 * @return
	 * @throws Exception
	 */
	public AttendanceStatisticalCycle getAttendanceStatisticalCycleFormMap( AttendanceDetail detail, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger ) throws Exception{
		List<AttendanceStatisticalCycle> unitCycles = null;
		Map<String, List<AttendanceStatisticalCycle>> unitAttendanceStatisticalCycleMap = null;
		String topUnitName = null, unitName = null;
		logger.info("출퇴근 기록 상세와 주기 설정 Map으로 출퇴근 기록에 맞는 통계 주기 설정 조회, 없으면 새로 생성.");
		//Map에서 최상위 조직과 조직에 해당하는 주기 설정 정보 조회
		if( topUnitAttendanceStatisticalCycleMap != null ){
			//전체 Map이 비어 있지 않으면
			unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( detail.getTopUnitName() );
			if( unitAttendanceStatisticalCycleMap != null ){
				topUnitName = detail.getTopUnitName();
				//현재 최상위 조직의 설정 정보가 있으므로 조직으로 조직 설정 목록 존재 여부 재조회 [topUnit - unit]
				unitCycles = unitAttendanceStatisticalCycleMap.get(detail.getUnitName());
				if( unitCycles != null){
					unitName = detail.getUnitName();
				}else{
					unitCycles = unitAttendanceStatisticalCycleMap.get("*");
					unitName = "*";
				}
			}else{
				//최상위 조직이 *인 Map이 있는지 확인
				unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( "*" );
				topUnitName = "*";
				if( unitAttendanceStatisticalCycleMap != null ){
					//현재 최상위 조직의 설정 정보가 있으므로 조직으로 조직 설정 목록 존재 여부 재조회 [topUnit - unit]
					unitCycles = unitAttendanceStatisticalCycleMap.get(detail.getUnitName());
					if( unitCycles != null){
						unitName = detail.getUnitName();
					}else{
						unitCycles = unitAttendanceStatisticalCycleMap.get("*");
						unitName = "*";
					}
				}else{
					unitName = "*";
				}
			}
		}else{
			topUnitName = "*";
			unitName = "*";
		}
		if( unitCycles != null && unitCycles.size() > 0 ){
			//출퇴근 기록 상세의 통계 주기 연월로 조회
			for( AttendanceStatisticalCycle attendanceStatisticalCycle : unitCycles ){
				if( detail.getCycleYear().equals(attendanceStatisticalCycle.getCycleYear())
					&& detail.getCycleMonth().equals(attendanceStatisticalCycle.getCycleMonth())
				){
					logger.info( "적용할 통계 주기 설정 정보를 찾아 조회 결과 반환......" );
					return attendanceStatisticalCycle;
				}
			}
		}else{
			logger.info("최상위 조직 ["+topUnitName+"]과 조직 ["+unitName+"]의 주기 데이터를 찾지 못해 새 통계 주기 데이터를 생성해야 합니다......1");
		}
		logger.info( "적용할 통계 주기 설정 정보를 찾지 못함......" );
		//여기까지 결과가 반환되지 않았다면 적합한 설정 레코드가 없으므로 새로 생성
		//관련 설정을 찾지 못했으므로 새 설정 생성
		DateOperation dateOperation = new DateOperation();
		String cycleMonth = dateOperation.getMonth( detail.getRecordDate() );
		if( Integer.parseInt(cycleMonth) < 10 ){
			cycleMonth = "0"+Integer.parseInt(cycleMonth);
		}
		logger.info( "cycleMonth="+cycleMonth );
		AttendanceStatisticalCycle attendanceStatisticalCycle = new AttendanceStatisticalCycle();
		attendanceStatisticalCycle.setTopUnitName(topUnitName);
		attendanceStatisticalCycle.setUnitName(unitName);
		attendanceStatisticalCycle.setCycleMonth( cycleMonth );
		attendanceStatisticalCycle.setCycleYear( dateOperation.getYear(detail.getRecordDate()) );
		attendanceStatisticalCycle.setCycleStartDate( dateOperation.getFirstDateInMonth(detail.getRecordDate()));
		attendanceStatisticalCycle.setCycleStartDateString( dateOperation.getFirstDateStringInMonth(detail.getRecordDate()));
		attendanceStatisticalCycle.setCycleEndDate(dateOperation.getLastDateInMonth(detail.getRecordDate()));
		attendanceStatisticalCycle.setCycleEndDateString(dateOperation.getLastDateStringInMonth(detail.getRecordDate()));
		attendanceStatisticalCycle.setDescription("시스템 자동 생성");
		//먼저 객체를 Map에 넣음
		if( topUnitAttendanceStatisticalCycleMap == null ){
			topUnitAttendanceStatisticalCycleMap = new HashMap<String, Map<String, List<AttendanceStatisticalCycle>>>();
		}
		unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( topUnitName );
		if( unitAttendanceStatisticalCycleMap == null ){
			unitAttendanceStatisticalCycleMap = new HashMap<String, List<AttendanceStatisticalCycle>>();
			topUnitAttendanceStatisticalCycleMap.put( topUnitName, unitAttendanceStatisticalCycleMap);
		}
		unitCycles = unitAttendanceStatisticalCycleMap.get(unitName);
		if( unitCycles == null ){
			unitCycles = new ArrayList<AttendanceStatisticalCycle>();
			unitAttendanceStatisticalCycleMap.put( unitName, unitCycles);
		}
		putDistinctCycleInList( attendanceStatisticalCycle, unitCycles, debugger );		
		logger.info("새로 생성한 통계 주기 정보 저장 준비......");
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			emc.beginTransaction(AttendanceStatisticalCycle.class);
			emc.persist( attendanceStatisticalCycle, CheckPersistType.all);
			emc.commit();
		}catch(Exception e){
			logger.info("시스템이 새 통계 주기 정보를 저장하는 중 예외가 발생했습니다!" );
			throw e;
		}
		return attendanceStatisticalCycle;
	}
	
	/**
	 * 최상위 조직, 조직, 연월로 통계 주기 설정 조회
	 * 없으면 새 주기 설정 생성
	 * 
	 * @param q_topUnitName
	 * @param q_unitName
	 * @param cycleYear
	 * @param cycleMonth
	 * @param topUnitAttendanceStatisticalCycleMap
	 * @return
	 * @throws Exception
	 */
	public AttendanceStatisticalCycle getAttendanceDetailStatisticCycle( String q_topUnitName, String q_unitName, String cycleYear, String cycleMonth, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger  ) throws Exception{
		List<AttendanceStatisticalCycle> unitCycles = null;
		Map<String, List<AttendanceStatisticalCycle>> unitAttendanceStatisticalCycleMap = null;
		Boolean hasConfig = false;
		String topUnitName = null, unitName = null;
		if( Integer.parseInt( cycleMonth ) < 10 ){
			cycleMonth = "0"+Integer.parseInt(cycleMonth);
		}

		//Map에서 최상위 조직과 조직에 해당하는 주기 설정 정보 조회
		if( topUnitAttendanceStatisticalCycleMap != null ){
			//전체 Map이 비어 있지 않으면
			unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( q_topUnitName );
			if( unitAttendanceStatisticalCycleMap != null ){
				topUnitName = q_topUnitName;
				logger.info("0>>>>>>>>>>최상위 조직 [" + topUnitName + "]의 통계 주기 설정");
				//현재 최상위 조직의 설정 정보가 있으므로 조직으로 조직 설정 목록 존재 여부 재조회 [topUnit - unit]
				unitCycles = unitAttendanceStatisticalCycleMap.get( q_unitName );
				if( unitCycles != null){
					unitName = q_unitName;
					logger.debug( debugger, "1>>>>>>>>>>조직 ["+unitName+"]의 통계 주기 설정");
				}else{
					logger.debug( debugger, "2>>>>>>>>>>조직 ["+unitName+"]의 통계 주기 설정, 조직은 *로 설정");
					unitName = "*";
					unitCycles = unitAttendanceStatisticalCycleMap.get("*");
				}
			}else{
				logger.debug( debugger, "3>>>>>>>>>>최상위 조직 ["+q_topUnitName+"]의 통계 주기 설정 없음, 최상위 조직은 *로 설정");
				//최상위 조직이 *인 Map이 있는지 확인
				unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( "*" );
				topUnitName = "*";
				if( unitAttendanceStatisticalCycleMap != null ){
					logger.debug( debugger, "4>>>>>>>>>>최상위 조직 [*]의 통계 주기 설정을 찾아 조직이 [*]인 설정 목록을 재조회");
					//현재 최상위 조직의 설정 정보가 있으므로 조직으로 조직 설정 목록 존재 여부 재조회 [topUnit - unit]
					unitCycles = unitAttendanceStatisticalCycleMap.get( q_unitName );
					if( unitCycles != null){
						logger.debug( debugger, "5>>>>>>>>>>최상위 조직 [*] 조직 ["+unitName+"]의 통계 주기 설정");
						unitName = q_unitName;
					}else{
						logger.debug( debugger, "6>>>>>>>>>>최상위 조직 [*] 조직 ["+unitName+"]의 통계 주기 설정, 조직은 *로 설정, 조직 [*]의 설정 조회");
						unitName = "*";
						unitCycles = unitAttendanceStatisticalCycleMap.get("*");
					}
				}else{
					logger.debug( debugger, "7>>>>>>>>>>최상위 조직 [*]의 통계 주기 설정 없음, 최상위 조직은 *로 설정, 시스템에 설정이 전혀 없음");
					unitName = "*";
				}
			}
		}else{
			logger.debug( debugger, "8>>>>>>>>>>통계 주기 설정 없음, 최상위 조직 *, 조직 *, 시스템에 설정이 전혀 없음");
			topUnitName = "*";
			unitName = "*";
		}
		if( unitCycles != null && unitCycles.size() > 0 ){
			//logger.debug( "주기 목록 정보 건수: " + unitCycles.size() );
			//설정 정보가 있으므로 설정 데이터가 출퇴근 기록에 필요한 주기를 충족하는지 확인
			for( AttendanceStatisticalCycle attendanceStatisticalCycle : unitCycles ){
				if( attendanceStatisticalCycle.getCycleYear().equals(cycleYear) &&
						attendanceStatisticalCycle.getCycleMonth().equals(cycleMonth)
				){
					hasConfig = true;
					return attendanceStatisticalCycle;
				}
			}
		}else{
			logger.info("최상위 조직 ["+topUnitName+"]과 조직 ["+unitName+"]의 주기 데이터를 찾지 못해 새 통계 주기 데이터를 생성해야 합니다......2");
		}

		if( !hasConfig ){
			logger.debug( debugger, ">>>>>>>>>>적합한 주기를 찾지 못해 출퇴근 기록 정보로 자연월 주기 생성");
			//관련 설정을 찾지 못했으므로 새 설정 생성
			DateOperation dateOperation = new DateOperation();
			Date day = dateOperation.getDateFromString( cycleYear + "-" + cycleMonth + "-01");

			AttendanceStatisticalCycle attendanceStatisticalCycle = new AttendanceStatisticalCycle();
			attendanceStatisticalCycle.setTopUnitName(topUnitName);
			attendanceStatisticalCycle.setUnitName(unitName);
			attendanceStatisticalCycle.setCycleMonth( cycleMonth );
			attendanceStatisticalCycle.setCycleYear( cycleYear );
			attendanceStatisticalCycle.setCycleStartDate( dateOperation.getFirstDateInMonth(day));
			attendanceStatisticalCycle.setCycleStartDateString( dateOperation.getFirstDateStringInMonth(day));
			attendanceStatisticalCycle.setCycleEndDate(dateOperation.getLastDateInMonth(day));
			attendanceStatisticalCycle.setCycleEndDateString(dateOperation.getLastDateStringInMonth(day));
			attendanceStatisticalCycle.setDescription("시스템 자동 생성");

			//먼저 객체를 Map에 넣음
			if( topUnitAttendanceStatisticalCycleMap == null ){
				topUnitAttendanceStatisticalCycleMap = new HashMap<String, Map<String, List<AttendanceStatisticalCycle>>>();
			}
			unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( topUnitName );
			if( unitAttendanceStatisticalCycleMap == null ){
				unitAttendanceStatisticalCycleMap = new HashMap<String, List<AttendanceStatisticalCycle>>();
				topUnitAttendanceStatisticalCycleMap.put( topUnitName, unitAttendanceStatisticalCycleMap);
			}
			unitCycles = unitAttendanceStatisticalCycleMap.get(unitName);
			if( unitCycles == null ){
				unitCycles = new ArrayList<AttendanceStatisticalCycle>();
				unitAttendanceStatisticalCycleMap.put( unitName, unitCycles);
			}
			putDistinctCycleInList( attendanceStatisticalCycle, unitCycles, debugger);
			/*logger.info(  ">>>>>>>>>>새로 생성한 통계 주기 정보 저장 준비......");
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				emc.beginTransaction(AttendanceStatisticalCycle.class);
				emc.persist( attendanceStatisticalCycle, CheckPersistType.all);
				emc.commit();
			}catch(Exception e){
				logger.info(  ">>>>>>>>>>시스템이 새 통계 주기 정보를 저장하는 중 예외가 발생했습니다!");
				throw e;
			}*/
			return attendanceStatisticalCycle;
		}
		return null;
	}

	/**
	 * 근태 사용자 설정, 연월로 통계 주기 설정 조회
	 * 없으면 새 주기 설정 생성
	 * @param cycleYear
	 * @param cycleMonth
	 * @param topUnitAttendanceStatisticalCycleMap
	 * @return
	 * @throws Exception
	 */
	public AttendanceStatisticalCycle getAttendanceDetailStatisticCycleByConfig(AttendanceEmployeeConfig attendanceEmployeeConfig, String cycleYear, String cycleMonth, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger  ) throws Exception{
		List<AttendanceStatisticalCycle> unitCycles = null;
		Map<String, List<AttendanceStatisticalCycle>> unitAttendanceStatisticalCycleMap = null;
		Boolean hasConfig = false;
		String q_topUnitName = attendanceEmployeeConfig.getTopUnitName();
		String q_unitName = attendanceEmployeeConfig.getUnitName();
		String topUnitName = null;
		String unitName = null;
		if( Integer.parseInt( cycleMonth ) < 10 ){
			cycleMonth = "0"+Integer.parseInt(cycleMonth);
		}
		logger.debug( debugger, "getAttendanceDetailStatisticCycleByConfig-----q_topUnitName="+q_topUnitName+"---q_unitName="+q_unitName);
		//Map에서 최상위 조직과 조직에 해당하는 주기 설정 정보 조회
		if( topUnitAttendanceStatisticalCycleMap != null ){
			//전체 Map이 비어 있지 않으면
			unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( q_topUnitName );
			if( unitAttendanceStatisticalCycleMap != null ){
				topUnitName = q_topUnitName;
				logger.debug( debugger, ">>>>>>>>>>최상위 조직 [" + topUnitName + "]의 통계 주기 설정");
				//현재 최상위 조직의 설정 정보가 있으므로 조직으로 조직 설정 목록 존재 여부 재조회 [topUnit - unit]
				unitCycles = unitAttendanceStatisticalCycleMap.get( q_unitName );
				if( unitCycles != null){
					unitName = q_unitName;
					logger.debug( debugger,  ">>>>>>>>>>조직 ["+unitName+"]의 통계 주기 설정");
				}else{
					logger.debug( debugger, ">>>>>>>>>>조직 ["+unitName+"]의 통계 주기 설정, 조직은 *로 설정");
					unitName = "*";
					unitCycles = unitAttendanceStatisticalCycleMap.get("*");
				}
			}else{
				logger.info( "3>>>>>>>>>>최상위 조직 ["+q_topUnitName+"]의 통계 주기 설정 없음, 최상위 조직은 *로 설정");
				//최상위 조직이 *인 Map이 있는지 확인
				unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( "*" );
				topUnitName = q_topUnitName;
				if( unitAttendanceStatisticalCycleMap != null ){
					logger.debug( debugger, ">>>>>>>>>>최상위 조직 ["+q_topUnitName+"]의 통계 주기 설정, 조직이 [*]인 설정 목록 재조회");
					//현재 최상위 조직의 설정 정보가 있으므로 조직으로 조직 설정 목록 존재 여부 재조회 [topUnit - unit]
					unitCycles = unitAttendanceStatisticalCycleMap.get( q_unitName );
					if( unitCycles != null){
						logger.debug( debugger, ">>>>>>>>>>최상위 조직 [*] 조직 ["+unitName+"]의 통계 주기 설정");
						unitName = q_unitName;
					}else{
						logger.debug( debugger, ">>>>>>>>>>최상위 조직 [*] 조직 ["+unitName+"]의 통계 주기 설정, 조직은 *로 설정, 조직 [*]의 설정 조회");
						unitName = "*";
						unitCycles = unitAttendanceStatisticalCycleMap.get("*");
					}
				}else{
					logger.debug( debugger, ">>>>>>>>>>최상위 조직 [*]의 통계 주기 설정 없음, 최상위 조직은 *로 설정, 시스템에 설정이 전혀 없음");
					unitName = "*";
				}
			}
		}else{
			logger.debug( debugger, ">>>>>>>>>>통계 주기 설정 없음, 최상위 조직 *, 조직 *, 시스템에 설정이 전혀 없음");
			topUnitName = q_topUnitName;
			unitName = "*";
		}
		if( unitCycles != null && unitCycles.size() > 0 ){
			//logger.debug( "주기 목록 정보 건수: " + unitCycles.size() );
			//설정 정보가 있으므로 설정 데이터가 출퇴근 기록에 필요한 주기를 충족하는지 확인
			for( AttendanceStatisticalCycle attendanceStatisticalCycle : unitCycles ){
				if( attendanceStatisticalCycle.getCycleYear().equals(cycleYear) &&
						attendanceStatisticalCycle.getCycleMonth().equals(cycleMonth)
				){
					hasConfig = true;
					return attendanceStatisticalCycle;
				}
			}
		}else{
			logger.info("최상위 조직 ["+topUnitName+"]과 조직 ["+unitName+"]의 주기 데이터를 찾지 못해 새 통계 주기 데이터를 생성해야 합니다......2");
		}

		if( !hasConfig ){
			logger.debug( debugger, ">>>>>>>>>>적합한 주기를 찾지 못해 출퇴근 기록 정보로 자연월 주기 생성");
			//관련 설정을 찾지 못했으므로 새 설정 생성
			DateOperation dateOperation = new DateOperation();
			Date day = dateOperation.getDateFromString( cycleYear + "-" + cycleMonth + "-01");

			AttendanceStatisticalCycle attendanceStatisticalCycle = new AttendanceStatisticalCycle();
			attendanceStatisticalCycle.setTopUnitName(topUnitName);
			attendanceStatisticalCycle.setUnitName(unitName);
			attendanceStatisticalCycle.setCycleMonth( cycleMonth );
			attendanceStatisticalCycle.setCycleYear( cycleYear );
			attendanceStatisticalCycle.setCycleStartDate( dateOperation.getFirstDateInMonth(day));
			attendanceStatisticalCycle.setCycleStartDateString( dateOperation.getFirstDateStringInMonth(day));
			attendanceStatisticalCycle.setCycleEndDate(dateOperation.getLastDateInMonth(day));
			attendanceStatisticalCycle.setCycleEndDateString(dateOperation.getLastDateStringInMonth(day));
			attendanceStatisticalCycle.setDescription("시스템 자동 생성");

			//먼저 객체를 Map에 넣음
			if( topUnitAttendanceStatisticalCycleMap == null ){
				topUnitAttendanceStatisticalCycleMap = new HashMap<String, Map<String, List<AttendanceStatisticalCycle>>>();
			}
			unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( topUnitName );
			if( unitAttendanceStatisticalCycleMap == null ){
				unitAttendanceStatisticalCycleMap = new HashMap<String, List<AttendanceStatisticalCycle>>();
				topUnitAttendanceStatisticalCycleMap.put( topUnitName, unitAttendanceStatisticalCycleMap);
			}
			unitCycles = unitAttendanceStatisticalCycleMap.get(unitName);
			if( unitCycles == null ){
				unitCycles = new ArrayList<AttendanceStatisticalCycle>();
				unitAttendanceStatisticalCycleMap.put( unitName, unitCycles);
			}
			putDistinctCycleInList( attendanceStatisticalCycle, unitCycles, debugger);
			/*logger.info(  ">>>>>>>>>>새로 생성한 통계 주기 정보 저장 준비......");
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				emc.beginTransaction(AttendanceStatisticalCycle.class);
				emc.persist( attendanceStatisticalCycle, CheckPersistType.all);
				emc.commit();
			}catch(Exception e){
				logger.info(  ">>>>>>>>>>시스템이 새 통계 주기 정보를 저장하는 중 예외가 발생했습니다!");
				throw e;
			}*/
			return attendanceStatisticalCycle;
		}
		return null;
	}


	/**
	 * 최상위 조직, 조직, 연월로 통계 주기 설정 조회, 없으면 새 주기 설정 생성
	 * @param employeeName
	 * @param cycleYear
	 * @param cycleMonth
	 * @param topUnitAttendanceStatisticalCycleMap
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public AttendanceStatisticalCycle getStatisticCycleByEmployee( String employeeName, String cycleYear, String cycleMonth, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger ) throws Exception{
		List<AttendanceStatisticalCycle> unitCycles = null;
		Map<String, List<AttendanceStatisticalCycle>> unitAttendanceStatisticalCycleMap = null;
		boolean hasConfig = false;
		List<String> identities = null;
		String unitName = null;
		String topUnitName = null;
		
		if( Integer.parseInt(cycleMonth) < 10 ){
			cycleMonth = "0"+Integer.parseInt(cycleMonth);
		}
		
		//Map에서 최상위 조직과 조직에 해당하는 주기 설정 정보 조회
		if( topUnitAttendanceStatisticalCycleMap != null ){
			//사용자 신분 조회 필요
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				identities = userManagerService.listIdentitiesWithPerson( employeeName );
				if (identities == null || identities.size() == 0) {//해당 직원에게 현재 할당된 신분 없음
					throw new Exception("can not get identity of person:" + employeeName + ".");
				}
			}catch(Exception e){
				logger.warn("시스템이 직원 ["+employeeName+"]의 시스템 내 신분을 조회하는 중 예외가 발생했습니다!");
				throw e;
			}
			
			if( identities != null && identities.size() > 0 ){
				for( String identity : identities ){
					logger.debug( debugger, ">>>>>>>>>>신분:" + identity );
					unitName = userManagerService.getUnitNameWithIdentity( identity );
					if( unitName != null ){
						topUnitName = userManagerService.getTopUnitNameByIdentity( identity );
						//전체 Map이 비어 있지 않으면
						unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( topUnitName );
						if( unitAttendanceStatisticalCycleMap != null ){
							logger.debug( debugger, ">>>>>>>>>>최상위 조직 [" + topUnitName + "]의 통계 주기 설정");
							//현재 최상위 조직의 설정 정보가 있으므로 조직으로 조직 설정 목록 존재 여부 재조회 [topUnit - unit]
							unitCycles = unitAttendanceStatisticalCycleMap.get( unitName );
							if( unitCycles == null){
								logger.debug( debugger, ">>>>>>>>>>조직 ["+unitName+"]의 통계 주기 설정, 조직은 *로 설정");
								unitName = "*";
								unitCycles = unitAttendanceStatisticalCycleMap.get("*");
							}
						}else{
							logger.debug( debugger, ">>>>>>>>>>최상위 조직 ["+topUnitName+"]의 통계 주기 설정 없음, 최상위 조직은 *로 설정");
							//최상위 조직이 *인 Map이 있는지 확인
							unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( "*" );
							topUnitName = "*";
							if( unitAttendanceStatisticalCycleMap != null ){
								logger.debug( debugger, ">>>>>>>>>>최상위 조직 [*]의 통계 주기 설정을 찾아 조직이 [*]인 설정 목록을 재조회");
								//현재 최상위 조직의 설정 정보가 있으므로 조직으로 조직 설정 목록 존재 여부 재조회 [topUnit - unit]
								unitCycles = unitAttendanceStatisticalCycleMap.get( unitName );
								if( unitCycles == null){
									//logger.debug("최상위 조직 [*] 조직 ["+detail.getUnitName()+"]의 통계 주기 설정 없음, 조직은 *로 설정, 조직 [*]의 설정 조회");
									unitName = "*";
									unitCycles = unitAttendanceStatisticalCycleMap.get("*");
								}
							}else{
								logger.debug( debugger, ">>>>>>>>>>최상위 조직 [*]의 통계 주기 설정 없음, 최상위 조직은 *로 설정, 시스템에 설정이 전혀 없음");
								unitName = "*";
							}
						}
					}					
					if( unitCycles != null && unitCycles.size() > 0 ){
						logger.debug( debugger, ">>>>>>>>>>주기 목록 정보 건수:" + unitCycles.size() );
						//설정 정보가 있으므로 설정 데이터가 출퇴근 기록에 필요한 주기를 충족하는지 확인
						for( AttendanceStatisticalCycle attendanceStatisticalCycle : unitCycles ){
							if( attendanceStatisticalCycle.getCycleYear().equals(cycleYear) && attendanceStatisticalCycle.getCycleMonth().equals(cycleMonth)){
								hasConfig = true;
								return attendanceStatisticalCycle;
							}
						}
					}
					
				}
			}
		}else{
			logger.debug( debugger, ">>>>>>>>>>통계 주기 설정 없음, 최상위 조직 *, 조직 *, 시스템에 설정이 전혀 없음");
			topUnitName = "*";
			unitName = "*";
		}
		
		if( !hasConfig ){
			logger.debug( debugger, ">>>>>>>>>>적합한 주기를 찾지 못해 출퇴근 기록 정보로 자연월 주기 생성");
			//관련 설정을 찾지 못했으므로 새 설정 생성
			DateOperation dateOperation = new DateOperation();
			Date day = dateOperation.getDateFromString( cycleYear + "-" + cycleMonth + "-01");
			
			AttendanceStatisticalCycle attendanceStatisticalCycle = new AttendanceStatisticalCycle();
			attendanceStatisticalCycle.setTopUnitName( topUnitName );
			attendanceStatisticalCycle.setUnitName( unitName );
			attendanceStatisticalCycle.setCycleMonth( cycleMonth );
			attendanceStatisticalCycle.setCycleYear( cycleYear );
			attendanceStatisticalCycle.setCycleStartDate( dateOperation.getFirstDateInMonth(day));
			attendanceStatisticalCycle.setCycleStartDateString( dateOperation.getFirstDateStringInMonth(day));
			attendanceStatisticalCycle.setCycleEndDate(dateOperation.getLastDateInMonth(day));
			attendanceStatisticalCycle.setCycleEndDateString(dateOperation.getLastDateStringInMonth(day));
			attendanceStatisticalCycle.setDescription("시스템 자동 생성");
			
			//먼저 객체를 Map에 넣음
			if( topUnitAttendanceStatisticalCycleMap == null ){
				topUnitAttendanceStatisticalCycleMap = new HashMap<String, Map<String, List<AttendanceStatisticalCycle>>>();
			}
			unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get( topUnitName );
			if( unitAttendanceStatisticalCycleMap == null ){
				unitAttendanceStatisticalCycleMap = new HashMap<String, List<AttendanceStatisticalCycle>>();
				topUnitAttendanceStatisticalCycleMap.put( topUnitName, unitAttendanceStatisticalCycleMap);
			}
			unitCycles = unitAttendanceStatisticalCycleMap.get( unitName );
			if( unitCycles == null ){
				unitCycles = new ArrayList<AttendanceStatisticalCycle>();
				unitAttendanceStatisticalCycleMap.put( unitName, unitCycles);
			}
			putDistinctCycleInList( attendanceStatisticalCycle, unitCycles, debugger);			
			logger.debug( debugger, ">>>>>>>>>>새로 생성한 통계 주기 정보 저장 준비......");
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				emc.beginTransaction(AttendanceStatisticalCycle.class);
				emc.persist( attendanceStatisticalCycle, CheckPersistType.all);
				emc.commit();
			}catch(Exception e){
				logger.warn("시스템이 새 통계 주기 정보를 저장하는 중 예외가 발생했습니다!");
				throw e;
			}
			return attendanceStatisticalCycle;
		}
		return null;
	}

	/**
	 * 단일 객체를 List에 넣고 중복 제거
	 * @param cycle
	 * @param unitCycles
	 * @param debugger
	 * @return
	 */
	public List<AttendanceStatisticalCycle> putDistinctCycleInList( AttendanceStatisticalCycle cycle, List<AttendanceStatisticalCycle> unitCycles, Boolean debugger ) {
		if( unitCycles == null ){
			unitCycles = new ArrayList<AttendanceStatisticalCycle>();
		}
		logger.debug( debugger, ">>>>>>>>>>조회: topUnitName="+cycle.getTopUnitName()+", unitName="+cycle.getUnitName()+", cycleYear="+cycle.getCycleYear()+", cycleMonth="+ cycle.getCycleMonth() );
		for( AttendanceStatisticalCycle attendanceStatisticalCycle : unitCycles ){
			logger.debug( debugger, ">>>>>>>>>>순회: topUnitName="+attendanceStatisticalCycle.getTopUnitName()+", unitName="+attendanceStatisticalCycle.getUnitName()+", cycleYear="+attendanceStatisticalCycle.getCycleYear()+", cycleMonth="+ attendanceStatisticalCycle.getCycleMonth() );
			if( attendanceStatisticalCycle.getTopUnitName().equals(cycle.getTopUnitName())
					&& attendanceStatisticalCycle.getUnitName().equals(cycle.getUnitName())
					&& attendanceStatisticalCycle.getCycleYear().equals(cycle.getCycleYear())
					&& attendanceStatisticalCycle.getCycleMonth().equals(cycle.getCycleMonth())
					
			){
				//객체가 이미 존재하므로 추가하지 않고 기존 List 반환
				logger.debug( debugger, ">>>>>>>>>>조회 성공, 객체가 이미 존재합니다!");
				return unitCycles;
			}
		}
		logger.debug( debugger, ">>>>>>>>>>추가: topUnitName="+cycle.getTopUnitName()+", unitName="+cycle.getUnitName()+", cycleYear="+cycle.getCycleYear()+", cycleMonth="+ cycle.getCycleMonth() );
		//반복이 끝났으면 존재하지 않으므로 객체 추가
		unitCycles.add( cycle );
		return unitCycles;
	}

    public Map<String, Map<String, List<AttendanceStatisticalCycle>>> getAllStatisticalCycleMapWithCache(boolean debugger) throws Exception {
		return attendanceStatisticCycleService.getAllStatisticalCycleMapWithCache(debugger);
    }

	/**
	 * 최상위 조직, 조직, 연월로 통계 주기 설정 조회, 없으면 새 주기 설정 생성
	 * @param attendanceStatisticRequireLog
	 * @param topUnitAttendanceStatisticalCycleMap
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public AttendanceStatisticalCycle getStatisticCycleByEmployee( AttendanceStatisticRequireLog attendanceStatisticRequireLog, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger ) throws Exception{
		return attendanceStatisticCycleService.getStatisticCycleByEmployee( attendanceStatisticRequireLog, topUnitAttendanceStatisticalCycleMap, debugger);
	}

	public void checkAttendanceStatisticCycle(AttendanceStatisticalCycle cycle){
		System.out.println("checkAttendanceStatisticCycle----start");
			if(cycle!=null){
				Boolean hasCofig = false;
				List<AttendanceStatisticalCycle> cycles = null;
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					cycles = attendanceStatisticCycleService.listAll( emc );
					if(cycles.size()>0){
						for( AttendanceStatisticalCycle attendanceStatisticalCycle : cycles ){
							System.out.println( ">>>>>>>>>>遍历：cycles="+attendanceStatisticalCycle.getTopUnitName()+", unitName="+attendanceStatisticalCycle.getUnitName()+", cycleYear="+attendanceStatisticalCycle.getCycleYear()+", cycleMonth="+ attendanceStatisticalCycle.getCycleMonth() );
							if( attendanceStatisticalCycle.getTopUnitName().equals(cycle.getTopUnitName())
									&& attendanceStatisticalCycle.getUnitName().equals(cycle.getUnitName())
									&& attendanceStatisticalCycle.getCycleYear().equals(cycle.getCycleYear())
									&& attendanceStatisticalCycle.getCycleMonth().equals(cycle.getCycleMonth())

							){
								//객체가 이미 존재하므로 저장하지 않음
								hasCofig = true;
								logger.info( ">>>>>>>>>>조회 성공, 객체가 이미 존재합니다!");
							}
						}
					}
				} catch ( Exception e ) {
					logger.info( "근태 통계 주기 정보를 찾을 수 없습니다.......");
				}
				if(!hasCofig){
					this.saveAttendanceStatisticCycle(cycle);
				}
			}
		System.out.println("checkAttendanceStatisticCycle----end");
	}
	public void saveAttendanceStatisticCycle(AttendanceStatisticalCycle cycle){
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.info( "saveAttendanceStatisticCycle>>>>>>>>>>새로 생성한 통계 주기 정보 저장 준비......");
			emc.beginTransaction(AttendanceStatisticalCycle.class);
			emc.persist( cycle, CheckPersistType.all);
			emc.commit();
		}catch ( Exception e ) {
			logger.info( "새로 생성한 통계 주기 정보 저장 오류.......");
		}
	}
}

