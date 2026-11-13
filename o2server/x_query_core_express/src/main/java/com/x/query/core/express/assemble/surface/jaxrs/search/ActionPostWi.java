package com.x.query.core.express.assemble.surface.jaxrs.search;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.query.core.express.index.Filter;
import com.x.query.core.express.index.Sort;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "com.x.query.core.express.jaxrs.search.ActionPostWi")
public class ActionPostWi extends GsonPropertyObject {

	private static final long serialVersionUID = 1360655000630283661L;

	@FieldDescribe("검색 내용.")
	@Schema(description = "검색 내용.")
	private String query;

	@FieldDescribe("페이지.")
	@Schema(description = "페이지.")
	private Integer page;

	@FieldDescribe("수량.")
	@Schema(description = "수량.")
	private Integer size;

	@FieldDescribe("사용자.")
	@Schema(description = "사용자.")
	private String person;

	@FieldDescribe("필터 조건.")
	@Schema(description = "필터 조건.")
	private List<Filter> filterList = new ArrayList<>();

	@FieldDescribe("검색 분류.")
	@Schema(description = "검색 분류.")
	private String category = "";

	@FieldDescribe("검색 저장소.")
	@Schema(description = "검색 저장소.")
	private String key = "";

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public List<Filter> getFilterList() {
		return filterList;
	}

	public void setFilterList(List<Filter> filterList) {
		this.filterList = filterList;
	}

}