package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMConversationExt;
import com.x.message.core.entity.IMMsg;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by fancyLou on 2022/12/1.
 * Copyright © 2022 O2. All rights reserved.
 */
public class ActionDeleteGroupConversation extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionDeleteGroupConversation.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String conversationId) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("그룹 채팅 삭제 person:{}, conversationId:{}.", effectivePerson.getDistinguishedName(), conversationId);
        }
        ActionResult<Wo> result = new ActionResult<>();
        if (StringUtils.isEmpty(conversationId)) {
            throw new ExceptionEmptyId();
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

            IMConversation conversation = emc.find(conversationId, IMConversation.class);
            if (conversation == null) {
                throw new ExceptionConversationNotExist();
            }
            // 권한 판단. 그룹 채팅은 관리자만 삭제 가능, 1:1 채팅은 삭제 불가
            if (conversation.getType().equals(IMConversation.CONVERSATION_TYPE_SINGLE) || (conversation.getType().equals(IMConversation.CONVERSATION_TYPE_GROUP)
                    && !effectivePerson.getDistinguishedName().equals(conversation.getAdminPerson()))) {
                throw new ExceptionConvDeleteNoPermission();
            }

            Business business = new Business(emc);
            // 이어서 대화 확장 객체 삭제
            List<String> extIds = business.imConversationFactory().listAllConversationExtIdsWithConversationId(conversationId);
            if (extIds == null || extIds.isEmpty()) {
                LOGGER.info("대화 확장이 없어 비울 필요가 없습니다! conversationId:" + conversationId);
            } else {
                emc.beginTransaction(IMConversationExt.class);
                emc.delete(IMConversationExt.class, extIds);
                emc.commit();
                LOGGER.info("대화 확장 삭제 성공! conversationId:" + conversationId + " ext size：" + extIds.size() + " person："
                        + effectivePerson.getDistinguishedName());
            }
            // 마지막으로 대화 객체 삭제
            emc.beginTransaction(IMConversation.class);
            emc.delete(IMConversation.class, conversation.getId());
            emc.commit();
            LOGGER.info("그룹 채팅 삭제 성공==============================================");
            // 메시지 발송
            sendConversationMsg(conversation.getPersonList(), conversation, MessageConnector.TYPE_IM_CONVERSATION_DELETE);

            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
        }
        return result;
    }

    public static class Wo extends WrapOutBoolean {

        private static final long serialVersionUID = -2723486586341189508L;
    }
}
