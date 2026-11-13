package com.x.attendance.entity.v2;

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Schema(name = "AttendanceV2Holiday", description = "중국 공휴일 데이터.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.AttendanceV2Holiday.table, indexes = {
        @Index(name = PersistenceProperties.AttendanceV2Holiday.table + JpaObject.IndexNameMiddle
                + AttendanceV2Holiday.year_FIELDNAME + "_IDX", columnList = JpaObject.ColumnNamePrefix
                + AttendanceV2Holiday.year_FIELDNAME),
        @Index(name = PersistenceProperties.AttendanceV2Holiday.table + JpaObject.IndexNameMiddle
                + AttendanceV2Holiday.dateString_FIELDNAME + "_IDX", columnList = JpaObject.ColumnNamePrefix
                + AttendanceV2Holiday.dateString_FIELDNAME)
})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2Holiday extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.AttendanceV2Holiday.table;
    private static final long serialVersionUID = 4601040815595094715L;
    public static final String SOURCE_SYNC = "SYNC";
    public static final String SOURCE_API = "API";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Id
    @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
    private String id = createId();

    public void onPersist() throws Exception {
    }

    public static final String year_FIELDNAME = "year";
    @FieldDescribe("연도")
    @Column(name = ColumnNamePrefix + year_FIELDNAME)
    private Integer year;

    public static final String dateString_FIELDNAME = "dateString";
    @FieldDescribe("날짜, 형식 yyyy-MM-dd")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + dateString_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String dateString;

    public static final String name_FIELDNAME = "name";
    @FieldDescribe("공휴일 이름")
    @Column(length = JpaObject.length_64B, name = ColumnNamePrefix + name_FIELDNAME)
    private String name;

    public static final String offDay_FIELDNAME = "offDay";
    @FieldDescribe("휴일 여부, true 휴일, false 대체 근무일")
    @Column(name = ColumnNamePrefix + offDay_FIELDNAME)
    private Boolean offDay;

    public static final String source_FIELDNAME = "source";
    @FieldDescribe("데이터 출처, SYNC 예약 작업 동기화, API 프런트엔드 인터페이스 추가")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + source_FIELDNAME)
    private String source;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getOffDay() {
        return offDay;
    }

    public void setOffDay(Boolean offDay) {
        this.offDay = offDay;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
