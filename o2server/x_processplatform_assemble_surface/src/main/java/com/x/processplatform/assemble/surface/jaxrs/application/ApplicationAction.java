package com.x.processplatform.assemble.surface.jaxrs.application;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AnonymousAction", description = "앱 인터페이스.")
@JaxrsDescribe("앱 인터페이스.")
@Path("application")
public class ApplicationAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationAction.class);
	private static final String OPERATIONID_PREFIX = "ApplicationAction::";

	@Operation(summary = "지정 앱 정보와 작업 권한 조회.", operationId = OPERATIONID_PREFIX + "get", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGet.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱 정보와 작업 권한 조회.", action = ActionGet.class)
	@GET
	@Path("{flag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("flag") String flag) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson, flag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "볼 수 있는 앱 조회, 앱에 시작 가능한 프로세스가 있는지 함께 판단.", operationId = OPERATIONID_PREFIX + "listWithPerson", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithPerson.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "볼 수 있는 앱 조회, 앱에 시작 가능한 프로세스가 있는지 함께 판단.", action = ActionListWithPerson.class)
	@GET
	@Path("list")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithPerson(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<List<ActionListWithPerson.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPerson().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "지정 범위 내 볼 수 있는 앱 조회, 지정하지 않으면 전체, 앱에 시작 가능한 프로세스가 있는지 함께 판단.", action = ActionListRangeWithPerson.class)
	@POST
	@Path("list/range")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listRangeWithPerson(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<List<ActionListRangeWithPerson.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListRangeWithPerson().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "볼 수 있는 앱 조회, 앱에 시작 가능한 프로세스가 있는지 판단하고 프로세스 이름 매칭 수행.", operationId = OPERATIONID_PREFIX
			+ "listWithPersonLike", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithPersonLike.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "볼 수 있는 앱 조회, 앱에 시작 가능한 프로세스가 있는지 판단하고 프로세스 이름 매칭 수행.", action = ActionListWithPersonLike.class)
	@GET
	@Path("list/key/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithPersonLike(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("키워드") @PathParam("key") String key) {
		ActionResult<List<ActionListWithPersonLike.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPersonLike().execute(effectivePerson, key);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "현재 사용자가 볼 수 있는 모든 Application을 조회하고 시작 가능한 Process를 연결.", operationId = OPERATIONID_PREFIX
			+ "listWithPersonComplex", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithPersonComplex.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "현재 사용자가 볼 수 있는 모든 Application을 조회하고 시작 가능한 Process를 연결.", action = ActionListWithPersonComplex.class)
	@GET
	@Path("list/complex")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithPersonComplex(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<List<ActionListWithPersonComplex.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPersonComplex().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "현재 사용자가 볼 수 있는 모든 Application을 조회하고 단말에서 시작 가능한 Process를 연결.", operationId = OPERATIONID_PREFIX
			+ "listWithPersonAndTerminal", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithPersonAndTerminal.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "현재 사용자가 볼 수 있는 모든 Application을 조회하고 단말에서 시작 가능한 Process를 연결.", action = ActionListWithPersonAndTerminal.class)
	@GET
	@Path("list/terminal/{terminal}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithPersonAndTerminal(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("프로세스 시작 단말: client(PC에서 시작 가능), mobile(모바일에서 시작 가능)") @PathParam("terminal") String terminal) {
		ActionResult<List<ActionListWithPersonAndTerminal.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPersonAndTerminal().execute(effectivePerson, terminal);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "전달된 사용자 기준으로 볼 수 있는 Application을 조회하고 시작 가능한 Process를 연결.", operationId = OPERATIONID_PREFIX
			+ "manageListWithPersonComplex", responses = { @ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionManageListWithPersonComplex.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "전달된 사용자 기준으로 볼 수 있는 Application을 조회하고 시작 가능한 Process를 연결.", action = ActionManageListWithPersonComplex.class)
	@GET
	@Path("list/complex/manage/{person}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageListWithPersonComplex(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("사용자") @PathParam("person") String person) {
		ActionResult<List<ActionManageListWithPersonComplex.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageListWithPersonComplex().execute(effectivePerson, person);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 Application의 Icon만 조회, 권한 제한 없음.", operationId = OPERATIONID_PREFIX + "getIcon", responses = {
			@ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionManageListWithPersonComplex.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 Application의 Icon만 조회, 권한 제한 없음.", action = ActionGetIcon.class)
	@GET
	@Path("{flag}/icon")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getIcon(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("flag") String flag) {
		ActionResult<ActionGetIcon.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetIcon().execute(effectivePerson, flag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "현재 사용자가 지정 앱의 관리자인지 확인.", operationId = OPERATIONID_PREFIX + "isManager", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionIsManager.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "현재 사용자가 지정 앱의 관리자인지 확인.", action = ActionIsManager.class)
	@GET
	@Path("{flag}/is/manager")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void isManager(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("flag") String flag) {
		ActionResult<ActionIsManager.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionIsManager().execute(effectivePerson, flag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "앱 식별자로 프로세스 인스턴스 데이터 삭제.", operationId = OPERATIONID_PREFIX + "deleteWorkOrWorkCompleted", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDeleteWorkOrWorkCompleted.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "앱 식별자로 프로세스 인스턴스 데이터 삭제", action = ActionDeleteWorkOrWorkCompleted.class)
	@DELETE
	@Path("{flag}/{onlyRemoveNotCompleted}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
										  @JaxrsParameterDescribe("앱 식별자") @PathParam("flag") String flag,
										  @JaxrsParameterDescribe("진행 중인 Work만 삭제") @PathParam("onlyRemoveNotCompleted") boolean onlyRemoveNotCompleted) {
		ActionResult<ActionDeleteWorkOrWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWorkOrWorkCompleted().execute(effectivePerson, flag, onlyRemoveNotCompleted);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
