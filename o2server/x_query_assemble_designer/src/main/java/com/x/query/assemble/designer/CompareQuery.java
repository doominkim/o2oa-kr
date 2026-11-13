package com.x.query.assemble.designer;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class CompareQuery extends GsonPropertyObject {

	private static final long serialVersionUID = 675001251642041128L;

	@FieldDescribe("가져오기 이름")
	private String name;

	@FieldDescribe("가져오기 id")
	private String id;

	@FieldDescribe("가져오기 별칭")
	private String alias;

	@FieldDescribe("이미 존재하는지 여부")
	private Boolean exist;

	@FieldDescribe("이미 존재하는 이름")
	private String existName;

	@FieldDescribe("이미 존재하는 id")
	private String existId;

	@FieldDescribe("이미 존재하는 별칭")
	private String existAlias;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Boolean getExist() {
		return exist;
	}

	public void setExist(Boolean exist) {
		this.exist = exist;
	}

	public String getExistName() {
		return existName;
	}

	public void setExistName(String existName) {
		this.existName = existName;
	}

	public String getExistId() {
		return existId;
	}

	public void setExistId(String existId) {
		this.existId = existId;
	}

	public String getExistAlias() {
		return existAlias;
	}

	public void setExistAlias(String existAlias) {
		this.existAlias = existAlias;
	}

}
