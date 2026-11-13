package com.x.attendance.assemble.control.jaxrs.v2.detail.model;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

/**
 * Created by fancyLou on 2023/3/15.
 * Copyright © 2023 O2. All rights reserved.
 */
public class StatisticWi extends GsonPropertyObject {


    private static final long serialVersionUID = -4697552136228406338L;


    @FieldDescribe("사용자 또는 조직 필터, 조직은 기본 재귀: 사용자 또는 조직의 DN, 예: xxx@xxx@P, xxx@xxx@U")
    private String filter;

    // 이 filterList 필드를 기준으로 함, 위 filter 필드는 사용 중단
    @FieldDescribe("사용자 또는 조직 필터, 조직은 기본 재귀: 사용자 또는 조직의 DN, 예: xxx@xxx@P, xxx@xxx@U")
    private List<String> filterList;

    @FieldDescribe("필터 조직의 하위 조직 사용자 재귀 조회 여부, 기본 true, false면 현재 조직 직속 사용자만 조회")
    private Boolean recursive;

    @FieldDescribe("시작일, 포함")
    private String startDate;

    @FieldDescribe("종료일, 포함")
    private String endDate;

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getFilterList() {
      return filterList;
    }

    public void setFilterList(List<String> filterList) {
      this.filterList = filterList;
    }

    public Boolean getRecursive() {
      return recursive;
    }

    public void setRecursive(Boolean recursive) {
      this.recursive = recursive;
    }

    
}
