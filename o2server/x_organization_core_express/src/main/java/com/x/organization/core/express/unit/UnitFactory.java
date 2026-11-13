package com.x.organization.core.express.unit;

import java.util.Arrays;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;

public class UnitFactory {

	public UnitFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 단일 조직의 distinguishedName 조회 */
	public String get(String value) throws Exception {
		List<String> os = ActionList.execute(context, Arrays.asList(value), false);
		if (ListTools.isEmpty(os)) {
			return "";
		} else {
			return os.get(0);
		}
	}

	public String get(String value, Boolean useNameFind) throws Exception {
		List<String> os = ActionList.execute(context, Arrays.asList(value), useNameFind);
		if (ListTools.isEmpty(os)) {
			return "";
		} else {
			return os.get(0);
		}
	}

	/** 사용자가 지정 조직에 신분을 등록했는지 확인. */
	public Boolean checkHasPerson(String person, String unit, Boolean recursive) throws Exception {
		return ActionHasPerson.execute(context, person, unit, recursive);
	}

	/** 신분과 조직 레벨로 조직의 distinguishedName 조회 */
	public String getWithIdentityWithLevel(String identity, Integer level) throws Exception {
		return ActionGetWithIdentityWithLevel.execute(context, identity, level);
	}

	/** 신분과 조직 속성으로 조직의 distinguishedName 조회 */
	public String getWithIdentityWithType(String identity, String type) throws Exception {
		return ActionGetWithIdentityWithType.execute(context, identity, type);
	}

	/** 조직 distinguishedName 일괄 조회 */
	public List<String> list(List<String> values) throws Exception {
		return ActionList.execute(context, values, false);
	}

	public List<String> list(List<String> values, Boolean useNameFind) throws Exception {
		return ActionList.execute(context, values, useNameFind);
	}

	/** 조직 distinguishedName 일괄 조회 */
	public List<String> list(String... values) throws Exception {
		return ActionList.execute(context, Arrays.asList(values), false);
	}

	/** 전체 조직 조회 */
	public List<String> listAll() throws Exception {
		return ActionListAll.execute(context);
	}

	/** 전체 조직 객체 조회 */
	public List<Unit> listAllObject() throws Exception {
		List<? extends Unit> os = ActionListAllObject.execute(context);
		return (List<Unit>) os;
	}

	/** 조직 객체 조회 */
	public Unit getObject(String value) throws Exception {
		List<? extends Unit> os = ActionListObject.execute(context, Arrays.asList(value), false);
		if (ListTools.isEmpty(os)) {
			return null;
		} else {
			return os.get(0);
		}
	}

	public Unit getObject(String value, Boolean useNameFind) throws Exception {
		List<? extends Unit> os = ActionListObject.execute(context, Arrays.asList(value), useNameFind);
		if (ListTools.isEmpty(os)) {
			return null;
		} else {
			return os.get(0);
		}
	}

	/** 조직 객체 일괄 조회 */
	public List<Unit> listObject(List<String> values) throws Exception {
		List<? extends Unit> os = ActionListObject.execute(context, values, false);
		return (List<Unit>) os;
	}

	public List<Unit> listObject(List<String> values, Boolean useNameFind) throws Exception {
		List<? extends Unit> os = ActionListObject.execute(context, values, useNameFind);
		return (List<Unit>) os;
	}

	/** 조직 객체 일괄 조회 */
	public List<Unit> listObject(String... values) throws Exception {
		List<? extends Unit> os = ActionListObject.execute(context, Arrays.asList(values), false);
		return (List<Unit>) os;
	}

	/** 신분이 속한 조직 조회 */
	public String getWithIdentity(String value) throws Exception {
		List<String> os = ActionListWithIdentity.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return "";
		} else {
			return os.get(0);
		}
	}

	/** 신분으로 조직 일괄 조회 */
	public List<String> listWithIdentity(List<String> values) throws Exception {
		return ActionListWithIdentity.execute(context, values);
	}

	/** 신분으로 조직 일괄 조회 */
	public List<String> listWithIdentity(String... values) throws Exception {
		return ActionListWithIdentity.execute(context, Arrays.asList(values));
	}

	/** 신분으로 조직 일괄 조회 및 상위 조직 재귀 조회 */
	public List<String> listWithIdentitySupNested(List<String> values) throws Exception {
		return ActionListWithIdentitySupNested.execute(context, values);
	}

	/** 신분으로 조직 일괄 조회 및 상위 조직 재귀 조회 */
	public List<String> listWithIdentitySupNested(String... values) throws Exception {
		return ActionListWithIdentitySupNested.execute(context, Arrays.asList(values));
	}

	/** 지정 조직 레벨의 조직 일괄 조회 */
	public List<String> listWithLevel(List<Integer> values) throws Exception {
		return ActionListWithLevel.execute(context, values);
	}

	/** 지정 조직 레벨의 조직 일괄 조회 */
	public List<String> listWithLevel(Integer... values) throws Exception {
		return ActionListWithLevel.execute(context, Arrays.asList(values));
	}

	/** 지정 조직 레벨의 조직 객체 일괄 조회 */
	public List<Unit> listWithLevelObject(List<Integer> values) throws Exception {
		List<? extends Unit> os = ActionListWithLevelObject.execute(context, values);
		return (List<Unit>) os;
	}

	/** 지정 조직 레벨의 조직 객체 일괄 조회 */
	public List<Unit> listWithLevelObject(Integer... values) throws Exception {
		List<? extends Unit> os = ActionListWithLevelObject.execute(context, Arrays.asList(values));
		return (List<Unit>) os;
	}

	/** 개인이 속한 조직 일괄 조회 */
	public List<String> listWithPerson(List<String> values) throws Exception {
		return ActionListWithPerson.execute(context, values);
	}

	/** 개인이 속한 조직 일괄 조회 */
	public List<String> listWithPerson(String... values) throws Exception {
		return ActionListWithPerson.execute(context, Arrays.asList(values));
	}

	/** 개인이 속한 조직 일괄 조회 */
	public List<String> listWithPerson(EffectivePerson effectivePerson) throws Exception {
		return ActionListWithPerson.execute(context, ListTools.toList(effectivePerson.getDistinguishedName()));
	}

	/** 개인이 속한 조직 일괄 조회 및 상위 조직 재귀 조회 */
	public List<String> listWithPersonSupNested(EffectivePerson effectivePerson) throws Exception {
		return ActionListWithPersonSupNested.execute(context, ListTools.toList(effectivePerson.getDistinguishedName()));
	}

	/** 개인이 속한 조직 일괄 조회 및 상위 조직 재귀 조회 */
	public List<String> listWithPersonSupNested(List<String> values) throws Exception {
		return ActionListWithPersonSupNested.execute(context, values);
	}

	/** 개인이 속한 조직 일괄 조회 및 상위 조직 재귀 조회 */
	public List<String> listWithPersonSupNested(String... values) throws Exception {
		return ActionListWithPersonSupNested.execute(context, Arrays.asList(values));
	}

	/** 조직 속성을 가진 조직 조회 */
	public List<String> listWithUnitAttribute(String name, String attribute) throws Exception {
		return ActionListWithUnitAttribute.execute(context, name, attribute);
	}

	/** 조직 직무를 가진 조직 조회 */
	public List<String> listWithUnitDuty(String name, String attribute) throws Exception {
		return ActionListWithUnitDuty.execute(context, name, attribute);
	}

	/** 조직의 직속 하위 조직 조회 */
	public List<String> listWithUnitSubDirect(List<String> values) throws Exception {
		return ActionListWithUnitSubDirect.execute(context, values);
	}

	/** 조직의 직속 하위 조직 조회 */
	public List<String> listWithUnitSubDirect(String... values) throws Exception {
		return ActionListWithUnitSubDirect.execute(context, Arrays.asList(values));
	}

	/** 조직의 중첩 하위 조직 조회 */
	public List<String> listWithUnitSubNested(List<String> values) throws Exception {
		return ActionListWithUnitSubNested.execute(context, values);
	}

	/** 조직의 중첩 하위 조직 조회 */
	public List<String> listWithUnitSubNested(String... values) throws Exception {
		return ActionListWithUnitSubNested.execute(context, Arrays.asList(values));
	}

	/** 조직의 직속 상위 조직 조회 */
	public List<String> listWithUnitSupDirect(List<String> values) throws Exception {
		return ActionListWithUnitSupDirect.execute(context, values);
	}

	/** 조직의 직속 상위 조직 조회 */
	public List<String> listWithUnitSupDirect(String... values) throws Exception {
		return ActionListWithUnitSupDirect.execute(context, Arrays.asList(values));
	}

	/** 조직의 중첩 상위 조직 조회 */
	public List<String> listWithUnitSupNested(List<String> values) throws Exception {
		return ActionListWithUnitSupNested.execute(context, values);
	}

	/** 조직의 중첩 상위 조직 조회 */
	public List<String> listWithUnitSupNested(String... values) throws Exception {
		return ActionListWithUnitSupNested.execute(context, Arrays.asList(values));
	}

	/** 조직의 정렬 번호 조회 */
	public Integer getOrderNumber(String value, Integer defaultValue) throws Exception {
		List<? extends Unit> os = ActionListObject.execute(context, Arrays.asList(value), false);
		if (os.isEmpty()) {
			return defaultValue;
		} else {
			return (os.get(0).getOrderNumber() == null) ? defaultValue : os.get(0).getOrderNumber();
		}
	}

	/** 조직의 계층 정렬 번호 조회 */
	public String getLevelOrderNumber(String value, String defaultValue) throws Exception {
		List<? extends Unit> os = ActionListObject.execute(context, Arrays.asList(value), false);
		if (os.isEmpty()) {
			return defaultValue;
		} else {
			return (os.get(0).getLevelOrderNumber() == null) ? defaultValue : os.get(0).getLevelOrderNumber();
		}
	}

	/** 조직의 계층 정렬 번호 조회 */
	public String getLevelName(String value, String defaultValue) throws Exception {
		List<? extends Unit> os = ActionListObject.execute(context, Arrays.asList(value), false);
		if (os.isEmpty()) {
			return defaultValue;
		} else {
			return (os.get(0).getLevelName() == null) ? defaultValue : os.get(0).getLevelName();
		}
	}
}
