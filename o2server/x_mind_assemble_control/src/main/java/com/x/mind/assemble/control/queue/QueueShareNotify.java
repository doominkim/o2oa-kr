package com.x.mind.assemble.control.queue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.control.MessageFactory;
import com.x.mind.assemble.control.service.UserManagerService;
import com.x.mind.entity.MindBaseInfo;

/**
 * 마인드맵 공유 후 모든 공유 대상자에게 메시지 알림 발송
 */
public class QueueShareNotify extends AbstractQueue<MindBaseInfo> {
	
	private static  Logger logger = LoggerFactory.getLogger( QueueShareNotify.class );

	public void execute( MindBaseInfo mindBaseInfo) throws Exception {
		if( mindBaseInfo == null ) {
			logger.warn("can not send publish notify for mind, because mindBaseInfo is NULL!" );
			return;
		}
		logger.debug("system try to send notify for mind:" + mindBaseInfo.getName() );

		List<String> persons = caculatePerson( mindBaseInfo );
		MessageWo wo = composeMessageWo( mindBaseInfo );
		MessageFactory.notify_forMindShare(persons, wo);
		logger.debug("system send notify for new replyInfo completed!" );
	}

	/**
	 * 지정 푸시 설정에 따라 알림을 받을 사용자 목록 계산
	 * @param mindBaseInfo
	 * @return
	 */
	private List<String> caculatePerson( MindBaseInfo mindBaseInfo ) throws Exception {
		UserManagerService userManagerService = new UserManagerService();
		List<String> sendPersons = new ArrayList<>();
		String[] sendPersonTypeConfig = null;

		List<String> groups = mindBaseInfo.getShareGroupList();
		List<String> units = mindBaseInfo.getShareUnitList();
		List<String> persons = mindBaseInfo.getSharePersonList();

		//답글 메시지 알림 유형: 총 3자리, 1번째는 구역 관리자 ForumManagerList 알림 여부, 2번째는 게시판 관리자 ModeratorNames 알림 여부, 3번째는 작성자 알림 여부, 0-알림 안 함|1-알림
		if( ListTools.isNotEmpty( groups )) {
			List<String> pl_ = userManagerService.listPersonListWithGroups( groups );
			if( ListTools.isNotEmpty( pl_ )) {
				sendPersons.addAll( pl_ );
			}
		}
		if( ListTools.isNotEmpty( units )) {
			List<String> pl_ = userManagerService.listPersonListWithUnits( units );
			if( ListTools.isNotEmpty( pl_ )) {
				sendPersons.addAll( pl_ );
			}
		}
		if( ListTools.isNotEmpty( persons )) {
			sendPersons.addAll( persons );
		}
		HashSet<String> set = new HashSet<String>( sendPersons );
		sendPersons.clear();
		sendPersons.addAll(set);
		return sendPersons;
	}

	private MessageWo composeMessageWo( MindBaseInfo mindBaseInfo ) {
		MessageWo messageWo = null;
		if( mindBaseInfo != null ){
			messageWo = new MessageWo();
			messageWo.setId( mindBaseInfo.getFolderId() );
			messageWo.setTitle( mindBaseInfo.getName() );
			messageWo.setCreatePerson( mindBaseInfo.getCreator() );
			messageWo.setCreateTime( mindBaseInfo.getCreateTime() );
		}
		return messageWo;
	}
}
