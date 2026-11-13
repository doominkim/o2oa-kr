package com.x.organization.core.express.empower;

import java.util.Arrays;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.organization.Empower;

public class EmpowerFactory {

	public EmpowerFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 앱과 프로세스로 지정 신분의 위임 여부 조회 */
	public List<Empower> listWithIdentityObject(String application, String edition, String process, String work,
			String value) throws Exception {
		return ActionListWithIdentityObject.execute(context, application, edition, process, work, Arrays.asList(value));
	}

	/** 앱과 프로세스로 지정 신분의 위임 여부 조회 */
	public List<Empower> listWithIdentityObject(String application, String edition, String process, String work,
			List<String> values) throws Exception {
		return ActionListWithIdentityObject.execute(context, application, edition, process, work, values);
	}

}
