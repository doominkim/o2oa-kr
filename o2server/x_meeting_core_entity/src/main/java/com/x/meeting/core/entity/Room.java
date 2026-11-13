package com.x.meeting.core.entity;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Room", description = "회의 관리 회의실.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Room.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Room.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Room extends SliceJpaObject {

	private static final long serialVersionUID = -4115399782853963899L;
	private static final String TABLE = PersistenceProperties.Room.table;

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
		this.pinyin = StringUtils.lowerCase(PinyinHelper.convertToPinyinString(name, "", PinyinFormat.WITHOUT_TONE));
		this.pinyinInitial = StringUtils.lowerCase(PinyinHelper.getShortPinyin(name));
	}

	/* 실행 메서드 업데이트 */
	public static final String pinyin_FIELDNAME = "pinyin";
	@FieldDescribe("name 병음.")
	@Index(name = TABLE + IndexNameMiddle + pinyin_FIELDNAME)
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + pinyin_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String pinyin;

	public static final String pinyinInitial_FIELDNAME = "pinyinInitial";
	@FieldDescribe("name 병음 첫 글자.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + pinyinInitial_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + pinyinInitial_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String pinyinInitial;

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("이름, 중복 불가.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationNotExists =
	/* 이름 중복 검증 */
	@CitationNotExist(fields = { "name", "id" }, type = Room.class))
	private String name;

	public static final String building_FIELDNAME = "building";
	@FieldDescribe("소속 building.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + building_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + building_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String building;

	public static final String floor_FIELDNAME = "floor";
	@FieldDescribe("소속 층.")
	@Column(name = ColumnNamePrefix + floor_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer floor;

	public static final String roomNumber_FIELDNAME = "roomNumber";
	@FieldDescribe("호실 번호.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + roomNumber_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + roomNumber_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String roomNumber;

	public static final String phoneNumber_FIELDNAME = "phoneNumber";
	@FieldDescribe("내선 번호.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + phoneNumber_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + phoneNumber_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String phoneNumber;

	public static final String device_FIELDNAME = "device";
	@FieldDescribe("장비 내용.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + device_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String device;

	public static final String photo_FIELDNAME = "photo";
	@FieldDescribe("사진.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + photo_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String photo;

	public static final String capacity_FIELDNAME = "capacity";
	@FieldDescribe("회의실 수용 인원")
	@Basic(fetch = FetchType.EAGER)
	@Column(name = ColumnNamePrefix + capacity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer capacity;

	public static final String auditor_FIELDNAME = "auditor";
	@FieldDescribe("회의실 신청 검토자")
	@Column(length = length_255B, name = ColumnNamePrefix + auditor_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + auditor_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String auditor;

	public static final String available_FIELDNAME = "available";
	@FieldDescribe("회의실 상태, 사용 가능, 사용 불가")
	@Column(name = ColumnNamePrefix + available_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean available;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("정렬 번호, 오름차순, 비어 있으면 마지막")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + orderNumber_FIELDNAME)
	private Integer orderNumber;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getPinyinInitial() {
		return pinyinInitial;
	}

	public void setPinyinInitial(String pinyinInitial) {
		this.pinyinInitial = pinyinInitial;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getAuditor() {
		return auditor;
	}

	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}

	public Integer getFloor() {
		return floor;
	}

	public void setFloor(Integer floor) {
		this.floor = floor;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
}
