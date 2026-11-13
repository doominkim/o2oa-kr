package com.x.base.core.project.organization;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class Unit extends GsonPropertyObject {

	private static final long serialVersionUID = 5213623128307370812L;

	@FieldDescribe("조직명")
	private String name;
	@FieldDescribe("고유 식별자")
	private String unique;
	@FieldDescribe("식별 이름")
	private String distinguishedName;
	@FieldDescribe("조직 유형")
	private List<String> typeList;
	@FieldDescribe("설명")
	private String description;
	@FieldDescribe("약칭")
	private String shortName;
	@FieldDescribe("조직 계층")
	private Integer level;
	@FieldDescribe("조직 계층명")
	private String levelName;
	@FieldDescribe("상위 조직")
	private String superior;
	@FieldDescribe("정렬 번호")
	private Integer orderNumber;
	@FieldDescribe("계층 정렬 번호")
	private String levelOrderNumber;
	@FieldDescribe("기업 위챗 Id")
	private String qiyeweixinId;
	@FieldDescribe("딩톡 Id")
	private String dingdingId;
	@FieldDescribe("정부 딩톡 Id")
	private String zhengwuDingdingId;
	@FieldDescribe("화웨이 WeLink Id")
	private String weLinkId;
	@FieldDescribe("조직 관리자")
	private List<String> controllerList;

	public String getWeLinkId() {
		return weLinkId;
	}

	public void setWeLinkId(String weLinkId) {
		this.weLinkId = weLinkId;
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

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public List<String> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<String> typeList) {
		this.typeList = typeList;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public String getSuperior() {
		return superior;
	}

	public void setSuperior(String superior) {
		this.superior = superior;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getQiyeweixinId() {
		return qiyeweixinId;
	}

	public void setQiyeweixinId(String qiyeweixinId) {
		this.qiyeweixinId = qiyeweixinId;
	}

	public String getDingdingId() {
		return dingdingId;
	}

	public void setDingdingId(String dingdingId) {
		this.dingdingId = dingdingId;
	}

	public String getZhengwuDingdingId() {
		return zhengwuDingdingId;
	}

	public void setZhengwuDingdingId(String zhengwuDingdingId) {
		this.zhengwuDingdingId = zhengwuDingdingId;
	}

	public String getLevelOrderNumber() {
		return levelOrderNumber;
	}

	public void setLevelOrderNumber(String levelOrderNumber) {
		this.levelOrderNumber = levelOrderNumber;
	}

	public List<String> getControllerList() {
		return controllerList;
	}

	public void setControllerList(List<String> controllerList) {
		this.controllerList = controllerList;
	}
}
