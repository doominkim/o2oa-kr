package com.x.organization.core.entity.accredit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.list.TreeList;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class Runtime extends GsonPropertyObject {

	@FieldDescribe("현재 사용자")
	public String person = "";

	@FieldDescribe("조직")
	public List<String> unitList = new TreeList<>();

	@FieldDescribe("그룹")
	public List<String> groupList = new TreeList<>();

	@FieldDescribe("역할")
	public List<String> roleList = new TreeList<>();

	@FieldDescribe("전체 그룹")
	public List<String> unitAllList = new TreeList<>();

	@FieldDescribe("신분")
	public List<String> identityList = new TreeList<>();

	@FieldDescribe("파라미터")
	public Map<String, String> parameter = new HashMap<>();

	@FieldDescribe("수량")
	public Integer count = 0;

}
