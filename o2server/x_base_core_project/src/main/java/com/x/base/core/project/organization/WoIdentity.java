package com.x.base.core.project.organization;

import com.x.base.core.project.annotation.FieldDescribe;

import java.util.List;

public class WoIdentity extends Identity {

	@FieldDescribe("조직 객체")
	private WoUnit woUnit;

	@FieldDescribe("조직 직무 객체")
	private List<WoUnitDuty> woUnitDutyList;

	public WoUnit getWoUnit() {
		return woUnit;
	}

	public void setWoUnit(WoUnit woUnit) {
		this.woUnit = woUnit;
	}

	public List<WoUnitDuty> getWoUnitDutyList() {
		return woUnitDutyList;
	}

	public void setWoUnitDutyList(List<WoUnitDuty> woUnitDutyList) {
		this.woUnitDutyList = woUnitDutyList;
	}
}
