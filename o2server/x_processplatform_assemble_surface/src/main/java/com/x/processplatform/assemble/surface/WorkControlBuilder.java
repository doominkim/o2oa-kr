package com.x.processplatform.assemble.surface;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.PropertyTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;
import com.x.processplatform.core.entity.ticket.Ticket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class WorkControlBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorkControlBuilder.class);

	private EffectivePerson effectivePerson;
	private Business business;
	private Work work;

	public WorkControlBuilder(EffectivePerson effectivePerson, Business business, Work work) {
		this.effectivePerson = effectivePerson;
		this.business = business;
		this.work = work;
	}

	// 관리 가능 여부
	private boolean ifAllowManage = false;
	// 조회 가능 여부
	private boolean ifAllowVisit = false;
	// 직접 진행 가능 여부
	private boolean ifAllowProcessing = false;
	// 열람 대기 처리 가능 여부
	private boolean ifAllowReadProcessing = false;
	// 데이터 저장 가능 여부
	private boolean ifAllowSave = false;
	// 처리자 재설정 가능 여부
	private boolean ifAllowReset = false;
	// 추가 결재자 지정 가능 여부
	private boolean ifAllowAddTask = false;
	// 조정 가능 여부
	private boolean ifAllowReroute = false;
	// 삭제 가능 여부
	private boolean ifAllowDelete = false;
	// 합의 분기 추가 가능 여부
	private boolean ifAllowAddSplit = false;
	// 회수 가능 여부
	private boolean ifAllowRetract = false;
	// 회수 가능 여부 V3
	private boolean ifAllowV3Retract = false;
	// 롤백 가능 여부
	private boolean ifAllowRollback = false;
	// 알림 가능 여부
	private boolean ifAllowPress = false;
	// 결재 대기 보류(결재 대기 시간 측정 일시정지) 가능 여부
	private boolean ifAllowPause = false;
	// 결재 대기 보류 취소(결재 대기 시간 측정 재개) 가능 여부
	private boolean ifAllowResume = false;
	// 반려 가능 여부
	private boolean ifAllowGoBack = false;
	// 종료 가능 여부
	private boolean ifAllowTerminate = false;

	public WorkControlBuilder enableAllowManage() {
		this.ifAllowManage = true;
		return this;
	}

	public WorkControlBuilder enableAllowVisit() {
		this.ifAllowVisit = true;
		return this;
	}

	public WorkControlBuilder enableAllowProcessing() {
		this.ifAllowProcessing = true;
		return this;
	}

	public WorkControlBuilder enableAllowReadProcessing() {
		this.ifAllowReadProcessing = true;
		return this;
	}

	public WorkControlBuilder enableAllowSave() {
		this.ifAllowSave = true;
		return this;
	}

	public WorkControlBuilder enableAllowReset() {
		this.ifAllowReset = true;
		return this;
	}

	public WorkControlBuilder enableAllowAddTask() {
		this.ifAllowAddTask = true;
		return this;
	}

	public WorkControlBuilder enableAllowReroute() {
		this.ifAllowReroute = true;
		return this;
	}

	public WorkControlBuilder enableAllowDelete() {
		this.ifAllowDelete = true;
		return this;
	}

	public WorkControlBuilder enableAllowAddSplit() {
		this.ifAllowAddSplit = true;
		return this;
	}

	public WorkControlBuilder enableAllowRetract() {
		this.ifAllowRetract = true;
		return this;
	}

	public WorkControlBuilder enableAllowV3Retract() {
		this.ifAllowV3Retract = true;
		return this;
	}

	public WorkControlBuilder enableAllowRollback() {
		this.ifAllowRollback = true;
		return this;
	}

	public WorkControlBuilder enableAllowPress() {
		this.ifAllowPress = true;
		return this;
	}

	public WorkControlBuilder enableAllowPause() {
		this.ifAllowPause = true;
		return this;
	}

	public WorkControlBuilder enableAllowResume() {
		this.ifAllowResume = true;
		return this;
	}

	public WorkControlBuilder enableAllowGoBack() {
		this.ifAllowGoBack = true;
		return this;
	}

	public WorkControlBuilder enableAllowTerminate() {
		this.ifAllowTerminate = true;
		return this;
	}

	public WorkControlBuilder enableAll() {
		enableAllowManage();
		enableAllowVisit();
		enableAllowProcessing();
		enableAllowReadProcessing();
		enableAllowSave();
		enableAllowReset();
		enableAllowAddTask();
		enableAllowReroute();
		enableAllowDelete();
		enableAllowAddSplit();
		enableAllowRetract();
		enableAllowV3Retract();
		enableAllowRollback();
		enableAllowPress();
		enableAllowPause();
		enableAllowResume();
		enableAllowGoBack();
		enableAllowTerminate();
		return this;
	}

	private Boolean canManage = null;

	/**
	 * 앱 또는 프로세스 관리 가능 여부 판단, 추가로 review의 permissionWrite 표시 판단
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean canManage() throws Exception {
		if (null == canManage) {
			this.canManage = this.business.ifPersonCanManageApplicationOrProcess(this.effectivePerson,
					this.work.getApplication(), this.work.getProcess())
					|| this.business.ifPersonHasPermissionWriteReviewWithJob(this.effectivePerson, this.work.getJob());
		}
		return this.canManage;
	}

	private Boolean readable = null;

	private boolean readable() throws Exception {
		if (null == readable) {
			this.readable = ((!BooleanUtils.isTrue(Config.ternaryManagement().getSecurityClearanceEnable()))
					|| business.ifPersonHasSufficientSecurityClearance(effectivePerson.getDistinguishedName(),
							work.getObjectSecurityClearance()))
					&& (business.ifPersonHasTaskReadTaskCompletedReadCompletedReviewWithJob(
							effectivePerson.getDistinguishedName(), work.getJob())
							|| business.ifJobHasBeenCorrelation(effectivePerson.getDistinguishedName(), work.getJob()));
		}
		return this.readable;
	}

	// 초깃값은 반드시 null이어야 하며, hasTaskWithWork 계산 여부 판단에 사용
	private Optional<Task> hasTaskWithWork = null;

	private Optional<Task> hasTaskWithWork() throws Exception {
		if (null == hasTaskWithWork) {
			this.hasTaskWithWork = business.ifPersonHasTaskWithWork(effectivePerson.getDistinguishedName(),
					work.getId());
		}
		return this.hasTaskWithWork;
	}

	private Boolean hasReadWithJob = null;

	private boolean hasReadWithJob() throws Exception {
		if (null == hasReadWithJob) {
			this.hasReadWithJob = business.ifPersonHasReadWithJob(effectivePerson.getDistinguishedName(),
					work.getJob());
		}
		return this.hasReadWithJob;
	}

	private Activity activity = null;

	private Activity activity() throws Exception {
		if (null == activity) {
			this.activity = business.getActivity(work);
		}
		return this.activity;
	}

	private Boolean hasTaskCompletedWithJob = null;

	private boolean hasTaskCompletedWithJob() throws Exception {
		if (null == hasTaskCompletedWithJob) {
			this.hasTaskCompletedWithJob = business
					.ifPersonHasTaskCompletedWithJob(effectivePerson.getDistinguishedName(), work.getJob());
		}
		return this.hasTaskCompletedWithJob;
	}

	private Boolean hasPauseTaskWithWork = null;

	private boolean hasPauseTaskWithWork() throws Exception {
		if (null == hasPauseTaskWithWork) {
			this.hasPauseTaskWithWork = business.ifPersonHasPauseTaskWithWork(effectivePerson.getDistinguishedName(),
					work.getId());
		}
		return this.hasPauseTaskWithWork;
	}

	private WorkLogTree workLogTree = null;

	private WorkLogTree workLogTree() throws Exception {
		if (null == this.workLogTree) {
			this.workLogTree = new WorkLogTree(business.entityManagerContainer().fetchEqual(WorkLog.class,
					WorkLogTree.RELY_WORKLOG_ITEMS, WorkLog.JOB_FIELDNAME, work.getJob()));
		}
		return this.workLogTree;
	}

	private Long taskCountWithWork = null;

	private Long taskCountWithWork() throws Exception {
		if (null == taskCountWithWork) {
			this.taskCountWithWork = business.entityManagerContainer().countEqual(Task.class, Task.work_FIELDNAME,
					work.getId());
		}
		return this.taskCountWithWork;
	}

	private Map<String, Boolean> hasTaskCompletedWithActivityToken = new HashMap<>();

	private boolean hasTaskCompletedWithActivityToken(EffectivePerson effectivePerson, Business business,
			String activityToken) {
		return this.hasTaskCompletedWithActivityToken.computeIfAbsent(activityToken, k -> {
			try {
				return business.entityManagerContainer().countEqualAndEqual(TaskCompleted.class,
						TaskCompleted.person_FIELDNAME, effectivePerson.getDistinguishedName(),
						TaskCompleted.activityToken_FIELDNAME, activityToken) > 0;
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return false;
		});
	}

	private TaskCompleted getLastTaskCompletedWithActivityToken(EffectivePerson effectivePerson, Business business,
			String activityToken) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskCompleted> cq = cb.createQuery(TaskCompleted.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.person), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.job), work.getJob()));
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.activityToken), activityToken));
		List<TaskCompleted> list = em.createQuery(cq.where(p).orderBy(cb.desc(root.get(JpaObject_.createTime))))
				.setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	public Control build() {
		Control control = new Control();
		if (null == work) {
			return control;
		}
		control.setWorkTitle(work.getTitle());
		control.setCreatorPerson(work.getCreatorPerson());
		control.setWorkJob(work.getJob());
		Arrays.<Pair<Boolean, Consumer<Control>>>asList(Pair.of(ifAllowManage, this::computeAllowManage),
				Pair.of(ifAllowVisit, this::computeAllowVisit),
				Pair.of(ifAllowProcessing, this::computeAllowProcessing),
				Pair.of(ifAllowReadProcessing, this::computeAllowReadProcessing),
				Pair.of(ifAllowSave, this::computeAllowSave), Pair.of(ifAllowReset, this::computeAllowReset),
				Pair.of(ifAllowAddTask, this::computeAllowAddTask), Pair.of(ifAllowReroute, this::computeAllowReroute),
				Pair.of(ifAllowDelete, this::computeAllowDelete), Pair.of(ifAllowAddSplit, this::computeAllowAddSplit),
				Pair.of(ifAllowRetract, this::computeAllowRetract),
				Pair.of(ifAllowV3Retract, this::computeAllowV3Retract),
				Pair.of(ifAllowRollback, this::computeAllowRollback), Pair.of(ifAllowPress, this::computeAllowPress),
				Pair.of(ifAllowPause, this::computeAllowPause), Pair.of(ifAllowResume, this::computeAllowResume),
				Pair.of(ifAllowGoBack, this::computeAllowGoBack),
				Pair.of(ifAllowTerminate, this::computeAllowTerminate)).stream().filter(Pair::first)
				.forEach(o -> o.second().accept(control));
		recalculate(work, control);
		return control;
	}

	private void computeAllowManage(Control control) {
		try {
			control.setAllowManage(canManage());
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowVisit(Control control) {
		try {
			control.setAllowVisit(canManage() || readable());
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowProcessing(Control control) {
		try {
			control.setAllowProcessing(hasTaskWithWork().isPresent());
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowReadProcessing(Control control) {
		try {
			control.setAllowReadProcessing(hasReadWithJob());
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowSave(Control control) {
		try {
			control.setAllowSave(canManage() || hasTaskWithWork().isPresent());
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowReset(Control control) {
		try {
			control.setAllowReset(Objects.nonNull(activity())
					&& PropertyTools.getOrElse(activity(), Manual.allowReset_FIELDNAME, Boolean.class, false)
					&& hasTaskWithWork().isPresent());
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowAddTask(Control control) {
		try {
			control.setAllowAddTask(Objects.nonNull(activity())
					&& PropertyTools.getOrElse(activity(), Manual.ALLOWADDTASK_FIELDNAME, Boolean.class, true)
					&& hasTaskWithWork().isPresent());
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowReroute(Control control) {
		try {
			control.setAllowReroute(canManage() && Objects.nonNull(activity())
					&& PropertyTools.getOrElse(activity(), Activity.allowReroute_FIELDNAME, Boolean.class, false));
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	/**
	 * 관리자는 삭제 가능, 또는 활동에 삭제 허용이 설정되고 && 결재 대기가 있는 경우
	 *
	 * @param control
	 */
	private void computeAllowDelete(Control control) {
		try {
			control.setAllowDelete(Objects.nonNull(activity())
					&& (PropertyTools.getOrElse(activity(), Manual.allowDeleteWork_FIELDNAME, Boolean.class, false)
							&& (canManage() || hasTaskWithWork().isPresent())));
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowAddSplit(Control control) {
		try {
			control.setAllowAddSplit(false);
			if (Objects.nonNull(activity())
					&& BooleanUtils.isTrue(
							PropertyTools.getOrElse(activity(), Manual.allowAddSplit_FIELDNAME, Boolean.class, false))
					&& BooleanUtils.isTrue(work.getSplitting())) {
				Node node = this.workLogTree().location(work);
				if (null != node) {
					computeAllowAddSplitLoopNode(control, work);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowAddSplitLoopNode(Control control, Work work) throws Exception {
		List<Node> list = this.workLogTree().nodes().stream()
				.filter(o -> Objects.equals(work.getSplitToken(), o.getWorkLog().getSplitToken())
						&& Objects.equals(ActivityType.split, o.getWorkLog().getFromActivityType()))
				.collect(Collectors.toList());
		for (Node n : list) {
			Nodes manuals = n.upTo(ActivityType.manual);
			for (Node m : manuals) {
				if (this.hasTaskCompletedWithActivityToken(effectivePerson, business,
						m.getWorkLog().getFromActivityToken())) {
					control.setAllowAddSplit(true);
					return;
				}
			}
		}
	}

//	private void computeAllowAddSplitLoopNode(Control control, Node node) {
//		Nodes nodes = new Nodes();
//		nodes.add(node);
//		for (int i = 0; i < work.getSplitTokenList().size(); i++) {
//			List<Node> temps = new ArrayList<>();
//			for (Node n : nodes) {
//				Nodes ups = n.upTo(ActivityType.split);
//				temps.addAll(ups);
//				for (Node u : ups) {
//					Nodes manuals = u.upTo(ActivityType.manual);
//					for (Node m : manuals) {
//						if (this.hasTaskCompletedWithActivityToken(effectivePerson, business,
//								m.getWorkLog().getFromActivityToken())) {
//							control.setAllowAddSplit(true);
//							return;
//						}
//					}
//				}
//			}
//			nodes.clear();
//			nodes.addAll(temps);
//		}
//	}

	/**
	 * 회수 가능 여부는 세 가지로 판단 1. 활동 단계에서 회수 허용 설정 2. 다중 인원 활동(순차·병렬)에서 아무도 처리하지 않음, 즉 현재 활동의 처리 완료가 없음
	 * 3. 되짚은 활동이 일부 비인력 단계를 거쳤다면 회수 가능 4. 분할 후 어느 업무 문서라도 처리되었으면 회수 불가
	 * 5. 병합 단계를 거쳐 진행된 경우 회수 불가.
	 *
	 * @param control 컨트롤러
	 */
	private void computeAllowRetract(Control control) {
		try {
			control.setAllowRetract(false);
			TaskCompleted taskCompleted = null;
			EntityManagerContainer emc = business.entityManagerContainer();
			if (Objects.nonNull(activity())
					&& BooleanUtils.isTrue(
					PropertyTools.getOrElse(activity(), Manual.allowRetract_FIELDNAME, Boolean.class, false))
					&& (emc.countEqualAndEqual(TaskCompleted.class,
					TaskCompleted.job_FIELDNAME, work.getJob(), TaskCompleted.activityToken_FIELDNAME,
					work.getActivityToken()) == 0)) {
				Node node = this.workLogTree().location(work);
				if (null != node) {
					Nodes ups = node.upTo(ActivityType.manual, ActivityType.agent, ActivityType.choice,
							ActivityType.delay, ActivityType.embed, ActivityType.invoke, ActivityType.parallel,
							ActivityType.split, ActivityType.publish);
					for (Node o : ups) {
						taskCompleted = this.getLastTaskCompletedWithActivityToken(effectivePerson, business, o.getWorkLog().getFromActivityToken());
						if (taskCompleted != null) {
							break;
						}
					}
				}
			}
			if(taskCompleted != null){
				final TaskCompleted lastTaskCompleted = taskCompleted;
				List<WorkLog> workLogs = emc.listEqual(WorkLog.class,
						WorkLog.JOB_FIELDNAME, taskCompleted.getJob());
				List<WorkLog> down = WorkLog.downTo(workLogs,
						workLogs.stream()
								.filter(o -> Objects.equals(o.getFromActivityToken(), lastTaskCompleted.getActivityToken()))
								.collect(Collectors.toList()),
						ActivityType.manual);
				if(!down.isEmpty() && down.stream().noneMatch(o -> BooleanUtils.isTrue(o.getConnected()))) {
					List<String> activityTokens = down.stream()
							.map(WorkLog::getFromActivityToken).filter(StringUtils::isNotBlank)
							.distinct()
							.collect(Collectors.toList());
					Long count = emc.countEqualAndIn(TaskCompleted.class, TaskCompleted.job_FIELDNAME,
							taskCompleted.getJob(), TaskCompleted.activityToken_FIELDNAME,
							activityTokens);
					if (count == 0 && emc.countEqualAndIn(Work.class, Work.job_FIELDNAME,
							taskCompleted.getJob(), Work.activityToken_FIELDNAME,
							activityTokens) > 0) {
						control.setAllowRetract(true);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowV3Retract(Control control) {
		try {
			control.setAllowV3Retract(false);
			EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TaskCompleted> cq = cb.createQuery(TaskCompleted.class);
			Root<TaskCompleted> root = cq.from(TaskCompleted.class);
			Predicate p = cb.equal(root.get(TaskCompleted_.person), effectivePerson.getDistinguishedName());
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.job), work.getJob()));
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.joinInquire), true));
			List<TaskCompleted> list = em.createQuery(cq.where(p).orderBy(cb.desc(root.get(JpaObject_.createTime))))
					.setMaxResults(1).getResultList();
			if (!list.isEmpty()) {
				TaskCompleted taskCompleted = list.get(0);
				List<WorkLog> workLogs = business.entityManagerContainer().listEqual(WorkLog.class,
						WorkLog.JOB_FIELDNAME, taskCompleted.getJob());
				List<WorkLog> down = WorkLog.downTo(workLogs,
						workLogs.stream()
								.filter(o -> Objects.equals(o.getFromActivityToken(), taskCompleted.getActivityToken()))
								.collect(Collectors.toList()),
						ActivityType.manual);
				List<String> activityTokens = down.stream().filter(o -> BooleanUtils.isNotTrue(o.getConnected()))
						.map(WorkLog::getFromActivityToken).filter(StringUtils::isNotBlank).distinct()
						.collect(Collectors.toList());
				if (business.entityManagerContainer().countEqualAndIn(Work.class, Work.job_FIELDNAME,
						taskCompleted.getJob(), Work.activityToken_FIELDNAME, activityTokens) > 0) {
					control.setAllowV3Retract(true);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowRollback(Control control) {
		try {
			control.setAllowRollback(canManage() && Objects.nonNull(activity())
					&& PropertyTools.getOrElse(activity(), Manual.allowRollback_FIELDNAME, Boolean.class, false));
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	/**
	 * 조건: 1. 알림 허용, 2. 처리 완료 있음, 3. 현재 사용자의 결재 대기 한 건만 있는 경우가 아님.
	 *
	 * @param control
	 */
	private void computeAllowPress(Control control) {
		try {
			boolean tag = Objects.nonNull(activity())
					&& PropertyTools.getOrElse(activity(), Manual.allowPress_FIELDNAME, Boolean.class, false)
					&& hasTaskCompletedWithJob() && (!((taskCountWithWork() == 1) && hasTaskWithWork().isPresent()));
			control.setAllowPress(tag);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowPause(Control control) {
		try {
			control.setAllowPause(false);
			if (Objects.nonNull(activity())
					&& BooleanUtils.isTrue(
							PropertyTools.getOrElse(activity(), Manual.allowPause_FIELDNAME, Boolean.class, false))
					&& BooleanUtils.isTrue(control.getAllowSave())) {
				// 이미 보류 상태이면 재개 허용
				control.setAllowPause(!hasPauseTaskWithWork());
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowResume(Control control) {
		try {
			control.setAllowResume(false);
			if (Objects.nonNull(activity())
					&& PropertyTools.getOrElse(activity(), Manual.allowPause_FIELDNAME, Boolean.class, false)
					&& control.getAllowSave()) {
				// 이미 보류 상태이면 재개 허용
				control.setAllowResume(hasPauseTaskWithWork());
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowGoBack(Control control) {
		try {
			control.setAllowGoBack(false);
			if (Objects.nonNull(activity()) && activity().getClass().isAssignableFrom(Manual.class)) {
				Manual manual = (Manual) activity;
				if (hasTaskWithWork().isPresent() && BooleanUtils.isNotFalse(manual.getAllowGoBack())) {
					Optional<Ticket> opt = work.getTickets().findTicketWithLabel(hasTaskWithWork.get().getLabel());
					if (opt.isPresent() && (BooleanUtils.isNotFalse(manual.getGoBackConfig().getMultiTaskEnable())
							|| ListUtils.intersection(
									work.getTickets().bubble().stream().map(Ticket::label).collect(Collectors.toList()),
									opt.get().fellow()).isEmpty())) {
						control.setAllowGoBack(true);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void computeAllowTerminate(Control control) {
		try {
			control.setAllowTerminate(false);
			if (Objects.nonNull(activity()) && activity().getClass().isAssignableFrom(Manual.class)) {
				Manual manual = (Manual) activity;
				if (BooleanUtils.isTrue(manual.getAllowTerminate()) && (canManage() || hasTaskWithWork().isPresent())) {
					control.setAllowTerminate(true);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	/**
	 * 반려 처리 중 getGoBackStore가 있으면 다음 단계에서 jump가 필요하므로 아래 기능 비활성화.
	 *
	 * @param wo
	 * @param work
	 */
	private void recalculate(Work work, Control ctrl) {
		if (null != work.getGoBackStore()) {
			recalculateHasGoBackStore(ctrl);
		}
	}

	private void recalculateHasGoBackStore(Control ctrl) {
		if (null != ctrl.getAllowAddTask()) {
			ctrl.setAllowAddTask(false);
		}
		if (null != ctrl.getAllowReset()) {
			ctrl.setAllowReset(false);
		}
		if (null != ctrl.getAllowPause()) {
			ctrl.setAllowPause(false);
		}
		if (null != ctrl.getAllowResume()) {
			ctrl.setAllowResume(false);
		}
		if (null != ctrl.getAllowAddSplit()) {
			ctrl.setAllowAddSplit(false);
		}
		if (null != ctrl.getAllowRetract()) {
			ctrl.setAllowRetract(false);
		}
		if (null != ctrl.getAllowGoBack()) {
			ctrl.setAllowGoBack(false);
		}
		if (null != ctrl.getAllowRollback()) {
			ctrl.setAllowRollback(false);
		}
	}
}
