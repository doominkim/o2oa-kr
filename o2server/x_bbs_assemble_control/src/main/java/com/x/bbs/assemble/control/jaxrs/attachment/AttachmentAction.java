package com.x.bbs.assemble.control.jaxrs.attachment;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

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

@Path("attachment")
@JaxrsDescribe("첨부파일 작업")
public class AttachmentAction extends StandardJaxrsAction {
    private static Logger logger = LoggerFactory.getLogger(AttachmentAction.class);

    @JaxrsMethodDescribe(value = "첨부파일 업로드.", action = StandardJaxrsAction.class)
    @POST
    @Path("upload/subject/{subjectId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void upload(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("주 게시글 ID") @PathParam("subjectId") String subjectId,
            @JaxrsParameterDescribe("위치") @FormDataParam("site") String site,
            @JaxrsParameterDescribe("파일 내용") @FormDataParam(FILE_FIELD) final byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUpload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUpload().execute(request, effectivePerson, subjectId, site, bytes, disposition);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "첨부파일 업로드(콜백 포함).", action = ActionUploadCallback.class)
    @POST
    @Path("upload/subject/{subjectId}/callback/{callback}")
    @Produces(HttpMediaType.TEXT_HTML_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void uploadCallback(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("주 게시글 ID") @PathParam("subjectId") String subjectId,
            @JaxrsParameterDescribe("콜백 함수명") @PathParam("callback") String callback,
            @JaxrsParameterDescribe("위치") @FormDataParam("site") String site,
            @JaxrsParameterDescribe("파일 내용") @FormDataParam(FILE_FIELD) final byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUploadCallback.Wo<ActionUploadCallback.WoObject>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUploadCallback().execute(request, effectivePerson, subjectId, callback, site, bytes,
                    disposition);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "첨부파일 Id로 단일 첨부파일 정보 조회.", action = ActionAttachmentGet.class)
    @GET
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getWithId(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 ID") @PathParam("id") String id) {
        ActionResult<ActionAttachmentGet.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionAttachmentGet().execute(request, effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "Subject로 Attachment 목록 조회.", action = ActionAttachmentListBySubjectId.class)
    @GET
    @Path("list/subject/{subjectId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listWithSubjectId(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("게시글 ID") @PathParam("subjectId") String subjectId) {
        ActionResult<List<ActionAttachmentListBySubjectId.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionAttachmentListBySubjectId().execute(request, effectivePerson, subjectId);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "ID로 지정 첨부파일 다운로드", action = StandardJaxrsAction.class)
    @GET
    @Path("download/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithSubject(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id) {
        ActionResult<ActionDownloadWithId.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithId().execute(request, effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "Work로 첨부파일 다운로드, stream 출력 사용 여부 설정", action = ActionDownloadWithIdStream.class)
    @GET
    @Path("download/{id}/stream/{stream}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithSubjectStream(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe(".APPLICATION_OCTET_STREAM 헤더로 출력") @PathParam("stream") Boolean stream) {
        ActionResult<ActionDownloadWithIdStream.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithIdStream().execute(request, effectivePerson, id, stream);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "지정 subject의 지정 첨부파일 삭제.", action = ActionAttachmentDelete.class)
    @DELETE
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id) {
        ActionResult<ActionAttachmentDelete.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionAttachmentDelete().execute(request, effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

}
