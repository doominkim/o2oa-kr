package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentViewRecord;
import com.x.cms.core.entity.Log;
import com.x.cms.core.express.tools.DateOperation;


/**
 * 문서 접근 기록 정보 관리 서비스 클래스(고급)
 * 고급 서비스는 Service로 트랜잭션 제어 가능
 *
 * @author O2LEE
 */
public class DocumentViewRecordServiceAdv {

	private UserManagerService userManagerService = new UserManagerService();
	private DocumentViewRecordService documentViewRecordService = new DocumentViewRecordService();

	public List<DocumentViewRecord> list( List<String> ids ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentViewRecordService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listByDocument( String docId ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentViewRecordService.listByDocument( emc, docId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listByPerson( String personName, Integer maxCount ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			List<String> ids =  documentViewRecordService.listByPerson( emc, personName, maxCount );
			if( ids == null ) {
				ids = new ArrayList<>();
			}
			return ids;
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listDocIdsByPerson( String personName, Integer maxCount ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			List<String> ids =  documentViewRecordService.listDocIdsByPerson( emc, personName, maxCount );
			if( ids == null ) {
				ids = new ArrayList<>();
			}
			return ids;
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void deleteByDocument( String docId ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			documentViewRecordService.deleteByDocument( emc, docId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * TODO: 접근 로그 기록, 사용자당 문서 하나에 한 건만 보존하고 접근 횟수와 마지막 접근 시간 갱신
	 *
	 * @param docId
	 * @param personName
	 * @throws Exception
	 */
	public Long addViewRecord( String docId, String personName ) throws Exception {
		DocumentViewRecord documentViewRecord = null;
		Document document = null;
		Business business = null;
		Long viewCount = 0L;
		List<String> ids = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			emc.beginTransaction( DocumentViewRecord.class );

			ids = business.documentViewRecordFactory().listByDocAndPerson( docId, personName );
			document = emc.find( docId, Document.class );

			if( document == null ) {
				throw new Exception("document not exists!ID:" + docId );
			}

			if( ListTools.isNotEmpty( ids )){
				int i = 0;
				for( String id : ids ){
					i++;
					documentViewRecord = emc.find( id, DocumentViewRecord.class );
					if( i == 1 ){
						documentViewRecord.setLastViewTime( new Date() );
						if( documentViewRecord.getViewCount() == null || documentViewRecord.getViewCount() == 0 ){
							documentViewRecord.setViewCount( 1 );
						}
						documentViewRecord.setViewCount( documentViewRecord.getViewCount() + 1 );
						emc.check( documentViewRecord, CheckPersistType.all );
					}else{
						//불필요한 로그 데이터 삭제
						emc.remove( documentViewRecord, CheckRemoveType.all );
					}
				}
			}else{
				if( document != null ){
					documentViewRecord = new DocumentViewRecord();
					documentViewRecord.setAppId( document.getAppId() );
					documentViewRecord.setCategoryId( document.getCategoryId() );
					documentViewRecord.setDocumentId( document.getId() );
					documentViewRecord.setViewerName( personName );
					documentViewRecord.setAppName( document.getAppName() );
					documentViewRecord.setCategoryName( document.getCategoryName() );
					documentViewRecord.setTitle( document.getTitle() );
					documentViewRecord.setLastViewTime( new Date() );
					documentViewRecord.setViewCount( 1 );
					documentViewRecord.setViewerTopUnitName( userManagerService.getTopUnitNameWithPerson( personName ));
					documentViewRecord.setViewerUnitName( userManagerService.getUnitNameWithPerson( personName ));
					emc.persist( documentViewRecord, CheckPersistType.all );
				}
			}
			emc.commit();

			emc.beginTransaction( Document.class );
			viewCount = business.documentViewRecordFactory().sumWithDocmentId( docId );
			document.setViewCount( viewCount );
			emc.check( document, CheckPersistType.all );
			emc.commit();

		} catch ( Exception e ) {
			throw e;
		}
		return viewCount;
	}

	public Long addViewRecord( String docId, String personName, Date viewTime) throws Exception {
		DocumentViewRecord documentViewRecord = null;
		Document document = null;
		Business business = null;
		Long viewCount = 0L;
		List<String> ids = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			emc.beginTransaction( DocumentViewRecord.class );

			ids = business.documentViewRecordFactory().listByDocAndPerson( docId, personName );
			document = emc.find( docId, Document.class );

			if( document == null ) {
				throw new Exception("document not exists!ID:" + docId );
			}

			if( ListTools.isNotEmpty( ids )){
				int i = 0;
				for( String id : ids ){
					i++;
					documentViewRecord = emc.find( id, DocumentViewRecord.class );
					if( i == 1 ){
						documentViewRecord.setLastViewTime(viewTime);
						if( documentViewRecord.getViewCount() == null || documentViewRecord.getViewCount() == 0 ){
							documentViewRecord.setViewCount( 1 );
						}
						documentViewRecord.setViewCount( documentViewRecord.getViewCount() + 1 );
						emc.check( documentViewRecord, CheckPersistType.all );
					}else{
						//불필요한 로그 데이터 삭제
						emc.remove( documentViewRecord, CheckRemoveType.all );
					}
				}
			}else{
				if( document != null ){
					documentViewRecord = new DocumentViewRecord();
					documentViewRecord.setCreateTime(viewTime);
					documentViewRecord.setAppId( document.getAppId() );
					documentViewRecord.setCategoryId( document.getCategoryId() );
					documentViewRecord.setDocumentId( document.getId() );
					documentViewRecord.setViewerName( personName );
					documentViewRecord.setAppName( document.getAppName() );
					documentViewRecord.setCategoryName( document.getCategoryName() );
					documentViewRecord.setTitle( document.getTitle() );
					documentViewRecord.setLastViewTime(viewTime);
					documentViewRecord.setViewCount( 1 );
					documentViewRecord.setViewerTopUnitName( userManagerService.getTopUnitNameWithPerson( personName ));
					documentViewRecord.setViewerUnitName( userManagerService.getUnitNameWithPerson( personName ));
					emc.persist( documentViewRecord, CheckPersistType.all );
				}
			}
			emc.commit();

			emc.beginTransaction( Document.class );
			viewCount = business.documentViewRecordFactory().sumWithDocmentId( docId );
			document.setViewCount( viewCount );
			emc.check( document, CheckPersistType.all );
			emc.commit();

		} catch ( Exception e ) {
			throw e;
		}
		return viewCount;
	}

	public List<DocumentViewRecord> listNextWithDocIds( String id, String docId, Integer count, String order) throws Exception {
		if( docId == null ){
			throw new Exception("docId is null!");
		}
		if( StringUtils.isEmpty( order ) ){
			order = "DESC";
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentViewRecordService.listNextWithDocIds( emc, id, docId, count, order );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Long countWithDocIds( String docId ) throws Exception {
		if( docId == null ){
			throw new Exception("docId is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentViewRecordService.countWithDocIds( emc, docId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Long countWithDocIdAndPerson( String docId, String person ) throws Exception {
		if( docId == null ){
			throw new Exception("docId is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentViewRecordService.countWithDocIdAndPerson( emc, docId, person);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 문서 접근 로그 정보 정리
	 * @param stay_yeanumr_viewRecord 로그 보존 연수
	 * @param stay_count_viewRecord 로그 보존 항목 수
	 * @throws Exception
	 */
	public void clean( Integer stay_yeanumr_viewRecord, Integer stay_count_viewRecord ) throws Exception {
		//먼저 시간 기준으로 정리
		cleanWithStayYearNumber( stay_yeanumr_viewRecord );
		cleanWithMaxCount( stay_count_viewRecord );
	}

	/**
	 * 최대 보존 항목 수 기준으로 정리
	 * @param stay_count_viewRecord
	 * @throws Exception
	 */
	private void cleanWithMaxCount(Integer stay_count_viewRecord ) throws Exception {
		Business business = null;
		List<String> ids = null;
		String last_id = null;
		Log log = null;
		Long total = 0L;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			total = business.getLogFactory().getTotal();
			if( total > stay_count_viewRecord ) {
				//기록 항목 수를 stay_count_operationLog의 60%로 축소
				ids = business.getLogFactory().getRecordIdsWithCount( Integer.parseInt(( stay_count_viewRecord * 0.6 )+""));
			}
			if( ids != null && !ids.isEmpty() ) {
				//마지막 항목을 가져와 보존 가능한 가장 이른 생성 시간 결정
				last_id = ids.get( (ids.size() -1) );
				log = emc.find( last_id, Log.class );
				//시간 기준으로 정리, 이 시간 이전 데이터는 보존 불가
				cleanWithDate( log.getCreateTime() );
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 보존 연수 기준으로 로그 정리
	 * @param stay_yeanumr_viewRecord
	 * @throws Exception
	 */
	private void cleanWithStayYearNumber( Integer stay_yeanumr_viewRecord ) throws Exception {
		if( stay_yeanumr_viewRecord == null ) {
			throw new Exception("stay_yeanumr_viewRecord is null!");
		}
		//1. 3년 기한 시점 계산, 3년 전 1월 1일, 1년을 더 더하는 것이 좋음
		Integer year = DateOperation.getYearNumber( new Date() );
		Date limitDate = DateOperation.getDateFromString( ( year-4 ) + "-01-01 00:00:00");
		cleanWithDate( limitDate );
	}


	/**
	 * 보존 연수 기준으로 로그 정리
	 * @param overTime
	 * @throws Exception
	 */
	private void cleanWithDate( Date overTime ) throws Exception {
		if( overTime == null ) {
			throw new Exception("overTime is null!");
		}
		Business business = null;
		List<String> ids = null;
		DocumentViewRecord log = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			ids = business.documentViewRecordFactory().listOverTime( overTime );
			if( ids != null && !ids.isEmpty() ) {
				emc.beginTransaction( DocumentViewRecord.class );
				for( String id : ids ) {
					log = emc.find( id, DocumentViewRecord.class );
					if( log != null ) {
						emc.remove( log, CheckRemoveType.all );
					}
				}
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 지정한 ID 목록으로 읽은 문서 ID 목록 조회
	 * @param ids
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public List<String> listReadDocId( List<String> ids, EffectivePerson effectivePerson ) throws Exception {
		if( ListTools.isEmpty(  ids ) ) {
			return new ArrayList<>();
		}
		if( effectivePerson == null ) {
			return new ArrayList<>();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business( emc );
			return business.documentViewRecordFactory().listReadDocId( ids, effectivePerson.getDistinguishedName() );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listDocIdsWithFilter(List<String> queryCategoryIds, EffectivePerson effectivePerson ) throws Exception {
		if( ListTools.isEmpty(  queryCategoryIds ) ) {
			return new ArrayList<>();
		}
		if( effectivePerson == null ) {
			return new ArrayList<>();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business( emc );
			return business.documentViewRecordFactory().listReadDocIdWithCategory( queryCategoryIds, effectivePerson.getDistinguishedName() );
		} catch ( Exception e ) {
			throw e;
		}
	}
}
