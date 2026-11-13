package com.x.cms.assemble.control.factory;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.CategoryInfo_;
import com.x.cms.core.express.tools.CriteriaBuilderTools;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 분류 정보 기본 기능 서비스 클래스
 *
 * @author O2LEE
 */
public class CategoryInfoFactory extends ElementFactory {

	public CategoryInfoFactory(Business business) throws Exception {
		super(business);
	}

	public CategoryInfo pick(String flag) throws Exception {
		return this.pick(flag, ExceptionWhen.none);
	}

	@Deprecated
	public CategoryInfo pick(String flag, ExceptionWhen exceptionWhen) throws Exception {
		return this.pick(flag, CategoryInfo.class);
	}

	//@MethodDescribe("지정한 Id의 CategoryInfo 분류 정보 객체 조회")
	public CategoryInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, CategoryInfo.class );
	}

	//@MethodDescribe("전체 CategoryInfo 분류 정보 목록 조회")
	@SuppressWarnings("unused")
	public List<CategoryInfo> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CategoryInfo> cq = cb.createQuery( CategoryInfo.class );
		Root<CategoryInfo> root = cq.from( CategoryInfo.class );
		return em.createQuery(cq).getResultList();
	}

	//@MethodDescribe("전체 CategoryInfo 분류 정보 목록 조회")
	public List<String> listAllIds() throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<CategoryInfo> root = cq.from( CategoryInfo.class );
		cq.select(root.get(CategoryInfo_.id));
		return em.createQuery(cq).getResultList();
	}

	public List<String> listByAppId( String appId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<CategoryInfo> root = cq.from( CategoryInfo.class );
		Predicate p = cb.equal(root.get( CategoryInfo_.appId ), appId );
		cq.select(root.get( CategoryInfo_.id));
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<String> listPeopleViewIds(String personName, List<String> unitNames, List<String> groupNames, String appId, Boolean isManager) throws Exception {
		if(BooleanUtils.isTrue(isManager)){
			return listByAppId(appId);
		}
		EntityManager em = this.entityManagerContainer().get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		cq.select(root.get(CategoryInfo_.id));
		Predicate query = cb.equal(root.get(CategoryInfo_.appId), appId);
		Predicate p = cb.isTrue(root.get(CategoryInfo_.allPeopleView));
		if (StringUtils.isNotEmpty(personName)) {
			p = cb.or(p, cb.isMember(personName, root.get(CategoryInfo_.viewablePersonList)));
			p = cb.or(p, cb.isMember(personName, root.get(CategoryInfo_.publishablePersonList)));
			p = cb.or(p, cb.isMember(personName, root.get(CategoryInfo_.manageablePersonList)));
		}
		if(ListTools.isNotEmpty(unitNames)){
			p = cb.or(p, root.get(CategoryInfo_.viewableUnitList).in(unitNames));
			p = cb.or(p, root.get(CategoryInfo_.publishableUnitList).in(unitNames));
			p = cb.or(p, root.get(CategoryInfo_.manageableUnitList).in(unitNames));
		}
		if(ListTools.isNotEmpty(groupNames)){
			p = cb.or(p, root.get(CategoryInfo_.viewableGroupList).in(groupNames));
			p = cb.or(p, root.get(CategoryInfo_.publishableGroupList).in(groupNames));
			p = cb.or(p, root.get(CategoryInfo_.manageableGroupList).in(groupNames));
		}
		query = cb.and(query, p);
		return em.createQuery(cq.where(query)).getResultList().stream().distinct().collect(Collectors.toList());
	}

	public List<String> listByAppIds( List<String> appIds, Boolean hasProcess) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<CategoryInfo> root = cq.from( CategoryInfo.class );
		Predicate p = cb.conjunction();
		if(ListTools.isNotEmpty(appIds)) {
			p = root.get(CategoryInfo_.appId).in(appIds);
		}
		if(BooleanUtils.isTrue(hasProcess)){
			p = cb.and(p, cb.isNotNull(root.get(CategoryInfo_.workflowFlag)));
			p = cb.and(p, cb.notEqual(root.get(CategoryInfo_.workflowFlag), ""));
		}
		cq.select(root.get( CategoryInfo_.id));
		return em.createQuery( cq.where(p) ).getResultList();
	}

	/**
	 * 필터링할 게시판 목록, 정보 유형, 최대 반환 건수에 따라 게시판 정보 ID 목록 조회
	 * @param appIds
	 * @param documentType
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<String> listByAppIds( List<String> appIds, String documentType, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<CategoryInfo> root = cq.from( CategoryInfo.class );
		TypedQuery<String> query = null;
		Predicate p = null;

		//필터링할 게시판 ID가 비어 있음
		if( ListTools.isEmpty( appIds )) {
			p = CriteriaBuilderTools.predicate_and(cb, null, root.get( CategoryInfo_.appId ).in( appIds ) );
		}
		//지정한 정보 유형의 모든 분류 정보 목록 조회
		if( StringUtils.isNotEmpty( documentType ) && !"전체".equals(documentType)&& !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, null, cb.equal( root.get( CategoryInfo_.documentType ), documentType ) );
		}
		if( p != null ){
			cq.select(root.get( CategoryInfo_.id)).where( p );
		}else{
			cq.select(root.get( CategoryInfo_.id));
		}

		return em.createQuery( cq.where(p) ).setMaxResults(maxCount).getResultList();
	}

	public List<CategoryInfo> listCategoryByAppId( String appId, String documentType, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CategoryInfo> cq = cb.createQuery( CategoryInfo.class );
		Root<CategoryInfo> root = cq.from( CategoryInfo.class );
		Predicate p = cb.equal(root.get( CategoryInfo_.appId ), appId );
		if( StringUtils.isNotEmpty( documentType ) && !"전체".equals(documentType)&& !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType ), documentType ));
		}
		return em.createQuery( cq.where(p) ).setMaxResults(maxCount).getResultList();
	}

	//@MethodDescribe("앱 ID로 모든 CategoryInfo 분류 정보 수 조회")
	public Long countByAppId( String appId, String documentType ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		Predicate p = cb.equal( root.get(CategoryInfo_.appId), appId );
		if( StringUtils.isNotEmpty( documentType ) && !"전체".equals(documentType)&& !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType ), documentType ));
		}
		cq.select(cb.count(root));
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe("분류 ID로 모든 하위 CategoryInfo 분류 정보 목록 조회")
	public List<String> listByParentId( String categoryId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<CategoryInfo> root = cq.from( CategoryInfo.class );
		Predicate p = cb.equal(root.get( CategoryInfo_.parentId ), categoryId );
		cq.select(root.get( CategoryInfo_.id));
		return em.createQuery( cq.where(p) ).setMaxResults(100).getResultList();
	}

	//@MethodDescribe("분류 정보를 유사 검색하여 정보 목록 반환.")
	public List<String> listLike(String keyStr, String documentType ) throws Exception {
		String str = keyStr.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from( CategoryInfo.class);
		Predicate p = cb.like(root.get( CategoryInfo_.categoryName ), "%" + str + "%", '\\');
		p = cb.or(p, cb.like(root.get( CategoryInfo_.categoryAlias ), str + "%", '\\'));
		if( StringUtils.isNotEmpty( documentType ) && !"전체".equals(documentType)&& !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType ), documentType ));
		}
		cq.select(root.get( CategoryInfo_.id));
		return em.createQuery(cq.where(p)).setMaxResults(200).getResultList();
	}

	public List<String> listMyCategoryWithAppId( List<String> myCategoryIds, String documentType, String appId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		cq.select(root.get(CategoryInfo_.id));
		Predicate p = cb.equal( root.get( CategoryInfo_.appId ), appId );
		if( myCategoryIds != null && !myCategoryIds.isEmpty() ){
			p = cb.and( p, root.get( CategoryInfo_.id ).in( myCategoryIds ) );
		}
		if( StringUtils.isNotEmpty( documentType) && !"전체".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType), documentType));
		}
		return em.createQuery(cq.where( p )).getResultList();
	}

	public List<String> listByAlias( List<String> categoryAlias) throws Exception {
		if(ListTools.isEmpty( categoryAlias )) {
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		cq.select(root.get(CategoryInfo_.id));
		Predicate p = root.get( CategoryInfo_.categoryAlias ).in( categoryAlias );
		return em.createQuery(cq.where( p )).getResultList();
	}

	public List<CategoryInfo> listCategoryByIds( List<String> ids) throws Exception {
		if(ListTools.isEmpty( ids )) {
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CategoryInfo> cq = cb.createQuery(CategoryInfo.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		Predicate p = root.get( CategoryInfo_.id ).in( ids );
		return em.createQuery(cq.where( p )).getResultList();
	}

	public List<CategoryInfo> listByAliases(List<String> categoryAlias) throws Exception {
		if(ListTools.isEmpty( categoryAlias )) {
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CategoryInfo> cq = cb.createQuery(CategoryInfo.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		Predicate p = root.get( CategoryInfo_.categoryAlias ).in( categoryAlias );
		return em.createQuery(cq.where( p )).getResultList();
	}

	/**
	 * 한정된 앱 안에서 사용자가 발행 권한을 가진 분류 조회
	 * @param inAppInfoIds
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param isManager
	 * @param hasProcess
	 * @return
	 * @throws Exception
	 */
	public List<String> listPeoplePublishCategoryInfoIds(List<String> inAppInfoIds, String personName, List<String> unitNames, List<String> groupNames, Boolean isManager, Boolean hasProcess) throws Exception {
		if(BooleanUtils.isTrue(isManager)){
			return this.listByAppIds(inAppInfoIds, hasProcess);
		}
		EntityManager em = this.entityManagerContainer().get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);

		Predicate p = cb.conjunction();
		if( ListTools.isNotEmpty( inAppInfoIds )) {
			p = root.get( CategoryInfo_.appId ).in( inAppInfoIds );
		}
		if(BooleanUtils.isTrue(hasProcess)){
			p = cb.and(p, cb.isNotNull(root.get(CategoryInfo_.workflowFlag)));
			p = cb.and(p, cb.notEqual(root.get(CategoryInfo_.workflowFlag), ""));
		}
		Predicate p_permission = cb.isTrue(root.get(CategoryInfo_.allPeoplePublish));
		if( StringUtils.isNotEmpty( personName )) {
			p_permission = cb.or( p_permission, cb.isMember( personName, root.get( CategoryInfo_.manageablePersonList )));
			p_permission = cb.or( p_permission, cb.isMember( personName, root.get( CategoryInfo_.publishablePersonList )));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			p_permission = cb.or( p_permission,  root.get( CategoryInfo_.publishableUnitList).in(unitNames));
			p_permission = cb.or( p_permission,  root.get( CategoryInfo_.manageableUnitList).in(unitNames));
		}
		if( ListTools.isNotEmpty( groupNames )) {
			p_permission = cb.or( p_permission,  root.get( CategoryInfo_.publishableGroupList).in(groupNames));
			p_permission = cb.or( p_permission,  root.get( CategoryInfo_.manageableGroupList).in(groupNames));
		}

		p = cb.and(p, p_permission);
		cq.select(root.get( CategoryInfo_.id )).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	/**
	 * 권한에 따라 사용자가 문서를 발행할 수 있는 분류 ID 목록 조회(권한 기준, allPeopleView와 allPeoplePublish 미확인)
	 * 관리 가능한 분류도 발행 가능한 분류에 포함
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds  - 한정할 게시판 ID 목록
	 * @param inCategoryIds  - 게시판 ID의 최대 범위
	 * @param excludCategoryIds - 제외할 게시판 ID
	 * @return
	 * @throws Exception
	 */
	public List<String> listPublishableCategoryInfoIdsWithPermission(String personName, List<String> unitNames,
			List<String> groupNames, List<String> inAppInfoIds, List<String> inCategoryIds,
			List<String> excludCategoryIds, String documentType, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);

		Predicate p = null;
		Predicate p_filter = null;
		//게시판 범위 한정
		if( ListTools.isNotEmpty( inAppInfoIds )) {
			p_filter = root.get( CategoryInfo_.appId ).in( inAppInfoIds );
		}
		//범위 한정
		if( ListTools.isNotEmpty( inCategoryIds )) {
			if( p_filter == null ) {
				p_filter = root.get( CategoryInfo_.id ).in( inCategoryIds );
			}else {
				p_filter = cb.and( p_filter, root.get( CategoryInfo_.id ).in( inCategoryIds ));
			}
		}
		//지정한 ID 목록 제외
		if( ListTools.isNotEmpty( excludCategoryIds )) {
			if( p_filter == null ) {
				p_filter = cb.not( root.get( CategoryInfo_.id ).in( excludCategoryIds ));
			}else {
				p_filter = cb.and( p_filter, cb.not( root.get( CategoryInfo_.id ).in( excludCategoryIds )));
			}
		}
		Predicate p_permission = null;
		if( StringUtils.isNotEmpty( personName )) {
			//관리할 수 있는 게시판에는 반드시 정보를 발행할 수 있음
			p_permission = cb.isMember( personName, root.get( CategoryInfo_.manageablePersonList ));
			p_permission = cb.or( p_permission, cb.isMember( personName, root.get( CategoryInfo_.publishablePersonList )));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			if( p_permission == null  ) {
				p_permission = root.get( CategoryInfo_.publishableUnitList).in(unitNames);
			}else {
				p_permission = cb.or( p_permission,  root.get( CategoryInfo_.publishableUnitList).in(unitNames));
			}
		}
		if( ListTools.isNotEmpty( groupNames )) {
			if( p_permission == null  ) {
				p_permission = root.get( CategoryInfo_.publishableGroupList).in(groupNames);
			}else {
				p_permission = cb.or( p_permission,  root.get( CategoryInfo_.publishableGroupList).in(groupNames));
			}
		}

		//새 조건으로 두 조건을 결합
		if( p_filter != null ) {
			p = p_filter;
		}
		if( p != null ) {
			if( p_permission != null ) {
				p = cb.and(p, p_permission);
			}
		}else {
			if( p_permission != null ) {
				p = p_permission;
			}
		}
		if( StringUtils.isNotEmpty( documentType) && !"전체".equals(documentType)&& !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType), documentType));
		}
		cq.select(root.get( CategoryInfo_.id ));
		return em.createQuery( cq.where( p ) ).setMaxResults(maxCount).getResultList();
	}

	/**
	 * 권한에 따라 사용자가 문서에 접근할 수 있는 분류 ID 목록 조회(권한 기준, allPeopleView와 allPeoplePublish 미확인)
	 * 발행·관리 가능한 분류도 열람 가능한 분류에 포함
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds  - 한정할 게시판 ID 목록
	 * @param inCategoryIds  - 게시판 ID의 최대 범위
	 * @param excludCategoryIds - 제외할 게시판 ID
	 * @return
	 * @throws Exception
	 */
	public List<String> listViewableCategoryInfoIdsWithPermission(String personName, List<String> unitNames, List<String> groupNames,
			List<String> inAppInfoIds, List<String> inCategoryIds, List<String> excludCategoryIds,
			String documentType, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);

		Predicate p = null;
		Predicate p_filter = null;
		//게시판 범위 한정
		if( ListTools.isNotEmpty( inAppInfoIds )) {
			p_filter = root.get( CategoryInfo_.appId ).in( inAppInfoIds );
		}
		//범위 한정
		if( ListTools.isNotEmpty( inCategoryIds )) {
			if( p_filter == null ) {
				p_filter = root.get( CategoryInfo_.id ).in( inCategoryIds );
			}else {
				p_filter = cb.and( p_filter, root.get( CategoryInfo_.id ).in( inCategoryIds ));
			}
		}
		//지정한 ID 목록 제외
		if( ListTools.isNotEmpty( excludCategoryIds )) {
			if( p_filter == null ) {
				p_filter = cb.not( root.get( CategoryInfo_.id ).in( excludCategoryIds ));
			}else {
				p_filter = cb.and( p_filter, cb.not( root.get( CategoryInfo_.id ).in( excludCategoryIds )));
			}
		}

		Predicate p_permission = null;
		if( StringUtils.isNotEmpty( personName )) {
			//관리할 수 있는 게시판에는 반드시 정보를 발행할 수 있음
			p_permission = cb.isMember( personName, root.get( CategoryInfo_.manageablePersonList ));
			p_permission = cb.or( p_permission, cb.isMember( personName, root.get( CategoryInfo_.publishablePersonList )));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			if( p_permission == null ){
				p_permission =  root.get( CategoryInfo_.publishableUnitList).in(unitNames);
				p_permission = cb.or( p_permission,  root.get( CategoryInfo_.viewableUnitList).in(unitNames));
			}else {
				p_permission = cb.or( p_permission,  root.get( CategoryInfo_.publishableUnitList).in(unitNames));
				p_permission = cb.or( p_permission,  root.get( CategoryInfo_.viewableUnitList).in(unitNames));
			}
		}
		if( ListTools.isNotEmpty( groupNames )) {
			if( p_permission == null ){
				p_permission = root.get( CategoryInfo_.publishableGroupList).in(groupNames);
				p_permission = cb.or( p_permission,  root.get( CategoryInfo_.viewableGroupList).in(groupNames));
			}else {
				p_permission = cb.or( p_permission,  root.get( CategoryInfo_.publishableGroupList).in(groupNames));
				p_permission = cb.or( p_permission,  root.get( CategoryInfo_.viewableGroupList).in(groupNames));
			}
		}

		//새 조건으로 두 조건을 결합
		if( p_filter != null ) {
			p = p_filter;
		}
		if( p != null ) {
			if( p_permission != null ) {
				p = cb.and(p, p_permission);
			}
		}else {
			if( p_permission != null ) {
				p = p_permission;
			}
		}
		if( StringUtils.isNotEmpty( documentType) && !"전체".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType), documentType));
		}
		cq.select(root.get( CategoryInfo_.id ));
		return em.createQuery( cq.where( p ) ).setMaxResults(maxCount).getResultList();
	}

	/**
	 * 모든 사용자가 발행할 수 있는 분류 ID 목록 조회
	 * @param inAppInfoIds
	 * @param inCategoryIds
	 * @param excludCategoryIds
	 * @return
	 * @throws Exception
	 */
	public List<String> listAllPeoplePublishableCategoryInfoIds(List<String> inAppInfoIds, List<String> inCategoryIds,
			List<String> excludCategoryIds, String documentType, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		cq.select(root.get(CategoryInfo_.id));
		Predicate p = cb.isTrue( root.get( CategoryInfo_.allPeoplePublish ) );
		if( ListTools.isNotEmpty( inAppInfoIds )) {
			p = cb.and( p,  root.get( CategoryInfo_.appId ).in( inAppInfoIds ) );
		}
		if( ListTools.isNotEmpty( inCategoryIds )) {
			p = cb.and( p, root.get( CategoryInfo_.id ).in( inCategoryIds ));
		}
		if( ListTools.isNotEmpty( excludCategoryIds )) {
			p = cb.and( p, cb.not( root.get( CategoryInfo_.id ).in( excludCategoryIds )));
		}
		if( StringUtils.isNotEmpty( documentType) && !"전체".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType), documentType));
		}
		return em.createQuery(cq.where( p )).setMaxResults(maxCount).getResultList();
	}

	/**
	 * 모든 사용자가 발행할 수 있는 분류 ID 목록 조회(allPeopleView와 allPeoplePublish 확인)
	 * @param inAppInfoIds
	 * @param inCategoryIds
	 * @param excludCategoryIds
	 * @return
	 * @throws Exception
	 */
	public List<String> listAllPeopleViewableCategoryInfoIds(List<String> inAppInfoIds, List<String> inCategoryIds, List<String> excludCategoryIds,
			String documentType, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		cq.select(root.get(CategoryInfo_.id));

		Predicate p_all = cb.isTrue( root.get( CategoryInfo_.allPeopleView ) );
		p_all = cb.or( p_all,  cb.isTrue( root.get( CategoryInfo_.allPeoplePublish )));

		Predicate p = root.get( CategoryInfo_.id ).isNotNull();
		if( ListTools.isNotEmpty( inAppInfoIds )) {
			p = cb.and( p,  root.get( CategoryInfo_.appId ).in( inAppInfoIds ) );
		}
		if( ListTools.isNotEmpty( inCategoryIds )) {
			p = cb.and( p, root.get( CategoryInfo_.id ).in( inCategoryIds ));
		}
		if( ListTools.isNotEmpty( excludCategoryIds )) {
			p = cb.and( p, cb.not( root.get( CategoryInfo_.id ).in( excludCategoryIds )));
		}
		p = cb.and( p, p_all );
		if( StringUtils.isNotEmpty( documentType) && !"전체".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType), documentType));
		}
		return em.createQuery(cq.where( p )).setMaxResults(maxCount).getResultList();
	}

	/**
	 * 지정한 사용자, 조직, 그룹이 관리할 수 있는 분류 목록 조회
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @return
	 * @throws Exception
	 */
	public List<String> listManageableCategoryIds( String personName, List<String> unitNames, List<String> groupNames, List<String> inAppInfoIds,
			String documentType, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);

		Predicate p = cb.isMember( personName, root.get( CategoryInfo_.manageablePersonList ));
		if( ListTools.isNotEmpty( inAppInfoIds )) {
			p = cb.and( p, root.get( CategoryInfo_.appId ).in( inAppInfoIds ) );
		}
		if( ListTools.isNotEmpty( unitNames )) {
			p = cb.or( p,  root.get( CategoryInfo_.manageableUnitList).in(unitNames));
		}
		if( ListTools.isNotEmpty( groupNames )) {
			p = cb.or( p,  root.get( CategoryInfo_.manageableGroupList).in(groupNames));
		}
		cq.select(root.get( CategoryInfo_.id ));
		if( StringUtils.isNotEmpty( documentType) && !"전체".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = cb.and( p, cb.equal( root.get( CategoryInfo_.documentType), documentType));
		}
		return em.createQuery( cq.where( p ) ).setMaxResults(maxCount).getResultList();
	}
}
