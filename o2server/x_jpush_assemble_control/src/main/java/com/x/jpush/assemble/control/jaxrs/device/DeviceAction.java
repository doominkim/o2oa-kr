package com.x.jpush.assemble.control.jaxrs.device;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("device")
@JaxrsDescribe("JPush 푸시 기기 서비스 모듈")
public class DeviceAction extends StandardJaxrsAction {
	private static Logger logger = LoggerFactory.getLogger(DeviceAction.class);

	@JaxrsMethodDescribe(value = "현재 사용자의 모든 바인딩 기기 조회", action = ActionListAll.class)
	@GET
	@Path("list/{pushType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listAllByPushType(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("푸시 채널 유형: jpush|huawei") @PathParam("pushType") String pushType) {
		ActionResult<List<ActionListAll.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListAll().execute(request, this.effectivePerson(request), pushType);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new IllegalArgumentException("현재 사용자의 연결된 기기 전체를 가져오는 중 오류가 발생했습니다!", e);
			result.error(exception);
			logger.error(e, this.effectivePerson(request), request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "기기 바인딩 여부 확인", action = ActionCheck.class)
	@GET
	@Path("check/{deviceName}/{deviceType}/{pushType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void checkBind(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("기기 번호") @PathParam("deviceName") String deviceName,
			@JaxrsParameterDescribe("기기 유형: android|ios") @PathParam("deviceType") String deviceType,
			@JaxrsParameterDescribe("푸시 채널 유형: jpush|huawei") @PathParam("pushType") String pushType) {

		ActionResult<ActionCheck.Wo> result = new ActionResult<>();
		try {
			result = new ActionCheck().execute(request, this.effectivePerson(request), deviceName, deviceType,
					pushType);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new IllegalArgumentException("기기가 이미 연결되었는지 확인하는 중 오류가 발생했습니다!", e);
			result.error(exception);
			logger.error(e, this.effectivePerson(request), request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "기기 바인딩", action = ActionBind.class)
	@POST
	@Path("bind")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void bind(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("바인딩할 기기 객체") JsonElement jsonElement) {

		ActionResult<ActionBind.Wo> result = new ActionResult<>();
		try {
			result = new ActionBind().execute(request, this.effectivePerson(request), jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new IllegalArgumentException("기기를 연결하는 중 오류가 발생했습니다!", e);
			result.error(exception);
			logger.error(e, this.effectivePerson(request), request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "기기 바인딩 해제", action = ActionRemoveBindAll.class)
	@POST
	@Path("admin/unbind/all/person")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void unbindByAdmin(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("바인딩할 기기 객체") JsonElement jsonElement) {

		ActionResult<ActionRemoveBindAll.Wo> result = new ActionResult<>();
		try {
			result = new ActionRemoveBindAll().execute(this.effectivePerson(request), jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new IllegalArgumentException("기기 연결을 해제하는 중 오류가 발생했습니다!", e);
			result.error(exception);
			logger.error(e, this.effectivePerson(request), request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "기기 바인딩 해제", action = ActionRemoveBind.class)
	@DELETE
	@Path("unbind/{deviceName}/{deviceType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeBind(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("기기 번호") @PathParam("deviceName") String deviceName,
			@JaxrsParameterDescribe("기기 유형: android|ios") @PathParam("deviceType") String deviceType) {

		ActionResult<ActionRemoveBind.Wo> result = new ActionResult<>();
		try {
			result = new ActionRemoveBind().execute(request, this.effectivePerson(request), deviceName, deviceType);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new IllegalArgumentException("기기 연결을 해제하는 중 오류가 발생했습니다!", e);
			result.error(exception);
			logger.error(e, this.effectivePerson(request), request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "기기 바인딩 해제(새 버전에 pushType 필드 추가)", action = ActionRemoveBindNew.class)
	@GET
	@Path("unbind/new/{deviceName}/{deviceType}/{pushType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeBindNew(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("기기 번호") @PathParam("deviceName") String deviceName,
			@JaxrsParameterDescribe("기기 유형: android|ios") @PathParam("deviceType") String deviceType,
			@JaxrsParameterDescribe("푸시 채널 유형: jpush|huawei") @PathParam("pushType") String pushType) {

		ActionResult<ActionRemoveBindNew.Wo> result = new ActionResult<>();
		try {
			result = new ActionRemoveBindNew().execute(request, this.effectivePerson(request), deviceName, deviceType,
					pushType);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new IllegalArgumentException("기기 연결을 해제하는 중 오류가 발생했습니다!", e);
			result.error(exception);
			logger.error(e, this.effectivePerson(request), request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "푸시 채널 유형 조회, jpush|huawei", action = ActionConfigPushType.class)
	@GET
	@Path("config/push/type")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void configPushType(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {

		ActionResult<ActionConfigPushType.Wo> result = new ActionResult<>();
		try {
			result = new ActionConfigPushType().execute(request, this.effectivePerson(request));
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new IllegalArgumentException("푸시 채널 유형을 조회하는 중 오류가 발생했습니다!", e);
			result.error(exception);
			logger.error(e, this.effectivePerson(request), request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
