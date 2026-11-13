package com.x.attendance.assemble.control.jaxrs.qywx;



import java.util.List;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceQywxDetail;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionDeleteAllQywxAttendanceData extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionDeleteAllQywxAttendanceData.class);


    public ActionResult<WrapBoolean> execute(EffectivePerson effectivePerson) {
        ActionResult<WrapBoolean> result = new ActionResult<>();

        try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (business.isManager(effectivePerson)) {
                List<AttendanceQywxDetail> details  = emc.listAll(AttendanceQywxDetail.class);
                if ( null != details && !details.isEmpty() ) {
                    //데이터베이스 영속화 작업 수행
                    emc.beginTransaction( AttendanceQywxDetail.class );
                    for (AttendanceQywxDetail d : details) {
                        emc.remove(d);
                    }
                    emc.commit();
                    logger.info( "모든 WeCom 출퇴근 기록 데이터를 삭제했습니다"  );
                }
                result.setData(new WrapBoolean(true));
            } else {
                throw new ExceptionNotManager();
            }

        } catch ( Exception e ) {
            result.error(e);
            logger.error(e);
        }

        return result;
    }


}
