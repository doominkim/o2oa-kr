package com.x.cms.assemble.control.jaxrs.document;

import com.google.gson.JsonElement;
import com.x.base.core.project.Applications;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_correlation_service_processing;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionInfo;
import com.x.cms.assemble.control.wrapin.WiSiteFileInfo;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionCreateTypeCmsWo;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionUpdateWi;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.SiteTargetWi;
import java.util.List;
import java.util.Map;

/**
 * 문서 수정
 *
 */
public class ActionUpdateDocument extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateDocument.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String docId, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}, docId:{}.", effectivePerson::getDistinguishedName, () -> docId);
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Document document = documentQueryService.get(docId);
		if (document == null) {
			throw new ExceptionEntityNotExist(docId);
		}
		CategoryInfo categoryInfo = categoryInfoServiceAdv.get(document.getCategoryId());
		AppInfo appInfo = appInfoServiceAdv.get(document.getAppId());
		if (categoryInfo == null) {
			throw new ExceptionCategoryInfoNotExists(document.getCategoryId());
		}
		if (appInfo == null) {
			throw new ExceptionAppInfoNotExists(document.getAppId());
		}
		Business business = new Business(null);
		if (!business.isDocumentEditor(effectivePerson, appInfo, categoryInfo, document)) {
			throw new ExceptionAccessDenied(effectivePerson);
		}
		this.updateDocument(document, wi, effectivePerson);
		CacheManager.notify(Document.class);
		Wo wo = new Wo();
		wo.setId(docId);
		result.setData(wo);
		return result;
	}

	private void updateDocument(Document document, Wi wi, EffectivePerson effectivePerson) throws Exception {
		String docId = document.getId();
		if(wi.getData() != null && !wi.getData().isJsonNull()){
			ThisApplication.context().applications().putQuery(x_cms_assemble_control.class,
							Applications.joinQueryUri("data", "document", docId), wi.getData())
					.getData(WoId.class);
		}
		if(ListTools.isNotEmpty(wi.getPermissionList())){
			Map<String, Object> map = Map.of("docId", docId, "permissionList", wi.getPermissionList());
			ThisApplication.context().applications().postQuery(x_cms_assemble_control.class,
							Applications.joinQueryUri("docpermission"), map)
					.getData(WoId.class);
		}
		if (ListTools.isNotEmpty(wi.getCorrelationTargetList())) {
			ActionUpdateWi req = new ActionUpdateWi();
			req.setSiteTargetList(wi.getCorrelationTargetList());
			req.setPerson(effectivePerson.getDistinguishedName());
			ThisApplication.context().applications()
					.postQuery(effectivePerson.getDebugger(), x_correlation_service_processing.class,
							Applications.joinQueryUri("correlation", "update", "type", "cms", "document", docId), req, docId)
					.getData(ActionCreateTypeCmsWo.class);
		}
		if(ListTools.isNotEmpty(wi.getSiteFileInfoList())){
			Map<String, Object> map = Map.of("person", effectivePerson.getDistinguishedName(), "siteFileInfoList", wi.getSiteFileInfoList());
			ThisApplication.context().applications().postQuery(x_cms_assemble_control.class,
							Applications.joinQueryUri("fileinfo", "replace", "to", "doc", docId), map)
					.getData(WrapBoolean.class);
		}
	}

	public static class Wi {
		@FieldDescribe("문서 업무 데이터, 업무 데이터 덮어쓰기, 비어 있으면 수정하지 않음, 데이터 형식은 json 객체, 예: {\"subject\":\"제목 수정\"}.")
		private JsonElement data;

		@FieldDescribe("문서 권한 목록, 권한 교체, 비어 있으면 처리하지 않음.")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "PermissionInfo", fieldValue = "{'permission':'권한 분류: 독자|작성자|관리','permissionObjectType':'사용 대상 분류: 모든 사용자|조직|사용자|그룹','permissionObjectCode':'사용 대상 코드: 모든 사용자|조직 코드|사용자 UID|그룹 코드','permissionObjectName':'사용 대상 이름: 모든 사용자|조직 이름|사용자 이름|그룹 이름'}")
		private List<PermissionInfo> permissionList;

		@FieldDescribe("관련 문서 목록, 문서 지정 site의 관련 문서 데이터를 교체, 비어 있으면 처리하지 않음.")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "TargetWi", fieldValue = "{'site':'관련 콘텐츠 영역 식별자','targetList':[{'type':'관련 대상 유형(cms 또는 processPlatform)','bundle':'관련 대상 식별자','view':'출처 뷰'}]}")
		private List<SiteTargetWi> correlationTargetList;

		@FieldDescribe("첨부파일 목록, 문서 지정 site의 첨부파일 목록 교체, 먼저 삭제 후 추가, 비어 있으면 처리하지 않음.")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "WiSiteFileInfo", fieldValue = "{'site':'첨부파일 영역 분류','fileInfoList':[{'id':'첨부파일 id','name':'첨부파일 이름'}],'type':'첨부파일 출처(cms 또는 processPlatform)'}")
		private List<WiSiteFileInfo> siteFileInfoList;

		public JsonElement getData() {
			return data;
		}

		public void setData(JsonElement data) {
			this.data = data;
		}

		public List<PermissionInfo> getPermissionList() {
			return permissionList;
		}

		public void setPermissionList(
				List<PermissionInfo> permissionList) {
			this.permissionList = permissionList;
		}

		public List<SiteTargetWi> getCorrelationTargetList() {
			return correlationTargetList;
		}

		public void setCorrelationTargetList(
				List<SiteTargetWi> correlationTargetList) {
			this.correlationTargetList = correlationTargetList;
		}

		public List<WiSiteFileInfo> getSiteFileInfoList() {
			return siteFileInfoList;
		}

		public void setSiteFileInfoList(
				List<WiSiteFileInfo> siteFileInfoList) {
			this.siteFileInfoList = siteFileInfoList;
		}
	}

	public static class Wo extends WoId {

	}
}
