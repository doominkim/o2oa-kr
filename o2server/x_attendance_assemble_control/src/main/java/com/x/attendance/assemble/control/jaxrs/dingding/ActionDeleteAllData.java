package com.x.attendance.assemble.control.jaxrs.dingding;


import java.util.List;

import com.x.attendance.entity.AttendanceDingtalkDetail;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionDeleteAllData extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionDeleteAllData.class);


    public ActionResult<WrapBoolean> execute(EffectivePerson effectivePerson) {
        ActionResult<WrapBoolean> result = new ActionResult<>();

        try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<AttendanceDingtalkDetail> details  = emc.listAll(AttendanceDingtalkDetail.class);
            if ( null == details ) {
                result.error( new FindEmptyException() );
            }else{
                //데이터베이스 영속화 작업 수행
                emc.beginTransaction( AttendanceDingtalkDetail.class );
                for (AttendanceDingtalkDetail d : details) {
                    emc.remove(d);
                }
                emc.commit();
                result.setData(new WrapBoolean(true));
                logger.info( "모든 DingTalk 출퇴근 기록 데이터를 삭제했습니다"  );
            }
        } catch ( Exception e ) {
            result.error(e);
            logger.error(e);
        }

        return result;
    }


}
