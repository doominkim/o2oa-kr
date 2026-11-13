package com.x.processplatform.service.processing.processor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Source;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.processplatform.ManualTaskIdentityMatrix;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Publish;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.SerialBuilder;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;

/***
 * 
 * 3가지 시간의 기본 기능 구현
 *
 */
public abstract class AbstractProcessor extends AbstractBaseProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProcessor.class);

	protected AbstractProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	public String arrive(String workId, ProcessingConfigurator processingConfigurator,
			ProcessingAttributes processingAttributes) {
		// 반환값, 반환값이 비어 있지 않으면 루프 계속
		try {
			Work work = this.entityManagerContainer().find(workId, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(workId);
			}
			ActivityType activityType = work.getDestinationActivityType();
			if (null == activityType) {
				throw new ExceptionEmptyActivityType(work.getTitle(), work.getId(), work.getDestinationActivityType());
			}
			Activity activity = this.business().element().get(work.getDestinationActivity(),
					ActivityType.getClassOfActivityType(activityType));
			if (null == activity) {
				throw new ExceptionActivityNotExist(work.getTitle(), work.getId(), work.getDestinationActivityType(),
						work.getDestinationActivity());
			}
			AeiObjects aeiObjects = new AeiObjects(this.business(), work, activity, processingAttributes);
			// 남아 있을 수 있는 Manual 활동 예상 사용자 비우기
			this.arriveCleanManualTaskIdentityMatrix(aeiObjects);
			// 남아 있을 수 있는 Manual 활동 위임 정보 비우기
			this.arriveCleanManualEmpowerMap(aeiObjects);
			// 수동 노드 경유 여부 계산
			this.arriveUpdateWorkThroughManual(aeiObjects);
			// BeforeExecuted 활동 1회 실행 이벤트 비우기
			work.setBeforeExecuted(false);
			aeiObjects.getUpdateWorks().add(work);
			this.callBeforeArriveScript(aeiObjects);
			this.arriveActivity(aeiObjects);
			// 열람 대기 및 참조 생성
			aeiObjects.getCreateReads().addAll(this.concreteRead(aeiObjects));
			aeiObjects.getCreateReviews().addAll(this.concreteReview(aeiObjects));
			// 주 메서드, 비즈니스 실행
			work = this.arriveProcessing(aeiObjects);
			// 주 메서드 종료
			if (null == work) {
				throw new IllegalStateException("arrvie return empty, work{id:" + workId + "}.");
			}
			if ((null != aeiObjects.getProcess())
					&& StringUtils.equalsIgnoreCase(aeiObjects.getProcess().getSerialActivity(),
							aeiObjects.getActivity().getId())
					&& (!StringUtils.equals(aeiObjects.getProcess().getSerialPhase(), Process.SERIALPHASE_INQUIRE))
					&& StringUtils.isEmpty(work.getSerial())) {
				SerialBuilder serialBuilder = new SerialBuilder(ThisApplication.context(),
						this.entityManagerContainer(), work.getProcess(), work.getId());
				String serial = serialBuilder.concrete(aeiObjects);
				work.setSerial(serial);
			}
			aeiObjects.commit();
			this.arriveCommitted(aeiObjects);
			// AfterArriveScript 이벤트 실행
			if (this.callAfterArriveScript(aeiObjects) && aeiObjects.commitData()) {
				aeiObjects.executeProjection();// 변경되었을 수 있는 데이터 매핑
				// AfterArriveScript의 코드 실행 시 data 데이터가 수정되었을 수 있음.
				aeiObjects.entityManagerContainer().commit();
			}
			return work.getId();
		} catch (Exception e) {
			LOGGER.error(e);
			return null;
		}
	}

	private void arriveCleanManualTaskIdentityMatrix(AeiObjects aeiObjects) {
		aeiObjects.getWork().setManualTaskIdentityMatrix(new ManualTaskIdentityMatrix());
	}

	private void arriveCleanManualEmpowerMap(AeiObjects aeiObjects) {
		aeiObjects.getWork().setManualEmpowerMap(new LinkedHashMap<>());
	}

	private void arriveUpdateWorkThroughManual(AeiObjects aeiObjects) throws Exception {
		boolean value = aeiObjects.getWorkLogs().stream()
				.filter(o -> Objects.equals(ActivityType.manual, o.getArrivedActivityType())
						&& BooleanUtils.isTrue(o.getConnected()))
				.count() > 0;
		aeiObjects.getWork().setWorkThroughManual(value);
	}

	private void callBeforeArriveScript(AeiObjects aeiObjects) throws Exception {
		if (BooleanUtils.isTrue(aeiObjects.getActivityProcessingConfigurator().getCallBeforeArriveScript())
				&& this.hasBeforeArriveScript(aeiObjects.getProcess(), aeiObjects.getActivity())) {
			Source source = null;
			if (this.hasBeforeArriveScript(aeiObjects.getProcess())) {
				source = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getProcess(), Business.EVENT_BEFOREARRIVE);
				GraalvmScriptingFactory.eval(source, aeiObjects.bindings());
			}
			if (this.hasBeforeArriveScript(aeiObjects.getActivity())) {
				source = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getActivity(), Business.EVENT_BEFOREARRIVE);
				GraalvmScriptingFactory.eval(source, aeiObjects.bindings());
			}
		}
	}

	private boolean callAfterArriveScript(AeiObjects aeiObjects) throws Exception {
		if (BooleanUtils.isTrue(aeiObjects.getActivityProcessingConfigurator().getCallAfterArriveScript())
				&& this.hasAfterArriveScript(aeiObjects.getProcess(), aeiObjects.getActivity())) {
			Source source = null;
			if (this.hasAfterArriveScript(aeiObjects.getProcess())) {
				source = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getProcess(), Business.EVENT_AFTERARRIVE);
				GraalvmScriptingFactory.eval(source, aeiObjects.bindings());
			}
			if (this.hasAfterArriveScript(aeiObjects.getActivity())) {
				source = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getActivity(), Business.EVENT_AFTERARRIVE);
				GraalvmScriptingFactory.eval(source, aeiObjects.bindings());
			}
			return true;
		}
		return false;
	}

	private List<Read> concreteRead(AeiObjects aeiObjects) throws Exception {
		List<Read> list = new ArrayList<>();
		List<String> identities = TranslateReadIdentityTools.translate(aeiObjects);
		for (String identity : ListTools.trim(identities, true, true)) {
			String unit = this.business().organization().unit().getWithIdentity(identity);
			String person = this.business().organization().person().getWithIdentity(identity);
			Read read = new Read(aeiObjects.getWork(), identity, unit, person);
			list.add(read);
		}
		return list;
	}

	private List<Review> concreteReview(AeiObjects aeiObjects) throws Exception {
		List<Review> list = new ArrayList<>();
		List<String> people = TranslateReviewPersonTools.translate(aeiObjects);
		for (String person : ListTools.trim(people, true, true)) {
			Review review = new Review(aeiObjects.getWork(), person);
			list.add(review);
		}
		return list;
	}

	public List<String> execute(String workId, ProcessingConfigurator processingConfigurator,
			ProcessingAttributes processingAttributes) {
		List<String> results = new ArrayList<>();
		try {
			Work work = this.entityManagerContainer().find(workId, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(workId);
			}
			ActivityType activityType = work.getActivityType();
			if (null == activityType) {
				throw new ExceptionEmptyActivityType(work.getTitle(), work.getId(), work.getActivityType());
			}
			Activity activity = this.business().element().get(work.getActivity(),
					ActivityType.getClassOfActivityType(activityType));
			if (null == activity) {
				throw new ExceptionActivityNotExist(work.getTitle(), work.getId(), work.getActivityType(),
						work.getActivity());
			}
			AeiObjects aeiObjects = new AeiObjects(this.business(), work, activity, processingAttributes);
			// 회수로 이 단계에 도착한 경우 arrive 단계를 건너뛰므로 여기서 activityUnique를 다시 할당해야 함
			work.setActivityUnique(aeiObjects.getActivity().getUnique());
			aeiObjects.getUpdateWorks().add(work);
			// 스케줄링 라우팅이면 froceRoute 재설정 필요
			if (BooleanUtils.isNotTrue(work.getBeforeExecuted())) {
				// BeforeExecuteScript의 코드는 1회만 실행
				this.callBeforeExecuteScript(aeiObjects);
				work.setBeforeExecuted(true);
			}
			List<Work> works = new ArrayList<>();
			// 8.2 이전 버전은 destinationActivity를 강제 라우팅으로 사용하지 않았음, 여기서 별도로 판단하지 않으면 구버전 데이터가 제자리에서 맴돌며 같은 단계에 다시 진입해 activityToken을 재생성하고, 그 결과 모든 결재 대기가 재생성됨.
			// 스케줄링 reroute는 이 코드로 실행을 건너뜀.
			if (StringUtils.isNotEmpty(work.getDestinationActivity())
					&& Objects.nonNull(work.getDestinationActivityType())
					&& BooleanUtils.isTrue(aeiObjects.getWork().getForceRouteEnable())) {
				works.add(work);
			} else {
				// 비즈니스 메서드 실행
				works.addAll(this.executeProcessing(aeiObjects));
			}
			if (ListTools.isNotEmpty(works)) {
				for (Work o : works) {
					results.add(o.getId());
				}
			}
			aeiObjects.commit();
			// 큐에 있는 결재 대기 메시지 발송, 결재 대기 메시지는 데이터 커밋 후 발송해야 함, 그렇지 않으면 결재 대기가 조회되지 않음
			this.executeCommitted(aeiObjects, works);
			if (ListTools.isNotEmpty(works) && callAfterExecuteScript(aeiObjects) && aeiObjects.commitData()) {
				aeiObjects.executeProjection();// 변경되었을 수 있는 데이터 매핑
				// 반환된 work가 현재 단계를 떠나려 함, AfterExecuteScript의 코드 실행 시 data 데이터가 수정되었을 수 있음.
				aeiObjects.entityManagerContainer().commit();
			}
//			if (StringUtils.isNotEmpty(aeiObjects.getProcess().getAfterExecuteScript())
//					|| StringUtils.isNotEmpty(aeiObjects.getProcess().getAfterExecuteScriptText())) {
//				Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
//						aeiObjects.getProcess(), Business.EVENT_AFTEREXECUTE);
//				GraalvmScriptingFactory.eval(source, aeiObjects.bindings());
//			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return results;
	}

	private void callBeforeExecuteScript(AeiObjects aeiObjects) throws Exception {
		if (BooleanUtils.isTrue(aeiObjects.getActivityProcessingConfigurator().getCallBeforeExecuteScript())
				&& this.hasBeforeExecuteScript(aeiObjects.getProcess(), aeiObjects.getActivity())) {
			Source source = null;
			if (this.hasBeforeExecuteScript(aeiObjects.getProcess())) {
				source = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getProcess(), Business.EVENT_BEFOREEXECUTE);
				GraalvmScriptingFactory.eval(source, aeiObjects.bindings());
			}
			if (this.hasBeforeExecuteScript(aeiObjects.getActivity())) {
				source = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getActivity(), Business.EVENT_BEFOREEXECUTE);
				GraalvmScriptingFactory.eval(source, aeiObjects.bindings());
			}
		}
	}

	private boolean callAfterExecuteScript(AeiObjects aeiObjects) throws Exception {
		if (BooleanUtils.isTrue(aeiObjects.getActivityProcessingConfigurator().getCallAfterExecuteScript())
				&& this.hasAfterExecuteScript(aeiObjects.getProcess(), aeiObjects.getActivity())) {
			Source source = null;
			if (this.hasAfterExecuteScript(aeiObjects.getProcess())) {
				source = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getProcess(), Business.EVENT_AFTEREXECUTE);
				GraalvmScriptingFactory.eval(source, aeiObjects.bindings());
			}
			if (this.hasAfterExecuteScript(aeiObjects.getActivity())) {
				source = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getActivity(), Business.EVENT_AFTEREXECUTE);
				GraalvmScriptingFactory.eval(source, aeiObjects.bindings());
			}
			return true;
		}
		return false;
	}

	public List<String> inquire(String workId, ProcessingConfigurator processingConfigurator,
			ProcessingAttributes processingAttributes) {
		List<String> results = new ArrayList<>();
		try {
			Work work = this.entityManagerContainer().find(workId, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(workId);
			}
			ActivityType activityType = work.getActivityType();
			if (null == activityType) {
				throw new ExceptionEmptyActivityType(work.getTitle(), work.getId(), work.getActivityType());
			}
			Activity activity = this.business().element().get(work.getActivity(),
					ActivityType.getClassOfActivityType(activityType));
			if (null == activity) {
				// 현재 활동이 삭제되었으면 work를 기준으로 가상 활동 생성;
				activity = this.createVirtualActivity(work);
			}
			AeiObjects aeiObjects = new AeiObjects(this.business(), work, activity, processingAttributes);
			aeiObjects.getUpdateWorks().add(work);
			// 라우팅 조회 전 스크립트 실행
			this.callBeforeInquireScript(aeiObjects);
			// 주 메서드 실행
			Route selectRoute = this.inquireProcessing(aeiObjects);
			// 주 메서드 실행 완료
			if (null == selectRoute) {
				return results;
			}
			List<Work> works = new ArrayList<>();
			// 라우팅 조회 후 스크립트 실행
			work.setDestinationActivity(selectRoute.getActivity());
			work.setDestinationActivityType(selectRoute.getActivityType());
			work.setDestinationRoute(selectRoute.getId());
			work.setDestinationRouteName(selectRoute.getName());
			works.add(work);
			for (Work o : works) {
				results.add(o.getId());
			}
			if ((null != aeiObjects.getProcess())
					&& StringUtils.equalsIgnoreCase(aeiObjects.getProcess().getSerialActivity(),
							aeiObjects.getActivity().getId())
					&& (StringUtils.equals(aeiObjects.getProcess().getSerialPhase(), Process.SERIALPHASE_INQUIRE))
					&& StringUtils.isEmpty(work.getSerial())) {
				SerialBuilder serialBuilder = new SerialBuilder(ThisApplication.context(),
						this.entityManagerContainer(), work.getProcess(), work.getId());
				String serial = serialBuilder.concrete(aeiObjects);
				work.setSerial(serial);
			}
			aeiObjects.commit();
			this.inquireCommitted(aeiObjects);
			// AfterInquireScript 이벤트 실행
			if (this.callAfterInquireScript(aeiObjects) && aeiObjects.commitData()) {
				aeiObjects.executeProjection();// 변경되었을 수 있는 데이터 매핑
				// AfterInquireScript의 코드 실행 시 data 데이터가 수정되었을 수 있음.
				aeiObjects.entityManagerContainer().commit();
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return results;
	}

	/**
	 * 활동이 없으면 임시 활동을 생성해 대체
	 * 
	 * @param work
	 * @return
	 */
	private Activity createVirtualActivity(Work work) {
		Activity activity = null;
		switch (work.getActivityType()) {
		case agent:
			activity = new Agent();
		case begin:
			activity = new Begin();
			break;
		case cancel:
			activity = new Cancel();
			break;
		case choice:
			activity = new Choice();
			break;
		case delay:
			activity = new Delay();
			break;
		case end:
			activity = new End();
			break;
		case embed:
			activity = new Embed();
			break;
		case invoke:
			activity = new Invoke();
			break;
		case manual:
			activity = new Manual();
			break;
		case merge:
			activity = new Merge();
			break;
		case parallel:
			activity = new Parallel();
			break;
		case publish:
			activity = new Publish();
			break;
		case service:
			activity = new Service();
			break;
		case split:
			activity = new Split();
			break;
		default:
			activity = new Begin();
		}
		activity.setId(work.getActivity());
		activity.setType(work.getActivityType());
		activity.setAlias(work.getActivityAlias());
		activity.setName(work.getActivityName());
		return activity;
	}

	private void callBeforeInquireScript(AeiObjects aeiObjects) throws Exception {
		if (BooleanUtils.isTrue(aeiObjects.getActivityProcessingConfigurator().getCallBeforeInquireScript())
				&& this.hasBeforeInquireScript(aeiObjects.getProcess(), aeiObjects.getActivity())) {
			Source source = null;
			if (this.hasBeforeInquireScript(aeiObjects.getProcess())) {
				source = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getProcess(), Business.EVENT_BEFOREINQUIRE);
				GraalvmScriptingFactory.eval(source, aeiObjects.bindings());
			}
			if (this.hasBeforeInquireScript(aeiObjects.getActivity())) {
				source = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getActivity(), Business.EVENT_BEFOREINQUIRE);
				GraalvmScriptingFactory.eval(source, aeiObjects.bindings());
			}
		}
	}

	private boolean callAfterInquireScript(AeiObjects aeiObjects) throws Exception {
		if (BooleanUtils.isTrue(aeiObjects.getActivityProcessingConfigurator().getCallAfterInquireScript())
				&& this.hasAfterInquireScript(aeiObjects.getProcess(), aeiObjects.getActivity())) {
			Source source = null;
			if (this.hasAfterInquireScript(aeiObjects.getProcess())) {
				source = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getProcess(), Business.EVENT_AFTERINQUIRE);
				GraalvmScriptingFactory.eval(source, aeiObjects.bindings());
			}
			if (this.hasAfterInquireScript(aeiObjects.getActivity())) {
				source = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getActivity(), Business.EVENT_AFTERINQUIRE);
				GraalvmScriptingFactory.eval(source, aeiObjects.bindings());
			}
			return true;
		}
		return false;
	}

	protected abstract Work arriveProcessing(AeiObjects aeiObjects) throws Exception;

	protected abstract void arriveCommitted(AeiObjects aeiObjects) throws Exception;

	protected abstract List<Work> executeProcessing(AeiObjects aeiObjects) throws Exception;

	protected abstract void executeCommitted(AeiObjects aeiObjects, List<Work> works) throws Exception;

	protected abstract Route inquireProcessing(AeiObjects aeiObjects) throws Exception;

	protected abstract void inquireCommitted(AeiObjects aeiObjects) throws Exception;

}