package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.assemble.control.processor.sender.SenderForAnalyseData;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionAnalyseAttendanceDetails extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionAnalyseAttendanceDetails.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String startDate, String endDate ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		List<String> personNames = null;
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap = null;
		StatusSystemImportOpt statusSystemImportOpt = StatusSystemImportOpt.getInstance();
		Boolean check = true;

		if( statusSystemImportOpt.getProcessing() ) {
			check = false;
			Exception exception = new ExceptionAttendanceDetailProcess( "근태 데이터 처리기가 데이터를 처리 중입니다. 잠시 후 다시 시도하십시오......" );
			result.error(exception);
		}
		
		if (check) {
			try {
				personNames = attendanceDetailServiceAdv.getAllAnalysenessPersonNames( startDate, endDate );
				if( personNames == null || personNames.isEmpty() ) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess( "분석할 체크 데이터가 아직 없습니다." + "시작 날짜:" + startDate + ", 종료 날짜:" + endDate );
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e, "시작 시간과 종료 시간으로 분석할 직원 이름 목록을 조회하는 중 오류가 발생했습니다." + "시작 날짜:" + startDate + ", 종료 날짜:" + endDate );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if ( check ) {
			try {
				attendanceWorkDayConfigList = attendanceWorkDayConfigServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "ID 목록으로 근무일·휴일 설정 목록을 조회하는 중 오류가 발생했습니다!" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if ( check ) {
			try {// 모든 주기 설정을 조회해 Map으로 구성
				topUnitAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles( effectivePerson.getDebugger() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "모든 통계 기간을 조회·구성하는 중 오류가 발생했습니다." );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if ( check ) {
			new SenderForAnalyseData().execute( personNames, startDate, endDate, attendanceWorkDayConfigList, topUnitAttendanceStatisticalCycleMap, effectivePerson.getDebugger() );
		}
		return result;
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}