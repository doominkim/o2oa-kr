package com.x.bbs.entity.enums;


/**
 * 포럼 역할 열거형 클래스
 * @author sword
 */
public enum BbsRoleEnum {

	FORUM_SUPER_MANAGER("FORUM_SUPER_MANAGER", "포럼 관리자", "이 포럼을 관리하는 최고 권한을 가집니다"),
	FORUM_VIEW("FORUM_VIEW", "포럼 보기", "사용자가 포럼 시스템에서 이 포럼에 접근할 수 있습니다"),
	FORUM_INFO_MANAGEMENT("FORUM_INFO_MANAGEMENT", "포럼 정보 관리", "사용자가 포럼의 섹션을 추가, 삭제, 수정할 수 있습니다"),
	FORUM_SUBJECT_PUBLISH("FORUM_SUBJECT_PUBLISH", "포럼 주제 게시", "사용자가 포럼의 모든 섹션에 주제를 게시할 수 있습니다"),
	FORUM_REPLY_PUBLISH("FORUM_REPLY_PUBLISH", "포럼 댓글 작성", "사용자가 포럼의 모든 주제에 댓글을 달 수 있습니다"),

	SECTION_MANAGER("SECTION_MANAGER", "섹션 관리자", "섹션과 섹션 내용을 관리하는 최고 권한을 가집니다");

	private String value;
	private String name;
	private String desc;

	private BbsRoleEnum(String value, String name, String desc) {
		this.value = value;
		this.name = name;
		this.desc = desc;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value == null ? null : value.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

}
