package com.x.cms.core.express.permission;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Review;
import com.x.cms.core.entity.Review_;
import com.x.cms.core.express.tools.CriteriaBuilderTools;
import com.x.cms.core.express.tools.filter.QueryFilter;

/**
 * CMS 게시판, 분류, 문서 권한 필터링 조회, CMS 앱에서 직접 참조
 *
 * @author O2LEE
 *
 */
public class CmsPermissionService {

	/**
	 * 조건에 따라 사용자가 접근 권한을 가진 모든 문서 ID 목록 조회
	 * @param emc
	 * @param queryFilter
	 * @param maxResultCount
	 * @return
	 * @throws Exception
	 */
	public List<String> lisViewableDocIdsWithFilter(EntityManagerContainer emc, QueryFilter queryFilter,
			Integer maxResultCount) throws Exception {
		if (maxResultCount == null || maxResultCount == 0) {
			maxResultCount = 500;
		}
		List<String> ids = new ArrayList<>();
		List<Review> reviews = null;
		EntityManager em = emc.get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Review> cq = cb.createQuery(Review.class);
		Root<Review> root = cq.from(Review.class);
		// Predicate p=null;
		Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter(Review_.class, cb, null, root, queryFilter);
		cq.orderBy(cb.desc(root.get(Review.publishTime_FIELDNAME)));
		reviews = em.createQuery(cq.where(p)).setMaxResults(maxResultCount).getResultList();
		if ( ListTools.isNotEmpty( reviews )) {
			for (Review review : reviews) {
				if (!ids.contains(review.getDocId())) {
					ids.add(review.getDocId());
				}
			}
		}
		return ids;
	}

	/**
	 * 지정 사용자, 조직, 그룹이 접근할 수 있는 모든 게시판 ID 목록 조회(전원 접근 가능 게시판 포함)
	 * @param emc
	 * @param personName
	 * @param isAnonymous
	 * @param unitNames
	 * @param groupNames
	 * @param roleNames
	 * @param inAppInfoIds
	 * @param excludeAppInfoIds
	 * @param documentType
	 * @param appType
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<String> listViewableAppIdByPerson(EntityManagerContainer emc, String personName, Boolean isAnonymous,
			List<String> unitNames, List<String> groupNames, List<String> roleNames, List<String> inAppInfoIds,
			List<String> excludeAppInfoIds, String documentType, String appType, Integer maxCount) throws Exception {

		List<String> viewableAppInfoIds = new ArrayList<>();

		//1. 접근 권한이 설정되지 않은 모든 게시판 ID 목록을 viewAbleAppInfoIds에 추가(전원 접근 가능 AppInfo)
		viewableAppInfoIds = addResultToSourceList(this.listAllPeopleViewAppIds(emc, documentType, appType), viewableAppInfoIds);
		//2. 모든 사람이 게시 가능한 게시판 추가
		viewableAppInfoIds = addResultToSourceList(this.listAllPeoplePublishAppIds( emc, documentType, appType ), viewableAppInfoIds);

		if (!isAnonymous) {
			// 2. 사용자가 관리자인 모든 게시판 ID 목록을 viewAbleAppInfoIds에 추가
			viewableAppInfoIds = addResultToSourceList(this.listManageableAppIdsByPerson(emc, personName, unitNames,
					groupNames, roleNames, null, null, null, documentType, maxCount), viewableAppInfoIds);

			// 3. 사용자 본인 및 소속 조직, 그룹이 접근 권한을 가진 모든 게시판 ID 목록을 viewAbleAppInfoIds에 추가
			addResultToSourceList(
					this.listViewableAppIdsInPermission(emc, personName, unitNames, groupNames, documentType, maxCount),
					viewableAppInfoIds);
		}

		if (ListTools.isNotEmpty(inAppInfoIds)) {
			viewableAppInfoIds.retainAll(inAppInfoIds);
		}

		viewableAppInfoIds = excludListContent(viewableAppInfoIds, excludeAppInfoIds);

		return viewableAppInfoIds;
	}

	/**
	 * 지정 사용자, 조직, 그룹이 문서를 게시할 수 있는 모든 게시판 ID 목록 조회
	 *
	 * @param personName
	 * @param isAnonymous
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @param excludAppInfoIds
	 * @return
	 * @throws Exception
	 */
	public List<String> listPublishableAppIdByPerson(EntityManagerContainer emc, String personName, Boolean isAnonymous,
			List<String> unitNames, List<String> groupNames, List<String> roleNames, List<String> inAppInfoIds, List<String> excludAppInfoIds,
			String documentType, String appType, Integer maxCount) throws Exception {
		List<String> publishableAppInfoIds = new ArrayList<>();

		// 1. 전원 게시 가능 게시판: 접근 권한이 설정되지 않은 모든 게시판 ID 목록을 viewAbleAppInfoIds에 추가
		publishableAppInfoIds = addResultToSourceList( this.listAllPeoplePublishAppIds(emc, documentType, appType), publishableAppInfoIds);

		if (!isAnonymous) {
			// 2. 사용자 관리 가능 게시판: 사용자가 관리자인 모든 게시판 ID 목록을 viewAbleAppInfoIds에 추가
			publishableAppInfoIds = addResultToSourceList(this.listManageableAppIdsByPerson(emc, personName, unitNames,
					groupNames, roleNames, null, null, null, documentType, maxCount), publishableAppInfoIds);

			// 3. 사용자 게시 권한이 설정된 게시판: 사용자 본인 및 소속 조직, 그룹이 접근 권한을 가진 모든 게시판 ID 목록을 viewAbleAppInfoIds에 추가
			addResultToSourceList(this.listPublishableAppIdsInPermission(emc, personName, unitNames, groupNames, roleNames,
					documentType, maxCount), publishableAppInfoIds);
		}

		if (ListTools.isNotEmpty(inAppInfoIds)) {
			publishableAppInfoIds.retainAll(inAppInfoIds);
		}

		if (ListTools.isNotEmpty(excludAppInfoIds)) {
			publishableAppInfoIds = excludListContent(publishableAppInfoIds, excludAppInfoIds);
		}

		return publishableAppInfoIds;
	}

	/**
	 * excludAppInfoIds로 불필요한 데이터 제외
	 *
	 * @param sourceList
	 * @param excludAppInfoIds
	 * @return
	 */
	private List<String> excludListContent(List<String> sourceList, List<String> excludAppInfoIds) {
		if (ListTools.isEmpty(excludAppInfoIds)) {
			return sourceList;
		}
		List<String> result = new ArrayList<>();
		if (ListTools.isNotEmpty(sourceList)) {
			for (String content : sourceList) {
				if (!excludAppInfoIds.contains(content)) {
					result.add(content);
				}
			}
		}
		return result;
	}

	/**
	 * 지정 사용자가 관리할 수 있는 모든 게시판 ID 목록 조회( with List copy )
	 *
	 * @param emc
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param appType
	 * @param documentType
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<String> listManageableAppIdsByPerson(EntityManagerContainer emc, String personName,
			List<String> unitNames, List<String> groupNames, List<String> roleNames,
			List<String> inAppInfoIds, List<String> excludAppInfoIds,
			String appType, String documentType, Integer maxCount)
			throws Exception {
		if (StringUtils.isEmpty(personName)) {
			throw new Exception("personName is empty!");
		}
		List<String> appInfoIds = null;
		List<String> appInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);

		Predicate p = null;
		if (StringUtils.isNotEmpty(appType) && !StringUtils.equals(appType, "未分类")) {
			p = CriteriaBuilderTools.predicate_and(cb, p, cb.equal(root.get(AppInfo.appType_FIELDNAME), appType));
		}
		if (StringUtils.isNotEmpty(appType) && StringUtils.equals(appType, "未分类")) {
			CriteriaBuilderTools.predicate_and(cb, p,
					CriteriaBuilderTools.predicate_or(cb, cb.isNull(root.get(AppInfo.appType_FIELDNAME)),
							CriteriaBuilderTools.predicate_or(cb, cb.equal(root.get(AppInfo.appType_FIELDNAME), ""),
									cb.equal(root.get(AppInfo.appType_FIELDNAME), "未分类"))));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType)
				&& !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.equal(root.get(AppInfo.documentType_FIELDNAME), documentType));
		}
		Predicate p_permission = null;
		if (StringUtils.isNotEmpty(personName)) {
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.isMember(personName, root.get(AppInfo.manageablePersonList_FIELDNAME)));
		}
		if (ListTools.isNotEmpty(unitNames)) {
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.or(p_permission, root.get(AppInfo.manageableUnitList_FIELDNAME).in(unitNames)));
		}
		if (ListTools.isNotEmpty(groupNames)) {
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.or(p_permission, root.get(AppInfo.manageableGroupList_FIELDNAME).in(groupNames)));
		}
		if (ListTools.isNotEmpty(roleNames)) {
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.or(p_permission, root.get(AppInfo.manageableRoleList_FIELDNAME).in(roleNames)));
		}
		p = CriteriaBuilderTools.predicate_and(cb, p, p_permission);

		cq.select(root.get(AppInfo.id_FIELDNAME));
		appInfoIds = em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
		if (appInfoIds == null) {
			appInfoIds = new ArrayList<>();
		}
		appInfoIds_out.addAll(appInfoIds);

		if (ListTools.isNotEmpty(inAppInfoIds)) {
			appInfoIds_out.retainAll(inAppInfoIds);
		}

		if (ListTools.isNotEmpty(excludAppInfoIds)) {
			appInfoIds_out = excludListContent(appInfoIds_out, excludAppInfoIds);
		}

		return appInfoIds_out;
	}

	/**
	 * 지정한 사용자, 조직, 그룹이 접근 가능한 모든 분류 ID 목록 조회(전원 접근 가능 분류 포함), 얻은 분류 목록은 접근 가능한 게시판 목록보다 클 수 있음
	 * @param emc
	 * @param personName
	 * @param isAnonymous
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @param inCategoryInfoIds
	 * @param excludCategoryInfoIds
	 * @param documentType
	 * @param appType
	 * @param maxCount
	 * @param manager
	 * @return
	 * @throws Exception
	 */
	public List<String> listViewableCategoryIdByPerson(EntityManagerContainer emc, String personName,
			Boolean isAnonymous, List<String> unitNames, List<String> groupNames, List<String> roleNames, List<String> inAppInfoIds,
			List<String> inCategoryInfoIds, List<String> excludCategoryInfoIds, String documentType, String appType, Integer maxCount,
			Boolean manager) throws Exception {
		List<String> viewableCategoryInfoIds = new ArrayList<>();
		List<String> allViewableAppIds = null;
		// 내가 접근 가능한 모든 게시판 ID 목록 조회
		if (manager) {
			allViewableAppIds = this.listAllAppIds(emc, inAppInfoIds, null, documentType, maxCount);
		} else {
			allViewableAppIds = this.listViewableAppIdByPerson(emc, personName, isAnonymous, unitNames, groupNames, roleNames,
					inAppInfoIds, null, documentType, appType, maxCount);
			if (ListTools.isNotEmpty(inAppInfoIds) && ListTools.isEmpty(allViewableAppIds)) {
				allViewableAppIds = new ArrayList<>();
				allViewableAppIds.add("无可见栏目");
			}
		}

		// 내가 접근 가능한 모든 게시판 범위 내에서 전원 열람 가능 분류 ID 목록 조회
		viewableCategoryInfoIds = addResultToSourceList(this.listAllPeopleViewCategoryIds(emc, allViewableAppIds,
				inCategoryInfoIds, excludCategoryInfoIds, documentType, maxCount), viewableCategoryInfoIds);

		if (!isAnonymous) {
			// 내가 접근 가능한 모든 게시판 범위 내에서 내가 볼 수 있는 분류 ID 목록 조회(게시 권한 있는 분류 포함, 전원 열람 식별자 미검사)
			viewableCategoryInfoIds = addResultToSourceList(
					this.listViewableCategoryIdsInPermission(emc, personName, unitNames, groupNames, inAppInfoIds,
							inCategoryInfoIds, excludCategoryInfoIds, documentType, maxCount, manager),
					viewableCategoryInfoIds);

			// 내가 게시 권한을 가진 모든 분류 조회(접근 가능한 게시판 ID 목록에 없어도 됨)
			viewableCategoryInfoIds = addResultToSourceList(
					this.listPublishableCategoryIdsInPermission(emc, personName, unitNames, groupNames, inAppInfoIds,
							inCategoryInfoIds, excludCategoryInfoIds, documentType, maxCount),
					viewableCategoryInfoIds);

			// 내가 관리 권한을 가진 모든 분류 조회(접근 가능한 게시판 ID 목록에 없어도 됨)
			viewableCategoryInfoIds = addResultToSourceList(this.listManageableCategoryIdsByPerson(emc, personName,
					unitNames, groupNames, inAppInfoIds, documentType, maxCount, manager), viewableCategoryInfoIds);
		}

		if (ListTools.isEmpty(viewableCategoryInfoIds)) {
			if (!manager) {
				viewableCategoryInfoIds.add("没有可用分类");
			}
		} else {
			if (ListTools.isNotEmpty(inCategoryInfoIds)) {
				viewableCategoryInfoIds.retainAll(inCategoryInfoIds);
			}
		}
		viewableCategoryInfoIds = excludListContent(viewableCategoryInfoIds, excludCategoryInfoIds);

		return viewableCategoryInfoIds;
	}

	/**
	 * 지정 사용자, 조직, 그룹이 문서를 게시할 수 있는 모든 분류 ID 목록 조회(전원 게시 가능 분류 포함) 얻은 분류 목록이 접근 가능한 게시판 목록보다 클 수 있음
	 *
	 * @param personName
	 * @param isAnonymous
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds          - 필터 게시판 ID
	 * @param inCategoryInfoIds     - 필터 분류 ID
	 * @param excludCategoryInfoIds - 제외 분류 ID
	 * @return
	 * @throws Exception
	 */
	public List<String> listPublishableCategoryIdByPerson(EntityManagerContainer emc, String personName,
			Boolean isAnonymous, List<String> unitNames, List<String> groupNames, List<String> roleNames, List<String> inAppInfoIds,
			List<String> inCategoryInfoIds, List<String> excludCategoryInfoIds, String documentType, String appType,
			Integer maxCount, Boolean manager) throws Exception {
		List<String> publishableCategoryInfoIds = new ArrayList<>();
		List<String> allPublishableAppIds = null;

		// 내가 문서를 게시할 수 있는 모든 게시판 ID 목록 조회(게시판 정보 기준으로만 판단, 분류 무관, 게시자 미설정 또는 게시자 권한 보유)
		allPublishableAppIds = this.listPublishableAppIdByPerson(emc, personName, isAnonymous, unitNames, groupNames, roleNames,
				inAppInfoIds, null, documentType, appType, maxCount);
		if (ListTools.isEmpty(allPublishableAppIds)) {
			allPublishableAppIds.add("无可发布栏目ID");
		}
		// 내가 직접 문서를 게시할 수 있는 게시판 범위 내에서 전원 열람 가능 분류 ID 목록 조회
		publishableCategoryInfoIds = addResultToSourceList(this.listAllPeoplePublishCategoryIds(emc,
				allPublishableAppIds, inCategoryInfoIds, excludCategoryInfoIds, documentType, maxCount),
				publishableCategoryInfoIds);

		if (!isAnonymous) {
			// 지정한 게시판 범위(inAppInfoIds) 내에서 내가 문서를 게시할 수 있는 분류 ID 목록 조회(게시 권한 있는 분류 포함, 전원 게시 식별자 미검사)
			publishableCategoryInfoIds = addResultToSourceList(
					this.listPublishableCategoryIdsInPermission(emc, personName, unitNames, groupNames, inAppInfoIds,
							inCategoryInfoIds, excludCategoryInfoIds, documentType, maxCount),
					publishableCategoryInfoIds);

			// 지정한 게시판 범위(inAppInfoIds) 내에서 내가 관리할 수 있는 분류 ID 목록 조회
			publishableCategoryInfoIds = addResultToSourceList(this.listManageableCategoryIdsByPerson(emc, personName,
					unitNames, groupNames, inAppInfoIds, documentType, maxCount, manager), publishableCategoryInfoIds);
		}

		if (ListTools.isEmpty(publishableCategoryInfoIds)) {
			if (!manager) {
				publishableCategoryInfoIds.add("没有可用分类");
			}
		}
		publishableCategoryInfoIds = excludListContent(publishableCategoryInfoIds, excludCategoryInfoIds);
		return publishableCategoryInfoIds;
	}

	/**
	 * 지정 사용자가 관리할 수 있는 모든 분류 ID 목록 조회( with List copy )
	 *
	 * @param emc
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listManageableCategoryIdsByPerson(EntityManagerContainer emc, String personName,
			List<String> unitNames, List<String> groupNames, List<String> inAppInfoIds, String documentType,
			Integer maxCount, Boolean manager) throws Exception {
		if (StringUtils.isEmpty(personName)) {
			throw new Exception("personName is empty!");
		}
		List<String> categoryInfoIds = null;
		List<String> categoryInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);

		Predicate p = null;
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, root.get(CategoryInfo.appId_FIELDNAME).in(inAppInfoIds));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType)
				&& !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.equal(root.get(CategoryInfo.documentType_FIELDNAME), documentType));
		}

		Predicate permission = cb.isMember(personName, root.get(CategoryInfo.manageablePersonList_FIELDNAME));
		if (ListTools.isNotEmpty(unitNames)) {
			permission = CriteriaBuilderTools.predicate_or(cb, permission,
					root.get(CategoryInfo.manageableUnitList_FIELDNAME).in(unitNames));
		}
		if (ListTools.isNotEmpty(groupNames)) {
			permission = CriteriaBuilderTools.predicate_or(cb, permission,
					root.get(CategoryInfo.manageableGroupList_FIELDNAME).in(groupNames));
		}

		p = CriteriaBuilderTools.predicate_and(cb, p, permission);

		cq.select(root.get(CategoryInfo.id_FIELDNAME));
		categoryInfoIds = em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
		if (categoryInfoIds == null) {
			categoryInfoIds = new ArrayList<>();
		}
		categoryInfoIds_out.addAll(categoryInfoIds);
		if (ListTools.isEmpty(categoryInfoIds_out)) {
			if (!manager) {
				categoryInfoIds_out.add("没有可用分类");
			}
		}
		return categoryInfoIds_out;
	}

	/**
	 * 사용자가 접근 권한을 가진 모든 분류 ID 목록 조회( allPeopleView와 allPeoplePublish 미검사, with List copy )
	 *
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @param inCategoryInfoIds
	 * @param excludCategoryInfoIds
	 * @return
	 * @throws Exception
	 */
	private List<String> listPublishableCategoryIdsInPermission(EntityManagerContainer emc, String personName,
			List<String> unitNames, List<String> groupNames, List<String> inAppInfoIds, List<String> inCategoryInfoIds,
			List<String> excludCategoryInfoIds, String documentType, Integer maxCount) throws Exception {
		List<String> categoryInfoIds = null;
		List<String> categoryInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);

		Predicate p = null;
		// 게시판 범위 한정
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, root.get(CategoryInfo.appId_FIELDNAME).in(inAppInfoIds));
		}
		// 범위 한정
		if (ListTools.isNotEmpty(inCategoryInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, root.get(CategoryInfo.id_FIELDNAME).in(inCategoryInfoIds));
		}
		// 지정한 ID 목록 제외
		if (ListTools.isNotEmpty(excludCategoryInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.not(root.get(CategoryInfo.id_FIELDNAME).in(excludCategoryInfoIds)));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType)
				&& !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.equal(root.get(CategoryInfo.documentType_FIELDNAME), documentType));
		}

		Predicate p_permission = null;
		if (StringUtils.isNotEmpty(personName)) {
			// 관리할 수 있는 게시판에는 반드시 정보를 발행할 수 있음
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.isMember(personName, root.get(CategoryInfo.manageablePersonList_FIELDNAME)));
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.isMember(personName, root.get(CategoryInfo.publishablePersonList_FIELDNAME)));
		}
		if (ListTools.isNotEmpty(unitNames)) {
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(CategoryInfo.publishableUnitList_FIELDNAME).in(unitNames));
		}
		if (ListTools.isNotEmpty(groupNames)) {
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(CategoryInfo.publishableGroupList_FIELDNAME).in(groupNames));
		}

		p = CriteriaBuilderTools.predicate_and(cb, p, p_permission);
		cq.select(root.get(CategoryInfo.id_FIELDNAME));
		categoryInfoIds = em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
		if (categoryInfoIds == null) {
			categoryInfoIds = new ArrayList<>();
		}
		categoryInfoIds_out.addAll(categoryInfoIds);
		categoryInfoIds_out = excludListContent(categoryInfoIds_out, excludCategoryInfoIds);
		return categoryInfoIds_out;
	}

	/**
	 * 사용자가 접근 권한을 가진 모든 분류 ID 목록 조회( allPeopleView와 allPeoplePublish 미검사, with List copy )
	 *
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @param inCategoryInfoIds
	 * @param excludCategoryInfoIds
	 * @return
	 * @throws Exception
	 */
	private List<String> listViewableCategoryIdsInPermission(EntityManagerContainer emc, String personName,
			List<String> unitNames, List<String> groupNames, List<String> inAppInfoIds, List<String> inCategoryInfoIds,
			List<String> excludCategoryInfoIds, String documentType, Integer maxCount, Boolean manager)
			throws Exception {
		List<String> categoryInfoIds = null;
		List<String> categoryInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);

		Predicate p = null;
		// 게시판 범위 한정
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, root.get(CategoryInfo.appId_FIELDNAME).in(inAppInfoIds));
		}
		// 범위 한정
		if (ListTools.isNotEmpty(inCategoryInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, root.get(CategoryInfo.id_FIELDNAME).in(inCategoryInfoIds));
		}
		// 지정한 ID 목록 제외
		if (ListTools.isNotEmpty(excludCategoryInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.not(root.get(CategoryInfo.id_FIELDNAME).in(excludCategoryInfoIds)));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType)
				&& !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.equal(root.get(CategoryInfo.documentType_FIELDNAME), documentType));
		}

		Predicate p_permission = null;
		if (StringUtils.isNotEmpty(personName)) {
			// 관리할 수 있는 게시판에는 반드시 정보를 발행할 수 있음
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.isMember(personName, root.get(CategoryInfo.viewablePersonList_FIELDNAME)));
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.isMember(personName, root.get(CategoryInfo.manageablePersonList_FIELDNAME)));
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.isMember(personName, root.get(CategoryInfo.publishablePersonList_FIELDNAME)));
		}
		if (ListTools.isNotEmpty(unitNames)) {
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(CategoryInfo.publishableUnitList_FIELDNAME).in(unitNames));
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(CategoryInfo.viewableUnitList_FIELDNAME).in(unitNames));
		}
		if (ListTools.isNotEmpty(groupNames)) {
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(CategoryInfo.publishableGroupList_FIELDNAME).in(groupNames));
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(CategoryInfo.viewableGroupList_FIELDNAME).in(groupNames));
		}

		p = CriteriaBuilderTools.predicate_and(cb, p, p_permission);
		cq.select(root.get(CategoryInfo.id_FIELDNAME));
		categoryInfoIds = em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
		if (categoryInfoIds == null) {
			categoryInfoIds = new ArrayList<>();
		}
		categoryInfoIds_out.addAll(categoryInfoIds);
		if (ListTools.isEmpty(categoryInfoIds_out)) {
			if (!manager) {
				categoryInfoIds_out.add("没有可用分类");
			}
		}
		categoryInfoIds_out = excludListContent(categoryInfoIds_out, excludCategoryInfoIds);
		return categoryInfoIds_out;
	}

	/**
	 * 사용자가 접근 권한을 가진 모든 게시판 ID 목록 조회( allPeopleView 미검사, with List copy )
	 *
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	private List<String> listViewableAppIdsInPermission(EntityManagerContainer emc, String personName,
			List<String> unitNames, List<String> groupNames, String documentType, Integer maxCount) throws Exception {
		return listViewableAppIdsInPermission(emc, personName, unitNames, groupNames, null, null, documentType,
				maxCount);
	}

	/**
	 * 사용자가 접근 권한을 가진 모든 게시판 ID 목록 조회( allPeopleView 미검사, with List copy )
	 *
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @param excludAppInfoIds
	 * @return
	 * @throws Exception
	 */
	private List<String> listViewableAppIdsInPermission(EntityManagerContainer emc, String personName,
			List<String> unitNames, List<String> groupNames, List<String> inAppInfoIds, List<String> excludAppInfoIds,
			String documentType, Integer maxCount) throws Exception {
		List<String> appInfoIds = null;
		List<String> appInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);

		Predicate p = null;
		// 범위 한정
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, root.get(AppInfo.id_FIELDNAME).in(inAppInfoIds));
		}
		// 지정한 ID 목록 제외
		if (ListTools.isNotEmpty(excludAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, cb.not(root.get(AppInfo.id_FIELDNAME).in(excludAppInfoIds)));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType)
				&& !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.equal(root.get(AppInfo.documentType_FIELDNAME), documentType));
		}

		Predicate p_permission = null;
		if (StringUtils.isNotEmpty(personName)) {
			// 관리 가능한 게시판은 반드시 볼 수 있음
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.isMember(personName, root.get(AppInfo.manageablePersonList_FIELDNAME)));
			// 게시 가능한 게시판은 반드시 볼 수 있음
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.isMember(personName, root.get(AppInfo.publishablePersonList_FIELDNAME)));
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					cb.isMember(personName, root.get(AppInfo.viewablePersonList_FIELDNAME)));
		}
		if (ListTools.isNotEmpty(unitNames)) {
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(AppInfo.publishableUnitList_FIELDNAME).in(unitNames));
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(AppInfo.viewableUnitList_FIELDNAME).in(unitNames));
		}
		if (ListTools.isNotEmpty(groupNames)) {
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(AppInfo.publishableGroupList_FIELDNAME).in(groupNames));
			p_permission = CriteriaBuilderTools.predicate_or(cb, p_permission,
					root.get(AppInfo.viewableGroupList_FIELDNAME).in(groupNames));
		}

		p = CriteriaBuilderTools.predicate_and(cb, p, p_permission);
		cq.select(root.get(AppInfo.id_FIELDNAME));
		appInfoIds = em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
		if (appInfoIds == null) {
			appInfoIds = new ArrayList<>();
		}
		appInfoIds_out.addAll(appInfoIds);
		appInfoIds_out = excludListContent(appInfoIds_out, excludAppInfoIds);
		return appInfoIds_out;
	}

	/**
	 * 사용자가 접근 권한을 가진 모든 게시판 ID 목록 조회( allPeopleView 미검사, with List copy )
	 * @param emc
	 * @param inAppInfoIds
	 * @param excludAppInfoIds
	 * @param documentType
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	private List<String> listAllAppIds(EntityManagerContainer emc, List<String> inAppInfoIds,
			List<String> excludAppInfoIds, String documentType, Integer maxCount) throws Exception {
		List<String> appInfoIds = null;
		List<String> appInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);

		Predicate p = null;
		// 범위 한정
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, root.get(AppInfo.id_FIELDNAME).in(inAppInfoIds));
		}
		// 지정한 ID 목록 제외
		if (ListTools.isNotEmpty(excludAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, cb.not(root.get(AppInfo.id_FIELDNAME).in(excludAppInfoIds)));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType)
				&& !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.equal(root.get(AppInfo.documentType_FIELDNAME), documentType));
		}
		cq.select(root.get(AppInfo.id_FIELDNAME));

		appInfoIds = em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
		if (appInfoIds == null) {
			appInfoIds = new ArrayList<>();
		}
		appInfoIds_out.addAll(appInfoIds);
		appInfoIds_out = excludListContent(appInfoIds_out, excludAppInfoIds);
		return appInfoIds_out;
	}

	/**
	 * 사용자가 문서 게시 권한을 가진 모든 게시판 ID 목록 조회( allPeoplePublish 미검사, with List copy )
	 *
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	private List<String> listPublishableAppIdsInPermission(EntityManagerContainer emc, String personName,
			List<String> unitNames, List<String> groupNames, List<String> roleNames, String documentType, Integer maxCount) throws Exception {
		return listPublishableAppIdsInPermission(emc, personName, unitNames, groupNames, roleNames, null, null, documentType,
				maxCount);
	}

	/**
	 * 사용자가 문서 게시 권한을 가진 모든 게시판 ID 목록 조회( allPeopleView 미검사, with List copy )
	 *
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @param excludAppInfoIds
	 * @return
	 * @throws Exception
	 */
	private List<String> listPublishableAppIdsInPermission(EntityManagerContainer emc, String personName,
			List<String> unitNames, List<String> groupNames, List<String> roleNames, List<String> inAppInfoIds, List<String> excludAppInfoIds,
			String documentType, Integer maxCount) throws Exception {
		List<String> appInfoIds = null;
		List<String> appInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);

		Predicate p = null;
		// 범위 한정
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, cb.and(p, root.get(AppInfo.id_FIELDNAME).in(inAppInfoIds)));
		}
		// 지정한 ID 목록 제외
		if (ListTools.isNotEmpty(excludAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, cb.not(root.get(AppInfo.id_FIELDNAME).in(excludAppInfoIds)));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType)
				&& !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.equal(root.get(AppInfo.documentType_FIELDNAME), documentType));
		}

		Predicate p_permission = null;
		if (StringUtils.isNotEmpty(personName)) {
			// 관리할 수 있는 게시판에는 반드시 정보를 발행할 수 있음
			p_permission = cb.isMember(personName, root.get(AppInfo.manageablePersonList_FIELDNAME));
			p_permission = cb.or(p_permission,
					cb.isMember(personName, root.get(AppInfo.publishablePersonList_FIELDNAME)));
		}
		if (ListTools.isNotEmpty(unitNames)) {
			p_permission = cb.or(p_permission, root.get(AppInfo.publishableUnitList_FIELDNAME).in(unitNames));
		}
		if (ListTools.isNotEmpty(groupNames)) {
			p_permission = cb.or(p_permission, root.get(AppInfo.publishableGroupList_FIELDNAME).in(groupNames));
		}
		if (ListTools.isNotEmpty(roleNames)) {
			p_permission = cb.or(p_permission, root.get(AppInfo.publishableRoleList_FIELDNAME).in(roleNames));
		}

		p = CriteriaBuilderTools.predicate_and(cb, p, p_permission);
		cq.select(root.get(AppInfo.id_FIELDNAME));
		appInfoIds = em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
		if (appInfoIds == null) {
			appInfoIds = new ArrayList<>();
		}
		appInfoIds_out.addAll(appInfoIds);
		appInfoIds_out = excludListContent(appInfoIds_out, excludAppInfoIds);
		return appInfoIds_out;
	}

	/**
	 * List 하나를 SourceList에 추가
	 *
	 * @param result
	 * @param sourceList
	 */
	private List<String> addResultToSourceList(List<String> result, List<String> sourceList) {
		if (ListTools.isNotEmpty(result)) {
			for (String id : result) {
				if (!sourceList.contains(id)) {
					sourceList.add(id);
				}
			}
		}
		return sourceList;
	}

	/**
	 * 열람 권한이 설정되지 않은 모든 AppInfo의 ID 목록 조회( with List copy ) 전체 사용자가 발행 가능한 게시판도 포함: allPeopleView or 확인
	 * allPeoplePublish
	 *
	 * @return
	 * @throws Exception
	 */
	private List<String> listAllPeopleViewAppIds(EntityManagerContainer emc, String documentType, String appType ) throws Exception {
		List<String> appInfoIds = null;
		List<String> appInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		cq.select(root.get(AppInfo.id_FIELDNAME));

		Predicate p = cb.isTrue(root.get(AppInfo.allPeopleView_FIELDNAME));
		if( StringUtils.isNotEmpty( appType ) && !"all".equalsIgnoreCase( appType ) && !"全部".equalsIgnoreCase( appType )   ){
			p = CriteriaBuilderTools.predicate_and(cb, p, cb.equal(root.get(AppInfo.appType_FIELDNAME), appType));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, cb.equal(root.get(AppInfo.documentType_FIELDNAME), documentType));
		}
		appInfoIds = em.createQuery(cq.where(p)).getResultList();
		if (appInfoIds == null) {
			appInfoIds = new ArrayList<>();
		}
		appInfoIds_out.addAll(appInfoIds);
		return appInfoIds_out;
	}

	/**
	 * 게시 권한이 설정되지 않은 모든 AppInfo의 ID 목록 조회( with List copy ) allPeoplePublish 판단
	 *
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	private List<String> listAllPeoplePublishAppIds(EntityManagerContainer emc, String documentType, String appType) throws Exception {
		List<String> appInfoIds = null;
		List<String> appInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		cq.select(root.get(AppInfo.id_FIELDNAME));
		Predicate p = cb.isTrue(root.get(AppInfo.allPeoplePublish_FIELDNAME));
		if( StringUtils.isNotEmpty( appType ) && !"all".equalsIgnoreCase( appType ) && !"全部".equalsIgnoreCase( appType )  ){
			p = CriteriaBuilderTools.predicate_and(cb, p, cb.equal(root.get(AppInfo.appType_FIELDNAME), appType));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType) && !"all".equalsIgnoreCase(documentType)) {
			p = cb.and(p, cb.equal(root.get(AppInfo.documentType_FIELDNAME), documentType));
		}
		appInfoIds = em.createQuery(cq.where(p)).getResultList();
		if (appInfoIds == null) {
			appInfoIds = new ArrayList<>();
		}
		appInfoIds_out.addAll(appInfoIds);
		return appInfoIds_out;
	}

	/**
	 * 모든 사용자가 접근 가능한 분류 ID 목록 조회(allPeopleView와 allPeoplePublish 검사)
	 *
	 * @param inAppInfoIds      - 필터 게시판 ID 목록
	 * @param inCategoryIds     - 필터 분류 ID 목록
	 * @param excludCategoryIds - 제외 분류 ID 목록
	 * @return
	 * @throws Exception
	 */
	private List<String> listAllPeopleViewCategoryIds(EntityManagerContainer emc, List<String> inAppInfoIds,
			List<String> inCategoryIds, List<String> excludCategoryIds, String documentType, Integer maxCount)
			throws Exception {
		List<String> categoryInfoIds = null;
		List<String> categoryInfoIds_out = new ArrayList<>();
		EntityManager em = emc.get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		cq.select(root.get(CategoryInfo.id_FIELDNAME));

		Predicate p_all = cb.isTrue(root.get(CategoryInfo.allPeopleView_FIELDNAME));
		p_all = cb.or(p_all, cb.isTrue(root.get(CategoryInfo.allPeoplePublish_FIELDNAME)));

		Predicate p = null;
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, root.get(CategoryInfo.appId_FIELDNAME).in(inAppInfoIds));
		}
		if (ListTools.isNotEmpty(inCategoryIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p, root.get(CategoryInfo.id_FIELDNAME).in(inCategoryIds));
		}
		if (ListTools.isNotEmpty(excludCategoryIds)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.not(root.get(CategoryInfo.id_FIELDNAME).in(excludCategoryIds)));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType)
				&& !"all".equalsIgnoreCase(documentType)) {
			p = CriteriaBuilderTools.predicate_and(cb, p,
					cb.equal(root.get(CategoryInfo.documentType_FIELDNAME), documentType));
		}

		p = CriteriaBuilderTools.predicate_and(cb, p, p_all);

		categoryInfoIds = em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
		if (categoryInfoIds == null) {
			categoryInfoIds = new ArrayList<>();
		}
		categoryInfoIds_out.addAll(categoryInfoIds);
		categoryInfoIds_out = excludListContent(categoryInfoIds_out, excludCategoryIds);
		return categoryInfoIds_out;
	}

	/**
	 * 모든 사용자가 게시 가능한 분류 ID 목록 조회(allPeoplePublish 검사)
	 *
	 * @param inAppInfoIds      - 필터 게시판 ID 목록
	 * @param inCategoryIds     - 필터 분류 ID 목록
	 * @param excludCategoryIds - 제외 분류 ID 목록
	 * @return
	 * @throws Exception
	 */
	private List<String> listAllPeoplePublishCategoryIds(EntityManagerContainer emc, List<String> inAppInfoIds,
			List<String> inCategoryIds, List<String> excludCategoryIds, String documentType, Integer maxCount)
			throws Exception {
		List<String> categoryInfoIds = null;
		List<String> categoryInfoIds_out = new ArrayList<>();

		EntityManager em = emc.get(CategoryInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		cq.select(root.get(CategoryInfo.id_FIELDNAME));

		Predicate p = cb.isTrue(root.get(CategoryInfo.allPeoplePublish_FIELDNAME));
		if (ListTools.isNotEmpty(inAppInfoIds)) {
			p = cb.and(p, root.get(CategoryInfo.appId_FIELDNAME).in(inAppInfoIds));
		}
		if (ListTools.isNotEmpty(inCategoryIds)) {
			p = cb.and(p, root.get(CategoryInfo.id_FIELDNAME).in(inCategoryIds));
		}
		if (ListTools.isNotEmpty(excludCategoryIds)) {
			p = cb.and(p, cb.not(root.get(CategoryInfo.id_FIELDNAME).in(excludCategoryIds)));
		}
		if (StringUtils.isNotEmpty(documentType) && !"全部".equals(documentType)
				&& !"all".equalsIgnoreCase(documentType)) {
			p = cb.and(p, cb.equal(root.get(CategoryInfo.documentType_FIELDNAME), documentType));
		}
		categoryInfoIds = em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
		if (categoryInfoIds == null) {
			categoryInfoIds = new ArrayList<>();
		}
		categoryInfoIds_out.addAll(categoryInfoIds);
		categoryInfoIds_out = excludListContent(categoryInfoIds_out, excludCategoryIds);
		return categoryInfoIds_out;
	}
}
