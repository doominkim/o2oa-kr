package com.x.bbs.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.common.date.DateOperation;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSPermissionRole;
import com.x.bbs.entity.BBSReplyInfo;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectContent;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.bbs.entity.BBSUserRole;

/**
 * 포럼 정보 관리 서비스 클래스
 * @author LIYI
 *
 */
public class BBSSectionInfoServiceAdv {
	private DateOperation dateOperation = new DateOperation();
	private BBSSectionInfoService sectionInfoService = new BBSSectionInfoService();
	
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
	public BBSSectionInfo getMainSectionBySectionName( String mainSectionName ) throws Exception {
		if( mainSectionName  == null || mainSectionName.isEmpty() ){
			throw new Exception( "mainSectionName is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.getMainSectionBySectionName( emc, mainSectionName);
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 데이터베이스에 BBSSectionInfo 객체 저장
	 * @param _bBSSectionInfo
	 */
	public BBSSectionInfo save( BBSSectionInfo _bBSSectionInfo ) throws Exception {
		if( _bBSSectionInfo  == null ){
			throw new Exception( "_bBSSectionInfo is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.save( emc, _bBSSectionInfo);
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * ID로 데이터베이스에서 BBSSectionInfo 객체 삭제
	 * @param sectionId
	 * @throws Exception
	 */
	public void delete( String sectionId ) throws Exception {
		
		if( StringUtils.isEmpty( sectionId ) ){
			throw new Exception( "sectionId can not null!" );
		}
		Business business = null;
		BBSForumInfo forumInfo = null;
		BBSSectionInfo sectionInfo = null;
		List<String> ids = null;
		List<BBSRoleInfo> roleInfoList = null;
		List<BBSUserRole> userRoleList = null;
		List<BBSPermissionRole> permissionRoleList = null;
		List<BBSPermissionInfo> permissionInfoList = null;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			
			//해당 게시판과 관련된 모든 것 삭제, 주로 권한이며 게시글과 게시판은 이미 삭제됨
			emc.beginTransaction( BBSForumInfo.class );
			emc.beginTransaction( BBSSectionInfo.class );
			emc.beginTransaction( BBSUserRole.class );
			emc.beginTransaction( BBSPermissionInfo.class );
			emc.beginTransaction( BBSPermissionRole.class );
			emc.beginTransaction( BBSRoleInfo.class );
			
			//포럼의 모든 사용자-역할 연결 정보 삭제
			ids = business.userRoleFactory().listBySectionId( sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				userRoleList = business.userRoleFactory().list( ids );
				if( ListTools.isNotEmpty( userRoleList )) {
					for( BBSUserRole userRole : userRoleList ) {
						emc.remove( userRole, CheckRemoveType.all );
					}
				}
			}
			
			//권한-역할 연결 정보 삭제
			ids = business.permissionRoleFactory().listBySectionId( sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				permissionRoleList = business.permissionRoleFactory().list( ids );
				if( ListTools.isNotEmpty( permissionRoleList )) {
					for( BBSPermissionRole permissionRole : permissionRoleList ) {
						emc.remove( permissionRole, CheckRemoveType.all );
					}
				}
			}
			
			//포럼의 모든 역할 정보 삭제
			ids = business.roleInfoFactory().listRoleBySectionId( sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				roleInfoList = business.roleInfoFactory().list( ids );
				if( ListTools.isNotEmpty( roleInfoList )) {
					for( BBSRoleInfo roleInfo : roleInfoList ) {
						emc.remove( roleInfo, CheckRemoveType.all );
					}
				}
			}
			
			//모든 권한 정보 삭제
			ids = business.permissionInfoFactory().listPermissionBySectionId(sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				permissionInfoList = business.permissionInfoFactory().list( ids );
				if( ListTools.isNotEmpty( permissionInfoList )) {
					for( BBSPermissionInfo permissionInfo : permissionInfoList ) {
						emc.remove( permissionInfo, CheckRemoveType.all );
					}
				}
			}
			
			sectionInfo = emc.find( sectionId, BBSSectionInfo.class );
			if( sectionInfo != null ) {
				//포럼 정보에서 해당 게시판 차감
				forumInfo = emc.find( sectionInfo.getForumId(), BBSForumInfo.class );
				if( forumInfo != null ) {
					forumInfo.minusSection(1L);
				}
				emc.check( forumInfo, CheckPersistType.all );
				emc.remove( sectionInfo, CheckRemoveType.all );
			}
			emc.commit();
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 게시판 또는 상위 게시판 강제 삭제
	 * 1. 상위 게시판이면 그 아래의 모든 하위 게시판 삭제
	 * 2. 하위 게시판, 주제, 답글, 권한, 역할 등 정보 삭제
	 * @param sectionId
	 * @throws Exception 
	 */
	public void deleteForce( String sectionId ) throws Exception {
		if( StringUtils.isEmpty( sectionId ) ){
			throw new Exception( "sectionId can not null!" );
		}
		Business business = null;
		BBSForumInfo forumInfo = null;
		BBSSectionInfo sectionInfo = null;
		BBSSubjectContent subjectContent = null;
		List<String> ids = null;
		List<String> subSectionIds = null;
		List<BBSSectionInfo> sectionList = null;
		List<BBSSubjectInfo> subjectList = null;
		List<BBSSubjectAttachment> attachmentList = null;
		List<BBSReplyInfo> replyList = null;
		List<BBSRoleInfo> roleInfoList = null;
		List<BBSUserRole> userRoleList = null;
		List<BBSPermissionRole> permissionRoleList = null;
		List<BBSPermissionInfo> permissionInfoList = null;
		Long subSectionTotal = 0L;
		Long subjectTotal = 0L;
		Long subjectTotalToday = 0L;
		Long replyTotal = 0L;
		Long replyTotalToday = 0L;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			emc.beginTransaction( BBSForumInfo.class );
			emc.beginTransaction( BBSSectionInfo.class );
			emc.beginTransaction( BBSSubjectInfo.class );
			emc.beginTransaction( BBSSubjectContent.class );
			emc.beginTransaction( BBSSubjectAttachment.class );
			emc.beginTransaction( BBSReplyInfo.class );
			emc.beginTransaction( BBSUserRole.class );
			emc.beginTransaction( BBSPermissionInfo.class );
			emc.beginTransaction( BBSPermissionRole.class );
			emc.beginTransaction( BBSRoleInfo.class );
			
			sectionInfo = emc.find( sectionId, BBSSectionInfo.class );
			if( sectionInfo != null ) {
				subSectionTotal = 1L;
				forumInfo = emc.find( sectionInfo.getForumId(), BBSForumInfo.class );
				
				//하위 게시판 존재 여부 조회, 개수 조회 필요
				subSectionIds = business.sectionInfoFactory().listSubSectionIdsByMainSectionId( sectionId );
				if( ListTools.isNotEmpty( subSectionIds ) ) {
					subSectionTotal += subSectionIds.size();
					sectionList = business.sectionInfoFactory().list(subSectionIds);
				}
				//모든 하위 게시판 정보 삭제
				if( ListTools.isNotEmpty( sectionList )) {
					for( BBSSectionInfo subSectionInfo : sectionList ) {
						emc.remove( subSectionInfo, CheckRemoveType.all );
					}
				}
				//게시판 정보 삭제
				emc.remove( sectionInfo, CheckRemoveType.all );
			}

			//오늘의 모든 주제글 수 조회
			subjectTotalToday = business.subjectInfoFactory().countSubjectByDate( sectionId, dateOperation.getTodayStartTime(), dateOperation.getTodayEndTime(), true );
			if( subjectTotalToday == null ) {
				subjectTotalToday = 0L;
			}
			
			//해당 게시판의 모든 주제글 삭제
			ids = business.subjectInfoFactory().listIdsByMainAndSubSectionId( sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				subjectTotal = Long.parseLong( ids.size() + "" );
				subjectList = business.subjectInfoFactory().list( ids );
				if( ListTools.isNotEmpty( subjectList )) {
					for( BBSSubjectInfo subjectInfo : subjectList ) {
						//상세 내용 삭제
						subjectContent = emc.find( subjectInfo.getId(), BBSSubjectContent.class );
						if( subjectContent != null ) {
							emc.remove( subjectContent, CheckRemoveType.all );
						}
						emc.remove( subjectInfo, CheckRemoveType.all );
					}
				}
			}
			
			//해당 게시판의 모든 주제글 첨부파일 정보 삭제
			ids = business.subjectAttachmentFactory().listBySectionId( sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				attachmentList = business.subjectAttachmentFactory().list( ids );
				if( ListTools.isNotEmpty( attachmentList )) {
					StorageMapping mapping = null;
					for( BBSSubjectAttachment attachment : attachmentList ) {
						mapping = ThisApplication.context().storageMappings().get( BBSSubjectAttachment.class, attachment.getStorage());
						attachment.deleteContent(mapping);
						emc.remove( attachment, CheckRemoveType.all );
					}
				}
			}
			
			//오늘의 모든 답글 수 조회
			replyTotalToday = business.replyInfoFactory().countReplyByDate( sectionId, dateOperation.getTodayStartTime(), dateOperation.getTodayEndTime(), true );
			if( replyTotalToday == null ) {
				replyTotalToday = 0L;
			}
			
			//해당 게시판의 모든 답글 삭제
			ids = business.replyInfoFactory().listIdsByMainAndSubSectionId( sectionId );
			if( ListTools.isNotEmpty( ids )) {
				replyTotal = Long.parseLong(ids.size() + "");
				replyList = business.replyInfoFactory().list( ids );
				if( ListTools.isNotEmpty( replyList )) {
					for( BBSReplyInfo replyInfo : replyList ) {
						emc.remove( replyInfo, CheckRemoveType.all );
					}
				}
			}
			
			//포럼의 모든 사용자-역할 연결 정보 삭제(상위 게시판 포함)
			ids = business.userRoleFactory().listBySectionId( sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				userRoleList = business.userRoleFactory().list( ids );
				if( ListTools.isNotEmpty( userRoleList )) {
					for( BBSUserRole userRole : userRoleList ) {
						emc.remove( userRole, CheckRemoveType.all );
					}
				}
			}
			
			//권한-역할 연결 정보 삭제(상위 게시판 포함)
			ids = business.permissionRoleFactory().listBySectionId( sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				permissionRoleList = business.permissionRoleFactory().list( ids );
				if( ListTools.isNotEmpty( permissionRoleList )) {
					for( BBSPermissionRole permissionRole : permissionRoleList ) {
						emc.remove( permissionRole, CheckRemoveType.all );
					}
				}
			}
			
			//포럼의 모든 역할 정보 삭제(상위 게시판 포함)
			ids = business.roleInfoFactory().listRoleBySectionId( sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				roleInfoList = business.roleInfoFactory().list( ids );
				if( ListTools.isNotEmpty( roleInfoList )) {
					for( BBSRoleInfo roleInfo : roleInfoList ) {
						emc.remove( roleInfo, CheckRemoveType.all );
					}
				}
			}
			
			//모든 권한 정보 삭제(상위 게시판 포함)
			ids = business.permissionInfoFactory().listPermissionBySectionId(sectionId, true );
			if( ListTools.isNotEmpty( ids )) {
				permissionInfoList = business.permissionInfoFactory().list( ids );
				if( ListTools.isNotEmpty( permissionInfoList )) {
					for( BBSPermissionInfo permissionInfo : permissionInfoList ) {
						emc.remove( permissionInfo, CheckRemoveType.all );
					}
				}
			}
			
			if( forumInfo != null ) {
				//포럼 정보에서 해당 게시판 수 및 관련 주제, 답글 수 차감
				forumInfo.minusSection( subSectionTotal );
				forumInfo.minusSubjectTotal( subjectTotal );
				forumInfo.minusSubjectTotalToday( subjectTotalToday );
				forumInfo.minusReplyTotal( replyTotal );
				forumInfo.minusReplyTotalToday( replyTotalToday );
				
				emc.check( forumInfo, CheckPersistType.all );
			}
			emc.commit();
		}catch( Exception e ){
			throw e;
		}
	}
	
	public List<BBSSectionInfo> listAll() throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.listAll( emc );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 포럼 ID로 포럼의 모든 상위 게시판 정보 수 조회
	 * @param forumId
	 * @return
	 * @throws Exception 
	 */
	public Long countMainSectionByForumId( String forumId ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.countMainSectionByForumId( emc, forumId);
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 상위 게시판 ID로 상위 게시판의 모든 하위 게시판 정보 수 조회
	 * @param sectionId
	 * @return
	 * @throws Exception 
	 */
	public Long countSubSectionByMainSectionId( String sectionId ) throws Exception {
		if( sectionId  == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.countSubSectionByMainSectionId( emc, sectionId);
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 포럼 ID로 포럼의 모든 게시판 정보 조회
	 * @param forumId
	 * @return
	 * @throws Exception 
	 */
	public List<BBSSectionInfo> listMainSectionByForumId( String forumId ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.listMainSectionByForumId( emc, forumId);
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 포럼 ID로 포럼의 모든 게시판 정보 조회
	 * @param forumId
	 * @return
	 * @throws Exception 
	 */
	public List<BBSSectionInfo> viewMainSectionByForumId( String forumId, List<String> viewableSectionIds ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.viewMainSectionByForumId( emc, forumId, viewableSectionIds );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 상위 게시판 ID로 모든 하위 게시판 정보 목록 조회
	 * @param sectionId
	 * @return
	 * @throws Exception 
	 */
	public List<BBSSectionInfo> viewSubSectionByMainSectionId( String sectionId, List<String> viewableSectionIds  ) throws Exception {
		if( sectionId  == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.viewSubSectionByMainSectionId( emc, sectionId, viewableSectionIds );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 상위 게시판 ID로 모든 하위 게시판 정보 목록 조회
	 * @param sectionId
	 * @return
	 * @throws Exception 
	 */
	public List<BBSSectionInfo> listSubSectionByMainSectionId( String sectionId  ) throws Exception {
		if( sectionId  == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.listSubSectionByMainSectionId( emc, sectionId );
		}catch( Exception e ){
			throw e;
		}
	}

	public void checkSectionManager( String sectionId ) throws Exception {
		if( sectionId  == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			sectionInfoService.checkSectionManager( emc, sectionId );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 게시판 관리자의 권한 및 역할 설정 확인
	 * @param sectionInfo
	 * @throws Exception 
	 */
	public void checkSectionManager( BBSSectionInfo sectionInfo ) throws Exception {
		if( sectionInfo == null ){
			throw new Exception( "sectionInfo is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			sectionInfoService.checkSectionManager( emc, sectionInfo );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 사용자 권한과 포럼 ID로 모든 상위 게시판 정보 ID 목록 조회
	 * @param forumId
	 * @param sectionIds 
	 * @return
	 * @throws Exception 
	 */
	public List<BBSSectionInfo> listAllViewAbleMainSectionWithUserPermission( String forumId, List<String> sectionIds ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.listAllViewAbleMainSectionWithUserPermission( emc, forumId, sectionIds );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 
	 * @param viewforumIds
	 * @param publicStatus 공개 여부
	 * @return
	 * @throws Exception
	 */
	public List<String> viewSectionByForumIds( List<String> viewforumIds, Boolean publicStatus ) throws Exception {
		if( viewforumIds  == null || viewforumIds.isEmpty() ){
			return null;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.viewSectionByForumIds( emc, viewforumIds, publicStatus );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<BBSSectionInfo> list(List<String> publicSectionIds) throws Exception {
		if( publicSectionIds  == null || publicSectionIds.isEmpty() ){
			return null;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return sectionInfoService.list( emc, publicSectionIds );
		}catch( Exception e ){
			throw e;
		}
	}	
}