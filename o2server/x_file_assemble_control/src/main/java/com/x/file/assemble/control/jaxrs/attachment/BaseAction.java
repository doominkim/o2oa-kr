package com.x.file.assemble.control.jaxrs.attachment;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.file.core.entity.personal.Attachment;

abstract class BaseAction extends StandardJaxrsAction {

	protected void message_send_attachment_share(Attachment attachment, String person) throws Exception {
		String title = "(" + OrganizationDefinition.name(attachment.getPerson()) + ")님이 공유한 파일: " + attachment.getName()
				+ ".";
		MessageConnector.send(MessageConnector.TYPE_ATTACHMENT_SHARE, title, person,
				XGsonBuilder.convert(attachment, Attachment.class));
	}

	protected void message_send_attachment_shareCancel(Attachment attachment, String person) throws Exception {
		String title = "(" + OrganizationDefinition.name(attachment.getPerson()) + ")님이 " + attachment.getName()
				+ " 파일 공유를 취소했습니다.";
		MessageConnector.send(MessageConnector.TYPE_ATTACHMENT_SHARECANCEL, title, person,
				XGsonBuilder.convert(attachment, Attachment.class));
	}

	protected void message_send_attachment_editor(Attachment attachment, String person) throws Exception {
		String title = "(" + OrganizationDefinition.name(attachment.getPerson()) + ")님이 편집 가능으로 공유한 파일: "
				+ attachment.getName() + ".";
		MessageConnector.send(MessageConnector.TYPE_ATTACHMENT_EDITOR, title, person,
				XGsonBuilder.convert(attachment, Attachment.class));
	}

	protected void message_send_attachment_editorCancel(Attachment attachment, String person) throws Exception {
		String title = "(" + OrganizationDefinition.name(attachment.getPerson()) + ")님이 " + attachment.getName()
				+ " 파일 공유 편집을 취소했습니다.";
		MessageConnector.send(MessageConnector.TYPE_ATTACHMENT_EDITORCANCEL, title, person,
				XGsonBuilder.convert(attachment, Attachment.class));
	}

	protected void message_send_attachment_editorModify(Attachment attachment, String editor, String person)
			throws Exception {
		String title = "(" + OrganizationDefinition.name(editor) + ")님이 " + attachment.getName() + " 파일을 수정했습니다.";
		MessageConnector.send(MessageConnector.TYPE_ATTACHMENT_EDITORMODIFY, title, person,
				XGsonBuilder.convert(attachment, Attachment.class));
	}
}