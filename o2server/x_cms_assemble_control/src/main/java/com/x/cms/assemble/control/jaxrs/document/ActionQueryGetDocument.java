package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Log;
import com.x.cms.core.entity.content.Data;
import com.x.cms.core.entity.element.Form;

public class ActionQueryGetDocument extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryGetDocument.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, String id, EffectivePerson effectivePerson)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		WoDocument wrapOutDocument = null;
		AppInfo appInfo = null;
		CategoryInfo categoryInfo = null;
		Document document = null;
		Boolean isAppAdmin = false;
		Boolean isCategoryAdmin = false;
		Boolean isManager = false;
		Boolean isEditor = false;
		Boolean isCreator = false;
		Boolean check = true;
		List<String> unitNames = null;
		List<String> groupNames = null;
		List<String> roleNames = null;
		String personName = effectivePerson.getDistinguishedName();

		if ( StringUtils.isEmpty(id)) {
			check = false;
			Exception exception = new ExceptionDocumentIdEmpty();
			result.error(exception);
		}

		if (check) {
			try {
				if (effectivePerson.isManager()) {
					isManager = true;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "사용자가 시스템 관리자인지 확인하는 중 오류가 발생했습니다! user:" + personName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), id, effectivePerson.getDistinguishedName(), isManager );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			wo = (Wo) optional.get();
			document = wo.getDocument();
			wrapOutDocument = wo.getDocument();
			result.setData(wo);
		} else {
			if (check) {
				try {
					document = documentQueryService.get(id);
					if (document == null) {
						check = false;
						Exception exception = new ExceptionDocumentNotExists(id);
						result.error(exception);
					} else {
						try {
							wrapOutDocument = WoDocument.copier.copy(document);
						} catch (Exception e) {
							check = false;
							Exception exception = new ExceptionDocumentInfoProcess(e, "조회한 게시글 정보 객체를 출력 데이터로 변환하는 중 오류가 발생했습니다.");
							result.error(exception);
							logger.error(e, effectivePerson, request, null);
						}
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e, "게시글 정보를 가져오는 중 오류가 발생했습니다. Id:" + id + ", Name:" + personName);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}

			if (check) {
				if (wrapOutDocument != null) {
					try {
						categoryInfo = categoryInfoServiceAdv.get(document.getCategoryId());
						wrapOutDocument.setForm(categoryInfo.getFormId());
						wrapOutDocument.setFormName(categoryInfo.getFormName());
						wrapOutDocument.setReadFormId(categoryInfo.getReadFormId());
						wrapOutDocument.setReadFormName(categoryInfo.getReadFormName());
						wrapOutDocument.setCategoryName(categoryInfo.getCategoryName());
						wrapOutDocument.setCategoryAlias(categoryInfo.getCategoryAlias());

						if (StringUtils.isNotEmpty( wrapOutDocument.getCreatorPerson() )) {
							wrapOutDocument.setCreatorPersonShort(wrapOutDocument.getCreatorPerson().split("@")[0]);
						}
						if (StringUtils.isNotEmpty( wrapOutDocument.getCreatorUnitName() )) {
							wrapOutDocument.setCreatorUnitNameShort(wrapOutDocument.getCreatorUnitName().split("@")[0]);
						}
						if (StringUtils.isNotEmpty( wrapOutDocument.getCreatorTopUnitName() )) {
							wrapOutDocument.setCreatorTopUnitNameShort(wrapOutDocument.getCreatorTopUnitName().split("@")[0]);
						}
						wo.setDocument(wrapOutDocument);
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionDocumentInfoProcess(e,
								"ID로 분류 정보 객체를 조회하는 중 오류가 발생했습니다. ID:" + document.getCategoryId());
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
			if (check) {
				if (wrapOutDocument != null) {
					try {
						wo.setData(documentQueryService.getDocumentData(document));
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionDocumentInfoProcess(e,
								"게시글 데이터 내용을 가져오는 중 오류가 발생했습니다. Id:" + document.getCategoryId());
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			}

			//사용자가 문서 작성자인지 판단, 작성자는 문서 편집 권한이 있음
			if (check) {
				if (wo.getDocument() != null && wo.getDocument().getCreatorPerson() != null && wo.getDocument().getCreatorPerson().equals(personName)) {
					isCreator = true;
					wo.setIsCreator(isCreator);
				}
			}

			if (check) {
				wo.setDocumentLogList(new ArrayList<WoLog>());
				CacheManager.put(cacheCategory, cacheKey, wo );
			}
		}

		if (check) {
			try {
				unitNames = userManagerService.listUnitNamesWithPerson( personName );
				groupNames = userManagerService.listGroupNamesByPerson( personName );
				roleNames = userManagerService.listRoleNamesByPerson( personName );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "사용자의 전체 조직 및 그룹 정보를 조회하는 중 오류가 발생했습니다! user:" + personName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		/////////////////////////////////////////////////////////////
		//캐시에서 조회하든 데이터베이스에서 조회하든 권한 판단을 다시 처리해야 함
		/////////////////////////////////////////////////////////////

		if (check) {
			try {
				appInfo = appInfoServiceAdv.get( document.getAppId() );
				if( appInfo == null ) {
					check = false;
					Exception exception = new ExceptionAppInfoNotExists( document.getAppId()  );
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "ID로 게시판 정보 객체를 조회하는 중 오류가 발생했습니다. ID:" + document.getAppId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if ( check ) {
			try {
				categoryInfo = categoryInfoServiceAdv.get(document.getCategoryId());
				if( categoryInfo == null ) {
					check = false;
					Exception exception = new ExceptionCategoryInfoNotExists( document.getCategoryId() );
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "ID로 분류 정보 객체를 조회하는 중 오류가 발생했습니다. ID:" + document.getCategoryId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		//사용자가 분류 관리자인지 판단, 분류 관리자는 문서 편집 권한이 있음
		if (check) {
			try {
				if ( categoryInfoServiceAdv.isCategoryInfoManager( categoryInfo, personName, unitNames, groupNames ) ) {
					isCategoryAdmin = true;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e,
						"사용자가 분류 관리자인지 확인하는 중 오류가 발생했습니다! user:" + personName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		//사용자가 게시판 관리자인지 판단, 게시판 관리자는 문서 편집 권한이 있음
		if (check) {
			try {
				if ( appInfoServiceAdv.isAppInfoManager( appInfo, personName, unitNames, groupNames, roleNames )) {
					isAppAdmin = true;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "사용자가 게시판 관리자인지 확인하는 중 오류가 발생했습니다! user:" + personName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			if (isManager || isAppAdmin || isCategoryAdmin || isCreator) {
				isEditor = true;
			} else {
				// 현재 로그인 사용자가 해당 문서의 편집자인지 판단
				try {
					if( ListTools.isNotEmpty( document.getAuthorPersonList() )) {
						if ( wrapOutDocument.getAuthorPersonList().contains( getShortTargetFlag(personName) )) {
							isEditor = true;
						}
					}
					if( ListTools.isNotEmpty( document.getAuthorUnitList() )) {
						if( ListTools.containsAny( getShortTargetFlag(unitNames) , wrapOutDocument.getAuthorUnitList() )) {
							isEditor = true;
						}
					}
					if( ListTools.isNotEmpty( document.getAuthorGroupList() )) {
						if( ListTools.containsAny( getShortTargetFlag(groupNames) , wrapOutDocument.getAuthorGroupList() )) {
							isEditor = true;
						}
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e, "사용자가 게시글을 편집할 수 있는지 확인하는 중 오류가 발생했습니다! user:" + personName);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}

		wo.setIsManager(isManager);
		wo.setIsAppAdmin(isAppAdmin);
		wo.setIsCategoryAdmin(isCategoryAdmin);
		wo.setIsEditor(isEditor);

		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("출력용 CMS 문서 데이터 객체.")
		private WoDocument document;

		@FieldDescribe("출력용 CMS 문서 조작 로그.")
		private List<WoLog> documentLogList;

		@FieldDescribe("문서 전체 데이터 정보.")
		private Data data;

		@FieldDescribe("편집용 CMS 문서 양식.")
		private WoForm form;

		@FieldDescribe("조회용 CMS 문서 양식.")
		private WoForm readForm;

		private Boolean isAppAdmin = false;
		private Boolean isCategoryAdmin = false;
		private Boolean isManager = false;
		private Boolean isCreator = false;
		private Boolean isEditor = false;

		public WoDocument getDocument() {
			return document;
		}

		public void setDocument(WoDocument document) {
			this.document = document;
		}

		public List<WoLog> getDocumentLogList() {
			return documentLogList;
		}

		public void setDocumentLogList(List<WoLog> documentLogList) {
			this.documentLogList = documentLogList;
		}

		public Data getData() {
			return data;
		}

		public void setData(Data data) {
			this.data = data;
		}

		public WoForm getForm() {
			return form;
		}

		public void setForm(WoForm form) {
			this.form = form;
		}

		public WoForm getReadForm() {
			return readForm;
		}

		public void setReadForm(WoForm readForm) {
			this.readForm = readForm;
		}

		public Boolean getIsAppAdmin() {
			return isAppAdmin;
		}

		public Boolean getIsCategoryAdmin() {
			return isCategoryAdmin;
		}

		public Boolean getIsManager() {
			return isManager;
		}

		public void setIsAppAdmin(Boolean isAppAdmin) {
			this.isAppAdmin = isAppAdmin;
		}

		public void setIsCategoryAdmin(Boolean isCategoryAdmin) {
			this.isCategoryAdmin = isCategoryAdmin;
		}

		public void setIsManager(Boolean isManager) {
			this.isManager = isManager;
		}

		public Boolean getIsCreator() {
			return isCreator;
		}

		public void setIsCreator(Boolean isCreator) {
			this.isCreator = isCreator;
		}

		public Boolean getIsEditor() {
			return isEditor;
		}

		public void setIsEditor(Boolean isEditor) {
			this.isEditor = isEditor;
		}
	}

	public static class WoDocument extends Document {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<String>();

		public static final WrapCopier<Document, WoDocument> copier = WrapCopierFactory.wo(Document.class, WoDocument.class,
				null, JpaObject.FieldsInvisible);

		/**
		 * 표시용으로만 사용
		 */
		private String creatorPersonShort = "";

		private String creatorUnitNameShort = "";

		private String creatorTopUnitNameShort = "";

		public String getCreatorPersonShort() {
			return creatorPersonShort;
		}

		public String getCreatorUnitNameShort() {
			return creatorUnitNameShort;
		}

		public String getCreatorTopUnitNameShort() {
			return creatorTopUnitNameShort;
		}

		public void setCreatorPersonShort(String creatorPersonShort) {
			this.creatorPersonShort = creatorPersonShort;
		}

		public void setCreatorUnitNameShort(String creatorUnitNameShort) {
			this.creatorUnitNameShort = creatorUnitNameShort;
		}

		public void setCreatorTopUnitNameShort(String creatorTopUnitNameShort) {
			this.creatorTopUnitNameShort = creatorTopUnitNameShort;
		}
	}

//	public static class WoFileInfo extends FileInfo {
//
//		private static final long serialVersionUID = -5076990764713538973L;
//
//		public static List<String> Excludes = new ArrayList<String>();
//
//		private WoControl control = new WoControl();
//
//		public WoControl getControl() {
//			return control;
//		}
//
//		public void setControl(WoControl control) {
//			this.control = control;
//		}
//
//		public static WrapCopier<FileInfo, WoFileInfo> copier = WrapCopierFactory.wo(FileInfo.class, WoFileInfo.class,
//				null, JpaObject.FieldsInvisible);
//
//		private Long referencedCount;
//
//		public Long getReferencedCount() {
//			return referencedCount;
//		}
//
//		public void setReferencedCount(Long referencedCount) {
//			this.referencedCount = referencedCount;
//		}
//	}

	public static class WoLog extends Log {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();
	}

	public static class WoForm extends Form {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();
	}

//	public static class WoControl extends GsonPropertyObject {
//
//		private Boolean allowRead = false;
//		private Boolean allowEdit = false;
//		private Boolean allowControl = false;
//
//		public Boolean getAllowRead() {
//			return allowRead;
//		}
//
//		public void setAllowRead(Boolean allowRead) {
//			this.allowRead = allowRead;
//		}
//
//		public Boolean getAllowEdit() {
//			return allowEdit;
//		}
//
//		public void setAllowEdit(Boolean allowEdit) {
//			this.allowEdit = allowEdit;
//		}
//
//		public Boolean getAllowControl() {
//			return allowControl;
//		}
//
//		public void setAllowControl(Boolean allowControl) {
//			this.allowControl = allowControl;
//		}
//	}

//	private boolean read( WoFileInfo woFileInfo, EffectivePerson effectivePerson, List<String> identities, List<String> units) throws Exception {
//		boolean value = false;
//		if (effectivePerson.isPerson(woFileInfo.getCreatorUid())) {
//			value = true;
//		} else if (ListTools.isEmpty(woFileInfo.getReadIdentityList()) && ListTools.isEmpty(woFileInfo.getReadUnitList())) {
//			value = true;
//		} else if (ListTools.containsAny(identities, woFileInfo.getReadIdentityList()) || ListTools.containsAny(units, woFileInfo.getReadUnitList())) {
//			value = true;
//		} else if (ListTools.containsAny(identities, woFileInfo.getEditIdentityList()) || ListTools.containsAny(units, woFileInfo.getEditUnitList())) {
//			value = true;
//		} else {
//			if (ListTools.containsAny(identities, woFileInfo.getControllerIdentityList()) || ListTools.containsAny(units, woFileInfo.getControllerUnitList() )) {
//				value = true;
//			}
//		}
//		return value;
//	}
//
//	private boolean edit( WoFileInfo woFileInfo, EffectivePerson effectivePerson, List<String> identities, List<String> units) throws Exception {
//		boolean value = false;
//		if (effectivePerson.isPerson(woFileInfo.getCreatorUid())) {
//			value = true;
//		} else if (ListTools.isEmpty(woFileInfo.getEditIdentityList()) && ListTools.isEmpty(woFileInfo.getEditUnitList())) {
//			value = true;
//		} else {
//			if (ListTools.containsAny(identities, woFileInfo.getEditIdentityList()) || ListTools.containsAny(units, woFileInfo.getEditUnitList())) {
//				value = true;
//			}
//		}
//		return value;
//	}
//
//	private boolean control( WoFileInfo woFileInfo, EffectivePerson effectivePerson, List<String> identities, List<String> units)
//			throws Exception {
//		boolean value = false;
//		if (effectivePerson.isPerson(woFileInfo.getCreatorUid())) {
//			value = true;
//		} else if (ListTools.isEmpty(woFileInfo.getControllerUnitList()) && ListTools.isEmpty(woFileInfo.getControllerIdentityList())) {
//			value = true;
//		} else {
//			if (ListTools.containsAny(identities, woFileInfo.getControllerIdentityList()) || ListTools.containsAny(units, woFileInfo.getControllerUnitList())) {
//				value = true;
//			}
//		}
//		return value;
//	}
}
