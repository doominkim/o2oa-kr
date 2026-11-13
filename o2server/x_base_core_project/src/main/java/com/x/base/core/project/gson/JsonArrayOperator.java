package com.x.base.core.project.gson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON 배열 조작 유틸리티 클래스
 */
public class JsonArrayOperator {

    private static final Gson gson = new Gson();

    /**
     * 지정 위치에 JSON 객체 삽입
     * @param jsonArray 원본 JSON 배열
     * @param index 삽입 위치 인덱스
     * @param jsonObject 삽입할 JSON 객체
     * @return 작업 후 새 JSON 배열
     * @throws IllegalArgumentException 인덱스가 범위를 벗어난 경우
     */
    public static JsonArray insert(JsonArray jsonArray, int index, JsonObject jsonObject) {
        if (jsonArray == null) {
            jsonArray = new JsonArray();
        }

        if (index < 0 || index > jsonArray.size()) {
            throw new IllegalArgumentException("삽입 위치 인덱스가 범위를 벗어났습니다: " + index);
        }

        JsonArray result = new JsonArray();

        // 삽입 위치 앞의 요소
        for (int i = 0; i < index; i++) {
            result.add(jsonArray.get(i));
        }

        // 새 요소 삽입
        result.add(jsonObject);

        // 삽입 위치 뒤의 요소
        for (int i = index; i < jsonArray.size(); i++) {
            result.add(jsonArray.get(i));
        }

        return result;
    }

    /**
     * 지정 위치에 객체 삽입(JSON으로 자동 변환)
     * @param jsonArray 원본 JSON 배열
     * @param index 삽입 위치 인덱스
     * @param object 삽입할 객체
     * @return 작업 후 새 JSON 배열
     */
    public static JsonArray insert(JsonArray jsonArray, int index, Object object) {
        JsonObject jsonObject = gson.toJsonTree(object).getAsJsonObject();
        return insert(jsonArray, index, jsonObject);
    }

    /**
     * 지정 위치의 JSON 객체 삭제
     * @param jsonArray 원본 JSON 배열
     * @param index 삭제할 위치 인덱스
     * @return 작업 후 새 JSON 배열
     * @throws IllegalArgumentException 인덱스가 범위를 벗어난 경우
     */
    public static JsonArray remove(JsonArray jsonArray, int index) {
        if (jsonArray == null || jsonArray.size() == 0) {
            return new JsonArray();
        }

        if (index < 0 || index >= jsonArray.size()) {
            throw new IllegalArgumentException("삭제 위치 인덱스가 범위를 벗어났습니다: " + index);
        }

        JsonArray result = new JsonArray();

        for (int i = 0; i < jsonArray.size(); i++) {
            if (i != index) {
                result.add(jsonArray.get(i));
            }
        }

        return result;
    }

    /**
     * 지정 위치의 객체를 새 위치로 이동
     * @param jsonArray 원본 JSON 배열
     * @param fromIndex 원래 위치 인덱스
     * @param toIndex 대상 위치 인덱스
     * @return 작업 후 새 JSON 배열
     * @throws IllegalArgumentException 인덱스가 범위를 벗어난 경우
     */
    public static JsonArray move(JsonArray jsonArray, int fromIndex, int toIndex) {
        if (jsonArray == null || jsonArray.size() == 0) {
            return new JsonArray();
        }

        if (fromIndex < 0 || fromIndex >= jsonArray.size() ||
                toIndex < 0 || toIndex >= jsonArray.size()) {
            throw new IllegalArgumentException("이동 위치 인덱스가 범위를 벗어났습니다: fromIndex=" + fromIndex + ", toIndex=" + toIndex);
        }

        if (fromIndex == toIndex) {
            // 위치가 같으면 원본 배열의 복사본을 바로 반환
            return deepCopy(jsonArray);
        }

        // 먼저 원래 위치의 요소 제거
        JsonElement movedElement = jsonArray.get(fromIndex);
        JsonArray tempArray = remove(jsonArray, fromIndex);

        // 새 배열에서의 삽입 위치 계산
        int insertIndex = (toIndex > fromIndex) ? toIndex - 1 : toIndex;

        // 새 위치에 삽입
        return insert(tempArray, insertIndex, movedElement.getAsJsonObject());
    }

    /**
     * JSON 배열 깊은 복사
     * @param jsonArray 원본 JSON 배열
     * @return 깊은 복사된 새 JSON 배열
     */
    public static JsonArray deepCopy(JsonArray jsonArray) {
        if (jsonArray == null) {
            return new JsonArray();
        }

        JsonArray copy = new JsonArray();
        for (JsonElement element : jsonArray) {
            copy.add(gson.fromJson(gson.toJson(element), JsonElement.class));
        }
        return copy;
    }

    /**
     * JSON 배열을 객체 목록으로 변환
     * @param jsonArray JSON 배열
     * @param clazz 대상 타입
     * @return 객체 목록
     */
    public static <T> List<T> toList(JsonArray jsonArray, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        if (jsonArray != null) {
            for (JsonElement element : jsonArray) {
                list.add(gson.fromJson(element, clazz));
            }
        }
        return list;
    }

    /**
     * 객체 목록을 JSON 배열로 변환
     * @param list 객체 목록
     * @return JSON 배열
     */
    public static <T> JsonArray fromList(List<T> list) {
        JsonArray jsonArray = new JsonArray();
        if (list != null) {
            for (T item : list) {
                jsonArray.add(gson.toJsonTree(item));
            }
        }
        return jsonArray;
    }
}
