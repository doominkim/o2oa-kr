package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.jaxrs.view.SimpleItemObj;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Review;
import com.x.cms.core.entity.content.Data;
import com.x.cms.core.express.tools.filter.QueryFilter;
import com.x.query.core.entity.Item;

/**
 * 문서 정보 조회 서비스 클래스
 */
public class DocumentQueryService {

    private UserManagerService userManagerService = new UserManagerService();
    private DocumentInfoService documentInfoService = new DocumentInfoService();

    private ReviewService reviewService = new ReviewService();

    public Document get(String id) throws Exception {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return emc.find(id, Document.class);
        }
    }

    public Long getViewableReview(String id, String personName) throws Exception {
        if (StringUtils.isEmpty(id)) {
            throw new Exception("id is null!");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return reviewService.countViewableWithFilter(emc, personName, new QueryFilter());
        } catch (Exception e) {
            throw e;
        }
    }

    public String getSequence(String id) throws Exception {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return documentInfoService.getSequence(emc, id);
        } catch (Exception e) {
            throw e;
        }
    }

    public Document view(String id, EffectivePerson effectivePerson) throws Exception {
        if (StringUtils.isEmpty(id)) {
            throw new Exception("id is null!");
        }
        Document document = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            document = emc.find(id, Document.class);
            return document;
        } catch (Exception e) {
            throw e;
        }
    }

    public Data getDocumentData(Document document) throws Exception {
        if (document == null) {
            throw new Exception("document is null!");
        }
        List<Item> dataItems = null;
        Business business = null;
        Gson gson = null;
        JsonElement jsonElement = null;
        DataItemConverter<Item> converter = null;

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            business = new Business(emc);
            dataItems = business.itemFactory().listWithDocmentWithPath(document.getId());

            if (ListTools.isEmpty(dataItems)) {
                return new Data();
            } else {
                converter = new DataItemConverter<>(Item.class);
                jsonElement = converter.assemble(dataItems);
                if (jsonElement != null && jsonElement.isJsonObject()) {
                    gson = XGsonBuilder.instance();
                    return gson.fromJson(jsonElement, Data.class);
                } else {
                    return new Data();
                }
            }
        } catch (Exception e) {
            System.out.println("문서로 문서 데이터를 조회하는 중 오류가 발생했습니다. ID:" + document.getId());
            throw e;
        }
    }

    public List<Document> listByCategoryId(String categoryId) throws Exception {
        if (StringUtils.isEmpty(categoryId)) {
            throw new Exception("categoryId is null!");
        }
        List<String> ids = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ids = documentInfoService.listByCategoryId(emc, categoryId);
            return emc.list(Document.class, ids);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<String> listIdsByCategoryId(String categoryId, String orderField, String orderType,
            int maxCount) throws Exception {
        if (StringUtils.isEmpty(categoryId)) {
            throw new Exception("categoryId is null!");
        }
        if (StringUtils.isEmpty(orderField)) {
            orderField = "publishTime";
        }
        if (StringUtils.isEmpty(orderType)) {
            orderType = "DESC";
        }
        if (maxCount == 0) {
            maxCount = 100;
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return documentInfoService.listByCategoryId(emc, categoryId, orderField, orderType,
                    maxCount);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<String> listIdsByCategoryId(String categoryId, Integer maxCount) throws Exception {
        if (StringUtils.isEmpty(categoryId)) {
            throw new Exception("categoryId is null!");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return documentInfoService.listByCategoryId(emc, categoryId, null, null, maxCount);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<String> listIdsByCategoryId(String categoryId, String orderField, String orderType,
            Integer maxCount) throws Exception {
        if (StringUtils.isEmpty(categoryId)) {
            throw new Exception("categoryId is null!");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return documentInfoService.listByCategoryId(emc, categoryId, orderField, orderType,
                    maxCount);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<String> listIdsByAppId(String appId, String documentType, Integer maxCount)
            throws Exception {
        if (StringUtils.isEmpty(appId)) {
            throw new Exception("categoryId is null!");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return documentInfoService.listByAppId(emc, appId, documentType, maxCount);
        } catch (Exception e) {
            throw e;
        }
    }

    public Long countByCategoryId(String categoryId, boolean containDraft) throws Exception {
        if (StringUtils.isEmpty(categoryId)) {
            throw new Exception("categoryId is null!");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return documentInfoService.countByCategoryId(emc, categoryId, containDraft);
        } catch (Exception e) {
            throw e;
        }
    }

    public Long countByAppId(String appId) throws Exception {
        if (StringUtils.isEmpty(appId)) {
            throw new Exception("appId is null!");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return documentInfoService.countByAppId(emc, appId);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Document> list(List<String> ids) throws Exception {
        if (ListTools.isEmpty(ids)) {
            return new ArrayList<>();
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return emc.list(Document.class, ids);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Document> listMyDraft(String name, List<String> categoryIdList, String documentType)
            throws Exception {
        if (StringUtils.isEmpty(name)) {
            throw new Exception("name is null!");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return documentInfoService.listMyDraft(emc, name, categoryIdList, documentType);
        } catch (Exception e) {
            throw e;
        }
    }

    public Item getDataWithDocIdWithPath(Document document, String path0) throws Exception {
        if (StringUtils.isEmpty(path0)) {
            throw new Exception("path0 is null!");
        }
        if (document == null) {
            throw new Exception("document is null!");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return documentInfoService.getDataWithDocIdWithPath(emc, document, path0, null, null,
                    null, null, null, null, null);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Document 테이블에서 권한을 무시하고 조건에 따라 문서 수 조회
     *
     * @param queryFilter
     * @return
     * @throws Exception
     */
    public Long countWithConditionOutofPermission(QueryFilter queryFilter) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return documentInfoService.countWithConditionOutofPermission(emc, queryFilter);
        } catch (Exception e) {
            throw e;
        }
    }

    public Long getViewCount(String id) throws Exception {
        if (StringUtils.isEmpty(id)) {
            throw new Exception("id is null!");
        }
        Business business = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            business = new Business(emc);
            return business.documentViewRecordFactory().sumWithDocmentId(id);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<String> listReviewedIdsByCategoryId(String categoryId, int maxCount)
            throws Exception {
        if (StringUtils.isEmpty(categoryId)) {
            throw new Exception("categoryId is empty!");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return documentInfoService.listReviewedIdsByCategoryId(emc, categoryId, maxCount);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<String> listUnReviewIds(Integer maxCount) throws Exception {
        if (maxCount == null) {
            maxCount = 1000;
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return documentInfoService.listUnReviewIds(emc, maxCount);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 조건에 따라 Review 테이블에서 사용자가 볼 수 있는 모든 문서 수 조회
     *
     * @param personName
     * @param queryFilter
     * @return
     * @throws Exception
     */
    public Long countWithConditionInReview(String personName, QueryFilter queryFilter)
            throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return reviewService.countViewableWithFilter(emc, personName, queryFilter);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Document> listNextWithConditionInReview(String lastId, Integer pageSize,
            String orderField, String orderType, String person, QueryFilter queryFilter)
            throws Exception {
        if (pageSize == 0) {
            pageSize = 20;
        }

        if (StringUtils.isEmpty(orderField)) {
            orderField = Document.sequence_FIELDNAME;
        }
        if (StringUtils.isEmpty(orderType)) {
            orderType = "desc";
        }
        Document document = null;
        List<Document> documentList = null;
        List<Review> reviewList = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            if (StringUtils.isNotEmpty(lastId)) {
                document = emc.find(lastId, Document.class);
            }
            if (document != null) {
                reviewList = reviewService.listNextViewableWithFilter(emc, pageSize,
                        document.getSequence(), orderField, orderType, person, queryFilter);
            } else {
                reviewList = reviewService.listNextViewableWithFilter(emc, pageSize, null,
                        orderField, orderType, person, queryFilter);
            }
            //Review 목록으로 Document 목록 정보 조회
            documentList = listDocumentsWithReview(reviewList);
            return documentList;
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Document> listPrevWithConditionInReview(String lastId, Integer pageSize,
            String orderField, String orderType, String person, QueryFilter queryFilter)
            throws Exception {
        if (pageSize == 0) {
            pageSize = 20;
        }

        if (StringUtils.isEmpty(orderField)) {
            orderField = Document.sequence_FIELDNAME;
        }
        if (StringUtils.isEmpty(orderType)) {
            orderType = "desc";
        }
        Document document = null;
        List<Document> documentList = null;
        List<Review> reviewList = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            if (StringUtils.isNotEmpty(lastId)) {
                document = emc.find(lastId, Document.class);
            }
            if (document != null) {
                reviewList = reviewService.listPrevViewableWithFilter(emc, pageSize,
                        document.getSequence(), orderField, orderType, person, queryFilter);
            } else {
                reviewList = reviewService.listPrevViewableWithFilter(emc, pageSize, null,
                        orderField, orderType, person, queryFilter);
            }
            //Review 목록으로 Document 목록 정보 조회
            documentList = listDocumentsWithReview(reviewList);
            return documentList;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 조건에 맞는 문서 정보 Review 목록을 지정한 건수만큼 조회
     *
     * @param orderField
     * @param orderType
     * @param person
     * @param queryFilter
     * @param maxCount
     * @return
     * @throws Exception
     */
    public List<Review> listNextWithConditionInReview(String orderField, String orderType,
            String person, QueryFilter queryFilter, int maxCount) throws Exception {
        if (maxCount == 0) {
            maxCount = 20;
        }
        if (StringUtils.isEmpty(orderField)) {
            orderField = Document.sequence_FIELDNAME;
        }
        if (StringUtils.isEmpty(orderType)) {
            orderType = "desc";
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<Review> reviews = reviewService.listViewableWithFilter(emc, orderField, orderType,
                    person, queryFilter, maxCount);
            return reviews;
        } catch (Exception e) {
            throw e;
        }
    }

    public List<String> listDocIdsWithConditionInReview(String personName, String orderField,
            String orderType, QueryFilter queryFilter, Integer maxCount) throws Exception {
        List<String> docIds = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            docIds = reviewService.listDocIdsWithConditionInReview(emc, personName, orderField,
                    orderType, queryFilter, maxCount);
            return docIds;
        } catch (Exception e) {
            throw e;
        }
    }

    public List<String> listDocIdsWithConditionInReview(String personName, QueryFilter queryFilter,
            Integer maxCount) throws Exception {
        List<String> docIds = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            docIds = reviewService.listDocIdsWithConditionInReview(emc, personName, queryFilter,
                    maxCount);
            return docIds;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Document 정보 페이지 조회(권한 무시) document와 Review는 sequence 외에 5개 정렬 열 title, appAlias, categoryAlias,
     * categoryName, creatorUnitName의 페이지 조회 지원 sequence와 title, appAlias, categoryAlias, categoryName,
     * creatorUnitName 외의 다른 열 정렬은 모두 메모리에서 페이지 처리
     *
     * @param lastId
     * @param pageSize
     * @param orderField
     * @param orderType
     * @param queryFilter
     * @return
     * @throws Exception
     */
    public List<Document> listNextWithConditionOutofPermission(String lastId, Integer pageSize,
            String orderField, String orderType, QueryFilter queryFilter) throws Exception {
        if (pageSize == 0) {
            pageSize = 20;
        }
        //일반 로직대로 시퀀스 기준 페이지 조회
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return documentInfoService.listNextWithCondition(emc, pageSize, lastId, orderField,
                    orderType, queryFilter);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Document 정보 페이지 조회(권한 무시) document와 Review는 sequence 외에 5개 정렬 열 title, appAlias, categoryAlias,
     * categoryName, creatorUnitName의 페이지 조회 지원 sequence와 title, appAlias, categoryAlias, categoryName,
     * creatorUnitName 외의 다른 열 정렬은 모두 메모리에서 페이지 처리
     *
     * @param lastId
     * @param pageSize
     * @param orderField
     * @param orderType
     * @param queryFilter
     * @return
     * @throws Exception
     */
    public List<Document> listPrevWithConditionOutofPermission(String lastId, Integer pageSize,
            String orderField, String orderType, QueryFilter queryFilter) throws Exception {
        if (pageSize == 0) {
            pageSize = 20;
        }
        //일반 로직대로 시퀀스 기준 페이지 조회
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return documentInfoService.listPrevWithCondition(emc, pageSize, lastId, orderField,
                    orderType, queryFilter);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Document 정보 페이지 조회(권한 포함)
     *
     * @param personName
     * @param orderField
     * @param orderType
     * @param queryFilter
     * @param adjustPage
     * @param pageSize
     * @return
     * @throws Exception
     */
    public List<Document> listPagingWithCondition(String personName, String orderField,
            String orderType, QueryFilter queryFilter, Integer adjustPage,
            Integer pageSize, Boolean isAuthor, Boolean excludeAllRead, String readFlag, Boolean isAdmin)
            throws Exception {
        if (pageSize == 0) {
            pageSize = 20;
        }
        //일반 로직대로 시퀀스 기준 페이지 조회
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            return business.getDocumentFactory()
                    .listPagingWithCondition(personName, orderField, orderType, queryFilter,
                            adjustPage, pageSize, isAuthor, excludeAllRead, readFlag, isAdmin);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 조건에 따라 Document 정보 통계(권한 포함)
     *
     * @param personName
     * @param queryFilter
     * @return
     * @throws Exception
     */
    public Long countWithCondition(String personName, QueryFilter queryFilter, Boolean isAuthor,
            Boolean excludeAllRead, String readFlag, Boolean isAdmin) throws Exception {
        //일반 로직대로 시퀀스 기준 페이지 조회
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            return business.getDocumentFactory()
                    .countWithCondition(personName, queryFilter, isAuthor, excludeAllRead,
                            readFlag, isAdmin);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 조건에 따라 지정 정렬 방식으로 지정 개수의 문서 정보 목록 조회
     *
     * @param orderField
     * @param orderType
     * @param queryFilter
     * @param maxCount
     * @return
     * @throws Exception
     */
    public List<Document> listNextWithConditionOutofPermission(String orderField, String orderType,
            QueryFilter queryFilter, int maxCount) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return documentInfoService.listNextWithCondition(emc, orderField, orderType,
                    queryFilter, maxCount);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Review 목록을 Document 목록으로 변환
     *
     * @param reviewList
     * @return
     * @throws Exception
     */
    private List<Document> listDocumentsWithReview(List<Review> reviewList) throws Exception {
        List<String> docIds = new ArrayList<>();
        if (ListTools.isNotEmpty(reviewList)) {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                for (Review review : reviewList) {
                    docIds.add(review.getDocId());
                }
                return emc.list(Document.class, true, docIds);
            } catch (Exception e) {
                throw e;
            }
        }
        return null;
    }

    /**
     * 지정 문서를 데이터 정렬한 목록 조회
     *
     * @param docIds
     * @param path0Name
     * @param fieldType
     * @param orderType
     * @return
     * @throws Exception
     */
    public List<SimpleItemObj> listSortObjWithOrderFieldInData(List<String> docIds,
            String path0Name, String fieldType, String orderType) throws Exception {
        if (StringUtils.isEmpty(fieldType)) {
            throw new Exception("fieldType is null!");
        }
        List<SimpleItemObj> list = new ArrayList<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<Item> items = documentInfoService.listSortObjWithOrderFieldInData(emc, docIds,
                    path0Name, fieldType, orderType);
            if (ListTools.isNotEmpty(items)) {
                SimpleItemObj simpleItemObj = null;
                for (Item item : items) {
                    simpleItemObj = new SimpleItemObj();
                    simpleItemObj.setId(item.getBundle());
                    simpleItemObj.setValue(item.getStringShortValue());
                    list.add(simpleItemObj);
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return list;
    }

    /**
     * 사용자에게 첨부파일 문서 작업 권한이 있는지 판단 0. 관리자 허용 1. 문서 작성자 허용 2. 분류와 게시판 관리자 허용 3. 문서 작성자 권한을 가진 사용자 허용
     *
     * @param effectivePerson
     * @param doc
     * @return
     * @throws Exception
     */
    public boolean getFileInfoManagerAssess(EffectivePerson effectivePerson, Document doc,
            CategoryInfo category, AppInfo appInfo) throws Exception {
        List<String> setting_permissonNames = new ArrayList<>();
        List<String> own_permissonNames = new ArrayList<>();
        own_permissonNames.add(effectivePerson.getDistinguishedName());
        own_permissonNames.addAll(userManagerService.listIdentitiesWithPerson(
                effectivePerson.getDistinguishedName()));
        own_permissonNames.addAll(
                userManagerService.listUnitNamesWithPerson(effectivePerson.getDistinguishedName()));
        own_permissonNames.addAll(
                userManagerService.listGroupNamesByPerson(effectivePerson.getDistinguishedName()));
        final List<String> oList = new ArrayList<>();
        own_permissonNames.stream().forEach(o -> {
            oList.add(getShortTargetFlag(o));
        });
        own_permissonNames = ListTools.add(own_permissonNames, true, true, oList);
//		LogUtil.INFO( ">>>>>>>>my_own_permissonNames" , own_permissonNames );
        //관리자 작업 허용
        if (effectivePerson.isManager()) {
            return true;
        }
        //관리자 작업 허용
        if (userManagerService.isHasPlatformRole(effectivePerson.getDistinguishedName(),
                ThisApplication.ROLE_CMSManager)) {
            return true;
        }
        //문서 작성자 작업 허용(게시자가 곧 작성자)
        if (doc.getCreatorPerson().equalsIgnoreCase(effectivePerson.getDistinguishedName())) {
            return true;
        }
        //게시판 관리자 작업 허용
        setting_permissonNames.clear();
        setting_permissonNames.addAll(appInfo.getManageablePersonList());
        setting_permissonNames.addAll(appInfo.getManageableUnitList());
        setting_permissonNames.addAll(appInfo.getManageableGroupList());
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//		LogUtil.INFO( ">>>>>>>>1my_own_permissonNames" , own_permissonNames );
//		LogUtil.INFO( ">>>>>>>>1setting_permissonNames" , setting_permissonNames );
        setting_permissonNames.retainAll(own_permissonNames);
        if (ListTools.isNotEmpty(setting_permissonNames)) {
            return true;
        }
        //분류 관리자 작업 허용
        setting_permissonNames.clear();
        setting_permissonNames.addAll(category.getManageablePersonList());
        setting_permissonNames.addAll(category.getManageableUnitList());
        setting_permissonNames.addAll(category.getManageableGroupList());
//		LogUtil.INFO( ">>>>>>>>2my_own_permissonNames" , own_permissonNames );
//		LogUtil.INFO( ">>>>>>>>2setting_permissonNames" , setting_permissonNames );
        setting_permissonNames.retainAll(own_permissonNames);
        if (ListTools.isNotEmpty(setting_permissonNames)) {
            return true;
        }
        //문서 작성자 권한을 가진 사용자 작업 허용
        setting_permissonNames.clear();
        setting_permissonNames.addAll(doc.getAuthorPersonList());
        setting_permissonNames.addAll(doc.getAuthorUnitList());
        setting_permissonNames.addAll(doc.getAuthorGroupList());
//		LogUtil.INFO( ">>>>>>>>3my_own_permissonNames" , own_permissonNames );
//		LogUtil.INFO( ">>>>>>>>3setting_permissonNames" , setting_permissonNames );
        setting_permissonNames.retainAll(own_permissonNames);
        if (ListTools.isNotEmpty(setting_permissonNames)) {
            return true;
        }
        return false;
    }

    private String getShortTargetFlag(String distinguishedName) {
        String target = null;
        if (StringUtils.isNotEmpty(distinguishedName)) {
            String[] array = distinguishedName.split("@");
            StringBuffer sb = new StringBuffer();
            if (array.length == 3) {
                target = sb.append(array[1]).append("@").append(array[2]).toString();
            } else if (array.length == 2) {
                //2단계
                target = sb.append(array[0]).append("@").append(array[1]).toString();
            } else {
                target = array[0];
            }
        }
        return target;
    }

    /**
     * isTop 속성이 비어 있는(NULL) 모든 문서 ID 목록 조회
     *
     * @return
     */
    public List<String> listNULLIsTopDocIds() throws Exception {
        //일반 로직대로 시퀀스 기준 페이지 조회
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            return business.getDocumentFactory().listNULLIsTopDocIds();
        } catch (Exception e) {
            throw e;
        }
    }
}
