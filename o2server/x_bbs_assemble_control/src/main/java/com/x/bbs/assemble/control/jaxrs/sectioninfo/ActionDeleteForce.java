package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionIdEmpty;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionInfoProcess;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionInsufficientPermission;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionNotExists;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionDeleteForce extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionDeleteForce.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;
		Wo wo = new Wo();
		BBSForumInfo forumInfo = null;
		BBSSectionInfo sectionInfo = null;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();

		if (check) {
			if ( StringUtils.isEmpty( id )) {
				check = false;
				Exception exception = new ExceptionSectionIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				sectionInfo = sectionInfoServiceAdv.get(id);
				if (sectionInfo == null) {
					check = false;
					Exception exception = new ExceptionSectionNotExists(id);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSectionInfoProcess(e, "지정한 ID로 섹션 정보를 조회하는 중 오류가 발생했습니다. ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				forumInfo = forumInfoServiceAdv.get( sectionInfo.getForumId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSectionInfoProcess(e, "ID로 BBS 포럼 분류 정보를 가져오는 중 오류가 발생했습니다! ID:" + sectionInfo.getForumId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		// 사용자의 게시판 삭제 권한 판단: 1. 시스템 관리자 2. 포럼에 설정된 관리자	
		if ( check ) {
			if ( !ThisApplication.isForumManager( effectivePerson, forumInfo ) ) {//게시판 삭제 권한이 없습니다
				check = false;
				String forumName = "없거나 삭제된 포럼";
				if( forumInfo != null ) {
					forumName = forumInfo.getForumName();
				}
				Exception exception = new ExceptionSectionInsufficientPermission( effectivePerson.getDistinguishedName(), forumName );
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				sectionInfoServiceAdv.deleteForce(id);
				wo.setId( id );

				CacheManager.notify( BBSForumInfo.class );
				CacheManager.notify( BBSSectionInfo.class );
				CacheManager.notify( BBSSubjectInfo.class );
				
				operationRecordService.sectionOperation(effectivePerson.getDistinguishedName(), sectionInfo, "DELETE", hostIp, hostName);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSectionInfoProcess(e, "지정한 ID의 섹션 정보를 삭제하는 중 오류가 발생했습니다. ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData( wo );
		return result;
	}

	public static class Wo extends WoId {

	}
}