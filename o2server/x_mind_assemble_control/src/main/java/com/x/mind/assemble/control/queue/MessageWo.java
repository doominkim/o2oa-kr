package com.x.mind.assemble.control.queue;

import java.util.Date;

import com.x.base.core.project.annotation.FieldDescribe;

public class MessageWo {

    @FieldDescribe("원글 ID 또는 답글 ID")
    private String id;

    @FieldDescribe("제목: 주제 또는 답글의 제목")
    private String title;

    @FieldDescribe("작업자")
    private String createPerson;

    @FieldDescribe("작업 시간")
    private Date createTime;

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getCreatePerson() {
        return this.createPerson;
    }

    public void setCreatePerson(final String createPerson) {
        this.createPerson = createPerson;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }
}
