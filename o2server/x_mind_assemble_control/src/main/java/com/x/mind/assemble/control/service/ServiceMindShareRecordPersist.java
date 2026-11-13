package com.x.mind.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindBaseInfo;
import com.x.mind.entity.MindShareRecord;

/**
 * 마인드맵 정보 영속화 작업 서비스 클래스: 저장, 수정, 삭제
 * @author O2LEE
 *
 */
class ServiceMindShareRecordPersist{
	
	/**
	 * 파일 공유 정보 저장
	 * @param emc 
	 * @param mindBaseInfo 
	 * @param mindShareRecords
	 * @throws Exception 
	 */
	public void share(EntityManagerContainer emc, MindBaseInfo mindBaseInfo, List<MindShareRecord> mindShareRecords) throws Exception {
		if( ListTools.isNotEmpty( mindShareRecords )) {
			Business business = new Business(emc);
			Boolean exists = false;

			//먼저 공유 정보가 이미 있는지 확인. 기준은 fileId, target, targetType
			for( MindShareRecord ｍindShareRecord :mindShareRecords ) {
				exists = false;
				
				//현재 공유 정보가 이미 있는지 판단
				exists = business.mindShareRecordFactory().exists( ｍindShareRecord );
				
				mindBaseInfo = emc.find( mindBaseInfo.getId(), MindBaseInfo.class );
				if( !exists ) {
					emc.beginTransaction( MindBaseInfo.class );
					emc.beginTransaction( MindShareRecord.class );
					//조회 권한 추가 및 공유 정보 추가
					if( mindBaseInfo != null ) {
						mindBaseInfo.setShared( true );
						//PERSON | UNIT | GROUP 
						if( "PERSON".equalsIgnoreCase( ｍindShareRecord.getTargetType() )) {
							if( mindBaseInfo.getSharePersonList() == null ) {
								mindBaseInfo.setSharePersonList( new ArrayList<>());
							}
							if( !mindBaseInfo.getSharePersonList().contains( ｍindShareRecord.getTarget() )) {
								mindBaseInfo.getSharePersonList().add( ｍindShareRecord.getTarget() );
							}
						}else if( "UNIT".equalsIgnoreCase( ｍindShareRecord.getTargetType() )) {
							if( mindBaseInfo.getShareUnitList() == null ) {
								mindBaseInfo.setShareUnitList( new ArrayList<>());
							}
							if( !mindBaseInfo.getShareUnitList().contains( ｍindShareRecord.getTarget() )) {
								mindBaseInfo.getShareUnitList().add( ｍindShareRecord.getTarget() );
							}
						}else if( "GROUP".equalsIgnoreCase( ｍindShareRecord.getTargetType() )) {
							if( mindBaseInfo.getShareGroupList() == null ) {
								mindBaseInfo.setShareGroupList( new ArrayList<>());
							}
							if( !mindBaseInfo.getShareGroupList().contains( ｍindShareRecord.getTarget() )) {
								mindBaseInfo.getShareGroupList().add( ｍindShareRecord.getTarget() );
							}
						}
						
						//공유 정보 추가
						emc.persist( ｍindShareRecord, CheckPersistType.all );
						//파일의 공유 표시 수정
						emc.check( mindBaseInfo, CheckPersistType.all );						
					}
				}
				emc.commit();
			}
		}
	}

	/**
	 * 마인드맵 공유 정보 ID로 마인드맵 공유 정보 삭제
	 * 
	 * @param shareRecordId
	 * @throws Exception 
	 */
	public Boolean shareCancel(EntityManagerContainer emc, String shareRecordId ) throws Exception {
		if( StringUtils.isEmpty( shareRecordId ) ) {
			throw new Exception("shareRecordId is empty!");
		}
		
		Business business = new Business(emc);
		MindShareRecord mindShareRecord = emc.find( shareRecordId, MindShareRecord.class);
		if( mindShareRecord == null ) {
			throw new Exception("mindShareRecord{ \"id\": \""+shareRecordId+"\" } is not exists!")  ;
		}
		//먼저 MindShareRecord 하나 삭제
		emc.beginTransaction( MindShareRecord.class );
		emc.remove( mindShareRecord, CheckRemoveType.all );
		emc.commit();
		
		//마인드맵 관련 정보 수정
		MindBaseInfo mindBaseInfo = emc.find( mindShareRecord.getFileId(), MindBaseInfo.class);
		emc.beginTransaction( MindBaseInfo.class );
		//PERSON | UNIT | GROUP 
		if( "PERSON".equalsIgnoreCase( mindShareRecord.getTargetType() )) {
			if( mindBaseInfo.getSharePersonList() == null ) {
				mindBaseInfo.setSharePersonList( new ArrayList<>());
			}
			if( mindBaseInfo.getSharePersonList().contains( mindShareRecord.getTarget() )) {
				mindBaseInfo.getSharePersonList().remove( mindShareRecord.getTarget() );
			}
		}else if( "UNIT".equalsIgnoreCase( mindShareRecord.getTargetType() )) {
			if( mindBaseInfo.getShareUnitList() == null ) {
				mindBaseInfo.setShareUnitList( new ArrayList<>());
			}
			if( mindBaseInfo.getShareUnitList().contains( mindShareRecord.getTarget() )) {
				mindBaseInfo.getShareUnitList().remove( mindShareRecord.getTarget() );
			}
		}else if( "GROUP".equalsIgnoreCase( mindShareRecord.getTargetType() )) {
			if( mindBaseInfo.getShareGroupList() == null ) {
				mindBaseInfo.setShareGroupList( new ArrayList<>());
			}
			if( mindBaseInfo.getShareGroupList().contains( mindShareRecord.getTarget() )) {
				mindBaseInfo.getShareGroupList().remove( mindShareRecord.getTarget() );
			}
		}
		
		//이 파일에 공유 기록이 남아 있는지 확인해 없으면 공유 표시를 false로 변경
		List<String> shareRecordIds= business.mindShareRecordFactory().listIdsWithMindId( mindShareRecord.getFileId() );
		if( ListTools.isEmpty( shareRecordIds )) {
			mindBaseInfo.setShared( false );
		}
		emc.check( mindBaseInfo, CheckPersistType.all );
		emc.commit();
		return true;
	}	
}
