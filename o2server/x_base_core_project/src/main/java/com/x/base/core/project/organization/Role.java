package com.x.base.core.project.organization;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class Role extends GsonPropertyObject {

	@FieldDescribe("역할 이름")
	private String name;
	@FieldDescribe("고유 식별자")
	private String unique;
	@FieldDescribe("설명")
	private String description;
	@FieldDescribe("식별 이름")
	private String distinguishedName;
	@FieldDescribe("정렬 번호")
	private Integer orderNumber;
	@FieldDescribe("구성원 개인")
	private List<String> personList = new ArrayList<>();
	@FieldDescribe("구성원 그룹")
	private List<String> groupList = new ArrayList<>();

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

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public List<String> getPersonList() {
		return personList;
	}

	public void setPersonList(List<String> personList) {
		this.personList = personList;
	}

	public List<String> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<String> groupList) {
		this.groupList = groupList;
	}

}
