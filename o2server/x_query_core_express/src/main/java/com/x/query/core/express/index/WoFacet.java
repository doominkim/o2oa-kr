package com.x.query.core.express.index;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.ValueCountPair;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "com.x.query.core.express.index.WoFacet")
public class WoFacet extends GsonPropertyObject {

    private static final long serialVersionUID = 6427227486701877864L;

    @FieldDescribe("필드 식별자.")
    @Schema(description = "필드 식별자.")
    private String field;

    @FieldDescribe("필드 이름.")
    @Schema(description = "필드 이름.")
    private String name;

    @FieldDescribe("분류 값 목록.")
    @Schema(description = "분류 값 목록.")
    private List<ValueCountPair> valueCountPairList = new ArrayList<>();

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ValueCountPair> getValueCountPairList() {
        return valueCountPairList;
    }

    public void setValueCountPairList(List<ValueCountPair> valueCountPairList) {
        this.valueCountPairList = valueCountPairList;
    }

}