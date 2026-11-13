package com.x.bbs.assemble.control.queue;

import java.util.Date;

import com.x.base.core.project.annotation.FieldDescribe;

public class MessageWo{

    @FieldDescribe("원글 ID 또는 답글 ID")
    private String id;

    @FieldDescribe("제목: 주제 또는 답글의 제목")
    private String title;

    @FieldDescribe("Subject | Subject")
    private String type;

    @FieldDescribe("원글 ID.")
    private String subjectId;

    @FieldDescribe("답글 ID")
    private String replyId;

    @FieldDescribe("포럼 구역 Id")
    private String forumId;

    @FieldDescribe("포럼 구역 이름")
    private String forumName;

    @FieldDescribe("게시판 Id")
    private String selectionId;

    @FieldDescribe("게시판 이름")
    private String selectionName;

    @FieldDescribe("작업자")
    private String createPerson;

    @FieldDescribe("작업 시간")
    private Date createTime;

    public String getReplyId() {
        return this.replyId;
    }

    public void setReplyId(final String replyId) {
        this.replyId = replyId;
    }

    public String getSubjectId() {
        return this.subjectId;
    }

    public void setSubjectId(final String subjectId) {
        this.subjectId = subjectId;
    }

    public String getForumId() {
        return this.forumId;
    }

    public void setForumId(final String forumId) {
        this.forumId = forumId;
    }

    public String getForumName() {
        return this.forumName;
    }

    public void setForumName(final String forumName) {
        this.forumName = forumName;
    }

    public String getSelectionId() {
        return this.selectionId;
    }

    public void setSelectionId(final String selectionId) {
        this.selectionId = selectionId;
    }

    public String getSelectionName() {
        return this.selectionName;
    }

    public void setSelectionName(final String selectionName) {
        this.selectionName = selectionName;
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

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }
}
