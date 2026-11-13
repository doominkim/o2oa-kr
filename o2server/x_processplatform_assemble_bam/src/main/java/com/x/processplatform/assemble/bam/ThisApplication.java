package com.x.processplatform.assemble.bam;

import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.processplatform.assemble.bam.jaxrs.period.Period;
import com.x.processplatform.assemble.bam.jaxrs.state.State;
import com.x.processplatform.assemble.bam.schedule.PeriodTimer;
import com.x.processplatform.assemble.bam.schedule.StateTimer;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			context().scheduleLocal(PeriodTimer.class, 180, 60 * 30);
			/* state 실행 통계는 조직 DB를 읽어야 하므로, 실행 시작 시 조직 앱이 아직 시작되지 않았으면 비어 있음 */
			context().scheduleLocal(StateTimer.class, 180, 60 * 60 * 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			CacheManager.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static State state = new State();
	public static Period period = new Period();

}
