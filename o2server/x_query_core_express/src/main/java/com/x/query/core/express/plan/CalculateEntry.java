package com.x.query.core.express.plan;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.DoubleSupplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class CalculateEntry extends GsonPropertyObject {

	@FieldDescribe("뷰")
	public String view;

	@FieldDescribe("통계 열")
	public String column;

	@FieldDescribe("통계 유형, sum, count, average")
	public String calculateType;

	@FieldDescribe("표시 이름")
	public String displayName;

	@FieldDescribe("정렬, original, asc, desc")
	public String orderType;

	@FieldDescribe("형식 유형: number, percent, currency")
	public String formatType;

	@FieldDescribe("빈 값의 기본값")
	public Double defaultValue;

	@FieldDescribe("소수점 자릿수")
	public Integer decimal;

	@FieldDescribe("식별용 id, 데이터베이스 id가 아님")
	public String id;

	public static final String FORMATTYPE_NUMBER = "number";
	public static final String FORMATTYPE_PERCENT = "percent";
	public static final String FORMATTYPE_CURRENCY = "currency";

	public Boolean available() {
		if (StringUtils.isEmpty(this.column)) {
			return false;
		}
		if (null == this.orderType) {
			return false;
		}
		return true;
	}

	protected Long count(Table table) throws Exception {
		Long result = table.stream().count();
		return result;
	}

	protected Double sum(Table table) throws Exception {
		Double result = table.stream().mapToDouble(row -> row.getAsDouble(this.column)).sum();
		return result;
	}

	protected Double average(Table table) throws Exception {
		DoubleSupplier ds = () -> {
			return 0d;
		};
		Double result = table.stream().mapToDouble(row -> row.getAsDouble(this.column)).average().orElseGet(ds);
		return result;
	}

	protected Map<Object, Long> groupCount(Table table, SelectEntry groupSelectEntry) throws Exception {
		Map<Object, Long> map = new TreeMap<Object, Long>();
		if ((null != groupSelectEntry) && (groupSelectEntry.available())) {
			map = table.stream()
					.collect(Collectors.groupingBy(row -> row.find(groupSelectEntry.column), Collectors.counting()));
		}
		return map;
	}

	protected Map<Object, Double> groupSum(Table table, SelectEntry groupSelectEntry) throws Exception {
		Map<Object, Double> map = table.stream().collect(Collectors.groupingBy(row -> row.find(groupSelectEntry.column),
				Collectors.summingDouble(row -> row.getAsDouble(this.column))));
		return map;
	}

	protected Map<Object, Double> groupAverage(List<Row> rows, SelectEntry groupSelectEntry) throws Exception {
		Map<Object, Double> map = rows.stream().collect(Collectors.groupingBy(row -> row.find(groupSelectEntry.column),
				Collectors.averagingDouble(row -> row.getAsDouble(this.column))));
		return map;
	}

}