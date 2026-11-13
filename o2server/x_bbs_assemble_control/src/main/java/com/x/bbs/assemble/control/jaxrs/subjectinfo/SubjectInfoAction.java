package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionRoleInfoProcess;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSectionIdEmpty;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectIdEmpty;

@Path("subject")
@JaxrsDescribe("원글 조회 서비스")
public class SubjectInfoAction extends StandardJaxrsAction {
	private static Logger logger = LoggerFactory.getLogger(SubjectInfoAction.class);

	@JaxrsMethodDescribe(value = "필터 조건에 따른 추천 주제 목록.", action = ActionSubjectListRecommendedForPages.class)
	@PUT
	@Path("recommended/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listRecommendedSubjectForPage(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("표시 페이지 번호") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionSubjectListRecommendedForPages.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionSubjectListRecommendedForPages().execute(request, effectivePerson, page, count,
						jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "필터 조건으로 추천 주제 목록을 조회하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "BBS 홈에 추천된 모든 주제 목록 조회.", action = ActionSubjectListRecommendedForBBSIndex.class)
	@GET
	@Path("recommended/index/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listRecommendedSubjectForBBSIndex(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("조회 최대 항목 수") @PathParam("count") Integer count) {
		ActionResult<List<ActionSubjectListRecommendedForBBSIndex.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			if (count == null || count <= 0) {
				count = 10;
			}
		}
		if (check) {
			try {
				result = new ActionSubjectListRecommendedForBBSIndex().execute(request, effectivePerson, count);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "BBS 홈에 추천된 주제 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	// 모든 상단 고정글을 전부 조회해야 함
	@JaxrsMethodDescribe(value = "조회 가능한 모든 상단 고정글 목록 조회.", action = ActionSubjectListTop.class)
	@GET
	@Path("top/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listTopSubject(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("게시판 정보 ID") @PathParam("sectionId") String sectionId) {
		ActionResult<List<ActionSubjectListTop.Wo>> result = new ActionResult<>();
		Boolean check = true;
		EffectivePerson effectivePerson = this.effectivePerson(request);

		if (check) {
			if (sectionId == null || sectionId.isEmpty()) {
				check = false;
				Exception exception = new ExceptionSectionIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				result = new ActionSubjectListTop().execute(request, effectivePerson, sectionId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "상단 고정 게시물 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건에 따른 우수 주제 목록.", action = ActionSubjectListCreamedForPages.class)
	@PUT
	@Path("creamed/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCreamedSubjectForPage(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("표시 페이지 번호") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionSubjectListCreamedForPages.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			if (page == null) {
				page = 1;
			}
		}
		if (check) {
			if (count == null) {
				count = 20;
			}
		}
		if (check) {
			try {
				result = new ActionSubjectListCreamedForPages().execute(request, effectivePerson, page, count,
						jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "필터 조건으로 추천 게시물 목록을 조회하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건에 따른 SubjectInfo 목록, 다음 페이지.", action = ActionSubjectListForPage.class)
	@PUT
	@Path("filter/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listSubjectForPage(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("표시 페이지 번호") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionSubjectListForPage.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionSubjectListForPage().execute(request, effectivePerson, page, count, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "필터 조건으로 추천 주제 목록을 조회하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건(게시판 이름, 제목 유형)에 따른 SubjectInfo 목록, 다음 페이지.", action = ActionSubjectListWithSubjectTypeForPage.class)
	@POST
	@Path("filter/listsubjectinfo/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listSubjectWithSubjectTypeForPage(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
								   @JaxrsParameterDescribe("표시 페이지 번호") @PathParam("page") Integer page,
								   @JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionSubjectListWithSubjectTypeForPage.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionSubjectListWithSubjectTypeForPage().execute(request, effectivePerson, page, count, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "필터 조건으로 추천 주제 목록을 조회하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건(게시판 이름, 제목 유형)에 따른 SubjectInfo 평점 통계", action = ActionSubjectStatGradeWithSubjectType.class)
	@GET
	@Path("statgrade/sectionName/{sectionName}/subjectType/{subjectType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void statSubjectGrade(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
												  @JaxrsParameterDescribe("게시판 이름") @PathParam("sectionName") String sectionName,
												  @JaxrsParameterDescribe("주제 유형") @PathParam("subjectType") String subjectType) {
		ActionResult<List<ActionSubjectStatGradeWithSubjectType.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionSubjectStatGradeWithSubjectType().execute(effectivePerson, sectionName, subjectType);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "필터 조건(섹션 이름, 제목 유형)으로 SubjectInfo 평점을 집계하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건에 따른 SubjectInfo 목록, 홈용 서비스.", action = ActionSubjectListForBBSIndex.class)
	@PUT
	@Path("index/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listSubjectForBBSIndex(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("표시 페이지 번호") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionSubjectListForBBSIndex.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionSubjectListForBBSIndex().execute(request, effectivePerson, page, count, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "필터 조건으로 추천 주제 목록을 조회하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건에 따른 SubjectInfo 목록, 다음 페이지.", action = ActionSubjectSearchForPage.class)
	@PUT
	@Path("search/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void searchSubjectForPage(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("표시 페이지 번호") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionSubjectSearchForPage.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionSubjectSearchForPage().execute(request, effectivePerson, page, count, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "필터 조건으로 SubjectInfo를 조회하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "지정 ID로 원글 상세 정보 조회, 조회 수와 인기도 기록 필요.", action = ActionSubjectView.class)
	@GET
	@Path("view/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void viewSubject(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id) {
		ActionResult<ActionSubjectView.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				result = new ActionSubjectView().execute(request, effectivePerson, id);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "지정한 ID의 주제 상세 정보를 조회하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}