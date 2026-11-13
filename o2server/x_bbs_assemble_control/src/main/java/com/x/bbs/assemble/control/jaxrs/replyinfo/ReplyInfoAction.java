package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

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
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionCountEmpty;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionPageEmpty;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyIdEmpty;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyInfoProcess;

@Path("reply")
@JaxrsDescribe("답글 조회 서비스")
public class ReplyInfoAction extends StandardJaxrsAction {
	private static Logger logger = LoggerFactory.getLogger(ReplyInfoAction.class);

	@JaxrsMethodDescribe(value = "필터 조건에 따른 ReplyInfo 목록, 다음 페이지.", action = ActionListWithSubjectForPage.class)
	@PUT
	@Path("filter/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithSubjectForPage(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("페이지 번호") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionListWithSubjectForPage.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			if (page == null) {
				check = false;
				Exception exception = new ExceptionPageEmpty();
				result.error(exception);
			}
		}
		if (check) {
			if (count == null) {
				check = false;
				Exception exception = new ExceptionCountEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				result = new ActionListWithSubjectForPage().execute(request, effectivePerson, page, count, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionReplyInfoProcess(e, "필터 조건으로 ReplyInfo 다음 페이지를 조회하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "지정 ID로 답글 정보 조회.", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("답글 정보 ID") @PathParam("id") String id) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionReplyIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				result = new ActionGet().execute(request, effectivePerson, id);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionReplyInfoProcess(e, "지정한 ID로 댓글 정보를 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "답글 ID로 해당 답글에 대한 답글 목록 조회.", action = ActionListWithReply.class)
	@GET
	@Path("list/sub/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listSubRepliesWithReply(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
					@JaxrsParameterDescribe("답글 정보 ID") @PathParam("id") String id) {
		ActionResult<List<ActionListWithReply.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			if (StringUtils.isEmpty( id )) {
				check = false;
				Exception exception = new ExceptionReplyIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				result = new ActionListWithReply().execute(request, effectivePerson, id);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionReplyInfoProcess(e, "댓글 ID로 해당 댓글의 답글 목록을 조회하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}