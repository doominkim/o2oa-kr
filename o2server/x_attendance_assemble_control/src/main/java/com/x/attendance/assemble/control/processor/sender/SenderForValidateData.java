package com.x.attendance.assemble.control.processor.sender;

import java.util.ArrayList;
import java.util.List;

import com.x.attendance.assemble.control.processor.EntityImportDataDetail;
import com.x.attendance.assemble.control.processor.ImportOptDefine;
import com.x.attendance.assemble.control.processor.monitor.StatusImportFileDetail;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.assemble.control.processor.thread.DataProcessThreadFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * EXCEL에서 분석한 데이터를 큐로 보내 해당 처리기가 데이터 검증 수행
 * 
 * @author O2LEE
 *
 */
public class SenderForValidateData {

	private static  Logger logger = LoggerFactory.getLogger( SenderForValidateData.class );
	
	public void execute( int sheetIndex, int curRow, List<String> colmlist, String fileKey ) {
		
		StatusImportFileDetail cacheImportFileStatus = StatusSystemImportOpt.getInstance().getCacheImportFileStatus( fileKey );
		List<String> data = new ArrayList<>();
		for( String str : colmlist ) {
			data.add( str );
		}
		
		try {
			cacheImportFileStatus.setCurrentProcessName( ImportOptDefine.VALIDATE );
			//데이터를 큐에 넣어 큐에서 데이터 형식 검증
			cacheImportFileStatus.increaseProcess_validate_total( 1 );
			DataProcessThreadFactory.getInstance().submit( new EntityImportDataDetail( "excel", sheetIndex, curRow, fileKey, data ), StatusSystemImportOpt.getInstance().getDebugger() );
		} catch (Exception e) {
			logger.error( e );
		}
	}
	
}
