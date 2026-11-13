package com.x.message.assemble.communicate.jaxrs.im;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMConversationExt;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class ActionPersonConversationList extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionPersonConversationList.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute ActionPersonConversationList :{}.", effectivePerson::getDistinguishedName);
		if (!effectivePerson.isManager()) {
			throw new ExceptionConversationCheckError("권한이 없습니다. 관리자가 처리해야 합니다");
		}
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (StringUtils.isEmpty(wi.getPerson())) {
			throw new ExceptionEmptyField("person");
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<IMConversation> list = business.imConversationFactory().listConversationWithPerson2(wi.getPerson());
			List<Wo> wos = Wo.copier.copy(list);
			for (Wo wo : wos) {
				IMConversationExt ext = business.imConversationFactory()
						.getConversationExt(wi.getPerson(), wo.getId());
				if (ext != null) {
					wo.setExt(ext);
				} else {
					LOGGER.info("해당 IMConversationExt를 찾을 수 없음?? " + wi.getPerson() + "  " + wo.getId());
				}
				if (BooleanUtils.isTrue(wi.getNeedCount())) {
					wo.setMessageCount(business.imConversationFactory().conversationMessageTotalCount(wo.getId()));
				}
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("사용자 DN")
		private String person;
		@FieldDescribe("메시지 수 통계 필요 여부(조금 느림)")
		private Boolean needCount;

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public Boolean getNeedCount() {
			return needCount;
		}

		public void setNeedCount(Boolean needCount) {
			this.needCount = needCount;
		}
	}

	public static class Wo extends IMConversation {

		private static final long serialVersionUID = 7287973396081917642L;
		@FieldDescribe("메시지 수.")
		private Long messageCount;
		// 확장. 업무 처리용
		private IMConversationExt ext;

		static WrapCopier<IMConversation, Wo> copier = WrapCopierFactory.wo(IMConversation.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, IMConversation.icon_FIELDNAME));


		public IMConversationExt getExt() {
			return ext;
		}

		public void setExt(IMConversationExt ext) {
			this.ext = ext;
		}

		public Long getMessageCount() {
			return messageCount;
		}

		public void setMessageCount(Long messageCount) {
			this.messageCount = messageCount;
		}
	}

}
