package com.x.base.core.entity.enums;

import com.x.base.core.entity.JpaObject;

/**
 * 플랫폼 설계 검색 업무 유형 열거형 클래스
 * script(스크립트: 프로세스 플랫폼 스크립트, 콘텐츠 관리 스크립트, 포털 스크립트, 서비스 관리 에이전트, 서비스 관리 인터페이스)
 * form(프로세스 플랫폼 양식, 콘텐츠 관리 양식)
 * page(포털 페이지)
 * widget(포털의 widget)
 * process(프로세스 플랫폼의 프로세스 템플릿)
 * view(데이터 센터 뷰)
 * table(데이터 센터 자체 생성 테이블)
 * stat(데이터 센터 통계)
 * statement(데이터 센터 쿼리문)
 */
public enum DesignerType {

	script, form, page, widget, process, view, table, stat, statement;
	public static final int length = JpaObject.length_64B;
}
