package com.x.cms.assemble.control.jaxrs.appinfo;

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
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("appinfo")
@JaxrsDescribe("정보 발행(CMS)-게시판(APPINFO) 관리 서비스")
public class AppInfoAction extends StandardJaxrsAction {

	private static  Logger logger = LoggerFactory.getLogger(AppInfoAction.class);

	@JaxrsMethodDescribe(value = "새 게시판 정보를 생성하거나 기존 게시판 정보를 갱신.", action = ActionSave.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void save( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionSave.Wo> result = new ActionResult<>();
		Boolean check = true;
		if (check) {
			try {
				result = new ActionSave().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAppInfoProcess(e, "게시판 정보를 저장하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "게시판 권한 설정.", action = ActionEditPermission.class)
	@POST
	@Path("{id}/permission")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updatePermission(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
								 @JaxrsParameterDescribe("식별자") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionEditPermission.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionEditPermission().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "ID로 지정한 게시판 정보 삭제(게시판에 분류 정보가 남아 있으면 삭제 불가).", action = ActionDelete.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("게시판 ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		try {
			result = new ActionDelete().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "ID로 CMS 앱 정보 객체를 삭제하는 중 알 수 없는 오류가 발생했습니다. ID:" + id);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "ID로 지정한 게시판 정보 삭제(게시판에 분류 정보가 남아 있으면 삭제 불가).", action = ActionDelete.class)
	@GET
	@Path("{id}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteMockDeleteToGet( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
						@JaxrsParameterDescribe("게시판 ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		try {
			result = new ActionDelete().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "ID로 CMS 앱 정보 객체를 삭제하는 중 알 수 없는 오류가 발생했습니다. ID:" + id);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "게시판 접근 제어 정보 조회.", action = ActionQueryGetControl.class)
	@GET
	@Path("{id}/control")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_getControl( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
								  @JaxrsParameterDescribe("게시판 ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionQueryGetControl.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = new ActionQueryGetControl().execute( request, id, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "게시판 ID로 지정한 게시판의 모든 정보 문서 삭제.", action = ActionEraseDocumentWithAppInfo.class)
	@DELETE
	@Path("erase/app/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void eraseWithAppId( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("게시판 ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionEraseDocumentWithAppInfo.Wo> result = new ActionResult<>();
		try {
			result = new ActionEraseDocumentWithAppInfo().execute(request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "게시판 ID로 모든 게시글을 삭제하는 중 알 수 없는 오류가 발생했습니다. ID:" + id);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "게시판 ID로 지정한 게시판의 모든 정보 문서 삭제.", action = ActionEraseDocumentWithAppInfo.class)
	@GET
	@Path("erase/app/{id}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void eraseWithAppIdMockDeleteToGet( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
								@JaxrsParameterDescribe("게시판 ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionEraseDocumentWithAppInfo.Wo> result = new ActionResult<>();
		try {
			result = new ActionEraseDocumentWithAppInfo().execute(request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "게시판 ID로 모든 게시글을 삭제하는 중 알 수 없는 오류가 발생했습니다. ID:" + id);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "식별자로 정보 게시판 정보 객체 조회.", action = ActionGet.class)
	@GET
	@Path("{flag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("게시판 ID") @PathParam("flag") String flag) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<BaseAction.Wo> result = new ActionResult<>();
		try {
			result = new ActionGet().execute( request, effectivePerson, flag );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "지정한 ID의 게시판 정보 객체를 조회하는 중 오류가 발생했습니다. flag:" + flag );
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "별칭으로 정보 게시판 정보 객체 조회.", action = ActionGetByAlias.class)
	@GET
	@Path("alias/{alias}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getByAlias( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("게시판 별칭") @PathParam("alias") String alias) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<BaseAction.Wo> result = new ActionResult<>();
		try {
			result = new ActionGetByAlias().execute(request, effectivePerson, alias);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "앱 고유 식별자로 게시판 정보 객체를 조회하는 중 오류가 발생했습니다. ALIAS:" + alias);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사용자가 열람 권한을 가진 모든 정보 게시판 정보 목록 조회.", action = ActionListWhatICanViewArticle_WithAppType.class)
	@GET
	@Path("list/user/view/article/type/{appType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWhatICanViewArticle_WithAppType( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("게시판 유형") @PathParam("appType") String appType ) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListWhatICanViewArticle_WithAppType.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWhatICanViewArticle_WithAppType().execute(request, effectivePerson, appType );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e,
					"권한에 따라 사용자가 접근할 수 있는 게시판 목록을 조회합니다. person:" + effectivePerson.getDistinguishedName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사용자가 열람 권한을 가진 모든 정보 게시판 정보 목록 조회.", action = ActionListWhatICanViewArticle.class)
	@GET
	@Path("list/user/view")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWhatICanView_Article( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListWhatICanViewArticle.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWhatICanViewArticle().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e,
					"권한에 따라 사용자가 접근할 수 있는 게시판 목록을 조회합니다. person:" + effectivePerson.getDistinguishedName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사용자가 열람 권한을 가진 모든 정보 게시판 정보 목록 조회.", action = ActionListWhatICanViewData.class)
	@GET
	@Path("list/user/view/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWhatICanView_Data( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListWhatICanViewData.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWhatICanViewData().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e,
					"권한에 따라 사용자가 접근할 수 있는 데이터 게시판 목록을 조회합니다. person:" + effectivePerson.getDistinguishedName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사용자가 열람 권한을 가진 모든 정보 게시판 정보 목록 조회.", action = ActionListWhatICanViewData_WithAppType.class)
	@GET
	@Path("list/user/view/data/type/{appType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWhatICanViewData_WithAppType( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("게시판 유형") @PathParam("appType") String appType ) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListWhatICanViewData.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWhatICanViewData_WithAppType().execute(request, effectivePerson, appType );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e,
					"권한에 따라 사용자가 접근할 수 있는 데이터 게시판 목록을 조회합니다. person:" + effectivePerson.getDistinguishedName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사용자가 열람 권한을 가진 모든 게시판 정보 목록 조회.", action = ActionListWhatICanViewAllDocType_WithAppType.class)
	@GET
	@Path("list/user/view/all/type/{appType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWhatICanViewAllType_WithAppType( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("게시판 유형") @PathParam("appType") String appType ) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListWhatICanViewAllDocType.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWhatICanViewAllDocType_WithAppType().execute( request, effectivePerson, appType );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e,
					"권한에 따라 사용자가 접근할 수 있는 데이터 게시판 목록을 조회합니다. person:" + effectivePerson.getDistinguishedName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사용자가 열람 권한을 가진 모든 게시판 정보 목록 조회.", action = ActionListWhatICanViewAllDocType.class)
	@GET
	@Path("list/user/view/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWhatICanView_AllType( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListWhatICanViewAllDocType.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWhatICanViewAllDocType().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e,
					"권한에 따라 사용자가 접근할 수 있는 데이터 게시판 목록을 조회합니다. person:" + effectivePerson.getDistinguishedName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사용자가 발행 권한을 가진 모든 정보 게시판 정보 목록 조회.", action = ActionListWhatICanPublish.class)
	@GET
	@Path("list/user/publish")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWhatICanPublish( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<BaseAction.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWhatICanPublish().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e,
					"사용자 권한으로 볼 수 있는 전체 게시판 정보를 조회하는 중 오류가 발생했습니다. Name:" + effectivePerson.getDistinguishedName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사용자가 발행 권한(프로세스 연결 분류)을 가진 모든 정보 게시판 정보 목록 조회.", action = ActionListPublishWithProcess.class)
	@GET
	@Path("list/user/publish/with/process")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPublishWithProcess( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<BaseAction.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListPublishWithProcess().execute(effectivePerson);
		} catch (Exception e) {
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사용자가 발행 권한을 가진 모든 정보 게시판 정보 목록 조회.", action = ActionListWhatICanPublish_WithAppType.class)
	@GET
	@Path("list/user/publish/type/{appType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWhatICanPublish_WithAppType( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("게시판 유형") @PathParam("appType") String appType ) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<BaseAction.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWhatICanPublish_WithAppType().execute(request, effectivePerson, appType );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e,
					"사용자 권한으로 볼 수 있는 전체 게시판 정보를 조회하는 중 오류가 발생했습니다. Name:" + effectivePerson.getDistinguishedName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사용자가 발행 권한을 가진 모든 정보 게시판 정보 목록 조회.", action = ActionGetPublishableAppInfo.class)
	@GET
	@Path("get/user/publish/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getPublishableAppInfo( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("게시판 ID") @PathParam("appId") String appId ) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<BaseAction.Wo> result = new ActionResult<>();
		try {
			result = new ActionGetPublishableAppInfo().execute(request, effectivePerson, appId );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e,
					"사용자 권한으로 볼 수 있는 게시판 정보를 조회하는 중 오류가 발생했습니다. Name:" + effectivePerson.getDistinguishedName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "모든 게시판 분류 정보 목록 조회.", action = ActionListAllAppType.class)
	@GET
	@Path("list/appType")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listAllAppType( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request ) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListAllAppType.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListAllAppType().execute(request, effectivePerson);
		} catch (Exception e) {
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "권한 있는 문서가 존재하는 게시판의 분류 목록 조회.", action = ActionListHasDocumentAppType.class)
	@GET
	@Path("list/has/document/appType")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listHasDocumentAppType( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request ) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListHasDocumentAppType.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListHasDocumentAppType().execute(request, effectivePerson);
		} catch (Exception e) {
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "관리 가능한 모든 게시판 분류 정보 목록 조회.", action = ActionListAllManageableAppType.class)
	@GET
	@Path("list/appType/manager")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listManageableAllAppType( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request ) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListAllManageableAppType.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListAllManageableAppType().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e,
					"전체 게시판 분류 목록을 가져오는 중 오류가 발생했습니다. Name:" + effectivePerson.getDistinguishedName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사용자가 관리 권한을 가진 모든 정보 게시판 정보 목록 조회.", action = ActionListWhatICanManage.class)
	@GET
	@Path("list/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWhatICanManage( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<BaseAction.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWhatICanManage().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e,
					"사용자 권한으로 관리하는 전체 게시판 정보를 조회하는 중 오류가 발생했습니다. Name:" + effectivePerson.getDistinguishedName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "게시판 유형 이름으로 사용자가 관리 권한을 가진 모든 정보 게시판 정보 목록 조회.", action = ActionListWhatICanManage_WithAppType.class)
	@GET
	@Path("list/manage/type/{appType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWhatICanManage_WithAppType( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("게시판 유형") @PathParam("appType") String appType ) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<BaseAction.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWhatICanManage_WithAppType().execute(request, effectivePerson, appType);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e,
					"게시판 유형 이름으로 사용자가 관리할 수 있는 게시판 목록을 가져오는 중 오류가 발생했습니다. Name:" + effectivePerson.getDistinguishedName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사용자가 접근 권한을 가진 모든 정보 게시판 정보 목록 조회.", action = ActionListAll.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listAllAppInfo( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<BaseAction.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListAll().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "전체 게시판 정보 객체를 조회하는 중 오류가 발생했습니다");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "문서 열람 권한이 있는 게시판 정보 목록 조회.", action = ActionListHasDocument.class)
	@GET
	@Path("list/has/document")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listHasDocument( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<BaseAction.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListHasDocument().execute(request, effectivePerson);
		} catch (Exception e) {
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "문서 열람 권한이 있고 분류를 지정한 게시판 목록 조회.", action = ActionListHasDocument_WithAppType.class)
	@GET
	@Path("list/has/document/type/{appType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listHasDocument_WithAppType( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
											 @JaxrsParameterDescribe("게시판 유형") @PathParam("appType") String appType) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<BaseAction.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListHasDocument_WithAppType().execute(request, effectivePerson, appType);
		} catch (Exception e) {
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건에 따른 정보 게시판 정보 목록, 다음 페이지.", action = ActionListNextWithFilter.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNextWithFilter( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("마지막 정보 ID, 첫 페이지면 (0)으로 대체 가능") @PathParam("id") String id,
			@JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListNextWithFilter.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListNextWithFilter().execute(request, effectivePerson, id, count, jsonElement );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "게시판 정보 객체를 조회하는 중 오류가 발생했습니다");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건에 따른 정보 게시판 정보 목록, 다음 페이지.", action = ActionListNextWithFilter.class)
	@POST
	@Path("filter/list/{id}/next/{count}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNextWithFilterMockPutToPost( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
									@JaxrsParameterDescribe("마지막 정보 ID, 첫 페이지면 (0)으로 대체 가능") @PathParam("id") String id,
									@JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count,
									JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListNextWithFilter.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListNextWithFilter().execute(request, effectivePerson, id, count, jsonElement );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "게시판 정보 객체를 조회하는 중 오류가 발생했습니다");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건에 따른 정보 게시판 정보 목록, 이전 페이지.", action = ActionListPrevWithFilter.class)
	@PUT
	@Path("filter/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPrevWithFilter( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("마지막 정보 ID, 첫 페이지면 (0)으로 대체 가능") @PathParam("id") String id,
			@JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListPrevWithFilter.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListPrevWithFilter().execute(request, effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "게시판 정보 객체를 조회하는 중 오류가 발생했습니다");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건에 따른 정보 게시판 정보 목록, 이전 페이지.", action = ActionListPrevWithFilter.class)
	@POST
	@Path("filter/list/{id}/prev/{count}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPrevWithFilterMockPutToPost( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
									@JaxrsParameterDescribe("마지막 정보 ID, 첫 페이지면 (0)으로 대체 가능") @PathParam("id") String id,
									@JaxrsParameterDescribe("페이지당 표시 항목 수") @PathParam("count") Integer count,
									JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListPrevWithFilter.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListPrevWithFilter().execute(request, effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "게시판 정보 객체를 조회하는 중 오류가 발생했습니다");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "게시판 아이콘 내용 업로드 또는 교체, 압축 크기 지정 가능	.", action = ActionAppIconUpload.class)
	@POST
	@Path("{appId}/icon/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void changeIcon(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("게시판 ID") @PathParam("appId") String appId,
			@JaxrsParameterDescribe("최대 너비") @PathParam("size") Integer size,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionAppIconUpload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionAppIconUpload().execute(effectivePerson, appId, size, bytes);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
