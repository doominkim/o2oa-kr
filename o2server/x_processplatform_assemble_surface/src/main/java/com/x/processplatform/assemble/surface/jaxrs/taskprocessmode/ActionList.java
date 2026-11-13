package com.x.processplatform.assemble.surface.jaxrs.taskprocessmode;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.TaskProcessMode;
import com.x.processplatform.core.entity.content.TaskProcessModeItem;
import com.x.processplatform.core.entity.element.Process;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 빠른 선택 목록 표시 규칙:
 * 현재 사용자, 프로세스, 프로세스 노드 기준 목록
 * 사전 결재자 추가, 사후 결재자 추가, 제출 결정, 반려, 재설정으로 나뉘며 각각 최대 3건, 횟수가 많은 것이 앞, 횟수가 같으면 최신이 앞, 중복 표시하지 않음.
 * @author sword
 */
class ActionList extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionList.class);
	private static final Integer MAX_ITEM = 3;

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (StringUtils.isBlank(wi.getProcess())) {
			throw new ExceptionFieldEmpty(TaskProcessMode.process_FIELDNAME);
		}
		if (StringUtils.isBlank(wi.getActivity())) {
			throw new ExceptionFieldEmpty(TaskProcessMode.activity_FIELDNAME);
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			ActionResult<List<Wo>> result = new ActionResult<>();

			Business business = new Business(emc);
			Process process = business.process().pick(wi.getProcess());
			if (null == process) {
				throw new ExceptionEntityNotExist(wi.getProcess(), Process.class.getSimpleName());
			}
			wi.setProcess(StringUtils.isBlank(process.getEdition()) ? wi.getProcess() : process.getEdition());
			List<TaskProcessMode> taskProcessModeList = new ArrayList<>(
					business.taskProcessMode().listMode(effectivePerson.getUnique(), wi));
			SortTools.desc(taskProcessModeList, TaskProcessMode.hitCount_FIELDNAME,
					TaskProcessMode.updateTime_FIELDNAME);
			List<Wo> wos = new ArrayList<>();
			for (TaskProcessMode mode : taskProcessModeList) {
				List<TaskProcessModeItem> itemList = new ArrayList<>(mode.getTaskProcessModeItemList());
				SortTools.desc(itemList, TaskProcessModeItem.hitCount_FIELDNAME,
						TaskProcessModeItem.updateTime_FIELDNAME);
				if (itemList.size() > MAX_ITEM) {
					itemList = itemList.subList(0, MAX_ITEM);
				}
				for (TaskProcessModeItem item : itemList) {
					Wo wo = Wo.copier.copy(mode);
					wo.setRouteGroup(item.getRouteGroup());
					wo.setKeepTask(item.getKeepTask());
					wo.setOpinion(item.getOpinion());
					wo.setOrganizations(item.getOrganizations());
					wo.setItemHitCount(item.getHitCount());
					wo.setItemUpdateTime(item.getUpdateTime());
					wos.add(wo);
				}
			}

			result.setData(wos);
			return result;
		}
	}

	public static class Wi extends TaskProcessMode {

		private static final long serialVersionUID = -5033720364055042229L;

		static WrapCopier<Wi, TaskProcessMode> copier = WrapCopierFactory.wi(Wi.class, TaskProcessMode.class,
				ListTools.toList(TaskProcessMode.process_FIELDNAME, TaskProcessMode.activity_FIELDNAME,
						TaskProcessMode.activityAlias_FIELDNAME, TaskProcessMode.activityName_FIELDNAME),
				null);

	}

	public static class Wo extends TaskProcessMode {

		private static final long serialVersionUID = 1398834553299330481L;
		static WrapCopier<TaskProcessMode, Wo> copier = WrapCopierFactory.wo(TaskProcessMode.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisibleIncludeProperites,
						TaskProcessMode.taskProcessModeItemList_FIELDNAME));

		@FieldDescribe("프로젝트 마지막 업데이트 시간.")
		private Date itemUpdateTime;

		@FieldDescribe("프로젝트 마지막 업데이트 시간.")
		private Integer itemHitCount;

		public Date getItemUpdateTime() {
			return itemUpdateTime;
		}

		public void setItemUpdateTime(Date itemUpdateTime) {
			this.itemUpdateTime = itemUpdateTime;
		}

		public Integer getItemHitCount() {
			return itemHitCount;
		}

		public void setItemHitCount(Integer itemHitCount) {
			this.itemHitCount = itemHitCount;
		}
	}
}
