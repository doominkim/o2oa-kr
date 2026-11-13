package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionAnalyseAttendanceDetail extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionAnalyseAttendanceDetail.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AttendanceDetail detail = null;
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap = null;
		Boolean check = true;

		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionDetailIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				detail = attendanceDetailServiceAdv.get(id);
				if (detail == null) {
					check = false;
					Exception exception = new ExceptionDetailNotExists(id);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "ID로 근태 체크 정보를 조회하는 중 오류가 발생했습니다." + id );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				attendanceWorkDayConfigList = attendanceWorkDayConfigServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "ID 목록으로 근무일·휴일 설정 목록을 조회하는 중 오류가 발생했습니다!" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				topUnitAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles( effectivePerson.getDebugger() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "모든 통계 기간을 조회·구성하는 중 오류가 발생했습니다." );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				attendanceDetailAnalyseServiceAdv.analyseAttendanceDetail( detail, attendanceWorkDayConfigList, topUnitAttendanceStatisticalCycleMap, effectivePerson.getDebugger()  );
				logger.debug( effectivePerson, ">>>>>>>>>>attendance detail analyse completed.person:" + detail.getEmpName() + ", date:" + detail.getRecordDateString());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e, "직원 체크 정보 분석 중 오류가 발생했습니다! ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		logger.debug( effectivePerson, ">>>>>>>>>>attendance detail analyse progress exit.");
		return result;
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}