package com.x.bbs.assemble.control.queue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSReplyInfo;
import com.x.bbs.entity.BBSSubjectInfo;

/**
 * 지정 사용자의 게시글과 답글 닉네임 일괄 수정
 * @author sword
 */
public class NickNameConsumeQueue extends AbstractQueue<String> {

	private static Logger logger = LoggerFactory.getLogger(NickNameConsumeQueue.class);

	@Override
	protected void execute(String person) throws Exception {
		if(StringUtils.isBlank(person)){
			return;
		}
		String nickName = person;
		List<String> subjectIds = new ArrayList<>();
		List<String> replyIds = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.debug("change nick name:{}.", person);
			Business business = new Business(emc);
			nickName = business.organization().person().getNickName(person);
			if(nickName.equals(person)){
				return;
			}
			subjectIds = business.subjectInfoFactory().listSubjectIdsByCreator(person);
			replyIds = business.replyInfoFactory().listReplyIdsByCreator(person);
		} catch (Exception e){
			logger.error(e);
		}
		for(String id : subjectIds){
			try {
				this.updateSubjectNickName(id, nickName);
			} catch (Exception e) {
				logger.warn("게시글 {}의 닉네임 {} 수정 예외: {}",id, nickName, e.getMessage());
			}
		}
		for(String id : replyIds){
			try {
				this.updateReplayNickName(id, nickName);
			} catch (Exception e) {
				logger.warn("답글 {}의 닉네임 {} 수정 예외: {}",id, nickName, e.getMessage());
			}
		}
		if(replyIds.size() > 0 || subjectIds.size() > 0){
			CacheManager.notify(BBSSubjectInfo.class);
		}
	}

	private void updateSubjectNickName(String id, String nickName) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			BBSSubjectInfo bbsSubjectInfo = emc.find(id, BBSSubjectInfo.class);
			if(!BooleanUtils.isTrue(bbsSubjectInfo.getAnonymousSubject())) {
				emc.beginTransaction(BBSSubjectInfo.class);
				bbsSubjectInfo.setNickName(nickName);
				emc.commit();
			}
		}
	}

	private void updateReplayNickName(String id, String nickName) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			BBSReplyInfo bbsReplyInfo = emc.find(id, BBSReplyInfo.class);
			emc.beginTransaction(BBSReplyInfo.class);
			bbsReplyInfo.setNickName(nickName);
			emc.commit();
		}
	}
}
