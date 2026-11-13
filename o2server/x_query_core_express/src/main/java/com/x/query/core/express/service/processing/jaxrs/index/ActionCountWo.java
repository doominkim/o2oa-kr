package com.x.query.core.express.service.processing.jaxrs.index;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "com.x.query.core.express.jaxrs.index.ActionPostWo")
public class ActionCountWo extends GsonPropertyObject {

    @FieldDescribe("분류.")
    @Schema(description = "분류.")
    private String category;

    @FieldDescribe("유형.")
    @Schema(description = "유형.")
    private String type;

    @FieldDescribe("식별자.")
    @Schema(description = "식별자.")
    private String key;

    @FieldDescribe("검색 내용.")
    @Schema(description = "검색 내용.")
    private Long count;

    @FieldDescribe("존재 여부.")
    @Schema(description = "존재 여부.")
    private Boolean exists;

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

}