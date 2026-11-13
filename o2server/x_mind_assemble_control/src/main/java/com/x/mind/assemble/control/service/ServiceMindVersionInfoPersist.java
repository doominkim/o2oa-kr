package com.x.mind.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindBaseInfo;
import com.x.mind.entity.MindContentInfo;
import com.x.mind.entity.MindRecycleInfo;
import com.x.mind.entity.MindShareRecord;
import com.x.mind.entity.MindVersionContent;
import com.x.mind.entity.MindVersionInfo;

/**
 * 마인드맵 정보 영속화 작업 서비스 클래스: 저장, 수정, 삭제
 * @author O2LEE
 *
 */
class ServiceMindVersionInfoPersist{
	
	/**
	 * 새 버전의 마인드맵 정보 저장
	 * @param emc
	 * @param _mindBaseInfo
	 * @param content
	 * @return
	 * @throws Exception
	 */
	MindVersionInfo createFromMind(EntityManagerContainer emc,  MindBaseInfo _mindBaseInfo, String content, int maxVersionCount ) throws Exception {
		if( StringUtils.isEmpty(_mindBaseInfo.getId()) ){
			throw new Exception("마인드맵 ID가 비어 있어 새 버전 정보를 저장할 수 없습니다!");
		}
		MindVersionInfo mindVersionInfo = null;
		MindVersionContent mindVersionContent = null;
		Business business = new Business(emc);
		
		//먼저 새 이전 버전 정보 생성
		mindVersionInfo = new MindVersionInfo();
		mindVersionInfo.setId(MindVersionInfo.createId());
		mindVersionInfo.setMindId( _mindBaseInfo.getId() );
		mindVersionInfo.setName( _mindBaseInfo.getName() );
		mindVersionInfo.setCreator( _mindBaseInfo.getCreator() );
		mindVersionInfo.setCreatorUnit( _mindBaseInfo.getCreatorUnit() );
		mindVersionInfo.setDescription( _mindBaseInfo.getDescription() );
		mindVersionInfo.setFileVersion( _mindBaseInfo.getFileVersion() );
		mindVersionInfo.setFolderId( _mindBaseInfo.getFolderId() );
		mindVersionInfo.setShared( _mindBaseInfo.getShared() );
		
		mindVersionContent = new MindVersionContent();
		mindVersionContent.setContent(content);
		mindVersionContent.setId(mindVersionInfo.getId());
		
		//버전 정보 저장
		emc.beginTransaction( MindBaseInfo.class );
		emc.beginTransaction( MindVersionInfo.class );
		emc.beginTransaction( MindVersionContent.class );
		
		emc.persist( mindVersionInfo, CheckPersistType.all);
		emc.persist( mindVersionContent, CheckPersistType.all);
		
		//버전 번호+1
		_mindBaseInfo = emc.find( _mindBaseInfo.getId() , MindBaseInfo.class );
		_mindBaseInfo.setFileVersion( _mindBaseInfo.getFileVersion() + 1 );
		emc.check( _mindBaseInfo, CheckPersistType.all);
		
		//커밋 후 가장 오래된 버전 삭제 필요 여부 판단
		emc.commit();
		
		//현재 마인드맵의 버전 수 조회
		Long  count = business.mindVersionInfoFactory().countMindVersionWithMindId( _mindBaseInfo.getId() );
		if( count > maxVersionCount ) {
			System.out.println(">>>>>>>>>>버전 수가 다음을 초과: "+ maxVersionCount +"개, 가장 오래된 버전 하나를 삭제합니다.");
			deleteEarliestVersion(emc, _mindBaseInfo.getId());
		}
		return mindVersionInfo;
	}

	/**
	 * 마인드맵 ID로 가장 오래된 버전 하나 삭제
	 * @param id
	 * @throws Exception 
	 */
	private void deleteEarliestVersion(EntityManagerContainer emc,  String mindId) throws Exception {
		Business business = new Business(emc);
		MindVersionInfo mindVersionInfo = business.mindVersionInfoFactory().getEarliestVersionInfoId(mindId);
		if( mindVersionInfo != null ) {
			MindVersionContent mindVersionContent = emc.find( mindVersionInfo.getCreator(),  MindVersionContent.class );
			emc.beginTransaction( MindVersionInfo.class );
			emc.beginTransaction( MindVersionContent.class );
			if( mindVersionInfo != null ) {
				emc.remove( mindVersionInfo, CheckRemoveType.all );
			}
			if( mindVersionContent != null ) {
				emc.remove( mindVersionContent, CheckRemoveType.all );
			}
			emc.commit();
		}
	}

	/**
	 * 마인드맵 ID로 마인드맵 정보 삭제
	 * 기본 정보, 상세 내용 및 공유 정보
	 * @param mindId
	 * @throws Exception 
	 */
	public Boolean delete(EntityManagerContainer emc, String mindId) throws Exception {
		Business business = new Business(emc);
		
		//먼저 ID로 마인드맵 삭제
		MindBaseInfo mindBaseInfo = emc.find( mindId, MindBaseInfo.class);
		MindContentInfo mindContentInfo = emc.find( mindId, MindContentInfo.class);
		List<MindShareRecord>mindShareInfos = null;
		MindRecycleInfo mindRecycleInfo = null;
		List<String> ids = null;
		
		ids = business.mindShareRecordFactory().listIdsWithMindId(mindId);
		if(ListTools.isNotEmpty( ids )) {
			mindShareInfos = business.mindShareRecordFactory().list(ids);
		}
		
		mindRecycleInfo = business.mindRecycleInfoFactory().get(mindId);
		
		emc.beginTransaction( MindRecycleInfo.class );
		emc.beginTransaction( MindBaseInfo.class );
		emc.beginTransaction( MindShareRecord.class );
		emc.beginTransaction( MindVersionInfo.class );
		
		//공유 테이블의 마인드맵 정보 삭제
		if(ListTools.isNotEmpty( mindShareInfos )) {
			for( MindShareRecord mindShareInfo : mindShareInfos) {
				emc.remove( mindShareInfo, CheckRemoveType.all );
			}
		}
		//휴지통의 마인드맵 정보 삭제
		if( mindRecycleInfo != null ) {
			emc.remove( mindRecycleInfo, CheckRemoveType.all );
		}
		//마인드맵 기본 정보 테이블 정보 삭제
		if( mindBaseInfo != null ) {
			emc.remove( mindBaseInfo, CheckRemoveType.all );
		}
		//상세 정보 테이블 정보 삭제
		if( mindContentInfo != null ) {
			emc.remove( mindBaseInfo, CheckRemoveType.all );
		}
		
		emc.commit();
		return true;
	}
}
