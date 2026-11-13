//package com.x.processplatform.assemble.surface.jaxrs.work;
//
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//import org.apache.commons.lang3.BooleanUtils;
//import org.apache.commons.lang3.ObjectUtils;
//import org.apache.commons.lang3.StringUtils;
//
//import com.google.gson.JsonElement;
//import com.x.base.core.container.EntityManagerContainer;
//import com.x.base.core.container.factory.EntityManagerContainerFactory;
//import com.x.base.core.entity.JpaObject;
//import com.x.base.core.project.Applications;
//import com.x.base.core.project.x_processplatform_service_processing;
//import com.x.base.core.project.annotation.FieldDescribe;
//import com.x.base.core.project.bean.WrapCopier;
//import com.x.base.core.project.bean.WrapCopierFactory;
//import com.x.base.core.project.exception.ExceptionAccessDenied;
//import com.x.base.core.project.gson.GsonPropertyObject;
//import com.x.base.core.project.http.ActionResult;
//import com.x.base.core.project.http.EffectivePerson;
//import com.x.base.core.project.http.TokenType;
//import com.x.base.core.project.jaxrs.WoId;
//import com.x.base.core.project.logger.Logger;
//import com.x.base.core.project.logger.LoggerFactory;
//import com.x.base.core.project.organization.Unit;
//import com.x.base.core.project.tools.DefaultCharset;
//import com.x.base.core.project.tools.ListTools;
//import com.x.base.core.project.tools.SortTools;
//import com.x.organization.core.express.Organization;
//import com.x.processplatform.assemble.surface.Business;
//import com.x.processplatform.assemble.surface.ThisApplication;
//import com.x.processplatform.core.entity.content.Task;
//import com.x.processplatform.core.entity.content.TaskCompleted;
//import com.x.processplatform.core.entity.content.Work;
//import com.x.processplatform.core.entity.content.WorkLog;
//import com.x.processplatform.core.entity.element.Application;
//import com.x.processplatform.core.entity.element.Process;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//
///*
// * 앱 이름과 프로세스 이름으로 생성, process로 직접 생성하는 것과 거의 동일
// * */
//
//@Deprecated(forRemoval = true)
//class ActionCreateWithApplicationProcessForce extends BaseAction {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateWithApplicationProcessForce.class);
//
//	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationFlag, String processFlag,
//			JsonElement jsonElement) throws Exception {
//		LOGGER.debug("execute:{}, applicationFlag:{}, processFlag:{}.", effectivePerson::getDistinguishedName,
//				() -> applicationFlag, () -> processFlag);
//		// 새 업무 문서 id
//		String workId = "";
//		// 기존 초안 id
//		String lastestWorkId = "";
//		String identity = null;
//		List<Wo> wos = new ArrayList<>();
//		ActionResult<List<Wo>> result = new ActionResult<>();
//		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			Business business = new Business(emc);
//			identity = this.decideCreatorIdentity(business, effectivePerson, wi);
//			Application application = business.application().pick(applicationFlag);
//			if (null == application) {
//				throw new ExceptionApplicationNotExist(applicationFlag);
//			}
//			Process process = business.process().pickProcessEditionEnabled(application, processFlag);
//			if (null == process) {
//				throw new ExceptionProcessNotExist(processFlag);
//			}
//			List<String> identities = List.of(identity);
//			List<String> units = business.organization().unit().listWithIdentitySupNested(identity);
//			List<String> groups = business.organization().group().listWithIdentity(identities);
//			if (!business.process().startable(effectivePerson, identities, units, groups, process)) {
//				throw new ExceptionAccessDenied(effectivePerson);
//			}
//			if (BooleanUtils.isTrue(wi.getLatest())) {
//				/* 이전에 생성한 초안을 직접 열지 판단, 초안 판단 기준:결재 대기는 있고 처리 완료는 없음 */
//				lastestWorkId = this.latest(business, process, identity);
//				workId = lastestWorkId;
//			}
//			if (StringUtils.isEmpty(workId)) {
//				WoId woId = ThisApplication.context().applications()
//						.postQuery(x_processplatform_service_processing.class,
//								Applications.joinQueryUri("work", "process", process.getId()), wi.getData(), null)
//						.getData(WoId.class);
//				workId = woId.getId();
//			}
//		}
//		/* Work 정보 설정 */
//		if (BooleanUtils.isFalse(wi.getLatest()) || (StringUtils.isEmpty(lastestWorkId))) {
//			/* 초안이 아니면 설정 필요 */
//			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//				Business business = new Business(emc);
//				Organization organization = business.organization();
//				emc.beginTransaction(Work.class);
//				Work work = emc.find(workId, Work.class);
//				if (null == work) {
//					throw new ExceptionWorkNotExist(workId);
//				}
//				work.setTitle(wi.getTitle());
//				work.setCreatorIdentity(identity);
//				work.setCreatorPerson(organization.person().getWithIdentity(identity));
//				work.setCreatorUnit(organization.unit().getWithIdentity(identity));
//				if (StringUtils.isNotEmpty(work.getCreatorUnit())) {
//					Unit unit = organization.unit().getObject(work.getCreatorUnit());
//					work.setCreatorUnitLevelName(unit.getLevelName());
//				}
//				emc.commit();
//			}
//			/* 업무 문서 구동 */
//			ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
//					"work/" + URLEncoder.encode(workId, DefaultCharset.name) + "/processing", null);
//		} else {
//			/* 초안이면 이후 직접 열기 준비 */
//			workId = lastestWorkId;
//		}
//		/* 반환 결과 조립 */
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			Business business = new Business(emc);
//			Work work = emc.find(workId, Work.class);
//			if (null == work) {
//				throw new ExceptionWorkNotExist(workId);
//			}
//			List<String> ids = business.workLog().listWithFromActivityTokenForwardNotConnected(work.getActivityToken());
//			/* 먼저 종료되지 않은 WorkLog 조회 */
//			List<WorkLog> list = emc.list(WorkLog.class, ids);
//			wos = this.refercenceWorkLog(business, list);
//			/* 현재 사용자의 결재 대기 표시 */
//			for (Wo o : wos) {
//				o.setCurrentTaskIndex(-1);
//				for (int i = 0; i < o.getTaskList().size(); i++) {
//					WoTask t = o.getTaskList().get(i);
//					if (StringUtils.equals(effectivePerson.getDistinguishedName(), t.getPerson())) {
//						o.setCurrentTaskIndex(i);
//					}
//				}
//			}
//		}
//		result.setData(wos);
//		return result;
//	}
//
//	@Schema(description = "com.x.processplatform.assemble.surface.jaxrs.work.ActionCreateWithApplicationProcessForce$Wi")
//	public class Wi extends GsonPropertyObject {
//
//		private static final long serialVersionUID = -2930419287174683732L;
//
//		@FieldDescribe("지정 사용자의 기존 초안을 직접 열기, 초안 판단:업무 문서에 처리 완료가 없고 이 사용자의 결재 대기만 하나 있음.")
//		@Schema(description = "지정 사용자의 기존 초안을 직접 열기, 초안 판단:업무 문서에 처리 완료가 없고 이 사용자의 결재 대기만 하나 있음.")
//		private Boolean latest;
//
//		@FieldDescribe("제목.")
//		@Schema(description = "제목.")
//		private String title;
//
//		@FieldDescribe("시작 사용자 신분.")
//		@Schema(description = "시작 사용자 신분.")
//		private String identity;
//
//		@FieldDescribe("업무 문서 데이터.")
//		@Schema(description = "업무 문서 데이터.")
//		private JsonElement data;
//
//		@FieldDescribe("상위 업무 문서 식별자.")
//		@Schema(description = "상위 업무 문서 식별자.")
//		private String parentWork;
//
//		public String getTitle() {
//			return title;
//		}
//
//		public void setTitle(String title) {
//			this.title = title;
//		}
//
//		public String getIdentity() {
//			return identity;
//		}
//
//		public void setIdentity(String identity) {
//			this.identity = identity;
//		}
//
//		public JsonElement getData() {
//			return data;
//		}
//
//		public void setData(JsonElement data) {
//			this.data = data;
//		}
//
//		public Boolean getLatest() {
//			return latest;
//		}
//
//		public void setLatest(Boolean latest) {
//			this.latest = latest;
//		}
//
//		public String getParentWork() {
//			return parentWork;
//		}
//
//		public void setParentWork(String parentWork) {
//			this.parentWork = parentWork;
//		}
//
//	}
//
//	@Schema(description = "com.x.processplatform.assemble.surface.jaxrs.work.ActionCreateWithApplicationProcessForce$Wo")
//	public static class Wo extends WorkLog {
//
//		private static final long serialVersionUID = 1307569946729101786L;
//
//		static WrapCopier<WorkLog, Wo> copier = WrapCopierFactory.wo(WorkLog.class, Wo.class, null,
//				JpaObject.FieldsInvisible);
//
//		@FieldDescribe("정렬 번호.")
//		@Schema(description = "정렬 번호.")
//		private Long rank;
//
//		@FieldDescribe("처리 완료 객체.")
//		@Schema(description = "처리 완료 객체.")
//		private List<WoTaskCompleted> taskCompletedList;
//
//		@FieldDescribe("결재 대기 객체.")
//		@Schema(description = "결재 대기 객체.")
//		private List<WoTask> taskList;
//
//		@FieldDescribe("현재 결재 대기 순번.")
//		@Schema(description = "현재 결재 대기 순번.")
//		private Integer currentTaskIndex;
//
//		public Long getRank() {
//			return rank;
//		}
//
//		public void setRank(Long rank) {
//			this.rank = rank;
//		}
//
//		public Integer getCurrentTaskIndex() {
//			return currentTaskIndex;
//		}
//
//		public void setCurrentTaskIndex(Integer currentTaskIndex) {
//			this.currentTaskIndex = currentTaskIndex;
//		}
//
//		public List<WoTask> getTaskList() {
//			return taskList;
//		}
//
//		public void setTaskList(List<WoTask> taskList) {
//			this.taskList = taskList;
//		}
//
//		public List<WoTaskCompleted> getTaskCompletedList() {
//			return taskCompletedList;
//		}
//
//		public void setTaskCompletedList(List<WoTaskCompleted> taskCompletedList) {
//			this.taskCompletedList = taskCompletedList;
//		}
//
//	}
//
//	@Schema(description = "com.x.processplatform.assemble.surface.jaxrs.work.ActionCreateWithApplicationProcessForce$WoTask")
//	public static class WoTask extends Task {
//
//		private static final long serialVersionUID = 2279846765261247910L;
//
//		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class, null,
//				JpaObject.FieldsInvisible);
//
//		private Long rank;
//
//		public Long getRank() {
//			return rank;
//		}
//
//		public void setRank(Long rank) {
//			this.rank = rank;
//		}
//
//	}
//
//	@Schema(description = "com.x.processplatform.assemble.surface.jaxrs.work.ActionCreateWithApplicationProcessForce$WoTaskCompleted")
//	public static class WoTaskCompleted extends TaskCompleted {
//
//		private static final long serialVersionUID = -7253999118308715077L;
//
//		public static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
//				WoTaskCompleted.class, null, JpaObject.FieldsInvisible);
//
//		private Long rank;
//
//		public Long getRank() {
//			return rank;
//		}
//
//		public void setRank(Long rank) {
//			this.rank = rank;
//		}
//	}
//
//	private List<Wo> refercenceWorkLog(Business business, List<WorkLog> list) throws Exception {
//		List<Wo> os = new ArrayList<>();
//		for (WorkLog o : list) {
//			Wo wo = Wo.copier.copy(o);
//			if (BooleanUtils.isNotTrue(o.getConnected())) {
//				this.referenceTask(business, wo);
//			} else {
//				/** 이미 완료된 것은 결재 대기가 없으므로 빈 배열 반환 */
//				wo.setTaskList(new ArrayList<>());
//			}
//			this.referenceTaskCompleted(business, wo);
//			os.add(wo);
//		}
//		SortTools.asc(os, false, "arrivedTime");
//		return os;
//	}
//
//	private String decideCreatorIdentity(Business business, EffectivePerson effectivePerson, Wi wi) throws Exception {
//		if (TokenType.cipher.equals(effectivePerson.getTokenType())) {
//			return business.organization().identity().get(wi.getIdentity());
//		} else if (StringUtils.isNotEmpty(wi.getIdentity())) {
//			List<String> identities = business.organization().identity()
//					.listWithPerson(effectivePerson.getDistinguishedName());
//			if (ListTools.isEmpty(identities)) {
//				throw new ExceptionNoneIdentity(effectivePerson.getDistinguishedName());
//			} else if (identities.size() == 1) {
//				return identities.get(0);
//			} else {
//				/* 신분이 여러 개이면 포함 여부를 하나씩 판단. */
//				for (String o : identities) {
//					if (StringUtils.equals(o, wi.getIdentity())) {
//						return o;
//					}
//				}
//			}
//		} else {
//			List<String> list = business.organization().identity()
//					.listWithPerson(effectivePerson.getDistinguishedName());
//			if (!list.isEmpty()) {
//				return list.get(0);
//			}
//		}
//		throw new Exception("decideCreatorIdentity error:" + wi.toString());
//	}
//
//	private void referenceTask(Business business, Wo wo) throws Exception {
//		List<String> ids = business.task().listWithActivityToken(wo.getFromActivityToken());
//		List<WoTask> list = WoTask.copier.copy(business.entityManagerContainer().list(Task.class, ids));
//		SortTools.asc(list, false, "startTime");
//		wo.setTaskList(list);
//	}
//
//	private void referenceTaskCompleted(Business business, Wo wo) throws Exception {
//		List<String> ids = business.taskCompleted().listWithActivityToken(wo.getFromActivityToken());
//		List<WoTaskCompleted> list = WoTaskCompleted.copier
//				.copy(business.entityManagerContainer().list(TaskCompleted.class, ids));
//		Collections.sort(list, new Comparator<WoTaskCompleted>() {
//			public int compare(WoTaskCompleted o1, WoTaskCompleted o2) {
//				return ObjectUtils.compare(o1.getCompletedTime(), o2.getCompletedTime(), true);
//			}
//		});
//		wo.setTaskCompletedList(list);
//	}
//
//}
