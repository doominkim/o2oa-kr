package com.x.program.center.jaxrs.deploy;

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
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("deploy")
@JaxrsDescribe("배포")
public class DeployAction extends StandardJaxrsAction {

	private static final Logger logger = LoggerFactory.getLogger(DeployAction.class);

	@JaxrsMethodDescribe(value = "프런트엔드 정적 리소스 파일 배포", action = ActionDeployWebRes.class)
	@POST
	@Path("web/resource/as/new/{asNew}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void deployWebResource(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("덮어쓰기 유형: true는 원본 파일 삭제 후 업로드, false는 원본 파일 덮어쓰기") @PathParam("asNew") Boolean asNew,
			@JaxrsParameterDescribe("첨부파일 저장 디렉터리(zip 파일은 비워도 되며, 그 외는 비울 수 없음)") @FormDataParam("filePath") String filePath,
			@JaxrsParameterDescribe("제목") @FormDataParam("title") String title,
			@JaxrsParameterDescribe("버전") @FormDataParam("version") String version,
			@JaxrsParameterDescribe("업그레이드 설명 업데이트") @FormDataParam("remark") String remark,
			@JaxrsParameterDescribe("첨부파일 이름") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("첨부파일 식별자") @FormDataParam(FILE_FIELD) final byte[] bytes,
			@JaxrsParameterDescribe("zip 파일 업로드") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionDeployWebRes.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			BaseAction.Wi wi = new BaseAction.Wi();
			wi.setAsNew(asNew);
			wi.setName(fileName);
			wi.setFilePath(filePath);
			wi.setTitle(title);
			wi.setVersion(version);
			wi.setRemark(remark);
			result = new ActionDeployWebRes().execute(effectivePerson,wi, bytes, disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "서버 war 패키지와 jar 패키지 배포(플랫폼 앱 및 사용자 정의 앱 포함)", action = ActionDeployServerRes.class)
	@POST
	@Path("server/resource")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void deployServerResource(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("제목") @FormDataParam("title") String title,
			@JaxrsParameterDescribe("버전") @FormDataParam("version") String version,
			@JaxrsParameterDescribe("업그레이드 설명 업데이트") @FormDataParam("remark") String remark,
			@JaxrsParameterDescribe("첨부파일 이름") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("파일 내용") @FormDataParam(FILE_FIELD) byte[] bytes,
			@JaxrsParameterDescribe("파일 업로드") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionDeployServerRes.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			BaseAction.Wi wi = new BaseAction.Wi();
			wi.setName(fileName);
			wi.setTitle(title);
			wi.setVersion(version);
			wi.setRemark(remark);
			result = new ActionDeployServerRes().execute(effectivePerson, wi, bytes, disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "o2server 업그레이드", action = ActionDeployO2Server.class)
	@POST
	@Path("server/o2")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void deployO2Server(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("제목") @FormDataParam("title") String title,
			@JaxrsParameterDescribe("버전") @FormDataParam("version") String version,
			@JaxrsParameterDescribe("업그레이드 설명 업데이트") @FormDataParam("remark") String remark,
			@JaxrsParameterDescribe("첨부파일 이름") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("o2server 업그레이드 패키지 업로드") @FormDataParam(FILE_FIELD) final FormDataBodyPart part) {
		ActionResult<ActionDeployO2Server.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			BaseAction.Wi wi = new BaseAction.Wi();
			wi.setName(fileName);
			wi.setTitle(title);
			wi.setVersion(version);
			wi.setRemark(remark);
			result = new ActionDeployO2Server().execute(effectivePerson, wi, part);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "배포 로그 정보 페이지 나열.", action = ActionListPaging.class)
	@POST
	@Path("list/paging/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("페이지") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("페이지당 개수") @PathParam("size") Integer size) {
		ActionResult<List<ActionListPaging.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListPaging().execute(effectivePerson, page, size);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "지정 배포 로그 조회.", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("식별자") @PathParam("id") String id) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
