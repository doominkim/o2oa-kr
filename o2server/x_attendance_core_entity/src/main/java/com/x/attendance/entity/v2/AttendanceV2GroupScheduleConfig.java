package com.x.attendance.entity.v2;

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

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AttendanceV2GroupScheduleConfig", description = "근태 그룹 근무 배정 설정 데이터.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceV2GroupScheduleConfig.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceV2GroupScheduleConfig.table
        + JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2GroupScheduleConfig  extends SliceJpaObject {


	
	private static final long serialVersionUID = 8147079789841383897L;
	private static final String TABLE = com.x.attendance.entity.PersistenceProperties.AttendanceV2GroupScheduleConfig.table;

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

	public static final String groupId_FIELDNAME = "groupId";
	@FieldDescribe("근태 그룹 id.")
	@Column(length = length_64B, name = ColumnNamePrefix + groupId_FIELDNAME)
  @CheckPersist(allowEmpty = false)
	private String groupId;
 

  public static final String scheduleConfigJson_FIELDNAME = "scheduleConfigJson";
  @FieldDescribe("설정 json 문자열")
	@Lob
	@Basic(fetch = FetchType.EAGER)
  @Column(length = JpaObject.length_128K, name = ColumnNamePrefix + scheduleConfigJson_FIELDNAME)
  private String scheduleConfigJson;
    

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

  public String getScheduleConfigJson() {
    return scheduleConfigJson;
  }

  public void setScheduleConfigJson(String scheduleConfigJson) {
    this.scheduleConfigJson = scheduleConfigJson;
  }
 

}
