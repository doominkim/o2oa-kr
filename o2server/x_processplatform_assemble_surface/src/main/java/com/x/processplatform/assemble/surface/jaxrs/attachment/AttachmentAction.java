package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AttachmentAction", description = "첨부파일 인터페이스.")
@Path("attachment")
@JaxrsDescribe("첨부파일 인터페이스.")
public class AttachmentAction extends StandardJaxrsAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentAction.class);
    private static final String OPERATIONID_PREFIX = "AttachmentAction::";

    @Operation(summary = "파일 존재 여부 판단.", operationId = OPERATIONID_PREFIX + "available", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionAvailable.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "파일 존재 여부 판단.", action = ActionAvailable.class)
    @GET
    @Path("{id}/available")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void available(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id) {
        ActionResult<ActionAvailable.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionAvailable().execute(effectivePerson, id);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "업무 문서 식별자와 첨부파일 식별자로 첨부파일 정보 조회.", operationId = OPERATIONID_PREFIX + "getWithWork", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGetWithWork.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "업무 문서 식별자와 첨부파일 식별자로 첨부파일 정보 조회.", action = ActionGetWithWork.class)
    @GET
    @Path("{id}/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id) {
        ActionResult<ActionGetWithWork.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionGetWithWork().execute(effectivePerson, id, workId);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "업무 문서 식별자와 첨부파일 식별자로 첨부파일 정보 조회.", operationId = OPERATIONID_PREFIX + "getWithWorkCompleted", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGetWithWork.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "WorkCompleted와 첨부파일 Id로 단일 첨부파일 정보 조회", action = ActionGetWithWorkCompleted.class)
    @GET
    @Path("{id}/workcompleted/{workCompletedId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getWithWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("완료된 업무 문서 식별자") @PathParam("workCompletedId") String workCompletedId,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id) {
        ActionResult<ActionGetWithWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionGetWithWorkCompleted().execute(effectivePerson, id, workCompletedId);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "업무 문서 식별자 또는 완료된 업무 문서 식별자와 첨부파일 식별자로 첨부파일 정보 조회.", operationId = OPERATIONID_PREFIX
            + "getWithWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionGetWithWorkOrWorkCompleted.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "업무 문서 식별자 또는 완료된 업무 문서 식별자와 첨부파일 식별자로 첨부파일 정보 조회.", action = ActionGetWithWorkOrWorkCompleted.class)
    @GET
    @Path("{id}/workorworkcompleted/{workOrWorkCompleted}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getWithWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("업무 문서 또는 완료된 업무 문서 식별자") @PathParam("workOrWorkCompleted") String workOrWorkCompleted,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id) {
        ActionResult<ActionGetWithWorkOrWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionGetWithWorkOrWorkCompleted().execute(effectivePerson, id, workOrWorkCompleted);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "업무 문서 식별자로 첨부파일 목록 조회.", operationId = OPERATIONID_PREFIX + "listWithWork", responses = {
            @ApiResponse(content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithWork.Wo.class))) }) })
    @JaxrsMethodDescribe(value = "업무 문서 식별자로 첨부파일 목록 조회.", action = ActionListWithWork.class)
    @GET
    @Path("list/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId) {
        ActionResult<List<ActionListWithWork.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListWithWork().execute(effectivePerson, workId);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "완료된 업무 문서 식별자로 첨부파일 목록 조회.", operationId = OPERATIONID_PREFIX + "listWithWorkCompleted", responses = {
            @ApiResponse(content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithWorkCompleted.Wo.class))) }) })
    @JaxrsMethodDescribe(value = "완료된 업무 문서 식별자로 첨부파일 목록 조회.", action = ActionListWithWorkCompleted.class)
    @GET
    @Path("list/workcompleted/{workCompletedId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listWithWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("완료된 업무 문서 식별자") @PathParam("workCompletedId") String workCompletedId) {
        ActionResult<List<ActionListWithWorkCompleted.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListWithWorkCompleted().execute(effectivePerson, workCompletedId);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "업무 문서 또는 완료된 업무 문서 식별자로 첨부파일 목록 조회.", operationId = OPERATIONID_PREFIX
            + "listWithWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithWorkOrWorkCompleted.Wo.class))) }) })
    @JaxrsMethodDescribe(value = "업무 문서 또는 완료된 업무 문서 식별자로 첨부파일 목록 조회.", action = ActionListWithWorkOrWorkCompleted.class)
    @GET
    @Path("list/workorworkcompleted/{workOrWorkCompleted}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listWithWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("업무 문서 또는 완료된 업무 문서 식별자") @PathParam("workOrWorkCompleted") String workOrWorkCompleted) {
        ActionResult<List<ActionListWithWorkOrWorkCompleted.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListWithWorkOrWorkCompleted().execute(effectivePerson, workOrWorkCompleted);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "작업 식별자로 첨부파일 목록 조회.", operationId = OPERATIONID_PREFIX + "listWithJob", responses = {
            @ApiResponse(content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithJob.Wo.class))) }) })
    @JaxrsMethodDescribe(value = "작업 식별자로 첨부파일 목록 조회.", action = ActionListWithJob.class)
    @GET
    @Path("list/job/{job}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listWithJob(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("업무 문서의 job") @PathParam("job") String job) {
        ActionResult<List<ActionListWithJob.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListWithJob().execute(effectivePerson, job);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "지정 식별자의 첨부파일 삭제.", operationId = OPERATIONID_PREFIX + "delete", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDelete.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "지정 식별자의 첨부파일 삭제.", action = ActionDelete.class)
    @DELETE
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id) {
        ActionResult<ActionDelete.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDelete().execute(effectivePerson, id);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "지정 식별자의 첨부파일 삭제(mock delete to get).", operationId = OPERATIONID_PREFIX
            + "deleteMockDeleteToGet", responses = {
                    @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDelete.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "지정 식별자의 첨부파일 삭제(mock delete to get).", action = ActionDelete.class)
    @GET
    @Path("{id}/mockdeletetoget")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteMockDeleteToGet(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id) {
        ActionResult<ActionDelete.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDelete().execute(effectivePerson, id);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "업무 문서 식별자와 첨부파일 식별자로 첨부파일 삭제..", operationId = OPERATIONID_PREFIX + "deleteWithWork", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDeleteWithWork.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "업무 문서 식별자와 첨부파일 식별자로 첨부파일 삭제.", action = ActionDeleteWithWork.class)
    @DELETE
    @Path("{id}/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId) {
        ActionResult<ActionDeleteWithWork.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDeleteWithWork().execute(effectivePerson, id, workId);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "업무 문서 식별자와 첨부파일 식별자로 첨부파일 삭제(mock delete to get).", operationId = OPERATIONID_PREFIX
            + "deleteWithWorkMockDeleteToGet", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDeleteWithWork.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "업무 문서 식별자와 첨부파일 식별자로 첨부파일 삭제(mock delete to get).", action = ActionDeleteWithWork.class)
    @GET
    @Path("{id}/work/{workId}/mockdeletetoget")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteWithWorkMockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId) {
        ActionResult<ActionDeleteWithWork.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDeleteWithWork().execute(effectivePerson, id, workId);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "완료된 업무 문서 식별자와 첨부파일 식별자로 첨부파일 삭제.", operationId = OPERATIONID_PREFIX
            + "deleteWithWorkMockDeleteToGet", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDeleteWithWorkCompleted.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "완료된 업무 문서 식별자와 첨부파일 식별자로 첨부파일 삭제. ", action = ActionDeleteWithWorkCompleted.class)
    @DELETE
    @Path("{id}/workcompleted/{workCompletedId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteWithWorkCompleted(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("완료된 업무 문서 식별자") @PathParam("workCompletedId") String workCompletedId) {
        ActionResult<ActionDeleteWithWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDeleteWithWorkCompleted().execute(effectivePerson, id, workCompletedId);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "완료된 업무 문서 식별자와 첨부파일 식별자로 첨부파일 삭제(mock delete to get).", operationId = OPERATIONID_PREFIX
            + "deleteWithWorkCompletedMockDeleteToGet", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDeleteWithWorkCompleted.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "완료된 업무 문서 식별자와 첨부파일 식별자로 첨부파일 삭제(mock delete to get).", action = ActionDeleteWithWorkCompleted.class)
    @GET
    @Path("{id}/workcompleted/{workCompletedId}/mockdeletetoget")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteWithWorkCompletedMockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("완료된 업무 문서 식별자") @PathParam("workCompletedId") String workCompletedId) {
        ActionResult<ActionDeleteWithWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDeleteWithWorkCompleted().execute(effectivePerson, id, workCompletedId);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "지정 식별자의 첨부파일 다운로드.", operationId = OPERATIONID_PREFIX + "download", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDownload.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "지정 식별자의 첨부파일 다운로드.", action = ActionDownload.class)
    @GET
    @Path("download/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void download(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("다운로드 첨부파일 이름") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownload().execute(effectivePerson, id, fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "지정 식별자의 첨부파일 다운로드, 스트림 출력 사용.", operationId = OPERATIONID_PREFIX + "downloadStream", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDownloadStream.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "지정 식별자의 첨부파일 다운로드, 스트림 출력 사용.", action = ActionDownloadStream.class)
    @GET
    @Path("download/{id}/stream")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadStream(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("다운로드 첨부파일 이름") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadStream.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadStream().execute(effectivePerson, id, fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "업무 문서 식별자와 첨부파일 식별자로 첨부파일 다운로드.", operationId = OPERATIONID_PREFIX + "downloadWithWork", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDownloadWithWork.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "업무 문서 식별자와 첨부파일 식별자로 첨부파일 다운로드.", action = ActionDownloadWithWork.class)
    @GET
    @Path("download/{id}/work/{workId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("다운로드 첨부파일 이름") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadWithWork.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithWork().execute(effectivePerson, id, workId, fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "업무 문서 식별자와 첨부파일 식별자로 지정한 이름으로 첨부파일 다운로드, 스트림 출력 사용.", operationId = OPERATIONID_PREFIX
            + "downloadWithWorkStream", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDownloadWithWorkStream.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "업무 문서 식별자와 첨부파일 식별자로 지정한 이름으로 첨부파일 다운로드, 스트림 출력 사용.", action = ActionDownloadWithWorkStream.class)
    @GET
    @Path("download/{id}/work/{workId}/stream")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithWorkStream(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("다운로드 첨부파일 이름") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadWithWorkStream.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithWorkStream().execute(effectivePerson, id, workId, fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "업무 문서 식별자와 첨부파일 식별자로 지정한 이름으로 첨부파일 다운로드.", operationId = OPERATIONID_PREFIX
            + "downloadWithWorkWithExtension", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDownloadWithWork.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "업무 문서 식별자와 첨부파일 식별자로 지정한 이름으로 첨부파일 다운로드.", action = ActionDownloadWithWork.class)
    @GET
    @Path("download/{id}/work/{workId}/{fileName}.{extension}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithWorkWithExtension(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("다운로드 첨부파일 이름") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadWithWork.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithWork().execute(effectivePerson, id, workId, fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "업무 문서 식별자와 첨부파일 식별자로 지정한 이름으로 첨부파일 다운로드, 스트림 출력 사용.", operationId = OPERATIONID_PREFIX
            + "downloadWithWorkStreamWithExtension", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDownloadWithWorkStream.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "업무 문서 식별자와 첨부파일 식별자로 지정한 이름으로 첨부파일 다운로드, 스트림 출력 사용.", action = ActionDownloadWithWorkStream.class)
    @GET
    @Path("download/{id}/work/{workId}/stream/{fileName}.{extension}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithWorkStreamWithExtension(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("다운로드 첨부파일 이름") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadWithWorkStream.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithWorkStream().execute(effectivePerson, id, workId, fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "완료된 업무 문서 식별자와 첨부파일 식별자로 첨부파일 다운로드.", operationId = OPERATIONID_PREFIX
            + "downloadWithWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDownloadWithWorkCompleted.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "완료된 업무 문서 식별자와 첨부파일 식별자로 첨부파일 다운로드.", action = ActionDownloadWithWorkCompleted.class)
    @GET
    @Path("download/{id}/workcompleted/{workCompletedId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithWorkCompleted(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("완료된 업무 문서 식별자") @PathParam("workCompletedId") String workCompletedId,
            @JaxrsParameterDescribe("다운로드 첨부파일 이름") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadWithWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithWorkCompleted().execute(effectivePerson, id, workCompletedId, fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "완료된 업무 문서 식별자와 첨부파일 식별자로 지정한 이름으로 첨부파일 다운로드.", operationId = OPERATIONID_PREFIX
            + "downloadWithWorkCompletedWithExtension", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDownloadWithWorkCompleted.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "완료된 업무 문서 식별자와 첨부파일 식별자로 지정한 이름으로 첨부파일 다운로드.", action = ActionDownloadWithWorkCompleted.class)
    @GET
    @Path("download/{id}/workcompleted/{workCompletedId}/{fileName}.{extension}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithWorkCompletedWithExtension(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("완료된 업무 문서 식별자") @PathParam("workCompletedId") String workCompletedId,
            @JaxrsParameterDescribe("다운로드 첨부파일 이름") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadWithWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithWorkCompleted().execute(effectivePerson, id, workCompletedId, fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "완료된 업무 문서 식별자와 첨부파일 식별자로 첨부파일 다운로드, 스트림 출력 사용.", operationId = OPERATIONID_PREFIX
            + "downloadWithWorkCompletedStream", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDownloadWithWorkCompletedStream.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "완료된 업무 문서 식별자와 첨부파일 식별자로 첨부파일 다운로드, 스트림 출력 사용.", action = ActionDownloadWithWorkCompletedStream.class)
    @GET
    @Path("download/{id}/workcompleted/{workCompletedId}/stream")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithWorkCompletedStream(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("완료된 업무 문서 식별자") @PathParam("workCompletedId") String workCompletedId,
            @JaxrsParameterDescribe("다운로드 첨부파일 이름") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadWithWorkCompletedStream.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithWorkCompletedStream().execute(effectivePerson, id, workCompletedId,
                    fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "완료된 업무 문서 식별자와 첨부파일 식별자로 지정한 이름으로 첨부파일 다운로드, 스트림 출력 사용.", operationId = OPERATIONID_PREFIX
            + "downloadWithWorkCompletedStreamWithExtension", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDownloadWithWorkCompletedStream.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "완료된 업무 문서 식별자와 첨부파일 식별자로 지정한 이름으로 첨부파일 다운로드, 스트림 출력 사용.", action = ActionDownloadWithWorkCompletedStream.class)
    @GET
    @Path("download/{id}/workcompleted/{workCompletedId}/stream/{fileName}.{extension}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithWorkCompletedStreamWithExtension(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("완료된 업무 문서 식별자") @PathParam("workCompletedId") String workCompletedId,
            @JaxrsParameterDescribe("다운로드 첨부파일 이름") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadWithWorkCompletedStream.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithWorkCompletedStream().execute(effectivePerson, id, workCompletedId,
                    fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "업무 문서 식별자로 첨부파일 업로드.", operationId = OPERATIONID_PREFIX + "upload", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUploadWithWork.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "첨부파일 업로드.", action = ActionUploadWithWork.class)
    @POST
    @Path("upload/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void upload(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("위치") @FormDataParam("site") String site,
            @JaxrsParameterDescribe("첨부파일 이름") @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("톈인 확장 필드") @FormDataParam("extraParam") String extraParam,
            @JaxrsParameterDescribe("파일 내용") @FormDataParam(FILE_FIELD) byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUploadWithWork.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            if (StringUtils.isEmpty(extraParam)) {
                extraParam = this.request2Json(request);
            }
            if (bytes == null) {
                Map<String, List<FormDataBodyPart>> map = form.getFields();
                for (String key : map.keySet()) {
                    FormDataBodyPart part = map.get(key).get(0);
                    if ("application".equals(part.getMediaType().getType())) {
                        bytes = part.getValueAs(byte[].class);
                        break;
                    }
                }
            }
            result = new ActionUploadWithWork().execute(effectivePerson, workId, site, fileName, bytes, disposition,
                    extraParam);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "완료된 업무 문서 식별자로 첨부파일 업로드.", operationId = OPERATIONID_PREFIX + "uploadWithWorkCompleted", responses = {
            @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionUploadWithWorkCompleted.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "첨부파일 업로드.", action = ActionUploadWithWorkCompleted.class)
    @POST
    @Path("upload/workcompleted/{workCompletedId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void uploadWithWorkCompleted(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("완료된 업무 문서 식별자") @PathParam("workCompletedId") String workCompletedId,
            @JaxrsParameterDescribe("위치") @FormDataParam("site") String site,
            @JaxrsParameterDescribe("첨부파일 이름") @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("톈인 확장 필드") @FormDataParam("extraParam") String extraParam,
            @JaxrsParameterDescribe("파일 내용") @FormDataParam(FILE_FIELD) byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUploadWithWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            if (StringUtils.isEmpty(extraParam)) {
                extraParam = this.request2Json(request);
            }
            if (bytes == null) {
                Map<String, List<FormDataBodyPart>> map = form.getFields();
                for (String key : map.keySet()) {
                    FormDataBodyPart part = map.get(key).get(0);
                    if ("application".equals(part.getMediaType().getType())) {
                        bytes = part.getValueAs(byte[].class);
                        break;
                    }
                }
            }
            result = new ActionUploadWithWorkCompleted().execute(effectivePerson, workCompletedId, site, fileName,
                    bytes, disposition, extraParam);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));

    }

    @Deprecated
    @Operation(summary = "업무 문서 식별자로 첨부파일 업로드, jsonp 콜백.", operationId = OPERATIONID_PREFIX
            + "uploadWithWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionUploadWithWorkCompleted.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "업무 문서 식별자로 첨부파일 업로드, jsonp 콜백.", action = ActionUploadCallback.class)
    @POST
    @Path("upload/work/{workId}/callback/{callback}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(HttpMediaType.TEXT_HTML_UTF_8)
    public void uploadCallback(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("콜백 함수명") @PathParam("callback") String callback,
            @JaxrsParameterDescribe("위치") @FormDataParam("site") String site,
            @JaxrsParameterDescribe("첨부파일 이름") @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("파일 내용") @FormDataParam(FILE_FIELD) final byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUploadCallback.Wo<ActionUploadCallback.WoObject>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUploadCallback().execute(effectivePerson, workId, callback, site, fileName, bytes,
                    disposition);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "업무 문서 식별자로 첨부파일 수정.", operationId = OPERATIONID_PREFIX + "update", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUpdate.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "업무 문서 식별자로 첨부파일 내용 수정.", action = ActionUpdate.class)
    @PUT
    @Path("update/{id}/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void update(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("첨부파일 이름") @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("톈인 확장 필드") @FormDataParam("extraParam") String extraParam,
            @FormDataParam(FILE_FIELD) byte[] bytes,
            @JaxrsParameterDescribe("첨부파일") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            if (StringUtils.isEmpty(extraParam)) {
                extraParam = this.request2Json(request);
            }
            if (bytes == null) {
                Map<String, List<FormDataBodyPart>> map = form.getFields();
                for (String key : map.keySet()) {
                    FormDataBodyPart part = map.get(key).get(0);
                    if ("application".equals(part.getMediaType().getType())) {
                        bytes = part.getValueAs(byte[].class);
                        break;
                    }
                }
            }
            result = new ActionUpdate().execute(effectivePerson, id, workId, fileName, bytes, disposition, extraParam);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "업무 문서 식별자로 첨부파일 수정(mock put to post).", operationId = OPERATIONID_PREFIX
            + "updateMockPutToPost", responses = {
                    @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUpdate.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "업무 문서 식별자로 첨부파일 내용 수정(mock put to post).", action = ActionUpdate.class)
    @POST
    @Path("update/{id}/work/{workId}/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void updateMockPutToPost(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("첨부파일 이름") @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("톈인 확장 필드") @FormDataParam("extraParam") String extraParam,
            @FormDataParam(FILE_FIELD) byte[] bytes,
            @JaxrsParameterDescribe("첨부파일") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            if (StringUtils.isEmpty(extraParam)) {
                extraParam = this.request2Json(request);
            }
            if (bytes == null) {
                Map<String, List<FormDataBodyPart>> map = form.getFields();
                for (String key : map.keySet()) {
                    FormDataBodyPart part = map.get(key).get(0);
                    if ("application".equals(part.getMediaType().getType())) {
                        bytes = part.getValueAs(byte[].class);
                        break;
                    }
                }
            }
            result = new ActionUpdate().execute(effectivePerson, id, workId, fileName, bytes, disposition, extraParam);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "첨부파일 식별자와 업무 문서 식별자로 첨부파일 정보 수정.", operationId = OPERATIONID_PREFIX + "updateContent", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUpdateContent.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "첨부파일 식별자와 업무 문서 식별자로 첨부파일 정보 수정.", action = ActionUpdateContent.class)
    @PUT
    @Path("update/content/{id}/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateContent(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId, JsonElement jsonElement) {
        ActionResult<ActionUpdateContent.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUpdateContent().execute(effectivePerson, id, workId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "첨부파일 식별자와 업무 문서 식별자로 첨부파일 정보 수정(mock put to post).", operationId = OPERATIONID_PREFIX
            + "updateContentMockPutToPost", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionUpdateContent.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "첨부파일 식별자와 업무 문서 식별자로 첨부파일 정보 수정(mock put to post).", action = ActionUpdateContent.class)
    @POST
    @Path("update/content/{id}/work/{workId}/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateContentMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId, JsonElement jsonElement) {
        ActionResult<ActionUpdateContent.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUpdateContent().execute(effectivePerson, id, workId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "첨부파일 식별자와 업무 문서 식별자로 첨부파일 수정, jsonp 콜백.", operationId = OPERATIONID_PREFIX + "updateCallback", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUpdateContent.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "첨부파일 식별자와 업무 문서 식별자로 첨부파일 수정, jsonp 콜백.", action = ActionUpdateCallback.class)
    @POST
    @Path("update/{id}/work/{workId}/callback/{callback}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(HttpMediaType.TEXT_HTML_UTF_8)
    public void updateCallback(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("콜백 함수명") @PathParam("callback") String callback,
            @JaxrsParameterDescribe("첨부파일 이름") @FormDataParam(FILENAME_FIELD) String fileName,
            @FormDataParam(FILE_FIELD) final byte[] bytes,
            @JaxrsParameterDescribe("첨부파일") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUpdateCallback.Wo<ActionUpdateCallback.WoObject>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUpdateCallback().execute(effectivePerson, id, workId, callback, fileName, bytes,
                    disposition);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "첨부파일 식별자와 업무 문서 식별자로 첨부파일 수정, update 메서드와 동일, ntko가 첨부파일 업로드에 post 메서드만 설정할 수 있어 호환용.", operationId = OPERATIONID_PREFIX
            + "updateCallback", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionUpdateContent.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "첨부파일 식별자와 업무 문서 식별자로 첨부파일 수정, update 메서드와 동일, ntko가 첨부파일 업로드에 post 메서드만 설정할 수 있어 호환용.", action = ActionUpdate.class)
    @POST
    @Path("update/{id}/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void updatePost(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("첨부파일 이름") @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("톈인 확장 필드") @FormDataParam("extraParam") String extraParam,
            @FormDataParam(FILE_FIELD) byte[] bytes,
            @JaxrsParameterDescribe("첨부파일") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            if (StringUtils.isEmpty(extraParam)) {
                extraParam = this.request2Json(request);
            }
            if (bytes == null) {
                Map<String, List<FormDataBodyPart>> map = form.getFields();
                for (String key : map.keySet()) {
                    FormDataBodyPart part = map.get(key).get(0);
                    if ("application".equals(part.getMediaType().getType())) {
                        bytes = part.getValueAs(byte[].class);
                        break;
                    }
                }
            }
            result = new ActionUpdate().execute(effectivePerson, id, workId, fileName, bytes, disposition, extraParam);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "업무 문서 식별자로 지정한 업무 문서에서 첨부파일 복사.", operationId = OPERATIONID_PREFIX + "copyToWork", responses = {
            @ApiResponse(content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ActionCopyToWork.Wo.class))) }) })
    @JaxrsMethodDescribe(value = "업무 문서 식별자로 지정한 업무 문서에서 첨부파일 복사.", action = ActionCopyToWork.class)
    @POST
    @Path("copy/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void copyToWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId, JsonElement jsonElement) {
        ActionResult<List<ActionCopyToWork.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionCopyToWork().execute(effectivePerson, workId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "완료된 업무 문서 식별자로 지정한 업무 문서에서 첨부파일 복사.", operationId = OPERATIONID_PREFIX + "copyToWorkCompleted", responses = {
            @ApiResponse(content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ActionCopyToWorkCompleted.Wo.class))) }) })
    @JaxrsMethodDescribe(value = "완료된 업무 문서 식별자로 지정한 업무 문서에서 첨부파일 복사.", action = ActionCopyToWorkCompleted.class)
    @POST
    @Path("copy/workcompleted/{workCompletedId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void copyToWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("완료된 업무 문서 식별자") @PathParam("workCompletedId") String workCompletedId,
            JsonElement jsonElement) {
        ActionResult<List<ActionCopyToWorkCompleted.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionCopyToWorkCompleted().execute(effectivePerson, workCompletedId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "업무 문서 식별자로 지정한 업무 문서에서 첨부파일 소프트 복사(실제 저장 첨부파일은 복사하지 않고 공유하며, 이 인터페이스로 복사한 첨부파일은 삭제 시 기록만 삭제하고 첨부파일은 삭제하지 않음).", operationId = OPERATIONID_PREFIX
            + "copyToWorkSoft", responses = { @ApiResponse(content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ActionCopyToWorkSoft.Wo.class))) }) })
    @JaxrsMethodDescribe(value = "업무 문서 식별자로 지정한 업무 문서에서 첨부파일 소프트 복사(실제 저장 첨부파일은 복사하지 않고 공유하며, 이 인터페이스로 복사한 첨부파일은 삭제 시 기록만 삭제하고 첨부파일은 삭제하지 않음).", action = ActionCopyToWorkSoft.class)
    @POST
    @Path("copy/work/{workId}/soft")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void copyToWorkSoft(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId, JsonElement jsonElement) {
        ActionResult<List<ActionCopyToWorkSoft.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionCopyToWorkSoft().execute(effectivePerson, workId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "완료된 업무 문서 식별자로 지정한 업무 문서에서 첨부파일 소프트 복사(실제 저장 첨부파일은 복사하지 않고 공유하며, 이 인터페이스로 복사한 첨부파일은 삭제 시 기록만 삭제하고 첨부파일은 삭제하지 않음).", operationId = OPERATIONID_PREFIX
            + "copyToWorkCompletedSoft", responses = { @ApiResponse(content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ActionCopyToWorkCompletedSoft.Wo.class))) }) })
    @JaxrsMethodDescribe(value = "완료된 업무 문서 식별자로 지정한 업무 문서에서 첨부파일 소프트 복사(실제 저장 첨부파일은 복사하지 않고 공유하며, 이 인터페이스로 복사한 첨부파일은 삭제 시 기록만 삭제하고 첨부파일은 삭제하지 않음).", action = ActionCopyToWorkCompletedSoft.class)
    @POST
    @Path("copy/workcompleted/{workCompletedId}/soft")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void copyToWorkCompletedSoft(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("완료된 업무 문서 식별자") @PathParam("workCompletedId") String workCompletedId,
            JsonElement jsonElement) {
        ActionResult<List<ActionCopyToWorkCompletedSoft.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionCopyToWorkCompletedSoft().execute(effectivePerson, workCompletedId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "지정 첨부파일 식별자의 첨부파일 위치 정보 수정.", operationId = OPERATIONID_PREFIX + "changeSite", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionChangeSite.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "지정 첨부파일 식별자의 첨부파일 위치 정보 수정.", action = ActionChangeSite.class)
    @GET
    @Path("{id}/work/{workId}/change/site/{site}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void changeSite(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("위치") @PathParam("site") String site) {
        ActionResult<ActionChangeSite.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionChangeSite().execute(effectivePerson, id, workId, site);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "지정 첨부파일 식별자의 첨부파일 정보 수정.", operationId = OPERATIONID_PREFIX + "edit", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionEdit.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "지정 첨부파일 식별자의 첨부파일 정보 수정.", action = ActionEdit.class)
    @PUT
    @Path("edit/{id}/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void edit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId, JsonElement jsonElement) {
        ActionResult<ActionEdit.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionEdit().execute(effectivePerson, id, workId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "지정 첨부파일 식별자의 첨부파일 정보 수정(mock put to post).", operationId = OPERATIONID_PREFIX + "edit", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionEdit.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "M 지정 첨부파일 식별자의 첨부파일 정보 수정(mock put to post).", action = ActionEdit.class)
    @POST
    @Path("edit/{id}/work/{workId}/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void editMockPutToPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId, JsonElement jsonElement) {
        ActionResult<ActionEdit.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionEdit().execute(effectivePerson, id, workId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "지정 첨부파일 식별자의 첨부파일 정렬 번호 수정.", operationId = OPERATIONID_PREFIX + "changeOrderNumber", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionChangeOrderNumber.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "지정 첨부파일 식별자의 첨부파일 정렬 번호 수정.", action = ActionChangeOrderNumber.class)
    @GET
    @Path("{id}/work/{workOrWorkCompleted}/change/ordernumber/{orderNumber}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void changeOrderNumber(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workOrWorkCompleted") String workOrWorkCompleted,
            @JaxrsParameterDescribe("정렬 번호") @PathParam("orderNumber") Integer orderNumber) {
        ActionResult<ActionChangeOrderNumber.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionChangeOrderNumber().execute(effectivePerson, id, workOrWorkCompleted, orderNumber);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "지정 첨부파일 식별자의 첨부파일 텍스트 인식 내용 수정.", operationId = OPERATIONID_PREFIX + "exitText", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionEditText.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "지정 첨부파일 식별자의 첨부파일 텍스트 인식 내용 수정.", action = ActionEditText.class)
    @PUT
    @Path("edit/{id}/work/{workId}/text")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void exitText(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId, JsonElement jsonElement) {
        ActionResult<ActionEditText.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionEditText().execute(effectivePerson, id, workId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "지정 첨부파일 식별자의 첨부파일 텍스트 인식 내용 수정(mock put to post).", operationId = OPERATIONID_PREFIX
            + "exitTextMockPutToPost", responses = {
                    @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionEditText.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "지정 첨부파일 식별자의 첨부파일 텍스트 인식 내용 수정(mock put to post).", action = ActionEditText.class)
    @POST
    @Path("edit/{id}/work/{workId}/text/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void exitTextMockPutToPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId, JsonElement jsonElement) {
        ActionResult<ActionEditText.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionEditText().execute(effectivePerson, id, workId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "지정 첨부파일 식별자의 첨부파일 텍스트 인식 내용 조회.", operationId = OPERATIONID_PREFIX + "getText", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGetText.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "지정 첨부파일 식별자의 첨부파일 텍스트 인식 내용 조회.", action = ActionGetText.class)
    @GET
    @Path("{id}/work/{workId}/text")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getText(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId) {
        ActionResult<ActionGetText.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionGetText().execute(effectivePerson, id, workId);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "지정 업무 문서 식별자의 업무 문서를 조회해 html 형식 공문을 Word 파일로 변환하고 첨부파일에 추가.", operationId = OPERATIONID_PREFIX
            + "docToWord", responses = {
                    @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDocToWord.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "지정 업무 문서 식별자의 업무 문서를 조회해 html 형식 공문을 Word 파일로 변환하고 첨부파일에 추가.", action = ActionDocToWord.class)
    @POST
    @Path("doc/to/word/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void docToWord(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId, JsonElement jsonElement) {
        ActionResult<ActionDocToWord.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDocToWord().execute(effectivePerson, workId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "지정 업무 문서 식별자 또는 완료된 업무 문서 식별자로 html 형식 공문을 Word 파일로 변환하고 첨부파일에 추가.", operationId = OPERATIONID_PREFIX
            + "docToWordWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDocToWordWorkOrWorkCompleted.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "지정 업무 문서 식별자 또는 완료된 업무 문서 식별자로 html 형식 공문을 Word 파일로 변환하고 첨부파일에 추가.", action = ActionDocToWordWorkOrWorkCompleted.class)
    @POST
    @Path("doc/to/word/workorworkcompleted/{workOrWorkCompleted}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void docToWordWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("업무 문서/완료된 업무 문서 식별자") @PathParam("workOrWorkCompleted") String workOrWorkCompleted,
            JsonElement jsonElement) {
        ActionResult<ActionDocToWordWorkOrWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDocToWordWorkOrWorkCompleted().execute(effectivePerson, workOrWorkCompleted,
                    jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "지정 첨부파일 식별자로 pdf 형식 미리보기 파일 정보 반환, doc, docx 지원.", operationId = OPERATIONID_PREFIX
            + "docToWordWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionPreviewPdf.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "지정 첨부파일 식별자로 pdf 형식 미리보기 파일 정보 반환, doc, docx 지원.", action = ActionPreviewPdf.class)
    @GET
    @Path("{id}/preview/pdf")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void previewPdf(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id) {
        ActionResult<ActionPreviewPdf.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPreviewPdf().execute(effectivePerson, id);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "지정 첨부파일 식별자로 pdf 형식 미리보기 파일 반환, doc, docx 지원.", operationId = OPERATIONID_PREFIX
            + "docToWordWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionPreviewPdfResult.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "지정 첨부파일 식별자로 pdf 형식 미리보기 파일 반환, doc, docx 지원.", action = ActionPreviewPdfResult.class)
    @GET
    @Path("preview/pdf/{flag}/result")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void previewPdfResult(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("식별자") @PathParam("flag") String flag) {
        ActionResult<ActionPreviewPdfResult.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPreviewPdfResult().execute(effectivePerson, flag);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "지정 첨부파일 식별자로 이미지 형식 미리보기 파일 정보 반환, doc, docx 지원.", operationId = OPERATIONID_PREFIX
            + "docToWordWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionPreviewImage.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "지정 첨부파일 식별자로 이미지 형식 미리보기 파일 정보 반환, doc, docx 지원.", action = ActionPreviewImage.class)
    @GET
    @Path("{id}/preview/image/page/{page}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void previewImage(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("페이지 수") @PathParam("page") Integer page) {
        ActionResult<ActionPreviewImage.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPreviewImage().execute(effectivePerson, id, page);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "지정 첨부파일 식별자로 이미지 형식 미리보기 파일 반환, doc, docx 지원.", operationId = OPERATIONID_PREFIX
            + "docToWordWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionPreviewImageResult.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "지정 첨부파일 식별자로 이미지 형식 미리보기 파일 반환, doc, docx 지원.", action = ActionPreviewImageResult.class)
    @GET
    @Path("preview/image/{flag}/result")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void previewImageResult(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("식별자") @PathParam("flag") String flag) {
        ActionResult<ActionPreviewImageResult.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPreviewImageResult().execute(effectivePerson, flag);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "업무 문서 식별자 또는 완료된 업무 문서 식별자로 첨부파일 일괄 다운로드 및 압축, stream 출력 사용.", operationId = OPERATIONID_PREFIX
            + "batchDownloadWithWorkOrWorkCompletedStream", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionBatchDownload.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "업무 문서 식별자 또는 완료된 업무 문서 식별자로 첨부파일 일괄 다운로드 및 압축, stream 출력 사용.", action = ActionBatchDownload.class)
    @GET
    @Path("batch/download/work/{workId}/site/{site}/stream")
    @Consumes(MediaType.APPLICATION_JSON)
    public void batchDownloadWithWorkOrWorkCompletedStream(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("*Work 또는 WorkCompleted의 업무 문서 식별자") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("*첨부파일 상자 분류, 여러 값은 ~로 구분, (0)은 전체") @PathParam("site") String site,
            @JaxrsParameterDescribe("다운로드 첨부파일 이름") @QueryParam("fileName") String fileName,
            @JaxrsParameterDescribe("uploadWorkInfo 업로드로 반환된 업무 문서 정보 저장 id, 여러 값은 쉼표로 구분") @QueryParam("flag") String flag) {
        ActionResult<ActionBatchDownload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionBatchDownload().execute(effectivePerson, workId, site,
                    fileName, flag);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "업무 문서 식별자 또는 완료된 업무 문서 식별자로 첨부파일 일괄 다운로드 및 압축.", operationId = OPERATIONID_PREFIX
            + "batchDownloadWithWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionBatchDownload.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "업무 문서 식별자 또는 완료된 업무 문서 식별자로 첨부파일 일괄 다운로드 및 압축.", action = ActionBatchDownload.class)
    @GET
    @Path("batch/download/work/{workId}/site/{site}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void batchDownloadWithWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("*Work 또는 WorkCompleted의 업무 문서 식별자") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("*첨부파일 상자 분류, 여러 값은 ~로 구분, (0)은 전체") @PathParam("site") String site,
            @JaxrsParameterDescribe("다운로드 첨부파일 이름") @QueryParam("fileName") String fileName,
            @JaxrsParameterDescribe("uploadWorkInfo 업로드로 반환된 업무 문서 정보 저장 id, 여러 값은 쉼표로 구분") @QueryParam("flag") String flag) {
        ActionResult<ActionBatchDownload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionBatchDownload().execute(effectivePerson, workId, site, fileName,
                    flag);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "업무 문서의 job으로 첨부파일 일괄 다운로드 및 압축.", operationId = OPERATIONID_PREFIX
            + "batchDownload", responses = { @ApiResponse(content = {
            @Content(schema = @Schema(implementation = ActionBatchDownload.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "업무 문서의 job으로 첨부파일 일괄 다운로드 및 압축.", action = ActionBatchDownload.class)
    @GET
    @Path("batch/download/job/{job}/site/{site}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void batchDownload(@Suspended final AsyncResponse asyncResponse,
                     @Context HttpServletRequest request,
                     @JaxrsParameterDescribe("*업무 문서의 job, 여러 값은 ~로 구분") @PathParam("job") String job,
                     @JaxrsParameterDescribe("*첨부파일 상자 분류, 여러 값은 ~로 구분, (0)은 전체") @PathParam("site") String site,
                     @JaxrsParameterDescribe("다운로드 첨부파일 이름") @QueryParam("fileName") String fileName,
                     @JaxrsParameterDescribe("uploadWorkInfo 업로드로 반환된 업무 문서 정보 저장 id, 여러 값은 쉼표로 구분") @QueryParam("flag") String flag) {
        ActionResult<ActionBatchDownload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionBatchDownload().execute(effectivePerson, job, site, fileName, flag);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "업무 문서의 양식, 결재 기록 등 html 정보를 첨부파일 일괄 다운로드에 업로드.", operationId = OPERATIONID_PREFIX
            + "uploadWorkInfo", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionUploadWorkInfo.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "업무 문서의 양식, 결재 기록 등 html 정보를 캐시에 업로드.", action = ActionUploadWorkInfo.class)
    @PUT
    @Path("upload/work/{workId}/save/as/{flag}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void uploadWorkInfo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("Work 또는 WorkCompleted의 업무 문서 식별자") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("다른 이름으로 저장 형식: (0)은 변환 안 함|pdf는 pdf로 변환|word는 word로 변환") @PathParam("flag") String flag,
            JsonElement jsonElement) {
        ActionResult<ActionUploadWorkInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUploadWorkInfo().execute(effectivePerson, workId, flag, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "업무 문서의 양식, 결재 기록 등 html 정보를 첨부파일 일괄 다운로드에 업로드(mock put to post).", operationId = OPERATIONID_PREFIX
            + "uploadWorkInfoMockPutToPost", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionUploadWorkInfo.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "업무 문서의 양식, 결재 기록 등 html 정보를 첨부파일 일괄 다운로드에 업로드(mock put to post).", action = ActionUploadWorkInfo.class)
    @POST
    @Path("upload/work/{workId}/save/as/{flag}/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void uploadWorkInfoMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("Work 또는 WorkCompleted의 업무 문서 식별자") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("다른 이름으로 저장 형식: (0)은 변환 안 함|pdf는 pdf로 변환|word는 word로 변환") @PathParam("flag") String flag,
            JsonElement jsonElement) {
        ActionResult<ActionUploadWorkInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUploadWorkInfo().execute(effectivePerson, workId, flag, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "업무 문서의 양식, 결재 기록 등 html 정보 다운로드.", operationId = OPERATIONID_PREFIX + "downloadWorkInfo", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDownloadWorkInfo.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "업무 문서의 양식, 결재 기록 등 html 정보 다운로드.", action = ActionDownloadWorkInfo.class)
    @GET
    @Path("download/work/{workId}/att/{flag}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWorkInfo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("*Work 또는 WorkCompleted의 업무 문서 식별자") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("*uploadWorkInfo 업로드로 반환된 첨부파일 id") @PathParam("flag") String flag,
            @JaxrsParameterDescribe("바로 다운로드 여부(true|false)") @QueryParam("stream") Boolean stream) {
        ActionResult<ActionDownloadWorkInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWorkInfo().execute(effectivePerson, workId, flag, BooleanUtils.isTrue(stream));
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "관리자 첨부파일 일괄 업로드.", operationId = OPERATIONID_PREFIX + "manageBatchUpload", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionManageBatchUpload.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "관리자 첨부파일 일괄 업로드.", action = ActionManageBatchUpload.class)
    @POST
    @Path("batch/upload/manage")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void manageBatchUpload(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("업무 문서 식별자 목록, 여러 값은 쉼표로 구분") @FormDataParam("workIds") String workIds,
            @JaxrsParameterDescribe("위치") @FormDataParam("site") String site,
            @JaxrsParameterDescribe("첨부파일 이름") @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("지정 사용자에게 업로드") @FormDataParam("person") String person,
            @JaxrsParameterDescribe("첨부파일 정렬 번호") @FormDataParam("orderNumber") Integer orderNumber,
            @JaxrsParameterDescribe("주 업무 문서 기준으로 다른 업무 문서에 첨부파일 소프트 복사 여부, 모든 문서가 주 문서의 저장 첨부파일 공유") @FormDataParam("isSoftUpload") Boolean isSoftUpload,
            @JaxrsParameterDescribe("주 업무 문서 표시(isSoftUpload가 true이면 필수)") @FormDataParam("mainWork") String mainWork,
            @JaxrsParameterDescribe("톈인 확장 필드") @FormDataParam("extraParam") String extraParam,
            @JaxrsParameterDescribe("파일 내용") @FormDataParam(FILE_FIELD) final byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionManageBatchUpload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionManageBatchUpload().execute(effectivePerson, workIds, site, fileName, bytes, disposition,
                    extraParam, person, orderNumber, isSoftUpload, mainWork);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "지정 첨부파일 식별자, 관리자 첨부파일 다운로드.", operationId = OPERATIONID_PREFIX + "manageDownload", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionManageDownload.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "지정 첨부파일 식별자, 관리자 첨부파일 다운로드.", action = ActionManageDownload.class)
    @GET
    @Path("download/{id}/manage")
    @Consumes(MediaType.APPLICATION_JSON)
    public void manageDownload(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id) {
        ActionResult<ActionManageDownload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionManageDownload().execute(effectivePerson, id);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "지정 첨부파일 식별자, 관리자 첨부파일 다운로드, stream 출력 사용.", operationId = OPERATIONID_PREFIX
            + "manageDownloadStream", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionManageDownloadStream.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "지정 첨부파일 식별자, 관리자 첨부파일 다운로드, stream 출력 사용.", action = ActionManageDownloadStream.class)
    @GET
    @Path("download/{id}/manage/stream")
    @Consumes(MediaType.APPLICATION_JSON)
    public void manageDownloadStream(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id) {
        ActionResult<ActionManageDownloadStream.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionManageDownloadStream().execute(effectivePerson, id);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "html을 pdf로 변환하는 도구 클래스, 변환 후 downloadTransfer 인터페이스로 다운로드.", operationId = OPERATIONID_PREFIX
            + "htmlToPdf", responses = {
                    @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionHtmlToPdf.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "html을 pdf로 변환하는 도구 클래스, 변환 후 downloadTransfer 인터페이스로 다운로드.", action = ActionHtmlToPdf.class)
    @POST
    @Path("html/to/pdf")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void htmlToPdf(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<ActionHtmlToPdf.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionHtmlToPdf().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "html을 pdf로 변환하는 도구 클래스, 변환된 첨부파일 다운로드.", operationId = OPERATIONID_PREFIX + "downloadTransfer", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDownloadTransfer.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "html을 pdf로 변환하는 도구 클래스, 변환된 첨부파일 다운로드.", action = ActionDownloadTransfer.class)
    @GET
    @Path("download/transfer/flag/{flag}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadTransfer(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("*변환 후 첨부파일 id") @PathParam("flag") String flag,
            @JaxrsParameterDescribe("바로 다운로드 여부(true|false)") @QueryParam("stream") Boolean stream,
            @JaxrsParameterDescribe("파일 이름") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadTransfer.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadTransfer().execute(effectivePerson, flag, BooleanUtils.isTrue(stream), fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "관리자 첨부파일 일괄 교체.", operationId = OPERATIONID_PREFIX + "manageBatchUpdate", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionManageBatchUpdate.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "관리자 첨부파일 일괄 교체.", action = ActionManageBatchUpdate.class)
    @POST
    @Path("batch/update/manage")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void manageBatchUpdate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("첨부파일 Id 목록, 여러 값은 쉼표로 구분") @FormDataParam("ids") String ids,
            @JaxrsParameterDescribe("첨부파일 이름") @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("톈인 확장 필드") @FormDataParam("extraParam") String extraParam,
            @JaxrsParameterDescribe("파일 내용") @FormDataParam(FILE_FIELD) final byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionManageBatchUpdate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionManageBatchUpdate().execute(effectivePerson, ids, fileName, bytes, disposition,
                    extraParam);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "관리자 첨부파일 일괄 삭제.", operationId = OPERATIONID_PREFIX + "manageBatchDelete", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionManageBatchDelete.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "관리자 첨부파일 일괄 삭제.", action = ActionManageBatchDelete.class)
    @POST
    @Path("batch/delete/manage")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void manageBatchDelete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<ActionManageBatchDelete.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionManageBatchDelete().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "첨부파일 업로드.", operationId = OPERATIONID_PREFIX + "ActionUploadWithUrl", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUploadWithUrl.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "첨부파일 업로드", action = ActionUploadWithUrl.class)
    @POST
    @Path("upload/with/url")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void uploadWithUrl(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<ActionUploadWithUrl.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUploadWithUrl().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "html을 이미지로 변환하는 도구 클래스, Microsoft playwright 도구로 스크린샷 방식으로 이미지 생성,"
            + "변환 후 업무 문서가 있으면 downloadWithWork 인터페이스로, 없으면 downloadTransfer 인터페이스로 다운로드.", operationId = OPERATIONID_PREFIX
                    + "htmlToImage", responses = { @ApiResponse(content = {
                            @Content(schema = @Schema(implementation = ActionHtmlToImage.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "html을 이미지로 변환하는 도구 클래스, Microsoft playwright 도구로 스크린샷 방식으로 이미지 생성,"
            + "변환 후 업무 문서가 있으면 downloadWithWork 인터페이스로, 없으면 downloadTransfer 인터페이스로 다운로드.", action = ActionHtmlToImage.class)
    @POST
    @Path("html/to/image")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void htmlToImage(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<ActionHtmlToImage.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionHtmlToImage().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "V2 업무 문서 식별자 또는 완료된 업무 문서 식별자로 첨부파일 업로드, 같은 이름의 첨부파일이 있으면 교체.", operationId = OPERATIONID_PREFIX
            + "v2UploadWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = V2UploadWorkOrWorkCompleted.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "V2 업무 문서 식별자 또는 완료된 업무 문서 식별자로 첨부파일 업로드, 같은 이름의 첨부파일이 있으면 교체.", action = V2UploadWorkOrWorkCompleted.class)
    @POST
    @Path("v2/upload/workorworkcompleted/{workOrWorkCompleted}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void v2UploadWorkOrWorkCompleted(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("업무 문서 또는 완료된 업무 문서 식별자") @PathParam("workOrWorkCompleted") String workOrWorkCompleted,
            @JaxrsParameterDescribe("위치") @FormDataParam("site") String site,
            @JaxrsParameterDescribe("첨부파일 이름") @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("파일 내용") @FormDataParam(FILE_FIELD) byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<V2UploadWorkOrWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new V2UploadWorkOrWorkCompleted().execute(effectivePerson, workOrWorkCompleted, site, fileName,
                    bytes, disposition);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "V2 업무 문서 식별자 또는 완료된 업무 문서 식별자로 첨부파일 업로드, 파일 내용을 base64로 인코딩한 텍스트 업로드, 같은 이름의 첨부파일이 있으면 교체.", operationId = OPERATIONID_PREFIX
            + "v2UploadWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = V2UploadWorkOrWorkCompletedBase64.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "V2 업무 문서 식별자 또는 완료된 업무 문서 식별자로 첨부파일 업로드, 파일 내용을 base64로 인코딩한 텍스트 업로드, 같은 이름의 첨부파일이 있으면 교체.", action = V2UploadWorkOrWorkCompletedBase64.class)
    @POST
    @Path("v2/upload/workorworkcompleted/{workOrWorkCompleted}/base64")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void v2UploadWorkOrWorkCompletedBase64(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("업무 문서 또는 완료된 업무 문서 식별자") @PathParam("workOrWorkCompleted") String workOrWorkCompleted,
            JsonElement jsonElement) {
        ActionResult<V2UploadWorkOrWorkCompletedBase64.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new V2UploadWorkOrWorkCompletedBase64().execute(effectivePerson, workOrWorkCompleted, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "지정 첨부파일의 온라인 편집 정보 조회.", operationId = OPERATIONID_PREFIX + "getOnlineInfo", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionOnlineInfo.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "지정 첨부파일의 온라인 편집 정보 조회.", action = ActionOnlineInfo.class)
    @GET
    @Path("{id}/online/info")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getOnlineInfo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                        @JaxrsParameterDescribe("첨부파일 식별자") @PathParam("id") String id) {
        ActionResult<ActionOnlineInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionOnlineInfo().execute(effectivePerson, id);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "업무 문서 내 송장 정보 조회.", action = ActionGetInvoiceInfo.class)
    @GET
    @Path("invoice/{id}/jobOrWorkOrWorkCompleted/{jobOrWorkOrWorkCompleted}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getInvoiceInfo(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("업무 문서 또는 완료된 업무 문서 식별자 또는 job") @PathParam("jobOrWorkOrWorkCompleted") String jobOrWorkOrWorkCompleted,
            @JaxrsParameterDescribe("송장 파일 식별자") @PathParam("id") String id) {
        ActionResult<ActionGetInvoiceInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionGetInvoiceInfo().execute(effectivePerson, id, jobOrWorkOrWorkCompleted);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "업무 문서 내 지정 송장 다운로드.", action = ActionDownloadInvoice.class)
    @GET
    @Path("download/invoice/{id}/jobOrWorkOrWorkCompleted/{jobOrWorkOrWorkCompleted}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadInvoice(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("*송장 파일 식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("*업무 문서 또는 완료된 업무 문서 식별자 또는 job") @PathParam("jobOrWorkOrWorkCompleted") String jobOrWorkOrWorkCompleted,
            @JaxrsParameterDescribe("다운로드 첨부파일 이름") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadInvoice.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadInvoice().execute(effectivePerson, id, jobOrWorkOrWorkCompleted, fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }
}
