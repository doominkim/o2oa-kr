package com.x.query.core.express.assemble.surface.jaxrs.morelikethis;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionPostWi extends GsonPropertyObject {

    private static final long serialVersionUID = 1360655000630283661L;

    @FieldDescribe("식별자.")
    @Schema(description = "식별자.")
    private String flag;

    @FieldDescribe("분류.")
    @Schema(description = "분류.")
    private String category;

    @FieldDescribe("지정 사용자.")
    @Schema(description = "지정 사용자.")
    private String person;

    @FieldDescribe("필터 분류.")
    @Schema(description = "필터 분류.")
    private String filterCategory;

    @FieldDescribe("필터 유형.")
    @Schema(description = "필터 유형.")
    private String filterType;

    @FieldDescribe("필터 디렉터리 유형.")
    @Schema(description = "필터 디렉터리 유형.")
    private String filterKey;

    @FieldDescribe("반환 개수.")
    @Schema(description = "반환 개수.")
    private Integer count;

    public String getFilterCategory() {
        return filterCategory;
    }

    public void setFilterCategory(String filterCategory) {
        this.filterCategory = filterCategory;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getFilterKey() {
        return filterKey;
    }

    public void setFilterKey(String filterKey) {
        this.filterKey = filterKey;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

}
