package com.x.hotpic.assemble.control.jaxrs.hotpic;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
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

@Path("cipher/hotpic")
@JaxrsDescribe("인기 정보 관리(서버 간 호출)")
public class HotPictureInfoCipherAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger(HotPictureInfoCipherAction.class);
	@JaxrsMethodDescribe(value = "CMS 문서 ID로 인기 정보 삭제", action= ActionCipherDeleteCMS.class )
	@DELETE
	@Path("cms/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteCms(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("CMS 문서 ID")  @PathParam("id") String id) {
		ActionResult<ActionCipherDeleteCMS.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCipherDeleteCMS().execute(effectivePerson,id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	

	@JaxrsMethodDescribe(value = "BBS 원글 ID로 인기 정보 삭제", action= ActionCipherDeleteBBS.class )
	@DELETE
	@Path("bbs/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteBBS(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("BBS 원글 ID")  @PathParam("id") String id) {
		ActionResult<ActionCipherDeleteBBS.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCipherDeleteBBS().execute(effectivePerson,id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	
	@JaxrsMethodDescribe(value = "ID로 단일 인기 이미지 정보 조회", action= ActionCipherGet.class )
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("인기 이미지 정보 id") @PathParam("id") String id) {
		ActionResult<ActionCipherGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCipherGet().execute(effectivePerson,id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	
	@JaxrsMethodDescribe(value = "필터 조건으로 HotPictureInfo 목록 조회, 다음 페이지", action= ActionCipherList.class )
	@PUT
	@Path("filter/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listForPage(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("현재 페이지 번호") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("페이지당 개수") @PathParam("count") Integer count,
		     JsonElement jsonElement) {
		ActionResult<List<ActionCipherList.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCipherList().execute(effectivePerson,page, count,jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}