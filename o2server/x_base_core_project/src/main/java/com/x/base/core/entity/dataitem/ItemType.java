package com.x.base.core.entity.dataitem;

import com.x.base.core.entity.JpaObject;

public enum ItemType {
	/*
	 * JSONElement의 유형,
	 * 2020년 3월 11일: Simon Wu
	 * o:JsonObject
	 * a:JsonArray
	 * p:JsonPrimitive 기본 유형
	 * n:JsonNull
	 * j:JsonElement
	 * */
	o, a, p, n,j;
	public static final int length = JpaObject.length_1B;
}
