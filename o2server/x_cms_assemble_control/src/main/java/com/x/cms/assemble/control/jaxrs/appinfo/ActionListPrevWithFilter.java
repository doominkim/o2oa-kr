package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.base.core.project.jaxrs.InTerms;
import com.x.base.core.project.jaxrs.LikeTerms;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionListPrevWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListPrevWithFilter.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id,
			Integer count, JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		EqualsTerms equals = new EqualsTerms();
		InTerms ins = new InTerms();
		LikeTerms likes = new LikeTerms();
		Wi wi = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionAppInfoProcess(e, "JSON 정보를 객체로 변환하는 중 오류가 발생했습니다. JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		if (check) {
			if (wi == null) {
				result.setCount(0L);
				result.setData(new ArrayList<Wo>());
				return result;
			}
			if (id == null) {
				id = "(0)";
			}
			if (count == null) {
				count = 20;
			}
			if ((null != wi.getAppIdList()) && (!wi.getAppIdList().isEmpty())) {
				ins.put("id", wi.getAppIdList());
			}
			if ((null != wi.getCreatorList()) && (!wi.getCreatorList().isEmpty())) {
				ins.put("creatorUid", wi.getCreatorList());
			}
			if (StringUtils.isNotEmpty(wi.getKey())) {
				String key = StringUtils.trim(StringUtils.replace(wi.getKey(), "\u3000", " "));
				if (StringUtils.isNotEmpty(key)) {
					likes.put("appName", key);
				}
			}
			try {
				result = this.standardListPrev(Wo.copier, id, count, "sequence", equals, null, likes, ins, null, null,
						null, null, true, DESC);
			} catch (Exception e) {
				result.error(e);
				logger.warn("게시판 정보 목록을 페이지 조회하는 중 오류가 발생했습니다.");
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public class Wi {

		@FieldDescribe("필터 조건으로 쓰는 CMS 앱 ID, 여러 개 가능, String 배열.")
		private List<String> appIdList;

		@FieldDescribe("필터 조건으로 쓰는 작성자 이름, 여러 개 가능, String 배열.")
		private List<String> creatorList;

		@FieldDescribe("필터 조건으로 쓰는 CMS 앱 키워드, 보통 앱 이름, String, 유사 검색.")
		private String key;

		@FieldDescribe("문서 유형: 전체 | 정보 | 데이터")
		private String documentType = "정보";

		public List<String> getAppIdList() {
			return appIdList;
		}

		public void setAppIdList(List<String> appIdList) {
			this.appIdList = appIdList;
		}

		public List<String> getCreatorList() {
			return creatorList;
		}

		public void setCreatorList(List<String> creatorList) {
			this.creatorList = creatorList;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getDocumentType() {
			return documentType;
		}

		public void setDocumentType(String documentType) {
			this.documentType = documentType;
		}

	}
}