package com.x.query.core.express.assemble.surface.jaxrs.index;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.query.core.express.index.Directory;
import com.x.query.core.express.index.Filter;
import com.x.query.core.express.index.Sort;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionExportWi extends GsonPropertyObject {

    private static final long serialVersionUID = 1360655000630283661L;

    @FieldDescribe("검색 디렉터리.")
    @Schema(description = "검색 디렉터리.")
    private List<Directory> directoryList = new ArrayList<>();

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

    @FieldDescribe("표시할 고정 필드.")
    @Schema(description = "표시할 고정 필드.")
    List<String> fixedFieldList = new ArrayList<>();

    @FieldDescribe("표시할 동적 필드.")
    @Schema(description = "표시할 동적 필드.")
    List<String> dynamicFieldList = new ArrayList<>();

    @FieldDescribe("필터 조건.")
    @Schema(description = "필터 조건.")
    List<Filter> filterList = new ArrayList<>();

    @FieldDescribe("정렬 필드.")
    @Schema(description = "정렬 필드.")
    Sort sort;

    public List<Directory> getDirectoryList() {
        return directoryList;
    }

    public void setDirectoryList(List<Directory> directoryList) {
        this.directoryList = directoryList;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public List<String> getFixedFieldList() {
        return null == fixedFieldList ? new ArrayList<>() : this.fixedFieldList;
    }

    public void setFixedFieldList(List<String> fixedFieldList) {
        this.fixedFieldList = fixedFieldList;
    }

    public List<String> getDynamicFieldList() {
        return null == dynamicFieldList ? new ArrayList<>() : this.dynamicFieldList;
    }

    public void setDynamicFieldList(List<String> dynamicFieldList) {
        this.dynamicFieldList = dynamicFieldList;
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
        return null == filterList ? new ArrayList<>() : this.filterList;
    }

    public void setFilterList(List<Filter> filterList) {
        this.filterList = filterList;
    }

}
