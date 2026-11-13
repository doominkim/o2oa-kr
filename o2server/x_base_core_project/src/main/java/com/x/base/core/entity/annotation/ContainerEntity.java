package com.x.base.core.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Entity 식별용. @Entity 표시는 다른 클래스까지 스캔될 수 있으므로 별도 표시로 자체 생성 클래스임을 나타내어 scan 시 구분할 수 있게 함
 * 
 * 
 * @author zhour
 * 
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface ContainerEntity {

	/**
	 * dump 실행 시 그룹 수
	 */
	public int dumpSize() default 200;

	/**
	 * 유형
	 */
	public Type type() default Type.content;

	/**
	 * 참조, 강한 데이터, 약한 데이터(약한 데이터는 시스템 실행에 영향을 주지 않음)
	 */

	public Reference reference() default Reference.strong;

	public enum Type {
		element, content, log, custom;
	}

	public enum Reference {
		strong, soft;
	}
}
