package com.x.mind.assemble.control.jaxrs.folder;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionFolderDelete;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionFolderNotExists;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindFolderQuery;
import com.x.mind.entity.MindFolderInfo;

/**
 * 폴더 정보 삭제
 * @author O2LEE
 *
 */
public class ActionFolderDelete extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionFolderDelete.class );

	@AuditLog(operation = "폴더 삭제")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String folderId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		MindFolderInfo folderInfo = null;
		Boolean check = true;
		
		if( check ){
			try {
				folderInfo = mindFolderInfoService.getWithId( folderId );
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindFolderQuery( e, "ID로 모든 마인드맵 폴더 정보를 조회하는 중 오류가 발생했습니다." );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( folderInfo == null ) {
				check = false;
				Exception exception = new ExceptionFolderNotExists( folderId );
				result.error(exception);
			}
		}
		//폴더에 마인드맵 정보가 남아 있는지 조회
		if( check ){
			Long count = mindFolderInfoService.countChildWithFolder(folderId);
			if( count > 0 ) {
				check = false;
				Exception exception = new ExceptionFolderDelete( "폴더 안에 아직 " +count+ "개의 하위 폴더가 있어 지금은 폴더를 삭제할 수 없습니다!" );
				result.error(exception);
			}
		}
		//폴더에 마인드맵 정보가 남아 있는지 조회
		if( check ){
			Long count = mindInfoService.countMindWithFolder(folderId);
			if( count > 0 ) {
				check = false;
				Exception exception = new ExceptionFolderDelete( "폴더 안에 아직 " +count+ "개의 마인드맵 파일이 있어 지금은 폴더를 삭제할 수 없습니다!" );
				result.error(exception);
			}
		}
		if( check ){
			try {
				mindFolderInfoService.delete(folderId);
				wo.setId( folderId );
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionFolderDelete( e, "{‘id’:'"+folderId+"'}" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoId {

	}
}