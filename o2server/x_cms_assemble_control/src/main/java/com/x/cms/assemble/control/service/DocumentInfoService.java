package com.x.cms.assemble.control.service;

import java.util.Date;
import java.util.List;

import com.x.cms.core.entity.*;
import com.x.cms.core.entity.enums.DocumentStatus;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.DocumentDataHelper;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.express.tools.filter.QueryFilter;
import com.x.query.core.entity.Item;

public class DocumentInfoService {

	public String getSequence(EntityManagerContainer emc, String id) throws Exception {
		if (StringUtils.isEmpty(id)) {
			return null;
		}
		Business business = new Business(emc);
		return business.getDocumentFactory().getSequence(id);
	}

	/**
	 * 지정한 분류의 문서 ID 조회, 지정한 열 기준 정렬 지원
	 * 
	 * @param emc
	 * @param categoryId
	 * @param orderField
	 * @param orderType
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<String> listByCategoryId(EntityManagerContainer emc, String categoryId, String orderField,
			String orderType, Integer maxCount) throws Exception {
		if (StringUtils.isEmpty(categoryId)) {
			return null;
		}
		Business business = new Business(emc);
		return business.getDocumentFactory().listByCategoryId(categoryId, orderField, orderType, maxCount);
	}

	public List<String> listByAppId(EntityManagerContainer emc, String appId, String documentType, Integer maxCount)
			throws Exception {
		if (StringUtils.isEmpty(appId)) {
			return null;
		}
		if (StringUtils.isEmpty(documentType)) {
			documentType = "全部";
		}
		Business business = new Business(emc);
		return business.getDocumentFactory().listByAppId(appId, documentType, maxCount);
	}

	public List<String> listByCategoryId(EntityManagerContainer emc, String categoryId) throws Exception {
		if (StringUtils.isEmpty(categoryId)) {
			return null;
		}
		Business business = new Business(emc);
		return business.getDocumentFactory().listByCategoryId(categoryId, 10000);
	}

	public Document get(EntityManagerContainer emc, String id) throws Exception {
		if (StringUtils.isEmpty(id)) {
			return null;
		}
		Business business = new Business(emc);
		return business.getDocumentFactory().get(id);
	}

	public Long countByCategoryId(EntityManagerContainer emc, String categoryId, boolean containDraft)
			throws Exception {
		if (StringUtils.isEmpty(categoryId)) {
			return null;
		}
		Business business = new Business(emc);
		if (containDraft) {
			return business.getDocumentFactory().countByCategoryId(categoryId);
		} else {
			return business.getDocumentFactory().countByCategoryIdNotContainDraft(categoryId);
		}
	}

	public Long countByAppId(EntityManagerContainer emc, String appId) throws Exception {
		if (StringUtils.isEmpty(appId)) {
			return null;
		}
		Business business = new Business(emc);
		return business.getDocumentFactory().countByAppId(appId);
	}

	/**
	 * Document 테이블에서 권한을 무시하고 조건에 따라 문서 수 조회
	 * 
	 * @param emc
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithConditionOutofPermission(EntityManagerContainer emc, QueryFilter queryFilter)
			throws Exception {
		if (queryFilter == null) {
			return null;
		}
		Business business = new Business(emc);
		return business.getDocumentFactory().countWithConditionOutofPermission(queryFilter);
	}

	public Document save(EntityManagerContainer emc, Document wrapIn) throws Exception {
		if (wrapIn == null) {
			throw new Exception("wrapIn document is null!");
		}
		Document document = null;
		Log log = null;
		document = emc.find(wrapIn.getId(), Document.class);
		emc.beginTransaction(Document.class);
		emc.beginTransaction(Log.class);

		if (wrapIn.getTitle() != null && wrapIn.getTitle().length() > 70) {
			wrapIn.setTitle(wrapIn.getTitle().substring(0, 70));
		}
		if (StringUtils.isEmpty(wrapIn.getAppAlias())) {
			wrapIn.setAppAlias(wrapIn.getAppName());
		}
		if (document == null) {
			document = new Document();
			wrapIn.copyTo(document);
			if (StringUtils.isEmpty(document.getId())) {
				document.setId(Document.createId());
			}
			emc.persist(document, CheckPersistType.all);
			log = getOperationLog(document.getCreatorPerson(), "用户[" + document.getCreatorPerson() + "]成功创建一个文档信息",
					document.getAppId(), document.getCategoryId(), document.getId(), "", "DOCUMENT", "新增");
			emc.persist(log, CheckPersistType.all);
		} else {
			// 일부 데이터는 갱신 불필요
			Date createTime = null;
			String createPerson = null;
			String creatorIdentity = null;
			String creatorUnitName = null;
			String creatorTopUnitName = null;
			Date publishTime = null;
			Long viewCount = wrapIn.getViewCount();

			// 먼저 기존 값 기록
			if (wrapIn.getViewCount() == null || wrapIn.getViewCount() < document.getViewCount()) {
				viewCount = document.getViewCount();
			}
			if (document.getCreateTime() != null) {
				createTime = document.getCreateTime();
			}
			if (document.getCreatorPerson() != null) {
				createPerson = document.getCreatorPerson();
			}
			if (document.getCreatorIdentity() != null) {
				creatorIdentity = document.getCreatorIdentity();
			}
			if (document.getCreatorUnitName() != null) {
				creatorUnitName = document.getCreatorUnitName();
			}
			if (document.getCreatorTopUnitName() != null) {
				creatorTopUnitName = document.getCreatorTopUnitName();
			}
			if (document.getPublishTime() != null) {
				publishTime = document.getPublishTime();
			}
			// objectSecurityClearance는 Data 저장을 통해 수정
			wrapIn.copyTo(document, ListTools.toList(JpaObject.FieldsUnmodify, Document.isTop_FIELDNAME,
					Document.isAllRead_FIELDNAME));
			document.setViewCount(viewCount);
			Business business = new Business(emc);
			document.setCommendCount(business.documentCommendFactory().countByDocAndType(document.getId(),
					DocumentCommend.COMMEND_TYPE_DOCUMENT));
			if (createTime != null) {
				document.setCreateTime(createTime);
			}
			if (createPerson != null) {
				document.setCreatorPerson(createPerson);
			}
			if (creatorIdentity != null) {
				document.setCreatorIdentity(creatorIdentity);
			}
			if (creatorUnitName != null) {
				document.setCreatorUnitName(creatorUnitName);
			}
			if (creatorTopUnitName != null) {
				document.setCreatorTopUnitName(creatorTopUnitName);
			}
			if (publishTime != null && DocumentStatus.PUBLISHED.getValue().equals(document.getDocStatus())) {
				document.setPublishTime(publishTime);
			}
			document.setReviewed(false);
			emc.check(document, CheckPersistType.all);

			log = getOperationLog(document.getCreatorPerson(), "用户[" + document.getCreatorPerson() + "]成功更新一个文档信息",
					document.getAppId(), document.getCategoryId(), document.getId(), "", "DOCUMENT", "更新");
			emc.persist(log, CheckPersistType.all);
		}
		emc.commit();
		return document;
	}

	public Log getOperationLog(String person, String description, String appId, String categoryId, String documentId,
			String fileId, String operationLevel, String operationType) throws Exception {
		String operatorUid = person;
		Log log = new Log();
		log.setAppId(appId);
		log.setCategoryId(categoryId);
		log.setDescription(description);
		log.setDocumentId(documentId);
		log.setFileId(fileId);
		log.setOperatorName(operatorUid);
		log.setOperatorUid(operatorUid);
		log.setOperationType(operationType);
		log.setOperationLevel(operationLevel);
		return log;
	}

	/**
	 * Document 정보 페이지 조회(권한 무시) document와 Review는 sequence 외에 5개 정렬 열 title, appAlias,
	 * categoryAlias, creatorPerson, creatorUnitName의 페이지 조회 지원 sequence와 title,
	 * appAlias, categoryAlias, categoryName, creatorUnitName 외의 다른 열 정렬은 모두 메모리에서 페이지 처리
	 * 
	 * @param emc
	 * @param pageSize
	 * @param lastId
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Document> listNextWithCondition(EntityManagerContainer emc, Integer pageSize, String lastId,
			String orderField, String orderType, QueryFilter queryFilter) throws Exception {
		if (pageSize == 0) {
			pageSize = 20;
		}
		if (StringUtils.isEmpty(orderType)) {
			orderType = "desc";
		}
		Business business = new Business(emc);
		Document document = null;
		String sequenceFieldValue = null;
		Object obj = null;

		// 정렬 열 보정
		orderField = Document.getSequnceFieldNameWithProperty(orderField);

		// lastId 값이 지정되면 해당 ID 문서의 sequence 조회 필요
		if (StringUtils.isNotEmpty(lastId) && !StringUtils.equalsIgnoreCase(lastId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			document = emc.find(lastId, Document.class);
			if (document != null) {// ID에 해당하는 기록의 sequence 조회
				obj = PropertyUtils.getProperty(document, orderField);
				if (obj != null) {
					sequenceFieldValue = obj.toString();
				}
			}
		}
		return business.getDocumentFactory().listNextWithCondition(pageSize, sequenceFieldValue, orderField, orderType,
				queryFilter);
	}

	/**
	 * Document 정보 페이지 조회(권한 무시) document와 Review는 sequence 외에 5개 정렬 열 title, appAlias,
	 * categoryAlias, creatorPerson, creatorUnitName의 페이지 조회 지원 sequence와 title,
	 * appAlias, categoryAlias, categoryName, creatorUnitName 외의 다른 열 정렬은 모두 메모리에서 페이지 처리
	 * 
	 * @param emc
	 * @param pageSize
	 * @param lastId
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Document> listPrevWithCondition(EntityManagerContainer emc, Integer pageSize, String lastId,
			String orderField, String orderType, QueryFilter queryFilter) throws Exception {
		if (pageSize == 0) {
			pageSize = 20;
		}
		if (StringUtils.isEmpty(orderType)) {
			orderType = "desc";
		}
		Business business = new Business(emc);
		Document document = null;
		String sequenceFieldValue = null;
		Object obj = null;

		// 정렬 열 보정
		orderField = Document.getSequnceFieldNameWithProperty(orderField);

		// lastId 값이 지정되면 해당 ID 문서의 sequence 조회 필요
		if (StringUtils.isNotEmpty(lastId) && !StringUtils.equalsIgnoreCase(lastId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			document = emc.find(lastId, Document.class);
			if (document != null) {// ID에 해당하는 기록의 sequence 조회
				obj = PropertyUtils.getProperty(document, orderField);
				if (obj != null) {
					sequenceFieldValue = obj.toString();
				}
			}
		}
		return business.getDocumentFactory().listPrevWithCondition(pageSize, sequenceFieldValue, orderField, orderType,
				queryFilter);
	}

	public List<Document> listNextWithCondition(EntityManagerContainer emc, String orderField, String orderType,
			QueryFilter queryFilter, int maxCount) throws Exception {
		if (maxCount == 0) {
			maxCount = 20;
		}
		Business business = new Business(emc);
		return business.getDocumentFactory().listNextWithCondition(orderField, orderType, queryFilter, maxCount);
	}

	public List<Document> listMyDraft(EntityManagerContainer emc, String name, List<String> categoryIdList,
			String documentType) throws Exception {
		Business business = new Business(emc);
		return business.getDocumentFactory().listMyDraft(name, categoryIdList, documentType);
	}

	public Item getDataWithDocIdWithPath(EntityManagerContainer emc, Document document, String path0, String path1,
			String path2, String path3, String path4, String path5, String path6, String path7) throws Exception {
		Business business = new Business(emc);
		return business.itemFactory().getWithDocmentWithPath(document.getId(), path0, path1, path2, path3, path4, path5,
				path6, path7);
	}

	public List<String> listReviewedIdsByCategoryId(EntityManagerContainer emc, String categoryId, int maxCount)
			throws Exception {
		Business business = new Business(emc);
		return business.getDocumentFactory().listReviewedIdsByCategoryId(categoryId, maxCount);
	}

	public Boolean inReview(EntityManagerContainer emc, String documentId) throws Exception {
		Document document = emc.find(documentId, Document.class);
		emc.beginTransaction(Document.class);
		document.setReviewed(false);
		emc.persist(document, CheckPersistType.all);
		emc.commit();
		return true;
	}

	public List<String> listUnReviewIds(EntityManagerContainer emc, Integer maxCount) throws Exception {
		Business business = new Business(emc);
		return business.getDocumentFactory().listUnReviewIds(maxCount);
	}

	public List<String> listUnReviewIds(EntityManagerContainer emc, Integer maxCount, Integer minutesAgo)
			throws Exception {
		Business business = new Business(emc);
		return business.getDocumentFactory().listUnReviewIds(maxCount, minutesAgo);
	}

	/**
	 * 지정 ID의 문서 삭제
	 * 
	 * @param emc
	 * @param docId
	 * @throws Exception
	 */
	public void delete(EntityManagerContainer emc, String docId) throws Exception {
		Business business = new Business(emc);
		emc.beginTransaction(Document.class);
		emc.beginTransaction(Item.class);
		emc.beginTransaction(FileInfo.class);
		emc.beginTransaction(DocumentCommentInfo.class);

		List<String> allFileInfoIds = null;
		Document document = emc.find(docId, Document.class);

		// 해당 문서와 관련된 모든 데이터 정보 삭제
		DocumentDataHelper documentDataHelper = new DocumentDataHelper(emc, document);
		documentDataHelper.remove();

		allFileInfoIds = business.getFileInfoFactory().listAllByDocument(docId);
		if (ListTools.isNotEmpty(allFileInfoIds)) {
			StorageMapping mapping = null;
			FileInfo fileInfo = null;
			for (String fileInfoId : allFileInfoIds) {
				fileInfo = emc.find(fileInfoId, FileInfo.class);
				if (fileInfo != null) {
					if ("ATTACHMENT".equals(fileInfo.getFileType())) {
						mapping = ThisApplication.context().storageMappings().get(FileInfo.class,
								fileInfo.getStorage());
						fileInfo.deleteContent(mapping);
					}
				}
				emc.remove(fileInfo, CheckRemoveType.all);
			}
		}

		List<String> commentIds = business.documentCommentInfoFactory().listWithDocument(docId);
		if (ListTools.isNotEmpty(commentIds)) {
			DocumentCommentInfo documentCommentInfo = null;
			for (String commentId : commentIds) {
				documentCommentInfo = emc.find(commentId, DocumentCommentInfo.class);
				emc.remove(documentCommentInfo, CheckRemoveType.all);
			}
		}
		if (document != null) {
			emc.remove(document, CheckRemoveType.all);
		}
		emc.commit();

		// 큐에 넣고 핫 이미지가 아직 존재하는지 확인하여 존재하면 삭제
		ThisApplication.queueDocumentDelete.send(document.getId());
	}

	/**
	 * 지정 문서의 path0 정렬 Item 목록 조회
	 * 
	 * @param emc
	 * @param docIds
	 * @param path0Name
	 * @param orderField
	 * @param orderType
	 * @return
	 * @throws Exception
	 */
	public List<Item> listSortObjWithOrderFieldInData(EntityManagerContainer emc, List<String> docIds, String path0Name,
			String fieldType, String orderType) throws Exception {
		Business business = new Business(emc);
		return business.itemFactory().listSortObjWithOrderFieldInData(docIds, path0Name, fieldType, orderType);
	}
}
