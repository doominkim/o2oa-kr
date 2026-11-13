package com.x.general.assemble.control.jaxrs.upgrade;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("upgrade")
@JaxrsDescribe("업그레이드")
public class UpgradeAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(UpgradeAction.class);

	@JaxrsMethodDescribe(value = "프로세스 엔진 라우트에 defaultSelected 필드를 추가하고 defaultSelected 값을 sole 값으로 직접 설정.", action = Action2021090901.class)
	@GET
	@Path("2021090901")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void action2021090901(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<Action2021090901.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new Action2021090901().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "프로세스 엔진 라우트에서 route.getProperties().getSoleDirect() 값을 모두 false로 강제 설정.", action = Action2021090902.class)
	@GET
	@Path("2021090902")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void action2021090902(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<Action2021090902.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new Action2021090902().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}