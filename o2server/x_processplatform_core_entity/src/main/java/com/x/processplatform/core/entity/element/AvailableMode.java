package com.x.processplatform.core.entity.element;

import com.x.base.core.entity.JpaObject;

public enum AvailableMode {
	/**
	 * 시작 방식: authenticated 인증된 사용자, assign 지정 사용자, none 아무도 시작 불가
	 */
	authenticated, assign, none;

	public static final int length = JpaObject.length_16B;
}
