package com.x.cms.assemble.control.jaxrs.comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

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
import com.x.cms.core.entity.DocumentCommentInfo;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		DocumentCommentInfo appInfo = null;
		Boolean check = true;
		
		if( StringUtils.isEmpty(id) ){
			check = false;
			Exception exception = new ExceptionCommentIdForQueryEmpty();
			result.error( exception );
		}

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), id );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			result.setData((Wo)optional.get());
		} else {
			if( check ){
				try {
					appInfo = documentCommentInfoQueryService.get(id);
					if( appInfo == null ){
						check = false;
						Exception exception = new ExceptionCommentNotExists( id );
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new CommentQueryException( e, "지정한 id로 게시판 정보 객체를 조회하는 중 오류가 발생했습니다. ID:" + id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if( check ){
				try {
					wo = Wo.copier.copy( appInfo );
					if( wo != null ) {
						wo.setContent( documentCommentInfoQueryService.getCommentContent(id));
					}
					CacheManager.put(cacheCategory, cacheKey, wo);
					result.setData( wo );
				} catch (Exception e) {
					Exception exception = new CommentQueryException( e, "조회한 게시판 정보 객체를 출력 데이터로 변환하는 중 오류가 발생했습니다." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		return result;
	}
	
public static class Wo extends DocumentCommentInfo {
		
		private Long rank;

		@FieldDescribe("내용")
		private String content = "";
		
		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
		
		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<DocumentCommentInfo, Wo> copier = WrapCopierFactory.wo( DocumentCommentInfo.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
}