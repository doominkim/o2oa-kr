package com.x.processplatform.assemble.surface.jaxrs.work;

import com.google.common.collect.Lists;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.ManualProperties.DefineConfig;
import com.x.processplatform.core.entity.element.ManualProperties.GoBackConfig;
import com.x.processplatform.core.entity.element.util.WorkLogTree;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;
import com.x.processplatform.core.express.ProcessingAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class V2ListActivityGoBack extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2ListActivityGoBack.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			Work work = emc.find(id, Work.class);

			if (null == work) {
				throw new ExceptionEntityExist(id, Work.class);
			}

			Business business = new Business(emc);

			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowManage()
					.enableAllowGoBack().build();

			if (BooleanUtils.isNotTrue(control.getAllowGoBack()) && BooleanUtils.isNotTrue(control.getAllowManage())) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}

			Manual manual = (Manual) business.getActivity(work.getActivity(), ActivityType.manual);

			if (null == manual) {
				throw new ExceptionEntityExist(work.getActivity());
			}

			// 조건 판단은 앞의 control에서 이미 수행됨
			WorkLogTree workLogTree = this.workLogTree(business, work.getJob());
			Node node = workLogTree.location(work);
			if (null != node) {
				Nodes nodes = workLogTree.up(node);
//				List<WorkLog> workLogs = truncateWorkLog(nodes, work.getGoBackActivityToken());
				List<WorkLog> workLogs = nodes.stream().map(Node::getWorkLog).collect(Collectors.toList());
				// 연결되지 않은 것, 반려 작업, manual 활동이 아닌 것, 현재 활동과 같은 활동을 필터링하고 활동마다 최근 workLog 하나만 취함, stream은 요소 순서 보장을 위해 LinkedHashMap 사용
				workLogs = workLogs.stream()
						.filter(o -> Objects.equals(o.getFromActivityType(), ActivityType.manual)
								&& (!StringUtils.equalsIgnoreCase(o.getType(), ProcessingAttributes.TYPE_GOBACK))
								&& BooleanUtils.isTrue(o.getConnected())
								&& (!StringUtils.equalsIgnoreCase(manual.getId(), o.getFromActivity())))
						.collect(Collectors.groupingBy(WorkLog::getFromActivity, LinkedHashMap::new, // 결과 저장용 새 LinkedHashMap 생성
								Collectors.toList()))
						.entrySet().stream()
						.map(o -> o.getValue().stream()
								.sorted(Comparator.comparing(WorkLog::getCreateTime,
										Comparator.nullsFirst(Date::compareTo).reversed()))
								.findFirst())
						.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
				wos = this.list(manual, workLogs);
				wos = this.supplement(business, wos);
			}
		}
		result.setData(wos);
		return result;
	}

	private List<Wo> list(Manual manual, List<WorkLog> workLogs) {
		List<Wo> list = new ArrayList<>();
		if (StringUtils.equalsIgnoreCase(manual.getGoBackConfig().getType(), GoBackConfig.TYPE_PREV)) {
			Optional<WorkLog> opt = workLogs.stream()
					.filter(o -> Objects.equals(o.getFromActivityType(), ActivityType.manual)
							&& BooleanUtils.isTrue((o.getConnected())))
					.findFirst();
			if (opt.isPresent()) {
				Wo wo = new Wo();
				wo.setActivity(opt.get().getFromActivity());
				wo.setLastModifyTime(opt.get().getFromTime());
				wo.setActivityToken(opt.get().getFromActivityToken());
				wo.setWay(manual.getGoBackConfig().getWay());
				list.add(wo);
			}
		} else if (StringUtils.equalsIgnoreCase(manual.getGoBackConfig().getType(), GoBackConfig.TYPE_DEFINE)) {
			workLogs.stream().forEach(o -> {
				Optional<DefineConfig> opt = manual.getGoBackConfig().getDefineConfigList().stream()
						.filter(d -> StringUtils.equalsIgnoreCase(d.getActivity(), o.getFromActivity())).findFirst();
				if (opt.isPresent()) {
					Wo wo = new Wo();
					wo.setActivity(opt.get().getActivity());
					wo.setWay(StringUtils.equalsIgnoreCase(opt.get().getWay(), GoBackConfig.WAY_DEFAULT)
							? manual.getGoBackConfig().getWay()
							: opt.get().getWay());
					wo.setLastModifyTime(o.getFromTime());
					wo.setActivityToken(o.getFromActivityToken());
					list.add(wo);
				}
			});
		} else {
			workLogs.stream().forEach(o -> {
				Wo wo = new Wo();
				wo.setActivity(o.getFromActivity());
				wo.setLastModifyTime(o.getFromTime());
				wo.setActivityToken(o.getFromActivityToken());
				wo.setWay(manual.getGoBackConfig().getWay());
				list.add(wo);
			});
		}
		// 마지막 시간을 이른 순서에서 늦은 순서로 출력, 프론트엔드에서 시간순 정렬.
		return Lists.reverse(list);
	}

	private List<Wo> supplement(Business business, List<Wo> wos) {
		List<Wo> list = new ArrayList<>();
		wos.stream().forEach(o -> {
			try {
				Manual m = (Manual) business.getActivity(o.getActivity(), ActivityType.manual);
				if (null != m) {
					o.setName(m.getName());
					list.add(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
		// 마지막 단계 처리자 조립
		list.stream().forEach(o -> {
			try {
				List<String> identities = business.entityManagerContainer()
						.listEqual(TaskCompleted.class, TaskCompleted.activityToken_FIELDNAME, o.getActivityToken())
						.stream().filter(t -> StringUtils.equalsIgnoreCase(t.getAct(), TaskCompleted.ACT_CREATE))
						.flatMap(t -> Stream.of(t.getDistinguishedName(), t.getIdentity()))
						.filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
				if (identities.isEmpty()) {
					identities = business.entityManagerContainer()
							.fetchEqualAndEqual(TaskCompleted.class, Arrays.asList(TaskCompleted.identity_FIELDNAME),
									TaskCompleted.activityToken_FIELDNAME, o.getActivityToken(),
									TaskCompleted.joinInquire_FIELDNAME, true)
							.stream().map(TaskCompleted::getIdentity).filter(StringUtils::isNotBlank).distinct()
							.collect(Collectors.toList());
				}
				o.setLastIdentityList(identities);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
		// 비어 있어 goBack 불가한 노드 필터링
		return list.stream().filter(o -> ListTools.isNotEmpty(o.getLastIdentityList())).collect(Collectors.toList());
	}

	private WorkLogTree workLogTree(Business business, String job) throws Exception {
		return new WorkLogTree(business.entityManagerContainer().listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, job));
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -7690519507479509393L;

		// 이전 처리 시간
		private Date lastModifyTime;
		// 반환 방식
		private String way;
		// 활동 식별자
		private String activity;
		// 활동 식별자
		private String activityToken;
		// 활동 이름
		private String name;
		// 이전 처리자
		private List<String> lastIdentityList = new ArrayList<>();

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public String getWay() {
			return way;
		}

		public void setWay(String way) {
			this.way = way;
		}

		public Date getLastModifyTime() {
			return lastModifyTime;
		}

		public void setLastModifyTime(Date lastModifyTime) {
			this.lastModifyTime = lastModifyTime;
		}

		public List<String> getLastIdentityList() {
			return lastIdentityList;
		}

		public void setLastIdentityList(List<String> lastIdentityList) {
			this.lastIdentityList = lastIdentityList;
		}

		public String getActivityToken() {
			return activityToken;
		}

		public void setActivityToken(String activityToken) {
			this.activityToken = activityToken;
		}

	}

}
