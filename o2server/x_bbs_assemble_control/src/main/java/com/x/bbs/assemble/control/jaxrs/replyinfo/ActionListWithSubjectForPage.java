package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionCountEmpty;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionPageEmpty;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyInfoProcess;
import com.x.bbs.entity.BBSReplyInfo;


/**
 * @apiNote 원글 ID로 원글에 대한 답글 목록 반환(해당 답글에 대한 답글 수를 나타내는 표시 포함)
 * @author O2LEE
 */
public class ActionListWithSubjectForPage extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListWithSubjectForPage.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, Integer page,
			Integer count, JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
			if( wrapIn.getShowSubReply() == null ){
				wrapIn.setShowSubReply(true);
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionReplyInfoProcess(e,"JSON 정보를 객체로 변환하는 중 오류가 발생했습니다. JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), wrapIn.getSubjectId(), page, count, wrapIn.getShowSubReply());
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );
			if( optional.isPresent() ){
				ActionResult<List<Wo>> result_cache = (ActionResult<List<Wo>>) optional.get();
				result.setData(result_cache.getData());
				result.setCount(result_cache.getCount());
			} else {
				result = getReplyQueryResult( wrapIn, request, effectivePerson, page, count );
				CacheManager.put( cacheCategory, cacheKey, result );
			}
		}
		return result;
	}

	public ActionResult<List<Wo>> getReplyQueryResult(Wi wrapIn, HttpServletRequest request,
			EffectivePerson effectivePerson, Integer page, Integer count) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<Wo> wrapSubReplies = new ArrayList<>();
		List<BBSReplyInfo> replyInfoList = null;
		List<BBSReplyInfo> replyInfoList_out = new ArrayList<BBSReplyInfo>();
		Long total = 0L;
		Boolean check = true;
		String config_BBS_REPLY_SORTTYPE = configSettingService.getValueWithConfigCode("BBS_REPLY_SORTTYPE");

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
				total = replyInfoService.countWithSubjectForPage(wrapIn.getSubjectId(), wrapIn.getShowSubReply() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e,"주제 ID로 주제 내 전체 댓글 수를 조회하는 중 오류가 발생했습니다. Subject:" + wrapIn.getSubjectId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (total > 0) {
				try {
					replyInfoList = replyInfoService.listWithSubjectForPage( wrapIn.getSubjectId(), wrapIn.getShowSubReply(), page * count, config_BBS_REPLY_SORTTYPE );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionReplyInfoProcess(e,"주제 ID로 주제 내 전체 댓글 목록을 조회하는 중 오류가 발생했습니다. Subject:" + wrapIn.getSubjectId());
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
			for ( int i = 0; replyInfoList != null && i < replyInfoList.size(); i++ ) {
				if (i < replyInfoList.size() && i >= startIndex && i < endIndex) {
					replyInfoList_out.add( replyInfoList.get(i) );
				}
			}
			if ( ListTools.isNotEmpty( replyInfoList_out )) {
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
				List<BBSReplyInfo> subReplies = null;
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

					//해당 답글의 하위 답글 존재 여부와 개수를 조회하고, 첫 번째를 제외한 나머지는 내용을 제거해 대량 네트워크 전송 방지
					subReplies = replyInfoService.listRelysWithRelyId( wo.getId(), config_BBS_REPLY_SORTTYPE );
					if( ListTools.isNotEmpty( subReplies )){
						wrapSubReplies = Wo.copier.copy( subReplies );
						for( int i=0; i<wrapSubReplies.size(); i++  ){
							if( i > 0 ){
								wrapSubReplies.get(i).setContent(null);
							}
						}
						wo.setSubReplyTotal( wrapSubReplies.size() );
						wo.setSubReplies( wrapSubReplies );
					}
				}
				result.setCount(total);

			}
		}
		result.setData(wraps);

		return result;
	}

	public static class Wi {

		@FieldDescribe("주제 Id")
		private String subjectId = null;

		@FieldDescribe("모든 답글을 같은 레벨로 표시할지 여부, false이면 첫 레벨만 표시")
		private Boolean showSubReply = true;

		public Boolean getShowSubReply() { return showSubReply; }

		public void setShowSubReply(Boolean showSubReply) { this.showSubReply = showSubReply; }

		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

		public String getSubjectId() {
			return subjectId;
		}

		public void setSubjectId(String subjectId) {
			this.subjectId = subjectId;
		}
	}

	public static class Wo extends BBSReplyInfo {
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<String>();
		public static WrapCopier<BBSReplyInfo, Wo> copier = WrapCopierFactory.wo(BBSReplyInfo.class, Wo.class, null, JpaObject.FieldsInvisible);

		@FieldDescribe("작성자 이름")
		private String creatorNameShort = "";

		@FieldDescribe("심사자 이름")
		private String auditorNameShort = "";

		@FieldDescribe("하위 답글 수, 기본값 0")
		private Integer subReplyTotal = 0;

		@FieldDescribe("하위 답글 수, 기본값 0")
		private List<Wo> subReplies;

		public List<Wo> getSubReplies() {
			return subReplies;
		}

		public void setSubReplies(List<Wo> subReplies) {
			this.subReplies = subReplies;
		}

		public Integer getSubReplyTotal() {
			return subReplyTotal;
		}

		public void setSubReplyTotal(Integer subReplyTotal) {
			this.subReplyTotal = subReplyTotal;
		}

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
}
