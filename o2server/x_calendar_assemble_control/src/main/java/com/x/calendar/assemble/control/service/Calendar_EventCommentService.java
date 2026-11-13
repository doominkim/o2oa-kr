package com.x.calendar.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.calendar.assemble.control.Business;
import com.x.calendar.common.date.DateOperation;
import com.x.calendar.core.entity.Calendar_EventComment;


/**
 * 캘린더 레코드 정보 서비스 클래스
 * @author O2LEE
 *
 */
public class Calendar_EventCommentService {
	
	private DateOperation dateOperation = new DateOperation();

	/**
	 * 지정 비고 정보 객체 목록 조회
	 * @return
	 * @throws Exception
	 */
	public List<Calendar_EventComment> list(EntityManagerContainer emc, List<String> ids ) throws Exception {
		Business business =  new Business( emc );
		return business.calendar_EventCommentFactory().list(ids);
	}

	public Long countEventBundle( EntityManagerContainer emc, String commentId ) throws Exception {
		if( StringUtils.isEmpty( commentId ) ) {
			return 0L;
		}
		Business business =  new Business( emc );
		return business.calendar_EventCommentFactory().countEventBundle(commentId);
	}

	public Long countRepeatMasterBundle( EntityManagerContainer emc, String commentId ) throws Exception {
		if( StringUtils.isEmpty( commentId ) ) {
			return 0L;
		}
		Business business =  new Business( emc );
		return business.calendar_EventCommentFactory().countRepeatMasterBundle(commentId);
	}

	/**
	 * 확인 시간이 now보다 이른 maxCount건의 ID 목록 조회
	 * @param emc
	 * @param now
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<String> listNeedCheckCommnetIds(EntityManagerContainer emc, Date now, Integer maxCount) throws Exception {
		if( now == null ) {
			now = new Date();
		}
		if( maxCount == null ) {
			maxCount = 1000;
		}
		Business business =  new Business( emc );
		return business.calendar_EventCommentFactory().listNeedCheckCommnetIds( now, maxCount );
	}
}
