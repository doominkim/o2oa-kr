package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Review;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.service.AppDictServiceAdv;
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentQueryService;
import com.x.cms.assemble.control.service.FormServiceAdv;
import com.x.cms.assemble.control.service.PermissionQueryService;
import com.x.cms.assemble.control.service.ScriptServiceAdv;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class BaseAction extends StandardJaxrsAction {

    protected Cache.CacheCategory cacheCategory = new Cache.CacheCategory(AppInfo.class, CategoryInfo.class,
            AppDict.class, AppDictItem.class, View.class,
            ViewCategory.class, ViewFieldConfig.class, Review.class);

    protected AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
    protected FormServiceAdv formServiceAdv = new FormServiceAdv();
    protected ScriptServiceAdv scriptServiceAdv = new ScriptServiceAdv();
    protected AppDictServiceAdv appDictServiceAdv = new AppDictServiceAdv();
    protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
    protected DocumentQueryService documentServiceAdv = new DocumentQueryService();
    protected UserManagerService userManagerService = new UserManagerService();
    protected PermissionQueryService permissionQueryService = new PermissionQueryService();

    /**
     * 현재 로그인 사용자의 접근 가능 게시판·분류 목록 조회
     * 1. 사용자의 접근 권한에 따라 접근 가능한 게시판 정보 ID 목록 조회
     * 2. 사용자의 접근 권한에 따라 접근 가능한 분류 정보 ID 목록 조회
     * 3. 게시판 정보와 분류 정보를 조회해 함께 구성, 분류만 있는 경우에도 게시판 정보를 추가
     * 4. 게시판 정보에 분류가 없으면 해당 게시판 정보는 출력에서 제외
     *
     * @param personName
     * @param isAnonymous
     * @param inAppInfoIds
     * @param appType
     * @param documentType
     * @param manager
     * @param maxCount
     * @return
     * @throws Exception
     */
    protected List<Wo> listViewAbleAppInfoByPermission(String personName, Boolean isAnonymous,
            List<String> inAppInfoIds, String appType, String documentType,
            Boolean manager, Integer maxCount) throws Exception {
        List<String> unitNames = null;
        List<String> groupNames = null;
        List<String> roleNames = null;
        List<String> viewableAppInfoIds = new ArrayList<>();
        List<String> viewableCategoryIds = new ArrayList<>();
        if (manager) {
            if (ListTools.isNotEmpty(inAppInfoIds)) {
                viewableAppInfoIds = inAppInfoIds; // 발행 가능 게시판은 inAppInfoIds로 제한
            } else {
                // 관리자는 모든 게시판을 볼 수 있어야 함
                viewableAppInfoIds = appInfoServiceAdv.listAllIds(documentType);
                if (ListTools.isEmpty(viewableAppInfoIds)) {
                    if (viewableAppInfoIds == null) {
                        viewableAppInfoIds = new ArrayList<>();
                    }
                    viewableAppInfoIds.add("NO_APPINFO");
                }
            }
            viewableCategoryIds = categoryInfoServiceAdv.listCategoryIdsWithAppIds(viewableAppInfoIds, documentType,
                    maxCount);
        } else {
            if (!isAnonymous) {
                unitNames = userManagerService.listUnitNamesWithPerson(personName);
                groupNames = userManagerService.listGroupNamesByPerson(personName);
                roleNames = userManagerService.listRoleNamesByPerson(personName);
            }
            // 사용자가 접근할 수 있는 게시판 조회
            viewableAppInfoIds = permissionQueryService.listViewableAppIdByPerson(personName, isAnonymous, unitNames,
                    groupNames, roleNames, inAppInfoIds, null, documentType, appType, maxCount);
            if (ListTools.isEmpty(viewableAppInfoIds)) {
                viewableAppInfoIds.add("NO_APPINFO");
            }

            // 사용자의 발행 권한에 따라 문서를 발행할 수 있는 분류 정보 ID 목록 조회
            viewableCategoryIds = permissionQueryService.listViewableCategoryIdByPerson(personName, isAnonymous,
                    unitNames, groupNames, roleNames, viewableAppInfoIds,
                    null, null, documentType, appType, maxCount, false);
        }
        return composeCategoriesIntoAppInfo(viewableAppInfoIds, viewableCategoryIds, appType);
    }

    /**
     * 현재 로그인 사용자의 문서 발행 게시판·분류 목록 조회<br/>
     * 1. 사용자의 발행 권한에 따라 문서를 발행할 수 있는 게시판 정보 ID 목록 조회<br/>
     * 2. 사용자의 발행 권한에 따라 문서를 발행할 수 있는 분류 정보 ID 목록 조회<br/>
     * 3. 게시판 정보와 분류 정보를 조회해 함께 구성, 분류만 있는 경우에도 게시판 정보를 추가
     * 4. 게시판 정보에 분류가 없으면 해당 게시판 정보는 출력에서 제외
     *
     * @param personName
     * @param isAnonymous
     * @param inAppInfoIds
     * @param documentType
     * @param appType
     * @param manager
     * @param maxCount
     * @return
     * @throws Exception
     */
    protected List<Wo> listPublishAbleAppInfoByPermission(String personName, Boolean isAnonymous,
            List<String> inAppInfoIds, String documentType, String appType, Boolean manager, Integer maxCount)
            throws Exception {
        List<String> unitNames = null;
        List<String> groupNames = null;
        List<String> roleNames = null;
        List<String> publishableCategoryIds = new ArrayList<>();
        if (manager) {
            // 관리자는 모든 게시판과 분류에서 발행할 수 있으므로 지정한 게시판 ID와 정보 유형만 필터링하면 됨
            publishableCategoryIds = categoryInfoServiceAdv.listCategoryIdsWithAppIds(inAppInfoIds, documentType,
                    maxCount);
        } else {
            // 관리자가 아니면 해당 사용자의 권한에 따라 열람 가능한 게시판과 분류를 추가로 분석해야 함
            if (!isAnonymous) {
                unitNames = userManagerService.listUnitNamesWithPerson(personName);
                groupNames = userManagerService.listGroupNamesByPerson(personName);
                roleNames = userManagerService.listRoleNamesByPerson(personName);
            }
            // 2. 사용자의 발행 권한에 따라 문서를 발행할 수 있는 분류 정보 ID 목록 조회
            publishableCategoryIds = permissionQueryService.listPublishableCategoryIdByPerson(
                    personName, isAnonymous, unitNames, groupNames, roleNames, inAppInfoIds, null, null,
                    documentType, appType, maxCount, false);
        }
        return composeCategoriesIntoAppInfo(inAppInfoIds, publishableCategoryIds, appType);
    }

    /**
     * 지정한 게시판과 분류 ID로 분류를 게시판 정보 안에 구성
     *
     * @param appInfoIds
     * @param categoryInfoIds
     * @param appType
     * @return
     * @throws Exception
     */
    private List<Wo> composeCategoriesIntoAppInfo(List<String> appInfoIds, List<String> categoryInfoIds, String appType)
            throws Exception {
        List<Wo> wraps = null;
        List<WoCategory> wrapCategories = null;
        Map<String, Wo> app_map = new HashMap<>();
        Map<String, WoCategory> category_map = new HashMap<>();
        List<AppInfo> appInfoList = appInfoServiceAdv.list(appInfoIds);
        List<CategoryInfo> categoryInfoList = categoryInfoServiceAdv.list(categoryInfoIds);
        if (ListTools.isNotEmpty(appInfoList)) {
            wraps = Wo.copier.copy(appInfoList);
            if (ListTools.isNotEmpty(wraps)) {
                for (Wo wo : wraps) {
                    app_map.put(wo.getId(), wo);
                }
            }
        }
        if (ListTools.isNotEmpty(categoryInfoList)) {
            wrapCategories = WoCategory.copier.copy(categoryInfoList);
            if (ListTools.isNotEmpty(wrapCategories)) {
                for (WoCategory woCategory : wrapCategories) {
                    category_map.put(woCategory.getId(), woCategory);
                }
            }
        }

        // category_map을 순회하며 category를 app_map의 해당 app에 조립
        Set<String> category_set = category_map.keySet();
        Iterator<String> category_iterator = category_set.iterator();
        String category_key = null;
        AppInfo appInfo = null;
        WoCategory woCategory = null;
        Wo wo_app = null;
        while (category_iterator.hasNext()) {
            category_key = category_iterator.next().toString();
            woCategory = category_map.get(category_key);
            wo_app = app_map.get(woCategory.getAppId());
            if (wo_app == null) {
                // 게시판 정보가 map에 없으면 게시판 정보를 조회해 map에 넣음
                appInfo = appInfoServiceAdv.get(woCategory.getAppId());
                if (appInfo != null) {
                    wo_app = Wo.copier.copy(appInfo);
                }
            }
            if (null != wo_app) {
                if (wo_app.getWrapOutCategoryList() == null) {
                    wo_app.setWrapOutCategoryList(new ArrayList<>());
                }
                wo_app.getWrapOutCategoryList().add(woCategory);
                app_map.put(wo_app.getId(), wo_app);
            }
        }
        // app_map을 List로 옮겨 반환
        if (wraps == null) {
            wraps = new ArrayList<>();
        }
        wraps.clear();
        Set<String> app_set = app_map.keySet();
        Iterator<String> app_iterator = app_set.iterator();
        String app_key = null;
        while (app_iterator.hasNext()) {
            app_key = app_iterator.next().toString();
            wo_app = app_map.get(app_key);
            if (StringUtils.isEmpty(wo_app.getAppType())) {
                wo_app.setAppType("미분류");
            }
            if ("전체".equalsIgnoreCase(appType) || "all".equalsIgnoreCase(appType)
                    || (StringUtils.isNotEmpty(wo_app.getAppType()) && wo_app.getAppType().equalsIgnoreCase(appType))) {
                wraps.add(wo_app);
            }
        }
        return wraps;
    }

    public static class Wo extends AppInfo {

        private Long rank;

        public Long getRank() {
            return rank;
        }

        public void setRank(Long rank) {
            this.rank = rank;
        }

        private static final long serialVersionUID = -5076990764713538973L;

        public static List<String> excludes = new ArrayList<String>();

        @FieldDescribe("게시판의 분류 정보 목록")
        private List<WoCategory> wrapOutCategoryList = null;

        @FieldDescribe("설정 지원 정보 JSON 내용")
        private String config = null;

        public List<WoCategory> getWrapOutCategoryList() {
            return wrapOutCategoryList;
        }

        public void setWrapOutCategoryList(List<WoCategory> wrapOutCategoryList) {
            this.wrapOutCategoryList = wrapOutCategoryList;
        }

        public String getConfig() {
            return this.config;
        }

        public void setConfig(final String config) {
            this.config = config;
        }

        static WrapCopier<AppInfo, Wo> copier = WrapCopierFactory.wo(AppInfo.class, Wo.class, null,
                ListTools.toList(JpaObject.FieldsInvisible));

        static WrapCopier<AppInfo, Wo> copier2 = WrapCopierFactory.wo(AppInfo.class, Wo.class,
                JpaObject.singularAttributeField(AppInfo.class, true, false), null);

        static WrapCopier<AppInfo, Wo> copier3 = WrapCopierFactory.wo(AppInfo.class, Wo.class,
                JpaObject.singularAttributeField(AppInfo.class, true, true), null);

    }

    public static class WoCategory extends CategoryInfo {

        private static final long serialVersionUID = -5076990764713538973L;

        public static List<String> Excludes = new ArrayList<String>();

        static WrapCopier<CategoryInfo, WoCategory> copier = WrapCopierFactory.wo(CategoryInfo.class, WoCategory.class,
                null, ListTools.toList(JpaObject.FieldsInvisible));

        static WrapCopier<CategoryInfo, WoCategory> copier2 = WrapCopierFactory.wo(CategoryInfo.class, WoCategory.class,
                JpaObject.singularAttributeField(CategoryInfo.class, true, true), null);

    }

    /**
     * 게시판 관리자 여부
     *
     * @param person
     * @param appInfo
     * @return
     * @throws Exception
     */
    public boolean isAppInfoManager(String person, List<String> unitNames, List<String> groupNames, AppInfo appInfo) throws Exception {
        if (appInfo != null) {
            if (ListTools.isNotEmpty(appInfo.getManageablePersonList())) {
                if (appInfo.getManageablePersonList().contains(person)) {
                    return true;
                }
            }
            if (ListTools.isNotEmpty(appInfo.getManageableUnitList())) {
                if (ListTools.isNotEmpty(unitNames) &&
                        ListTools.containsAny(unitNames, appInfo.getManageableUnitList())) {
                    return true;
                }
            }
            if (ListTools.isNotEmpty(appInfo.getManageableGroupList())) {
                if (ListTools.isNotEmpty(groupNames) &&
                        ListTools.containsAny(groupNames, appInfo.getManageableGroupList())) {
                    return true;
                }
            }
        }
        return false;
    }

}
