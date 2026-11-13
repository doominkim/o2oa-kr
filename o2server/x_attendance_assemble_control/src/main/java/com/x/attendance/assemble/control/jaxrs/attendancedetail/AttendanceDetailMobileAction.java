package com.x.attendance.assemble.control.jaxrs.attendancedetail;

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

@Path("attendancedetail/mobile")
@JaxrsDescribe("모바일 근태 출퇴근 정보 관리 서비스(사용 중단)")
public class AttendanceDetailMobileAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(AttendanceDetailMobileAction.class);

	@JaxrsMethodDescribe(value = "ID로 모바일 출퇴근 정보 기록 조회", action = ActionGetMobile.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("모바일 근태 출퇴근 정보 ID") @PathParam("id") String id) {
		ActionResult<ActionGetMobile.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionGetMobile().execute(request, effectivePerson, id);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceDetailProcess(e, "ID로 정보를 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "로그인 사용자의 당일 모든 모바일 출퇴근 정보 기록 조회", action = ActionListMyMobileRecordToday.class)
	@Path("my")
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyRecords(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request ) {
		ActionResult<ActionListMyMobileRecordToday.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListMyMobileRecordToday().execute( request, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				// Exception exception = new ExceptionAttendanceDetailProcess(e, "로그인 사용자의 당일 모든 모바일 출퇴근 정보 기록 조회 중 예외가 발생했습니다!");
				result.error(e);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "모바일 출퇴근 정보 기록 상세 조회", action = ActionListMobileWithFilter.class)
	@Path("filter/list/page/{page}/count/{count}")
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listDataForMobile(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("표시할 페이지 번호") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("페이지당 표시할 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionListMobileWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListMobileWithFilter().execute(request, effectivePerson, page, count, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceDetailProcess(e, "ID로 정보를 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	/**
	 * 출퇴근 정보 수신, 모바일 특성상 여러 번·부분적으로 수신되어 한 번의 정보가 불완전함. 수신 후 저장만 하고 정기 에이전트가 주기적으로 데이터 통합·적재 및 분석 1-직원 이름
	 * EmployeeName 2-사번 EmployeeNo 3-날짜 RecordDateString 4-기록 시간 SignTime 6-기록 위치
	 * 7-기록 좌표
	 * 
	 * @author liyi_
	 */
	@JaxrsMethodDescribe(value = "모바일 출퇴근 정보 수신, 모바일 특성상 여러 번·부분적으로 수신되어 한 번의 정보가 불완전함, 수신 후 바로 분석하지 않음", action = ActionReciveAttendanceMobile.class)
	@Path("recive")
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void reciveForMobile(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionReciveAttendanceMobile.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			try {
				result = new ActionReciveAttendanceMobile().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				// Exception exception = new ExceptionAttendanceDetailProcess(e, "출퇴근 기록 정보 수신 중 예외가 발생했습니다!");
				result.error(e);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "ID로 모바일 출퇴근 정보 삭제", action = ActionDeleteMobile.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("모바일 근태 출퇴근 정보 ID") @PathParam("id") String id) {
		ActionResult<ActionDeleteMobile.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			try {
				result = new ActionDeleteMobile().execute(request, effectivePerson, id);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceDetailProcess(e, "ID로 퇴근 체크 정보를 삭제하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "사전 기록: 현재 사용자와 해당 근무 배정 설정으로 기록 결과 사전 판단", action = ActionMobilePreview.class)
	@Path("mobilepreview")
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void mobilePreview(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request ) {
		ActionResult<ActionMobilePreview.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionMobilePreview().execute( request, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceDetailProcess(e, "로그인 사용자의 당일 모바일 체크 기록을 조회하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}