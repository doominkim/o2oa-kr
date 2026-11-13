package com.x.attendance.assemble.control.jaxrs.attendanceimportfileinfo;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.entity.AttendanceImportFileInfo;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionDelete extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionDelete.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AttendanceImportFileInfo attendanceImportFileInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionFileIdEmpty();
				result.error( exception );
			}
		}
		if( check ){
			try {
				attendanceImportFileInfo = attendanceImportFileInfoServiceAdv.get(id);
				if( attendanceImportFileInfo == null ){
					check = false;
					Exception exception = new ExceptionImportFileNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceImportFileProcess( e, "ID로 인원 근태 데이터 가져오기 파일 정보를 조회하는 중 오류가 발생했습니다. ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceImportFileInfoServiceAdv.delete( id );
				result.setData( new Wo( id ) );
				
				//임시 파일이 있으면 삭제
				if( StringUtils.isNotEmpty( attendanceImportFileInfo.getTempFilePath() ) ) {
					File file = new File( attendanceImportFileInfo.getTempFilePath() );
					if( file.exists() ) {
						file.delete();
					}
				}
				
				//시스템 작업 상태 캐시 삭제
				StatusSystemImportOpt.getInstance().cleanCacheImportFileStatus( id );
				StatusSystemImportOpt.getInstance().removeCacheImportFileStatus( id );
				
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceImportFileProcess( e, "ID로 인원 근태 데이터 가져오기 파일 정보를 삭제하는 중 오류가 발생했습니다. ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}