package com.x.portal.assemble.surface.jaxrs.dict;

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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Tag(name = "DictAction", description = "数据字典接口.")
@JaxrsDescribe("数据字典接口.")
@Path("dict")
public class DictAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(DictAction.class);
	private static final String OPERATIONID_PREFIX = "DictAction::";

	@Operation(summary = "단일 데이터 사전 및 데이터 사전 데이터 조회.", operationId = OPERATIONID_PREFIX + "get", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGet.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "단일 데이터 사전 및 데이터 사전 데이터 조회.", action = ActionGet.class)
	@GET
	@Path("{dictFlag}/portal/{portalFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("dictFlag") String dictFlag,
			@JaxrsParameterDescribe("포털 앱 식별자") @PathParam("portalFlag") String portalFlag) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		LOGGER.debug("run get dictFlag:{}, portalFlag:{}.", dictFlag, portalFlag);
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson, dictFlag, portalFlag);
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
	@Path("list/portal/{portalFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithApplication(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("포털 앱 식별자") @PathParam("portalFlag") String portalFlag) {
		ActionResult<List<ActionListWithApplication.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithApplication().execute(effectivePerson, portalFlag);
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
	@Path("{dictFlag}/portal/{portalFlag}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getData(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("dictFlag") String dictFlag,
			@JaxrsParameterDescribe("포털 앱 식별자") @PathParam("portalFlag") String portalFlag) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetData().execute(effectivePerson, dictFlag, portalFlag);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정한 앱에서 경로로 데이터 사전 데이터 조회.", operationId = OPERATIONID_PREFIX + "getDataPath", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = JsonElement.class)) }) })
	@JaxrsMethodDescribe(value = "지정한 앱에서 경로로 데이터 사전 데이터 조회.", action = ActionGetDataPath.class)
	@GET
	@Path("{dictFlag}/portal/{portalFlag}/{path}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getDataPath(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("dictFlag") String dictFlag,
			@JaxrsParameterDescribe("포털 앱 식별자") @PathParam("portalFlag") String portalFlag,
			@JaxrsParameterDescribe("경로(다단계 경로는 .으로 구분, 예: person.name)") @PathParam("path") String path) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath().execute(effectivePerson, dictFlag, portalFlag, path);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정한 앱에서 경로로 데이터 사전 데이터 수정.", operationId = OPERATIONID_PREFIX + "updateDataPath", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUpdateDataPath.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정한 앱에서 경로로 데이터 사전 데이터 수정.", action = ActionUpdateDataPath.class)
	@PUT
	@Path("{dictFlag}/portal/{portalFlag}/{path}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPath(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("dictFlag") String dictFlag,
			@JaxrsParameterDescribe("포털 앱 식별자") @PathParam("portalFlag") String portalFlag,
			@JaxrsParameterDescribe("경로(다단계 경로는 .으로 구분, 예: person.name)") @PathParam("path") String path,
								JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath().execute(effectivePerson, dictFlag, portalFlag, path,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정한 앱에서 경로로 데이터 사전 데이터 수정(mock put to post).", operationId = OPERATIONID_PREFIX
			+ "updateDataPathMockPutToPost", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionUpdateDataPath.Wo.class)) }) }, deprecated = true)
	@JaxrsMethodDescribe(value = "지정한 앱에서 경로로 데이터 사전 데이터 수정(mock put to post).", action = ActionUpdateDataPath.class)
	@POST
	@Path("{dictFlag}/portal/{portalFlag}/{path}/data/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDataPathMockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("dictFlag") String dictFlag,
			@JaxrsParameterDescribe("포털 앱 식별자") @PathParam("portalFlag") String portalFlag,
			@JaxrsParameterDescribe("경로(다단계 경로는 .으로 구분, 예: person.name)") @PathParam("path") String path, JsonElement jsonElement) {
		ActionResult<ActionUpdateDataPath.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateDataPath().execute(effectivePerson, dictFlag, portalFlag, path,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정한 앱에서 경로로 데이터 사전 데이터 추가.", operationId = OPERATIONID_PREFIX + "createDataPath", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionCreateDataPath.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정한 앱에서 경로로 데이터 사전 데이터 추가.", action = ActionCreateDataPath.class)
	@POST
	@Path("{dictFlag}/portal/{portalFlag}/{path}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createDataPath(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("dictFlag") String dictFlag,
			@JaxrsParameterDescribe("포털 앱 식별자") @PathParam("portalFlag") String portalFlag,
			@JaxrsParameterDescribe("경로(다단계 경로는 .으로 구분, 예: person.name)") @PathParam("path") String path, JsonElement jsonElement) {
		ActionResult<ActionCreateDataPath.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateDataPath().execute(effectivePerson, dictFlag, portalFlag, path,
					jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정한 앱에서 경로로 데이터 사전 데이터 삭제.", operationId = OPERATIONID_PREFIX + "deleteDataPath", responses = {
			@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDeleteDataPath.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "지정한 앱에서 경로로 데이터 사전 데이터 삭제.", action = ActionDeleteDataPath.class)
	@DELETE
	@Path("{dictFlag}/portal/{portalFlag}/{path}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPath(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("dictFlag") String dictFlag,
			@JaxrsParameterDescribe("포털 앱 식별자") @PathParam("portalFlag") String portalFlag,
			@JaxrsParameterDescribe("경로(다단계 경로는 .으로 구분, 예: person.name)") @PathParam("path") String path) {
		ActionResult<ActionDeleteDataPath.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath().execute(effectivePerson, dictFlag, portalFlag, path);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "지정한 앱에서 경로로 데이터 사전 데이터 삭제(mock delete to get).", operationId = OPERATIONID_PREFIX
			+ "deleteDataPathMockDeleteToGet", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionDeleteDataPath.Wo.class)) }) }, deprecated = true)
	@JaxrsMethodDescribe(value = "지정한 앱에서 경로로 데이터 사전 데이터 삭제(mock delete to get).", action = ActionDeleteDataPath.class)
	@GET
	@Path("{dictFlag}/portal/{portalFlag}/{path}/data/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteDataPathMockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("데이터 사전 식별자") @PathParam("dictFlag") String dictFlag,
			@JaxrsParameterDescribe("포털 앱 식별자") @PathParam("portalFlag") String portalFlag,
			@JaxrsParameterDescribe("경로(다단계 경로는 .으로 구분, 예: person.name)") @PathParam("path") String path) {
		ActionResult<ActionDeleteDataPath.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteDataPath().execute(effectivePerson, dictFlag, portalFlag, path);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
