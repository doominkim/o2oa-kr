package com.x.attendance.assemble.control.processor.thread;

import com.x.attendance.assemble.control.processor.EntitySupplementData;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.assemble.control.service.AttendanceDetailServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceEmployeeConfigServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceStatisticalCycleServiceAdv;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class OperatorDataSupplement implements Runnable{

	private static  Logger logger = LoggerFactory.getLogger( OperatorDataSupplement.class );
	private AttendanceDetailServiceAdv attendanceDetailServiceAdv = null;
	private StatusSystemImportOpt statusSystemImportOpt = null;
	private AttendanceStatisticalCycleServiceAdv attendanceStatisticCycleServiceAdv = null;
	private AttendanceEmployeeConfigServiceAdv attendanceEmployeeConfigServiceAdv = null;
	
	private EntitySupplementData entitySupplementData = null;
	private Boolean debugger = false;
	
	public OperatorDataSupplement( EntitySupplementData entitySupplementData, Boolean debugger ) {
		attendanceEmployeeConfigServiceAdv = new AttendanceEmployeeConfigServiceAdv();
		attendanceStatisticCycleServiceAdv = new AttendanceStatisticalCycleServiceAdv();
		statusSystemImportOpt = StatusSystemImportOpt.getInstance();
		attendanceDetailServiceAdv = new AttendanceDetailServiceAdv();
		this.entitySupplementData = entitySupplementData ;
		this.debugger = debugger;
	}
	
	@Override
	public void run() {
		execute( entitySupplementData );
	}
	
	private void execute( EntitySupplementData entitySupplementData ) {
		
		statusSystemImportOpt.setProcessing( true );
		statusSystemImportOpt.setProcessing_supplement( true );
		
		AttendanceEmployeeConfig attendanceEmployeeConfig = null;
		AttendanceStatisticalCycle attendanceStatisticalCycle = null;
		Boolean check = true;
		attendanceEmployeeConfig = entitySupplementData.getAttendanceEmployeeConfig();

		/*if( check ) {
			//설정 파일에 있는 사용자의 소속 조직과 최상위 조직 정보가 정상인지 보충·검사
			try {
				attendanceEmployeeConfig = attendanceEmployeeConfigServiceAdv.checkAttendanceEmployeeConfig( entitySupplementData.getAttendanceEmployeeConfig() );
			} catch (Exception e) {
				check = false;
				logger.warn( "시스템이 근태 대상 직원 설정 정보를 검사하는 중 예외가 발생했습니다." );
				logger.error(e);
			}
		}
		logger.debug( debugger, ">>>>>>>>>>시스템이 직원 근태 출퇴근 정보를 확인 및 보충하는 중: " + attendanceEmployeeConfig.getEmployeeName() );
		*/
		if( check ) {
			//조직 정보, 통계 주기 연·월로 전체 최상위 조직 통계 주기 MAP에서 적합한 통계 주기를 조회하고, 없으면 해당 조직의 새 설정을 생성
			try {
				logger.debug( debugger, ">>>>>>>>>>지정한 통계 주기 조회:"+attendanceEmployeeConfig.getTopUnitName() + ", " +
						attendanceEmployeeConfig.getUnitName() + ",  " +
						entitySupplementData.getCycleYear() + "-" +
						entitySupplementData.getCycleMonth() + "."
				);
				attendanceStatisticalCycle = attendanceStatisticCycleServiceAdv.getAttendanceDetailStatisticCycle( 
						attendanceEmployeeConfig.getTopUnitName(),
						attendanceEmployeeConfig.getUnitName(), 
						entitySupplementData.getCycleYear(), 
						entitySupplementData.getCycleMonth(),
						entitySupplementData.getTopUnitAttendanceStatisticalCycleMap(),
						debugger
				);
			} catch (Exception e) {
				check = false;
				logger.warn( "시스템이 직원의 최상위 조직과 조직 이름으로 지정한 통계 주기를 조회하는 중 예외가 발생했습니다."
						+ "TopUnit:" + attendanceEmployeeConfig.getTopUnitName()
						+ ", Unit:" + attendanceEmployeeConfig.getUnitName()
						+ ", CycleYear:" + entitySupplementData.getCycleYear()
						+ ", CycleMonth:" + entitySupplementData.getCycleMonth() );
				logger.error(e);
			}
		}
		
		if( check ) {
			if ( attendanceStatisticalCycle != null ) {
				//System.out.println( ">>>>>>>>>>통계 주기 조회됨: cycle="+attendanceStatisticalCycle.getTopUnitName()+", unitName="+attendanceStatisticalCycle.getUnitName()+", CycleStartDate="+attendanceStatisticalCycle.getCycleStartDateString()+", cycleEndDate="+ attendanceStatisticalCycle.getCycleEndDateString() );
				try {
					logger.warn( "시스템이 사용자 근태 데이터 확인 및 보충을 시도합니다."
							+ "StartDate:" + attendanceStatisticalCycle.getCycleStartDate()
							+ ", EndDate:" + attendanceStatisticalCycle.getCycleEndDate() );
					attendanceDetailServiceAdv.dataSupplement( 
							attendanceStatisticalCycle.getCycleStartDate(),
							attendanceStatisticalCycle.getCycleEndDate(), 
							attendanceEmployeeConfig
					);
				} catch (Exception e) {
					logger.warn( "시스템이 시간 목록으로 직원 출퇴근 정보를 확인·보충하는 중 예외가 발생했습니다. CycleStartDate:" + attendanceStatisticalCycle.getCycleStartDate() + ", CycleEndDate:" + attendanceStatisticalCycle.getCycleEndDate());
				}
			}else {
				logger.info( "근태 통계 주기 정보를 찾을 수 없습니다.......");
			}
		}
		
		statusSystemImportOpt.increaseProcess_supplement_count( 1 );
	}
}
