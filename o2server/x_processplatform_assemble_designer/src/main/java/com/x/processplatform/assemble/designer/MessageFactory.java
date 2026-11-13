package com.x.processplatform.assemble.designer;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

public class MessageFactory {
	public static void application_create(Application application) throws Exception {
		String title = "프로세스 플랫폼 앱 생성:" + application.getName();
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_APPLICATION_CREATE, title, "", application);
	}

	public static void application_update(Application application) throws Exception {
		String title = "프로세스 플랫폼 앱 수정:" + application.getName();
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_APPLICATION_UPDATE, title, "", application);
	}

	public static void application_delete(Application application) throws Exception {
		String title = "프로세스 플랫폼 앱 삭제:" + application.getName();
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_APPLICATION_DELETE, title, "", application);
	}

	public static void process_create(Process process) throws Exception {
		String title = "프로세스 플랫폼 프로세스 생성:" + process.getName();
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_PROCESS_CREATE, title, "", process);
	}

	public static void process_update(Process process) throws Exception {
		String title = "프로세스 플랫폼 프로세스 수정:" + process.getName();
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_PROCESS_UPDATE, title, "", process);
	}

	public static void process_delete(Process process) throws Exception {
		String title = "프로세스 플랫폼 프로세스 삭제:" + process.getName();
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_PROCESS_DELETE, title, "", process);
	}
}
