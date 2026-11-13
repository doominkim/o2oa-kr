package com.x.bbs.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BBSConfigSetting", description = "포럼 설정.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.BBSConfigSetting.table, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.BBSConfigSetting.table + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BBSConfigSetting extends SliceJpaObject {

    public static final String BBS_CONFIG_USE_NICKNAME = "BBS_USE_NICKNAME";

    public static final String BBS_CONFIG_USE_NICKNAME_YES = "YES";

    private static final long serialVersionUID = 3856138316794473794L;
    private static final String TABLE = PersistenceProperties.BBSConfigSetting.table;

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

    @Override
    public void onPersist() throws Exception {
        // nothing
    }
    /*
     * =========================================================================
     * ========= 위는 JpaObject 기본 필드
     * =========================================================================
     * =========
     */

    /*
     * =========================================================================
     * ========= 아래는 업무별 데이터 테이블 필드 요구사항
     * =========================================================================
     * =========
     */

    public static final String configName_FIELDNAME = "configName";
    @FieldDescribe("시스템 설정 이름")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + configName_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String configName = null;

    public static final String configCode_FIELDNAME = "configCode";
    @FieldDescribe("시스템 설정 코드")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + configCode_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + configCode_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String configCode = null;

    public static final String configValue_FIELDNAME = "configValue";
    @FieldDescribe("설정 값")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + configValue_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String configValue = null;

    public static final String valueType_FIELDNAME = "valueType";
    @FieldDescribe("값 유형: select | identity | number | date | text")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + valueType_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String valueType = null;

    public static final String selectContent_FIELDNAME = "selectContent";
    @FieldDescribe("선택 값, select와 함께 사용, ‘|’로 구분")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + selectContent_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String selectContent = "--없음--";

    public static final String isMultiple_FIELDNAME = "isMultiple";
    @FieldDescribe("다중 값 허용 여부")
    @Column(name = ColumnNamePrefix + isMultiple_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private Boolean isMultiple = false;

    public static final String orderNumber_FIELDNAME = "orderNumber";
    @FieldDescribe("정렬 번호")
    @Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private Integer orderNumber = 1;

    public static final String description_FIELDNAME = "description";
    @FieldDescribe("비고 설명")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + description_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String description = null;

    /**
     * 설정 이름 조회
     *
     * @return
     */
    public String getConfigName() {
        return configName;
    }

    /**
     * 설정 이름 설정
     *
     * @param configName
     */
    public void setConfigName(String configName) {
        this.configName = configName;
    }

    /**
     * 설정 코드 조회
     *
     * @return
     */
    public String getConfigCode() {
        return configCode;
    }

    /**
     * 설정 코드 설정
     *
     * @param configCode
     */
    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    /**
     * 설정 값 조회
     *
     * @return
     */
    public String getConfigValue() {
        return configValue;
    }

    /**
     * 설정 값 설정
     *
     * @param configValue
     */
    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    /**
     * 정렬 번호 조회
     *
     * @return
     */
    public Integer getOrderNumber() {
        return orderNumber;
    }

    /**
     * 정렬 번호 설정
     *
     * @param orderNumber
     */
    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * 비고 설명 정보 조회
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * 비고 설명 정보 설정
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getSelectContent() {
        return selectContent;
    }

    public void setSelectContent(String selectContent) {
        this.selectContent = selectContent;
    }

    public Boolean getIsMultiple() {
        return isMultiple;
    }

    public void setIsMultiple(Boolean isMultiple) {
        this.isMultiple = isMultiple;
    }

}
