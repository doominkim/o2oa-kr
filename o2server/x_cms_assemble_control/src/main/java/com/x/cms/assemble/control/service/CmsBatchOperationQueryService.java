package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.cms.core.entity.CmsBatchOperation;

/**
 * 배치 처리 작업 정보를 관리하는 서비스 클래스, Service로 트랜잭션 제어
 * 
 */
public class CmsBatchOperationQueryService {

	private CmsBatchOperationService cmsBatchOperationService = new CmsBatchOperationService();
	
	public List<CmsBatchOperation> list( List<String> ids ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return cmsBatchOperationService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<CmsBatchOperation> list( Integer maxCount ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return cmsBatchOperationService.list( emc, maxCount );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<CmsBatchOperation> list( Integer maxCount, Integer minutesAgo) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return cmsBatchOperationService.list( emc, maxCount, minutesAgo);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<CmsBatchOperation> listNotRun( Integer maxCount ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return cmsBatchOperationService.listNotRun( emc, maxCount );
		} catch ( Exception e ) {
			throw e;
		}
	}
}
