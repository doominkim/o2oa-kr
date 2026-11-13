package com.x.mind.assemble.control.jaxrs.mind;

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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("mind")
@JaxrsDescribe("마인드맵 정보 관리 서비스")
public class MindInfoAction extends BaseAction {

    // private StandardJaxrsActionProxy proxy = new
    // StandardJaxrsActionProxy(ThisApplication.context());
    private Logger logger = LoggerFactory.getLogger(MindInfoAction.class);

    @JaxrsMethodDescribe(value = "ID로 마인드맵 정보 조회", action = ActionMindGetWithId.class)
    @GET
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getMindBaseInfoWithId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("마인드맵 ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionMindGetWithId.Wo> result = new ActionResult<>();
        try {
            result = new ActionMindGetWithId().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result = new ActionResult<>();
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "ID로 마인드맵 썸네일 조회", action = ActionMindIconGetWithId.class)
    @GET
    @Path("{id}/icon")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getMindIconWithId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("마인드맵 ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionMindIconGetWithId.Wo> result = new ActionResult<>();
        try {
            result = new ActionMindIconGetWithId().execute(request, effectivePerson, id);
        } catch (Exception e) {
            result = new ActionResult<>();
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "마인드맵 ID로 모든 버전 정보 조회", action = ActionListVersionsWithMindId.class)
    @GET
    @Path("list/{id}/version")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listVersionsWithMindId(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("마인드맵 ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionListVersionsWithMindId.Wo>> result = new ActionResult<>();
        try {
            result = new ActionListVersionsWithMindId().execute(request, effectivePerson, id);
        } catch (Exception e) {
            result = new ActionResult<>();
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "마인드맵 ID로 모든 공유 정보 조회", action = ActionListShareRecordsWithMindId.class)
    @GET
    @Path("list/{id}/shareRecords")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listShareRecordsWithMindId(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("마인드맵 ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionListShareRecordsWithMindId.Wo>> result = new ActionResult<>();
        try {
            result = new ActionListShareRecordsWithMindId().execute(request, effectivePerson, id);
        } catch (Exception e) {
            result = new ActionResult<>();
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "필터 조건에 맞는 마인드맵 정보 내용 목록, 다음 페이지.", action = ActionMyShareMindNextWithFilter.class)
    @PUT
    @Path("filter/shared/{id}/next/{count}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listMySharedMindNextWithFilter(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("마지막 정보 ID, 첫 페이지면 (0)으로 대체 가능") @PathParam("id") String id,
            @JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionMyShareMindNextWithFilter.Wo>> result = new ActionResult<>();
        try {
            result = new ActionMyShareMindNextWithFilter().execute(request, effectivePerson, id, count, jsonElement);
        } catch (Exception e) {
            result = new ActionResult<>();
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "필터 조건에 맞는 마인드맵 정보 내용 목록, 다음 페이지.", action = ActionMyRecycleNextWithFilter.class)
    @PUT
    @Path("filter/recycle/{id}/next/{count}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listMyRecycleNextWithFilter(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("마지막 정보 ID, 첫 페이지면 (0)으로 대체 가능") @PathParam("id") String id,
            @JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionMyRecycleNextWithFilter.Wo>> result = new ActionResult<>();
        try {
            result = new ActionMyRecycleNextWithFilter().execute(request, effectivePerson, id, count, jsonElement);
        } catch (Exception e) {
            result = new ActionResult<>();
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "필터 조건에 맞는 마인드맵 정보 내용 목록, 다음 페이지.", action = ActionMyReciveMindNextWithFilter.class)
    @PUT
    @Path("filter/recived/{id}/next/{count}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listMyRecivedMindNextWithFilter(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("마지막 정보 ID, 첫 페이지면 (0)으로 대체 가능") @PathParam("id") String id,
            @JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionMyReciveMindNextWithFilter.Wo>> result = new ActionResult<>();
        try {
            result = new ActionMyReciveMindNextWithFilter().execute(request, effectivePerson, id, count, jsonElement);
        } catch (Exception e) {
            result = new ActionResult<>();
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "ID로 마인드맵 정보 조회(마인드맵 내용 포함)", action = ActionMindViewWithId.class)
    @GET
    @Path("view/{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void viewMindWithId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("마인드맵 ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionMindViewWithId.Wo> result = new ActionResult<>();
        try {
            result = new ActionMindViewWithId().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result = new ActionResult<>();
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "이전 버전 정보 ID로 마인드맵 정보 조회(마인드맵 내용 포함)", action = ActionMindVersionViewWithId.class)
    @GET
    @Path("version/{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void viewMindVersionWithId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("마인드맵 이전 버전 정보 ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionMindVersionViewWithId.Wo> result = new ActionResult<>();
        try {
            result = new ActionMindVersionViewWithId().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result = new ActionResult<>();
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "마인드맵 정보 저장(생성 또는 수정)", action = ActionMindSave.class)
    @POST
    @Path("save")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void saveMind(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionMindSave.Wo> result = new ActionResult<>();
        try {
            result = new ActionMindSave().execute(request, effectivePerson, jsonElement);
        } catch (Exception e) {
            result = new ActionResult<>();
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "게시판 아이콘 내용 업로드 또는 교체, 압축 크기 지정 가능	.", action = ActionMindIconUpdate.class)
    @POST
    @Path("{mindId}/icon/size/{size}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void changeMindIcon(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("마인드맵 정보 ID") @PathParam("mindId") String mindId,
            @JaxrsParameterDescribe("최대 너비") @PathParam("size") Integer size,
            @JaxrsParameterDescribe("파일 내용") @FormDataParam(FILE_FIELD) final byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionMindIconUpdate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionMindIconUpdate().execute(request, effectivePerson, mindId, size, bytes, disposition);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "필터 조건에 맞는 마인드맵 정보 내용 목록, 다음 페이지.", action = ActionMindListNextWithFilter.class)
    @PUT
    @Path("filter/list/{id}/next/{count}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listNextWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("마지막 정보 ID, 첫 페이지면 (0)으로 대체 가능") @PathParam("id") String id,
            @JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionMindListNextWithFilter.Wo>> result = new ActionResult<>();
        try {
            result = new ActionMindListNextWithFilter().execute(request, id, count, jsonElement, effectivePerson);
        } catch (Exception e) {
            result = new ActionResult<>();
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "ID로 휴지통에서 마인드맵 정보 복원", action = ActionMindRestore.class)
    @GET
    @Path("restore/{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void restoreMindWithId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("휴지통 마인드맵 ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionMindRestore.Wo> result = new ActionResult<>();
        try {
            result = new ActionMindRestore().execute(request, effectivePerson, id);
        } catch (Exception e) {
            result = new ActionResult<>();
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "지정 마인드맵을 다른 사용자 또는 조직에 공유", action = ActionMindShare.class)
    @PUT
    @Path("share/{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void shareMindWithId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("마인드맵 ID") @PathParam("id") String id, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionMindShare.Wo> result = new ActionResult<>();
        try {
            result = new ActionMindShare().execute(request, effectivePerson, id, jsonElement);
        } catch (Exception e) {
            result = new ActionResult<>();
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "공유 기록 ID로 마인드맵 공유 취소", action = ActionMindShareCancel.class)
    @PUT
    @Path("share/{shareId}/cancel")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void shareCancelWithId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("마인드맵 공유 정보 ID") @PathParam("shareId") String shareId) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionMindShareCancel.Wo> result = new ActionResult<>();
        try {
            result = new ActionMindShareCancel().execute(request, effectivePerson, shareId);
        } catch (Exception e) {
            result = new ActionResult<>();
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "마인드맵 파일 ID로 마인드맵 정보 삭제", action = ActionMindDestroyFromNormal.class)
    @DELETE
    @Path("{id}/destorymind")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void destoryFromNormal(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("마인드맵 ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionMindDestroyFromNormal.Wo> result = new ActionResult<>();
        try {
            result = new ActionMindDestroyFromNormal().execute(request, effectivePerson, id);
        } catch (Exception e) {
            result = new ActionResult<>();
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "휴지통에서 정보 ID로 마인드맵 정보 삭제", action = ActionMindDestroyFromRecycle.class)
    @DELETE
    @Path("{recycleId}/destoryrecycle")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void destoryFromRecycle(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("휴지통 마인드맵 ID") @PathParam("recycleId") String recycleId) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionMindDestroyFromRecycle.Wo> result = new ActionResult<>();
        try {
            result = new ActionMindDestroyFromRecycle().execute(request, effectivePerson, recycleId);
        } catch (Exception e) {
            result = new ActionResult<>();
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "지정 마인드맵을 휴지통으로 이동", action = ActionMindRecycle.class)
    @DELETE
    @Path("recycle/{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void recycleMindWithId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("마인드맵 ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionMindRecycle.Wo> result = new ActionResult<>();
        try {
            result = new ActionMindRecycle().execute(request, effectivePerson, id);
        } catch (Exception e) {
            result = new ActionResult<>();
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }
}