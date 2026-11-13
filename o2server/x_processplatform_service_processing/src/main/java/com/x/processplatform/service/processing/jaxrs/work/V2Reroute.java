package com.x.processplatform.service.processing.jaxrs.work;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.ticket.Tickets;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2RerouteWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2RerouteWo;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;
import com.x.processplatform.service.processing.processor.AeiObjects;

class V2Reroute extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Reroute.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		Param param = this.init(id, jsonElement);

		Callable<ActionResult<Wo>> callable = new CallableImpl(param);

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.job).submit(callable)
				.get(Config.processPlatform().getProcessingTimeout(), TimeUnit.SECONDS);

	}

	private Param init(String id, JsonElement jsonElement) throws Exception {
		Param param = new Param();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		param.distinguishedNameList = wi.getDistinguishedNameList();
		param.mergeWork = BooleanUtils.isTrue(wi.getMergeWork());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME, Work.process_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			param.job = work.getJob();
			param.id = work.getId();
			Activity activity = business.element().getActivity(wi.getActivity());
			if (null == activity) {
				throw new ExceptionEntityNotExist(wi.getActivity());
			}
			if (!StringUtils.equals(work.getProcess(), activity.getProcess())) {
				throw new ExceptionProcessNotMatch();
			}
			param.activity = activity;
		}
		return param;
	}

	private class Param {

		private String id;
		private String job;
		private Activity activity;
		private Boolean mergeWork;
		private List<String> distinguishedNameList;

	}

	public static class Wi extends V2RerouteWi {

		private static final long serialVersionUID = 4131889338839380226L;

	}

	public static class Wo extends V2RerouteWo {

		private static final long serialVersionUID = 6797942626499506636L;
	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private Param param;

		private CallableImpl(Param param) {
			this.param = param;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			Work work;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				work = emc.find(param.id, Work.class);
				if (null == work) {
					throw new ExceptionEntityNotExist(param.id, Work.class);
				}
				AeiObjects aeiObjects = new AeiObjects(business, work, param.activity, new ProcessingAttributes());
				if (BooleanUtils.isTrue(param.mergeWork)) {
					// 모든 결재 대기 삭제
					aeiObjects.getTasks().stream().forEach(aeiObjects.getDeleteTasks()::add);
					// 다른 업무 문서 삭제
					aeiObjects.getWorks().stream().filter(o -> (!StringUtils.equalsIgnoreCase(o.getId(), work.getId())))
							.forEach(aeiObjects.getDeleteWorks()::add);
					// 다른 업무 문서의 연결되지 않은 workLog 삭제
					aeiObjects.getWorkLogs().stream().filter(o -> BooleanUtils.isNotTrue(o.getConnected()))
							.filter(o -> (!StringUtils.equalsIgnoreCase(o.getFromActivity(), work.getActivity())))
							.forEach(aeiObjects.getDeleteWorkLogs()::add);
					// read와 연관된 work 재지정
					aeiObjects.getReads().stream()
							.filter(o -> (!StringUtils.equalsIgnoreCase(o.getWork(), work.getId()))).forEach(o -> {
								o.setWork(work.getId());
								aeiObjects.getUpdateReads().add(o);
							});
				} else {
					// 남아 있을 수 있는 결재 대기 삭제
					aeiObjects.getTasks().stream().filter(o -> StringUtils.equals(work.getId(), o.getWork()))
							.forEach(aeiObjects.getDeleteTasks()::add);
				}
				if (Manual.class.isAssignableFrom(param.activity.getClass())) {
					// 양식 재설정
					String formId = business.element().lookupSuitableForm(work.getProcess(), param.activity.getId());
					if (StringUtils.isNotBlank(formId)) {
						work.setForm(formId);
					}
					// 처리자 재설정
					if (ListTools.isNotEmpty(param.distinguishedNameList)) {
						work.setTickets(((Manual) param.activity).identitiesToTickets(param.distinguishedNameList));
					} else {
						work.setTickets(new Tickets());
					}
				}
				// 스케줄링 시 이 플래그를 강제로 true로 설정, 초안 상태에서 스케줄링할 때 시스템이 초안 상태로 판단해 기본적으로 결재 대기를 생성하지 않는 것을 방지.
				work.setWorkThroughManual(true);
				work.setDestinationActivity(param.activity.getId());
				work.setDestinationActivityType(param.activity.getActivityType());
				work.setForceRouteEnable(true);
				work.setDestinationRoute("");
				work.setDestinationRouteName("");
				aeiObjects.getUpdateWorks().add(work);
				aeiObjects.commit();
			}
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}
}