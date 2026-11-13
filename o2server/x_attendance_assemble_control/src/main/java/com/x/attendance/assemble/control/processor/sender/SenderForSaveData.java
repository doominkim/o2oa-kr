package com.x.attendance.assemble.control.processor.sender;

import java.util.List;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.processor.EntityImportDataDetail;
import com.x.attendance.assemble.control.processor.monitor.StatusImportFileDetail;
import com.x.attendance.assemble.control.processor.thread.DataProcessThreadFactory;
import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * EXCEL에서 분석한 데이터를 큐로 보내 해당 처리기가 데이터 저장 수행
 * 
 * @author O2LEE
 *
 */
public class SenderForSaveData {

	private static  Logger logger = LoggerFactory.getLogger( SenderForSaveData.class );
	
	public void execute( StatusImportFileDetail cacheImportFileStatus, Boolean debugger ) {
		
		String file_id = cacheImportFileStatus.getFileId();
		
		//데이터 검증이 완료되고 모두 성공하면 데이터를 저장할 수 있음, 먼저 단일 스레드로 데이터 삭제
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business( emc );
			AttendanceDetail detail = null;
			
			// 먼저 이 파일 ID의 모든 기록 삭제
			List<String> ids = business.getAttendanceDetailFactory().listByBatchName( file_id );
			emc.beginTransaction(AttendanceDetail.class);
			if (ids != null && ids.size() > 0) {
				for (String id : ids) {
					detail = emc.find(id, AttendanceDetail.class);
					emc.remove( detail );
				}
			}
			emc.commit();
		} catch (Exception e) {
			logger.warn( "file["+ file_id +"]，delete attendance detail got an exception.ID:" + file_id );
			logger.error( e );
		}
		
		//데이터 저장 시작, queue-consumerFileDataSaver로 전송
		logger.info("file["+ file_id +"], system will send save data to queeu, record total:" + cacheImportFileStatus.getDetailList().size() );
		
		//오류 데이터 수를 0으로 복원
		cacheImportFileStatus.setErrorCount( 0 );
		for ( EntityImportDataDetail cacheImportRowDetail : cacheImportFileStatus.getDetailList() ) {
			if( cacheImportRowDetail != null ) {
				try {
					//데이터를 큐에 넣어 큐에서 데이터 영구 저장
					cacheImportRowDetail.setFile_id( file_id );
					cacheImportRowDetail.setData_type("saveData");
					cacheImportFileStatus.increaseProcess_save_total( 1 );
					DataProcessThreadFactory.getInstance().submit( cacheImportRowDetail, debugger );
				} catch (Exception e) {
					logger.error( e );
				}
			}else {
				logger.warn( "저장할 데이터가 비어 있습니다......" );
			}
		}
	}
	
}
