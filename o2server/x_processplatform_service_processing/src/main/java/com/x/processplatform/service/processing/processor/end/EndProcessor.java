package com.x.processplatform.service.processing.processor.end;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Source;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.core.entity.message.Event;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.Processing;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class EndProcessor extends AbstractEndProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(EndProcessor.class);

	public EndProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, End end) throws Exception {
		// ProcessingSignal 발송
		aeiObjects.getProcessingAttributes().push(Signal.endArrive(aeiObjects.getWork().getActivityToken(), end));
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, End end) throws Exception {
		// nothing
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, End end) throws Exception {
		// ProcessingSignal 발송
		aeiObjects.getProcessingAttributes().push(Signal.endExecute(aeiObjects.getWork().getActivityToken(), end));
		List<Work> results = new ArrayList<>();
		Work other = aeiObjects.getWorks().stream().filter(o -> o != aeiObjects.getWork())
				.sorted(Comparator.comparing(Work::getCreateTime)).findFirst().orElse(null);
		if (null != other) {
			aeiObjects.getUpdateWorks().add(other);
			aeiObjects.getDeleteWorks().add(aeiObjects.getWork());
			this.mergeTaskCompleted(aeiObjects, aeiObjects.getWork(), other);
			this.mergeRead(aeiObjects, aeiObjects.getWork(), other);
			this.mergeReadCompleted(aeiObjects, aeiObjects.getWork(), other);
			this.mergeReview(aeiObjects, aeiObjects.getWork(), other);
			this.mergeAttachment(aeiObjects, aeiObjects.getWork(), other);
			this.mergeWorkLog(aeiObjects, aeiObjects.getWork(), other);
			this.mergeRecord(aeiObjects, aeiObjects.getWork(), other);
			aeiObjects.getWorkLogs().stream()
					.filter(p -> StringUtils.equals(p.getFromActivityToken(), aeiObjects.getWork().getActivityToken()))
					.forEach(obj -> aeiObjects.getDeleteWorkLogs().add(obj));
		} else {
			WorkCompleted workCompleted = this.createWorkCompleted(aeiObjects.getWork(), end);
			workCompleted.setAllowRollback(end.getAllowRollback());
			aeiObjects.getCreateWorkCompleteds().add(workCompleted);
			aeiObjects.getTasks().forEach(o -> aeiObjects.getDeleteTasks().add(o));
			aeiObjects.getTaskCompleteds().forEach(o -> {
				// 처리 완료의 완료 시간은 업데이트할 필요 없음
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				// 매핑 필드 재할당
				o.copyProjectionFields(workCompleted);
				// 트랜잭션 시작 보장을 위해 업데이트 큐에 추가
				aeiObjects.getUpdateTaskCompleteds().add(o);
			});
			aeiObjects.getReads().forEach(o -> {
				// 열람 대기의 완료 시간은 업데이트할 필요 없음
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				// 매핑 필드 재할당
				o.copyProjectionFields(workCompleted);
				// 트랜잭션 시작 보장을 위해 업데이트 큐에 추가
				aeiObjects.getUpdateReads().add(o);
			});
			aeiObjects.getReadCompleteds().forEach(o -> {
				// 열람 완료의 완료 시간은 업데이트할 필요 없음
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				// 매핑 필드 재할당
				o.copyProjectionFields(workCompleted);
				// 트랜잭션 시작 보장을 위해 업데이트 큐에 추가
				aeiObjects.getUpdateReadCompleteds().add(o);
			});
			aeiObjects.getRecords().forEach(o -> {
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				aeiObjects.getUpdateRecords().add(o);
			});
			aeiObjects.getReviews().forEach(o -> {
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				o.setCompletedTime(workCompleted.getCompletedTime());
				o.setCompletedTimeMonth(workCompleted.getCompletedTimeMonth());
				// 매핑 필드 재할당
				o.copyProjectionFields(workCompleted);
				// 트랜잭션 시작 보장을 위해 업데이트 큐에 추가
				aeiObjects.getUpdateReviews().add(o);
			});
			aeiObjects.getWorkLogs().forEach(o -> {
				o.setSplitting(false);
				o.setSplitToken("");
				o.getProperties().setSplitTokenList(new ArrayList<>());
				o.setSplitValue("");
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				// 트랜잭션 시작 보장을 위해 업데이트 큐에 추가
				aeiObjects.getUpdateWorkLogs().add(o);
				// 연결되지 않은 WorkLog 삭제 d0431924-849f-4bf6-81b2-bfe997e62b7f
				if (BooleanUtils.isNotTrue(o.getConnected())) {
					aeiObjects.getDeleteWorkLogs().add(o);
				}
			});
			aeiObjects.getAttachments().forEach(o -> {
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				// 트랜잭션 시작 보장을 위해 업데이트 큐에 추가
				aeiObjects.getUpdateAttachments().add(o);
			});
			// workCompleted 데이터를 기준으로 업데이트
			aeiObjects.getData().setWork(workCompleted);
			aeiObjects.getData().setAttachmentList(aeiObjects.getAttachments());
			aeiObjects.getDeleteWorks().addAll(aeiObjects.getWorks());
			// 스냅샷 삭제
			aeiObjects.getDeleteSnaps().addAll(aeiObjects.getSnaps());
		}
		// work를 반환해야 함, 그렇지 않으면 이벤트 실행 불가.
		results.add(aeiObjects.getWork());
		return results;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, End end, List<Work> works) throws Exception {
		if (StringUtils.isNotEmpty(aeiObjects.getProcess().getAfterEndScript())
				|| StringUtils.isNotEmpty(aeiObjects.getProcess().getAfterEndScriptText())) {
			Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
					aeiObjects.getProcess(), Business.EVENT_PROCESSAFTEREND);
			GraalvmScriptingFactory.eval(source, aeiObjects.bindings());
		}
		// 상위 Work에 다시 기록
		tryUpdateParentWork(aeiObjects);
		addUpdateTableEvent(aeiObjects);
	}

	private void addUpdateTableEvent(AeiObjects aeiObjects) {
		try {
			if (BooleanUtils.isTrue(aeiObjects.getProcess().getUpdateTableEnable())
					&& ListTools.isNotEmpty(aeiObjects.getProcess().getUpdateTableList())) {
				List<Event> events = new ArrayList<>();
				for (String table : aeiObjects.getProcess().getUpdateTableList()) {
					if (StringUtils.isNotEmpty(table)) {
						Event event = new Event();
						event.setTarget(table);
						event.setJob(aeiObjects.getWork().getJob());
						event.setType(Event.EVENTTYPE_UPDATETABLE);
						events.add(event);
					}
				}
				sendUpdateTableEvent(aeiObjects, events);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void sendUpdateTableEvent(AeiObjects aeiObjects, List<Event> events) throws Exception {
		if (!events.isEmpty()) {
			aeiObjects.entityManagerContainer().beginTransaction(Event.class);
			for (Event event : events) {
				aeiObjects.entityManagerContainer().persist(event, CheckPersistType.all);
			}
			aeiObjects.entityManagerContainer().commit();
			for (Event event : events) {
				ThisApplication.updateTableQueue.send(event.getId());
			}
		}
	}

	private void tryUpdateParentWork(AeiObjects aeiObjects) {
		if (StringUtils.isNotBlank(aeiObjects.getWork().getParentWork())) {
			try {
				Work parent = aeiObjects.entityManagerContainer().find(aeiObjects.getWork().getParentWork(),
						Work.class);
				if ((null != parent) && Objects.equals(parent.getActivityType(), ActivityType.embed)) {
					Embed embed = (Embed) aeiObjects.business().element().get(parent.getActivity(), ActivityType.embed);

					if ((null != embed) && BooleanUtils.isTrue(embed.getWaitUntilCompleted())) {
						updateParentWork(aeiObjects, parent, embed);
					}
				}
			} catch (Exception e) {
				LOGGER.error(new ExceptionUpdateParentWork(e, aeiObjects.getWork().getId(),
						aeiObjects.getWork().getParentWork()));
			}
		}
	}

	private void updateParentWork(AeiObjects aeiObjects, Work parent, Embed embed) throws Exception {
		// 상태값을 먼저 주입해 스크립트 실행 시 값을 가져올 수 있게 함.
		parent.setEmbedCompleted(ActivityType.end.toString());
		AeiObjects parentAeiObjects = new AeiObjects(aeiObjects.business(), parent, embed,
				aeiObjects.getProcessingAttributes());
		parentAeiObjects.entityManagerContainer().beginTransaction(Work.class);
		if (this.hasEmbedCompletedScript(embed) || this.hasEmbedCompletedEndScript(embed)) {
			GraalvmScriptingFactory.Bindings bindings = parentAeiObjects.bindings()
					.putMember(GraalvmScriptingFactory.BINDING_NAME_EMBEDDATA, aeiObjects.getData());
			if (this.hasEmbedCompletedScript(embed)) {
				Source source = parentAeiObjects.business().element().getCompiledScript(
						parentAeiObjects.getWork().getApplication(), embed, Business.EVENT_EMBEDCOMPLETED);
				GraalvmScriptingFactory.eval(source, bindings);
			}
			if (this.hasEmbedCompletedEndScript(embed)) {
				Source source = parentAeiObjects.business().element().getCompiledScript(
						parentAeiObjects.getWork().getApplication(), embed, Business.EVENT_EMBEDCOMPLETEDEND);
				GraalvmScriptingFactory.eval(source, bindings);
			}
		}
		parentAeiObjects.commit();
		touchWork(parent.getId());
//		if (this.hasEmbedCompletedScript(embed) || this.hasEmbedCompletedEndScript(embed)) {
//		GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings()
//				.putMember(GraalvmScriptingFactory.BINDING_NAME_EMBEDDATA, aeiObjects.getData());
//		if (this.hasEmbedCompletedScript(embed)) {
//			Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
//					embed, Business.EVENT_EMBEDCOMPLETED);
//			GraalvmScriptingFactory.eval(source, bindings);
//		}
//		if (this.hasEmbedCompletedEndScript(embed)) {
//			Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
//					embed, Business.EVENT_EMBEDCOMPLETEDEND);
//			GraalvmScriptingFactory.eval(source, bindings);
//		}
//		aeiObjects.getWorkDataHelper().update(aeiObjects.getData());
//	}

	}

	/**
	 * 대기 상태의 상위 문서 트리거
	 *
	 * @param workId
	 */
	private void touchWork(String workId) {
		new Thread(EndProcessor.class.getName() + "_touchWork") {
			@Override
			public void run() {
				try {
					new Processing(new ProcessingAttributes()).processing(workId);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
		}.start();
	}

	@Override
	protected Optional<Route> inquiring(AeiObjects aeiObjects, End end) throws Exception {
		// ProcessingSignal 발송
		aeiObjects.getProcessingAttributes().push(Signal.endInquire(aeiObjects.getWork().getActivityToken(), end));
		if (StringUtils.isNotEmpty(aeiObjects.getWork().getDestinationActivity())
				&& Objects.nonNull(aeiObjects.getWork().getDestinationActivityType())
				&& BooleanUtils.isTrue(aeiObjects.getWork().getForceRouteEnable())) {
			Activity activity = aeiObjects.business().element()
					.getActivity(aeiObjects.getWork().getDestinationActivity());
			if (null != activity) {
				Route route = new Route();
				route.setActivity(activity.getId());
				route.setActivityType(activity.getActivityType());
				// goBackStore 정리
				aeiObjects.getWork().setGoBackStore(null);
				// 반려 대상 activityToken 표시 정리
				aeiObjects.getWork().setGoBackActivityToken(null);
				return Optional.of(route);
			}
		}
		return Optional.empty();
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, End end) throws Exception {
		// nothing
	}

	/* work와 data를 기준으로 최종 저장할 workCompleted 생성 */
	private WorkCompleted createWorkCompleted(Work work, End end) throws Exception {
		Date completedTime = new Date();
		Long duration = Config.workTime().betweenMinutes(work.getStartTime(), completedTime);
		WorkCompleted workCompleted = new WorkCompleted(work, completedTime, duration);
		workCompleted.setCompletedType(WorkCompleted.COMPLETEDTYPE_END);
		workCompleted.setActivity(end.getId());
		workCompleted.setActivityAlias(end.getAlias());
		workCompleted.setActivityDescription(end.getDescription());
		workCompleted.setActivityName(end.getName());
		return workCompleted;
	}
}
