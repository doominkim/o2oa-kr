package com.x.attendance.assemble.control.jaxrs.v2.group;


import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.jaxrs.v2.WoGroupShift;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2GroupSchedule;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.tools.DateTools;

public class ActionGetWithShiftByPersonDate extends BaseAction {

  ActionResult<WoGroupShift> execute(String person, String date) throws Exception {
    if (StringUtils.isEmpty(person)) {
      throw new ExceptionEmptyParameter("person");
    }
    if (StringUtils.isEmpty(date)) {
      throw new ExceptionEmptyParameter("date");
    }
    if (!AttendanceV2Helper.isValidDateString(date)) {
      throw new ExceptionWithMessage("날짜 형식이 올바르지 않습니다. 필요한 형식: yyyy-MM-dd!");
    }
    Date dateCheck = null;
    try {
      dateCheck = DateTools.parse(date, DateTools.format_yyyyMMdd);
    } catch (Exception ignore) {}
    if (dateCheck == null) {
      throw new ExceptionWithMessage("날짜 형식이 잘못되었습니다. 필요한 형식: yyyy-MM-dd");
    }
    ActionResult<WoGroupShift> result = new ActionResult<>();
    WoGroupShift wo = new WoGroupShift();
    try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
      // 현재 사용자의 근태 그룹 조회
      Business business = new Business(emc);
      List<AttendanceV2Group> groups = business.getAttendanceV2ManagerFactory()
          .listGroupWithPerson(person);
      if (groups == null || groups.isEmpty()) {
        result.setData(wo);
        return result;
      }
      AttendanceV2Group group = groups.get(0);
      wo.setGroup(WoGroupShift.WoGroup.copier.copy(group));
      // 고정 근무제
      if (group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Fixed)) {
        // 정상 교대 id
        String shiftId = group.getWorkDateProperties().shiftIdWithDate(dateCheck);
        // 특별 근무일 여부
        if (StringUtils.isEmpty(shiftId)) {
          shiftId = AttendanceV2Helper.specialWorkDayShift(date, group);
        }
        // 특별 공휴일 여부, shiftid 비움
        if (StringUtils.isNotEmpty(shiftId) && AttendanceV2Helper.isSpecialRestDay(date, group)) {
          shiftId = null;
        }
        if (StringUtils.isNotEmpty(shiftId)) {
          AttendanceV2Shift shift = business.getAttendanceV2ManagerFactory().pick(shiftId, AttendanceV2Shift.class);
          if (shift != null) {
            wo.setShift(WoGroupShift.WoShift.copier.copy(shift));
          }
        }
      } else if (group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Arrangement)) {
        List<AttendanceV2GroupSchedule> schedules = business.getAttendanceV2ManagerFactory()
            .listGroupSchedule(group.getId(), null, date, person);
        if (schedules != null && !schedules.isEmpty()) { // 교대 편성 있음
          AttendanceV2GroupSchedule schedule = schedules.get(0);
          if (StringUtils.isNotEmpty(schedule.getShiftId())) {
            AttendanceV2Shift shift = business.getAttendanceV2ManagerFactory().pick(schedule.getShiftId(),
                AttendanceV2Shift.class);
            if (shift != null) { // 교대 객체 있음
              wo.setShift(WoGroupShift.WoShift.copier.copy(shift));
            }
          }
        }
      }
      result.setData(wo);
    }
    return result;
  }
 

  
}
