package com.x.ai.assemble.control.jaxrs;

import com.x.ai.assemble.control.jaxrs.chat.ChatAction;
import com.x.ai.assemble.control.jaxrs.config.ConfigAction;
import com.x.ai.assemble.control.jaxrs.file.FileAction;
import com.x.ai.assemble.control.jaxrs.index.IndexAction;
import com.x.base.core.project.jaxrs.AbstractActionApplication;
import java.util.Set;
import javax.ws.rs.ApplicationPath;

/**
 * Jaxrs 서비스 등록 클래스, 이 클래스에 등록된 Action이 외부에 서비스 제공
 * @author sword
 */
@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	@Override
	public Set<Class<?>> getClasses() {

		//서비스를 제공하는 Action 클래스는 여기에 등록해야 하며, 그렇지 않으면 외부에 서비스를 제공할 수 없음
		this.classes.add( ConfigAction.class);
		this.classes.add( ChatAction.class);
		this.classes.add( IndexAction.class);
		this.classes.add( FileAction.class);

		return this.classes;
	}

}
