package com.x.attendance.assemble.control.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.selfholiday.ActionListNextWithFilter;
import com.x.attendance.assemble.control.jaxrs.selfholiday.WrapInFilter;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.attendance.entity.AttendanceSelfHoliday_;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * 시스템 설정 정보 테이블 기본 기능 서비스 클래스
 */
public class AttendanceSelfHolidayFactory extends AbstractFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceSelfHolidayFactory.class);

    public AttendanceSelfHolidayFactory(Business business) throws Exception {
        super(business);
    }

    // @MethodDescribe("지정 Id의 AttendanceSelfHoliday 정보 객체 조회")
    public AttendanceSelfHoliday get(String id) throws Exception {
        return this.entityManagerContainer().find(id, AttendanceSelfHoliday.class, ExceptionWhen.none);
    }

//@MethodDescribe("전체 AttendanceSelfHoliday 정보 목록 조회")
    public List<String> listAll() throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceSelfHoliday.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<AttendanceSelfHoliday> root = cq.from(AttendanceSelfHoliday.class);
        cq.select(root.get(AttendanceSelfHoliday_.id));
        return em.createQuery(cq).getResultList();
    }

//	@MethodDescribe("지정 Id의 AttendanceSelfHoliday 정보 목록 조회")
    public List<AttendanceSelfHoliday> list(List<String> ids) throws Exception {
        if (ids == null || ids.size() == 0) {
            return new ArrayList<AttendanceSelfHoliday>();
        }
        EntityManager em = this.entityManagerContainer().get(AttendanceSelfHoliday.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceSelfHoliday> cq = cb.createQuery(AttendanceSelfHoliday.class);
        Root<AttendanceSelfHoliday> root = cq.from(AttendanceSelfHoliday.class);
        Predicate p = root.get(AttendanceSelfHoliday_.id).in(ids);
        return em.createQuery(cq.where(p)).getResultList();
    }

    public List<AttendanceSelfHoliday> listWithBatchFlag(String batchFlag) throws Exception {
        if (StringUtils.isEmpty(batchFlag)) {
            return null;
        }
        EntityManager em = this.entityManagerContainer().get(AttendanceSelfHoliday.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceSelfHoliday> cq = cb.createQuery(AttendanceSelfHoliday.class);
        Root<AttendanceSelfHoliday> root = cq.from(AttendanceSelfHoliday.class);
        Predicate p = cb.equal(root.get(AttendanceSelfHoliday_.batchFlag), batchFlag);
        return em.createQuery(cq.where(p)).getResultList();
    }

    public List<String> listIdsWithBatchFlag(String batchFlag) throws Exception {
        if (StringUtils.isEmpty(batchFlag)) {
            return null;
        }
        EntityManager em = this.entityManagerContainer().get(AttendanceSelfHoliday.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<AttendanceSelfHoliday> root = cq.from(AttendanceSelfHoliday.class);
        Predicate p = cb.equal(root.get(AttendanceSelfHoliday_.batchFlag), batchFlag);
        cq.select(root.get(AttendanceSelfHoliday_.id));
        return em.createQuery(cq.where(p)).getResultList();
    }

    // @MethodDescribe("직원 한 명의 AttendanceSelfHoliday 정보 목록 조회")
    public List<String> getByEmployeeName(String empName) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceSelfHoliday.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<AttendanceSelfHoliday> root = cq.from(AttendanceSelfHoliday.class);
        Predicate p = root.get(AttendanceSelfHoliday_.employeeName).in(empName);
        cq.select(root.get(AttendanceSelfHoliday_.id));
        return em.createQuery(cq.where(p)).getResultList();
    }

    // @MethodDescribe("프로세스 문서 ID로 직원의 AttendanceSelfHoliday 정보 목록 조회")
//	public List<String> getByWorkFlowDocId(String docId) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(AttendanceSelfHoliday.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<AttendanceSelfHoliday> root = cq.from( AttendanceSelfHoliday.class);
//		Predicate p = cb.equal(root.get(AttendanceSelfHoliday_.docId), docId);
//		cq.select(root.get(AttendanceSelfHoliday_.id));
//		return em.createQuery(cq.where(p)).getResultList();
//	}

    public List<String> listByStartDateAndEndDate(Date startDate, Date endDate) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceSelfHoliday.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<AttendanceSelfHoliday> root = cq.from(AttendanceSelfHoliday.class);
        Predicate p = cb.between(root.get(AttendanceSelfHoliday_.startTime), startDate, endDate);
        p = cb.and(p, cb.between(root.get(AttendanceSelfHoliday_.endTime), startDate, endDate));
        cq.select(root.get(AttendanceSelfHoliday_.id));
        return em.createQuery(cq.where(p)).getResultList();
    }

    /**
     * 다음 페이지 정보 데이터 조회
     * 
     * @param id
     * @param count
     * @param sequence
     * @param wrapIn
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public List<AttendanceSelfHoliday> listIdsNextWithFilter(String id, Integer count, Object sequence,
            ActionListNextWithFilter.WrapIn wrapIn) throws Exception {
        // 이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
        EntityManager em = this.entityManagerContainer().get(AttendanceSelfHoliday.class);
        String order = wrapIn.getOrder();// 정렬 방식
        List<Object> vs = new ArrayList<>();
        StringBuffer sql_stringBuffer = new StringBuffer();

        if (order == null || order.isEmpty()) {
            order = "DESC";
        }

        Integer index = 1;
        sql_stringBuffer.append("SELECT o FROM " + AttendanceSelfHoliday.class.getCanonicalName() + " o where 1=1");

        if ((null != sequence)) {
            sql_stringBuffer.append(
                    " and o.sequence " + (StringUtils.equalsIgnoreCase(order, "DESC") ? "<" : ">") + (" ?" + (index)));
            vs.add(sequence);
            index++;
        }
        if ((null != wrapIn.getQ_empName()) && (!wrapIn.getQ_empName().isEmpty())) {
            sql_stringBuffer.append(" and o.employeeName = ?" + (index));
            vs.add(wrapIn.getQ_empName());
            index++;
        }
        if (null != wrapIn.getUnitNames() && wrapIn.getUnitNames().size() > 0) {
            sql_stringBuffer.append(" and o.unitOu in ( ?" + (index) + ")");
            vs.add(wrapIn.getUnitNames());
            index++;
        }
        if (null != wrapIn.getTopUnitNames() && wrapIn.getTopUnitNames().size() > 0) {
            sql_stringBuffer.append(" and o.topUnitOu in ( ?" + (index) + ")");
            vs.add(wrapIn.getTopUnitNames());
            index++;
        }
        if (null != wrapIn.getStartdate() && null != wrapIn.getEnddate()) {
            sql_stringBuffer.append(" and o.startTime >=  ?" + (index));
            vs.add(wrapIn.getStartdate());
            index++;

            sql_stringBuffer.append(" and o.endTime <= ?" + (index));
            vs.add(wrapIn.getEnddate());
            index++;
        }

        if (StringUtils.isNotEmpty(wrapIn.getKey())) {
            sql_stringBuffer.append(" order by o." + wrapIn.getKey() + " " + order);
        } else {
            sql_stringBuffer.append(" order by o.sequence " + order);
        }

        Query query = em.createQuery(sql_stringBuffer.toString(), AttendanceSelfHoliday.class);
        // System.out.println("query=" +query.toString());
        // 조회에 모든 매개변수 값 설정
        for (int i = 0; i < vs.size(); i++) {
            query.setParameter(i + 1, vs.get(i));
        }
        return query.setMaxResults(count).getResultList();
    }

    /**
     * 이전 페이지 문서 정보 데이터 조회
     * 
     * @param id
     * @param count
     * @param sequence
     * @param wrapIn
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public List<AttendanceSelfHoliday> listIdsPrevWithFilter(String id, Integer count, Object sequence,
            WrapInFilter wrapIn) throws Exception {
        // 이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
        EntityManager em = this.entityManagerContainer().get(AttendanceSelfHoliday.class);
        String order = wrapIn.getOrder();// 정렬 방식
        List<Object> vs = new ArrayList<>();
        StringBuffer sql_stringBuffer = new StringBuffer();
        Integer index = 1;

        if (order == null || order.isEmpty()) {
            order = "DESC";
        }

        sql_stringBuffer.append("SELECT o FROM " + AttendanceSelfHoliday.class.getCanonicalName() + " o where 1=1");
        if ((null != sequence)) {
            sql_stringBuffer.append(
                    " and o.sequence " + (StringUtils.equalsIgnoreCase(order, "DESC") ? ">" : "<") + (" ?" + (index)));
            vs.add(sequence);
            index++;
        }
        if ((null != wrapIn.getQ_empName()) && (!wrapIn.getQ_empName().isEmpty())) {
            sql_stringBuffer.append(" and o.employeeName = ?" + (index));
            vs.add(wrapIn.getQ_empName());
            index++;
        }
        if (null != wrapIn.getUnitNames() && wrapIn.getUnitNames().size() > 0) {
            sql_stringBuffer.append(" and o.unitName in ( ?" + (index) + ")");
            vs.add(wrapIn.getUnitNames());
            index++;
        }
        if (null != wrapIn.getTopUnitNames() && wrapIn.getTopUnitNames().size() > 0) {
            sql_stringBuffer.append(" and o.topUnitName in ( ?" + (index) + ")");
            vs.add(wrapIn.getTopUnitNames());
            index++;
        }

        if (StringUtils.isNotEmpty(wrapIn.getKey())) {
            sql_stringBuffer.append(" order by o." + wrapIn.getKey() + " " + order);
        } else {
            sql_stringBuffer.append(" order by o.sequence " + order);
        }

        Query query = em.createQuery(sql_stringBuffer.toString(), AttendanceSelfHoliday.class);
        // 조회에 모든 매개변수 값 설정
        for (int i = 0; i < vs.size(); i++) {
            query.setParameter(i + 1, vs.get(i));
        }
        LOGGER.debug("query :{}, parameter:{}.", query::toString, () -> vs);
        return query.setMaxResults(20).getResultList();
    }

    /**
     * 조건에 맞는 문서 정보 총수 조회
     * 
     * @param wrapIn
     * @return
     * @throws Exception
     */
    public long getCountWithFilter(WrapInFilter wrapIn) throws Exception {
        // 이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
        EntityManager em = this.entityManagerContainer().get(AttendanceSelfHoliday.class);
        List<Object> vs = new ArrayList<>();
        StringBuffer sql_stringBuffer = new StringBuffer();
        Integer index = 1;

        sql_stringBuffer
                .append("SELECT count(o.id) FROM " + AttendanceSelfHoliday.class.getCanonicalName() + " o where 1=1");

        if ((null != wrapIn.getQ_empName()) && (!wrapIn.getQ_empName().isEmpty())) {
            sql_stringBuffer.append(" and o.employeeName = ?" + (index));
            vs.add(wrapIn.getQ_empName());
            index++;
        }
        if (null != wrapIn.getUnitNames() && wrapIn.getUnitNames().size() > 0) {
            sql_stringBuffer.append(" and o.unitName in ( ?" + (index) + ")");
            vs.add(wrapIn.getUnitNames());
            index++;
        }
        if (null != wrapIn.getTopUnitNames() && wrapIn.getTopUnitNames().size() > 0) {
            sql_stringBuffer.append(" and o.topUnitName in ( ?" + (index) + ")");
            vs.add(wrapIn.getTopUnitNames());
            index++;
        }

        Query query = em.createQuery(sql_stringBuffer.toString(), AttendanceSelfHoliday.class);
        // 조회에 모든 매개변수 값 설정
        for (int i = 0; i < vs.size(); i++) {
            query.setParameter(i + 1, vs.get(i));
        }
        return (Long) query.getSingleResult();
    }

    public List<String> getByPersonName(String personName) throws Exception {
        if (personName == null || personName.isEmpty()) {
            throw new Exception("personName is null.");
        }
        EntityManager em = this.entityManagerContainer().get(AttendanceSelfHoliday.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<AttendanceSelfHoliday> root = cq.from(AttendanceSelfHoliday.class);
        Predicate p = cb.equal(root.get(AttendanceSelfHoliday_.employeeName), personName);
        cq.select(root.get(AttendanceSelfHoliday_.id));
        return em.createQuery(cq.where(p)).getResultList();
    }
}