package com.x.bbs.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSPermissionRole;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.bbs.entity.BBSUserRole;

/**
 * 포럼 정보 관리 서비스 클래스
 * @author LIYI
 *
 */
public class BBSForumInfoServiceAdv {
	
	private BBSForumInfoService forumInfoService = new BBSForumInfoService();
	
	/**
	 * 전달된 ID로 데이터베이스에서 BBSForumInfo 객체 조회
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public BBSForumInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, BBSForumInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 데이터베이스에 BBSForumInfo 객체 저장
	 * @param wrapIn
	 */
	public BBSForumInfo save( BBSForumInfo _bBSForumInfo ) throws Exception {
		if( _bBSForumInfo == null ){
			throw new Exception( "bBSForumInfo can not null!" );
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return forumInfoService.save( emc, _bBSForumInfo );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * ID로 데이터베이스에서 BBSForumInfo 객체 삭제
	 * @param id
	 * @throws Exception
	 */
	public void delete( String forumId ) throws Exception {
		if( StringUtils.isEmpty( forumId ) ){
			throw new Exception( "forumId can not null!" );
		}
		Business business = null;
		BBSForumInfo forumInfo = null;
		List<String> ids = null;
		List<BBSRoleInfo> roleInfoList = null;
		List<BBSUserRole> userRoleList = null;
		List<BBSPermissionRole> permissionRoleList = null;
		List<BBSPermissionInfo> permissionInfoList = null;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			
			//해당 포럼 관련 모든 항목 삭제, 주로 권한이며 게시글과 게시판은 이미 삭제됨
			emc.beginTransaction( BBSForumInfo.class );
			emc.beginTransaction( BBSUserRole.class );
			emc.beginTransaction( BBSPermissionInfo.class );
			emc.beginTransaction( BBSPermissionRole.class );
			emc.beginTransaction( BBSRoleInfo.class );
			
			//포럼의 모든 사용자-역할 연결 정보 삭제
			ids = business.userRoleFactory().listByForumId( forumId );
			if( ListTools.isNotEmpty( ids )) {
				userRoleList = business.userRoleFactory().list( ids );
				if( ListTools.isNotEmpty( userRoleList )) {
					for( BBSUserRole userRole : userRoleList ) {
						emc.remove( userRole, CheckRemoveType.all );
					}
				}
			}
			
			//권한-역할 연결 정보 삭제
			ids = business.permissionRoleFactory().listByForumId( forumId );
			if( ListTools.isNotEmpty( ids )) {
				permissionRoleList = business.permissionRoleFactory().list( ids );
				if( ListTools.isNotEmpty( permissionRoleList )) {
					for( BBSPermissionRole permissionRole : permissionRoleList ) {
						emc.remove( permissionRole, CheckRemoveType.all );
					}
				}
			}
			
			//포럼의 모든 역할 정보 삭제
			ids = business.roleInfoFactory().listRoleByForumId(forumId);
			if( ListTools.isNotEmpty( ids )) {
				roleInfoList = business.roleInfoFactory().list( ids );
				if( ListTools.isNotEmpty( roleInfoList )) {
					for( BBSRoleInfo roleInfo : roleInfoList ) {
						emc.remove( roleInfo, CheckRemoveType.all );
					}
				}
			}
			
			//모든 권한 정보 삭제
			ids = business.permissionInfoFactory().listPermissionByForumId(forumId);
			if( ListTools.isNotEmpty( ids )) {
				permissionInfoList = business.permissionInfoFactory().list( ids );
				if( ListTools.isNotEmpty( permissionInfoList )) {
					for( BBSPermissionInfo permissionInfo : permissionInfoList ) {
						emc.remove( permissionInfo, CheckRemoveType.all );
					}
				}
			}
			
			forumInfo = emc.find( forumId, BBSForumInfo.class );
			if( forumInfo != null ) {
				emc.remove( forumInfo, CheckRemoveType.all );
			}
			emc.commit();
		}catch( Exception e ){
			throw e;
		}
	}

	public List<BBSForumInfo> listAll() throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return forumInfoService.listAll( emc );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 권한에 따라 접근 가능한 모든 포럼 정보 목록 조회
	 * 1. 전 직원 접근 가능한 모든 포럼 정보
	 * 2. 내가 접근 권한을 가진 모든 포럼 정보
	 * 
	 * @return
	 * @throws Exception 
	 */
	public List<BBSForumInfo> listAllViewAbleForumWithUserPermission( List<String> viewAbleForumIds ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return forumInfoService.listAllViewAbleForumWithUserPermission( emc, viewAbleForumIds );
		}catch( Exception e ){
			throw e;
		}
	}

	public void checkForumManager( BBSForumInfo forumInfo ) throws Exception {
		if( forumInfo == null ){
			throw new Exception( "forumInfo can not null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			forumInfoService.checkForumManager( emc, forumInfo );
		}catch( Exception e ){
			throw e;
		}
	}

	public void deleteForumManager( String forumId ) throws Exception {
		if( forumId == null || forumId.isEmpty() ){
			throw new Exception( "forumId can not null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			forumInfoService.deleteForumManager( emc, forumId );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<String> listAllPublicForumIds() throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return forumInfoService.listAllPublicForumIds( emc );
		}catch( Exception e ){
			throw e;
		}
	}
}