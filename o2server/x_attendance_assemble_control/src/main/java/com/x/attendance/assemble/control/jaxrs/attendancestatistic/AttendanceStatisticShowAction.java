package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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

@Path("statisticshow")
@JaxrsDescribe("근태 데이터 통계 정보 관리 서비스(사용 중단)")
public class AttendanceStatisticShowAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(AttendanceStatisticShowAction.class);

	@JaxrsMethodDescribe(value = "직원의 지정 월 통계 데이터 조회", action = ActionShowStmForPerson.class)
	@GET
	@Path("person/{name}/{year}/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void showStmForPerson(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("통계 직원 이름") @PathParam("name") String name,
			@JaxrsParameterDescribe("통계 주기 연도") @PathParam("year") String year,
			@JaxrsParameterDescribe("통계 주기 월") @PathParam("month") String month) {
		ActionResult<List<ActionShowStmForPerson.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionShowStmForPerson().execute(request, effectivePerson, name, year, month);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "지정 조직 전체 직원의 지정 월 통계 데이터 조회", action = ActionShowStForPersonInUnit.class)
	@GET
	@Path("persons/unit/{name}/{year}/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void showStForPersonInUnit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("통계 직원 이름") @PathParam("name") String name,
			@JaxrsParameterDescribe("통계 주기 연도") @PathParam("year") String year,
			@JaxrsParameterDescribe("통계 주기 월") @PathParam("month") String month) {
		ActionResult<List<ActionShowStForPersonInUnit.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionShowStForPersonInUnit().execute(request, effectivePerson, name, year, month);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "지정 조직 전체 직원의 지정 월 통계 데이터 조회(하위 조직 포함)", action = ActionShowStForPersonInUnitSubNested.class)
	@GET
	@Path("persons/unit/subnested/{name}/{year}/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void showStForPersonInUnitSubNested(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("통계 직원 이름") @PathParam("name") String name,
			@JaxrsParameterDescribe("통계 주기 연도") @PathParam("year") String year,
			@JaxrsParameterDescribe("통계 주기 월") @PathParam("month") String month) {
		ActionResult<List<ActionShowStForPersonInUnitSubNested.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionShowStForPersonInUnitSubNested().execute(request, effectivePerson, name, year,
						month);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "조직의 지정 월 통계 데이터 조회", action = ActionShowStmForUnitSubnested.class)
	@GET
	@Path("unit/subnested/{name}/{year}/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void showStmForUnitSubnested(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("통계 조직명") @PathParam("name") String name,
			@JaxrsParameterDescribe("통계 주기 연도") @PathParam("year") String year,
			@JaxrsParameterDescribe("통계 주기 월") @PathParam("month") String month) {
		ActionResult<List<ActionShowStmForUnitSubnested.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionShowStmForUnitSubnested().execute(request, effectivePerson, name, year, month);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "최상위 조직의 지정 월 통계 데이터 조회", action = ActionShowStForUnitInTopUnit.class)
	@GET
	@Path("unit/topUnit/{name}/{year}/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void showStForUnitInTopUnit(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("통계 최상위 조직명") @PathParam("name") String name,
			@JaxrsParameterDescribe("통계 주기 연도") @PathParam("year") String year,
			@JaxrsParameterDescribe("통계 주기 월") @PathParam("month") String month) {
		ActionResult<List<ActionShowStForUnitInTopUnit.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionShowStForUnitInTopUnit().execute(request, effectivePerson, name, year, month);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "조직의 지정 월 통계 데이터 조회", action = ActionShowStmForUnit.class)
	@GET
	@Path("unit/{name}/{year}/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void showStmForUnit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("통계 조직명") @PathParam("name") String name,
			@JaxrsParameterDescribe("통계 주기 연도") @PathParam("year") String year,
			@JaxrsParameterDescribe("통계 주기 월") @PathParam("month") String month) {
		ActionResult<List<ActionShowStmForUnit.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionShowStmForUnit().execute(request, effectivePerson, name, year, month);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "조직의 지정 월 통계 데이터 조회", action = ActionSumStForUnit.class)
	@GET
	@Path("unit/sum/{name}/{year}/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void sumUnitStatistic(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("통계 조직명") @PathParam("name") String name,
			@JaxrsParameterDescribe("통계 주기 연도") @PathParam("year") String year,
			@JaxrsParameterDescribe("통계 주기 월") @PathParam("month") String month) {
		ActionResult<ActionSumStForUnit.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionSumStForUnit().execute(request, effectivePerson, name, year, month);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "최상위 조직의 지정 월 통계 데이터 조회", action = ActionShowStmForTopUnit.class)
	@GET
	@Path("topUnit/{name}/{year}/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void showStmForTopUnit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("통계 최상위 조직명") @PathParam("name") String name,
			@JaxrsParameterDescribe("통계 주기 연도") @PathParam("year") String year,
			@JaxrsParameterDescribe("통계 주기 월") @PathParam("month") String month) {
		ActionResult<List<ActionShowStmForTopUnit.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionShowStmForTopUnit().execute(request, effectivePerson, name, year, month);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "조직의 지정 월 일별 통계 데이터 조회", action = ActionShowStmForTopUnit.class)
	@GET
	@Path("unit/day/{name}/{year}/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void showStdForUnit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("통계 조직명") @PathParam("name") String name,
			@JaxrsParameterDescribe("통계 주기 연도") @PathParam("year") String year,
			@JaxrsParameterDescribe("통계 주기 월") @PathParam("month") String month) {
		ActionResult<List<ActionShowStdForUnit.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionShowStdForUnit().execute(request, effectivePerson, name, year, month);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "최상위 조직의 지정 월 일별 통계 데이터 조회", action = ActionShowStdForTopUnit.class)
	@GET
	@Path("topUnit/day/{name}/{year}/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void showStdForTopUnit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("통계 최상위 조직명") @PathParam("name") String name,
			@JaxrsParameterDescribe("통계 주기 연도") @PathParam("year") String year,
			@JaxrsParameterDescribe("통계 주기 월") @PathParam("month") String month) {
		ActionResult<List<ActionShowStdForTopUnit.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionShowStdForTopUnit().execute(request, effectivePerson, name, year, month);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "조직의 지정 날짜 통계 데이터 조회", action = ActionShowStdForUnitWithDate.class)
	@GET
	@Path("unit/day/{name}/{date}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void showStdForUnitWithDate(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("통계 조직명") @PathParam("name") String name,
			@JaxrsParameterDescribe("통계 날짜") @PathParam("date") String date) {
		ActionResult<List<ActionShowStdForUnitWithDate.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionShowStdForUnitWithDate().execute(request, effectivePerson, name, date);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "조직의 지정 날짜 통계 데이터 조회", action = ActionShowStdForUnitInTopUnitWithDate.class)
	@GET
	@Path("unit/day/topUnit/{name}/{date}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void showStdForUnitInTopUnitWithDate(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("통계 조직명") @PathParam("name") String name,
			@JaxrsParameterDescribe("통계 날짜") @PathParam("date") String date) {
		ActionResult<List<ActionShowStdForUnitInTopUnitWithDate.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionShowStdForUnitInTopUnitWithDate().execute(request, effectivePerson, name, date);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건에 따른 개인 월별 통계 데이터 목록, 다음 페이지", action = ActionListStmForPersonNextWithFilter.class)
	@PUT
	@Path("filter/personMonth/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listStmForPersonNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("이전 페이지 마지막 정보 ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("페이지당 표시 정보 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionListStmForPersonNextWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListStmForPersonNextWithFilter().execute(request, effectivePerson, id, count,
						jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건에 따른 개인 월별 통계 데이터 목록, 이전 페이지", action = ActionListStmForPersonPrevWithFilter.class)
	@PUT
	@Path("filter/personMonth/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listStmForPersonPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("이전 페이지 마지막 정보 ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("페이지당 표시 정보 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionListStmForPersonPrevWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListStmForPersonPrevWithFilter().execute(request, effectivePerson, id, count,
						jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건에 따른 조직 월별 통계 데이터 목록, 다음 페이지", action = ActionListStmForUnitNextWithFilter.class)
	@PUT
	@Path("filter/unitMonth/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listStmForUnitNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("이전 페이지 마지막 정보 ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("페이지당 표시 정보 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionListStmForUnitNextWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListStmForUnitNextWithFilter().execute(request, effectivePerson, id, count,
						jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건에 따른 조직 월별 통계 데이터 목록, 이전 페이지", action = ActionListStmForUnitPrevWithFilter.class)
	@PUT
	@Path("filter/unitMonth/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listStmForUnitPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("이전 페이지 마지막 정보 ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("페이지당 표시 정보 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionListStmForUnitPrevWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListStmForUnitPrevWithFilter().execute(request, effectivePerson, id, count,
						jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건에 따른 최상위 조직 월별 통계 데이터 목록, 다음 페이지", action = ActionListStmForTopUnitNextWithFilter.class)
	@PUT
	@Path("filter/topUnitMonth/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listStmForTopUnitNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("이전 페이지 마지막 정보 ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("페이지당 표시 정보 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionListStmForTopUnitNextWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListStmForTopUnitNextWithFilter().execute(request, effectivePerson, id, count,
						jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건에 따른 최상위 조직 월별 통계 데이터 목록, 이전 페이지", action = ActionListStmForTopUnitPrevWithFilter.class)
	@PUT
	@Path("filter/topUnitMonth/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listStmForTopUnitPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("이전 페이지 마지막 정보 ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("페이지당 표시 정보 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionListStmForTopUnitPrevWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListStmForTopUnitPrevWithFilter().execute(request, effectivePerson, id, count,
						jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건에 따른 조직 일별 통계 데이터 목록, 다음 페이지", action = ActionListStdForUnitNextWithFilter.class)
	@PUT
	@Path("filter/unitDay/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listStdForUnitNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("이전 페이지 마지막 정보 ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("페이지당 표시 정보 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionListStdForUnitNextWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListStdForUnitNextWithFilter().execute(request, effectivePerson, id, count,
						jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건에 따른 조직 일별 통계 데이터 목록, 이전 페이지", action = ActionListStdForUnitPrevWithFilter.class)
	@PUT
	@Path("filter/unitDay/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listStdForUnitPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("이전 페이지 마지막 정보 ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("페이지당 표시 정보 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionListStdForUnitPrevWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListStdForUnitPrevWithFilter().execute(request, effectivePerson, id, count,
						jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건에 따른 최상위 조직 일별 통계 데이터 목록, 다음 페이지", action = ActionListStdForTopUnitNextWithFilter.class)
	@PUT
	@Path("filter/topUnitDay/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listStdForTopUnitNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("이전 페이지 마지막 정보 ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("페이지당 표시 정보 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionListStdForTopUnitNextWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListStdForTopUnitNextWithFilter().execute(request, effectivePerson, id, count,
						jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "필터 조건에 따른 최상위 조직 일별 통계 데이터 목록, 이전 페이지", action = ActionListStdForTopUnitPrevWithFilter.class)
	@PUT
	@Path("filter/topUnitDay/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listStdForTopUnitPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("이전 페이지 마지막 정보 ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("페이지당 표시 정보 항목 수") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionListStdForTopUnitPrevWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionListStdForTopUnitPrevWithFilter().execute(request, effectivePerson, id, count,
						jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "전체 데이터 통계 목록을 가져오는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}