package com.x.attendance.assemble.control.factory.v2;

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.WoGroupShift;
import com.x.attendance.assemble.control.jaxrs.v2.appeal.AppealInfoWi;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveRequestEnums.LeaveRequestStatusEnum;
import com.x.attendance.entity.v2.*;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.x_attendance_assemble_control;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.tools.DefaultCharset;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by fancyLou on 2023/1/31.
 * Copyright © 2023 O2. All rights reserved.
 */
public class AttendanceV2ManagerFactory extends AbstractFactory {

    protected Cache.CacheCategory cacheCategory;

    public AttendanceV2ManagerFactory(Business business) throws Exception {
        super(business);
    }

    /**
     * 내부 요청: 사용자의 특정 일자 근태 그룹 근무 교대 현황 조회
     * 
     * @param person 사용자 dn
     * @param date   yyyy-MM-dd
     * @return
     * @throws Exception
     */
    public WoGroupShift getGroupShiftByPersonDate(String person, String date) throws Exception {
        String encodePerson = URLEncoder.encode(person, DefaultCharset.name);
        return ThisApplication.context().applications()
                .getQuery(x_attendance_assemble_control.class, "v2/group/person/" + encodePerson + "/date/" + date)
                .getData(WoGroupShift.class);
    }

    /**
     * 캐시 객체
     * 
     * @param <T>
     * @param flag
     * @param clz
     * @return
     * @throws Exception
     */
    public <T extends JpaObject> T pick(String flag, Class<T> clz) throws Exception {
        Cache.CacheCategory cacheCategory = new Cache.CacheCategory(clz);
        T t = null;
        Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), flag);
        Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
        if (optional.isPresent()) {
            if (null != optional.get()) {
                t = (T) optional.get();
            }
        } else {
            t = this.entityManagerContainer().flag(flag, clz);
            if (t != null) {
                this.entityManagerContainer().get(clz).detach(t);
                CacheManager.put(cacheCategory, cacheKey, t);
            }
        }
        return t;
    }

    /**
     * 근태 그룹 목록 조회
     * 페이지 조회용
     * 
     * @param adjustPage
     * @param adjustPageSize
     * @param assistAdmin    보조 관리자
     * @param name           비어 있어도 됨
     * @return
     * @throws Exception
     */
    public List<AttendanceV2Group> listGroupWithNameByPage(Integer adjustPage,
            Integer adjustPageSize, String assistAdmin, String name) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Group.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2Group> cq = cb.createQuery(AttendanceV2Group.class);
        Root<AttendanceV2Group> root = cq.from(AttendanceV2Group.class);
        Predicate p = cb.conjunction();
        if (StringUtils.isNotEmpty(name)) {
            p = cb.and(p, cb.like(root.get(AttendanceV2Group_.groupName), "%" + name + "%"));
        }
        if (StringUtils.isNotEmpty(assistAdmin)) {
            p = cb.and(p, cb.isMember(assistAdmin, root.get(AttendanceV2Group_.assistAdminList)));
        }
        cq.select(root).where(p).orderBy(cb.desc(root.get(AttendanceV2Group_.createTime)));
        return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
                .getResultList();
    }

    /**
     * 근태 그룹 총수 조회
     * 페이지 조회용
     * 
     * @param name        비어 있어도 됨
     * @param assistAdmin 보조 관리자
     * @return
     * @throws Exception
     */
    public Long groupCountWithName(String assistAdmin, String name) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Group.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AttendanceV2Group> root = cq.from(AttendanceV2Group.class);
        Predicate p = cb.conjunction();
        if (StringUtils.isNotEmpty(name)) {
            p = cb.and(p, cb.like(root.get(AttendanceV2Group_.groupName), "%" + name + "%"));
        }
        if (StringUtils.isNotEmpty(assistAdmin)) {
            p = cb.and(p, cb.isMember(assistAdmin, root.get(AttendanceV2Group_.assistAdminList)));
        }
        return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
    }

    /**
     * 근무 교대 목록 조회
     * 페이지 조회용
     * 
     * @param adjustPage
     * @param adjustPageSize
     * @param name           비어 있어도 됨
     * @return
     * @throws Exception
     */
    public List<AttendanceV2Shift> listShiftWithNameByPage(Integer adjustPage,
            Integer adjustPageSize, String name) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Shift.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2Shift> cq = cb.createQuery(AttendanceV2Shift.class);
        Root<AttendanceV2Shift> root = cq.from(AttendanceV2Shift.class);
        if (StringUtils.isNotEmpty(name)) {
            Predicate p = cb.like(root.get(AttendanceV2Shift_.shiftName), "%" + name + "%");
            cq.select(root).where(p).orderBy(cb.desc(root.get(AttendanceV2Shift_.createTime)));
        } else {
            cq.select(root).orderBy(cb.desc(root.get(AttendanceV2Shift_.createTime)));
        }
        return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
                .getResultList();
    }

    /**
     * 근무 교대 총수 조회
     * 페이지 조회용
     * 
     * @param name 비어 있어도 됨
     * @return
     * @throws Exception
     */
    public Long shiftCountWithName(String name) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Shift.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AttendanceV2Shift> root = cq.from(AttendanceV2Shift.class);
        if (StringUtils.isNotEmpty(name)) {
            Predicate p = cb.like(root.get(AttendanceV2Shift_.shiftName), "%" + name + "%");
            return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
        }
        return em.createQuery(cq.select(cb.count(root))).getSingleResult();
    }

    /**
     * 근무 교대 id로 해당 교대를 사용하는 모든 근태 그룹 조회
     * 
     * @param shiftId
     * @return
     * @throws Exception
     */
    public List<AttendanceV2Group> listGroupWithShiftId(String shiftId) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Group.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2Group> cq = cb.createQuery(AttendanceV2Group.class);
        Root<AttendanceV2Group> root = cq.from(AttendanceV2Group.class);
        Predicate p = cb.equal(root.get(AttendanceV2Group_.shiftId), shiftId);
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    /**
     * 근무지 id로 해당 근무지를 사용하는 모든 근태 그룹 조회
     * 
     * @param workPlaceId
     * @return
     * @throws Exception
     */
    public List<AttendanceV2Group> listGroupWithWorkPlaceId(String workPlaceId) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Group.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2Group> cq = cb.createQuery(AttendanceV2Group.class);
        Root<AttendanceV2Group> root = cq.from(AttendanceV2Group.class);
        Predicate p = cb.isMember(workPlaceId, root.get(AttendanceV2Group_.workPlaceIdList));
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    /**
     * 사용자가 속한 근태 그룹 조회
     * 
     * @param person distinguishName
     * @return
     * @throws Exception
     */
    public List<AttendanceV2Group> listGroupWithPerson(String person) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Group.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2Group> cq = cb.createQuery(AttendanceV2Group.class);
        Root<AttendanceV2Group> root = cq.from(AttendanceV2Group.class);
        Predicate p = cb.isMember(person, root.get(AttendanceV2Group_.trueParticipantList));
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    /**
     * 사용자가 특정 근태 그룹의 보조 관리자인지 조회
     * @param person
     * @return
     * @throws Exception
     */
    public boolean isAssistAdmin(String person) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Group.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2Group> cq = cb.createQuery(AttendanceV2Group.class);
        Root<AttendanceV2Group> root = cq.from(AttendanceV2Group.class);
        Predicate p = cb.isMember(person, root.get(AttendanceV2Group_.assistAdminList));
        List<AttendanceV2Group> list = em.createQuery(cq.select(root).where(p)).getResultList();
        return (list != null && list.size() > 0) ;
    }

    /**
     * 출퇴근 기록 조회
     * 
     * @param person distinguishName
     * @param date   yyyy-MM-dd
     * @return
     * @throws Exception
     */
    public List<AttendanceV2CheckInRecord> listRecordWithPersonAndDate(String person, String date) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2CheckInRecord.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2CheckInRecord> cq = cb.createQuery(AttendanceV2CheckInRecord.class);
        Root<AttendanceV2CheckInRecord> root = cq.from(AttendanceV2CheckInRecord.class);
        Predicate p = cb.equal(root.get(AttendanceV2CheckInRecord_.userId), person);
        p = cb.and(p, cb.equal(root.get(AttendanceV2CheckInRecord_.recordDateString), date));
        return em
                .createQuery(cq.select(root).where(p).orderBy(cb.asc(root.get(AttendanceV2CheckInRecord_.recordDate))))
                .getResultList();
    }

    /**
     * 출퇴근 기록 조회
     * 페이지 조회용
     * 
     * @param adjustPage
     * @param adjustPageSize
     * @param userId
     * @param startDate      Date
     * @param endDate        Date
     * @return
     * @throws Exception
     */
    public List<AttendanceV2CheckInRecord> listRecordByPage(Integer adjustPage,
            Integer adjustPageSize, String userId, Date startDate, Date endDate, String sourceType, String checkInResult,
                                                            String checkInType, Boolean fieldWork) throws Exception {
        return this.listRecordByPage(adjustPage, adjustPageSize, userId, null, startDate, endDate, sourceType,
                checkInResult, checkInType, fieldWork);
    }

    /**
     * 출퇴근 기록 조회
     * 페이지 조회용
     *
     * @param adjustPage
     * @param adjustPageSize
     * @param userId
     * @param userList       필터링할 사용자 목록, 비어 있어도 됨
     * @param startDate      Date
     * @param endDate        Date
     * @return
     * @throws Exception
     */
    public List<AttendanceV2CheckInRecord> listRecordByPage(Integer adjustPage,
            Integer adjustPageSize, String userId, List<String> userList, Date startDate, Date endDate,
            String sourceType, String checkInResult, String checkInType, Boolean fieldWork) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2CheckInRecord.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2CheckInRecord> cq = cb.createQuery(AttendanceV2CheckInRecord.class);
        Root<AttendanceV2CheckInRecord> root = cq.from(AttendanceV2CheckInRecord.class);
        Predicate p = buildPredicate(cb, root, userId, userList, startDate, endDate, sourceType, checkInResult, checkInType,
                fieldWork);
        if (p == null) {
            cq.select(root).orderBy(cb.desc(root.get(AttendanceV2CheckInRecord_.recordDate)));
        } else {
            cq.select(root).where(p).orderBy(cb.desc(root.get(AttendanceV2CheckInRecord_.recordDate)));
        }
        return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
                .getResultList();
    }

    /**
     * 출퇴근 기록 조회
     * 페이지 조회용
     * 
     * @param userId    비어 있어도 됨
     * @param startDate Date
     * @param endDate   Date
     * @return
     * @throws Exception
     */
    public Long recordCount(String userId, Date startDate, Date endDate, String sourceType, String checkInResult,
                            String checkInType, Boolean fieldWork) throws Exception {
        return this.recordCount(userId, null, startDate, endDate, sourceType, checkInResult, checkInType, fieldWork);
    }

    /**
     * 출퇴근 기록 조회
     * 페이지 조회용
     *
     * @param userId    비어 있어도 됨
     * @param userList  필터링할 사용자 목록, 비어 있어도 됨
     * @param startDate Date
     * @param endDate   Date
     * @return
     * @throws Exception
     */
    public Long recordCount(String userId, List<String> userList, Date startDate, Date endDate, String sourceType,
            String checkInResult, String checkInType, Boolean fieldWork) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2CheckInRecord.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AttendanceV2CheckInRecord> root = cq.from(AttendanceV2CheckInRecord.class);
        Predicate p = buildPredicate(cb, root, userId, userList, startDate, endDate, sourceType, checkInResult, checkInType,
                fieldWork);
        if (p == null) {
            cq.select(cb.count(root));
        } else {
            cq.select(cb.count(root)).where(p);
        }
        return em.createQuery(cq).getSingleResult();
    }

    private Predicate buildPredicate(CriteriaBuilder cb, Root<AttendanceV2CheckInRecord> root, String userId, Date startDate, Date endDate,
                                     String sourceType, String checkInResult,
                                     String checkInType, Boolean fieldWork) {
        return this.buildPredicate(cb, root, userId, null, startDate, endDate, sourceType, checkInResult, checkInType,
                fieldWork);
    }

    private Predicate buildPredicate(CriteriaBuilder cb, Root<AttendanceV2CheckInRecord> root, String userId,
                                     List<String> userList, Date startDate, Date endDate,
                                     String sourceType, String checkInResult,
                                     String checkInType, Boolean fieldWork) {
        Predicate p = null;
        if (StringUtils.isNotEmpty(userId)) {
            p = cb.equal(root.get(AttendanceV2CheckInRecord_.userId), userId);
        }
        if (userList != null && !userList.isEmpty()) {
            if (p == null) {
                p = root.get(AttendanceV2CheckInRecord_.userId).in(userList);
            } else {
                p = cb.and(p, root.get(AttendanceV2CheckInRecord_.userId).in(userList));
            }
        }
        if (startDate != null && endDate != null) {
            if (p == null) {
                p = cb.between(root.get(AttendanceV2CheckInRecord_.recordDate), startDate, endDate);
            } else {
                p = cb.and(p, cb.between(root.get(AttendanceV2CheckInRecord_.recordDate), startDate, endDate));
            }
        }
        if (StringUtils.isNotEmpty(sourceType)) {
            if (p == null) {
                p = cb.equal(root.get(AttendanceV2CheckInRecord_.sourceType), sourceType);
            } else {
                p = cb.and(p, cb.equal(root.get(AttendanceV2CheckInRecord_.sourceType), sourceType));
            }
        }
        if (StringUtils.isNotEmpty(checkInResult)) {
            if (p == null) {
                p = cb.equal(root.get(AttendanceV2CheckInRecord_.checkInResult), checkInResult);
            } else {
                p = cb.and(p, cb.equal(root.get(AttendanceV2CheckInRecord_.checkInResult), checkInResult));
            }
        }
        if (StringUtils.isNotEmpty(checkInType)) {
            if (p == null) {
                p = cb.equal(root.get(AttendanceV2CheckInRecord_.checkInType), checkInType);
            } else {
                p = cb.and(p, cb.equal(root.get(AttendanceV2CheckInRecord_.checkInType), checkInType));
            }
        }
        if (fieldWork != null) {
            if (p == null) {
                p = cb.equal(root.get(AttendanceV2CheckInRecord_.fieldWork), fieldWork);
            } else {
                p = cb.and(p, cb.equal(root.get(AttendanceV2CheckInRecord_.fieldWork), fieldWork));
            }
        }
        return p;
    }

    /**
     * 사용자와 날짜로 근태 상세 조회
     * 
     * @param person
     * @param date
     * @return
     * @throws Exception
     */
    public List<AttendanceV2Detail> listDetailWithPersonAndDate(String person, String date) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Detail.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2Detail> cq = cb.createQuery(AttendanceV2Detail.class);
        Root<AttendanceV2Detail> root = cq.from(AttendanceV2Detail.class);
        Predicate p = cb.equal(root.get(AttendanceV2Detail_.userId), person);
        p = cb.and(p, cb.equal(root.get(AttendanceV2Detail_.recordDateString), date));
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    /**
     * 사용자와 시작·종료 날짜로 근태 상세 조회
     * 
     * @param person
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public List<AttendanceV2Detail> listDetailWithPersonAndStartEndDate(String person, String startDate, String endDate)
            throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Detail.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2Detail> cq = cb.createQuery(AttendanceV2Detail.class);
        Root<AttendanceV2Detail> root = cq.from(AttendanceV2Detail.class);
        Predicate p = cb.equal(root.get(AttendanceV2Detail_.userId), person);
        p = cb.and(p, cb.lessThanOrEqualTo(root.get(AttendanceV2Detail_.recordDateString), endDate));
        p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2Detail_.recordDateString), startDate));
        cq.select(root).where(p).orderBy(cb.asc(root.get(AttendanceV2Detail_.recordDateString)));
        return em.createQuery(cq).getResultList();
    }

    /**
     * 근태 상세 목록 조회
     * 페이지 조회용
     * 
     * @param adjustPage
     * @param adjustPageSize
     * @param userId         비어 있어도 됨
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public List<AttendanceV2Detail> listDetailByPage(Integer adjustPage,
            Integer adjustPageSize, String userId, String startDate, String endDate) throws Exception {
        return this.listDetailByPage(adjustPage, adjustPageSize, userId, null, startDate, endDate);
    }

    /**
     * 근태 상세 목록 조회
     * 페이지 조회용
     *
     * @param adjustPage
     * @param adjustPageSize
     * @param userId         비어 있어도 됨
     * @param userList       필터링할 사용자 목록, 비어 있어도 됨
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public List<AttendanceV2Detail> listDetailByPage(Integer adjustPage,
            Integer adjustPageSize, String userId, List<String> userList, String startDate, String endDate)
            throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Detail.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2Detail> cq = cb.createQuery(AttendanceV2Detail.class);
        Root<AttendanceV2Detail> root = cq.from(AttendanceV2Detail.class);
        Predicate p = cb.lessThanOrEqualTo(root.get(AttendanceV2Detail_.recordDateString), endDate);
        p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2Detail_.recordDateString), startDate));
        if (StringUtils.isNotEmpty(userId)) {
            p = cb.and(p, cb.equal(root.get(AttendanceV2Detail_.userId), userId));
        }
        if (userList != null && !userList.isEmpty()) {
            p = cb.and(p, root.get(AttendanceV2Detail_.userId).in(userList));
        }
        cq.select(root).where(p).orderBy(cb.asc(root.get(AttendanceV2Detail_.createTime)));
        return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
                .getResultList();
    }

    /**
     * 근태 그룹 총수 조회
     * 페이지 조회용
     * 
     * @param userId    비어 있어도 됨
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public Long detailCount(String userId, String startDate, String endDate) throws Exception {
        return this.detailCount(userId, null, startDate, endDate);
    }

    /**
     * 근태 그룹 총수 조회
     * 페이지 조회용
     *
     * @param userId    비어 있어도 됨
     * @param userList  필터링할 사용자 목록, 비어 있어도 됨
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public Long detailCount(String userId, List<String> userList, String startDate, String endDate) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Detail.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AttendanceV2Detail> root = cq.from(AttendanceV2Detail.class);
        Predicate p = cb.lessThanOrEqualTo(root.get(AttendanceV2Detail_.recordDateString), endDate);
        p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2Detail_.recordDateString), startDate));
        if (StringUtils.isNotEmpty(userId)) {
            p = cb.and(p, cb.equal(root.get(AttendanceV2Detail_.userId), userId));
        }
        if (userList != null && !userList.isEmpty()) {
            p = cb.and(p, root.get(AttendanceV2Detail_.userId).in(userList));
        }
        return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
    }

    /**
     * 출퇴근 기록 id로 이의 신청 기록 조회
     * 
     * @param recordId 출퇴근 기록 id
     * @return
     * @throws Exception
     */
    public List<AttendanceV2AppealInfo> listAppealInfoWithRecordId(String recordId) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2AppealInfo.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2AppealInfo> cq = cb.createQuery(AttendanceV2AppealInfo.class);
        Root<AttendanceV2AppealInfo> root = cq.from(AttendanceV2AppealInfo.class);
        Predicate p = cb.equal(root.get(AttendanceV2AppealInfo_.recordId), recordId);
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    /**
     * 날짜로 모든 이의 신청 기록 조회
     * 
     * @param recordDateString 날짜
     * @return
     * @throws Exception
     */
    public List<AttendanceV2AppealInfo> listAppealInfoWithRecordDateString(String recordDateString) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2AppealInfo.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2AppealInfo> cq = cb.createQuery(AttendanceV2AppealInfo.class);
        Root<AttendanceV2AppealInfo> root = cq.from(AttendanceV2AppealInfo.class);
        Predicate p = cb.equal(root.get(AttendanceV2AppealInfo_.recordDateString), recordDateString);
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    /**
     * 지정 사용자의 지정 날짜 범위·상태의 이의 신청 기록 조회.
     *
     * @param person    사용자 dn
     * @param startDate 시작 날짜 yyyy-MM-dd
     * @param endDate   종료 날짜 yyyy-MM-dd
     * @param statuses  이의 신청 상태
     * @return
     * @throws Exception
     */
    public List<AttendanceV2AppealInfo> listAppealInfoByPersonDateAndStatus(String person, String startDate,
            String endDate, Integer... statuses) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2AppealInfo.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2AppealInfo> cq = cb.createQuery(AttendanceV2AppealInfo.class);
        Root<AttendanceV2AppealInfo> root = cq.from(AttendanceV2AppealInfo.class);
        Predicate p = cb.equal(root.get(AttendanceV2AppealInfo_.userId), person);
        p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2AppealInfo_.recordDateString), startDate));
        p = cb.and(p, cb.lessThanOrEqualTo(root.get(AttendanceV2AppealInfo_.recordDateString), endDate));
        if (statuses != null && statuses.length > 0) {
            p = cb.and(p, root.get(AttendanceV2AppealInfo_.status).in((Object[]) statuses));
        }
        cq.select(root).where(p).orderBy(cb.desc(root.get(AttendanceV2AppealInfo_.recordDate)));
        return em.createQuery(cq).getResultList();
    }

    /**
     * 이의 신청 기록 조회
     * 페이지 조회용
     * startDate 와 endDate 는 함께 값이 있어야 함
     * 
     * @param adjustPage
     * @param adjustPageSize
     * @return
     * @throws Exception
     */
    public List<AttendanceV2AppealInfo> listAppealInfoByPage(Integer adjustPage,
            Integer adjustPageSize, AppealInfoWi wi) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2AppealInfo.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2AppealInfo> cq = cb.createQuery(AttendanceV2AppealInfo.class);
        Root<AttendanceV2AppealInfo> root = cq.from(AttendanceV2AppealInfo.class);
        Predicate p = null;
        if (wi.getUsers() != null && !wi.getUsers().isEmpty()) {
            p = root.get(AttendanceV2AppealInfo_.userId).in(wi.getUsers());
            // p = cb.equal(root.get(AttendanceV2AppealInfo_.userId), userId);
        }
        if (StringUtils.isNotEmpty(wi.getStartDate()) && StringUtils.isNotEmpty(wi.getEndDate())) {
            if (p == null) {
                p = cb.lessThanOrEqualTo(root.get(AttendanceV2AppealInfo_.recordDateString), wi.getEndDate());
                p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2AppealInfo_.recordDateString), wi.getStartDate()));
            } else {
                p = cb.and(p, cb.lessThanOrEqualTo(root.get(AttendanceV2AppealInfo_.recordDateString), wi.getEndDate()));
                p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2AppealInfo_.recordDateString), wi.getStartDate()));
            }
        }
        if (StringUtils.isNotEmpty(wi.getRecordId())) {
            if (p == null) {
                p = cb.equal(root.get(AttendanceV2AppealInfo_.recordId), wi.getRecordId());
            } else {
                p = cb.and(p, cb.equal(root.get(AttendanceV2AppealInfo_.recordId), wi.getRecordId()));
            }
        }
        if (StringUtils.isNotEmpty(wi.getStatus()) && wi.isStatusValid()) {
            if (p == null) {
                p = cb.equal(root.get(AttendanceV2AppealInfo_.status), wi.getStatus());
            } else {
                p = cb.and(p, cb.equal(root.get(AttendanceV2AppealInfo_.status), wi.getStatus()));
            }
        }

        if (p == null) {
            cq.select(root).orderBy(cb.desc(root.get(AttendanceV2AppealInfo_.recordDate)));
        } else {
            cq.select(root).where(p).orderBy(cb.desc(root.get(AttendanceV2AppealInfo_.recordDate)));
        }
        return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
                .getResultList();
    }

    /**
     * 이의 신청 기록 총수 조회
     * 페이지 조회용
     * 
     * @param users
     * @return
     * @throws Exception
     */
    public Long appealCount(List<String> users, String startDate, String endDate) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2AppealInfo.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AttendanceV2AppealInfo> root = cq.from(AttendanceV2AppealInfo.class);
        Predicate p = null;
        if (users != null && !users.isEmpty()) {
            p = root.get(AttendanceV2AppealInfo_.userId).in(users);
            // p = cb.equal(root.get(AttendanceV2AppealInfo_.userId), userId);
        }
        if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
            if (p == null) {
                p = cb.lessThanOrEqualTo(root.get(AttendanceV2AppealInfo_.recordDateString), endDate);
                p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2AppealInfo_.recordDateString), startDate));
            } else {
                p = cb.and(p, cb.lessThanOrEqualTo(root.get(AttendanceV2AppealInfo_.recordDateString), endDate));
                p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2AppealInfo_.recordDateString), startDate));
            }
        }
        if (p == null) {
            return em.createQuery(cq.select(cb.count(root))).getSingleResult();
        } else {
            return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
        }
    }

    /**
     * 날짜로 이의 신청 기록 조회
     * 이의 신청 횟수 계산용, init 및 admin 처리 건 제외
     * 
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @param userId    선택
     * @return
     * @throws Exception
     */
    public List<AttendanceV2AppealInfo> listAppealInfoByDateNotInit(Date startDate, Date endDate, String userId)
            throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2AppealInfo.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2AppealInfo> cq = cb.createQuery(AttendanceV2AppealInfo.class);
        Root<AttendanceV2AppealInfo> root = cq.from(AttendanceV2AppealInfo.class);
        Predicate p = cb.between(root.get(AttendanceV2AppealInfo_.recordDate), startDate, endDate);
        p = cb.and(p, cb.equal(root.get(AttendanceV2AppealInfo_.userId), userId));
        p = cb.and(p, cb.notEqual(root.get(AttendanceV2AppealInfo_.status), AttendanceV2AppealInfo.status_TYPE_INIT));
        p = cb.and(p,
                cb.notEqual(root.get(AttendanceV2AppealInfo_.status), AttendanceV2AppealInfo.status_TYPE_END_BY_ADMIN));

        cq.select(root).where(p).orderBy(cb.desc(root.get(AttendanceV2AppealInfo_.recordDate)));
        return em.createQuery(cq).getResultList();
    }

    /**
     * 전달된 시간보다 이전의 미발송 메시지 데이터
     * 
     * @param date
     * @return
     * @throws Exception
     */
    public List<AttendanceV2AlertMessage> listAlertMessageBeforeDate(Date date) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2AlertMessage.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2AlertMessage> cq = cb.createQuery(AttendanceV2AlertMessage.class);
        Root<AttendanceV2AlertMessage> root = cq.from(AttendanceV2AlertMessage.class);
        Predicate p = cb.lessThan(root.get(AttendanceV2AlertMessage_.sendDateTime), date);
        p = cb.and(p, cb.equal(root.get(AttendanceV2AlertMessage_.sendStatus), false));
        cq.select(root).where(p);
        return em.createQuery(cq).getResultList();
    }

    /**
     * 개인 설정
     * 
     * @param person
     * @return
     * @throws Exception
     */
    public List<AttendanceV2PersonConfig> personConfigWithPerson(String person) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2PersonConfig.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2PersonConfig> cq = cb.createQuery(AttendanceV2PersonConfig.class);
        Root<AttendanceV2PersonConfig> root = cq.from(AttendanceV2PersonConfig.class);
        Predicate p = cb.equal(root.get(AttendanceV2PersonConfig_.person), person);
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    /**
     * 사용자의 출퇴근 기록 시간이 외출 데이터에 포함되는지 조회
     * 
     * @param person     사용자
     * @param recordTime 출퇴근 기록 시간
     * @return
     * @throws Exception
     */
    public List<AttendanceV2LeaveData> listLeaveDataWithRecordTime(String person, Date recordTime) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2LeaveData.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2LeaveData> cq = cb.createQuery(AttendanceV2LeaveData.class);
        Root<AttendanceV2LeaveData> root = cq.from(AttendanceV2LeaveData.class);
        Predicate p = cb.equal(root.get(AttendanceV2LeaveData_.person), person);
        p = cb.and(p, cb.lessThanOrEqualTo(root.get(AttendanceV2LeaveData_.startTime), recordTime));
        p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2LeaveData_.endTime), recordTime));
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    /**
     * 사용자의 출퇴근 기록 시간이 휴가 데이터에 포함되는지 조회
     * @param person
     * @param recordTime
     * @return
     * @throws Exception
     */
    public List<AttendanceV2LeaveRequest> listLeaveRequestWithRecordTime(String person, Date recordTime) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2LeaveRequest.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2LeaveRequest> cq = cb.createQuery(AttendanceV2LeaveRequest.class);
        Root<AttendanceV2LeaveRequest> root = cq.from(AttendanceV2LeaveRequest.class);
        Predicate p = cb.equal(root.get(AttendanceV2LeaveRequest_.person), person);
        p = cb.and(p, cb.equal(root.get(AttendanceV2LeaveRequest_.status), LeaveRequestStatusEnum.APPLYING.getValue()));
        p = cb.and(p, cb.lessThanOrEqualTo(root.get(AttendanceV2LeaveRequest_.startTime), recordTime));
        p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2LeaveRequest_.endTime), recordTime));
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    /**
     * 휴가 데이터 조회
     * 페이지 조회용
     * 
     * @param adjustPage
     * @param adjustPageSize
     * @param person
     * @return
     * @throws Exception
     */
    public List<AttendanceV2LeaveData> listLeaveDataByPage(Integer adjustPage,
            Integer adjustPageSize, String person) throws Exception {
        return listLeaveDataByPage(adjustPage, adjustPageSize, person, null, null, null);
    }

    public List<AttendanceV2LeaveData> listLeaveDataByPage(Integer adjustPage, Integer adjustPageSize, String person,
            List<String> personList, Date startDate, Date endDate) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2LeaveData.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2LeaveData> cq = cb.createQuery(AttendanceV2LeaveData.class);
        Root<AttendanceV2LeaveData> root = cq.from(AttendanceV2LeaveData.class);
        Predicate p = leaveDataPredicate(cb, root, person, personList, startDate, endDate);
        cq.select(root).where(p).orderBy(cb.desc(root.get(AttendanceV2LeaveData_.startTime)));
        return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
                .getResultList();
    }

    /**
     * 휴가 데이터 조회
     * 페이지 조회용
     * 
     * @param person
     * @return
     * @throws Exception
     */
    public Long listLeaveDataCount(String person) throws Exception {
        return listLeaveDataCount(person, null, null, null);
    }

    public Long listLeaveDataCount(String person, List<String> personList, Date startDate, Date endDate)
            throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2LeaveData.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AttendanceV2LeaveData> root = cq.from(AttendanceV2LeaveData.class);
        Predicate p = leaveDataPredicate(cb, root, person, personList, startDate, endDate);
        return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
    }

    private Predicate leaveDataPredicate(CriteriaBuilder cb, Root<AttendanceV2LeaveData> root, String person,
            List<String> personList, Date startDate, Date endDate) {
        Predicate p = cb.conjunction();
        if (StringUtils.isNotEmpty(person)) {
            p = cb.and(p, cb.equal(root.get(AttendanceV2LeaveData_.person), person));
        }
        if (personList != null && !personList.isEmpty()) {
            p = cb.and(p, root.get(AttendanceV2LeaveData_.person).in(personList));
        }
        if (endDate != null) {
            p = cb.and(p, cb.lessThanOrEqualTo(root.get(AttendanceV2LeaveData_.startTime), endDate));
        }
        if (startDate != null) {
            p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2LeaveData_.endTime), startDate));
        }
        return p;
    }

    /**
     * 근무 배정 데이터 조회
     * 
     * @param groupId 필수 근태 그룹 id
     * @param month   월 필드 yyyy-MM
     * @param day     날짜 필드 yyyy-MM-dd
     * @param person  사용자 dn
     * @return
     * @throws Exception
     */
    public List<AttendanceV2GroupSchedule> listGroupSchedule(String groupId, String month, String day, String person)
            throws Exception {
        if (StringUtils.isEmpty(groupId)) {
            return null;
        }
        EntityManager em = this.entityManagerContainer().get(AttendanceV2GroupSchedule.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2GroupSchedule> cq = cb.createQuery(AttendanceV2GroupSchedule.class);
        Root<AttendanceV2GroupSchedule> root = cq.from(AttendanceV2GroupSchedule.class);
        Predicate p = cb.equal(root.get(AttendanceV2GroupSchedule_.groupId), groupId);
        if (StringUtils.isNotEmpty(month)) {
            p = cb.and(p, cb.equal(root.get(AttendanceV2GroupSchedule_.scheduleMonthString), month));
        }
        if (StringUtils.isNotEmpty(day)) {
            p = cb.and(p, cb.equal(root.get(AttendanceV2GroupSchedule_.scheduleDateString), day));
        }
        if (StringUtils.isNotEmpty(person)) {
            p = cb.and(p, cb.equal(root.get(AttendanceV2GroupSchedule_.userId), person));
        }
        cq.select(root).where(p).orderBy(cb.asc(root.get(AttendanceV2GroupSchedule_.scheduleDateString)));

        return em.createQuery(cq).getResultList();
    }

    /**
     * 근무 배정 설정 데이터 조회
     * @param groupId
     * @return
     * @throws Exception
     */
    public List<AttendanceV2GroupScheduleConfig> listGroupScheduleConfig(String groupId) throws Exception {
        if (StringUtils.isEmpty(groupId)) {
            return null;
        }
        EntityManager em = this.entityManagerContainer().get(AttendanceV2GroupScheduleConfig.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2GroupScheduleConfig> cq = cb.createQuery(AttendanceV2GroupScheduleConfig.class);
        Root<AttendanceV2GroupScheduleConfig> root = cq.from(AttendanceV2GroupScheduleConfig.class);
        Predicate p = cb.equal(root.get(AttendanceV2GroupScheduleConfig_.groupId), groupId);
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    public Integer findAttendanceV2LeaveTypeBiggestOrderNumber() throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2LeaveType.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<AttendanceV2LeaveType> root = cq.from(AttendanceV2LeaveType.class);
        cq.select(cb.max(root.get(AttendanceV2LeaveType_.orderNumber)));
        Integer maxOrderNumber = em.createQuery(cq).getSingleResult();
        return maxOrderNumber == null ? 100 : maxOrderNumber;
    }
}
