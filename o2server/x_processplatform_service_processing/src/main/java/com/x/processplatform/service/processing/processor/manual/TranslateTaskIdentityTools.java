package com.x.processplatform.service.processing.processor.manual;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Source;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.PropertyTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.ManualProperties;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.processor.AeiObjects;

/**
 * Manual 단계에서 모든 결재 대기자의 Identity 계산
 * 
 * @author Rui
 *
 */
public class TranslateTaskIdentityTools {

	private static final Logger LOGGER = LoggerFactory.getLogger(TranslateTaskIdentityTools.class);

	private TranslateTaskIdentityTools() {
		// nothing
	}

	/* manual 노드의 모든 결재 대기를 계산하여 모두 Identity로 변환 */
	public static TaskIdentities translate(AeiObjects aeiObjects, Manual manual) throws Exception {
		TaskIdentities taskIdentities = new TaskIdentities();
		// 정상 실행
		List<String> units = new ArrayList<>();
		List<String> groups = new ArrayList<>();
		// 지정 신분
		if (ListTools.isNotEmpty(manual.getTaskIdentityList())) {
			taskIdentities.addIdentities(manual.getTaskIdentityList());
		}
		// 프로세스 역할
		if ((null != manual.getTaskParticipant()) && StringUtils.isNotBlank(manual.getTaskParticipant().getType())) {
			taskIdentities.addIdentities(participant(aeiObjects, manual));
		}
		// 직무 선택
		taskIdentities.addIdentities(duty(aeiObjects, manual));
		// data 데이터 경로 값 지정
		data(taskIdentities, units, groups, aeiObjects.getData(), manual);
		// 스크립트로 계산
		script(taskIdentities, units, groups, aeiObjects, manual);
		// 처리 조직 지정
		if (ListTools.isNotEmpty(manual.getTaskUnitList())) {
			units.addAll(manual.getTaskUnitList());
		}
		// 처리 그룹 지정
		if (ListTools.isNotEmpty(manual.getTaskGroupList())) {
			groups.addAll(manual.getTaskGroupList());
		}
		if (ListTools.isNotEmpty(units)) {
			taskIdentities.addIdentities(aeiObjects.business().organization().identity().listWithUnitSubDirect(units));
		}
		if (ListTools.isNotEmpty(groups)) {
			taskIdentities.addIdentities(aeiObjects.business().organization().identity().listWithGroup(groups));
		}
		List<String> identities = aeiObjects.business().organization().identity().list(taskIdentities.identities());
		return new TaskIdentities(identities);
	}

	/* 지정 직무의 identity 조회 */
	private static List<String> duty(AeiObjects aeiObjects, Manual manual) throws Exception {
		List<String> list = new ArrayList<>();
		if (StringUtils.isNotEmpty(manual.getTaskDuty())) {
			JsonArray array = XGsonBuilder.instance().fromJson(manual.getTaskDuty(), JsonArray.class);
			Iterator<JsonElement> iterator = array.iterator();
			while (iterator.hasNext()) {
				JsonObject o = iterator.next().getAsJsonObject();
				String name = o.get("name").getAsString();
				String code = o.get("code").getAsString();
				Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getActivity(),
						Business.EVENT_TASKDUTY, name, code);
				List<String> ds = GraalvmScriptingFactory.evalAsDistinguishedNames(source, aeiObjects.bindings());
				if (ListTools.isNotEmpty(ds)) {
					for (String str : ds) {
						List<String> os = aeiObjects.business().organization().unitDuty()
								.listIdentityWithUnitWithName(str, name);
						if (ListTools.isNotEmpty(os)) {
							list.addAll(os);
						}
					}
				}
			}
		}
		return ListTools.trim(list, true, true);
	}

	private static List<String> participant(AeiObjects aeiObjects, Manual manual) throws Exception {
		List<String> list = new ArrayList<>();
		switch (Objects.toString(manual.getTaskParticipant().getType(), "")) {
		case ManualProperties.Participant.TYPE_ACTIVITY:
			if ((null != manual.getTaskParticipant().getData())
					&& (manual.getTaskParticipant().getData().isJsonArray())) {
				final List<String> taskParticipantActivities = new ArrayList<>();
				manual.getTaskParticipant().getData().getAsJsonArray()
						.forEach(o -> taskParticipantActivities.add(o.getAsString()));
				aeiObjects.getTaskCompleteds().stream()
						.filter(o -> (!StringUtils.equalsIgnoreCase(o.getAct(), TaskCompleted.ACT_EMPOWER)))
						.filter(o -> taskParticipantActivities.contains(o.getActivity()))
						.map(TaskCompleted::getIdentity).filter(StringUtils::isNotBlank).forEach(list::add);
			}
			break;
		case ManualProperties.Participant.TYPE_MAINTENANCE:
			if (StringUtils.isNotBlank(aeiObjects.getProcess().getMaintenanceIdentity())) {
				list.add(aeiObjects.getProcess().getMaintenanceIdentity());
			}
			break;
		case ManualProperties.Participant.TYPE_CREATOR:
			list.add(aeiObjects.getWork().getCreatorIdentity());
			break;
		default:
			break;
		}
		return list;
	}

	/* script로 지정된 identity 조회 */
	private static List<String> script(TaskIdentities taskIdentities, List<String> units, List<String> groups,
			AeiObjects aeiObjects, Manual manual) throws Exception {
		List<String> list = new ArrayList<>();
		if ((StringUtils.isNotEmpty(manual.getTaskScript())) || (StringUtils.isNotEmpty(manual.getTaskScriptText()))) {
			Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
					manual, Business.EVENT_MANUALTASK);
			GraalvmScriptingFactory.eval(source, aeiObjects.bindings(), jsonElement -> {
				try {
					addObjectToTaskIdentities(taskIdentities, units, groups,
							GraalvmScriptingFactory.Helper.stringOrDistinguishedNameAsList(jsonElement));
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
		}
		return list;
	}

	/* 경로로 지정된 identity 조회 */
	private static void data(TaskIdentities taskIdentities, List<String> units, List<String> groups, Data data,
			Manual manual) throws Exception {
		if (ListTools.isNotEmpty(manual.getTaskDataPathList())) {
			for (String str : ListTools.trim(manual.getTaskDataPathList(), true, true)) {
				Object o = data.find(str);
				if (null != o) {
					addObjectToTaskIdentities(taskIdentities, units, groups, o);
				}
			}
		}
	}

	private static void addObjectToTaskIdentities(TaskIdentities taskIdentities, List<String> units,
			List<String> groups, Object o) throws Exception {
		for (String d : asDistinguishedNames(o)) {
			if (OrganizationDefinition.isIdentityDistinguishedName(d)) {
				Boolean ignore = BooleanUtils.isTrue(BooleanUtils.toBooleanObject(Objects.toString(
						PropertyTools.getOrElse(o, TaskIdentity.FIELD_IGNOREEMPOWER, Boolean.class, Boolean.FALSE),
						"false")));
				TaskIdentity taskIdentity = new TaskIdentity();
				taskIdentity.setIdentity(d);
				taskIdentity.setIgnoreEmpower(ignore);
				taskIdentities.add(taskIdentity);
			} else if (OrganizationDefinition.isUnitDistinguishedName(d)) {
				units.add(d);
			} else if (OrganizationDefinition.isGroupDistinguishedName(d)) {
				groups.add(d);
			}
		}
	}

	public static List<String> asDistinguishedNames(Object o)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<String> list = new ArrayList<>();
		if (null != o) {
			if (o instanceof CharSequence) {
				list.add(Objects.toString(o));
			} else if (o instanceof Iterable) {
				asIterable(o, list);
			} else {
				Object obj = PropertyUtils.getProperty(o, JpaObject.DISTINGUISHEDNAME);
				String str = Objects.toString(obj, "");
				if (StringUtils.isNotEmpty(str)) {
					list.add(str);
				}
			}
		}
		return list;
	}

	private static void asIterable(Object o, List<String> list)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		for (Object obj : (Iterable<?>) o) {
			if (null != obj) {
				if (obj instanceof CharSequence) {
					list.add(Objects.toString(obj));
				} else {
					Object d = PropertyUtils.getProperty(obj, JpaObject.DISTINGUISHEDNAME);
					if (null != d) {
						list.add(Objects.toString(d));
					}
				}
			}
		}
	}
}