package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionForumCanNotCreateSection;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionForumIdEmpty;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionForumInfoNotExists;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionPersonNotExists;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionInfoProcess;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionInsufficientPermission;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionNameEmpty;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionNotExists;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionTypeCategoryInvalid;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Boolean check = true;
		List<String> names = null;
		String[] typeCategory = null;
		String personName = null;
		BBSForumInfo forumInfo = null;
		BBSSectionInfo sectionInfo_sub = null;
		BBSSectionInfo sectionInfo = new BBSSectionInfo();
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		Wi wrapIn = null;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionSectionInfoProcess( e, "JSON 정보를 객체로 변환하는 중 오류가 발생했습니다. JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		// 게시판 이름 검증
		if (check) {
			if (wrapIn.getSectionName() == null || wrapIn.getSectionName().isEmpty()) {
				check = false;
				Exception exception = new ExceptionSectionNameEmpty();
				result.error(exception);
			}
		}
		// 포럼 정보 유효성 검증 및 포럼 이름 보완
		if (check) {
			if (wrapIn.getForumId() == null || wrapIn.getForumId().isEmpty()) {
				check = false;
				Exception exception = new ExceptionForumIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				forumInfo = forumInfoServiceAdv.get(wrapIn.getForumId());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSectionInfoProcess(e,
						"ID로 BBS 포럼 분류 정보를 가져오는 중 오류가 발생했습니다! ID:" + wrapIn.getForumId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (forumInfo == null) {
				// 포럼 정보가 존재하지 않습니다
				check = false;
				Exception exception = new ExceptionForumInfoNotExists(wrapIn.getForumId());
				result.error(exception);
			} else {
				// 포럼 이름 보완
				wrapIn.setForumName(forumInfo.getForumName());
			}
		}
		
		// 포럼의 사용자 게시판 생성 허용 여부 판단
		if (check) {
			if (!forumInfo.getSectionCreateAble()) {
				check = false;
				Exception exception = new ExceptionForumCanNotCreateSection(forumInfo.getForumName());
				result.error(exception);
			}
		}
		
		// 사용자의 포럼 정보 추가·수정 권한 판단:
		// 1. 시스템 관리자 2. 포럼에 설정된 관리자
		if ( check ) {
			if ( !ThisApplication.isForumManager( effectivePerson, forumInfo ) ) {//게시판 삭제 권한이 없습니다
				check = false;
				String forumName = "없거나 삭제된 포럼";
				if( forumInfo != null ) {
					forumName = forumInfo.getForumName();
				}
				Exception exception = new ExceptionSectionInsufficientPermission( effectivePerson.getDistinguishedName(), forumName );
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if (wrapIn.getSubjectType() == null || wrapIn.getSubjectType().isEmpty()) {
				wrapIn.setSubjectType(forumInfo.getSubjectType());
			}
			if (wrapIn.getSubjectType() == null || wrapIn.getSubjectType().isEmpty()) {
				wrapIn.setSubjectType("뉴스|토론");
			}
		}
		if (check) {
			if ( StringUtils.isNotEmpty( wrapIn.getTypeCategory() )) {
				typeCategory = wrapIn.getTypeCategory().split("\\|");
				if (typeCategory != null && typeCategory.length > 0) {
					for (String category : typeCategory) {
						if (!"정보".equals(category) && !"질문".equals(category) && !"투표".equals(category)) {
							check = false;
							Exception exception = new ExceptionSectionTypeCategoryInvalid(category);
							result.error(exception);
						}
					}
				}
			} else {
				wrapIn.setTypeCategory(forumInfo.getTypeCategory());
			}
		}
		// 게시판 관리자(운영자) 존재 여부 검증
		if (check) {
			if ( ListTools.isNotEmpty( wrapIn.getModeratorNames() )) {
				// 지정 사용자 존재 여부 판단
				names = wrapIn.getModeratorNames();
				for (String name : names) {
					try {
						personName = userManagerService.getPersonNameByFlag( name );
						if ( personName == null ) {
							check = false;
							Exception exception = new ExceptionPersonNotExists(name);
							result.error(exception);
							break;
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionSectionInfoProcess(e, "사용자 고유 식별자로 사용자 정보를 조회하는 중 오류가 발생했습니다. Name:" + name);
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
						break;
					}
				}
			} else {
				wrapIn.addModeratorName( effectivePerson.getDistinguishedName() );
			}
		}
		if (check) {
			wrapIn.setCreatorName(effectivePerson.getDistinguishedName());
		}
		if (check) {
			try {
				sectionInfo = Wi.copier.copy( wrapIn );
				if( StringUtils.isNotEmpty( wrapIn.getId() )) {
					sectionInfo.setId( wrapIn.getId() );
				}
				if (StringUtils.isEmpty( sectionInfo.getId() )) {
					sectionInfo.setId(BBSSectionInfo.createId());
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSectionInfoProcess(e, "사용자가 전달한 정보를 섹션 정보 객체로 변환하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			// 상위 게시판 ID가 비어 있거나 현재 ID와 같으면 1단계 게시판, 즉 상위 게시판
			if (sectionInfo.getMainSectionId() == null || sectionInfo.getMainSectionId().isEmpty() || sectionInfo.getId().equals(sectionInfo.getMainSectionId())) {
				sectionInfo.setMainSectionId(sectionInfo.getId());
				sectionInfo.setMainSectionName(sectionInfo.getSectionName());
				sectionInfo.setSectionLevel("상위 섹션");
			} else {
				try {
					sectionInfo_sub = sectionInfoServiceAdv.get(sectionInfo.getMainSectionId());
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSectionInfoProcess(e, "지정한 상위 섹션 ID로 하위 섹션 정보를 조회하는 중 오류가 발생했습니다. MainId:" + sectionInfo.getMainSectionId());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
				if (check) {
					if (sectionInfo_sub != null) {
						sectionInfo.setMainSectionId(sectionInfo_sub.getId());
						sectionInfo.setMainSectionName(sectionInfo_sub.getSectionName());
						sectionInfo.setSectionLevel("하위 섹션");
					} else {
						check = false;
						Exception exception = new ExceptionSectionNotExists(sectionInfo.getMainSectionId());
						result.error(exception);
					}
				}
			}
		}

		if (check) {
			List<String> arrayList = new ArrayList<>(); 
			if( StringUtils.equals( wrapIn.getSectionVisible(), "권한에 따라" )) {
				if( StringUtils.isNotEmpty( wrapIn.getSectionVisibleResult() )) {
					arrayList.clear();
					Collections.addAll(arrayList, wrapIn.getSectionVisibleResult().split( "," ));
					sectionInfo.setVisiblePermissionList( arrayList );
				}
			}else {
				sectionInfo.setVisiblePermissionList( new ArrayList<>() );
			}
			
			if( StringUtils.equals( wrapIn.getReplyPublishAble(), "권한에 따라" )) {
				if( StringUtils.isNotEmpty( wrapIn.getReplyPublishResult() )) {
					arrayList.clear();
					Collections.addAll(arrayList, wrapIn.getReplyPublishResult().split( "," ));
					sectionInfo.setReplyPermissionList( arrayList );
				}
			}else {
				sectionInfo.setReplyPermissionList( new ArrayList<>() );
			}

			if( StringUtils.equals( wrapIn.getSubjectPublishAble(), "권한에 따라" )) {
				if( StringUtils.isNotEmpty( wrapIn.getSubjectPublishResult() )) {
					arrayList.clear();
					Collections.addAll(arrayList, wrapIn.getSubjectPublishResult().split( "," ));
					sectionInfo.setPublishPermissionList( arrayList );
				}
			}else {
				sectionInfo.setPublishPermissionList( new ArrayList<>() );
			}
		}
		
		if (check) {
			try {
				if( sectionInfo.getCreateTime() == null ) {
					sectionInfo.setCreateTime( new Date() );
				}
				if( sectionInfo.getUpdateTime() == null ) {
					sectionInfo.setUpdateTime( sectionInfo.getCreateTime() );
				}
				sectionInfo = sectionInfoServiceAdv.save( sectionInfo );				
				wo.setId( sectionInfo.getId() );

				CacheManager.notify( BBSForumInfo.class );
				CacheManager.notify( BBSSectionInfo.class );
				CacheManager.notify( BBSSubjectInfo.class );
				
				if ( sectionInfo.getCreateTime().compareTo( sectionInfo.getUpdateTime() ) == 0 ) {
					operationRecordService.sectionOperation(effectivePerson.getDistinguishedName(), sectionInfo, "CREATE", hostIp, hostName);
				} else {
					operationRecordService.sectionOperation(effectivePerson.getDistinguishedName(), sectionInfo, "MODIFY", hostIp, hostName);	
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSectionInfoProcess(e, "섹션 정보를 저장하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				// 포럼 정보 추가 성공, 권한과 역할 정보 계속 추가
				permissionInfoService.createSectionPermission(sectionInfo);
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system create forum permission info got an exception!");
				logger.error(e);

			}
		}

		if (check) {
			try {
				// 포럼 정보 추가 성공, 권한과 역할 정보 계속 추가
				roleInfoService.createSectionRole( effectivePerson.getDistinguishedName(), sectionInfo);
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system create section role info got an exception!");
				logger.error(e);
			}
		}
		if (check) {
			if (sectionInfo.getMainSectionId() != null
					&& !sectionInfo.getMainSectionId().equalsIgnoreCase(sectionInfo.getId())) {
				try {
					// 하위 게시판이면 상위 게시판 역할도 재설정 필요
					roleInfoService.createSectionRole( effectivePerson.getDistinguishedName(), sectionInfo.getMainSectionId());
				} catch (Exception e) {
					check = false;
					result.error(e);
					logger.warn("system create main section role info got an exception!");
					logger.error(e);
				}
			}
		}
		if (check) {
			try {
				// 게시판이 속한 포럼의 역할 재설정 필요
				roleInfoService.createForumRole( effectivePerson, sectionInfo.getForumId());
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system create forum role info got an exception!");
				logger.error(e);
			}
		}
		if (check) {// 게시판 관리자 권한 설정 확인
			try {
				sectionInfoServiceAdv.checkSectionManager(sectionInfo);
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system bind role for section manager got an exception!");
				logger.error(e);
			}
		}
		if (check) {
			if (sectionInfo.getMainSectionId() != null
					&& !sectionInfo.getMainSectionId().equalsIgnoreCase(sectionInfo.getId())) {
				try {
					sectionInfoServiceAdv.checkSectionManager(sectionInfo.getMainSectionId());
				} catch (Exception e) {
					check = false;
					result.error(e);
					logger.warn("system bind role for section manager got an exception!");
					logger.error(e);
				}
			}
		}
		if (check) {
			try {
				forumInfoServiceAdv.checkForumManager(forumInfo);
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system bind role for section manager got an exception!");
				logger.error(e);
			}
		}
		result.setData( wo );
		return result;
	}

	public static class Wi extends BBSSectionInfo{

		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier< Wi, BBSSectionInfo > copier = WrapCopierFactory.wi( Wi.class, BBSSectionInfo.class, null, JpaObject.FieldsUnmodify);
		
		@FieldDescribe("게시판 접근 권한 목록, 파라미터 수신용.")
		private String sectionVisibleResult ;
		
		@FieldDescribe("게시판 접근 권한 목록, 파라미터 수신용.")
		private String replyPublishResult ;
		
		@FieldDescribe("게시판 접근 권한 목록, 파라미터 수신용.")
		private String subjectPublishResult ;

		public String getSectionVisibleResult() {
			return sectionVisibleResult;
		}

		public void setSectionVisibleResult(String sectionVisibleResult) {
			this.sectionVisibleResult = sectionVisibleResult;
		}

		public String getReplyPublishResult() {
			return replyPublishResult;
		}

		public void setReplyPublishResult(String replyPublishResult) {
			this.replyPublishResult = replyPublishResult;
		}

		public String getSubjectPublishResult() {
			return subjectPublishResult;
		}

		public void setSubjectPublishResult(String subjectPublishResult) {
			this.subjectPublishResult = subjectPublishResult;
		}
	}

	
	public static class Wo extends WoId {

	}
}