package com.x.cms.assemble.control.jaxrs.permission;

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

@Path("permission/management")
@JaxrsDescribe("문서 권한 작업 서비스(관리자)")
public class PermissionManagerAction extends StandardJaxrsAction {

	private static  Logger logger = LoggerFactory.getLogger( PermissionManagerAction.class );

	@JaxrsMethodDescribe(value = "모든 문서의 권한 정보 재계산.", action = ActionRefreshAllDocumentPermission.class)
	@GET
	@Path("refresh/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void refreshAllDocument( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionRefreshAllDocumentPermission.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = new ActionRefreshAllDocumentPermission().execute( request, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "로그인 사용자가 지정한 게시판의 관리자인지 조회하는 중 오류가 발생했습니다." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "지정 분류 문서의 권한 정보 재계산.", action = ActionRefreshCategoryDocPermission.class)
	@GET
	@Path("refresh/category/{categoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void refreshCategoryDocument( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
										 @JaxrsParameterDescribe("분류ID") @PathParam("categoryId") String categoryId ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionRefreshCategoryDocPermission.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = new ActionRefreshCategoryDocPermission().execute( request, effectivePerson, categoryId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "로그인 사용자가 지정한 게시판의 관리자인지 조회하는 중 오류가 발생했습니다." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}