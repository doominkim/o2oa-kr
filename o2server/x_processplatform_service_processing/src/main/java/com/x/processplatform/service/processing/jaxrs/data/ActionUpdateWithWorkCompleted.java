package com.x.processplatform.service.processing.jaxrs.data;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.express.service.processing.jaxrs.data.DataWi;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionUpdateWithWorkCompleted extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateWithWorkCompleted.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		String executorSeed = null;
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		// 빈 데이터 제출로 data가 비워지는 것을 방지
		if (null == wi.getJsonElement() || (!wi.getJsonElement().isJsonObject())) {
			throw new ExceptionNotJsonObject();
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WorkCompleted workCompleted = emc.fetch(id, WorkCompleted.class,
					ListTools.toList(WorkCompleted.job_FIELDNAME));
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(id, WorkCompleted.class);
			}
			executorSeed = workCompleted.getJob();
		}

		Callable<ActionResult<Wo>> callable = () -> {
			ActionResult<Wo> result1 = new ActionResult<>();
			Wo wo1 = new Wo();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				WorkCompleted workCompleted = emc.find(id, WorkCompleted.class);
				if (null == workCompleted) {
					throw new ExceptionEntityNotExist(id, WorkCompleted.class);
				}
				if (BooleanUtils.isTrue(workCompleted.getMerged())) {
					throw new ExceptionModifyMerged(workCompleted.getId());
				}

				JsonElement source = getData(business, workCompleted.getJob());
				JsonElement merge = XGsonBuilder.merge(wi.getJsonElement(), source);

				/* title과 serial을 먼저 수정한 뒤 DataItem 수정, 기존 DataItem에도 title과 serial 데이터가 있기 때문. */
				updateTitleSerialObjectSecurityClearance(business, workCompleted, merge);
				updateData(business, workCompleted, merge);
				/* updateTitleSerial과 updateData 메서드 내에서 커밋함 */

				wi.init(workCompleted, merge);
				createDataRecord(business, wi);

				wo1.setId(workCompleted.getId());
			}
			result1.setData(wo1);
			return result1;
		};

		ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);

		result.setData(wo);
		return result;
	}

	public static class Wi extends DataWi {

		private static final long serialVersionUID = 2412698845557505026L;
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -8616306011949609536L;

	}

}
