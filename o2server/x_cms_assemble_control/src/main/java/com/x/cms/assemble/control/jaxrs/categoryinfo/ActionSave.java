package com.x.cms.assemble.control.jaxrs.categoryinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.cms.assemble.control.Business;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;
import com.x.cms.assemble.control.service.CmsBatchOperationProcessService;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.element.ViewCategory;


/**
 * 분류 정보 저장
 * @author sword
 */
public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		String unitName = null;
		String topUnitName = null;
		CategoryInfo old_categoryInfo = null;
		CategoryInfo categoryInfo = null;
		Boolean check = true;

		Wi wi = this.convertToWrapIn( jsonElement, Wi.class );
		String identityName = wi.getIdentity();
		if(StringUtils.isBlank(wi.getAppId())){
			throw new ExceptionAppIdEmpty();
		}

		AppInfo appInfo = appInfoServiceAdv.get(wi.getAppId());
		if(appInfo == null){
			throw new ExceptionAppInfoNotExists(wi.getAppId());
		}

		Business business = new Business(null);
		if (!business.isAppInfoManager(effectivePerson, appInfo)) {
			throw new ExceptionAccessDenied(effectivePerson);
		}

		if (check) {
			if ( StringUtils.isEmpty( identityName ) && !"xadmin".equalsIgnoreCase(effectivePerson.getDistinguishedName())) {
				try {
					identityName = userManagerService.getPersonIdentity( effectivePerson.getDistinguishedName(), identityName);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionCategoryInfoProcess(e, "사용자 소속 이름을 가져오는 중 오류가 발생했습니다. 지정 소속: " + identityName);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			} else {
				identityName = "xadmin";
				unitName = "xadmin";
				topUnitName = "xadmin";
			}
		}

		if( check ) {
			if ( StringUtils.isEmpty( identityName ) ) {
				identityName = wi.getCreatorIdentity();
			}
		}

		if( check ) {
			if ( StringUtils.isEmpty( wi.getAppId() ) ) {
				check = false;
				Exception exception = new ExceptionAppIdEmpty();
				result.error(exception);
			}
		}

		if ( check && !"xadmin".equals( identityName ) ) {
			try {
				unitName = userManagerService.getUnitNameByIdentity(identityName);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess(e, "사용자 신분 정보로 소속 조직 이름을 조회하는 중 오류가 발생했습니다. Identity:" + identityName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check && !"xadmin".equals( identityName )) {
			try {
				topUnitName = userManagerService.getTopUnitNameByIdentity(identityName);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess(e, "사용자 신분 정보로 소속 최상위 조직 이름을 조회하는 중 오류가 발생했습니다. Identity:" + identityName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if( StringUtils.isEmpty( wi.getId() )) {
				wi.setId( CategoryInfo.createId() );
			}
			try {
				//기존 분류 정보를 먼저 조회하고, 있으면 분류 이름 변경 여부를 비교해 변경되었으면 모든 문서에 기록된 분류 이름을 수정해야 함
				old_categoryInfo = categoryInfoServiceAdv.get( wi.getId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess(e, "ID로 분류 정보를 조회하는 중 오류가 발생했습니다. ID:" + wi.getId() );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			wi.setCreatorIdentity( identityName );
			wi.setCreatorPerson( effectivePerson.getDistinguishedName() );
			wi.setCreatorUnitName(unitName);
			wi.setCreatorTopUnitName(topUnitName);

			if( StringUtils.equals( "정보", wi.getDocumentType() ) ) {
				if( wi.getSendNotify() == null ) {
					if( appInfo.getSendNotify() == null) {
						wi.setSendNotify( true );
					}else {
						//게시판의 알림 설정 상속
						wi.setSendNotify( appInfo.getSendNotify() );
					}
				}
			}else {
				//데이터는 기본값 false, 알림 안 함
				if( wi.getSendNotify() == null ) {
					wi.setSendNotify( false );
				}
			}

			try {
				CacheManager.notify(CategoryInfo.class);
				categoryInfo = categoryInfoServiceAdv.save( wi, wi.getExtContent(), effectivePerson );

				Wo wo = new Wo();
				wo.setId(categoryInfo.getId());
				result.setData(wo);

				if( old_categoryInfo != null ) {
					if( !old_categoryInfo.getCategoryName().equalsIgnoreCase( categoryInfo.getCategoryName() ) ||
							 !old_categoryInfo.getCategoryAlias().equalsIgnoreCase( categoryInfo.getCategoryAlias() )	) {
						//분류 이름이 수정되어 게시판 삭제 일괄 작업(분류 및 문서 대상) 정보 추가
						new CmsBatchOperationPersistService().addOperation(
								CmsBatchOperationProcessService.OPT_OBJ_CATEGORY,
								CmsBatchOperationProcessService.OPT_TYPE_UPDATENAME,  categoryInfo.getId(), old_categoryInfo.getCategoryName(), "분류 이름 수정: ID=" + categoryInfo.getId() );
					}
					if(  permissionQueryService.hasDiffrentViewPermissionInCategory( old_categoryInfo, categoryInfo )) {
						//게시판 이름 또는 별칭이 수정되어 게시판 삭제 일괄 작업(분류 및 문서 대상) 정보 추가
						new CmsBatchOperationPersistService().addOperation(
								CmsBatchOperationProcessService.OPT_OBJ_CATEGORY,
								CmsBatchOperationProcessService.OPT_TYPE_PERMISSION,  categoryInfo.getId(), categoryInfo.getCategoryName(), "분류 공개 권한 변경: ID=" + categoryInfo.getId() );
					}
					new LogService().log(null, effectivePerson.getDistinguishedName(), categoryInfo.getAppName() + "-" + categoryInfo.getCategoryName(), categoryInfo.getId(), "", "", "", "CATEGORY", "수정");
				}else {
					new LogService().log(null, effectivePerson.getDistinguishedName(), categoryInfo.getAppName() + "-" + categoryInfo.getCategoryName(), categoryInfo.getId(), "", "", "", "CATEGORY", "추가");
				}

				CacheManager.notify(AppInfo.class);
				CacheManager.notify(CategoryInfo.class);
				CacheManager.notify(ViewCategory.class);
				CacheManager.notify(Document.class);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess(e, "분류 정보를 저장하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends CategoryInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodify);

		public static WrapCopier<Wi, CategoryInfo> copier = WrapCopierFactory.wi(Wi.class, CategoryInfo.class, null,
				JpaObject.FieldsUnmodify);

		@FieldDescribe("사용자가 저장한 신분")
		private String identity = null;

		@FieldDescribe("확장 정보 JSON 내용")
		private String extContent = null;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public String getExtContent() {
			return extContent;
		}

		public void setExtContent(String extContent) {
			this.extContent = extContent;
		}

	}

	public static class Wo extends WoId {

	}
}
