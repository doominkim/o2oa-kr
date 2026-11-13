package com.x.organization.core.express.unitduty;

import java.util.Arrays;
import java.util.List;

import com.x.base.core.project.AbstractContext;

public class UnitDutyFactory {

	public UnitDutyFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 조직 속성 값 조회 */
	public List<String> listIdentityWithUnitWithName(String unit, String name) throws Exception {
		return ActionListIdenityWithUnitWithName.execute(context, unit, name);
	}

	/** 조직의 조직 직무 이름 조회 */
	public List<String> listNameWithUnit(String value) throws Exception {
		return ActionListNameWithUnit.execute(context, Arrays.asList(value));
	}

	/** 신분의 조직 직무 이름 조회 */
	public List<String> listNameWithIdentity(String value) throws Exception {
		return ActionListNameWithIdentity.execute(context, Arrays.asList(value));
	}

}