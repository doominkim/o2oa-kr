package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSectionNotExists;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;
import com.x.bbs.entity.BBSSectionInfo;

public class ActionGetSectionOperationPermission extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionGetSectionOperationPermission.class);

	protected ActionResult<Wo> execute(HttpServletRequest request,
			EffectivePerson effectivePerson, String sectionId) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = new Wo();
		RoleAndPermission roleAndPermission = null;
		BBSSectionInfo sectionInfo = sectionInfoService.get(sectionId);
		Boolean hasPermission = false;
		String checkUserPermission;
		if (sectionInfo == null) {
			throw  new ExceptionSectionNotExists(sectionId);
		}
		if (!effectivePerson.isAnonymous()) {
			roleAndPermission = UserPermissionService.getUserRoleAndPermission(effectivePerson.getDistinguishedName());
		}
		if (roleAndPermission == null) {
			roleAndPermission = new RoleAndPermission();
		}
		// 주제 게시 권한 판단
		wrap.setSubjectPublishAble(sectionInfoService.hasPublishPermission(sectionInfo, effectivePerson, roleAndPermission.getPermissionInfoList()));

		// 게시판 주제 답글 권한 판단
		if ("모든 사용자".equals(sectionInfo.getReplyPublishAble())) {
			wrap.setReplyPublishAble(true);
		} else {// 권한 판단
			checkUserPermission = "SECTION_REPLY_PUBLISH_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setReplyPublishAble(true);
			}
		}

		checkUserPermission = "SECTION_SUBJECT_AUDIT_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setSubjectAuditAble(true);
		}

		checkUserPermission = "SECTION_SUBJECT_MANAGEMENT_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setSubjectManageAble(true);
		}

		checkUserPermission = "SECTION_SUBJECT_RECOMMEND_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setSubjectRecommendAble(true);
		}

		checkUserPermission = "SECTION_SUBJECT_STICK_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setSubjectStickAble(true);
		}

		checkUserPermission = "SECTION_SUBJECT_CREAM_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setSubjectCreamAble(true);
		}

		checkUserPermission = "SECTION_REPLY_AUDIT_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setReplyAuditAble(true);
		}

		checkUserPermission = "SECTION_REPLY_MANAGEMENT_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setReplyManageAble(true);
		}

		checkUserPermission = "SECTION_SECTION_MANAGER_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setSectionManageAble(true);
		}

		checkUserPermission = "SECTION_PERMISSION_MANAGEMENT_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setSectionPermissoinManageAble(true);
		}

		checkUserPermission = "SECTION_CONFIG_MANAGEMENT_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setSectionConfigManageAble(true);
		}

		if (effectivePerson.getDistinguishedName() != null
				&& sectionInfo.getCreatorName().equalsIgnoreCase(effectivePerson.getDistinguishedName())) {
			wrap.setSectionManageAble(true);
			wrap.setSectionPermissoinManageAble(true);
			wrap.setSectionConfigManageAble(true);
		}
		//관리자 여부
		Business business = new Business(null);
		Boolean userBBSManager = business.isManager(effectivePerson);
		if(userBBSManager) {
			wrap.setSubjectPublishAble(true);
			wrap.setReplyPublishAble(true);
			wrap.setReplyAuditAble(true);
			wrap.setSubjectAuditAble(true);
			wrap.setSubjectManageAble(true);
			wrap.setSubjectRecommendAble(true);
			wrap.setSubjectStickAble(true);
			wrap.setSubjectCreamAble(true);
			wrap.setReplyManageAble(true);

			wrap.setSectionManageAble(true);
			wrap.setSectionPermissoinManageAble(true);
			wrap.setSectionConfigManageAble(true);
		}

		result.setData(wrap);
		return result;
	}

	public static class Wo{

		@FieldDescribe( "대응 권한 SECTION_SUBJECT_PUBLISH: 사용자가 게시판에 주제를 게시할 수 있는지 여부." )
		private Boolean subjectPublishAble = false;

		@FieldDescribe( "대응 권한 SECTION_SUBJECT_AUDIT: 사용자가 게시판에 게시된 주제를 심사할 수 있는지 여부." )
		private Boolean subjectAuditAble = false;

		@FieldDescribe( "대응 권한 SECTION_SUBJECT_MANAGEMENT: 사용자가 게시판에서 게시된 주제에 삭제 등 관리 작업을 할 수 있는지 여부." )
		private Boolean subjectManageAble = false;

		@FieldDescribe( "대응 권한 SECTION_SUBJECT_RECOMMEND: 사용자가 게시판에서 주제를 추천할 수 있는지 여부." )
		private Boolean subjectRecommendAble = false;

		@FieldDescribe( "대응 권한 SECTION_SUBJECT_STICK: 사용자가 게시판에서 주제를 상단 고정할 수 있는지 여부." )
		private Boolean subjectStickAble = false;

		@FieldDescribe( "대응 권한 SECTION_SUBJECT_CREAM: 사용자가 게시판에서 지정 주제를 우수 주제로 설정할 수 있는지 여부." )
		private Boolean subjectCreamAble = false;

		@FieldDescribe( "대응 권한 SECTION_REPLY_PUBLISH: 사용자가 게시판의 모든 답글 내용을 심사할 수 있는지 여부." )
		private Boolean replyPublishAble = false;

		@FieldDescribe( "대응 권한 SECTION_REPLY_AUDIT: 사용자가 게시판에서 답글을 심사할 수 있는지 여부." )
		private Boolean replyAuditAble = false;

		@FieldDescribe( "대응 권한 SECTION_REPLY_MANAGEMENT: 사용자가 게시판에서 답글을 조회하거나 삭제할 수 있는지 여부." )
		private Boolean replyManageAble = false;

		@FieldDescribe( "대응 권한 SECTION_SECTION_MANAGER: 사용자가 게시판에서 하위 게시판 생성·삭제 등을 할 수 있는지 여부." )
		private Boolean sectionManageAble = false;

		@FieldDescribe( "대응 권한 SECTION_PERMISSION_MANAGEMENT: 사용자가 포럼 사용자의 게시판 권한을 관리할 수 있는지 여부." )
		private Boolean sectionPermissoinManageAble = false;

		@FieldDescribe( "대응 권한 SECTION_CONFIG_MANAGEMENT: 사용자가 게시판 시스템 파라미터 설정을 수정할 수 있는지 여부." )
		private Boolean sectionConfigManageAble = false;

		public Boolean getSubjectPublishAble() {
			return subjectPublishAble;
		}

		public void setSubjectPublishAble(Boolean subjectPublishAble) {
			this.subjectPublishAble = subjectPublishAble;
		}

		public Boolean getSubjectAuditAble() {
			return subjectAuditAble;
		}

		public void setSubjectAuditAble(Boolean subjectAuditAble) {
			this.subjectAuditAble = subjectAuditAble;
		}

		public Boolean getSubjectManageAble() {
			return subjectManageAble;
		}

		public void setSubjectManageAble(Boolean subjectManageAble) {
			this.subjectManageAble = subjectManageAble;
		}

		public Boolean getSubjectRecommendAble() {
			return subjectRecommendAble;
		}

		public void setSubjectRecommendAble(Boolean subjectRecommendAble) {
			this.subjectRecommendAble = subjectRecommendAble;
		}

		public Boolean getSubjectStickAble() {
			return subjectStickAble;
		}

		public void setSubjectStickAble(Boolean subjectStickAble) {
			this.subjectStickAble = subjectStickAble;
		}

		public Boolean getSubjectCreamAble() {
			return subjectCreamAble;
		}

		public void setSubjectCreamAble(Boolean subjectCreamAble) {
			this.subjectCreamAble = subjectCreamAble;
		}

		public Boolean getReplyPublishAble() {
			return replyPublishAble;
		}

		public void setReplyPublishAble(Boolean replyPublishAble) {
			this.replyPublishAble = replyPublishAble;
		}

		public Boolean getReplyAuditAble() {
			return replyAuditAble;
		}

		public void setReplyAuditAble(Boolean replyAuditAble) {
			this.replyAuditAble = replyAuditAble;
		}

		public Boolean getReplyManageAble() {
			return replyManageAble;
		}

		public void setReplyManageAble(Boolean replyManageAble) {
			this.replyManageAble = replyManageAble;
		}

		public Boolean getSectionManageAble() {
			return sectionManageAble;
		}

		public void setSectionManageAble(Boolean sectionManageAble) {
			this.sectionManageAble = sectionManageAble;
		}

		public Boolean getSectionPermissoinManageAble() {
			return sectionPermissoinManageAble;
		}

		public void setSectionPermissoinManageAble(Boolean sectionPermissoinManageAble) {
			this.sectionPermissoinManageAble = sectionPermissoinManageAble;
		}

		public Boolean getSectionConfigManageAble() {
			return sectionConfigManageAble;
		}

		public void setSectionConfigManageAble(Boolean sectionConfigManageAble) {
			this.sectionConfigManageAble = sectionConfigManageAble;
		}

	}
}
