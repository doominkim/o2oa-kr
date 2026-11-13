package com.x.bbs.assemble.control.queue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.MessageFactory;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSReplyInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

/**
 * Document 정식 게시 후 모든 열람자에게 메시지 알림 푸시
 */
public class QueueNewReplyNotify extends AbstractQueue<BBSReplyInfo> {
	
	private static  Logger logger = LoggerFactory.getLogger( QueueNewReplyNotify.class );

	public void execute( BBSReplyInfo replyInfo) throws Exception {
		if( replyInfo == null ) {
			logger.warn("can not send publish notify for subject reply, bbsReplyInfo is NULL!" );
			return;
		}
		logger.debug("system try to send notify for new replyInfo:" + replyInfo.getTitle() );

		BBSForumInfo forumInfo = null;
		BBSSectionInfo mainSectionInfo = null;
		BBSSectionInfo sectionInfo = null;
		BBSSubjectInfo subjectInfo = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			if( StringUtils.isNotEmpty( replyInfo.getForumId() )){
				forumInfo = emc.find( replyInfo.getForumId(), BBSForumInfo.class );
			}
			if( StringUtils.isNotEmpty( replyInfo.getMainSectionId() )){
				mainSectionInfo = emc.find( replyInfo.getMainSectionId(), BBSSectionInfo.class );
			}
			if( StringUtils.isNotEmpty( replyInfo.getSectionId() )){
				sectionInfo = emc.find( replyInfo.getSectionId(), BBSSectionInfo.class );
			}
			if( StringUtils.isNotEmpty( replyInfo.getSubjectId() )){
				subjectInfo = emc.find( replyInfo.getSubjectId(), BBSSubjectInfo.class );
			}
		}catch( Exception e ){
			throw e;
		}

		Boolean send_pushMessage = false;
		String replyMessageNotifyType = null;
		if( sectionInfo !=null  ){
			if( !sectionInfo.getReplyMessageNotify() ){
				send_pushMessage = false;
			}else{
				send_pushMessage = true;
				replyMessageNotifyType = sectionInfo.getReplyMessageNotifyType();
			}
		}else{
			send_pushMessage = false;
		}

		if( mainSectionInfo !=null  ){
			if( !mainSectionInfo.getReplyMessageNotify() ){
				send_pushMessage = false;
			}else{
				send_pushMessage = true;
				replyMessageNotifyType = mainSectionInfo.getReplyMessageNotifyType();
			}
		}

		if( forumInfo !=null  ){
			if( !forumInfo.getReplyMessageNotify() ){
				send_pushMessage = false;
			}else{
				send_pushMessage = true;
				replyMessageNotifyType = forumInfo.getReplyMessageNotifyType();
			}
		}else{
			send_pushMessage = false;
		}

		MessageWo wo = composeMessageWo( forumInfo, sectionInfo, subjectInfo, replyInfo );
		if( send_pushMessage && StringUtils.isNotEmpty( replyMessageNotifyType )){
			//pushMessage 전송 시도
			List<String> managerAndModerator = caculatePerson( forumInfo, sectionInfo, replyMessageNotifyType );
			//중복 제거
			HashSet<String> set = new HashSet<String>( managerAndModerator );
			managerAndModerator.clear();
			managerAndModerator.addAll(set);

			//관리자에게 Message 알림 전송
			MessageFactory.notifyManager_forNewReply( managerAndModerator, wo );
			//주제 작성자에게 Message 알림 전송
			if( needNotifySubjectCreator( replyMessageNotifyType ) ){
				MessageFactory.notifySubjectCreator_forNewReply( subjectInfo.getCreatorName(), wo );
			}
		}
		logger.debug("system send notify for new replyInfo completed!" );
	}

	/**
	 * 지정 푸시 설정에 따라 알림을 받을 사용자 목록 계산
	 * @param forumInfo
	 * @param sectionInfo
	 * @param replyMessageNotifyType
	 * @return
	 */
	private List<String> caculatePerson( BBSForumInfo forumInfo, BBSSectionInfo sectionInfo, String replyMessageNotifyType ) {
		List<String> persons = new ArrayList<>();
		String[] sendPersonTypeConfig = null;
		//답글 메시지 알림 유형: 총 3자리, 1번째는 구역 관리자 ForumManagerList 알림 여부, 2번째는 게시판 관리자 ModeratorNames 알림 여부, 3번째는 작성자 알림 여부, 0-알림 안 함|1-알림
		if( StringUtils.isNotEmpty( replyMessageNotifyType )) {
			sendPersonTypeConfig = replyMessageNotifyType.split(",");
		}
		if( sendPersonTypeConfig != null && sendPersonTypeConfig.length == 3 ){
			if( StringUtils.equals( sendPersonTypeConfig[0], "1" ) ){
				if( forumInfo != null && ListTools.isNotEmpty( forumInfo.getForumManagerList() ) ){
					for( String manager : forumInfo.getForumManagerList() ){
						if( !ListTools.contains( persons, manager )){
							persons.add( manager );
						}
					}
				}
			}
			if( StringUtils.equals( sendPersonTypeConfig[1], "1" ) ){
				if( sectionInfo != null && ListTools.isNotEmpty( sectionInfo.getModeratorNames())){
					for( String moderator : sectionInfo.getModeratorNames()){
						if( !ListTools.contains( persons, moderator )){
							persons.add( moderator );
						}
					}
				}
			}
		}
		return persons;
	}

	/**
	 * 알림 유형 설정에 따라 주제 작성자에게 알림을 보낼지 확인
	 * @param replyMessageNotifyType
	 * @return
	 */
	private boolean needNotifySubjectCreator( String replyMessageNotifyType ) {
		String[] sendPersonTypeConfig = null;
		//답글 메시지 알림 유형: 총 3자리, 1번째는 구역 관리자 ForumManagerList 알림 여부, 2번째는 게시판 관리자 ModeratorNames 알림 여부, 3번째는 작성자 알림 여부, 0-알림 안 함|1-알림
		if( StringUtils.isNotEmpty( replyMessageNotifyType )) {
			sendPersonTypeConfig = replyMessageNotifyType.split(",");
		}
		if( sendPersonTypeConfig != null && sendPersonTypeConfig.length == 3 ){
			if( StringUtils.equals( sendPersonTypeConfig[2], "1" ) ){
				return true;
			}
		}
		return false;
	}

	private MessageWo composeMessageWo(BBSForumInfo forumInfo, BBSSectionInfo sectionInfo, BBSSubjectInfo subjectInfo, BBSReplyInfo replyInfo) {
		MessageWo messageWo = new MessageWo();
		if( forumInfo != null ){
			messageWo.setForumId( forumInfo.getId() );
			messageWo.setForumName( forumInfo.getForumName() );
		}
		if( sectionInfo != null ){
			messageWo.setSelectionId( sectionInfo.getId() );
			messageWo.setSelectionName( sectionInfo.getSectionName() );
		}
		if( subjectInfo != null ){
			messageWo.setTitle( subjectInfo.getTitle() );
			messageWo.setSubjectId( subjectInfo.getId() );
		}
		if( subjectInfo != null ){
			messageWo.setReplyId( replyInfo.getId() );
			messageWo.setCreatePerson( replyInfo.getCreatorName() );
			messageWo.setCreateTime(replyInfo.getCreateTime() );
		}
		messageWo.setId( replyInfo.getId() );
		messageWo.setType( "Reply" );
		return messageWo;
	}
}
