package com.x.server.console;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.node.RefreshApplicationsEvent;
import com.x.server.console.node.RegistApplicationsEvent;
import com.x.server.console.node.UpdateApplicationsEvent;
import com.x.server.console.node.VoteCenterEvent;
import com.x.server.console.server.Servers;

public class RegistApplicationsAndVoteCenterTask implements Job {

	private static Logger logger = LoggerFactory.getLogger(RegistApplicationsAndVoteCenterTask.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			if (BooleanUtils.isTrue(Servers.applicationServerIsStarted())) {
				// node의 모든 application을 각 center에 등록
				Config.resource_node_eventQueue()
						.put(XGsonBuilder.instance().toJsonTree(new RegistApplicationsEvent()));
				// 먼저 center 선출
				Config.resource_node_eventQueue().put(XGsonBuilder.instance().toJsonTree(new VoteCenterEvent()));
			}
			if (StringUtils.equalsIgnoreCase(Config.node(), Config.resource_node_centersPirmaryNode())) {
				// if (BooleanUtils.isTrue(Servers.centerServerIsStarted())) {
				// 로컬 application 갱신
				Config.resource_node_eventQueue()
						.put(XGsonBuilder.instance().toJsonTree(new RefreshApplicationsEvent()));
			} else {
				// 주 center에서 로컬 데이터 업데이트
				Config.resource_node_eventQueue()
						.put(XGsonBuilder.instance().toJsonTree(new UpdateApplicationsEvent()));
			}
		} catch (Exception e) {
			logger.error(e);
			Thread.currentThread().interrupt();
		}
	}
}