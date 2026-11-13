package com.x.base.core.project.jaxrs;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.CollectionTools;
import com.x.base.core.project.tools.ListTools;

public abstract class StandardJaxrsAction extends AbstractJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(StandardJaxrsAction.class);

	protected static Integer list_max = 200;
	protected static Integer list_min = 1;

	protected static final String DESC = "desc";
	protected static final String ASC = "asc";

	/**
	 * 조회 데이터의 다음 페이지 객체 정보
	 *
	 * [2015년 12월 24일 홍길동이 주석 추가, 코드 변경 없음]
	 *
	 * @param cls           엔티티 클래스
	 * @param wcls          wrap 클래스
	 * @param id            이전 페이지 마지막 항목의 ID
	 * @param count         페이지당 항목 수:pagesize
	 * @param sequenceField 페이징 순서로 쓸 속성명
	 * @param equals        같음 조건 집합
	 * @param notEquals     같지 않음(예외) 조건 집합
	 * @param likes         퍼지 조회 조건 집합
	 * @param ins           IN 조회 조건 집합
	 * @param notIns        NOT IN 조회 조건 집합
	 * @param members       소속
	 * @param notMembers    비소속
	 * @param andJoin       조건 연결 방식
	 * @param order         정렬 방식 ASC|DESC
	 * @return
	 * @throws Exception
	 */
	public <T extends JpaObject, W extends GsonPropertyObject> ActionResult<List<W>> standardListNext(Class<T> cls,
			Class<W> wcls, String id, Integer count, String sequenceField, EqualsTerms equals, NotEqualsTerms notEquals,
			LikeTerms likes, InTerms ins, NotInTerms notIns, MemberTerms members, NotMemberTerms notMembers,
			BetweenTerms betweens, boolean andJoin, String order) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
			Object sequence = null;
			if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
				sequence = PropertyUtils.getProperty(emc.find(id, cls, ExceptionWhen.not_found), sequenceField);
			}
			EntityManager em = emc.get(cls);
			String str = "SELECT o FROM " + cls.getCanonicalName() + " o";
			/* 사전 컴파일 SQL 문의 파라미터 순번, 반드시 1부터 시작 */
			Integer index = 1;
			List<String> ps = new ArrayList<>();
			List<Object> vs = new ArrayList<>();
			// sequence 값이 비어 있지 않으면 sequence부터, 비어 있으면 처음부터 시작
			if (null != sequence) {
				ps.add("o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? "<" : ">")
						+ (" ?" + (index)));
				vs.add(sequence);
				index++;
			}
			// 아래에서 각종 조건절을 구성해 List<String> ps에, 해당 값은 List<Object> vs에 넣음
			if (null != equals && (!equals.isEmpty())) {
				for (Entry<String, Object> en : equals.entrySet()) {
					ps.add("o." + en.getKey() + (" = ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notEquals && (!notEquals.isEmpty())) {
				for (Entry<String, Object> en : notEquals.entrySet()) {
					ps.add("(o." + en.getKey() + (" <> ?" + index) + " or o." + en.getKey() + " is null)");
					vs.add(en.getValue());
					index++;
				}
			}
			// like는 특수함, 여러 like는 보통 or로 연결
			if (null != likes && (!likes.isEmpty())) {
				List<String> ors = new ArrayList<>();
				for (Entry<String, Object> en : likes.entrySet()) {
					for (String s : StringUtils.split(en.getValue().toString(), " ")) {
						ors.add("o." + en.getKey() + (" Like ?" + index));
						vs.add("%" + s + "%");
						index++;
					}
				}
				ps.add("(" + StringUtils.join(ors, " or ") + ")");
			}
			if (null != ins && (!ins.isEmpty())) {
				for (Entry<String, Collection<?>> en : ins.entrySet()) {
					ps.add("o." + en.getKey() + (" in ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notIns && (!notIns.isEmpty())) {
				for (Entry<String, Collection<?>> en : notIns.entrySet()) {
					ps.add("o." + en.getKey() + (" not in ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != members && (!members.isEmpty())) {
				for (Entry<String, Object> en : members.entrySet()) {
					ps.add(("?" + index) + (" member of o." + en.getKey()));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notMembers && (!notMembers.isEmpty())) {
				for (Entry<String, Object> en : notMembers.entrySet()) {
					ps.add(("?" + index) + (" not member of o." + en.getKey()));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != betweens && (!betweens.isEmpty())) {
				for (Entry<String, List<Object>> en : betweens.entrySet()) {
					ps.add("(o." + en.getKey() + (" between ?" + index) + " AND ?" + (index + 1) + ")");
					vs.add(en.getValue().get(0));
					vs.add(en.getValue().get(1));
					index++;
					index++;
				}
			}
			// 지정 방식으로 조건절 결합
			if (!ps.isEmpty()) {
				str += " where " + StringUtils.join(ps, (andJoin ? " and " : " or "));
			}
			// 정렬
			str += " order by o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? DESC : ASC);
			Query query = em.createQuery(str, cls);
			// 조회에 모든 매개변수 값 설정
			for (int i = 0; i < vs.size(); i++) {
				query.setParameter(i + 1, vs.get(i));
			}

			List<W> wraps = new ArrayList<W>();
			// 조회 건수 제한, 보통 한 페이지 항목 수를 사용하며 최대 list_max를 넘지 않음
			@SuppressWarnings("unchecked")
			List<T> list = query.setMaxResults(Math.max(Math.min(count, list_max), list_min)).getResultList();
			if (!list.isEmpty()) {
				// 조회 시작 번호
				Long rank = this.rank(emc, cls, sequenceField, PropertyUtils.getProperty(list.get(0), sequenceField),
						equals, notEquals, likes, ins, notIns, members, notMembers, andJoin, order);
				Constructor<W> constructor = wcls.getConstructor(new Class[] { cls });
				for (T t : list) {
					W w = (W) constructor.newInstance(new Object[] { t });
					PropertyUtils.setProperty(w, "rank", rank++);
					wraps.add(w);
				}
			}
			ActionResult<List<W>> result = new ActionResult<>();
			result.setData(wraps);
			// 조회 결과 총 항목 수 설정
			result.setCount(this.count(emc, cls, equals, notEquals, likes, ins, notIns, members, notMembers, andJoin));
			return result;
		}
	}

	/**
	 * 조회 데이터의 다음 페이지 객체 정보
	 *
	 * [2015년 12월 24일 홍길동이 주석 추가, 코드 변경 없음]
	 *
	 * @param copier        객체 변환 클래스
	 * @param id            이전 페이지 마지막 항목의 ID
	 * @param count         페이지당 항목 수:pagesize
	 * @param sequenceField 페이징 순서로 쓸 속성명
	 * @param equals        같음 조건 집합
	 * @param notEquals     같지 않음(예외) 조건 집합
	 * @param likes         퍼지 조회 조건 집합
	 * @param ins           IN 조회 조건 집합
	 * @param notIns        NOT IN 조회 조건 집합
	 * @param members       소속
	 * @param notMembers    비소속
	 * @param andJoin       조건 연결 방식
	 * @param order         정렬 방식 ASC|DESC
	 * @return
	 * @throws Exception
	 */
	public <T extends JpaObject, W> ActionResult<List<W>> standardListNext(WrapCopier<T, W> copier, String id,
			Integer count, String sequenceField, EqualsTerms equals, NotEqualsTerms notEquals, LikeTerms likes,
			InTerms ins, NotInTerms notIns, MemberTerms members, NotMemberTerms notMembers, BetweenTerms betweens,
			boolean andJoin, String order) throws Exception {
		Class<T> tClass = (Class<T>) copier.getOrigClass();
		Class<W> wClass = (Class<W>) copier.getDestClass();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
			Object sequence = null;
			if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
				sequence = PropertyUtils.getProperty(emc.find(id, tClass, ExceptionWhen.not_found), sequenceField);
			}
			EntityManager em = emc.get(tClass);
			String str = "SELECT o FROM " + tClass.getCanonicalName() + " o";
			/* 사전 컴파일 SQL 문의 파라미터 순번, 반드시 1부터 시작 */
			Integer index = 1;
			List<String> ps = new ArrayList<>();
			List<Object> vs = new ArrayList<>();
			// sequence 값이 비어 있지 않으면 sequence부터, 비어 있으면 처음부터 시작
			if (null != sequence) {
				ps.add("o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? "<" : ">")
						+ (" ?" + (index)));
				vs.add(sequence);
				index++;
			}
			// 아래에서 각종 조건절을 구성해 List<String> ps에, 해당 값은 List<Object> vs에 넣음
			if (null != equals && (!equals.isEmpty())) {
				for (Entry<String, Object> en : equals.entrySet()) {
					ps.add("o." + en.getKey() + (" = ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notEquals && (!notEquals.isEmpty())) {
				for (Entry<String, Object> en : notEquals.entrySet()) {
					ps.add("(o." + en.getKey() + (" <> ?" + index) + " or o." + en.getKey() + " is null)");
					vs.add(en.getValue());
					index++;
				}
			}
			// like는 특수함, 여러 like는 보통 or로 연결
			if (null != likes && (!likes.isEmpty())) {
				List<String> ors = new ArrayList<>();
				for (Entry<String, Object> en : likes.entrySet()) {
					for (String s : StringUtils.split(en.getValue().toString(), " ")) {
						ors.add("o." + en.getKey() + (" Like ?" + index));
						vs.add("%" + s + "%");
						index++;
					}
				}
				ps.add("(" + StringUtils.join(ors, " or ") + ")");
			}
			if (null != ins && (!ins.isEmpty())) {
				for (Entry<String, Collection<?>> en : ins.entrySet()) {
					ps.add("o." + en.getKey() + (" in ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notIns && (!notIns.isEmpty())) {
				for (Entry<String, Collection<?>> en : notIns.entrySet()) {
					ps.add("o." + en.getKey() + (" not in ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != members && (!members.isEmpty())) {
				for (Entry<String, Object> en : members.entrySet()) {
					ps.add(("?" + index) + (" member of o." + en.getKey()));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notMembers && (!notMembers.isEmpty())) {
				for (Entry<String, Object> en : notMembers.entrySet()) {
					ps.add(("?" + index) + (" not member of o." + en.getKey()));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != betweens && (!betweens.isEmpty())) {
				for (Entry<String, List<Object>> en : betweens.entrySet()) {
					ps.add("(o." + en.getKey() + (" between ?" + index) + " AND ?" + (index + 1) + ")");
					vs.add(en.getValue().get(0));
					vs.add(en.getValue().get(1));
					index++;
					index++;
				}
			}
			// 지정 방식으로 조건절 결합
			if (!ps.isEmpty()) {
				str += " where " + StringUtils.join(ps, (andJoin ? " and " : " or "));
			}
			// 정렬
			str += " order by o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? DESC : ASC);
			Query query = em.createQuery(str, tClass);
			// 조회에 모든 매개변수 값 설정
			for (int i = 0; i < vs.size(); i++) {
				query.setParameter(i + 1, vs.get(i));
			}
			List<W> wraps = new ArrayList<W>();
			// 조회 건수 제한, 보통 한 페이지 항목 수를 사용하며 최대 list_max를 넘지 않음
			@SuppressWarnings("unchecked")
			List<T> list = query.setMaxResults(Math.max(Math.min(count, list_max), list_min)).getResultList();
			if (!list.isEmpty()) {
				// 조회 시작 번호
				Long rank = this.rank(emc, tClass, sequenceField, PropertyUtils.getProperty(list.get(0), sequenceField),
						equals, notEquals, likes, ins, notIns, members, notMembers, andJoin, order);
				// 출력 결과에 번호 부여
				for (T t : list) {
					W w = wClass.newInstance();
					copier.copy(t, w);
					PropertyUtils.setProperty(w, "rank", rank++);
					wraps.add(w);
				}
			}
			ActionResult<List<W>> result = new ActionResult<>();
			result.setData(wraps);
			// 조회 결과 총 항목 수 설정
			result.setCount(
					this.count(emc, tClass, equals, notEquals, likes, ins, notIns, members, notMembers, andJoin));
			return result;
		}
	}

	/**
	 * 조회 데이터의 이전 페이지 객체 정보
	 *
	 * [2015년 12월 24일 홍길동이 주석 추가, 코드 변경 없음]
	 *
	 * @param cls           엔티티 클래스
	 * @param wcls          wrap 클래스
	 * @param id            이전 페이지 마지막 항목의 ID
	 * @param count         페이지당 항목 수:pagesize
	 * @param sequenceField 페이징 순서로 쓸 속성명
	 * @param equals        같음 조건 집합
	 * @param notEquals     같지 않음(예외) 조건 집합
	 * @param likes         퍼지 조회 조건 집합
	 * @param ins           IN 조회 조건 집합
	 * @param notIns        NOT IN 조회 조건 집합
	 * @param members       소속
	 * @param notMembers    비소속
	 * @param andJoin       조건 연결 방식
	 * @param order         정렬 방식 ASC|DESC
	 * @return
	 * @throws Exception
	 */
	public <T extends JpaObject, W extends GsonPropertyObject> ActionResult<List<W>> standardListPrev(Class<T> cls,
			Class<W> wcls, String id, Integer count, String sequenceField, EqualsTerms equals, NotEqualsTerms notEquals,
			LikeTerms likes, InTerms ins, NotInTerms notIns, MemberTerms members, NotMemberTerms notMembers,
			BetweenTerms betweens, boolean andJoin, String order) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
			Object sequence = null;
			if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
				sequence = PropertyUtils.getProperty(emc.find(id, cls, ExceptionWhen.not_found), sequenceField);
			}
			EntityManager em = emc.get(cls);
			String str = "SELECT o FROM " + cls.getCanonicalName() + " o";
			/* 사전 컴파일 SQL 문의 파라미터 순번, 반드시 1부터 시작 */
			Integer index = 1;
			List<String> ps = new ArrayList<>();
			List<Object> vs = new ArrayList<>();
			// sequence 값이 비어 있지 않으면 sequence부터, 비어 있으면 처음부터 시작
			if (null != sequence) {
				ps.add("o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? ">" : "<")
						+ (" ?" + (index)));
				vs.add(sequence);
				index++;
			}
			// 아래에서 각종 조건절을 구성해 List<String> ps에, 해당 값은 List<Object> vs에 넣음
			if (null != equals && (!equals.isEmpty())) {
				for (Entry<String, Object> en : equals.entrySet()) {
					ps.add("o." + en.getKey() + (" = ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notEquals && (!notEquals.isEmpty())) {
				for (Entry<String, Object> en : notEquals.entrySet()) {
					ps.add("(o." + en.getKey() + (" <> ?" + index) + " or o." + en.getKey() + " is null)");
					vs.add(en.getValue());
					index++;
				}
			}
			// like는 특수함, 여러 like는 보통 or로 연결
			if (null != likes && (!likes.isEmpty())) {
				List<String> ors = new ArrayList<>();
				for (Entry<String, Object> en : likes.entrySet()) {
					for (String s : StringUtils.split(en.getValue().toString(), " ")) {
						ors.add("o." + en.getKey() + (" Like ?" + index));
						vs.add("%" + s + "%");
						index++;
					}
				}
				ps.add("(" + StringUtils.join(ors, " or ") + ")");
			}
			if (null != ins && (!ins.isEmpty())) {
				for (Entry<String, Collection<?>> en : ins.entrySet()) {
					ps.add("o." + en.getKey() + (" in ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notIns && (!notIns.isEmpty())) {
				for (Entry<String, Collection<?>> en : notIns.entrySet()) {
					ps.add("o." + en.getKey() + (" not in ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != members && (!members.isEmpty())) {
				for (Entry<String, Object> en : members.entrySet()) {
					ps.add(("?" + index) + (" member of o." + en.getKey()));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notMembers && (!notMembers.isEmpty())) {
				for (Entry<String, Object> en : notMembers.entrySet()) {
					ps.add(("?" + index) + (" not member of o." + en.getKey()));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != betweens && (!betweens.isEmpty())) {
				for (Entry<String, List<Object>> en : betweens.entrySet()) {
					ps.add("(o." + en.getKey() + (" between ?" + index) + " AND ?" + (index + 1) + ")");
					vs.add(en.getValue().get(0));
					vs.add(en.getValue().get(1));
					index++;
					index++;
				}
			}
			// 지정 방식으로 조건절 결합
			if (!ps.isEmpty()) {
				str += " where " + StringUtils.join(ps, (andJoin ? " and " : " or "));
			}
			// 정렬
			str += " order by o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? ASC : DESC);
			Query query = em.createQuery(str, cls);
			// 조회에 모든 매개변수 값 설정
			for (int i = 0; i < vs.size(); i++) {
				query.setParameter(i + 1, vs.get(i));
			}
			List<W> wraps = new ArrayList<W>();
			// 조회 건수 제한, 보통 한 페이지 항목 수를 사용하며 최대 list_max를 넘지 않음
			@SuppressWarnings("unchecked")
			List<T> list = query.setMaxResults(Math.max(Math.min(count, list_max), list_min)).getResultList();
			if (!list.isEmpty()) {
				// 조회 시작 번호
				Long rank = this.rank(emc, cls, sequenceField,
						PropertyUtils.getProperty(list.get(list.size() - 1), sequenceField), equals, notEquals, likes,
						ins, notIns, members, notMembers, andJoin, order);
				Constructor<W> constructor = wcls.getConstructor(new Class[] { cls });
				// 출력 결과에 번호 부여
				for (int i = list.size() - 1; i >= 0; i--) {
					W w = (W) constructor.newInstance(new Object[] { list.get(i) });
					PropertyUtils.setProperty(w, "rank", rank++);
					wraps.add(w);
				}
			}
			ActionResult<List<W>> result = new ActionResult<>();
			result.setData(wraps);
			// 조회 결과 총 항목 수 설정
			result.setCount(this.count(emc, cls, equals, notEquals, likes, ins, notIns, members, notMembers, andJoin));
			return result;
		}
	}

	/**
	 * 조회 데이터의 이전 페이지 객체 정보
	 *
	 * [2015년 12월 24일 홍길동이 주석 추가, 코드 변경 없음]
	 *
	 * @param id            이전 페이지 마지막 항목의 ID
	 * @param count         페이지당 항목 수:pagesize
	 * @param sequenceField 페이징 순서로 쓸 속성명
	 * @param equals        같음 조건 집합
	 * @param notEquals     같지 않음(예외) 조건 집합
	 * @param likes         퍼지 조회 조건 집합
	 * @param ins           IN 조회 조건 집합
	 * @param notIns        NOT IN 조회 조건 집합
	 * @param members       소속
	 * @param notMembers    비소속
	 * @param andJoin       조건 연결 방식
	 * @param order         정렬 방식 ASC|DESC
	 * @return
	 * @throws Exception
	 */
	public <T extends JpaObject, W> ActionResult<List<W>> standardListPrev(WrapCopier<T, W> copier, String id,
			Integer count, String sequenceField, EqualsTerms equals, NotEqualsTerms notEquals, LikeTerms likes,
			InTerms ins, NotInTerms notIns, MemberTerms members, NotMemberTerms notMembers, BetweenTerms betweens,
			boolean andJoin, String order) throws Exception {
		Class<T> tClass = (Class<T>) copier.getOrigClass();
		Class<W> wClass = (Class<W>) copier.getDestClass();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
			Object sequence = null;
			if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
				sequence = PropertyUtils.getProperty(emc.find(id, tClass, ExceptionWhen.not_found), sequenceField);
			}
			EntityManager em = emc.get(tClass);
			String str = "SELECT o FROM " + tClass.getCanonicalName() + " o";
			/* 사전 컴파일 SQL 문의 파라미터 순번, 반드시 1부터 시작 */
			Integer index = 1;
			List<String> ps = new ArrayList<>();
			List<Object> vs = new ArrayList<>();
			// sequence 값이 비어 있지 않으면 sequence부터, 비어 있으면 처음부터 시작
			if (null != sequence) {
				ps.add("o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? ">" : "<")
						+ (" ?" + (index)));
				vs.add(sequence);
				index++;
			}
			// 아래에서 각종 조건절을 구성해 List<String> ps에, 해당 값은 List<Object> vs에 넣음
			if (null != equals && (!equals.isEmpty())) {
				for (Entry<String, Object> en : equals.entrySet()) {
					ps.add("o." + en.getKey() + (" = ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notEquals && (!notEquals.isEmpty())) {
				for (Entry<String, Object> en : notEquals.entrySet()) {
					ps.add("(o." + en.getKey() + (" <> ?" + index) + " or o." + en.getKey() + " is null)");
					vs.add(en.getValue());
					index++;
				}
			}
			// like는 특수함, 여러 like는 보통 or로 연결
			if (null != likes && (!likes.isEmpty())) {
				List<String> ors = new ArrayList<>();
				for (Entry<String, Object> en : likes.entrySet()) {
					for (String s : StringUtils.split(en.getValue().toString(), " ")) {
						ors.add("o." + en.getKey() + (" Like ?" + index));
						vs.add("%" + s + "%");
						index++;
					}
				}
				ps.add("(" + StringUtils.join(ors, " or ") + ")");
			}
			if (null != ins && (!ins.isEmpty())) {
				for (Entry<String, Collection<?>> en : ins.entrySet()) {
					ps.add("o." + en.getKey() + (" in ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notIns && (!notIns.isEmpty())) {
				for (Entry<String, Collection<?>> en : notIns.entrySet()) {
					ps.add("o." + en.getKey() + (" not in ?" + index));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != members && (!members.isEmpty())) {
				for (Entry<String, Object> en : members.entrySet()) {
					ps.add(("?" + index) + (" member of o." + en.getKey()));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != notMembers && (!notMembers.isEmpty())) {
				for (Entry<String, Object> en : notMembers.entrySet()) {
					ps.add(("?" + index) + (" not member of o." + en.getKey()));
					vs.add(en.getValue());
					index++;
				}
			}
			if (null != betweens && (!betweens.isEmpty())) {
				for (Entry<String, List<Object>> en : betweens.entrySet()) {
					ps.add("(o." + en.getKey() + (" between ?" + index) + " AND ?" + (index + 1) + ")");
					vs.add(en.getValue().get(0));
					vs.add(en.getValue().get(1));
					index++;
					index++;
				}
			}
			// 지정 방식으로 조건절 결합
			if (!ps.isEmpty()) {
				str += " where " + StringUtils.join(ps, (andJoin ? " and " : " or "));
			}
			// 정렬
			str += " order by o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? ASC : DESC);
			Query query = em.createQuery(str, tClass);
			// 조회에 모든 매개변수 값 설정
			for (int i = 0; i < vs.size(); i++) {
				query.setParameter(i + 1, vs.get(i));
			}
			List<W> wraps = new ArrayList<W>();
			// 조회 건수 제한, 보통 한 페이지 항목 수를 사용하며 최대 list_max를 넘지 않음
			@SuppressWarnings("unchecked")
			List<T> list = query.setMaxResults(Math.max(Math.min(count, list_max), list_min)).getResultList();
			if (!list.isEmpty()) {
				// 조회 시작 번호
				Long rank = this.rank(emc, tClass, sequenceField,
						PropertyUtils.getProperty(list.get(list.size() - 1), sequenceField), equals, notEquals, likes,
						ins, notIns, members, notMembers, andJoin, order);
				// 출력 결과에 번호 부여
				for (int i = list.size() - 1; i >= 0; i--) {
					W w = wClass.newInstance();
					copier.copy(list.get(i), w);
					PropertyUtils.setProperty(w, "rank", rank++);
					wraps.add(w);
				}
			}
			ActionResult<List<W>> result = new ActionResult<>();
			result.setData(wraps);
			// 조회 결과 총 항목 수 설정
			result.setCount(
					this.count(emc, tClass, equals, notEquals, likes, ins, notIns, members, notMembers, andJoin));
			return result;
		}
	}

	private <T extends JpaObject> Long rank(EntityManagerContainer emc, Class<T> cls, String sequenceField,
			Object sequence, ListOrderedMap<String, Object> equals, ListOrderedMap<String, Object> notEquals,
			ListOrderedMap<String, Object> likes, ListOrderedMap<String, Collection<?>> ins,
			ListOrderedMap<String, Collection<?>> notIns, ListOrderedMap<String, Object> members,
			ListOrderedMap<String, Object> notMembers, boolean andJoin, String order) throws Exception {
		EntityManager em = emc.get(cls);
		String str = "SELECT count(o) FROM " + cls.getCanonicalName() + " o";
		/* 사전 컴파일 SQL 문의 파라미터 순번, 반드시 1부터 시작 */
		Integer index = 1;
		List<String> ps = new ArrayList<>();
		List<Object> vs = new ArrayList<>();
		if (null != sequence) {
			ps.add("o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? ">" : "<")
					+ (" ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if (null != equals && (!equals.isEmpty())) {
			for (Entry<String, Object> en : equals.entrySet()) {
				ps.add("o." + en.getKey() + (" = ?" + index));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != notEquals && (!notEquals.isEmpty())) {
			for (Entry<String, Object> en : notEquals.entrySet()) {
				ps.add("(o." + en.getKey() + (" <> ?" + index) + " or o." + en.getKey() + " is null)");
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != likes && (!likes.isEmpty())) {
			List<String> ors = new ArrayList<>();
			for (Entry<String, Object> en : likes.entrySet()) {
				ors.add("o." + en.getKey() + (" Like ?" + index));
				vs.add("%" + en.getValue() + "%");
				index++;
			}
			ps.add("(" + StringUtils.join(ors, " or ") + ")");
		}
		if (null != ins && (!ins.isEmpty())) {
			for (Entry<String, Collection<?>> en : ins.entrySet()) {
				ps.add("o." + en.getKey() + (" in ?" + index));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != notIns && (!notIns.isEmpty())) {
			for (Entry<String, Collection<?>> en : notIns.entrySet()) {
				ps.add("o." + en.getKey() + (" not in ?" + index));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != members && (!members.isEmpty())) {
			for (Entry<String, Object> en : members.entrySet()) {
				ps.add(("?" + index) + (" member of o." + en.getKey()));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != notMembers && (!notMembers.isEmpty())) {
			for (Entry<String, Object> en : notMembers.entrySet()) {
				ps.add(("?" + index) + (" not member of o." + en.getKey()));
				vs.add(en.getValue());
				index++;
			}
		}
		if (!ps.isEmpty()) {
			str += " where " + StringUtils.join(ps, (andJoin ? " and " : " or "));
		}
		Query query = em.createQuery(str, cls);
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		return (Long) query.getSingleResult() + 1;
	}

	private <T extends JpaObject> Long count(EntityManagerContainer emc, Class<T> cls, EqualsTerms equals,
			NotEqualsTerms notEquals, LikeTerms likes, InTerms ins, NotInTerms notIns, MemberTerms members,
			NotMemberTerms notMembers, boolean andJoin) throws Exception {
		EntityManager em = emc.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(cls);
		List<Predicate> ps = new ArrayList<>();
		if (null != equals && (!equals.isEmpty())) {
			for (Entry<String, Object> en : equals.entrySet()) {
				ps.add(cb.equal(root.get(en.getKey()), en.getValue()));
			}
		}
		if (null != notEquals && (!notEquals.isEmpty())) {
			for (Entry<String, Object> en : notEquals.entrySet()) {
				ps.add(cb.or(cb.isNull(root.get(en.getKey())), cb.notEqual(root.get(en.getKey()), en.getValue())));
			}
		}
		if (null != likes && (!likes.isEmpty())) {
			List<Predicate> ors = new ArrayList<>();
			for (Entry<String, Object> en : likes.entrySet()) {
				ors.add(cb.like(root.get(en.getKey()), "%" + en.getValue() + "%"));
			}
			ps.add(cb.or(CollectionTools.toArray(ors, Predicate.class)));
		}
		if (null != ins && (!ins.isEmpty())) {
			for (Entry<String, Collection<?>> en : ins.entrySet()) {
				ps.add(root.get(en.getKey()).in(en.getValue()));
			}
		}
		if (null != notIns && (!notIns.isEmpty())) {
			for (Entry<String, Collection<?>> en : notIns.entrySet()) {
				ps.add(cb.not(root.get(en.getKey()).in(en.getValue())));
			}
		}
		if (null != members && (!members.isEmpty())) {
			for (Entry<String, Object> en : members.entrySet()) {
				ps.add(cb.isMember(en.getValue(), root.get(en.getKey())));
			}
		}
		if (null != notMembers && (!notMembers.isEmpty())) {
			for (Entry<String, Object> en : notMembers.entrySet()) {
				ps.add(cb.isNotMember(en.getValue(), root.get(en.getKey())));
			}
		}
		Predicate p = null;
		if (BooleanUtils.isTrue(andJoin)) {
			p = cb.and(CollectionTools.toArray(ps, Predicate.class));
		} else {
			p = cb.or(CollectionTools.toArray(ps, Predicate.class));
		}
		return em.createQuery(cq.select(cb.countDistinct(root)).where(p)).getSingleResult();
	}

	/**
	 * 조회 데이터의 다음 페이지 객체 정보
	 *
	 * [2015년 12월 24일 홍길동이 주석 추가, 코드 변경 없음]
	 *
	 * @param copier        객체 변환 클래스
	 * @param id            이전 페이지 마지막 항목의 ID
	 * @param count         페이지당 항목 수:pagesize
	 * @param sequenceField 페이징 순서로 쓸 속성명
	 * @param equals        같음 조건 집합
	 * @param notEquals     같지 않음(예외) 조건 집합
	 * @param likes         퍼지 조회 조건 집합
	 * @param ins           IN 조회 조건 집합
	 * @param notIns        NOT IN 조회 조건 집합
	 * @param members       소속
	 * @param notMembers    비소속
	 * @param andJoin       조건 연결 방식
	 * @param order         정렬 방식 ASC|DESC
	 * @return
	 * @throws Exception
	 */
	public <T extends JpaObject, W extends GsonPropertyObject> ActionResult<List<W>> standardFetchAttributeListNext(
			EntityManagerContainer emc, WrapCopier<T, W> copier, String id, Integer count, String sequenceField,
			EqualsTerms equals, NotEqualsTerms notEquals, LikeTerms likes, InTerms ins, NotInTerms notIns,
			MemberTerms members, NotMemberTerms notMembers, boolean andJoin, String order) throws Exception {
		Class<T> tClass = (Class<T>) copier.getOrigClass();
		/** 이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회 */
		Object sequence = null;
		if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
			Object o = emc.find(id, tClass);
			if (null != o) {
				sequence = PropertyUtils.getProperty(o, sequenceField);
			}
		}
		EntityManager em = emc.get(tClass);
		String str = "SELECT o.id FROM " + tClass.getCanonicalName() + " o";
		/** 사전 컴파일 SQL 문의 파라미터 순번, 반드시 1부터 시작 */
		Integer index = 1;
		List<String> ps = new ArrayList<>();
		List<Object> vs = new ArrayList<>();
		/** sequence 값이 비어 있지 않으면 sequence부터, 비어 있으면 처음부터 시작 */
		if (null != sequence) {
			ps.add("o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? "<" : ">")
					+ (" ?" + (index)));
			vs.add(sequence);
			index++;
		}
		/** 아래에서 각종 조건절을 구성해 List<String> ps에, 해당 값은 List<Object> vs에 넣음 */
		if (null != equals && (!equals.isEmpty())) {
			for (Entry<String, Object> en : equals.entrySet()) {
				ps.add("o." + en.getKey() + (" = ?" + index));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != notEquals && (!notEquals.isEmpty())) {
			for (Entry<String, Object> en : notEquals.entrySet()) {
				ps.add("(o." + en.getKey() + (" <> ?" + index) + " or o." + en.getKey() + " is null)");
				vs.add(en.getValue());
				index++;
			}
		}
		/** like는 특수함, 여러 like는 보통 or로 연결 */
		if (null != likes && (!likes.isEmpty())) {
			List<String> ors = new ArrayList<>();
			for (Entry<String, Object> en : likes.entrySet()) {
				for (String s : StringUtils.split(en.getValue().toString(), " ")) {
					ors.add("o." + en.getKey() + (" Like ?" + index));
					vs.add("%" + s + "%");
					index++;
				}
			}
			ps.add("(" + StringUtils.join(ors, " or ") + ")");
		}
		if (null != ins && (!ins.isEmpty())) {
			for (Entry<String, Collection<?>> en : ins.entrySet()) {
				ps.add("o." + en.getKey() + (" in ?" + index));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != notIns && (!notIns.isEmpty())) {
			for (Entry<String, Collection<?>> en : notIns.entrySet()) {
				ps.add("o." + en.getKey() + (" not in ?" + index));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != members && (!members.isEmpty())) {
			for (Entry<String, Object> en : members.entrySet()) {
				ps.add(("?" + index) + (" member of o." + en.getKey()));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != notMembers && (!notMembers.isEmpty())) {
			for (Entry<String, Object> en : notMembers.entrySet()) {
				ps.add(("?" + index) + (" not member of o." + en.getKey()));
				vs.add(en.getValue());
				index++;
			}
		}
		/** 지정 방식으로 조건절 결합 */
		if (!ps.isEmpty()) {
			str += " where " + StringUtils.join(ps, (andJoin ? " and " : " or "));
		}
		/** 정렬 */
		str += " order by o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? DESC : ASC);
		Query query = em.createQuery(str, tClass);
		/** 조회에 모든 매개변수 값 설정 */
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		/** 조회 건수 제한, 보통 한 페이지 항목 수를 사용하며 최대 list_max를 넘지 않음 */
		@SuppressWarnings("unchecked")
		List<String> ids = query.setMaxResults(Math.max(Math.min(count, list_max), list_min)).getResultList();
		/** 조건에 맞는 모든 id 조회 */
		List<W> wos = emc.fetch(ids, copier);
		if (!wos.isEmpty()) {
			/** 조회 시작 번호 */
			Long rank = this.rank(emc, tClass, sequenceField, PropertyUtils.getProperty(wos.get(0), sequenceField),
					equals, notEquals, likes, ins, notIns, members, notMembers, andJoin, order);
			/** 출력 결과에 번호 부여 */
			for (W w : wos) {
				PropertyUtils.setProperty(w, "rank", rank++);
			}
		}
		ActionResult<List<W>> result = new ActionResult<>();
		result.setData(wos);
		/** 조회 결과 총 항목 수 설정 */
		result.setCount(this.count(emc, tClass, equals, notEquals, likes, ins, notIns, members, notMembers, andJoin));
		return result;
	}

	public <T extends JpaObject, W extends GsonPropertyObject> ActionResult<List<W>> standardFetchAttributeListPrev(
			EntityManagerContainer emc, WrapCopier<T, W> copier, String id, Integer count, String sequenceField,
			EqualsTerms equals, NotEqualsTerms notEquals, LikeTerms likes, InTerms ins, NotInTerms notIns,
			MemberTerms members, NotMemberTerms notMembers, boolean andJoin, String order) throws Exception {
		Class<T> tClass = (Class<T>) copier.getOrigClass();
		/** 이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회 */
		Object sequence = null;
		if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
			Object o = emc.find(id, tClass);
			if (null != o) {
				sequence = PropertyUtils.getProperty(o, sequenceField);
			}
		}
		EntityManager em = emc.get(tClass);
		String str = "SELECT o.id FROM " + tClass.getCanonicalName() + " o";
		/** 사전 컴파일 SQL 문의 파라미터 순번, 반드시 1부터 시작 */
		Integer index = 1;
		List<String> ps = new ArrayList<>();
		List<Object> vs = new ArrayList<>();
		/** sequence 값이 비어 있지 않으면 sequence부터, 비어 있으면 처음부터 시작 */
		if (null != sequence) {
			ps.add("o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? ">" : "<")
					+ (" ?" + (index)));
			vs.add(sequence);
			index++;
		}
		/** 아래에서 각종 조건절을 구성해 List<String> ps에, 해당 값은 List<Object> vs에 넣음 */
		if (null != equals && (!equals.isEmpty())) {
			for (Entry<String, Object> en : equals.entrySet()) {
				ps.add("o." + en.getKey() + (" = ?" + index));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != notEquals && (!notEquals.isEmpty())) {
			for (Entry<String, Object> en : notEquals.entrySet()) {
				ps.add("(o." + en.getKey() + (" <> ?" + index) + " or o." + en.getKey() + " is null)");
				vs.add(en.getValue());
				index++;
			}
		}
		/** like는 특수함, 여러 like는 보통 or로 연결 */
		if (null != likes && (!likes.isEmpty())) {
			List<String> ors = new ArrayList<>();
			for (Entry<String, Object> en : likes.entrySet()) {
				for (String s : StringUtils.split(en.getValue().toString(), " ")) {
					ors.add("o." + en.getKey() + (" Like ?" + index));
					vs.add("%" + s + "%");
					index++;
				}
			}
			ps.add("(" + StringUtils.join(ors, " or ") + ")");
		}
		if (null != ins && (!ins.isEmpty())) {
			for (Entry<String, Collection<?>> en : ins.entrySet()) {
				ps.add("o." + en.getKey() + (" in ?" + index));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != notIns && (!notIns.isEmpty())) {
			for (Entry<String, Collection<?>> en : notIns.entrySet()) {
				ps.add("o." + en.getKey() + (" not in ?" + index));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != members && (!members.isEmpty())) {
			for (Entry<String, Object> en : members.entrySet()) {
				ps.add(("?" + index) + (" member of o." + en.getKey()));
				vs.add(en.getValue());
				index++;
			}
		}
		if (null != notMembers && (!notMembers.isEmpty())) {
			for (Entry<String, Object> en : notMembers.entrySet()) {
				ps.add(("?" + index) + (" not member of o." + en.getKey()));
				vs.add(en.getValue());
				index++;
			}
		}
		/** 지정 방식으로 조건절 결합 */
		if (!ps.isEmpty()) {
			str += " where " + StringUtils.join(ps, (andJoin ? " and " : " or "));
		}
		/** 정렬 */
		str += " order by o." + sequenceField + " " + (StringUtils.equalsIgnoreCase(order, DESC) ? ASC : DESC);
		Query query = em.createQuery(str, tClass);
		/** 조회에 모든 매개변수 값 설정 */
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		/** 조회 건수 제한, 보통 한 페이지 항목 수를 사용하며 최대 list_max를 넘지 않음 */
		@SuppressWarnings("unchecked")
		List<String> ids = query.setMaxResults(Math.max(Math.min(count, list_max), list_min)).getResultList();
		List<W> wos = emc.fetch(ids, copier);
		if (!wos.isEmpty()) {
			/** 조회 시작 번호 */
			Long rank = this.rank(emc, tClass, sequenceField,
					PropertyUtils.getProperty(wos.get(wos.size() - 1), sequenceField), equals, notEquals, likes, ins,
					notIns, members, notMembers, andJoin, order);
			/** 출력 결과에 번호 부여 */
			for (int i = wos.size() - 1; i >= 0; i--) {
				PropertyUtils.setProperty(wos.get(i), "rank", rank++);
			}
		}
		ActionResult<List<W>> result = new ActionResult<>();
		result.setData(wos);
		/** 조회 결과 총 항목 수 설정 */
		result.setCount(this.count(emc, tClass, equals, notEquals, likes, ins, notIns, members, notMembers, andJoin));
		return result;
	}

	public Integer adjustPage(Integer page) {
		return (page == null || page < 1) ? 1 : page;
	}

	public Integer adjustSize(Integer pageSize) {
		if(pageSize == null || pageSize < 1){
			return EntityManagerContainer.DEFAULT_PAGESIZE;
		}
		return (pageSize > EntityManagerContainer.MAX_PAGESIZE) ? EntityManagerContainer.MAX_PAGESIZE : pageSize;
	}

	public <T extends JpaObject, W> ActionResult<List<W>> standardListNext(WrapCopier<T, W> copier, String id,
			Integer count, String sequenceField, String order, Predicate predicate) throws Exception {
		Class<T> tClass = (Class<T>) copier.getOrigClass();
		Class<W> wClass = (Class<W>) copier.getDestClass();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
			Object sequence = null;
			if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
				T t = emc.fetch(id, tClass, ListTools.toList(sequenceField));
				if (null != t) {
					sequence = PropertyUtils.getProperty(t, sequenceField);
				}
			}
			EntityManager em = emc.get(tClass);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<T> root = cq.from(tClass);

			if (null == predicate) {
				predicate = cb.conjunction();
			}

			if (StringUtils.equalsIgnoreCase(order, DESC)) {
				cq.where(null == sequence ? predicate
						: cb.and(predicate, cb.lessThan(root.get(sequenceField), (Comparable) sequence)));
				cq.orderBy(cb.desc(root.get(sequenceField)));
			} else {
				cq.where(null == sequence ? predicate
						: cb.and(predicate, cb.greaterThan(root.get(sequenceField), (Comparable) sequence)));
				cq.orderBy(cb.asc(root.get(sequenceField)));
			}

			List<Selection<?>> selections = new ArrayList<>();

			List<String> fields = copier.getCopyFields();

			for (String field : fields) {
				selections.add(root.get(field));
			}

			List<Tuple> os = em.createQuery(cq.multiselect(selections))
					.setMaxResults(Math.max(Math.min(count, list_max), list_min)).getResultList();

			List<W> ws = new ArrayList<W>();

			for (Tuple tuple : os) {
				W w = wClass.newInstance();
				for (int i = 0; i < selections.size(); i++) {
					PropertyUtils.setProperty(w, fields.get(i), tuple.get(selections.get(i)));
				}
				ws.add(w);
			}
			ActionResult<List<W>> result = new ActionResult<>();
			result.setData(ws);
			// 조회 결과 총 항목 수 설정
			result.setCount(this.count(emc, tClass, predicate));
			return result;
		}
	}

	public <T extends JpaObject, W> ActionResult<List<W>> standardListPrev(WrapCopier<T, W> copier, String id,
			Integer count, String sequenceField, String order, Predicate predicate) throws Exception {
		Class<T> tClass = (Class<T>) copier.getOrigClass();
		Class<W> wClass = (Class<W>) copier.getDestClass();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
			Object sequence = null;
			if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
				T t = emc.fetch(id, tClass, ListTools.toList(sequenceField));
				if (null != t) {
					sequence = PropertyUtils.getProperty(t, sequenceField);
				}
			}
			EntityManager em = emc.get(tClass);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<T> root = cq.from(tClass);

			if (null == predicate) {
				predicate = cb.conjunction();
			}

			if (StringUtils.equalsIgnoreCase(order, DESC)) {
				cq.where(null == sequence ? predicate
						: cb.and(predicate, cb.greaterThan(root.get(sequenceField), (Comparable) sequence)));
				cq.orderBy(cb.asc(root.get(sequenceField)));
			} else {
				cq.where(null == sequence ? predicate
						: cb.and(predicate, cb.lessThan(root.get(sequenceField), (Comparable) sequence)));
				cq.orderBy(cb.desc(root.get(sequenceField)));
			}

			List<Selection<?>> selections = new ArrayList<>();

			List<String> fields = copier.getCopyFields();

			for (String field : fields) {
				selections.add(root.get(field));
			}

			List<Tuple> os = em.createQuery(cq.multiselect(selections))
					.setMaxResults(Math.max(Math.min(count, list_max), list_min)).getResultList();

			List<W> ws = new ArrayList<W>();

			Tuple tuple = null;

			for (int i = os.size() - 1; i >= 0; i--) {
				tuple = os.get(i);
				W w = wClass.newInstance();
				for (int j = 0; j < selections.size(); j++) {
					PropertyUtils.setProperty(w, fields.get(j), tuple.get(selections.get(j)));
				}
				ws.add(w);
			}

			ActionResult<List<W>> result = new ActionResult<>();
			result.setData(ws);
			// 조회 결과 총 항목 수 설정
			result.setCount(this.count(emc, tClass, predicate));
			return result;
		}
	}

	public <T extends JpaObject> ActionResult<List<T>> standardListNext(Class<T> cls, String id, Integer count,
			String sequenceField, String order, Predicate predicate) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
			Object sequence = null;
			if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
				T t = emc.fetch(id, cls, ListTools.toList(sequenceField));
				if (null != t) {
					sequence = PropertyUtils.getProperty(t, sequenceField);
				}
			}

			EntityManager em = emc.get(cls);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(cls);
			Root<T> root = cq.from(cls);

			if (null == predicate) {
				predicate = cb.conjunction();
			}

			if (StringUtils.equalsIgnoreCase(order, DESC)) {
				cq.where(null == sequence ? predicate
						: cb.and(predicate, cb.lessThan(root.get(sequenceField), (Comparable) sequence)));
				cq.orderBy(cb.desc(root.get(sequenceField)));
			} else {
				cq.where(null == sequence ? predicate
						: cb.and(predicate, cb.greaterThan(root.get(sequenceField), (Comparable) sequence)));
				cq.orderBy(cb.asc(root.get(sequenceField)));
			}

			List<T> os = em.createQuery(cq.select(root)).setMaxResults(Math.max(Math.min(count, list_max), list_min))
					.getResultList();

			ActionResult<List<T>> result = new ActionResult<>();
			result.setData(new ArrayList<T>(os));
			// 조회 결과 총 항목 수 설정
			result.setCount(this.count(emc, cls, predicate));
			return result;
		}
	}

	public <T extends JpaObject> ActionResult<List<T>> standardListPrev(Class<T> cls, String id, Integer count,
			String sequenceField, String order, Predicate predicate) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
			Object sequence = null;
			if (!StringUtils.equalsIgnoreCase(id, EMPTY_SYMBOL)) {
				T t = emc.fetch(id, cls, ListTools.toList(sequenceField));
				if (null != t) {
					sequence = PropertyUtils.getProperty(t, sequenceField);
				}
			}
			EntityManager em = emc.get(cls);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(cls);
			Root<T> root = cq.from(cls);

			if (null == predicate) {
				predicate = cb.conjunction();
			}

			if (StringUtils.equalsIgnoreCase(order, DESC)) {
				cq.where(null == sequence ? predicate
						: cb.and(predicate, cb.greaterThan(root.get(sequenceField), (Comparable) sequence)));
				cq.orderBy(cb.asc(root.get(sequenceField)));
			} else {
				cq.where(null == sequence ? predicate
						: cb.and(predicate, cb.lessThan(root.get(sequenceField), (Comparable) sequence)));
				cq.orderBy(cb.desc(root.get(sequenceField)));
			}

			List<T> os = em.createQuery(cq.select(root)).setMaxResults(Math.max(Math.min(count, list_max), list_min))
					.getResultList();

			List<T> wos = new ArrayList<>();

			for (int i = os.size() - 1; i >= 0; i--) {
				wos.add(os.get(i));
			}

			ActionResult<List<T>> result = new ActionResult<>();
			result.setData(wos);
			// 조회 결과 총 항목 수 설정
			result.setCount(this.count(emc, cls, predicate));
			return result;
		}
	}

	private <T extends JpaObject> Long count(EntityManagerContainer emc, Class<T> cls, Predicate predicate)
			throws Exception {
		EntityManager em = emc.get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(cls);
		return em.createQuery(cq.select(cb.countDistinct(root)).where(predicate)).getSingleResult();
	}

	/**
	 * request 파라미터 값을 json으로 변환
	 */
	public String request2Json(HttpServletRequest request) {
		Map<String, String> map = new HashMap<>();
		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			String[] pv = request.getParameterValues(paramName);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < pv.length; i++) {
				if (pv[i].length() > 0) {
					if (i > 0) {
						sb.append(",");
					}
					sb.append(pv[i]);
				}
			}
			map.put(paramName, sb.toString());
		}
		return gson.toJson(map);
	}

}
