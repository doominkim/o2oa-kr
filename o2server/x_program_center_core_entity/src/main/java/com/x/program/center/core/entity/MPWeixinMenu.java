package com.x.program.center.core.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Created by fancyLou on 3/12/21.
 * Copyright © 2021 O2. All rights reserved.
 */
@Schema(name = "MPWeixinMenu", description = "서비스 관리 위챗 메뉴.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.MPWeixinMenu.TABLE, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.MPWeixinMenu.TABLE + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class MPWeixinMenu extends SliceJpaObject {


    private static final String TABLE = PersistenceProperties.MPWeixinMenu.TABLE;
    private static final long serialVersionUID = 1188998090619699016L;


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


    public static final String name_FIELDNAME = "name";
    @FieldDescribe("메뉴 표시 이름.")
    @Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String name;


    public static final String type_FIELDNAME = "type";
    @FieldDescribe("위챗 메뉴 유형, 현재 click, view, miniprogram 지원 ")
    @Column(length = length_255B, name = ColumnNamePrefix + type_FIELDNAME)
    private String type;


    public static final String key_FIELDNAME = "key";
    @FieldDescribe("클릭 이벤트의 key, type이 click이면 이 key는 필수")
    @Column(length = length_255B, name = ColumnNamePrefix + key_FIELDNAME)
    private String key;


    public static final String content_FIELDNAME = "content";
    @FieldDescribe("type이 click이면 발송할 메시지 내용, 현재 텍스트 메시지 지원, a 링크 추가 가능")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(length = length_1M, name = ColumnNamePrefix + content_FIELDNAME)
    private String content;



    public static final String url_FIELDNAME = "url";
    @FieldDescribe("열 웹 페이지 주소, type이 view 또는 miniprogram이면 이 url은 필수")
    @Column(length = length_2K, name = ColumnNamePrefix + url_FIELDNAME)
    private String url;



    public static final String appid_FIELDNAME = "appid";
    @FieldDescribe("열 미니프로그램 id, type이 miniprogram이면 이 appid는 필수")
    @Column(length = length_255B, name = ColumnNamePrefix + appid_FIELDNAME)
    private String appid;



    public static final String pagepath_FIELDNAME = "pagepath";
    @FieldDescribe("열 미니프로그램 페이지 주소, type이 miniprogram이면 이 pagepath는 필수")
    @Column(length = length_255B, name = ColumnNamePrefix + pagepath_FIELDNAME)
    private String pagepath;


    public static final String parentId_FIELDNAME = "parentId";
    @FieldDescribe("상위 메뉴 Id")
    @Column(length = length_64B, name = ColumnNamePrefix + parentId_FIELDNAME)
    private String parentId;



    public static final String order_FIELDNAME = "order";
    @FieldDescribe("정렬 번호, 문자열 정렬")
    @Column(length = length_16B, name = ColumnNamePrefix + order_FIELDNAME)
    private String order;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPagepath() {
        return pagepath;
    }

    public void setPagepath(String pagepath) {
        this.pagepath = pagepath;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
