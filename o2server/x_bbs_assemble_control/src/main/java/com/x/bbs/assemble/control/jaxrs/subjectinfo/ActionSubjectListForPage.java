package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.x.base.core.project.tools.MD5Tool;
import com.x.base.core.project.tools.SortTools;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSectionNotExists;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectFilter;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectInfoProcess;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectWrapOut;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionWrapInConvert;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.bbs.entity.BBSVoteOption;
import com.x.bbs.entity.BBSVoteOptionGroup;

public class ActionSubjectListForPage extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionSubjectListForPage.class );

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, Integer page, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wrapIn = null;
		Boolean isBBSManager = false;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if ( check ) {
			isBBSManager = ThisApplication.isBBSManager(effectivePerson);
		}

		if( check ) {
			Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), effectivePerson.getDistinguishedName(), MD5Tool.getMD5Str(gson.toJson(wrapIn)), isBBSManager, page, count);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );
			if( optional.isPresent() ){
				ActionResult<List<Wo>> result_cache = (ActionResult<List<Wo>>) optional.get();
				result.setData( result_cache.getData() );
				result.setCount( result_cache.getCount() );
			} else {
				//데이터 조회 계속
				result = getSubjectQueryResult(wrapIn, request, effectivePerson, page, count);
				CacheManager.put( cacheCategory, cacheKey, result );
			}
		}
		return result;
	}

	public ActionResult<List<Wo>> getSubjectQueryResult( Wi wrapIn, HttpServletRequest request, EffectivePerson effectivePerson, Integer page, Integer count ) {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps_nonTop = new ArrayList<>();
		List<Wo> wraps_top = new ArrayList<>();
		List<Wo> wraps_out = new ArrayList<Wo>();
		BBSSectionInfo sectionInfo = null;
		List<BBSSubjectInfo> subjectInfoList = null;
		List<BBSSubjectInfo> subjectInfoList_top = null;
		List<String> viewSectionIds = new ArrayList<String>();
		Integer selectTotal = 0;
		Long total = 0L;
		Integer topTotal = 0;
		Boolean check = true;

		if( check && StringUtils.isNotEmpty( wrapIn.getSectionId() ) ){
			if (check) {
				try {
					sectionInfo = sectionInfoServiceAdv.get( wrapIn.getSectionId() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSubjectInfoProcess( e, "지정한 ID로 섹션 정보를 조회하는 중 오류가 발생했습니다. ID:" + wrapIn.getSectionId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if (check) {
				if ( sectionInfo == null ) {
					check = false;
					Exception exception = new ExceptionSectionNotExists( wrapIn.getSectionId() );
					result.error( exception );
				}
			}
		}

		if( check ){
			try{
				viewSectionIds = getViewableSectionIds( request, effectivePerson );
			}catch( Exception e ){
				check = false;
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			if( page == null ){
				page = 1;
			}
		}

		if( check ){
			if( count == null ){
				count = 20;
			}
		}

		//조회 최대 항목 수
		selectTotal = page * count;
		Boolean selectTopInSection = null;//기본적으로 게시판 내 모든 상단 고정글과 일반글을 조회

		//모든 상단 고정글 조회
		if ( check && wrapIn != null && wrapIn.getWithTopSubject() != null && wrapIn.getWithTopSubject() ) {
			try {
				subjectInfoList_top = subjectInfoServiceAdv.listAllTopSubject( sectionInfo, wrapIn.getCreatorName(), viewSectionIds ,wrapIn.getStartTime() ,  wrapIn.getEndTime());
				if( subjectInfoList_top != null ){
					topTotal = subjectInfoList_top.size();
					try {
						wraps_top = Wo.copier.copy( subjectInfoList_top );
						SortTools.desc( wraps_top, "latestReplyTime" );
					} catch (Exception e) {
						Exception exception = new ExceptionSubjectWrapOut( e );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSubjectFilter( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			//상단 고정글은 페이지당 항목 수를 차지함
			if( wraps_top != null ){
				if( selectTotal < wraps_top.size() ){
					selectTotal = 0;
				}else{
					selectTotal = selectTotal - wraps_top.size();
				}
			}
		}

		if( check ){
			selectTopInSection = false; //상단 고정글은 앞에서 이미 처리되어 List에 들어 있으므로 다시 조회할 필요 없음, 이후 조회에서 상단 고정글 제외
			if( selectTotal > 0 ){
				try{
					total = subjectInfoServiceAdv.countSubjectInSectionForPage( wrapIn.getSearchContent(), wrapIn.getForumId(), wrapIn.getMainSectionId(), wrapIn.getSectionId(),
							wrapIn.getCreatorName(), wrapIn.getNeedPicture(), selectTopInSection, viewSectionIds ,wrapIn.getStartTime() ,  wrapIn.getEndTime());
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSubjectFilter( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}

		if( check ){
			if( selectTotal > 0 && total > 0 ){
				try{
					subjectInfoList = subjectInfoServiceAdv.listSubjectInSectionForPage( wrapIn.getSearchContent(), wrapIn.getForumId(), wrapIn.getMainSectionId(), wrapIn.getSectionId(), wrapIn.getCreatorName(), wrapIn.getNeedPicture(), selectTopInSection, selectTotal, viewSectionIds ,  wrapIn.getStartTime() ,  wrapIn.getEndTime() );
					if( subjectInfoList != null ){
						try {
							wraps_nonTop = Wo.copier.copy( subjectInfoList );
							SortTools.desc( wraps_nonTop, "latestReplyTime" );
						} catch (Exception e) {
							Exception exception = new ExceptionSubjectWrapOut( e );
							result.error( exception );
							logger.error( e, effectivePerson, request, null);
						}
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSubjectFilter( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}

		if( check ){
			if( page <= 0 ){
				page = 1;
			}
			if( count <= 0 ){
				count = 20;
			}
			int startIndex = ( page - 1 ) * count;
			int endIndex = page * count;
			int i = 0;
			for( ; wraps_top != null && i< wraps_top.size(); i++ ){
				if( i >= startIndex && i < endIndex ){
					wraps_out.add( wraps_top.get( i ) );
				}
			}
			for( int j=0; wraps_nonTop != null && j< wraps_nonTop.size(); j++ ){
				if( i+j >= startIndex && i+j < endIndex ){
					wraps_out.add( wraps_nonTop.get( j ) );
				}
			}
			if( ListTools.isNotEmpty( wraps_out ) ){
				for( Wo wo : wraps_out ) {
					//@ 형식의 사용자 식별자를 사용자 이름으로 바꿔 xxShort 속성에 할당
					cutPersonNames( wo );
				}
				try {
					result.setData( wraps_out );
					result.setCount( total + topTotal );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSubjectWrapOut( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	/**
	 *  @ 형식의 사용자 식별자를 사용자 이름으로 바꿔 xxShort 속성에 할당
	 *
	 *  latestReplyUserShort = "";
		bBSIndexSetterNameShort = "";
		screamSetterNameShort = "";
		originalSetterNameShort = "";
		creatorNameShort = "";
		auditorNameShort = "";

	 * @param subject
	 */
	private void cutPersonNames( Wo subject ) {
		if( subject != null ) {
			if(StringUtils.isBlank(subject.getNickName())){
				subject.setNickName(subject.getCreatorName());
			}
			if ( StringUtils.isNotEmpty( subject.getLatestReplyUser() )) {
				subject.setLatestReplyUserNickName(subject.getLatestReplyUser().split("@")[0]);
				try {
					if(configSettingService.useNickName()) {
						Business business = new Business(null);
						subject.setLatestReplyUserNickName(business.organization().person().getNickName(subject.getLatestReplyUser()));
					}
				} catch (Exception e) {
					logger.debug(e.getMessage());
				}
			}
			if( StringUtils.isNotEmpty( subject.getbBSIndexSetterName() ) ) {
				subject.setbBSIndexSetterNameShort( subject.getbBSIndexSetterName().split( "@" )[0]);
			}
			if( StringUtils.isNotEmpty( subject.getScreamSetterName() ) ) {
				subject.setScreamSetterNameShort( subject.getScreamSetterName().split( "@" )[0]);
			}
			if( StringUtils.isNotEmpty( subject.getOriginalSetterName() ) ) {
				subject.setOriginalSetterNameShort( subject.getOriginalSetterName().split( "@" )[0]);
			}
			if( StringUtils.isNotEmpty( subject.getCreatorName() ) ) {
				subject.setCreatorNameShort( subject.getCreatorName().split( "@" )[0]);
			}
			if( StringUtils.isNotEmpty( subject.getAuditorName() ) ) {
				subject.setAuditorNameShort( subject.getAuditorName().split( "@" )[0]);
			}
		}
	}

	public static class Wi{

		@FieldDescribe( "게시글 ID." )
		private String subjectId = null;

		@FieldDescribe( "투표 선택지 ID." )
		private String voteOptionId = null;

		@FieldDescribe( "게시글 소속 포럼 ID." )
		private String forumId = null;

		@FieldDescribe( "게시글 소속 상위 게시판 ID." )
		private String mainSectionId = null;

		@FieldDescribe( "게시글 소속 게시판 ID." )
		private String sectionId = null;

		@FieldDescribe( "제목 유사 검색 키워드" )
		private String searchContent = null;

		@FieldDescribe( "작성자 이름." )
		private String creatorName = null;

		@FieldDescribe( "큰 이미지가 있는 게시글만 조회할지 여부." )
		private Boolean needPicture = false;

		@FieldDescribe( "상단 고정글 포함 여부." )
		private Boolean withTopSubject = false; // 상단 고정글 포함 여부

		@FieldDescribe( "작성일 시작." )
		private Date startTime = null;

		@FieldDescribe( "작성일 종료." )
		private Date endTime = null;

		public static List<String> Excludes = new ArrayList<String>( JpaObject.FieldsUnmodify );


		public String getForumId() {
			return forumId;
		}
		public void setForumId(String forumId) {
			this.forumId = forumId;
		}
		public String getSectionId() {
			return sectionId;
		}
		public void setSectionId(String sectionId) {
			this.sectionId = sectionId;
		}
		public String getMainSectionId() {
			return mainSectionId;
		}
		public void setMainSectionId(String mainSectionId) {
			this.mainSectionId = mainSectionId;
		}
		public Boolean getNeedPicture() {
			return needPicture;
		}
		public void setNeedPicture(Boolean needPicture) {
			this.needPicture = needPicture;
		}
		public Boolean getWithTopSubject() {
			return withTopSubject;
		}
		public void setWithTopSubject(Boolean withTopSubject) {
			this.withTopSubject = withTopSubject;
		}
		public String getSearchContent() {
			if( StringUtils.isNotEmpty( this.searchContent ) && this.searchContent.indexOf( "%" ) < 0 ){
				return "%" + searchContent + "%";
			}
			return searchContent;
		}
		public void setSearchContent( String searchContent ) {
			this.searchContent = searchContent;
		}
		public String getCreatorName() {
			return creatorName;
		}
		public void setCreatorName(String creatorName) {
			this.creatorName = creatorName;
		}
		public String getSubjectId() {
			return subjectId;
		}
		public void setSubjectId(String subjectId) {
			this.subjectId = subjectId;
		}
		public String getVoteOptionId() {
			return voteOptionId;
		}
		public void setVoteOptionId(String voteOptionId) {
			this.voteOptionId = voteOptionId;
		}

		public String getCacheKey(EffectivePerson effectivePerson, Boolean isBBSManager) {
			StringBuffer sb = new StringBuffer();
			String pattern = "yyyy-MM-dd HH:mm:ss";
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			if( StringUtils.isNotEmpty( effectivePerson.getDistinguishedName() )) {
				sb.append( effectivePerson.getDistinguishedName() );
			}
			if( StringUtils.isNotEmpty( effectivePerson.getDistinguishedName() )) {
				sb.append( "#" );
				sb.append( isBBSManager );
			}
			if( StringUtils.isNotEmpty( subjectId )) {
				sb.append( "#" );
				sb.append( subjectId );
			}
			if( StringUtils.isNotEmpty( voteOptionId )) {
				sb.append( "#" );
				sb.append( voteOptionId );
			}
			if( StringUtils.isNotEmpty( forumId )) {
				sb.append( "#" );
				sb.append( forumId );
			}
			if( StringUtils.isNotEmpty( mainSectionId )) {
				sb.append( "#" );
				sb.append( mainSectionId );
			}
			if( StringUtils.isNotEmpty( sectionId )) {
				sb.append( "#" );
				sb.append( sectionId );
			}
			if( StringUtils.isNotEmpty( searchContent )) {
				sb.append( "#" );
				sb.append( searchContent );
			}
			if( StringUtils.isNotEmpty( creatorName )) {
				sb.append( "#" );
				sb.append( creatorName );
			}

			if(  startTime != null ) {
				sb.append( "#" );
				sb.append( formatter.format(startTime));
			}
			if(  endTime != null ) {
				sb.append( "#" );
				sb.append( formatter.format(endTime));
			}

			sb.append( "#" );
			sb.append( needPicture );
			sb.append( "#" );
			sb.append( withTopSubject );
			return sb.toString();
		}

		public Date getStartTime() {
			return startTime;
		}
		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}
		public Date getEndTime() {
			return endTime;
		}
		public void setEndTime(Date endTime) {
			this.endTime = endTime;
		}



	}

	public static class Wo extends BBSSubjectInfo{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier< BBSSubjectInfo, Wo > copier = WrapCopierFactory.wo( BBSSubjectInfo.class, Wo.class, null, JpaObject.FieldsInvisible);

		private List<WoSubjectAttachment> subjectAttachmentList;

		@FieldDescribe( "투표 주제의 전체 투표 선택지 목록." )
		private List<WoBBSVoteOptionGroup> voteOptionGroupList;

		private String content = null;

		private Long voteCount = 0L;

		private String pictureBase64 = null;

		@FieldDescribe("최근 답글 사용자 닉네임")
		private String latestReplyUserNickName = "";

		@FieldDescribe( "홈 추천자 이름" )
		private String bBSIndexSetterNameShort = "";

		@FieldDescribe( "우수 주제 설정자 이름" )
		private String screamSetterNameShort = "";

		@FieldDescribe( "원작 설정자 이름" )
		private String originalSetterNameShort = "";

		@FieldDescribe( "작성자 이름" )
		private String creatorNameShort = "";

		@FieldDescribe( "심사자 이름" )
		private String auditorNameShort = "";

		@FieldDescribe( "현재 사용자의 투표 여부." )
		private Boolean voted = false;

		public String getbBSIndexSetterNameShort() {
			return bBSIndexSetterNameShort;
		}

		public String getScreamSetterNameShort() {
			return screamSetterNameShort;
		}

		public String getOriginalSetterNameShort() {
			return originalSetterNameShort;
		}

		public String getCreatorNameShort() {
			return creatorNameShort;
		}

		public String getAuditorNameShort() {
			return auditorNameShort;
		}

		public void setbBSIndexSetterNameShort(String bBSIndexSetterNameShort) {
			this.bBSIndexSetterNameShort = bBSIndexSetterNameShort;
		}

		public void setScreamSetterNameShort(String screamSetterNameShort) {
			this.screamSetterNameShort = screamSetterNameShort;
		}

		public void setOriginalSetterNameShort(String originalSetterNameShort) {
			this.originalSetterNameShort = originalSetterNameShort;
		}

		public void setCreatorNameShort(String creatorNameShort) {
			this.creatorNameShort = creatorNameShort;
		}

		public void setAuditorNameShort(String auditorNameShort) {
			this.auditorNameShort = auditorNameShort;
		}

		public List<WoSubjectAttachment> getSubjectAttachmentList() {
			return subjectAttachmentList;
		}

		public void setSubjectAttachmentList(List<WoSubjectAttachment> subjectAttachmentList) {
			this.subjectAttachmentList = subjectAttachmentList;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getPictureBase64() {
			return pictureBase64;
		}

		public void setPictureBase64(String pictureBase64) {
			this.pictureBase64 = pictureBase64;
		}

		public List<WoBBSVoteOptionGroup> getVoteOptionGroupList() {
			return voteOptionGroupList;
		}

		public void setVoteOptionGroupList(List<WoBBSVoteOptionGroup> voteOptionGroupList) {
			this.voteOptionGroupList = voteOptionGroupList;
		}

		public Boolean getVoted() {
			return voted;
		}

		public void setVoted(Boolean voted) {
			this.voted = voted;
		}

		public Long getVoteCount() {
			return voteCount;
		}

		public void setVoteCount(Long voteCount) {
			this.voteCount = voteCount;
		}

		public String getLatestReplyUserNickName() {
			return latestReplyUserNickName;
		}

		public void setLatestReplyUserNickName(String latestReplyUserNickName) {
			this.latestReplyUserNickName = latestReplyUserNickName;
		}
	}

	public static class WoSubjectAttachment extends BBSSubjectAttachment{

		private static final long serialVersionUID = -5076990764713538973L;


		public static WrapCopier< BBSSubjectAttachment, WoSubjectAttachment > copier = WrapCopierFactory.wo( BBSSubjectAttachment.class, WoSubjectAttachment.class, null, JpaObject.FieldsInvisible);
	}

	public static class WoBBSVoteOptionGroup extends BBSVoteOptionGroup{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier< BBSVoteOptionGroup, WoBBSVoteOptionGroup > copier = WrapCopierFactory.wo( BBSVoteOptionGroup.class, WoBBSVoteOptionGroup.class, null, JpaObject.FieldsInvisible);

		private List<WoBBSVoteOption> voteOptions = null;

		public List<WoBBSVoteOption> getVoteOptions() {
			return voteOptions;
		}

		public void setVoteOptions(List<WoBBSVoteOption> voteOptions) {
			this.voteOptions = voteOptions;
		}
	}

	public static class WoBBSVoteOption extends BBSVoteOption{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier< BBSVoteOption, WoBBSVoteOption > copier = WrapCopierFactory.wo( BBSVoteOption.class, WoBBSVoteOption.class, null, JpaObject.FieldsInvisible);

		private Boolean voted = false;

		public Boolean getVoted() {
			return voted;
		}

		public void setVoted(Boolean voted) {
			this.voted = voted;
		}
	}
}
