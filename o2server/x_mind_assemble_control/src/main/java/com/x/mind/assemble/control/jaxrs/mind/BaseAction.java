package com.x.mind.assemble.control.jaxrs.mind;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.mind.assemble.control.service.MindFolderInfoService;
import com.x.mind.assemble.control.service.MindInfoService;
import com.x.mind.assemble.control.service.UserManagerService;

public class BaseAction extends StandardJaxrsAction{
	/**
	 * 마인드맵 정보 작업 서비스
	 */
	protected MindInfoService mindInfoService = new MindInfoService();
	
	/**
	 * 마인드맵 폴더 정보 작업 서비스
	 */
	protected MindFolderInfoService mindFolderInfoService = new MindFolderInfoService();
	
	/**
	 * 사용자 정보 관리 서비스
	 */
	protected UserManagerService userManagerService = new UserManagerService();
}
