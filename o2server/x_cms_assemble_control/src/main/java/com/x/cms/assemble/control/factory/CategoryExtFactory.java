package com.x.cms.assemble.control.factory;

import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.CategoryExt;

/**
 * 분류 확장 정보 기본 기능 서비스 클래스
 * 
 * @author O2LEE
 */
public class CategoryExtFactory extends AbstractFactory {

	public CategoryExtFactory(Business business) throws Exception {
		super(business);
	}

	public CategoryExt get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, CategoryExt.class );
	}
	
	public String getContent( String id ) throws Exception {
		CategoryExt categoryExt = this.entityManagerContainer().find( id, CategoryExt.class );
		if( categoryExt != null ) {
			return categoryExt.getContent();
		}
		return null;
	}
}