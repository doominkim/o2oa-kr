package com.x.mind.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.mind.assemble.control.factory.MindBaseInfoFactory;
import com.x.mind.assemble.control.factory.MindContentInfoFactory;
import com.x.mind.assemble.control.factory.MindFolderInfoFactory;
import com.x.mind.assemble.control.factory.MindIconInfoFactory;
import com.x.mind.assemble.control.factory.MindRecycleInfoFactory;
import com.x.mind.assemble.control.factory.MindShareRecordFactory;
import com.x.mind.assemble.control.factory.MindVersionInfoFactory;
import com.x.organization.core.express.Organization;

public class Business {

	private EntityManagerContainer emc;
	
	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}
	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}
	
	private Organization organization;
	private MindBaseInfoFactory mindBaseInfoFactory;
	private MindContentInfoFactory mindContentInfoFactory;
	private MindFolderInfoFactory mindFolderInfoFactory;
	private MindRecycleInfoFactory mindRecycleInfoFactory;
	private MindShareRecordFactory mindShareInfoFactory;
	private MindIconInfoFactory mindIconInfoFactory;
	private MindVersionInfoFactory mindVersionInfoFactory;
	
	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}
	
	/**
	 * 마인드맵 버전 정보 데이터 서비스 팩토리 클래스
	 * @return
	 * @throws Exception
	 */
	public MindVersionInfoFactory mindVersionInfoFactory() throws Exception {
		if (null == this.mindVersionInfoFactory) {
			this.mindVersionInfoFactory = new MindVersionInfoFactory( this );
		}
		return mindVersionInfoFactory;
	}
	
	/**
	 * 마인드맵 썸네일 데이터 서비스 팩토리 클래스
	 * @return
	 * @throws Exception
	 */
	public MindIconInfoFactory mindIconInfoFactory() throws Exception {
		if (null == this.mindIconInfoFactory) {
			this.mindIconInfoFactory = new MindIconInfoFactory( this );
		}
		return mindIconInfoFactory;
	}
	
	/**
	 * 마인드맵 공유 정보 데이터베이스 작업 팩토리 클래스
	 * @return
	 * @throws Exception
	 */
	public MindShareRecordFactory mindShareRecordFactory() throws Exception {
		if (null == this.mindShareInfoFactory) {
			this.mindShareInfoFactory = new MindShareRecordFactory( this );
		}
		return mindShareInfoFactory;
	}
	
	/**
	 * 마인드맵 기본 정보 데이터베이스 작업 팩토리 클래스
	 * @return
	 * @throws Exception
	 */
	public MindBaseInfoFactory mindBaseInfoFactory() throws Exception {
		if (null == this.mindBaseInfoFactory) {
			this.mindBaseInfoFactory = new MindBaseInfoFactory( this );
		}
		return mindBaseInfoFactory;
	}
	
	/**
	 * 마인드맵 내용 정보 데이터베이스 작업 팩토리 클래스
	 * @return
	 * @throws Exception
	 */
	public MindContentInfoFactory mindContentInfoFactory() throws Exception {
		if (null == this.mindContentInfoFactory) {
			this.mindContentInfoFactory = new MindContentInfoFactory( this );
		}
		return mindContentInfoFactory;
	}
	
	/**
	 * 마인드맵 폴더 정보 데이터베이스 작업 팩토리 클래스
	 * @return
	 * @throws Exception
	 */
	public MindFolderInfoFactory mindFolderInfoFactory() throws Exception {
		if (null == this.mindFolderInfoFactory) {
			this.mindFolderInfoFactory = new MindFolderInfoFactory( this );
		}
		return mindFolderInfoFactory;
	}
	
	/**
	 * 휴지통 마인드맵 정보 데이터베이스 작업 팩토리 클래스
	 * @return
	 * @throws Exception
	 */
	public MindRecycleInfoFactory mindRecycleInfoFactory() throws Exception {
		if (null == this.mindRecycleInfoFactory) {
			this.mindRecycleInfoFactory = new MindRecycleInfoFactory( this );
		}
		return mindRecycleInfoFactory;
	}
}
