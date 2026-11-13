package com.x.base.core.project.gson;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.x.base.core.project.tools.DateTools;

public class DateSerializer implements JsonSerializer<Date> {
	public JsonElement serialize(Date date, Type type, JsonSerializationContext context) {
		if (null == date) {
			return JsonNull.INSTANCE;
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			if ((cal.get(Calendar.YEAR) == 1970) && (cal.get(Calendar.MONTH) == 0) && (cal.get(Calendar.DATE) == 1)) {
				/** 시간 내용만 있고 날짜 형식이 기본값이면 시간만 출력 */
				return new JsonPrimitive(DateTools.format(date, DateTools.format_HHmmss));
			} else if ((cal.get(Calendar.HOUR_OF_DAY) == 0) && (cal.get(Calendar.MINUTE) == 0)
					&& (cal.get(Calendar.SECOND) == 0)) {
				/** 날짜 내용만 있고 시간 내용이 모두 0이면 날짜만 출력 */
				return new JsonPrimitive(DateTools.format(date, DateTools.format_yyyyMMdd));
			} else {
				return new JsonPrimitive(DateTools.format(date));
			}
		}
	}
}