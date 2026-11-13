package com.x.bbs.assemble.control.jaxrs.permissioninfo;

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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ExceptionConfigSettingProcess;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSectionIdEmpty;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSubjectIdEmpty;

/**
 * @author sword
 */
@Path("permission")
@JaxrsDescribe("권한 조회 서비스")
public class PermissionInfoAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(PermissionInfoAction.class);

	@JaxrsMethodDescribe(value = "현재 사용자의 작업 권한 조회.", action = ActionGetUserPermission.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getUserPermission(@Suspended final AsyncResponse asyncResponse,
											  @Context HttpServletRequest request) {
		ActionResult<ActionGetUserPermission.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetUserPermission().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사용자의 지정 게시판 내 전체 작업 권한 조회.", action = ActionGetSectionOperationPermission.class)
	@GET
	@Path("section/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getSectionOperationPermissoin(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("게시판 ID") @PathParam("sectionId") String sectionId) {
		ActionResult<ActionGetSectionOperationPermission.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetSectionOperationPermission().execute(request, effectivePerson, sectionId);
		} catch (Exception e) {
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사용자의 지정 주제에 대한 전체 작업 권한 조회.", action = ActionGetSubjectOperationPermissoin.class)
	@GET
	@Path("subject/{subjectId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getSubjectOperationPermissoin(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("주 게시글 ID") @PathParam("subjectId") String subjectId) {
		ActionResult<ActionGetSubjectOperationPermissoin.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			if (subjectId == null || subjectId.isEmpty()) {
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				result = new ActionGetSubjectOperationPermissoin().execute(request, effectivePerson, subjectId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionConfigSettingProcess(e, "지정한 주제에 대한 사용자의 전체 작업 권한을 조회하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사용자의 지정 주제에 대한 전체 작업 권한 조회.", action = ActionCheckSubjectPublishable.class)
	@GET
	@Path("subjectPublishable/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void subjectPublishable(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("게시판 ID") @PathParam("sectionId") String sectionId) {
		ActionResult<ActionCheckSubjectPublishable.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCheckSubjectPublishable().execute(request, effectivePerson, sectionId);
		} catch (Exception e) {
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사용자의 지정 주제 답글 가능 여부 조회.", action = ActionCheckReplyPublishable.class)
	@GET
	@Path("replyPublishable/{subjectId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void replyPublishable(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("주 게시글 ID") @PathParam("subjectId") String subjectId) {
		ActionResult<ActionCheckReplyPublishable.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			if (subjectId == null || subjectId.isEmpty()) {
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				result = new ActionCheckReplyPublishable().execute(request, effectivePerson, subjectId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionConfigSettingProcess(e, "사용자가 지정한 주제에 댓글을 달 수 있는지 조회하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
