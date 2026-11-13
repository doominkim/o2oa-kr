package com.x.organization.assemble.personal.jaxrs.exmail;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.x.base.core.project.annotation.DescribeScope;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "ExmailAction", description = "텐센트 기업 메일 인터페이스.")
@Path("exmail")
@JaxrsDescribe("텐센트 기업 메일 인터페이스.")
public class ExmailAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExmailAction.class);
	private static final String OPERATIONID_PREFIX = "ExmailAction::";

	@Operation(summary = "현재 사용자의 exmail 메일 수 조회, 텐센트 기업 메일에 즉시 접근해 조회.", operationId = OPERATIONID_PREFIX + "newCount", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionNewCount.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "현재 사용자의 exmail 메일 수 조회, 텐센트 기업 메일에 즉시 접근해 조회.", action = ActionNewCount.class, scope = DescribeScope.commonly)
	@GET
	@Path("new/count")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void newCount(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionNewCount.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionNewCount().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "현재 사용자의 exmail 메일 수 조회, 콜백으로 PersonAttribute에 기록된 값 읽기.", operationId = OPERATIONID_PREFIX
			+ "newCountPassive", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionNewCountPassive.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "현재 사용자의 exmail 메일 수 조회, 콜백으로 PersonAttribute에 기록된 값 읽기.", action = ActionNewCountPassive.class, scope = DescribeScope.commonly)
	@GET
	@Path("new/count/passive")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void newCountPassive(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionNewCountPassive.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionNewCountPassive().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "현재 사용자의 텐센트 기업 메일 제목 목록, 콜백으로 PersonAttribute에 기록된 값 읽기.", operationId = OPERATIONID_PREFIX
			+ "listTitlePassive", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListTitlePassive.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "현재 사용자의 텐센트 기업 메일 제목 목록, 콜백으로 PersonAttribute에 기록된 값 읽기.", action = ActionListTitlePassive.class, scope = DescribeScope.commonly)
	@GET
	@Path("list/title/passive")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listTitlePassive(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<List<ActionListTitlePassive.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListTitlePassive().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "현재 사용자 메일 SSO 주소 조회.", operationId = OPERATIONID_PREFIX + "sso", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionSso.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "현재 사용자 메일 SSO 주소 조회.", action = ActionSso.class, scope = DescribeScope.commonly)
	@GET
	@Path("sso")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void sso(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionSso.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSso().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "텐센트 기업 메일 콜백 GET 메서드 수신.", operationId = OPERATIONID_PREFIX + "get", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGet.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "텐센트 기업 메일 콜백 GET 메서드 수신.", action = ActionGet.class, scope = DescribeScope.system)
	@GET
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("msg_signature") @QueryParam("msg_signature") String msg_signature,
			@JaxrsParameterDescribe("timestamp") @QueryParam("timestamp") String timestamp,
			@JaxrsParameterDescribe("nonce") @QueryParam("nonce") String nonce,
			@JaxrsParameterDescribe("echostr") @QueryParam("echostr") String echostr) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		try {
			result = new ActionGet().execute(effectivePerson, msg_signature, timestamp, nonce, echostr);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "텐센트 기업 메일 콜백 POST 메서드 수신.", operationId = OPERATIONID_PREFIX + "callback", responses = {
			@ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionCallback.Wo.class)) }) }, requestBody = @RequestBody(content = {
							@Content(schema = @Schema(implementation = String.class)) }))
	@JaxrsMethodDescribe(value = "텐센트 기업 메일 콜백 POST 메서드 수신.", action = ActionCallback.class, scope = DescribeScope.system)
	@POST
	public void callback(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("msg_signature") @QueryParam("msg_signature") String msg_signature,
			@JaxrsParameterDescribe("timestamp") @QueryParam("timestamp") String timestamp,
			@JaxrsParameterDescribe("nonce") @QueryParam("nonce") String nonce, String body) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionCallback.Wo> result = new ActionResult<>();
		try {
			result = new ActionCallback().execute(effectivePerson, msg_signature, timestamp, nonce, body);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}