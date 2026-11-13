package com.x.bbs.assemble.control.jaxrs.sectioninfo;

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
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionForumIdEmpty;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionForumInfoNotExists;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionInfoProcess;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;

public class ActionListWithForum extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionListWithForum.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String forumId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<BBSSectionInfo> sectionInfoList = null;
		BBSForumInfo forumInfo = new BBSForumInfo();
		Boolean check = true;
		if( check ){
			if( forumId == null || forumId.isEmpty() ){
				check = false;
				Exception exception = new ExceptionForumIdEmpty();
				result.error( exception );
			}
		}
		if( check ){ //포럼 정보 존재 여부 조회
			try{
				forumInfo = forumInfoServiceAdv.get( forumId );
			}catch( Exception e ){
				check = false;
				Exception exception = new ExceptionSectionInfoProcess( e, "ID로 BBS 포럼 분류 정보를 가져오는 중 오류가 발생했습니다! ID:" + forumId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( forumInfo == null ){
				check = false;
				Exception exception = new ExceptionForumInfoNotExists( forumId );
				result.error( exception );
			}
		}
		if( check ){
			//데이터베이스에서 상위 게시판 목록 조회
			try {
				sectionInfoList = sectionInfoServiceAdv.listMainSectionByForumId( forumId );
				if (sectionInfoList == null) {
					sectionInfoList = new ArrayList<BBSSectionInfo>();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSectionInfoProcess( e, "포럼 분류 ID로 전체 상위 섹션 정보를 조회하는 중 오류가 발생했습니다. Forum:" + forumId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( sectionInfoList != null && sectionInfoList.size() > 0 ){
				try {
					wraps = Wo.copier.copy( sectionInfoList );
					if( ListTools.isNotEmpty( wraps )) {
						for( Wo wrap : wraps ) {
							wrap.setSectionVisibleResult( wrap.getVisiblePermissionList() );
							wrap.setSubjectPublishResult( wrap.getPublishPermissionList() );
							wrap.setReplyPublishResult( wrap.getReplyPermissionList() );
						}	
					}
					result.setData(wraps);
				} catch (Exception e) {
					Exception exception = new ExceptionSectionInfoProcess( e, "BBS 섹션 정보를 출력 객체로 변환하는 중 오류가 발생했습니다." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}		
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