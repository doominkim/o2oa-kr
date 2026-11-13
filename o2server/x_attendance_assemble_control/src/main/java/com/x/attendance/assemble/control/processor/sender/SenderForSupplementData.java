package com.x.attendance.assemble.control.processor.sender;

import java.util.List;
import java.util.Map;

import com.x.attendance.assemble.control.processor.EntitySupplementData;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.assemble.control.processor.thread.DataProcessThreadFactory;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * EXCEL에서 분석한 데이터를 큐로 보내 해당 처리기가 데이터 보충 수행
 * 
 * @author O2LEE
 *
 */
public class SenderForSupplementData {

	private static  Logger logger = LoggerFactory.getLogger( SenderForSupplementData.class );
	
	public void execute( List<AttendanceEmployeeConfig> attendanceEmployeeConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, String cycleYear, String cycleMonth, Boolean debugger ) {	

		StatusSystemImportOpt statusSystemImportOpt = StatusSystemImportOpt.getInstance();		

		for ( AttendanceEmployeeConfig attendanceEmployeeConfig : attendanceEmployeeConfigList ) {
			//System.out.println( ">>>>>>>>>>현재 참여자 설정: attendanceEmployeeConfig_topUnit="+attendanceEmployeeConfig.getTopUnitName()+", unitName="+attendanceEmployeeConfig.getUnitName()+", employeeName="+attendanceEmployeeConfig.getEmployeeName() );
			/*if( attendanceEmployeeConfig.getEmpInTopUnitTime() == null || attendanceEmployeeConfig.getEmpInTopUnitTime().isEmpty() ){
				logger.warn( "person["+attendanceEmployeeConfig.getEmployeeName()+"] in company date is null, system can not supplement data for person." );
				return;
			}else {*/
				try {
					statusSystemImportOpt.setProcessing( true );
					statusSystemImportOpt.setProcessing_supplement( true );
					statusSystemImportOpt.increaseProcess_supplement_total( 1 );
					DataProcessThreadFactory.getInstance().submit( new EntitySupplementData( "supplement", cycleYear, cycleMonth, attendanceEmployeeConfig, topUnitAttendanceStatisticalCycleMap ), debugger );
				} catch (Exception e) {
					logger.error( e );
				}
			//}
		}
	}
	
}
