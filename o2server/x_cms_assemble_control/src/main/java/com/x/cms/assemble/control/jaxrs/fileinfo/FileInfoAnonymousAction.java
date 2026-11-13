package com.x.cms.assemble.control.jaxrs.fileinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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

@Path("anonymous/fileinfo")
@JaxrsDescribe("익명 접근 가능한 첨부파일 정보 관리 서비스")
public class FileInfoAnonymousAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(FileInfoAnonymousAction.class);

	@JaxrsMethodDescribe(value = "지정 문서의 전체 첨부파일 정보 목록 조회.", action = ActionListByDocId.class)
	@GET
	@Path("list/document/{documentId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listFileInfoByDocumentId(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("정보 문서ID") @PathParam("documentId") String documentId) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListByDocId.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListByDocId().execute(effectivePerson, documentId);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "ID로 fileInfo 객체 조회.", action = ActionGet.class)
	@GET
	@Path("{id}/document/{documentId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("첨부파일 정보 ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("정보 문서ID") @PathParam("documentId") String documentId) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		try {
			result = new ActionGet().execute(request, effectivePerson, id, documentId);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "ID로 지정 첨부파일 다운로드", action = ActionFileDownload.class)
	@GET
	@Path("download/document/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void attachmentDownLoad(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
			@JaxrsParameterDescribe("다운로드 첨부파일 이름") @QueryParam("fileName") String fileName) {
		ActionResult<ActionFileDownload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionFileDownload().execute(request, effectivePerson, id, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "첨부파일 ID로 첨부파일 다운로드, stream 출력 사용 여부 설정", action = ActionFileDownloadStream.class)
	@GET
	@Path("download/document/{id}/stream")
	@Consumes(MediaType.APPLICATION_JSON)
	public void attachmentDownloadStream(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
			@JaxrsParameterDescribe("다운로드 첨부파일 이름") @QueryParam("fileName") String fileName) {
		ActionResult<ActionFileDownloadStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionFileDownloadStream().execute(request, effectivePerson, id, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
