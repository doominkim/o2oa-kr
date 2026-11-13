package com.x.processplatform.service.processing.processor.cancel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Source;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.Processing;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class CancelProcessor extends AbstractCancelProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(CancelProcessor.class);

	public CancelProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Cancel cancel) throws Exception {
		// ProcessingSignal 발송
		aeiObjects.getProcessingAttributes().push(Signal.cancelArrive(aeiObjects.getWork().getActivityToken(), cancel));
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Cancel cancel) throws Exception {
		// nothing
	}

	@Override
	public List<Work> executing(AeiObjects aeiObjects, Cancel cancel) throws Exception {
		// ProcessingSignal 발송
		aeiObjects.getProcessingAttributes()
				.push(Signal.cancelExecute(aeiObjects.getWork().getActivityToken(), cancel));
		// 유일 work 처리
		if (aeiObjects.getWorks().size() > 1) {
			aeiObjects.getDeleteWorks().add(aeiObjects.getWork());
			aeiObjects.getTasks().stream().filter(o -> StringUtils.equals(o.getWork(), aeiObjects.getWork().getId()))
					.forEach(o -> aeiObjects.getDeleteTasks().add(o));
		} else {
			aeiObjects.getTasks().stream().forEach(o -> aeiObjects.getDeleteTasks().add(o));
			aeiObjects.getTaskCompleteds().stream().forEach(o -> aeiObjects.getDeleteTaskCompleteds().add(o));
			aeiObjects.getReads().stream().forEach(o -> aeiObjects.getDeleteReads().add(o));
			aeiObjects.getReadCompleteds().stream().forEach(o -> aeiObjects.getDeleteReadCompleteds().add(o));
			aeiObjects.getReviews().stream().forEach(o -> aeiObjects.getDeleteReviews().add(o));
			aeiObjects.getDocumentVersions().stream().forEach(o -> aeiObjects.getDeleteDocumentVersions().add(o));
			aeiObjects.getDocSignScrawls().stream().forEach(o -> aeiObjects.getDeleteDocSignScrawls().add(o));
			aeiObjects.getDocSigns().stream().forEach(o -> aeiObjects.getDeleteDocSigns().add(o));
			aeiObjects.getRecords().stream().forEach(o -> aeiObjects.getDeleteRecords().add(o));
			aeiObjects.getWorkLogs().stream().forEach(o -> aeiObjects.getDeleteWorkLogs().add(o));
			// 첨부파일 삭제는 별도 처리, Attachment의 바이너리 파일 삭제 필요
			aeiObjects.getAttachments().stream().forEach(o -> aeiObjects.getDeleteAttachments().add(o));
			// 데이터가 하나뿐이고 분할되지 않았으면 Data 삭제
			aeiObjects.getWorkDataHelper().remove();
			aeiObjects.getWorks().stream().forEach(o -> aeiObjects.getDeleteWorks().add(o));
			// cancel은 스냅샷을 삭제하지 않아도 됨
		}
		// work를 반환해야 함, 그렇지 않으면 이벤트 실행 불가.
		List<Work> results = new ArrayList<>();
		results.add(aeiObjects.getWork());
		return results;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Cancel cancel, List<Work> works) throws Exception {
		// 삭제 후 다시 확인, 여러 사본이 있고 모두 End 상태이면 하나를 진행 시도
		if (!aeiObjects.getWorks().isEmpty()) {
			Predicate<Work> p = o -> Objects.equals(ActivityType.end, o.getActivityType());
			if (aeiObjects.getWorks().stream().allMatch(p)) {
				touchWork(aeiObjects.getWorks().get(0).getId());
			}
		}
		tryUpdateParentWork(aeiObjects);
	}

	private void tryUpdateParentWork(AeiObjects aeiObjects) {
		if (StringUtils.isNotBlank(aeiObjects.getWork().getParentWork())) {
			try {
				Work parent = aeiObjects.entityManagerContainer().find(aeiObjects.getWork().getParentWork(),
						Work.class);
				if ((null != parent) && Objects.equals(parent.getActivityType(), ActivityType.embed)) {
					Embed embed = (Embed) aeiObjects.business().element().get(parent.getActivity(),
							parent.getActivityType());
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
		parent.setEmbedCompleted(ActivityType.cancel.toString());
		AeiObjects embedAeiObjects = new AeiObjects(aeiObjects.business(), parent, embed,
				aeiObjects.getProcessingAttributes());
		embedAeiObjects.entityManagerContainer().beginTransaction(Work.class);
		if (this.hasEmbedCompletedScript(embed) || this.hasEmbedCompletedCancelScript(embed)) {
			GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings()
					.putMember(GraalvmScriptingFactory.BINDING_NAME_EMBEDDATA, aeiObjects.getData());
			if (this.hasEmbedCompletedScript(embed)) {
				Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
						embed, Business.EVENT_EMBEDCOMPLETED);
				GraalvmScriptingFactory.eval(source, bindings);
			}
			if (this.hasEmbedCompletedCancelScript(embed)) {
				Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
						embed, Business.EVENT_EMBEDCOMPLETEDCANCEL);
				GraalvmScriptingFactory.eval(source, bindings);
			}
		}
		embedAeiObjects.commit();
		touchWork(parent.getId());
	}

	/**
	 * 대기 상태의 상위 문서 트리거
	 * 
	 * @param parentWorkId
	 */
	private void touchWork(String workId) {
		new Thread(CancelProcessor.class.getName() + "_touchWork") {
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
	protected Optional<Route> inquiring(AeiObjects aeiObjects, Cancel cancel) throws Exception {
		// ProcessingSignal 발송
		aeiObjects.getProcessingAttributes()
				.push(Signal.cancelInquire(aeiObjects.getWork().getActivityToken(), cancel));
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
	protected void inquiringCommitted(AeiObjects aeiObjects, Cancel cancel) throws Exception {
		// nothing
	}

}