package com.x.processplatform.service.processing.processor.merge;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class MergeProcessor extends AbstractMergeProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(MergeProcessor.class);

	public MergeProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Merge merge) throws Exception {
		// ProcessingSignal 발송
		aeiObjects.getProcessingAttributes().push(Signal.mergeArrive(aeiObjects.getWork().getActivityToken(), merge));
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Merge merge) throws Exception {
		// nothing
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Merge merge) throws Exception {
		// ProcessingSignal 발송
		aeiObjects.getProcessingAttributes().push(Signal.mergeExecute(aeiObjects.getWork().getActivityToken(), merge));
		List<Work> results = new ArrayList<>();
		if (BooleanUtils.isNotTrue(aeiObjects.getWork().getSplitting())) {
			/* 분할 문서가 아니면 바로 통과 */
			results.add(aeiObjects.getWork());
			return results;
		}
		Optional<Work> other = findWorkSameLevelOrDeeper(aeiObjects);
		if (other.isPresent()) {
			aeiObjects.getUpdateWorks().add(other.get());
			aeiObjects.getDeleteWorks().add(aeiObjects.getWork());
			mergeTaskCompleted(aeiObjects, aeiObjects.getWork(), other.get());
			mergeRead(aeiObjects, aeiObjects.getWork(), other.get());
			mergeReadCompleted(aeiObjects, aeiObjects.getWork(), other.get());
			mergeReview(aeiObjects, aeiObjects.getWork(), other.get());
			mergeAttachment(aeiObjects, aeiObjects.getWork(), other.get());
			mergeWorkLog(aeiObjects, aeiObjects.getWork(), other.get());
			aeiObjects.getWorkLogs().stream()
					.filter(p -> StringUtils.equals(p.getFromActivityToken(), aeiObjects.getWork().getActivityToken()))
					.forEach(obj -> aeiObjects.getDeleteWorkLogs().add(obj));
			// 본체가 삭제됨, 다른 병합 대상이 merge 단계에 있으면 진행 시도
			if (Objects.equals(other.get().getActivityType(), ActivityType.merge)) {
				LOGGER.warn(
						"The work stays in merge is an illegal state, try to trigger the work(id:{}, job:{}) again.",
						other.get().getId(), other.get().getJob());
				results.add(other.get());
			}
		} else {
			Optional<List<String>> splitTokenList = this.findWorkShallower(aeiObjects);
			if (splitTokenList.isPresent()) {
				gotoShallower(aeiObjects, merge, splitTokenList.get());
			} else {
				// 하나뿐이어도 바로 병합할 수 없음, 병합 레벨이 설정되어 처리자가 분할 값인 상태로 돌아갔을 수 있음
				if ((null != merge.getMergeLayerThreshold()) && (merge.getMergeLayerThreshold() > 0)
						&& aeiObjects.getWork().getSplitTokenList().size() > merge.getMergeLayerThreshold()) {
					List<String> list = aeiObjects.getWork().getSplitTokenList().subList(0,
							aeiObjects.getWork().getSplitTokenList().size() - merge.getMergeLayerThreshold());
					gotoShallower(aeiObjects, merge, list);
				} else {
					// 유일하고 병합 레벨이 현재 총 레벨 이상이면 분할 상태 종료.
					aeiObjects.getWork().setSplitting(false);
					aeiObjects.getWork().setSplitToken("");
					aeiObjects.getWork().setSplitTokenList(new ArrayList<>());
					aeiObjects.getWork().setSplitValue("");
					aeiObjects.getWork().setSplitValueList(new ArrayList<>());
					aeiObjects.getWork().setSplitTokenValueMap(new LinkedHashMap<>());
				}
			}
			// 삭제하지 않으면 반드시 계속 진행하여 merge 단계를 떠남
			results.add(aeiObjects.getWork());
		}
		return results;
	}

	private void gotoShallower(AeiObjects aeiObjects, Merge merge, List<String> splitTokenList) {
		aeiObjects.getWork().setSplitting(true);
		int mergeLayerCount = aeiObjects.getWork().getSplitTokenList().size() - splitTokenList.size();
		if ((null != merge.getMergeLayerThreshold()) && (merge.getMergeLayerThreshold() > 0)) {
			mergeLayerCount = Math.min(mergeLayerCount, merge.getMergeLayerThreshold());
		}
		int threshold = aeiObjects.getWork().getSplitTokenList().size() - mergeLayerCount;
		// splitTokenList 롤백
		aeiObjects.getWork().setSplitTokenList(aeiObjects.getWork().getSplitTokenList().subList(0, threshold));
		// splitToken 롤백
		aeiObjects.getWork().setSplitToken(
				aeiObjects.getWork().getSplitTokenList().get(aeiObjects.getWork().getSplitTokenList().size() - 1));
		// splitValueList 롤백, 병렬로 와서 분할 값이 없으면 저장된 값으로 조립
		aeiObjects.getWork()
				.setSplitValueList(aeiObjects.getWork().getSplitTokenList().stream()
						.map(o -> aeiObjects.getWork().getSplitTokenValueMap().getOrDefault(o, null))
						.filter(Objects::nonNull).collect(Collectors.toList()));
		// splitValue 롤백
		if (ListTools.isNotEmpty(aeiObjects.getWork().getSplitValueList())) {
			aeiObjects.getWork().setSplitValue(
					aeiObjects.getWork().getSplitValueList().get(aeiObjects.getWork().getSplitValueList().size() - 1));
		} else {
			aeiObjects.getWork().setSplitValue("");
		}
	}

	/**
	 * 같은 레벨 또는 더 깊게 분할된 문서 조회
	 * 
	 * @param aeiObjects
	 * @return
	 * @throws Exception
	 */
	private Optional<Work> findWorkSameLevelOrDeeper(AeiObjects aeiObjects) throws Exception {
		String join = StringUtils.join(aeiObjects.getWork().getSplitTokenList(), ",");
		// 동일 레벨 조회
		Optional<Work> other = aeiObjects.getWorks().stream()
				.filter(o -> BooleanUtils.isTrue(o.getSplitting()) && (o != aeiObjects.getWork())
						&& StringUtils.equals(StringUtils.join(o.getSplitTokenList(), ","), join))
				.sorted((o1, o2) -> o1.getCreateTime().compareTo(o2.getCreateTime())).findFirst();
		if (other.isPresent() && LOGGER.isDebugEnabled()) {
			LOGGER.debug("findWorkMergeTo work {} found same split level work {}.", aeiObjects.getWork()::getId,
					other.get()::getId);
		}
		// 동일 레벨을 찾지 못하면 더 깊은 레벨의 문서 조회 시작
		if (other.isEmpty()) {
			other = aeiObjects.getWorks().stream()
					.filter(o -> BooleanUtils.isTrue(o.getSplitting()) && (o != aeiObjects.getWork())
							&& StringUtils.startsWith(StringUtils.join(o.getSplitTokenList(), ","), join))
					.sorted((o1, o2) -> {
						int compare = o2.getSplitTokenList().size() - o1.getSplitTokenList().size();
						if (compare == 0) {
							return o2.getCreateTime().compareTo(o1.getCreateTime());
						}
						return compare;
					}).findFirst();
			if (other.isPresent() && LOGGER.isDebugEnabled()) {
				LOGGER.debug("findWorkMergeTo work {} found further split level work {}.", aeiObjects.getWork()::getId,
						other.get()::getId);
			}
		}
		return other;
	}

	/**
	 * 더 얕은 레벨의 분할 문서 조회
	 * 
	 * @param aeiObjects
	 * @return
	 * @throws Exception
	 */
	private Optional<List<String>> findWorkShallower(AeiObjects aeiObjects) throws Exception {
		List<String> list = aeiObjects.getWorks().stream()
				.filter(o -> BooleanUtils.isTrue(o.getSplitting()) && (o != aeiObjects.getWork()))
				.map(Work::getSplitTokenList).<List<String>>reduce(new ArrayList<>(), (a, b) -> {
					List<String> ac = ListUtils.longestCommonSubsequence(aeiObjects.getWork().getSplitTokenList(), a);
					List<String> bc = ListUtils.longestCommonSubsequence(aeiObjects.getWork().getSplitTokenList(), b);
					return (ac.size() > bc.size()) ? ac : bc;
				}, (x, y) -> x.size() >= y.size() ? x : y);
		return list.isEmpty() ? Optional.empty() : Optional.of(list);
	}

	/**
	 * V3Retract에서 호출됨
	 * 
	 * @param aeiObjects
	 * @param work
	 * @param oldest
	 */
	public static void mergeTaskCompleted(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getTaskCompleteds().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId()))
					.forEach(o -> {
						o.setWork(oldest.getId());
						aeiObjects.getUpdateTaskCompleteds().add(o);
					});
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	public static void mergeRead(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getReads().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId())).forEach(o -> {
				o.setWork(oldest.getId());
				aeiObjects.getUpdateReads().add(o);
			});
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	public static void mergeReadCompleted(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getReadCompleteds().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId()))
					.forEach(o -> {
						o.setWork(oldest.getId());
						aeiObjects.getUpdateReadCompleteds().add(o);
					});
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	public static void mergeReview(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getReviews().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId())).forEach(o -> {
				o.setWork(oldest.getId());
				aeiObjects.getUpdateReviews().add(o);
			});
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	public static void mergeAttachment(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getAttachments().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId()))
					.forEach(o -> {
						o.setWork(oldest.getId());
						aeiObjects.getUpdateAttachments().add(o);
					});
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	public static void mergeWorkLog(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getWorkLogs().stream()
					.filter(o -> StringUtils.equals(work.getActivityToken(), o.getArrivedActivityToken())
							&& StringUtils.equals(o.getWork(), work.getId()))
					.forEach(o -> {
						o.setWork(oldest.getId());
						aeiObjects.getUpdateWorkLogs().add(o);
					});
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Merge merge, List<Work> works) throws Exception {
		// nothing
	}

	@Override
	protected Optional<Route> inquiring(AeiObjects aeiObjects, Merge merge) throws Exception {
		// ProcessingSignal 발송
		aeiObjects.getProcessingAttributes().push(Signal.mergeInquire(aeiObjects.getWork().getActivityToken(), merge));
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
		return aeiObjects.getRoutes().stream().findFirst();
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Merge merge) throws Exception {
		// nothing
	}
}