package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyIdEmpty;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyInfoProcess;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyNotExists;
import com.x.bbs.entity.BBSReplyInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		BBSReplyInfo replyInfo = null;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		Boolean check = true;
		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionReplyIdEmpty();
				result.error(exception);
			}
		}
		// 주제 정보 존재 여부 판단
		if (check) {
			try {
				replyInfo = replyInfoService.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e, "지정한 ID로 댓글 정보를 조회하는 중 오류가 발생했습니다. ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (replyInfo == null) {
				check = false;
				Exception exception = new ExceptionReplyNotExists(id);
				result.error(exception);
			}
		}
		if (check) {
			try {
				String config_BBS_REPLY_DELETETYPE = configSettingService.getValueWithConfigCode("BBS_REPLY_DELETETYPE");

				if(StringUtils.equals( "Recursively", config_BBS_REPLY_DELETETYPE )){
					//재귀 삭제
					List<String> subIds = replyInfoService.listAllSubReplyIds(id, null);
					if (ListTools.isNotEmpty(subIds)) {
						for (String replyId : subIds) {
							logger.debug("하위 답글 정보 삭제, ID=" + replyId);
							replyInfoService.delete(replyId);
						}
					}
				}

				replyInfoService.delete(id);

				Wo wo = new Wo();
				wo.setId(id);
				result.setData(wo);

				CacheManager.notify(BBSSubjectInfo.class);
				CacheManager.notify(BBSReplyInfo.class);
				CacheManager.notify(BBSSectionInfo.class);

				operationRecordService.replyOperation(effectivePerson.getDistinguishedName(), replyInfo, "DELETE", hostIp, hostName);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e, "지정한 ID로 댓글 정보를 삭제하는 중 오류가 발생했습니다. ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoId {

	}
}