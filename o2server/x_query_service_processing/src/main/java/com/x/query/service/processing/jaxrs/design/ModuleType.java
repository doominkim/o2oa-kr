package com.x.query.service.processing.jaxrs.design;

import com.x.base.core.entity.JpaObject;

/**
 * processPlatform(프로세스 관리 플랫폼)
 * portal(포털 관리 플랫폼)
 * cms(콘텐츠 관리 플랫폼)
 * query (데이터 센터 플랫폼)
 * service(서비스 관리 플랫폼)
 */
public enum ModuleType {

	processPlatform, portal, cms, query, service;
	public static final int length = JpaObject.length_64B;
}
