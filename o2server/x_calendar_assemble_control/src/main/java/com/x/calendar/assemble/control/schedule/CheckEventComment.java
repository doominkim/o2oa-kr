package com.x.calendar.assemble.control.schedule;

import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.service.Calendar_EventCommentQueryService;
import com.x.calendar.core.entity.Calendar_EventComment;

/**
 * event 또는 event_master에서 참조되지 않는 EventComment 레코드가 있는지 주기적으로 확인하고, 있으면 삭제
 * 
 * @author O2LEE
 *
 */
public class CheckEventComment extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(CheckEventComment.class);
	protected Calendar_EventCommentQueryService calendar_EventCommentQueryService = new Calendar_EventCommentQueryService();

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		Date now = new Date();
		List<String> ids = calendar_EventCommentQueryService.listNeedCheckCommnetIds( now, 500 );
		Integer maxWhileTimes = 10;
		while( ListTools.isNotEmpty( ids )){
			if( --maxWhileTimes < 0 ){
				break;
			}
			ids.forEach( commentId->{
				try {
					if( !check( commentId, now )){
						removeEventComment( commentId );
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			ids = calendar_EventCommentQueryService.listNeedCheckCommnetIds( now, 500 );
		}
		logger.info("The trigger for calendar alarm execute completed." + new Date());
	}

	/**
	 * commentId로 지정 비고 정보 삭제
	 * @param commentId
	 */
	private void removeEventComment(String commentId ) throws Exception {
		Boolean quote = false;
		if( calendar_EventCommentQueryService.countEventBundle( commentId ) > 0 ){
			quote = true;
		}
		if( calendar_EventCommentQueryService.countRepeatMasterBundle( commentId ) > 0 ){
			quote = true;
		}
		if( !quote ){
			//참조 없음, 바로
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Calendar_EventComment entity = emc.find( commentId, Calendar_EventComment.class );
				if( entity != null ){
					emc.beginTransaction( Calendar_EventComment.class );
					emc.remove( entity, CheckRemoveType.all );
					emc.commit();
				}
			}
		}
	}

	/**
	 * 해당 ID를 참조하는 이벤트가 있는지 확인
	 * 
	 * @param commentId
	 * @return
	 * @throws Exception
	 */
	private boolean check( String commentId, Date now ) throws Exception {
		if( calendar_EventCommentQueryService.countEventBundle( commentId ) > 0 ){
			return true;
		}
		if( calendar_EventCommentQueryService.countRepeatMasterBundle( commentId ) > 0 ){
			return true;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Calendar_EventComment entity = emc.find( commentId, Calendar_EventComment.class );
			if( entity != null ){
				emc.beginTransaction( Calendar_EventComment.class );
				entity.setCheckTime( new Date() );
				emc.check( entity, CheckPersistType.all );
				emc.commit();
			}
		}
		return false;
	}

}
