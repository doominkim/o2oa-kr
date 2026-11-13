package com.x.attendance.assemble.control.jaxrs.v2.appeal;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import java.util.List;

public class AppealInfoWi extends GsonPropertyObject {

    private static final long serialVersionUID = 3800681861289573236L;

    @FieldDescribe("이의 신청한 출퇴근 근태 기록 ID")
    private String recordId;
    @FieldDescribe("사용자 식별자")
    private List<String> users;
    @FieldDescribe("사용자 또는 조직 필터, 조직은 기본 재귀: 사용자 또는 조직의 DN, 예: xxx@xxx@P, xxx@xxx@U")
    private List<String> filterList;
    @FieldDescribe("필터 조직의 하위 조직 사용자 재귀 조회 여부, 기본 true, false면 현재 조직 직속 사용자만 조회")
    private Boolean recursive;
    @FieldDescribe("시작 날짜")
    private String startDate;
    @FieldDescribe("종료 날짜")
    private String endDate;
    @FieldDescribe("이의 신청 상태:0-대기, 1-결재 중(프로세스 시작됨), 2-승인, 3-반려 4-관리자 처리")
    private String status;

    public boolean isStatusValid() {
        return status != null && (status.equals("0") || status.equals("1") || status.equals("2") || status.equals("3") || status.equals("4"));
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
