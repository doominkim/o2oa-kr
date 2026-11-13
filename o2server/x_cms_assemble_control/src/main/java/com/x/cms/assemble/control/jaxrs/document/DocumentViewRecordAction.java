package com.x.cms.assemble.control.jaxrs.document;

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

@Path("viewrecord")
@JaxrsDescribe("정보 접근 로그 관리")
public class DocumentViewRecordAction extends StandardJaxrsAction{

	private static  Logger logger = LoggerFactory.getLogger( DocumentViewRecordAction.class );

	@JaxrsMethodDescribe(value = "문서ID로 해당 문서의 접근 사용자 기록 정보 조회, 시간 역순, 상위 50건.", action = ActionQueryListViewRecordByFilterNext.class)
	@GET
	@Path("document/{docId}/filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listByDocumentFilterNext( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("정보 문서ID") @PathParam("docId") String docId,
			@JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count,
			@JaxrsParameterDescribe("마지막 정보 ID, 첫 페이지면 (0)으로 대체 가능") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionQueryListViewRecordByFilterNext.Wo>> result = null;
		try {
			result = new ActionQueryListViewRecordByFilterNext().execute( request, effectivePerson, docId, id, count );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionServiceLogic( e,"게시글 접근 정보를 조회하는 중 알 수 없는 오류가 발생했습니다." );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사용자 이름으로 해당 사용자가 접근한 문서 기록 조회, 시간 역순, 상위 50건.", action = ActionQueryListViewRecordByPerson.class)
	@GET
	@Path( "person/{name}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listByPerson( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("사용자 이름") @PathParam("name") String name ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionQueryListViewRecordByPerson.Wo>> result = null;
		try {
			result = new ActionQueryListViewRecordByPerson().execute( request, effectivePerson, name );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionServiceLogic( e,"게시글 접근 정보를 조회하는 중 알 수 없는 오류가 발생했습니다." );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "지정 문서ID 목록에서 미열람 문서ID 목록 판단.", action = ActionQueryListUnReadDocIds.class)
	@PUT
	@Path( "unread" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listUnReadIds( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult< ActionQueryListUnReadDocIds.Wo> result = null;
		try {
			result = new ActionQueryListUnReadDocIds().execute( request, effectivePerson, jsonElement );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionServiceLogic( e,"지정한 게시글 ID 목록에서 읽지 않은 게시글 ID 목록을 확인하는 중 알 수 없는 오류가 발생했습니다." );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "지정 문서ID 목록에서 미열람 문서ID 목록 판단.", action = ActionQueryListUnReadDocIds.class)
	@POST
	@Path( "unread/mockputtopost" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listUnReadIdsMockPutToPost( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							   JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult< ActionQueryListUnReadDocIds.Wo> result = null;
		try {
			result = new ActionQueryListUnReadDocIds().execute( request, effectivePerson, jsonElement );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionServiceLogic( e,"지정한 게시글 ID 목록에서 읽지 않은 게시글 ID 목록을 확인하는 중 알 수 없는 오류가 발생했습니다." );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "문서ID로 현재 사용자의 열람 기록 여부 조회.", action = ActionQueryHasViewDocument.class)
	@GET
	@Path("document/{docId}/has/view")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void hasViewDocument( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
										  @JaxrsParameterDescribe("정보 문서ID") @PathParam("docId") String docId) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionQueryHasViewDocument.Wo> result = null;
		try {
			result = new ActionQueryHasViewDocument().execute(effectivePerson, docId);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "열람 기록 페이지 조회.", action = ActionQueryListViewRecordPaging.class)
	@POST
	@Path("list/install/log/paging/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
									 @JaxrsParameterDescribe("페이지") @PathParam("page") Integer page,
									 @JaxrsParameterDescribe("페이지당 개수") @PathParam("size") Integer size, JsonElement jsonElement) {
		ActionResult<List<ActionQueryListViewRecordPaging.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionQueryListViewRecordPaging().execute(effectivePerson, page, size, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

}
