package com.x.bbs.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.assemble.control.factory.BBSPermissionInfoFactory;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSSectionInfo;

/**
 * 포럼 권한 정보 관리 서비스 클래스
 * 
 * 권한 목록:
       포럼 권한:
              포럼 열람(FORUM_VIEW): 사용자가 BBS 시스템에서 해당 포럼 접근 가능
              //포럼 주제 게시(FORUM_SUBJECT_PUBLISH): 사용자가 포럼의 모든 게시판에 주제 게시 가능
              //포럼 답글 작성(FORUM_REPLY_PUBLISH): 사용자가 포럼의 모든 주제에 답글 작성 가능
              //포럼 주제 추천(FORUM_SUBJECT_RECOMMEND): 사용자가 지정 포럼의 모든 게시판에서 지정 주제를 포럼 홈에 추천 가능
              //포럼 주제 상단 고정(FORUM_SUBJECT_STICK): 사용자가 포럼의 모든 주제에 대한 상단 고정 권한 보유
              //포럼 우수 주제 신청(FORUM_SUBJECT_CREAM): 사용자가 포럼의 모든 주제에 대한 우수 주제 설정 권한 보유
              //포럼 주제 관리(FORUM_SUBJECT_MANAGEMENT): 사용자가 포럼의 모든 주제에 대한 잠금·삭제 권한 보유
              //포럼 답글 관리(FORUM_REPLY_MANAGEMENT): 사용자가 포럼의 모든 답글에 대한 삭제 권한 보유
              포럼 게시판 관리(FORUM_INFO_MANAGEMENT): 사용자가 포럼 게시판의 추가·삭제·수정 권한 보유
              포럼 권한 관리(FORUM_PERMISSION_MANAGEMENT): 사용자가 포럼 사용자에게 해당 포럼 권한을 설정할 수 있는 권한 보유
              포럼 설정 관리(FORUM_CONFIG_MANAGEMENT): 사용자가 포럼 매개변수 설정 권한 보유

       게시판 권한:
              포럼 열람(SECTION_VIEW): 사용자가 BBS 시스템에서 해당 게시판 접근 가능
              주제 게시(SECTION_SUBJECT_PUBLISH): 사용자가 지정 게시판에 주제 게시 가능
              주제 심사(SECTION_SUBJECT_AUDIT): 주제 심사가 필요한 경우 사용자가 지정 게시판에 게시된 주제 심사 가능
              주제 관리(SECTION_SUBJECT_MANAGEMENT): 사용자가 지정 게시판에서 게시된 주제를 조회·삭제 가능
              답글 작성(SECTION_REPLY_PUBLISH): 사용자가 지정 게시판의 모든 주제에 답글 작성 가능
              답글 심사(SECTION_REPLY_AUDIT): 답글 심사가 필요한 경우 사용자가 지정 게시판의 모든 답글 심사 가능
              답글 관리(SECTION_REPLY_MANAGEMENT): 사용자가 지정 게시판에서 답글 조회 또는 삭제 가능
              게시판 주제 추천(SECTION_SUBJECT_RECOMMEND): 사용자가 지정 게시판에서 지정 주제 추천 가능
              게시판 주제 상단 고정(SECTION_SUBJECT_STICK): 사용자가 지정 게시판에서 지정 주제 상단 고정 가능
              게시판 우수 주제 신청(SECTION_SUBJECT_CREAM): 사용자가 지정 게시판에서 지정 주제의 우수 주제 설정 가능
              게시판 관리(SECTION_SECTION_MANAGER): 사용자가 지정 게시판에서 하위 게시판 생성·삭제 등 가능
              게시판 권한 관리(SECTION_PERMISSION_MANAGEMENT): 사용자가 포럼 사용자의 지정 게시판 권한 관리 가능
              게시판 설정 관리(SECTION_CONFIG_MANAGEMENT): 사용자가 지정 게시판의 시스템 매개변수 설정 수정 가능
              
 * @author LIYI
 *
 */
public class BBSPermissionInfoService {
	
	private UserManagerService userManagerService = new UserManagerService();
	private static  Logger logger = LoggerFactory.getLogger( BBSPermissionInfoService.class );
	
	/**
	 * 역할 CODE로 역할에 연결된 모든 권한 정보 목록 조회
	 * @param roleCode
	 * @return
	 * @throws Exception
	 */
	public List<BBSPermissionInfo> listPermissionByRoleCode( String roleCode ) throws Exception {
		if( roleCode == null ){
			throw new Exception("roleCode is null, can not query any permission info!");
		}
		Business business = null;
		List<String> ids = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.permissionRoleFactory().listPermissionByRoleCode( roleCode );
			if( ListTools.isNotEmpty( ids ) ){
				return business.permissionInfoFactory().list(ids);
			}else{
				return null;
			}
		}catch( Exception e ){
			logger.warn( "system list permission by role code got an exception!" );
			throw e;
		}
	}
	
	/**
	 * 포럼 ID로 지정 포럼의 모든 권한 정보 목록 조회
	 * @param roleCode
	 * @return
	 * @throws Exception
	 */
	public List<BBSPermissionInfo> listPermissionByForumId( String forumId ) throws Exception {
		if( forumId == null ){
			throw new Exception("forumId is null, can not query any permission info!");
		}
		Business business = null;
		List<String> ids = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.permissionInfoFactory().listPermissionByForumId( forumId );
			if( ListTools.isNotEmpty( ids ) ){
				return business.permissionInfoFactory().list(ids);
			}else{
				return null;
			}
		}catch( Exception e ){
			logger.warn( "system list permission by forum id got an exception!" );
			throw e;
		}
	}
	
	/**
	 * 포럼 ID로 지정 게시판의 모든 권한 정보 목록 조회
	 * @param roleCode
	 * @return
	 * @throws Exception
	 */
	public List<BBSPermissionInfo> listPermissionBySection( String sectionId ) throws Exception {
		if( sectionId == null ){
			throw new Exception("sectionId is null, can not query any permission info!");
		}
		Business business = null;
		List<String> ids = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.permissionInfoFactory().listPermissionBySectionId( sectionId, false );
			if( ListTools.isNotEmpty( ids ) ){
				return business.permissionInfoFactory().list(ids);
			}else{
				return null;
			}
		}catch( Exception e ){
			logger.warn( "system list permission by section id got an exception!" );
			throw e;
		}
	}
	
	/**
	 * 모든 권한 목록 조회
	 * @return
	 * @throws Exception
	 */
	public List<BBSPermissionInfo> listAllPermissionInfo( ) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.permissionInfoFactory().listAll();
		}catch( Exception e ){
			logger.warn( "system list all permission got an exception!" );
			throw e;
		}
	}
	
	/**
	 * 지정 ID 목록으로 권한 정보 목록 조회
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<BBSPermissionInfo> listPermissionByIds( List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			throw new Exception("ids is null, can not query any permission info!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.permissionInfoFactory().list(ids);
		}catch( Exception e ){
			logger.warn( "system list permission by ids got an exception!" );
			throw e;
		}
	}
	
	/**
	 * 지정 포럼의 권한 객체 확인 및 생성
	 * @param _bBSForumInfo
	 * @throws Exception
	 */
	public void createForumPermission( BBSForumInfo _bBSForumInfo ) throws Exception {
		if( _bBSForumInfo == null ){
			throw new Exception("forum info is null, can not create any permission info!");
		}
		String forumId = _bBSForumInfo.getId();
		String forumName = _bBSForumInfo.getForumName();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//포럼 열람(FORUM_VIEW): 사용자가 BBS 시스템에서 해당 포럼 접근 가능
			checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_VIEW", "FORUM_VIEW_" + forumId, "포럼["+forumName+"] 공개", "포럼[" + forumName + "]에 사용자가 접근할 수 있습니다" );			
			//포럼 주제 게시(FORUM_SUBJECT_PUBLISH): 사용자가 포럼의 모든 게시판에 주제 게시 가능
			//checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_SUBJECT_PUBLISH", "FORUM_SUBJECT_PUBLISH_" + forumId, "포럼["+forumName+"] 주제 게시", "포럼[" + forumName + "]에서 사용자가 모든 게시판에 주제 게시 가능" );			
			//포럼 답글 작성(FORUM_REPLY_PUBLISH): 사용자가 포럼의 모든 주제에 답글 작성 가능
			//checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_REPLY_PUBLISH", "FORUM_REPLY_PUBLISH_" + forumId, "포럼["+forumName+"] 답글 작성", "포럼[" + forumName + "]에서 사용자가 모든 주제에 답글 작성 가능" );			
            //포럼 주제 추천(FORUM_SUBJECT_RECOMMEND): 사용자가 지정 포럼의 모든 게시판에서 지정 주제를 포럼 홈에 추천 가능
			//checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_SUBJECT_RECOMMEND", "FORUM_SUBJECT_RECOMMEND_" + forumId, "포럼["+forumName+"] 주제 추천", "포럼[" + forumName + "]에서 사용자가 모든 게시판의 지정 주제를 포럼 홈에 추천 가능" );            
            //포럼 주제 상단 고정(FORUM_SUBJECT_STICK): 사용자가 포럼의 모든 주제에 대한 상단 고정 권한 보유
			//checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_SUBJECT_STICK", "FORUM_SUBJECT_STICK_" + forumId, "포럼["+forumName+"] 주제 상단 고정", "포럼[" + forumName + "]에서 사용자가 모든 주제의 상단 고정 권한 보유" );            
            //포럼 우수 주제 신청(FORUM_SUBJECT_CREAM): 사용자가 포럼의 모든 주제에 대한 우수 주제 설정 권한 보유
            //checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_SUBJECT_CREAM", "FORUM_SUBJECT_CREAM_" + forumId, "포럼["+forumName+"] 우수 주제 신청", "포럼[" + forumName + "]에서 사용자가 모든 주제의 우수 주제 설정 권한 보유" );            
            //포럼 주제 관리(FORUM_SUBJECT_MANAGEMENT): 사용자가 포럼의 모든 주제에 대한 잠금·삭제 권한 보유
            //checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_SUBJECT_MANAGEMENT", "FORUM_SUBJECT_MANAGEMENT_" + forumId, "포럼["+forumName+"] 주제 관리", "포럼[" + forumName + "]에서 사용자가 모든 주제의 잠금·삭제 권한 보유" );            
            //포럼 답글 관리(FORUM_REPLY_MANAGEMENT): 사용자가 포럼의 모든 답글에 대한 삭제 권한 보유
            //checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_REPLY_MANAGEMENT", "FORUM_REPLY_MANAGEMENT_" + forumId, "포럼["+forumName+"] 답글 관리", "포럼[" + forumName + "]에서 사용자가 모든 답글의 삭제 권한 보유" );            
            //포럼 게시판 관리(FORUM_INFO_MANAGEMENT): 사용자가 포럼 게시판의 추가·삭제·수정 권한 보유
            checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_INFO_MANAGEMENT", "FORUM_INFO_MANAGEMENT_" + forumId, "포럼["+forumName+"] 섹션 관리", "포럼[" + forumName + "]에서 섹션을 추가, 삭제, 수정할 수 있습니다" );            
            //포럼 권한 관리(FORUM_PERMISSION_MANAGEMENT): 사용자가 포럼 사용자에게 해당 포럼 권한을 설정할 수 있는 권한 보유
            checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_PERMISSION_MANAGEMENT", "FORUM_PERMISSION_MANAGEMENT_" + forumId, "포럼["+forumName+"] 권한 관리", "포럼[" + forumName + "]에서 사용자 권한을 설정할 수 있습니다" );            
            //포럼 설정 관리(FORUM_CONFIG_MANAGEMENT): 사용자가 포럼 매개변수 설정 권한 보유
            checkAndSaveBBSPermissionInfo( emc, _bBSForumInfo, "FORUM_CONFIG_MANAGEMENT", "FORUM_CONFIG_MANAGEMENT_" + forumId, "포럼["+forumName+"] 설정 관리", "포럼[" + forumName + "]의 매개변수를 설정할 수 있습니다" );            
		}catch( Exception e ){
			logger.warn( "system check and create forum permission got an exception!" );
			throw e;
		}
	}
	
	/**
	 * 지정 게시판의 권한 객체 확인 및 생성
	 * @param _sectionInfo
	 * @throws Exception
	 */
	public void createSectionPermission( BBSSectionInfo _sectionInfo ) throws Exception {
		if( _sectionInfo == null ){
			throw new Exception("section info is null, can not create any permission info!");
		}
		String forumName = _sectionInfo.getForumName();
		String sectionId = _sectionInfo.getId();
		String sectionName = _sectionInfo.getSectionName();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//포럼 열람(SECTION_VIEW): 사용자가 BBS 시스템에서 해당 게시판 접근 가능
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_VIEW", "SECTION_VIEW_" + sectionId, "섹션["+forumName+"-"+sectionName+"] 공개", "섹션["+forumName+"-"+sectionName+"]에 사용자가 접근할 수 있습니다." );
			//주제 게시(SECTION_SUBJECT_PUBLISH): 사용자가 지정 게시판에 주제 게시 가능
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_SUBJECT_PUBLISH", "SECTION_SUBJECT_PUBLISH_" + sectionId, "섹션["+forumName+"-"+sectionName+"] 주제 게시", "섹션["+forumName+"-"+sectionName+"]에 주제를 게시할 수 있습니다" );
            //주제 심사(SECTION_SUBJECT_AUDIT): 주제 심사가 필요한 경우 사용자가 지정 게시판에 게시된 주제 심사 가능
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_SUBJECT_AUDIT", "SECTION_SUBJECT_AUDIT_" + sectionId, "섹션["+forumName+"-"+sectionName+"] 주제 검토", "섹션["+forumName+"-"+sectionName+"]에 게시된 주제를 검토할 수 있습니다(검토가 필요한 경우)" );
            //주제 관리(SECTION_SUBJECT_MANAGEMENT): 사용자가 지정 게시판에서 게시된 주제를 조회·삭제 가능
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_SUBJECT_MANAGEMENT", "SECTION_SUBJECT_MANAGEMENT_" + sectionId, "섹션["+forumName+"-"+sectionName+"] 주제 관리", "섹션["+forumName+"-"+sectionName+"]에 게시된 주제를 조회하거나 삭제할 수 있습니다" );
            //답글 작성(SECTION_REPLY_PUBLISH): 사용자가 지정 게시판의 모든 주제에 답글 작성 가능
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_REPLY_PUBLISH", "SECTION_REPLY_PUBLISH_" + sectionId, "섹션["+forumName+"-"+sectionName+"] 댓글 작성", "섹션["+forumName+"-"+sectionName+"]의 모든 주제에 댓글을 작성할 수 있습니다" );
            //답글 심사(SECTION_REPLY_AUDIT): 답글 심사가 필요한 경우 사용자가 지정 게시판의 모든 답글 심사 가능
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_REPLY_AUDIT", "SECTION_REPLY_AUDIT_" + sectionId, "섹션["+forumName+"-"+sectionName+"] 댓글 검토", "섹션["+forumName+"-"+sectionName+"]의 모든 댓글을 검토할 수 있습니다(검토가 필요한 경우)" );
            //답글 관리(SECTION_REPLY_MANAGEMENT): 사용자가 지정 게시판에서 답글 조회 또는 삭제 가능
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_REPLY_MANAGEMENT", "SECTION_REPLY_MANAGEMENT_" + sectionId, "섹션["+forumName+"-"+sectionName+"] 댓글 관리", "섹션["+forumName+"-"+sectionName+"]의 댓글을 조회하거나 삭제할 수 있습니다" );
            //게시판 주제 추천(SECTION_SUBJECT_RECOMMEND): 사용자가 지정 게시판에서 지정 주제 추천 가능
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_SUBJECT_RECOMMEND", "SECTION_SUBJECT_RECOMMEND_" + sectionId, "섹션["+forumName+"-"+sectionName+"] 주제 추천", "섹션["+forumName+"-"+sectionName+"]의 주제를 추천할 수 있습니다" );
            //게시판 주제 상단 고정(SECTION_SUBJECT_STICK): 사용자가 지정 게시판에서 지정 주제 상단 고정 가능
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_SUBJECT_STICK", "SECTION_SUBJECT_STICK_" + sectionId, "섹션["+forumName+"-"+sectionName+"] 주제 상단 고정", "섹션["+forumName+"-"+sectionName+"]의 주제를 상단에 고정할 수 있습니다" );
            //게시판 우수 주제 신청(SECTION_SUBJECT_CREAM): 사용자가 지정 게시판에서 지정 주제의 우수 주제 설정 가능
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_SUBJECT_CREAM", "SECTION_SUBJECT_CREAM_" + sectionId, "섹션["+forumName+"-"+sectionName+"] 추천 게시물 지정", "섹션["+forumName+"-"+sectionName+"]의 주제를 추천 게시물로 지정할 수 있습니다" );
            //게시판 관리(SECTION_SECTION_MANAGER): 사용자가 지정 게시판에서 하위 게시판 생성·삭제 등 가능
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_INFO_MANAGER", "SECTION_INFO_MANAGER_" + sectionId, "섹션["+forumName+"-"+sectionName+"] 정보 관리", "섹션["+forumName+"-"+sectionName+"]의 정보를 관리하고 하위 섹션을 만들거나 삭제할 수 있습니다" );
			//게시판 권한 관리(SECTION_PERMISSION_MANAGEMENT): 사용자가 포럼 사용자의 지정 게시판 권한 관리 가능
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_PERMISSION_MANAGEMENT", "SECTION_PERMISSION_MANAGEMENT_" + sectionId, "섹션["+forumName+"-"+sectionName+"] 권한 관리", "섹션["+forumName+"-"+sectionName+"]의 사용자 권한을 관리할 수 있습니다" );
            //게시판 설정 관리(SECTION_CONFIG_MANAGEMENT): 사용자가 지정 게시판의 시스템 매개변수 설정 수정 가능
			checkAndSaveBBSPermissionInfo( emc, _sectionInfo, "SECTION_CONFIG_MANAGEMENT", "SECTION_CONFIG_MANAGEMENT_" + sectionId, "섹션["+forumName+"-"+sectionName+"] 설정 관리", "섹션["+forumName+"-"+sectionName+"]의 시스템 매개변수를 수정할 수 있습니다" );
		}catch( Exception e ){
			logger.warn( "system check and create section permission got an exception!" );
			throw e;
		}
	}
	
	/**
	 * 전달된 매개변수로 권한 정보를 구성하고 권한 존재 여부를 검증, 없으면 권한 정보 추가
	 * @param emc
	 * @param _bBSForumInfo
	 * @param permissionType
	 * @param permissionCode
	 * @param permissionName
	 * @param description
	 * @throws Exception
	 */
	private void checkAndSaveBBSPermissionInfo( EntityManagerContainer emc, BBSForumInfo _bBSForumInfo, String permissionFunction, String permissionCode, String permissionName, String description ) throws Exception{
		Business business = new Business( emc );
		BBSPermissionInfoFactory permissionInfoFactory = business.permissionInfoFactory();
		BBSPermissionInfo permissionInfo = permissionInfoFactory.getPermissionByCode( permissionCode );
        if( permissionInfo == null ){
        	permissionInfo = new BBSPermissionInfo(  _bBSForumInfo.getId(), _bBSForumInfo.getForumName(), null, null, null, null, "论坛权限",  permissionName, permissionFunction, permissionCode,  description, _bBSForumInfo.getOrderNumber() );
        	emc.beginTransaction( BBSPermissionInfo.class );
        	emc.persist( permissionInfo, CheckPersistType.all );
        	emc.commit();
        }
	}
	
	/**
	 * 전달된 매개변수로 권한 정보를 구성하고 권한 존재 여부를 검증, 없으면 권한 정보 추가
	 * @param emc
	 * @param _sectionInfo
	 * @param permissionCode
	 * @param permissionName
	 * @param description
	 * @throws Exception
	 */
	private void checkAndSaveBBSPermissionInfo( EntityManagerContainer emc, BBSSectionInfo _sectionInfo, String permissionFunction, String permissionCode, String permissionName, String description ) throws Exception{
		Business business = new Business( emc );
		BBSPermissionInfoFactory permissionInfoFactory = business.permissionInfoFactory();
		BBSPermissionInfo permissionInfo = permissionInfoFactory.getPermissionByCode( permissionCode );
        if( permissionInfo == null ){
        	permissionInfo = new BBSPermissionInfo(  _sectionInfo.getForumId(), _sectionInfo.getForumName(), _sectionInfo.getId(), _sectionInfo.getSectionName(), _sectionInfo.getMainSectionId(), _sectionInfo.getMainSectionName(), 
        			"版块权限",  permissionName, permissionFunction, permissionCode,  description, _sectionInfo.getOrderNumber() );
        	emc.beginTransaction( BBSPermissionInfo.class );
        	emc.persist( permissionInfo, CheckPersistType.all );
        	emc.commit();
        }
	}
	
	public List<BBSPermissionInfo> filterPermissionListByPermissionFunction(String permissionFunction, List<BBSPermissionInfo> permissionList) throws Exception {
		if( permissionFunction  == null || permissionFunction.isEmpty() ){
			throw new Exception( "permissionFunction is null, return null!" );
		}
		List<BBSPermissionInfo> resultList = new ArrayList<BBSPermissionInfo>();
		if( ListTools.isNotEmpty( permissionList ) ){			
			for( BBSPermissionInfo permission : permissionList ){
				if( permissionFunction.equals( permission.getPermissionFunction() )){
					resultList.add( permission );
				}
			}
		}
		return resultList;
	}

	public List<BBSPermissionInfo> listPermissionByRoleCodes( List<String> roleCodes ) throws Exception {
		if( roleCodes  == null || roleCodes.isEmpty() ){
			throw new Exception( "roleIds is null, return null!" );
		}
		Business business = null;
		List<String> permissionCodes = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			permissionCodes = business.permissionRoleFactory().listPermissionCodesByRoleCodes( roleCodes );
			return business.permissionInfoFactory().listByPermissionCodes(permissionCodes);
		}catch( Exception e ){
			logger.warn( "system list permission by ids got an exception!" );
			throw e;
		}
	}
	
	public List<String> listPermissionCodesByRoleCodes( List<String> roleCodes ) throws Exception {
		if( roleCodes  == null || roleCodes.isEmpty() ){
			throw new Exception( "roleIds is null, return null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.permissionRoleFactory().listPermissionCodesByRoleCodes( roleCodes );
		}catch( Exception e ){
			logger.warn( "system list permission by ids got an exception!" );
			throw e;
		}
	}
	
	/**
	 * 사용자 이름으로 시스템이 사용자에게 할당한 모든 권한 목록 조회
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public List<BBSPermissionInfo> listAllPermissionForUser( String userName ) throws Exception {
		if( userName  == null || userName.isEmpty() ){
			throw new Exception( "userName is null, return null!" );
		}
		Business business = null;
		List<String> permissionCodes = null;
		List<String> roleCodes = null;
		List<String> groups = null;
		List<String> units = null;
		List<String> user_units = new ArrayList<>();
		List<String> uniqueIds = new ArrayList<String>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			
			//사용자 이름을 고유 식별자 목록에 추가
			uniqueIds.add( userName );
			
			//사용자 소속 조직 및 상위 조직 등 조회
			units = userManagerService.listUnitNamesWithPerson( userName );
			if( ListTools.isNotEmpty( units ) ){
				for( String unit : units ){
					user_units = listSuperUnit( unit, user_units );
				}
			}
			if( ListTools.isNotEmpty( user_units ) ){
				for( String unit : user_units ){
					if( uniqueIds.contains( unit ) ){
						uniqueIds.add( unit );
					}
				}
			}			
			//사용자 소속 그룹 조회
			groups = userManagerService.listGroupNamesSupNestedWithPerson( userName ); 
			if( ListTools.isNotEmpty( groups ) ){
				for( String group : groups){
					if( !uniqueIds.contains( group ) ){
						uniqueIds.add( group );
					}
				}
			}			
			//먼저 사용자에게 할당된 역할 목록 조회
			roleCodes = business.userRoleFactory().listRoleCodesByObjectUnique( uniqueIds );			
			if( ListTools.isNotEmpty( roleCodes ) ){
				//이어서 모든 역할에 포함된 권한 ID 목록 조회
				permissionCodes = business.permissionRoleFactory().listPermissionCodesByRoleCodes( roleCodes );
			}
			if( ListTools.isNotEmpty( permissionCodes ) ){
				//마지막으로 모든 권한 ID 목록으로 권한 객체 정보를 조회하여 반환
				return business.permissionInfoFactory().listByPermissionCodes( permissionCodes );
			}
			return null;
		}catch( Exception e ){
			logger.warn( "system list permission by username got an exception!" );
			throw e;
		}
	}
	
	/**
	 * 조직 객체와 상위 조직 객체를 조직 목록에 추가
	 * @param unit
	 * @param unitList
	 * @return
	 * @throws Exception
	 */
	public List<String> listSuperUnit( String unit,  List<String> unitList ) throws Exception {
		if( unitList == null ){
			unitList = new ArrayList<>();
		}
		if( unit != null ){
			//조직 정보를 조직 정보 목록에 추가
			unitList.add( unit );
			List<String> units = null;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				units = userManagerService.listSupUnitNameWithParent( unit );
				if( ListTools.isNotEmpty( units ) ){
					for( String unit_super : units ){
						unitList.add( unit_super );
					}
				}
			}catch( Exception e ){
				logger.warn( "system list sup nested by unit got an exception!" );
				throw e;
			}
		}
		return unitList;
	}

	public List<BBSPermissionInfo> listPermissionByCodes(List<String> permissionCodes) throws Exception {
		if( permissionCodes == null || permissionCodes.isEmpty() ){
			throw new Exception("permissionCodes is null, can not query any permission info!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.permissionInfoFactory().listByPermissionCodes(permissionCodes);
		}catch( Exception e ){
			logger.warn( "system list permission by codes got an exception!" );
			throw e;
		}
	}	
}