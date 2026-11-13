package com.x.processplatform.assemble.surface.jaxrs.task;

import com.google.gson.JsonElement;
import com.x.base.core.project.exception.ExceptionDeprecatedAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

@Deprecated(forRemoval = true)
class ActionManageReset extends BaseAction {

//	private Wi wi;
//	private EffectivePerson effectivePerson;
//	private WorkLog workLog;
//	private Task task;
//	private List<String> existTaskIds = new ArrayList<>();

	/** A의 결재 대기를 B의 결재 대기로 직접 변경, 전달 */
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		throw new ExceptionDeprecatedAction(V2Reset.class.getName());
//		ActionResult<Wo> result = new ActionResult<>();
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			wi = this.convertToWrapIn(jsonElement, Wi.class);
//			this.effectivePerson = effectivePerson;
//			Business business = new Business(emc);
//			task = emc.find(id, Task.class);
//			if (null == task) {
//				throw new ExceptionEntityNotExist(id, Task.class);
//			}
//			WoControl control = business.getControl(effectivePerson, task, WoControl.class);
//			if (BooleanUtils.isNotTrue(control.getAllowReset())) {
//				throw new ExceptionAccessDenied(effectivePerson, task);
//			}
//			this.workLog = emc.firstEqualAndEqual(WorkLog.class, WorkLog.job_FIELDNAME, task.getJob(),
//					WorkLog.fromActivityToken_FIELDNAME, task.getActivityToken());
//
//			if (null == workLog) {
//				throw new ExceptionEntityNotExist(WorkLog.class);
//			}
//			existTaskIds = emc.idsEqual(Task.class, Task.job_FIELDNAME, task.getJob());
//			/* reset 사용자 검사 */
//			List<String> identites = business.organization().identity().list(wi.getIdentityList());
//			if (identites.isEmpty()) {
//				throw new ExceptionIdentityEmpty();
//			}
//			wi.setIdentityList(identites);
//			emc.beginTransaction(Task.class);
//			/* 새 경로를 선택하면 이전 선택을 덮어씀 */
//			if (StringUtils.isNotEmpty(wi.getRouteName())) {
//				task.setRouteName(wi.getRouteName());
//			}
//			/* 새 프로세스 의견이 있으면 프로세스 의견을 덮어씀 */
//			if (StringUtils.isNotEmpty(wi.getOpinion())) {
//				task.setOpinion(wi.getOpinion());
//			}
//			emc.commit();
//		}
//		ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
//				Applications.joinQueryUri("task", task.getId(), "reset"), wi, task.getJob());
//		this.record();
//		Wo wo = new Wo();
//		wo.setValue(true);
//		result.setData(wo);
//		return result;
	}

//	private void record() throws Exception {
//		Record rec = RecordBuilder.processing(null);
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			final List<String> nextTaskIdentities = new ArrayList<>();
//			record = new Record(workLog, task);
//			// workCompleted 검사, 존재하면 업무 문서가 이미 완료된 것이므로 상태를 완료로 표시.
//			WorkCompleted workCompleted = emc.firstEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME,
//					task.getJob());
//			if (null != workCompleted) {
//				record.setCompleted(true);
//				record.setWorkCompleted(workCompleted.getId());
//			}
//			record.setPerson(effectivePerson.getDistinguishedName());
//			record.setType(Record.TYPE_RESET);
//			List<String> ids = emc.idsEqual(Task.class, Task.job_FIELDNAME, task.getJob());
//			ids = ListUtils.subtract(ids, existTaskIds);
//			List<Task> list = emc.fetch(ids, Task.class,
//					ListTools.toList(Task.identity_FIELDNAME, Task.job_FIELDNAME, Task.work_FIELDNAME,
//							Task.activity_FIELDNAME, Task.activityAlias_FIELDNAME, Task.activityName_FIELDNAME,
//							Task.activityToken_FIELDNAME, Task.activityType_FIELDNAME, Task.identity_FIELDNAME));
//			if (BooleanUtils.isNotFalse(wi.getKeep())) {
//				// 자신을 제외하지 않으면 자신을 다시 추가
//				list.add(task);
//			}
//			list.stream().collect(Collectors.groupingBy(Task::getActivity, Collectors.toList())).entrySet().stream()
//					.forEach(o -> {
//						Task next = o.getValue().get(0);
//						NextManual nextManual = new NextManual();
//						nextManual.setActivity(next.getActivity());
//						nextManual.setActivityAlias(next.getActivityAlias());
//						nextManual.setActivityName(next.getActivityName());
//						nextManual.setActivityToken(next.getActivityToken());
//						nextManual.setActivityType(next.getActivityType());
//						for (Task t : o.getValue()) {
//							nextManual.getTaskIdentityList().add(t.getIdentity());
//							nextTaskIdentities.add(t.getIdentity());
//						}
//						record.getProperties().getNextManualList().add(nextManual);
//					});
//			/* 중복 제거 */
//			record.getProperties().setNextManualTaskIdentityList(ListTools.trim(nextTaskIdentities, true, true));
//		}
//		WoId resp = ThisApplication.context().applications()
//				.postQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
//						Applications.joinQueryUri("record", "job", task.getJob()), record, this.task.getJob())
//				.getData(WoId.class);
//		if (StringUtils.isBlank(resp.getId())) {
//			throw new ExceptionReset(this.task.getId());
//		}
//	}
//
//	public static class Wi extends V2ResetWi {
//
//	}
//
//	public static class WoControl extends WorkControl {
//	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -7490452448799400254L;

	}

}