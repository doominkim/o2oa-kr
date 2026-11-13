package com.x.query.core.express.index;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "com.x.query.core.express.index.Sort")
public class Sort extends GsonPropertyObject {

    private static final long serialVersionUID = 8404002412785606829L;

    public static final String ORDER_DESC = "desc";
    public static final String ORDER_ASC = "asc";

    @FieldDescribe("필드.")
    @Schema(description = "필드.")
    String field;

    @FieldDescribe("정렬 순서, asc 오름차순, desc 내림차순.")
    @Schema(description = "정렬 순서, asc 오름차순, desc 내림차순.")
    String order;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

}