package com.x.program.center.qiyeweixin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class QiyeweixinFactory {

	private static final Logger logger = LoggerFactory.getLogger(QiyeweixinFactory.class);

	private String accessToken;

	private List<Department> orgs = new ArrayList<>();

	private List<User> users = new ArrayList<>();

	public QiyeweixinFactory(String accessToken) throws Exception {
		this.accessToken = accessToken;
		for (Department o : this.orgs()) {
			orgs.add(o);
			users.addAll(this.users(o));
		}
		users = ListTools.trim(users, true, true);
		logger.info("qi ye wei xin sync org num:{}, user num:{}", orgs.size(), users.size());
		logger.info("qw 사용자 정보 출력:{}", users.get(0).toString());
	}

	public List<User> getUsers() {
		return this.users;
	}

	// 2023-03-14 새 api는 앱 권한에 따라 조직 동기화, 조직이 반드시 루트 노드는 아니므로 루트 노드 id==1 판단은 더 이상 유효하지 않음
	public List<Department> roots() {
//		return orgs.stream().filter(o -> 1L == o.getId()).collect(Collectors.toList());
		Set<Long> ids = orgs.stream().map(Department::getId).collect(Collectors.toSet());
		return orgs.stream()
				.filter(dept -> !ids.contains(dept.getParentid()))
				.collect(Collectors.toList());
	}

	private List<Department> orgs() throws Exception {
		String address = Config.qiyeweixin().getApiAddress() + "/cgi-bin/department/list?access_token="
				+ this.accessToken + "&id=";
		OrgListResp resp = HttpConnection.getAsObject(address, null, OrgListResp.class);
		logger.debug("orgs response:{}.", resp);
		if (resp.getErrcode() != 0) {
			throw new ExceptionListOrg(resp.getErrcode(), resp.getErrmsg());
		}
		return resp.getDepartment();
	}

	private List<User> users(Department department) throws Exception {
		String address = Config.qiyeweixin().getApiAddress() + "/cgi-bin/user/list?access_token=" + this.accessToken
				+ "&department_id=" + department.getId();
		UserListResp resp = HttpConnection.getAsObject(address, null, UserListResp.class);
		logger.debug("users response:{}.", resp);
		if (resp.getErrcode() != 0) {
			throw new ExceptionListUser(resp.getErrcode(), resp.getErrmsg());
		}
		// @Date 2022-09-29 기업 위챗 api 권한 조정으로 사용자 휴대폰 번호를 읽을 수 없어 여기서 가짜 값 채움
		List<User> userList = resp.getUserlist();
		if (userList != null && !userList.isEmpty()) {
			return userList.stream().peek(this::setDefaultMobileStr).collect(Collectors.toList());

		}
		return userList;
	}

	private void setDefaultMobileStr(User user) {
		if (user != null && StringUtils.isNotEmpty(user.getUserid()) && StringUtils.isEmpty(user.getMobile())) {
			String userId = user.getUserid();
			if (userId.length() < 11) { // 사용자명이 너무 짧은 경우
				userId = String.format("%11s", userId).replace(" ","0");
			}
			if (userId.length() > 32) { // 사용자명이 너무 긴 경우
				userId = userId.substring(userId.length() - 32);
			}
			user.setMobile(userId);
		}
	}

	public List<User> listUser(Department org) throws Exception {
		return users.stream().filter(o -> ListTools.contains(o.getDepartment(), org.getId()))
				.collect(Collectors.toList());
	}

	public List<Department> listSub(Department org) throws Exception {
		return orgs.stream().filter(o -> {
			return Objects.equals(o.getParentid(), org.getId()) ? true : false;
		}).sorted(Comparator.comparing(Department::getOrder, Comparator.nullsLast(Long::compareTo)))
				.collect(Collectors.toList());
	}

	public static class OrgListResp extends GsonPropertyObject {

		// {
		// "retCode": 0,
		// "retMessage": "success",
		// "retData": {
		// "orgNumber": 100016000,
		// "name": "인터넷+사업부",
		// "type": 1,
		// "parentId": 1,
		// "fullName": "",
		// "orgCode": "",
		// "postCode": "",
		// "deptHiding": false,
		// "deptPerimits": [],
		// "outerDept": false,
		// "outerPermitDepts": [],
		// "createDeptGroup": false,
		// "orgDeptGroupOwner": null,
		// "deptManagerUseridList": [],
		// "order": 0
		// }
		// }

		private Integer errcode;
		private String errmsg;
		private List<Department> department;

		public Integer getErrcode() {
			return errcode;
		}

		public void setErrcode(Integer errcode) {
			this.errcode = errcode;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}

		public List<Department> getDepartment() {
			return department;
		}

		public void setDepartment(List<Department> department) {
			this.department = department;
		}

	}

//	public static class OrgResp extends GsonPropertyObject {
//
//		private Integer retCode;
//		private String retMessage;
//		private Department retData;
//
//		public Integer getRetCode() {
//			return retCode;
//		}
//
//		public void setRetCode(Integer retCode) {
//			this.retCode = retCode;
//		}
//
//		public String getRetMessage() {
//			return retMessage;
//		}
//
//		public void setRetMessage(String retMessage) {
//			this.retMessage = retMessage;
//		}
//
//		public Department getRetData() {
//			return retData;
//		}
//
//		public void setRetData(Department retData) {
//			this.retData = retData;
//		}
//	}

	public static class UserListResp extends GsonPropertyObject {
		private Integer errcode;
		private String errmsg;
		private List<User> userlist;

		public Integer getErrcode() {
			return errcode;
		}

		public void setErrcode(Integer errcode) {
			this.errcode = errcode;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}

		public List<User> getUserlist() {
			return userlist;
		}

		public void setUserlist(List<User> userlist) {
			this.userlist = userlist;
		}
	}

}
