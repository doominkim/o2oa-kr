package com.x.mind.assemble.control.service;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.mind.entity.MindFolderInfo;

/**
 * 마인드맵 폴더 정보 영속화 작업 서비스 클래스: 저장, 수정, 삭제
 * @author O2LEE
 *
 */
class ServiceMindFolderInfoPersist{
	
	/**
	 * 마인드맵 디렉터리 저장
	 * @param mindFolderInfo
	 * @return
	 * @throws Exception 
	 */
	MindFolderInfo save(EntityManagerContainer emc, MindFolderInfo _mindFolderInfo) throws Exception {
		if( StringUtils.isEmpty(_mindFolderInfo.getId()) ){
			_mindFolderInfo.setId( MindFolderInfo.createId() );
		}
		if( StringUtils.isEmpty(_mindFolderInfo.getParentId()) ) {
			_mindFolderInfo.setParentId( "root" );
		}
		MindFolderInfo oldFolder = null;
		oldFolder = emc.find( _mindFolderInfo.getId(), MindFolderInfo.class );
		emc.beginTransaction( MindFolderInfo.class );
		if( oldFolder == null ){
			oldFolder = _mindFolderInfo;
			emc.persist( oldFolder, CheckPersistType.all);
		}else{
			_mindFolderInfo.copyTo( oldFolder, JpaObject.FieldsUnmodify  );
			emc.check( oldFolder, CheckPersistType.all );	
		}
		emc.commit();
		return oldFolder;
	}

	/**
	 * ID로 폴더 정보 삭제
	 * @param emc
	 * @param folderId
	 * @return
	 * @throws Exception
	 */
	public Boolean delete(EntityManagerContainer emc, String folderId) throws Exception {
		MindFolderInfo entity = emc.find( folderId, MindFolderInfo.class );
		if( entity != null ){
			emc.beginTransaction( MindFolderInfo.class );
			emc.remove( entity,  CheckRemoveType.all);	
			emc.commit();
		}
		return true;
	}
}
