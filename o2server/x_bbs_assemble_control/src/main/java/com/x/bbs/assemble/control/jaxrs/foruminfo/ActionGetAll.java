package com.x.bbs.assemble.control.jaxrs.foruminfo;

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
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionForumInfoProcess;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;

public class ActionGetAll extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionGetAll.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Boolean check = true;
		
		if( check ){
			Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass());
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );
			if( optional.isPresent() ){
				ActionResult<List<Wo>> result_cache = (ActionResult<List<Wo>>) optional.get();
				result.setData( result_cache.getData() );
				result.setCount( 1L);
			} else {
				//데이터 조회 계속
				result = getForumAllQueryResult( request, effectivePerson );
				CacheManager.put( cacheCategory, cacheKey, result );
			}
		}
		return result;
	}

	private ActionResult<List<Wo>> getForumAllQueryResult(HttpServletRequest request, EffectivePerson effectivePerson) {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<BBSForumInfo> forumInfoList = null;
		Boolean check = true;
		
		if( check ){
			//DB에서 포럼 목록 조회
			try {
				forumInfoList = forumInfoServiceAdv.listAll();
			} catch (Exception e) {
				Exception exception = new ExceptionForumInfoProcess( e, "전체 BBS 포럼 분류 정보를 가져오는 중 오류가 발생했습니다." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		if( check ){
			if( ListTools.isNotEmpty( forumInfoList ) ){
				try {
					wraps = Wo.copier.copy( forumInfoList );
				} catch (Exception e) {
					Exception exception = new ExceptionForumInfoProcess( e, "전체 BBS 포럼 분류 정보를 출력 객체로 변환하는 중 오류가 발생했습니다." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
				
			}
		}
		
		//TODO 프론트엔드 로직을 바꾸지 않기 위해 여기서 List를 String으로 변환해 출력, 쉼표 구분
		if( check ){
			if( ListTools.isNotEmpty( wraps ) ){
				for( Wo wo : wraps ) {
					wo.setForumVisibleResult( wo.getVisiblePermissionList() );
					wo.setForumManagerName( wo.transferStringListToString( wo.getForumManagerList() ));
				}
			}
		}
		result.setData( wraps );
		result.setCount( Long.parseLong( wraps.size() + "") );
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
		private List<WoSectionInfo> subSections = null;

		public List<WoSectionInfo> getSubSections() {
			return subSections;
		}
		public void setSubSections(List<WoSectionInfo> subSections) {
			this.subSections = subSections;
		}	
	}
}