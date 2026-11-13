package com.x.base.core.project.organization;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class Identity extends GsonPropertyObject {

	@FieldDescribe("일치 필드")
	private String matchKey;
	@FieldDescribe("신분 이름")
	private String name;
	@FieldDescribe("신분 식별자")
	private String unique;
	@FieldDescribe("설명")
	private String description;
	@FieldDescribe("식별 이름")
	private String distinguishedName;
	@FieldDescribe("사용자")
	private String person;
	@FieldDescribe("조직")
	private String unit;
	@FieldDescribe("조직명")
	private String unitName;
	@FieldDescribe("조직 레벨")
	private Integer unitLevel;
	@FieldDescribe("조직 레벨명")
	private String unitLevelName;
	@FieldDescribe("정렬 번호")
	private Integer orderNumber;
	@FieldDescribe("지정된 주 신분 여부")
	private Boolean major;

	public String getMatchKey() {
		return matchKey;
	}

	public void setMatchKey(String matchKey) {
		this.matchKey = matchKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnique() {
		return unique;
	}

	public void setUnique(String unique) {
		this.unique = unique;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getUnitLevelName() {
		return unitLevelName;
	}

	public void setUnitLevelName(String unitLevelName) {
		this.unitLevelName = unitLevelName;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Integer getUnitLevel() {
		return unitLevel;
	}

	public void setUnitLevel(Integer unitLevel) {
		this.unitLevel = unitLevel;
	}

	public Boolean getMajor() {
		return major;
	}

	public void setMajor(Boolean major) {
		this.major = major;
	}
}
