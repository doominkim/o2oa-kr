package com.x.base.core.project.gson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.x.base.core.project.bean.tuple.Sextuple;
import com.x.base.core.project.tools.DateTools;

public class XGsonBuilder {

	private static Gson INSTANCE;
	private static Gson COMPACTINSTANCE;
	public static final String PATH_DOT = ".";

	public static Gson instance() {
		if (null == INSTANCE) {
			synchronized (XGsonBuilder.class) {
				if (null == INSTANCE) {
					GsonBuilder gson = new GsonBuilder();
					gson.setDateFormat(DateTools.format_yyyyMMddHHmmss);
					gson.registerTypeAdapter(Integer.class, new IntegerDeserializer());
					gson.registerTypeAdapter(Double.class, new DoubleDeserializer());
					gson.registerTypeAdapter(Float.class, new FloatDeserializer());
					gson.registerTypeAdapter(Long.class, new LongDeserializer());
					gson.registerTypeAdapter(Date.class, new DateDeserializer());
					gson.registerTypeAdapter(Date.class, new DateSerializer());
					gson.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
					INSTANCE = gson.setPrettyPrinting().serializeSpecialFloatingPointValues().create();
				}
			}
		}
		return INSTANCE;
	}

	public static Gson compactInstance() {
		if (null == COMPACTINSTANCE) {
			synchronized (XGsonBuilder.class) {
				if (null == COMPACTINSTANCE) {
					GsonBuilder gson = new GsonBuilder();
					gson.setDateFormat(DateTools.format_yyyyMMddHHmmss);
					gson.registerTypeAdapter(Integer.class, new IntegerDeserializer());
					gson.registerTypeAdapter(Double.class, new DoubleDeserializer());
					gson.registerTypeAdapter(Float.class, new FloatDeserializer());
					gson.registerTypeAdapter(Long.class, new LongDeserializer());
					gson.registerTypeAdapter(Date.class, new DateDeserializer());
					gson.registerTypeAdapter(Date.class, new DateSerializer());
					gson.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
					COMPACTINSTANCE = gson.create();
				}
			}
		}
		return COMPACTINSTANCE;
	}

	public static <T> T convert(Object o, Class<T> cls) {
		if (null == o) {
			return null;
		}
		return instance().fromJson(instance().toJson(o), cls);
	}

	public static String toJson(Object o) {
		return instance().toJson(o);
	}

	public static String toText(Object o) {
		return instance().toJsonTree(o).toString();
	}

	public static String extractString(JsonElement jsonElement, String name) {
		if ((null != jsonElement) && jsonElement.isJsonObject() && StringUtils.isNotEmpty(name)) {
			JsonElement element = extract(jsonElement, name);
			if (null != element && element.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
				if (jsonPrimitive.isString())
					return jsonPrimitive.getAsString();
			}
		}
		return null;
	}

	public static Boolean extractBoolean(JsonElement jsonElement, String name) {
		if ((null != jsonElement) && jsonElement.isJsonObject() && StringUtils.isNotEmpty(name)) {
			JsonElement element = extract(jsonElement, name);
			if (null != element && element.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
				if (jsonPrimitive.isBoolean())
					return jsonPrimitive.getAsBoolean();
			}
		}
		return null;
	}

	public static Integer extractInteger(JsonElement jsonElement, String name) {
		if ((null != jsonElement) && jsonElement.isJsonObject() && StringUtils.isNotEmpty(name)) {
			JsonElement element = extract(jsonElement, name);
			if (null != element && element.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber())
					return jsonPrimitive.getAsInt();
			}
		}
		return null;
	}

	public static List<String> extractStringList(JsonElement jsonElement, String name) {
		List<String> list = new ArrayList<>();
		if ((null != jsonElement) && jsonElement.isJsonObject() && StringUtils.isNotEmpty(name)) {
			JsonElement element = extract(jsonElement, name);
			if (null != element) {
				if (element.isJsonPrimitive()) {
					JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
					if (jsonPrimitive.isString()) {
						list.add(jsonPrimitive.getAsString());
					}
				} else if (element.isJsonArray()) {
					JsonArray jsonArray = element.getAsJsonArray();
					jsonArray.forEach(o -> {
						if ((null != o) && o.isJsonPrimitive()) {
							JsonPrimitive jsonPrimitive = o.getAsJsonPrimitive();
							if (jsonPrimitive.isString()) {
								list.add(jsonPrimitive.getAsString());
							}
						}
					});
				}
			}
		}
		return list;
	}

	public static JsonElement extract(JsonElement jsonElement, String path) {
		if (null == jsonElement || StringUtils.isEmpty(path)) {
			return null;
		}

		String name = path;
		boolean hasDot = false;
		if (StringUtils.contains(path, PATH_DOT)) {
			name = StringUtils.substringBefore(path, PATH_DOT);
			path = StringUtils.substringAfter(path, PATH_DOT);
			hasDot = true;
		}
		if(StringUtils.isNumeric(name) && jsonElement.isJsonArray()){
			int index = Integer.parseInt(name);
			JsonArray jsonArray = jsonElement.getAsJsonArray();
			if(jsonArray.size() > index && index > -1){
				return hasDot ? extract(jsonArray.get(index), path) : jsonArray.get(index);
			}
		}else if(jsonElement.isJsonObject()){
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			return hasDot ? extract(jsonObject.get(name), path) : jsonObject.get(name);
		}
		return null;
	}

	public static <T> T extract(JsonElement jsonElement, String name, Class<T> cls, T defaultValue) {
		JsonElement element = extract(jsonElement, name);
		if (element == null || element.isJsonNull()) {
			return defaultValue;
		}
		return instance().fromJson(element, cls);
	}

	public static void setValueToPath(JsonElement jsonElement, String path, JsonElement value) {
		if(jsonElement == null || value == null || StringUtils.isBlank(path)){
			return;
		}
		String[] paths = StringUtils.split(path, PATH_DOT);
		for (int i=0;i<paths.length;i++){
			String name = paths[i];
			if(StringUtils.isBlank(name)){
				return;
			}
			if(i == paths.length-1){
				if(jsonElement.isJsonObject()){
					jsonElement.getAsJsonObject().add(name, value);
				}
				return;
			}
			if(StringUtils.isNumeric(name)){
				jsonElement = setValueHandleJsonArray(jsonElement, name);
            }else{
				jsonElement = setValueHandleJsonObject(jsonElement, name, paths[i+1]);
            }
            if(jsonElement == null){
                return;
            }
        }
	}

	private static JsonElement setValueHandleJsonArray(JsonElement jsonElement, String name) {
		JsonElement je = null;
		if(jsonElement.isJsonArray()){
			JsonArray jsonArray = jsonElement.getAsJsonArray();
			int index = Integer.parseInt(name);
			if(jsonArray.size() > index && index > -1){
				je = jsonArray.get(index);
			}else if(jsonArray.isEmpty() && index == 0){
				JsonObject jsonObject = new JsonObject();
				jsonArray.add(jsonObject);
				je = jsonObject;
			}
		}
		return je;
	}

	private static JsonElement setValueHandleJsonObject(JsonElement jsonElement, String name, String nextName) {
		JsonElement result = null;
		if(jsonElement.isJsonObject()){
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			if(jsonObject.has(name)){
				result = jsonObject.get(name);
			}else{
				JsonElement je = new JsonObject();
				if(StringUtils.isNumeric(nextName)){
					je = new JsonArray();
				}
				jsonObject.add(name, je);
				result = je;
			}
		}
		return result;
	}

	public static boolean isJsonObject(String json) {
		if (StringUtils.isBlank(json)) {
			return false;
		}
		try {
			JsonElement jsonElement = new JsonParser().parse(json);
			if (jsonElement.isJsonObject()) {
				return true;
			} else {
				return false;
			}
		} catch (JsonParseException e) {
			return false;
		}
	}

	public static boolean isJsonArray(String json) {
		if (StringUtils.isBlank(json)) {
			return false;
		}
		try {
			JsonElement jsonElement = new JsonParser().parse(json);
			if (jsonElement.isJsonArray()) {
				return true;
			} else {
				return false;
			}
		} catch (JsonParseException e) {
			return false;
		}
	}

	public static JsonElement merge(JsonElement from, JsonElement to) throws Exception {
		if (from == null) {
			throw new Exception("from jsonElement can't be null.");
		}
		if (to == null) {
			throw new Exception("to jsonElement can't be null.");
		}
		if (!from.isJsonObject()) {
			throw new Exception("from jsonElement must be a jsonObject.");
		}
		if (!to.isJsonObject()) {
			throw new Exception("to jsonElement must be a jsonObject.");
		}
		return merge(from.getAsJsonObject(), to.deepCopy().getAsJsonObject());
	}

	private static JsonObject merge(JsonObject from, JsonObject to) {
		for (Map.Entry<String, JsonElement> fromEntry : from.entrySet()) {
			String key = fromEntry.getKey();
			if(StringUtils.isBlank(key)){
				continue;
			}
			JsonElement fromValue = fromEntry.getValue();
			if (to.has(key)) {
				JsonElement toValue = to.get(key);
				if ((!fromValue.isJsonObject()) || (!toValue.isJsonObject())) {
					to.add(key, fromValue);
				} else {
					merge(fromValue.getAsJsonObject(), toValue.getAsJsonObject());
				}
			} else {
				to.add(key, fromValue);
			}
		}
		return to;
	}

	public static JsonElement replace(JsonElement from, JsonElement to, String path) throws Exception{
		if (from == null) {
			throw new Exception("from jsonElement can't be null.");
		}
		if (to == null) {
			throw new Exception("to jsonElement can't be null.");
		}
		if (!to.isJsonObject()) {
			throw new Exception("to jsonElement must be a jsonObject.");
		}
		if(StringUtils.isBlank(path)){
			if(from.isJsonObject()){
				return from;
			}else{
				return null;
			}
		}else{
			JsonElement result = to.deepCopy();
			if(path.indexOf(PATH_DOT) > -1){
				String key = StringUtils.substringAfterLast(path,PATH_DOT);
				path = StringUtils.substringBeforeLast(path,PATH_DOT);
				JsonElement pathJson = extract(result, path);
				if(pathJson != null && pathJson.isJsonObject()){
					JsonObject jsonObject = pathJson.getAsJsonObject();
					jsonObject.add(key, from);
				}else{
					return null;
				}
			}else{
				JsonObject jsonObject = result.getAsJsonObject();
				jsonObject.add(path, from);
			}
			return result;
		}
	}

	/**
	 * from을 to의 지정 path 아래로 병합, path는 여러 단계 가능하며 여러 단계는 .으로 구분
	 * @param from JsonObject 객체 또는 JsonArray 객체 가능
	 * @param to 반드시 JsonObject 객체
	 * @param path
	 * @return 새 json 객체 반환
	 * @throws Exception
	 */
	public static JsonElement cover(JsonElement from, JsonElement to, String path) throws Exception{
		if (from == null) {
			throw new Exception("from jsonElement can't be null.");
		}
		if (to == null) {
			throw new Exception("to jsonElement can't be null.");
		}
		if (!to.isJsonObject()) {
			throw new Exception("to jsonElement must be a jsonObject.");
		}
		JsonObject result = to.deepCopy().getAsJsonObject();
		if(StringUtils.isBlank(path)){
			if(from.isJsonObject()){
				JsonObject fromJson = from.getAsJsonObject();
				for (String key : fromJson.keySet()) {
					result.add(key, fromJson.get(key));
				}
			}else{
				return null;
			}
		}else{
			JsonElement pathElement = extract(result, path);
			String key = path;
			if(pathElement == null){
				if(path.indexOf(PATH_DOT) > -1){
					key = StringUtils.substringAfterLast(path,PATH_DOT);
					path = StringUtils.substringBeforeLast(path,PATH_DOT);
					JsonElement jsonElement = extract(result, path);
					if(jsonElement!=null && jsonElement.isJsonObject()){
						jsonElement.getAsJsonObject().add(key, from);
					}
				}else{
					result.add(key, from);
				}
			}else {
				if (from.isJsonObject() && pathElement.isJsonObject()) {
					JsonObject fromJson = from.getAsJsonObject();
					JsonObject pathJson = pathElement.getAsJsonObject();
					for (String subKey : fromJson.keySet()) {
						pathJson.add(subKey, fromJson.get(subKey));
					}
				} else if (from.isJsonArray() && pathElement.isJsonArray()) {
					JsonArray jsonArray = pathElement.getAsJsonArray();
					for (JsonElement jsonFrom : from.getAsJsonArray()) {
						boolean flag = false;
						for (JsonElement jsonTo : jsonArray) {
							if (jsonFrom.toString().equalsIgnoreCase(jsonTo.toString())) {
								flag = true;
								break;
							}
						}
						if (!flag) {
							jsonArray.add(jsonFrom);
						}
					}
				} else {
					return null;
				}
			}
		}
		return result;
	}

	/**
	 * 지정 path 아래 요소를 재귀 탐색, path가 한 단계면 삭제, 여러 단계면 대체
	 * @param data
	 * @param fromData
	 * @param path
	 */
	public static void replaceWithPath(JsonElement data, JsonElement fromData, String path){
		if (data == null) {
			return;
		}
		if (!data.isJsonObject()) {
			return;
		}
		if (!fromData.isJsonObject()) {
			return;
		}
		if(StringUtils.isBlank(path)){
			return;
		}
		List<String> pathParts = Arrays.asList(StringUtils.split(path, PATH_DOT));
		if(pathParts.size() == 1) {
			processJsonElement(data, pathParts, 0);
		}else{
			processJsonElement(data, fromData, pathParts, 0);
		}
	}

	private static void processJsonElement(JsonElement element, JsonElement fromData, List<String> pathParts, int currentIndex){
		if(element == null || fromData == null || currentIndex >= pathParts.size()){
			return;
		}
		String currentPart = pathParts.get(currentIndex).trim();
		if(StringUtils.isBlank(currentPart)){
			return;
		}
		boolean isLastPart = (currentIndex == pathParts.size() - 1);
		if (element.isJsonObject()) {
			if(!fromData.isJsonObject()){
				return;
			}
			JsonObject jsonObject = element.getAsJsonObject();
			JsonObject fromObject = fromData.getAsJsonObject();
			if (jsonObject.has(currentPart)) {
				if (isLastPart) {
					jsonObject.add(currentPart, fromObject.get(currentPart));
				} else {
					processJsonElement(jsonObject.get(currentPart), fromObject.get(currentPart), pathParts, currentIndex + 1);
				}
			}
		} else if (element.isJsonArray()) {
			if(!fromData.isJsonArray()){
				return;
			}
			JsonArray jsonArray = element.getAsJsonArray();
			JsonArray fromArray = fromData.getAsJsonArray();
			if ("*".equals(currentPart)) {
				for (int i = 0; i<jsonArray.size(); i++) {
					if (!isLastPart && i < fromArray.size()) {
						processJsonElement(jsonArray.get(i), fromArray.get(i), pathParts, currentIndex + 1);
					}
				}
			} else {
				try {
					int index = Integer.parseInt(currentPart);
					if (index >= 0 && index < jsonArray.size() && index < fromArray.size()) {
						if (isLastPart) {
							jsonArray.set(index, fromArray.get(index));
						} else {
							processJsonElement(jsonArray.get(index), fromArray.get(index), pathParts, currentIndex + 1);
						}
					}
				} catch (NumberFormatException e) {
					// 현재 부분이 숫자도 와일드카드도 아니어서 배열을 처리할 수 없음
				}
			}
		}
	}

	/**
	 * 지정 path 아래 요소를 재귀 탐색하여 삭제
	 * @param data
	 * @param path
	 */
	public static void removeWithPath(JsonElement data, String path){
		if (data == null) {
			return;
		}
		if (!data.isJsonObject()) {
			return;
		}
		if(StringUtils.isBlank(path)){
			return;
		}
		List<String> pathParts = Arrays.asList(StringUtils.split(path, PATH_DOT));
		processJsonElement(data, pathParts, 0);
	}

	private static void processJsonElement(JsonElement element, List<String> pathParts, int currentIndex){
		if(element == null || currentIndex >= pathParts.size()){
			return;
		}
		String currentPart = pathParts.get(currentIndex).trim();
		if(StringUtils.isBlank(currentPart)){
			return;
		}
		boolean isLastPart = (currentIndex == pathParts.size() - 1);

		if (element.isJsonObject()) {
			JsonObject jsonObject = element.getAsJsonObject();
			if (jsonObject.has(currentPart)) {
				if (isLastPart) {
					jsonObject.remove(currentPart);
				} else {
					processJsonElement(jsonObject.get(currentPart), pathParts, currentIndex + 1);
				}
			}
		} else if (element.isJsonArray()) {
			JsonArray jsonArray = element.getAsJsonArray();
			if ("*".equals(currentPart)) {
				for (JsonElement arrayElement : jsonArray) {
					if (!isLastPart) {
						processJsonElement(arrayElement, pathParts, currentIndex + 1);
					}
				}
			} else {
				try {
					int index = Integer.parseInt(currentPart);
					if (index >= 0 && index < jsonArray.size()) {
						if (isLastPart) {
							jsonArray.set(index, JsonNull.INSTANCE);
						} else {
							processJsonElement(jsonArray.get(index), pathParts, currentIndex + 1);
						}
					}
				} catch (NumberFormatException e) {
					// 현재 부분이 숫자도 와일드카드도 아니어서 배열을 처리할 수 없음
				}
			}
		}
	}

	 /**
     * JsonElement 객체를 문자열, 숫자, 불리언, 문자열 목록, 숫자 목록, 불리언 목록을 각각 저장하는 6개의 Map으로 분리.
     *
     * @param prefix
     * @param jsonElement
     * @return
     */
    public static Sextuple<Map<String, String>, Map<String, Number>, Map<String, Boolean>, Map<String, List<String>>, Map<String, List<Number>>, Map<String, List<Boolean>>> separate(
                    String prefix, JsonElement jsonElement) {
        Sextuple<Map<String, String>, Map<String, Number>, Map<String, Boolean>, Map<String, List<String>>, Map<String, List<Number>>, Map<String, List<Boolean>>> sextuple =
                        Sextuple.of(new LinkedHashMap<>(), new LinkedHashMap<>(),
                                        new LinkedHashMap<>(), new LinkedHashMap<>(),
                                        new LinkedHashMap<>(), new LinkedHashMap<>());
        if (Objects.nonNull(jsonElement) && (!jsonElement.isJsonNull())) {
            separate(jsonElement, Objects.toString(prefix, ""), sextuple);
        }
        return sextuple;
    }

    private static void separate(JsonElement jsonElement, String name,
                    Sextuple<Map<String, String>, Map<String, Number>, Map<String, Boolean>, Map<String, List<String>>, Map<String, List<Number>>, Map<String, List<Boolean>>> sextuple) {
        if (jsonElement.isJsonPrimitive()) {
            separatePrimitive(jsonElement.getAsJsonPrimitive(), name, sextuple);
        } else if (jsonElement.isJsonArray()) {
            separateArray(jsonElement.getAsJsonArray(), name, sextuple);
        } else if (jsonElement.isJsonObject()) {
            jsonElement.getAsJsonObject().entrySet().stream().forEach(o -> separate(o.getValue(),
                            StringUtils.isBlank(name) ? o.getKey() : (name + "." + o.getKey()),
                            sextuple));
        }
    }

    private static void separatePrimitive(JsonPrimitive jsonPrimitive, String name,
                    Sextuple<Map<String, String>, Map<String, Number>, Map<String, Boolean>, Map<String, List<String>>, Map<String, List<Number>>, Map<String, List<Boolean>>> sextuple) {
        if (jsonPrimitive.isString()) {
            sextuple.first().put(name, jsonPrimitive.getAsString());
        } else if (jsonPrimitive.isNumber()) {
            sextuple.second().put(name, jsonPrimitive.getAsNumber());
        } else if (jsonPrimitive.isBoolean()) {
            sextuple.third().put(name, jsonPrimitive.getAsBoolean());
        }
    }

    private static void separateArray(JsonArray jsonArray, String name,
                    Sextuple<Map<String, String>, Map<String, Number>, Map<String, Boolean>, Map<String, List<String>>, Map<String, List<Number>>, Map<String, List<Boolean>>> sextuple) {
        List<JsonPrimitive> list = new ArrayList<>();
        jsonArray.forEach(o -> {
            if (o.isJsonObject()) {
                separate(o, name, sextuple);
            } else if (o.isJsonPrimitive()) {
                list.add(o.getAsJsonPrimitive());
            }
        });
        if (!list.isEmpty()) {
            if (list.stream().map(JsonPrimitive::isString).reduce(true, (a, b) -> a && b)
                            .booleanValue()) {
                sextuple.fourth().put(name, list.stream().map(JsonPrimitive::getAsString).collect(Collectors.toList()));
            } else if (list.stream().map(JsonPrimitive::isNumber).reduce(true, (a, b) -> a && b)
                            .booleanValue()) {
                sextuple.fifth().put(name, list.stream().map(JsonPrimitive::getAsNumber).collect(Collectors.toList()));
            } else if (list.stream().map(JsonPrimitive::isBoolean).reduce(true, (a, b) -> a && b)
                            .booleanValue()) {
                sextuple.sixth().put(name, list.stream().map(JsonPrimitive::getAsBoolean).collect(Collectors.toList()));
            }
        }
    }

}
