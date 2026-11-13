package com.x.mind.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindBaseInfo;
import com.x.mind.entity.MindContentInfo;
import com.x.mind.entity.MindFolderInfo;
import com.x.mind.entity.MindRecycleInfo;
import com.x.mind.entity.MindShareRecord;
import com.x.mind.entity.MindVersionInfo;

/**
 * 마인드맵 정보 영속화 작업 서비스 클래스: 저장, 수정, 삭제
 * @author O2LEE
 *
 */
class ServiceMindInfoPersist{
	
	/**
	 * 마인드맵 정보 저장
	 * @param emc
	 * @param _mindBaseInfo
	 * @param content
	 * @return
	 * @throws Exception
	 */
	MindBaseInfo save(EntityManagerContainer emc, MindBaseInfo _mindBaseInfo, String content) throws Exception {
		if( StringUtils.isEmpty(_mindBaseInfo.getId()) ){
			_mindBaseInfo.setId( MindBaseInfo.createId() );
		}
		if( StringUtils.isEmpty(_mindBaseInfo.getFolderId()) ) {
			_mindBaseInfo.setFolderId( "root" );
		}
		MindBaseInfo oldMindBase = null;
		MindContentInfo oldContent = null;
		oldMindBase = emc.find( _mindBaseInfo.getId(), MindBaseInfo.class );
		oldContent = emc.find( _mindBaseInfo.getId(), MindContentInfo.class );
		emc.beginTransaction( MindBaseInfo.class );
		emc.beginTransaction( MindContentInfo.class );
		//마인드맵 기본 정보 저장
		if( oldMindBase == null ){
			oldMindBase = _mindBaseInfo;
			oldMindBase.composeSequnces();
			emc.persist( oldMindBase, CheckPersistType.all );
		}else{
			_mindBaseInfo.copyTo( oldMindBase, JpaObject.FieldsUnmodify  );
			_mindBaseInfo.setId( oldMindBase.getId() );
			_mindBaseInfo.setCreator_sequence( oldMindBase.getCreator_sequence() );
			_mindBaseInfo.setFolder_sequence( oldMindBase.getFolder_sequence() );
			_mindBaseInfo.setCreatorUnit_sequence( oldMindBase.getCreatorUnit_sequence() );
			_mindBaseInfo.setShared_sequence( oldMindBase.getShared_sequence() );
			_mindBaseInfo.composeSequnces();
			emc.check( _mindBaseInfo, CheckPersistType.all );	
		}
		//마인드맵 내용 저장
		if( oldContent == null ){
			oldContent =  new MindContentInfo();
			oldContent.setContent(content);
			oldContent.setId(oldMindBase.getId());
			emc.persist( oldContent, CheckPersistType.all);
		}else{
			oldContent.setContent(content);
			emc.check( oldContent, CheckPersistType.all );	
		}
		
		emc.commit();
		return oldMindBase;
	}

	/**
	 * 마인드맵 ID로 마인드맵 정보 삭제
	 * 기본 정보, 상세 내용 및 공유 정보
	 * @param mindId
	 * @throws Exception 
	 */
	public Boolean destroyMind(EntityManagerContainer emc, String mindId) throws Exception {
		Business business = new Business(emc);
		
		//먼저 ID로 마인드맵 삭제
		MindBaseInfo mindBaseInfo = emc.find( mindId, MindBaseInfo.class);
		MindContentInfo mindContentInfo = emc.find( mindId, MindContentInfo.class);
		List<MindShareRecord>mindShareRecords = null;
		MindRecycleInfo mindRecycleInfo = null;
		List<String> ids = null;
		
		ids = business.mindShareRecordFactory().listIdsWithMindId(mindId);
		if(ListTools.isNotEmpty( ids )) {
			mindShareRecords = business.mindShareRecordFactory().list(ids);
		}
		
		mindRecycleInfo = business.mindRecycleInfoFactory().get(mindId);
		
		emc.beginTransaction( MindRecycleInfo.class );
		emc.beginTransaction( MindBaseInfo.class );
		emc.beginTransaction( MindShareRecord.class );
		emc.beginTransaction( MindVersionInfo.class );
		
		//공유 테이블의 마인드맵 정보 삭제
		if(ListTools.isNotEmpty( mindShareRecords )) {
			for( MindShareRecord mindShareInfo : mindShareRecords) {
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
			emc.remove( mindContentInfo, CheckRemoveType.all );
		}
		
		emc.commit();
		return true;
	}

	/**
	 * 마인드맵 ID로 마인드맵 정보를 휴지통으로 이동
	 * 기본 정보를 기본 정보 테이블에서 휴지통 정보 테이블로 옮기고 공유 정보 상태를 삭제됨으로 설정
	 * @param mindId
	 * @throws Exception 
	 */
	public Boolean recycle(EntityManagerContainer emc, String mindId) throws Exception {
		Business business = new Business(emc);
		
		//먼저 휴지통에 이 mindBaseInfo가 있는지 조회해 있으면 먼저 삭제하고 없으면 새로 추가
		MindRecycleInfo mindRecycleInfo = emc.find( mindId,  MindRecycleInfo.class );

		//먼저 휴지통에서 해당 마인드맵 관련 정보 삭제
		if( mindRecycleInfo != null ) {
			emc.beginTransaction( MindRecycleInfo.class );
			emc.remove( mindRecycleInfo, CheckRemoveType.all );
			emc.commit();
		}
		
		//마인드맵 정보 조회
		MindBaseInfo mindBaseInfo = emc.find( mindId, MindBaseInfo.class);
		
		//마인드맵 정보로 새 마인드맵 휴지통 정보 생성
		MindRecycleInfo newMindRecycleInfo = new MindRecycleInfo();
		newMindRecycleInfo.setId( mindBaseInfo.getId() );
		newMindRecycleInfo.setName( mindBaseInfo.getName() );
		newMindRecycleInfo.setFolderId( mindBaseInfo.getFolderId() );
		newMindRecycleInfo.setCreator( mindBaseInfo.getCreator() );
		newMindRecycleInfo.setCreatorUnit( mindBaseInfo.getCreatorUnit() );
		newMindRecycleInfo.setDescription( mindBaseInfo.getDescription() );
		newMindRecycleInfo.setFileVersion( mindBaseInfo.getFileVersion() );
		newMindRecycleInfo.setShared( false );//휴지통으로 이동하면 파일의 공유 정보가 모두 삭제됨
		
		//공유했던 모든 마인드맵 문서 조회
		List<MindShareRecord> mindShareRecords = null;
		List<String> ids = business.mindShareRecordFactory().listIdsWithMindId(mindId);
		if( ListTools.isNotEmpty( ids )) {
			mindShareRecords = business.mindShareRecordFactory().list(ids);
		}
		
		//마인드맵 문서의 모든 버전 정보 조회
		List<MindVersionInfo> mindVersionInfos = null;
		ids = business.mindVersionInfoFactory().listVersionsWithMindId(mindId);
		if( ListTools.isNotEmpty( ids )) {
			mindVersionInfos = business.mindVersionInfoFactory().listVersionsWithIds(ids);
		}
		
		//마지막으로 영속화 작업 수행
		emc.beginTransaction( MindRecycleInfo.class );
		emc.beginTransaction( MindBaseInfo.class );
		emc.beginTransaction( MindShareRecord.class );
		emc.beginTransaction( MindVersionInfo.class );
		
		//공유했던 마인드맵 파일의 기록 상태 삭제
		if( ListTools.isNotEmpty( mindShareRecords )) {
			for( MindShareRecord mindShareInfo : mindShareRecords ) {
				emc.remove( mindShareInfo, CheckRemoveType.all);
			}
		}
		
		//마인드맵 문서의 모든 버전 정보 조회
		if( ListTools.isNotEmpty( mindVersionInfos )) {
			for( MindVersionInfo mindVersionInfo : mindVersionInfos ) {
				emc.remove( mindVersionInfo, CheckRemoveType.all);
			}
		}
		
		//휴지통 마인드맵 정보 추가
		newMindRecycleInfo.composeSequnces();
		emc.persist( newMindRecycleInfo, CheckPersistType.all );
		
		//마인드맵 기본 정보 객체 삭제
		emc.remove( mindBaseInfo, CheckRemoveType.all );
		
		emc.commit();
		return true;
	}
	
	/**
	 * 마인드맵 ID로 휴지통에서 마인드맵 정보 복원
	 * 기본 정보를 휴지통 정보 테이블에서 기본 정보 테이블로 옮기고 공유 정보 상태를 정상으로 설정
	 * 원래 디렉터리가 아직 있는지 확인하고, 없으면 루트 디렉터리 root에 넣음
	 * @param mindId
	 * @throws Exception 
	 */
	public Boolean restore(EntityManagerContainer emc, String mindId) throws Exception {
		if( StringUtils.isEmpty( mindId ) ) {
			throw new Exception("마인드맵 ID가 비어 있어 마인드맵 정보를 복원할 수 없습니다!");
		}
		//먼저 휴지통에 이 MindRecycleInfo가 있는지 조회해 없으면 복원할 수 없음
		MindRecycleInfo mindRecycleInfo = emc.find( mindId,  MindRecycleInfo.class );
		if( mindRecycleInfo == null ) {
			throw new Exception("지정한 ID로 휴지통에서 마인드맵 정보를 찾지 못해 복원할 수 없습니다! mindId:" + mindId);
		}
		
		//마인드맵 정보 조회, 이미 있으면 먼저 삭제한 뒤 복원
		MindBaseInfo mindBaseInfo = emc.find( mindId, MindBaseInfo.class);
		if( mindBaseInfo != null ) {
			emc.beginTransaction( MindBaseInfo.class );
			emc.remove( mindBaseInfo, CheckRemoveType.all );
			emc.commit();
		}
		
		//마인드맵의 원래 디렉터리가 아직 있는지 조회해 없으면 마인드맵을 루트 디렉터리 root에 넣음
		MindFolderInfo mindFolderInfo = emc.find( mindRecycleInfo.getFolderId(), MindFolderInfo.class );
		
		//마인드맵 휴지통 정보로 새 마인드맵 정보 생성
		mindBaseInfo = new MindBaseInfo();
		mindBaseInfo.setId( mindId );
		mindBaseInfo.setName( mindRecycleInfo.getName() );
		if( mindFolderInfo == null ) {
			mindBaseInfo.setFolderId( "root" );
		}else {
			mindBaseInfo.setFolderId( mindRecycleInfo.getFolderId() );
		}
		mindBaseInfo.setCreator( mindRecycleInfo.getCreator() );
		mindBaseInfo.setCreatorUnit( mindRecycleInfo.getCreatorUnit() );
		mindBaseInfo.setDescription( mindRecycleInfo.getDescription() );
		mindBaseInfo.setFileVersion( mindRecycleInfo.getFileVersion() );
		mindBaseInfo.setShared( false ); //휴지통으로 이동하면 모든 공유 정보가 삭제됨
		
		List<String> editorList = new ArrayList<>();
		editorList.add( mindRecycleInfo.getCreator() );
		mindBaseInfo.setEditorList(editorList);
		
		//마지막으로 영속화 작업 수행
		emc.beginTransaction( MindRecycleInfo.class );
		emc.beginTransaction( MindBaseInfo.class );
		
		//마인드맵 정보 추가, 상세 정보는 이미 있음
		mindBaseInfo.composeSequnces();
		emc.persist( mindBaseInfo, CheckPersistType.all );
		
		//휴지통의 마인드맵 기본 정보 객체 삭제
		emc.remove( mindRecycleInfo, CheckRemoveType.all );
		
		emc.commit();
		return true;
	}
}
