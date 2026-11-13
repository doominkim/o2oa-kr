package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.exception.ExceptionAccessDenied;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionForumInfoNotExists;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionForumInsufficientPermission;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionForumPermissionsCheck;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyContentEmpty;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyInfoProcess;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplySubjectIdEmpty;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionSectionInsufficientPermissions;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionSectionNotExists;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionSectionPermissionsCheck;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionSubjectLocked;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionSubjectNotExists;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSReplyInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		BBSSubjectInfo subjectInfo = null;
		BBSReplyInfo replyInfo = null;
		BBSSectionInfo sectionInfo = null;
		BBSForumInfo forumInfo = null;
		Boolean hasPermission = false;
		Boolean check = true;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		Wi wrapIn = null;

		if(!effectivePerson.isAnonymous() && this.userManagerService.personHasShutup(effectivePerson.getDistinguishedName())){
			check = false;
			Exception exception = new ExceptionAccessDenied(effectivePerson);
			result.error(exception);
		}

		if (check) {
			try {
				wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e,
						"JSON 정보를 객체로 변환하는 중 오류가 발생했습니다. JSON:" + jsonElement.toString());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			wrapIn.setHostIp(request.getRemoteHost());
			if (wrapIn.getSubjectId() == null) {
				check = false;
				Exception exception = new ExceptionReplySubjectIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			if (wrapIn.getContent() == null) {
				check = false;
				Exception exception = new ExceptionReplyContentEmpty();
				result.error(exception);
			}
		}
		// 연관 주제 정보 존재 여부 조회
		if (check) {
			try {
				subjectInfo = subjectInfoService.get(wrapIn.getSubjectId());
				if (subjectInfo == null) {
					check = false;
					Exception exception = new ExceptionSubjectNotExists(wrapIn.getSubjectId());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e, "지정한 ID로 주제 정보를 조회하는 중 오류가 발생했습니다. ID:" + wrapIn.getSubjectId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		// 주제의 사용자 답글 허용 여부 판단, 잠긴 주제는 답글 불가
		if (check) {
			if ("잠김".equals(subjectInfo.getSubjectStatus())) {
				check = false;
				Exception exception = new ExceptionSubjectLocked(wrapIn.getSubjectId());
				result.error(exception);
			}
		}

		// 주제가 속한 게시판의 사용자 답글 허용 여부 또는 사용자의 주제 답글 권한 판단
		if (check) {
			try {
				sectionInfo = sectionInfoServiceAdv.get(subjectInfo.getSectionId());
				if (sectionInfo == null) {
					check = false;
					Exception exception = new ExceptionSectionNotExists(subjectInfo.getSectionId());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e,
						"지정한 ID로 섹션 정보를 조회하는 중 오류가 발생했습니다. ID:" + subjectInfo.getSectionId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		// 사용자의 게시판 내 주제 답글 권한 판단 시작
		if (check) {
			if ("권한에 따라".equals(sectionInfo.getReplyPublishAble())) {
				try {
					hasPermission = UserPermissionService.hasPermission(effectivePerson.getDistinguishedName(),
							"SECTION_REPLY_PUBLISH_" + subjectInfo.getSectionId());
					if (!hasPermission) {
						check = false;
						Exception exception = new ExceptionSectionInsufficientPermissions(sectionInfo.getSectionName(),
								"SECTION_REPLY_PUBLISH");
						result.error(exception);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSectionPermissionsCheck(e,
							effectivePerson.getDistinguishedName(), sectionInfo.getSectionName(),
							"SECTION_REPLY_PUBLISH");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		// 사용자의 상위 게시판 답글 권한 추가 조회
		if ( subjectInfo != null && !subjectInfo.getMainSectionId().equals(subjectInfo.getSectionId())) {
			if (check) {
				try {
					sectionInfo = sectionInfoServiceAdv.get(subjectInfo.getMainSectionId());
					if (sectionInfo == null) {
						check = false;
						Exception exception = new ExceptionSectionNotExists(subjectInfo.getMainSectionId());
						result.error(exception);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionReplyInfoProcess(e,
							"지정한 ID로 섹션 정보를 조회하는 중 오류가 발생했습니다. ID:" + subjectInfo.getMainSectionId());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			if (check) {
				if ("권한에 따라".equals(sectionInfo.getReplyPublishAble())) {
					// 사용자의 게시판 내 주제 답글 권한 판단 시작
					try {
						hasPermission = UserPermissionService.hasPermission(effectivePerson.getDistinguishedName(),
								"SECTION_REPLY_PUBLISH_" + subjectInfo.getMainSectionId());
						if (!hasPermission) {
							check = false;
							Exception exception = new ExceptionSectionInsufficientPermissions( sectionInfo.getSectionName(), "SECTION_REPLY_PUBLISH");
							result.error(exception);
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionSectionPermissionsCheck(e,
								effectivePerson.getDistinguishedName(), sectionInfo.getSectionName(), "SECTION_REPLY_PUBLISH");
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
		}
		// 주제가 속한 포럼의 사용자 답글 허용 여부 또는 사용자의 주제 답글 권한 판단
		if (check) {
			try {
				forumInfo = forumInfoServiceAdv.get(subjectInfo.getForumId());
				if (forumInfo == null) {
					check = false;
					Exception exception = new ExceptionForumInfoNotExists(subjectInfo.getForumId());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e,
						"ID로 BBS 포럼 분류 정보를 가져오는 중 오류가 발생했습니다! ID:" + subjectInfo.getForumId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if ("권한에 따라".equals(forumInfo.getReplyPublishAble())) {
				// 사용자의 포럼 구역 주제 답글 권한 판단 시작
				try {
					hasPermission = UserPermissionService.hasPermission(effectivePerson.getDistinguishedName(),
							"FORUM_REPLY_PUBLISH_" + subjectInfo.getForumId());
					if (!hasPermission) {
						check = false;
						Exception exception = new ExceptionForumInsufficientPermission(subjectInfo.getForumName(),
								"FORUM_REPLY_PUBLISH");
						result.error(exception);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionForumPermissionsCheck(e, effectivePerson.getDistinguishedName(), subjectInfo.getForumName(), "FORUM_REPLY_PUBLISH");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}

		// 권한 판단 완료, 답글 내용 저장 시작
		if (check) {
			wrapIn.setForumId(subjectInfo.getForumId());
			wrapIn.setForumName(subjectInfo.getForumName());
			wrapIn.setMainSectionId(subjectInfo.getMainSectionId());
			wrapIn.setMainSectionName(subjectInfo.getMainSectionName());
			wrapIn.setSectionId(subjectInfo.getSectionId());
			wrapIn.setSectionName(subjectInfo.getSectionName());
			wrapIn.setCreatorName(effectivePerson.getDistinguishedName());
			Business business = new Business(null);
			wrapIn.setNickName(business.organization().person().getNickName(effectivePerson.getDistinguishedName()));
		}

		if (check) {
			if ( wrapIn.getTitle() == null || wrapIn.getTitle().isEmpty()) {
				if ( StringUtils.isNotEmpty( subjectInfo.getTitle() )) {
					wrapIn.setTitle(subjectInfo.getTitle());
				} else {
					wrapIn.setTitle("제목 없음");
				}
			}
		}
		if (check) {
			try {
				replyInfo = Wi.copier.copy(wrapIn);
				if ( StringUtils.isNotEmpty( wrapIn.getId() )) {
					replyInfo.setId(wrapIn.getId());
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e, "사용자가 전달한 정보를 댓글 객체로 변환하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				replyInfo.setMachineName(wrapIn.getReplyMachineName());
				replyInfo.setSystemType(wrapIn.getReplySystemName());
				replyInfo = replyInfoService.save(replyInfo);

				Wo wo = new Wo();
				wo.setId(replyInfo.getId());
				result.setData(wo);

				CacheManager.notify( BBSReplyInfo.class );
				CacheManager.notify( BBSForumInfo.class );
				CacheManager.notify( BBSSectionInfo.class );
				CacheManager.notify( BBSSubjectInfo.class );

				operationRecordService.replyOperation(effectivePerson.getDistinguishedName(), replyInfo, "CREATE", hostIp, hostName);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e, "댓글 저장 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if ( check ) {
			//포럼(Forum), 게시판(Selection), 상위 게시판(MainSelection) 설정에 따라 해당 답글의 작성자 메시지 알림·메일 알림 허용 여부 판단
			try {
				ThisApplication.queueNewReplyNotify.send( replyInfo );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}

		}
		return result;
	}

	public static class Wi extends BBSReplyInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<Wi, BBSReplyInfo> copier = WrapCopierFactory.wi(Wi.class, BBSReplyInfo.class, null,
				JpaObject.FieldsUnmodify);

		private String replyMachineName = "PC";

		private String replySystemName = "Windows";

		private String userHostIp = "";

		public String getReplyMachineName() {
			return replyMachineName;
		}

		public void setReplyMachineName(String replyMachineName) {
			this.replyMachineName = replyMachineName;
		}

		public String getReplySystemName() {
			return replySystemName;
		}

		public void setReplySystemName(String replySystemName) {
			this.replySystemName = replySystemName;
		}

		public String getUserHostIp() {
			return userHostIp;
		}

		public void setUserHostIp(String userHostIp) {
			this.userHostIp = userHostIp;
		}

	}

	public static class Wo extends WoId {

	}
}
