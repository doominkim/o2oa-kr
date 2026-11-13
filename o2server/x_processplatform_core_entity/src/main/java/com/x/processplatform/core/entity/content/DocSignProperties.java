package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 문서 서명 결재 확장 정보
 * @author sword
 */
public class DocSignProperties extends JsonProperties {

    private Data data = new Data();

    private String title;

    @FieldDescribe("입력 상자 목록.")
    private List<String> inputList = new ArrayList<>();

    @FieldDescribe("손글씨 목록.")
    private List<String> scrawlList = new ArrayList<>();

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getInputList() {
        return inputList;
    }

    public void setInputList(List<String> inputList) {
        this.inputList = inputList;
    }

    public List<String> getScrawlList() {
        return scrawlList;
    }

    public void setScrawlList(List<String> scrawlList) {
        this.scrawlList = scrawlList;
    }
}
