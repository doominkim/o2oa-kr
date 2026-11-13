package com.x.bbs.assemble.control.jaxrs.sectioninfo;

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
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionIdEmpty;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionInfoProcess;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionNotExists;
import com.x.bbs.entity.BBSSectionInfo;

public class ActionGet extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSectionIdEmpty();
				result.error( exception );
			}
		}
		
		if( check ){
			Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), id);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );
			if( optional.isPresent() ){
				ActionResult<Wo> result_cache = (ActionResult<Wo>) optional.get();
				result.setData( result_cache.getData() );
				result.setCount( 1L);
			} else {
				//데이터 조회 계속
				result = getSectionQueryResult( id, request, effectivePerson );
				CacheManager.put( cacheCategory, cacheKey, result );
			}
		}
		return result;
	}

	private ActionResult<Wo> getSectionQueryResult(String id, HttpServletRequest request, EffectivePerson effectivePerson) {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		BBSSectionInfo sectionInfo = null;
		Boolean check = true;
		
		if( check ){
			try {
				sectionInfo = sectionInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSectionInfoProcess( e, "지정한 ID로 섹션 정보를 조회하는 중 오류가 발생했습니다. ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( sectionInfo != null ){
				try {
					wrap = Wo.copier.copy( sectionInfo );
					wrap.setSectionVisibleResult( wrap.getVisiblePermissionList() );
					wrap.setSubjectPublishResult( wrap.getPublishPermissionList() );
					wrap.setReplyPublishResult( wrap.getReplyPermissionList() );
					result.setData( wrap );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSectionInfoProcess( e, "BBS 섹션 정보를 출력 객체로 변환하는 중 오류가 발생했습니다." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}else{
				Exception exception = new ExceptionSectionNotExists( id );
				result.error( exception );
			}
		}
		return result;
	}
	
	public static class Wo extends BBSSectionInfo{
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier< BBSSectionInfo, Wo > copier = WrapCopierFactory.wo( BBSSectionInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		@FieldDescribe("게시판 접근 권한 목록, 파라미터 수신용.")
		private String sectionVisibleResult ;
		
		@FieldDescribe("게시판 접근 권한 목록, 파라미터 수신용.")
		private String replyPublishResult ;
		
		@FieldDescribe("게시판 접근 권한 목록, 파라미터 수신용.")
		private String subjectPublishResult ;
		
		//게시판의 하위 게시판 정보 목록
		@FieldDescribe("하위 게시판 목록.")
		private List<Wo> subSections = null;

		public List<Wo> getSubSections() {
			return subSections;
		}
		public void setSubSections(List<Wo> subSections) {
			this.subSections = subSections;
		}
		public String getSectionVisibleResult() {
			return sectionVisibleResult;
		}
		public void setSectionVisibleResult(String sectionVisibleResult) {
			this.sectionVisibleResult = sectionVisibleResult;
		}
		public String getReplyPublishResult() {
			return replyPublishResult;
		}
		public void setReplyPublishResult(String replyPublishResult) {
			this.replyPublishResult = replyPublishResult;
		}
		public String getSubjectPublishResult() {
			return subjectPublishResult;
		}
		public void setSubjectPublishResult(String subjectPublishResult) {
			this.subjectPublishResult = subjectPublishResult;
		}
		
		public void setSubjectPublishResult(List<String> list) {
			this.subjectPublishResult = transferStringListToString(list);
		}
		public void setReplyPublishResult(List<String> list) {
			this.replyPublishResult = transferStringListToString(list);
		}
		public void setSectionVisibleResult(List<String> list) {
			this.sectionVisibleResult = transferStringListToString(list);
		}
		public String transferStringListToString( List<String> list ) {
			StringBuffer sb = new StringBuffer();
			if( ListTools.isNotEmpty( list )) {
				for( String str : list ) {
					if( StringUtils.isNotEmpty( sb.toString() )) {
						sb.append(",");
					}
					sb.append(str);
				}
			}
			return sb.toString();
		}
	}
}