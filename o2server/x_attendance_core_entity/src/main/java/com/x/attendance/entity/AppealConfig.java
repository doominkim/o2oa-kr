package com.x.attendance.entity;

public class AppealConfig {
	public static final String APPEAL_AUDIFLOWTYPE_WORKFLOW = "WORKFLOW"; //사용자 정의 워크플로
	public static final String APPEAL_AUDIFLOWTYPE_BUILTIN = "BUILTIN";  //고정 심사 단계
	public static final String APPEAL_AUDITTYPE_PERSON = "지정 사용자";
	public static final String APPEAL_AUDITTYPE_PERSONATTRIBUTE = "사용자 속성";
	public static final String APPEAL_AUDITTYPE_REPORTLEADER = "보고 대상";
	public static final String APPEAL_AUDITTYPE_UNITDUTY = "소속 부서 직무";
	public static final String APPEAL_CHOOSEVALUE_AUDITTYPE = "지정 사용자|사용자 속성|보고 대상|소속 부서 직무";
	public static final String APPEAL_CHOOSEVALUE_CHECKTYPE = "없음|지정 사용자|사용자 속성|보고 대상|소속 부서 직무";
}
