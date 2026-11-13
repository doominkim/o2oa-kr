package com.x.mind.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindShareRecord;

/**
 * 마인드맵 공유 정보 조회 작업 서비스 클래스: 조회
 * @author O2LEE
 *
 */
class ServiceMindShareRecordQuery{

	/**
	 * 지정 ID 목록으로 마인드맵 기본 정보 목록 조회
	 * @param emc
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	List<MindShareRecord> listWithIds(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business = new Business( emc );
		return business.mindShareRecordFactory().list(ids);
	}
	/**
	 * 마인드맵 ID로 마인드맵 공유 기록 Id 목록 조회
	 * @param mindId
	 * @throws Exception 
	 */
	public List<String> listIdsWithMindId(EntityManagerContainer emc, String mindId) throws Exception {
		Business business = new Business( emc );
		return business.mindShareRecordFactory().listIdsWithMindId(mindId);
	}
	
	public List<String> listSharedRecordIds(EntityManagerContainer emc, String source, List<String> targetList, List<String> inMindIds ) throws Exception {
		Business business = new Business( emc );
		return business.mindShareRecordFactory().listSharedRecordIds(source, targetList, inMindIds);
	}
	public List<MindShareRecord> listSharedRecords(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business = new Business( emc );
		return business.mindShareRecordFactory().list(ids);
	}
	public List<String> listSharedMindIdsFromRecord(EntityManagerContainer emc, String source, List<String> targetList,
			List<String> inMindIds) throws Exception {
		Business business = new Business( emc );
		return business.mindShareRecordFactory().listSharedMindIdsFromRecord(source, targetList, inMindIds);
	}

}
