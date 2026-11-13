package com.x.cms.assemble.control.jaxrs.document;

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
import com.x.cms.assemble.control.queue.DataImportStatus;
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

@Path("document")
@JaxrsDescribe("정보 게시 정보 문서 관리")
public class DocumentAction extends StandardJaxrsAction {

    private static final Logger logger = LoggerFactory.getLogger(DocumentAction.class);

    @JaxrsMethodDescribe(value = "지정 문서의 분류 정보 변경.", action = ActionPersistChangeCategory.class)
    @PUT
    @Path("category/change")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_changeCategory(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistChangeCategory.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistChangeCategory().execute(request, jsonElement, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "지정 문서의 분류 정보 변경.", action = ActionPersistChangeCategory.class)
    @POST
    @Path("category/change/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_changeCategoryMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistChangeCategory.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistChangeCategory().execute(request, jsonElement, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "지정 문서의 데이터만 수정.", action = ActionPersistBatchModifyData.class)
    @PUT
    @Path("batch/data/modify")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_batchDataModify(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistBatchModifyData.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistBatchModifyData().execute(request, jsonElement, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "지정 문서의 데이터만 수정.", action = ActionPersistBatchModifyData.class)
    @POST
    @Path("batch/data/modify/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_batchDataModifyMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistBatchModifyData.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistBatchModifyData().execute(request, jsonElement, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "가져오기 배치 번호로 가져오기 상태 정보 조회.", action = ActionQueryImportStatusWithName.class)
    @GET
    @Path("batch/{batchName}/status")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_checkImportStatus(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("가져오기 배치 번호") @PathParam("batchName") String batchName) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<DataImportStatus> result = new ActionResult<>();
        try {
            result = new ActionQueryImportStatusWithName().execute(request, effectivePerson, batchName);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "전체 가져오기 상태 정보 조회.", action = ActionQueryAllImportStatus.class)
    @GET
    @Path("batch/status")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_checkAllImportStatus(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<DataImportStatus>> result = new ActionResult<>();
        try {
            result = new ActionQueryAllImportStatus().execute(request, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "ID로 정보 게시 문서 정보 객체 상세 조회, 첨부파일 목록과 데이터 정보 포함.", action = ActionQueryGetDocument.class)
    @GET
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("정보 문서ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryGetDocument.Wo> result = new ActionResult<>();
        try {
            result = new ActionQueryGetDocument().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "정렬과 표시에 사용할 수 있는 문서 객체 열 이름 목록.", action = ActionQueryListDocumentFields.class)
    @GET
    @Path("document/fields")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listDocumentFields(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryListDocumentFields.Wo> result = new ActionResult<>();
        try {
            result = new ActionQueryListDocumentFields().execute(request);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "ID로 정보 게시 문서 정보 객체 상세 접근, 첨부파일 목록과 데이터 정보 포함.", action = ActionQueryViewDocument.class)
    @GET
    @Path("{id}/view")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_view(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("정보 문서ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryViewDocument.Wo> result = new ActionResult<>();
        try {
            result = new ActionQueryViewDocument().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "ID로 정보 게시 문서 정보 조회 횟수 조회.", action = ActionQueryCountViewTimes.class)
    @GET
    @Path("{id}/view/count")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_getViewCount(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("정보 문서ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryCountViewTimes.Wo> result = new ActionResult<>();
        try {
            result = new ActionQueryCountViewTimes().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "필터 조건에 맞는 게시된 정보 수 조회.", action = ActionQueryCountWithFilter.class)
    @PUT
    @Path("filter/count")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_countDocumentWithFilter(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryCountWithFilter.Wo> result = new ActionResult<>();
        try {
            result = new ActionQueryCountWithFilter().execute(request, jsonElement, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "필터 조건에 맞는 게시된 정보 수 조회.", action = ActionQueryCountWithFilter.class)
    @POST
    @Path("filter/count/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_countDocumentWithFilterMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryCountWithFilter.Wo> result = new ActionResult<>();
        try {
            result = new ActionQueryCountWithFilter().execute(request, jsonElement, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "ID로 정보 게시 문서 정보 삭제.", action = ActionPersistDeleteDocument.class)
    @DELETE
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("정보 문서ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistDeleteDocument.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistDeleteDocument().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "ID로 정보 게시 문서 정보 삭제.", action = ActionPersistDeleteDocument.class)
    @GET
    @Path("{id}/mockdeletetoget")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_deleteMockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("정보 문서ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistDeleteDocument.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistDeleteDocument().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "배치 번호로 정보 게시 문서 정보 삭제.", action = ActionPersistDeleteWithBatch.class)
    @DELETE
    @Path("batch/{batchId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_deleteWithBatchName(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("정보 문서ID") @PathParam("batchId") String batchId) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistDeleteWithBatch.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistDeleteWithBatch().execute(request, batchId, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "배치 번호로 정보 게시 문서 정보 삭제.", action = ActionPersistDeleteWithBatch.class)
    @GET
    @Path("batch/{batchId}/mockdeletetoget")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_deleteWithBatchNameMockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("정보 문서ID") @PathParam("batchId") String batchId) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistDeleteWithBatch.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistDeleteWithBatch().execute(request, batchId, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "ID로 정보 게시 문서 정보 보관.", action = ActionPersistArchive.class)
    @GET
    @Path("achive/{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_achive(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("정보 문서ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistArchive.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistArchive().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "문서ID로 문서 정식 게시, 메시지는 publishNotify 인터페이스로 발송.", action = ActionPersistPublishAndNotify.class)
    @PUT
    @Path("publish/{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_publishAndNotify(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("문서ID") @PathParam("id") String id,
            JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistPublishAndNotify.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistPublishAndNotify().execute(request, id, effectivePerson, jsonElement);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "문서ID로 문서 정식 게시, 메시지는 publishNotify 인터페이스로 발송.", action = ActionPersistPublishAndNotify.class)
    @POST
    @Path("publish/{id}/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_publishAndNotifyMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("문서ID") @PathParam("id") String id,
            JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistPublishAndNotify.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistPublishAndNotify().execute(request, id, effectivePerson, jsonElement);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "정보 내용 바로 게시, 새 정보 게시 문서를 생성하고 바로 게시.", action = ActionPersistPublishContent.class)
    @PUT
    @Path("publish/content")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_publishContent(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistPublishContent.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistPublishContent().execute(request, jsonElement, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "정보 내용 바로 게시, 새 정보 게시 문서를 생성하고 바로 게시.", action = ActionPersistPublishContent.class)
    @POST
    @Path("publish/content/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_publishContentMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistPublishContent.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistPublishContent().execute(request, jsonElement, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "ID로 정보 내용 게시 상태를 취소하고 초안으로 변경.", action = ActionPersistPublishCancel.class)
    @PUT
    @Path("publish/{id}/cancel")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_publishCancel(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("정보 문서ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistPublishCancel.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistPublishCancel().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "ID로 정보 내용 게시 상태를 취소하고 초안으로 변경.", action = ActionPersistPublishCancel.class)
    @POST
    @Path("publish/{id}/cancel/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_publishCancelMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("정보 문서ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistPublishCancel.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistPublishCancel().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "필터 조건에 맞는 게시된 정보 내용 목록, 다음 페이지.", action = ActionQueryListNextWithFilter.class)
    @PUT
    @Path("filter/list/{id}/next/{count}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listNextWithFilter(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("마지막 정보 ID, 첫 페이지면 (0)으로 대체 가능") @PathParam("id") String id,
            @JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListNextWithFilter.Wo>> result = new ActionResult<>();
        try {
            result = new ActionQueryListNextWithFilter().execute(request, id, count, jsonElement, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "필터 조건에 맞는 게시된 정보 내용 목록, 다음 페이지.", action = ActionQueryListNextWithFilter.class)
    @POST
    @Path("filter/list/{id}/next/{count}/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listNextWithFilterMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("마지막 정보 ID, 첫 페이지면 (0)으로 대체 가능") @PathParam("id") String id,
            @JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListNextWithFilter.Wo>> result = new ActionResult<>();
        try {
            result = new ActionQueryListNextWithFilter().execute(request, id, count, jsonElement, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "필터 조건에 맞는 게시된 정보 내용 목록, 이전 페이지.", action = ActionQueryListPrevWithFilter.class)
    @PUT
    @Path("filter/list/{id}/prev/{count}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("마지막 정보 ID, 첫 페이지면 (0)으로 대체 가능") @PathParam("id") String id,
            @JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListPrevWithFilter.Wo>> result = new ActionResult<>();
        try {
            result = new ActionQueryListPrevWithFilter().execute(request, id, count, jsonElement, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "필터 조건에 맞는 게시된 정보 내용 목록, 이전 페이지.", action = ActionQueryListPrevWithFilter.class)
    @POST
    @Path("filter/list/{id}/prev/{count}/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listPrevWithFilterMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("마지막 정보 ID, 첫 페이지면 (0)으로 대체 가능") @PathParam("id") String id,
            @JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListPrevWithFilter.Wo>> result = new ActionResult<>();
        try {
            result = new ActionQueryListPrevWithFilter().execute(request, id, count, jsonElement, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "필터 조건에 맞는 초안 정보 내용 목록, 다음 페이지.", action = ActionQueryListDraftNextWithFilter.class)
    @PUT
    @Path("draft/list/{id}/next/{count}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listDraftNextWithFilter(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("마지막 정보 ID, 첫 페이지면 (0)으로 대체 가능") @PathParam("id") String id,
            @JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListDraftNextWithFilter.Wo>> result = new ActionResult<>();
        try {
            result = new ActionQueryListDraftNextWithFilter().execute(request, id, count, jsonElement,
                    effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "필터 조건에 맞는 초안 정보 내용 목록, 다음 페이지.", action = ActionQueryListDraftNextWithFilter.class)
    @POST
    @Path("draft/list/{id}/next/{count}/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listDraftNextWithFilterMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("마지막 정보 ID, 첫 페이지면 (0)으로 대체 가능") @PathParam("id") String id,
            @JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListDraftNextWithFilter.Wo>> result = new ActionResult<>();
        try {
            result = new ActionQueryListDraftNextWithFilter().execute(request, id, count, jsonElement,
                    effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "정보 게시 문서 정보 객체 저장.", action = ActionPersistSaveDocument.class)
    @POST
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_save(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistSaveDocument.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistSaveDocument().execute(request, jsonElement, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, jsonElement);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "문서 좋아요.", action = ActionPersistCommend.class)
    @GET
    @Path("{id}/commend")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_commend(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("정보 문서ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistCommend.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistCommend().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "문서 좋아요 취소.", action = ActionPersistUnCommend.class)
    @GET
    @Path("{id}/uncommend")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_unCommend(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("정보 문서ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistUnCommend.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistUnCommend().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "문서 상단 고정.", action = ActionPersistTopDocument.class)
    @GET
    @Path("{id}/top")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_top(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("정보 문서ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistTopDocument.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistTopDocument().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "문서 상단 고정 취소.", action = ActionPersistUnTopDocument.class)
    @GET
    @Path("{id}/unTop")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_unTop(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("정보 문서ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistUnTopDocument.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistUnTopDocument().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "필터 조건에 맞는 게시된 정보 내용 페이지 조회.", action = ActionQueryListWithFilterPaging.class)
    @PUT
    @Path("filter/list/{page}/size/{size}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listWithFilterPaging(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("페이지") @PathParam("page") Integer page,
            @JaxrsParameterDescribe("수량") @PathParam("size") Integer size, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListWithFilterPaging.Wo>> result = new ActionResult<>();
        try {
            result = new ActionQueryListWithFilterPaging().execute(request, page, size, jsonElement,
                    effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "필터 조건에 맞는 게시된 정보 내용 페이지 조회.", action = ActionQueryListWithFilterPaging.class)
    @POST
    @Path("filter/list/{page}/size/{size}/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listWithFilterPagingMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("페이지") @PathParam("page") Integer page,
            @JaxrsParameterDescribe("수량") @PathParam("size") Integer size, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListWithFilterPaging.Wo>> result = new ActionResult<>();
        try {
            result = new ActionQueryListWithFilterPaging().execute(request, page, size, jsonElement,
                    effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "파일 접근 제어 정보 조회.", action = ActionQueryGetControl.class)
    @GET
    @Path("{id}/control")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_getControl(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("정보 문서ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryGetControl.Wo> result = new ActionResult<>();
        try {
            result = new ActionQueryGetControl().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "파일 공개 범위 내의 모든 사용자 조회.", action = ActionQueryListVisiblePersons.class)
    @GET
    @Path("{id}/persons")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_getVisiblePersons(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("정보 문서ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryListVisiblePersons.Wo> result = new ActionResult<>();
        try {
            result = new ActionQueryListVisiblePersons().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "현재 사용자의 문서 열람 권한 여부 조회.", action = ActionQueryPermissionReadDocument.class)
    @GET
    @Path("{id}/permission/read")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_PermissionReadDocument(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("정보 문서ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryPermissionReadDocument.Wo> result = new ActionResult<>();
        try {
            result = new ActionQueryPermissionReadDocument().execute(effectivePerson, id, null);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "문서 데이터 목록.", action = ActionQueryListDocumentData.class)
    @POST
    @Path("list/document/data")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listDocumentData(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListDocumentData.Wo>> result = new ActionResult<>();
        try {
            result = new ActionQueryListDocumentData().execute(request, effectivePerson, jsonElement);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, jsonElement);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "문서 목록.", action = ActionQueryListDocument.class)
    @POST
    @Path("list/document")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listDocument(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListDocument.Wo>> result = new ActionResult<>();
        try {
            result = new ActionQueryListDocument().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, jsonElement);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "문서 데이터 조회.", action = ActionQueryGetDocumentData.class)
    @GET
    @Path("{id}/document/data")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_getDocumentData(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("정보 문서ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryGetDocumentData.Wo> result = new ActionResult<>();
        try {
            result = new ActionQueryGetDocumentData().execute(request, effectivePerson, id);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "관리자용 필터 조건에 맞는 게시된 정보 내용 페이지 조회.", action = ActionQueryListWithFilterPagingAdmin.class)
    @POST
    @Path("filter/list/{page}/size/{size}/manager")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void managerQuery_listWithFilterPaging(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("페이지") @PathParam("page") Integer page,
            @JaxrsParameterDescribe("수량") @PathParam("size") Integer size, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListWithFilterPagingAdmin.Wo>> result = new ActionResult<>();
        try {
            result = new ActionQueryListWithFilterPagingAdmin().execute(request, page, size, jsonElement,
                    effectivePerson);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "게시된 문서 메시지 알림.", action = ActionNotify.class)
    @POST
    @Path("{id}/notify")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void publishNotify(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("문서ID") @PathParam("id") String id, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionNotify.Wo> result = new ActionResult<>();
        try {
            result = new ActionNotify().execute(effectivePerson, id, jsonElement);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, jsonElement);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "게시된 문서 html 텍스트를 webServer로 발송.", action = ActionPublishHtml.class)
    @POST
    @Path("{id}/publish/html")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void publishHtml(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("문서ID") @PathParam("id") String id, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPublishHtml.Wo> result = new ActionResult<>();
        try {
            result = new ActionPublishHtml().execute(effectivePerson, id, jsonElement);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, jsonElement);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "문서 정보 수정.", action = ActionUpdateDocument.class)
    @POST
    @Path("{id}/update")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateDocument(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("문서ID") @PathParam("id") String id, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionUpdateDocument.Wo> result = new ActionResult<>();
        try {
            result = new ActionUpdateDocument().execute(effectivePerson, id, jsonElement);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, jsonElement);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }
}
