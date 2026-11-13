package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionPermissionInfoProcess;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSectionNotExists;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSubjectIdEmpty;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSubjectNotExists;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionGetSubjectOperationPermissoin extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionGetSubjectOperationPermissoin.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String subjectId) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = new Wo();
		RoleAndPermission roleAndPermission = null;
		BBSSubjectInfo subjectInfo = null;
		BBSSectionInfo sectionInfo = null;
		Boolean hasPermission = false;
		Boolean publishAble = true;
		Boolean check = true;
		String checkUserPermission = null;

		if (check) {
			if (subjectId == null || subjectId.isEmpty()) {
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				subjectInfo = subjectInfoService.get(subjectId);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionPermissionInfoProcess(e, "지정한 ID로 주제 정보를 조회하는 중 오류가 발생했습니다. ID:" + subjectId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (subjectInfo == null) {
				check = false;
				Exception exception = new ExceptionSubjectNotExists(subjectId);
				result.error(exception);
			}
		}
		if (check) {
			try {
				sectionInfo = sectionInfoService.get(subjectInfo.getSectionId());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionPermissionInfoProcess(e,
						"지정한 ID로 섹션 정보를 조회하는 중 오류가 발생했습니다. ID:" + subjectInfo.getSectionId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check && publishAble) {
			if (sectionInfo == null) {
				check = false;
				Exception exception = new ExceptionSectionNotExists(subjectInfo.getSectionId());
				result.error(exception);
				// logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {// 사용자 권한 목록 조회
			if (!"anonymous".equalsIgnoreCase(effectivePerson.getTokenType().name())) {
				try {
					roleAndPermission = UserPermissionService.getUserRoleAndPermission(effectivePerson.getDistinguishedName());
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionPermissionInfoProcess(e,
							"사용자의 포럼 접근 권한 목록을 가져오는 중 오류가 발생했습니다. Person:" + effectivePerson.getDistinguishedName());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if (roleAndPermission == null) {
			roleAndPermission = new RoleAndPermission();
		}
		if (check) {
			if ("모든 사용자".equals(sectionInfo.getReplyPublishAble())) {
				wrap.setReplyPublishAble(true);
			} else {
				checkUserPermission = "SECTION_REPLY_PUBLISH_" + sectionInfo.getId();
				hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
				if (hasPermission) {
					wrap.setReplyPublishAble(true);
				}
			}
		}
		if (check) {
			checkUserPermission = "SECTION_SUBJECT_AUDIT_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setAuditAble(true);
			}
		}
		if (check) {
			checkUserPermission = "SECTION_SUBJECT_MANAGEMENT_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setManageAble(true);
				wrap.setEditAble(true);
			}
		}
		if (check) {
			checkUserPermission = "SECTION_SUBJECT_RECOMMEND_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setRecommendAble(true);
			}
		}
		if (check) {
			checkUserPermission = "SECTION_SUBJECT_STICK_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setStickAble(true);
			}
		}
		if (check) {
			checkUserPermission = "SECTION_SUBJECT_CREAM_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setCreamAble(true);
			}
		}
		if (check) {
			checkUserPermission = "SECTION_REPLY_AUDIT_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setReplyAuditAble(true);
			}
		}
		if (check) {
			checkUserPermission = "SECTION_REPLY_MANAGEMENT_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setReplyManageAble(true);
			}
		}
		if (check) {
			if (subjectInfo.getStopReply()) {
				wrap.setReplyPublishAble(false);
			}
			if (!"사용".equals(subjectInfo.getSubjectStatus())) {
				wrap.setReplyPublishAble(false);
			}
		}
		// 본인이 작성한 게시글은 본인이 삭제·편집 가능
		if (check) {
			if (effectivePerson.getDistinguishedName() != null
					&& subjectInfo.getCreatorName().equalsIgnoreCase(effectivePerson.getDistinguishedName())) {
				//게시글에 이미 답글이 있으면 삭제 불가
				if( replyInfoService.countWithSubjectForPage( subjectId, false ) == 0 ){
					wrap.setEditAble(true);
				}else{
					wrap.setEditAble(false);
				}
			}
		}
		
		//관리자 여부
		Business business = new Business(null);
		Boolean userBBSManager = business.organization().person().hasRole(effectivePerson, OrganizationDefinition.BBSManager);
		Boolean userManager = business.organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager);
		if(userBBSManager || userManager) {
			wrap.setReplyPublishAble(true);
			wrap.setAuditAble(true);
			wrap.setManageAble(true);
			wrap.setEditAble(true);
			wrap.setRecommendAble(true);
			wrap.setStickAble(true);
			wrap.setCreamAble(true);
			wrap.setReplyAuditAble(true);
		    wrap.setReplyManageAble(true);		
		 }

		result.setData(wrap);
		return result;
	}

	public static class Wo{
		
		@FieldDescribe( "사용자의 해당 주제 심사 가능 여부." )
		private Boolean auditAble = false;
		
		@FieldDescribe( "사용자의 해당 주제 편집 가능 여부." )
		private Boolean editAble = false;
		
		@FieldDescribe( "사용자의 해당 주제 관리 가능 여부." )
		private Boolean manageAble = false;	
		
		@FieldDescribe( "사용자의 해당 주제 추천 가능 여부." )
		private Boolean recommendAble = false;
		
		@FieldDescribe( "사용자의 해당 주제 상단 고정 가능 여부." )
		private Boolean stickAble = false;
		
		@FieldDescribe( "사용자의 해당 주제 우수 주제 설정 가능 여부." )
		private Boolean creamAble = false;
		
		@FieldDescribe( "사용자의 해당 주제 답글 작성 가능 여부." )
		private Boolean replyPublishAble = false;
		
		@FieldDescribe( "사용자의 게시판 내 답글 심사 가능 여부." )
		private Boolean replyAuditAble = false;	
		
		@FieldDescribe( "사용자의 주제 내 답글 조회 또는 삭제 가능 여부." )
		private Boolean replyManageAble = false;		

		public Boolean getEditAble() {
			return editAble;
		}

		public void setEditAble(Boolean editAble) {
			this.editAble = editAble;
		}

		public Boolean getAuditAble() {
			return auditAble;
		}

		public void setAuditAble(Boolean auditAble) {
			this.auditAble = auditAble;
		}

		public Boolean getManageAble() {
			return manageAble;
		}

		public void setManageAble(Boolean manageAble) {
			this.manageAble = manageAble;
		}

		public Boolean getRecommendAble() {
			return recommendAble;
		}

		public void setRecommendAble(Boolean recommendAble) {
			this.recommendAble = recommendAble;
		}

		public Boolean getStickAble() {
			return stickAble;
		}

		public void setStickAble(Boolean stickAble) {
			this.stickAble = stickAble;
		}

		public Boolean getCreamAble() {
			return creamAble;
		}

		public void setCreamAble(Boolean creamAble) {
			this.creamAble = creamAble;
		}

		public Boolean getReplyPublishAble() {
			return replyPublishAble;
		}

		public void setReplyPublishAble(Boolean replyPublishAble) {
			this.replyPublishAble = replyPublishAble;
		}

		public Boolean getReplyManageAble() {
			return replyManageAble;
		}

		public void setReplyManageAble(Boolean replyManageAble) {
			this.replyManageAble = replyManageAble;
		}

		public Boolean getReplyAuditAble() {
			return replyAuditAble;
		}

		public void setReplyAuditAble(Boolean replyAuditAble) {
			this.replyAuditAble = replyAuditAble;
		}
		
	}
}