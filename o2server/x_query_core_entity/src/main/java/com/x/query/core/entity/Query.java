package com.x.query.core.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckRemove;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 데이터 센터 앱
 *
 * @author sword
 */
@Schema(name = "Query", description = "데이터 센터 앱.")
@Entity
@ContainerEntity(dumpSize = 10, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Query.TABLE, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.Query.TABLE + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Query extends SliceJpaObject {

    private static final long serialVersionUID = -7520516033901189347L;
    private static final String TABLE = PersistenceProperties.Query.TABLE;

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
    @CheckRemove(citationNotExists = {
            /* Query를 사용하는 View가 더 이상 없음 */
            @CitationNotExist(type = View.class, fields = View.query_FIELDNAME),
            /* Query를 사용하는 Stat이 더 이상 없음 */
            @CitationNotExist(type = Stat.class, fields = Stat.query_FIELDNAME) })
    private String id = createId();

    /* 위는 JpaObject 기본 필드 */

    @Override
    public void onPersist() throws Exception {
        this.name = StringUtils.trimToEmpty(this.name);
        this.alias = StringUtils.trimToEmpty(this.alias);
        this.queryCategory = StringUtils.trimToEmpty(this.queryCategory);
    }

    /* 실행 메서드 업데이트 */

    /* flag 플래그 비트 */
    /* Entity 기본 필드 끝 */

    public static final String name_FIELDNAME = "name";
    @Flag
    @FieldDescribe("이름.")
    @Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
    @CheckPersist(allowEmpty = false, simplyString = true, citationNotExists = @CitationNotExist(type = Query.class, fields = {
            "name", "alias" }))
    private String name;

    public static final String alias_FIELDNAME = "alias";
    @Flag
    @FieldDescribe("앱 별칭, 있으면 고유해야 함.")
    @Column(length = length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + alias_FIELDNAME)
    @CheckPersist(allowEmpty = true, simplyString = true, citationNotExists = @CitationNotExist(type = Query.class, fields = {
            "name", "alias" }))
    private String alias;

    public static final String description_FIELDNAME = "description";
    @FieldDescribe("설명.")
    @Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String description;

    public static final String availableIdentityList_FIELDNAME = "availableIdentityList";
    @FieldDescribe("사용 허용 사용자.")
    @PersistentCollection(fetch = FetchType.EAGER)
    @ContainerTable(name = TABLE + ContainerTableNameMiddle
            + availableIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
                    + availableIdentityList_FIELDNAME + JoinIndexNameSuffix))
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ElementColumn(length = length_255B, name = ColumnNamePrefix + availableIdentityList_FIELDNAME)
    @ElementIndex(name = TABLE + IndexNameMiddle + availableIdentityList_FIELDNAME + ElementIndexNameSuffix)
    @CheckPersist(allowEmpty = true)
    private List<String> availableIdentityList;

    public static final String availableUnitList_FIELDNAME = "availableUnitList";
    @FieldDescribe("사용 허용 조직.")
    @PersistentCollection(fetch = FetchType.EAGER)
    @ContainerTable(name = TABLE + ContainerTableNameMiddle
            + availableUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
                    + availableUnitList_FIELDNAME + JoinIndexNameSuffix))
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ElementColumn(length = length_255B, name = ColumnNamePrefix + availableUnitList_FIELDNAME)
    @ElementIndex(name = TABLE + IndexNameMiddle + availableUnitList_FIELDNAME + ElementIndexNameSuffix)
    @CheckPersist(allowEmpty = true)
    private List<String> availableUnitList;

    public static final String availableGroupList_FIELDNAME = "availableGroupList";
    @FieldDescribe("사용 허용 그룹.")
    @PersistentCollection(fetch = FetchType.EAGER)
    @ContainerTable(name = TABLE + ContainerTableNameMiddle
            + availableGroupList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
                    + availableGroupList_FIELDNAME + JoinIndexNameSuffix))
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ElementColumn(length = length_255B, name = ColumnNamePrefix + availableGroupList_FIELDNAME)
    @ElementIndex(name = TABLE + IndexNameMiddle + availableGroupList_FIELDNAME + ElementIndexNameSuffix)
    @CheckPersist(allowEmpty = true)
    private List<String> availableGroupList;

    public static final String icon_FIELDNAME = "icon";
    @FieldDescribe("icon Base64 인코딩 텍스트.")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(length = JpaObject.length_1M, name = ColumnNamePrefix + icon_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String icon;

    public static final String iconHue_FIELDNAME = "iconHue";
    @FieldDescribe("icon의 주 색상")
    @Column(length = JpaObject.length_8B, name = ColumnNamePrefix + iconHue_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String iconHue;

    public static final String controllerList_FIELDNAME = "controllerList";
    @FieldDescribe("쿼리 관리자")
    @PersistentCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ContainerTable(name = TABLE + ContainerTableNameMiddle
            + controllerList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + controllerList_FIELDNAME
                    + JoinIndexNameSuffix))
    @ElementColumn(length = length_255B, name = ColumnNamePrefix + controllerList_FIELDNAME)
    @ElementIndex(name = TABLE + IndexNameMiddle + controllerList_FIELDNAME + ElementIndexNameSuffix)
    @CheckPersist(allowEmpty = true)
    private List<String> controllerList;

    public static final String creatorPerson_FIELDNAME = "creatorPerson";
    @FieldDescribe("쿼리 생성자")
    @CheckPersist(allowEmpty = false)
    @Column(length = length_255B, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
    private String creatorPerson;

    public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
    @FieldDescribe("쿼리 최종 수정 시간")
    @CheckPersist(allowEmpty = false)
    @Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
    private Date lastUpdateTime;

    public static final String lastUpdatePerson_FIELDNAME = "lastUpdatePerson";
    @FieldDescribe("쿼리 최종 수정자")
    @CheckPersist(allowEmpty = false)
    @Column(length = length_255B, name = ColumnNamePrefix + lastUpdatePerson_FIELDNAME)
    private String lastUpdatePerson;

    public static final String queryCategory_FIELDNAME = "queryCategory";
    @FieldDescribe("분류")
    @CheckPersist(allowEmpty = true)
    @Column(length = length_255B, name = ColumnNamePrefix + queryCategory_FIELDNAME)
    private String queryCategory;

    public static final String data_FIELDNAME = "data";
    @FieldDescribe("사용자 정의 필드.")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(length = JpaObject.length_1M, name = ColumnNamePrefix + data_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAvailableIdentityList() {
        return availableIdentityList;
    }

    public void setAvailableIdentityList(List<String> availableIdentityList) {
        this.availableIdentityList = availableIdentityList;
    }

    public List<String> getAvailableUnitList() {
        return availableUnitList;
    }

    public void setAvailableUnitList(List<String> availableUnitList) {
        this.availableUnitList = availableUnitList;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<String> getControllerList() {
        return controllerList;
    }

    public void setControllerList(List<String> controllerList) {
        this.controllerList = controllerList;
    }

    public String getCreatorPerson() {
        return creatorPerson;
    }

    public void setCreatorPerson(String creatorPerson) {
        this.creatorPerson = creatorPerson;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastUpdatePerson() {
        return lastUpdatePerson;
    }

    public void setLastUpdatePerson(String lastUpdatePerson) {
        this.lastUpdatePerson = lastUpdatePerson;
    }

    public String getIconHue() {
        return iconHue;
    }

    public void setIconHue(String iconHue) {
        this.iconHue = iconHue;
    }

    public String getQueryCategory() {
        return queryCategory;
    }

    public void setQueryCategory(String queryCategory) {
        this.queryCategory = queryCategory;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<String> getAvailableGroupList() {
        return availableGroupList;
    }

    public void setAvailableGroupList(List<String> availableGroupList) {
        this.availableGroupList = availableGroupList;
    }
}
