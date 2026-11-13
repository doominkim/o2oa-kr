package com.x.cms.assemble.control.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.DocumentDataHelper;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.factory.ProjectionFactory;
import com.x.cms.assemble.control.jaxrs.document.ActionPersistBatchModifyData.WiDataChange;
import com.x.cms.assemble.control.jaxrs.document.ActionPersistBatchModifyData.Wo;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Document_;
import com.x.cms.core.entity.FileInfo;
import com.x.cms.core.entity.Projection;
import com.x.cms.core.entity.content.Data;
import com.x.cms.core.entity.enums.DocumentStatus;
import com.x.cms.core.entity.message.DocumentEvent;
import com.x.query.core.entity.Item;

/**
 * 문서 정보 영속화 서비스 클래스
 *
 * @author sword
 */
public class DocumentPersistService {
	private static Logger logger = LoggerFactory.getLogger(DocumentPersistService.class);
	private static ReentrantLock lock = new ReentrantLock();
	private DocumentInfoService documentInfoService = new DocumentInfoService();
	private PermissionOperateService permissionService = new PermissionOperateService();
	private CmsBatchOperationPersistService batchOperationPersistService = new CmsBatchOperationPersistService();

	public Document save(Document document, JsonElement jsonElement, String projection) throws Exception {
		if (document == null) {
			throw new Exception("document is null!");
		}
		if (ListTools.isNotEmpty(document.getPictureList())) {
			document.setHasIndexPic(true);
			if (document.getPictureList().size() > 4) {
				document.setIndexPics(StringUtils.join(document.getPictureList().subList(0, 3), ","));
			} else {
				document.setIndexPics(StringUtils.join(document.getPictureList(), ","));
			}
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			document.setModifyTime(new Date());
			document.setSequenceAppAlias(document.getAppAlias() + document.getId());
			document.setSequenceCategoryAlias(document.getCategoryAlias() + document.getId());
			if (document.getTitle().length() > 30) {
				document.setSequenceTitle(document.getTitle().substring(0, 30) + document.getId());
			} else {
				document.setSequenceTitle(document.getTitle() + document.getId());
			}
			if (document.getCreatorPerson().length() > 50) {
				document.setSequenceCreatorPerson(document.getCreatorPerson().substring(0, 50) + document.getId());
			} else {
				document.setSequenceCreatorPerson(document.getCreatorPerson() + document.getId());
			}
			if (document.getCreatorUnitName().length() > 50) {
				document.setSequenceCreatorUnitName(document.getCreatorUnitName().substring(0, 50) + document.getId());
			} else {
				document.setSequenceCreatorUnitName(document.getCreatorUnitName() + document.getId());
			}

			document = documentInfoService.save(emc, document);
			// 데이터 정보가 있으면 데이터 정보 저장

			DocumentDataHelper documentDataHelper = new DocumentDataHelper(emc, document);
			if (jsonElement != null) {
				documentDataHelper.update(jsonElement);
			}
			emc.commit();

			Data data = documentDataHelper.get();
			this.doProjection(emc, projection, data, document);
			data.setDocument(document);
			data.setAttachmentList(emc.listEqual(FileInfo.class, FileInfo.documentId_FIELDNAME, document.getId()));
			documentDataHelper.update(data);
			if (DocumentStatus.isEndStatus(document.getDocStatus())) {
				emc.beginTransaction(DocumentEvent.class);
				DocumentEvent documentEvent = DocumentEvent.updateEventInstance(document);
				emc.persist(documentEvent);
			}

			emc.commit();
			return document;
		} catch (Exception e) {
			throw e;
		}
	}

	public void doProjection(EntityManagerContainer emc, String projection, Data data, Document document)
			throws Exception {
		if (StringUtils.isNotBlank(projection) && XGsonBuilder.isJsonArray(projection)) {
			emc.beginTransaction(Document.class);
			List<Projection> projections = XGsonBuilder.instance().fromJson(projection,
					new TypeToken<List<Projection>>() {
					}.getType());
			ProjectionFactory.projectionDocument(projections, data, document);
		}
	}

	/**
	 * 문서 정보로 데이터 내용의 문서 정보 갱신
	 *
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public Document refreshDocInfoData(Document document) throws Exception {
		if (document == null) {
			throw new Exception("document is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			document = documentInfoService.save(emc, document);
			// 데이터 정보가 있으면 데이터 정보 저장
			DocumentDataHelper documentDataHelper = new DocumentDataHelper(emc, document);
			Data data = documentDataHelper.get();
			data.setDocument(document);
			data.setAttachmentList(emc.listEqual(FileInfo.class, FileInfo.documentId_FIELDNAME, document.getId()));
			documentDataHelper.update(data);
			emc.commit();
			return document;
		} catch (Exception e) {
			throw e;
		}
	}

	void fill(Item o, Document document) {
		/** DateItem과 Document를 같은 파티션에 배치 */
		o.setDistributeFactor(document.getDistributeFactor());
		o.setBundle(document.getId());
		o.setItemCategory(ItemCategory.cms);
	}

	/**
	 * 문서의 분류 정보 변경
	 *
	 * @param document
	 * @param categoryInfo
	 * @throws Exception
	 */
	public Boolean changeCategory(Document document, CategoryInfo categoryInfo) throws Exception {
		if (document == null) {
			throw new Exception("document is empty!");
		}
		if (categoryInfo == null) {
			throw new Exception("categoryInfo is empty!");
		}
		Data data = null;
		DocumentDataHelper documentDataHelper = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction(Document.class);
			emc.beginTransaction(Item.class);

			document = emc.find(document.getId(), Document.class);

			// document 분류 관련 정보 갱신
			document.setAppId(categoryInfo.getAppId());
			document.setAppName(categoryInfo.getAppName());
			document.setCategoryId(categoryInfo.getId());
			document.setCategoryName(categoryInfo.getCategoryName());
			document.setCategoryAlias(categoryInfo.getCategoryAlias());
			document.setForm(categoryInfo.getReadFormId());
			document.setFormName(categoryInfo.getReadFormName());
			document.setModifyTime(new Date());

			document.setSequenceAppAlias(document.getAppAlias() + document.getId());
			document.setSequenceCategoryAlias(document.getCategoryAlias() + document.getId());
			if (document.getTitle().length() > 30) {
				document.setSequenceTitle(document.getTitle().substring(0, 30) + document.getId());
			} else {
				document.setSequenceTitle(document.getTitle() + document.getId());
			}
			if (document.getCreatorPerson().length() > 50) {
				document.setSequenceCreatorPerson(document.getCreatorPerson().substring(0, 50) + document.getId());
			} else {
				document.setSequenceCreatorPerson(document.getCreatorPerson() + document.getId());
			}
			if (document.getCreatorUnitName().length() > 50) {
				document.setSequenceCreatorUnitName(document.getCreatorUnitName().substring(0, 50) + document.getId());
			} else {
				document.setSequenceCreatorUnitName(document.getCreatorUnitName() + document.getId());
			}

			emc.check(document, CheckPersistType.all);

			// 데이터의 document 객체 정보 갱신
			documentDataHelper = new DocumentDataHelper(emc, document);
			data = documentDataHelper.get();
			data.setDocument(document);
			data.setAttachmentList(emc.listEqual(FileInfo.class, FileInfo.documentId_FIELDNAME, document.getId()));
			documentDataHelper.update(data);

			emc.commit();
			return true;
		} catch (Exception e) {
			throw e;
		}
	}

	public Wo changeData(List<String> docIds, List<WiDataChange> dataChanges) throws Exception {
		if (ListTools.isEmpty(docIds)) {
			throw new Exception("docIds is empty!");
		}
		if (ListTools.isEmpty(dataChanges)) {
			throw new Exception("dataChanges is empty!");
		}
		Wo wo = new Wo();
		Data data = null;
		Document document_entity = null;
		DocumentDataHelper documentDataHelper = null;
		for (String docId : docIds) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

				emc.beginTransaction(Document.class);
				emc.beginTransaction(Item.class);

				document_entity = emc.find(docId, Document.class);
				document_entity.setModifyTime(new Date());

				documentDataHelper = new DocumentDataHelper(emc, document_entity);
				data = documentDataHelper.get();
				for (WiDataChange dataChange : dataChanges) {
					if ("Integer".equals(dataChange.getDataType())) {
						data.put(dataChange.getDataPath(), dataChange.getDataInteger());
					} else if ("String".equals(dataChange.getDataType())) {
						data.put(dataChange.getDataPath(), dataChange.getDataString());
					} else if ("Date".equals(dataChange.getDataType())) {
						data.put(dataChange.getDataPath(), dataChange.getDataDate());
					} else if ("Boolean".equals(dataChange.getDataType())) {
						data.put(dataChange.getDataPath(), dataChange.getDataBoolean());
					} else {
						data.put(dataChange.getDataPath(), dataChange.getDataString());
					}
				}
				data.setDocument(document_entity);
				data.setAttachmentList(emc.listEqual(FileInfo.class, FileInfo.documentId_FIELDNAME, document_entity.getId()));
				documentDataHelper.update(data);
				emc.commit();
				wo.increaseSuccess_count(1);
			} catch (Exception e) {
				wo.appendErorrId(docId);
				wo.increaseError_count(1);
				e.printStackTrace();
			}
		}
		return wo;
	}

	public Boolean inReview(String documentId) throws Exception {
		if (StringUtils.isEmpty(documentId)) {
			throw new Exception("documentId is empty!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return documentInfoService.inReview(emc, documentId);
		} catch (Exception e) {
			throw e;
		}
	}

	public void topDocument(String id) throws Exception {
		if (StringUtils.isEmpty(id)) {
			throw new Exception("id is empty!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Document document = emc.find(id, Document.class);
			if (document != null) {
//				emc.beginTransaction( Item.class );
				emc.beginTransaction(Document.class);
				document.setIsTop(true);

				DocumentDataHelper documentDataHelper = new DocumentDataHelper(emc, document);
				Data data = documentDataHelper.get();
				data.setDocument(document);
				data.setAttachmentList(emc.listEqual(FileInfo.class, FileInfo.documentId_FIELDNAME, document.getId()));
				documentDataHelper.update(data);

				emc.commit();
			}

		} catch (Exception e) {
			throw e;
		}
	}

	public void unTopDocument(String id) throws Exception {
		if (StringUtils.isEmpty(id)) {
			throw new Exception("id is empty!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Document document = emc.find(id, Document.class);
			if (document != null) {
				emc.beginTransaction(Document.class);
				document.setIsTop(false);
				DocumentDataHelper documentDataHelper = new DocumentDataHelper(emc, document);
				Data data = documentDataHelper.get();
				data.setDocument(document);
				data.setAttachmentList(emc.listEqual(FileInfo.class, FileInfo.documentId_FIELDNAME, document.getId()));
				documentDataHelper.update(data);
				emc.commit();
			}

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 지정 ID의 문서 삭제
	 *
	 * @param docId
	 * @throws Exception
	 */
	public void delete(String docId) throws Exception {
		if (StringUtils.isEmpty(docId)) {
			throw new Exception("docId is empty!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			documentInfoService.delete(emc, docId);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 독자·작성자 목록으로 문서의 모든 권한 정보 갱신
	 *
	 * @param docId
	 * @param readerList
	 * @param authorList
	 * @throws Exception
	 */
	public Document refreshDocumentPermission(String docId, List<PermissionInfo> readerList,
			List<PermissionInfo> authorList) throws Exception {
		if (StringUtils.isEmpty(docId)) {
			throw new Exception("docId is empty!");
		}
		Document document = permissionService.refreshDocumentPermission(docId, readerList, authorList);

		batchOperationPersistService.addDocumentOperation(CmsBatchOperationProcessService.OPT_OBJ_DOCUMENT,
				CmsBatchOperationProcessService.OPT_TYPE_PERMISSION, docId, docId, "문서 권한 갱신: ID=" + docId);

		return document;
	}

	/**
	 * 구성된 권한 정보 목록으로 지정 문서의 권한 정보 갱신
	 *
	 * @param docId
	 * @param permissionList
	 * @throws Exception
	 */
	public void refreshDocumentPermission(String docId, List<PermissionInfo> permissionList) throws Exception {
		if (StringUtils.isEmpty(docId)) {
			throw new Exception("docId is empty!");
		}
		permissionService.refreshDocumentPermission(docId, permissionList);
	}

	/**
	 * 모든 문서의 권한과 review 정보 재계산
	 */
	public void refreshAllDocumentPermission(boolean flag) throws Exception {
		try {
			if (lock.tryLock()) {
				AppInfoServiceAdv appInfoService = new AppInfoServiceAdv();
				DocumentQueryService documentQueryService = new DocumentQueryService();
				List<String> appIds = appInfoService.listAllIds("정보");
				if (ListTools.isNotEmpty(appIds)) {
					for (String appId : appIds) {
						// 지정 App의 모든 문서 Id 조회
						List<String> documentIds = documentQueryService.listIdsByAppId(appId, "정보", 50000);
						logger.info("앱 {}의 데이터 총 {}건 새로 고침", appId, documentIds.size());
						if (ListTools.isNotEmpty(documentIds)) {
							int count = 0;
							for (List<String> partDocIds : ListTools.batch(documentIds, 4)) {
								count = count + 4;
								List<CompletableFuture<Void>> futures = new TreeList<>();
								for (String documentId : partDocIds) {
									CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
										try (EntityManagerContainer emc = EntityManagerContainerFactory.instance()
												.create()) {
											ReviewService reviewService = new ReviewService();
											boolean fullRead = reviewService.refreshDocumentReview(emc, documentId);
											Document document = emc.find(documentId, Document.class);
											emc.beginTransaction(Document.class);
											document.setIsAllRead(fullRead);
											emc.commit();
										} catch (Exception e) {
											logger.warn("문서 권한 새로 고침 예외 1: {}", e.getMessage());
										}
									}, ThisApplication.forkJoinPool());
									futures.add(future);
								}
								if (!flag) {
									Calendar cal = DateUtils.toCalendar(new Date());
									if (cal.get(Calendar.HOUR_OF_DAY) > 6) {
										lock.unlock();
										return;
									}
								}
								for (CompletableFuture<Void> future : futures) {
									try {
										future.get(200, TimeUnit.SECONDS);
									} catch (Exception e) {
										logger.warn("문서 권한 새로 고침 예외 2: {}", e.getMessage());
									}
								}
								futures.clear();
								if (flag && count > 199 && count % 200 == 0) {
									logger.info("앱 문서 권한 {}개 새로 고침 완료", count);
								}
							}
						}
					}
					CacheManager.notify(Document.class);
				}
				lock.unlock();
			}
		} catch (Exception e) {
			lock.unlock();
			logger.error(e);
		}
	}

	public boolean refreshDocumentPermissionByCategory(String categoryId) {
		boolean flag = false;
		try {
			if (lock.tryLock()) {
				List<String> documentIds = null;
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					EntityManager em = emc.get(Document.class);
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<String> cq = cb.createQuery(String.class);
					Root<Document> root = cq.from(Document.class);
					Predicate p = cb.equal(root.get(Document_.categoryId), categoryId);
					p = cb.and(p, cb.equal(root.get(Document_.documentType), "정보"));
					cq.select(root.get(Document_.id)).where(p);
					documentIds = em.createQuery(cq).getResultList();
				}
				if (ListTools.isNotEmpty(documentIds)) {
					logger.info("분류 {}의 데이터 총 {}건 새로 고침", categoryId, documentIds.size());
					int count = 0;
					for (List<String> partDocIds : ListTools.batch(documentIds, 10)) {
						count = count + 10;
						List<CompletableFuture<Void>> futures = new TreeList<>();
						for (String documentId : partDocIds) {
							CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
								try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
									ReviewService reviewService = new ReviewService();
									boolean fullRead = reviewService.refreshDocumentReview(emc, documentId);
									Document document = emc.find(documentId, Document.class);
									emc.beginTransaction(Document.class);
									document.setIsAllRead(fullRead);
									emc.commit();
								} catch (Exception e) {
									logger.warn("문서 권한 새로 고침 예외 1: {}", e.getMessage());
								}
							}, ThisApplication.forkJoinPool());
							futures.add(future);
						}
						for (CompletableFuture<Void> future : futures) {
							try {
								future.get(200, TimeUnit.SECONDS);
							} catch (Exception e) {
								logger.warn("문서 권한 새로 고침 예외 2: {}", e.getMessage());
							}
						}
						futures.clear();
						if (count > 99 && count % 100 == 0) {
							logger.info("분류 문서 권한 {}개 새로 고침 완료", count);
						}
					}
				}
				CacheManager.notify(Document.class);
				lock.unlock();
				flag = true;
				logger.info("분류 {}의 권한 새로 고침 완료", categoryId);
			} else {
				logger.info("권한을 새로 고치는 중인 분류가 있습니다. 잠시 후 다시 시도하십시오....");
			}
		} catch (Exception e) {
			lock.unlock();
			logger.error(e);
		}
		return flag;
	}
}
