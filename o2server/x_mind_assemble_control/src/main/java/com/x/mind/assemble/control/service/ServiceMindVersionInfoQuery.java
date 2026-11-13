package com.x.mind.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindVersionContent;
import com.x.mind.entity.MindVersionInfo;

/**
 * 마인드맵 이전 버전 정보 조회 작업 서비스 클래스: 조회
 * @author O2LEE
 *
 */
class ServiceMindVersionInfoQuery{

	/**
	 * 지정 ID 목록으로 마인드맵 기본 정보 목록 조회
	 * @param emc
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	List<MindVersionInfo> listWithIds(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business = new Business( emc );
		return business.mindVersionInfoFactory().listVersionsWithIds(ids);
	}
	
	/**
	 * ID로 지정 마인드맵 이전 버전 정보 객체 조회
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception
	 */
	MindVersionInfo getMindVersionInfo(EntityManagerContainer emc, String id) throws Exception {
		return emc.find( id,   MindVersionInfo.class );
	}
	
	/**
	 * ID로 지정 마인드맵 이전 버전 상세 내용 정보 객체 조회
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception
	 */
	MindVersionContent getMindVersionContentInfo(EntityManagerContainer emc, String id) throws Exception {
		return emc.find( id,   MindVersionContent.class );
	}
	
	/**
	 * ID로 지정 마인드맵 이전 버전의 상세 내용 조회
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception
	 */
	String getMindVersionContent(EntityManagerContainer emc, String id) throws Exception {
		MindVersionContent mindVersionContent = emc.find( id,   MindVersionContent.class );
		if( mindVersionContent != null ) {
			return mindVersionContent.getContent();
		}
		return null;
	}

	/**
	 * 마인드맵 ID로 마인드맵의 이전 버전 수 조회
	 * @param emc
	 * @param mindId
	 * @return
	 * @throws Exception
	 */
	public Long countMindVersionWithMindId(EntityManagerContainer emc, String mindId) throws Exception {
		Business business = new Business( emc );
		return business.mindVersionInfoFactory().countMindVersionWithMindId(mindId);
	}

	
	/**
	 * 마인드맵 ID로 해당 마인드맵의 모든 버전 조회
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public List<String> listVersionsWithMindId(EntityManagerContainer emc, String mindId) throws Exception {
		Business business = new Business( emc );
		return business.mindVersionInfoFactory().listVersionsWithMindId(mindId);
	}

	public MindVersionInfo getLatestVersionWithMind(EntityManagerContainer emc, String mindId ) throws Exception {
		Business business = new Business( emc );
		return business.mindVersionInfoFactory().getLatestVersionWithMind( mindId );
	}
	
	public MindVersionInfo getEarliestVersionInfoId(EntityManagerContainer emc, String mindId ) throws Exception {
		Business business = new Business( emc );
		return business.mindVersionInfoFactory().getEarliestVersionInfoId( mindId );
	}

	public MindVersionInfo deleteEarliestVersionInfoId(EntityManagerContainer emc, String mindId) {
		// TODO Auto-generated method stub
		return null;
	}
}
