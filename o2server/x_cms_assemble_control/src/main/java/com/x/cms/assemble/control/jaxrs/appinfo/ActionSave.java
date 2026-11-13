package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Token;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
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
 * 게시판 저장
 * @author sword
 */
public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AppInfo appInfo = null;
		List<String> ids = null;
		String identityName = null;
		String unitName = null;
		String topUnitName = null;
		Boolean check = true;
		Business business = new Business(null);
		Wi wi = this.convertToWrapIn( jsonElement, Wi.class );
		if( StringUtils.isEmpty( wi.getId() )) {
			wi.setId( AppInfo.createId() );
		}
		AppInfo old_appInfo = appInfoServiceAdv.get( wi.getId() );

		if (!business.isAppCreatorManager( effectivePerson, old_appInfo)) {
			throw new ExceptionAccessDenied(effectivePerson);
		}

		if ( StringUtils.isEmpty( wi.getAppName() ) ) {
			throw new ExceptionAppInfoNameEmpty();
		}

		try {
			ids = appInfoServiceAdv.listByAppName( wi.getAppName());
			if ( ListTools.isNotEmpty( ids ) ) {
				for( String _id : ids ) {
					if( !_id.equalsIgnoreCase( wi.getId() )) {
						check = false;
						Exception exception = new ExceptionAppInfoNameAlreadyExists( wi.getAppName());
						result.error(exception);
					}
				}
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionAppInfoProcess(e, "게시판 이름으로 게시판 정보를 조회하는 중 오류가 발생했습니다. AppName:" + wi.getAppName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		identityName = wi.getIdentity();

		if (check) {
			if ( !Token.defaultInitialManager.equalsIgnoreCase( effectivePerson.getDistinguishedName()) ) {
				try {
					identityName = userManagerService.getPersonIdentity( effectivePerson.getDistinguishedName(), identityName );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAppInfoProcess( e, "사용자 소속 이름을 가져오는 중 오류가 발생했습니다. 지정 소속: " + identityName );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}else {
				identityName = Token.defaultInitialManager;
				unitName = Token.defaultInitialManager;
				topUnitName = Token.defaultInitialManager;
			}
		}

		if (check && !Token.defaultInitialManager.equals(identityName)) {
			try {
				unitName = userManagerService.getUnitNameByIdentity( identityName );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess(e, "사용자 신분 정보로 소속 조직 이름을 조회하는 중 오류가 발생했습니다. Identity:" + identityName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check && !Token.defaultInitialManager.equals(identityName)) {
			try {
				topUnitName = userManagerService.getTopUnitNameByIdentity( identityName );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess(e, "사용자 신분 정보로 소속 최상위 조직 이름을 조회하는 중 오류가 발생했습니다. Identity:" + identityName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if( StringUtils.isEmpty( wi.getDocumentType() ) ) {
				wi.setDocumentType( "정보" );
			}else {
				if( !"정보".equals(wi.getDocumentType()) && !"데이터".equals( wi.getDocumentType() )) {
					wi.setDocumentType( "정보" );
				}
			}
		}

		if (check) {
			wi.setCreatorIdentity(identityName);
			wi.setCreatorPerson(effectivePerson.getDistinguishedName());
			wi.setCreatorUnitName( unitName );
			wi.setCreatorTopUnitName( topUnitName );

			if( StringUtils.equals( "정보", wi.getDocumentType() ) && wi.getSendNotify() == null ) {
				wi.setSendNotify( true );
			}

			try {
				appInfo = appInfoServiceAdv.save( wi, wi.getConfig(), effectivePerson );
				Wo wo = new Wo();
				wo.setId( appInfo.getId() );
				result.setData( wo );

				if( old_appInfo != null ) {
					if( !old_appInfo.getAppName().equalsIgnoreCase( appInfo.getAppName() ) ||
						 !old_appInfo.getAppAlias().equalsIgnoreCase( appInfo.getAppAlias() )	) {
						//게시판 이름 또는 별칭이 수정되어 게시판 삭제 일괄 작업(분류 및 문서 대상) 정보 추가
						new CmsBatchOperationPersistService().addOperation(
								CmsBatchOperationProcessService.OPT_OBJ_APPINFO,
								CmsBatchOperationProcessService.OPT_TYPE_UPDATENAME,  appInfo.getId(), old_appInfo.getAppName(), "게시판 이름 수정: ID=" + appInfo.getId() );
					}
					if(  permissionQueryService.hasDiffrentViewPermissionInAppInfo( old_appInfo, appInfo )) {
							//게시판 이름 또는 별칭이 수정되어 게시판 삭제 일괄 작업(분류 및 문서 대상) 정보 추가
							new CmsBatchOperationPersistService().addOperation(
									CmsBatchOperationProcessService.OPT_OBJ_APPINFO,
									CmsBatchOperationProcessService.OPT_TYPE_PERMISSION,  appInfo.getId(), appInfo.getAppName(), "게시판 공개 권한 변경: ID=" + appInfo.getId() );
					}
					new LogService().log(null, effectivePerson.getDistinguishedName(), appInfo.getAppName(), appInfo.getId(), "", "", "", "APPINFO", "수정");
				}else {
					new LogService().log(null, effectivePerson.getDistinguishedName(), appInfo.getAppName(), appInfo.getId(), "", "", "", "APPINFO", "추가");
				}

				// 캐시 갱신
				CacheManager.notify(AppInfo.class);
				CacheManager.notify(AppDict.class);
				CacheManager.notify(AppDictItem.class);
				CacheManager.notify(View.class);
				CacheManager.notify(ViewCategory.class);
				CacheManager.notify(ViewFieldConfig.class);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess(e, "게시판 정보를 저장하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends AppInfo {

		private static final long serialVersionUID = -6314932919066148113L;

		@FieldDescribe("작업에 사용할 신분 지정, 선택 매개변수")
		private String identity = null;

		@FieldDescribe("게시판 설정 지원 정보, JSON")
		private String config = "{}";

		public String getConfig() { return this.config; }
		public void setConfig(final String config) { this.config = config; }
		public String getIdentity() {
			return identity;
		}
		public void setIdentity(String identity) {
			this.identity = identity;
		}
		public static WrapCopier<Wi, AppInfo> copier = WrapCopierFactory.wi( Wi.class, AppInfo.class, null, null );
	}

	public static class Wo extends WoId {

	}

}
