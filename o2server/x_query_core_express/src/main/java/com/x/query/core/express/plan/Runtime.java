package com.x.query.core.express.plan;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.list.TreeList;

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

	@FieldDescribe("전체 권한 집합")
	public List<String> authList = new ArrayList<>();

	@FieldDescribe("필터")
	public List<FilterEntry> filterList = new TreeList<>();

	@FieldDescribe("지정 정렬")
	public List<SelectEntry> orderList = new TreeList<>();

	@FieldDescribe("파라미터")
	public Map<String, String> parameter = new HashMap<>();

	@FieldDescribe("페이지당 개수")
	public Integer count = 0;

	@FieldDescribe("페이지 번호")
	public Integer page = 0;

	@FieldDescribe("결과 집합 제한")
	public List<String> bundleList = new TreeList<>();

	@FieldDescribe("지정 필드 목록")
	public SelectEntries selectList;

	public boolean hasBundle = false;

	public boolean isManager = false;

}
