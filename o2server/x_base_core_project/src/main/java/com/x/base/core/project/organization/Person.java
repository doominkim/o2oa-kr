package com.x.base.core.project.organization;

import java.util.Date;

import com.x.base.core.entity.type.GenderType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class Person extends GsonPropertyObject {

	@FieldDescribe("일치 필드")
	private String matchKey;
	@FieldDescribe("데이터베이스 기본 키")
	private String id;
	@FieldDescribe("개인 이름")
	private String name;
	@FieldDescribe("닉네임")
	private String nickName;
	@FieldDescribe("성별,m:남,f:여,d:알 수 없음")
	private GenderType genderType;
	@FieldDescribe("서명")
	private String signature;
	@FieldDescribe("설명")
	private String description;
	@FieldDescribe("사번")
	private String employee;
	@FieldDescribe("고유 식별자")
	private String unique;
	@FieldDescribe("식별 이름")
	private String distinguishedName;
	@FieldDescribe("정렬 번호")
	private Integer orderNumber;
	@FieldDescribe("상급자")
	private String superior;
	@FieldDescribe("이메일 주소")
	private String mail;
	@FieldDescribe("위챗 ID")
	private String weixin;
	@FieldDescribe("qq 번호")
	private String qq;
	@FieldDescribe("휴대폰 번호")
	private String mobile;
	@FieldDescribe("사무실 전화")
	private String officePhone;
	@FieldDescribe("입사일")
	private Date boardDate;
	@FieldDescribe("생일")
	private Date birthday;
	@FieldDescribe("나이")
	private Integer age;
	@FieldDescribe("기업 위챗 Id")
	private String qiyeweixinId;
	@FieldDescribe("딩톡 Id")
	private String dingdingId;
	@FieldDescribe("정부 딩톡 Id")
	private String zhengwuDingdingId;
	@FieldDescribe("화웨이 WeLink id")
	private String weLinkId;
	@FieldDescribe("주체 보안 등급 식별자.")
	private Integer subjectSecurityClearance;

	public Integer getSubjectSecurityClearance() {
		return subjectSecurityClearance;
	}

	public void setSubjectSecurityClearance(Integer subjectSecurityClearance) {
		this.subjectSecurityClearance = subjectSecurityClearance;
	}

	public String getMatchKey() {
		return matchKey;
	}

	public void setMatchKey(String matchKey) {
		this.matchKey = matchKey;
	}

	public String getWeLinkId() {
		return weLinkId;
	}

	public void setWeLinkId(String weLinkId) {
		this.weLinkId = weLinkId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GenderType getGenderType() {
		return genderType;
	}

	public void setGenderType(GenderType genderType) {
		this.genderType = genderType;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public String getUnique() {
		return unique;
	}

	public void setUnique(String unique) {
		this.unique = unique;
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getSuperior() {
		return superior;
	}

	public void setSuperior(String superior) {
		this.superior = superior;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getWeixin() {
		return weixin;
	}

	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getOfficePhone() {
		return officePhone;
	}

	public void setOfficePhone(String officePhone) {
		this.officePhone = officePhone;
	}

	public Date getBoardDate() {
		return boardDate;
	}

	public void setBoardDate(Date boardDate) {
		this.boardDate = boardDate;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getQiyeweixinId() {
		return qiyeweixinId;
	}

	public void setQiyeweixinId(String qiyeweixinId) {
		this.qiyeweixinId = qiyeweixinId;
	}

	public String getDingdingId() {
		return dingdingId;
	}

	public void setDingdingId(String dingdingId) {
		this.dingdingId = dingdingId;
	}

	public String getZhengwuDingdingId() {
		return zhengwuDingdingId;
	}

	public void setZhengwuDingdingId(String zhengwuDingdingId) {
		this.zhengwuDingdingId = zhengwuDingdingId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
}
