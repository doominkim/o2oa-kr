package com.x.bbs.assemble.control.service;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.assemble.control.factory.BBSPermissionInfoFactory;
import com.x.bbs.assemble.control.factory.BBSPermissionRoleFactory;
import com.x.bbs.assemble.control.factory.BBSRoleInfoFactory;
import com.x.bbs.assemble.control.jaxrs.roleinfo.bean.BindObject;
import com.x.bbs.entity.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 포럼 역할 정보 관리 서비스 클래스
 *
 * 포럼 역할
 * 포럼 방문자(FORUM_GUEST): 포럼 방문자
 * 		0. 포럼 보기(FORUM_VIEW): 사용자가 BBS 시스템에서 해당 포럼에 접근 가능
 *
 * 포럼 관리자(FORUM_SUPER_MANAGER): 포럼 관리자, 해당 포럼 관리의 최대 권한 보유
 *      0. 포럼 보기(FORUM_VIEW): 사용자가 BBS 시스템에서 해당 포럼에 접근 가능
 * 		1. 포럼 정보 관리(FORUM_INFO_MANAGEMENT): 사용자가 포럼의 게시판 추가, 삭제, 수정 권한 보유
 * 		1. 포럼 주제 게시(FORUM_SUBJECT_PUBLISH): 사용자가 포럼의 모든 게시판에 주제 게시 가능
 * 		2. 포럼 답글 작성(FORUM_REPLY_PUBLISH): 사용자가 포럼의 모든 주제에 답글 작성 가능
 * 		3. 포럼 주제 추천(FORUM_SUBJECT_RECOMMEND): 사용자가 지정한 포럼의 모든 게시판에서 지정한 주제를 포럼 첫 페이지에 추천 가능
 * 		4. 포럼 주제 상단 고정(FORUM_SUBJECT_STICK): 사용자가 포럼의 모든 주제에 대한 상단 고정 권한 보유
 * 		5. 포럼 주제 우수글 지정(FORUM_SUBJECT_CREAM): 사용자가 포럼의 모든 주제에 대한 우수글 설정 권한 보유
 * 		6. 포럼 주제 관리(FORUM_SUBJECT_MANAGEMENT): 사용자가 포럼의 모든 주제에 대한 잠금·삭제 권한 보유
 * 		7. 포럼 답글 관리(FORUM_REPLY_MANAGEMENT): 사용자가 포럼의 모든 답글에 대한 삭제 권한 보유
 * 		8. 포럼 권한 관리(FORUM_PERMISSION_MANAGEMENT): 사용자가 포럼 사용자에 대해 해당 포럼 권한을 설정할 수 있는 권한 보유
 * 		9. 포럼 설정 관리(FORUM_CONFIG_MANAGEMENT): 사용자가 포럼 매개변수 설정 권한 보유
 *
 *
 * 게시판 역할
 * 게시판 방문자(SECTION_GUEST): 게시판 방문자
 * 		0. 포럼 보기(SECTION_VIEW): 사용자가 BBS 시스템에서 해당 게시판에 접근 가능
 *
 * 게시판 관리자(SECTION_MANAGER): 게시판 및 게시판 콘텐츠 관리의 최대 권한 보유
 *      0. 포럼 보기(SECTION_VIEW): 사용자가 BBS 시스템에서 해당 게시판에 접근 가능
 * 		1. 주제 게시(SECTION_SUBJECT_PUBLISH): 사용자가 지정한 게시판에 주제 게시 가능
 *		2. 주제 심사(SECTION_SUBJECT_AUDIT): 주제에 심사가 필요한 경우 사용자가 지정한 게시판에 게시된 주제를 심사 가능
 * 		3. 주제 관리(SECTION_SUBJECT_MANAGEMENT): 사용자가 지정한 게시판에서 게시된 주제를 조회·삭제 가능
 * 		4. 답글 작성(SECTION_REPLY_PUBLISH): 사용자가 지정한 게시판의 모든 주제에 답글 작성 가능
 *		5. 답글 심사(SECTION_REPLY_AUDIT): 답글에 심사가 필요한 경우 사용자가 지정한 게시판의 모든 답글 내용을 심사 가능
 *		6. 답글 관리(SECTION_REPLY_MANAGEMENT): 사용자가 지정한 게시판에서 답글을 조회 또는 삭제 가능
 * 		7. 게시판 주제 추천(SECTION_SUBJECT_RECOMMEND): 사용자가 지정한 게시판에서 지정한 주제를 추천 가능
 * 		8. 게시판 주제 상단 고정(SECTION_SUBJECT_STICK): 사용자가 지정한 게시판에서 지정한 주제를 상단 고정 가능
 * 		9. 게시판 주제 우수글 지정(SECTION_SUBJECT_CREAM): 사용자가 지정한 게시판에서 지정한 주제를 우수글로 설정 가능
 * 		10. 게시판 권한 관리(SECTION_PERMISSION_MANAGEMENT): 사용자가 포럼 사용자에 대해 지정한 게시판의 권한 관리 가능
 * 		11. 게시판 설정 관리(SECTION_CONFIG_MANAGEMENT): 사용자가 지정한 게시판의 시스템 매개변수 설정 수정 가능
 *
 * 주제 게시자(SECTION_SUBJECT_PUBLISHER): 지정한 게시판에 주제 게시 허용
 * 		0. 포럼 보기(SECTION_VIEW): 사용자가 BBS 시스템에서 해당 게시판에 접근 가능
 * 		1. 주제 게시(SECTION_SUBJECT_PUBLISH): 사용자가 지정한 게시판에 주제 게시 가능
 *
 * 주제 답글 작성자(SECTION_REPLY_PUBLISHER): 지정한 게시판에 주제 게시 허용
 * 		0. 포럼 보기(SECTION_VIEW): 사용자가 BBS 시스템에서 해당 게시판에 접근 가능
 * 		1. 답글 작성(SECTION_REPLY_PUBLISH): 사용자가 지정한 게시판의 모든 주제에 답글 작성 가능
 *
 * 주제 추천자(SECTION_RECOMMENDER): 지정한 게시판에 주제 게시 허용
 * 		0. 포럼 보기(SECTION_VIEW): 사용자가 BBS 시스템에서 해당 게시판에 접근 가능
 * 		1. 게시판 주제 추천(SECTION_SUBJECT_RECOMMEND): 사용자가 지정한 게시판에서 지정한 주제를 추천 가능
 *
 * 주제 심사자(SECTION_SUBJECT_AUDITOR): 지정한 게시판에 주제 게시 허용
 * 		0. 포럼 보기(SECTION_VIEW): 사용자가 BBS 시스템에서 해당 게시판에 접근 가능
 * 		1. 주제 심사(SECTION_SUBJECT_AUDIT): 주제에 심사가 필요한 경우 사용자가 지정한 게시판에 게시된 주제를 심사 가능
 *
 * 답글 심사자(SECTION_REPLY_AUDITOR): 지정한 게시판에 주제 게시 허용
 * 		0. 포럼 보기(SECTION_VIEW): 사용자가 BBS 시스템에서 해당 게시판에 접근 가능
 * 		1. 답글 심사(SECTION_REPLY_AUDIT): 답글에 심사가 필요한 경우 사용자가 지정한 게시판의 모든 답글 내용을 심사 가능 *
 *
 * @author LIYI
 *
 */
public class BBSRoleInfoService {

	private static  Logger logger = LoggerFactory.getLogger( BBSRoleInfoService.class );
	private UserManagerService userManagerService = new UserManagerService();
	/**
	 * ID로 지정한 역할 정보 객체 조회
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public BBSRoleInfo get( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null, can not query any role info!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.roleInfoFactory().get( id );
		}catch( Exception e ){
			logger.warn( "system get role by id got an exception!" );
			throw e;
		}
	}

	/**
	 * 모든 역할 정보 조회
	 *
	 * @return
	 * @throws Exception
	 */
	public List<BBSRoleInfo> listAllRoleInfo( ) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.roleInfoFactory().listAll();
		}catch( Exception e ){
			logger.warn( "system list all role got an exception!" );
			throw e;
		}
	}

	/**
	 * 지정한 ID 목록으로 역할 정보 조회
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<BBSRoleInfo> listRoleInfoByIds( List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			throw new Exception("ids is null, can not query any role info!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.roleInfoFactory().list(ids);
		}catch( Exception e ){
			logger.warn( "system list role by ids got an exception!" );
			throw e;
		}
	}

	public void createForumRole( EffectivePerson effectivePerson, String forumId ) throws Exception {
		BBSForumInfo forumInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			forumInfo = emc.find( forumId, BBSForumInfo.class );
		}catch( Exception e ){
			logger.warn( "system get forum by id got an exception!" );
			throw e;
		}
		if( forumInfo != null ){
			createForumRole( effectivePerson, forumInfo );
		}
	}

	/**
	 * 지정한 포럼의 역할 객체 확인 및 생성
	 * @param _bBSForumInfo
	 * @throws Exception
	 */
	public void createForumRole( EffectivePerson effectivePerson, BBSForumInfo _bBSForumInfo ) throws Exception {
		if( _bBSForumInfo == null ){
			throw new Exception( "forum info is null, can not create any role info!" );
		}
		String forumId = _bBSForumInfo.getId();
		String forumName = _bBSForumInfo.getForumName();
		List<BBSSectionInfo> sectionInfoList = null;
		List<String> permissionCodeList = new ArrayList<String>();
		String sectionId = null;
		Business business = null;
		String personName = null;
		if( effectivePerson == null ) {
			personName = "System";
		}else {
			personName = effectivePerson.getDistinguishedName();
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			/**
			 * 포럼 역할 정보 관리 서비스 클래스
			 *
			 * 포럼 역할
			 * 포럼 방문자(FORUM_GUEST): 포럼 방문자
			 * 		0. 포럼 보기(FORUM_VIEW): 사용자가 BBS 시스템에서 해당 포럼에 접근 가능
			 *
			 * 포럼 관리자(FORUM_SUPER_MANAGER): 포럼 관리자, 해당 포럼 관리의 최대 권한 보유
			 *      0. 포럼 보기(FORUM_VIEW): 사용자가 BBS 시스템에서 해당 포럼에 접근 가능
			 * 		1. 포럼 정보 관리(FORUM_INFO_MANAGEMENT): 사용자가 포럼의 게시판 추가, 삭제, 수정 권한 보유
			 * 		1. 포럼 주제 게시(FORUM_SUBJECT_PUBLISH): 사용자가 포럼의 모든 게시판에 주제 게시 가능
			 * 		2. 포럼 답글 작성(FORUM_REPLY_PUBLISH): 사용자가 포럼의 모든 주제에 답글 작성 가능
			 * 		3. 포럼 주제 추천(FORUM_SUBJECT_RECOMMEND): 사용자가 지정한 포럼의 모든 게시판에서 지정한 주제를 포럼 첫 페이지에 추천 가능
			 * 		4. 포럼 주제 상단 고정(FORUM_SUBJECT_STICK): 사용자가 포럼의 모든 주제에 대한 상단 고정 권한 보유
			 * 		5. 포럼 주제 우수글 지정(FORUM_SUBJECT_CREAM): 사용자가 포럼의 모든 주제에 대한 우수글 설정 권한 보유
			 * 		6. 포럼 주제 관리(FORUM_SUBJECT_MANAGEMENT): 사용자가 포럼의 모든 주제에 대한 잠금·삭제 권한 보유
			 * 		7. 포럼 답글 관리(FORUM_REPLY_MANAGEMENT): 사용자가 포럼의 모든 답글에 대한 삭제 권한 보유
			 * 		8. 포럼 권한 관리(FORUM_PERMISSION_MANAGEMENT): 사용자가 포럼 사용자에 대해 해당 포럼 권한을 설정할 수 있는 권한 보유
			 * 		9. 포럼 설정 관리(FORUM_CONFIG_MANAGEMENT): 사용자가 포럼 매개변수 설정 권한 보유
			 */
			permissionCodeList.clear();
			permissionCodeList.add( "FORUM_VIEW_" + forumId );
			checkAndSaveBBSRoleInfo( emc, personName,
					_bBSForumInfo, "FORUM_GUEST_" + forumId, forumName + "-포럼 방문자", "시스템 역할: 포럼["+ forumName +"]이(가) 지정된 범위의 사용자에게 공개됩니다.",
					permissionCodeList );

			//해당 포럼의 게시판 수 조회
			sectionInfoList = business.sectionInfoFactory().listAllSectionByForumId( _bBSForumInfo.getId() );

			//포럼 최고 관리자(FORUM_SUPER_MANAGER): 삭제, 심사, 답글, 상단 고정, 우수글, 추천 등
			permissionCodeList.clear();
			permissionCodeList.add( "FORUM_VIEW_" + forumId );
			permissionCodeList.add( "FORUM_INFO_MANAGEMENT_" + forumId );
//			permissionCodeList.add( "FORUM_SUBJECT_PUBLISH_" + forumId );
//			permissionCodeList.add( "FORUM_REPLY_PUBLISH_" + forumId );
//			permissionCodeList.add( "FORUM_SUBJECT_RECOMMEND_" + forumId );
//			permissionCodeList.add( "FORUM_SUBJECT_STICK_" + forumId );
//			permissionCodeList.add( "FORUM_SUBJECT_CREAM_" + forumId );
//			permissionCodeList.add( "FORUM_SUBJECT_MANAGEMENT_" + forumId );
//			permissionCodeList.add( "FORUM_REPLY_MANAGEMENT_" + forumId );
			permissionCodeList.add( "FORUM_PERMISSION_MANAGEMENT_" + forumId );
			permissionCodeList.add( "FORUM_CONFIG_MANAGEMENT_" + forumId );
			//해당 포럼의 모든 게시판 관련 권한을 모두 부여
			if( sectionInfoList != null ){
				for( BBSSectionInfo section : sectionInfoList ){
					sectionId = section.getId();
					permissionCodeList.add( "SECTION_VIEW_" + sectionId );
					permissionCodeList.add( "SECTION_SUBJECT_PUBLISH_" + sectionId );
					permissionCodeList.add( "SECTION_SUBJECT_AUDIT_" + sectionId );
					permissionCodeList.add( "SECTION_REPLY_PUBLISH_" + sectionId );
					permissionCodeList.add( "SECTION_REPLY_AUDIT_" + sectionId );
					permissionCodeList.add( "SECTION_SUBJECT_RECOMMEND_" + sectionId );
					permissionCodeList.add( "SECTION_SUBJECT_STICK_" + sectionId );
					permissionCodeList.add( "SECTION_SUBJECT_CREAM_" + sectionId );
					permissionCodeList.add( "SECTION_SUBJECT_MANAGEMENT_" + sectionId );
					permissionCodeList.add( "SECTION_REPLY_MANAGEMENT_" + sectionId );
					permissionCodeList.add( "SECTION_INFO_MANAGER_" + sectionId );
					permissionCodeList.add( "SECTION_PERMISSION_MANAGEMENT_" + sectionId );
					permissionCodeList.add( "SECTION_CONFIG_MANAGEMENT_" + sectionId );
				}
			}
			checkAndSaveBBSRoleInfo( emc, personName, _bBSForumInfo, "FORUM_SUPER_MANAGER_" + forumId,
					forumName + "-포럼 최고 관리자", "시스템 역할: 포럼["+ forumName +"]에서 최고 관리 권한을 가집니다.",
					permissionCodeList );
		}catch( Exception e ){
			logger.warn( "system check and create forum role got an exception!" );
			throw e;
		}
	}

	public void createSectionRole( String creatorName, String sectionId ) throws Exception {
		BBSSectionInfo sectionInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			sectionInfo = emc.find( sectionId, BBSSectionInfo.class );
			if( sectionInfo != null ){
				createSectionRole( creatorName, sectionInfo );
			}
		}catch( Exception e ){
			logger.warn( "system get section by id got an exception!" );
			throw e;
		}
	}
	/**
	 * 지정 게시판의 권한 객체 확인 및 생성
	 * @param _sectionInfo
	 * @throws Exception
	 */
	public void createSectionRole( String creatorName, BBSSectionInfo _sectionInfo ) throws Exception {
		if( _sectionInfo == null ){
			throw new Exception("section info is null, can not create any permission info!");
		}
		String forumName = _sectionInfo.getForumName();
		String sectionId = _sectionInfo.getId();
		String sectionId_tmp = null;
		String sectionName = _sectionInfo.getSectionName();
		List<BBSSectionInfo> sectionInfoList = null;
		List<String> permissionCodeList = new ArrayList<String>();
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			//해당 포럼의 게시판 수 조회
			sectionInfoList = business.sectionInfoFactory().listSubSectionByMainSectionId( _sectionInfo.getId() );

			permissionCodeList.clear();
			permissionCodeList.add( "SECTION_VIEW_" + sectionId );
			if( sectionInfoList != null ){
				for( BBSSectionInfo section : sectionInfoList ){
					sectionId_tmp = section.getId();
					permissionCodeList.add( "SECTION_VIEW_" + sectionId_tmp );
				}
			}
			checkAndSaveBBSRoleInfo( emc, creatorName,  _sectionInfo, "SECTION_GUEST_" + sectionId, forumName + "-" + sectionName + "-섹션 방문", "시스템 역할: 섹션[" + forumName + "-"+ sectionName +"]이(가) 지정된 범위의 사용자에게 공개되며, 게시와 댓글 작성이 가능합니다.", permissionCodeList );

			//게시판 관리자(SECTION_MANAGER): 게시판 및 게시판 콘텐츠 관리의 최대 권한 보유
			permissionCodeList.clear();
			permissionCodeList.add( "SECTION_VIEW_" + sectionId );
			permissionCodeList.add( "SECTION_INFO_MANAGER_" + sectionId );
			permissionCodeList.add( "SECTION_SUBJECT_PUBLISH_" + sectionId );
			permissionCodeList.add( "SECTION_SUBJECT_AUDIT_" + sectionId );
			permissionCodeList.add( "SECTION_SUBJECT_MANAGEMENT_" + sectionId );
			permissionCodeList.add( "SECTION_REPLY_PUBLISH_" + sectionId );
			permissionCodeList.add( "SECTION_REPLY_AUDIT_" + sectionId );
			permissionCodeList.add( "SECTION_REPLY_MANAGEMENT_" + sectionId );
			permissionCodeList.add( "SECTION_SUBJECT_RECOMMEND_" + sectionId );
			permissionCodeList.add( "SECTION_SUBJECT_STICK_" + sectionId );
			permissionCodeList.add( "SECTION_SUBJECT_CREAM_" + sectionId );
			permissionCodeList.add( "SECTION_PERMISSION_MANAGEMENT_" + sectionId );
			permissionCodeList.add( "SECTION_CONFIG_MANAGEMENT_" + sectionId );
			//해당 게시판의 모든 하위 게시판 관련 권한을 모두 부여
			if( sectionInfoList != null ){
				for( BBSSectionInfo section : sectionInfoList ){
					sectionId_tmp = section.getId();
					permissionCodeList.add( "SECTION_VIEW_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_INFO_MANAGER_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_SUBJECT_PUBLISH_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_SUBJECT_AUDIT_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_SUBJECT_MANAGEMENT_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_REPLY_PUBLISH_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_REPLY_AUDIT_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_REPLY_MANAGEMENT_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_SUBJECT_RECOMMEND_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_SUBJECT_STICK_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_SUBJECT_CREAM_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_PERMISSION_MANAGEMENT_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_CONFIG_MANAGEMENT_" + sectionId_tmp );
				}
			}
			checkAndSaveBBSRoleInfo( emc, creatorName, _sectionInfo, "SECTION_MANAGER_" + sectionId, forumName + "-" + sectionName + "-섹션 관리자", "시스템 역할: 섹션[" + forumName + "-"+ sectionName +"]과(와) 그 콘텐츠에 대한 최고 관리 권한을 가집니다.", permissionCodeList );

			permissionCodeList.clear();
			permissionCodeList.add( "SECTION_VIEW_" + sectionId );
			permissionCodeList.add( "SECTION_SUBJECT_PUBLISH_" + sectionId );
			if( sectionInfoList != null ){
				for( BBSSectionInfo section : sectionInfoList ){
					sectionId_tmp = section.getId();
					permissionCodeList.add( "SECTION_VIEW_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_SUBJECT_PUBLISH_" + sectionId_tmp );
				}
			}
			checkAndSaveBBSRoleInfo( emc, creatorName, _sectionInfo, "SECTION_SUBJECT_PUBLISHER_" + sectionId, forumName + "-" + sectionName + "-주제 게시자", "시스템 역할: 섹션[" + forumName + "-"+ sectionName +"]에 주제를 게시할 수 있습니다.", permissionCodeList );

			permissionCodeList.clear();
			permissionCodeList.add( "SECTION_VIEW_" + sectionId );
			permissionCodeList.add( "SECTION_REPLY_PUBLISH_" + sectionId );
			if( sectionInfoList != null ){
				for( BBSSectionInfo section : sectionInfoList ){
					sectionId_tmp = section.getId();
					permissionCodeList.add( "SECTION_VIEW_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_REPLY_PUBLISH_" + sectionId_tmp );
				}
			}
			checkAndSaveBBSRoleInfo( emc, creatorName, _sectionInfo, "SECTION_REPLY_PUBLISHER_" + sectionId, forumName + "-" + sectionName + "-댓글 작성자", "시스템 역할: 섹션[" + forumName + "-"+ sectionName +"]에 댓글을 작성할 수 있습니다.", permissionCodeList );

			permissionCodeList.clear();
			permissionCodeList.add( "SECTION_VIEW_" + sectionId );
			permissionCodeList.add( "SECTION_SUBJECT_RECOMMEND_" + sectionId );
			if( sectionInfoList != null ){
				for( BBSSectionInfo section : sectionInfoList ){
					sectionId_tmp = section.getId();
					permissionCodeList.add( "SECTION_VIEW_" + sectionId_tmp );
					permissionCodeList.add( "SECTION_SUBJECT_RECOMMEND_" + sectionId_tmp );
				}
			}
			checkAndSaveBBSRoleInfo( emc, creatorName, _sectionInfo, "SECTION_RECOMMENDER_" + sectionId, forumName + "-" + sectionName + "-주제 추천자", "시스템 역할: 섹션[" + forumName + "-"+ sectionName +"]의 주제를 홈에 추천할 수 있습니다.", permissionCodeList );

			permissionCodeList.clear();
			permissionCodeList.add( "SECTION_VIEW_" + sectionId );
			permissionCodeList.add( "SECTION_SUBJECT_AUDIT_" + sectionId );
			if( sectionInfoList != null ){
				for( BBSSectionInfo section : sectionInfoList ){
					sectionId_tmp = section.getId();
					permissionCodeList.add( "SECTION_VIEW_" + sectionId );
					permissionCodeList.add( "SECTION_SUBJECT_AUDIT_" + sectionId_tmp );
				}
			}
			checkAndSaveBBSRoleInfo( emc, creatorName, _sectionInfo, "SECTION_SUBJECT_AUDITOR_" + sectionId, forumName + "-" + sectionName + "-주제 검토자", "시스템 역할: 섹션[" + forumName + "-"+ sectionName +"]에 게시된 주제를 검토할 수 있습니다.", permissionCodeList );

			permissionCodeList.clear();
			permissionCodeList.add( "SECTION_VIEW_" + sectionId );
			permissionCodeList.add( "SECTION_REPLY_AUDIT_" + sectionId );
			if( sectionInfoList != null ){
				for( BBSSectionInfo section : sectionInfoList ){
					sectionId_tmp = section.getId();
					permissionCodeList.add( "SECTION_VIEW_" + sectionId );
					permissionCodeList.add( "SECTION_REPLY_AUDIT_" + sectionId_tmp );
				}
			}
			checkAndSaveBBSRoleInfo( emc, creatorName, _sectionInfo, "SECTION_REPLY_AUDITOR_" + sectionId, forumName + "-" + sectionName + "-댓글 검토자", "시스템 역할: 섹션[" + forumName + "-"+ sectionName +"]의 댓글을 검토할 수 있습니다.", permissionCodeList );

		}catch( Exception e ){
			logger.warn( "system check and create section role got an exception!" );
			throw e;
		}
	}

	/**
	 * 전달된 매개변수로 역할 정보를 구성하고 역할 존재 여부를 확인, 없으면 역할 정보를 새로 추가
	 * @param emc
	 * @param _bBSForumInfo
	 * @param roleCode
	 * @param roleName
	 * @param description
	 * @throws Exception
	 */
	private void checkAndSaveBBSRoleInfo( EntityManagerContainer emc, String creatorName, BBSForumInfo _bBSForumInfo, String roleCode, String roleName, String description, List<String> permissionCodeList ) throws Exception{
		Business business = new Business( emc );
		BBSPermissionInfo permissionInfo  = null;
		BBSPermissionRole permissionRole = null;
		BBSRoleInfoFactory roleInfoFactory = business.roleInfoFactory();
		BBSPermissionInfoFactory permissionInfoFactory = business.permissionInfoFactory();
		BBSPermissionRoleFactory permissionRoleFactory  = business.permissionRoleFactory();
		BBSRoleInfo roleInfo = roleInfoFactory.getRoleByCode( roleCode );
        if( roleInfo == null ){
        	roleInfo = new BBSRoleInfo( creatorName,  _bBSForumInfo.getId(), _bBSForumInfo.getForumName(), null, null, null, null,
        			"포럼 역할", roleName, roleCode, description, _bBSForumInfo.getOrderNumber() );
        	emc.beginTransaction( BBSRoleInfo.class );
        	emc.persist( roleInfo, CheckPersistType.all );
        	if( ListTools.isNotEmpty( permissionCodeList ) ){
        		for( String permissionCode : permissionCodeList ){
        			//권한 존재 여부 조회
        			permissionInfo = permissionInfoFactory.getPermissionByCode( permissionCode );
        			if( permissionInfo == null ){
        				continue;
        			}
        			//역할과 권한의 바인딩 관계 존재 여부를 조회하고 없으면 데이터 추가, 이 작업은 역할을 새로 추가할 때 한 번만 수행
        			if( !permissionRoleFactory.exsistPermissionRole( roleCode, permissionCode ) ){
        				emc.beginTransaction( BBSPermissionRole.class );
        				permissionRole = new BBSPermissionRole( roleInfo.getForumId(), roleInfo.getForumName(), roleInfo.getSectionId(),
        						roleInfo.getSectionName(), roleInfo.getMainSectionId(), roleInfo.getMainSectionName(), permissionInfo.getPermissionType(),
        						permissionInfo.getPermissionName(), permissionInfo.getPermissionCode(), roleInfo.getId(), roleName, roleCode, description, _bBSForumInfo.getOrderNumber() );
        				emc.persist( permissionRole, CheckPersistType.all );
        			}
        		}
        	}
        	emc.commit();
        }
	}

	/**
	 * 전달된 매개변수로 권한 정보를 구성하고 권한 존재 여부를 검증, 없으면 권한 정보 추가
	 * @param emc
	 * @param _sectionInfo
	 * @param roleCode
	 * @param roleName
	 * @param description
	 * @throws Exception
	 */
	private void checkAndSaveBBSRoleInfo( EntityManagerContainer emc, String creatorName, BBSSectionInfo _sectionInfo, String roleCode, String roleName,
			String description, List<String> permissionCodeList ) throws Exception{
		Business business = new Business( emc );
		BBSPermissionInfo permissionInfo  = null;
		BBSPermissionRole permissionRole = null;
		BBSRoleInfoFactory roleInfoFactory = business.roleInfoFactory();
		BBSPermissionInfoFactory permissionInfoFactory = business.permissionInfoFactory();
		BBSPermissionRoleFactory permissionRoleFactory  = business.permissionRoleFactory();
		BBSRoleInfo roleInfo = roleInfoFactory.getRoleByCode( roleCode );
        if( roleInfo == null ){
        	roleInfo = new BBSRoleInfo( creatorName, _sectionInfo.getForumId(), _sectionInfo.getForumName(), _sectionInfo.getId(), _sectionInfo.getSectionName(), _sectionInfo.getMainSectionId(), _sectionInfo.getMainSectionName(),
        			"섹션 역할", roleName, roleCode, description, _sectionInfo.getOrderNumber() );
        	emc.beginTransaction( BBSRoleInfo.class );
        	emc.persist( roleInfo, CheckPersistType.all );
        	if( ListTools.isNotEmpty( permissionCodeList ) ){
        		for( String permissionCode : permissionCodeList ){
        			//권한 존재 여부 조회
        			permissionInfo = permissionInfoFactory.getPermissionByCode( permissionCode );
        			if( permissionInfo == null ){
        				continue;
        			}
        			//역할과 권한의 바인딩 관계 존재 여부를 조회하고 없으면 데이터 추가, 이 작업은 역할을 새로 추가할 때 한 번만 수행
        			if( !permissionRoleFactory.exsistPermissionRole( roleCode, permissionCode ) ){
        				emc.beginTransaction( BBSPermissionRole.class );
        				permissionRole = new BBSPermissionRole( roleInfo.getForumId(), roleInfo.getForumName(), roleInfo.getSectionId(),
        						roleInfo.getSectionName(), roleInfo.getMainSectionId(), roleInfo.getMainSectionName(), permissionInfo.getPermissionType(),
        						permissionInfo.getPermissionName(), permissionInfo.getPermissionCode(), roleInfo.getId(), roleName, roleCode, description, _sectionInfo.getOrderNumber() );
        				emc.persist( permissionRole, CheckPersistType.all );
        			}
        		}
        	}
        	emc.commit();
        }
	}

	public List<BBSRoleInfo> listRoleByForumId( String forumId ) throws Exception {
		if( forumId == null ){
			throw new Exception("forumId is null, can not query any role info!");
		}
		Business business = null;
		List<String> ids = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.roleInfoFactory().listRoleByForumId( forumId );
			return business.roleInfoFactory().list(ids);
		}catch( Exception e ){
			logger.warn( "system list role by forum id got an exception!" );
			throw e;
		}
	}

	public List<BBSRoleInfo> listRoleBySectionId( String sectionId ) throws Exception {
		if( sectionId == null ){
			throw new Exception("sectionId is null, can not query any role info!");
		}
		Business business = null;
		List<String> ids = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.roleInfoFactory().listRoleBySectionId( sectionId, false );
			return business.roleInfoFactory().list(ids);
		}catch( Exception e ){
			logger.warn( "system list role by section id got an exception!" );
			throw e;
		}
	}

	/**
	 * 객체 유형과 객체 고유 식별자로 객체에 연결된 역할 정보 조회
	 *
	 * @param uniqueId
	 * @param objectType
	 * @return
	 * @throws Exception
	 */
	public List<BBSRoleInfo> listRoleByObjectUniqueId( String uniqueId, String objectType ) throws Exception {
		if( uniqueId == null ){
			throw new Exception("uniqueId is null, can not query any role info!");
		}
		if( objectType == null ){
			throw new Exception("objectType is null, can not query any role info!");
		}
		Business business = null;
		List<String> ids = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.userRoleFactory().listRoleIdsByObjectUniqueId( uniqueId, objectType );
			return business.roleInfoFactory().list(ids);
		}catch( Exception e ){
			logger.warn( "system list role by object unique id got an exception!" );
			throw e;
		}
	}

	/**
	 * 역할 코드로 역할 정보 조회
	 * @param roleCode
	 * @return
	 * @throws Exception
	 */
	public BBSRoleInfo getByRoleCode( String roleCode ) throws Exception {
		if( roleCode == null ){
			throw new Exception("roleCode is null, can not query any role info!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.roleInfoFactory().getRoleByCode( roleCode );
		}catch( Exception e ){
			logger.warn( "system get role by role code got an exception!" );
			throw e;
		}
	}

	/**
	 * 역할 정보 저장
	 * 역할에 바인딩된 권한 정보도 함께 저장
	 * 1. 바인딩 권한 목록에 없는 유효하지 않은 권한 정보 삭제
	 * 2. 역할 정보 추가 또는 갱신
	 * 3. 필요한 역할 권한 바인딩 정보 확인 및 추가
	 * @param _roleInfo
	 * @param permissionCodes
	 * @return
	 * @throws Exception
	 */
	public BBSRoleInfo save( BBSRoleInfo _roleInfo, List<String> permissionCodes ) throws Exception {
		BBSRoleInfo _roleInfo_tmp = null;
		Business business = null;
		BBSPermissionRole permissionRole = null;
		List<String> ids = null;
		List<BBSPermissionInfo> permissionInfoList = null;
		List<BBSPermissionRole> permissionRoleList = null;
		Boolean needDelete = false;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			emc.beginTransaction( BBSRoleInfo.class );
			emc.beginTransaction( BBSPermissionInfo.class );
			emc.beginTransaction( BBSPermissionRole.class );

			//=========================================  1. 바인딩 권한 목록에 없는 유효하지 않은 권한 정보 삭제
			//역할 코드로 모든 권한 정보 목록 조회
			ids = business.permissionRoleFactory().listPermissionByRoleCode( _roleInfo.getRoleCode() );
			//불필요한 바인딩 관계 먼저 삭제
			if( ListTools.isNotEmpty( ids ) ){
				permissionRoleList = business.permissionRoleFactory().list( ids );
				if( ListTools.isNotEmpty( permissionRoleList ) ){
					for( BBSPermissionRole _permissionRole : permissionRoleList ){
						needDelete = true;
						for( String permissionCode : permissionCodes ){
							if( permissionCode.equals( _permissionRole.getPermissionCode() )){
								needDelete = false;
							}
						}
						if( needDelete ){
							emc.remove( _permissionRole, CheckRemoveType.all );
						}
					}
				}
			}

			//=========================================  2. 역할 정보 추가 또는 갱신
			_roleInfo_tmp = business.roleInfoFactory().getRoleByCode( _roleInfo.getRoleCode() );
			if( _roleInfo_tmp == null ){
				//새 레코드 생성
				if( _roleInfo.getId() == null || _roleInfo.getId().isEmpty() ){
					_roleInfo.setId( BBSRoleInfo.createId() );
				}
				emc.persist( _roleInfo, CheckPersistType.all);
			}else{
				//레코드 수정
				_roleInfo.copyTo( _roleInfo_tmp, JpaObject.FieldsUnmodify  );
				emc.check( _roleInfo_tmp, CheckPersistType.all );
			}

			//=========================================  3. 필요한 역할 권한 바인딩 정보 확인 및 추가
			//권한 ID로 모든 권한 정보 목록 조회
			permissionInfoList = business.permissionInfoFactory().listByPermissionCodes( permissionCodes );
			if( ListTools.isNotEmpty( permissionInfoList ) ){
				for( BBSPermissionInfo permissionInfo : permissionInfoList ){
					//지정한 역할과 권한의 바인딩 관계 조회
					permissionRole = business.permissionRoleFactory().getByRoleAndPermission( _roleInfo.getRoleCode(), permissionInfo.getPermissionCode() );
					if( permissionRole == null ){
						permissionRole = new BBSPermissionRole( _roleInfo.getForumId(), _roleInfo.getForumName(), _roleInfo.getSectionId(), _roleInfo.getSectionName(),
								_roleInfo.getMainSectionId(), _roleInfo.getMainSectionName(), permissionInfo.getPermissionType(), permissionInfo.getPermissionName(),
								permissionInfo.getPermissionCode(), _roleInfo.getId(), _roleInfo.getRoleName(), _roleInfo.getRoleCode(), _roleInfo.getDescription(), _roleInfo.getOrderNumber() );
						emc.persist( permissionRole, CheckPersistType.all);
					}
				}
			}

			emc.commit();
		}catch( Exception e ){
			logger.warn( "system find BBSRoleInfo{'id':'"+_roleInfo.getId()+"'} got an exception!" );
			throw e;
		}
		return _roleInfo;
	}

	public void delete( String id ) throws Exception {
		BBSRoleInfo roleInfo = null;
		List<String> ids = null;
		List<BBSPermissionRole> permissionRoleList = null;
		List<BBSUserRole> uerRoleList = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );

			emc.beginTransaction( BBSRoleInfo.class );
			emc.beginTransaction( BBSPermissionRole.class );
			emc.beginTransaction( BBSUserRole.class );

			roleInfo = emc.find( id, BBSRoleInfo.class );
			if( roleInfo == null ){
				throw new Exception("role info not exists, can not excute delete.");
			}
			//역할 정보가 존재하면 역할 정보, 역할-권한 바인딩, 역할-사용자·조직·그룹 바인딩 삭제
			ids = business.permissionRoleFactory().listPermissionByRoleCode( roleInfo.getRoleCode() );
			if( ids != null ){
				permissionRoleList = business.permissionRoleFactory().list(ids);
				if( ListTools.isNotEmpty( permissionRoleList ) ){
					//전체 삭제
					for( BBSPermissionRole permissionRole : permissionRoleList ){
						emc.remove( permissionRole, CheckRemoveType.all );
					}
				}
			}
			ids = business.userRoleFactory().listIdsByRoleCode( roleInfo.getRoleCode() );
			if( ids != null ){
				uerRoleList = business.userRoleFactory().list(ids);
				if( ListTools.isNotEmpty( uerRoleList ) ){
					//전체 삭제
					for( BBSUserRole userRole : uerRoleList ){
						emc.remove( userRole, CheckRemoveType.all );
					}
				}
			}
			emc.remove( roleInfo, CheckRemoveType.all );
			emc.commit();
		}catch( Exception e ){
			logger.warn( "system delete BBSRoleInfo{'id':'"+ id +"'} got an exception!" );
			throw e;
		}
	}

	public void bindRoleToUser( BindObject bindObject, List<String> bindRoleCodes ) throws Exception {
		if( bindObject == null ){
			throw new Exception("bindObject is null, can not bind role!");
		}
		List<String> ids = null;
		BBSUserRole userRole = null;
		BBSRoleInfo roleInfo = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			emc.beginTransaction( BBSUserRole.class );
			//해당 사용자의 모든 역할 바인딩 먼저 삭제
			ids = business.userRoleFactory().listRoleIdsByObjectUniqueId( bindObject.getObjectName(), bindObject.getObjectType() );
			for( String id : ids ){
				userRole = emc.find( id, BBSUserRole.class );
				if( userRole != null ){
					emc.remove( userRole, CheckRemoveType.all );
				}
			}
			if( ListTools.isNotEmpty( bindRoleCodes ) ){
				for( String bindRoleCode : bindRoleCodes ){
					roleInfo = business.roleInfoFactory().getRoleByCode( bindRoleCode );
					if( roleInfo != null ){
						userRole = new BBSUserRole();
						userRole.setForumId( roleInfo.getForumId() );
						userRole.setMainSectionId( roleInfo.getMainSectionId() );
						userRole.setSectionId( roleInfo.getSectionId() );
						userRole.setObjectName( bindObject.getObjectName() );
						userRole.setObjectType( bindObject.getObjectType() );
						userRole.setRoleCode( roleInfo.getRoleCode() );
						userRole.setRoleId( roleInfo.getId() );
						userRole.setRoleName( roleInfo.getRoleName() );
						userRole.setUniqueId( bindObject.getObjectName() );
						userRole.setOrderNumber( userRole.getOrderNumber() );
						emc.persist( userRole, CheckPersistType.all );
					}
				}
			}
			emc.commit();
		}catch( Exception e ){
			logger.warn( "system bindRoleToUser got an exception!" );
			throw e;
		}
	}

	public void bindUserToRole( String bindRoleCode, List<BindObject> bindObjects ) throws Exception {
		if( bindRoleCode == null || bindRoleCode.isEmpty() ){
			throw new Exception("bindRoleCode is null, can not query any role info!");
		}
		List<String> ids = null;
		BBSUserRole userRole = null;
		BBSRoleInfo roleInfo = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			emc.beginTransaction( BBSUserRole.class );
			roleInfo = business.roleInfoFactory().getRoleByCode( bindRoleCode );
			if( roleInfo == null ){
				throw new Exception( "roleInfo not exists!bindRoleCode = " + bindRoleCode );
			}

			//해당 사용자의 모든 역할 바인딩 먼저 삭제
			ids = business.userRoleFactory().listIdsByRoleCode( bindRoleCode );
			for( String id : ids ){
				userRole = emc.find( id, BBSUserRole.class );
				if( userRole != null ){
					emc.remove( userRole, CheckRemoveType.all );
				}
			}

			if( ListTools.isNotEmpty( bindObjects )){
				String unitName = null;
				String topUnitName = null;
				for( BindObject bindObject : bindObjects ){
					userRole = new BBSUserRole();
					userRole.setForumId( roleInfo.getForumId() );
					userRole.setMainSectionId( roleInfo.getMainSectionId() );
					userRole.setSectionId( roleInfo.getSectionId() );
					userRole.setObjectName( bindObject.getObjectName() );
					userRole.setObjectType( bindObject.getObjectType() );
					userRole.setRoleCode( roleInfo.getRoleCode() );
					userRole.setRoleId( roleInfo.getId() );
					userRole.setRoleName( roleInfo.getRoleName() );
					userRole.setOrderNumber( userRole.getOrderNumber() );
					userRole.setUniqueId( bindObject.getObjectName() );
					if(OrganizationDefinition.isPersonDistinguishedName(bindObject.getObjectName())) {
						try {
							unitName = userManagerService.getUnitNameWithPerson(bindObject.getObjectName());
						} catch (Exception e) {
							logger.warn("user has no identity!user:" + bindObject.getObjectName());
							unitName = "알 수 없는 조직";
						}
						try {
							topUnitName = userManagerService.getTopUnitNameWithPerson(bindObject.getObjectName());
						} catch (Exception e) {
							logger.warn("user has no identity!user:" + bindObject.getObjectName());
							topUnitName = "알 수 없는 회사";
						}
					}else if(OrganizationDefinition.isUnitDistinguishedName(bindObject.getObjectName())){
						unitName = bindObject.getObjectName();
					}

					userRole.setTopUnitName( topUnitName );
					userRole.setUnitName( unitName );
					emc.persist( userRole, CheckPersistType.all );
				}
			}
			emc.commit();
		}catch( Exception e ){
			logger.warn( "system bindUserToRole got an exception!" );
			throw e;
		}
	}

	/**
	 * 사용자 이름으로 사용자의 모든 역할 정보 목록 조회
	 * 1. 사용자가 속한 조직 및 상위 조직 조회
	 * 2. 사용자가 속한 최상위 조직 조회
	 * 3. 사용자가 속한 모든 그룹 조회
	 * 사용자의 모든 조직, 최상위 조직, 그룹에 연결된 모든 역할 목록 조회
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public List<BBSRoleInfo> listAllRoleForUser( String userName ) throws Exception {
		if( userName == null ){
			throw new Exception("userName is null, can not query any role info!");
		}
		//1. 사용자가 속한 조직 정보 조회, 상위 조직 정보는 재귀 조회 필요
		Business business = null;
		List<String> objectUniqueIds = new ArrayList<>();
		List<String> unitNameList = new ArrayList<>();
		List<String> ids = null;
		List<String> groupNameList = null;

		objectUniqueIds.add( userName );

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);

			unitNameList = userManagerService.listUnitNamesWithPerson( userName );
			groupNameList = userManagerService.listGroupNamesSupNestedWithPerson(userName);

			//그 다음 조직 이름, 그룹 이름을 같은 LIST에 넣어 조회에 사용
			if( ListTools.isNotEmpty( unitNameList ) ){
				for( String unitName : unitNameList ){
					objectUniqueIds.add( unitName );
				}
			}
			if( ListTools.isNotEmpty( groupNameList ) ){
				for( String groupName : groupNameList ){
					objectUniqueIds.add( groupName );
				}
			}
			ids = business.userRoleFactory().listRoleIdsByObjectUnique( objectUniqueIds );

			return business.roleInfoFactory().list(ids);
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listAllRoleCodesForUser(String userName) throws Exception {
		if( userName == null ){
			throw new Exception("userName is null, can not query any role info!");
		}
		//1. 사용자가 속한 조직 정보 조회, 상위 조직 정보는 재귀 조회 필요
		Business business = null;
		List<String> objectUniqueIds = new ArrayList<>();
		List<String> unitNameList = new ArrayList<>();
		List<String> groupNameList = null;

		objectUniqueIds.add( userName );

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);

			unitNameList = userManagerService.listUnitSupNestedWithPerson( userName );
			groupNameList = userManagerService.listGroupNamesSupNestedWithPerson(userName);

			//그 다음 조직 이름, 그룹 이름을 같은 LIST에 넣어 조회에 사용
			if( ListTools.isNotEmpty( unitNameList ) ){
				for( String unitName : unitNameList ){
					objectUniqueIds.add( unitName );
				}
			}
			if( ListTools.isNotEmpty( groupNameList ) ){
				for( String groupName : groupNameList ){
					objectUniqueIds.add( groupName );
				}
			}
			return  business.userRoleFactory().listRoleCodesByObjectUnique( objectUniqueIds );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<BBSUserRole> listUserRoleByRoleCode(String roleCode) throws Exception {
		Business business = null;
		List<String> ids = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.userRoleFactory().listIdsByRoleCode(roleCode);
			if( ListTools.isNotEmpty( ids )){
				return business.userRoleFactory().list(ids);
			}else{
				return null;
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

}
