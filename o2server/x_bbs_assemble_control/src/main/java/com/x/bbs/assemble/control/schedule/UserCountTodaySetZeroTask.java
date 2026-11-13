package com.x.bbs.assemble.control.schedule;

import java.util.List;

import org.quartz.JobExecutionContext;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSUserInfoService;
import com.x.bbs.entity.BBSUserInfo;

/**
 * 정기 에이전트, 모든 사용자의 오늘 답글·오늘 주제를 0으로 설정.
 * 
 * @author LIYI
 *
 */
public class UserCountTodaySetZeroTask extends AbstractJob {

	private Logger logger = LoggerFactory.getLogger(UserCountTodaySetZeroTask.class);

	private BBSUserInfoService userInfoService = new BBSUserInfoService();
	private BBSOperationRecordService operationRecordService = new BBSOperationRecordService();

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
				userCountTodaySetZero(u);
			});
		}
	}

	private void userCountTodaySetZero(String userName) {
		if (userName == null || userName.isEmpty()) {
			return;
		}
		//개인 답글 수 1 증가
		BBSUserInfo userInfo;
		try {
			userInfo = userInfoService.getByUserName(userName);
			userInfo.setReplyCountToday((long)0);
			userInfo.setSubjectCountToday((long)0);
			userInfoService.save(userInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.warn("system distinct all operation user names got an exception.");
			logger.error(e);
		}
	
	}

}