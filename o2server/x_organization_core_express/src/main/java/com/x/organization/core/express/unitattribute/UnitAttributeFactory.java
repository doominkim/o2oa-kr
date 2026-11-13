package com.x.organization.core.express.unitattribute;

import java.util.Arrays;
import java.util.List;

import com.x.base.core.project.AbstractContext;

public class UnitAttributeFactory {

	public UnitAttributeFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 조직 속성 값 추가 */
	public boolean appendWithUnitWithName(String unit, String name, List<String> values) throws Exception {
		return ActionAppendWithUnitWithName.execute(context, unit, name, values);
	}

	/** 조직 속성 값 조회 */
	public List<String> listAttributeWithUnitWithName(String unit, String name) throws Exception {
		return ActionListAttributeWithUnitWithName.execute(context, unit, name);
	}

	/** 조직 속성 이름 조회 */
	public List<String> listNameWithUnit(String value) throws Exception {
		return ActionListNameWithUnit.execute(context, Arrays.asList(value));
	}

	/** 조직 속성 값 설정 */
	public boolean setWithUnitWithName(String unit, String name, List<String> values) throws Exception {
		return ActionSetWithUnitWithName.execute(context, unit, name, values);
	}

}