package com.x.bbs.assemble.control.jaxrs.foruminfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionForumCanNotDelete;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionForumInfoIdEmpty;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionForumInfoNotExists;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionForumInfoProcess;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionInsufficientPermissions;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		BBSForumInfo forumInfo = null;
		Long sectionCount = 0L;
		Boolean check = true;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();

		if (check) {
			//삭제 작업 권한 판단
			try {
				if ( !ThisApplication.isBBSManager(effectivePerson) ) {
					check = false;
					logger.warn("사용자에게 BBSManager 역할이 없고 시스템 관리자도 아닙니다! USER:" + effectivePerson.getDistinguishedName());
					Exception exception = new ExceptionInsufficientPermissions(effectivePerson.getDistinguishedName(), ThisApplication.BBSMANAGER );
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionInsufficientPermissions(effectivePerson.getDistinguishedName(), ThisApplication.BBSMANAGER );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionForumInfoIdEmpty();
				result.error(exception);
			}
		}
		if (check) { // 포럼 정보 존재 여부 조회
			try {
				forumInfo = forumInfoServiceAdv.get(id);
				if (forumInfo == null) {
					check = false;
					Exception exception = new ExceptionForumInfoNotExists(id);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionForumInfoProcess(e, "ID로 BBS 포럼 분류 정보를 가져오는 중 오류가 발생했습니다! ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			// 포럼에 게시판 정보가 남아 있는지 조회
			try {
				sectionCount = sectionInfoServiceAdv.countMainSectionByForumId(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionForumInfoProcess(e, "포럼 ID로 섹션 수를 조회하는 중 오류가 발생했습니다. ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if (sectionCount > 0) {
				check = false;
				logger.warn("포럼 [" + forumInfo.getForumName() + "]에 아직 " + sectionCount + "개의 게시판이 있어 삭제를 계속할 수 없습니다! ID=" + id);
				Exception exception = new ExceptionForumCanNotDelete(
						"포럼 [" + forumInfo.getForumName() + "]에 아직 " + sectionCount + "개의 섹션이 남아 있어 삭제할 수 없습니다!");
				result.error(exception);
			}
		}
		
		if (check) {
			//게시판이 하나도 없으므로 포럼 정보와 포럼 권한 정보 삭제 가능
			try {
				forumInfoServiceAdv.delete(id);
				
				wo.setId( forumInfo.getId() );
				
				CacheManager.notify( BBSForumInfo.class );
				CacheManager.notify( BBSSectionInfo.class );
				CacheManager.notify( BBSSubjectInfo.class );
				operationRecordService.forumOperation(effectivePerson.getDistinguishedName(), forumInfo, "DELETE", hostIp, hostName);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionForumInfoProcess(e, "ID로 BBS 포럼 분류 정보를 삭제하는 중 오류가 발생했습니다. ID:" + id);
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