package com.x.cms.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.CmsBatchOperation;
import com.x.cms.core.entity.Document;

/**
 * 일괄 처리 작업 정보 영속화 서비스 클래스, Service로 트랜잭션 제어
 *
 */
public class CmsBatchOperationPersistService {

	CmsBatchOperationService cmsBatchOperationService = new CmsBatchOperationService();
	/**
	 * 일괄 처리 정보 객체 저장
	 * @param cmsBatchOperation
	 * @return
	 * @throws Exception
	 */
	public CmsBatchOperation addOperation( CmsBatchOperation cmsBatchOperation ) throws Exception {
		if( cmsBatchOperation == null ) {
			throw new Exception("cmsBatchOperation can not null for save!");
		}
		if( StringUtils.isEmpty(cmsBatchOperation.getOptType()  )) {
			throw new Exception("optType can not empty for save CmsBatchOperation!");
		}
		if( StringUtils.isEmpty( cmsBatchOperation.getObjType()  )) {
			throw new Exception("objType can not empty for save CmsBatchOperation!");
		}
		if( StringUtils.isEmpty( cmsBatchOperation.getBundle()  )) {
			throw new Exception("bundle can not empty for save CmsBatchOperation!");
		}
		if( StringUtils.isEmpty( cmsBatchOperation.getDescription()  )) {
			throw new Exception("description can not empty for save CmsBatchOperation!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			emc.beginTransaction( CmsBatchOperation.class );
			emc.persist( cmsBatchOperation, CheckPersistType.all );
			emc.commit();
		}
		//일괄 작업 정보를 큐에 추가
		ThisApplication.queueBatchOperation.send( cmsBatchOperation );
		return cmsBatchOperation;
	}

	/**
	 * 일괄 처리 정보 객체 저장
	 *
	 * @param objType 객체 유형
	 * @param optType 작업 유형
	 * @param bundle 연결 ID
	 * @param oldInfo 기존 정보
	 * @param description 작업 유형
	 * @return
	 * @throws Exception
	 */
	public CmsBatchOperation addOperation( String objType, String optType, String bundle, String oldInfo, String description ) throws Exception {
		CmsBatchOperation cmsBatchOperation = new CmsBatchOperation();
		cmsBatchOperation.setOptType(optType);
		cmsBatchOperation.setObjType(objType);
		cmsBatchOperation.setBundle(bundle);
		cmsBatchOperation.setDescription(description);
		cmsBatchOperation.setErrorCount( 0 );
		cmsBatchOperation.setIsRunning( false );
		cmsBatchOperation.setOldInfo( oldInfo );
		return addOperation( cmsBatchOperation ) ;
	}

	public void addDocumentOperation( String objType, String optType, String bundle, String oldInfo, String description ) throws Exception {
		CmsBatchOperation cmsBatchOperation = new CmsBatchOperation();
		cmsBatchOperation.setOptType(optType);
		cmsBatchOperation.setObjType(objType);
		cmsBatchOperation.setBundle(bundle);
		cmsBatchOperation.setDescription(description);
		cmsBatchOperation.setErrorCount( 0 );
		cmsBatchOperation.setIsRunning( false );
		cmsBatchOperation.setOldInfo( oldInfo );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			emc.beginTransaction( CmsBatchOperation.class );
			emc.persist( cmsBatchOperation, CheckPersistType.all );
			emc.commit();
		}
		//일괄 작업 정보를 큐에 추가
		ThisApplication.queueDocumentOperation.send( cmsBatchOperation );
	}

	/**
	 * 일괄 처리 정보 객체 삭제
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		if( id == null ) {
			throw new Exception("id can not empty for delete!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			CmsBatchOperation cmsBatchOperation = emc.find(id, CmsBatchOperation.class );
			if( cmsBatchOperation == null ) {
				throw new Exception("Entity for CmsBatchOperation not exists with id:" + id );
			}
			emc.beginTransaction( CmsBatchOperation.class );
			emc.remove( cmsBatchOperation, CheckRemoveType.all );
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void addErrorTime(CmsBatchOperation operation, int i ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			CmsBatchOperation cmsBatchOperation = emc.find( operation.getId(), CmsBatchOperation.class );
			if( cmsBatchOperation == null ) {
				throw new Exception("Entity for CmsBatchOperation not exists with id:" + operation.getId() );
			}
			cmsBatchOperation.addErrorCount(1);
			emc.beginTransaction( CmsBatchOperation.class );
			emc.check( cmsBatchOperation, CheckPersistType.all );
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void initOperationRunning() throws Exception {
		List<CmsBatchOperation> operations = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			operations = cmsBatchOperationService.list( emc, 1000 );
			if( ListTools.isNotEmpty( operations )) {
				emc.beginTransaction( CmsBatchOperation.class );
				for( CmsBatchOperation operation : operations ) {
					operation.setIsRunning( false );
					emc.check( operation, CheckPersistType.all );
				}
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * review되지 않은 Document 존재 여부 검사
	 * 예: 갱신으로 문서 Review가 생성되지 않아 문서가 보이지 않는 경우
	 */
	public void checkDocumentReviewStatus() {
		DocumentInfoService documentInfoService = new DocumentInfoService();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<String> ids = documentInfoService.listUnReviewIds(emc, 5000);
			if( ListTools.isNotEmpty( ids )) {
				CmsBatchOperationPersistService cmsBatchOperationPersistService = new CmsBatchOperationPersistService();
				for( String docId : ids ) {
					cmsBatchOperationPersistService.addOperation(
							CmsBatchOperationProcessService.OPT_OBJ_DOCUMENT,
							CmsBatchOperationProcessService.OPT_TYPE_PERMISSION,  docId,  docId, "문서 권한 갱신: ID=" +  docId );
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 문서가 IsTop으로 설정되었는지 검사, isTop이 비어 있으면 false로 설정해 null 방지
	 * @param defaultIsTop 설정할 기본값
	 */
    public void checkDocumentIsTop( boolean defaultIsTop ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			DocumentQueryService documentQueryService = new DocumentQueryService();
			DocumentPersistService documentPersistService = new DocumentPersistService();
			List<String> ids = documentQueryService.listNULLIsTopDocIds();
			Document document = null;
			if( ListTools.isNotEmpty( ids )) {
				for( String id : ids ) {
					document = documentQueryService.get( id );
					if( document != null ){
						documentPersistService.refreshDocInfoData( document );
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
    }
}
