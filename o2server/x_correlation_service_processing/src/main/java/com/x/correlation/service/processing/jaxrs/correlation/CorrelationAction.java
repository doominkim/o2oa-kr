package com.x.correlation.service.processing.jaxrs.correlation;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
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

@Path("correlation")
@JaxrsDescribe("관련 콘텐츠.")
public class CorrelationAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(CorrelationAction.class);

	@JaxrsMethodDescribe(value = "지정한 프로세스 플랫폼 업무 문서 또는 완료된 업무 문서에 연관 콘텐츠 생성.", action = ActionCreateTypeProcessPlatform.class)
	@POST
	@Path("type/processplatform/job/{job}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createTypeProcessPlatform(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("프로세스 플랫폼 작업 식별자") @PathParam("job") String job,
			JsonElement jsonElement) {
		ActionResult<ActionCreateTypeProcessPlatform.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateTypeProcessPlatform().execute(effectivePerson, job, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "지정한 콘텐츠 관리 문서에 연관 콘텐츠 생성.", action = ActionCreateTypeCms.class)
	@POST
	@Path("type/cms/document/{document}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createTypeCms(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("콘텐츠 관리 문서 식별자") @PathParam("document") String document, JsonElement jsonElement) {
		ActionResult<ActionCreateTypeCms.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreateTypeCms().execute(effectivePerson, document, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "지정한 프로세스 업무 문서의 연관 콘텐츠 교체.", action = ActionUpdateTypeProcessPlatform.class)
	@POST
	@Path("update/type/processplatform/job/{job}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateTypeProcessPlatform(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("프로세스 플랫폼 작업 식별자") @PathParam("job") String job,
			JsonElement jsonElement) {
		ActionResult<ActionUpdateTypeProcessPlatform.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateTypeProcessPlatform().execute(effectivePerson, job, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "지정한 콘텐츠 관리 문서의 연관 콘텐츠 교체.", action = ActionUpdateTypeCms.class)
	@POST
	@Path("update/type/cms/document/{document}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateTypeCms(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("콘텐츠 관리 문서 식별자") @PathParam("document") String document, JsonElement jsonElement) {
		ActionResult<ActionUpdateTypeCms.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateTypeCms().execute(effectivePerson, document, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "id로 여러 프로세스 플랫폼 연관 콘텐츠 삭제.", action = ActionDeleteTypeProcessPlatform.class)
	@POST
	@Path("delete/type/processplatform/job/{job}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteTypeProcessPlatform(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("프로세스 플랫폼 작업 식별자") @PathParam("job") String job,
			JsonElement jsonElement) {
		ActionResult<ActionDeleteTypeProcessPlatform.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteTypeProcessPlatform().execute(effectivePerson, job, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "id로 여러 콘텐츠 관리 연관 콘텐츠 삭제.", action = ActionDeleteTypeCms.class)
	@POST
	@Path("delete/type/cms/document/{document}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteTypeCms(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("프로세스 플랫폼 작업 식별자") @PathParam("document") String document, JsonElement jsonElement) {
		ActionResult<ActionDeleteTypeCms.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteTypeCms().execute(effectivePerson, document, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "프로세스 플랫폼 지정 job의 작업이 연관을 통해 읽기 권한을 가지는지 판단.", action = ActionReadableTypeProcessPlatform.class)
	@POST
	@Path("readable/type/processplatform")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void readableTypeProcessPlatform(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<ActionReadableTypeProcessPlatform.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionReadableTypeProcessPlatform().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "콘텐츠 플랫폼 지정 document의 작업이 연관을 통해 읽기 권한을 가지는지 판단.", action = ActionReadableTypeCms.class)
	@POST
	@Path("readable/type/cms")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void readableTypeCms(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionReadableTypeCms.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionReadableTypeCms().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "프로세스 플랫폼 지정 job 식별자의 연관 콘텐츠 목록.", action = ActionListTypeProcessPlatform.class)
	@GET
	@Path("list/type/processplatform/job/{job}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listTypeProcessPlatform(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("작업 식별자") @PathParam("job") String job) {
		ActionResult<List<ActionListTypeProcessPlatform.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListTypeProcessPlatform().execute(effectivePerson, job);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "프로세스 플랫폼 지정 job 식별자, 지정 연관 콘텐츠 박스 식별자의 연관 콘텐츠 목록.", action = ActionListTypeProcessPlatformWithSite.class)
	@GET
	@Path("list/type/processplatform/job/{job}/site/{site}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listTypeProcessPlatformWithSite(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("작업 식별자") @PathParam("job") String job,
			@JaxrsParameterDescribe("관련 콘텐츠 상자 식별자") @PathParam("site") String site) {
		ActionResult<List<ActionListTypeProcessPlatformWithSite.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListTypeProcessPlatformWithSite().execute(effectivePerson, job, site);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "콘텐츠 관리 지정 document 식별자의 연관 콘텐츠 목록.", action = ActionListTypeCms.class)
	@GET
	@Path("list/type/cms/document/{document}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listTypeCms(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("콘텐츠 관리 문서 식별자") @PathParam("document") String document) {
		ActionResult<List<ActionListTypeCms.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListTypeCms().execute(effectivePerson, document);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "콘텐츠 관리 지정 document 식별자, 지정 연관 콘텐츠 박스 식별자의 연관 콘텐츠 목록.", action = ActionListTypeCmsWithSite.class)
	@GET
	@Path("list/type/cms/document/{document}/site/{site}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listTypeCmsWithSite(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("콘텐츠 관리 문서 식별자") @PathParam("document") String document,
			@JaxrsParameterDescribe("관련 콘텐츠 상자 식별자") @PathParam("site") String site) {
		ActionResult<List<ActionListTypeCmsWithSite.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListTypeCmsWithSite().execute(effectivePerson, document, site);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
