package com.x.processplatform.core.entity.content;

import com.x.base.core.entity.JpaObject;

/**
 * 
 * @author zhour 진행 작업을 정의하여 TaskCompleted에 작업 유형을 기록, 판단에 사용
 */
public enum ProcessingType {
	/* 계속 진행 */
	processing,
	/* 시작 */
	start,
	/* 조정 */
	reroute,
	/* 회수 */
	retract,
	/* 처리자 재설정 */
	reset,
	/* 지능형 진행 */
	sameTarget,
	/* 시간 초과 진행 */
	expire,
	/* 관리자 진행 */
	control,
	/* 처리자 추가 */
	appendTask,
	/* 추가된 처리자 */
	beAppendedTask,
	/* 위임, 다른 사람에게 처리 위임 */
	empower;

	public static final int length = JpaObject.length_16B;
}
