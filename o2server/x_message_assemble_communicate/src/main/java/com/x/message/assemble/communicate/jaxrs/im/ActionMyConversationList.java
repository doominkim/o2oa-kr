package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMConversationExt;
import com.x.message.core.entity.IMMsg;
import org.apache.commons.lang3.BooleanUtils;

import static com.x.message.core.entity.IMConversation.CONVERSATION_TYPE_GROUP;
import static com.x.message.core.entity.IMConversation.CONVERSATION_TYPE_SINGLE;

public class ActionMyConversationList extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionMyConversationList.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<IMConversation> list = business.imConversationFactory().listConversationWithPerson2(effectivePerson.getDistinguishedName());
			List<Wo> wos = Wo.copier.copy(list);
			// 일괄 조회 최적화
			List<String> conversationIds = wos.stream().map(Wo::getId).collect(Collectors.toList());
			Map<String, IMConversationExt> extMap = business.imConversationFactory().getConversationExts(effectivePerson.getDistinguishedName(), conversationIds);
			Map<String, Long> unreadMap = business.imConversationFactory().getUnreadNumbers(new ArrayList<>(extMap.values()));
			Map<String, IMMsg> lastMsgMap = business.imConversationFactory().getLastMessages(conversationIds);
			for (Wo wo : wos) {
				IMConversationExt ext = extMap.get(wo.getId());
				if (ext != null) {
					wo.setIsTop(ext.getIsTop());
					wo.setUnreadNumber(unreadMap.getOrDefault(wo.getId(), 0L));
					wo.setExt(ext);
				} else {
					LOGGER.info("해당 IMConversationExt를 찾을 수 없음?? " + effectivePerson.getDistinguishedName() + "  " + wo.getId());
				}
			}

			List<Wo> trueWos = wos.stream().filter((wo) -> { // 삭제된 1:1 채팅은 표시하지 않음
				if (wo.getExt() != null && wo.getExt().getIsDeleted() != null && BooleanUtils.isTrue(wo.getExt().getIsDeleted()) && wo.getType().equals(CONVERSATION_TYPE_SINGLE)) {
					return false;
				}
				return true;
			}).filter((wo)-> {// 빈 대화 삭제
				WoMsg woMsg = null;
				IMMsg lastMsg = lastMsgMap.get(wo.getId());
				if (lastMsg != null) {
					woMsg = WoMsg.copier.copy(lastMsg);
					wo.setLastMessage(woMsg);
				}
				// 그룹 채팅은 채팅 메시지 유무와 관계없이 표시.
				if (wo.getType() != null && wo.getType().equals(CONVERSATION_TYPE_GROUP)) {
					return true;
				}
				// 1:1 채팅은 채팅 메시지가 없으면 표시하지 않음
				return (woMsg != null);
			}).filter(Objects::nonNull).sorted((a, b)-> {
				if (a.getLastMessage() == null && b.getLastMessage() == null) {
					return 0;
				} else if (a.getLastMessage() == null) {
					return 1;
				} else if (b.getLastMessage() == null) {
					return -1;
				}
				Date aC = a.getLastMessage().getCreateTime();
				Date bC = b.getLastMessage().getCreateTime();
				if (aC == null && bC == null) return 0;
				if (aC == null) return 1;
				if (bC == null) return -1;
				return Long.compare(bC.getTime(), aC.getTime());
			}).collect(Collectors.toList());
			result.setData(trueWos);
			return result;
		}
	}

	public static class Wo extends IMConversation {

		@FieldDescribe("상단 고정 여부.")
		private Boolean isTop = false;

		@FieldDescribe("읽지 않은 수.")
		private Long unreadNumber;

		@FieldDescribe("마지막 메시지.")
		private WoMsg lastMessage;
		// 확장. 업무 처리용
		private IMConversationExt ext;

		private static final long serialVersionUID = 3434938936805201380L;

		static WrapCopier<IMConversation, Wo> copier = WrapCopierFactory.wo(IMConversation.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, IMConversation.icon_FIELDNAME));

		public Boolean getIsTop() {
			return isTop;
		}

		public void setIsTop(Boolean isTop) {
			this.isTop = isTop;
		}

		public Long getUnreadNumber() {
			return unreadNumber;
		}

		public void setUnreadNumber(Long unreadNumber) {
			this.unreadNumber = unreadNumber;
		}

		public WoMsg getLastMessage() {
			return lastMessage;
		}

		public void setLastMessage(WoMsg lastMessage) {
			this.lastMessage = lastMessage;
		}

		public IMConversationExt getExt() {
			return ext;
		}

		public void setExt(IMConversationExt ext) {
			this.ext = ext;
		}
	}

	public static class WoMsg extends IMMsg {
		private static final long serialVersionUID = 5910475322522970446L;
		static WrapCopier<IMMsg, WoMsg> copier = WrapCopierFactory.wo(IMMsg.class, WoMsg.class, null,
				JpaObject.FieldsInvisible);
	}
}
