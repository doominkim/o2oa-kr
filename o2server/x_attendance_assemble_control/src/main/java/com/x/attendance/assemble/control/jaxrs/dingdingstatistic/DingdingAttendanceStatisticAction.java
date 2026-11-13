package com.x.attendance.assemble.control.jaxrs.dingdingstatistic;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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

/**
 * Created by fancyLou on 2020-04-05.
 * Copyright © 2020 O2. All rights reserved.
 */
@Path("dingdingstatistic")
@JaxrsDescribe("DingTalk 출퇴근 기록 데이터 통계 관리(사용 중단)")
public class DingdingAttendanceStatisticAction extends StandardJaxrsAction {
    private static final Logger logger = LoggerFactory.getLogger(DingdingAttendanceStatisticAction.class);




    @JaxrsMethodDescribe(value = "사용자 월별 통계 조회", action = ActionPersonStatistic.class)
    @GET
    @Path("person/{person}/{year}/{month}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void personMonth(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                            @JaxrsParameterDescribe("사용자") @PathParam("person") String person,
                            @JaxrsParameterDescribe("연도: yyyy") @PathParam("year") String year,
                            @JaxrsParameterDescribe("월: MM") @PathParam("month") String month) {
        ActionResult<List<ActionPersonStatistic.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPersonStatistic().execute(person, year, month);
        }catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "부서별 사용자 월별 통계 조회", action = ActionPersonStatisticWithUnit.class)
    @GET
    @Path("person/unit/{unit}/{year}/{month}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void personMonthWithUnit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                            @JaxrsParameterDescribe("부서") @PathParam("unit") String unit,
                            @JaxrsParameterDescribe("연도: yyyy") @PathParam("year") String year,
                            @JaxrsParameterDescribe("월: MM") @PathParam("month") String month) {
        ActionResult<List<ActionPersonStatisticWithUnit.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPersonStatisticWithUnit().execute(unit, year, month);
        }catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "부서 월별 통계 조회", action = ActionUnitStatistic.class)
    @GET
    @Path("unit/{unit}/{year}/{month}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void unitMonth(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                            @JaxrsParameterDescribe("부서") @PathParam("unit") String unit,
                            @JaxrsParameterDescribe("연도: yyyy") @PathParam("year") String year,
                            @JaxrsParameterDescribe("월: MM") @PathParam("month") String month) {
        ActionResult<ActionUnitStatistic.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUnitStatistic().execute(unit, year, month);
        }catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

}
