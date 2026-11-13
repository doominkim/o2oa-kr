package com.x.base.core.project.organization;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class Group extends GsonPropertyObject {

	@FieldDescribe("일치 필드")
	private String matchKey;
	@FieldDescribe("그룹 id")
	private String id;
	@FieldDescribe("그룹 이름")
	private String name;
	@FieldDescribe("그룹 식별자")
	private String unique;
	@FieldDescribe("설명")
	private String description;
	@FieldDescribe("식별 이름")
	private String distinguishedName;
	@FieldDescribe("정렬 번호")
	private Integer orderNumber;
	@FieldDescribe("개인 구성원")
	private List<String> personList = new ArrayList<>();
	@FieldDescribe("그룹 구성원")
	private List<String> groupList = new ArrayList<>();
	@FieldDescribe("조직 구성원")
	private List<String> unitList = new ArrayList<>();
	@FieldDescribe("신분 구성원")
	private List<String> identityList = new ArrayList<>();
	@FieldDescribe("직속 하위 조직 수")
	private Long subDirectGroupCount = 0L;

	@FieldDescribe("직속 하위 사용자 수")
	private Long subDirectPersonCount = 0L;

	@FieldDescribe("직속 하위 신분 수")
	private Long subDirectIdentityCount = 0L;

	@FieldDescribe("직속 하위 조직 수")
	private Long subDirectOrgCount = 0L;

	public String getMatchKey() {
		return matchKey;
	}

	public void setMatchKey(String matchKey) {
		this.matchKey = matchKey;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getUnique() {
		return unique;
	}

	public void setUnique(String unique) {
		this.unique = unique;
	}

	public List<String> getUnitList() {
		return unitList;
	}

	public void setUnitList(List<String> unitList) {
		this.unitList = unitList;
	}

	public List<String> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<String> identityList) {
		this.identityList = identityList;
	}

	public Long getSubDirectGroupCount() {
		return subDirectGroupCount;
	}

	public void setSubDirectGroupCount(Long subDirectGroupCount) {
		this.subDirectGroupCount = subDirectGroupCount;
	}

	public Long getSubDirectPersonCount() {
		return subDirectPersonCount;
	}

	public void setSubDirectPersonCount(Long subDirectPersonCount) {
		this.subDirectPersonCount = subDirectPersonCount;
	}

	public Long getSubDirectIdentityCount() {
		return subDirectIdentityCount;
	}

	public void setSubDirectIdentityCount(Long subDirectIdentityCount) {
		this.subDirectIdentityCount = subDirectIdentityCount;
	}

	public Long getSubDirectOrgCount() {
		return subDirectOrgCount;
	}

	public void setSubDirectOrgCount(Long subDirectOrgCount) {
		this.subDirectOrgCount = subDirectOrgCount;
	}
}
