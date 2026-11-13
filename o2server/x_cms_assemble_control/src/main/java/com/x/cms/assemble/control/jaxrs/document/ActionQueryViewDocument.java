package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentCommend;
import com.x.cms.core.entity.Log;
import com.x.cms.core.entity.content.Data;
import com.x.cms.core.entity.element.Form;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class ActionQueryViewDocument extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionQueryViewDocument.class);

	@SuppressWarnings("unchecked")
	protected ActionResult<Wo> execute(HttpServletRequest request, String id, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result;
		boolean isManager = effectivePerson.isManager();
		String personName = effectivePerson.getDistinguishedName();

		if ( StringUtils.isEmpty(id)) {
			throw new ExceptionDocumentIdEmpty();
		}

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), id, effectivePerson.getDistinguishedName() );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (!effectivePerson.isAnonymous() && optional.isPresent()) {
			result = (ActionResult<Wo>) optional.get();
		} else {
			result = getDocumentQueryResult( id, effectivePerson, isManager );
			if (!effectivePerson.isAnonymous()) {
				CacheManager.put(cacheCategory, cacheKey, result);
			}
		}

		//관리자 접근이 아니면 해당 문서의 접근 기록 저장
		if (!OrganizationDefinition.isSystemUser(personName)) {
			try {
				Long viewCount = documentViewRecordServiceAdv.addViewRecord( id, personName );
				result.getData().document.setViewCount( viewCount );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}

		//뷰 통계를 위해 item의 조회수를 비동기 갱신
		try {
			ThisApplication.queueDocumentViewCountUpdate.send( result.getData().getDocument() );
		} catch ( Exception e ) {
			logger.error(e);
		}
		return result;
	}

	/**
	 * 반환할 문서 정보 객체 조회
	 * @param id
	 * @param effectivePerson
	 * @param isManager 현재 사용자가 시스템 관리자 또는 CMS 관리자인지 여부
	 * @return
	 */
	private ActionResult<Wo> getDocumentQueryResult( String id, EffectivePerson effectivePerson, boolean isManager ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		boolean isAppAdmin = false;
		boolean isCategoryAdmin = false;
		boolean isEditor = false;
		boolean isCreator = false;
		List<String> unitNames = null;
		List<String> groupNames = null;
		List<String> roleNames = null;
		boolean isAnonymous = effectivePerson.isAnonymous();
		String personName = effectivePerson.getDistinguishedName();

		Document document = documentQueryService.view( id, effectivePerson );
		if ( document == null ) {
			throw new ExceptionDocumentNotExists(id);
		}
		AppInfo appInfo = appInfoServiceAdv.get( document.getAppId() );
		if( appInfo == null ) {
			throw new ExceptionAppInfoNotExists( document.getAppId() );
		}
		CategoryInfo categoryInfo = categoryInfoServiceAdv.get(document.getCategoryId());
		if( categoryInfo == null ) {
			throw new ExceptionCategoryInfoNotExists( document.getCategoryId() );
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if(!business.isDocumentReader(effectivePerson, document, appInfo)){
				throw new ExceptionAccessDenied(effectivePerson, document);
			}
		}

		if( !isAnonymous ) {
			unitNames = userManagerService.listUnitNamesWithPerson( personName );
			groupNames = userManagerService.listGroupNamesByPerson( personName );
			roleNames = userManagerService.listRoleNamesByPerson( personName );
		}


		WoDocument woOutDocument = WoDocument.copier.copy( document );
		woOutDocument.setForm(categoryInfo.getFormId());
		woOutDocument.setFormName(categoryInfo.getFormName());
		woOutDocument.setReadFormId(categoryInfo.getReadFormId());
		woOutDocument.setReadFormName(categoryInfo.getReadFormName());
		woOutDocument.setCategoryName(categoryInfo.getCategoryName());
		woOutDocument.setCategoryAlias(categoryInfo.getCategoryAlias());

		if( woOutDocument.getCreatorPerson() != null && !woOutDocument.getCreatorPerson().isEmpty() ) {
			woOutDocument.setCreatorPersonShort( woOutDocument.getCreatorPerson().split( "@" )[0]);
		}
		if( woOutDocument.getCreatorUnitName() != null && !woOutDocument.getCreatorUnitName().isEmpty() ) {
			woOutDocument.setCreatorUnitNameShort( woOutDocument.getCreatorUnitName().split( "@" )[0]);
		}
		if( woOutDocument.getCreatorTopUnitName() != null && !woOutDocument.getCreatorTopUnitName().isEmpty() ) {
			woOutDocument.setCreatorTopUnitNameShort( woOutDocument.getCreatorTopUnitName().split( "@" )[0]);
		}
		wo.setDocument(woOutDocument);

		wo.setData( documentQueryService.getDocumentData( document ) );

		//사용자가 문서 작성자인지 판단, 작성자는 문서 편집 권한이 있음
		if(!isAnonymous && wo.getDocument() != null && wo.getDocument().getCreatorPerson() != null && wo.getDocument().getCreatorPerson().equals( personName )) {
			isCreator = true;
			wo.setIsCreator( isCreator );
		}

		//사용자가 분류 관리자인지 판단, 분류 관리자는 문서 편집 권한이 있음
		if (!isAnonymous && BooleanUtils.isTrue(categoryInfoServiceAdv.isCategoryInfoManager( categoryInfo, personName, unitNames, groupNames ))) {
			isCategoryAdmin = true;
		}

		//사용자가 게시판 관리자인지 판단, 게시판 관리자는 문서 편집 권한이 있음
		if (!isAnonymous && BooleanUtils.isTrue(appInfoServiceAdv.isAppInfoManager( appInfo, personName, unitNames, groupNames, roleNames ))) {
			isAppAdmin = true;
		}

		if ( isManager || isAppAdmin || isCategoryAdmin || isCreator ) {
			isEditor = true;
		} else if( !isAnonymous ) {
			if( ListTools.isNotEmpty( document.getAuthorPersonList() )) {
				if( document.getAuthorPersonList().contains( getShortTargetFlag(personName) ) ) {
					isEditor = true;
				}
			}
			if( ListTools.isNotEmpty( document.getAuthorUnitList() )) {
				if( ListTools.containsAny( getShortTargetFlag(unitNames), document.getAuthorUnitList())) {
					isEditor = true;
				}
			}
			if( ListTools.isNotEmpty( document.getAuthorGroupList() )) {
				if( ListTools.containsAny( getShortTargetFlag(groupNames), document.getAuthorGroupList())) {
					isEditor = true;
				}
			}
		}

		if(ListTools.isNotEmpty(docCommendQueryService.listByDocAndPerson(id, effectivePerson.getDistinguishedName(), 1, DocumentCommend.COMMEND_TYPE_DOCUMENT))){
			wo.setIsCommend(true);
		}

		wo.setIsManager( isManager );
		wo.setIsAppAdmin( isAppAdmin );
		wo.setIsCategoryAdmin( isCategoryAdmin );
		wo.setIsEditor( isEditor );

		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe( "출력용 CMS 문서 데이터 객체." )
		private WoDocument document;

		@FieldDescribe( "출력용 CMS 문서 조작 로그." )
		private List<WoLog> documentLogList;

		@FieldDescribe( "문서 전체 데이터 정보." )
		private Data data;

		@FieldDescribe( "편집용 CMS 문서 양식." )
		private WoForm form;

		@FieldDescribe( "조회용 CMS 문서 양식." )
		private WoForm readForm;

		@FieldDescribe( "현재 사용자의 문서 좋아요 여부." )
		private Boolean isCommend = false;

		private Boolean isAppAdmin = false;
		private Boolean isCategoryAdmin = false;
		private Boolean isManager = false;
		private Boolean isCreator = false;
		private Boolean isEditor = false;


		public WoDocument getDocument() {
			return document;
		}

		public void setDocument( WoDocument document) {
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

		public Boolean getIsEditor() {
			return isEditor;
		}

		public void setIsEditor(Boolean isEditor) {
			this.isEditor = isEditor;
		}

		public Boolean getIsCreator() {
			return isCreator;
		}

		public void setIsCreator(Boolean isCreator) {
			this.isCreator = isCreator;
		}

		public Boolean getIsCommend() {
			return isCommend;
		}

		public void setIsCommend(Boolean isCommend) {
			this.isCommend = isCommend;
		}
	}

	public static class WoDocument extends Document {

		private static final long serialVersionUID = -5076990764713538973L;

		public static final WrapCopier<Document, WoDocument> copier = WrapCopierFactory.wo( Document.class, WoDocument.class, null,JpaObject.FieldsInvisible);

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

	public static class WoLog extends Log {

		private static final long serialVersionUID = -5076990764713538973L;

	}

	public static class WoForm extends Form {

		private static final long serialVersionUID = -5076990764713538973L;

	}

}
