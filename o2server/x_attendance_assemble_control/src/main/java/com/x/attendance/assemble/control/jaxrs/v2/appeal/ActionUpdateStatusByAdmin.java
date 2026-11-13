package com.x.attendance.assemble.control.jaxrs.v2.appeal;

import com.x.attendance.assemble.control.Business;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.schedule.v2.QueueAttendanceV2DetailModel;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;

/**
 * 관리자가 이상 데이터를 정상으로 수정
 * 
 */
public class ActionUpdateStatusByAdmin extends BaseAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateStatusByAdmin.class);

  ActionResult<Wo> execute(EffectivePerson person, String id) throws Exception {
    if (StringUtils.isEmpty(id)) {
      throw new ExceptionEmptyParameter("id");
    }
    try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
      Business business = new Business(emc);
      if (!business.isManager(person)) {
        throw new ExceptionAccessDenied(person);
      }
      AttendanceV2AppealInfo info = emc.find(id, AttendanceV2AppealInfo.class);
      if (info == null) {
        throw new ExceptionNotExistObject("데이터가 없습니다, " + id);
      }
      emc.beginTransaction(AttendanceV2AppealInfo.class);
      info.setStatus(AttendanceV2AppealInfo.status_TYPE_END_BY_ADMIN); // 관리자 처리
      info.setEndTime(DateTools.now());
      info.setUpdateStatusAdminPerson(person.getDistinguishedName()); // 처리자
      emc.check(info, CheckPersistType.all);
      emc.commit();
      AttendanceV2CheckInRecord record = emc.find(info.getRecordId(), AttendanceV2CheckInRecord.class);
      if (record != null) {
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        record.setCheckInResult(AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL); // 정상으로 수정
        record.setAppealId(info.getId()); // id 반영
        record.setDescription("관리자 처리!"); // 설명
        emc.check(record, CheckPersistType.all);
        emc.commit();
        // 이의 신청 성공 후 해당 데이터 재생성
        LOGGER.info("관리자 처리 성공, 근태 데이터 재생성 요청, Date: {} person: {}", record.getRecordDateString(), record.getUserId());
        ThisApplication.queueV2Detail
            .send(new QueueAttendanceV2DetailModel(record.getUserId(), record.getRecordDateString()));
      } else {
        LOGGER.info("해당 출퇴근 기록 데이터를 찾지 못했습니다," + info.getRecordId());
      }
    }
    ActionResult<Wo> result = new ActionResult<>();
    Wo wo = new Wo();
    wo.setValue(true);
    result.setData(wo);
    return result;
  }

  public static class Wo extends WrapBoolean {

  }
}
