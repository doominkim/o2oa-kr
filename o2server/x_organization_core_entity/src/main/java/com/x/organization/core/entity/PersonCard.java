package com.x.organization.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.entity.type.GenderType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(name = "PersonCard", description = "조직 사용자 명함.")
@Entity
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.PersonCard.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.PersonCard.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class PersonCard extends SliceJpaObject {

	public static final String HIDDENMOBILESYMBOL = "***********";

	private static final long serialVersionUID = 5733185578089403629L;

	private static final String TABLE = PersistenceProperties.PersonCard.table;

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
		/** 기본 정렬 번호 생성 */
		if (null == this.orderNumber) {
			this.orderNumber = DateTools.timeOrderNumber();
		}
		/*if (null == this.groupType || "" == this.groupType) {
			this.groupType = "기본 분류";
		}*/
	}

	/* 실행 메서드 업데이트 */

	/* Entity 기본 필드 끝 */
	
	public static final String name_FIELDNAME = "name";
	@FieldDescribe("이름, 중복 가능.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = false)
	private String name;

	public static final String genderType_FIELDNAME = "genderType";
	@FieldDescribe("성별. 남:m, 여:f, 알 수 없음:d")
	@Enumerated(EnumType.STRING)
	@Column(length = GenderType.length, name = ColumnNamePrefix + genderType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + genderType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private GenderType genderType;
	
	public static final String groupType_FIELDNAME = "groupType";
	@FieldDescribe("소속 분류.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + groupType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + groupType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String groupType;


	public static final String pinyin_FIELDNAME = "pinyin";
	@FieldDescribe("name 병음, 자동 생성")
	@Column(length = length_255B, name = ColumnNamePrefix + pinyin_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + pinyin_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String pinyin;

	public static final String pinyinInitial_FIELDNAME = "pinyinInitial";
	@FieldDescribe("name 병음 첫 글자, 자동 생성")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ pinyinInitial_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + pinyinInitial_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String pinyinInitial;
	
	public static final String distinguishedName_FIELDNAME = "distinguishedName";
	@Flag
	@FieldDescribe("입력자, @P로 끝남.")
	@Column(length = length_255B, name = ColumnNamePrefix + distinguishedName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + distinguishedName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String distinguishedName;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("정렬 번호, 오름차순, 비어 있으면 마지막")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + orderNumber_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer orderNumber;

	public static final String mobile_FIELDNAME = "mobile";
	@Flag
	@FieldDescribe("필수, 휴대폰 번호.")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + mobile_FIELDNAME)
	/** 다른 지역 휴대폰 번호 형식이 달라 외부 검증 사용, mobileString 미사용 */
	@Index(name = TABLE + IndexNameMiddle + mobile_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String mobile;

	public static final String officePhone_FIELDNAME = "officePhone";
	@FieldDescribe("사무실 전화.")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + officePhone_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String officePhone;
	
	public static final String address_FIELDNAME = "address";
	@FieldDescribe("주소.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + address_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + address_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String address;
	
	public static final String description_FIELDNAME = "description";
	@FieldDescribe("비고.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;
	
	public static final String extend1_FIELDNAME = "extend1";
	@Flag
	@FieldDescribe("extend1 확장 필드.")
	@Column(length = length_255B, name = ColumnNamePrefix + extend1_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + extend1_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String extend1;

	public static final String extend2_FIELDNAME = "extend2";
	@Flag
	@FieldDescribe("extend2 확장 필드.")
	@Column(length = length_255B, name = ColumnNamePrefix + extend2_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + extend2_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String extend2;
	
	public static final String extend3_FIELDNAME = "extend3";
	@Flag
	@FieldDescribe("extend3 확장 필드.")
	@Column(length = length_255B, name = ColumnNamePrefix + extend3_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + extend3_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String extend3;

	public static final String inputTime_FIELDNAME = "inputTime";
	@FieldDescribe("입력 시간.")
	@Index(name = TABLE + IndexNameMiddle + inputTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Column(length = length_255B, name = ColumnNamePrefix + inputTime_FIELDNAME)
	private String inputTime;
	
	public static final String status_FIELDNAME = "status";
	@FieldDescribe("상태. 초안, 게시")
	@Column(length = length_255B, name = ColumnNamePrefix + status_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + status_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
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
	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public GenderType getGenderType() {
		return genderType;
	}

	public void setGenderType(GenderType genderType) {
		this.genderType = genderType;
	}

	public String getOfficePhone() {
		return officePhone;
	}

	public void setOfficePhone(String officePhone) {
		this.officePhone = officePhone;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}	

	public String getExtend1() {
		return extend1;
	}

	public void setExtend1(String extend1) {
		this.extend1 = extend1;
	}
	public String getExtend2() {
		return extend2;
	}
	public void setExtend2(String extend2) {
		this.extend2 = extend2;
	}
	public String getExtend3() {
		return extend3;
	}
	public void setExtend3(String extend3) {
		this.extend3 = extend3;
	}
	

	public String getInputTime() {
		return inputTime;
	}

	public void setInputTime(String inputTime) {
		this.inputTime = inputTime;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}