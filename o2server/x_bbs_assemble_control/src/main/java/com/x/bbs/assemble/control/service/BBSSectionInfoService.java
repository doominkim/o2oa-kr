package com.x.bbs.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSPermissionRole;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSUserRole;

/**
 * 포럼 정보 관리 서비스 클래스
 * @author LIYI
 *
 */
public class BBSSectionInfoService {
	private static  Logger logger = LoggerFactory.getLogger( BBSSectionInfoService.class );
	private BBSSubjectInfoService subjectInfoService = new BBSSubjectInfoService();
	private UserManagerService userManagerService = new UserManagerService();

	/**
	 * 전달된 ID로 데이터베이스에서 BBSSectionInfo 객체 조회
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public BBSSectionInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, BBSSectionInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 전달된 게시판 이름으로 데이터베이스에서 BBSSectionInfo 객체 조회
	 * @param mainSectionName
	 * @return
	 * @throws Exception
	 */
	public BBSSectionInfo getMainSectionBySectionName( EntityManagerContainer emc, String mainSectionName ) throws Exception {
		if( mainSectionName  == null || mainSectionName.isEmpty() ){
			throw new Exception( "mainSectionName is null!" );
		}
		Business business = new Business( emc );
		List<BBSSectionInfo> bBSSectionInfoList= business.sectionInfoFactory().getMainSectionBySectionName( mainSectionName );
		if(ListTools.isNotEmpty(bBSSectionInfoList)){
			return bBSSectionInfoList.get(0);
		}else{
			return null;
		}
	}

	/**
	 * 데이터베이스에 BBSSectionInfo 객체 저장
	 * @param emc
	 * @param _bBSSectionInfo
	 * @return
	 * @throws Exception
	 */
	public BBSSectionInfo save( EntityManagerContainer emc, BBSSectionInfo _bBSSectionInfo ) throws Exception {
		BBSSectionInfo _bBSSectionInfo_tmp = null;
		BBSForumInfo _forumInfo_tmp = null;
		if( _bBSSectionInfo.getId() == null ){
			_bBSSectionInfo.setId( BBSSectionInfo.createId() );
		}
		_bBSSectionInfo_tmp = emc.find( _bBSSectionInfo.getId(), BBSSectionInfo.class );
		_forumInfo_tmp = emc.find( _bBSSectionInfo.getForumId(), BBSForumInfo.class );
		emc.beginTransaction( BBSForumInfo.class );
		emc.beginTransaction( BBSSectionInfo.class );
		if( _bBSSectionInfo_tmp == null ){
			_bBSSectionInfo.setUpdateTime( _bBSSectionInfo.getCreateTime() );
			emc.persist( _bBSSectionInfo, CheckPersistType.all);
			if( _forumInfo_tmp != null ){
				_forumInfo_tmp.setSectionTotal( _forumInfo_tmp.getSectionTotal() + 1 );
				if( StringUtils.isEmpty( _forumInfo_tmp.getReplyMessageNotifyType() )){
					_forumInfo_tmp.setReplyMessageNotifyType("0,0,0");
				}
				emc.check( _forumInfo_tmp, CheckPersistType.all );
			}
		}else{
			_bBSSectionInfo.setCreateTime( _bBSSectionInfo_tmp.getCreateTime() );
			_bBSSectionInfo.copyTo( _bBSSectionInfo_tmp, JpaObject.FieldsUnmodify  );
			if( StringUtils.isEmpty( _bBSSectionInfo_tmp.getReplyMessageNotifyType() )){
				_bBSSectionInfo_tmp.setReplyMessageNotifyType("0,0,0");
			}
			emc.check( _bBSSectionInfo_tmp, CheckPersistType.all );
		}
		emc.commit();
		return _bBSSectionInfo;
	}

	/**
	 * ID로 데이터베이스에서 BBSSectionInfo 객체 삭제
	 * @param id
	 * @throws Exception
	 */
	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		BBSSectionInfo bBSSectionInfo = null;
		BBSForumInfo forumInfo = null;
		Business business = null;
		List<String> subSectionIds = null;
		List<String> subjectIds = null;
		List<BBSPermissionInfo> permissionList = null;
		List<BBSPermissionRole> permissionRoleList = null;
		business = new Business( emc );

		emc.beginTransaction( BBSPermissionInfo.class );
		emc.beginTransaction( BBSPermissionRole.class );

		//모든 하위 게시판 재귀 삭제
		subSectionIds = business.sectionInfoFactory().listSubSectionIdsByMainSectionId( id );
		if( ListTools.isNotEmpty( subSectionIds ) ){
			for( String subSectionId : subSectionIds ){
				if( !id.equalsIgnoreCase( subSectionId )){
					delete( emc, subSectionId );
				}
			}
		}

		//모든 권한 및 역할-권한 연결 삭제
		permissionList = business.permissionInfoFactory().listPermissionByMainSectionId( id );
		if( ListTools.isNotEmpty( permissionList ) ){
			for( BBSPermissionInfo permissionInfo : permissionList ){
				//역할-권한 연결 삭제
				permissionRoleList = business.permissionRoleFactory().listByPermissionCode( permissionInfo.getPermissionCode() );
				if( ListTools.isNotEmpty( permissionRoleList ) ){
					for( BBSPermissionRole permissionRole : permissionRoleList ){
						emc.remove( permissionRole, CheckRemoveType.all );
					}
				}
				emc.remove( permissionInfo, CheckRemoveType.all );
			}
		}

		//상위 게시판 및 하위 게시판의 모든 주제글과 답글 삭제, 한 번에 1000건 삭제
		subjectIds = business.subjectInfoFactory().listSubjectIdsBySection( id, 1000 );
		do{
			if( ListTools.isNotEmpty( subjectIds ) ){
				for( String subjectId : subjectIds ){
					subjectInfoService.delete( emc, subjectId );
				}
			}
			subjectIds = business.subjectInfoFactory().listSubjectIdsBySection( id, 1000 );
		}while( ListTools.isNotEmpty( subjectIds ) );

		bBSSectionInfo = emc.find(id, BBSSectionInfo.class);
		if ( null != bBSSectionInfo ) {
			emc.beginTransaction( BBSForumInfo.class );
			emc.beginTransaction( BBSSectionInfo.class );
			forumInfo = emc.find( bBSSectionInfo.getForumId(), BBSForumInfo.class );
			if( forumInfo != null ){//포럼 게시판 수 -1
				if( forumInfo.getSectionTotal() > 0 ){//포럼 게시판 수 -1
					forumInfo.setSectionTotal( forumInfo.getSectionTotal() - 1 );
				}else{
					forumInfo.setSectionTotal( 0L );
				}
				if( StringUtils.isEmpty( forumInfo.getReplyMessageNotifyType() )){
					forumInfo.setReplyMessageNotifyType("0,0,0");
				}
			}
			emc.check( forumInfo, CheckPersistType.all );
			emc.remove( bBSSectionInfo, CheckRemoveType.all );
		}
		emc.commit();
	}

	public List<BBSSectionInfo> listAll( EntityManagerContainer emc ) throws Exception {
		Business business = new Business( emc );
		return business.sectionInfoFactory().listAll();
	}

	public List<String> listByForumId( EntityManagerContainer emc, String forumId ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		Business business = new Business( emc );
		return business.sectionInfoFactory().listByForumId( forumId );
	}

	/**
	 * 포럼 ID로 포럼의 모든 상위 게시판 정보 수 조회
	 * @param emc
	 * @param forumId
	 * @return
	 * @throws Exception
	 */
	public Long countMainSectionByForumId( EntityManagerContainer emc, String forumId ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		Business business = new Business( emc );
		return business.sectionInfoFactory().countMainSectionByForumId( forumId );
	}

	/**
	 * 상위 게시판 ID로 상위 게시판의 모든 하위 게시판 정보 수 조회
	 * @param emc
	 * @param sectionId
	 * @return
	 * @throws Exception
	 */
	public Long countSubSectionByMainSectionId( EntityManagerContainer emc, String sectionId ) throws Exception {
		if( sectionId  == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		Business business = new Business( emc );
		return business.sectionInfoFactory().countSubSectionByMainSectionId( sectionId );
	}

	/**
	 * 포럼 ID로 포럼의 모든 게시판 정보 조회
	 * @param forumId
	 * @return
	 * @throws Exception
	 */
	public List<BBSSectionInfo> listMainSectionByForumId( EntityManagerContainer emc, String forumId ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		Business business = new Business( emc );
		return business.sectionInfoFactory().listMainSectionByForumId( forumId );
	}

	/**
	 * 포럼 ID로 포럼의 모든 게시판 정보 조회
	 * @param forumId
	 * @return
	 * @throws Exception
	 */
	public List<BBSSectionInfo> viewMainSectionByForumId(  EntityManagerContainer emc, String forumId, List<String> viewableSectionIds ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		Business business = new Business( emc );
		return business.sectionInfoFactory().viewMainSectionByForumId( forumId, viewableSectionIds );
	}

	/**
	 * 상위 게시판 ID로 모든 하위 게시판 정보 목록 조회
	 * @param sectionId
	 * @return
	 * @throws Exception
	 */
	public List<BBSSectionInfo> viewSubSectionByMainSectionId( EntityManagerContainer emc, String sectionId, List<String> viewableSectionIds  ) throws Exception {
		if( sectionId  == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		Business business = new Business( emc );
		return business.sectionInfoFactory().viewSubSectionByMainSectionId( sectionId, viewableSectionIds );
	}

	/**
	 * 상위 게시판 ID로 모든 하위 게시판 정보 목록 조회
	 * @param sectionId
	 * @return
	 * @throws Exception
	 */
	public List<BBSSectionInfo> listSubSectionByMainSectionId( EntityManagerContainer emc, String sectionId  ) throws Exception {
		if( sectionId  == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		Business business = new Business( emc );
		return business.sectionInfoFactory().listSubSectionByMainSectionId( sectionId );
	}

	public void checkSectionManager( EntityManagerContainer emc, String sectionId ) throws Exception {
		if( sectionId  == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		BBSSectionInfo sectionInfo = emc.find( sectionId, BBSSectionInfo.class );
		checkSectionManager( emc, sectionInfo );
	}

	/**
	 * 게시판 관리자의 권한 및 역할 설정 확인
	 * @param sectionInfo
	 * @throws Exception
	 */
	public void checkSectionManager( EntityManagerContainer emc, BBSSectionInfo sectionInfo ) throws Exception {
		if( sectionInfo == null ){
			throw new Exception( "sectionInfo is null!" );
		}

		List<String> ids = null;
		List<BBSUserRole> userRoleList= null;
		String unitName = null;
		String topUnitName = null;
		BBSRoleInfo roleInfo = null;
		BBSUserRole userRole_new = null;
		Business business = null;
		Boolean exists = false;
		List<String> currentManagerNames = new ArrayList<>();

		if( ListTools.isNotEmpty( sectionInfo.getModeratorNames() ) ){
			currentManagerNames = sectionInfo.getModeratorNames();
		}

		business = new Business( emc );
		emc.beginTransaction( BBSUserRole.class );
		roleInfo = business.roleInfoFactory().getRoleByCode( "SECTION_MANAGER_" + sectionInfo.getId() );
		if( roleInfo == null ){
			throw new Exception("role info{'code':'"+"SECTION_MANAGER_" + sectionInfo.getId()+"'} is not exists.");
		}
		ids = business.userRoleFactory().listIdsByRoleCode( "SECTION_MANAGER_" + sectionInfo.getId() );
		if( ListTools.isNotEmpty( ids ) ){
			userRoleList = business.userRoleFactory().list( ids );
		}
		if( ListTools.isNotEmpty( userRoleList ) ){
			for( BBSUserRole userRole : userRoleList ){
				exists = false;
				if( ListTools.isNotEmpty(currentManagerNames) ){
					for( String name : currentManagerNames ){
						if( name.equals( userRole.getObjectName()) || name.equalsIgnoreCase( userRole.getUniqueId() )){
							exists = true;
						}
					}
				}
				if( !exists ){
					emc.remove( userRole, CheckRemoveType.all );
				}
			}
		}
		if( ListTools.isNotEmpty( currentManagerNames ) ){
			for( String name : currentManagerNames ){
				exists = false;
				if( ListTools.isNotEmpty( userRoleList ) ){
					for( BBSUserRole userRole : userRoleList ){
						if( name.equals( userRole.getObjectName()) || name.equalsIgnoreCase( userRole.getUniqueId() )){
							exists = true;
						}
					}
				}
				if( !exists ){
					userRole_new = new BBSUserRole();
					userRole_new.setForumId( roleInfo.getForumId() );
					userRole_new.setMainSectionId( roleInfo.getMainSectionId() );
					userRole_new.setSectionId( roleInfo.getSectionId() );
					userRole_new.setObjectName( name );
					userRole_new.setUniqueId( name );
					userRole_new.setObjectType( "人员" ); //사용자|조직|그룹
					userRole_new.setRoleCode( roleInfo.getRoleCode() );
					userRole_new.setRoleId( roleInfo.getId() );
					userRole_new.setRoleName( roleInfo.getRoleName() );
					try {
						unitName = userManagerService.getUnitNameWithPerson( name );
					}catch( Exception e ) {
						logger.warn("user has no identity!user:" + name );
						unitName = "알 수 없는 조직";
					}
					try {
						topUnitName = userManagerService.getTopUnitNameWithPerson( name );
					}catch( Exception e ) {
						logger.warn("user has no identity!user:" + name );
						topUnitName = "알 수 없는 회사";
					}

					userRole_new.setTopUnitName( topUnitName );
					userRole_new.setUnitName( unitName );

					emc.persist( userRole_new, CheckPersistType.all );
				}
			}
		}
		emc.commit();
	}

	/**
	 * 사용자 권한과 포럼 ID로 모든 상위 게시판 정보 ID 목록 조회
	 * @param forumId
	 * @param sectionIds
	 * @return
	 * @throws Exception
	 */
	public List<BBSSectionInfo> listAllViewAbleMainSectionWithUserPermission( EntityManagerContainer emc, String forumId, List<String> sectionIds ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		Business business = new Business( emc );
		return business.sectionInfoFactory().viewMainSectionByForumId( forumId, sectionIds );
	}

	/**
	 *
	 * @param viewforumIds
	 * @param publicStatus 공개 여부
	 * @return
	 * @throws Exception
	 */
	public List<String> viewSectionByForumIds( EntityManagerContainer emc, List<String> viewforumIds, Boolean publicStatus ) throws Exception {
		if( viewforumIds  == null || viewforumIds.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.sectionInfoFactory().viewSectionByForumId( viewforumIds, publicStatus );
	}

	public List<BBSSectionInfo> list( EntityManagerContainer emc, List<String> publicSectionIds) throws Exception {
		Business business = new Business( emc );
		return business.sectionInfoFactory().list( publicSectionIds );
	}

	public boolean hasPublishPermission(BBSSectionInfo sectionInfo, EffectivePerson person, List<String> permissionInfoList) throws Exception{
		String checkUserPermission = "SECTION_SUBJECT_PUBLISH_" + sectionInfo.getId();
		String allUser = "所有人";
		System.out.println("可发布人员："+XGsonBuilder.toJson(sectionInfo.getPublishPermissionList()));
		if (allUser.equals(sectionInfo.getSubjectPublishAble())) {
			return true;
		}else if(sectionInfo.getPublishPermissionList().contains(person.getDistinguishedName())) {
			return true;
		}else if(sectionInfo.getModeratorNames().contains(checkUserPermission)){
			return true;
		}else if(permissionInfoList!=null && permissionInfoList.contains(checkUserPermission)){
			return true;
		}else{
			List<String> unitList = userManagerService.listUnitNamesWithPerson(person.getDistinguishedName());
			if(ListTools.containsAny(sectionInfo.getPublishPermissionList(), unitList)){
				return true;
			}

			Business business = new Business(null);
			return business.isManager(person);
		}
	}
}
