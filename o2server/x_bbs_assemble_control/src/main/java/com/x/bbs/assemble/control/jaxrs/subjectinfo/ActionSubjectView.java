package com.x.bbs.assemble.control.jaxrs.subjectinfo;

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
import com.x.bbs.assemble.control.Business;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectContentQueryById;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectFilter;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectIdEmpty;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectNotExists;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectView;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectWrapOut;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionVoteOptionListById;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionVoteResultQueryById;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.bbs.entity.BBSVoteOption;
import com.x.bbs.entity.BBSVoteOptionGroup;

public class ActionSubjectView extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger( ActionSubjectView.class );

	@SuppressWarnings("unchecked")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
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
				result = getSubjectViewQueryResult( id, request, effectivePerson );
				CacheManager.put( cacheCategory, cacheKey, result );
			}
		}

		if( check && result.getData() != null ) {
			WoBBSSubjectInfo currentSubject = result.getData().getCurrentSubject();
			if (check) {
				if( currentSubject != null ){//해당 주제의 투표 선택지 그룹 조회
					List<BBSVoteOption> voteOptionList = null;
					List<BBSVoteOptionGroup> voteOptionGroupList = null;
					List<WoBBSVoteOptionGroup> wrapOutSubjectVoteOptionGroupList = null;
					List<WoBBSVoteOption> wrapOutSubjectVoteOptionList = null;

					currentSubject.setVoted( false );

					try {
						voteOptionGroupList = subjectVoteService.listVoteOptionGroup( id );
						if( ListTools.isNotEmpty( voteOptionGroupList ) ){
							wrapOutSubjectVoteOptionGroupList = WoBBSVoteOptionGroup.copier.copy( voteOptionGroupList );
							for( WoBBSVoteOptionGroup group : wrapOutSubjectVoteOptionGroupList ){
								voteOptionList = subjectVoteService.listVoteOptionByGroupId( group.getId() );
								if( ListTools.isNotEmpty( voteOptionList ) ){
									try {
										wrapOutSubjectVoteOptionList = WoBBSVoteOption.copier.copy( voteOptionList );
										for( WoBBSVoteOption wrapOutBBSVoteOption: wrapOutSubjectVoteOptionList ){
											wrapOutBBSVoteOption.setVoted( false );
											try {
												if( subjectVoteService.optionHasVoted( effectivePerson, wrapOutBBSVoteOption.getId() )){
													//이미 투표했는지 판단
													wrapOutBBSVoteOption.setVoted( true );
													currentSubject.setVoted( true );
												}
											} catch (Exception e) {
												check = false;
												Exception exception = new ExceptionVoteResultQueryById( e, id );
												result.error( exception );
												logger.error( e, effectivePerson, request, null);
											}
										}
										group.setVoteOptions( wrapOutSubjectVoteOptionList );
									} catch (Exception e) {
										check = false;
										Exception exception = new ExceptionSubjectWrapOut( e );
										result.error( exception );
										logger.error( e, effectivePerson, request, null);
									}
								}
							}
							currentSubject.setVoteOptionGroupList( wrapOutSubjectVoteOptionGroupList );
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionVoteOptionListById( e, id );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
					try {
						//투표 총수 조회
						Long voteCount = subjectVoteService.countVoteRecordForSubject( id, null );
						currentSubject.setVoteCount( voteCount );
					}catch (Exception e) {
						check = false;
						Exception exception = new ExceptionVoteOptionListById( e, id );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}

					try {
						//투표 총 인원 조회
						List<String> voteUsers = subjectVoteService.listVoteUserForSubject( id, null );
						currentSubject.setVoteUserCount( voteUsers.size() );
					}catch (Exception e) {
						check = false;
						Exception exception = new ExceptionVoteOptionListById( e, id );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}

//					try {
//						voteOptionList = subjectVoteService.listVoteOption( id );
//					} catch (Exception e) {
//						check = false;
//						Exception exception = new ExceptionVoteOptionListById( e, id );
//						result.error( exception );
//						logger.error( e, effectivePerson, request, null);
//					}
					result.getData().setCurrentSubject(currentSubject);
				}
			}
		}

		if( check ){
			try {
				// 조회 수 +1
				subjectInfoServiceAdv.addViewCount( id );
			}catch(Exception e) {
				check = false;
				Exception exception = new ExceptionSubjectView( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

	private ActionResult<Wo> getSubjectViewQueryResult(String id, HttpServletRequest request, EffectivePerson effectivePerson) {
		ActionResult<Wo> result = new ActionResult<>();
		List<WoSubjectAttachment> wrapSubjectAttachmentList = null;
		List<BBSSubjectAttachment> subjectAttachmentList = null;
		Wo wrapOutNearSubjectInfo = new Wo();
		WoBBSSubjectInfo lastSubject = null;
		WoBBSSubjectInfo currentSubject = null;
		WoBBSSubjectInfo nextSubject = null;
		BBSSubjectInfo subjectInfo = null;
		String subjectContent = null;
		Boolean check = true;

		if (check) {//게시판 정보 존재 여부 조회
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSubjectView( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if (check) {
			if ( subjectInfo == null ) {
				check = false;
				Exception exception = new ExceptionSubjectNotExists( id );
				result.error( exception );
			}else{//주제 정보 조회됨
				try {
					currentSubject = WoBBSSubjectInfo.copier.copy( subjectInfo );

					//첨부파일 ID 목록으로 첨부파일 정보 조회
					if( currentSubject.getAttachmentList() != null && currentSubject.getAttachmentList().size() > 0 ){
						subjectAttachmentList = subjectInfoServiceAdv.listAttachmentByIds( currentSubject.getAttachmentList() );
						if( subjectAttachmentList != null && subjectAttachmentList.size() > 0 ){
							wrapSubjectAttachmentList = WoSubjectAttachment.copier.copy( subjectAttachmentList );
							currentSubject.setSubjectAttachmentList( wrapSubjectAttachmentList );
						}
					}
					wrapOutNearSubjectInfo.setCurrentSubject( currentSubject );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSubjectWrapOut( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}

		if (check) {
			if( wrapOutNearSubjectInfo.getCurrentSubject() != null ){
				currentSubject = wrapOutNearSubjectInfo.getCurrentSubject();
				//주제 내용 정보 채우기
				try {
					subjectContent = subjectInfoServiceAdv.getSubjectContent( id );
					if( subjectContent != null ){
						currentSubject.setContent( subjectContent );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSubjectContentQueryById( e, id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}

		//이전 주제 정보 조회 시작
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.getLastSubject( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSubjectFilter( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if( subjectInfo != null ){
				lastSubject = new WoBBSSubjectInfo();
				lastSubject.setId( subjectInfo.getId() );
				lastSubject.setTitle( subjectInfo.getTitle() );
				wrapOutNearSubjectInfo.setLastSubject( lastSubject );
			}
		}

		//다음 주제 정보 조회 시작
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.getNextSubject( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSubjectFilter( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if( subjectInfo != null ){
				nextSubject = new WoBBSSubjectInfo();
				nextSubject.setId( subjectInfo.getId() );
				nextSubject.setTitle( subjectInfo.getTitle() );
				wrapOutNearSubjectInfo.setNextSubject( nextSubject );
			}
		}
		//@ 형식의 사용자 식별자를 사용자 이름으로 바꿔 xxShort 속성에 할당
		cutPersonNames( wrapOutNearSubjectInfo.getCurrentSubject() );

		result.setData( wrapOutNearSubjectInfo );
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
	private void cutPersonNames( WoBBSSubjectInfo subject ) {
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

	public static class Wo{

		private WoBBSSubjectInfo lastSubject = null;

		private WoBBSSubjectInfo currentSubject = null;

		private WoBBSSubjectInfo nextSubject = null;

		public WoBBSSubjectInfo getLastSubject() {
			return lastSubject;
		}

		public void setLastSubject(WoBBSSubjectInfo lastSubject) {
			this.lastSubject = lastSubject;
		}

		public WoBBSSubjectInfo getNextSubject() {
			return nextSubject;
		}

		public void setNextSubject(WoBBSSubjectInfo nextSubject) {
			this.nextSubject = nextSubject;
		}

		public WoBBSSubjectInfo getCurrentSubject() {
			return currentSubject;
		}
		public void setCurrentSubject(WoBBSSubjectInfo currentSubject) {
			this.currentSubject = currentSubject;
		}
	}

	public static class WoBBSSubjectInfo extends BBSSubjectInfo{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier< BBSSubjectInfo, WoBBSSubjectInfo > copier = WrapCopierFactory.wo( BBSSubjectInfo.class, WoBBSSubjectInfo.class, null, JpaObject.FieldsInvisible);

		private List<WoSubjectAttachment> subjectAttachmentList;

		@FieldDescribe( "투표 주제의 전체 투표 선택지 목록." )
		private List<WoBBSVoteOptionGroup> voteOptionGroupList;

		@FieldDescribe( "원글 내용" )
		private String content = null;

		@FieldDescribe( "투표 총수" )
		private Long voteCount = 0L;

		@FieldDescribe( "투표 인원" )
		private Integer voteUserCount = 0;

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

		public Integer getVoteUserCount() { return voteUserCount; }

		public void setVoteUserCount(Integer voteUserCount) { this.voteUserCount = voteUserCount; }

		public String getLatestReplyUserNickName() {
			return latestReplyUserNickName;
		}

		public void setLatestReplyUserNickName(String latestReplyUserNickName) {
			this.latestReplyUserNickName = latestReplyUserNickName;
		}

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
	}

	public static class WoSubjectAttachment extends BBSSubjectAttachment{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier< BBSSubjectAttachment, WoSubjectAttachment > copier = WrapCopierFactory.wo( BBSSubjectAttachment.class, WoSubjectAttachment.class, null, JpaObject.FieldsInvisible);
	}

	public static class WoBBSVoteOptionGroup extends BBSVoteOptionGroup{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

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

		public static List<String> Excludes = new ArrayList<String>();

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
