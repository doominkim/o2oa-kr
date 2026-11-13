package com.x.processplatform.assemble.surface.jaxrs.datarecord;

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

/**
 * @author sword
 */
@Tag(name = "ReadRecordAction", description = "업무 데이터 변경 기록 인터페이스.")
@Path("datarecord")
@JaxrsDescribe("업무 데이터 변경 기록 인터페이스.")
public class DataRecordAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataRecordAction.class);
	private static final String OPERATIONID_PREFIX = "DataRecordAction::";

	@Operation(summary = "작업 식별자로 업무 데이터 변경 기록 조회.", operationId = OPERATIONID_PREFIX + "listWithJob", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithJob.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "작업 식별자로 업무 데이터 변경 기록 조회.", action = ActionListWithJob.class)
	@GET
	@Path("list/job/{job}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithJob(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("업무 문서의 job") @PathParam("job") String job) {
		ActionResult<List<ActionListWithJob.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithJob().execute(effectivePerson, job);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "작업 식별자와 업무 필드로 데이터 변경 기록 조회.", operationId = OPERATIONID_PREFIX + "getWithJobPath", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionGetWithJobPath.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "작업 식별자와 업무 필드로 데이터 변경 기록 조회.", action = ActionGetWithJobPath.class)
	@GET
	@Path("get/job/{job}/path/{path}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithJobPath(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							@JaxrsParameterDescribe("업무 문서의 job") @PathParam("job") String job,
							@JaxrsParameterDescribe("필드 path") @PathParam("path") String path) {
		ActionResult<ActionGetWithJobPath.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithJobPath().execute(effectivePerson, job, path);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
