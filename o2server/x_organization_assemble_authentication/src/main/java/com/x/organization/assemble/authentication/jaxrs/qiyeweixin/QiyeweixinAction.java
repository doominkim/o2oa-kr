package com.x.organization.assemble.authentication.jaxrs.qiyeweixin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("qiyeweixin")
@JaxrsDescribe("기업 위챗 SSO 로그인")
public class QiyeweixinAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(QiyeweixinAction.class);

    @JaxrsMethodDescribe(value = "기업 위챗 SSO 로그인.", action = ActionGetLogin.class)
    @GET
    @Path("code/{code}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getLogin(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @Context HttpServletResponse response, @JaxrsParameterDescribe("코드") @PathParam("code") String code) {
        ActionResult<ActionGetLogin.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionGetLogin().execute(request, response, effectivePerson, code);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "기업 위챗 사용자 상세 정보 조회.", action = ActionLoginAndGetPrivateInfo.class)
    @GET
    @Path("update/person/detail/{code}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void updatePersonInfoFromQywx(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @Context HttpServletResponse response, @JaxrsParameterDescribe("코드") @PathParam("code") String code) {
        ActionResult<ActionLoginAndGetPrivateInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLoginAndGetPrivateInfo().execute(request, response, effectivePerson, code);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }



	@JaxrsMethodDescribe(value = "기업 위챗 jssdk 서명 정보 조회.", action = ActionJssdkSignInfo.class)
	@POST
	@Path("info/sign")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void info(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response, JsonElement jsonElement) {
		ActionResult<ActionJssdkSignInfo.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionJssdkSignInfo().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}