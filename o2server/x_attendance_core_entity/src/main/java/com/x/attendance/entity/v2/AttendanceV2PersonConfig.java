package com.x.attendance.entity.v2;

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;

import javax.persistence.*;
import java.util.List;
import org.apache.openjpa.persistence.jdbc.Strategy;

/**
 * 근태 개인 설정
 * Created by fancyLou on 2023/2/28.
 * Copyright © 2023 O2. All rights reserved.
 */

@Schema(name = "AttendanceV2PersonConfig", description = "근태 개인 설정 정보.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceV2PersonConfig.table, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.AttendanceV2PersonConfig.table + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = {JpaObject.IDCOLUMN,
                JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN}),
        @UniqueConstraint(name = PersistenceProperties.AttendanceV2PersonConfig.table + JpaObject.IndexNameMiddle
                + "person_UNIQUE", columnNames = {JpaObject.ColumnNamePrefix
                + AttendanceV2PersonConfig.person_FIELDNAME})})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2PersonConfig extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.AttendanceV2PersonConfig.table;
    private static final long serialVersionUID = -3015660811179908189L;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @FieldDescribe("데이터베이스 기본 키, 자동 생성.")
    @Id
    @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
    private String id = createId();

    public void onPersist() throws Exception {
    }
    /*
     * =============================================================================
     * ===== 위는 JpaObject 기본 필드
     * =============================================================================
     * =====
     */

    /*
     * =============================================================================
     * ===== 아래는 업무별 데이터 테이블 필드 요구사항
     * =============================================================================
     * =====
     */

    public static final String person_FIELDNAME = "person";
    @FieldDescribe("인원 DN")
    @Column(length = length_128B, name = ColumnNamePrefix + person_FIELDNAME)
    private String person;

    public static final String onDutyFastCheckInEnable_FIELDNAME = "onDutyFastCheckInEnable";
    @FieldDescribe("출근 빠른 체크, app에서만 유효")
    @Column(name = ColumnNamePrefix + onDutyFastCheckInEnable_FIELDNAME)
    private Boolean onDutyFastCheckInEnable = false;

    public static final String onDutyFastCheckInBegin_FIELDNAME = "onDutyFastCheckInBegin";
    @FieldDescribe("출근 빠른 출근 체크 시작, 출근 체크 몇 분 전, 기본값 출근 시간 1시간 전, 앱에서 유효")
    @Column( name = ColumnNamePrefix + onDutyFastCheckInBegin_FIELDNAME)
    private Integer onDutyFastCheckInBegin = -60;

    public static final String onDutyFastCheckInEnd_FIELDNAME = "onDutyFastCheckInEnd";
    @FieldDescribe("출근 빠른 출근 체크 종료, 출근 체크 몇 분 후, 기본값 출근 시간까지, 앱에서 유효")
    @Column(  name = ColumnNamePrefix + onDutyFastCheckInEnd_FIELDNAME)
    private Integer onDutyFastCheckInEnd = 0;

    public static final String offDutyFastCheckInEnable_FIELDNAME = "offDutyFastCheckInEnable";
    @FieldDescribe("퇴근 빠른 체크, app에서만 유효")
    @Column(name = ColumnNamePrefix + offDutyFastCheckInEnable_FIELDNAME)
    private Boolean offDutyFastCheckInEnable = false;

    public static final String offDutyFastCheckInBegin_FIELDNAME = "offDutyFastCheckInBegin";
    @FieldDescribe("퇴근 빠른 퇴근 체크 시작, 퇴근 몇 분 후, 기본값 퇴근 시간, 앱에서 유효")
    @Column( name = ColumnNamePrefix + offDutyFastCheckInBegin_FIELDNAME)
    private Integer offDutyFastCheckInBegin = 0;

    public static final String offDutyFastCheckInEnd_FIELDNAME = "offDutyFastCheckInEnd";
    @FieldDescribe("퇴근 빠른 체크 종료, 퇴근 후 몇 분, 기본값은 퇴근 후 1시간, app에서만 유효")
    @Column(  name = ColumnNamePrefix + offDutyFastCheckInEnd_FIELDNAME)
    private Integer offDutyFastCheckInEnd = 60;


    public static final String PROPERTIES_FIELDNAME = "properties";
    @FieldDescribe("추가 설정 정보.")
    @Persistent
    @Strategy(JsonPropertiesValueHandler)
    @Column(length = JpaObject.length_1M, name = ColumnNamePrefix + PROPERTIES_FIELDNAME)
    private AttendanceV2PersonConfigProperties properties;


    public AttendanceV2PersonConfigProperties getProperties() {
        return properties;
    }

    public void setProperties(AttendanceV2PersonConfigProperties properties) {
        this.properties = properties;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public Boolean getOnDutyFastCheckInEnable() {
        return onDutyFastCheckInEnable;
    }

    public void setOnDutyFastCheckInEnable(Boolean onDutyFastCheckInEnable) {
        this.onDutyFastCheckInEnable = onDutyFastCheckInEnable;
    }

    public Integer getOnDutyFastCheckInBegin() {
        return onDutyFastCheckInBegin;
    }

    public void setOnDutyFastCheckInBegin(Integer onDutyFastCheckInBegin) {
        this.onDutyFastCheckInBegin = onDutyFastCheckInBegin;
    }

    public Integer getOnDutyFastCheckInEnd() {
        return onDutyFastCheckInEnd;
    }

    public void setOnDutyFastCheckInEnd(Integer onDutyFastCheckInEnd) {
        this.onDutyFastCheckInEnd = onDutyFastCheckInEnd;
    }

    public Boolean getOffDutyFastCheckInEnable() {
        return offDutyFastCheckInEnable;
    }

    public void setOffDutyFastCheckInEnable(Boolean offDutyFastCheckInEnable) {
        this.offDutyFastCheckInEnable = offDutyFastCheckInEnable;
    }

    public Integer getOffDutyFastCheckInBegin() {
        return offDutyFastCheckInBegin;
    }

    public void setOffDutyFastCheckInBegin(Integer offDutyFastCheckInBegin) {
        this.offDutyFastCheckInBegin = offDutyFastCheckInBegin;
    }

    public Integer getOffDutyFastCheckInEnd() {
        return offDutyFastCheckInEnd;
    }

    public void setOffDutyFastCheckInEnd(Integer offDutyFastCheckInEnd) {
        this.offDutyFastCheckInEnd = offDutyFastCheckInEnd;
    }
}
