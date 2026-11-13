package com.x.cms.assemble.control.jaxrs.search;

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

@Path("searchfilter")
@JaxrsDescribe("정보 문서 검색 서비스")
public class SearchFilterAction extends StandardJaxrsAction {

	@JaxrsMethodDescribe(value = "사용자가 접근 권한을 가진 모든 게시된 문서 분류 목록 조회.", action = ActionListAppSearchFilterForDocStatus.class)
	@GET
	@Path("list/publish/filter/category/{categoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPublishAppSearchFilter( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("분류ID") @PathParam("categoryId") String categoryId) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionListAppSearchFilterForDocStatus.Wo> result =
				new ActionListAppSearchFilterForDocStatus().execute(request, effectivePerson, "published", categoryId);
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사용자가 접근 권한을 가진 모든 초안 문서 분류 목록 조회.", action = ActionListAppSearchFilterForDocStatus.class)
	@GET
	@Path("list/draft/filter/category/{categoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listDraftAppSearchFilter( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("분류ID") @PathParam("categoryId") String categoryId) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionListAppSearchFilterForDocStatus.Wo> result =
				new ActionListAppSearchFilterForDocStatus().execute(request, effectivePerson, "draft", categoryId);
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사용자가 접근 권한을 가진 모든 보관된 문서 분류 목록 조회.", action = ActionListAppSearchFilterForDocStatus.class)
	@GET
	@Path("list/archive/filter/category/{categoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listArchivedAppSearchFilter( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("분류ID") @PathParam("categoryId") String categoryId) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionListAppSearchFilterForDocStatus.Wo> result =
				new ActionListAppSearchFilterForDocStatus().execute(request, effectivePerson, "archived", categoryId);
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}	
}