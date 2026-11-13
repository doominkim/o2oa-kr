package com.x.bbs.assemble.control.schedule;

import java.util.List;

import org.quartz.JobExecutionContext;

import com.google.gson.Gson;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSPermissionInfoService;
import com.x.bbs.assemble.control.service.BBSReplyInfoService;
import com.x.bbs.assemble.control.service.BBSRoleInfoService;
import com.x.bbs.assemble.control.service.BBSSubjectInfoService;
import com.x.bbs.assemble.control.service.BBSUserInfoService;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;

/**
 * 정기 에이전트, 모든 사용자의 게시글·답글 수와 권한을 분석·통계하여 기록.
 * 
 * @author LIYI
 *
 */
public class UserSubjectReplyPermissionStatisticTask extends AbstractJob {

	private Logger logger = LoggerFactory.getLogger(UserSubjectReplyPermissionStatisticTask.class);

	private BBSUserInfoService userInfoService = new BBSUserInfoService();
	private BBSSubjectInfoService subjectInfoService = new BBSSubjectInfoService();
	private BBSReplyInfoService replyInfoService = new BBSReplyInfoService();
	private BBSOperationRecordService operationRecordService = new BBSOperationRecordService();
	private BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
	private BBSRoleInfoService roleInfoService = new BBSRoleInfoService();

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		process();
		logger.info("Timertask[UserSubjectReplyStatisticTask] completed and excute success.");
	}

	/**
	 * 정기 에이전트 업무 로직 실행
	 */
	private void process() {
		List<String> operationUserNames = null;
		// 1. BBS_OPERATIONRECORD 테이블에서 BBS 시스템 참여 사용자 이름 목록 조회
		try {
			operationUserNames = operationRecordService.distinctAllOperationUserNames();
		} catch (Exception e) {
			logger.warn("system distinct all operation user names got an exception.");
			logger.error(e);
		}
		if ( ListTools.isNotEmpty( operationUserNames )) {
			// 2. 모든 사용자를 순회하며 각각 통계
			operationUserNames.forEach(u -> {
				statisticSubjectReplyCountForUser(u);
			});
		}
	}

	private void statisticSubjectReplyCountForUser(String userName) {
		if (userName == null || userName.isEmpty()) {
			return;
		}
		Gson gson = null;
		RoleAndPermission roleAndPermission = null;
		List<String> roleCodes = null;
		List<String> permissionCodes = null;
		Long subjectCount = 0L; // 사용자 게시글 수
		Long replyCount = 0L; // 사용자 답글 수
		Long subjectCountToday = 0L; // 오늘 게시글 수
		Long replyCountToday = 0L;// 오늘 답글 수
		Long creamCount = 0L;// 우수 게시글 수
		Long originalCount = 0L;// 원작 게시글 수
		Long fansCount = 0L; // 팬 수
		Long popularity = 0L; // 인기
		Long credit = 0L;// 포인트
		String permissionContent = ""; // 권한 역할 내용

		// 사용자 게시글 수 통계
		try {
			subjectCount = subjectInfoService.countSubjectByUserName(userName);
		} catch (Exception e) {
			logger.warn("system count subject total by user name got an exception.");
			logger.error(e);
		}

		// 사용자 답글 수
		try {
			replyCount = replyInfoService.countReplyByUserName(userName);
		} catch (Exception e) {
			logger.warn("system count reply total by user name got an exception.");
			logger.error(e);
		}

		// 오늘 게시글 수
		try {
			subjectCountToday = subjectInfoService.countSubjectForTodayByUserName(userName);
		} catch (Exception e) {
			logger.warn("system count today subject total by user name got an exception.");
			logger.error(e);
		}

		// 오늘 답글 수
		try {
			replyCountToday = replyInfoService.countReplyForTodayByUserName(userName);
		} catch (Exception e) {
			logger.warn("system count today reply total by user name got an exception.");
			logger.error(e);
		}

		// 우수 게시글 수
		try {
			creamCount = subjectInfoService.countCreamSubjectByUserName(userName);
		} catch (Exception e) {
			logger.warn("system count cream subject total by user name got an exception.");
			logger.error(e);
		}

		// 원작 게시글 수
		try {
			originalCount = subjectInfoService.countOriginalSubjectByUserName(userName);
		} catch (Exception e) {
			logger.warn("system count original subject total by user name got an exception.");
			logger.error(e);
		}

		// 팬 수
		// 인기
		// 포인트
		// 권한 역할 내용
		gson = XGsonBuilder.instance();
		roleAndPermission = new RoleAndPermission();
		roleAndPermission.setPerson(userName);
		try {
			roleCodes = roleInfoService.listAllRoleCodesForUser(userName);
			roleAndPermission.setRoleInfoList(roleCodes);
		} catch (Exception e) {
			logger.warn("system list all role for user got an exception.");
			logger.error(e);
		}
		if ( ListTools.isNotEmpty( roleCodes )) {
			try {
				permissionCodes = permissionInfoService.listPermissionCodesByRoleCodes(roleCodes);
				roleAndPermission.setPermissionInfoList(permissionCodes);
			} catch (Exception e) {
				logger.warn("system list all permission for user got an exception.");
				logger.error(e);
			}
		}
		try {
			permissionContent = gson.toJson(roleAndPermission);
		} catch (Exception e) {
			logger.warn("system translate object to json got an exception.");
			logger.error(e);
		}
		// 데이터베이스에서 사용자 정보를 조회하여 정보 갱신
		try {
			userInfoService.save(userName, subjectCount, replyCount, subjectCountToday, replyCountToday, creamCount,
					originalCount, fansCount, popularity, credit, permissionContent);
		} catch (Exception e) {
			logger.warn("system save user info got an exception. username:" + userName);
			logger.error(e);
		}
	}

}