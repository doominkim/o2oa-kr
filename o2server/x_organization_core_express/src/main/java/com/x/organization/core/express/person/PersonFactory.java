package com.x.organization.core.express.person;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.IdentityPersonPair;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.PersonDetail;
import com.x.base.core.project.tools.ListTools;

public class PersonFactory {

	public PersonFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 사용자가 지정 역할 중 하나 이상을 보유했는지 확인 */
	public Boolean hasRole(EffectivePerson effectivePerson, List<String> values) throws Exception {
		return ActionHasRole.execute(context, effectivePerson.getDistinguishedName(), values);
	}

	/** 사용자가 지정 역할 중 하나 이상을 보유했는지 확인 */
	public Boolean hasRole(EffectivePerson effectivePerson, String... values) throws Exception {
		return ActionHasRole.execute(context, effectivePerson.getDistinguishedName(), Arrays.asList(values));
	}

	/** 사용자가 지정 역할 중 하나 이상을 보유했는지 확인 */
	public Boolean hasRole(String person, List<String> values) throws Exception {
		return ActionHasRole.execute(context, person, values);
	}

	/** 사용자가 지정 역할 중 하나 이상을 보유했는지 확인 */
	public Boolean hasRole(String person, String... values) throws Exception {
		return ActionHasRole.execute(context, person, Arrays.asList(values));
	}

	/** 단일 개인의 distinguishedName 조회 */
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

	/** 개인 닉네임 조회 */
	public String getNickName(String value) throws Exception {
		return ActionGetNickName.execute(context, value);
	}

	/** 개인 및 관련 신분, 신분 소속 조직, 신분 소속 직무, 개인·신분이 속한 그룹, 개인 보유 역할의 dn 목록 조회 */
	public List<String> getAuthInfo(String value) throws Exception {
		return ActionGetAuthInfo.execute(context, value);
	}

	/** 개인 distinguishedName 일괄 조회 */
	public List<String> list(List<String> values) throws Exception {
		return ActionList.execute(context, values, false);
	}

	public List<String> list(List<String> values, Boolean useNameFind) throws Exception {
		return ActionList.execute(context, values, useNameFind);
	}

	/** 개인 distinguishedName 일괄 조회 */
	public List<String> list(String... values) throws Exception {
		return ActionList.execute(context, Arrays.asList(values), false);
	}

	/** 전체 개인 조회 */
	public List<String> listAll() throws Exception {
		return ActionListAll.execute(context);
	}

	/** 전체 개인 객체 조회 */
	public List<Person> listAllObject() throws Exception {
		List<? extends Person> os = ActionListAllObject.execute(context);
		return (List<Person>) os;
	}

	/** 개인 객체 조회 */
	public Person getObject(String value) throws Exception {
		List<? extends Person> os = ActionListObject.execute(context, Arrays.asList(value), false);
		if (ListTools.isEmpty(os)) {
			return null;
		} else {
			return os.get(0);
		}
	}

	public Person getObject(String value, Boolean useNameFind) throws Exception {
		List<? extends Person> os = ActionListObject.execute(context, Arrays.asList(value), useNameFind);
		if (ListTools.isEmpty(os)) {
			return null;
		} else {
			return os.get(0);
		}
	}

	/** 지정 날짜 이후 로그인한 개인 조회 */
	public List<String> listLoginAfter(Date date) throws Exception {
		return ActionListLoginAfter.execute(context, date);
	}

	/** 지정 날짜 이후 로그인한 개인 객체 조회 */
	public List<Person> listLoginAfterObject(Date date) throws Exception {
		List<? extends Person> os = ActionListLoginAfterObject.execute(context, date);
		return (List<Person>) os;
	}

	/** 최근 로그인한 개인을 지정 수만큼 조회 */
	public List<String> listLoginRecent(Integer count) throws Exception {
		return ActionListLoginRecent.execute(context, count);
	}

	/** 최근 로그인한 개인 객체를 지정 수만큼 조회 */
	public List<Person> listLoginRecentObject(Integer count) throws Exception {
		List<? extends Person> os = ActionListLoginRecentObject.execute(context, count);
		return (List<Person>) os;
	}

	/** 개인 객체 일괄 조회 */
	public List<Person> listObject(List<String> values) throws Exception {
		List<? extends Person> os = ActionListObject.execute(context, values, false);
		return (List<Person>) os;
	}

	public List<Person> listObject(List<String> values, Boolean useNameFind) throws Exception {
		List<? extends Person> os = ActionListObject.execute(context, values, useNameFind);
		return (List<Person>) os;
	}

	/** 개인 객체 일괄 조회 */
	public List<Person> listObject(String... values) throws Exception {
		List<? extends Person> os = ActionListObject.execute(context, Arrays.asList(values), false);
		return (List<Person>) os;
	}

	/** 그룹의 재귀 하위 멤버 개인 조회 */
	public List<String> listWithGroup(List<String> values) throws Exception {
		return ActionListWithGroup.execute(context, values);
	}

	/** 그룹의 재귀 하위 멤버 개인 조회 */
	public List<String> listWithGroup(String... values) throws Exception {
		return ActionListWithGroup.execute(context, Arrays.asList(values));
	}

	/** 신분으로 개인 조회 */
	public String getWithIdentity(String value) throws Exception {
		List<String> os = ActionListWithIdentity.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return "";
		} else {
			return os.get(0);
		}
	}

	/** 신분으로 개인 일괄 조회 */
	public List<String> listWithIdentity(List<String> values) throws Exception {
		return ActionListWithIdentity.execute(context, values);
	}

	/** 신분으로 개인 일괄 조회 */
	public List<String> listWithIdentity(String... values) throws Exception {
		return ActionListWithIdentity.execute(context, Arrays.asList(values));
	}

	/** 개인 속성을 가진 개인 조회 */
	public List<String> listWithPersonAttribute(String name, String value) throws Exception {
		return ActionListWithPersonAttribute.execute(context, name, value);
	}

	/** 개인이 직접 관할하는 개인 조회 */
	public List<String> listWithPersonSubDirect(List<String> values) throws Exception {
		return ActionListWithPersonSubDirect.execute(context, values);
	}

	public List<String> listWithPersonSubDirect(String... values) throws Exception {
		return ActionListWithPersonSubDirect.execute(context, Arrays.asList(values));
	}

	/** 개인이 중첩 관할하는 개인 조회 */
	public List<String> listWithPersonSubNested(List<String> values) throws Exception {
		return ActionListWithPersonSubNested.execute(context, values);
	}

	public List<String> listWithPersonSubNested(String... values) throws Exception {
		return ActionListWithPersonSubNested.execute(context, Arrays.asList(values));
	}

	/** 개인의 직속 보고 대상 조회 */
	public String getWithPersonSupDirect(String value) throws Exception {
		List<String> os = ActionListWithPersonSupDirect.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return "";
		} else {
			return os.get(0);
		}
	}

	/** 개인의 직속 상관 조회 */
	public List<String> listWithPersonSupDirect(List<String> values) throws Exception {
		return ActionListWithPersonSupDirect.execute(context, values);
	}

	public List<String> listWithPersonSupDirect(String... values) throws Exception {
		return ActionListWithPersonSupDirect.execute(context, Arrays.asList(values));
	}

	/** 개인의 중첩 상관 조회 */
	public List<String> listWithPersonSupNested(List<String> values) throws Exception {
		return ActionListWithPersonSupNested.execute(context, values);
	}

	public List<String> listWithPersonSupNested(String... values) throws Exception {
		return ActionListWithPersonSupNested.execute(context, Arrays.asList(values));
	}

	/** 역할의 재귀 하위 멤버 개인 조회 */
	public List<String> listWithRole(List<String> values) throws Exception {
		return ActionListWithRole.execute(context, values);
	}

	/** 역할의 재귀 하위 멤버 개인 조회 */
	public List<String> listWithRole(String... values) throws Exception {
		return ActionListWithRole.execute(context, Arrays.asList(values));
	}

	/** 조직의 직속 하위 멤버에 해당하는 개인 조회 */
	public List<String> listWithUnitSubDirect(List<String> values) throws Exception {
		return ActionListWithUnitSubDirect.execute(context, values);
	}

	/** 조직의 직속 하위 멤버에 해당하는 개인 조회 */
	public List<String> listWithUnitSubDirect(String... values) throws Exception {
		return ActionListWithUnitSubDirect.execute(context, Arrays.asList(values));
	}

	/** 조직의 재귀 하위 멤버에 해당하는 개인 조회 */
	public List<String> listWithUnitSubNested(List<String> values) throws Exception {
		return ActionListWithUnitSubNested.execute(context, values);
	}

	/** 조직의 재귀 하위 멤버에 해당하는 개인 조회 */
	public List<String> listWithUnitSubNested(String... values) throws Exception {
		return ActionListWithUnitSubNested.execute(context, Arrays.asList(values));
	}

	// 신분으로 개인 조회, 신분-사용자 매칭 쌍 반환
	public IdentityPersonPair getPairIdentity(String value) throws Exception {
		List<IdentityPersonPair> os = ActionListPairIdentity.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return null;
		} else {
			return os.get(0);
		}
	}

	// 신분으로 개인 일괄 조회, 신분-사용자 매칭 쌍 반환
	public List<IdentityPersonPair> listPairIdentity(List<String> values) throws Exception {
		return ActionListPairIdentity.execute(context, values);
	}

	// 신분으로 개인 일괄 조회, 신분-사용자 매칭 쌍 반환
	public List<IdentityPersonPair> listPairIdentity(String... values) throws Exception {
		return ActionListPairIdentity.execute(context, Arrays.asList(values));
	}

	/**
	 * 확장된 개인 객체 조회, 개인 신분·신분 소속 조직 등 정보 반환
	 *
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public Person getExt(String value) throws Exception {
		return ActionExt.execute(context, value);
	}

	/**
	 * 개인 신분, 조직, 조직 직무, 그룹, 역할, 개인 속성 조회
	 *
	 * @param value                 개인 식별자
	 * @param fectchIdentity        신분 조회 여부
	 * @param fectchUnit            조직 조회 여부
	 * @param fectchUnitDuty        조직 직무 조회 여부
	 * @param fectchGroup           그룹 조회 여부
	 * @param fectchRole            역할 조회 여부
	 * @param fectchPersonAttribute 개인 속성 조회 여부
	 * @return
	 * @throws Exception
	 */
	public PersonDetail detail(String value, Boolean fectchIdentity, Boolean fectchUnit, Boolean fectchUnitDuty,
			Boolean fectchGroup, Boolean fectchRole, Boolean fectchPersonAttribute) throws Exception {
		return ActionDetail.execute(context, value, fectchIdentity, fectchUnit, fectchUnitDuty, fectchGroup, fectchRole,
				fectchPersonAttribute);
	}

}
