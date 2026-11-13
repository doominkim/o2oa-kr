package com.x.attendance.assemble.control;

import java.lang.reflect.Field;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

public class CriteriaQueryTools {
    public static Order setOrder(CriteriaBuilder cb, Root<?> root, Class<?> clazz_, String fieldName, String orderType) {

        Boolean fieldExists = false;
        Field[] fields = clazz_.getFields();
        for (Field field : fields) {
            if (StringUtils.equalsIgnoreCase(field.getName(), fieldName)) {
                fieldName = field.getName(); // 대소문자 무시 후 조회 필드 이름 재설정
                fieldExists = true;
            }
        }

        if (!fieldExists) {
            return null; // 조회 필드가 object와 전혀 맞지 않으면 null 반환
        }

        if (StringUtils.equalsIgnoreCase(orderType, "asc")) {
            return cb.asc(root.get(fieldName));
        } else {
            return cb.desc(root.get(fieldName));
        }
    }

    public static Predicate predicate_or(CriteriaBuilder criteriaBuilder, Predicate predicate, Predicate predicate_target) {
        if (predicate == null) {
            return predicate_target;
        } else {
            if (predicate_target != null) {
                return criteriaBuilder.or(predicate, predicate_target);
            } else {
                return predicate;
            }
        }
    }

    public static Predicate predicate_and(CriteriaBuilder criteriaBuilder, Predicate predicate, Predicate predicate_target) {
        if (predicate == null) {
            return predicate_target;
        } else {
            if (predicate_target != null) {
                return criteriaBuilder.and(predicate, predicate_target);
            } else {
                return predicate;
            }
        }
    }
}
