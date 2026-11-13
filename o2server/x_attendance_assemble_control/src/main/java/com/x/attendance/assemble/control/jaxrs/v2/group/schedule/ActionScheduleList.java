package com.x.attendance.assemble.control.jaxrs.v2.group.schedule;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.base.core.project.x_attendance_assemble_control;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionScheduleList extends BaseAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionScheduleList.class);

  ActionResult<List<ScheduleWo>> execute(String groupId, String month) throws Exception {
    if (StringUtils.isEmpty(groupId)) {
      throw new ExceptionEmptyParameter("groupId");
    }
    if (StringUtils.isEmpty(month)) {
      throw new ExceptionEmptyParameter("month");
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("교대 편성 데이터 조회  groupId {} month {}", groupId, month);
    }
    ActionResult<List<ScheduleWo>> result = new ActionResult<>();
    ActionScheduleListFilter.Wi filterWi = new ActionScheduleListFilter.Wi();
    filterWi.setGroupId(groupId);
    filterWi.setMonth(month);
    // ActionScheduleListFilter 실행해 교대 편성 데이터 조회
    List<ScheduleWo> wos = ThisApplication.context().applications()
        .postQuery(x_attendance_assemble_control.class, "v2/groupschedule/list/filter", filterWi)
        .getDataAsList(ScheduleWo.class);
    result.setData(wos);
    return result;
  }
}
