package com.x.organization.assemble.authentication.jaxrs.oauth;

import com.google.gson.JsonElement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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

@Path("oauth")
@JaxrsDescribe("Oauth2 SSO 로그인")
public class OauthAction extends StandardJaxrsAction {

    private static final Logger logger = LoggerFactory.getLogger(OauthAction.class);

    @JaxrsMethodDescribe(value = "GET 방식 oauth 인증 auth 메서드", action = ActionAuth.class)
    @GET
    @Path("auth")
    public void auth(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @Context HttpServletResponse response,
            @JaxrsParameterDescribe("권한 부여 유형, 필수 항목, 값은 code로 고정") @QueryParam("response_type") String response_type,
            @JaxrsParameterDescribe("클라이언트 ID") @QueryParam("client_id") String client_id,
            @JaxrsParameterDescribe("리다이렉트 URI") @QueryParam("redirect_uri") String redirect_uri,
            @JaxrsParameterDescribe("요청 권한 범위") @QueryParam("scope") String scope,
            @JaxrsParameterDescribe("클라이언트의 현재 상태, 임의 값 지정 가능하며 인증 서버가 이 값을 그대로 반환함") @QueryParam("state") String state) {
        ActionResult<ActionAuth.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionAuth().execute(effectivePerson, response_type, client_id, redirect_uri, scope, state);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "관리자가 지정 사용자의 oauth SSO code 생성.", action = ActionAuthCode.class)
    @POST
    @Path("generate/code")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void generateCode(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @Context HttpServletResponse response, JsonElement jsonElement) {
        ActionResult<ActionAuthCode.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionAuthCode().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "POST 방식 oauth 인증 token 메서드", action = ActionToken.class)
    @POST
    @Path("token")
    @Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED })
    public void postToken(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @Context HttpServletResponse response,
            @JaxrsParameterDescribe("클라이언트 ID") @FormParam("client_id") String client_id,
            @JaxrsParameterDescribe("클라이언트 시크릿") @FormParam("client_secret") String client_secret,
            @JaxrsParameterDescribe("코드") @FormParam("code") String code,
            @JaxrsParameterDescribe("권한 부여 유형") @FormParam("grant_type") String grant_type,
            @JaxrsParameterDescribe("response CONTENT_TYPE 설정, 기본값 text/plain; charset=UTF-8") @FormParam("contentType") String contentType) {
        ActionResult<ActionToken.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionToken().execute(effectivePerson, code, grant_type, contentType);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "POST 방식 oauth 인증 token 메서드, jira 대응.", action = ActionToken.class)
    @POST
    @Path("token/jira")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON })
    public void postTokenJira(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @Context HttpServletResponse response, @JaxrsParameterDescribe("코드") @FormParam("code") String code,
            @JaxrsParameterDescribe("권한 부여 유형") @FormParam("grant_type") String grant_type) {
        ActionResult<ActionToken.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionToken().execute(effectivePerson, code, grant_type, MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "GET 방식 oauth 인증 token 메서드", action = ActionToken.class)
    @GET
    @Path("token")
    public void getToken(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @Context HttpServletResponse response,
            @JaxrsParameterDescribe("클라이언트 ID") @QueryParam("client_id") String client_id,
            @JaxrsParameterDescribe("클라이언트 시크릿") @QueryParam("client_secret") String client_secret,
            @JaxrsParameterDescribe("code") @QueryParam("code") String code,
            @JaxrsParameterDescribe("grant_type") @QueryParam("grant_type") String grant_type,
            @JaxrsParameterDescribe("response CONTENT_TYPE 설정, 기본값 text/plain; charset=UTF-8") @QueryParam("contentType") String contentType) {
        ActionResult<ActionToken.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionToken().execute(effectivePerson, code, grant_type, contentType);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "POST 방식 oauth 인증 info 메서드.", action = ActionInfo.class)
    @POST
    @Path("info")
    @Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED })
    public void postInfo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @Context HttpServletResponse response,
            @JaxrsParameterDescribe("access_token") @FormParam("access_token") String access_token,
            @JaxrsParameterDescribe("response CONTENT_TYPE 설정, 기본값 text/plain; charset=UTF-8") @FormParam("contentType") String contentType) {
        ActionResult<ActionInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionInfo().execute(request, effectivePerson, access_token, contentType);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "POST 방식 oauth 인증 info 메서드, jira 대응.", action = ActionInfo.class)
    @POST
    @Path("info/jira")
    @Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED })
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    public void postInfoJira(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @Context HttpServletResponse response,
            @JaxrsParameterDescribe("액세스 토큰") @FormParam("access_token") String access_token) {
        ActionResult<ActionInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionInfo().execute(request, effectivePerson, access_token, MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "GET 방식 oauth 인증 info 메서드.", action = ActionInfo.class)
    @GET
    @Path("info")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    public void getInfo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @Context HttpServletResponse response,
            @JaxrsParameterDescribe("access_token") @QueryParam("access_token") String access_token,
            @JaxrsParameterDescribe("response CONTENT_TYPE 설정, 기본값 text/plain; charset=UTF-8") @QueryParam("contentType") String contentType) {
        ActionResult<ActionInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionInfo().execute(request, effectivePerson, access_token, contentType);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "GET 방식 oauth 인증 info 메서드, jira 대응.", action = ActionInfo.class)
    @GET
    @Path("info/jira")
    @Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED })
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    public void getInfoJira(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @Context HttpServletResponse response,
            @JaxrsParameterDescribe("access_token") @QueryParam("access_token") String access_token) {
        ActionResult<ActionInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionInfo().execute(request, effectivePerson, access_token, MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

}
