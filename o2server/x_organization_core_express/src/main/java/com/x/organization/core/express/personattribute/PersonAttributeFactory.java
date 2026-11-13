package com.x.organization.core.express.personattribute;

import java.util.Arrays;
import java.util.List;

import com.x.base.core.project.AbstractContext;

public class PersonAttributeFactory {

	public PersonAttributeFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 개인 속성 값 추가 */
	public boolean appendWithPersonWithName(String person, String name, List<String> values) throws Exception {
		return ActionAppendWithPersonWithName.execute(context, person, name, values);
	}

	/** 개인 속성 값 조회 */
	public List<String> listAttributeWithPersonWithName(String person, String name) throws Exception {
		return ActionListAttributeWithPersonWithName.execute(context, person, name);
	}

	/** 개인 속성 이름 조회 */
	public List<String> listNameWithPerson(String value) throws Exception {
		return ActionListNameWithPerson.execute(context, Arrays.asList(value));
	}

	/** 개인 속성 값 추가 */
	public boolean setWithPersonWithName(String person, String name, List<String> values) throws Exception {
		return ActionSetWithPersonWithName.execute(context, person, name, values);
	}

}