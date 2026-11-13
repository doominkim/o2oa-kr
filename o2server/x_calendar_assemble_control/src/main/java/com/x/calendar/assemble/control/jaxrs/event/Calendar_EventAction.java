package com.x.calendar.assemble.control.jaxrs.event;

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

@Path("event")
@JaxrsDescribe("캘린더 이벤트 정보 관리 서비스")
public class Calendar_EventAction extends StandardJaxrsAction {

	// private StandardJaxrsActionProxy proxy = new
	// StandardJaxrsActionProxy(ThisApplication.context());
	private static Logger logger = LoggerFactory.getLogger(Calendar_EventAction.class);

	@JaxrsMethodDescribe(value = "ID로 캘린더 이벤트 정보 조회", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("캘린더 이벤트 정보 ID") @PathParam("id") String id) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionGet().execute(request, effectivePerson, id);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "ID로 일정 정보를 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "ID로 캘린더 이벤트 정보의 RFC 내용 조회", action = ActionGetRFCContent.class)
	@GET
	@Path("rfc/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getRFC(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("캘린더 이벤트 정보 ID") @PathParam("id") String id) {
		ActionResult<ActionGetRFCContent.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionGetRFCContent().execute(request, effectivePerson, id);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "ID로 일정 정보의 RFC 내용을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "이벤트 ID로 지정 캘린더 이벤트 정보 삭제", action = ActionDestroySingleEventWithId.class)
	@DELETE
	@Path("single/{eventId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void destorySingleEvent(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("캘린더 이벤트 정보 ID") @PathParam("eventId") String eventId) {
		ActionResult<ActionDestroySingleEventWithId.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionDestroySingleEventWithId().execute(request, effectivePerson, eventId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "ID로 일정 정보를 삭제하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "지정 이벤트 ID로 모든 반복 캘린더 이벤트 정보 삭제", action = ActionDestroyWithMaster.class)
	@DELETE
	@Path("all/{eventId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void destoryAllEventsWithRepeatMaster(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("캘린더 이벤트 정보 ID") @PathParam("eventId") String eventId) {
		ActionResult<ActionDestroyWithMaster.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionDestroyWithMaster().execute(request, effectivePerson, eventId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "지정한 일정 ID의 모든 반복 일정을 삭제하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "반복 일정 이벤트 중 지정 이벤트부터 이후의 모든 일정 이벤트 정보 삭제", action = ActionDestroyAfterEventId.class)
	@DELETE
	@Path("after/{eventId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void destoryAfterEvent(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("캘린더 이벤트 정보 ID") @PathParam("eventId") String eventId) {
		ActionResult<ActionDestroyAfterEventId.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionDestroyAfterEventId().execute(request, effectivePerson, eventId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "반복 일정에서 지정한 일정 이후의 모든 일정을 삭제하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "조건에 따라 캘린더 이벤트 정보 목록 조회", action = ActionListWithCondition.class)
	@PUT
	@Path("list/filter")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("조회 조건") JsonElement jsonElement) {
		ActionResult<ActionListWithCondition.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListWithCondition().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "조건으로 일정 목록을 조회합니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "조건에 따라 캘린더 이벤트 정보 목록 조회(단순 목록 반환)", action = ActionSimpleListWithCondition.class)
	@PUT
	@Path("list/filter/sample")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithFilterSample(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("조회 조건") JsonElement jsonElement) {
		ActionResult<List<ActionSimpleListWithCondition.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionSimpleListWithCondition().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "조건으로 일정 목록을 조회합니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "관리자가 조건에 따라 캘린더 이벤트 정보 목록 조회(단순 목록 반환)", action = ActionSimpleListWithConditionManager.class)
	@POST
	@Path("list/filter/sample/manager")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithFilterSampleManager(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("조회 조건") JsonElement jsonElement) {
		ActionResult<List<ActionSimpleListWithConditionManager.Wo>> result;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSimpleListWithConditionManager().execute(request, effectivePerson, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionEventProcess(e, "조건으로 일정 목록을 조회합니다!");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "새 캘린더 이벤트 정보 생성", action = ActionCreate.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void create(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
					   @JaxrsParameterDescribe("캘린더 이벤트 정보") JsonElement jsonElement) {
		ActionResult<ActionCreate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionCreate().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "일정 정보를 새로 만들거나 수정하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "관리자가 새 캘린더 이벤트 정보 생성", action = ActionManageCreate.class)
	@Path("manage")
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageCreate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
					   @JaxrsParameterDescribe("캘린더 이벤트 정보") JsonElement jsonElement) {
		ActionResult<ActionManageCreate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionManageCreate().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "일정 정보를 새로 만들거나 수정하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "이벤트의 모든 반복 일정 이벤트 수정", action = ActionUpdateWithMaster.class)
	@Path("update/all/{eventId}")
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateAllEventsWithRepeatMaster(
			@JaxrsParameterDescribe("캘린더 이벤트 정보 ID") @PathParam("eventId") String eventId,
			@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("캘린더 이벤트 정보") JsonElement jsonElement) {
		ActionResult<ActionUpdateWithMaster.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionUpdateWithMaster().execute(request, effectivePerson, eventId, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "일정 정보를 수정하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "반복 일정 이벤트 중 지정 이벤트부터 이후의 모든 일정 이벤트 정보 수정", action = ActionUpdateAfterEventId.class)
	@Path("update/after/{eventId}")
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateAfterEvent(@JaxrsParameterDescribe("캘린더 이벤트 정보 ID") @PathParam("eventId") String eventId,
			@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("캘린더 이벤트 정보") JsonElement jsonElement) {
		ActionResult<ActionUpdateAfterEventId.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionUpdateAfterEventId().execute(request, effectivePerson, eventId, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "일정 정보를 수정하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "이벤트 ID로 단일 일정 이벤트 정보 수정", action = ActionUpdateSingleEventWithId.class)
	@Path("update/single/{eventId}")
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateSingleEvent(@JaxrsParameterDescribe("캘린더 이벤트 정보 ID") @PathParam("eventId") String eventId,
			@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("캘린더 이벤트 정보") JsonElement jsonElement) {
		ActionResult<ActionUpdateSingleEventWithId.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionUpdateSingleEventWithId().execute(request, effectivePerson, eventId, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionEventProcess(e, "일정 정보를 수정하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
