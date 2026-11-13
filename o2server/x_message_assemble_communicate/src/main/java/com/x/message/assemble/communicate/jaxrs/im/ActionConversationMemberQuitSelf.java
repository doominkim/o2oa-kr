package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMConversationExt;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class ActionConversationMemberQuitSelf extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionConversationMemberQuitSelf.class);

    ActionResult<Wo> execute(
            EffectivePerson effectivePerson, String conversationId) throws Exception {
        String me = effectivePerson.getDistinguishedName();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute:{}.", me);
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            if (StringUtils.isEmpty(conversationId)) {
                throw new ExceptionEmptyId();
            }
            IMConversation conversation = emc.find(conversationId, IMConversation.class);
            if (conversation == null) {
                throw new ExceptionConversationNotExist();
            }
            ActionResult<ActionImConfig.Wo> config = new ActionImConfig().execute(effectivePerson);
            // 설정 없음 또는 회수 미사용
            if (config == null || !BooleanUtils.isTrue(config.getData().getEnableGroupMemberQuitSelf())) {
                throw new ExceptionConversationQuitError("설정이 활성화되지 않았습니다.");
            }
            // 자신이 만든 대화에서는 나갈 수 없습니다
            if (me.equals(conversation.getAdminPerson())) {
                throw new ExceptionConversationQuitError("자신이 만든 대화방에서는 나갈 수 없습니다.");
            }
            // 기존 구성원 목록
            List<String> oldMembers = conversation.getPersonList();
            if (!oldMembers.contains(me)) {
                throw new ExceptionConversationQuitError("대화방 멤버가 아니므로 나갈 수 없습니다.");
            }
            emc.beginTransaction(IMConversation.class);
            // 대화 나가기
            oldMembers.remove(me);
            conversation.setPersonList(oldMembers);
            conversation.setUpdateTime(new Date());
            emc.check(conversation, CheckPersistType.all);
            emc.commit();
            // 대화 확장 정보 삭제
            Business business = new Business(emc);
            IMConversationExt ext = business.imConversationFactory().getConversationExt(me, conversation.getId());
            if (ext != null) {
                emc.beginTransaction(IMConversationExt.class);
                emc.delete(IMConversationExt.class, ext.getId());
                emc.commit();
            }
            // 대화 아이콘 재생성
            generateConversationIcon(conversation.getId());
            // 메시지 발송
            sendConversationMsg(oldMembers, conversation,
                    MessageConnector.TYPE_IM_CONVERSATION_UPDATE);
            Wo wo = new Wo();
            wo.setValue(true);
            ActionResult<Wo> result = new ActionResult<>();
            result.setData(wo);
            return result;
        }

    }

    public static class Wo extends WrapOutBoolean {

        private static final long serialVersionUID = -7676752582998670730L;
    }


}
