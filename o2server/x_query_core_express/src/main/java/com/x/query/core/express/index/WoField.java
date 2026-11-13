package com.x.query.core.express.index;

import java.util.Objects;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "com.x.query.core.express.index.WoField")
public class WoField extends GsonPropertyObject {

    private static final long serialVersionUID = 1L;

    public WoField() {
        // nothing
    }

    public WoField(String field, String name, String fieldType) {
        this.field = field;
        this.name = name;
        this.fieldType = fieldType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, fieldType, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WoField other = (WoField) obj;
        return Objects.equals(field, other.field) && Objects.equals(fieldType, other.fieldType)
                && Objects.equals(name, other.name);
    }

    @FieldDescribe("필드 식별자.")
    @Schema(description = "필드 식별자.")
    private String field;

    @FieldDescribe("필드 이름.")
    @Schema(description = "필드 이름.")
    private String name;

    @FieldDescribe("필드 유형.")
    @Schema(description = "필드 유형.")
    private String fieldType;

    @FieldDescribe("하한값.")
    @Schema(description = "하한값.")
    private Object min;

    @FieldDescribe("상한값.")
    @Schema(description = "상한값.")
    private Object max;

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

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Object getMin() {
        return min;
    }

    public void setMin(Object min) {
        this.min = min;
    }

    public Object getMax() {
        return max;
    }

    public void setMax(Object max) {
        this.max = max;
    }
}
