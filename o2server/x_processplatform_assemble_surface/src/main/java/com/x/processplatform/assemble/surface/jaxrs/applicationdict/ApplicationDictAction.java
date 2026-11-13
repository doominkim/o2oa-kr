package com.x.processplatform.assemble.surface.jaxrs.applicationdict;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "ApplicationDictAction", description = "데이터 사전 인터페이스.")
@JaxrsDescribe("데이터 사전 인터페이스.")
@Path("applicationdict")
public class ApplicationDictAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationDictAction.class);
	private static final String OPERATIONID_PREFIX = "ApplicationDictAction::";

	@Operation(summary = "단일 데이터 사전 및 데이터 사전 데이터 조회.", operationId = OPERATIONID_PREFIX + "get", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGet.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "단일 데이터 사전 및 데이터 사전 데이터 조회.", action = ActionGet.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		LOGGER.debug("run get applicationDictFlag:{}, applicationFlag:{}.", applicationDictFlag, applicationFlag);
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson, applicationDictFlag, applicationFlag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정한 앱의 데이터 사전 목록 조회.", operationId = OPERATIONID_PREFIX + "listWithApplication", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithApplication.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "지정한 앱의 데이터 사전 목록 조회.", action = ActionListWithApplication.class)
	@GET
	@Path("list/application/{applicationFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithApplication(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<List<ActionListWithApplication.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithApplication().execute(effectivePerson, applicationFlag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정한 앱의 데이터 사전 데이터 조회.", operationId = OPERATIONID_PREFIX + "getData", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "지정한 앱의 데이터 사전 데이터 조회.", action = ActionGetData.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getData(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetData().execute(effectivePerson, applicationDictFlag, applicationFlag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 1단계 경로로 데이터 사전 데이터 조회.", operationId = OPERATIONID_PREFIX + "getDataPath0", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 1단계 경로로 데이터 사전 데이터 조회.", action = ActionGetDataPath0.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getDataPath0(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath0().execute(effectivePerson, applicationDictFlag, applicationFlag, path0);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 2단계 경로로 데이터 사전 데이터 조회.", operationId = OPERATIONID_PREFIX + "getDataPath1", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 2단계 경로로 데이터 사전 데이터 조회.", action = ActionGetDataPath1.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getDataPath1(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath1().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 3단계 경로로 데이터 사전 데이터 조회.", operationId = OPERATIONID_PREFIX + "getDataPath2", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 3단계 경로로 데이터 사전 데이터 조회.", action = ActionGetDataPath2.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getDataPath2(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath2().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 4단계 경로로 데이터 사전 데이터 조회.", operationId = OPERATIONID_PREFIX + "getDataPath3", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 4단계 경로로 데이터 사전 데이터 조회.", action = ActionGetDataPath3.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getDataPath3(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath3().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 5단계 경로로 데이터 사전 데이터 조회.", operationId = OPERATIONID_PREFIX + "getDataPath4", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 5단계 경로로 데이터 사전 데이터 조회.", action = ActionGetDataPath4.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getDataPath4(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath4().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 6단계 경로로 데이터 사전 데이터 조회.", operationId = OPERATIONID_PREFIX + "getDataPath5", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 6단계 경로로 데이터 사전 데이터 조회.", action = ActionGetDataPath5.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getDataPath5(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5단계 경로") @PathParam("path5") String path5) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath5().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, path5);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 7단계 경로로 데이터 사전 데이터 조회.", operationId = OPERATIONID_PREFIX + "getDataPath6", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 7단계 경로로 데이터 사전 데이터 조회.", action = ActionGetDataPath6.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getDataPath6(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5단계 경로") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6단계 경로") @PathParam("path6") String path6) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath6().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, path5, path6);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 8단계 경로로 데이터 사전 데이터 조회.", operationId = OPERATIONID_PREFIX + "getDataPath7", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 8단계 경로로 데이터 사전 데이터 조회.", action = ActionGetDataPath7.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getDataPath7(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5단계 경로") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6단계 경로") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7단계 경로") @PathParam("path7") String path7) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath7().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, path5, path6, path7);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 1단계 경로로 데이터 사전 데이터 수정.", operationId = OPERATIONID_PREFIX + "updateDataPath0", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUpdateDataPath0.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 1단계 경로로 데이터 사전 데이터 수정.", action = ActionUpdateDataPath0.class)
	@PUT
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath0(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath0().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 1단계 경로로 데이터 사전 데이터 수정(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateDataPath0MockPutToPost", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionUpdateDataPath0.Wo.class)) }) }, deprecated = true)
	@JaxrsMethodDescribe(value = "지정 앱의 1단계 경로로 데이터 사전 데이터 수정(mock put to post).", action = ActionUpdateDataPath0.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/data/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath0MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath0().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 2단계 경로로 데이터 사전 데이터 수정.", operationId = OPERATIONID_PREFIX + "updateDataPath1", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUpdateDataPath1.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 2단계 경로로 데이터 사전 데이터 수정.", action = ActionUpdateDataPath1.class)
	@PUT
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath1(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath1().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 2단계 경로로 데이터 사전 데이터 수정(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateDataPath1MockPutToPost", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionUpdateDataPath1.Wo.class)) }) }, deprecated = true)
	@JaxrsMethodDescribe(value = "지정 앱의 2단계 경로로 데이터 사전 데이터 수정(mock put to post).", action = ActionUpdateDataPath1.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/data/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath1MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath1().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 3단계 경로로 데이터 사전 데이터 수정.", operationId = OPERATIONID_PREFIX + "updateDataPath2", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUpdateDataPath2.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 3단계 경로로 데이터 사전 데이터 수정.", action = ActionUpdateDataPath2.class)
	@PUT
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath2(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath2().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 3단계 경로로 데이터 사전 데이터 수정(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateDataPath2MockPutToPost", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionUpdateDataPath2.Wo.class)) }) }, deprecated = true)
	@JaxrsMethodDescribe(value = "지정 앱의 3단계 경로로 데이터 사전 데이터 수정(mock put to post).", action = ActionUpdateDataPath2.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/data/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath2MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath2().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 4단계 경로로 데이터 사전 데이터 수정.", operationId = OPERATIONID_PREFIX + "updateDataPath3", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUpdateDataPath3.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 4단계 경로로 데이터 사전 데이터 수정.", action = ActionUpdateDataPath3.class)
	@PUT
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath3(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath3().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 4단계 경로로 데이터 사전 데이터 수정(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateDataPath3MockPutToPost", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionUpdateDataPath3.Wo.class)) }) }, deprecated = true)
	@JaxrsMethodDescribe(value = "지정 앱의 4단계 경로로 데이터 사전 데이터 수정(mock put to post).", action = ActionUpdateDataPath3.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/data/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath3MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath3().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 5단계 경로로 데이터 사전 데이터 수정.", operationId = OPERATIONID_PREFIX + "updateDataPath4", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUpdateDataPath4.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 5단계 경로로 데이터 사전 데이터 수정.", action = ActionUpdateDataPath4.class)
	@PUT
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath4(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath4().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 5단계 경로로 데이터 사전 데이터 수정(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateDataPath4MockPutToPost", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionUpdateDataPath4.Wo.class)) }) }, deprecated = true)
	@JaxrsMethodDescribe(value = "지정 앱의 5단계 경로로 데이터 사전 데이터 수정(mock put to post).", action = ActionUpdateDataPath4.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/data/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath4MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath4().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 6단계 경로로 데이터 사전 데이터 수정.", operationId = OPERATIONID_PREFIX + "updateDataPath5", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUpdateDataPath5.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 6단계 경로로 데이터 사전 데이터 수정.", action = ActionUpdateDataPath5.class)
	@PUT
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath5(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5단계 경로") @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath5().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, path5, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 6단계 경로로 데이터 사전 데이터 수정(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateDataPath5MockPutToPost", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionUpdateDataPath5.Wo.class)) }) }, deprecated = true)
	@JaxrsMethodDescribe(value = "지정 앱의 6단계 경로로 데이터 사전 데이터 수정(mock put to post).", action = ActionUpdateDataPath5.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath5MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5단계 경로") @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath5().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, path5, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 7단계 경로로 데이터 사전 데이터 수정.", operationId = OPERATIONID_PREFIX + "updateDataPath6", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUpdateDataPath6.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 7단계 경로로 데이터 사전 데이터 수정.", action = ActionUpdateDataPath6.class)
	@PUT
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath6(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5단계 경로") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6단계 경로") @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath6().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, path5, path6, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 7단계 경로로 데이터 사전 데이터 수정(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateDataPath6MockPutToPost", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionUpdateDataPath6.Wo.class)) }) }, deprecated = true)
	@JaxrsMethodDescribe(value = "지정 앱의 7단계 경로로 데이터 사전 데이터 수정(mock put to post).", action = ActionUpdateDataPath6.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath6MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5단계 경로") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6단계 경로") @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath6().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, path5, path6, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 8단계 경로로 데이터 사전 데이터 수정.", operationId = OPERATIONID_PREFIX + "updateDataPath7", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionUpdateDataPath7.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 8단계 경로로 데이터 사전 데이터 수정.", action = ActionUpdateDataPath7.class)
	@PUT
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath7(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5단계 경로") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6단계 경로") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7단계 경로") @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath7().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, path5, path6, path7, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 8단계 경로로 데이터 사전 데이터 수정(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateDataPath7MockPutToPost", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionUpdateDataPath7.Wo.class)) }) }, deprecated = true)
	@JaxrsMethodDescribe(value = "지정 앱의 8단계 경로로 데이터 사전 데이터 수정(mock put to post).", action = ActionUpdateDataPath7.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath7MockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5단계 경로") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6단계 경로") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7단계 경로") @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath7().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, path5, path6, path7, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 1단계 경로로 데이터 사전 데이터 추가.", operationId = OPERATIONID_PREFIX + "createDataPath0", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionCreateDataPath0.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 1단계 경로로 데이터 사전 데이터 추가.", action = ActionCreateDataPath0.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createDataPath0(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<ActionCreateDataPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath0().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 2단계 경로로 데이터 사전 데이터 추가.", operationId = OPERATIONID_PREFIX + "createDataPath1", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionCreateDataPath1.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 2단계 경로로 데이터 사전 데이터 추가.", action = ActionCreateDataPath1.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createDataPath1(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<ActionCreateDataPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath1().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 3단계 경로로 데이터 사전 데이터 추가.", operationId = OPERATIONID_PREFIX + "createDataPath2", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionCreateDataPath2.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 3단계 경로로 데이터 사전 데이터 추가.", action = ActionCreateDataPath2.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createDataPath2(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2, JsonElement jsonElement) {
		ActionResult<ActionCreateDataPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath2().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 4단계 경로로 데이터 사전 데이터 추가.", operationId = OPERATIONID_PREFIX + "createDataPath3", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionCreateDataPath3.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 4단계 경로로 데이터 사전 데이터 추가.", action = ActionCreateDataPath3.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createDataPath3(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<ActionCreateDataPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath3().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 5단계 경로로 데이터 사전 데이터 추가.", operationId = OPERATIONID_PREFIX + "createDataPath4", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionCreateDataPath4.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "사전 ID와 경로로 Application의 새 부분 데이터 추가.", action = ActionCreateDataPath4.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createDataPath4(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<ActionCreateDataPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath4().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 6단계 경로로 데이터 사전 데이터 추가.", operationId = OPERATIONID_PREFIX + "createDataPath5", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionCreateDataPath5.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 6단계 경로로 데이터 사전 데이터 추가.", action = ActionCreateDataPath5.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createDataPath5(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5단계 경로") @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<ActionCreateDataPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath5().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, path5, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 7단계 경로로 데이터 사전 데이터 추가.", operationId = OPERATIONID_PREFIX + "createDataPath6", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionCreateDataPath6.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 7단계 경로로 데이터 사전 데이터 추가.", action = ActionCreateDataPath6.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createDataPath6(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5단계 경로") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6단계 경로") @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<ActionCreateDataPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath6().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, path5, path6, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 8단계 경로로 데이터 사전 데이터 추가.", operationId = OPERATIONID_PREFIX + "createDataPath7", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionCreateDataPath7.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 8단계 경로로 데이터 사전 데이터 추가.", action = ActionCreateDataPath7.class)
	@POST
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createDataPath7(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5단계 경로") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6단계 경로") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7단계 경로") @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<ActionCreateDataPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath7().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, path5, path6, path7, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 1단계 경로로 데이터 사전 데이터 삭제.", operationId = OPERATIONID_PREFIX + "deleteDataPath0", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDeleteDataPath0.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 1단계 경로로 데이터 사전 데이터 삭제.", action = ActionDeleteDataPath0.class)
	@DELETE
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath0(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0) {
		ActionResult<ActionDeleteDataPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath0().execute(effectivePerson, applicationDictFlag, applicationFlag, path0);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 1단계 경로로 데이터 사전 데이터 삭제(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteDataPath0MockDeleteToGet", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionDeleteDataPath0.Wo.class)) }) }, deprecated = true)
	@JaxrsMethodDescribe(value = "지정 앱의 1단계 경로로 데이터 사전 데이터 삭제(mock delete to get).", action = ActionDeleteDataPath0.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/data/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath0MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0) {
		ActionResult<ActionDeleteDataPath0.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath0().execute(effectivePerson, applicationDictFlag, applicationFlag, path0);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 2단계 경로로 데이터 사전 데이터 삭제.", operationId = OPERATIONID_PREFIX + "deleteDataPath1", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDeleteDataPath1.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 1단계 경로로 데이터 사전 데이터 삭제.", action = ActionDeleteDataPath1.class)
	@DELETE
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath1(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1) {
		ActionResult<ActionDeleteDataPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath1().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 2단계 경로로 데이터 사전 데이터 삭제(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteDataPath1MockDeleteToGet", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionDeleteDataPath1.Wo.class)) }) }, deprecated = true)
	@JaxrsMethodDescribe(value = "지정 앱의 2단계 경로로 데이터 사전 데이터 삭제(mock delete to get).", action = ActionDeleteDataPath1.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/data/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath1MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1) {
		ActionResult<ActionDeleteDataPath1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath1().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 3단계 경로로 데이터 사전 데이터 삭제.", operationId = OPERATIONID_PREFIX + "deleteDataPath2", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDeleteDataPath2.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 3단계 경로로 데이터 사전 데이터 삭제.", action = ActionDeleteDataPath2.class)
	@DELETE
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath2(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2) {
		ActionResult<ActionDeleteDataPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath2().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 3단계 경로로 데이터 사전 데이터 삭제(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteDataPath2MockDeleteToGet", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionDeleteDataPath2.Wo.class)) }) }, deprecated = true)
	@JaxrsMethodDescribe(value = "지정 앱의 3단계 경로로 데이터 사전 데이터 삭제(mock delete to get).", action = ActionDeleteDataPath2.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/data/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath2MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2) {
		ActionResult<ActionDeleteDataPath2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath2().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 4단계 경로로 데이터 사전 데이터 삭제.", operationId = OPERATIONID_PREFIX + "deleteDataPath3", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDeleteDataPath3.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 4단계 경로로 데이터 사전 데이터 삭제.", action = ActionDeleteDataPath3.class)
	@DELETE
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath3(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3) {
		ActionResult<ActionDeleteDataPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath3().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 4단계 경로로 데이터 사전 데이터 삭제(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteDataPath3MockDeleteToGet", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionDeleteDataPath3.Wo.class)) }) }, deprecated = true)
	@JaxrsMethodDescribe(value = "지정 앱의 4단계 경로로 데이터 사전 데이터 삭제(mock delete to get).", action = ActionDeleteDataPath3.class)
	@DELETE
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/data/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath3MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3) {
		ActionResult<ActionDeleteDataPath3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath3().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 5단계 경로로 데이터 사전 데이터 삭제.", operationId = OPERATIONID_PREFIX + "deleteDataPath4", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDeleteDataPath4.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 5단계 경로로 데이터 사전 데이터 삭제.", action = ActionDeleteDataPath4.class)
	@DELETE
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath4(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4) {
		ActionResult<ActionDeleteDataPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath4().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 5단계 경로로 데이터 사전 데이터 삭제(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteDataPath4MockDeleteToGet", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionDeleteDataPath4.Wo.class)) }) }, deprecated = true)
	@JaxrsMethodDescribe(value = "지정 앱의 5단계 경로로 데이터 사전 데이터 삭제(mock delete to get).", action = ActionDeleteDataPath4.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/data/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath4MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4) {
		ActionResult<ActionDeleteDataPath4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath4().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 6단계 경로로 데이터 사전 데이터 삭제.", operationId = OPERATIONID_PREFIX + "deleteDataPath5", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDeleteDataPath5.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 6단계 경로로 데이터 사전 데이터 삭제.", action = ActionDeleteDataPath5.class)
	@DELETE
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath5(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5단계 경로") @PathParam("path5") String path5) {
		ActionResult<ActionDeleteDataPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath5().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, path5);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 6단계 경로로 데이터 사전 데이터 삭제(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteDataPath5MockDeleteToGet", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionDeleteDataPath5.Wo.class)) }) }, deprecated = true)
	@JaxrsMethodDescribe(value = "지정 앱의 6단계 경로로 데이터 사전 데이터 삭제(mock delete to get).", action = ActionDeleteDataPath5.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath5MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5단계 경로") @PathParam("path5") String path5) {
		ActionResult<ActionDeleteDataPath5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath5().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, path5);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 7단계 경로로 데이터 사전 데이터 삭제.", operationId = OPERATIONID_PREFIX + "deleteDataPath6", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDeleteDataPath6.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 7단계 경로로 데이터 사전 데이터 삭제.", action = ActionDeleteDataPath6.class)
	@DELETE
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath6(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5단계 경로") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6단계 경로") @PathParam("path6") String path6) {
		ActionResult<ActionDeleteDataPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath6().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, path5, path6);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 7단계 경로로 데이터 사전 데이터 삭제(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteDataPath6MockDeleteToGet", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionDeleteDataPath6.Wo.class)) }) }, deprecated = true)
	@JaxrsMethodDescribe(value = "지정 앱의 7단계 경로로 데이터 사전 데이터 삭제(mock delete to get).", action = ActionDeleteDataPath6.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath6MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5단계 경로") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6단계 경로") @PathParam("path6") String path6) {
		ActionResult<ActionDeleteDataPath6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath6().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, path5, path6);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 8단계 경로로 데이터 사전 데이터 삭제.", operationId = OPERATIONID_PREFIX + "deleteDataPath7", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDeleteDataPath7.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정 앱의 8단계 경로로 데이터 사전 데이터 삭제.", action = ActionDeleteDataPath7.class)
	@DELETE
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath7(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5단계 경로") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6단계 경로") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7단계 경로") @PathParam("path7") String path7) {
		ActionResult<ActionDeleteDataPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath7().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, path5, path6, path7);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정 앱의 8단계 경로로 데이터 사전 데이터 삭제(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteDataPath7MockDeleteToGet", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionDeleteDataPath7.Wo.class)) }) }, deprecated = true)
	@JaxrsMethodDescribe(value = "지정 앱의 8단계 경로로 데이터 사전 데이터 삭제(mock delete to get).", action = ActionDeleteDataPath7.class)
	@GET
	@Path("{applicationDictFlag}/application/{applicationFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath7MockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("applicationDictFlag") String applicationDictFlag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
			@JaxrsParameterDescribe("0단계 경로") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1단계 경로") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2단계 경로") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3단계 경로") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4단계 경로") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5단계 경로") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6단계 경로") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7단계 경로") @PathParam("path7") String path7) {
		ActionResult<ActionDeleteDataPath7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath7().execute(effectivePerson, applicationDictFlag, applicationFlag, path0,
					path1, path2, path3, path4, path5, path6, path7);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}