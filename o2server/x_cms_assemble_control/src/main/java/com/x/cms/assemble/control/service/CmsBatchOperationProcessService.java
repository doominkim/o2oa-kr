package com.x.cms.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.CmsBatchOperation;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Review;

/**
 * 일괄 처리 작업 실행
 */
public class CmsBatchOperationProcessService {

	public static final String OPT_OBJ_DOCUMENT = "DOCUMENT";
	public static final String OPT_OBJ_CATEGORY = "CATEGORY";
	public static final String OPT_OBJ_APPINFO = "APPINFO";
	public static final String OPT_TYPE_PERMISSION = "PERMISSION";
	public static final String OPT_TYPE_UPDATENAME = "UPDATENAME";
	public static final String OPT_TYPE_DELETE = "DELETE";

	private static  Logger logger = LoggerFactory.getLogger( CmsBatchOperationProcessService.class );
	private DocumentInfoService documentInfoService = new DocumentInfoService();
	private ReviewService reviewService = new ReviewService();
	/**
	 * 일괄 처리 작업 실행
	 * @param cmsBatchOperation
	 * @return
	 * @throws Exception
	 */
	public String process( CmsBatchOperation cmsBatchOperation ) throws Exception {
		logger.debug( "process -> Cms processing batch operation: " + cmsBatchOperation.toString() );
		//먼저 cmsBatchOperation 상태를 실행 중으로 변경
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			cmsBatchOperation = emc.find( cmsBatchOperation.getId(), CmsBatchOperation.class );
			if( cmsBatchOperation != null ) {
				cmsBatchOperation.setIsRunning( true );
				emc.beginTransaction( CmsBatchOperation.class );
				emc.check( cmsBatchOperation, CheckPersistType.all );
				emc.commit();
				logger.debug( "process -> cms change batch operation running......: " );
			}
		} catch (Exception e) {
			throw e;
		}

		if( "DOCUMENT".equalsIgnoreCase( cmsBatchOperation.getObjType() )) {
			if( "PERMISSION".equalsIgnoreCase( cmsBatchOperation.getOptType() )) {//문서 처리
				//categoryName이 기존 분류 이름인 모든 문서의 분류 및 게시판 관련 정보 갱신 후 마지막으로 현재 일괄 처리 정보 삭제
				refreshDocumentReview( cmsBatchOperation.getId(), cmsBatchOperation.getBundle() );
			}else if( "DELETE".equalsIgnoreCase( cmsBatchOperation.getOptType()  )) {
				//문서 삭제 및 분류가 속한 게시판의 분류 수와 문서 수 수정
				deleteDocumentReview( cmsBatchOperation.getId(), cmsBatchOperation.getBundle() );
			}
		}else  if( "CATEGORY".equalsIgnoreCase( cmsBatchOperation.getObjType() )) {
			//분류 처리: 분류 이름 변경, 분류 삭제
			if( "UPDATENAME".equalsIgnoreCase( cmsBatchOperation.getOptType() )) {
				if( StringUtils.isNotEmpty( cmsBatchOperation.getOldInfo() )) {
					//categoryName이 기존 분류 이름인 모든 문서의 분류 및 게시판 관련 정보 갱신 후 마지막으로 현재 일괄 처리 정보 삭제
					changeCategoryNameInDocument( cmsBatchOperation.getId(), cmsBatchOperation.getBundle(), cmsBatchOperation.getOldInfo() );
				}
			}else if( "PERMISSION".equalsIgnoreCase( cmsBatchOperation.getOptType()  )) {
				//분류 수정 권한
				refreshDocumentReviewInCagetory( cmsBatchOperation.getId(), cmsBatchOperation.getBundle() );
			}else if( "DELETE".equalsIgnoreCase( cmsBatchOperation.getOptType()  )) {
				//문서 삭제 및 분류가 속한 게시판의 분류 수와 문서 수 수정
				deleteDocumentInCategory( cmsBatchOperation.getId(), cmsBatchOperation.getBundle() );
			}
		}else  if( "APPINFO".equalsIgnoreCase( cmsBatchOperation.getObjType() )) {
			//게시판 처리: 게시판 이름 변경, 게시판 삭제
			if( "UPDATENAME".equalsIgnoreCase( cmsBatchOperation.getOptType() )) {
				if( StringUtils.isNotEmpty( cmsBatchOperation.getOldInfo() )) {
					//AppName이 기존 게시판 이름인 모든 분류와 문서의 게시판 관련 정보를 갱신하고, 마지막으로 현재 배치 처리 정보 삭제
					changeAppNameInCategory( cmsBatchOperation.getId(), cmsBatchOperation.getBundle(), cmsBatchOperation.getOldInfo() );
				}
			}else if( "PERMISSION".equalsIgnoreCase( cmsBatchOperation.getOptType()  )) {
				//게시판 수정 권한
				refreshDocumentReviewInAppInfo( cmsBatchOperation.getId(), cmsBatchOperation.getBundle() );
			}else if( "DELETE".equalsIgnoreCase( cmsBatchOperation.getOptType()  )) {
				//분류와 문서 삭제 및 분류가 속한 게시판의 분류 수와 문서 수 수정
				deleteDocumentInApp( cmsBatchOperation.getId(), cmsBatchOperation.getBundle() );
			}
		}
		logger.info( "cms batch operation process completed." );
		return "error";
	}

	private void refreshDocumentReviewInAppInfo(String id, String appId) throws Exception {
		CmsBatchOperation cmsBatchOperation = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			cmsBatchOperation = emc.find( id, CmsBatchOperation.class );
			//분류의 모든 문서를 조회해 문서 Review 갱신으로 재발송, 게시판 삭제 일괄 작업(분류 및 문서 대상) 정보 추가
			List<String> ids = null;
			List<Document> documentList = null;
			Integer maxQueryCount = 1000;
			Long count = business.getDocumentFactory().countByAppId(appId);
			Long maxTimes = count/maxQueryCount + 2;
			AppInfo appInfo = emc.find(appId, AppInfo.class );
			logger.info( "refreshDocumentReviewInAppInfo -> There are '" + count +"' document need refresh review, need to process '" + maxTimes + "' times ......" );
			for( int i=0; i<=maxTimes; i++ ) {
				ids = business.getDocumentFactory().listReviewedIdsByAppId( appId, maxQueryCount );
				logger.info( "refreshDocumentReviewInAppInfo -> This is the '" + ( i + 1 ) +"' times process......" );
				if(ListTools.isNotEmpty( ids )) {
					documentList = emc.list( Document.class, ids);
				}
				if( ListTools.isNotEmpty( documentList )) {
					CmsBatchOperationPersistService cmsBatchOperationPersistService = new CmsBatchOperationPersistService();
					for( Document document : documentList ) {
						emc.beginTransaction(Document.class );
						document.setReviewed( false );
						document.setAppName( appInfo.getAppName() );
						document.setAppAlias( appInfo.getAppAlias() );
						if( StringUtils.isEmpty( document.getAppAlias() )) {
							document.setAppAlias( appInfo.getAppName() );
						}
						emc.check( document, CheckPersistType.all );
						emc.commit();
						cmsBatchOperationPersistService.addOperation(
								CmsBatchOperationProcessService.OPT_OBJ_DOCUMENT,
								CmsBatchOperationProcessService.OPT_TYPE_PERMISSION, document.getId(), document.getId(), "게시판 권한 변경에 따른 문서 Review 변경: ID=" + document.getId() );
					}

				}
				ids = null;
				documentList = null;
			}
			if( cmsBatchOperation != null ) {
				emc.beginTransaction( CmsBatchOperation.class );
				emc.remove( cmsBatchOperation, CheckRemoveType.all );
				logger.info( "cms delete batch operation: " + cmsBatchOperation.toString()  );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private void refreshDocumentReviewInCagetory(String id, String categoryId) throws Exception {
		CmsBatchOperation cmsBatchOperation = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			cmsBatchOperation = emc.find( id, CmsBatchOperation.class );
			//분류의 모든 문서를 조회해 문서 Review 갱신으로 재발송, 게시판 삭제 일괄 작업(분류 및 문서 대상) 정보 추가
			List<String> ids = null;
			List<Document> documentList = null;
			Integer maxQueryCount = 1000;
			Long count = business.getDocumentFactory().countByCategoryId(categoryId);
			Long maxTimes = count/maxQueryCount + 2;
			logger.info( "refreshDocumentReviewInCagetory -> There are : " + count + " documents need to refresh review, maxTimes=" + maxTimes  );

			for( int i=0; i<=maxTimes; i++ ) {
				ids = business.getDocumentFactory().listReviewedIdsByCategoryId(categoryId, maxQueryCount );
				if(ListTools.isNotEmpty( ids )) {
					documentList = emc.list( Document.class, ids);
				}
				if( ListTools.isNotEmpty( documentList )) {
					CmsBatchOperationPersistService cmsBatchOperationPersistService = new CmsBatchOperationPersistService();
					for( Document document : documentList ) {
						emc.beginTransaction(Document.class );
						document.setReviewed( false );
						emc.check( document, CheckPersistType.all );
						emc.commit();

						logger.info( "refreshDocumentReviewInCagetory -> Send docment permission operation to queue[queueBatchOperation], document:" + document.getTitle()  );
						cmsBatchOperationPersistService.addOperation(
								CmsBatchOperationProcessService.OPT_OBJ_DOCUMENT,
								CmsBatchOperationProcessService.OPT_TYPE_PERMISSION, document.getId(), document.getId(), "분류 권한 변경에 따른 문서 Review 변경: ID=" + document.getId() );
					}

				}
				ids = null;
				documentList = null;
			}
			if( cmsBatchOperation != null ) {
				emc.beginTransaction( CmsBatchOperation.class );
				emc.remove( cmsBatchOperation, CheckRemoveType.all );
				logger.info( "refreshDocumentReviewInCagetory -> cms delete batch operation: " + cmsBatchOperation.toString()  );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private void deleteDocumentReview(String id, String docId) throws Exception {
		CmsBatchOperation cmsBatchOperation = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			cmsBatchOperation = emc.find( id, CmsBatchOperation.class );
			logger.info( "deleteDocumentReview -> delete all reviews for document: " + docId );
			reviewService.deleteDocumentReview( emc, docId );
			if( cmsBatchOperation != null ) {
				emc.beginTransaction( CmsBatchOperation.class );
				emc.remove( cmsBatchOperation, CheckRemoveType.all );
				logger.info( "deleteDocumentReview -> cms delete batch operation: " + cmsBatchOperation.toString()  );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
		CacheManager.notify( Review.class );
		CacheManager.notify( Document.class );
	}

	/**
	 * DB의 문서 정보로 문서 Review 정보 재계산
	 * 전체 삭제 후 Review 기록 다시 삽입
	 * @param id
	 * @param docId
	 * @throws Exception
	 */
	private void refreshDocumentReview( String id, String docId ) throws Exception {
		CmsBatchOperation cmsBatchOperation = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			cmsBatchOperation = emc.find( id, CmsBatchOperation.class );

			boolean fullRead = reviewService.refreshDocumentReview( emc, docId );

			Document document = emc.find( docId, Document.class );
			if( document != null ) {
				emc.beginTransaction( Document.class );
				document.setReviewed( true );
				document.setIsAllRead(fullRead);

				if(StringUtils.isEmpty( document.getAppAlias())) {
					document.setAppAlias( document.getAppName() );
				}

				document.setSequenceAppAlias( document.getAppAlias() + document.getId() );
				document.setSequenceCategoryAlias( document.getCategoryAlias() + document.getId() );
				if( StringUtils.isNotEmpty( document.getTitle() ) && document.getTitle().length() > 30 ) {
					document.setSequenceTitle( document.getTitle().substring(0, 30) + document.getId() );
				}else {
					document.setSequenceTitle( document.getTitle() + document.getId() );
				}
				if( StringUtils.isNotEmpty( document.getCreatorPerson() ) && document.getCreatorPerson().length() > 50 ) {
					document.setSequenceCreatorPerson( document.getCreatorPerson().substring(0, 50) + document.getId() );
				}else {
					document.setSequenceCreatorPerson( document.getCreatorPerson() + document.getId() );
				}
				if( StringUtils.isNotEmpty( document.getCreatorUnitName() ) && document.getCreatorUnitName().length() > 50 ) {
					document.setSequenceCreatorUnitName( document.getCreatorUnitName().substring(0, 50) + document.getId() );
				}else {
					document.setSequenceCreatorUnitName( document.getCreatorUnitName() + document.getId() );
				}

				emc.check( document, CheckPersistType.all );
				emc.commit();
			}

			if( cmsBatchOperation != null ) {
				emc.beginTransaction( CmsBatchOperation.class );
				emc.remove( cmsBatchOperation, CheckRemoveType.all );
				logger.info( "refreshDocumentReview -> cms delete batch operation: " + cmsBatchOperation.toString()  );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
		CacheManager.notify( Review.class );
		CacheManager.notify( Document.class );
	}

	/**
	 * DB의 문서 정보로 문서 Review 정보 재계산
	 * 전체 삭제 후 Review 기록 다시 삽입
	 * @param docId
	 * @throws Exception
	 */
	public void refreshDocumentReview(String docId ) throws Exception {
		logger.debug( "refreshDocumentReview {}", docId);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			boolean fullRead = reviewService.refreshDocumentReview( emc, docId );

			Document document = emc.find( docId, Document.class );
			if( document != null ) {
				emc.beginTransaction( Document.class );
				document.setReviewed( true );
				document.setIsAllRead(fullRead);
				if( StringUtils.isEmpty( document.getAppAlias()  )) {
					document.setAppAlias( document.getAppName() );
				}

				document.setSequenceAppAlias( document.getAppAlias() + document.getId() );
				document.setSequenceCategoryAlias( document.getCategoryAlias() + document.getId() );
				if( StringUtils.isNotEmpty( document.getTitle() ) && document.getTitle().length() > 30 ) {
					document.setSequenceTitle( document.getTitle().substring(0, 30) + document.getId() );
				}else {
					document.setSequenceTitle( document.getTitle() + document.getId() );
				}
				if( StringUtils.isNotEmpty( document.getCreatorPerson() ) && document.getCreatorPerson().length() > 50 ) {
					document.setSequenceCreatorPerson( document.getCreatorPerson().substring(0, 50) + document.getId() );
				}else {
					document.setSequenceCreatorPerson( document.getCreatorPerson() + document.getId() );
				}
				if( StringUtils.isNotEmpty( document.getCreatorUnitName() ) && document.getCreatorUnitName().length() > 50 ) {
					document.setSequenceCreatorUnitName( document.getCreatorUnitName().substring(0, 50) + document.getId() );
				}else {
					document.setSequenceCreatorUnitName( document.getCreatorUnitName() + document.getId() );
				}

				emc.check( document, CheckPersistType.all );
				emc.commit();
			}
		} catch (Exception e) {
			logger.warn("refreshDocumentReview error:{}",e.getMessage());
		}finally {
			CacheManager.notify( Review.class );
			CacheManager.notify( Document.class );
		}
	}


	/**
	 * 게시판 아래 모든 문서 삭제 후 마지막으로 현재 일괄 처리 정보 삭제
	 * @param id
	 * @param bundle
	 * @throws Exception
	 */
	private void deleteDocumentInApp(String id, String bundle) throws Exception {
		Long docCount = 0L;
		Integer totalWhileCount = 0;
		Integer currenteWhileCount = 0;
		Integer queryMaxCount = 1000;
		List<String> document_ids = null;
		List<String> categoryIds = null;
		CmsBatchOperation cmsBatchOperation = null;
		CategoryInfo categoryInfo = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			cmsBatchOperation = emc.find( id, CmsBatchOperation.class );
			categoryIds = business.getCategoryInfoFactory().listByAppId( bundle );

			if( ListTools.isNotEmpty( categoryIds )) {
				for( String categoryId : categoryIds ) {
					categoryInfo = emc.find( categoryId, CategoryInfo.class );
					if( categoryInfo != null ) {
						emc.beginTransaction( CategoryInfo.class );
						emc.remove( categoryInfo, CheckRemoveType.all );
						logger.info( "deleteDocumentInApp -> Cms processing batch operation: remove category: " + categoryInfo.getId() + ", alias: " + categoryInfo.getCategoryAlias() );
						emc.commit();
					}

					//해당 디렉터리 아래 모든 문서 삭제
					docCount = business.getDocumentFactory().countByCategoryId( categoryId );
					if( docCount > 0 ) {
						totalWhileCount = (int) (docCount/queryMaxCount) + 1;
						if( totalWhileCount > 0 ) {
							while( docCount > 0 && currenteWhileCount <= totalWhileCount ) {//문서 1000건 조회 후 처리
								document_ids = business.getDocumentFactory().listByCategoryId( bundle, queryMaxCount );
								for( String docId : document_ids ){
									try {
										documentInfoService.delete( emc, docId );
										new CmsBatchOperationPersistService().addOperation(
												CmsBatchOperationProcessService.OPT_OBJ_DOCUMENT,
												CmsBatchOperationProcessService.OPT_TYPE_DELETE, id, id, "게시판 삭제에 따른 문서 삭제: ID=" + id );
										logger.info( "deleteDocumentInApp -> cms processing batch operation: remove document("+ currenteWhileCount +"/" + totalWhileCount + "): " + docId );
									}catch( Exception e ) {
										e.printStackTrace();
									}
								}
								//현재 반복 횟수 +1
								currenteWhileCount ++;
								//남은 문서 수 다시 조회
								docCount = business.getDocumentFactory().countByCategoryId( categoryInfo.getId() );
							}
						}
					}
				}
			}
			if( cmsBatchOperation != null ) {
				emc.beginTransaction( CmsBatchOperation.class );
				emc.remove( cmsBatchOperation, CheckRemoveType.all );
				logger.info( "deleteDocumentInApp -> cms delete batch operation: " + cmsBatchOperation.toString()  );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}

		CacheManager.notify( Document.class );
		CacheManager.notify( CategoryInfo.class );
	}

	/**
	 * AppName이 기존 게시판 이름인 모든 분류와 문서의 게시판 관련 정보를 갱신하고, 마지막으로 현재 배치 처리 정보 삭제
	 * @param id
	 * @param bundle
	 * @param oldName
	 * @throws Exception
	 */
	private void changeAppNameInCategory( String id, String bundle, String oldName ) throws Exception {
		Long docCount = 0L;
		Integer totalWhileCount = 0;
		Integer currenteWhileCount = 0;
		Integer queryMaxCount = 1000;
		List<String> document_ids = null;
		List<String> categoryIds = null;
		AppInfo appInfo = null;
		CategoryInfo categoryInfo = null;
		CmsBatchOperation cmsBatchOperation = null;

		//먼저 해당 게시판 아래 모든 분류 정보 목록 조회
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			cmsBatchOperation = emc.find( id, CmsBatchOperation.class );
			appInfo = emc.find( bundle, AppInfo.class );
			categoryIds = business.getCategoryInfoFactory().listByAppId( bundle );
			if( appInfo == null ) {
				throw new Exception("appinfo not exists.ID: " + bundle );
			}
			if( ListTools.isNotEmpty( categoryIds )) {
				for( String categoryId : categoryIds ) {
					categoryInfo = emc.find( categoryId, CategoryInfo.class );
					if( categoryInfo != null ) {
						categoryInfo.setAppName( appInfo.getAppName() );
						if( StringUtils.isEmpty( categoryInfo.getCategoryAlias() )) {
							categoryInfo.setCategoryAlias( appInfo.getAppName() + "-" + categoryInfo.getCategoryName() );
						}
						emc.beginTransaction( CategoryInfo.class );
						emc.check( categoryInfo, CheckPersistType.all );
						logger.info( "changeAppNameInCategory -> cms processing batch operation: change category: " + categoryInfo.getId() + ", app_name: " + oldName + " -> " + categoryInfo.getAppName() );
						emc.commit();
					}
					//이어서 해당 분류 아래 모든 문서 정보 처리
					docCount = business.getDocumentFactory().countByCategoryId( categoryInfo.getId() );
					if( docCount > 0 ) {
						totalWhileCount = (int) (docCount/queryMaxCount) + 1;
						if( totalWhileCount > 0 ) {
							while( docCount > 0 && currenteWhileCount <= totalWhileCount ) {
								//문서 1000건 조회 후 처리
								document_ids = business.getDocumentFactory().listByCategoryIdAndNotEqualAppName( categoryInfo.getId(), appInfo.getAppName(), queryMaxCount );
								changeDocumentInfoWithCategory( emc, document_ids, categoryInfo );
								logger.info( "changeAppNameInCategory -> cms processing batch operation: update app and category info for document, batch("+ currenteWhileCount +"/" + totalWhileCount + ") " );
								//현재 반복 횟수 +1
								currenteWhileCount ++;
								//게시판 이름이 아직 변경되지 않은 남은 문서 수 다시 조회
								docCount = business.getDocumentFactory().countByCategoryIdAndNotEqualAppName( categoryInfo.getId(), appInfo.getAppName() );
							}
						}
					}
				}
			}
			if( cmsBatchOperation != null ) {
				emc.beginTransaction( CmsBatchOperation.class );
				emc.remove( cmsBatchOperation, CheckRemoveType.all );
				logger.info( "changeAppNameInCategory -> cms delete batch operation: " + cmsBatchOperation.toString()  );
				emc.commit();
			}
		}
		CacheManager.notify( Document.class );
		CacheManager.notify( CategoryInfo.class );
	}

	/**
	 * 분류 아래 모든 문서 삭제 후 마지막으로 현재 일괄 처리 정보 삭제
	 * @param id
	 * @param bundle
	 * @throws Exception
	 */
	private void deleteDocumentInCategory( String id, String bundle ) throws Exception {
		Long docCount = 0L;
		Integer totalWhileCount = 0;
		Integer currenteWhileCount = 0;
		Integer queryMaxCount = 1000;
		List<String> document_ids = null;
		CmsBatchOperation cmsBatchOperation = null;
		CategoryInfo categoryInfo = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			cmsBatchOperation = emc.find( id, CmsBatchOperation.class );
			categoryInfo = emc.find( bundle, CategoryInfo.class );

			if( categoryInfo != null ) {
				emc.beginTransaction( CategoryInfo.class );
				emc.remove( categoryInfo, CheckRemoveType.all );
				logger.info( "deleteDocumentInCategory -> cms processing batch operation: remove category, id:"+ bundle );
				emc.commit();
			}

			//해당 디렉터리 아래 모든 문서 삭제
			docCount = business.getDocumentFactory().countByCategoryId( bundle );
			if( docCount > 0 ) {
				totalWhileCount = (int) (docCount/queryMaxCount) + 1;
				if( totalWhileCount > 0 ) {
					while( docCount > 0 && currenteWhileCount <= totalWhileCount ) {//문서 1000건 조회 후 처리
						document_ids = business.getDocumentFactory().listByCategoryId( bundle, queryMaxCount );
						for( String docId : document_ids ){
							try {
								documentInfoService.delete( emc, docId );
								new CmsBatchOperationPersistService().addOperation(
										CmsBatchOperationProcessService.OPT_OBJ_DOCUMENT,
										CmsBatchOperationProcessService.OPT_TYPE_DELETE, id, id, "분류 삭제에 따른 문서 삭제: ID=" + id );
								logger.info( "deleteDocumentInCategory -> cms processing batch operation: remove document("+ currenteWhileCount +"/" + totalWhileCount + "): " + docId );
							}catch( Exception e ) {
								e.printStackTrace();
							}
						}
						//현재 반복 횟수 +1
						currenteWhileCount ++;
						//분류 이름이 아직 변경되지 않은 남은 문서 수 다시 조회
						docCount = business.getDocumentFactory().countByCategoryId( categoryInfo.getId() );
					}
				}
			}
			if( cmsBatchOperation != null ) {
				emc.beginTransaction( CmsBatchOperation.class );
				emc.remove( cmsBatchOperation, CheckRemoveType.all );
				logger.info( "deleteDocumentInCategory -> cms delete batch operation: " + cmsBatchOperation.toString()  );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
		CacheManager.notify( CategoryInfo.class );
		CacheManager.notify( Document.class );
	}

	/**
	 * categoryName이 기존 분류 이름인 모든 문서의 분류 및 게시판 관련 정보 갱신 후 마지막으로 현재 일괄 처리 정보 삭제
	 * @param id 일괄 처리 정보 ID
	 * @param bundle  연결된 분류 정보 ID
	 * @param oldName 분류의 기존 이름
	 * @throws Exception
	 */
	private void changeCategoryNameInDocument( String id, String bundle, String oldName ) throws Exception {
		Long docCount = 0L;
		Integer totalWhileCount = 0;
		Integer currenteWhileCount = 0;
		Integer queryMaxCount = 1000;
		List<String> document_ids = null;
		CmsBatchOperation cmsBatchOperation = null;
		CategoryInfo categoryInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			cmsBatchOperation = emc.find( id, CmsBatchOperation.class );
			categoryInfo = emc.find( bundle, CategoryInfo.class );
			if( categoryInfo != null ) {
				//해당 디렉터리 아래 모든 문서의 게시판 이름과 분류 별칭 조정
				docCount = business.getDocumentFactory().countByCategoryId( bundle );
				if( docCount > 0 ) {
					totalWhileCount = (int) (docCount/queryMaxCount) + 1;
					if( totalWhileCount > 0 ) {
						while( docCount > 0 && currenteWhileCount <= totalWhileCount ) {
							//문서 1000건 조회 후 처리
							document_ids = business.getDocumentFactory().listByCategoryIdAndCategoryName(bundle, categoryInfo.getCategoryName(), queryMaxCount );
							changeDocumentInfoWithCategory( emc, document_ids, categoryInfo );
							logger.info( "changeCategoryNameInDocument -> cms processing batch operation: update app and category info for document, batch("+ currenteWhileCount +"/" + totalWhileCount + ") " );
							//현재 반복 횟수 +1
							currenteWhileCount ++;
							//남은 문서 수 다시 조회
							docCount = business.getDocumentFactory().countByCategoryIdAndNotEqualsCategoryName( categoryInfo.getId(), categoryInfo.getCategoryName() );
						}
					}
				}
			}
			if( cmsBatchOperation != null ) {
				emc.beginTransaction( CmsBatchOperation.class );
				emc.remove( cmsBatchOperation, CheckRemoveType.all );
				logger.info( "changeCategoryNameInDocument -> cms delete batch operation: " + cmsBatchOperation.toString()  );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
		CacheManager.notify( Document.class );
		CacheManager.notify( CategoryInfo.class );
	}

	/**
	 * 지정 문서의 게시판 및 분류 정보 내용 갱신
	 * @param emc
	 * @param document_ids
	 * @param categoryInfo
	 * @throws Exception
	 */
	private void changeDocumentInfoWithCategory( EntityManagerContainer emc, List<String> document_ids, CategoryInfo categoryInfo ) throws Exception {
		if( ListTools.isNotEmpty( document_ids ) ){
			emc.beginTransaction( Document.class );
			Document document = null;
			for( String docId : document_ids ){
				try {
					document = emc.find( docId, Document.class );
					document.setAppId( categoryInfo.getAppId() );
					document.setAppName( categoryInfo.getAppName() );
					document.setCategoryAlias( categoryInfo.getCategoryAlias() );
					document.setCategoryName( categoryInfo.getCategoryName() );
					if( document.getHasIndexPic() == null ){
						document.setHasIndexPic( false );
					}
					emc.check( document, CheckPersistType.all );
					logger.info( "changeDocumentInfoWithCategory -> cms processing batch operation: change app and category info for document: " + docId );
				}catch( Exception e ) {
					e.printStackTrace();
				}
			}
			emc.commit();
		}
	}
}
