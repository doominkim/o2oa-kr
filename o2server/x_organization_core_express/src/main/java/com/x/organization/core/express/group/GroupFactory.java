package com.x.organization.core.express.group;

import java.util.Arrays;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.Group;
import com.x.base.core.project.tools.ListTools;

public class GroupFactory {

	public GroupFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 그룹이 지정 역할 중 하나 이상을 보유했는지 확인 */
	public Boolean hasRole(String group, List<String> values) throws Exception {
		return ActionHasRole.execute(context, group, values);
	}

	/** 그룹이 지정 역할 중 하나 이상을 보유했는지 확인 */
	public Boolean hasRole(String group, String... values) throws Exception {
		return ActionHasRole.execute(context, group, Arrays.asList(values));
	}

	/** 단일 그룹의 distinguishedName 조회 */
	public String get(String value) throws Exception {
		List<String> os = ActionList.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return "";
		} else {
			return os.get(0);
		}
	}

	/** 그룹 distinguishedName 일괄 조회 */
	public List<String> list(List<String> values) throws Exception {
		return ActionList.execute(context, values);
	}

	/** 그룹 distinguishedName 일괄 조회 */
	public List<String> list(String... values) throws Exception {
		return ActionList.execute(context, Arrays.asList(values));
	}

	/** 그룹 객체 조회 */
	public Group getObject(String value) throws Exception {
		List<? extends Group> os = ActionListObject.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return null;
		} else {
			return os.get(0);
		}
	}

	/** 그룹 객체 일괄 조회 */
	public List<Group> listObject(List<String> values) throws Exception {
		List<? extends Group> os = ActionListObject.execute(context, values);
		return (List<Group>) os;
	}

	/** 그룹 객체 일괄 조회 */
	public List<Group> listObject(String... values) throws Exception {
		List<? extends Group> os = ActionListObject.execute(context, Arrays.asList(values));
		return (List<Group>) os;
	}

	/** 그룹의 직속 하위 그룹 조회 */
	public List<String> listWithGroupSubDirect(List<String> values) throws Exception {
		return ActionListWithGroupSubDirect.execute(context, values);
	}

	/** 그룹의 직속 하위 그룹 조회 */
	public List<String> listWithGroupSubDirect(String... values) throws Exception {
		return ActionListWithGroupSubDirect.execute(context, Arrays.asList(values));
	}

	/** 그룹의 재귀 하위 그룹 조회 */
	public List<String> listWithGroupSubNested(List<String> values) throws Exception {
		return ActionListWithGroupSubNested.execute(context, values);
	}

	/** 그룹의 재귀 하위 그룹 조회 */
	public List<String> listWithGroupSubNested(String... values) throws Exception {
		return ActionListWithGroupSubNested.execute(context, Arrays.asList(values));
	}

	/** 그룹의 직속 상위 그룹 조회 */
	public List<String> listWithGroupSupDirect(List<String> values) throws Exception {
		return ActionListWithGroupSupDirect.execute(context, values);
	}

	/** 그룹의 직속 상위 그룹 조회 */
	public List<String> listWithGroupSupDirect(String... values) throws Exception {
		return ActionListWithGroupSupDirect.execute(context, Arrays.asList(values));
	}

	/** 그룹의 재귀 상위 그룹 조회 */
	public List<String> listWithGroupSupNested(List<String> values) throws Exception {
		return ActionListWithGroupSupNested.execute(context, values);
	}

	/** 그룹의 재귀 상위 그룹 조회 */
	public List<String> listWithGroupSupNested(String... values) throws Exception {
		return ActionListWithGroupSupNested.execute(context, Arrays.asList(values));
	}

	/** 사용자가 속한 그룹 조회 */
	public List<String> listWithPerson(List<String> values) throws Exception {
		return listWithPersonReference(values, true, true, true);
	}

	/** 사용자가 속한 그룹 조회 */
	public List<String> listWithPerson(String... values) throws Exception {
		return listWithPersonReference(Arrays.asList(values), true, true, true);
	}

	/** 사용자가 속한 그룹 조회 */
	public List<String> listWithPerson(EffectivePerson effectivePerson) throws Exception {
		return listWithPersonReference(ListTools.toList(effectivePerson.getDistinguishedName()), true, true, true);
	}

	/**
	 * 사용자 및 연관 신분·조직이 속한 그룹 조회
	 *
	 * @param values             사용자
	 * @param recursiveGroupFlag 상위 그룹 재귀 조회 여부
	 * @param referenceFlag      사용자 신분 구성원·사용자 소속 조직 구성원이 속한 그룹 포함 여부
	 * @param recursiveOrgFlag   사용자 소속 조직의 상위 조직이 속한 그룹 재귀 조회 여부, referenceFlag가 true일 때만 적용
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithPersonReference(List<String> values, boolean recursiveGroupFlag, boolean referenceFlag,
			boolean recursiveOrgFlag) throws Exception {
		return ActionListWithPerson.execute(context, values, recursiveGroupFlag, referenceFlag, recursiveOrgFlag);
	}

	/** 신분이 속한 그룹 조회 */
	public List<String> listWithIdentity(List<String> values) throws Exception {
		return listWithIdentityReference(values, true, true, true);
	}

	/** 신분이 속한 그룹 조회 */
	public List<String> listWithIdentity(String... values) throws Exception {
		return listWithIdentityReference(Arrays.asList(values), true, true, true);
	}

	/**
	 * 신분 및 연관 조직이 속한 그룹 조회
	 *
	 * @param values             신분
	 * @param recursiveGroupFlag 상위 그룹 재귀 조회 여부
	 * @param referenceFlag      사용자 신분 구성원·사용자 소속 조직 구성원이 속한 그룹 포함 여부
	 * @param recursiveOrgFlag   사용자 소속 조직의 상위 조직이 속한 그룹 재귀 조회 여부, referenceFlag가 true일 때만 적용
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithIdentityReference(List<String> values, boolean recursiveGroupFlag,
			boolean referenceFlag, boolean recursiveOrgFlag) throws Exception {
		return ActionListWithIdentity.execute(context, values, recursiveGroupFlag, referenceFlag, recursiveOrgFlag);
	}

}
