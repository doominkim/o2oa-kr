package com.x.attendance.assemble.control.jaxrs.v2;

import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.base.core.project.exception.PromptException;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

public class ExceptionShiftUsed extends PromptException {


	private static final long serialVersionUID = 385265565348213466L;

	public ExceptionShiftUsed(@NotNull List<AttendanceV2Group> groupList) {
		super("현재 근무조는 근태 그룹 【"+ groupList.stream().map(AttendanceV2Group::getGroupName).collect(Collectors.joining(",")) +"】에서 사용 중입니다!");
	}
}
