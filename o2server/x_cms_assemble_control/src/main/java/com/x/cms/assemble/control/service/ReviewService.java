package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.x.base.core.project.cache.CacheManager;
import com.x.cms.core.entity.enums.DocumentStatus;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Review;
import com.x.cms.core.express.tools.filter.QueryFilter;

public class ReviewService {

	private UserManagerService userManagerService = new UserManagerService();
	private static Logger logger = LoggerFactory.getLogger( ReviewService.class );
	/**
	 * 권한과 조건에 맞는 열람 가능한 모든 Review 총수 조회
	 * @param emc
	 * @param personName
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countViewableWithFilter( EntityManagerContainer emc, String personName, QueryFilter queryFilter ) throws Exception {
		Business business = new Business(emc);
		return business.reviewFactory().countWithFilter( personName, queryFilter );
	}

	/**
	 * 권한과 조건에 맞는 열람 가능한 문서 Review 정보 목록 조회
	 * @param emc
	 * @param pageSize
	 * @param docSequence
	 * @param orderField
	 * @param orderType
	 * @param person
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Review> listNextViewableWithFilter( EntityManagerContainer emc, Integer pageSize, String docSequence, String orderField,
			String orderType, String person, QueryFilter queryFilter ) throws Exception {
		Business business = new Business(emc);
		return business.reviewFactory().listNextWithFilter( pageSize, docSequence, orderField, orderType, person, queryFilter);
	}

	/**
	 * 권한과 조건에 맞는 열람 가능한 문서 Review 정보 목록 조회
	 * @param emc
	 * @param pageSize
	 * @param docSequence
	 * @param orderField
	 * @param orderType
	 * @param person
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<Review> listPrevViewableWithFilter( EntityManagerContainer emc, Integer pageSize, String docSequence, String orderField,
													String orderType, String person, QueryFilter queryFilter ) throws Exception {
		Business business = new Business(emc);
		return business.reviewFactory().listPrevWithFilter( pageSize, docSequence, orderField, orderType, person, queryFilter);
	}

	/**
	 * 조건에 맞는 문서 정보 Review 목록을 지정한 건수만큼 조회
	 * @param emc
	 * @param orderField
	 * @param orderType
	 * @param person
	 * @param queryFilter
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<Review> listViewableWithFilter(EntityManagerContainer emc, String orderField, String orderType,
			String person, QueryFilter queryFilter, int maxCount ) throws Exception {
		Business business = new Business(emc);
		return business.reviewFactory().listWithFilter( orderField, orderType, person, queryFilter, maxCount);
	}

	public List<String> listDocIdsWithConditionInReview( EntityManagerContainer emc, String personName, QueryFilter queryFilter, Integer maxCount ) throws Exception {
		Business business = new Business(emc);
		return business.reviewFactory().listDocIdsWithConditionInReview( personName, queryFilter, maxCount);
	}

	public List<String> listDocIdsWithConditionInReview( EntityManagerContainer emc, String personName, String orderField, String orderType, QueryFilter queryFilter, Integer maxCount ) throws Exception {
		Business business = new Business(emc);
		return business.reviewFactory().listDocIdsWithConditionInReview( personName, orderField, orderType, queryFilter, maxCount);
	}

	/**
	 * 지정 문서의 권한 정보로 모든 Review 정보 초기화 또는 새로 고침
	 * @param emc
	 * @param docId
	 * @throws Exception
	 */
	public boolean refreshDocumentReview( EntityManagerContainer emc, String docId ) throws Exception {
		boolean fullRead = false;
		Document document = emc.find( docId, Document.class );

		if( document != null ) {
			AppInfo appInfo = emc.find( document.getAppId(), AppInfo.class );
			CategoryInfo categoryInfo = emc.find( document.getCategoryId(), CategoryInfo.class );

			if( DocumentStatus.DRAFT.getValue().equals( document.getDocStatus() ) ) {
				logger.debug( "refreshDocumentReview -> refresh review for draft document: " + document.getTitle() );
				//초안은 기안자만 볼 수 있음
				List<String> persons = new ArrayList<>();
				persons.add( document.getCreatorPerson() );
				logger.debug( "refreshDocumentReview -> there are "+ persons.size() +" permission in this document: " + document.getTitle() );
				refreshDocumentReview( emc, document, persons );
			}else if( DocumentStatus.PUBLISHED.getValue().equals( document.getDocStatus() ) ||
					DocumentStatus.WAIT_PUBLISH.getValue().equals( document.getDocStatus() )) {
				logger.debug( "refreshDocumentReview -> refresh review for published document: " + document.getTitle() );
				List<String> persons = listPermissionPersons( appInfo, categoryInfo, document );
				if(persons.contains("*")){
					fullRead = true;
				}
				//문서의 새 권한을 데이터베이스의 권한과 비교하여 생성 또는 갱신
				logger.debug( "refreshDocumentReview -> there are "+ persons.size() +" permission in this document: " + document.getTitle() );
				refreshDocumentReview( emc, document, persons );
			}else if( DocumentStatus.ARCHIVED.getValue().equals( document.getDocStatus() ) ) {
				logger.debug( "refreshDocumentReview -> refresh review for archived document: " + document.getTitle() );
				//보관된 문서는 관리자와 기안자만 볼 수 있어야 함
				List<String> persons = listPublishAndManagePersons( appInfo, categoryInfo, document );
				logger.debug( "refreshDocumentReview -> there are "+ persons.size() +" permission in this document: " + document.getTitle() );
				refreshDocumentReview( emc, document, persons );
			}
		}else {
			logger.debug( "refreshDocumentReview -> document not exists: " + docId );
		}
		return fullRead;
	}

	private List<String> listPublishAndManagePersons(AppInfo appInfo, CategoryInfo categoryInfo, Document document) throws Exception {
		List<String> persons = new ArrayList<>();
		persons.add( document.getCreatorPerson() ); //작성자
		persons = addListToList( persons, categoryInfo.getPublishablePersonList() );
		persons = addPermissionObj( persons, categoryInfo.getPublishableUnitList() );
		persons = addPermissionObj( persons, categoryInfo.getPublishableGroupList() );
		persons = addListToList( persons, categoryInfo.getManageablePersonList() );
		persons = addPermissionObj( persons, categoryInfo.getManageableUnitList() );
		persons = addPermissionObj( persons, categoryInfo.getManageableGroupList() );
		persons = addListToList( persons, appInfo.getManageablePersonList() );
		persons = addPermissionObj( persons, appInfo.getManageableUnitList() );
		persons = addPermissionObj( persons, appInfo.getManageableGroupList() );
		persons = addListToList( persons, appInfo.getPublishablePersonList() );
		persons = addPermissionObj( persons, appInfo.getPublishableUnitList() );
		persons = addPermissionObj( persons, appInfo.getPublishableGroupList() );
		return persons;
	}

	/**
	 * 문서에 관련된 모든 권한을 사용자로 변환, 데이터 문서이면 전원 열람으로 설정
	 * @param appInfo
	 * @param categoryInfo
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public List<String> listPermissionPersons( AppInfo appInfo, CategoryInfo categoryInfo, Document document ) throws Exception {
		List<String> permissionObjs = new ArrayList<>();
		Boolean documentHasPermissionControl = documentViewPermissionExists(document);
		Boolean categoryHasPermissionControl = categoryPermissionExists(categoryInfo);
		Boolean appInfoHasPermissionControl = appInfoPermissionExists(appInfo);

		if( "数据".equals( document.getDocumentType() )) {
			//데이터는 기본적으로 Review에서 모두 전원 열람으로 설정하고 게시판과 분류로 열람 권한 제어
			permissionObjs.add( "*" );
			return permissionObjs;
		}

		if( !appInfoHasPermissionControl ) {
			if( !categoryHasPermissionControl ) {
				if( !documentHasPermissionControl ) {
					logger.debug("栏目没有阅读权限限制，分类没有阅读权限限制，文档没有阅读权限限制。文档所有人可见：*");
					permissionObjs.add( "*" );
				}else {
					logger.debug("게시판 열람 권한 제한 없음, 분류 열람 권한 제한 없음, 문서 열람 권한 제한 있음. 문서 열람 범위는 문서 권한 기준");
					addDocumentAllPermission( permissionObjs, document, appInfo, categoryInfo);
				}
			}else {//분류 권한 있음
				if( !documentHasPermissionControl ) {
					logger.debug("게시판 열람 권한 제한 없음, 분류 열람 권한 제한 있음, 문서 열람 권한 제한 없음. 문서 열람 범위는 분류 권한 기준");
					addCategoryAllPermission( permissionObjs, document, categoryInfo, appInfo );
				}else {
					logger.debug("게시판 열람 권한 제한 없음, 분류 열람 권한 제한 있음, 문서 열람 권한 제한 있음. 문서 열람 범위는 문서 권한 기준");
					addDocumentAllPermission( permissionObjs, document, appInfo, categoryInfo);
				}
			}
		}else {//게시판 권한 있음
			if( !categoryHasPermissionControl ) {
				if( !documentHasPermissionControl ) {
					logger.debug("게시판 열람 권한 제한 있음, 분류 열람 권한 제한 없음, 문서 열람 권한 제한 없음. 문서 열람 범위는 게시판 권한 기준");
					addAppInfoAllPermission( permissionObjs, document, categoryInfo, appInfo );
				}else {
					logger.debug("게시판 열람 권한 제한 있음, 분류 열람 권한 제한 없음, 문서 열람 권한 제한 있음. 문서 열람 범위는 문서 권한 기준");
					addDocumentAllPermission( permissionObjs, document, appInfo, categoryInfo);
				}
			}else {//분류 권한 있음
				if( !documentHasPermissionControl ) {
					logger.debug("게시판 열람 권한 제한 있음, 분류 열람 권한 제한 있음, 문서 열람 권한 제한 없음. 문서 열람 범위는 분류 권한 기준");
					addCategoryAllPermission( permissionObjs, document, categoryInfo, appInfo);
				}else {
					logger.debug("게시판 열람 권한 제한 있음, 분류 열람 권한 제한 있음, 문서 열람 권한 제한 있음. 문서 열람 범위는 문서 권한 기준");
					addDocumentAllPermission( permissionObjs, document, appInfo, categoryInfo);
				}
			}
		}
		if( permissionObjs.contains("*")) {
			//전원 열람이면 권한 기록 하나만 보존하면 됨
			permissionObjs.clear();
			logger.debug(">>>>>>>Document all person can read. document has read permission *， clean all other permissions. ");
			permissionObjs.add( "*" );
		}
		return permissionObjs;
	}

	/**
	 * 문서에 접근 권한 제어가 있는지 판단
	 * @param document
	 * @return
	 */
	private Boolean documentViewPermissionExists( Document document ) {
		if( document == null ) {
			return false;
		}
		if( ListTools.isNotEmpty( document.getReadPersonList() ) ) {
			if( !document.getReadPersonList().contains( "所有人" )) {
				return true;
			}else{
				return false;
			}
		}
		if( ListTools.isNotEmpty( document.getReadUnitList() ) ) {
			if( !document.getReadUnitList().contains( "所有人" )) {
				return true;
			}
		}
		if( ListTools.isNotEmpty( document.getReadGroupList() ) ) {
			if( !document.getReadGroupList().contains( "所有人" )) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 분류에 접근 권한 제어가 있는지 판단
	 * @param categoryInfo
	 * @return
	 */
	private Boolean categoryPermissionExists(CategoryInfo categoryInfo) {
		if( categoryInfo == null ) {
			return false;
		}
		if( ListTools.isNotEmpty( categoryInfo.getViewablePersonList() ) ) {
			if( !categoryInfo.getViewablePersonList().contains( "所有人" )) {
				return true;
			}
		}
		if( ListTools.isNotEmpty( categoryInfo.getViewableUnitList() ) ) {
			return true;
		}
		if( ListTools.isNotEmpty( categoryInfo.getViewableGroupList() ) ) {
			return true;
		}
		return false;
	}

	/**
	 * 게시판에 접근 권한 제어가 있는지 판단
	 * @param appInfo
	 * @return
	 */
	private Boolean appInfoPermissionExists(AppInfo appInfo) {
		if( appInfo == null ) {
			return false;
		}
		if( ListTools.isNotEmpty( appInfo.getViewablePersonList() ) ) {
			if( !appInfo.getViewablePersonList().contains( "所有人" )) {
				return true;
			}
		}
		if( ListTools.isNotEmpty( appInfo.getViewableUnitList() ) ) {
			return true;
		}
		if( ListTools.isNotEmpty( appInfo.getViewableGroupList() ) ) {
			return true;
		}
		return false;
	}

	/**
	 * 게시판의 모든 권한을 권한 목록에 추가
	 * @param permissionObjs
	 * @param appInfo
	 * @return
	 * @throws Exception
	 */
	private List<String> addAppInfoAllPermission(List<String> permissionObjs, Document document, CategoryInfo categoryInfo, AppInfo appInfo ) throws Exception {
		if( permissionObjs == null ) {
			permissionObjs = new ArrayList<>();
		}
		if( appInfo == null ) {
			return permissionObjs;
		}
		if( ListTools.isNotEmpty( document.getAuthorPersonList() ) ) {
			addPermissionObj( permissionObjs, document.getAuthorPersonList() );
		}
		if( ListTools.isNotEmpty( document.getAuthorUnitList() ) ) {
			addPermissionObj( permissionObjs, document.getAuthorUnitList() );
		}
		if( ListTools.isNotEmpty( document.getAuthorGroupList() ) ) {
			addPermissionObj( permissionObjs, document.getAuthorUnitList() );
		}
		if( ListTools.isNotEmpty( document.getManagerList())) {
			addPermissionObj( permissionObjs, document.getManagerList() );
		}
		if( ListTools.isNotEmpty( appInfo.getViewablePersonList())) {
			permissionObjs = addPermissionObj( permissionObjs, appInfo.getViewablePersonList() );
		}
		if( ListTools.isNotEmpty( appInfo.getViewableUnitList())) {
			permissionObjs = addPermissionObj( permissionObjs, appInfo.getViewableUnitList() );
		}
		if( ListTools.isNotEmpty( appInfo.getViewableGroupList())) {
			permissionObjs = addPermissionObj( permissionObjs, appInfo.getViewableGroupList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getManageablePersonList())) {
			permissionObjs = addPermissionObj( permissionObjs, categoryInfo.getManageablePersonList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getManageableUnitList())) {
			permissionObjs = addPermissionObj( permissionObjs, categoryInfo.getManageableUnitList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getManageableGroupList())) {
			permissionObjs = addPermissionObj( permissionObjs, categoryInfo.getManageableGroupList() );
		}
		if( ListTools.isNotEmpty( appInfo.getManageablePersonList())) {
			permissionObjs = addPermissionObj( permissionObjs, appInfo.getManageablePersonList() );
		}
		if( ListTools.isNotEmpty( appInfo.getManageableUnitList())) {
			permissionObjs = addPermissionObj( permissionObjs, appInfo.getManageableUnitList() );
		}
		if( ListTools.isNotEmpty( appInfo.getManageableGroupList())) {
			permissionObjs = addPermissionObj( permissionObjs, appInfo.getManageableGroupList() );
		}
		return permissionObjs;
	}

	/**
	 * 분류의 모든 권한을 권한 목록에 추가
	 * @param permissionObjs
	 * @param categoryInfo
	 * @return
	 * @throws Exception
	 */
	private List<String> addCategoryAllPermission(List<String> permissionObjs, Document document, CategoryInfo categoryInfo, AppInfo appInfo) throws Exception {
		if( permissionObjs == null ) {
			permissionObjs = new ArrayList<>();
		}
		if( categoryInfo == null ) {
			return permissionObjs;
		}
		if( ListTools.isNotEmpty( document.getAuthorPersonList() ) ) {
			addPermissionObj( permissionObjs, document.getAuthorPersonList() );
		}
		if( ListTools.isNotEmpty( document.getAuthorUnitList() ) ) {
			addPermissionObj( permissionObjs, document.getAuthorUnitList() );
		}
		if( ListTools.isNotEmpty( document.getAuthorGroupList() ) ) {
			addPermissionObj( permissionObjs, document.getAuthorUnitList() );
		}
		if( ListTools.isNotEmpty( document.getManagerList())) {
			addPermissionObj( permissionObjs, document.getManagerList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getViewablePersonList())) {
			permissionObjs = addPermissionObj( permissionObjs, categoryInfo.getViewablePersonList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getViewableUnitList())) {
			permissionObjs = addPermissionObj( permissionObjs, categoryInfo.getViewableUnitList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getViewableGroupList())) {
			permissionObjs = addPermissionObj( permissionObjs, categoryInfo.getViewableGroupList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getManageablePersonList())) {
			permissionObjs = addPermissionObj( permissionObjs, categoryInfo.getManageablePersonList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getManageableUnitList())) {
			permissionObjs = addPermissionObj( permissionObjs, categoryInfo.getManageableUnitList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getManageableGroupList())) {
			permissionObjs = addPermissionObj( permissionObjs, categoryInfo.getManageableGroupList() );
		}
		if( ListTools.isNotEmpty( appInfo.getManageablePersonList())) {
			permissionObjs = addPermissionObj( permissionObjs, appInfo.getManageablePersonList() );
		}
		if( ListTools.isNotEmpty( appInfo.getManageableUnitList())) {
			permissionObjs = addPermissionObj( permissionObjs, appInfo.getManageableUnitList() );
		}
		if( ListTools.isNotEmpty( appInfo.getManageableGroupList())) {
			permissionObjs = addPermissionObj( permissionObjs, appInfo.getManageableGroupList() );
		}
		return permissionObjs;
	}

	/**
	 * 문서 내 모든 권한을 권한 목록에 추가
	 * @param permissionObjs
	 * @param document
	 * @return
	 * @throws Exception
	 */
	private List<String> addDocumentAllPermission(List<String> permissionObjs, Document document, AppInfo appInfo, CategoryInfo categoryInfo) throws Exception {
		if( permissionObjs == null ) {
			permissionObjs = new ArrayList<>();
		}
		if( document == null ) {
			return permissionObjs;
		}
		if( ListTools.isNotEmpty( document.getReadPersonList() ) ) {
			addPermissionObj( permissionObjs, document.getReadPersonList() );
		}
		if( ListTools.isNotEmpty( document.getReadUnitList() ) ) {
			addPermissionObj( permissionObjs, document.getReadUnitList() );
		}
		if( ListTools.isNotEmpty( document.getReadGroupList() ) ) {
			addPermissionObj( permissionObjs, document.getReadGroupList() );
		}
		if( !permissionObjs.contains( document.getCreatorPerson() )) {
			permissionObjs.add( document.getCreatorPerson() );
		}
		if( ListTools.isNotEmpty( document.getAuthorPersonList() ) ) {//문서 편집자
			addPermissionObj( permissionObjs, document.getAuthorPersonList() );
		}
		if( ListTools.isNotEmpty( document.getAuthorUnitList() ) ) {//문서 편집자-조직
			addPermissionObj( permissionObjs, document.getAuthorUnitList() );
		}
		if( ListTools.isNotEmpty( document.getAuthorGroupList() ) ) {//문서 편집자-그룹
			addPermissionObj( permissionObjs, document.getAuthorUnitList() );
		}
		if( ListTools.isNotEmpty( document.getManagerList())) { //문서 관리자
			addPermissionObj( permissionObjs, document.getManagerList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getManageablePersonList())) {
			addPermissionObj( permissionObjs, categoryInfo.getManageablePersonList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getManageableUnitList())) {
			addPermissionObj( permissionObjs, categoryInfo.getManageableUnitList() );
		}
		if( ListTools.isNotEmpty( categoryInfo.getManageableGroupList())) {
			addPermissionObj( permissionObjs, categoryInfo.getManageableGroupList() );
		}
		if( ListTools.isNotEmpty( appInfo.getManageablePersonList())) {
			addPermissionObj( permissionObjs, appInfo.getManageablePersonList() );
		}
		if( ListTools.isNotEmpty( appInfo.getManageableUnitList())) {
			addPermissionObj( permissionObjs, appInfo.getManageableUnitList() );
		}
		if( ListTools.isNotEmpty( appInfo.getManageableGroupList())) {
			addPermissionObj( permissionObjs, appInfo.getManageableGroupList() );
		}
		return permissionObjs;
	}

	/**
	 * 지정 권한 이름을 사용자로 분해하여 permissionObjs에 추가하고 반환
	 * @param permissionObjs
	 * @param objNames
	 * @return
	 * @throws Exception
	 */
	private List<String> addPermissionObj(List<String> permissionObjs, List<String> objNames ) throws Exception {
		String result = null;
		List<String> persons = null;
		if( permissionObjs == null ) {
			permissionObjs = new ArrayList<>();
		}
		if( ListTools.isNotEmpty(objNames)){
			Person person;
			String[] array;
			for( String objName : objNames ) {
				if(StringUtils.isNotEmpty(objName)){
					if( objName.trim().endsWith( "@P" ) ) {
						//사용자 직접 추가
						array = objName.split("@");
						if( array.length == 2){
							person = userManagerService.getPerson(array[0]);
							if( person != null ){
								objName = person.getDistinguishedName();
							}
						}
						addStringToList( permissionObjs, objName );
					}else if( objName.trim().endsWith( "@I" ) ) {//Identity를 사용자로 변환
						//신분에서 사용자로
						array = objName.split("@");
						if( array.length == 2){
							result = userManagerService.getPersonNameWithIdentity(array[0]);
						}else{
							result = userManagerService.getPersonNameWithIdentity( objName );
						}
						addStringToList( permissionObjs, result );
					}else if( objName.trim().endsWith( "@U" ) ) {//조직을 사용자로 분해
						//최상위 조직이 아니거나 최상위 조직이 유일하지 않을 때만 조직을 사용자로 해석
						if( !userManagerService.isTopUnit( objName ) || userManagerService.countTopUnit() > 1 ) {
							persons = userManagerService.listPersonWithUnit( objName );
							permissionObjs = addListToList( permissionObjs, persons );
						}else {
							//최상위 조직이고 최상위 조직이 하나뿐이면
							addStringToList( permissionObjs, "*" );
						}
					}else if( objName.trim().endsWith( "@G" ) ) {//그룹을 사용자로 분해
						persons = userManagerService.listPersonWithGroup( objName );
						permissionObjs = addListToList( permissionObjs, persons );
					}else if( objName.trim().endsWith( "@R" ) ) {
						persons = userManagerService.listPersonWithRole( objName );
						permissionObjs = addListToList( permissionObjs, persons );
					}else if( "*".equals( objName.trim() ) ) {
						permissionObjs = addStringToList( permissionObjs, objName );
					}
				}
			}
		}
		return permissionObjs;
	}

	/**
	 * 문자열을 집합에 추가, 중복 제거
	 * @param list
	 * @param string
	 * @return list
	 */
	private List<String> addStringToList( List<String> list, String string ) {
		if( list == null ) {
			list = new ArrayList<>();
		}
		if(StringUtils.isNotBlank(string) && !list.contains( string )) {
			list.add( string);
		}
		return list;
	}

	/**
	 * 문자열 목록을 집합에 추가, 중복 제거
	 * @param list
	 * @param list2
	 * @return list
	 */
	private List<String> addListToList( List<String> list, List<String> list2 ) {
		if( list == null ) {
			list = new ArrayList<>();
		}
		if( ListTools.isEmpty( list2 ) ) {
			return list;
		}else {
			for( String string : list2 ) {
				if( !list.contains( string )) {
					list.add( string);
				}
			}
		}
		return list;
	}

	/**
	 * 지정 문서의 Review를 지정 사용자 열람으로 새로 고침
	 * @param emc
	 * @param document
	 * @param permissionPersons
	 * @throws Exception
	 */
	private void refreshDocumentReview( EntityManagerContainer emc, Document document, List<String> permissionPersons) throws Exception {
		Business business = new Business(emc);
		Set<String> permissionSet = new HashSet<>(permissionPersons);
		permissionPersons.clear();
		//먼저 해당 문서의 Review 정보 존재 여부 확인
		List<String> oldReviewIds = business.reviewFactory().listByDocument( document.getId(), null );

		//먼저 기존 Review 정보 모두 삭제
		if( ListTools.isNotEmpty( oldReviewIds )) {
			emc.beginTransaction( Review.class );
			int i = 0;
			for( String reviewId : oldReviewIds ){
				Review oldReview = emc.find( reviewId, Review.class );
				if(permissionSet.contains(oldReview.getPermissionObj())){
					permissionSet.remove(oldReview.getPermissionObj());
				}else{
					emc.remove( oldReview, CheckRemoveType.all );
					i++;
					if(i>99 && i % 100 ==0){
						emc.commit();
						emc.beginTransaction( Review.class );
					}
				}
			}
			emc.commit();
		}

		//새 Review 정보 추가
		if( permissionSet.size() > 0) {
			emc.beginTransaction( Review.class );
			Person personObj = null;
			String personName = null;
			int i = 0;
			for( String person : permissionSet ) {
				if( !person.equalsIgnoreCase( "*" )) {
					//개인이 존재하는지 확인, 이름이나 고유 식별자 변경으로 문서 권한이 잘못되는 것 방지
					personObj = userManagerService.getPerson( person );
					if( personObj != null ) {
						personName = personObj.getDistinguishedName();
					}
				}else {
					personName = "*";
				}
				if( StringUtils.isNotEmpty( personName )) {
					//데이터베이스에 동일한 데이터가 있는지 조회, 있으면 추가하지 않음
					oldReviewIds = business.reviewFactory().listByDocumentAndPerson( document.getId(), personName );
					if( ListTools.isEmpty( oldReviewIds )) {
						Review review = createReviewWithDocument( document, personName );
						emc.persist( review, CheckPersistType.all );
						i++;
						if(i>99 && i % 100 ==0){
							emc.commit();
							emc.beginTransaction( Review.class );
						}
					}
				}
			}
			emc.commit();
		}

		CacheManager.notify(Review.class);
	}

	/**
	 * 집합 중복 제거
	 * @param permissionPersons
	 * @return
	 */
	private List<String> removeSameValue(List<String> permissionPersons) {
		List<String> list = new ArrayList<>();
		if( ListTools.isNotEmpty( permissionPersons )) {
			for( String permission : permissionPersons ) {
				if( !list.contains( permission ) ) {
					list.add( permission );
				}
			}
		}
		return list;
	}

	/**
	 *
	 * 게시판, 분류, 문서 정보와 열람 권한으로 Review 객체 구성
	 * @param document
	 * @param person
	 * @return
	 */
	private Review createReviewWithDocument( Document document, String person ) {
		Review review = new Review();

		review.setId( Review.createId() );
		review.setDocId( document.getId() );
		review.setTitle( document.getTitle() );
		review.setDocStatus( document.getDocStatus() );
		review.setDocumentType( document.getDocumentType() );
		review.setDocSequence( document.getSequence() );

		review.setAppAlias( document.getAppAlias() );
		review.setAppId( document.getAppId() );
		review.setAppName( document.getAppName() );
		review.setCategoryAlias( document.getCategoryAlias() );
		review.setCategoryId( document.getCategoryId() );
		review.setCategoryName( document.getCategoryName() );

		review.setCreateTime( document.getCreateTime() );
		review.setDocCreateTime( document.getCreateTime() );
		review.setPublishTime( document.getPublishTime() );
		review.setUpdateTime( document.getUpdateTime() );

		review.setCreatorPerson( document.getCreatorPerson() );
		review.setCreatorTopUnitName( document.getCreatorTopUnitName() );
		review.setCreatorUnitName( document.getCreatorUnitName() );

		if(StringUtils.isEmpty( review.getCreatorPerson() )) {
			review.setCreatorPerson( "xadmin" );
			review.setCreatorTopUnitName( "xadmin" );
			review.setCreatorUnitName( "xadmin" );
		}

		review.setPermissionObj( person );
		if( "*".equals( person ) ) {
			review.setPermissionObjType( "*" );
		}else {
			review.setPermissionObjType( "PERSON" );
		}
		return review;
	}

	/**
	 * 지정 문서의 모든 Review 정보 삭제
	 * @param emc
	 * @param docId
	 * @throws Exception
	 */
	public void deleteDocumentReview( EntityManagerContainer emc, String docId ) throws Exception {
		Business business = new Business(emc);
		Integer maxQueryCount = 1000;
		Long count = business.reviewFactory().countByDocuemnt(docId);
		Long maxTimes = count/maxQueryCount + 1 + 1; //한 번 더 보정
		List<Review> reviewList = null;
		List<String> ids = null;
		for( int i = 0 ; i <= maxTimes; i++ ) {
			ids = business.reviewFactory().listByDocument(docId, maxQueryCount);
			if( ListTools.isNotEmpty( ids )) {
				reviewList = emc.list( Review.class, ids);
			}
			if( ListTools.isNotEmpty( reviewList )) {
				emc.beginTransaction( Review.class );
				for( Review review : reviewList ) {
					emc.remove( review );
				}
				emc.commit();
			}
		}
	}
}
