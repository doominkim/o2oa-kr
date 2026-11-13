package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapInFilter extends GsonPropertyObject {

	@FieldDescribe("조회 대상 직원 식별자, 사용자 DistinguishedName")
	private String q_empName;

	private List<String> topUnitNames;

	@FieldDescribe("조회 대상 최상위 조직(회사) 식별자, 조직 DistinguishedName")
	private String q_topUnitName;
	
	private List<String> unitNames;

	@FieldDescribe("조회 대상 조직(부서) 식별자, 조직 DistinguishedName")
	private String q_unitName;

	@FieldDescribe("조회 데이터 연도, yyyy")
	private String q_year;

	@FieldDescribe("조회 데이터 월, mm")
	private String q_month;

	@FieldDescribe("조회 데이터의 통계 주기 연도, 보통 데이터 날짜의 연도와 같음")
	private String cycleYear;

	@FieldDescribe("조회 데이터의 통계 주기 월, 보통 데이터 날짜의 월과 같음")
	private String cycleMonth;

	@FieldDescribe("조회 데이터의 구체적 날짜, yyyy-mm-dd")
	private String q_date;

	private int recordStatus = 999;
	
	private Boolean isAbsent = null;
	
	private Boolean isLate = null;
	
	private Boolean isLeaveEarlier = null;
	
	private Boolean isLackOfTime = null;
	
	private String order = "DESC";

	private String key;

	public String getQ_empName() {
		return q_empName;
	}

	public String getCycleYear() {
		return cycleYear;
	}

	public void setCycleYear(String cycleYear) {
		this.cycleYear = cycleYear;
	}

	public String getCycleMonth() {
		return cycleMonth;
	}

	public void setCycleMonth(String cycleMonth) {
		this.cycleMonth = cycleMonth;
	}

	public void setQ_empName(String q_empName) {
		this.q_empName = q_empName;
	}

	public String getQ_year() {
		return q_year;
	}

	public void setQ_year(String q_year) {
		this.q_year = q_year;
	}

	public String getQ_month() {
		return q_month;
	}

	public void setQ_month(String q_month) {
		this.q_month = q_month;
	}

	public List<String> getTopUnitNames() {
		return topUnitNames;
	}

	public void setTopUnitNames(List<String> topUnitNames) {
		this.topUnitNames = topUnitNames;
	}

	public List<String> getUnitNames() {
		return unitNames;
	}

	public void setUnitNames(List<String> unitNames) {
		this.unitNames = unitNames;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getQ_date() {
		return q_date;
	}

	public void setQ_date(String q_date) {
		this.q_date = q_date;
	}
	
	public Boolean getIsAbsent() {
		return isAbsent;
	}

	public void setIsAbsent(Boolean isAbsent) {
		this.isAbsent = isAbsent;
	}

	public Boolean getIsLate() {
		return isLate;
	}

	public void setIsLate(Boolean isLate) {
		this.isLate = isLate;
	}

	public Boolean getIsLeaveEarlier() {
		return isLeaveEarlier;
	}

	public void setIsLeaveEarlier(Boolean isLeaveEarlier) {
		this.isLeaveEarlier = isLeaveEarlier;
	}

	public Boolean getIsLackOfTime() {
		return isLackOfTime;
	}

	public void setIsLackOfTime(Boolean isLackOfTime) {
		this.isLackOfTime = isLackOfTime;
	}

	public int getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(int recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getQ_topUnitName() {
		return q_topUnitName;
	}

	public void setQ_topUnitName(String q_topUnitName) {
		this.q_topUnitName = q_topUnitName;
	}

	public String getQ_unitName() {
		return q_unitName;
	}

	public void setQ_unitName(String q_unitName) {
		this.q_unitName = q_unitName;
	}
	
}
