package com.x.attendance.assemble.control.processor.sender;

import java.util.List;
import java.util.Map;

import com.x.attendance.assemble.control.processor.EntityAnalyseData;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.assemble.control.processor.thread.DataProcessThreadFactory;
import com.x.attendance.assemble.control.service.AttendanceDetailServiceAdv;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * 분석할 모든 데이터를 데이터 분석 큐로 보내 해당 처리기가 데이터 분석 수행
 * 직원 이름별로 그룹화하여 직원 근무 일정 정보를 여러 번 조작하지 않도록 함
 * 
 * @author O2LEE
 *
 */
public class SenderForAnalyseData {

	private static  Logger logger = LoggerFactory.getLogger( SenderForAnalyseData.class );
	private AttendanceDetailServiceAdv attendanceDetailServiceAdv = new AttendanceDetailServiceAdv();
	
	public void execute(List<String> personNames, String startDate, String endDate, List<AttendanceWorkDayConfig> attendanceWorkDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger ) {
		
		StatusSystemImportOpt statusSystemImportOpt = StatusSystemImportOpt.getInstance();
		List<String> ids = null;
		
		for ( String personName : personNames ) {
			try {
				ids = attendanceDetailServiceAdv.getAllAnalysenessDetails( startDate, endDate, personName );
				if( ids != null && !ids.isEmpty() ) {
					statusSystemImportOpt.setProcessing( true );
					statusSystemImportOpt.setProcessing_analysis( true );
					statusSystemImportOpt.increaseProcess_analysis_total( ids.size() );
					DataProcessThreadFactory.getInstance().submit( new EntityAnalyseData( "analyse", personName, ids, attendanceWorkDayConfigList, topUnitAttendanceStatisticalCycleMap ), debugger );
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
	
	public void executeForce(List<String> personNames, String startDate, String endDate,Boolean forceFlag, List<AttendanceWorkDayConfig> attendanceWorkDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger ) {
			
			StatusSystemImportOpt statusSystemImportOpt = StatusSystemImportOpt.getInstance();
			List<String> ids = null;
			 
			for ( String personName : personNames ) {
				try {
					ids = attendanceDetailServiceAdv.getAllAnalysenessDetailsForce( startDate, endDate, personName ,forceFlag);
					if( ids != null && !ids.isEmpty() ) {
						statusSystemImportOpt.setProcessing( true );
						statusSystemImportOpt.setProcessing_analysis( true );
						statusSystemImportOpt.increaseProcess_analysis_total( ids.size() );
						DataProcessThreadFactory.getInstance().submit( new EntityAnalyseData( "analyse", personName, ids, attendanceWorkDayConfigList, topUnitAttendanceStatisticalCycleMap ), debugger );
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
		
	}
