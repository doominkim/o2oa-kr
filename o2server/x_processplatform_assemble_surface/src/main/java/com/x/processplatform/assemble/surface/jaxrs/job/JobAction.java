package com.x.processplatform.assemble.surface.jaxrs.job;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
import com.x.base.core.project.jaxrs.openapi.ActionGet;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "JobAction", description = "任务接口.")
@Path("job")
@JaxrsDescribe("任务接口.")
public class JobAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobAction.class);
	private static final String OPERATIONID_PREFIX = "JobAction::";

	@Operation(summary = "작업 식별자로 해당 업무 문서와 완료된 업무 문서 조회.", operationId = OPERATIONID_PREFIX
			+ "findWorkWorkCompleted", responses = { @ApiResponse(content = {
					@Content(schema = @Schema(implementation = ActionFindWorkWorkCompleted.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "작업 식별자로 해당 업무 문서와 완료된 업무 문서 조회.", action = ActionFindWorkWorkCompleted.class)
	@GET
	@Path("{job}/find/work/workcompleted")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void findWorkWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("작업 식별자") @PathParam("job") String job) {
		ActionResult<ActionFindWorkWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionFindWorkWorkCompleted().execute(effectivePerson, job);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "문서 번호로 업무 문서 또는 완료된 업무 문서를 찾아 가장 최근에 생성된 하나를 반환.", operationId = OPERATIONID_PREFIX
			+ "latestWorkWorkCompletedWithSerial", responses = {
					@ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGet.Wo.class)) }) })
	@JaxrsMethodDescribe(value = "문서 번호로 업무 문서 또는 완료된 업무 문서를 찾아 가장 최근에 생성된 하나를 반환.", action = ActionLatestWorkWorkCompletedWithSerial.class)
	@GET
	@Path("latest/work/workcompleted/serial/{serial}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void latestWorkWorkCompletedWithSerial(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("번호") @PathParam("serial") String serial) {
		ActionResult<ActionLatestWorkWorkCompletedWithSerial.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionLatestWorkWorkCompletedWithSerial().execute(effectivePerson, serial);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "지정 작업 식별자로 필드 매핑 즉시 실행.", action = V2Projection.class)
	@GET
	@Path("v2/{job}/projection")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void v2Projection(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("식별자") @PathParam("job") String job) {
		ActionResult<V2Projection.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new V2Projection().execute(effectivePerson, job);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "지정 job의 allowVisit 여부 반환.", action = ActionAllowVisitWithPerson.class)
	@GET
	@Path("{job}/allow/visit/person/{person}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void allowVisitWithPerson(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("식별자") @PathParam("job") String job,
			@JaxrsParameterDescribe("식별자") @PathParam("person") String person) {
		ActionResult<ActionAllowVisitWithPerson.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionAllowVisitWithPerson().execute(effectivePerson, job, person);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}