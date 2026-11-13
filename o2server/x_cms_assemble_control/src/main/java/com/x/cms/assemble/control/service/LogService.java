package com.x.cms.assemble.control.service;

import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Log;
import com.x.cms.core.express.tools.DateOperation;


public class LogService {
	
	/**
	 * 로그 정보 저장
	 * @param person  처리자 계정
 	 * @param description   로그 설명
	 * @param appId   앱 ID
	 * @param categoryId  분류 ID
	 * @param documentId  문서 ID
	 * @param fileId      첨부파일 ID
	 * @param operationLevel  작업 수준: 앱|파일|분류|문서
	 * @param operationType  작업 유형: 추가|갱신|삭제
	 * @return
	 * @throws Exception 
	 */
	public boolean log( EntityManagerContainer emc, String person, String description, String appId, String categoryId, String documentId, String fileId, String operationLevel, String operationType ) throws Exception {
		if( emc == null ){
			emc = EntityManagerContainerFactory.instance().create();
		}
		String operatorUid = person;
		Log log = new Log();
		log.setDescription(description);
		log.setAppId(appId);
		log.setCategoryId(categoryId);
		log.setDocumentId(documentId);
		log.setFileId(fileId);
		log.setOperatorName(operatorUid);
		log.setOperatorUid(operatorUid);
		log.setOperationType(operationType);
		log.setOperationLevel(operationLevel);
		try {
			emc.beginTransaction(Log.class);
			emc.persist( log, CheckPersistType.all );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
		return true;
	}
	
	/**
	 * 작업 로그 정보 정리
	 * @param stay_yearnum_operationLog 로그 보존 연수
	 * @param stay_count_operationLog 로그 보존 항목 수
	 * @throws Exception 
	 */
	public void clean( Integer stay_yearnum_operationLog, Integer stay_count_operationLog ) throws Exception {
		//먼저 시간 기준으로 정리
		cleanWithStayYearNumber( stay_yearnum_operationLog );
		cleanWithMaxCount( stay_count_operationLog );
	}

	/**
	 * 최대 보존 항목 수 기준으로 정리
	 * @param stay_count_operationLog
	 * @throws Exception 
	 */
	private void cleanWithMaxCount(Integer stay_count_operationLog) throws Exception {
		Business business = null;
		List<String> ids = null;
		String last_id = null;
		Log log = null;
		Long total = 0L;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			total = business.getLogFactory().getTotal();
			if( total > stay_count_operationLog ) {
				//기록 항목 수를 stay_count_operationLog의 60%로 축소
				ids = business.getLogFactory().getRecordIdsWithCount( (int)( stay_count_operationLog * 0.6 ));
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
	 * @param stay_yearnum_operationLog
	 * @throws Exception 
	 */
	private void cleanWithStayYearNumber( Integer stay_yearnum_operationLog ) throws Exception {
		if( stay_yearnum_operationLog == null ) {
			throw new Exception("stay_yearnum_operationLog is null!");
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
		Log log = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			ids = business.getLogFactory().listOverTime( overTime );
			if( ids != null && !ids.isEmpty() ) {
				emc.beginTransaction( Log.class );
				for( String id : ids ) {
					log = emc.find( id, Log.class );
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

}
