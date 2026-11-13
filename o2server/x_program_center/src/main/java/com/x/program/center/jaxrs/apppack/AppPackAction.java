package com.x.program.center.jaxrs.apppack;

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

/**
 * Created by fancyLou on 6/11/21.
 * Copyright © 2021 O2. All rights reserved.
 */

@Path("apppack")
@JaxrsDescribe("모바일 클라이언트 온라인 패키징 서비스")
public class AppPackAction extends BaseAction  {

    private static Logger logger = LoggerFactory.getLogger(AppPackAction.class);

    @JaxrsMethodDescribe(value = "패키징 서버 점검.", action = ActionConnectPackServer.class)
    @GET
    @Path("server/connect")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void connect(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<ActionConnectPackServer.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionConnectPackServer().execute();
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "최근 패키징 정보 조회.", action = ActionPackInfo.class)
    @GET
    @Path("pack/info")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void packInfo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<ActionPackInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPackInfo().execute();
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "최근 패키징 logo 조회.", action = ActionPackInfoLogoDownload.class)
    @GET
    @Path("pack/info/logo")
    @Consumes(MediaType.APPLICATION_JSON)
    public void packInfoLogo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<ActionPackInfoLogoDownload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPackInfoLogoDownload().execute();
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "자료 제출, Android app 패키징 요청.", action = ActionAndroidPack.class)
    @POST
    @Path("pack/info/android/start")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    public void androidPackStart(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                                 @JaxrsParameterDescribe("appName") @FormDataParam("appName") String appName,
                                 @JaxrsParameterDescribe("o2ServerProtocol") @FormDataParam("o2ServerProtocol") String o2ServerProtocol,
                                 @JaxrsParameterDescribe("o2ServerHost") @FormDataParam("o2ServerHost") String o2ServerHost,
                                 @JaxrsParameterDescribe("o2ServerPort") @FormDataParam("o2ServerPort") String o2ServerPort,
                                 @JaxrsParameterDescribe("o2ServerContext") @FormDataParam("o2ServerContext") String o2ServerContext,
                                 @JaxrsParameterDescribe("isPackAppIdOuter") @FormDataParam("isPackAppIdOuter") String isPackAppIdOuter,
                                 @JaxrsParameterDescribe("화웨이 HMS 삭제 여부") @FormDataParam("deleteHuawei") String deleteHuawei,
                                 @JaxrsParameterDescribe("urlMapping") @FormDataParam("urlMapping") String urlMapping,
                                 @JaxrsParameterDescribe("버전 이름") @FormDataParam("appVersionName") String appVersionName,
                                 @JaxrsParameterDescribe("버전 번호") @FormDataParam("appBuildNo") String appBuildNo,
                           @JaxrsParameterDescribe("첨부파일 이름") @FormDataParam(FILENAME_FIELD) String fileName,
                           @JaxrsParameterDescribe("첨부파일 식별자") @FormDataParam(FILE_FIELD) final byte[] bytes,
                           @JaxrsParameterDescribe("파일 업로드") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition){
        ActionResult<ActionAndroidPack.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionAndroidPack().execute(appName, o2ServerProtocol, o2ServerHost, o2ServerPort, o2ServerContext, isPackAppIdOuter, urlMapping, appVersionName, appBuildNo, deleteHuawei, fileName, bytes, disposition);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }



    @JaxrsMethodDescribe(value = "기존 자료로 Android app 다시 패키징.", action = ActionAndroidRePack.class)
    @GET
    @Path("pack/info/android/repack")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void androidPackReStart(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<ActionAndroidRePack.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionAndroidRePack().execute();
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "app 패키지 다운로드", action = ActionPackFileDownload.class)
    @GET
    @Path("pack/info/file/download/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void download(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                         @JaxrsParameterDescribe("app 다운로드 식별자") @PathParam("id") String id) {
        ActionResult<ActionPackFileDownload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPackFileDownload().execute(effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }



    @JaxrsMethodDescribe(value = "최신 배포 app 다운로드 패키지 조회.", action = ActionLastPackFileInfo.class)
    @GET
    @Path("pack/info/file/last")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void androidPackLastAPk(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<ActionLastPackFileInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLastPackFileInfo().execute();
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    // 배포된 모든 패키지 삭제

    // app을 로컬 서버에 배포
    @JaxrsMethodDescribe(value = "apk 파일을 로컬 서버에 배포.", action = ActionPublishAPK2Local.class)
    @POST
    @Path("pack/info/file/publish")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void publishApk(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,  JsonElement jsonElement) {
        ActionResult<ActionPublishAPK2Local.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPublishAPK2Local().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }


}
