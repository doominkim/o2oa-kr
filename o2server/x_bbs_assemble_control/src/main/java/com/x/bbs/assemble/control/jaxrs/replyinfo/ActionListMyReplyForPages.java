package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionCountEmpty;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionPageEmpty;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyInfoProcess;
import com.x.bbs.entity.BBSReplyInfo;

public class ActionListMyReplyForPages extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListMyReplyForPages.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, Integer page,
			Integer count) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<BBSReplyInfo> replyInfoList = null;
		List<BBSReplyInfo> replyInfoList_out = new ArrayList<BBSReplyInfo>();
		Long total = 0L;
		Boolean check = true;
		String config_BBS_MYREPLY_SORTTYPE = configSettingService.getValueWithConfigCode("BBS_MYREPLY_SORTTYPE");

		if (check) {
			if (page == null) {
				check = false;
				Exception exception = new ExceptionPageEmpty();
				result.error(exception);
			}
		}
		if (check) {
			if (count == null) {
				check = false;
				Exception exception = new ExceptionCountEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				total = replyInfoService.countReplyByUserName(effectivePerson.getDistinguishedName());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e,
						"개인별 주제 내 전체 댓글 수를 조회하는 중 오류가 발생했습니다. Person:" + effectivePerson.getDistinguishedName());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (total > 0) {
				try {
					replyInfoList = replyInfoService.listReplyByUserNameForPage(effectivePerson.getDistinguishedName(), config_BBS_MYREPLY_SORTTYPE,
							page * count);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionReplyInfoProcess(e,
							"개인별 주제 내 전체 댓글 목록을 조회하는 중 오류가 발생했습니다. Person:" + effectivePerson.getDistinguishedName());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			if (page <= 0) {
				page = 1;
			}
			if (count <= 0) {
				count = 20;
			}
			int startIndex = (page - 1) * count;
			int endIndex = page * count;
			for (int i = 0; replyInfoList != null && i < replyInfoList.size(); i++) {
				if (i < replyInfoList.size() && i >= startIndex && i < endIndex) {
					replyInfoList_out.add(replyInfoList.get(i));
				}
			}
			if (ListTools.isNotEmpty(replyInfoList_out)) {
				try {
					wraps = Wo.copier.copy(replyInfoList_out);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionReplyInfoProcess(e, "조회 결과를 출력용 데이터로 변환하는 중 오류가 발생했습니다.");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			if (ListTools.isNotEmpty(wraps)) {
				for (Wo wo : wraps) {
					if(StringUtils.isBlank(wo.getNickName())){
						wo.setNickName(wo.getCreatorName());
					}
					if (StringUtils.isNotEmpty(wo.getCreatorName())) {
						wo.setCreatorNameShort(wo.getCreatorName().split("@")[0]);
					}
					if (StringUtils.isNotEmpty(wo.getAuditorName())) {
						wo.setAuditorNameShort(wo.getAuditorName().split("@")[0]);
					}
				}
			}
		}
		result.setData(wraps);
		result.setCount(total);
		return result;
	}

	public static class Wo extends BBSReplyInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<BBSReplyInfo, Wo> copier = WrapCopierFactory.wo(BBSReplyInfo.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("작성자 이름")
		private String creatorNameShort = "";

		@FieldDescribe("심사자 이름")
		private String auditorNameShort = "";

		public String getCreatorNameShort() {
			return creatorNameShort;
		}

		public String getAuditorNameShort() {
			return auditorNameShort;
		}

		public void setCreatorNameShort(String creatorNameShort) {
			this.creatorNameShort = creatorNameShort;
		}

		public void setAuditorNameShort(String auditorNameShort) {
			this.auditorNameShort = auditorNameShort;
		}
	}

	public static class Wi {

		@FieldDescribe("주제 Id")
		private String subjectId = null;

		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

		public String getSubjectId() {
			return subjectId;
		}

		public void setSubjectId(String subjectId) {
			this.subjectId = subjectId;
		}
	}
}
