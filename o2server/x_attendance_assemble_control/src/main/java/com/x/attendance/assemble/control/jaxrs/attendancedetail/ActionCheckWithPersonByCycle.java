package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.assemble.control.processor.sender.SenderForSupplementData;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;

public class ActionCheckWithPersonByCycle extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionCheckWithPersonByCycle.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String cycleYear, String cycleMonth ) throws Exception {
		logger.info( ">>>>>>>>>>시스템이 통계 주기[" + cycleYear + "-" + cycleMonth + "]의 출퇴근 데이터 대조 시도......");
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap = null;
		List<AttendanceEmployeeConfig> attendanceEmployeeConfigList = null;
		List<AttendanceEmployeeConfig> unitConfigList = null;
		StatusSystemImportOpt statusSystemImportOpt = StatusSystemImportOpt.getInstance();
		Boolean check = true;
		
		if( statusSystemImportOpt.getProcessing() ) {
			check = false;
			Exception exception = new ExceptionAttendanceDetailProcess( "근태 데이터 처리기가 데이터를 처리 중입니다. 잠시 후 다시 시도하십시오......" );
			result.error(exception);
		}
		
		if (check) {
			//시스템이 데이터 작업 중인지 조회
			if( statusSystemImportOpt.getProcessing() ) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( "데이터를 처리 중입니다. 잠시 후 다시 작업하십시오." );
				result.error( exception );
			}else {
				statusSystemImportOpt.setProcessing( true );
			}
		}
		
		if (check) {
			if ( cycleYear == null || cycleYear.isEmpty() ) {
				check = false;
				Exception exception = new ExceptionCycleYearEmpty();
				result.error(exception);
			}
		}
		
		if (check) {
			if ( cycleMonth == null || cycleMonth.isEmpty() ) {
				check = false;
				Exception exception = new ExceptionCycleMonthEmpty();
				result.error(exception);
			}
		}
		
		if ( check ) {
			try {
				attendanceEmployeeConfigList = attendanceEmployeeConfigServiceAdv.listByConfigType( "REQUIRED" );
				if( ListTools.isEmpty( attendanceEmployeeConfigList ) ) {
					//근태 대상 사용자 설정이 없으면 전원이 근태 대상
					attendanceEmployeeConfigList = new ArrayList<>();
					AttendanceEmployeeConfig config = null;
					String identity = null;
					Unit unit = null;
					Unit topUnit = null;
					List<Person> allPersonObjs = userManagerService.listAllPersons();
					if(ListTools.isNotEmpty( allPersonObjs )){
						for( Person person : allPersonObjs ){
							identity = userManagerService.getMajorIdentityWithPerson( person.getDistinguishedName() );
							if(StringUtils.isNotEmpty(identity)){
								unit = userManagerService.getUnitWithIdentity(identity);
							}
							if( unit != null ){
								topUnit = userManagerService.getTopUnitWithUnitName( unit.getDistinguishedName() );
							}
							if( topUnit != null ){
								config = new AttendanceEmployeeConfig();
								config.setId( AttendanceEmployeeConfig.createId() );
								config.setConfigType( "REQUIRED" );
								config.setEmpInTopUnitTime("1900-01-01");
								config.setEmployeeName( person.getDistinguishedName() );
								config.setEmployeeNumber( person.getEmployee() );
								config.setUnitOu( unit.getUnique() );
								config.setUnitName(  unit.getDistinguishedName());
								config.setTopUnitName( topUnit.getDistinguishedName() );
								config.setTopUnitOu( topUnit.getUnique() );
								attendanceEmployeeConfigList.add( config );
							}
						}
					}
				}else{
					//기존 근태 사용자 설정을 사용자 단위로 해석
					unitConfigList = new ArrayList<>();
					for(AttendanceEmployeeConfig attendanceEmployeeConfig : attendanceEmployeeConfigList){
						if(StringUtils.isNotEmpty(attendanceEmployeeConfig.getEmployeeName())){
							unitConfigList.add(attendanceEmployeeConfig);
						}else if(StringUtils.isNotEmpty(attendanceEmployeeConfig.getUnitName())){
							List<String> unitPersons = userManagerService.listWithUnitSubNested(attendanceEmployeeConfig.getUnitName());
							for(String personId :unitPersons){
								Person person = userManagerService.getPersonObjByName(personId);
								if(person !=null){
									AttendanceEmployeeConfig newEmployeeConfig = new AttendanceEmployeeConfig();
									newEmployeeConfig = attendanceEmployeeConfig;
									newEmployeeConfig.setEmployeeName( person.getDistinguishedName() );
									newEmployeeConfig.setEmployeeNumber( person.getEmployee() );
									unitConfigList.add(newEmployeeConfig);
								}
							}
						}else if(StringUtils.isNotEmpty(attendanceEmployeeConfig.getTopUnitName())){
							//회사 사용자 추가
							AttendanceEmployeeConfig newEmployeeConfig = null;
							String identity = null;
							Unit unit = null;
							List<String> unitPersons = userManagerService.listWithUnitSubNested(attendanceEmployeeConfig.getTopUnitName());
							for(String personId :unitPersons){
								Person person = userManagerService.getPersonObjByName(personId);
								if(person !=null){
									newEmployeeConfig = new AttendanceEmployeeConfig();
									newEmployeeConfig.setTopUnitName(attendanceEmployeeConfig.getTopUnitName());
									newEmployeeConfig.setTopUnitOu(attendanceEmployeeConfig.getTopUnitOu());
									newEmployeeConfig.setConfigType(attendanceEmployeeConfig.getConfigType());
									identity = userManagerService.getMajorIdentityWithPerson( person.getDistinguishedName() );
									if(StringUtils.isNotEmpty(identity)){
										unit = userManagerService.getUnitWithIdentity(identity);
									}
									if( unit != null ){
										newEmployeeConfig.setUnitOu(unit.getUnique() );
										newEmployeeConfig.setUnitName( unit.getDistinguishedName());
									}
									newEmployeeConfig.setEmployeeName( person.getDistinguishedName() );
									newEmployeeConfig.setEmployeeNumber( person.getEmployee() );
									unitConfigList.add(newEmployeeConfig);
								}
							}
						}
					}
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "근태 대상자 설정 목록을 조회하는 중 오류가 발생했습니다." );
				result.error( exception );
				logger.error( e, currentPerson, request, null );
			}
		}
		
		if (check) {
			try {// 모든 통계 주기 설정을 조회해 Map으로 구성
				topUnitAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles( effectivePerson.getDebugger() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "모든 통계 기간을 조회·구성하는 중 오류가 발생했습니다." );
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			List<AttendanceEmployeeConfig> emconfigList = new ArrayList<>();
			if(ListTools.isNotEmpty(unitConfigList)){
				emconfigList.addAll(unitConfigList);
			}else{
				emconfigList.addAll(attendanceEmployeeConfigList);
			}
			//통계 주기 검사를 멀티스레드로 하여 통제 불가한 중복이 생기므로 통계 주기를 미리 검사하도록 변경
			for(AttendanceEmployeeConfig attendanceEmployeeConfig : emconfigList){
				/*AttendanceStatisticalCycle attendanceStatisticalCycle = attendanceStatisticCycleServiceAdv.getAttendanceDetailStatisticCycle(
						attendanceEmployeeConfig.getTopUnitName(),
						attendanceEmployeeConfig.getUnitName(),
						cycleYear,
						cycleMonth,
						topUnitAttendanceStatisticalCycleMap,
						false
				);*/
				AttendanceStatisticalCycle attendanceStatisticalCycle = attendanceStatisticCycleServiceAdv.getAttendanceDetailStatisticCycleByConfig(
						attendanceEmployeeConfig,
						cycleYear,
						cycleMonth,
						topUnitAttendanceStatisticalCycleMap,
						false
				);
				attendanceStatisticCycleServiceAdv.checkAttendanceStatisticCycle(attendanceStatisticalCycle);
			}
			new SenderForSupplementData().execute( emconfigList, topUnitAttendanceStatisticalCycleMap, cycleYear, cycleMonth, effectivePerson.getDebugger() );
		}
		
		return result;
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}