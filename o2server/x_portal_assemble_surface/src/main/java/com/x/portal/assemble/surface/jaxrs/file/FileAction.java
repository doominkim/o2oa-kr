package com.x.portal.assemble.surface.jaxrs.file;

import java.util.List;

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

@Path("file")
@JaxrsDescribe("파일 작업")
public class FileAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(FileAction.class);

	@JaxrsMethodDescribe(value = "지정한 사이트의 표시 가능한 모든 파일 목록.", action = ActionListWithPortal.class)
	@GET
	@Path("list/portal/{portalFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithPortal(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("식별자") @PathParam("portalFlag") String applicationFlag) {
		ActionResult<List<ActionListWithPortal.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPortal().execute(effectivePerson, applicationFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "파일 내용 접근. stream 형식으로 다운로드", action = ActionDownload.class)
	@GET
	@Path("{flag}/portal/{portalFlag}/download")
	@Consumes(MediaType.APPLICATION_JSON)
	public void download(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("식별자") @PathParam("flag") String flag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("portalFlag") String applicationFlag) {
		ActionResult<ActionDownload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownload().execute(effectivePerson, flag, applicationFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "파일 내용 접근. stream 형식으로 다운로드", action = ActionDownloadV2.class)
	@GET
	@Path("{flag}/download")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadV2(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
						 @JaxrsParameterDescribe("파일 식별자(파일 id, 파일 별칭 또는 파일 단축 url 코드)") @PathParam("flag") String flag) {
		ActionResult<ActionDownloadV2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadV2().execute(effectivePerson, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "파일 내용 접근.", action = ActionContent.class)
	@GET
	@Path("{flag}/portal/{portalFlag}/content")
	@Consumes(MediaType.APPLICATION_JSON)
	public void content(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("식별자") @PathParam("flag") String flag,
			@JaxrsParameterDescribe("앱 식별자") @PathParam("portalFlag") String applicationFlag) {
		ActionResult<ActionContent.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionContent().execute(effectivePerson, flag, applicationFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "파일 내용 접근.", action = ActionContentV2.class)
	@GET
	@Path("{flag}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void contentV2(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
						@JaxrsParameterDescribe("파일 식별자(파일 id, 파일 별칭 또는 파일 단축 url 코드)") @PathParam("flag") String flag) {
		ActionResult<ActionContentV2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionContentV2().execute(effectivePerson, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
