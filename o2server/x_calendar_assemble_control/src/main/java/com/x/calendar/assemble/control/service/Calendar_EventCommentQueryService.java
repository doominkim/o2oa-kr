package com.x.calendar.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.calendar.core.entity.Calendar_EventComment;

/**
 * 일정 캘린더 비고 정보 서비스 클래스
 * @author O2LEE
 *
 */
public class Calendar_EventCommentQueryService {
	private Calendar_EventCommentService calendar_EventCommentService = new Calendar_EventCommentService();

	public List<Calendar_EventComment> list(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_EventCommentService.list(emc, ids);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * ID로 지정 캘린더 레코드 비고 정보 조회
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Calendar_EventComment get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(id, Calendar_EventComment.class);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 이벤트 정보에서 commentId가 참조된 횟수 조회
	 * @param commentId
	 * @return
	 * @throws Exception
	 */
	public Long countEventBundle( String commentId ) throws Exception {
		if( StringUtils.isEmpty( commentId ) ) {
			return 0L;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_EventCommentService.countEventBundle(emc, commentId);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 이벤트 반복 주체 정보에서 commentId가 참조된 횟수 조회
	 * @param commentId
	 * @return
	 * @throws Exception
	 */
	public Long countRepeatMasterBundle( String commentId ) throws Exception {
		if( StringUtils.isEmpty( commentId ) ) {
			return 0L;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_EventCommentService.countRepeatMasterBundle(emc, commentId);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 수정 시간이 전달된 시간보다 이른 상위 N개 정보 ID 조회
	 * @param now
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<String> listNeedCheckCommnetIds( Date now, Integer maxCount ) throws Exception {
		if( now == null ) {
			now = new Date();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_EventCommentService.listNeedCheckCommnetIds( emc, now, maxCount );
		} catch ( Exception e ) {
			throw e;
		}
	}
}
