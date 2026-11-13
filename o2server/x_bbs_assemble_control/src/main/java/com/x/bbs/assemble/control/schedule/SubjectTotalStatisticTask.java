package com.x.bbs.assemble.control.schedule;

import java.util.List;

import org.quartz.JobExecutionContext;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSForumSubjectStatisticService;
import com.x.bbs.entity.BBSForumInfo;

/**
 * 정기 에이전트, 모든 포럼과 게시판의 주제·답글 수, 참여 인원 통계. 1. 모든 포럼 순회 2. 각 포럼, 각 상위 게시판 및 하위 게시판별 데이터 통계
 * 
 * @author LIYI
 *
 */
public class SubjectTotalStatisticTask extends AbstractJob {

	private Logger logger = LoggerFactory.getLogger(SubjectTotalStatisticTask.class);
	private BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	private BBSForumSubjectStatisticService forumSubjectStatisticService = new BBSForumSubjectStatisticService();

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		List<BBSForumInfo> forumInfoList = null;
		try {
			forumInfoList = forumInfoServiceAdv.listAll();
			if (ListTools.isNotEmpty(forumInfoList)) {
				forumSubjectStatisticService.statisticSubjectTotalAndReplayTotalForForum(forumInfoList);
			}
			logger.info("Timertask[SubjectReplyTotalStatisticTask] completed and excute success.");
		} catch (Exception e) {
			logger.warn("SubjectTotalStatisticTask got an exception.");
			logger.error(e);
		}
	}
}