package com.x.ai.assemble.control;

import com.x.ai.assemble.control.quartz.CmsDocumentIndexTask;
import com.x.ai.assemble.control.quartz.InitConfigTask;
import com.x.ai.assemble.control.queue.QueueDocumentIndex;
import com.x.base.core.project.Context;

/**
 * 앱 초기화 및 종료 처리
 * @author sword
 */
public class ThisApplication {

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static final QueueDocumentIndex queueDocumentIndex = new QueueDocumentIndex();

	public static void init() throws Exception {
		context().startQueue(queueDocumentIndex);
		context.schedule(CmsDocumentIndexTask.class, "0 0 1,12 * * ?");
		context.schedule(InitConfigTask.class, "0 0/5 * * * ?");
	}

	public static void destroy() {
	}
}
