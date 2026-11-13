package com.x.processplatform.core.entity.element;

import com.x.base.core.entity.JpaObject;

/**
 * 노드 방식: 단일 활동, 병렬, 순차 실행, 선점 처리
 */
public enum ManualMode {
	single, parallel, queue, grab;

	public static final int length = JpaObject.length_16B;
}