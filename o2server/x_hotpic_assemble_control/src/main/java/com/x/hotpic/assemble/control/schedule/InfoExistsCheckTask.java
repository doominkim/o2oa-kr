package com.x.hotpic.assemble.control.schedule;

import org.quartz.JobExecutionContext;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.hotpic.assemble.control.service.HotPictureInfoServiceAdv;

/**
 * 모든 정보 객체를 확인하여 존재하지 않으면 삭제
 * 
 * @author liyi_
 *
 */
public class InfoExistsCheckTask extends AbstractJob {

	private Logger logger = LoggerFactory.getLogger(InfoExistsCheckTask.class);
	private HotPictureInfoServiceAdv hotPictureInfoServiceAdv = new HotPictureInfoServiceAdv();

	/**
	 * 1. 먼저 전체 정보 목록을 정렬 번호와 수정 시간 역순으로 조회 2. 50개를 초과하는 정보 객체 삭제
	 * 3. 최대 50개의 정보 객체를 확인하여 각 객체 정보가 여전히 존재하는지 조회하고, 존재하지 않으면 삭제
	 */
	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		hotPictureInfoServiceAdv.documentExistsCheck();
		logger.info("Timertask Hotpicture InfoExistsCheckTask excute completed.");
	}

}