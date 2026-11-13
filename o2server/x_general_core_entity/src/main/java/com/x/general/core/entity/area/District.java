package com.x.general.core.entity.area;

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
import com.x.general.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "District", description = "공통 지역.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.soft)
@Entity
@Table(name = PersistenceProperties.Area.District.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Area.District.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class District extends SliceJpaObject {

	private static final long serialVersionUID = -4792895013245264264L;

	private static final String TABLE = PersistenceProperties.Area.District.table;

	public static final String LEVEL_PROVINCE = "province";
	public static final String LEVEL_CITY = "city";
	public static final String LEVEL_DISTRICT = "district";
	public static final String LEVEL_STREET = "street";

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

	/* 위는 JpaObject 기본 필드 */

	public void onPersist() throws Exception {

	}

	/* 실행 메서드 업데이트 */

	/* 기본 내용 끝 */

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("이름.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String name;

	public static final String zipCode_FIELDNAME = "zipCode";
	@FieldDescribe("우편번호.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + zipCode_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + zipCode_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String zipCode;

	public static final String cityCode_FIELDNAME = "cityCode";
	@FieldDescribe("도시 지역번호.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + cityCode_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + cityCode_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String cityCode;

	public static final String level_FIELDNAME = "level";
	@FieldDescribe("등급.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + level_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + level_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String level;

	public static final String center_FIELDNAME = "center";
	@FieldDescribe("중심 좌표.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + center_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + center_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String center;

	public static final String province_FIELDNAME = "province";
	@FieldDescribe("상위 도.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + province_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + province_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String province;

	public static final String city_FIELDNAME = "city";
	@FieldDescribe("상위 시.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + city_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + city_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String city;

	public static final String district_FIELDNAME = "district";
	@FieldDescribe("상위 구.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + district_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + district_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String district;

	public static final String sha_FIELDNAME = "sha";
	@FieldDescribe("md5 검증 코드.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + sha_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sha_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sha;

	public String getName() {
		return name;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getCenter() {
		return center;
	}

	public void setCenter(String center) {
		this.center = center;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	public void setName(String name) {
		this.name = name;
	}

}