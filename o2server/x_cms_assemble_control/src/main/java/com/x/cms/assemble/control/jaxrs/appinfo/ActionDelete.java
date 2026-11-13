package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.cms.core.entity.element.File;
import com.x.cms.core.entity.element.Script;
import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;
import com.x.cms.assemble.control.service.CmsBatchOperationProcessService;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

/**
 * 게시판 정보 삭제
 * @author sword
 */
public class ActionDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionDelete.class );

	@AuditLog(operation = "删除栏目信息")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;

		Business business = new Business(null);

		AppInfo appInfo = appInfoServiceAdv.get( id );
		if( appInfo == null ){
			throw new ExceptionAppInfoNotExists( id );
		}

		if (!business.isAppInfoManager(effectivePerson, appInfo)) {
			throw new ExceptionAccessDenied(effectivePerson);
		}

		Long count = 0L;
		try {
			count = appInfoServiceAdv.countCategoryByAppId( id, "全部" );
			if ( count > 0 ){
				check = false;
				Exception exception = new ExceptionAppInfoCanNotDelete( count);
				result.error( exception );
			}
		} catch ( Exception e ) {
			check = false;
			Exception exception = new ExceptionAppInfoProcess( e, "게시판 ID로 하위 분류 수를 조회하는 중 오류가 발생했습니다. ID:" + id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try {
				// 게시판 정보 삭제
				appInfoServiceAdv.delete( id, effectivePerson );
				new LogService().log( null, effectivePerson.getDistinguishedName(), appInfo.getAppName(), id, "", "", "", "APPINFO", "删除");

				//게시판 삭제 일괄 작업(분류 및 문서 대상) 정보 추가
				new CmsBatchOperationPersistService().addOperation(
						CmsBatchOperationProcessService.OPT_OBJ_APPINFO,
						CmsBatchOperationProcessService.OPT_TYPE_DELETE, id, id, "删除栏目：ID=" + id );

				//캐시 갱신
				CacheManager.notify( AppInfo.class );
				CacheManager.notify( AppDict.class );
				CacheManager.notify( AppDictItem.class );
				CacheManager.notify( View.class );
				CacheManager.notify( ViewCategory.class );
				CacheManager.notify( ViewFieldConfig.class );
				CacheManager.notify( Script.class );
				CacheManager.notify( File.class );

				Wo wo = new Wo();
				wo.setId( appInfo.getId() );
				result.setData( wo );
			} catch (Exception e) {
				Exception exception = new ExceptionAppInfoProcess( e, "根据ID执行删除CMS应用信息对象操作时发生未知异常，ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoId {

	}

}
