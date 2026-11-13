package com.x.cms.assemble.control.jaxrs.document;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.config.Token;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionInfo;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.enums.DocumentStatus;
import com.x.cms.core.entity.query.DocumentNotify;
import com.x.processplatform.core.entity.content.Attachment;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 문서 저장
 *
 * @author O2LEE
 *
 */
public class ActionPersistSaveDocument extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPersistSaveDocument.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, JsonElement jsonElement,
			EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Document document = Wi.copier.copy(wi);
		document.setId(wi.getId());
		String identity = wi.getIdentity();
		if (StringUtils.isBlank(identity)) {
			identity = userManagerService.getPersonIdentity(effectivePerson.getDistinguishedName(), identity);
		}

		if (StringUtils.isEmpty(wi.getCategoryId())) {
			throw new ExceptionDocumentCategoryIdEmpty();
		}

		CategoryInfo categoryInfo;
		AppInfo appInfo = null;

		Document oldDocument = documentQueryService.get(document.getId());
		if (oldDocument != null) {
			categoryInfo = categoryInfoServiceAdv.get(oldDocument.getCategoryId());
			appInfo = appInfoServiceAdv.get(oldDocument.getAppId());
		} else {
			categoryInfo = categoryInfoServiceAdv.get(wi.getCategoryId());
			if (categoryInfo != null) {
				appInfo = appInfoServiceAdv.get(categoryInfo.getAppId());
			}
		}

		if (categoryInfo == null) {
			throw new ExceptionCategoryInfoNotExists(wi.getCategoryId());
		}
		if (appInfo == null) {
			throw new ExceptionAppInfoNotExists(categoryInfo.getAppId());
		}

		Business business = new Business(null);
		if (!business.isDocumentEditor(effectivePerson, appInfo, categoryInfo, oldDocument)) {
			throw new ExceptionAccessDenied(effectivePerson, document);
		}

		// 분류에 설정된 편집 양식 조회
		if (StringUtils.isEmpty(categoryInfo.getFormId())) {
			throw new ExceptionCategoryFormIdEmpty();
		}

		Form form = formServiceAdv.get(categoryInfo.getFormId());
		if (form == null) {
			throw new ExceptionFormForEditNotExists(categoryInfo.getFormId());
		} else {
			document.setForm(form.getId());
			document.setFormName(form.getName());
		}

		if (check) {
			if (categoryInfo.getReadFormId() != null && !categoryInfo.getReadFormId().isEmpty()) {
				try {
					form = formServiceAdv.get(categoryInfo.getReadFormId());
					if (form == null) {
						check = false;
						Exception exception = new ExceptionFormForReadNotExists(categoryInfo.getReadFormId());
						result.error(exception);
					} else {
						document.setReadFormId(form.getId());
						document.setReadFormName(form.getName());
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e,
							"ID로 읽기 양식을 조회하는 중 오류가 발생했습니다! ID: " + categoryInfo.getReadFormId());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}

		if (check) {
			document.setAppId(appInfo.getId());
			document.setAppAlias(appInfo.getAppAlias());
			document.setAppName(appInfo.getAppName());
			document.setCategoryName(categoryInfo.getCategoryName());
			document.setCategoryAlias(categoryInfo.getCategoryAlias());
			document.setDocumentType(categoryInfo.getDocumentType());
			if(!Document.DOCUMENT_TYPE_DATA.equals( document.getDocumentType() )) {
				document.setDocumentType( Document.DOCUMENT_TYPE_INFO );
			}
		}

		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				if (StringUtils.isNotEmpty(identity)) {
					document.setCreatorIdentity(identity);
				}

				if (StringUtils.isEmpty(document.getCreatorIdentity())) {
					if (StringUtils.equals(EffectivePerson.CIPHER, effectivePerson.getDistinguishedName())
							|| StringUtils.equals(Token.defaultInitialManager,
									effectivePerson.getDistinguishedName())) {
						document.setCreatorIdentity(effectivePerson.getDistinguishedName());
						document.setCreatorPerson(effectivePerson.getDistinguishedName());
						document.setCreatorUnitName(effectivePerson.getDistinguishedName());
						document.setCreatorTopUnitName(effectivePerson.getDistinguishedName());
					} else {
						// 현재 사용자로 사용자의 첫 번째 신분 조회 시도
						document.setCreatorIdentity(
								userManagerService.getMajorIdentityWithPerson(effectivePerson.getDistinguishedName()));
					}
				}

				if (!StringUtils.equals(EffectivePerson.CIPHER, document.getCreatorIdentity())
						&& !StringUtils.equals(Token.defaultInitialManager, document.getCreatorIdentity())) {
					if (StringUtils.isNotEmpty(document.getCreatorIdentity())) {
						document.setCreatorPerson(
								userManagerService.getPersonNameWithIdentity(document.getCreatorIdentity()));
						document.setCreatorUnitName(
								userManagerService.getUnitNameByIdentity(document.getCreatorIdentity()));
						document.setCreatorTopUnitName(
								userManagerService.getTopUnitNameByIdentity(document.getCreatorIdentity()));
					} else {
						Exception exception = new ExceptionPersonHasNoIdentity(document.getCreatorIdentity());
						result.error(exception);
					}
				}
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}

		if (check) {
			if (DocumentStatus.PUBLISHED.getValue().equals(wi.getDocStatus())
					|| DocumentStatus.WAIT_PUBLISH.getValue().equals(wi.getDocStatus())) {
				if (document.getPublishTime() == null) {
					document.setPublishTime(new Date());
				}
			}
		}

		if (check) {
			if (StringUtils.isEmpty(document.getTitle())) {
				document.setTitle(appInfo.getAppName() + " - " + categoryInfo.getCategoryName() + " - 제목 없는 게시글");
			}
		}

		if (check) {
			try {
				document.getProperties().setCloudPictures(wi.getCloudPictures());
				document.getProperties().setDocumentNotify(wi.documentNotify);
				document = documentPersistService.save(document, wi.getDocData(), categoryInfo.getProjection());
				CacheManager.notify(Document.class);

				Wo wo = new Wo();
				wo.setId(document.getId());
				result.setData(wo);

				// 핫 이미지 삭제 필요 여부 확인
				try {
					ThisApplication.queueDocumentUpdate.send(document);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "게시글을 만드는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		// 프로세스 관리의 모든 첨부파일을 CMS로 복사
		if (check) {
			if (wi.getWf_attachmentIds() != null && wi.getWf_attachmentIds().length > 0) {
				FileInfo fileInfo = null;
				Attachment attachment = null;
				StorageMapping mapping_attachment = null;
				StorageMapping mapping_fileInfo = null;
				for (String attachmentId : wi.getWf_attachmentIds()) {
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						document = emc.find(document.getId(), Document.class, ExceptionWhen.not_found);
						attachment = emc.find(attachmentId, Attachment.class, ExceptionWhen.not_found);
						if (attachment != null) {
							emc.beginTransaction(FileInfo.class);
							emc.beginTransaction(Document.class);

							mapping_attachment = ThisApplication.context().storageMappings().get(Attachment.class,
									attachment.getStorage());
							byte[] bs = attachment.readContent(mapping_attachment);

							mapping_fileInfo = ThisApplication.context().storageMappings().random(FileInfo.class);
							fileInfo = concreteFileInfo(effectivePerson.getDistinguishedName(), document,
									mapping_fileInfo, attachment.getName(), attachment.getSite());
							fileInfo.saveContent(mapping_fileInfo, bs, attachment.getName(),
									Config.general().getStorageEncrypt());
							if (attachment.getOrderNumber() != null) {
								fileInfo.setSeqNumber(attachment.getOrderNumber());
							}
							fileInfo.setName(attachment.getName());
							fileInfo.setBusinessId(attachment.getBusinessId());
							emc.check(document, CheckPersistType.all);
							emc.persist(fileInfo, CheckPersistType.all);

							emc.commit();
						}
					}
				}
			}
		}

		if (check) {
			try {// 독자 및 작성자 정보를 데이터베이스에 저장
				if (oldDocument == null || wi.getReaderList() != null || wi.getAuthorList() != null) {
					documentPersistService.refreshDocumentPermission(document.getId(), wi.getReaderList(),
							wi.getAuthorList());
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "게시글 접근·관리 권한을 확인하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	private FileInfo concreteFileInfo(String person, Document document, StorageMapping storage, String name,
			String site) throws Exception {
		String fileName = UUID.randomUUID().toString();
		String extension = FilenameUtils.getExtension(name);
		FileInfo attachment = new FileInfo();
		if (StringUtils.isEmpty(extension)) {
			throw new Exception("file extension is empty.");
		} else {
			fileName = fileName + "." + extension;
		}
		if (name.indexOf("\\") > 0) {
			name = StringUtils.substringAfterLast(name, "\\");
		}
		if (name.indexOf("/") > 0) {
			name = StringUtils.substringAfterLast(name, "/");
		}
		attachment.setCreateTime(new Date());
		attachment.setLastUpdateTime(new Date());
		attachment.setExtension(extension);
		attachment.setName(name);
		attachment.setFileName(fileName);
		attachment.setStorage(storage.getName());
		attachment.setAppId(document.getAppId());
		attachment.setCategoryId(document.getCategoryId());
		attachment.setDocumentId(document.getId());
		attachment.setCreatorUid(person);
		attachment.setSite(site);
		attachment.setFileHost("");
		attachment.setFilePath("");
		attachment.setFileType("ATTACHMENT");

		return attachment;
	}

	public static class Wi {

		@FieldDescribe("ID, 선택 사항, 수정 시 필수, 없으면 새 문서 추가")
		private String id;

		@FieldDescribe("문서 제목, <font style='color:red'>필수</font>")
		private String title;

		@FieldDescribe("객체 보안 등급 식별자")
		private Integer objectSecurityClearance;

		@FieldDescribe("분류ID, <font style='color:red'>필수</font>")
		private String categoryId;

		@FieldDescribe("문서 조작자 신분, 전달하지 않으면 로그인 사용자 정보를 사용합니다.")
		private String identity = null;

		@FieldDescribe("문서 요약, 선택 사항")
		private String summary;

		@FieldDescribe("문서 상태: published | draft | waitPublish, 선택 사항, 기본값 draft")
		private String docStatus = "draft";

		@FieldDescribe("문서 게시 시간")
		private Date publishTime;

		@FieldDescribe("대표 이미지 목록, 선택 사항")
		private List<String> pictureList = null;

		@FieldDescribe("데이터 경로 목록, 선택 사항")
		private String[] dataPaths = null;

		@FieldDescribe("프로세스를 시작한 JobId, 선택 사항")
		private String wf_jobId = null;

		@FieldDescribe("프로세스를 시작한 WorkId, 선택 사항")
		private String wf_workId = null;

		@FieldDescribe("프로세스를 시작한 첨부파일 목록, 선택 사항")
		private String[] wf_attachmentIds = null;

		@FieldDescribe("문서 데이터, 선택 사항")
		private JsonElement docData = null;

		@FieldDescribe("문서 독자, 선택 사항: {'permission':'독자', 'permissionObjectType':'조직', 'permissionObjectCode':'조직 전체 이름', 'permissionObjectName':'조직 전체 이름'}")
		private List<PermissionInfo> readerList = null;

		@FieldDescribe("문서 편집자, 선택 사항: {'permission':'독자', 'permissionObjectType':'조직', 'permissionObjectCode':'조직 전체 이름', 'permissionObjectName':'조직 전체 이름'}")
		private List<PermissionInfo> authorList = null;

		@FieldDescribe("예약 게시 문서 메시지 알림 대상(파라미터는 메시지 게시 인터페이스 객체와 동일).")
		private DocumentNotify documentNotify;

		@FieldDescribe("이미지 목록, 선택 사항")
		private List<String> cloudPictures = null;

		@FieldDescribe("권한 수정 안 함(권한 설정을 건너뛰고 기존 설정 유지), 선택 사항")
		private Boolean skipPermission = false;

		@FieldDescribe("업무 데이터 String 값01.")
		private String stringValue01;

		@FieldDescribe("업무 데이터 String 값02.")
		private String stringValue02;

		@FieldDescribe("업무 데이터 String 값03.")
		private String stringValue03;

		private String importBatchName;

		public static final WrapCopier<Wi, Document> copier = WrapCopierFactory.wi(Wi.class, Document.class, null,
				JpaObject.FieldsUnmodifyExcludeId);

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getSummary() {
			return summary;
		}

		public void setSummary(String summary) {
			this.summary = summary;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getCategoryId() {
			return categoryId;
		}

		public void setCategoryId(String categoryId) {
			this.categoryId = categoryId;
		}

		public String getDocStatus() {
			return docStatus;
		}

		public void setDocStatus(String docStatus) {
			this.docStatus = docStatus;
		}

		public List<String> getPictureList() {
			return pictureList;
		}

		public void setPictureList(List<String> pictureList) {
			this.pictureList = pictureList;
		}

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public List<PermissionInfo> getReaderList() {
			return readerList;
		}

		public List<PermissionInfo> getAuthorList() {
			return authorList;
		}

		public void setReaderList(List<PermissionInfo> readerList) {
			this.readerList = readerList;
		}

		public void setAuthorList(List<PermissionInfo> authorList) {
			this.authorList = authorList;
		}

		public String[] getDataPaths() {
			if (dataPaths != null && dataPaths.length == 1 && dataPaths[0].equals("null")) {
				return null;
			}
			return dataPaths;
		}

		public void setDataPaths(String[] dataPaths) {
			this.dataPaths = dataPaths;
		}

		public JsonElement getDocData() {
			return docData;
		}

		public void setDocData(JsonElement docData) {
			this.docData = docData;
		}

		public String getWf_jobId() {
			return wf_jobId;
		}

		public String getWf_workId() {
			return wf_workId;
		}

		public String[] getWf_attachmentIds() {
			return wf_attachmentIds;
		}

		public void setWf_jobId(String wf_jobId) {
			this.wf_jobId = wf_jobId;
		}

		public void setWf_workId(String wf_workId) {
			this.wf_workId = wf_workId;
		}

		public void setWf_attachmentIds(String[] wf_attachmentIds) {
			this.wf_attachmentIds = wf_attachmentIds;
		}

		public List<String> getCloudPictures() {
			return cloudPictures;
		}

		public void setCloudPictures(List<String> cloudPictures) {
			this.cloudPictures = cloudPictures;
		}

		public Boolean getSkipPermission() {
			return skipPermission;
		}

		public void setSkipPermission(Boolean skipPermission) {
			this.skipPermission = skipPermission;
		}

		public Date getPublishTime() {
			return publishTime;
		}

		public void setPublishTime(Date publishTime) {
			this.publishTime = publishTime;
		}

		public String getStringValue01() {
			return stringValue01;
		}

		public void setStringValue01(String stringValue01) {
			this.stringValue01 = stringValue01;
		}

		public String getStringValue02() {
			return stringValue02;
		}

		public void setStringValue02(String stringValue02) {
			this.stringValue02 = stringValue02;
		}

		public String getStringValue03() {
			return stringValue03;
		}

		public void setStringValue03(String stringValue03) {
			this.stringValue03 = stringValue03;
		}

		public DocumentNotify getDocumentNotify() {
			return documentNotify;
		}

		public void setDocumentNotify(DocumentNotify documentNotify) {
			this.documentNotify = documentNotify;
		}

		public String getImportBatchName() {
			return importBatchName;
		}

		public void setImportBatchName(String importBatchName) {
			this.importBatchName = importBatchName;
		}

		public Integer getObjectSecurityClearance() {
			return objectSecurityClearance;
		}

		public void setObjectSecurityClearance(Integer objectSecurityClearance) {
			this.objectSecurityClearance = objectSecurityClearance;
		}
	}

	public static class Wo extends WoId {

	}
}
