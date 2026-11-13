package com.x.mind.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindFolderInfo;

/**
 * 마인드맵 폴더 정보 조회 작업 서비스 클래스: 조회
 * @author O2LEE
 *
 */
class ServiceMindFolderInfoQuery{

	/**
	 * 사용자 이름으로 사용자의 모든 개인 폴더 정보 ID 목록 조회
	 * @param emc
	 * @param person
	 * @return
	 * @throws Exception 
	 */
	List<String> listAllIdsWithPerson(EntityManagerContainer emc, String person) throws Exception {
		Business business = new Business( emc );
		return business.mindFolderInfoFactory().list(null, null, null, person, null);
	}

	/**
	 * 지정 ID 목록으로 마인드맵 디렉터리 정보 목록 조회
	 * @param emc
	 * @param ids
	 * @return
	 * @throws Exception 
	 */
	List<MindFolderInfo> listWithIds(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business = new Business( emc );
		return business.mindFolderInfoFactory().list(ids);
	}

	/**
	 * ID로 마인드맵 폴더 정보 객체 조회
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MindFolderInfo get(EntityManagerContainer emc,String id) throws Exception {
		Business business = new Business( emc );
		return business.mindFolderInfoFactory().get(id);
	}

	/**
	 * ID로 폴더의 하위 디렉터리 수 조회
	 * @param emc
	 * @param folderId
	 * @return
	 * @throws Exception 
	 */
	public Long countChildWithFolder(EntityManagerContainer emc, String folderId) throws Exception {
		Business business = new Business( emc );
		return business.mindFolderInfoFactory().countChildWithFolder(folderId);
	}

	/**
	 * 폴더 ID로 모든 하위 폴더 ID 조회(재귀 조회)
	 * @param emc
	 * @param folderId
	 * @param allFolderIds
	 * @return
	 * @throws Exception
	 */
	public List<String> listAllChildrenIds(EntityManagerContainer emc,String folderId, List<String> allFolderIds) throws Exception {
		if( allFolderIds == null) {
			allFolderIds = new ArrayList<>();
		}
		List<String> childrenIds = null;
		childrenIds = listChildrenIds(emc, folderId);
		if( ListTools.isNotEmpty( childrenIds )) {
			for( String id : childrenIds ) {
				if( !allFolderIds.contains( id )) {
					allFolderIds.add( id );
					allFolderIds = listAllChildrenIds( emc, id, allFolderIds);
				}
			}
		}
		return allFolderIds;
	}

	/**
	 * 상위 폴더 ID로 하위 폴더 ID 목록 조회
	 * @param emc
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	public List<String> listChildrenIds(EntityManagerContainer emc, String parentId) throws Exception {
		Business business = new Business( emc );
		return business.mindFolderInfoFactory().list(parentId, null, null, null, null);
	}
}