package com.x.processplatform.assemble.surface.jaxrs.snap;

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

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "SnapAction", description = "작업 스냅샷 인터페이스.")
@Path("snap")
@JaxrsDescribe("작업 스냅샷 인터페이스.")
public class SnapAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(SnapAction.class);

    @JaxrsMethodDescribe(value = "업무 문서 스냅샷.", action = ActionTypeSnap.class)
    @GET
    @Path("work/{workId}/type/snap")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void typeSnap(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId) {
        ActionResult<ActionTypeSnap.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionTypeSnap().execute(effectivePerson, workId);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "업무 문서를 스냅샷하고 폐기로 표시.", action = ActionTypeAbandoned.class)
    @GET
    @Path("work/{workId}/type/abandoned")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void typeAbandoned(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId) {
        ActionResult<ActionTypeAbandoned.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionTypeAbandoned().execute(effectivePerson, workId);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "업무 문서 보류", action = ActionTypeSuspend.class)
    @GET
    @Path("work/{workId}/type/suspend")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void typeSuspend(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workId") String workId) {
        ActionResult<ActionTypeSuspend.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionTypeSuspend().execute(effectivePerson, workId);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "완료된 업무 문서 스냅샷.", action = ActionTypeSnapWorkCompleted.class)
    @GET
    @Path("workcompleted/{workCompletedId}/type/snapworkcompleted")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void typeSnapWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workCompletedId") String workCompletedId) {
        ActionResult<ActionTypeSnapWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionTypeSnapWorkCompleted().execute(effectivePerson, workCompletedId);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "완료된 업무 문서를 스냅샷하고 폐기로 표시.", action = ActionTypeAbandonedWorkCompleted.class)
    @GET
    @Path("workcompleted/{workCompletedId}/type/abandonedworkcompleted")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void typeAbandonedWorkCompleted(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("업무 문서 식별자") @PathParam("workCompletedId") String workCompletedId) {
        ActionResult<ActionTypeAbandonedWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionTypeAbandonedWorkCompleted().execute(effectivePerson, workCompletedId);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "스냅샷 객체 조회.", action = ActionGet.class)
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

    @JaxrsMethodDescribe(value = "스냅샷 삭제", action = ActionDelete.class)
    @DELETE
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("식별자") @PathParam("id") String id) {
        ActionResult<ActionDelete.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDelete().execute(effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "스냅샷 삭제", action = ActionDelete.class)
    @GET
    @Path("{id}/mockdeletetoget")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteMockGet2Delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("식별자") @PathParam("id") String id) {
        ActionResult<ActionDelete.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDelete().execute(effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "스냅샷 복원", action = ActionRestore.class)
    @GET
    @Path("{id}/restore")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void restore(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("식별자") @PathParam("id") String id) {
        ActionResult<ActionRestore.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionRestore().execute(effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "현재 사용자의 스냅샷 목록, 페이지.", action = ActionListMyPaging.class)
    @GET
    @Path("list/my/paging/{page}/size/{size}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listMyPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("페이지") @PathParam("page") Integer page,
            @JaxrsParameterDescribe("수량") @PathParam("size") Integer size) {
        ActionResult<List<ActionListMyPaging.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListMyPaging().execute(effectivePerson, page, size);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "조건에 따라 현재 사용자 스냅샷 페이지 표시.", action = ActionListMyFilterPaging.class)
    @POST
    @Path("list/my/filter/{page}/size/{size}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listMyFilterPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("페이지") @PathParam("page") Integer page,
            @JaxrsParameterDescribe("수량") @PathParam("size") Integer size, JsonElement jsonElement) {
        ActionResult<List<ActionListMyFilterPaging.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListMyFilterPaging().execute(effectivePerson, page, size, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "현재 사용자가 생성한 스냅샷 객체 목록, 다음 페이지.", action = ActionListNext.class)
    @GET
    @Path("list/{id}/next/{count}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listNext(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("수량") @PathParam("count") Integer count) {
        ActionResult<List<ActionListNext.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListNext().execute(effectivePerson, id, count);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "현재 사용자가 생성한 스냅샷 객체 목록, 이전 페이지.", action = ActionListPrev.class)
    @GET
    @Path("list/{id}/prev/{count}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listPrev(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("수량") @PathParam("count") Integer count) {
        ActionResult<List<ActionListPrev.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListPrev().execute(effectivePerson, id, count);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "지정 앱의 현재 사용자 스냅샷 객체 목록, 다음 페이지.", action = ActionListNextWithApplication.class)
    @GET
    @Path("list/{id}/next/{count}/application/{applicationFlag}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listNextWithApplication(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("수량") @PathParam("count") Integer count,
            @JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag) {
        ActionResult<List<ActionListNextWithApplication.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListNextWithApplication().execute(effectivePerson, id, count, applicationFlag);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "지정 앱의 현재 사용자 스냅샷 객체 목록, 이전 페이지.", action = ActionListPrevWithApplication.class)
    @GET
    @Path("list/{id}/prev/{count}/application/{applicationFlag}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listPrevWithApplication(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("수량") @PathParam("count") Integer count,
            @JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag) {
        ActionResult<List<ActionListPrevWithApplication.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListPrevWithApplication().execute(effectivePerson, id, count, applicationFlag);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "지정 프로세스의 현재 사용자 스냅샷 객체 목록, 다음 페이지.", action = ActionListNextWithProcess.class)
    @GET
    @Path("list/{id}/next/{count}/process/{processFlag}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listNextWithProcess(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("수량") @PathParam("count") Integer count,
            @JaxrsParameterDescribe("프로세스 식별자") @PathParam("processFlag") String processFlag) {
        ActionResult<List<ActionListNextWithProcess.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListNextWithProcess().execute(effectivePerson, id, count, processFlag);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "지정 프로세스의 현재 사용자 스냅샷 객체 목록, 이전 페이지.", action = ActionListPrevWithProcess.class)
    @GET
    @Path("list/{id}/prev/{count}/process/{processFlag}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listPrevWithProcess(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("수량") @PathParam("count") Integer count,
            @JaxrsParameterDescribe("프로세스 식별자") @PathParam("processFlag") String processFlag) {
        ActionResult<List<ActionListPrevWithProcess.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListPrevWithProcess().execute(effectivePerson, id, count, processFlag);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }

        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "스냅샷 내보내기.", action = ActionDownload.class)
    @GET
    @Path("{id}/download")
    @Consumes(MediaType.APPLICATION_JSON)
    public void download(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("식별자") @PathParam("id") String id) {
        ActionResult<ActionDownload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownload().execute(effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "스냅샷 가져오기.", action = ActionUpload.class)
    @POST
    @Path("upload")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void upload(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("파일 내용") @FormDataParam(FILE_FIELD) byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUpload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUpload().execute(effectivePerson, bytes);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "생성된 모든 스냅샷 객체 목록, 다음 페이지.", action = ActionManageListNext.class)
    @GET
    @Path("list/{id}/next/{count}/manage")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void manageListNext(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("수량") @PathParam("count") Integer count) {
        ActionResult<List<ActionManageListNext.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionManageListNext().execute(effectivePerson, id, count);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "생성된 모든 스냅샷 객체 목록, 이전 페이지.", action = ActionManageListPrev.class)
    @GET
    @Path("list/{id}/prev/{count}/manage")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void manageListPrev(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("수량") @PathParam("count") Integer count) {
        ActionResult<List<ActionManageListPrev.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionManageListPrev().execute(effectivePerson, id, count);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "지정 조건의 스냅샷 조회, 다음 페이지.", action = ActionManageListNextFilter.class)
    @POST
    @Path("list/{id}/next/{count}/filter/manage")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void manageListNextWithFilter(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("수량") @PathParam("count") Integer count, JsonElement jsonElement) {
        ActionResult<List<ActionManageListNextFilter.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionManageListNextFilter().execute(effectivePerson, id, count, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "지정 조건의 스냅샷 조회, 이전 페이지.", action = ActionManageListPrevFilter.class)
    @POST
    @Path("list/{id}/prev/{count}/filter/manage")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void manageListPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("식별자") @PathParam("id") String id,
            @JaxrsParameterDescribe("수량") @PathParam("count") Integer count, JsonElement jsonElement) {
        ActionResult<List<ActionManageListPrevFilter.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionManageListPrevFilter().execute(effectivePerson, id, count, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "조건에 따라 스냅샷 페이지 표시.", action = ActionManageListFilterPaging.class)
    @POST
    @Path("list/filter/{page}/size/{size}/manage")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void manageListFilterPaging(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("페이지") @PathParam("page") Integer page,
            @JaxrsParameterDescribe("수량") @PathParam("size") Integer size, JsonElement jsonElement) {
        ActionResult<List<ActionManageListFilterPaging.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionManageListFilterPaging().execute(effectivePerson, page, size, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "지정 앱에서 필터 조건에 따른 snap 페이지 목록.", action = ActionManageListWithApplicationPaging.class)
    @POST
    @Path("list/paging/{page}/size/{size}/application/{applicationFlag}/filter/manage")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void manageListWithApplicationPaging(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("페이지") @PathParam("page") Integer page,
            @JaxrsParameterDescribe("페이지당 개수") @PathParam("size") Integer size,
            @JaxrsParameterDescribe("앱 식별자") @PathParam("applicationFlag") String applicationFlag,
            JsonElement jsonElement) {
        ActionResult<List<ActionManageListWithApplicationPaging.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionManageListWithApplicationPaging().execute(effectivePerson, page, size, applicationFlag,
                    jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

}
