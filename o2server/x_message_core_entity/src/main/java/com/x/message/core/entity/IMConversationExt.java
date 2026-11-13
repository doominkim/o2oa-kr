package com.x.message.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 대화 확장 객체. 사용자별로 하나씩 두며 현재 사용자의 대화 확장 속성을 저장
 */
@Schema(name = "IMConversationExt", description = "메시지 대화 확장.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.IMConversationExt.table, indexes = {
        @javax.persistence.Index(name = PersistenceProperties.IMConversationExt.table + "_person_conversationId", columnList = "xperson, xconversationId")
}, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.IMConversationExt.table + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class IMConversationExt extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.IMConversationExt.table;
    private static final long serialVersionUID = 1928069073101719523L;

    @Override
    public void onPersist() throws Exception {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @FieldDescribe("데이터베이스 기본 키, 자동 생성.")
    @Id
    @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
    private String id = createId();

    public static final String conversationId_FIELDNAME = "conversationId";
    @FieldDescribe("대화 id, 연결된 대화.")
    @Column(length = length_64B, name = ColumnNamePrefix + conversationId_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String conversationId;

    public static final String person_FIELDNAME = "person";
    @FieldDescribe("소속 사용자")
    @Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String person;

    public static final String isTop_FIELDNAME = "isTop";
    @FieldDescribe("상단 고정 여부.")
    @CheckPersist(allowEmpty = false)
    @Column(name = ColumnNamePrefix + isTop_FIELDNAME)
    private Boolean isTop = false;

    public static final String lastReadTime_FIELDNAME = "lastReadTime";
    @FieldDescribe("마지막 읽은 시간")
    @Column(name = ColumnNamePrefix + lastReadTime_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private Date lastReadTime;

    public static final String lastDeleteTime_FIELDNAME = "lastDeleteTime";
    @FieldDescribe("현재 사용자가 대화를 삭제한 시간")
    @Column(name = ColumnNamePrefix + lastDeleteTime_FIELDNAME)
    private Date lastDeleteTime;

    public static final String isDeleted_FIELDNAME = "isDeleted";
    @FieldDescribe("삭제 여부. 개인이 삭제하면 대화 목록에 표시되지 않으며, 대화에 새 채팅 메시지가 있으면 true로 재설정됨.")
    @Column(name = ColumnNamePrefix + isDeleted_FIELDNAME)
    private Boolean isDeleted = false;

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public Boolean getIsTop() {
        return isTop;
    }

    public void setIsTop(Boolean isTop) {
        this.isTop = isTop;
    }

    public Date getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(Date lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public Date getLastDeleteTime() {
        return lastDeleteTime;
    }

    public void setLastDeleteTime(Date lastDeleteTime) {
        this.lastDeleteTime = lastDeleteTime;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
