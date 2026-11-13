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
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

/**
 * 새 주제 게시 후 설정에 따라 지정 사용자에게 메시지 알림 전송
 */
public class QueueNewSubjectNotify extends AbstractQueue<BBSSubjectInfo> {
	
	private static  Logger logger = LoggerFactory.getLogger( QueueNewSubjectNotify.class );

	public void execute( BBSSubjectInfo subjectInfo) throws Exception {
		if( subjectInfo == null ) {
			logger.warn("can not send publish notify for subject: NULL!" );
			return;
		}
		logger.debug("system try to send notify for new subject info:" + subjectInfo.getTitle() );

		BBSForumInfo forumInfo = null;
		BBSSectionInfo mainSectionInfo = null;
		BBSSectionInfo sectionInfo = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			if( StringUtils.isNotEmpty( subjectInfo.getForumId() )){
				forumInfo = emc.find( subjectInfo.getForumId(), BBSForumInfo.class );
			}
			if( StringUtils.isNotEmpty( subjectInfo.getMainSectionId() )){
				mainSectionInfo = emc.find( subjectInfo.getMainSectionId(), BBSSectionInfo.class );
			}
			if( StringUtils.isNotEmpty( subjectInfo.getSectionId() )){
				sectionInfo = emc.find( subjectInfo.getSectionId(), BBSSectionInfo.class );
			}
		}catch( Exception e ){
			throw e;
		}

		Boolean send_pushMessage = false;
		String subjectMessageNotifyType = null;
		if( sectionInfo !=null  ){
			if( !sectionInfo.getSubjectMessageNotify() ){
				send_pushMessage = false;
			}else{
				send_pushMessage = true;
				subjectMessageNotifyType = sectionInfo.getSubjectMessageNotifyType();
			}
		}else{
			send_pushMessage = false;
		}

		if( mainSectionInfo !=null  ){
			if( !mainSectionInfo.getSubjectMessageNotify() ){
				send_pushMessage = false;
			}else{
				send_pushMessage = true;
				subjectMessageNotifyType = mainSectionInfo.getSubjectMessageNotifyType();
			}
		}

		if( forumInfo !=null  ){
			if( !forumInfo.getSubjectMessageNotify() ){
				send_pushMessage = false;
			}else{
				send_pushMessage = true;
				subjectMessageNotifyType = forumInfo.getSubjectMessageNotifyType();
			}
		}else{
			send_pushMessage = false;
		}

		MessageWo wo = composeMessageWo( forumInfo, sectionInfo, subjectInfo );
		if( send_pushMessage && StringUtils.isNotEmpty( subjectMessageNotifyType )){
			//pushMessage 전송 시도
			List<String> managerAndModerator = caculatePerson( forumInfo, sectionInfo, subjectMessageNotifyType );
			//중복 제거
			HashSet<String> set = new HashSet<String>( managerAndModerator );
			managerAndModerator.clear();
			managerAndModerator.addAll(set);

			//관리자에게 Message 알림 전송
			MessageFactory.notifyManager_forNewSubject( managerAndModerator, wo );
		}
		logger.debug("system send notify for new SubjectInfo completed!" );
	}

	/**
	 * 지정 푸시 설정에 따라 알림을 받을 사용자 목록 계산
	 * @param forumInfo
	 * @param sectionInfo
	 * @param subjectMessageNotifyType
	 * @return
	 */
	private List<String> caculatePerson( BBSForumInfo forumInfo, BBSSectionInfo sectionInfo, String subjectMessageNotifyType ) {
		List<String> persons = new ArrayList<>();
		String[] sendPersonTypeConfig = null;
		//새 주제 게시 메시지 알림 유형: 총 3자리, 1번째는 구역 관리자 ForumManagerList 알림 여부, 2번째는 게시판 관리자 ModeratorNames 알림 여부, 0-알림 안 함|1-알림
		if( StringUtils.isNotEmpty( subjectMessageNotifyType )) {
			sendPersonTypeConfig = subjectMessageNotifyType.split(",");
		}
		if( sendPersonTypeConfig != null && sendPersonTypeConfig.length == 2 ){
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

	private MessageWo composeMessageWo(BBSForumInfo forumInfo, BBSSectionInfo sectionInfo, BBSSubjectInfo subjectInfo ) {
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
			messageWo.setCreatePerson( subjectInfo.getCreatorName() );
			messageWo.setCreateTime(subjectInfo.getCreateTime() );
		}
		messageWo.setId( subjectInfo.getId() );
		messageWo.setType( "Subject" );
		return messageWo;
	}
}
