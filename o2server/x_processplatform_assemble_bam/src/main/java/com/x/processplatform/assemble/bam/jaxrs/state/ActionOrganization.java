package com.x.processplatform.assemble.bam.jaxrs.state;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.bam.ThisApplication;

class ActionOrganization extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionOrganization.class);

	ActionResult<Wo> execute() throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(ThisApplication.state.getOrganization());
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("조직별 통계")
		private List<WoUnit> unit = new ArrayList<>();

		@FieldDescribe("개인별 통계")
		private List<WoPerson> person = new ArrayList<>();

		public List<WoUnit> getUnit() {
			return unit;
		}

		public void setUnit(List<WoUnit> unit) {
			this.unit = unit;
		}

		public List<WoPerson> getPerson() {
			return person;
		}

		public void setPerson(List<WoPerson> person) {
			this.person = person;
		}

	}

	public static class WoUnit extends GsonPropertyObject {

		@FieldDescribe("조직명")
		String name;
		@FieldDescribe("조직 식별자")
		String value;
		@FieldDescribe("결재 대기 수")
		Long count;
		@FieldDescribe("기한 초과 결재 대기 수")
		Long expiredCount;
		@FieldDescribe("결재 대기 체류 총 시간(분)")
		Long duration;
		@FieldDescribe("처리 완료 수")
		Long completedCount;
		@FieldDescribe("기한 초과 처리 완료 수")
		Long completedExpiredCount;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

		public Long getExpiredCount() {
			return expiredCount;
		}

		public void setExpiredCount(Long expiredCount) {
			this.expiredCount = expiredCount;
		}

		public Long getDuration() {
			return duration;
		}

		public void setDuration(Long duration) {
			this.duration = duration;
		}

		public Long getCompletedCount() {
			return completedCount;
		}

		public void setCompletedCount(Long completedCount) {
			this.completedCount = completedCount;
		}

		public Long getCompletedExpiredCount() {
			return completedExpiredCount;
		}

		public void setCompletedExpiredCount(Long completedExpiredCount) {
			this.completedExpiredCount = completedExpiredCount;
		}

	}

	public static class WoPerson extends GsonPropertyObject {

		@FieldDescribe("개인 이름")
		String name;
		@FieldDescribe("개인 식별자")
		String value;
		@FieldDescribe("결재 대기 수")
		Long count;
		@FieldDescribe("기한 초과 결재 대기 수")
		Long expiredCount;
		@FieldDescribe("결재 대기 체류 총 시간(분)")
		Long duration;
		@FieldDescribe("처리 완료 수")
		Long completedCount;
		@FieldDescribe("기한 초과 처리 완료 수")
		Long completedExpiredCount;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

		public Long getExpiredCount() {
			return expiredCount;
		}

		public void setExpiredCount(Long expiredCount) {
			this.expiredCount = expiredCount;
		}

		public Long getDuration() {
			return duration;
		}

		public void setDuration(Long duration) {
			this.duration = duration;
		}

		public Long getCompletedCount() {
			return completedCount;
		}

		public void setCompletedCount(Long completedCount) {
			this.completedCount = completedCount;
		}

		public Long getCompletedExpiredCount() {
			return completedExpiredCount;
		}

		public void setCompletedExpiredCount(Long completedExpiredCount) {
			this.completedExpiredCount = completedExpiredCount;
		}

	}

}
