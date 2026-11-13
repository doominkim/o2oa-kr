package com.x.attendance.assemble.control.processor.thread;

import com.x.attendance.assemble.control.processor.EntityImportDataDetail;
import com.x.attendance.assemble.control.processor.ImportOptDefine;
import com.x.attendance.assemble.control.processor.monitor.StatusImportFileDetail;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.assemble.control.service.AttendanceDetailServiceAdv;
import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class OperatorDataSaver implements Runnable {

	private static  Logger logger = LoggerFactory.getLogger( OperatorDataSaver.class );
	
	private EntityImportDataDetail cacheImportRowDetail = null;
	
	private AttendanceDetailServiceAdv attendanceDetailServiceAdv = new AttendanceDetailServiceAdv();
	public OperatorDataSaver( EntityImportDataDetail cacheImportRowDetail, Boolean debugger ) {
		this.cacheImportRowDetail = cacheImportRowDetail ;
	}
	
	@Override
	public void run() {
		execute( cacheImportRowDetail );
	}
	
	private void execute( EntityImportDataDetail cacheImportRowDetail ) {
		
		String file_id = cacheImportRowDetail.getFile_id();
		
		StatusImportFileDetail cacheImportFileStatus = StatusSystemImportOpt.getInstance().getCacheImportFileStatus( file_id );
		
		cacheImportFileStatus.setCurrentProcessName( ImportOptDefine.SAVEDATA );
		cacheImportFileStatus.setProcessing_save( true );
		cacheImportFileStatus.setProcessing( true );
		
		AttendanceDetail attendanceDetail = null;
		Boolean check = true;
		
		attendanceDetail = new AttendanceDetail();
		attendanceDetail.setEmpNo(cacheImportRowDetail.getEmployeeNo());
		attendanceDetail.setEmpName(cacheImportRowDetail.getEmployeeName());
		attendanceDetail.setYearString(cacheImportRowDetail.getRecordYearString());
		attendanceDetail.setMonthString(cacheImportRowDetail.getRecordMonthString());
		attendanceDetail.setRecordDate( cacheImportRowDetail.getRecordDate() );
		attendanceDetail.setRecordDateString(cacheImportRowDetail.getRecordDateStringFormated());
		attendanceDetail.setOnDutyTime(cacheImportRowDetail.getOnDutyTimeFormated());
		attendanceDetail.setMorningOffDutyTime(cacheImportRowDetail.getMorningOffDutyTimeFormated());
		attendanceDetail.setAfternoonOnDutyTime(cacheImportRowDetail.getAfternoonOnDutyTimeFormated());
		attendanceDetail.setOffDutyTime(cacheImportRowDetail.getOffDutyTimeFormated());
		attendanceDetail.setRecordStatus( 0 );
		attendanceDetail.setBatchName( file_id );
		if( attendanceDetail.getId() == null ) {
			attendanceDetail.setId( AttendanceDetail.createId() );
		}
		
		
		if( check ) {
			// 1. 각 항목을 검사하여 근태 제외 사용자 명단에 있으면 저장하지 않음(미판단).
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				attendanceDetail = attendanceDetailServiceAdv.save( attendanceDetail );
				cacheImportFileStatus.sendStartTime( attendanceDetail.getRecordDate() );
				cacheImportFileStatus.sendEndTime( attendanceDetail.getRecordDate() );
				cacheImportFileStatus.increaseProcess_save_count( 1 );
			} catch (Exception e) {
				cacheImportFileStatus.increaseErrorCount( 1 );
				logger.warn( "파일["+ file_id +"], 데이터 저장 중 알 수 없는 예외가 발생했습니다. ID:" + file_id );
				logger.error(e );
			}
		}
	}	
}
