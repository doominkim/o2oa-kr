package com.x.bbs.assemble.control.jaxrs.subjectinfo;

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
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionRoleInfoProcess;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectIdEmpty;

@Path("user/subject")
@JaxrsDescribe("원글 관리 서비스")
public class SubjectInfoManagerUserAction extends StandardJaxrsAction {

	private static  Logger logger = LoggerFactory.getLogger( SubjectInfoManagerUserAction.class );

	@JaxrsMethodDescribe( value = "지정 ID로 주제 상세 정보 조회.", action = ActionSubjectGet.class )
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectGet().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "지정한 ID의 주제 상세 정보를 가져오는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "우수 주제로 설정.", action = ActionSubjectSetCream.class )
	@GET
	@Path("setCream/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setCream( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectSetCream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectSetCream().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "추천 게시물로 지정하는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "우수 주제 취소.", action = ActionSubjectNonCream.class )
	@GET
	@Path("nonCream/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void nonCream( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectNonCream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectNonCream().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "주제를 잠그는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "원작 주제로 설정.", action = ActionSubjectSetOriginal.class )
	@GET
	@Path("setOriginal/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setOriginal( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectSetOriginal.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectSetOriginal().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "원본 게시물로 지정하는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "원작 주제 취소.", action = ActionSubjectNonOriginal.class )
	@GET
	@Path("nonOriginal/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void nonOriginal( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectNonOriginal.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectNonOriginal().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "원본 게시물 지정을 취소하는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "주제 잠금: 상태를 '사용'으로 변경, 속성 stopReply = false.", action = ActionSubjectUnLock.class )
	@GET
	@Path("unlock/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void unlock( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectUnLock.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectUnLock().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "주제를 잠그는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "주제 잠금: 상태를 '잠김'으로 변경, 속성 stopReply = true", action = ActionSubjectLock.class )
	@GET
	@Path("lock/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void lock( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectLock.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectLock().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "주제를 잠그는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "주제 완료 취소: 속성 isCompleted = false.", action = ActionSubjectUnComplete.class )
	@GET
	@Path("uncomplete/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void unComplete( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectUnComplete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectUnComplete().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "주제 완료를 취소하는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "주제 완료: 속성 isCompleted = true", action = ActionSubjectCompleted.class )
	@GET
	@Path("complete/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void complete( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectCompleted().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "주제를 완료 처리하는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "질문 글 답글 채택 취소", action = ActionSubjectUnAcceptReply.class )
	@GET
	@Path("unacceptreply/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void unAcceptReply( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectUnAcceptReply.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectUnAcceptReply().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "질문 게시물의 댓글 채택을 취소하는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "질문 글 답글 채택", action = ActionSubjectAcceptReply.class )
	@GET
	@Path("acceptreply/{id}/{replyId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void acceptReply( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("답글 정보 ID") @PathParam("replyId") String replyId ) {
		ActionResult<ActionSubjectAcceptReply.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectAcceptReply().execute( request, effectivePerson, id, replyId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "질문 게시물의 댓글을 채택하는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "게시판 상단 고정", action = ActionSubjectTopToSection.class )
	@GET
	@Path("topToSection/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void topToSection( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectTopToSection.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectTopToSection().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "섹션 상단 고정 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "게시판 상단 고정 취소", action = ActionSubjectNonTopToSection.class )
	@GET
	@Path("nonTopToSection/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void nonTopToSection( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectNonTopToSection.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectNonTopToSection().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "섹션 상단 고정을 취소하는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "상위 게시판 상단 고정", action = ActionSubjectTopToMainSection.class )
	@GET
	@Path("topToMainSection/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void topToMainSection( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectTopToMainSection.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectTopToMainSection().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "상위 섹션 상단 고정 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "상위 게시판 상단 고정 취소", action = ActionSubjectNonTopToMainSection.class )
	@GET
	@Path("nonTopToMainSection/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void nonTopToMainSection( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectNonTopToMainSection.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectNonTopToMainSection().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "상위 섹션 상단 고정을 취소하는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "포럼 상단 고정", action = ActionSubjectTopToForum.class )
	@GET
	@Path("topToForum/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void topToForum( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectTopToForum.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectTopToForum().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "전체 상단 고정 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "포럼 상단 고정 취소.", action = ActionSubjectNonTopToForum.class )
	@GET
	@Path("nonTopToForum/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void nonTopToForum( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectNonTopToForum.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectNonTopToForum().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "포럼 상단 고정을 취소하는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "전체 상단 고정.", action = ActionSubjectTopToBBS.class )
	@GET
	@Path("topToBBS/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void topToBBS( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectTopToBBS.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectTopToBBS().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "전체 상단 고정 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "BBS 홈에 추천.", action = ActionSubjectSetRecommendToBBSIndex.class )
	@GET
	@Path("setRecommendToBBSIndex/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setRecommendToBBSIndex( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectSetRecommendToBBSIndex.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectSetRecommendToBBSIndex().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "BBS 홈에 추천하는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "BBS 홈 추천 취소.", action = ActionSubjectNonRecommendToBBSIndex.class )
	@GET
	@Path("nonRecommendToBBSIndex/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void nonRecommendToBBSIndex( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectNonRecommendToBBSIndex.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectNonRecommendToBBSIndex().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "BBS 홈 추천을 취소하는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "전체 상단 고정 취소.", action = ActionSubjectNonTopToBBS.class )
	@GET
	@Path("nonTopToBBS/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void nonTopToBBS( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectNonTopToBBS.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectNonTopToBBS().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "전체 상단 고정을 취소하는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "새 주제 정보 생성 또는 주제 정보 수정.", action = ActionSubjectSave.class )
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void save(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<ActionSubjectSave.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Boolean check = true;

		if(check){
			try {
				result = new ActionSubjectSave().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				logger.error( e, effectivePerson, request, jsonElement);
				result.error( e );
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "ID로 지정 주제 정보 삭제.", action = ActionSubjectDelete.class )
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("원글 정보 ID") @PathParam("id") String id) {
		ActionResult<ActionSubjectDelete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectDelete().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "주제를 완료 처리하는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "지정 ID의 원글을 다른 게시판으로 이동.", action = ActionSubjectChangeSection.class )
	@PUT
	@Path("change/section")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void changeSection( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement ) {
		ActionResult<ActionSubjectChangeSection.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if(check){
			try {
				result = new ActionSubjectChangeSection().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "지정한 ID의 게시물을 다른 섹션으로 옮기는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "투표 기록 목록.", action = ActionVoteRecordListMyForPage.class )
	@PUT
	@Path("voterecord/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listVoteRecordForPage( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("표시 페이지 번호") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count,
			JsonElement jsonElement ) {
		ActionResult<List<ActionVoteRecordListMyForPage.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if(check){
			try {
				result = new ActionVoteRecordListMyForPage().execute( request, effectivePerson, page, count, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "주제 투표 기록을 조회하는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "투표 정보 제출.", action = ActionSubjectSubmitVoteResult.class )
	@Path("vote/submit")
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void voteSubmit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<ActionSubjectSubmitVoteResult.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Boolean check = true;

		if(check){
			try {
				result = new ActionSubjectSubmitVoteResult().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "투표 결과를 제출하는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "내가 게시한 주제 목록.", action = ActionSubjectListMyForPage.class )
	@PUT
	@Path("my/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMySubjectForPage( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("표시 페이지 번호") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count,
			JsonElement jsonElement ) {
		ActionResult<List<ActionSubjectListMyForPage.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if(check){
			try {
				result = new ActionSubjectListMyForPage().execute( request, effectivePerson, page, count, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "내가 작성한 주제를 조회하는 중 오류가 발생했습니다!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
