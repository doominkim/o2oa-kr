package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.lib.util.StringUtil;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionInfo;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionName;
import com.x.cms.core.entity.Document;

public class PermissionOperateService {
	
	private static Logger logger = LoggerFactory.getLogger(PermissionOperateService.class);

	private static ReentrantLock lock = new ReentrantLock();
	/**
	 * 문서 ID로 문서에 사용자 접근 및 관리 권한 설정
	 * @param docId
	 * @param readerList
	 * @param authorList
	 * @throws Exception
	 */
	public Document refreshDocumentPermission( String docId, List<PermissionInfo> readerList, List<PermissionInfo> authorList ) throws Exception {
		List<PermissionInfo> permissionList = composeDocmentAllPermissions(readerList, authorList);
		lock.lock();
		try {
			//독자 및 작성자 정보를 데이터베이스에 저장
			return refreshDocumentPermission( docId, permissionList );
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 독자와 작성자로 모든 권한 객체 목록 구성
	 * @param readerList
	 * @param authorList
	 * @throws Exception
	 */
	public List<PermissionInfo> composeDocmentAllPermissions( List<PermissionInfo> readerList, List<PermissionInfo> authorList ) throws Exception {
		
		List<PermissionInfo> permissionList = new ArrayList<>();
		PermissionInfo new_permissionInfo = null;
		//독자 정보 추가, 독자를 제한하지 않으면 분류에 접근 가능한 모든 사용자가 열람 가능
		if ( ListTools.isEmpty( readerList ) ) {
			permissionList.add( new PermissionInfo( PermissionName.READER, "모든 사용자", "모든 사용자", "모든 사용자" ) );
		} else {
			for( PermissionInfo p : readerList ) {				
//				System.out.println(">>>>>readerList:" + p.getPermissionObjectCode());
				new_permissionInfo = createPermissionInfo( PermissionName.READER, p.getPermissionObjectType(), p.getPermissionObjectCode(), p.getPermissionObjectName() );
				if( new_permissionInfo != null ) {
					if( StringUtils.isEmpty( new_permissionInfo.getPermissionObjectCode() )) {
						new_permissionInfo.setPermissionObjectCode( new_permissionInfo.getPermissionObjectName() );
					}					
					permissionList.add( new_permissionInfo );
				}
			}
		}
		
		//모든 작성자를 독자에 추가
		if ( authorList != null && !authorList.isEmpty() ) {
			for( PermissionInfo p : authorList ) {
//				System.out.println(">>>>>authorList:" + p.getPermissionObjectCode());
				//p가 모든 사람이면 모든 사람이 아닌 독자가 있는지 판단해야 하며, 있으면 모든 사람 열람을 추가할 수 없음
				Boolean existsReaderPermission = false;
				if( StringUtils.equals( p.getPermissionObjectCode(), "모든 사용자")) {
					//모든 사람 열람이 아닌 권한이 있는지 판단
					for(  PermissionInfo p1 : permissionList ) {
						if( ( StringUtils.equals( p1.getPermission(), "독자") || StringUtils.equals( p1.getPermission(), "열람"))
								&& !StringUtils.equals( p1.getPermissionObjectCode(),  "모든 사용자" )) {
							existsReaderPermission = true;
						}
					}
				}
				
				if( !existsReaderPermission ) {
					new_permissionInfo = createPermissionInfo( PermissionName.READER, p.getPermissionObjectType(), p.getPermissionObjectCode(), p.getPermissionObjectName() );
					if( new_permissionInfo != null ) {
						if( StringUtils.isEmpty( new_permissionInfo.getPermissionObjectCode() )) {
							new_permissionInfo.setPermissionObjectCode( new_permissionInfo.getPermissionObjectName() );
						}
						permissionList.add( new_permissionInfo );
					}
				}
				
								
				//작성자 정보 추가, 작성자는 문서 편집 가능
				new_permissionInfo = createPermissionInfo( PermissionName.AUTHOR, p.getPermissionObjectType(), p.getPermissionObjectCode(), p.getPermissionObjectName() );
				if( new_permissionInfo != null ) {
					if( StringUtils.isEmpty( new_permissionInfo.getPermissionObjectCode() )) {
						new_permissionInfo.setPermissionObjectCode( new_permissionInfo.getPermissionObjectName() );
					}
					permissionList.add( new_permissionInfo );
				}
			}
		}		
		return permissionList;
	}
	
	
	private PermissionInfo createPermissionInfo( String permission, String permissionObjectType, String permissionObjectCode, String permissionObjectName ) throws Exception {
		//신분 정보가 있으면 신분 정보를 개인 정보로 대체
		String personFlag = null;
		if( StringUtils.isEmpty( permissionObjectCode ) ) {
			permissionObjectCode = permissionObjectName;
		}
		if( StringUtil.isNotEmpty( permissionObjectCode ) && permissionObjectCode.endsWith("@I") ) {
			//신분을 개인으로 변환 @P
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				personFlag = new Business(emc).organization().person().getWithIdentity( permissionObjectCode );
			}
			if( StringUtil.isNotEmpty( personFlag )  ) {
				if( StringUtil.isNotEmpty( permissionObjectName ) && permissionObjectName.endsWith("@I")) {
					permissionObjectName = personFlag;
				}
				return new PermissionInfo(permission, permissionObjectType, personFlag, permissionObjectName);
			}else {
				logger.warn("can not find person with identity. Identity:" + permissionObjectCode );
				return null;
			}			
		}else {
			return new PermissionInfo(permission, permissionObjectType, permissionObjectCode, permissionObjectName);
		}
	}
	
	/**
	 * 문서 ID로 문서에 사용자 접근 및 관리 권한 설정
	 * @param docId
	 * @param permissionList
	 * @throws Exception 
	 */
	public Document refreshDocumentPermission( String docId, List<PermissionInfo> permissionList ) throws Exception {
		if( StringUtils.isEmpty(docId) ){
			throw new Exception( "docId is empty！" );
		}
		logger.info("dear refreshDocumentPermission doc:{}",docId);
		Document document = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			document = emc.find( docId, Document.class );
			if( document != null ) {
				//문서 권한 정보 초기화
				document.setManagerList( null );
				document.setReadPersonList( null );
				document.setReadUnitList( null );
				document.setReadGroupList( null );
				document.setAuthorPersonList( null );
				document.setAuthorUnitList( null );
				document.setAuthorGroupList( null );
				
				if( ListTools.isNotEmpty( permissionList ) ){
					for( PermissionInfo permission : permissionList ){					
						if( "관리".equals( permission.getPermission() )) {
							document.addToManagerList(permission.getPermissionObjectCode());
						}else if( "독자".equals( permission.getPermission() ) || "열람".equals( permission.getPermission() )) {
							if( "사용자".equals( permission.getPermissionObjectType() )) {
								document.addToReadPersonList(permission.getPermissionObjectCode());
							}else if( "부서".equals(  permission.getPermissionObjectType() ) || "조직".equals( permission.getPermissionObjectType() )) {
								document.addToReadUnitList(permission.getPermissionObjectCode());
							}else if( "그룹".equals( permission.getPermissionObjectType() )) {
								document.addToReadGroupList(permission.getPermissionObjectCode());
							}else if( "모든 사용자".equals( permission.getPermissionObjectCode() )) {
								document.addToReadPersonList("모든 사용자");
							}
						}else if( "작성자".equals( permission.getPermission() )) {
							if( "사용자".equals( permission.getPermissionObjectType() )) {
								document.addToAuthorPersonList(permission.getPermissionObjectCode());
							}else if( "부서".equals(  permission.getPermissionObjectType() ) || "조직".equals( permission.getPermissionObjectType() )) {
								document.addToAuthorUnitList(permission.getPermissionObjectCode());
							}else if( "그룹".equals( permission.getPermissionObjectType() )) {
								document.addToAuthorGroupList(permission.getPermissionObjectCode());
							}else if( "모든 사용자".equals( permission.getPermissionObjectCode() )) {
								document.addToAuthorPersonList("모든 사용자");
							}
						}
					}
				}
				
				if( ListTools.isEmpty( document.getReadPersonList() ) && ListTools.isEmpty( document.getReadUnitList() ) 
						&& ListTools.isEmpty( document.getReadGroupList() )) {
					//열람 범위가 모두 비어 있으면 모든 사람이 접근 가능
					document.addToReadPersonList("모든 사용자");
					document.addToReadPersonList(document.getCreatorPerson());
				}
				if( ListTools.isEmpty( document.getAuthorPersonList() ) && ListTools.isEmpty( document.getAuthorUnitList() ) 
						&& ListTools.isEmpty( document.getAuthorGroupList() )) {
					//편집 범위가 모두 비어 있으면 작성자가 편집 가능
					document.addToReadPersonList( document.getCreatorPerson() );
					document.addToAuthorPersonList( document.getCreatorPerson() );
				}
				if( ListTools.isEmpty( document.getManagerList() ) ) {
					//관리 범위가 모두 비어 있으면 작성자가 관리 가능
					document.addToReadPersonList( document.getCreatorPerson() );
					document.addToManagerList( document.getCreatorPerson() );
				}
				emc.beginTransaction( Document.class );
				emc.check( document , CheckPersistType.all );
				emc.commit();
			}		
		} catch ( Exception e ) {
			logger.warn("refreshDocumentPermission doc error:{}",document);
			throw e;
		}

		return document;
	}

//	private boolean existsPermission(List<PermissionInfo> permissionList, String objectType, String permissionCode) {
//		if( ListTools.isNotEmpty( permissionList )){
//			for ( PermissionInfo permission : permissionList ){
//				if( StringUtils.equalsIgnoreCase( permission.getPermissionObjectCode(), permissionCode ) &&
//						StringUtils.equalsIgnoreCase( permission.getPermissionObjectType(), objectType )){
//					return true;
//				}
//			}
//		}
//		return false;
//	}
}
