package com.x.jpush.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.jpush.assemble.control.jaxrs.device.DeviceAction;
import com.x.jpush.assemble.control.jaxrs.message.MessageAction;

/**
 * Jaxrs 서비스 등록 클래스, 이 클래스에 등록된 Action이 외부에 서비스 제공
 */
@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		
		//서비스를 제공하는 Action 클래스는 여기에 등록해야 하며, 그렇지 않으면 외부에 서비스를 제공할 수 없음
		this.classes.add(DeviceAction.class);
		this.classes.add(MessageAction.class);

		return this.classes;
	}

}