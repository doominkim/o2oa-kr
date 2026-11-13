package com.x.bbs.assemble.control.factory;

import com.x.bbs.assemble.control.AbstractFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSSubjectVoteResult;

/**
 * 클래스명: BBSSubjectVoteResultFactory<br/>
 * 엔티티 클래스: BBSSubjectVoteResult<br/>
 * 작성자: Liyi<br/>
 * 소속: O2 Team<br/>
 * 날짜: 2016-05-20 17:17:26
**/
public class BBSSubjectVoteResultFactory extends AbstractFactory {

	public BBSSubjectVoteResultFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "지정 Id의 BBSSubjectVoteResult 엔티티 정보 객체 조회" )
	public BBSSubjectVoteResult get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSSubjectVoteResult.class );
	}
	
	//@MethodDescribe( "지정 Id의 BBSSubjectVoteResult 엔티티 정보 객체 조회" )
	public String getStatisticResult( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		BBSSubjectVoteResult subjectVoteResult = this.entityManagerContainer().find( id, BBSSubjectVoteResult.class );
		if( subjectVoteResult != null ){
			return subjectVoteResult.getStatisticContent();
		}
		return null;
	}
}
