package com.x.organization.core.express.role;

import java.util.Arrays;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.Role;
import com.x.base.core.project.tools.ListTools;

public class RoleFactory {

	public RoleFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 단일 역할의 distinguishedName 조회 */
	public String get(String value) throws Exception {
		List<String> os = ActionList.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return "";
		} else {
			return os.get(0);
		}
	}

	/** 역할 distinguishedName 일괄 조회 */
	public List<String> list(List<String> values) throws Exception {
		return ActionList.execute(context, values);
	}

	/** 역할 distinguishedName 일괄 조회 */
	public List<String> list(String... values) throws Exception {
		return ActionList.execute(context, Arrays.asList(values));
	}

	/** 역할 객체 조회 */
	public Role getObject(String value) throws Exception {
		List<? extends Role> os = ActionListObject.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return null;
		} else {
			return os.get(0);
		}
	}

	/** 역할 객체 일괄 조회 */
	public List<Role> listObject(List<String> values) throws Exception {
		List<? extends Role> os = ActionListObject.execute(context, values);
		return (List<Role>) os;
	}

	/** 역할 객체 일괄 조회 */
	public List<Role> listObject(String... values) throws Exception {
		List<? extends Role> os = ActionListObject.execute(context, Arrays.asList(values));
		return (List<Role>) os;
	}

	/** 사용자가 가진 역할 조회 */
	public List<String> listWithPerson(EffectivePerson effectivePerson) throws Exception {
		return ActionListWithPerson.execute(context, ListTools.toList(effectivePerson.getDistinguishedName()));
	}

	/** 사용자가 가진 역할 조회 */
	public List<String> listWithPerson(List<String> values) throws Exception {
		return ActionListWithPerson.execute(context, values);
	}

	/** 사용자가 가진 역할 조회 */
	public List<String> listWithPerson(String... values) throws Exception {
		return ActionListWithPerson.execute(context, Arrays.asList(values));
	}

	/** 사용자가 가진 역할 객체 조회 */
	public List<Role> listWithPersonObject(EffectivePerson effectivePerson) throws Exception {
		List<? extends Role> os = ActionListWithPersonObject.execute(context,
				ListTools.toList(effectivePerson.getDistinguishedName()));
		return (List<Role>) os;
	}

	/** 사용자가 가진 역할 객체 조회 */
	public List<Role> listWithPersonObject(List<String> values) throws Exception {
		List<? extends Role> os = ActionListWithPersonObject.execute(context, values);
		return (List<Role>) os;
	}

	/** 사용자가 가진 역할 객체 조회 */
	public List<Role> listWithPersonObject(String... values) throws Exception {
		List<? extends Role> os = ActionListWithPersonObject.execute(context, Arrays.asList(values));
		return (List<Role>) os;
	}

}