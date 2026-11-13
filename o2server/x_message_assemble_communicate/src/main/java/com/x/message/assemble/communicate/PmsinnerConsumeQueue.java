package com.x.message.assemble.communicate;

import com.google.gson.JsonObject;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Application;
import com.x.base.core.project.x_jpush_assemble_control;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.message.assemble.communicate.message.PmsInnerMessage;
import com.x.message.core.entity.Message;

public class PmsinnerConsumeQueue extends AbstractQueue<Message> {
	private static final Logger LOGGER = LoggerFactory.getLogger(PmsinnerConsumeQueue.class);


	protected void execute(Message message) throws Exception {
		Application app = ThisApplication.context().applications()
				.randomWithWeight(x_jpush_assemble_control.class.getName());
		if (null != app) {
			PmsInnerMessage innerMessage = new PmsInnerMessage();
			innerMessage.setPerson(message.getPerson());
			innerMessage.setMessage(message.getTitle());
			Object e = extra(message);
			if (e != null) {
				Map<String, Object> jsonExtras = new HashMap<>();
				jsonExtras.put("business", e);
				innerMessage.setJsonExtras(jsonExtras);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("PMS로 메시지 발송, 메시지:{}",   innerMessage.toString());
			}
			WrapBoolean wrapBoolean = ThisApplication.context().applications()
					.postQuery(false, app, "message/send", innerMessage).getData(WrapBoolean.class);
			// 푸시 메시지 단독 발송용. message 객체를 저장하지 않아 id가 없으므로 업데이트 불필요
			if (StringUtils.isEmpty(message.getId())) {
				return;
			}
			if (BooleanUtils.isTrue(wrapBoolean.getValue())) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Message messageEntityObject = emc.find(message.getId(), Message.class);
					if (null != messageEntityObject) {
						emc.beginTransaction(Message.class);
						messageEntityObject.setConsumed(true);
						emc.commit();
					}
				}
			}
		}
	}

	private BaseMessageExtra extra(Message message) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Message 메시지:{}",   message.toString());
		}
		switch (message.getType()) {
			case MessageConnector.TYPE_TASK_CREATE:
			case MessageConnector.TYPE_TASK_URGE:
			case MessageConnector.TYPE_TASK_EXPIRE:
			case MessageConnector.TYPE_TASK_PRESS:
			case MessageConnector.TYPE_TASKCOMPLETED_CREATE:
			case MessageConnector.TYPE_READ_CREATE:
			case MessageConnector.TYPE_REVIEW_CREATE:
				String work = DingdingConsumeQueue.OuterMessageHelper.getWorkIdFromBody(message.getBody());
				if (StringUtils.isNotEmpty(work)) {
                    return new WorkExtra(work);
				}
				break;
			case MessageConnector.TYPE_MEETING_INVITE:
				return BaseMessageExtra.meetingExtra();
			case MessageConnector.TYPE_CMS_PUBLISH:
			case MessageConnector.TYPE_CMS_PUBLISH_TO_CREATOR:
				String docId = DingdingConsumeQueue.OuterMessageHelper.getCmsDocumentId(message.getBody());
				if (StringUtils.isNotEmpty(docId)) {
                    return new DocExtra(docId);
				}
				break;
			case MessageConnector.TYPE_IM_CREATE:
				String conversationId = getStringValue(message.getBody(), "conversationId");
				if (StringUtils.isNotEmpty(conversationId)) {
					return new ChatExtra(conversationId);
				}
				break;
			case MessageConnector.TYPE_ATTENDANCE_CHECK_IN_ALERT:
			case MessageConnector.TYPE_ATTENDANCE_CHECK_IN_EXCEPTION:
				return BaseMessageExtra.attendanceExtra();
			case MessageConnector.TYPE_CALENDAR_ALARM:
				return BaseMessageExtra.calendarExtra();

		}

		return null;
	}

	private String getStringValue(String messageBody, String key) {
		try {
			JsonObject object = XGsonBuilder.instance().fromJson(messageBody, JsonObject.class);
			if (object.get(key) != null) {
				return object.get(key).getAsString();
			}
		} catch (Exception e) {
			// ignore
		}
		return null;
	}


	public static class BaseMessageExtra extends GsonPropertyObject {
		public enum Type {
			work("work", "문서"),
			doc("doc", "게시글"),
			chat("chat", "메신저"),
			bbs("bbs", "포럼"),
			yunpan("yunpan", "웹하드"),
			attendance("attendance", "근태"),
			calendar("calendar", "일정"),
			meeting("meeting", "회의");

			private String key;
			private String name;

			Type(String key, String name){
				this.key = key;
				this.name = name;
			}

			public String getKey() {
				return key;
			}

			public String getName() {
				return name;
			}
		}

		public static BaseMessageExtra meetingExtra() {
			BaseMessageExtra extra = new BaseMessageExtra();
			extra.setType(Type.meeting.getKey());
			return extra;
		}

		public static BaseMessageExtra bbsExtra() {
			BaseMessageExtra extra = new BaseMessageExtra();
			extra.setType(Type.bbs.getKey());
			return extra;
		}
		public static BaseMessageExtra yunpanExtra() {
			BaseMessageExtra extra = new BaseMessageExtra();
			extra.setType(Type.yunpan.getKey());
			return extra;
		}
		public static BaseMessageExtra attendanceExtra() {
			BaseMessageExtra extra = new BaseMessageExtra();
			extra.setType(Type.attendance.getKey());
			return extra;
		}
		public static BaseMessageExtra calendarExtra() {
			BaseMessageExtra extra = new BaseMessageExtra();
			extra.setType(Type.calendar.getKey());
			return extra;
		}

		private String type; // 메시지 유형


		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

	public static class WorkExtra extends BaseMessageExtra {

		private String work; // work or workCompleted

		WorkExtra(String work) {
			this.work = work;
			this.setType(Type.work.getKey());
		}


		public String getWork() {
			return work;
		}

		public void setWork(String work) {
			this.work = work;
		}
	}

	public static class DocExtra extends BaseMessageExtra {

		private String docId;

		DocExtra(String docId) {
			this.docId = docId;
			this.setType(Type.doc.getKey());
		}

		public String getDocId() {
			return docId;
		}

		public void setDocId(String docId) {
			this.docId = docId;
		}
	}

	public static class ChatExtra extends BaseMessageExtra {

		private String chatId;

		ChatExtra(String chatId) {
			this.chatId = chatId;
			this.setType(Type.chat.getKey());
		}

		public String getChatId() {
			return chatId;
		}

		public void setChatId(String chatId) {
			this.chatId = chatId;
		}
	}
}
