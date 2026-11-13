package com.x.program.center.welink;

import com.x.base.core.project.gson.GsonPropertyObject;

public class User extends GsonPropertyObject {

	//{
	//          "userStatus": "1",                     //상태, 1: 미개설, 2: 개설 중, 3: 개설됨, 4: 해지됨
	//          "userId": "zhangsan1@welink",       //사용자 계정, Key값
	//          "deptCode": "10001",                   //부서Id, Key값, 필수
	//          "deptNameCn": "72270테스트부서",
	//          "deptNameEn": "72270Test Dept",
	//          "mobileNumber": "+86-15811847236",     //바인딩 휴대폰 번호, 필수
	//          "phoneNumber": "+86-15811847236",      //휴대폰 번호
	//          "landlineNumber": "0755-88888888",     //전화번호(유선)
	//          "userNameCn": "홍길동",                  //사용자 중국어 이름, 필수
	//          "userNameEn": "zhangshan",            //사용자 영문 이름, 필수
	//          "sex": "M",                           //성별, M/F만: M: 남, F: 여, 필수
	//          "corpUserId": "36188",                //사용자 사번(연동용 필드, 계정 개설 시 입력하지 않았으면 비어 있음)
	//          "userEmail": "zhangshan4@126.com",    //사용자 이메일, 필수
	//          "secretary": "zhangshan@welink",   //비서(사용자 계정)
	//          "address": "광둥성 선전",               //주소
	//          "remark": "WeLink에 오신 것을 환영합니다",        //비고
	//          "creationTime": "2018-05-03 13:58:02",  //생성 시간
	//          "lastUpdatedTime": "2018-05-03 13:58:02"  //최종 수정 시간
	//        }

	private String userStatus;
	private String userId;
	private String deptCode;
	private String deptNameCn;
	private String deptNameEn;
	private String mobileNumber;
	private String phoneNumber;
	private String landlineNumber;
	private String userNameCn;
	private String userNameEn;
	private String sex;
	private String corpUserId;
	private String userEmail;
	private String secretary;
	private String address;
	private String remark;
	private String creationTime;
	private String lastUpdatedTime;

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public String getDeptNameCn() {
		return deptNameCn;
	}

	public void setDeptNameCn(String deptNameCn) {
		this.deptNameCn = deptNameCn;
	}

	public String getDeptNameEn() {
		return deptNameEn;
	}

	public void setDeptNameEn(String deptNameEn) {
		this.deptNameEn = deptNameEn;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getLandlineNumber() {
		return landlineNumber;
	}

	public void setLandlineNumber(String landlineNumber) {
		this.landlineNumber = landlineNumber;
	}

	public String getUserNameCn() {
		return userNameCn;
	}

	public void setUserNameCn(String userNameCn) {
		this.userNameCn = userNameCn;
	}

	public String getUserNameEn() {
		return userNameEn;
	}

	public void setUserNameEn(String userNameEn) {
		this.userNameEn = userNameEn;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getCorpUserId() {
		return corpUserId;
	}

	public void setCorpUserId(String corpUserId) {
		this.corpUserId = corpUserId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getSecretary() {
		return secretary;
	}

	public void setSecretary(String secretary) {
		this.secretary = secretary;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public String getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public void setLastUpdatedTime(String lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

}