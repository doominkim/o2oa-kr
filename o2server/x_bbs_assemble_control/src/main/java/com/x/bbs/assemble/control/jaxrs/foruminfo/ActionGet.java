package com.x.bbs.assemble.control.jaxrs.foruminfo;

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
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionForumInfoIdEmpty;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionForumInfoNotExists;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionForumInfoProcess;
import com.x.bbs.entity.BBSForumInfo;
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
				Exception exception = new ExceptionForumInfoIdEmpty();
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
				result = getForumQueryResult( id, request, effectivePerson );
				CacheManager.put( cacheCategory, cacheKey, result );
			}
		}
		return result;
	}
	
	private ActionResult<Wo> getForumQueryResult(String id, HttpServletRequest request, EffectivePerson effectivePerson) {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		BBSForumInfo forumInfo = null;
		Boolean check = true;
		
		if( check ){
			try {
				forumInfo = forumInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionForumInfoProcess( e, "ID로 BBS 포럼 분류 정보를 가져오는 중 오류가 발생했습니다! ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( forumInfo != null ){
				try {
					wrap = Wo.copier.copy( forumInfo );
					wrap.setForumVisibleResult( wrap.getVisiblePermissionList() );
					//TODO 프론트엔드 로직을 바꾸지 않기 위해 여기서 List를 String으로 변환해 출력, 쉼표 구분
					wrap.setForumManagerName( wrap.transferStringListToString( wrap.getForumManagerList()) );
					result.setData( wrap );
					result.setCount(1L);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionForumInfoProcess( e, "포럼 정보 객체를 출력 데이터로 변환하는 중 오류가 발생했습니다." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}else{
				Exception exception = new ExceptionForumInfoNotExists( id );
				result.error( exception );
			}
		}
		return result;
	}

	public static class Wo extends BBSForumInfo{
		
		@FieldDescribe("문자열 형식의 관리자 정보, 쉼표(,)로 구분.")
		private String forumManagerName = null;
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static WrapCopier< BBSForumInfo, Wo > copier = WrapCopierFactory.wo( BBSForumInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		@FieldDescribe("게시판 접근 권한 목록, 파라미터 수신용.")
		private String forumVisibleResult ;
		
		//포럼 게시판 목록
		@FieldDescribe("게시판 목록.")
		private List<WoSectionInfo> sections = null;

		public List<WoSectionInfo> getSections() {
			return sections;
		}

		public void setSections(List<WoSectionInfo> sections) {
			this.sections = sections;
		}

		public String getForumManagerName() {
			return forumManagerName;
		}

		public void setForumManagerName( String forumManagerName ) {
			this.forumManagerName = forumManagerName;
		}
		
		public String getForumVisibleResult() {
			return forumVisibleResult;
		}
		public void setForumVisibleResult(String forumVisibleResult) {
			this.forumVisibleResult = forumVisibleResult;
		}
		
		public void setForumVisibleResult( List<String> list ) {
			this.forumVisibleResult = transferStringListToString(list);
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
	
	public static class WoSectionInfo extends BBSSectionInfo{
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		//게시판의 하위 게시판 정보 목록
		@FieldDescribe("하위 게시판 목록.")
		private List<WoSectionInfo> subSections = null;

		public List<WoSectionInfo> getSubSections() {
			return subSections;
		}
		public void setSubSections(List<WoSectionInfo> subSections) {
			this.subSections = subSections;
		}
		
	}


}