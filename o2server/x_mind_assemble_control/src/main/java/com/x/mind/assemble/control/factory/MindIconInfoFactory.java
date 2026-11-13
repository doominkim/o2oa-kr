package com.x.mind.assemble.control.factory;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.mind.assemble.control.AbstractFactory;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindIconInfo;


/**
 * 클래스 이름: MindIconInfoFactory<br/>
 * 엔티티 클래스: MindIconInfo<br/>
 * 작성자: O2LEE<br/>
 * 소속: O2 Team<br/>
 * 일시: 2016-11-15 17:17:26 
**/
public class MindIconInfoFactory extends AbstractFactory {

	public MindIconInfoFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 지정 Id의 마인드맵 썸네일 조회
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MindIconInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, MindIconInfo.class, ExceptionWhen.none );
	}
	
}
